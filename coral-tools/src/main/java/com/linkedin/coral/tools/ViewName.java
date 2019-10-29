/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Represents a dali view
 */
class ViewName {
  public static final Pattern VIEW_NAME_PATTERN = Pattern.compile("([a-zA-Z0-9_]*)_(\\d+)_(\\d+)_(\\d+)$");

  private final String basename;
  private final Version version;
  private static final Version VERSION_ZERO = new Version(0, 0, 0);

  public static ViewName create(String fullViewName) {
    Matcher m = VIEW_NAME_PATTERN.matcher(fullViewName);
    if (!m.matches()) {
      return new ViewName(fullViewName, new Version(0, 0, 0));
    }

    return new ViewName(m.group(1),
        new Version(Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4))));
  }

  ViewName(String name, Version v) {
    this.basename = name;
    this.version = v;
  }

  public String getBasename() {
    return basename;
  }

  public Version getVersion() {
    return version;
  }

  public boolean isSameView(ViewName rhs) {
    return basename.equals(rhs.basename);
  }

  @Override
  public String toString() {
    return version.equals(VERSION_ZERO) ? basename : String.join("_", basename, version.toString());
  }
}
