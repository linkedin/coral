/**
 * Copyright 2017-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.parsetree;

public class UnsupportedASTException extends RuntimeException {

  public UnsupportedASTException(String msg) {
    super(msg);
  }
}
