/**
 * Copyright 2020-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.functions;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

import org.apache.ivy.Ivy;
import org.apache.ivy.core.LogOptions;
import org.apache.ivy.core.module.descriptor.DefaultDependencyArtifactDescriptor;
import org.apache.ivy.core.module.descriptor.DefaultDependencyDescriptor;
import org.apache.ivy.core.module.descriptor.DefaultModuleDescriptor;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.core.report.ArtifactDownloadReport;
import org.apache.ivy.core.report.ResolveReport;
import org.apache.ivy.core.resolve.ResolveOptions;
import org.apache.ivy.core.settings.IvySettings;
import org.apache.ivy.util.DefaultMessageLogger;
import org.apache.ivy.util.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ArtifactsResolver {
  private static final Logger LOG = LoggerFactory.getLogger(ArtifactsResolver.class);
  private static final String IVY_SETTINGS_LOCATION = "IVY_SETTINGS_LOCATION";
  private static final String IVY_CACHE_DIR = "IVY_CACHE_DIR";
  private static final String IVY_SETTINGS_FILE_NAME = "ivy.settings.xml";
  private static final String IVY_LOG_LEVEL = "IVY_LOG_LEVEL";
  private final Ivy _ivyInstance;
  public ArtifactsResolver() {
    // Ivy spews a lot of junk on the console. Hiding it
    Message.setDefaultLogger(new DefaultMessageLogger(Message.MSG_ERR));
    final IvySettings settings = setupIvySettings();
    _ivyInstance = Ivy.newInstance(settings);
  }

  /**
   * Parses an dependencySpecString in Gradle string notation and
   * returns the corresponding dependency along with
   * all the transitive dependencies
   *
   * Gradle string dependency notations looks like
   *
   * "group:name:version:classifier@extension?transitive=false"
   * "group:name:version:classifier@extension"
   * "group:name:version@extension"
   * "group:name:version"
   */
  public List<File> resolve(String udfClassName, String dependencySpecString) {
    List<File> uris = new ArrayList<>();
    try {
      _ivyInstance.pushContext();
      final ResolveReport report = getDependencies(udfClassName, createDependencySpec(dependencySpecString));
      for (ArtifactDownloadReport adr : report.getAllArtifactsReports()) {
        if (adr.getLocalFile() != null) {
          uris.add(adr.getLocalFile());
        }
      }
      return uris;
    } finally {
      _ivyInstance.popContext();
    }
  }

  private static DependencySpec createDependencySpec(String dependencySpecString) {
    final DependencySpec dependencySpec = new DependencySpec();
    String withoutIvy = dependencySpecString.startsWith("ivy://") ? dependencySpecString.replaceFirst("ivy://", "")
        : dependencySpecString;
    final String[] withParameters = withoutIvy.split("\\?");
    final String[] withExtension = withParameters[0].split("@");
    final String[] parts = withExtension[0].split(":");
    if (withParameters.length == 2) {
      for (String parameter : withParameters[1].split("&")) {
        String[] parameterKeyValue = parameter.trim().split("=");
        if (parameterKeyValue[0].equalsIgnoreCase("classifier")) {
          dependencySpec.classifier = parameterKeyValue[1];
        } else if (parameterKeyValue[0].equalsIgnoreCase("transitive")) {
          dependencySpec.transitive = Boolean.parseBoolean(parameterKeyValue[1]);
        }
      }
    }
    if (withExtension.length == 2) {
      dependencySpec.ext = withExtension[1];
    }
    // required parts
    dependencySpec.group = parts[0];
    dependencySpec.module = parts[1];
    dependencySpec.version = parts[2];
    // optional
    if (parts.length > 3) {
      dependencySpec.classifier = parts[3];
    }
    return dependencySpec;
  }

  private ResolveReport getDependencies(String udfClassName, DependencySpec dependencySpec) {
    final long millis = System.currentTimeMillis();
    final DefaultModuleDescriptor md = DefaultModuleDescriptor
        .newDefaultInstance(ModuleRevisionId.newInstance("caller", "all-caller", "working-" + millis));
    final ModuleRevisionId mrid = dependencySpec.mrid();
    final DefaultDependencyDescriptor dd = new DefaultDependencyDescriptor(md, mrid, true, false, true);
    dd.addDependencyConfiguration("default", "*");
    md.addDependency(dd);
    if (dependencySpec.classifier != null || dependencySpec.ext != null) {
      final Map attrs =
          dependencySpec.classifier != null ? ImmutableMap.of("classifier", dependencySpec.classifier) : null;
      final DefaultDependencyArtifactDescriptor dad = new DefaultDependencyArtifactDescriptor(dd, mrid.getName(),
          dependencySpec.type, dependencySpec.ext, null, attrs);
      dad.addConfiguration("*");
      dd.addDependencyArtifact("default", dad);
    }
    final ResolveOptions resolveOptions = new ResolveOptions().setConfs(new String[] { "default" })
        .setOutputReport(true).setValidate(true).setTransitive(dependencySpec.transitive);
    resolveOptions.setLog(LogOptions.LOG_QUIET);
    resolveOptions.setOutputReport(false);
    try {
      final ResolveReport report = _ivyInstance.resolve(md, resolveOptions);
      if (report.hasError()) {
        LOG.warn(
            String.format("Unable to fetch dependencies for UDF %s: %s", udfClassName, report.getAllProblemMessages()));
      }
      return report;
    } catch (ParseException | IOException e) {
      throw new RuntimeException(String.format("Unable to fetch dependencies for UDF %s", udfClassName), e);
    }
  }

  private static IvySettings setupIvySettings() {
    final IvySettings settings = new IvySettings();
    try {
      if (System.getenv().containsKey(IVY_SETTINGS_LOCATION)) {
        File settingsFile = getSettingsFile();
        LOG.info("Reading Ivy settings from: " + settingsFile);
        settings.load(settingsFile);
      } else {
        final URL settingsUrl = ArtifactsResolver.class.getClassLoader().getResource(IVY_SETTINGS_FILE_NAME);
        LOG.info("Reading Ivy settings from: " + settingsUrl);
        settings.load(settingsUrl);
      }
    } catch (ParseException | IOException e) {
      throw new RuntimeException("Unable to configure Ivy", e);
    }
    String ivyCacheDir = System.getenv(IVY_CACHE_DIR);
    if (ivyCacheDir != null) {
      settings.setDefaultCache(new File(ivyCacheDir));
    }
    setupCacheDir(settings.getDefaultCache());
    settings.setVariable("ivy.default.configuration.m2compatible", "true");
    return settings;
  }

  private static File getSettingsFile() {
    final String s = System.getenv().get(IVY_SETTINGS_LOCATION);
    if (s == null) {
      return null;
    }
    final File file = new File(s);
    if (!file.exists()) {
      throw new RuntimeException("Ivy settings file: " + file + " does not exist");
    }
    return file;
  }

  private static void setupCacheDir(File cache) {
    if (!cache.exists()) {
      if (!cache.mkdirs()) {
        throw new RuntimeException("Unable to create cache directory: " + cache);
      }
    } else if (!cache.isDirectory()) {
      throw new RuntimeException("Default cache is not a directory: " + cache);
    }
  }
  private static class DependencySpec {
    String module;
    String type = "jar";
    String ext = "jar";
    String classifier;
    String group;
    String version;
    boolean transitive = true;
    ModuleRevisionId mrid() {
      return ModuleRevisionId.newInstance(group, module, version);
    }
  }
  private static int getIvyLogLevel() {
    final String r = System.getenv(IVY_LOG_LEVEL);
    if (Strings.isNullOrEmpty(r)) {
      return Message.MSG_INFO;
    }
    try {
      return Integer.parseInt(r);
    } catch (NumberFormatException ignore) {
      LOG.warn("Could not parse number: " + r + " corresponding to key: " + IVY_LOG_LEVEL);
      return Message.MSG_INFO;
    }
  }
}
