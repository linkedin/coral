/**
 * Copyright 2020 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import groovy.grape.Grape;
import groovy.lang.GroovyClassLoader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArtifactsResolver {
  private static final Logger LOG = LoggerFactory.getLogger(ArtifactsResolver.class);

  private static final String IVY_SETTINGS_LOCATION = "IVY_SETTINGS_LOCATION";
  private static final String IVY_SETTINGS_FILE_NAME = "ivy.settings.xml";

  static {
    final File ivySettingsFile = getIvySettingsFile();
    setupGrape(ivySettingsFile);
  }

  /**
   * Parses the input artifact and downloads the
   * specified artifact possibly along with its transitive
   * dependencies and returns those as a set of local
   * uris
   *
   * @param artifactUri Artifact string artifactUri
   * @return List of resolved dependencies
   * @throws IllegalArgumentException If the artifact is not a valid URI
   */
  public List<URI> downloadDependencies(URI artifactUri) {
    final Map<String, Object> artifactMap = Maps.newHashMap();
    final String authority = artifactUri.getAuthority();
    Preconditions.checkArgument(
        authority != null,
        "Invalid artifactUri: Expected artifactUri's authority as 'org:module:version', found null");

    String[] authorityTokens = authority.split(":");
    Preconditions.checkArgument(
        authorityTokens.length == 3,
        "Invalid artifactUri: Expected 'org:module:version', found " + authority);

    artifactMap.put("org", authorityTokens[0]);
    artifactMap.put("module", authorityTokens[1]);
    artifactMap.put("version", authorityTokens[2]);

    Map<String, Object> queryMap = parseQueryString(artifactUri.getQuery());
    artifactMap.putAll(queryMap);

    return grab(artifactMap);
  }

  /**
   * @param queryString e.g 'ext=jar&exclude=org.mortbay.jetty:jetty&transitive=true'
   * @return queryMap
   * e.g {ext: jar, exclude: {group:org.mortbay.jetty, module:jetty}, transitive: true}
   * @throws IllegalArgumentException If the queryString is not well formed
   */
  private static Map<String, Object> parseQueryString(String queryString) {
    if (Strings.isNullOrEmpty(queryString)) {
      return ImmutableMap.of();
    }

    final Set<Map<String, String>> excludes = Sets.newHashSet();
    final Map<String, Object> queryMap = Maps.newHashMap();
    final String[] tokens = queryString.split("&");

    for (String token : tokens) {
      String[] kvPair = token.split("=");
      Preconditions.checkArgument(
          kvPair.length == 2,
          "Invalid query string: " + queryString + ". Token: " + token + " not a key-value " + "pair separated by '='");

      if (kvPair[0].equals("exclude")) {
        excludes.addAll(computeExcludes(kvPair[1]));

      } else if (kvPair[0].equals("transitive")) {
        queryMap.put(kvPair[0], Boolean.parseBoolean(kvPair[1]));

      } else {
        queryMap.put(kvPair[0], kvPair[1]);
      }
    }
    if (!excludes.isEmpty()) {
      queryMap.put("exclude", excludes);
    }
    return queryMap;
  }

  private static Set<Map<String, String>> computeExcludes(String excludeStr) throws RuntimeException {
    final String[] excludes = excludeStr.split(",");
    final Set<Map<String, String>> excludeSet = Sets.newHashSet();

    for (String exclude : excludes) {
      Map<String, String> artifactMap = Maps.newHashMap();
      if (exclude.indexOf(':') != -1) {
        final String[] args = exclude.split(":");
        Preconditions.checkArgument(
            args.length == 2, "Invalid exclude string: expected 'org[:module]?', found: " + exclude);

        artifactMap.put("group", args[0]);
        artifactMap.put("module", args[1]);

      } else {
        artifactMap.put("group", exclude);
        artifactMap.put("module", "*");
      }
      excludeSet.add(artifactMap);
    }
    return excludeSet;
  }

  /**
   * @param artifact to resolve
   * @return List of URIs of downloaded dependencies
   */
  private static List<URI> grab(Map<String, Object> artifact) {
    final Map<String, Object> args = Maps.newHashMap();

    // Grape expects excludes key in args map
    if (artifact.containsKey("exclude")) {
      args.put("excludes", artifact.get("exclude"));
    }

    // Set transitive to true by default
    if (!artifact.containsKey("transitive")) {
      artifact.put("transitive", true);
    }

    args.put("classLoader", new GroovyClassLoader());

    try {
      LOG.info("Resolving artifact: " + artifact);
      final URI[] uris = Grape.resolve(args, artifact);
      LOG.info("Finish resolving artifact: " + Arrays.toString(uris));
      return Lists.newArrayList(uris);

    } catch (RuntimeException e) {
      // Can't improve upon this unfortunately
      final String msg = e.getMessage();
      if (msg != null && msg.contains("configuration not found")) {
        throw new RuntimeException("configuration not found", e);
      }
      throw new RuntimeException("cannot resolve artifact", e);
    }
  }

  private static File getIvySettingsFile() {
    if (System.getenv().containsKey(IVY_SETTINGS_LOCATION)) {
      final String s = System.getenv().get(IVY_SETTINGS_LOCATION);
      final File settingsFile = new File(s);
      if (!settingsFile.exists()) {
        throw new RuntimeException(
            "Ivy settings file: " + settingsFile + " specified under " + IVY_SETTINGS_LOCATION
                + " does not exist");
      }
      LOG.info("Reading Ivy settings from: " + settingsFile);
      return settingsFile;
    } else {
      return setupIvyFileFromClassPath();
    }
  }

  private static File setupIvyFileFromClassPath() {
    // settingsUrl may reside inside a jar
    final URL settingsUrl = Thread.currentThread().getContextClassLoader().getResource(IVY_SETTINGS_FILE_NAME);
    if (settingsUrl == null) {
      throw new RuntimeException("Failed to find " + IVY_SETTINGS_FILE_NAME + " from class path");
    }
    LOG.info("Ivy settings url: " + settingsUrl);

    // Check if settingsUrl is file on classpath
    File ivySettingsFile = new File(settingsUrl.getFile());
    if (ivySettingsFile.exists()) {
      // can access settingsUrl as a file
      return ivySettingsFile;
    }

    // Create temporary Ivy settings file.
    try {
      ivySettingsFile = File.createTempFile("ivy.settings", ".xml");
      ivySettingsFile.deleteOnExit();
    } catch (IOException e) {
      throw new RuntimeException("Failed to create temporary Ivy settings file.", e);
    }

    // Copy contents of Ivy file in jar to temporary Ivy settings file.
    try (OutputStream os = new BufferedOutputStream(new FileOutputStream(ivySettingsFile))) {
      Resources.copy(settingsUrl, os);
    } catch (IOException e) {
      throw new RuntimeException("Failed to setup ivy settings file: " + ivySettingsFile, e);
    }
    LOG.info("Created ivy settings file: " + ivySettingsFile + " for current session");
    return ivySettingsFile;
  }

  /*
   * Need to set up grape.root. By default the grape.root is ~/.groovy/grapes. This may not be
   * available. For example, in spark yarn-cluster mode, client runs in AM, the home dir
   * is not available on AM host.
   * As a last resort, we make a tmp dir on the current directory as grape.root
   */
  private static void setupGrape(File ivySettingsFile) {
    System.setProperty("grape.config", ivySettingsFile.getAbsolutePath());
    LOG.info("Resolving grape.root dir.");

    // First, if grape.root sys property is already set, validate this property
    String grapeRoot = System.getProperty("grape.root");
    if (!Strings.isNullOrEmpty(grapeRoot)) {
      File grapeRootDir = new File(grapeRoot);
      if (grapeRootDir.exists() || grapeRootDir.mkdirs()) {
        LOG.info("Using Grape root dir: " + grapeRoot);
        return;
      } else {
        LOG.warn("Grape.root system property is not valid: " + grapeRoot);
      }
    } else {
      LOG.info("Grape.root system property is not set.");
    }

    // Second, validate home dir, and set grape.root as ${user.home}/.groovy/grapes
    grapeRoot = System.getProperty("user.home") + "/.groovy/grapes";
    File grapeRootDir = new File(grapeRoot);
    if (grapeRootDir.exists() || grapeRootDir.mkdirs()) {
      // Home dir is valid, set grape.root as ${user.home}/.groovy/grapes
      System.setProperty("grape.root", grapeRoot);
      LOG.info("Using Grape root dir: " + grapeRoot);
      return;
    } else {
      LOG.warn("Home dir is not valid as grape.root.");
    }

    // The grape.root and home dir are not available.
    // We make a tmp dir on the current directory as grape.root
    File tmpDir = new File("" + System.currentTimeMillis());
    if (tmpDir.mkdirs()) {
      System.setProperty("grape.root", tmpDir.getAbsolutePath());
      LOG.info("Using Grape root dir: " + tmpDir.getAbsolutePath());
    } else {
      throw new RuntimeException("Unable to make grape root dir: " + tmpDir.getAbsolutePath());
    }
  }
}

