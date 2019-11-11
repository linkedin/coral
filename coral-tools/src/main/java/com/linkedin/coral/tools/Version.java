package com.linkedin.coral.tools;

/**
 * Represent a version
 */
class Version implements Comparable<Version> {
  int major;
  int minor;
  int patch;

  Version(int major, int minor, int patch) {
    this.major = major;
    this.minor = minor;
    this.patch = patch;
  }

  @Override
  public int compareTo(Version rhs) {
    if (major > rhs.major) {
      return 1;
    }
    if (major < rhs.major) {
      return -1;
    }
    // major = rhs.major
    if (minor > rhs.minor) {
      return 1;
    }
    if (minor < rhs.minor) {
      return -1;
    }
    return Integer.compare(patch, rhs.patch);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Version)) {
      return false;
    }

    Version version = (Version) o;

    if (major != version.major) {
      return false;
    }
    if (minor != version.minor) {
      return false;
    }
    return patch == version.patch;
  }

  @Override
  public int hashCode() {
    int result = major;
    result = 31 * result + minor;
    result = 31 * result + patch;
    return result;
  }

  @Override
  public String toString() {
    return String.join("_", String.valueOf(major), String.valueOf(minor), String.valueOf(patch));
  }
}