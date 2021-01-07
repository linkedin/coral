/**
 * Copyright 2017-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.parsetree.parser;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.RecognitionException;

/*
 * SemanticException.java
 *
 * Created on April 1, 2008, 1:20 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */


//spotless:off
public class ParseError {
  private final BaseRecognizer br;
  private final RecognitionException re;
  private final String[] tokenNames;

  ParseError(BaseRecognizer br, RecognitionException re, String[] tokenNames) {
    this.br = br;
    this.re = re;
    this.tokenNames = tokenNames;
  }

  BaseRecognizer getBaseRecognizer() {
    return br;
  }

  RecognitionException getRecognitionException() {
    return re;
  }

  String[] getTokenNames() {
    return tokenNames;
  }

  String getMessage() {
    return br.getErrorHeader(re) + " " + br.getErrorMessage(re, tokenNames);
  }
}
//spotless:on
