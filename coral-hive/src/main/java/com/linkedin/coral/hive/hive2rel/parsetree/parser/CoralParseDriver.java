/**
 * Copyright 2021-2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.parsetree.parser;

import java.util.ArrayList;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenRewriteStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class CoralParseDriver extends ParseDriver {

  private static final Log LOG =
      LogFactory.getLog("com.linkedin.coral.hive.hive2rel.parsetree.parser.CoralParseDriver");

  @Override
  public ASTNode parse(String command) throws ParseException {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Parsing command: " + command);
    }

    HiveLexerCoral lexer = new HiveLexerCoral(new ANTLRNoCaseStringStream(command));
    TokenRewriteStream tokens = new TokenRewriteStream(lexer);
    HiveParser parser = new HiveParser(tokens);
    parser.setTreeAdaptor(adaptor);
    HiveParser.statement_return r = null;
    try {
      r = parser.statement();
    } catch (RecognitionException e) {
      e.printStackTrace();
      throw new ParseException(parser.errors);
    }

    if (lexer.getErrors().size() == 0 && parser.errors.size() == 0) {
      LOG.debug("Parse Completed");
    } else if (lexer.getErrors().size() != 0) {
      throw new ParseException(lexer.getErrors());
    } else {
      throw new ParseException(parser.errors);
    }

    ASTNode tree = (ASTNode) r.getTree();
    tree.setUnknownTokenBoundaries();
    return tree;
  }

  public static class HiveLexerCoral extends HiveLexer {

    private final ArrayList<ParseError> errors;

    public HiveLexerCoral() {
      super();
      errors = new ArrayList<>();
    }

    public HiveLexerCoral(CharStream input) {
      super(input);
      errors = new ArrayList<>();
    }

    @Override
    public void displayRecognitionError(String[] tokenNames, RecognitionException e) {

      errors.add(new ParseError(this, e, tokenNames));
    }

    @Override
    public String getErrorMessage(RecognitionException e, String[] tokenNames) {
      String msg = null;

      if (e instanceof NoViableAltException) {
        @SuppressWarnings("unused")
        NoViableAltException nvae = (NoViableAltException) e;
        // for development, can add
        // "decision=<<"+nvae.grammarDecisionDescription+">>"
        // and "(decision="+nvae.decisionNumber+") and
        // "state "+nvae.stateNumber
        msg = "character " + getCharErrorDisplay(e.c) + " not supported here";
      } else {
        msg = super.getErrorMessage(e, tokenNames);
      }

      return msg;
    }

    public ArrayList<ParseError> getErrors() {
      return errors;
    }

    @Override
    protected boolean allowQuotedId() {
      return true;
    }
  }
}
