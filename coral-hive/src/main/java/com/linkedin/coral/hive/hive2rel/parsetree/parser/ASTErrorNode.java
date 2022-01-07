/**
 * Copyright 2017-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.parsetree.parser;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonErrorNode;


//spotless:off
public class ASTErrorNode extends ASTNode {

  private static final long serialVersionUID = 1L;
  final CommonErrorNode delegate;

  public ASTErrorNode(TokenStream input, Token start, Token stop, RecognitionException e) {
    delegate = new CommonErrorNode(input, start, stop, e);
  }

  @Override
  public boolean isNil() {
    return delegate.isNil();
  }

  @Override
  public int getType() {
    return delegate.getType();
  }

  @Override
  public String getText() {
    return delegate.getText();
  }

  @Override
  public String toString() {
    return delegate.toString();
  }
}
//spotless:on
