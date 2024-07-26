/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.coralservice.utils;

import com.fasterxml.jackson.annotation.JsonCreator;


public enum RewriteType {
  NONE("none"),
  INCREMENTAL("incremental"),
  DATAMASKING("datamasking");

  private final String type;

  RewriteType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return type;
  }

  @JsonCreator
  public static RewriteType getRewriteTypeFromCode(String value) {
    for (RewriteType type : RewriteType.values()) {
      if (type.toString().equals(value)) {
        return type;
      }
    }
    return null;
  }

}
