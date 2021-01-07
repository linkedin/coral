/**
 * Copyright 2017-2021 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.hive.hive2rel.parsetree.parser;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.DFA;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.FailedPredicateException;
import org.antlr.runtime.IntStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;


//spotless:off
@SuppressWarnings({ "all", "warnings", "unchecked" })
public class HiveLexer extends Lexer {
  public static final int EOF = -1;
  public static final int AMPERSAND = 4;
  public static final int BITWISEOR = 5;
  public static final int BITWISEXOR = 6;
  public static final int BigintLiteral = 7;
  public static final int ByteLengthLiteral = 8;
  public static final int COLON = 9;
  public static final int COMMA = 10;
  public static final int COMMENT = 11;
  public static final int CharSetLiteral = 12;
  public static final int CharSetName = 13;
  public static final int DIV = 14;
  public static final int DIVIDE = 15;
  public static final int DOLLAR = 16;
  public static final int DOT = 17;
  public static final int DecimalLiteral = 18;
  public static final int Digit = 19;
  public static final int EQUAL = 20;
  public static final int EQUAL_NS = 21;
  public static final int Exponent = 22;
  public static final int GREATERTHAN = 23;
  public static final int GREATERTHANOREQUALTO = 24;
  public static final int HexDigit = 25;
  public static final int Identifier = 26;
  public static final int KW_ADD = 27;
  public static final int KW_ADMIN = 28;
  public static final int KW_AFTER = 29;
  public static final int KW_ALL = 30;
  public static final int KW_ALTER = 31;
  public static final int KW_ANALYZE = 32;
  public static final int KW_AND = 33;
  public static final int KW_ARCHIVE = 34;
  public static final int KW_ARRAY = 35;
  public static final int KW_AS = 36;
  public static final int KW_ASC = 37;
  public static final int KW_AUTHORIZATION = 38;
  public static final int KW_BEFORE = 39;
  public static final int KW_BETWEEN = 40;
  public static final int KW_BIGINT = 41;
  public static final int KW_BINARY = 42;
  public static final int KW_BOOLEAN = 43;
  public static final int KW_BOTH = 44;
  public static final int KW_BUCKET = 45;
  public static final int KW_BUCKETS = 46;
  public static final int KW_BY = 47;
  public static final int KW_CASCADE = 48;
  public static final int KW_CASE = 49;
  public static final int KW_CAST = 50;
  public static final int KW_CHANGE = 51;
  public static final int KW_CHAR = 52;
  public static final int KW_CLUSTER = 53;
  public static final int KW_CLUSTERED = 54;
  public static final int KW_CLUSTERSTATUS = 55;
  public static final int KW_COLLECTION = 56;
  public static final int KW_COLUMN = 57;
  public static final int KW_COLUMNS = 58;
  public static final int KW_COMMENT = 59;
  public static final int KW_COMPACT = 60;
  public static final int KW_COMPACTIONS = 61;
  public static final int KW_COMPUTE = 62;
  public static final int KW_CONCATENATE = 63;
  public static final int KW_CONF = 64;
  public static final int KW_CONTINUE = 65;
  public static final int KW_CREATE = 66;
  public static final int KW_CROSS = 67;
  public static final int KW_CUBE = 68;
  public static final int KW_CURRENT = 69;
  public static final int KW_CURRENT_DATE = 70;
  public static final int KW_CURRENT_TIMESTAMP = 71;
  public static final int KW_CURSOR = 72;
  public static final int KW_DATA = 73;
  public static final int KW_DATABASE = 74;
  public static final int KW_DATABASES = 75;
  public static final int KW_DATE = 76;
  public static final int KW_DATETIME = 77;
  public static final int KW_DBPROPERTIES = 78;
  public static final int KW_DECIMAL = 79;
  public static final int KW_DEFAULT = 80;
  public static final int KW_DEFERRED = 81;
  public static final int KW_DEFINED = 82;
  public static final int KW_DELETE = 83;
  public static final int KW_DELIMITED = 84;
  public static final int KW_DEPENDENCY = 85;
  public static final int KW_DESC = 86;
  public static final int KW_DESCRIBE = 87;
  public static final int KW_DIRECTORIES = 88;
  public static final int KW_DIRECTORY = 89;
  public static final int KW_DISABLE = 90;
  public static final int KW_DISTINCT = 91;
  public static final int KW_DISTRIBUTE = 92;
  public static final int KW_DOUBLE = 93;
  public static final int KW_DROP = 94;
  public static final int KW_ELEM_TYPE = 95;
  public static final int KW_ELSE = 96;
  public static final int KW_ENABLE = 97;
  public static final int KW_END = 98;
  public static final int KW_ESCAPED = 99;
  public static final int KW_EXCHANGE = 100;
  public static final int KW_EXCLUSIVE = 101;
  public static final int KW_EXISTS = 102;
  public static final int KW_EXPLAIN = 103;
  public static final int KW_EXPORT = 104;
  public static final int KW_EXTENDED = 105;
  public static final int KW_EXTERNAL = 106;
  public static final int KW_FALSE = 107;
  public static final int KW_FETCH = 108;
  public static final int KW_FIELDS = 109;
  public static final int KW_FILE = 110;
  public static final int KW_FILEFORMAT = 111;
  public static final int KW_FIRST = 112;
  public static final int KW_FLOAT = 113;
  public static final int KW_FOLLOWING = 114;
  public static final int KW_FOR = 115;
  public static final int KW_FORMAT = 116;
  public static final int KW_FORMATTED = 117;
  public static final int KW_FROM = 118;
  public static final int KW_FULL = 119;
  public static final int KW_FUNCTION = 120;
  public static final int KW_FUNCTIONS = 121;
  public static final int KW_GRANT = 122;
  public static final int KW_GROUP = 123;
  public static final int KW_GROUPING = 124;
  public static final int KW_HAVING = 125;
  public static final int KW_HOLD_DDLTIME = 126;
  public static final int KW_IDXPROPERTIES = 127;
  public static final int KW_IF = 128;
  public static final int KW_IGNORE = 129;
  public static final int KW_IMPORT = 130;
  public static final int KW_IN = 131;
  public static final int KW_INDEX = 132;
  public static final int KW_INDEXES = 133;
  public static final int KW_INNER = 134;
  public static final int KW_INPATH = 135;
  public static final int KW_INPUTDRIVER = 136;
  public static final int KW_INPUTFORMAT = 137;
  public static final int KW_INSERT = 138;
  public static final int KW_INT = 139;
  public static final int KW_INTERSECT = 140;
  public static final int KW_INTO = 141;
  public static final int KW_IS = 142;
  public static final int KW_ITEMS = 143;
  public static final int KW_JAR = 144;
  public static final int KW_JOIN = 145;
  public static final int KW_KEYS = 146;
  public static final int KW_KEY_TYPE = 147;
  public static final int KW_LATERAL = 148;
  public static final int KW_LEFT = 149;
  public static final int KW_LESS = 150;
  public static final int KW_LIKE = 151;
  public static final int KW_LIMIT = 152;
  public static final int KW_LINES = 153;
  public static final int KW_LOAD = 154;
  public static final int KW_LOCAL = 155;
  public static final int KW_LOCATION = 156;
  public static final int KW_LOCK = 157;
  public static final int KW_LOCKS = 158;
  public static final int KW_LOGICAL = 159;
  public static final int KW_LONG = 160;
  public static final int KW_MACRO = 161;
  public static final int KW_MAP = 162;
  public static final int KW_MAPJOIN = 163;
  public static final int KW_MATERIALIZED = 164;
  public static final int KW_METADATA = 165;
  public static final int KW_MINUS = 166;
  public static final int KW_MORE = 167;
  public static final int KW_MSCK = 168;
  public static final int KW_NONE = 169;
  public static final int KW_NOSCAN = 170;
  public static final int KW_NOT = 171;
  public static final int KW_NO_DROP = 172;
  public static final int KW_NULL = 173;
  public static final int KW_OF = 174;
  public static final int KW_OFFLINE = 175;
  public static final int KW_ON = 176;
  public static final int KW_OPTION = 177;
  public static final int KW_OR = 178;
  public static final int KW_ORDER = 179;
  public static final int KW_OUT = 180;
  public static final int KW_OUTER = 181;
  public static final int KW_OUTPUTDRIVER = 182;
  public static final int KW_OUTPUTFORMAT = 183;
  public static final int KW_OVER = 184;
  public static final int KW_OVERWRITE = 185;
  public static final int KW_OWNER = 186;
  public static final int KW_PARTIALSCAN = 187;
  public static final int KW_PARTITION = 188;
  public static final int KW_PARTITIONED = 189;
  public static final int KW_PARTITIONS = 190;
  public static final int KW_PERCENT = 191;
  public static final int KW_PLUS = 192;
  public static final int KW_PRECEDING = 193;
  public static final int KW_PRESERVE = 194;
  public static final int KW_PRETTY = 195;
  public static final int KW_PRINCIPALS = 196;
  public static final int KW_PROCEDURE = 197;
  public static final int KW_PROTECTION = 198;
  public static final int KW_PURGE = 199;
  public static final int KW_RANGE = 200;
  public static final int KW_READ = 201;
  public static final int KW_READONLY = 202;
  public static final int KW_READS = 203;
  public static final int KW_REBUILD = 204;
  public static final int KW_RECORDREADER = 205;
  public static final int KW_RECORDWRITER = 206;
  public static final int KW_REDUCE = 207;
  public static final int KW_REGEXP = 208;
  public static final int KW_RELOAD = 209;
  public static final int KW_RENAME = 210;
  public static final int KW_REPAIR = 211;
  public static final int KW_REPLACE = 212;
  public static final int KW_REPLICATION = 213;
  public static final int KW_RESTRICT = 214;
  public static final int KW_REVOKE = 215;
  public static final int KW_REWRITE = 216;
  public static final int KW_RIGHT = 217;
  public static final int KW_RLIKE = 218;
  public static final int KW_ROLE = 219;
  public static final int KW_ROLES = 220;
  public static final int KW_ROLLUP = 221;
  public static final int KW_ROW = 222;
  public static final int KW_ROWS = 223;
  public static final int KW_SCHEMA = 224;
  public static final int KW_SCHEMAS = 225;
  public static final int KW_SELECT = 226;
  public static final int KW_SEMI = 227;
  public static final int KW_SERDE = 228;
  public static final int KW_SERDEPROPERTIES = 229;
  public static final int KW_SERVER = 230;
  public static final int KW_SET = 231;
  public static final int KW_SETS = 232;
  public static final int KW_SHARED = 233;
  public static final int KW_SHOW = 234;
  public static final int KW_SHOW_DATABASE = 235;
  public static final int KW_SKEWED = 236;
  public static final int KW_SMALLINT = 237;
  public static final int KW_SORT = 238;
  public static final int KW_SORTED = 239;
  public static final int KW_SSL = 240;
  public static final int KW_STATISTICS = 241;
  public static final int KW_STORED = 242;
  public static final int KW_STREAMTABLE = 243;
  public static final int KW_STRING = 244;
  public static final int KW_STRUCT = 245;
  public static final int KW_TABLE = 246;
  public static final int KW_TABLES = 247;
  public static final int KW_TABLESAMPLE = 248;
  public static final int KW_TBLPROPERTIES = 249;
  public static final int KW_TEMPORARY = 250;
  public static final int KW_TERMINATED = 251;
  public static final int KW_THEN = 252;
  public static final int KW_TIMESTAMP = 253;
  public static final int KW_TINYINT = 254;
  public static final int KW_TO = 255;
  public static final int KW_TOUCH = 256;
  public static final int KW_TRANSACTIONS = 257;
  public static final int KW_TRANSFORM = 258;
  public static final int KW_TRIGGER = 259;
  public static final int KW_TRUE = 260;
  public static final int KW_TRUNCATE = 261;
  public static final int KW_UNARCHIVE = 262;
  public static final int KW_UNBOUNDED = 263;
  public static final int KW_UNDO = 264;
  public static final int KW_UNION = 265;
  public static final int KW_UNIONTYPE = 266;
  public static final int KW_UNIQUEJOIN = 267;
  public static final int KW_UNLOCK = 268;
  public static final int KW_UNSET = 269;
  public static final int KW_UNSIGNED = 270;
  public static final int KW_UPDATE = 271;
  public static final int KW_URI = 272;
  public static final int KW_USE = 273;
  public static final int KW_USER = 274;
  public static final int KW_USING = 275;
  public static final int KW_UTC = 276;
  public static final int KW_UTCTIMESTAMP = 277;
  public static final int KW_VALUES = 278;
  public static final int KW_VALUE_TYPE = 279;
  public static final int KW_VARCHAR = 280;
  public static final int KW_VIEW = 281;
  public static final int KW_WHEN = 282;
  public static final int KW_WHERE = 283;
  public static final int KW_WHILE = 284;
  public static final int KW_WINDOW = 285;
  public static final int KW_WITH = 286;
  public static final int LCURLY = 287;
  public static final int LESSTHAN = 288;
  public static final int LESSTHANOREQUALTO = 289;
  public static final int LPAREN = 290;
  public static final int LSQUARE = 291;
  public static final int Letter = 292;
  public static final int MINUS = 293;
  public static final int MOD = 294;
  public static final int NOTEQUAL = 295;
  public static final int Number = 296;
  public static final int PLUS = 297;
  public static final int QUESTION = 298;
  public static final int QuotedIdentifier = 299;
  public static final int RCURLY = 300;
  public static final int RPAREN = 301;
  public static final int RSQUARE = 302;
  public static final int RegexComponent = 303;
  public static final int SEMICOLON = 304;
  public static final int STAR = 305;
  public static final int SmallintLiteral = 306;
  public static final int StringLiteral = 307;
  public static final int TILDE = 308;
  public static final int TinyintLiteral = 309;
  public static final int WS = 310;

  protected boolean allowQuotedId() {
    return true;
  }

  // delegates
  // delegators
  public Lexer[] getDelegates() {
    return new Lexer[] {};
  }

  public HiveLexer() {
  }

  public HiveLexer(CharStream input) {
    this(input, new RecognizerSharedState());
  }

  public HiveLexer(CharStream input, RecognizerSharedState state) {
    super(input, state);
  }

  public String getGrammarFileName() {
    return "org/apache/hadoop/hive/ql/parse/HiveLexer.g";
  }

  // $ANTLR start "KW_TRUE"
  public final void mKW_TRUE() throws RecognitionException {
    try {
      int _type = KW_TRUE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:46:9: ( 'TRUE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:46:11: 'TRUE'
      {
        match("TRUE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_TRUE"

  // $ANTLR start "KW_FALSE"
  public final void mKW_FALSE() throws RecognitionException {
    try {
      int _type = KW_FALSE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:47:10: ( 'FALSE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:47:12: 'FALSE'
      {
        match("FALSE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_FALSE"

  // $ANTLR start "KW_ALL"
  public final void mKW_ALL() throws RecognitionException {
    try {
      int _type = KW_ALL;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:48:8: ( 'ALL' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:48:10: 'ALL'
      {
        match("ALL");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_ALL"

  // $ANTLR start "KW_NONE"
  public final void mKW_NONE() throws RecognitionException {
    try {
      int _type = KW_NONE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:49:8: ( 'NONE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:49:10: 'NONE'
      {
        match("NONE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_NONE"

  // $ANTLR start "KW_DEFAULT"
  public final void mKW_DEFAULT() throws RecognitionException {
    try {
      int _type = KW_DEFAULT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:50:12: ( 'DEFAULT' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:50:14: 'DEFAULT'
      {
        match("DEFAULT");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_DEFAULT"

  // $ANTLR start "KW_AND"
  public final void mKW_AND() throws RecognitionException {
    try {
      int _type = KW_AND;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:51:8: ( 'AND' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:51:10: 'AND'
      {
        match("AND");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_AND"

  // $ANTLR start "KW_OR"
  public final void mKW_OR() throws RecognitionException {
    try {
      int _type = KW_OR;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:52:7: ( 'OR' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:52:9: 'OR'
      {
        match("OR");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_OR"

  // $ANTLR start "KW_NOT"
  public final void mKW_NOT() throws RecognitionException {
    try {
      int _type = KW_NOT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:53:8: ( 'NOT' | '!' )
      int alt1 = 2;
      switch (input.LA(1)) {
        case 'N': {
          alt1 = 1;
        }
          break;
        case '!': {
          alt1 = 2;
        }
          break;
        default:
          NoViableAltException nvae = new NoViableAltException("", 1, 0, input);

          throw nvae;
      }

      switch (alt1) {
        case 1:
        // org/apache/hadoop/hive/ql/parse/HiveLexer.g:53:10: 'NOT'
        {
          match("NOT");
        }
          break;
        case 2:
        // org/apache/hadoop/hive/ql/parse/HiveLexer.g:53:18: '!'
        {
          match('!');
        }
          break;
      }
      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_NOT"

  // $ANTLR start "KW_LIKE"
  public final void mKW_LIKE() throws RecognitionException {
    try {
      int _type = KW_LIKE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:54:9: ( 'LIKE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:54:11: 'LIKE'
      {
        match("LIKE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_LIKE"

  // $ANTLR start "KW_IF"
  public final void mKW_IF() throws RecognitionException {
    try {
      int _type = KW_IF;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:56:7: ( 'IF' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:56:9: 'IF'
      {
        match("IF");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_IF"

  // $ANTLR start "KW_EXISTS"
  public final void mKW_EXISTS() throws RecognitionException {
    try {
      int _type = KW_EXISTS;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:57:11: ( 'EXISTS' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:57:13: 'EXISTS'
      {
        match("EXISTS");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_EXISTS"

  // $ANTLR start "KW_ASC"
  public final void mKW_ASC() throws RecognitionException {
    try {
      int _type = KW_ASC;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:59:8: ( 'ASC' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:59:10: 'ASC'
      {
        match("ASC");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_ASC"

  // $ANTLR start "KW_DESC"
  public final void mKW_DESC() throws RecognitionException {
    try {
      int _type = KW_DESC;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:60:9: ( 'DESC' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:60:11: 'DESC'
      {
        match("DESC");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_DESC"

  // $ANTLR start "KW_ORDER"
  public final void mKW_ORDER() throws RecognitionException {
    try {
      int _type = KW_ORDER;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:61:10: ( 'ORDER' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:61:12: 'ORDER'
      {
        match("ORDER");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_ORDER"

  // $ANTLR start "KW_GROUP"
  public final void mKW_GROUP() throws RecognitionException {
    try {
      int _type = KW_GROUP;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:62:10: ( 'GROUP' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:62:12: 'GROUP'
      {
        match("GROUP");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_GROUP"

  // $ANTLR start "KW_BY"
  public final void mKW_BY() throws RecognitionException {
    try {
      int _type = KW_BY;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:63:7: ( 'BY' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:63:9: 'BY'
      {
        match("BY");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_BY"

  // $ANTLR start "KW_HAVING"
  public final void mKW_HAVING() throws RecognitionException {
    try {
      int _type = KW_HAVING;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:64:11: ( 'HAVING' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:64:13: 'HAVING'
      {
        match("HAVING");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_HAVING"

  // $ANTLR start "KW_WHERE"
  public final void mKW_WHERE() throws RecognitionException {
    try {
      int _type = KW_WHERE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:65:10: ( 'WHERE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:65:12: 'WHERE'
      {
        match("WHERE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_WHERE"

  // $ANTLR start "KW_FROM"
  public final void mKW_FROM() throws RecognitionException {
    try {
      int _type = KW_FROM;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:66:9: ( 'FROM' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:66:11: 'FROM'
      {
        match("FROM");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_FROM"

  // $ANTLR start "KW_AS"
  public final void mKW_AS() throws RecognitionException {
    try {
      int _type = KW_AS;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:67:7: ( 'AS' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:67:9: 'AS'
      {
        match("AS");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_AS"

  // $ANTLR start "KW_SELECT"
  public final void mKW_SELECT() throws RecognitionException {
    try {
      int _type = KW_SELECT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:68:11: ( 'SELECT' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:68:13: 'SELECT'
      {
        match("SELECT");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_SELECT"

  // $ANTLR start "KW_DISTINCT"
  public final void mKW_DISTINCT() throws RecognitionException {
    try {
      int _type = KW_DISTINCT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:69:13: ( 'DISTINCT' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:69:15: 'DISTINCT'
      {
        match("DISTINCT");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_DISTINCT"

  // $ANTLR start "KW_INSERT"
  public final void mKW_INSERT() throws RecognitionException {
    try {
      int _type = KW_INSERT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:70:11: ( 'INSERT' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:70:13: 'INSERT'
      {
        match("INSERT");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_INSERT"

  // $ANTLR start "KW_OVERWRITE"
  public final void mKW_OVERWRITE() throws RecognitionException {
    try {
      int _type = KW_OVERWRITE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:71:14: ( 'OVERWRITE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:71:16: 'OVERWRITE'
      {
        match("OVERWRITE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_OVERWRITE"

  // $ANTLR start "KW_OUTER"
  public final void mKW_OUTER() throws RecognitionException {
    try {
      int _type = KW_OUTER;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:72:10: ( 'OUTER' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:72:12: 'OUTER'
      {
        match("OUTER");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_OUTER"

  // $ANTLR start "KW_UNIQUEJOIN"
  public final void mKW_UNIQUEJOIN() throws RecognitionException {
    try {
      int _type = KW_UNIQUEJOIN;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:73:15: ( 'UNIQUEJOIN' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:73:17: 'UNIQUEJOIN'
      {
        match("UNIQUEJOIN");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_UNIQUEJOIN"

  // $ANTLR start "KW_PRESERVE"
  public final void mKW_PRESERVE() throws RecognitionException {
    try {
      int _type = KW_PRESERVE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:74:13: ( 'PRESERVE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:74:15: 'PRESERVE'
      {
        match("PRESERVE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_PRESERVE"

  // $ANTLR start "KW_JOIN"
  public final void mKW_JOIN() throws RecognitionException {
    try {
      int _type = KW_JOIN;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:75:9: ( 'JOIN' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:75:11: 'JOIN'
      {
        match("JOIN");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_JOIN"

  // $ANTLR start "KW_LEFT"
  public final void mKW_LEFT() throws RecognitionException {
    try {
      int _type = KW_LEFT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:76:9: ( 'LEFT' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:76:11: 'LEFT'
      {
        match("LEFT");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_LEFT"

  // $ANTLR start "KW_RIGHT"
  public final void mKW_RIGHT() throws RecognitionException {
    try {
      int _type = KW_RIGHT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:77:10: ( 'RIGHT' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:77:12: 'RIGHT'
      {
        match("RIGHT");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_RIGHT"

  // $ANTLR start "KW_FULL"
  public final void mKW_FULL() throws RecognitionException {
    try {
      int _type = KW_FULL;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:78:9: ( 'FULL' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:78:11: 'FULL'
      {
        match("FULL");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_FULL"

  // $ANTLR start "KW_ON"
  public final void mKW_ON() throws RecognitionException {
    try {
      int _type = KW_ON;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:79:7: ( 'ON' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:79:9: 'ON'
      {
        match("ON");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_ON"

  // $ANTLR start "KW_PARTITION"
  public final void mKW_PARTITION() throws RecognitionException {
    try {
      int _type = KW_PARTITION;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:80:14: ( 'PARTITION' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:80:16: 'PARTITION'
      {
        match("PARTITION");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_PARTITION"

  // $ANTLR start "KW_PARTITIONS"
  public final void mKW_PARTITIONS() throws RecognitionException {
    try {
      int _type = KW_PARTITIONS;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:81:15: ( 'PARTITIONS' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:81:17: 'PARTITIONS'
      {
        match("PARTITIONS");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_PARTITIONS"

  // $ANTLR start "KW_TABLE"
  public final void mKW_TABLE() throws RecognitionException {
    try {
      int _type = KW_TABLE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:82:9: ( 'TABLE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:82:11: 'TABLE'
      {
        match("TABLE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_TABLE"

  // $ANTLR start "KW_TABLES"
  public final void mKW_TABLES() throws RecognitionException {
    try {
      int _type = KW_TABLES;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:83:10: ( 'TABLES' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:83:12: 'TABLES'
      {
        match("TABLES");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_TABLES"

  // $ANTLR start "KW_COLUMNS"
  public final void mKW_COLUMNS() throws RecognitionException {
    try {
      int _type = KW_COLUMNS;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:84:11: ( 'COLUMNS' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:84:13: 'COLUMNS'
      {
        match("COLUMNS");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_COLUMNS"

  // $ANTLR start "KW_INDEX"
  public final void mKW_INDEX() throws RecognitionException {
    try {
      int _type = KW_INDEX;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:85:9: ( 'INDEX' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:85:11: 'INDEX'
      {
        match("INDEX");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_INDEX"

  // $ANTLR start "KW_INDEXES"
  public final void mKW_INDEXES() throws RecognitionException {
    try {
      int _type = KW_INDEXES;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:86:11: ( 'INDEXES' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:86:13: 'INDEXES'
      {
        match("INDEXES");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_INDEXES"

  // $ANTLR start "KW_REBUILD"
  public final void mKW_REBUILD() throws RecognitionException {
    try {
      int _type = KW_REBUILD;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:87:11: ( 'REBUILD' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:87:13: 'REBUILD'
      {
        match("REBUILD");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_REBUILD"

  // $ANTLR start "KW_FUNCTIONS"
  public final void mKW_FUNCTIONS() throws RecognitionException {
    try {
      int _type = KW_FUNCTIONS;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:88:13: ( 'FUNCTIONS' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:88:15: 'FUNCTIONS'
      {
        match("FUNCTIONS");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_FUNCTIONS"

  // $ANTLR start "KW_SHOW"
  public final void mKW_SHOW() throws RecognitionException {
    try {
      int _type = KW_SHOW;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:89:8: ( 'SHOW' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:89:10: 'SHOW'
      {
        match("SHOW");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_SHOW"

  // $ANTLR start "KW_MSCK"
  public final void mKW_MSCK() throws RecognitionException {
    try {
      int _type = KW_MSCK;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:90:8: ( 'MSCK' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:90:10: 'MSCK'
      {
        match("MSCK");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_MSCK"

  // $ANTLR start "KW_REPAIR"
  public final void mKW_REPAIR() throws RecognitionException {
    try {
      int _type = KW_REPAIR;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:91:10: ( 'REPAIR' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:91:12: 'REPAIR'
      {
        match("REPAIR");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_REPAIR"

  // $ANTLR start "KW_DIRECTORY"
  public final void mKW_DIRECTORY() throws RecognitionException {
    try {
      int _type = KW_DIRECTORY;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:92:13: ( 'DIRECTORY' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:92:15: 'DIRECTORY'
      {
        match("DIRECTORY");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_DIRECTORY"

  // $ANTLR start "KW_LOCAL"
  public final void mKW_LOCAL() throws RecognitionException {
    try {
      int _type = KW_LOCAL;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:93:9: ( 'LOCAL' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:93:11: 'LOCAL'
      {
        match("LOCAL");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_LOCAL"

  // $ANTLR start "KW_TRANSFORM"
  public final void mKW_TRANSFORM() throws RecognitionException {
    try {
      int _type = KW_TRANSFORM;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:94:14: ( 'TRANSFORM' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:94:16: 'TRANSFORM'
      {
        match("TRANSFORM");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_TRANSFORM"

  // $ANTLR start "KW_USING"
  public final void mKW_USING() throws RecognitionException {
    try {
      int _type = KW_USING;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:95:9: ( 'USING' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:95:11: 'USING'
      {
        match("USING");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_USING"

  // $ANTLR start "KW_CLUSTER"
  public final void mKW_CLUSTER() throws RecognitionException {
    try {
      int _type = KW_CLUSTER;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:96:11: ( 'CLUSTER' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:96:13: 'CLUSTER'
      {
        match("CLUSTER");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_CLUSTER"

  // $ANTLR start "KW_DISTRIBUTE"
  public final void mKW_DISTRIBUTE() throws RecognitionException {
    try {
      int _type = KW_DISTRIBUTE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:97:14: ( 'DISTRIBUTE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:97:16: 'DISTRIBUTE'
      {
        match("DISTRIBUTE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_DISTRIBUTE"

  // $ANTLR start "KW_SORT"
  public final void mKW_SORT() throws RecognitionException {
    try {
      int _type = KW_SORT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:98:8: ( 'SORT' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:98:10: 'SORT'
      {
        match("SORT");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_SORT"

  // $ANTLR start "KW_UNION"
  public final void mKW_UNION() throws RecognitionException {
    try {
      int _type = KW_UNION;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:99:9: ( 'UNION' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:99:11: 'UNION'
      {
        match("UNION");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_UNION"

  // $ANTLR start "KW_LOAD"
  public final void mKW_LOAD() throws RecognitionException {
    try {
      int _type = KW_LOAD;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:100:8: ( 'LOAD' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:100:10: 'LOAD'
      {
        match("LOAD");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_LOAD"

  // $ANTLR start "KW_EXPORT"
  public final void mKW_EXPORT() throws RecognitionException {
    try {
      int _type = KW_EXPORT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:101:10: ( 'EXPORT' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:101:12: 'EXPORT'
      {
        match("EXPORT");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_EXPORT"

  // $ANTLR start "KW_IMPORT"
  public final void mKW_IMPORT() throws RecognitionException {
    try {
      int _type = KW_IMPORT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:102:10: ( 'IMPORT' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:102:12: 'IMPORT'
      {
        match("IMPORT");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_IMPORT"

  // $ANTLR start "KW_REPLICATION"
  public final void mKW_REPLICATION() throws RecognitionException {
    try {
      int _type = KW_REPLICATION;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:103:15: ( 'REPLICATION' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:103:17: 'REPLICATION'
      {
        match("REPLICATION");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_REPLICATION"

  // $ANTLR start "KW_METADATA"
  public final void mKW_METADATA() throws RecognitionException {
    try {
      int _type = KW_METADATA;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:104:12: ( 'METADATA' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:104:14: 'METADATA'
      {
        match("METADATA");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_METADATA"

  // $ANTLR start "KW_DATA"
  public final void mKW_DATA() throws RecognitionException {
    try {
      int _type = KW_DATA;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:105:8: ( 'DATA' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:105:10: 'DATA'
      {
        match("DATA");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_DATA"

  // $ANTLR start "KW_INPATH"
  public final void mKW_INPATH() throws RecognitionException {
    try {
      int _type = KW_INPATH;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:106:10: ( 'INPATH' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:106:12: 'INPATH'
      {
        match("INPATH");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_INPATH"

  // $ANTLR start "KW_IS"
  public final void mKW_IS() throws RecognitionException {
    try {
      int _type = KW_IS;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:107:6: ( 'IS' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:107:8: 'IS'
      {
        match("IS");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_IS"

  // $ANTLR start "KW_NULL"
  public final void mKW_NULL() throws RecognitionException {
    try {
      int _type = KW_NULL;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:108:8: ( 'NULL' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:108:10: 'NULL'
      {
        match("NULL");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_NULL"

  // $ANTLR start "KW_CREATE"
  public final void mKW_CREATE() throws RecognitionException {
    try {
      int _type = KW_CREATE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:109:10: ( 'CREATE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:109:12: 'CREATE'
      {
        match("CREATE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_CREATE"

  // $ANTLR start "KW_EXTERNAL"
  public final void mKW_EXTERNAL() throws RecognitionException {
    try {
      int _type = KW_EXTERNAL;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:110:12: ( 'EXTERNAL' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:110:14: 'EXTERNAL'
      {
        match("EXTERNAL");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_EXTERNAL"

  // $ANTLR start "KW_ALTER"
  public final void mKW_ALTER() throws RecognitionException {
    try {
      int _type = KW_ALTER;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:111:9: ( 'ALTER' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:111:11: 'ALTER'
      {
        match("ALTER");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_ALTER"

  // $ANTLR start "KW_CHANGE"
  public final void mKW_CHANGE() throws RecognitionException {
    try {
      int _type = KW_CHANGE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:112:10: ( 'CHANGE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:112:12: 'CHANGE'
      {
        match("CHANGE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_CHANGE"

  // $ANTLR start "KW_COLUMN"
  public final void mKW_COLUMN() throws RecognitionException {
    try {
      int _type = KW_COLUMN;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:113:10: ( 'COLUMN' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:113:12: 'COLUMN'
      {
        match("COLUMN");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_COLUMN"

  // $ANTLR start "KW_FIRST"
  public final void mKW_FIRST() throws RecognitionException {
    try {
      int _type = KW_FIRST;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:114:9: ( 'FIRST' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:114:11: 'FIRST'
      {
        match("FIRST");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_FIRST"

  // $ANTLR start "KW_AFTER"
  public final void mKW_AFTER() throws RecognitionException {
    try {
      int _type = KW_AFTER;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:115:9: ( 'AFTER' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:115:11: 'AFTER'
      {
        match("AFTER");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_AFTER"

  // $ANTLR start "KW_DESCRIBE"
  public final void mKW_DESCRIBE() throws RecognitionException {
    try {
      int _type = KW_DESCRIBE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:116:12: ( 'DESCRIBE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:116:14: 'DESCRIBE'
      {
        match("DESCRIBE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_DESCRIBE"

  // $ANTLR start "KW_DROP"
  public final void mKW_DROP() throws RecognitionException {
    try {
      int _type = KW_DROP;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:117:8: ( 'DROP' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:117:10: 'DROP'
      {
        match("DROP");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_DROP"

  // $ANTLR start "KW_RENAME"
  public final void mKW_RENAME() throws RecognitionException {
    try {
      int _type = KW_RENAME;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:118:10: ( 'RENAME' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:118:12: 'RENAME'
      {
        match("RENAME");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_RENAME"

  // $ANTLR start "KW_IGNORE"
  public final void mKW_IGNORE() throws RecognitionException {
    try {
      int _type = KW_IGNORE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:119:10: ( 'IGNORE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:119:12: 'IGNORE'
      {
        match("IGNORE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_IGNORE"

  // $ANTLR start "KW_PROTECTION"
  public final void mKW_PROTECTION() throws RecognitionException {
    try {
      int _type = KW_PROTECTION;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:120:14: ( 'PROTECTION' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:120:16: 'PROTECTION'
      {
        match("PROTECTION");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_PROTECTION"

  // $ANTLR start "KW_TO"
  public final void mKW_TO() throws RecognitionException {
    try {
      int _type = KW_TO;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:121:6: ( 'TO' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:121:8: 'TO'
      {
        match("TO");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_TO"

  // $ANTLR start "KW_COMMENT"
  public final void mKW_COMMENT() throws RecognitionException {
    try {
      int _type = KW_COMMENT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:122:11: ( 'COMMENT' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:122:13: 'COMMENT'
      {
        match("COMMENT");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_COMMENT"

  // $ANTLR start "KW_BOOLEAN"
  public final void mKW_BOOLEAN() throws RecognitionException {
    try {
      int _type = KW_BOOLEAN;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:123:11: ( 'BOOLEAN' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:123:13: 'BOOLEAN'
      {
        match("BOOLEAN");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_BOOLEAN"

  // $ANTLR start "KW_TINYINT"
  public final void mKW_TINYINT() throws RecognitionException {
    try {
      int _type = KW_TINYINT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:124:11: ( 'TINYINT' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:124:13: 'TINYINT'
      {
        match("TINYINT");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_TINYINT"

  // $ANTLR start "KW_SMALLINT"
  public final void mKW_SMALLINT() throws RecognitionException {
    try {
      int _type = KW_SMALLINT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:125:12: ( 'SMALLINT' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:125:14: 'SMALLINT'
      {
        match("SMALLINT");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_SMALLINT"

  // $ANTLR start "KW_INT"
  public final void mKW_INT() throws RecognitionException {
    try {
      int _type = KW_INT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:126:7: ( 'INT' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:126:9: 'INT'
      {
        match("INT");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_INT"

  // $ANTLR start "KW_BIGINT"
  public final void mKW_BIGINT() throws RecognitionException {
    try {
      int _type = KW_BIGINT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:127:10: ( 'BIGINT' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:127:12: 'BIGINT'
      {
        match("BIGINT");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_BIGINT"

  // $ANTLR start "KW_FLOAT"
  public final void mKW_FLOAT() throws RecognitionException {
    try {
      int _type = KW_FLOAT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:128:9: ( 'FLOAT' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:128:11: 'FLOAT'
      {
        match("FLOAT");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_FLOAT"

  // $ANTLR start "KW_DOUBLE"
  public final void mKW_DOUBLE() throws RecognitionException {
    try {
      int _type = KW_DOUBLE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:129:10: ( 'DOUBLE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:129:12: 'DOUBLE'
      {
        match("DOUBLE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_DOUBLE"

  // $ANTLR start "KW_DATE"
  public final void mKW_DATE() throws RecognitionException {
    try {
      int _type = KW_DATE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:130:8: ( 'DATE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:130:10: 'DATE'
      {
        match("DATE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_DATE"

  // $ANTLR start "KW_DATETIME"
  public final void mKW_DATETIME() throws RecognitionException {
    try {
      int _type = KW_DATETIME;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:131:12: ( 'DATETIME' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:131:14: 'DATETIME'
      {
        match("DATETIME");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_DATETIME"

  // $ANTLR start "KW_TIMESTAMP"
  public final void mKW_TIMESTAMP() throws RecognitionException {
    try {
      int _type = KW_TIMESTAMP;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:132:13: ( 'TIMESTAMP' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:132:15: 'TIMESTAMP'
      {
        match("TIMESTAMP");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_TIMESTAMP"

  // $ANTLR start "KW_DECIMAL"
  public final void mKW_DECIMAL() throws RecognitionException {
    try {
      int _type = KW_DECIMAL;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:133:11: ( 'DECIMAL' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:133:13: 'DECIMAL'
      {
        match("DECIMAL");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_DECIMAL"

  // $ANTLR start "KW_STRING"
  public final void mKW_STRING() throws RecognitionException {
    try {
      int _type = KW_STRING;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:134:10: ( 'STRING' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:134:12: 'STRING'
      {
        match("STRING");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_STRING"

  // $ANTLR start "KW_CHAR"
  public final void mKW_CHAR() throws RecognitionException {
    try {
      int _type = KW_CHAR;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:135:8: ( 'CHAR' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:135:10: 'CHAR'
      {
        match("CHAR");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_CHAR"

  // $ANTLR start "KW_VARCHAR"
  public final void mKW_VARCHAR() throws RecognitionException {
    try {
      int _type = KW_VARCHAR;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:136:11: ( 'VARCHAR' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:136:13: 'VARCHAR'
      {
        match("VARCHAR");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_VARCHAR"

  // $ANTLR start "KW_ARRAY"
  public final void mKW_ARRAY() throws RecognitionException {
    try {
      int _type = KW_ARRAY;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:137:9: ( 'ARRAY' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:137:11: 'ARRAY'
      {
        match("ARRAY");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_ARRAY"

  // $ANTLR start "KW_STRUCT"
  public final void mKW_STRUCT() throws RecognitionException {
    try {
      int _type = KW_STRUCT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:138:10: ( 'STRUCT' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:138:12: 'STRUCT'
      {
        match("STRUCT");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_STRUCT"

  // $ANTLR start "KW_MAP"
  public final void mKW_MAP() throws RecognitionException {
    try {
      int _type = KW_MAP;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:139:7: ( 'MAP' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:139:9: 'MAP'
      {
        match("MAP");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_MAP"

  // $ANTLR start "KW_UNIONTYPE"
  public final void mKW_UNIONTYPE() throws RecognitionException {
    try {
      int _type = KW_UNIONTYPE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:140:13: ( 'UNIONTYPE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:140:15: 'UNIONTYPE'
      {
        match("UNIONTYPE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_UNIONTYPE"

  // $ANTLR start "KW_REDUCE"
  public final void mKW_REDUCE() throws RecognitionException {
    try {
      int _type = KW_REDUCE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:141:10: ( 'REDUCE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:141:12: 'REDUCE'
      {
        match("REDUCE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_REDUCE"

  // $ANTLR start "KW_PARTITIONED"
  public final void mKW_PARTITIONED() throws RecognitionException {
    try {
      int _type = KW_PARTITIONED;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:142:15: ( 'PARTITIONED' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:142:17: 'PARTITIONED'
      {
        match("PARTITIONED");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_PARTITIONED"

  // $ANTLR start "KW_CLUSTERED"
  public final void mKW_CLUSTERED() throws RecognitionException {
    try {
      int _type = KW_CLUSTERED;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:143:13: ( 'CLUSTERED' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:143:15: 'CLUSTERED'
      {
        match("CLUSTERED");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_CLUSTERED"

  // $ANTLR start "KW_SORTED"
  public final void mKW_SORTED() throws RecognitionException {
    try {
      int _type = KW_SORTED;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:144:10: ( 'SORTED' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:144:12: 'SORTED'
      {
        match("SORTED");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_SORTED"

  // $ANTLR start "KW_INTO"
  public final void mKW_INTO() throws RecognitionException {
    try {
      int _type = KW_INTO;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:145:8: ( 'INTO' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:145:10: 'INTO'
      {
        match("INTO");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_INTO"

  // $ANTLR start "KW_BUCKETS"
  public final void mKW_BUCKETS() throws RecognitionException {
    try {
      int _type = KW_BUCKETS;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:146:11: ( 'BUCKETS' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:146:13: 'BUCKETS'
      {
        match("BUCKETS");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_BUCKETS"

  // $ANTLR start "KW_ROW"
  public final void mKW_ROW() throws RecognitionException {
    try {
      int _type = KW_ROW;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:147:7: ( 'ROW' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:147:9: 'ROW'
      {
        match("ROW");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_ROW"

  // $ANTLR start "KW_ROWS"
  public final void mKW_ROWS() throws RecognitionException {
    try {
      int _type = KW_ROWS;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:148:8: ( 'ROWS' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:148:10: 'ROWS'
      {
        match("ROWS");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_ROWS"

  // $ANTLR start "KW_FORMAT"
  public final void mKW_FORMAT() throws RecognitionException {
    try {
      int _type = KW_FORMAT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:149:10: ( 'FORMAT' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:149:12: 'FORMAT'
      {
        match("FORMAT");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_FORMAT"

  // $ANTLR start "KW_DELIMITED"
  public final void mKW_DELIMITED() throws RecognitionException {
    try {
      int _type = KW_DELIMITED;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:150:13: ( 'DELIMITED' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:150:15: 'DELIMITED'
      {
        match("DELIMITED");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_DELIMITED"

  // $ANTLR start "KW_FIELDS"
  public final void mKW_FIELDS() throws RecognitionException {
    try {
      int _type = KW_FIELDS;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:151:10: ( 'FIELDS' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:151:12: 'FIELDS'
      {
        match("FIELDS");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_FIELDS"

  // $ANTLR start "KW_TERMINATED"
  public final void mKW_TERMINATED() throws RecognitionException {
    try {
      int _type = KW_TERMINATED;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:152:14: ( 'TERMINATED' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:152:16: 'TERMINATED'
      {
        match("TERMINATED");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_TERMINATED"

  // $ANTLR start "KW_ESCAPED"
  public final void mKW_ESCAPED() throws RecognitionException {
    try {
      int _type = KW_ESCAPED;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:153:11: ( 'ESCAPED' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:153:13: 'ESCAPED'
      {
        match("ESCAPED");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_ESCAPED"

  // $ANTLR start "KW_COLLECTION"
  public final void mKW_COLLECTION() throws RecognitionException {
    try {
      int _type = KW_COLLECTION;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:154:14: ( 'COLLECTION' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:154:16: 'COLLECTION'
      {
        match("COLLECTION");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_COLLECTION"

  // $ANTLR start "KW_ITEMS"
  public final void mKW_ITEMS() throws RecognitionException {
    try {
      int _type = KW_ITEMS;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:155:9: ( 'ITEMS' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:155:11: 'ITEMS'
      {
        match("ITEMS");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_ITEMS"

  // $ANTLR start "KW_KEYS"
  public final void mKW_KEYS() throws RecognitionException {
    try {
      int _type = KW_KEYS;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:156:8: ( 'KEYS' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:156:10: 'KEYS'
      {
        match("KEYS");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_KEYS"

  // $ANTLR start "KW_KEY_TYPE"
  public final void mKW_KEY_TYPE() throws RecognitionException {
    try {
      int _type = KW_KEY_TYPE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:157:12: ( '$KEY$' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:157:14: '$KEY$'
      {
        match("$KEY$");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_KEY_TYPE"

  // $ANTLR start "KW_LINES"
  public final void mKW_LINES() throws RecognitionException {
    try {
      int _type = KW_LINES;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:158:9: ( 'LINES' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:158:11: 'LINES'
      {
        match("LINES");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_LINES"

  // $ANTLR start "KW_STORED"
  public final void mKW_STORED() throws RecognitionException {
    try {
      int _type = KW_STORED;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:159:10: ( 'STORED' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:159:12: 'STORED'
      {
        match("STORED");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_STORED"

  // $ANTLR start "KW_FILEFORMAT"
  public final void mKW_FILEFORMAT() throws RecognitionException {
    try {
      int _type = KW_FILEFORMAT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:160:14: ( 'FILEFORMAT' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:160:16: 'FILEFORMAT'
      {
        match("FILEFORMAT");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_FILEFORMAT"

  // $ANTLR start "KW_INPUTFORMAT"
  public final void mKW_INPUTFORMAT() throws RecognitionException {
    try {
      int _type = KW_INPUTFORMAT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:161:15: ( 'INPUTFORMAT' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:161:17: 'INPUTFORMAT'
      {
        match("INPUTFORMAT");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_INPUTFORMAT"

  // $ANTLR start "KW_OUTPUTFORMAT"
  public final void mKW_OUTPUTFORMAT() throws RecognitionException {
    try {
      int _type = KW_OUTPUTFORMAT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:162:16: ( 'OUTPUTFORMAT' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:162:18: 'OUTPUTFORMAT'
      {
        match("OUTPUTFORMAT");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_OUTPUTFORMAT"

  // $ANTLR start "KW_INPUTDRIVER"
  public final void mKW_INPUTDRIVER() throws RecognitionException {
    try {
      int _type = KW_INPUTDRIVER;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:163:15: ( 'INPUTDRIVER' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:163:17: 'INPUTDRIVER'
      {
        match("INPUTDRIVER");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_INPUTDRIVER"

  // $ANTLR start "KW_OUTPUTDRIVER"
  public final void mKW_OUTPUTDRIVER() throws RecognitionException {
    try {
      int _type = KW_OUTPUTDRIVER;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:164:16: ( 'OUTPUTDRIVER' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:164:18: 'OUTPUTDRIVER'
      {
        match("OUTPUTDRIVER");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_OUTPUTDRIVER"

  // $ANTLR start "KW_OFFLINE"
  public final void mKW_OFFLINE() throws RecognitionException {
    try {
      int _type = KW_OFFLINE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:165:11: ( 'OFFLINE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:165:13: 'OFFLINE'
      {
        match("OFFLINE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_OFFLINE"

  // $ANTLR start "KW_ENABLE"
  public final void mKW_ENABLE() throws RecognitionException {
    try {
      int _type = KW_ENABLE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:166:10: ( 'ENABLE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:166:12: 'ENABLE'
      {
        match("ENABLE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_ENABLE"

  // $ANTLR start "KW_DISABLE"
  public final void mKW_DISABLE() throws RecognitionException {
    try {
      int _type = KW_DISABLE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:167:11: ( 'DISABLE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:167:13: 'DISABLE'
      {
        match("DISABLE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_DISABLE"

  // $ANTLR start "KW_READONLY"
  public final void mKW_READONLY() throws RecognitionException {
    try {
      int _type = KW_READONLY;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:168:12: ( 'READONLY' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:168:14: 'READONLY'
      {
        match("READONLY");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_READONLY"

  // $ANTLR start "KW_NO_DROP"
  public final void mKW_NO_DROP() throws RecognitionException {
    try {
      int _type = KW_NO_DROP;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:169:11: ( 'NO_DROP' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:169:13: 'NO_DROP'
      {
        match("NO_DROP");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_NO_DROP"

  // $ANTLR start "KW_LOCATION"
  public final void mKW_LOCATION() throws RecognitionException {
    try {
      int _type = KW_LOCATION;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:170:12: ( 'LOCATION' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:170:14: 'LOCATION'
      {
        match("LOCATION");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_LOCATION"

  // $ANTLR start "KW_TABLESAMPLE"
  public final void mKW_TABLESAMPLE() throws RecognitionException {
    try {
      int _type = KW_TABLESAMPLE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:171:15: ( 'TABLESAMPLE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:171:17: 'TABLESAMPLE'
      {
        match("TABLESAMPLE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_TABLESAMPLE"

  // $ANTLR start "KW_BUCKET"
  public final void mKW_BUCKET() throws RecognitionException {
    try {
      int _type = KW_BUCKET;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:172:10: ( 'BUCKET' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:172:12: 'BUCKET'
      {
        match("BUCKET");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_BUCKET"

  // $ANTLR start "KW_OUT"
  public final void mKW_OUT() throws RecognitionException {
    try {
      int _type = KW_OUT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:173:7: ( 'OUT' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:173:9: 'OUT'
      {
        match("OUT");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_OUT"

  // $ANTLR start "KW_OF"
  public final void mKW_OF() throws RecognitionException {
    try {
      int _type = KW_OF;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:174:6: ( 'OF' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:174:8: 'OF'
      {
        match("OF");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_OF"

  // $ANTLR start "KW_PERCENT"
  public final void mKW_PERCENT() throws RecognitionException {
    try {
      int _type = KW_PERCENT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:175:11: ( 'PERCENT' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:175:13: 'PERCENT'
      {
        match("PERCENT");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_PERCENT"

  // $ANTLR start "KW_CAST"
  public final void mKW_CAST() throws RecognitionException {
    try {
      int _type = KW_CAST;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:176:8: ( 'CAST' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:176:10: 'CAST'
      {
        match("CAST");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_CAST"

  // $ANTLR start "KW_ADD"
  public final void mKW_ADD() throws RecognitionException {
    try {
      int _type = KW_ADD;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:177:7: ( 'ADD' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:177:9: 'ADD'
      {
        match("ADD");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_ADD"

  // $ANTLR start "KW_REPLACE"
  public final void mKW_REPLACE() throws RecognitionException {
    try {
      int _type = KW_REPLACE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:178:11: ( 'REPLACE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:178:13: 'REPLACE'
      {
        match("REPLACE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_REPLACE"

  // $ANTLR start "KW_RLIKE"
  public final void mKW_RLIKE() throws RecognitionException {
    try {
      int _type = KW_RLIKE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:179:9: ( 'RLIKE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:179:11: 'RLIKE'
      {
        match("RLIKE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_RLIKE"

  // $ANTLR start "KW_REGEXP"
  public final void mKW_REGEXP() throws RecognitionException {
    try {
      int _type = KW_REGEXP;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:180:10: ( 'REGEXP' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:180:12: 'REGEXP'
      {
        match("REGEXP");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_REGEXP"

  // $ANTLR start "KW_TEMPORARY"
  public final void mKW_TEMPORARY() throws RecognitionException {
    try {
      int _type = KW_TEMPORARY;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:181:13: ( 'TEMPORARY' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:181:15: 'TEMPORARY'
      {
        match("TEMPORARY");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_TEMPORARY"

  // $ANTLR start "KW_FUNCTION"
  public final void mKW_FUNCTION() throws RecognitionException {
    try {
      int _type = KW_FUNCTION;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:182:12: ( 'FUNCTION' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:182:14: 'FUNCTION'
      {
        match("FUNCTION");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_FUNCTION"

  // $ANTLR start "KW_MACRO"
  public final void mKW_MACRO() throws RecognitionException {
    try {
      int _type = KW_MACRO;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:183:9: ( 'MACRO' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:183:11: 'MACRO'
      {
        match("MACRO");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_MACRO"

  // $ANTLR start "KW_FILE"
  public final void mKW_FILE() throws RecognitionException {
    try {
      int _type = KW_FILE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:184:8: ( 'FILE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:184:10: 'FILE'
      {
        match("FILE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_FILE"

  // $ANTLR start "KW_JAR"
  public final void mKW_JAR() throws RecognitionException {
    try {
      int _type = KW_JAR;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:185:7: ( 'JAR' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:185:9: 'JAR'
      {
        match("JAR");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_JAR"

  // $ANTLR start "KW_EXPLAIN"
  public final void mKW_EXPLAIN() throws RecognitionException {
    try {
      int _type = KW_EXPLAIN;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:186:11: ( 'EXPLAIN' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:186:13: 'EXPLAIN'
      {
        match("EXPLAIN");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_EXPLAIN"

  // $ANTLR start "KW_EXTENDED"
  public final void mKW_EXTENDED() throws RecognitionException {
    try {
      int _type = KW_EXTENDED;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:187:12: ( 'EXTENDED' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:187:14: 'EXTENDED'
      {
        match("EXTENDED");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_EXTENDED"

  // $ANTLR start "KW_FORMATTED"
  public final void mKW_FORMATTED() throws RecognitionException {
    try {
      int _type = KW_FORMATTED;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:188:13: ( 'FORMATTED' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:188:15: 'FORMATTED'
      {
        match("FORMATTED");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_FORMATTED"

  // $ANTLR start "KW_PRETTY"
  public final void mKW_PRETTY() throws RecognitionException {
    try {
      int _type = KW_PRETTY;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:189:10: ( 'PRETTY' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:189:12: 'PRETTY'
      {
        match("PRETTY");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_PRETTY"

  // $ANTLR start "KW_DEPENDENCY"
  public final void mKW_DEPENDENCY() throws RecognitionException {
    try {
      int _type = KW_DEPENDENCY;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:190:14: ( 'DEPENDENCY' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:190:16: 'DEPENDENCY'
      {
        match("DEPENDENCY");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_DEPENDENCY"

  // $ANTLR start "KW_LOGICAL"
  public final void mKW_LOGICAL() throws RecognitionException {
    try {
      int _type = KW_LOGICAL;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:191:11: ( 'LOGICAL' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:191:13: 'LOGICAL'
      {
        match("LOGICAL");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_LOGICAL"

  // $ANTLR start "KW_SERDE"
  public final void mKW_SERDE() throws RecognitionException {
    try {
      int _type = KW_SERDE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:192:9: ( 'SERDE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:192:11: 'SERDE'
      {
        match("SERDE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_SERDE"

  // $ANTLR start "KW_WITH"
  public final void mKW_WITH() throws RecognitionException {
    try {
      int _type = KW_WITH;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:193:8: ( 'WITH' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:193:10: 'WITH'
      {
        match("WITH");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_WITH"

  // $ANTLR start "KW_DEFERRED"
  public final void mKW_DEFERRED() throws RecognitionException {
    try {
      int _type = KW_DEFERRED;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:194:12: ( 'DEFERRED' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:194:14: 'DEFERRED'
      {
        match("DEFERRED");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_DEFERRED"

  // $ANTLR start "KW_SERDEPROPERTIES"
  public final void mKW_SERDEPROPERTIES() throws RecognitionException {
    try {
      int _type = KW_SERDEPROPERTIES;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:195:19: ( 'SERDEPROPERTIES' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:195:21: 'SERDEPROPERTIES'
      {
        match("SERDEPROPERTIES");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_SERDEPROPERTIES"

  // $ANTLR start "KW_DBPROPERTIES"
  public final void mKW_DBPROPERTIES() throws RecognitionException {
    try {
      int _type = KW_DBPROPERTIES;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:196:16: ( 'DBPROPERTIES' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:196:18: 'DBPROPERTIES'
      {
        match("DBPROPERTIES");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_DBPROPERTIES"

  // $ANTLR start "KW_LIMIT"
  public final void mKW_LIMIT() throws RecognitionException {
    try {
      int _type = KW_LIMIT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:197:9: ( 'LIMIT' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:197:11: 'LIMIT'
      {
        match("LIMIT");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_LIMIT"

  // $ANTLR start "KW_SET"
  public final void mKW_SET() throws RecognitionException {
    try {
      int _type = KW_SET;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:198:7: ( 'SET' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:198:9: 'SET'
      {
        match("SET");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_SET"

  // $ANTLR start "KW_UNSET"
  public final void mKW_UNSET() throws RecognitionException {
    try {
      int _type = KW_UNSET;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:199:9: ( 'UNSET' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:199:11: 'UNSET'
      {
        match("UNSET");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_UNSET"

  // $ANTLR start "KW_TBLPROPERTIES"
  public final void mKW_TBLPROPERTIES() throws RecognitionException {
    try {
      int _type = KW_TBLPROPERTIES;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:200:17: ( 'TBLPROPERTIES' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:200:19: 'TBLPROPERTIES'
      {
        match("TBLPROPERTIES");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_TBLPROPERTIES"

  // $ANTLR start "KW_IDXPROPERTIES"
  public final void mKW_IDXPROPERTIES() throws RecognitionException {
    try {
      int _type = KW_IDXPROPERTIES;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:201:17: ( 'IDXPROPERTIES' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:201:19: 'IDXPROPERTIES'
      {
        match("IDXPROPERTIES");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_IDXPROPERTIES"

  // $ANTLR start "KW_VALUE_TYPE"
  public final void mKW_VALUE_TYPE() throws RecognitionException {
    try {
      int _type = KW_VALUE_TYPE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:202:14: ( '$VALUE$' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:202:16: '$VALUE$'
      {
        match("$VALUE$");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_VALUE_TYPE"

  // $ANTLR start "KW_ELEM_TYPE"
  public final void mKW_ELEM_TYPE() throws RecognitionException {
    try {
      int _type = KW_ELEM_TYPE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:203:13: ( '$ELEM$' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:203:15: '$ELEM$'
      {
        match("$ELEM$");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_ELEM_TYPE"

  // $ANTLR start "KW_DEFINED"
  public final void mKW_DEFINED() throws RecognitionException {
    try {
      int _type = KW_DEFINED;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:204:11: ( 'DEFINED' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:204:13: 'DEFINED'
      {
        match("DEFINED");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_DEFINED"

  // $ANTLR start "KW_CASE"
  public final void mKW_CASE() throws RecognitionException {
    try {
      int _type = KW_CASE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:205:8: ( 'CASE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:205:10: 'CASE'
      {
        match("CASE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_CASE"

  // $ANTLR start "KW_WHEN"
  public final void mKW_WHEN() throws RecognitionException {
    try {
      int _type = KW_WHEN;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:206:8: ( 'WHEN' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:206:10: 'WHEN'
      {
        match("WHEN");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_WHEN"

  // $ANTLR start "KW_THEN"
  public final void mKW_THEN() throws RecognitionException {
    try {
      int _type = KW_THEN;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:207:8: ( 'THEN' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:207:10: 'THEN'
      {
        match("THEN");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_THEN"

  // $ANTLR start "KW_ELSE"
  public final void mKW_ELSE() throws RecognitionException {
    try {
      int _type = KW_ELSE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:208:8: ( 'ELSE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:208:10: 'ELSE'
      {
        match("ELSE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_ELSE"

  // $ANTLR start "KW_END"
  public final void mKW_END() throws RecognitionException {
    try {
      int _type = KW_END;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:209:7: ( 'END' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:209:9: 'END'
      {
        match("END");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_END"

  // $ANTLR start "KW_MAPJOIN"
  public final void mKW_MAPJOIN() throws RecognitionException {
    try {
      int _type = KW_MAPJOIN;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:210:11: ( 'MAPJOIN' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:210:13: 'MAPJOIN'
      {
        match("MAPJOIN");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_MAPJOIN"

  // $ANTLR start "KW_STREAMTABLE"
  public final void mKW_STREAMTABLE() throws RecognitionException {
    try {
      int _type = KW_STREAMTABLE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:211:15: ( 'STREAMTABLE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:211:17: 'STREAMTABLE'
      {
        match("STREAMTABLE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_STREAMTABLE"

  // $ANTLR start "KW_HOLD_DDLTIME"
  public final void mKW_HOLD_DDLTIME() throws RecognitionException {
    try {
      int _type = KW_HOLD_DDLTIME;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:212:16: ( 'HOLD_DDLTIME' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:212:18: 'HOLD_DDLTIME'
      {
        match("HOLD_DDLTIME");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_HOLD_DDLTIME"

  // $ANTLR start "KW_CLUSTERSTATUS"
  public final void mKW_CLUSTERSTATUS() throws RecognitionException {
    try {
      int _type = KW_CLUSTERSTATUS;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:213:17: ( 'CLUSTERSTATUS' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:213:19: 'CLUSTERSTATUS'
      {
        match("CLUSTERSTATUS");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_CLUSTERSTATUS"

  // $ANTLR start "KW_UTC"
  public final void mKW_UTC() throws RecognitionException {
    try {
      int _type = KW_UTC;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:214:7: ( 'UTC' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:214:9: 'UTC'
      {
        match("UTC");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_UTC"

  // $ANTLR start "KW_UTCTIMESTAMP"
  public final void mKW_UTCTIMESTAMP() throws RecognitionException {
    try {
      int _type = KW_UTCTIMESTAMP;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:215:16: ( 'UTC_TMESTAMP' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:215:18: 'UTC_TMESTAMP'
      {
        match("UTC_TMESTAMP");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_UTCTIMESTAMP"

  // $ANTLR start "KW_LONG"
  public final void mKW_LONG() throws RecognitionException {
    try {
      int _type = KW_LONG;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:216:8: ( 'LONG' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:216:10: 'LONG'
      {
        match("LONG");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_LONG"

  // $ANTLR start "KW_DELETE"
  public final void mKW_DELETE() throws RecognitionException {
    try {
      int _type = KW_DELETE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:217:10: ( 'DELETE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:217:12: 'DELETE'
      {
        match("DELETE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_DELETE"

  // $ANTLR start "KW_PLUS"
  public final void mKW_PLUS() throws RecognitionException {
    try {
      int _type = KW_PLUS;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:218:8: ( 'PLUS' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:218:10: 'PLUS'
      {
        match("PLUS");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_PLUS"

  // $ANTLR start "KW_MINUS"
  public final void mKW_MINUS() throws RecognitionException {
    try {
      int _type = KW_MINUS;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:219:9: ( 'MINUS' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:219:11: 'MINUS'
      {
        match("MINUS");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_MINUS"

  // $ANTLR start "KW_FETCH"
  public final void mKW_FETCH() throws RecognitionException {
    try {
      int _type = KW_FETCH;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:220:9: ( 'FETCH' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:220:11: 'FETCH'
      {
        match("FETCH");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_FETCH"

  // $ANTLR start "KW_INTERSECT"
  public final void mKW_INTERSECT() throws RecognitionException {
    try {
      int _type = KW_INTERSECT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:221:13: ( 'INTERSECT' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:221:15: 'INTERSECT'
      {
        match("INTERSECT");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_INTERSECT"

  // $ANTLR start "KW_VIEW"
  public final void mKW_VIEW() throws RecognitionException {
    try {
      int _type = KW_VIEW;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:222:8: ( 'VIEW' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:222:10: 'VIEW'
      {
        match("VIEW");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_VIEW"

  // $ANTLR start "KW_IN"
  public final void mKW_IN() throws RecognitionException {
    try {
      int _type = KW_IN;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:223:6: ( 'IN' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:223:8: 'IN'
      {
        match("IN");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_IN"

  // $ANTLR start "KW_DATABASE"
  public final void mKW_DATABASE() throws RecognitionException {
    try {
      int _type = KW_DATABASE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:224:12: ( 'DATABASE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:224:14: 'DATABASE'
      {
        match("DATABASE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_DATABASE"

  // $ANTLR start "KW_DATABASES"
  public final void mKW_DATABASES() throws RecognitionException {
    try {
      int _type = KW_DATABASES;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:225:13: ( 'DATABASES' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:225:15: 'DATABASES'
      {
        match("DATABASES");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_DATABASES"

  // $ANTLR start "KW_MATERIALIZED"
  public final void mKW_MATERIALIZED() throws RecognitionException {
    try {
      int _type = KW_MATERIALIZED;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:226:16: ( 'MATERIALIZED' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:226:18: 'MATERIALIZED'
      {
        match("MATERIALIZED");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_MATERIALIZED"

  // $ANTLR start "KW_SCHEMA"
  public final void mKW_SCHEMA() throws RecognitionException {
    try {
      int _type = KW_SCHEMA;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:227:10: ( 'SCHEMA' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:227:12: 'SCHEMA'
      {
        match("SCHEMA");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_SCHEMA"

  // $ANTLR start "KW_SCHEMAS"
  public final void mKW_SCHEMAS() throws RecognitionException {
    try {
      int _type = KW_SCHEMAS;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:228:11: ( 'SCHEMAS' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:228:13: 'SCHEMAS'
      {
        match("SCHEMAS");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_SCHEMAS"

  // $ANTLR start "KW_GRANT"
  public final void mKW_GRANT() throws RecognitionException {
    try {
      int _type = KW_GRANT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:229:9: ( 'GRANT' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:229:11: 'GRANT'
      {
        match("GRANT");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_GRANT"

  // $ANTLR start "KW_REVOKE"
  public final void mKW_REVOKE() throws RecognitionException {
    try {
      int _type = KW_REVOKE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:230:10: ( 'REVOKE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:230:12: 'REVOKE'
      {
        match("REVOKE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_REVOKE"

  // $ANTLR start "KW_SSL"
  public final void mKW_SSL() throws RecognitionException {
    try {
      int _type = KW_SSL;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:231:7: ( 'SSL' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:231:9: 'SSL'
      {
        match("SSL");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_SSL"

  // $ANTLR start "KW_UNDO"
  public final void mKW_UNDO() throws RecognitionException {
    try {
      int _type = KW_UNDO;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:232:8: ( 'UNDO' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:232:10: 'UNDO'
      {
        match("UNDO");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_UNDO"

  // $ANTLR start "KW_LOCK"
  public final void mKW_LOCK() throws RecognitionException {
    try {
      int _type = KW_LOCK;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:233:8: ( 'LOCK' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:233:10: 'LOCK'
      {
        match("LOCK");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_LOCK"

  // $ANTLR start "KW_LOCKS"
  public final void mKW_LOCKS() throws RecognitionException {
    try {
      int _type = KW_LOCKS;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:234:9: ( 'LOCKS' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:234:11: 'LOCKS'
      {
        match("LOCKS");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_LOCKS"

  // $ANTLR start "KW_UNLOCK"
  public final void mKW_UNLOCK() throws RecognitionException {
    try {
      int _type = KW_UNLOCK;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:235:10: ( 'UNLOCK' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:235:12: 'UNLOCK'
      {
        match("UNLOCK");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_UNLOCK"

  // $ANTLR start "KW_SHARED"
  public final void mKW_SHARED() throws RecognitionException {
    try {
      int _type = KW_SHARED;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:236:10: ( 'SHARED' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:236:12: 'SHARED'
      {
        match("SHARED");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_SHARED"

  // $ANTLR start "KW_EXCLUSIVE"
  public final void mKW_EXCLUSIVE() throws RecognitionException {
    try {
      int _type = KW_EXCLUSIVE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:237:13: ( 'EXCLUSIVE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:237:15: 'EXCLUSIVE'
      {
        match("EXCLUSIVE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_EXCLUSIVE"

  // $ANTLR start "KW_PROCEDURE"
  public final void mKW_PROCEDURE() throws RecognitionException {
    try {
      int _type = KW_PROCEDURE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:238:13: ( 'PROCEDURE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:238:15: 'PROCEDURE'
      {
        match("PROCEDURE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_PROCEDURE"

  // $ANTLR start "KW_UNSIGNED"
  public final void mKW_UNSIGNED() throws RecognitionException {
    try {
      int _type = KW_UNSIGNED;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:239:12: ( 'UNSIGNED' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:239:14: 'UNSIGNED'
      {
        match("UNSIGNED");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_UNSIGNED"

  // $ANTLR start "KW_WHILE"
  public final void mKW_WHILE() throws RecognitionException {
    try {
      int _type = KW_WHILE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:240:9: ( 'WHILE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:240:11: 'WHILE'
      {
        match("WHILE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_WHILE"

  // $ANTLR start "KW_READ"
  public final void mKW_READ() throws RecognitionException {
    try {
      int _type = KW_READ;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:241:8: ( 'READ' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:241:10: 'READ'
      {
        match("READ");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_READ"

  // $ANTLR start "KW_READS"
  public final void mKW_READS() throws RecognitionException {
    try {
      int _type = KW_READS;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:242:9: ( 'READS' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:242:11: 'READS'
      {
        match("READS");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_READS"

  // $ANTLR start "KW_PURGE"
  public final void mKW_PURGE() throws RecognitionException {
    try {
      int _type = KW_PURGE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:243:9: ( 'PURGE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:243:11: 'PURGE'
      {
        match("PURGE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_PURGE"

  // $ANTLR start "KW_RANGE"
  public final void mKW_RANGE() throws RecognitionException {
    try {
      int _type = KW_RANGE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:244:9: ( 'RANGE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:244:11: 'RANGE'
      {
        match("RANGE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_RANGE"

  // $ANTLR start "KW_ANALYZE"
  public final void mKW_ANALYZE() throws RecognitionException {
    try {
      int _type = KW_ANALYZE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:245:11: ( 'ANALYZE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:245:13: 'ANALYZE'
      {
        match("ANALYZE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_ANALYZE"

  // $ANTLR start "KW_BEFORE"
  public final void mKW_BEFORE() throws RecognitionException {
    try {
      int _type = KW_BEFORE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:246:10: ( 'BEFORE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:246:12: 'BEFORE'
      {
        match("BEFORE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_BEFORE"

  // $ANTLR start "KW_BETWEEN"
  public final void mKW_BETWEEN() throws RecognitionException {
    try {
      int _type = KW_BETWEEN;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:247:11: ( 'BETWEEN' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:247:13: 'BETWEEN'
      {
        match("BETWEEN");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_BETWEEN"

  // $ANTLR start "KW_BOTH"
  public final void mKW_BOTH() throws RecognitionException {
    try {
      int _type = KW_BOTH;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:248:8: ( 'BOTH' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:248:10: 'BOTH'
      {
        match("BOTH");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_BOTH"

  // $ANTLR start "KW_BINARY"
  public final void mKW_BINARY() throws RecognitionException {
    try {
      int _type = KW_BINARY;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:249:10: ( 'BINARY' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:249:12: 'BINARY'
      {
        match("BINARY");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_BINARY"

  // $ANTLR start "KW_CROSS"
  public final void mKW_CROSS() throws RecognitionException {
    try {
      int _type = KW_CROSS;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:250:9: ( 'CROSS' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:250:11: 'CROSS'
      {
        match("CROSS");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_CROSS"

  // $ANTLR start "KW_CONTINUE"
  public final void mKW_CONTINUE() throws RecognitionException {
    try {
      int _type = KW_CONTINUE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:251:12: ( 'CONTINUE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:251:14: 'CONTINUE'
      {
        match("CONTINUE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_CONTINUE"

  // $ANTLR start "KW_CURSOR"
  public final void mKW_CURSOR() throws RecognitionException {
    try {
      int _type = KW_CURSOR;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:252:10: ( 'CURSOR' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:252:12: 'CURSOR'
      {
        match("CURSOR");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_CURSOR"

  // $ANTLR start "KW_TRIGGER"
  public final void mKW_TRIGGER() throws RecognitionException {
    try {
      int _type = KW_TRIGGER;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:253:11: ( 'TRIGGER' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:253:13: 'TRIGGER'
      {
        match("TRIGGER");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_TRIGGER"

  // $ANTLR start "KW_RECORDREADER"
  public final void mKW_RECORDREADER() throws RecognitionException {
    try {
      int _type = KW_RECORDREADER;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:254:16: ( 'RECORDREADER' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:254:18: 'RECORDREADER'
      {
        match("RECORDREADER");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_RECORDREADER"

  // $ANTLR start "KW_RECORDWRITER"
  public final void mKW_RECORDWRITER() throws RecognitionException {
    try {
      int _type = KW_RECORDWRITER;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:255:16: ( 'RECORDWRITER' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:255:18: 'RECORDWRITER'
      {
        match("RECORDWRITER");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_RECORDWRITER"

  // $ANTLR start "KW_SEMI"
  public final void mKW_SEMI() throws RecognitionException {
    try {
      int _type = KW_SEMI;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:256:8: ( 'SEMI' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:256:10: 'SEMI'
      {
        match("SEMI");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_SEMI"

  // $ANTLR start "KW_LATERAL"
  public final void mKW_LATERAL() throws RecognitionException {
    try {
      int _type = KW_LATERAL;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:257:11: ( 'LATERAL' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:257:13: 'LATERAL'
      {
        match("LATERAL");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_LATERAL"

  // $ANTLR start "KW_TOUCH"
  public final void mKW_TOUCH() throws RecognitionException {
    try {
      int _type = KW_TOUCH;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:258:9: ( 'TOUCH' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:258:11: 'TOUCH'
      {
        match("TOUCH");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_TOUCH"

  // $ANTLR start "KW_ARCHIVE"
  public final void mKW_ARCHIVE() throws RecognitionException {
    try {
      int _type = KW_ARCHIVE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:259:11: ( 'ARCHIVE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:259:13: 'ARCHIVE'
      {
        match("ARCHIVE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_ARCHIVE"

  // $ANTLR start "KW_UNARCHIVE"
  public final void mKW_UNARCHIVE() throws RecognitionException {
    try {
      int _type = KW_UNARCHIVE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:260:13: ( 'UNARCHIVE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:260:15: 'UNARCHIVE'
      {
        match("UNARCHIVE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_UNARCHIVE"

  // $ANTLR start "KW_COMPUTE"
  public final void mKW_COMPUTE() throws RecognitionException {
    try {
      int _type = KW_COMPUTE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:261:11: ( 'COMPUTE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:261:13: 'COMPUTE'
      {
        match("COMPUTE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_COMPUTE"

  // $ANTLR start "KW_STATISTICS"
  public final void mKW_STATISTICS() throws RecognitionException {
    try {
      int _type = KW_STATISTICS;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:262:14: ( 'STATISTICS' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:262:16: 'STATISTICS'
      {
        match("STATISTICS");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_STATISTICS"

  // $ANTLR start "KW_USE"
  public final void mKW_USE() throws RecognitionException {
    try {
      int _type = KW_USE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:263:7: ( 'USE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:263:9: 'USE'
      {
        match("USE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_USE"

  // $ANTLR start "KW_OPTION"
  public final void mKW_OPTION() throws RecognitionException {
    try {
      int _type = KW_OPTION;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:264:10: ( 'OPTION' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:264:12: 'OPTION'
      {
        match("OPTION");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_OPTION"

  // $ANTLR start "KW_CONCATENATE"
  public final void mKW_CONCATENATE() throws RecognitionException {
    try {
      int _type = KW_CONCATENATE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:265:15: ( 'CONCATENATE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:265:17: 'CONCATENATE'
      {
        match("CONCATENATE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_CONCATENATE"

  // $ANTLR start "KW_SHOW_DATABASE"
  public final void mKW_SHOW_DATABASE() throws RecognitionException {
    try {
      int _type = KW_SHOW_DATABASE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:266:17: ( 'SHOW_DATABASE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:266:19: 'SHOW_DATABASE'
      {
        match("SHOW_DATABASE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_SHOW_DATABASE"

  // $ANTLR start "KW_UPDATE"
  public final void mKW_UPDATE() throws RecognitionException {
    try {
      int _type = KW_UPDATE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:267:10: ( 'UPDATE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:267:12: 'UPDATE'
      {
        match("UPDATE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_UPDATE"

  // $ANTLR start "KW_RESTRICT"
  public final void mKW_RESTRICT() throws RecognitionException {
    try {
      int _type = KW_RESTRICT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:268:12: ( 'RESTRICT' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:268:14: 'RESTRICT'
      {
        match("RESTRICT");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_RESTRICT"

  // $ANTLR start "KW_CASCADE"
  public final void mKW_CASCADE() throws RecognitionException {
    try {
      int _type = KW_CASCADE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:269:11: ( 'CASCADE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:269:13: 'CASCADE'
      {
        match("CASCADE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_CASCADE"

  // $ANTLR start "KW_SKEWED"
  public final void mKW_SKEWED() throws RecognitionException {
    try {
      int _type = KW_SKEWED;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:270:10: ( 'SKEWED' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:270:12: 'SKEWED'
      {
        match("SKEWED");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_SKEWED"

  // $ANTLR start "KW_ROLLUP"
  public final void mKW_ROLLUP() throws RecognitionException {
    try {
      int _type = KW_ROLLUP;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:271:10: ( 'ROLLUP' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:271:12: 'ROLLUP'
      {
        match("ROLLUP");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_ROLLUP"

  // $ANTLR start "KW_CUBE"
  public final void mKW_CUBE() throws RecognitionException {
    try {
      int _type = KW_CUBE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:272:8: ( 'CUBE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:272:10: 'CUBE'
      {
        match("CUBE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_CUBE"

  // $ANTLR start "KW_DIRECTORIES"
  public final void mKW_DIRECTORIES() throws RecognitionException {
    try {
      int _type = KW_DIRECTORIES;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:273:15: ( 'DIRECTORIES' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:273:17: 'DIRECTORIES'
      {
        match("DIRECTORIES");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_DIRECTORIES"

  // $ANTLR start "KW_FOR"
  public final void mKW_FOR() throws RecognitionException {
    try {
      int _type = KW_FOR;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:274:7: ( 'FOR' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:274:9: 'FOR'
      {
        match("FOR");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_FOR"

  // $ANTLR start "KW_WINDOW"
  public final void mKW_WINDOW() throws RecognitionException {
    try {
      int _type = KW_WINDOW;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:275:10: ( 'WINDOW' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:275:12: 'WINDOW'
      {
        match("WINDOW");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_WINDOW"

  // $ANTLR start "KW_UNBOUNDED"
  public final void mKW_UNBOUNDED() throws RecognitionException {
    try {
      int _type = KW_UNBOUNDED;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:276:13: ( 'UNBOUNDED' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:276:15: 'UNBOUNDED'
      {
        match("UNBOUNDED");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_UNBOUNDED"

  // $ANTLR start "KW_PRECEDING"
  public final void mKW_PRECEDING() throws RecognitionException {
    try {
      int _type = KW_PRECEDING;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:277:13: ( 'PRECEDING' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:277:15: 'PRECEDING'
      {
        match("PRECEDING");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_PRECEDING"

  // $ANTLR start "KW_FOLLOWING"
  public final void mKW_FOLLOWING() throws RecognitionException {
    try {
      int _type = KW_FOLLOWING;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:278:13: ( 'FOLLOWING' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:278:15: 'FOLLOWING'
      {
        match("FOLLOWING");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_FOLLOWING"

  // $ANTLR start "KW_CURRENT"
  public final void mKW_CURRENT() throws RecognitionException {
    try {
      int _type = KW_CURRENT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:279:11: ( 'CURRENT' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:279:13: 'CURRENT'
      {
        match("CURRENT");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_CURRENT"

  // $ANTLR start "KW_CURRENT_DATE"
  public final void mKW_CURRENT_DATE() throws RecognitionException {
    try {
      int _type = KW_CURRENT_DATE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:280:16: ( 'CURRENT_DATE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:280:18: 'CURRENT_DATE'
      {
        match("CURRENT_DATE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_CURRENT_DATE"

  // $ANTLR start "KW_CURRENT_TIMESTAMP"
  public final void mKW_CURRENT_TIMESTAMP() throws RecognitionException {
    try {
      int _type = KW_CURRENT_TIMESTAMP;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:281:21: ( 'CURRENT_TIMESTAMP' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:281:23: 'CURRENT_TIMESTAMP'
      {
        match("CURRENT_TIMESTAMP");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_CURRENT_TIMESTAMP"

  // $ANTLR start "KW_LESS"
  public final void mKW_LESS() throws RecognitionException {
    try {
      int _type = KW_LESS;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:282:8: ( 'LESS' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:282:10: 'LESS'
      {
        match("LESS");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_LESS"

  // $ANTLR start "KW_MORE"
  public final void mKW_MORE() throws RecognitionException {
    try {
      int _type = KW_MORE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:283:8: ( 'MORE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:283:10: 'MORE'
      {
        match("MORE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_MORE"

  // $ANTLR start "KW_OVER"
  public final void mKW_OVER() throws RecognitionException {
    try {
      int _type = KW_OVER;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:284:8: ( 'OVER' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:284:10: 'OVER'
      {
        match("OVER");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_OVER"

  // $ANTLR start "KW_GROUPING"
  public final void mKW_GROUPING() throws RecognitionException {
    try {
      int _type = KW_GROUPING;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:285:12: ( 'GROUPING' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:285:14: 'GROUPING'
      {
        match("GROUPING");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_GROUPING"

  // $ANTLR start "KW_SETS"
  public final void mKW_SETS() throws RecognitionException {
    try {
      int _type = KW_SETS;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:286:8: ( 'SETS' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:286:10: 'SETS'
      {
        match("SETS");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_SETS"

  // $ANTLR start "KW_TRUNCATE"
  public final void mKW_TRUNCATE() throws RecognitionException {
    try {
      int _type = KW_TRUNCATE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:287:12: ( 'TRUNCATE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:287:14: 'TRUNCATE'
      {
        match("TRUNCATE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_TRUNCATE"

  // $ANTLR start "KW_NOSCAN"
  public final void mKW_NOSCAN() throws RecognitionException {
    try {
      int _type = KW_NOSCAN;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:288:10: ( 'NOSCAN' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:288:12: 'NOSCAN'
      {
        match("NOSCAN");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_NOSCAN"

  // $ANTLR start "KW_PARTIALSCAN"
  public final void mKW_PARTIALSCAN() throws RecognitionException {
    try {
      int _type = KW_PARTIALSCAN;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:289:15: ( 'PARTIALSCAN' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:289:17: 'PARTIALSCAN'
      {
        match("PARTIALSCAN");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_PARTIALSCAN"

  // $ANTLR start "KW_USER"
  public final void mKW_USER() throws RecognitionException {
    try {
      int _type = KW_USER;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:290:8: ( 'USER' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:290:10: 'USER'
      {
        match("USER");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_USER"

  // $ANTLR start "KW_ROLE"
  public final void mKW_ROLE() throws RecognitionException {
    try {
      int _type = KW_ROLE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:291:8: ( 'ROLE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:291:10: 'ROLE'
      {
        match("ROLE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_ROLE"

  // $ANTLR start "KW_ROLES"
  public final void mKW_ROLES() throws RecognitionException {
    try {
      int _type = KW_ROLES;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:292:9: ( 'ROLES' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:292:11: 'ROLES'
      {
        match("ROLES");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_ROLES"

  // $ANTLR start "KW_INNER"
  public final void mKW_INNER() throws RecognitionException {
    try {
      int _type = KW_INNER;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:293:9: ( 'INNER' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:293:11: 'INNER'
      {
        match("INNER");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_INNER"

  // $ANTLR start "KW_EXCHANGE"
  public final void mKW_EXCHANGE() throws RecognitionException {
    try {
      int _type = KW_EXCHANGE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:294:12: ( 'EXCHANGE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:294:14: 'EXCHANGE'
      {
        match("EXCHANGE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_EXCHANGE"

  // $ANTLR start "KW_URI"
  public final void mKW_URI() throws RecognitionException {
    try {
      int _type = KW_URI;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:295:7: ( 'URI' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:295:9: 'URI'
      {
        match("URI");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_URI"

  // $ANTLR start "KW_SERVER"
  public final void mKW_SERVER() throws RecognitionException {
    try {
      int _type = KW_SERVER;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:296:11: ( 'SERVER' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:296:13: 'SERVER'
      {
        match("SERVER");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_SERVER"

  // $ANTLR start "KW_ADMIN"
  public final void mKW_ADMIN() throws RecognitionException {
    try {
      int _type = KW_ADMIN;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:297:9: ( 'ADMIN' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:297:11: 'ADMIN'
      {
        match("ADMIN");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_ADMIN"

  // $ANTLR start "KW_OWNER"
  public final void mKW_OWNER() throws RecognitionException {
    try {
      int _type = KW_OWNER;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:298:9: ( 'OWNER' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:298:11: 'OWNER'
      {
        match("OWNER");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_OWNER"

  // $ANTLR start "KW_PRINCIPALS"
  public final void mKW_PRINCIPALS() throws RecognitionException {
    try {
      int _type = KW_PRINCIPALS;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:299:14: ( 'PRINCIPALS' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:299:16: 'PRINCIPALS'
      {
        match("PRINCIPALS");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_PRINCIPALS"

  // $ANTLR start "KW_COMPACT"
  public final void mKW_COMPACT() throws RecognitionException {
    try {
      int _type = KW_COMPACT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:300:11: ( 'COMPACT' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:300:13: 'COMPACT'
      {
        match("COMPACT");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_COMPACT"

  // $ANTLR start "KW_COMPACTIONS"
  public final void mKW_COMPACTIONS() throws RecognitionException {
    try {
      int _type = KW_COMPACTIONS;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:301:15: ( 'COMPACTIONS' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:301:17: 'COMPACTIONS'
      {
        match("COMPACTIONS");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_COMPACTIONS"

  // $ANTLR start "KW_TRANSACTIONS"
  public final void mKW_TRANSACTIONS() throws RecognitionException {
    try {
      int _type = KW_TRANSACTIONS;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:302:16: ( 'TRANSACTIONS' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:302:18: 'TRANSACTIONS'
      {
        match("TRANSACTIONS");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_TRANSACTIONS"

  // $ANTLR start "KW_REWRITE"
  public final void mKW_REWRITE() throws RecognitionException {
    try {
      int _type = KW_REWRITE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:303:12: ( 'REWRITE' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:303:14: 'REWRITE'
      {
        match("REWRITE");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_REWRITE"

  // $ANTLR start "KW_AUTHORIZATION"
  public final void mKW_AUTHORIZATION() throws RecognitionException {
    try {
      int _type = KW_AUTHORIZATION;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:304:17: ( 'AUTHORIZATION' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:304:19: 'AUTHORIZATION'
      {
        match("AUTHORIZATION");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_AUTHORIZATION"

  // $ANTLR start "KW_CONF"
  public final void mKW_CONF() throws RecognitionException {
    try {
      int _type = KW_CONF;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:305:8: ( 'CONF' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:305:10: 'CONF'
      {
        match("CONF");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_CONF"

  // $ANTLR start "KW_VALUES"
  public final void mKW_VALUES() throws RecognitionException {
    try {
      int _type = KW_VALUES;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:306:10: ( 'VALUES' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:306:12: 'VALUES'
      {
        match("VALUES");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_VALUES"

  // $ANTLR start "KW_RELOAD"
  public final void mKW_RELOAD() throws RecognitionException {
    try {
      int _type = KW_RELOAD;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:307:10: ( 'RELOAD' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:307:12: 'RELOAD'
      {
        match("RELOAD");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "KW_RELOAD"

  // $ANTLR start "DOT"
  public final void mDOT() throws RecognitionException {
    try {
      int _type = DOT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:312:5: ( '.' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:312:7: '.'
      {
        match('.');
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "DOT"

  // $ANTLR start "COLON"
  public final void mCOLON() throws RecognitionException {
    try {
      int _type = COLON;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:313:7: ( ':' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:313:9: ':'
      {
        match(':');
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "COLON"

  // $ANTLR start "COMMA"
  public final void mCOMMA() throws RecognitionException {
    try {
      int _type = COMMA;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:314:7: ( ',' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:314:9: ','
      {
        match(',');
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "COMMA"

  // $ANTLR start "SEMICOLON"
  public final void mSEMICOLON() throws RecognitionException {
    try {
      int _type = SEMICOLON;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:315:11: ( ';' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:315:13: ';'
      {
        match(';');
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "SEMICOLON"

  // $ANTLR start "LPAREN"
  public final void mLPAREN() throws RecognitionException {
    try {
      int _type = LPAREN;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:317:8: ( '(' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:317:10: '('
      {
        match('(');
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "LPAREN"

  // $ANTLR start "RPAREN"
  public final void mRPAREN() throws RecognitionException {
    try {
      int _type = RPAREN;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:318:8: ( ')' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:318:10: ')'
      {
        match(')');
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "RPAREN"

  // $ANTLR start "LSQUARE"
  public final void mLSQUARE() throws RecognitionException {
    try {
      int _type = LSQUARE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:319:9: ( '[' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:319:11: '['
      {
        match('[');
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "LSQUARE"

  // $ANTLR start "RSQUARE"
  public final void mRSQUARE() throws RecognitionException {
    try {
      int _type = RSQUARE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:320:9: ( ']' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:320:11: ']'
      {
        match(']');
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "RSQUARE"

  // $ANTLR start "LCURLY"
  public final void mLCURLY() throws RecognitionException {
    try {
      int _type = LCURLY;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:321:8: ( '{' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:321:10: '{'
      {
        match('{');
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "LCURLY"

  // $ANTLR start "RCURLY"
  public final void mRCURLY() throws RecognitionException {
    try {
      int _type = RCURLY;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:322:8: ( '}' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:322:10: '}'
      {
        match('}');
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "RCURLY"

  // $ANTLR start "EQUAL"
  public final void mEQUAL() throws RecognitionException {
    try {
      int _type = EQUAL;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:324:7: ( '=' | '==' )
      int alt2 = 2;
      switch (input.LA(1)) {
        case '=': {
          switch (input.LA(2)) {
            case '=': {
              alt2 = 2;
            }
              break;
            default:
              alt2 = 1;
          }
        }
          break;
        default:
          NoViableAltException nvae = new NoViableAltException("", 2, 0, input);

          throw nvae;
      }

      switch (alt2) {
        case 1:
        // org/apache/hadoop/hive/ql/parse/HiveLexer.g:324:9: '='
        {
          match('=');
        }
          break;
        case 2:
        // org/apache/hadoop/hive/ql/parse/HiveLexer.g:324:15: '=='
        {
          match("==");
        }
          break;
      }
      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "EQUAL"

  // $ANTLR start "EQUAL_NS"
  public final void mEQUAL_NS() throws RecognitionException {
    try {
      int _type = EQUAL_NS;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:325:10: ( '<=>' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:325:12: '<=>'
      {
        match("<=>");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "EQUAL_NS"

  // $ANTLR start "NOTEQUAL"
  public final void mNOTEQUAL() throws RecognitionException {
    try {
      int _type = NOTEQUAL;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:326:10: ( '<>' | '!=' )
      int alt3 = 2;
      switch (input.LA(1)) {
        case '<': {
          alt3 = 1;
        }
          break;
        case '!': {
          alt3 = 2;
        }
          break;
        default:
          NoViableAltException nvae = new NoViableAltException("", 3, 0, input);

          throw nvae;
      }

      switch (alt3) {
        case 1:
        // org/apache/hadoop/hive/ql/parse/HiveLexer.g:326:12: '<>'
        {
          match("<>");
        }
          break;
        case 2:
        // org/apache/hadoop/hive/ql/parse/HiveLexer.g:326:19: '!='
        {
          match("!=");
        }
          break;
      }
      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "NOTEQUAL"

  // $ANTLR start "LESSTHANOREQUALTO"
  public final void mLESSTHANOREQUALTO() throws RecognitionException {
    try {
      int _type = LESSTHANOREQUALTO;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:327:19: ( '<=' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:327:21: '<='
      {
        match("<=");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "LESSTHANOREQUALTO"

  // $ANTLR start "LESSTHAN"
  public final void mLESSTHAN() throws RecognitionException {
    try {
      int _type = LESSTHAN;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:328:10: ( '<' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:328:12: '<'
      {
        match('<');
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "LESSTHAN"

  // $ANTLR start "GREATERTHANOREQUALTO"
  public final void mGREATERTHANOREQUALTO() throws RecognitionException {
    try {
      int _type = GREATERTHANOREQUALTO;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:329:22: ( '>=' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:329:24: '>='
      {
        match(">=");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "GREATERTHANOREQUALTO"

  // $ANTLR start "GREATERTHAN"
  public final void mGREATERTHAN() throws RecognitionException {
    try {
      int _type = GREATERTHAN;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:330:13: ( '>' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:330:15: '>'
      {
        match('>');
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "GREATERTHAN"

  // $ANTLR start "DIVIDE"
  public final void mDIVIDE() throws RecognitionException {
    try {
      int _type = DIVIDE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:332:8: ( '/' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:332:10: '/'
      {
        match('/');
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "DIVIDE"

  // $ANTLR start "PLUS"
  public final void mPLUS() throws RecognitionException {
    try {
      int _type = PLUS;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:333:6: ( '+' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:333:8: '+'
      {
        match('+');
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "PLUS"

  // $ANTLR start "MINUS"
  public final void mMINUS() throws RecognitionException {
    try {
      int _type = MINUS;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:334:7: ( '-' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:334:9: '-'
      {
        match('-');
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "MINUS"

  // $ANTLR start "STAR"
  public final void mSTAR() throws RecognitionException {
    try {
      int _type = STAR;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:335:6: ( '*' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:335:8: '*'
      {
        match('*');
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "STAR"

  // $ANTLR start "MOD"
  public final void mMOD() throws RecognitionException {
    try {
      int _type = MOD;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:336:5: ( '%' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:336:7: '%'
      {
        match('%');
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "MOD"

  // $ANTLR start "DIV"
  public final void mDIV() throws RecognitionException {
    try {
      int _type = DIV;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:337:5: ( 'DIV' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:337:7: 'DIV'
      {
        match("DIV");
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "DIV"

  // $ANTLR start "AMPERSAND"
  public final void mAMPERSAND() throws RecognitionException {
    try {
      int _type = AMPERSAND;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:339:11: ( '&' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:339:13: '&'
      {
        match('&');
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "AMPERSAND"

  // $ANTLR start "TILDE"
  public final void mTILDE() throws RecognitionException {
    try {
      int _type = TILDE;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:340:7: ( '~' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:340:9: '~'
      {
        match('~');
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "TILDE"

  // $ANTLR start "BITWISEOR"
  public final void mBITWISEOR() throws RecognitionException {
    try {
      int _type = BITWISEOR;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:341:11: ( '|' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:341:13: '|'
      {
        match('|');
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "BITWISEOR"

  // $ANTLR start "BITWISEXOR"
  public final void mBITWISEXOR() throws RecognitionException {
    try {
      int _type = BITWISEXOR;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:342:12: ( '^' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:342:14: '^'
      {
        match('^');
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "BITWISEXOR"

  // $ANTLR start "QUESTION"
  public final void mQUESTION() throws RecognitionException {
    try {
      int _type = QUESTION;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:343:10: ( '?' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:343:12: '?'
      {
        match('?');
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "QUESTION"

  // $ANTLR start "DOLLAR"
  public final void mDOLLAR() throws RecognitionException {
    try {
      int _type = DOLLAR;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:344:8: ( '$' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:344:10: '$'
      {
        match('$');
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "DOLLAR"

  // $ANTLR start "Letter"
  public final void mLetter() throws RecognitionException {
    try {
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:349:5: ( 'a' .. 'z' | 'A' .. 'Z' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:
      {
        if ((input.LA(1) >= 'A' && input.LA(1) <= 'Z') || (input.LA(1) >= 'a' && input.LA(1) <= 'z')) {
          input.consume();
        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }
      }
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "Letter"

  // $ANTLR start "HexDigit"
  public final void mHexDigit() throws RecognitionException {
    try {
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:354:5: ( 'a' .. 'f' | 'A' .. 'F' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:
      {
        if ((input.LA(1) >= 'A' && input.LA(1) <= 'F') || (input.LA(1) >= 'a' && input.LA(1) <= 'f')) {
          input.consume();
        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }
      }
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "HexDigit"

  // $ANTLR start "Digit"
  public final void mDigit() throws RecognitionException {
    try {
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:359:5: ( '0' .. '9' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:
      {
        if ((input.LA(1) >= '0' && input.LA(1) <= '9')) {
          input.consume();
        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }
      }
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "Digit"

  // $ANTLR start "Exponent"
  public final void mExponent() throws RecognitionException {
    try {
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:365:5: ( ( 'e' | 'E' ) ( PLUS | MINUS )? ( Digit )+ )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:366:5: ( 'e' | 'E' ) ( PLUS | MINUS )? ( Digit )+
      {
        if (input.LA(1) == 'E' || input.LA(1) == 'e') {
          input.consume();
        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }

        // org/apache/hadoop/hive/ql/parse/HiveLexer.g:366:17: ( PLUS | MINUS )?
        int alt4 = 2;
        switch (input.LA(1)) {
          case '+':
          case '-': {
            alt4 = 1;
          }
            break;
        }

        switch (alt4) {
          case 1:
          // org/apache/hadoop/hive/ql/parse/HiveLexer.g:
          {
            if (input.LA(1) == '+' || input.LA(1) == '-') {
              input.consume();
            } else {
              MismatchedSetException mse = new MismatchedSetException(null, input);
              recover(mse);
              throw mse;
            }
          }
            break;
        }

        // org/apache/hadoop/hive/ql/parse/HiveLexer.g:366:33: ( Digit )+
        int cnt5 = 0;
        loop5: do {
          int alt5 = 2;
          switch (input.LA(1)) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9': {
              alt5 = 1;
            }
              break;
          }

          switch (alt5) {
            case 1:
            // org/apache/hadoop/hive/ql/parse/HiveLexer.g:
            {
              if ((input.LA(1) >= '0' && input.LA(1) <= '9')) {
                input.consume();
              } else {
                MismatchedSetException mse = new MismatchedSetException(null, input);
                recover(mse);
                throw mse;
              }
            }
              break;

            default:
              if (cnt5 >= 1) {
                break loop5;
              }
              EarlyExitException eee = new EarlyExitException(5, input);
              throw eee;
          }
          cnt5++;
        } while (true);
      }
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "Exponent"

  // $ANTLR start "RegexComponent"
  public final void mRegexComponent() throws RecognitionException {
    try {
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:371:5: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | PLUS | STAR | QUESTION | MINUS | DOT | LPAREN | RPAREN | LSQUARE | RSQUARE | LCURLY | RCURLY | BITWISEXOR | BITWISEOR | DOLLAR )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:
      {
        if (input.LA(1) == '$' || (input.LA(1) >= '(' && input.LA(1) <= '+')
            || (input.LA(1) >= '-' && input.LA(1) <= '.') || (input.LA(1) >= '0' && input.LA(1) <= '9')
            || input.LA(1) == '?' || (input.LA(1) >= 'A' && input.LA(1) <= '[')
            || (input.LA(1) >= ']' && input.LA(1) <= '_') || (input.LA(1) >= 'a' && input.LA(1) <= '}')) {
          input.consume();
        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }
      }
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "RegexComponent"

  // $ANTLR start "StringLiteral"
  public final void mStringLiteral() throws RecognitionException {
    try {
      int _type = StringLiteral;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:378:5: ( ( '\\'' (~ ( '\\'' | '\\\\' ) | ( '\\\\' . ) )* '\\'' | '\\\"' (~ ( '\\\"' | '\\\\' ) | ( '\\\\' . ) )* '\\\"' )+ )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:379:5: ( '\\'' (~ ( '\\'' | '\\\\' ) | ( '\\\\' . ) )* '\\'' | '\\\"' (~ ( '\\\"' | '\\\\' ) | ( '\\\\' . ) )* '\\\"' )+
      {
        // org/apache/hadoop/hive/ql/parse/HiveLexer.g:379:5: ( '\\'' (~ ( '\\'' | '\\\\' ) | ( '\\\\' . ) )* '\\'' | '\\\"' (~ ( '\\\"' | '\\\\' ) | ( '\\\\' . ) )* '\\\"' )+
        int cnt8 = 0;
        loop8: do {
          int alt8 = 3;
          switch (input.LA(1)) {
            case '\'': {
              alt8 = 1;
            }
              break;
            case '\"': {
              alt8 = 2;
            }
              break;
          }

          switch (alt8) {
            case 1:
            // org/apache/hadoop/hive/ql/parse/HiveLexer.g:379:7: '\\'' (~ ( '\\'' | '\\\\' ) | ( '\\\\' . ) )* '\\''
            {
              match('\'');

              // org/apache/hadoop/hive/ql/parse/HiveLexer.g:379:12: (~ ( '\\'' | '\\\\' ) | ( '\\\\' . ) )*
              loop6: do {
                int alt6 = 3;
                int LA6_0 = input.LA(1);

                if (((LA6_0 >= '\u0000' && LA6_0 <= '&') || (LA6_0 >= '(' && LA6_0 <= '[')
                    || (LA6_0 >= ']' && LA6_0 <= '\uFFFF'))) {
                  alt6 = 1;
                } else if ((LA6_0 == '\\')) {
                  alt6 = 2;
                }

                switch (alt6) {
                  case 1:
                  // org/apache/hadoop/hive/ql/parse/HiveLexer.g:379:14: ~ ( '\\'' | '\\\\' )
                  {
                    if ((input.LA(1) >= '\u0000' && input.LA(1) <= '&') || (input.LA(1) >= '(' && input.LA(1) <= '[')
                        || (input.LA(1) >= ']' && input.LA(1) <= '\uFFFF')) {
                      input.consume();
                    } else {
                      MismatchedSetException mse = new MismatchedSetException(null, input);
                      recover(mse);
                      throw mse;
                    }
                  }
                    break;
                  case 2:
                  // org/apache/hadoop/hive/ql/parse/HiveLexer.g:379:29: ( '\\\\' . )
                  {
                    // org/apache/hadoop/hive/ql/parse/HiveLexer.g:379:29: ( '\\\\' . )
                    // org/apache/hadoop/hive/ql/parse/HiveLexer.g:379:30: '\\\\' .
                    {
                      match('\\');

                      matchAny();
                    }
                  }
                    break;

                  default:
                    break loop6;
                }
              } while (true);

              match('\'');
            }
              break;
            case 2:
            // org/apache/hadoop/hive/ql/parse/HiveLexer.g:380:7: '\\\"' (~ ( '\\\"' | '\\\\' ) | ( '\\\\' . ) )* '\\\"'
            {
              match('\"');

              // org/apache/hadoop/hive/ql/parse/HiveLexer.g:380:12: (~ ( '\\\"' | '\\\\' ) | ( '\\\\' . ) )*
              loop7: do {
                int alt7 = 3;
                int LA7_0 = input.LA(1);

                if (((LA7_0 >= '\u0000' && LA7_0 <= '!') || (LA7_0 >= '#' && LA7_0 <= '[')
                    || (LA7_0 >= ']' && LA7_0 <= '\uFFFF'))) {
                  alt7 = 1;
                } else if ((LA7_0 == '\\')) {
                  alt7 = 2;
                }

                switch (alt7) {
                  case 1:
                  // org/apache/hadoop/hive/ql/parse/HiveLexer.g:380:14: ~ ( '\\\"' | '\\\\' )
                  {
                    if ((input.LA(1) >= '\u0000' && input.LA(1) <= '!') || (input.LA(1) >= '#' && input.LA(1) <= '[')
                        || (input.LA(1) >= ']' && input.LA(1) <= '\uFFFF')) {
                      input.consume();
                    } else {
                      MismatchedSetException mse = new MismatchedSetException(null, input);
                      recover(mse);
                      throw mse;
                    }
                  }
                    break;
                  case 2:
                  // org/apache/hadoop/hive/ql/parse/HiveLexer.g:380:29: ( '\\\\' . )
                  {
                    // org/apache/hadoop/hive/ql/parse/HiveLexer.g:380:29: ( '\\\\' . )
                    // org/apache/hadoop/hive/ql/parse/HiveLexer.g:380:30: '\\\\' .
                    {
                      match('\\');

                      matchAny();
                    }
                  }
                    break;

                  default:
                    break loop7;
                }
              } while (true);

              match('\"');
            }
              break;

            default:
              if (cnt8 >= 1) {
                break loop8;
              }
              EarlyExitException eee = new EarlyExitException(8, input);
              throw eee;
          }
          cnt8++;
        } while (true);
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "StringLiteral"

  // $ANTLR start "CharSetLiteral"
  public final void mCharSetLiteral() throws RecognitionException {
    try {
      int _type = CharSetLiteral;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:385:5: ( StringLiteral | '0' 'X' ( HexDigit | Digit )+ )
      int alt10 = 2;
      switch (input.LA(1)) {
        case '\"':
        case '\'': {
          alt10 = 1;
        }
          break;
        case '0': {
          alt10 = 2;
        }
          break;
        default:
          NoViableAltException nvae = new NoViableAltException("", 10, 0, input);

          throw nvae;
      }

      switch (alt10) {
        case 1:
        // org/apache/hadoop/hive/ql/parse/HiveLexer.g:386:5: StringLiteral
        {
          mStringLiteral();
        }
          break;
        case 2:
        // org/apache/hadoop/hive/ql/parse/HiveLexer.g:387:7: '0' 'X' ( HexDigit | Digit )+
        {
          match('0');

          match('X');

          // org/apache/hadoop/hive/ql/parse/HiveLexer.g:387:15: ( HexDigit | Digit )+
          int cnt9 = 0;
          loop9: do {
            int alt9 = 2;
            switch (input.LA(1)) {
              case '0':
              case '1':
              case '2':
              case '3':
              case '4':
              case '5':
              case '6':
              case '7':
              case '8':
              case '9':
              case 'A':
              case 'B':
              case 'C':
              case 'D':
              case 'E':
              case 'F':
              case 'a':
              case 'b':
              case 'c':
              case 'd':
              case 'e':
              case 'f': {
                alt9 = 1;
              }
                break;
            }

            switch (alt9) {
              case 1:
              // org/apache/hadoop/hive/ql/parse/HiveLexer.g:
              {
                if ((input.LA(1) >= '0' && input.LA(1) <= '9') || (input.LA(1) >= 'A' && input.LA(1) <= 'F')
                    || (input.LA(1) >= 'a' && input.LA(1) <= 'f')) {
                  input.consume();
                } else {
                  MismatchedSetException mse = new MismatchedSetException(null, input);
                  recover(mse);
                  throw mse;
                }
              }
                break;

              default:
                if (cnt9 >= 1) {
                  break loop9;
                }
                EarlyExitException eee = new EarlyExitException(9, input);
                throw eee;
            }
            cnt9++;
          } while (true);
        }
          break;
      }
      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "CharSetLiteral"

  // $ANTLR start "BigintLiteral"
  public final void mBigintLiteral() throws RecognitionException {
    try {
      int _type = BigintLiteral;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:391:5: ( ( Digit )+ 'L' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:392:5: ( Digit )+ 'L'
      {
        // org/apache/hadoop/hive/ql/parse/HiveLexer.g:392:5: ( Digit )+
        int cnt11 = 0;
        loop11: do {
          int alt11 = 2;
          switch (input.LA(1)) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9': {
              alt11 = 1;
            }
              break;
          }

          switch (alt11) {
            case 1:
            // org/apache/hadoop/hive/ql/parse/HiveLexer.g:
            {
              if ((input.LA(1) >= '0' && input.LA(1) <= '9')) {
                input.consume();
              } else {
                MismatchedSetException mse = new MismatchedSetException(null, input);
                recover(mse);
                throw mse;
              }
            }
              break;

            default:
              if (cnt11 >= 1) {
                break loop11;
              }
              EarlyExitException eee = new EarlyExitException(11, input);
              throw eee;
          }
          cnt11++;
        } while (true);

        match('L');
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "BigintLiteral"

  // $ANTLR start "SmallintLiteral"
  public final void mSmallintLiteral() throws RecognitionException {
    try {
      int _type = SmallintLiteral;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:396:5: ( ( Digit )+ 'S' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:397:5: ( Digit )+ 'S'
      {
        // org/apache/hadoop/hive/ql/parse/HiveLexer.g:397:5: ( Digit )+
        int cnt12 = 0;
        loop12: do {
          int alt12 = 2;
          switch (input.LA(1)) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9': {
              alt12 = 1;
            }
              break;
          }

          switch (alt12) {
            case 1:
            // org/apache/hadoop/hive/ql/parse/HiveLexer.g:
            {
              if ((input.LA(1) >= '0' && input.LA(1) <= '9')) {
                input.consume();
              } else {
                MismatchedSetException mse = new MismatchedSetException(null, input);
                recover(mse);
                throw mse;
              }
            }
              break;

            default:
              if (cnt12 >= 1) {
                break loop12;
              }
              EarlyExitException eee = new EarlyExitException(12, input);
              throw eee;
          }
          cnt12++;
        } while (true);

        match('S');
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "SmallintLiteral"

  // $ANTLR start "TinyintLiteral"
  public final void mTinyintLiteral() throws RecognitionException {
    try {
      int _type = TinyintLiteral;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:401:5: ( ( Digit )+ 'Y' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:402:5: ( Digit )+ 'Y'
      {
        // org/apache/hadoop/hive/ql/parse/HiveLexer.g:402:5: ( Digit )+
        int cnt13 = 0;
        loop13: do {
          int alt13 = 2;
          switch (input.LA(1)) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9': {
              alt13 = 1;
            }
              break;
          }

          switch (alt13) {
            case 1:
            // org/apache/hadoop/hive/ql/parse/HiveLexer.g:
            {
              if ((input.LA(1) >= '0' && input.LA(1) <= '9')) {
                input.consume();
              } else {
                MismatchedSetException mse = new MismatchedSetException(null, input);
                recover(mse);
                throw mse;
              }
            }
              break;

            default:
              if (cnt13 >= 1) {
                break loop13;
              }
              EarlyExitException eee = new EarlyExitException(13, input);
              throw eee;
          }
          cnt13++;
        } while (true);

        match('Y');
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "TinyintLiteral"

  // $ANTLR start "DecimalLiteral"
  public final void mDecimalLiteral() throws RecognitionException {
    try {
      int _type = DecimalLiteral;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:406:5: ( Number 'B' 'D' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:407:5: Number 'B' 'D'
      {
        mNumber();

        match('B');

        match('D');
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "DecimalLiteral"

  // $ANTLR start "ByteLengthLiteral"
  public final void mByteLengthLiteral() throws RecognitionException {
    try {
      int _type = ByteLengthLiteral;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:411:5: ( ( Digit )+ ( 'b' | 'B' | 'k' | 'K' | 'm' | 'M' | 'g' | 'G' ) )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:412:5: ( Digit )+ ( 'b' | 'B' | 'k' | 'K' | 'm' | 'M' | 'g' | 'G' )
      {
        // org/apache/hadoop/hive/ql/parse/HiveLexer.g:412:5: ( Digit )+
        int cnt14 = 0;
        loop14: do {
          int alt14 = 2;
          switch (input.LA(1)) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9': {
              alt14 = 1;
            }
              break;
          }

          switch (alt14) {
            case 1:
            // org/apache/hadoop/hive/ql/parse/HiveLexer.g:
            {
              if ((input.LA(1) >= '0' && input.LA(1) <= '9')) {
                input.consume();
              } else {
                MismatchedSetException mse = new MismatchedSetException(null, input);
                recover(mse);
                throw mse;
              }
            }
              break;

            default:
              if (cnt14 >= 1) {
                break loop14;
              }
              EarlyExitException eee = new EarlyExitException(14, input);
              throw eee;
          }
          cnt14++;
        } while (true);

        if (input.LA(1) == 'B' || input.LA(1) == 'G' || input.LA(1) == 'K' || input.LA(1) == 'M' || input.LA(1) == 'b'
            || input.LA(1) == 'g' || input.LA(1) == 'k' || input.LA(1) == 'm') {
          input.consume();
        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "ByteLengthLiteral"

  // $ANTLR start "Number"
  public final void mNumber() throws RecognitionException {
    try {
      int _type = Number;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:416:5: ( ( Digit )+ ( DOT ( Digit )* ( Exponent )? | Exponent )? )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:417:5: ( Digit )+ ( DOT ( Digit )* ( Exponent )? | Exponent )?
      {
        // org/apache/hadoop/hive/ql/parse/HiveLexer.g:417:5: ( Digit )+
        int cnt15 = 0;
        loop15: do {
          int alt15 = 2;
          switch (input.LA(1)) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9': {
              alt15 = 1;
            }
              break;
          }

          switch (alt15) {
            case 1:
            // org/apache/hadoop/hive/ql/parse/HiveLexer.g:
            {
              if ((input.LA(1) >= '0' && input.LA(1) <= '9')) {
                input.consume();
              } else {
                MismatchedSetException mse = new MismatchedSetException(null, input);
                recover(mse);
                throw mse;
              }
            }
              break;

            default:
              if (cnt15 >= 1) {
                break loop15;
              }
              EarlyExitException eee = new EarlyExitException(15, input);
              throw eee;
          }
          cnt15++;
        } while (true);

        // org/apache/hadoop/hive/ql/parse/HiveLexer.g:417:14: ( DOT ( Digit )* ( Exponent )? | Exponent )?
        int alt18 = 3;
        switch (input.LA(1)) {
          case '.': {
            alt18 = 1;
          }
            break;
          case 'E':
          case 'e': {
            alt18 = 2;
          }
            break;
        }

        switch (alt18) {
          case 1:
          // org/apache/hadoop/hive/ql/parse/HiveLexer.g:417:16: DOT ( Digit )* ( Exponent )?
          {
            mDOT();

            // org/apache/hadoop/hive/ql/parse/HiveLexer.g:417:20: ( Digit )*
            loop16: do {
              int alt16 = 2;
              switch (input.LA(1)) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9': {
                  alt16 = 1;
                }
                  break;
              }

              switch (alt16) {
                case 1:
                // org/apache/hadoop/hive/ql/parse/HiveLexer.g:
                {
                  if ((input.LA(1) >= '0' && input.LA(1) <= '9')) {
                    input.consume();
                  } else {
                    MismatchedSetException mse = new MismatchedSetException(null, input);
                    recover(mse);
                    throw mse;
                  }
                }
                  break;

                default:
                  break loop16;
              }
            } while (true);

            // org/apache/hadoop/hive/ql/parse/HiveLexer.g:417:29: ( Exponent )?
            int alt17 = 2;
            switch (input.LA(1)) {
              case 'E':
              case 'e': {
                alt17 = 1;
              }
                break;
            }

            switch (alt17) {
              case 1:
              // org/apache/hadoop/hive/ql/parse/HiveLexer.g:417:30: Exponent
              {
                mExponent();
              }
                break;
            }
          }
            break;
          case 2:
          // org/apache/hadoop/hive/ql/parse/HiveLexer.g:417:43: Exponent
          {
            mExponent();
          }
            break;
        }
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "Number"

  // $ANTLR start "Identifier"
  public final void mIdentifier() throws RecognitionException {
    try {
      int _type = Identifier;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:442:5: ( ( Letter | Digit ) ( Letter | Digit | '_' )* |{...}? QuotedIdentifier | '`' ( RegexComponent )+ '`' )
      int alt21 = 3;
      alt21 = dfa21.predict(input);
      switch (alt21) {
        case 1:
        // org/apache/hadoop/hive/ql/parse/HiveLexer.g:443:5: ( Letter | Digit ) ( Letter | Digit | '_' )*
        {
          if ((input.LA(1) >= '0' && input.LA(1) <= '9') || (input.LA(1) >= 'A' && input.LA(1) <= 'Z')
              || (input.LA(1) >= 'a' && input.LA(1) <= 'z')) {
            input.consume();
          } else {
            MismatchedSetException mse = new MismatchedSetException(null, input);
            recover(mse);
            throw mse;
          }

          // org/apache/hadoop/hive/ql/parse/HiveLexer.g:443:22: ( Letter | Digit | '_' )*
          loop19: do {
            int alt19 = 2;
            switch (input.LA(1)) {
              case '0':
              case '1':
              case '2':
              case '3':
              case '4':
              case '5':
              case '6':
              case '7':
              case '8':
              case '9':
              case 'A':
              case 'B':
              case 'C':
              case 'D':
              case 'E':
              case 'F':
              case 'G':
              case 'H':
              case 'I':
              case 'J':
              case 'K':
              case 'L':
              case 'M':
              case 'N':
              case 'O':
              case 'P':
              case 'Q':
              case 'R':
              case 'S':
              case 'T':
              case 'U':
              case 'V':
              case 'W':
              case 'X':
              case 'Y':
              case 'Z':
              case '_':
              case 'a':
              case 'b':
              case 'c':
              case 'd':
              case 'e':
              case 'f':
              case 'g':
              case 'h':
              case 'i':
              case 'j':
              case 'k':
              case 'l':
              case 'm':
              case 'n':
              case 'o':
              case 'p':
              case 'q':
              case 'r':
              case 's':
              case 't':
              case 'u':
              case 'v':
              case 'w':
              case 'x':
              case 'y':
              case 'z': {
                alt19 = 1;
              }
                break;
            }

            switch (alt19) {
              case 1:
              // org/apache/hadoop/hive/ql/parse/HiveLexer.g:
              {
                if ((input.LA(1) >= '0' && input.LA(1) <= '9') || (input.LA(1) >= 'A' && input.LA(1) <= 'Z')
                    || input.LA(1) == '_' || (input.LA(1) >= 'a' && input.LA(1) <= 'z')) {
                  input.consume();
                } else {
                  MismatchedSetException mse = new MismatchedSetException(null, input);
                  recover(mse);
                  throw mse;
                }
              }
                break;

              default:
                break loop19;
            }
          } while (true);
        }
          break;
        case 2:
        // org/apache/hadoop/hive/ql/parse/HiveLexer.g:444:7: {...}? QuotedIdentifier
        {
          if (!((allowQuotedId()))) {
            throw new FailedPredicateException(input, "Identifier", "allowQuotedId()");
          }

          mQuotedIdentifier();
        }
          break;
        case 3:
        // org/apache/hadoop/hive/ql/parse/HiveLexer.g:446:7: '`' ( RegexComponent )+ '`'
        {
          match('`');

          // org/apache/hadoop/hive/ql/parse/HiveLexer.g:446:11: ( RegexComponent )+
          int cnt20 = 0;
          loop20: do {
            int alt20 = 2;
            switch (input.LA(1)) {
              case '$':
              case '(':
              case ')':
              case '*':
              case '+':
              case '-':
              case '.':
              case '0':
              case '1':
              case '2':
              case '3':
              case '4':
              case '5':
              case '6':
              case '7':
              case '8':
              case '9':
              case '?':
              case 'A':
              case 'B':
              case 'C':
              case 'D':
              case 'E':
              case 'F':
              case 'G':
              case 'H':
              case 'I':
              case 'J':
              case 'K':
              case 'L':
              case 'M':
              case 'N':
              case 'O':
              case 'P':
              case 'Q':
              case 'R':
              case 'S':
              case 'T':
              case 'U':
              case 'V':
              case 'W':
              case 'X':
              case 'Y':
              case 'Z':
              case '[':
              case ']':
              case '^':
              case '_':
              case 'a':
              case 'b':
              case 'c':
              case 'd':
              case 'e':
              case 'f':
              case 'g':
              case 'h':
              case 'i':
              case 'j':
              case 'k':
              case 'l':
              case 'm':
              case 'n':
              case 'o':
              case 'p':
              case 'q':
              case 'r':
              case 's':
              case 't':
              case 'u':
              case 'v':
              case 'w':
              case 'x':
              case 'y':
              case 'z':
              case '{':
              case '|':
              case '}': {
                alt20 = 1;
              }
                break;
            }

            switch (alt20) {
              case 1:
              // org/apache/hadoop/hive/ql/parse/HiveLexer.g:
              {
                if (input.LA(1) == '$' || (input.LA(1) >= '(' && input.LA(1) <= '+')
                    || (input.LA(1) >= '-' && input.LA(1) <= '.') || (input.LA(1) >= '0' && input.LA(1) <= '9')
                    || input.LA(1) == '?' || (input.LA(1) >= 'A' && input.LA(1) <= '[')
                    || (input.LA(1) >= ']' && input.LA(1) <= '_') || (input.LA(1) >= 'a' && input.LA(1) <= '}')) {
                  input.consume();
                } else {
                  MismatchedSetException mse = new MismatchedSetException(null, input);
                  recover(mse);
                  throw mse;
                }
              }
                break;

              default:
                if (cnt20 >= 1) {
                  break loop20;
                }
                EarlyExitException eee = new EarlyExitException(20, input);
                throw eee;
            }
            cnt20++;
          } while (true);

          match('`');
        }
          break;
      }
      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "Identifier"

  // $ANTLR start "QuotedIdentifier"
  public final void mQuotedIdentifier() throws RecognitionException {
    try {
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:451:5: ( '`' ( '``' |~ ( '`' ) )* '`' )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:452:5: '`' ( '``' |~ ( '`' ) )* '`'
      {
        match('`');

        // org/apache/hadoop/hive/ql/parse/HiveLexer.g:452:10: ( '``' |~ ( '`' ) )*
        loop22: do {
          int alt22 = 3;
          int LA22_0 = input.LA(1);

          if ((LA22_0 == '`')) {
            switch (input.LA(2)) {
              case '`': {
                alt22 = 1;
              }
                break;
            }
          } else if (((LA22_0 >= '\u0000' && LA22_0 <= '_') || (LA22_0 >= 'a' && LA22_0 <= '\uFFFF'))) {
            alt22 = 2;
          }

          switch (alt22) {
            case 1:
            // org/apache/hadoop/hive/ql/parse/HiveLexer.g:452:12: '``'
            {
              match("``");
            }
              break;
            case 2:
            // org/apache/hadoop/hive/ql/parse/HiveLexer.g:452:19: ~ ( '`' )
            {
              if ((input.LA(1) >= '\u0000' && input.LA(1) <= '_') || (input.LA(1) >= 'a' && input.LA(1) <= '\uFFFF')) {
                input.consume();
              } else {
                MismatchedSetException mse = new MismatchedSetException(null, input);
                recover(mse);
                throw mse;
              }
            }
              break;

            default:
              break loop22;
          }
        } while (true);

        match('`');

        setText(getText().substring(1, getText().length() - 1).replaceAll("``", "`"));
      }
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "QuotedIdentifier"

  // $ANTLR start "CharSetName"
  public final void mCharSetName() throws RecognitionException {
    try {
      int _type = CharSetName;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:456:5: ( '_' ( Letter | Digit | '_' | '-' | '.' | ':' )+ )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:457:5: '_' ( Letter | Digit | '_' | '-' | '.' | ':' )+
      {
        match('_');

        // org/apache/hadoop/hive/ql/parse/HiveLexer.g:457:9: ( Letter | Digit | '_' | '-' | '.' | ':' )+
        int cnt23 = 0;
        loop23: do {
          int alt23 = 2;
          switch (input.LA(1)) {
            case '-':
            case '.':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case ':':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '_':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z': {
              alt23 = 1;
            }
              break;
          }

          switch (alt23) {
            case 1:
            // org/apache/hadoop/hive/ql/parse/HiveLexer.g:
            {
              if ((input.LA(1) >= '-' && input.LA(1) <= '.') || (input.LA(1) >= '0' && input.LA(1) <= ':')
                  || (input.LA(1) >= 'A' && input.LA(1) <= 'Z') || input.LA(1) == '_'
                  || (input.LA(1) >= 'a' && input.LA(1) <= 'z')) {
                input.consume();
              } else {
                MismatchedSetException mse = new MismatchedSetException(null, input);
                recover(mse);
                throw mse;
              }
            }
              break;

            default:
              if (cnt23 >= 1) {
                break loop23;
              }
              EarlyExitException eee = new EarlyExitException(23, input);
              throw eee;
          }
          cnt23++;
        } while (true);
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "CharSetName"

  // $ANTLR start "WS"
  public final void mWS() throws RecognitionException {
    try {
      int _type = WS;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:460:5: ( ( ' ' | '\\r' | '\\t' | '\\n' ) )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:460:8: ( ' ' | '\\r' | '\\t' | '\\n' )
      {
        if ((input.LA(1) >= '\t' && input.LA(1) <= '\n') || input.LA(1) == '\r' || input.LA(1) == ' ') {
          input.consume();
        } else {
          MismatchedSetException mse = new MismatchedSetException(null, input);
          recover(mse);
          throw mse;
        }

        _channel = HIDDEN;
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "WS"

  // $ANTLR start "COMMENT"
  public final void mCOMMENT() throws RecognitionException {
    try {
      int _type = COMMENT;
      int _channel = DEFAULT_TOKEN_CHANNEL;
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:464:3: ( '--' (~ ( '\\n' | '\\r' ) )* )
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:464:5: '--' (~ ( '\\n' | '\\r' ) )*
      {
        match("--");

        // org/apache/hadoop/hive/ql/parse/HiveLexer.g:464:10: (~ ( '\\n' | '\\r' ) )*
        loop24: do {
          int alt24 = 2;
          int LA24_0 = input.LA(1);

          if (((LA24_0 >= '\u0000' && LA24_0 <= '\t') || (LA24_0 >= '\u000B' && LA24_0 <= '\f')
              || (LA24_0 >= '\u000E' && LA24_0 <= '\uFFFF'))) {
            alt24 = 1;
          }

          switch (alt24) {
            case 1:
            // org/apache/hadoop/hive/ql/parse/HiveLexer.g:
            {
              if ((input.LA(1) >= '\u0000' && input.LA(1) <= '\t') || (input.LA(1) >= '\u000B' && input.LA(1) <= '\f')
                  || (input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF')) {
                input.consume();
              } else {
                MismatchedSetException mse = new MismatchedSetException(null, input);
                recover(mse);
                throw mse;
              }
            }
              break;

            default:
              break loop24;
          }
        } while (true);

        _channel = HIDDEN;
      }

      state.type = _type;
      state.channel = _channel;
    } finally {
      // do for sure before leaving
    }
  }
  // $ANTLR end "COMMENT"

  public void mTokens() throws RecognitionException {
    // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:8: ( KW_TRUE | KW_FALSE | KW_ALL | KW_NONE | KW_DEFAULT | KW_AND | KW_OR | KW_NOT | KW_LIKE | KW_IF | KW_EXISTS | KW_ASC | KW_DESC | KW_ORDER | KW_GROUP | KW_BY | KW_HAVING | KW_WHERE | KW_FROM | KW_AS | KW_SELECT | KW_DISTINCT | KW_INSERT | KW_OVERWRITE | KW_OUTER | KW_UNIQUEJOIN | KW_PRESERVE | KW_JOIN | KW_LEFT | KW_RIGHT | KW_FULL | KW_ON | KW_PARTITION | KW_PARTITIONS | KW_TABLE | KW_TABLES | KW_COLUMNS | KW_INDEX | KW_INDEXES | KW_REBUILD | KW_FUNCTIONS | KW_SHOW | KW_MSCK | KW_REPAIR | KW_DIRECTORY | KW_LOCAL | KW_TRANSFORM | KW_USING | KW_CLUSTER | KW_DISTRIBUTE | KW_SORT | KW_UNION | KW_LOAD | KW_EXPORT | KW_IMPORT | KW_REPLICATION | KW_METADATA | KW_DATA | KW_INPATH | KW_IS | KW_NULL | KW_CREATE | KW_EXTERNAL | KW_ALTER | KW_CHANGE | KW_COLUMN | KW_FIRST | KW_AFTER | KW_DESCRIBE | KW_DROP | KW_RENAME | KW_IGNORE | KW_PROTECTION | KW_TO | KW_COMMENT | KW_BOOLEAN | KW_TINYINT | KW_SMALLINT | KW_INT | KW_BIGINT | KW_FLOAT | KW_DOUBLE | KW_DATE | KW_DATETIME | KW_TIMESTAMP | KW_DECIMAL | KW_STRING | KW_CHAR | KW_VARCHAR | KW_ARRAY | KW_STRUCT | KW_MAP | KW_UNIONTYPE | KW_REDUCE | KW_PARTITIONED | KW_CLUSTERED | KW_SORTED | KW_INTO | KW_BUCKETS | KW_ROW | KW_ROWS | KW_FORMAT | KW_DELIMITED | KW_FIELDS | KW_TERMINATED | KW_ESCAPED | KW_COLLECTION | KW_ITEMS | KW_KEYS | KW_KEY_TYPE | KW_LINES | KW_STORED | KW_FILEFORMAT | KW_INPUTFORMAT | KW_OUTPUTFORMAT | KW_INPUTDRIVER | KW_OUTPUTDRIVER | KW_OFFLINE | KW_ENABLE | KW_DISABLE | KW_READONLY | KW_NO_DROP | KW_LOCATION | KW_TABLESAMPLE | KW_BUCKET | KW_OUT | KW_OF | KW_PERCENT | KW_CAST | KW_ADD | KW_REPLACE | KW_RLIKE | KW_REGEXP | KW_TEMPORARY | KW_FUNCTION | KW_MACRO | KW_FILE | KW_JAR | KW_EXPLAIN | KW_EXTENDED | KW_FORMATTED | KW_PRETTY | KW_DEPENDENCY | KW_LOGICAL | KW_SERDE | KW_WITH | KW_DEFERRED | KW_SERDEPROPERTIES | KW_DBPROPERTIES | KW_LIMIT | KW_SET | KW_UNSET | KW_TBLPROPERTIES | KW_IDXPROPERTIES | KW_VALUE_TYPE | KW_ELEM_TYPE | KW_DEFINED | KW_CASE | KW_WHEN | KW_THEN | KW_ELSE | KW_END | KW_MAPJOIN | KW_STREAMTABLE | KW_HOLD_DDLTIME | KW_CLUSTERSTATUS | KW_UTC | KW_UTCTIMESTAMP | KW_LONG | KW_DELETE | KW_PLUS | KW_MINUS | KW_FETCH | KW_INTERSECT | KW_VIEW | KW_IN | KW_DATABASE | KW_DATABASES | KW_MATERIALIZED | KW_SCHEMA | KW_SCHEMAS | KW_GRANT | KW_REVOKE | KW_SSL | KW_UNDO | KW_LOCK | KW_LOCKS | KW_UNLOCK | KW_SHARED | KW_EXCLUSIVE | KW_PROCEDURE | KW_UNSIGNED | KW_WHILE | KW_READ | KW_READS | KW_PURGE | KW_RANGE | KW_ANALYZE | KW_BEFORE | KW_BETWEEN | KW_BOTH | KW_BINARY | KW_CROSS | KW_CONTINUE | KW_CURSOR | KW_TRIGGER | KW_RECORDREADER | KW_RECORDWRITER | KW_SEMI | KW_LATERAL | KW_TOUCH | KW_ARCHIVE | KW_UNARCHIVE | KW_COMPUTE | KW_STATISTICS | KW_USE | KW_OPTION | KW_CONCATENATE | KW_SHOW_DATABASE | KW_UPDATE | KW_RESTRICT | KW_CASCADE | KW_SKEWED | KW_ROLLUP | KW_CUBE | KW_DIRECTORIES | KW_FOR | KW_WINDOW | KW_UNBOUNDED | KW_PRECEDING | KW_FOLLOWING | KW_CURRENT | KW_CURRENT_DATE | KW_CURRENT_TIMESTAMP | KW_LESS | KW_MORE | KW_OVER | KW_GROUPING | KW_SETS | KW_TRUNCATE | KW_NOSCAN | KW_PARTIALSCAN | KW_USER | KW_ROLE | KW_ROLES | KW_INNER | KW_EXCHANGE | KW_URI | KW_SERVER | KW_ADMIN | KW_OWNER | KW_PRINCIPALS | KW_COMPACT | KW_COMPACTIONS | KW_TRANSACTIONS | KW_REWRITE | KW_AUTHORIZATION | KW_CONF | KW_VALUES | KW_RELOAD | DOT | COLON | COMMA | SEMICOLON | LPAREN | RPAREN | LSQUARE | RSQUARE | LCURLY | RCURLY | EQUAL | EQUAL_NS | NOTEQUAL | LESSTHANOREQUALTO | LESSTHAN | GREATERTHANOREQUALTO | GREATERTHAN | DIVIDE | PLUS | MINUS | STAR | MOD | DIV | AMPERSAND | TILDE | BITWISEOR | BITWISEXOR | QUESTION | DOLLAR | StringLiteral | CharSetLiteral | BigintLiteral | SmallintLiteral | TinyintLiteral | DecimalLiteral | ByteLengthLiteral | Number | Identifier | CharSetName | WS | COMMENT )
    int alt25 = 301;
    alt25 = dfa25.predict(input);
    switch (alt25) {
      case 1:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:10: KW_TRUE
      {
        mKW_TRUE();
      }
        break;
      case 2:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:18: KW_FALSE
      {
        mKW_FALSE();
      }
        break;
      case 3:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:27: KW_ALL
      {
        mKW_ALL();
      }
        break;
      case 4:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:34: KW_NONE
      {
        mKW_NONE();
      }
        break;
      case 5:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:42: KW_DEFAULT
      {
        mKW_DEFAULT();
      }
        break;
      case 6:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:53: KW_AND
      {
        mKW_AND();
      }
        break;
      case 7:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:60: KW_OR
      {
        mKW_OR();
      }
        break;
      case 8:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:66: KW_NOT
      {
        mKW_NOT();
      }
        break;
      case 9:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:73: KW_LIKE
      {
        mKW_LIKE();
      }
        break;
      case 10:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:81: KW_IF
      {
        mKW_IF();
      }
        break;
      case 11:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:87: KW_EXISTS
      {
        mKW_EXISTS();
      }
        break;
      case 12:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:97: KW_ASC
      {
        mKW_ASC();
      }
        break;
      case 13:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:104: KW_DESC
      {
        mKW_DESC();
      }
        break;
      case 14:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:112: KW_ORDER
      {
        mKW_ORDER();
      }
        break;
      case 15:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:121: KW_GROUP
      {
        mKW_GROUP();
      }
        break;
      case 16:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:130: KW_BY
      {
        mKW_BY();
      }
        break;
      case 17:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:136: KW_HAVING
      {
        mKW_HAVING();
      }
        break;
      case 18:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:146: KW_WHERE
      {
        mKW_WHERE();
      }
        break;
      case 19:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:155: KW_FROM
      {
        mKW_FROM();
      }
        break;
      case 20:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:163: KW_AS
      {
        mKW_AS();
      }
        break;
      case 21:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:169: KW_SELECT
      {
        mKW_SELECT();
      }
        break;
      case 22:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:179: KW_DISTINCT
      {
        mKW_DISTINCT();
      }
        break;
      case 23:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:191: KW_INSERT
      {
        mKW_INSERT();
      }
        break;
      case 24:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:201: KW_OVERWRITE
      {
        mKW_OVERWRITE();
      }
        break;
      case 25:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:214: KW_OUTER
      {
        mKW_OUTER();
      }
        break;
      case 26:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:223: KW_UNIQUEJOIN
      {
        mKW_UNIQUEJOIN();
      }
        break;
      case 27:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:237: KW_PRESERVE
      {
        mKW_PRESERVE();
      }
        break;
      case 28:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:249: KW_JOIN
      {
        mKW_JOIN();
      }
        break;
      case 29:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:257: KW_LEFT
      {
        mKW_LEFT();
      }
        break;
      case 30:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:265: KW_RIGHT
      {
        mKW_RIGHT();
      }
        break;
      case 31:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:274: KW_FULL
      {
        mKW_FULL();
      }
        break;
      case 32:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:282: KW_ON
      {
        mKW_ON();
      }
        break;
      case 33:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:288: KW_PARTITION
      {
        mKW_PARTITION();
      }
        break;
      case 34:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:301: KW_PARTITIONS
      {
        mKW_PARTITIONS();
      }
        break;
      case 35:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:315: KW_TABLE
      {
        mKW_TABLE();
      }
        break;
      case 36:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:324: KW_TABLES
      {
        mKW_TABLES();
      }
        break;
      case 37:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:334: KW_COLUMNS
      {
        mKW_COLUMNS();
      }
        break;
      case 38:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:345: KW_INDEX
      {
        mKW_INDEX();
      }
        break;
      case 39:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:354: KW_INDEXES
      {
        mKW_INDEXES();
      }
        break;
      case 40:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:365: KW_REBUILD
      {
        mKW_REBUILD();
      }
        break;
      case 41:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:376: KW_FUNCTIONS
      {
        mKW_FUNCTIONS();
      }
        break;
      case 42:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:389: KW_SHOW
      {
        mKW_SHOW();
      }
        break;
      case 43:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:397: KW_MSCK
      {
        mKW_MSCK();
      }
        break;
      case 44:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:405: KW_REPAIR
      {
        mKW_REPAIR();
      }
        break;
      case 45:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:415: KW_DIRECTORY
      {
        mKW_DIRECTORY();
      }
        break;
      case 46:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:428: KW_LOCAL
      {
        mKW_LOCAL();
      }
        break;
      case 47:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:437: KW_TRANSFORM
      {
        mKW_TRANSFORM();
      }
        break;
      case 48:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:450: KW_USING
      {
        mKW_USING();
      }
        break;
      case 49:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:459: KW_CLUSTER
      {
        mKW_CLUSTER();
      }
        break;
      case 50:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:470: KW_DISTRIBUTE
      {
        mKW_DISTRIBUTE();
      }
        break;
      case 51:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:484: KW_SORT
      {
        mKW_SORT();
      }
        break;
      case 52:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:492: KW_UNION
      {
        mKW_UNION();
      }
        break;
      case 53:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:501: KW_LOAD
      {
        mKW_LOAD();
      }
        break;
      case 54:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:509: KW_EXPORT
      {
        mKW_EXPORT();
      }
        break;
      case 55:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:519: KW_IMPORT
      {
        mKW_IMPORT();
      }
        break;
      case 56:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:529: KW_REPLICATION
      {
        mKW_REPLICATION();
      }
        break;
      case 57:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:544: KW_METADATA
      {
        mKW_METADATA();
      }
        break;
      case 58:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:556: KW_DATA
      {
        mKW_DATA();
      }
        break;
      case 59:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:564: KW_INPATH
      {
        mKW_INPATH();
      }
        break;
      case 60:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:574: KW_IS
      {
        mKW_IS();
      }
        break;
      case 61:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:580: KW_NULL
      {
        mKW_NULL();
      }
        break;
      case 62:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:588: KW_CREATE
      {
        mKW_CREATE();
      }
        break;
      case 63:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:598: KW_EXTERNAL
      {
        mKW_EXTERNAL();
      }
        break;
      case 64:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:610: KW_ALTER
      {
        mKW_ALTER();
      }
        break;
      case 65:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:619: KW_CHANGE
      {
        mKW_CHANGE();
      }
        break;
      case 66:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:629: KW_COLUMN
      {
        mKW_COLUMN();
      }
        break;
      case 67:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:639: KW_FIRST
      {
        mKW_FIRST();
      }
        break;
      case 68:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:648: KW_AFTER
      {
        mKW_AFTER();
      }
        break;
      case 69:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:657: KW_DESCRIBE
      {
        mKW_DESCRIBE();
      }
        break;
      case 70:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:669: KW_DROP
      {
        mKW_DROP();
      }
        break;
      case 71:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:677: KW_RENAME
      {
        mKW_RENAME();
      }
        break;
      case 72:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:687: KW_IGNORE
      {
        mKW_IGNORE();
      }
        break;
      case 73:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:697: KW_PROTECTION
      {
        mKW_PROTECTION();
      }
        break;
      case 74:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:711: KW_TO
      {
        mKW_TO();
      }
        break;
      case 75:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:717: KW_COMMENT
      {
        mKW_COMMENT();
      }
        break;
      case 76:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:728: KW_BOOLEAN
      {
        mKW_BOOLEAN();
      }
        break;
      case 77:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:739: KW_TINYINT
      {
        mKW_TINYINT();
      }
        break;
      case 78:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:750: KW_SMALLINT
      {
        mKW_SMALLINT();
      }
        break;
      case 79:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:762: KW_INT
      {
        mKW_INT();
      }
        break;
      case 80:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:769: KW_BIGINT
      {
        mKW_BIGINT();
      }
        break;
      case 81:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:779: KW_FLOAT
      {
        mKW_FLOAT();
      }
        break;
      case 82:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:788: KW_DOUBLE
      {
        mKW_DOUBLE();
      }
        break;
      case 83:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:798: KW_DATE
      {
        mKW_DATE();
      }
        break;
      case 84:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:806: KW_DATETIME
      {
        mKW_DATETIME();
      }
        break;
      case 85:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:818: KW_TIMESTAMP
      {
        mKW_TIMESTAMP();
      }
        break;
      case 86:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:831: KW_DECIMAL
      {
        mKW_DECIMAL();
      }
        break;
      case 87:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:842: KW_STRING
      {
        mKW_STRING();
      }
        break;
      case 88:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:852: KW_CHAR
      {
        mKW_CHAR();
      }
        break;
      case 89:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:860: KW_VARCHAR
      {
        mKW_VARCHAR();
      }
        break;
      case 90:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:871: KW_ARRAY
      {
        mKW_ARRAY();
      }
        break;
      case 91:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:880: KW_STRUCT
      {
        mKW_STRUCT();
      }
        break;
      case 92:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:890: KW_MAP
      {
        mKW_MAP();
      }
        break;
      case 93:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:897: KW_UNIONTYPE
      {
        mKW_UNIONTYPE();
      }
        break;
      case 94:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:910: KW_REDUCE
      {
        mKW_REDUCE();
      }
        break;
      case 95:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:920: KW_PARTITIONED
      {
        mKW_PARTITIONED();
      }
        break;
      case 96:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:935: KW_CLUSTERED
      {
        mKW_CLUSTERED();
      }
        break;
      case 97:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:948: KW_SORTED
      {
        mKW_SORTED();
      }
        break;
      case 98:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:958: KW_INTO
      {
        mKW_INTO();
      }
        break;
      case 99:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:966: KW_BUCKETS
      {
        mKW_BUCKETS();
      }
        break;
      case 100:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:977: KW_ROW
      {
        mKW_ROW();
      }
        break;
      case 101:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:984: KW_ROWS
      {
        mKW_ROWS();
      }
        break;
      case 102:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:992: KW_FORMAT
      {
        mKW_FORMAT();
      }
        break;
      case 103:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1002: KW_DELIMITED
      {
        mKW_DELIMITED();
      }
        break;
      case 104:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1015: KW_FIELDS
      {
        mKW_FIELDS();
      }
        break;
      case 105:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1025: KW_TERMINATED
      {
        mKW_TERMINATED();
      }
        break;
      case 106:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1039: KW_ESCAPED
      {
        mKW_ESCAPED();
      }
        break;
      case 107:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1050: KW_COLLECTION
      {
        mKW_COLLECTION();
      }
        break;
      case 108:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1064: KW_ITEMS
      {
        mKW_ITEMS();
      }
        break;
      case 109:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1073: KW_KEYS
      {
        mKW_KEYS();
      }
        break;
      case 110:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1081: KW_KEY_TYPE
      {
        mKW_KEY_TYPE();
      }
        break;
      case 111:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1093: KW_LINES
      {
        mKW_LINES();
      }
        break;
      case 112:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1102: KW_STORED
      {
        mKW_STORED();
      }
        break;
      case 113:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1112: KW_FILEFORMAT
      {
        mKW_FILEFORMAT();
      }
        break;
      case 114:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1126: KW_INPUTFORMAT
      {
        mKW_INPUTFORMAT();
      }
        break;
      case 115:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1141: KW_OUTPUTFORMAT
      {
        mKW_OUTPUTFORMAT();
      }
        break;
      case 116:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1157: KW_INPUTDRIVER
      {
        mKW_INPUTDRIVER();
      }
        break;
      case 117:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1172: KW_OUTPUTDRIVER
      {
        mKW_OUTPUTDRIVER();
      }
        break;
      case 118:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1188: KW_OFFLINE
      {
        mKW_OFFLINE();
      }
        break;
      case 119:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1199: KW_ENABLE
      {
        mKW_ENABLE();
      }
        break;
      case 120:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1209: KW_DISABLE
      {
        mKW_DISABLE();
      }
        break;
      case 121:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1220: KW_READONLY
      {
        mKW_READONLY();
      }
        break;
      case 122:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1232: KW_NO_DROP
      {
        mKW_NO_DROP();
      }
        break;
      case 123:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1243: KW_LOCATION
      {
        mKW_LOCATION();
      }
        break;
      case 124:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1255: KW_TABLESAMPLE
      {
        mKW_TABLESAMPLE();
      }
        break;
      case 125:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1270: KW_BUCKET
      {
        mKW_BUCKET();
      }
        break;
      case 126:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1280: KW_OUT
      {
        mKW_OUT();
      }
        break;
      case 127:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1287: KW_OF
      {
        mKW_OF();
      }
        break;
      case 128:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1293: KW_PERCENT
      {
        mKW_PERCENT();
      }
        break;
      case 129:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1304: KW_CAST
      {
        mKW_CAST();
      }
        break;
      case 130:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1312: KW_ADD
      {
        mKW_ADD();
      }
        break;
      case 131:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1319: KW_REPLACE
      {
        mKW_REPLACE();
      }
        break;
      case 132:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1330: KW_RLIKE
      {
        mKW_RLIKE();
      }
        break;
      case 133:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1339: KW_REGEXP
      {
        mKW_REGEXP();
      }
        break;
      case 134:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1349: KW_TEMPORARY
      {
        mKW_TEMPORARY();
      }
        break;
      case 135:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1362: KW_FUNCTION
      {
        mKW_FUNCTION();
      }
        break;
      case 136:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1374: KW_MACRO
      {
        mKW_MACRO();
      }
        break;
      case 137:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1383: KW_FILE
      {
        mKW_FILE();
      }
        break;
      case 138:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1391: KW_JAR
      {
        mKW_JAR();
      }
        break;
      case 139:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1398: KW_EXPLAIN
      {
        mKW_EXPLAIN();
      }
        break;
      case 140:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1409: KW_EXTENDED
      {
        mKW_EXTENDED();
      }
        break;
      case 141:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1421: KW_FORMATTED
      {
        mKW_FORMATTED();
      }
        break;
      case 142:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1434: KW_PRETTY
      {
        mKW_PRETTY();
      }
        break;
      case 143:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1444: KW_DEPENDENCY
      {
        mKW_DEPENDENCY();
      }
        break;
      case 144:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1458: KW_LOGICAL
      {
        mKW_LOGICAL();
      }
        break;
      case 145:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1469: KW_SERDE
      {
        mKW_SERDE();
      }
        break;
      case 146:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1478: KW_WITH
      {
        mKW_WITH();
      }
        break;
      case 147:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1486: KW_DEFERRED
      {
        mKW_DEFERRED();
      }
        break;
      case 148:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1498: KW_SERDEPROPERTIES
      {
        mKW_SERDEPROPERTIES();
      }
        break;
      case 149:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1517: KW_DBPROPERTIES
      {
        mKW_DBPROPERTIES();
      }
        break;
      case 150:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1533: KW_LIMIT
      {
        mKW_LIMIT();
      }
        break;
      case 151:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1542: KW_SET
      {
        mKW_SET();
      }
        break;
      case 152:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1549: KW_UNSET
      {
        mKW_UNSET();
      }
        break;
      case 153:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1558: KW_TBLPROPERTIES
      {
        mKW_TBLPROPERTIES();
      }
        break;
      case 154:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1575: KW_IDXPROPERTIES
      {
        mKW_IDXPROPERTIES();
      }
        break;
      case 155:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1592: KW_VALUE_TYPE
      {
        mKW_VALUE_TYPE();
      }
        break;
      case 156:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1606: KW_ELEM_TYPE
      {
        mKW_ELEM_TYPE();
      }
        break;
      case 157:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1619: KW_DEFINED
      {
        mKW_DEFINED();
      }
        break;
      case 158:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1630: KW_CASE
      {
        mKW_CASE();
      }
        break;
      case 159:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1638: KW_WHEN
      {
        mKW_WHEN();
      }
        break;
      case 160:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1646: KW_THEN
      {
        mKW_THEN();
      }
        break;
      case 161:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1654: KW_ELSE
      {
        mKW_ELSE();
      }
        break;
      case 162:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1662: KW_END
      {
        mKW_END();
      }
        break;
      case 163:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1669: KW_MAPJOIN
      {
        mKW_MAPJOIN();
      }
        break;
      case 164:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1680: KW_STREAMTABLE
      {
        mKW_STREAMTABLE();
      }
        break;
      case 165:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1695: KW_HOLD_DDLTIME
      {
        mKW_HOLD_DDLTIME();
      }
        break;
      case 166:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1711: KW_CLUSTERSTATUS
      {
        mKW_CLUSTERSTATUS();
      }
        break;
      case 167:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1728: KW_UTC
      {
        mKW_UTC();
      }
        break;
      case 168:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1735: KW_UTCTIMESTAMP
      {
        mKW_UTCTIMESTAMP();
      }
        break;
      case 169:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1751: KW_LONG
      {
        mKW_LONG();
      }
        break;
      case 170:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1759: KW_DELETE
      {
        mKW_DELETE();
      }
        break;
      case 171:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1769: KW_PLUS
      {
        mKW_PLUS();
      }
        break;
      case 172:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1777: KW_MINUS
      {
        mKW_MINUS();
      }
        break;
      case 173:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1786: KW_FETCH
      {
        mKW_FETCH();
      }
        break;
      case 174:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1795: KW_INTERSECT
      {
        mKW_INTERSECT();
      }
        break;
      case 175:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1808: KW_VIEW
      {
        mKW_VIEW();
      }
        break;
      case 176:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1816: KW_IN
      {
        mKW_IN();
      }
        break;
      case 177:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1822: KW_DATABASE
      {
        mKW_DATABASE();
      }
        break;
      case 178:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1834: KW_DATABASES
      {
        mKW_DATABASES();
      }
        break;
      case 179:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1847: KW_MATERIALIZED
      {
        mKW_MATERIALIZED();
      }
        break;
      case 180:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1863: KW_SCHEMA
      {
        mKW_SCHEMA();
      }
        break;
      case 181:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1873: KW_SCHEMAS
      {
        mKW_SCHEMAS();
      }
        break;
      case 182:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1884: KW_GRANT
      {
        mKW_GRANT();
      }
        break;
      case 183:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1893: KW_REVOKE
      {
        mKW_REVOKE();
      }
        break;
      case 184:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1903: KW_SSL
      {
        mKW_SSL();
      }
        break;
      case 185:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1910: KW_UNDO
      {
        mKW_UNDO();
      }
        break;
      case 186:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1918: KW_LOCK
      {
        mKW_LOCK();
      }
        break;
      case 187:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1926: KW_LOCKS
      {
        mKW_LOCKS();
      }
        break;
      case 188:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1935: KW_UNLOCK
      {
        mKW_UNLOCK();
      }
        break;
      case 189:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1945: KW_SHARED
      {
        mKW_SHARED();
      }
        break;
      case 190:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1955: KW_EXCLUSIVE
      {
        mKW_EXCLUSIVE();
      }
        break;
      case 191:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1968: KW_PROCEDURE
      {
        mKW_PROCEDURE();
      }
        break;
      case 192:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1981: KW_UNSIGNED
      {
        mKW_UNSIGNED();
      }
        break;
      case 193:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:1993: KW_WHILE
      {
        mKW_WHILE();
      }
        break;
      case 194:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2002: KW_READ
      {
        mKW_READ();
      }
        break;
      case 195:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2010: KW_READS
      {
        mKW_READS();
      }
        break;
      case 196:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2019: KW_PURGE
      {
        mKW_PURGE();
      }
        break;
      case 197:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2028: KW_RANGE
      {
        mKW_RANGE();
      }
        break;
      case 198:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2037: KW_ANALYZE
      {
        mKW_ANALYZE();
      }
        break;
      case 199:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2048: KW_BEFORE
      {
        mKW_BEFORE();
      }
        break;
      case 200:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2058: KW_BETWEEN
      {
        mKW_BETWEEN();
      }
        break;
      case 201:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2069: KW_BOTH
      {
        mKW_BOTH();
      }
        break;
      case 202:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2077: KW_BINARY
      {
        mKW_BINARY();
      }
        break;
      case 203:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2087: KW_CROSS
      {
        mKW_CROSS();
      }
        break;
      case 204:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2096: KW_CONTINUE
      {
        mKW_CONTINUE();
      }
        break;
      case 205:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2108: KW_CURSOR
      {
        mKW_CURSOR();
      }
        break;
      case 206:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2118: KW_TRIGGER
      {
        mKW_TRIGGER();
      }
        break;
      case 207:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2129: KW_RECORDREADER
      {
        mKW_RECORDREADER();
      }
        break;
      case 208:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2145: KW_RECORDWRITER
      {
        mKW_RECORDWRITER();
      }
        break;
      case 209:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2161: KW_SEMI
      {
        mKW_SEMI();
      }
        break;
      case 210:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2169: KW_LATERAL
      {
        mKW_LATERAL();
      }
        break;
      case 211:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2180: KW_TOUCH
      {
        mKW_TOUCH();
      }
        break;
      case 212:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2189: KW_ARCHIVE
      {
        mKW_ARCHIVE();
      }
        break;
      case 213:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2200: KW_UNARCHIVE
      {
        mKW_UNARCHIVE();
      }
        break;
      case 214:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2213: KW_COMPUTE
      {
        mKW_COMPUTE();
      }
        break;
      case 215:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2224: KW_STATISTICS
      {
        mKW_STATISTICS();
      }
        break;
      case 216:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2238: KW_USE
      {
        mKW_USE();
      }
        break;
      case 217:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2245: KW_OPTION
      {
        mKW_OPTION();
      }
        break;
      case 218:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2255: KW_CONCATENATE
      {
        mKW_CONCATENATE();
      }
        break;
      case 219:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2270: KW_SHOW_DATABASE
      {
        mKW_SHOW_DATABASE();
      }
        break;
      case 220:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2287: KW_UPDATE
      {
        mKW_UPDATE();
      }
        break;
      case 221:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2297: KW_RESTRICT
      {
        mKW_RESTRICT();
      }
        break;
      case 222:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2309: KW_CASCADE
      {
        mKW_CASCADE();
      }
        break;
      case 223:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2320: KW_SKEWED
      {
        mKW_SKEWED();
      }
        break;
      case 224:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2330: KW_ROLLUP
      {
        mKW_ROLLUP();
      }
        break;
      case 225:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2340: KW_CUBE
      {
        mKW_CUBE();
      }
        break;
      case 226:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2348: KW_DIRECTORIES
      {
        mKW_DIRECTORIES();
      }
        break;
      case 227:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2363: KW_FOR
      {
        mKW_FOR();
      }
        break;
      case 228:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2370: KW_WINDOW
      {
        mKW_WINDOW();
      }
        break;
      case 229:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2380: KW_UNBOUNDED
      {
        mKW_UNBOUNDED();
      }
        break;
      case 230:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2393: KW_PRECEDING
      {
        mKW_PRECEDING();
      }
        break;
      case 231:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2406: KW_FOLLOWING
      {
        mKW_FOLLOWING();
      }
        break;
      case 232:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2419: KW_CURRENT
      {
        mKW_CURRENT();
      }
        break;
      case 233:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2430: KW_CURRENT_DATE
      {
        mKW_CURRENT_DATE();
      }
        break;
      case 234:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2446: KW_CURRENT_TIMESTAMP
      {
        mKW_CURRENT_TIMESTAMP();
      }
        break;
      case 235:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2467: KW_LESS
      {
        mKW_LESS();
      }
        break;
      case 236:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2475: KW_MORE
      {
        mKW_MORE();
      }
        break;
      case 237:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2483: KW_OVER
      {
        mKW_OVER();
      }
        break;
      case 238:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2491: KW_GROUPING
      {
        mKW_GROUPING();
      }
        break;
      case 239:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2503: KW_SETS
      {
        mKW_SETS();
      }
        break;
      case 240:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2511: KW_TRUNCATE
      {
        mKW_TRUNCATE();
      }
        break;
      case 241:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2523: KW_NOSCAN
      {
        mKW_NOSCAN();
      }
        break;
      case 242:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2533: KW_PARTIALSCAN
      {
        mKW_PARTIALSCAN();
      }
        break;
      case 243:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2548: KW_USER
      {
        mKW_USER();
      }
        break;
      case 244:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2556: KW_ROLE
      {
        mKW_ROLE();
      }
        break;
      case 245:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2564: KW_ROLES
      {
        mKW_ROLES();
      }
        break;
      case 246:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2573: KW_INNER
      {
        mKW_INNER();
      }
        break;
      case 247:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2582: KW_EXCHANGE
      {
        mKW_EXCHANGE();
      }
        break;
      case 248:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2594: KW_URI
      {
        mKW_URI();
      }
        break;
      case 249:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2601: KW_SERVER
      {
        mKW_SERVER();
      }
        break;
      case 250:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2611: KW_ADMIN
      {
        mKW_ADMIN();
      }
        break;
      case 251:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2620: KW_OWNER
      {
        mKW_OWNER();
      }
        break;
      case 252:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2629: KW_PRINCIPALS
      {
        mKW_PRINCIPALS();
      }
        break;
      case 253:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2643: KW_COMPACT
      {
        mKW_COMPACT();
      }
        break;
      case 254:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2654: KW_COMPACTIONS
      {
        mKW_COMPACTIONS();
      }
        break;
      case 255:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2669: KW_TRANSACTIONS
      {
        mKW_TRANSACTIONS();
      }
        break;
      case 256:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2685: KW_REWRITE
      {
        mKW_REWRITE();
      }
        break;
      case 257:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2696: KW_AUTHORIZATION
      {
        mKW_AUTHORIZATION();
      }
        break;
      case 258:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2713: KW_CONF
      {
        mKW_CONF();
      }
        break;
      case 259:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2721: KW_VALUES
      {
        mKW_VALUES();
      }
        break;
      case 260:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2731: KW_RELOAD
      {
        mKW_RELOAD();
      }
        break;
      case 261:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2741: DOT
      {
        mDOT();
      }
        break;
      case 262:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2745: COLON
      {
        mCOLON();
      }
        break;
      case 263:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2751: COMMA
      {
        mCOMMA();
      }
        break;
      case 264:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2757: SEMICOLON
      {
        mSEMICOLON();
      }
        break;
      case 265:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2767: LPAREN
      {
        mLPAREN();
      }
        break;
      case 266:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2774: RPAREN
      {
        mRPAREN();
      }
        break;
      case 267:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2781: LSQUARE
      {
        mLSQUARE();
      }
        break;
      case 268:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2789: RSQUARE
      {
        mRSQUARE();
      }
        break;
      case 269:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2797: LCURLY
      {
        mLCURLY();
      }
        break;
      case 270:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2804: RCURLY
      {
        mRCURLY();
      }
        break;
      case 271:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2811: EQUAL
      {
        mEQUAL();
      }
        break;
      case 272:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2817: EQUAL_NS
      {
        mEQUAL_NS();
      }
        break;
      case 273:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2826: NOTEQUAL
      {
        mNOTEQUAL();
      }
        break;
      case 274:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2835: LESSTHANOREQUALTO
      {
        mLESSTHANOREQUALTO();
      }
        break;
      case 275:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2853: LESSTHAN
      {
        mLESSTHAN();
      }
        break;
      case 276:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2862: GREATERTHANOREQUALTO
      {
        mGREATERTHANOREQUALTO();
      }
        break;
      case 277:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2883: GREATERTHAN
      {
        mGREATERTHAN();
      }
        break;
      case 278:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2895: DIVIDE
      {
        mDIVIDE();
      }
        break;
      case 279:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2902: PLUS
      {
        mPLUS();
      }
        break;
      case 280:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2907: MINUS
      {
        mMINUS();
      }
        break;
      case 281:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2913: STAR
      {
        mSTAR();
      }
        break;
      case 282:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2918: MOD
      {
        mMOD();
      }
        break;
      case 283:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2922: DIV
      {
        mDIV();
      }
        break;
      case 284:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2926: AMPERSAND
      {
        mAMPERSAND();
      }
        break;
      case 285:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2936: TILDE
      {
        mTILDE();
      }
        break;
      case 286:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2942: BITWISEOR
      {
        mBITWISEOR();
      }
        break;
      case 287:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2952: BITWISEXOR
      {
        mBITWISEXOR();
      }
        break;
      case 288:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2963: QUESTION
      {
        mQUESTION();
      }
        break;
      case 289:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2972: DOLLAR
      {
        mDOLLAR();
      }
        break;
      case 290:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2979: StringLiteral
      {
        mStringLiteral();
      }
        break;
      case 291:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:2993: CharSetLiteral
      {
        mCharSetLiteral();
      }
        break;
      case 292:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:3008: BigintLiteral
      {
        mBigintLiteral();
      }
        break;
      case 293:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:3022: SmallintLiteral
      {
        mSmallintLiteral();
      }
        break;
      case 294:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:3038: TinyintLiteral
      {
        mTinyintLiteral();
      }
        break;
      case 295:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:3053: DecimalLiteral
      {
        mDecimalLiteral();
      }
        break;
      case 296:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:3068: ByteLengthLiteral
      {
        mByteLengthLiteral();
      }
        break;
      case 297:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:3086: Number
      {
        mNumber();
      }
        break;
      case 298:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:3093: Identifier
      {
        mIdentifier();
      }
        break;
      case 299:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:3104: CharSetName
      {
        mCharSetName();
      }
        break;
      case 300:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:3116: WS
      {
        mWS();
      }
        break;
      case 301:
      // org/apache/hadoop/hive/ql/parse/HiveLexer.g:1:3119: COMMENT
      {
        mCOMMENT();
      }
        break;
    }
  }

  protected DFA21 dfa21 = new DFA21(this);
  protected DFA25 dfa25 = new DFA25(this);
  static final String DFA21_eotS = "\7\uffff";
  static final String DFA21_eofS = "\7\uffff";
  static final String DFA21_minS = "\1\60\1\uffff\1\0\1\uffff\2\0\1\uffff";
  static final String DFA21_maxS = "\1\172\1\uffff\1\uffff\1\uffff\1\uffff\1\0\1\uffff";
  static final String DFA21_acceptS = "\1\uffff\1\1\1\uffff\1\2\2\uffff\1\3";
  static final String DFA21_specialS = "\2\uffff\1\2\1\uffff\1\0\1\1\1\uffff}>";
  static final String[] DFA21_transitionS =
      { "\12\1\7\uffff\32\1\5\uffff\1\2\32\1", "", "\44\3\1\4\3\3\4\4\1\3\2\4\1\3\12\4\5\3\1\4\1\3\33\4\1\3\3\4"
          + "\1\3\35\4\uff82\3", "", "\44\3\1\4\3\3\4\4\1\3\2\4\1\3\12\4\5\3\1\4\1\3\33\4\1\3\3\4"
              + "\1\5\35\4\uff82\3", "\1\uffff", "" };

  static final short[] DFA21_eot = DFA.unpackEncodedString(DFA21_eotS);
  static final short[] DFA21_eof = DFA.unpackEncodedString(DFA21_eofS);
  static final char[] DFA21_min = DFA.unpackEncodedStringToUnsignedChars(DFA21_minS);
  static final char[] DFA21_max = DFA.unpackEncodedStringToUnsignedChars(DFA21_maxS);
  static final short[] DFA21_accept = DFA.unpackEncodedString(DFA21_acceptS);
  static final short[] DFA21_special = DFA.unpackEncodedString(DFA21_specialS);
  static final short[][] DFA21_transition;

  static {
    int numStates = DFA21_transitionS.length;
    DFA21_transition = new short[numStates][];
    for (int i = 0; i < numStates; i++) {
      DFA21_transition[i] = DFA.unpackEncodedString(DFA21_transitionS[i]);
    }
  }

  class DFA21 extends DFA {

    public DFA21(BaseRecognizer recognizer) {
      this.recognizer = recognizer;
      this.decisionNumber = 21;
      this.eot = DFA21_eot;
      this.eof = DFA21_eof;
      this.min = DFA21_min;
      this.max = DFA21_max;
      this.accept = DFA21_accept;
      this.special = DFA21_special;
      this.transition = DFA21_transition;
    }

    public String getDescription() {
      return "441:1: Identifier : ( ( Letter | Digit ) ( Letter | Digit | '_' )* |{...}? QuotedIdentifier | '`' ( RegexComponent )+ '`' );";
    }

    public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
      IntStream input = _input;
      int _s = s;
      switch (s) {
        case 0:
          int LA21_4 = input.LA(1);

          s = -1;
          if ((LA21_4 == '`')) {
            s = 5;
          } else if ((LA21_4 == '$' || (LA21_4 >= '(' && LA21_4 <= '+') || (LA21_4 >= '-' && LA21_4 <= '.')
              || (LA21_4 >= '0' && LA21_4 <= '9') || LA21_4 == '?' || (LA21_4 >= 'A' && LA21_4 <= '[')
              || (LA21_4 >= ']' && LA21_4 <= '_') || (LA21_4 >= 'a' && LA21_4 <= '}'))) {
            s = 4;
          } else if (((LA21_4 >= '\u0000' && LA21_4 <= '#') || (LA21_4 >= '%' && LA21_4 <= '\'') || LA21_4 == ','
              || LA21_4 == '/' || (LA21_4 >= ':' && LA21_4 <= '>') || LA21_4 == '@' || LA21_4 == '\\'
              || (LA21_4 >= '~' && LA21_4 <= '\uFFFF'))) {
            s = 3;
          }

          if (s >= 0) {
            return s;
          }
          break;
        case 1:
          int LA21_5 = input.LA(1);

          int index21_5 = input.index();
          input.rewind();

          s = -1;
          if (((allowQuotedId()))) {
            s = 3;
          } else if ((true)) {
            s = 6;
          }

          input.seek(index21_5);

          if (s >= 0) {
            return s;
          }
          break;
        case 2:
          int LA21_2 = input.LA(1);

          s = -1;
          if (((LA21_2 >= '\u0000' && LA21_2 <= '#') || (LA21_2 >= '%' && LA21_2 <= '\'') || LA21_2 == ','
              || LA21_2 == '/' || (LA21_2 >= ':' && LA21_2 <= '>') || LA21_2 == '@' || LA21_2 == '\\' || LA21_2 == '`'
              || (LA21_2 >= '~' && LA21_2 <= '\uFFFF'))) {
            s = 3;
          } else if ((LA21_2 == '$' || (LA21_2 >= '(' && LA21_2 <= '+') || (LA21_2 >= '-' && LA21_2 <= '.')
              || (LA21_2 >= '0' && LA21_2 <= '9') || LA21_2 == '?' || (LA21_2 >= 'A' && LA21_2 <= '[')
              || (LA21_2 >= ']' && LA21_2 <= '_') || (LA21_2 >= 'a' && LA21_2 <= '}'))) {
            s = 4;
          }

          if (s >= 0) {
            return s;
          }
          break;
      }
      NoViableAltException nvae = new NoViableAltException(getDescription(), 21, _s, input);
      error(nvae);
      throw nvae;
    }
  }

  static final String DFA25_eotS = "\1\uffff\6\64\1\134\20\64\1\u00a0\13\uffff\1\u00a2\1\u00a4\2\uffff"
      + "\1\u00a6\11\uffff\2\u00b4\3\uffff\2\64\1\u00bc\15\64\1\u00d3\14"
      + "\64\1\u00ec\2\64\1\u00ef\1\u00f1\2\64\2\uffff\4\64\1\u00fe\1\u0104"
      + "\1\64\1\u0106\10\64\1\u0114\57\64\4\uffff\1\u016a\7\uffff\1\u016c"
      + "\2\uffff\1\u016c\1\64\1\u016f\1\u00b4\1\u0170\1\u0171\1\u00b4\1"
      + "\64\1\uffff\2\u0178\5\64\1\uffff\16\64\1\u018e\2\64\1\u0191\1\64"
      + "\1\u0193\1\64\1\u0195\1\uffff\3\64\1\u0199\3\64\1\134\12\64\1\u01ab"
      + "\5\64\1\uffff\1\64\1\u01b5\1\uffff\1\64\1\uffff\14\64\1\uffff\3"
      + "\64\1\u01ca\1\64\1\uffff\1\64\1\uffff\11\64\1\u01d8\3\64\1\uffff"
      + "\17\64\1\u01ee\11\64\1\u01fa\10\64\1\u0206\1\u0208\1\64\1\u020a"
      + "\10\64\1\u0216\14\64\1\u0225\17\64\1\u023f\10\64\5\uffff\1\u0248"
      + "\3\uffff\1\u00b4\3\uffff\1\u00b4\1\u0174\1\uffff\1\u024d\12\64\1"
      + "\u0258\1\64\1\u025a\1\u025b\3\64\1\u0260\2\64\1\uffff\2\64\1\uffff"
      + "\1\64\1\uffff\1\64\1\uffff\3\64\1\uffff\2\64\1\u026c\2\64\1\u026f"
      + "\3\64\1\u0274\7\64\1\uffff\1\u027e\1\u0280\1\u0281\3\64\1\u0286"
      + "\2\64\1\uffff\3\64\1\u028c\2\64\1\u028f\1\u0290\1\64\1\u0294\1\u0295"
      + "\1\64\1\u0297\5\64\1\u029d\1\64\1\uffff\15\64\1\uffff\1\u02ad\3"
      + "\64\1\u02b1\10\64\1\u02ba\1\64\1\u02bc\4\64\1\u02c1\1\uffff\1\u02c2"
      + "\1\u02c4\1\64\1\u02c7\7\64\1\uffff\5\64\1\u02d4\4\64\1\u02d9\1\uffff"
      + "\1\64\1\uffff\1\64\1\uffff\10\64\1\u02e4\1\64\1\u02e6\1\uffff\6"
      + "\64\1\u02f0\6\64\1\u02f7\1\uffff\1\64\1\u02fa\10\64\1\u0304\4\64"
      + "\1\u0309\1\u030a\1\u030b\3\64\1\u030f\1\u0310\2\64\1\uffff\3\64"
      + "\1\u0316\2\64\1\u0319\1\u031a\2\uffff\2\u00b4\1\64\1\uffff\3\64"
      + "\1\u0320\1\u0321\5\64\1\uffff\1\u0327\2\uffff\1\64\1\u0329\2\64"
      + "\1\uffff\1\u032c\2\64\1\u032f\1\u0330\1\64\1\u0332\1\u0333\1\64"
      + "\1\u0335\1\64\1\uffff\2\64\1\uffff\4\64\1\uffff\11\64\1\uffff\1"
      + "\64\2\uffff\2\64\1\u0349\1\64\1\uffff\1\u034b\3\64\1\u034f\1\uffff"
      + "\1\u0350\1\u0351\2\uffff\1\u0352\1\64\1\u0354\2\uffff\1\64\1\uffff"
      + "\2\64\1\u0359\2\64\1\uffff\1\64\1\u035e\2\64\1\u0361\12\64\1\uffff"
      + "\1\u036d\1\u036e\1\64\1\uffff\7\64\1\u0377\1\uffff\1\u0378\1\uffff"
      + "\2\64\1\u037c\1\64\2\uffff\1\64\1\uffff\2\64\1\uffff\11\64\1\u038b"
      + "\1\u038c\1\64\1\uffff\3\64\1\u0391\1\uffff\12\64\1\uffff\1\u039d"
      + "\1\uffff\1\u039e\7\64\1\u03a6\1\uffff\6\64\1\uffff\1\64\1\u03ae"
      + "\1\uffff\1\u03af\1\u03b0\7\64\1\uffff\2\64\1\u03ba\1\64\3\uffff"
      + "\3\64\2\uffff\2\64\1\u03c1\1\64\1\u03c3\1\uffff\2\64\2\uffff\4\64"
      + "\1\u03cb\2\uffff\5\64\1\uffff\1\64\1\uffff\1\u03d2\1\64\1\uffff"
      + "\1\u03d5\1\64\2\uffff\1\64\2\uffff\1\64\1\uffff\2\64\1\u03db\6\64"
      + "\1\u03e2\7\64\1\u03ea\1\64\1\uffff\1\64\1\uffff\2\64\1\u03f0\4\uffff"
      + "\1\64\1\uffff\2\64\1\u03f4\1\64\1\uffff\1\u03f6\3\64\1\uffff\1\u03fa"
      + "\1\u03fb\1\uffff\1\64\1\u03fd\1\u03fe\6\64\1\u0405\1\64\2\uffff"
      + "\1\64\1\u0408\1\u0409\1\u040b\1\u040c\1\64\1\u040e\1\64\2\uffff"
      + "\1\u0410\1\u0411\1\64\1\uffff\1\u0413\1\64\1\u0415\1\u0416\1\64"
      + "\1\u0418\1\u0419\1\64\1\u041b\1\64\1\u041e\1\u041f\2\64\2\uffff"
      + "\1\64\1\u0423\2\64\1\uffff\1\64\1\u0427\1\64\1\u0429\7\64\2\uffff"
      + "\1\64\1\u0432\2\64\1\u0435\1\u0436\1\64\1\uffff\1\u0438\1\u0439"
      + "\3\64\1\u043e\1\u043f\3\uffff\1\u0441\7\64\1\u0449\1\uffff\1\u044a"
      + "\1\64\1\u044c\3\64\1\uffff\1\64\1\uffff\1\64\1\u0452\3\64\1\u0456"
      + "\1\64\1\uffff\1\u0458\5\64\1\uffff\2\64\1\uffff\1\64\1\u0461\1\u0462"
      + "\1\64\1\u0464\1\uffff\1\u0465\1\64\1\u0467\1\64\1\u0469\1\64\1\uffff"
      + "\3\64\1\u046e\3\64\1\uffff\4\64\1\u0476\1\uffff\1\64\1\u0478\1\u0479"
      + "\1\uffff\1\u047a\1\uffff\3\64\2\uffff\1\64\2\uffff\1\u047f\4\64"
      + "\1\u0484\1\uffff\1\64\1\u0486\2\uffff\1\u0487\2\uffff\1\u0488\1"
      + "\uffff\1\64\2\uffff\1\64\1\uffff\1\64\2\uffff\1\64\2\uffff\1\64"
      + "\1\uffff\1\64\1\u048f\2\uffff\3\64\1\uffff\3\64\1\uffff\1\64\1\uffff"
      + "\6\64\1\u049d\1\u049e\1\uffff\1\64\1\u04a0\2\uffff\1\64\2\uffff"
      + "\3\64\1\u04a5\2\uffff\1\u04a6\1\uffff\1\64\1\u04a8\1\u04a9\1\u04ab"
      + "\2\64\1\u04b0\2\uffff\1\u04b1\1\uffff\1\u04b3\1\64\1\u04b5\1\64"
      + "\1\u04b7\1\uffff\1\u04b8\2\64\1\uffff\1\64\1\uffff\4\64\1\u04c1"
      + "\3\64\2\uffff\1\64\2\uffff\1\u04c6\1\uffff\1\u04c7\1\uffff\2\64"
      + "\1\u04ca\1\64\1\uffff\1\64\1\u04cf\1\u04d0\4\64\1\uffff\1\u04d5"
      + "\3\uffff\4\64\1\uffff\1\u04da\1\u04db\1\64\1\u04dd\1\uffff\1\u04de"
      + "\3\uffff\3\64\1\u04e2\2\64\1\uffff\2\64\1\u04e7\3\64\1\u04eb\6\64"
      + "\2\uffff\1\64\1\uffff\1\u04f3\2\64\1\u04f6\2\uffff\1\64\2\uffff"
      + "\1\64\1\uffff\1\u04f9\3\64\2\uffff\1\64\1\uffff\1\u04ff\1\uffff"
      + "\1\64\2\uffff\1\u0501\2\64\1\u0504\1\64\1\u0506\1\64\1\u0508\1\uffff"
      + "\1\64\1\u050a\1\u050b\1\64\2\uffff\1\u050d\1\64\1\uffff\1\64\1\u0510"
      + "\1\64\1\u0512\2\uffff\1\64\1\u0514\2\64\1\uffff\2\64\1\u0519\1\64"
      + "\2\uffff\1\u051b\2\uffff\3\64\1\uffff\3\64\1\u0522\1\uffff\1\u0523"
      + "\1\u0524\1\64\1\uffff\1\u0526\1\64\1\u0528\1\64\1\u052c\2\64\1\uffff"
      + "\2\64\1\uffff\2\64\1\uffff\1\64\1\u0534\3\64\1\uffff\1\64\1\uffff"
      + "\2\64\1\uffff\1\u053b\1\uffff\1\64\1\uffff\1\u053d\2\uffff\1\64"
      + "\1\uffff\1\u053f\1\u0540\1\uffff\1\64\1\uffff\1\64\1\uffff\4\64"
      + "\1\uffff\1\64\1\uffff\4\64\1\u054c\1\u054d\3\uffff\1\64\1\uffff"
      + "\1\u054f\1\uffff\1\u0550\1\u0551\1\64\1\uffff\4\64\1\u0557\2\64"
      + "\1\uffff\5\64\1\u055f\1\uffff\1\64\1\uffff\1\64\2\uffff\1\u0562"
      + "\3\64\1\u0566\1\u0567\4\64\1\u056c\2\uffff\1\64\3\uffff\1\u056e"
      + "\1\u056f\1\u0570\2\64\1\uffff\1\u0573\1\u0574\4\64\1\u0579\1\uffff"
      + "\2\64\1\uffff\1\u057c\1\u057d\1\u057e\2\uffff\1\64\1\u0580\2\64"
      + "\1\uffff\1\u0583\3\uffff\1\u0584\1\u0585\2\uffff\1\64\1\u0587\1"
      + "\64\1\u0589\1\uffff\1\u058a\1\u058b\3\uffff\1\u058c\1\uffff\1\64"
      + "\1\u058e\3\uffff\1\u058f\1\uffff\1\64\4\uffff\1\64\2\uffff\1\64"
      + "\1\u0593\1\64\1\uffff\1\64\1\u0596\1\uffff";
  static final String DFA25_eofS = "\u0597\uffff";
  static final String DFA25_minS = "\1\11\2\101\1\104\1\117\1\101\1\106\1\75\1\101\1\104\1\114\1\122"
      + "\1\105\1\101\1\110\1\103\1\116\6\101\2\105\13\uffff\2\75\2\uffff"
      + "\1\55\7\uffff\2\0\2\56\3\uffff\1\101\1\102\1\60\2\115\1\114\1\105"
      + "\1\114\1\117\1\114\1\105\1\117\1\114\1\124\1\114\1\101\1\60\1\124"
      + "\1\103\1\104\1\124\1\116\1\114\1\103\1\122\1\124\1\117\1\125\1\120"
      + "\1\60\1\105\1\124\2\60\1\124\1\116\2\uffff\1\113\1\106\1\101\1\124"
      + "\2\60\1\120\1\60\1\116\1\105\1\130\2\103\1\101\1\123\1\101\1\60"
      + "\1\117\1\107\1\103\1\106\1\126\1\114\1\105\1\116\1\114\1\101\1\122"
      + "\2\101\1\110\1\114\1\105\1\101\1\105\1\103\1\104\1\111\1\105\2\122"
      + "\1\125\1\122\1\111\1\122\1\107\1\101\1\114\1\111\1\116\1\114\1\125"
      + "\1\105\1\101\1\123\1\102\1\103\1\124\1\103\1\116\1\122\1\114\1\105"
      + "\1\131\4\uffff\1\76\5\uffff\2\0\1\42\2\0\1\42\2\60\1\56\3\60\1\53"
      + "\1\uffff\2\60\1\105\1\116\1\107\1\114\1\103\1\uffff\1\131\1\105"
      + "\1\115\2\120\1\116\1\123\1\115\1\114\1\103\1\123\1\114\1\105\1\101"
      + "\1\60\1\114\1\103\1\60\1\105\1\60\1\114\1\60\1\uffff\1\105\1\101"
      + "\1\110\1\60\1\111\1\110\1\105\1\60\1\104\1\103\1\114\1\101\1\103"
      + "\1\111\2\105\1\101\1\105\1\60\1\101\1\120\1\102\1\122\1\105\1\uffff"
      + "\1\122\1\60\1\uffff\1\114\1\uffff\1\111\3\105\1\111\1\124\1\123"
      + "\1\101\1\104\1\111\1\107\1\105\1\uffff\2\105\1\101\1\60\1\105\1"
      + "\uffff\1\117\1\uffff\1\117\1\115\1\120\1\123\1\114\1\105\1\110\1"
      + "\101\1\102\1\60\1\105\1\125\1\116\1\uffff\1\114\1\110\1\111\1\101"
      + "\1\113\1\117\1\127\1\111\1\104\1\116\1\114\1\110\1\104\1\105\1\104"
      + "\1\60\1\111\1\127\1\122\1\124\1\114\1\105\1\122\1\124\1\105\1\60"
      + "\1\127\1\117\1\105\2\117\1\122\1\117\1\116\2\60\1\101\1\60\2\103"
      + "\1\116\1\124\1\103\1\123\1\107\1\116\1\60\1\110\1\125\2\101\1\125"
      + "\1\104\1\105\2\117\1\124\1\122\1\117\1\60\1\105\1\113\1\107\1\114"
      + "\1\115\1\103\1\123\1\101\1\123\1\116\1\103\1\122\1\105\1\113\1\101"
      + "\1\60\1\122\1\105\1\125\1\105\1\103\1\125\1\127\1\123\2\uffff\1"
      + "\0\1\uffff\1\0\1\60\3\uffff\1\60\1\53\1\uffff\3\60\1\uffff\1\60"
      + "\1\103\1\123\1\107\1\105\1\110\1\111\1\123\1\111\1\117\1\122\1\60"
      + "\1\105\2\60\2\124\1\104\1\60\1\124\1\101\1\uffff\1\117\1\110\1\uffff"
      + "\1\122\1\uffff\1\131\1\uffff\1\122\1\131\1\111\1\uffff\1\116\1\117"
      + "\1\60\1\122\1\101\1\60\1\125\1\122\1\116\1\60\2\115\1\124\1\116"
      + "\1\111\1\102\1\103\1\uffff\3\60\1\114\1\117\1\122\1\60\1\122\1\125"
      + "\1\uffff\1\111\1\117\1\122\1\60\1\123\1\124\2\60\1\114\2\60\1\103"
      + "\1\60\2\122\1\130\2\124\1\60\1\122\1\uffff\3\122\1\123\1\122\1\124"
      + "\1\122\1\101\1\116\1\125\1\101\1\120\1\114\1\uffff\1\60\1\120\1"
      + "\124\1\105\1\60\1\116\1\122\1\105\1\122\1\105\1\116\1\137\1\105"
      + "\1\60\1\105\1\60\1\117\1\103\2\105\1\60\1\uffff\2\60\1\105\1\60"
      + "\1\114\1\116\1\103\1\101\1\105\1\111\1\115\1\uffff\1\105\1\125\1"
      + "\116\1\124\1\107\1\60\2\103\1\125\1\107\1\60\1\uffff\1\124\1\uffff"
      + "\1\124\1\uffff\1\105\1\124\3\105\1\103\1\111\1\105\1\60\1\105\1"
      + "\60\1\uffff\1\124\2\111\1\101\1\115\1\103\1\60\1\130\1\113\2\122"
      + "\1\111\1\101\1\60\1\uffff\1\125\1\60\2\105\1\115\2\105\1\101\1\111"
      + "\1\101\1\60\2\124\1\123\1\107\3\60\1\101\1\117\1\105\2\60\1\104"
      + "\1\117\1\uffff\1\117\1\122\1\123\1\60\1\110\1\105\2\60\1\uffff\3"
      + "\60\1\104\1\uffff\2\101\1\105\2\60\1\116\1\124\1\116\1\122\1\117"
      + "\1\uffff\1\60\2\uffff\1\111\1\60\1\123\1\117\1\uffff\1\60\1\124"
      + "\1\127\2\60\1\132\2\60\1\126\1\60\1\122\1\uffff\1\117\1\116\1\uffff"
      + "\1\114\1\122\1\105\1\111\1\uffff\1\101\1\111\1\105\1\104\1\116\1"
      + "\111\1\114\1\124\1\101\1\uffff\1\111\2\uffff\1\105\1\120\1\60\1"
      + "\122\1\uffff\1\60\1\124\2\116\1\60\1\uffff\2\60\2\uffff\1\60\1\111"
      + "\1\60\2\uffff\1\101\1\uffff\1\101\1\124\1\60\1\110\1\104\1\uffff"
      + "\1\123\1\60\1\124\1\105\1\60\1\117\1\123\1\124\1\111\1\116\1\104"
      + "\1\123\1\116\2\105\1\uffff\2\60\1\101\1\uffff\1\124\1\131\1\124"
      + "\2\105\1\107\1\104\1\60\1\uffff\1\60\1\uffff\1\127\1\124\1\60\1"
      + "\122\2\uffff\1\104\1\uffff\2\104\1\uffff\1\111\1\107\1\124\1\115"
      + "\1\104\1\123\1\101\1\104\1\105\2\60\1\116\1\uffff\1\113\1\110\1"
      + "\116\1\60\1\uffff\1\115\1\105\1\122\1\131\1\104\1\103\1\104\1\111"
      + "\1\101\1\116\1\uffff\1\60\1\uffff\1\60\1\114\1\122\2\103\2\105\1"
      + "\116\1\60\1\uffff\1\120\1\105\1\104\1\111\1\124\1\104\1\uffff\1"
      + "\120\1\60\1\uffff\2\60\1\116\1\103\1\116\1\124\1\103\1\116\1\124"
      + "\1\uffff\2\105\1\60\1\105\3\uffff\1\104\1\122\1\116\2\uffff\1\101"
      + "\1\111\1\60\1\111\1\60\1\uffff\1\101\1\123\2\uffff\1\124\1\117\1"
      + "\103\1\122\1\60\2\uffff\1\124\3\101\1\120\1\uffff\1\117\1\uffff"
      + "\1\60\1\122\1\uffff\1\60\1\111\2\uffff\1\105\2\uffff\1\105\1\uffff"
      + "\1\111\1\120\1\60\1\124\1\105\1\104\1\102\1\114\1\124\1\60\1\105"
      + "\1\103\1\102\1\105\1\117\1\123\1\115\1\60\1\105\1\uffff\1\111\1"
      + "\uffff\1\104\1\105\1\60\4\uffff\1\117\1\uffff\2\114\1\60\1\123\1"
      + "\uffff\1\60\1\117\1\122\1\105\1\uffff\2\60\1\uffff\1\120\2\60\1"
      + "\116\1\101\1\105\1\111\1\107\1\104\1\60\1\116\2\uffff\1\116\4\60"
      + "\1\116\1\60\1\104\2\uffff\2\60\1\122\1\uffff\1\60\1\101\2\60\1\116"
      + "\2\60\1\124\1\60\1\124\2\60\1\112\1\131\2\uffff\1\105\1\60\1\111"
      + "\1\104\1\uffff\1\105\1\60\1\126\1\60\1\111\1\124\1\125\1\120\1\111"
      + "\1\114\1\124\2\uffff\1\104\1\60\1\101\1\105\2\60\1\114\1\uffff\2"
      + "\60\1\122\1\103\1\105\2\60\3\uffff\1\60\2\124\1\105\1\124\1\125"
      + "\1\105\1\122\1\60\1\uffff\1\60\1\105\1\60\2\124\1\116\1\uffff\1"
      + "\101\1\uffff\1\122\1\60\1\105\1\122\1\124\1\60\1\115\1\uffff\1\60"
      + "\1\115\1\124\1\122\1\105\1\116\1\uffff\1\115\1\105\1\uffff\1\116"
      + "\2\60\1\132\1\60\1\uffff\1\60\1\104\1\60\1\105\1\60\1\105\1\uffff"
      + "\1\116\1\124\1\125\1\60\1\122\2\105\1\uffff\1\122\1\124\1\117\1"
      + "\122\1\60\1\uffff\1\116\2\60\1\uffff\1\60\1\uffff\1\122\1\111\1"
      + "\103\2\uffff\1\105\2\uffff\1\60\1\114\1\104\1\126\1\105\1\60\1\uffff"
      + "\1\107\1\60\2\uffff\1\60\2\uffff\1\60\1\uffff\1\114\2\uffff\1\117"
      + "\1\uffff\1\124\2\uffff\1\124\2\uffff\1\101\1\uffff\1\111\1\60\2"
      + "\uffff\1\117\1\120\1\104\1\uffff\1\126\1\105\1\123\1\uffff\1\105"
      + "\1\uffff\1\116\1\111\1\122\1\101\1\117\1\123\2\60\1\uffff\1\124"
      + "\1\60\2\uffff\1\131\2\uffff\1\105\1\122\1\124\1\60\2\uffff\1\60"
      + "\1\uffff\1\111\3\60\1\105\1\116\1\60\2\uffff\1\60\1\uffff\1\60\1"
      + "\101\1\60\1\114\1\60\1\uffff\1\60\1\115\1\111\1\uffff\1\120\1\uffff"
      + "\1\120\1\105\1\131\1\122\1\60\1\101\1\104\1\107\2\uffff\1\101\2"
      + "\uffff\1\60\1\uffff\1\60\1\uffff\1\104\1\103\1\60\1\124\1\uffff"
      + "\1\111\2\60\1\124\1\105\1\122\1\111\1\uffff\1\60\3\uffff\1\115\1"
      + "\126\1\124\1\122\1\uffff\2\60\1\105\1\60\1\uffff\1\60\3\uffff\1"
      + "\124\1\120\1\101\1\60\1\102\1\103\1\uffff\1\111\1\105\1\60\1\105"
      + "\1\104\1\124\1\60\1\107\1\117\1\105\1\114\1\116\1\103\2\uffff\1"
      + "\111\1\uffff\1\60\1\101\1\111\1\60\2\uffff\1\117\2\uffff\1\117\1"
      + "\uffff\1\60\1\101\1\104\1\124\2\uffff\1\104\1\uffff\1\60\1\uffff"
      + "\1\111\2\uffff\1\60\1\117\1\114\1\60\1\104\1\60\1\124\1\60\1\uffff"
      + "\1\124\2\60\1\124\2\uffff\1\60\1\131\1\uffff\1\105\1\60\1\105\1"
      + "\60\2\uffff\1\111\1\60\1\115\1\126\1\uffff\1\101\1\105\1\60\1\124"
      + "\2\uffff\1\60\2\uffff\1\111\1\105\1\102\1\uffff\1\114\1\123\1\116"
      + "\1\60\1\uffff\2\60\1\101\1\uffff\1\60\1\116\1\60\1\123\1\60\1\101"
      + "\1\117\1\uffff\1\104\1\124\1\uffff\2\116\1\uffff\1\124\1\60\2\101"
      + "\1\111\1\uffff\1\132\1\uffff\1\116\1\105\1\uffff\1\60\1\uffff\1"
      + "\111\1\uffff\1\60\2\uffff\1\111\1\uffff\2\60\1\uffff\1\123\1\uffff"
      + "\1\105\1\uffff\1\101\1\105\1\124\1\122\1\uffff\1\111\1\uffff\1\115"
      + "\1\122\1\101\1\105\2\60\3\uffff\1\115\1\uffff\1\60\1\uffff\2\60"
      + "\1\104\1\uffff\2\116\2\105\1\60\1\123\1\105\1\uffff\2\124\1\115"
      + "\1\105\1\123\1\60\1\uffff\1\105\1\uffff\1\117\2\uffff\1\60\1\123"
      + "\1\124\1\122\2\60\2\105\1\124\1\123\1\60\2\uffff\1\120\3\uffff\3"
      + "\60\2\122\1\uffff\2\60\1\125\2\105\1\104\1\60\1\uffff\1\123\1\116"
      + "\1\uffff\3\60\2\uffff\1\123\1\60\1\111\1\105\1\uffff\1\60\3\uffff"
      + "\2\60\2\uffff\1\123\1\60\1\123\1\60\1\uffff\2\60\3\uffff\1\60\1"
      + "\uffff\1\105\1\60\3\uffff\1\60\1\uffff\1\124\4\uffff\1\123\2\uffff"
      + "\1\101\1\60\1\115\1\uffff\1\120\1\60\1\uffff";
  static final String DFA25_maxS = "\1\176\1\122\3\125\1\122\1\127\1\75\1\117\1\124\1\130\1\122\1\131"
      + "\1\117\1\111\2\124\1\125\2\117\1\125\1\123\1\111\1\105\1\126\13"
      + "\uffff\1\76\1\75\2\uffff\1\55\7\uffff\2\uffff\2\172\3\uffff\1\125"
      + "\1\102\1\172\1\116\1\122\1\114\1\105\1\114\1\117\1\116\1\122\1\117"
      + "\1\122\2\124\1\104\1\172\1\124\1\122\1\115\1\124\1\137\1\114\1\123"
      + "\1\126\1\124\1\117\1\125\1\120\1\172\1\105\1\124\2\172\1\124\1\116"
      + "\2\uffff\1\116\1\123\1\116\1\124\2\172\1\120\1\172\1\116\1\105\1"
      + "\130\1\124\1\103\1\104\1\123\1\117\1\172\1\124\1\116\1\103\1\124"
      + "\1\126\1\114\1\111\2\124\1\117\1\122\1\101\1\122\1\110\1\114\1\105"
      + "\1\123\1\111\1\103\1\104\1\111\1\117\2\122\1\125\1\122\1\111\1\122"
      + "\1\107\2\127\1\111\2\116\1\125\1\117\1\101\1\123\1\122\1\103\2\124"
      + "\1\116\2\122\1\105\1\131\4\uffff\1\76\5\uffff\2\uffff\1\47\2\uffff"
      + "\1\47\1\146\4\172\1\145\1\71\1\uffff\2\172\2\116\1\107\1\114\1\103"
      + "\1\uffff\1\131\1\105\1\115\2\120\1\116\1\123\1\115\1\114\1\103\1"
      + "\123\1\114\1\105\1\101\1\172\1\114\1\103\1\172\1\105\1\172\1\114"
      + "\1\172\1\uffff\1\105\1\101\1\110\1\172\1\111\1\110\1\105\1\172\1"
      + "\104\1\103\1\114\1\111\1\103\2\111\1\105\1\124\1\105\1\172\1\105"
      + "\1\120\1\102\1\122\1\105\1\uffff\1\122\1\172\1\uffff\1\114\1\uffff"
      + "\1\111\3\105\1\111\1\124\1\123\1\113\1\104\1\111\1\107\1\105\1\uffff"
      + "\2\105\1\125\1\172\1\105\1\uffff\1\117\1\uffff\1\117\1\115\1\120"
      + "\1\123\1\117\1\105\1\114\1\101\1\102\1\172\1\105\1\125\1\116\1\uffff"
      + "\1\114\1\110\1\111\1\101\1\113\1\117\1\127\1\111\1\104\1\122\1\114"
      + "\1\110\1\104\1\105\1\126\1\172\1\111\1\127\1\122\1\124\1\114\1\125"
      + "\1\122\1\124\1\105\1\172\1\127\1\121\1\111\2\117\1\122\1\117\1\116"
      + "\2\172\1\101\1\172\2\124\1\116\1\124\1\103\1\123\1\107\1\116\1\172"
      + "\1\110\1\125\1\114\1\101\1\125\1\104\1\105\2\117\1\124\1\122\1\117"
      + "\1\172\1\114\1\113\1\107\1\125\1\120\1\124\1\123\1\101\1\123\1\122"
      + "\1\124\1\123\1\105\1\113\1\101\1\172\1\122\1\105\1\125\1\105\1\103"
      + "\1\125\1\127\1\123\2\uffff\1\uffff\1\uffff\1\uffff\1\172\3\uffff"
      + "\1\145\1\71\1\uffff\1\71\2\172\1\uffff\1\172\1\103\1\123\1\107\1"
      + "\105\1\110\1\111\1\123\1\111\1\117\1\122\1\172\1\105\2\172\2\124"
      + "\1\104\1\172\1\124\1\101\1\uffff\1\117\1\110\1\uffff\1\122\1\uffff"
      + "\1\131\1\uffff\1\122\1\131\1\111\1\uffff\1\116\1\117\1\172\1\122"
      + "\1\101\1\172\1\125\1\122\1\116\1\172\2\115\1\124\1\116\1\122\1\102"
      + "\1\103\1\uffff\3\172\1\114\1\117\1\122\1\172\1\122\1\125\1\uffff"
      + "\1\111\1\117\1\122\1\172\1\123\1\124\2\172\1\124\2\172\1\103\1\172"
      + "\2\122\1\130\2\124\1\172\1\122\1\uffff\3\122\1\123\1\122\1\124\1"
      + "\122\1\101\1\122\1\125\1\101\1\120\1\114\1\uffff\1\172\1\120\1\124"
      + "\1\105\1\172\1\116\1\122\1\105\1\122\1\105\1\116\1\137\1\105\1\172"
      + "\1\105\1\172\1\117\1\103\2\105\1\172\1\uffff\2\172\1\105\1\172\1"
      + "\114\1\116\1\103\1\101\1\105\1\111\1\115\1\uffff\1\105\1\125\1\116"
      + "\1\124\1\107\1\172\2\103\1\125\1\107\1\172\1\uffff\1\124\1\uffff"
      + "\1\124\1\uffff\1\105\1\124\3\105\1\103\1\111\1\105\1\172\1\105\1"
      + "\172\1\uffff\1\124\3\111\1\115\1\103\1\172\1\130\1\113\2\122\1\111"
      + "\1\101\1\172\1\uffff\1\125\1\172\2\105\1\115\2\105\1\125\1\111\1"
      + "\101\1\172\2\124\1\123\1\107\3\172\1\101\1\117\1\105\2\172\1\104"
      + "\1\117\1\uffff\1\117\1\122\1\123\1\172\1\110\1\105\2\172\1\uffff"
      + "\1\71\2\102\1\104\1\uffff\1\101\1\106\1\105\2\172\1\116\1\124\1"
      + "\116\1\122\1\117\1\uffff\1\172\2\uffff\1\111\1\172\1\123\1\117\1"
      + "\uffff\1\172\1\124\1\127\2\172\1\132\2\172\1\126\1\172\1\122\1\uffff"
      + "\1\117\1\116\1\uffff\1\114\1\122\1\105\1\111\1\uffff\1\101\1\111"
      + "\1\105\1\104\1\116\1\111\1\114\1\124\1\101\1\uffff\1\111\2\uffff"
      + "\1\105\1\120\1\172\1\122\1\uffff\1\172\1\124\2\116\1\172\1\uffff"
      + "\2\172\2\uffff\1\172\1\111\1\172\2\uffff\1\101\1\uffff\1\101\1\124"
      + "\1\172\1\110\1\106\1\uffff\1\123\1\172\1\124\1\105\1\172\1\117\1"
      + "\123\1\124\1\111\1\116\1\104\1\123\1\116\2\105\1\uffff\2\172\1\101"
      + "\1\uffff\1\124\1\131\1\124\2\105\1\107\1\104\1\172\1\uffff\1\172"
      + "\1\uffff\1\127\1\124\1\172\1\122\2\uffff\1\104\1\uffff\2\104\1\uffff"
      + "\1\111\1\107\1\124\1\115\1\104\1\123\1\101\1\104\1\105\2\172\1\116"
      + "\1\uffff\1\113\1\110\1\116\1\172\1\uffff\1\115\1\105\1\122\1\131"
      + "\1\104\1\103\1\104\1\111\1\124\1\116\1\uffff\1\172\1\uffff\1\172"
      + "\1\114\1\122\2\103\2\105\1\116\1\172\1\uffff\1\120\1\105\1\104\1"
      + "\111\1\124\1\104\1\uffff\1\120\1\172\1\uffff\2\172\1\116\1\103\1"
      + "\116\1\124\1\103\1\116\1\124\1\uffff\2\105\1\172\1\105\3\uffff\1"
      + "\104\1\122\1\116\2\uffff\1\101\1\111\1\172\1\111\1\172\1\uffff\1"
      + "\101\1\123\2\uffff\1\124\1\117\1\103\1\122\1\172\2\uffff\1\124\3"
      + "\101\1\120\1\uffff\1\117\1\uffff\1\172\1\122\1\uffff\1\172\1\111"
      + "\2\uffff\1\105\2\uffff\1\105\1\uffff\1\111\1\120\1\172\1\124\1\105"
      + "\1\104\1\102\1\114\1\124\1\172\1\105\1\103\1\102\1\105\1\117\1\123"
      + "\1\115\1\172\1\105\1\uffff\1\111\1\uffff\1\106\1\105\1\172\4\uffff"
      + "\1\117\1\uffff\2\114\1\172\1\123\1\uffff\1\172\1\117\1\122\1\105"
      + "\1\uffff\2\172\1\uffff\1\120\2\172\1\116\1\101\1\105\1\111\1\107"
      + "\1\104\1\172\1\116\2\uffff\1\116\4\172\1\116\1\172\1\104\2\uffff"
      + "\2\172\1\122\1\uffff\1\172\1\101\2\172\1\116\2\172\1\124\1\172\1"
      + "\124\2\172\1\112\1\131\2\uffff\1\105\1\172\1\111\1\104\1\uffff\1"
      + "\105\1\172\1\126\1\172\1\111\1\124\1\125\1\120\1\111\1\114\1\124"
      + "\2\uffff\1\104\1\172\1\101\1\105\2\172\1\114\1\uffff\2\172\1\127"
      + "\1\103\1\105\2\172\3\uffff\1\172\2\124\1\105\1\124\1\125\1\105\1"
      + "\122\1\172\1\uffff\1\172\1\105\1\172\2\124\1\116\1\uffff\1\101\1"
      + "\uffff\1\122\1\172\1\105\1\122\1\124\1\172\1\115\1\uffff\1\172\1"
      + "\115\1\124\1\122\1\105\1\116\1\uffff\1\115\1\105\1\uffff\1\116\2"
      + "\172\1\132\1\172\1\uffff\1\172\1\104\1\172\1\105\1\172\1\105\1\uffff"
      + "\1\116\1\124\1\125\1\172\1\122\2\105\1\uffff\1\122\1\124\1\117\1"
      + "\122\1\172\1\uffff\1\116\2\172\1\uffff\1\172\1\uffff\1\122\1\111"
      + "\1\103\2\uffff\1\105\2\uffff\1\172\1\114\1\104\1\126\1\105\1\172"
      + "\1\uffff\1\107\1\172\2\uffff\1\172\2\uffff\1\172\1\uffff\1\114\2"
      + "\uffff\1\117\1\uffff\1\124\2\uffff\1\124\2\uffff\1\101\1\uffff\1"
      + "\111\1\172\2\uffff\1\117\1\120\1\104\1\uffff\1\126\1\105\1\123\1"
      + "\uffff\1\105\1\uffff\1\116\1\111\1\122\1\101\1\117\1\123\2\172\1"
      + "\uffff\1\124\1\172\2\uffff\1\131\2\uffff\1\105\1\122\1\124\1\172"
      + "\2\uffff\1\172\1\uffff\1\111\3\172\1\105\1\116\1\172\2\uffff\1\172"
      + "\1\uffff\1\172\1\101\1\172\1\114\1\172\1\uffff\1\172\1\115\1\111"
      + "\1\uffff\1\120\1\uffff\1\120\1\105\1\131\1\122\1\172\1\101\1\104"
      + "\1\107\2\uffff\1\101\2\uffff\1\172\1\uffff\1\172\1\uffff\1\104\1"
      + "\103\1\172\1\124\1\uffff\1\131\2\172\1\124\1\105\1\122\1\111\1\uffff"
      + "\1\172\3\uffff\1\115\1\126\1\124\1\122\1\uffff\2\172\1\105\1\172"
      + "\1\uffff\1\172\3\uffff\1\124\1\120\1\101\1\172\1\102\1\103\1\uffff"
      + "\1\111\1\105\1\172\1\105\1\104\1\124\1\172\1\107\1\117\1\105\1\114"
      + "\1\116\1\103\2\uffff\1\111\1\uffff\1\172\1\101\1\111\1\172\2\uffff"
      + "\1\117\2\uffff\1\117\1\uffff\1\172\1\101\1\104\1\124\2\uffff\1\124"
      + "\1\uffff\1\172\1\uffff\1\111\2\uffff\1\172\1\117\1\114\1\172\1\104"
      + "\1\172\1\124\1\172\1\uffff\1\124\2\172\1\124\2\uffff\1\172\1\131"
      + "\1\uffff\1\105\1\172\1\105\1\172\2\uffff\1\111\1\172\1\115\1\126"
      + "\1\uffff\1\101\1\105\1\172\1\124\2\uffff\1\172\2\uffff\1\111\1\105"
      + "\1\102\1\uffff\1\114\1\123\1\116\1\172\1\uffff\2\172\1\101\1\uffff"
      + "\1\172\1\116\1\172\1\123\1\172\1\101\1\117\1\uffff\1\104\1\124\1"
      + "\uffff\2\116\1\uffff\1\124\1\172\2\101\1\111\1\uffff\1\132\1\uffff"
      + "\1\116\1\105\1\uffff\1\172\1\uffff\1\111\1\uffff\1\172\2\uffff\1"
      + "\111\1\uffff\2\172\1\uffff\1\123\1\uffff\1\105\1\uffff\1\101\1\105"
      + "\1\124\1\122\1\uffff\1\111\1\uffff\1\115\1\122\1\101\1\105\2\172"
      + "\3\uffff\1\115\1\uffff\1\172\1\uffff\2\172\1\104\1\uffff\2\116\2"
      + "\105\1\172\1\123\1\105\1\uffff\2\124\1\115\1\105\1\123\1\172\1\uffff"
      + "\1\105\1\uffff\1\117\2\uffff\1\172\1\123\1\124\1\122\2\172\2\105"
      + "\1\124\1\123\1\172\2\uffff\1\120\3\uffff\3\172\2\122\1\uffff\2\172"
      + "\1\125\2\105\1\104\1\172\1\uffff\1\123\1\116\1\uffff\3\172\2\uffff"
      + "\1\123\1\172\1\111\1\105\1\uffff\1\172\3\uffff\2\172\2\uffff\1\123"
      + "\1\172\1\123\1\172\1\uffff\2\172\3\uffff\1\172\1\uffff\1\105\1\172"
      + "\3\uffff\1\172\1\uffff\1\124\4\uffff\1\123\2\uffff\1\101\1\172\1" + "\115\1\uffff\1\120\1\172\1\uffff";
  static final String DFA25_acceptS = "\31\uffff\1\u0105\1\u0106\1\u0107\1\u0108\1\u0109\1\u010a\1\u010b"
      + "\1\u010c\1\u010d\1\u010e\1\u010f\2\uffff\1\u0116\1\u0117\1\uffff"
      + "\1\u0119\1\u011a\1\u011c\1\u011d\1\u011e\1\u011f\1\u0120\4\uffff"
      + "\1\u012a\1\u012b\1\u012c\44\uffff\1\u0111\1\10\100\uffff\1\156\1"
      + "\u009b\1\u009c\1\u0121\1\uffff\1\u0113\1\u0114\1\u0115\1\u012d\1"
      + "\u0118\15\uffff\1\u0129\7\uffff\1\112\26\uffff\1\24\30\uffff\1\7"
      + "\2\uffff\1\40\1\uffff\1\177\14\uffff\1\12\5\uffff\1\u00b0\1\uffff"
      + "\1\74\15\uffff\1\20\124\uffff\1\u0110\1\u0112\1\uffff\1\u0122\2"
      + "\uffff\1\u0124\1\u0125\1\u0126\2\uffff\1\u0127\3\uffff\1\u0128\25"
      + "\uffff\1\u00e3\2\uffff\1\3\1\uffff\1\6\1\uffff\1\14\3\uffff\1\u0082"
      + "\21\uffff\1\u011b\11\uffff\1\176\24\uffff\1\117\15\uffff\1\u00a2"
      + "\25\uffff\1\u0097\13\uffff\1\u00b8\13\uffff\1\u00d8\1\uffff\1\u00a7"
      + "\1\uffff\1\u00f8\13\uffff\1\u008a\16\uffff\1\144\31\uffff\1\134"
      + "\10\uffff\1\u0123\4\uffff\1\1\12\uffff\1\u00a0\1\uffff\1\23\1\37"
      + "\4\uffff\1\u0089\13\uffff\1\4\2\uffff\1\75\4\uffff\1\15\11\uffff"
      + "\1\72\1\uffff\1\123\1\106\4\uffff\1\u00ed\5\uffff\1\11\2\uffff\1"
      + "\35\1\u00eb\3\uffff\1\u00ba\1\65\1\uffff\1\u00a9\5\uffff\1\142\17"
      + "\uffff\1\u00a1\3\uffff\1\u00c9\10\uffff\1\u009f\1\uffff\1\u0092"
      + "\4\uffff\1\u00ef\1\u00d1\1\uffff\1\52\2\uffff\1\63\14\uffff\1\u00b9"
      + "\4\uffff\1\u00f3\12\uffff\1\u00ab\1\uffff\1\34\11\uffff\1\u00c2"
      + "\6\uffff\1\145\2\uffff\1\u00f4\11\uffff\1\u0102\4\uffff\1\130\1"
      + "\u0081\1\u009e\3\uffff\1\u00e1\1\53\5\uffff\1\u00ec\2\uffff\1\u00af"
      + "\1\155\5\uffff\1\43\1\u00d3\5\uffff\1\2\1\uffff\1\103\2\uffff\1"
      + "\121\2\uffff\1\u00ad\1\100\1\uffff\1\104\1\132\1\uffff\1\u00fa\23"
      + "\uffff\1\16\1\uffff\1\31\3\uffff\1\u00fb\1\157\1\u0096\1\56\1\uffff"
      + "\1\u00bb\4\uffff\1\46\4\uffff\1\u00f6\2\uffff\1\154\13\uffff\1\17"
      + "\1\u00b6\10\uffff\1\22\1\u00c1\3\uffff\1\u0091\16\uffff\1\64\1\u0098"
      + "\4\uffff\1\60\13\uffff\1\u00c4\1\36\7\uffff\1\u00c3\7\uffff\1\u00f5"
      + "\1\u0084\1\u00c5\11\uffff\1\u00cb\6\uffff\1\u0088\1\uffff\1\u00ac"
      + "\7\uffff\1\44\6\uffff\1\150\2\uffff\1\146\5\uffff\1\u00f1\6\uffff"
      + "\1\u00aa\7\uffff\1\122\5\uffff\1\u00d9\3\uffff\1\27\1\uffff\1\73"
      + "\3\uffff\1\67\1\110\1\uffff\1\13\1\66\6\uffff\1\167\2\uffff\1\120"
      + "\1\u00ca\1\uffff\1\175\1\u00c7\1\uffff\1\21\1\uffff\1\u00e4\1\25"
      + "\1\uffff\1\u00f9\1\uffff\1\u00bd\1\141\1\uffff\1\127\1\133\1\uffff"
      + "\1\160\2\uffff\1\u00b4\1\u00df\3\uffff\1\u00bc\3\uffff\1\u00dc\1"
      + "\uffff\1\u008e\10\uffff\1\54\2\uffff\1\107\1\136\1\uffff\1\u0085"
      + "\1\u00b7\4\uffff\1\u0104\1\u00e0\1\uffff\1\102\7\uffff\1\76\1\101"
      + "\1\uffff\1\u00cd\5\uffff\1\u0103\3\uffff\1\u00ce\1\uffff\1\115\10"
      + "\uffff\1\u00c6\1\u00d4\1\uffff\1\172\1\5\1\uffff\1\u009d\1\uffff"
      + "\1\126\4\uffff\1\170\7\uffff\1\166\1\uffff\1\u0090\1\u00d2\1\47"
      + "\4\uffff\1\u008b\4\uffff\1\152\1\uffff\1\114\1\143\1\u00c8\6\uffff"
      + "\1\u00b5\15\uffff\1\u0080\1\50\1\uffff\1\u0083\4\uffff\1\u0100\1"
      + "\45\1\uffff\1\113\1\u00d6\1\uffff\1\u00fd\4\uffff\1\61\1\u00de\1"
      + "\uffff\1\u00e8\1\uffff\1\u00a3\1\uffff\1\131\1\u00f0\10\uffff\1"
      + "\u0087\4\uffff\1\u0093\1\105\2\uffff\1\26\4\uffff\1\u00b1\1\124"
      + "\4\uffff\1\173\4\uffff\1\77\1\u008c\1\uffff\1\u00f7\1\u00ee\3\uffff"
      + "\1\116\4\uffff\1\u00c0\3\uffff\1\33\7\uffff\1\171\2\uffff\1\u00dd"
      + "\2\uffff\1\u00cc\5\uffff\1\71\1\uffff\1\57\2\uffff\1\125\1\uffff"
      + "\1\u0086\1\uffff\1\51\1\uffff\1\u008d\1\u00e7\1\uffff\1\147\2\uffff"
      + "\1\55\1\uffff\1\u00b2\1\uffff\1\30\4\uffff\1\u00ae\1\uffff\1\u00be"
      + "\6\uffff\1\135\1\u00d5\1\u00e5\1\uffff\1\u00e6\1\uffff\1\u00bf\3"
      + "\uffff\1\41\7\uffff\1\140\6\uffff\1\151\1\uffff\1\161\1\uffff\1"
      + "\u008f\1\62\13\uffff\1\u00d7\1\32\1\uffff\1\111\1\u00fc\1\42\5\uffff"
      + "\1\153\7\uffff\1\174\2\uffff\1\u00e2\3\uffff\1\162\1\164\4\uffff"
      + "\1\u00a4\1\uffff\1\137\1\u00f2\1\70\2\uffff\1\u00fe\1\u00da\4\uffff"
      + "\1\u00ff\2\uffff\1\u0095\1\163\1\165\1\uffff\1\u00a5\2\uffff\1\u00a8"
      + "\1\u00cf\1\u00d0\1\uffff\1\u00e9\1\uffff\1\u00b3\1\u0099\1\u0101"
      + "\1\u009a\1\uffff\1\u00db\1\u00a6\3\uffff\1\u0094\2\uffff\1\u00ea";
  static final String DFA25_specialS =
      "\60\uffff\1\6\1\7\165\uffff\1\2\1\0\1\uffff\1\4\1\1\u00bf\uffff" + "\1\3\1\uffff\1\5\u0429\uffff}>";
  static final String[] DFA25_transitionS = { "\2\66\2\uffff\1\66\22\uffff\1\66\1\7\1\61\1\uffff\1\30\1\52"
      + "\1\53\1\60\1\35\1\36\1\51\1\47\1\33\1\50\1\31\1\46\1\62\11\63"
      + "\1\32\1\34\1\44\1\43\1\45\1\57\1\uffff\1\3\1\14\1\24\1\5\1\12"
      + "\1\2\1\13\1\15\1\11\1\22\1\27\1\10\1\25\1\4\1\6\1\21\1\64\1"
      + "\23\1\17\1\1\1\20\1\26\1\16\3\64\1\37\1\uffff\1\40\1\56\1\65"
      + "\33\64\1\41\1\55\1\42\1\54", "\1\70\1\74\2\uffff\1\73\2\uffff\1\75\1\72\5\uffff\1\71\2\uffff"
          + "\1\67", "\1\76\3\uffff\1\104\3\uffff\1\101\2\uffff\1\102\2\uffff\1\103"
              + "\2\uffff\1\77\2\uffff\1\100", "\1\112\1\uffff\1\110\5\uffff\1\105\1\uffff\1\106\3\uffff\1"
                  + "\111\1\107\1\uffff\1\113", "\1\114\5\uffff\1\115", "\1\120\1\123\2\uffff\1\116\3\uffff\1\117\5\uffff\1\122\2\uffff"
                      + "\1\121", "\1\130\7\uffff\1\127\1\uffff\1\131\1\uffff\1\124\2\uffff\1"
                          + "\126\1\125\1\132", "\1\133", "\1\140\3\uffff\1\136\3\uffff\1\135\5\uffff\1\137", "\1\147\1\uffff\1\141\1\145\5\uffff\1\143\1\142\4\uffff\1\144"
                              + "\1\146", "\1\153\1\uffff\1\152\4\uffff\1\151\4\uffff\1\150", "\1\154", "\1\161\3\uffff\1\157\5\uffff\1\156\5\uffff\1\160\3\uffff\1"
                                  + "\155", "\1\162\15\uffff\1\163", "\1\164\1\165", "\1\173\1\uffff\1\166\2\uffff\1\167\2\uffff\1\175\1\uffff\1"
                                      + "\171\1\uffff\1\170\3\uffff\1\174\1\172", "\1\176\1\uffff\1\u0081\1\uffff\1\u0082\1\177\1\u0080", "\1\u0084\3\uffff\1\u0085\6\uffff\1\u0086\5\uffff\1\u0083\2"
                                          + "\uffff\1\u0087", "\1\u0089\15\uffff\1\u0088", "\1\u008e\3\uffff\1\u008b\3\uffff\1\u008a\2\uffff\1\u008d\2"
                                              + "\uffff\1\u008c", "\1\u0093\6\uffff\1\u0092\3\uffff\1\u0090\2\uffff\1\u008f\2"
                                                  + "\uffff\1\u0091\2\uffff\1\u0094", "\1\u0097\3\uffff\1\u0096\3\uffff\1\u0098\5\uffff\1\u0099\3"
                                                      + "\uffff\1\u0095", "\1\u009a\7\uffff\1\u009b", "\1\u009c", "\1\u009f\5\uffff\1\u009d\12\uffff\1\u009e", "", "", "", "", "", "", "", "", "", "", "", "\1\u00a1\1\133", "\1\u00a3", "", "", "\1\u00a5", "", "", "", "", "", "", "", "\47\u00a7\1\u00a9\64\u00a7\1\u00a8\uffa3\u00a7", "\42\u00aa\1\u00ac\71\u00aa\1\u00ab\uffa3\u00aa", "\1\u00b2\1\uffff\12\u00af\7\uffff\1\64\1\u00b5\2\64\1\u00b3"
                                                          + "\1\64\1\u00b6\3\64\1\u00b6\1\u00ae\1\u00b6\5\64\1\u00b0\4\64"
                                                          + "\1\u00ad\1\u00b1\1\64\4\uffff\1\64\1\uffff\1\64\1\u00b6\2\64"
                                                          + "\1\u00b3\1\64\1\u00b6\3\64\1\u00b6\1\64\1\u00b6\15\64", "\1\u00b2\1\uffff\12\u00af\7\uffff\1\64\1\u00b5\2\64\1\u00b3"
                                                              + "\1\64\1\u00b6\3\64\1\u00b6\1\u00ae\1\u00b6\5\64\1\u00b0\5\64"
                                                              + "\1\u00b1\1\64\4\uffff\1\64\1\uffff\1\64\1\u00b6\2\64\1\u00b3"
                                                              + "\1\64\1\u00b6\3\64\1\u00b6\1\64\1\u00b6\15\64", "", "", "", "\1\u00b8\7\uffff\1\u00b9\13\uffff\1\u00b7", "\1\u00ba", "\12\64\7\uffff\24\64\1\u00bb\5\64\4\uffff\1\64\1\uffff\32\64", "\1\u00be\1\u00bd", "\1\u00c0\4\uffff\1\u00bf", "\1\u00c1", "\1\u00c2", "\1\u00c3", "\1\u00c4", "\1\u00c5\1\uffff\1\u00c6", "\1\u00c8\6\uffff\1\u00c9\5\uffff\1\u00c7", "\1\u00ca", "\1\u00cc\5\uffff\1\u00cb", "\1\u00cd", "\1\u00ce\7\uffff\1\u00cf", "\1\u00d1\2\uffff\1\u00d0", "\12\64\7\uffff\2\64\1\u00d2\27\64\4\uffff\1\64\1\uffff\32\64", "\1\u00d4", "\1\u00d6\16\uffff\1\u00d5", "\1\u00d7\10\uffff\1\u00d8", "\1\u00d9", "\1\u00da\4\uffff\1\u00dd\1\u00db\12\uffff\1\u00dc", "\1\u00de", "\1\u00e1\2\uffff\1\u00df\5\uffff\1\u00e2\3\uffff\1\u00e3\2"
                                                                  + "\uffff\1\u00e0", "\1\u00e5\1\u00e4\2\uffff\1\u00e6", "\1\u00e7", "\1\u00e8", "\1\u00e9", "\1\u00ea", "\12\64\7\uffff\3\64\1\u00eb\26\64\4\uffff\1\64\1\uffff\32\64", "\1\u00ed", "\1\u00ee", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\5\64\1\u00f0\24\64\4\uffff\1\64\1\uffff\32\64", "\1\u00f2", "\1\u00f3", "", "", "\1\u00f4\1\uffff\1\u00f6\1\u00f5", "\1\u00f7\14\uffff\1\u00f8", "\1\u00fa\1\uffff\1\u00f9\3\uffff\1\u00fb\6\uffff\1\u00fc", "\1\u00fd", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\3\64\1\u0100\11\64\1\u0103\1\64\1\u0101\2\64"
                                                                      + "\1\u00ff\1\u0102\6\64\4\uffff\1\64\1\uffff\32\64", "\1\u0105", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0107", "\1\u0108", "\1\u0109", "\1\u010d\5\uffff\1\u010a\6\uffff\1\u010b\3\uffff\1\u010c", "\1\u010e", "\1\u010f\2\uffff\1\u0110", "\1\u0111", "\1\u0113\15\uffff\1\u0112", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0115\4\uffff\1\u0116", "\1\u0117\6\uffff\1\u0118", "\1\u0119", "\1\u011a\15\uffff\1\u011b", "\1\u011c", "\1\u011d", "\1\u011e\3\uffff\1\u011f", "\1\u0121\5\uffff\1\u0120", "\1\u0122\1\u0125\4\uffff\1\u0123\1\uffff\1\u0124", "\1\u0127\15\uffff\1\u0126", "\1\u0128", "\1\u0129", "\1\u012c\15\uffff\1\u012b\2\uffff\1\u012a", "\1\u012d", "\1\u012e", "\1\u012f", "\1\u0134\1\u0135\1\uffff\1\u0132\4\uffff\1\u0130\2\uffff\1"
                                                                          + "\u0133\6\uffff\1\u0131", "\1\u0137\3\uffff\1\u0136", "\1\u0138", "\1\u0139", "\1\u013a", "\1\u013b\3\uffff\1\u013d\5\uffff\1\u013c", "\1\u013e", "\1\u013f", "\1\u0140", "\1\u0141", "\1\u0142", "\1\u0143", "\1\u0144", "\1\u0149\1\u0145\1\u014c\1\u0148\2\uffff\1\u014a\4\uffff\1"
                                                                              + "\u014f\1\uffff\1\u0147\1\uffff\1\u0146\2\uffff\1\u014d\2\uffff"
                                                                              + "\1\u014b\1\u014e", "\1\u0151\12\uffff\1\u0150", "\1\u0152", "\1\u0153", "\1\u0154\1\u0155\1\u0156", "\1\u0157", "\1\u0158\11\uffff\1\u0159", "\1\u015a", "\1\u015b", "\1\u015d\17\uffff\1\u015c", "\1\u015e", "\1\u015f", "\1\u0161\14\uffff\1\u0160\3\uffff\1\u0162", "\1\u0163", "\1\u0164", "\1\u0166\5\uffff\1\u0165", "\1\u0167", "\1\u0168", "", "", "", "", "\1\u0169", "", "", "", "", "", "\47\u00a7\1\u00a9\64\u00a7\1\u00a8\uffa3\u00a7", "\0\u016b", "\1\61\4\uffff\1\60", "\42\u00aa\1\u00ac\71\u00aa\1\u00ab\uffa3\u00aa", "\0\u016d", "\1\61\4\uffff\1\60", "\12\u016e\7\uffff\6\u016e\32\uffff\6\u016e", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u00b2\1\uffff\12\u00af\7\uffff\1\64\1\u00b5\2\64\1\u00b3"
                                                                                  + "\1\64\1\u00b6\3\64\1\u00b6\1\u00ae\1\u00b6\5\64\1\u00b0\5\64"
                                                                                  + "\1\u00b1\1\64\4\uffff\1\64\1\uffff\1\64\1\u00b6\2\64\1\u00b3"
                                                                                  + "\1\64\1\u00b6\3\64\1\u00b6\1\64\1\u00b6\15\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\u0172\10\uffff\1\u0174\2\uffff\1\u0173\37\uffff\1\u0173", "\1\u0175\1\uffff\1\u0175\2\uffff\12\u0176", "", "\12\64\7\uffff\3\64\1\u0177\26\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0179\10\uffff\1\u017a", "\1\u017b", "\1\u017c", "\1\u017d", "\1\u017e", "", "\1\u017f", "\1\u0180", "\1\u0181", "\1\u0182", "\1\u0183", "\1\u0184", "\1\u0185", "\1\u0186", "\1\u0187", "\1\u0188", "\1\u0189", "\1\u018a", "\1\u018b", "\1\u018c", "\12\64\7\uffff\14\64\1\u018d\15\64\4\uffff\1\64\1\uffff\32"
                                                                                      + "\64", "\1\u018f", "\1\u0190", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0192", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0194", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\1\u0196", "\1\u0197", "\1\u0198", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u019a", "\1\u019b", "\1\u019c", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u019d", "\1\u019e", "\1\u019f", "\1\u01a0\3\uffff\1\u01a1\3\uffff\1\u01a2", "\1\u01a3", "\1\u01a4", "\1\u01a6\3\uffff\1\u01a5", "\1\u01a7", "\1\u01a9\22\uffff\1\u01a8", "\1\u01aa", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u01ac\3\uffff\1\u01ad", "\1\u01ae", "\1\u01af", "\1\u01b0", "\1\u01b1", "", "\1\u01b2", "\12\64\7\uffff\4\64\1\u01b3\12\64\1\u01b4\12\64\4\uffff\1\64"
                                                                                          + "\1\uffff\32\64", "", "\1\u01b6", "", "\1\u01b7", "\1\u01b8", "\1\u01b9", "\1\u01ba", "\1\u01bb", "\1\u01bc", "\1\u01bd", "\1\u01be\11\uffff\1\u01bf", "\1\u01c0", "\1\u01c1", "\1\u01c2", "\1\u01c3", "", "\1\u01c4", "\1\u01c5", "\1\u01c6\23\uffff\1\u01c7", "\12\64\7\uffff\4\64\1\u01c9\11\64\1\u01c8\13\64\4\uffff\1\64"
                                                                                              + "\1\uffff\32\64", "\1\u01cb", "", "\1\u01cc", "", "\1\u01cd", "\1\u01ce", "\1\u01cf", "\1\u01d0", "\1\u01d2\2\uffff\1\u01d1", "\1\u01d3", "\1\u01d5\3\uffff\1\u01d4", "\1\u01d6", "\1\u01d7", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u01d9", "\1\u01da", "\1\u01db", "", "\1\u01dc", "\1\u01dd", "\1\u01de", "\1\u01df", "\1\u01e0", "\1\u01e1", "\1\u01e2", "\1\u01e3", "\1\u01e4", "\1\u01e6\3\uffff\1\u01e5", "\1\u01e7", "\1\u01e8", "\1\u01e9", "\1\u01ea", "\1\u01eb\21\uffff\1\u01ec", "\12\64\7\uffff\22\64\1\u01ed\7\64\4\uffff\1\64\1\uffff\32\64", "\1\u01ef", "\1\u01f0", "\1\u01f1", "\1\u01f2", "\1\u01f3", "\1\u01f6\3\uffff\1\u01f4\13\uffff\1\u01f5", "\1\u01f7", "\1\u01f8", "\1\u01f9", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u01fb", "\1\u01fd\1\uffff\1\u01fc", "\1\u01fe\3\uffff\1\u01ff", "\1\u0200", "\1\u0201", "\1\u0202", "\1\u0203", "\1\u0204", "\12\64\7\uffff\21\64\1\u0205\10\64\4\uffff\1\64\1\uffff\32"
                                                                                                  + "\64", "\12\64\7\uffff\32\64\4\uffff\1\u0207\1\uffff\32\64", "\1\u0209", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u020d\17\uffff\1\u020b\1\u020c", "\1\u020f\20\uffff\1\u020e", "\1\u0210", "\1\u0211", "\1\u0212", "\1\u0213", "\1\u0214", "\1\u0215", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0217", "\1\u0218", "\1\u0219\12\uffff\1\u021a", "\1\u021b", "\1\u021c", "\1\u021d", "\1\u021e", "\1\u021f", "\1\u0220", "\1\u0221", "\1\u0222", "\1\u0223", "\12\64\7\uffff\22\64\1\u0224\7\64\4\uffff\1\64\1\uffff\32\64", "\1\u0227\6\uffff\1\u0226", "\1\u0228", "\1\u0229", "\1\u022b\10\uffff\1\u022a", "\1\u022c\2\uffff\1\u022d", "\1\u022f\2\uffff\1\u0230\15\uffff\1\u022e", "\1\u0231", "\1\u0232", "\1\u0233", "\1\u0234\3\uffff\1\u0235", "\1\u0238\1\uffff\1\u0237\16\uffff\1\u0236", "\1\u023a\1\u0239", "\1\u023b", "\1\u023c", "\1\u023d", "\12\64\7\uffff\11\64\1\u023e\20\64\4\uffff\1\64\1\uffff\32"
                                                                                                      + "\64", "\1\u0240", "\1\u0241", "\1\u0242", "\1\u0243", "\1\u0244", "\1\u0245", "\1\u0246", "\1\u0247", "", "", "\47\u00a7\1\u00a9\64\u00a7\1\u00a8\uffa3\u00a7", "", "\42\u00aa\1\u00ac\71\u00aa\1\u00ab\uffa3\u00aa", "\12\u016e\7\uffff\6\u016e\24\64\4\uffff\1\64\1\uffff\6\u016e"
                                                                                                          + "\24\64", "", "", "", "\12\u0172\10\uffff\1\u0174\2\uffff\1\u0173\37\uffff\1\u0173", "\1\u0249\1\uffff\1\u0249\2\uffff\12\u024a", "", "\12\u024b", "\12\u0176\7\uffff\1\64\1\u024c\30\64\4\uffff\1\64\1\uffff\32"
                                                                                                              + "\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u024e", "\1\u024f", "\1\u0250", "\1\u0251", "\1\u0252", "\1\u0253", "\1\u0254", "\1\u0255", "\1\u0256", "\1\u0257", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0259", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u025c", "\1\u025d", "\1\u025e", "\12\64\7\uffff\5\64\1\u025f\24\64\4\uffff\1\64\1\uffff\32\64", "\1\u0261", "\1\u0262", "", "\1\u0263", "\1\u0264", "", "\1\u0265", "", "\1\u0266", "", "\1\u0267", "\1\u0268", "\1\u0269", "", "\1\u026a", "\1\u026b", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u026d", "\1\u026e", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0270", "\1\u0271", "\1\u0272", "\12\64\7\uffff\21\64\1\u0273\10\64\4\uffff\1\64\1\uffff\32"
                                                                                                                  + "\64", "\1\u0275", "\1\u0276", "\1\u0277", "\1\u0278", "\1\u0279\10\uffff\1\u027a", "\1\u027b", "\1\u027c", "", "\12\64\7\uffff\1\64\1\u027d\30\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\23\64\1\u027f\6\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0282", "\1\u0283", "\1\u0284", "\12\64\7\uffff\26\64\1\u0285\3\64\4\uffff\1\64\1\uffff\32\64", "\1\u0287", "\1\u0288", "", "\1\u0289", "\1\u028a", "\1\u028b", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u028d", "\1\u028e", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0291\7\uffff\1\u0292", "\12\64\7\uffff\22\64\1\u0293\7\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0296", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0298", "\1\u0299", "\1\u029a", "\1\u029b", "\1\u029c", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u029e", "", "\1\u029f", "\1\u02a0", "\1\u02a1", "\1\u02a2", "\1\u02a3", "\1\u02a4", "\1\u02a5", "\1\u02a6", "\1\u02a8\3\uffff\1\u02a7", "\1\u02a9", "\1\u02aa", "\1\u02ab", "\1\u02ac", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u02ae", "\1\u02af", "\1\u02b0", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u02b2", "\1\u02b3", "\1\u02b4", "\1\u02b5", "\1\u02b6", "\1\u02b7", "\1\u02b8", "\1\u02b9", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u02bb", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u02bd", "\1\u02be", "\1\u02bf", "\1\u02c0", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\u02c3\1\uffff\32\64", "\1\u02c5", "\12\64\7\uffff\4\64\1\u02c6\25\64\4\uffff\1\64\1\uffff\32\64", "\1\u02c8", "\1\u02c9", "\1\u02ca", "\1\u02cb", "\1\u02cc", "\1\u02cd", "\1\u02ce", "", "\1\u02cf", "\1\u02d0", "\1\u02d1", "\1\u02d2", "\1\u02d3", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u02d5", "\1\u02d6", "\1\u02d7", "\1\u02d8", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\1\u02da", "", "\1\u02db", "", "\1\u02dc", "\1\u02dd", "\1\u02de", "\1\u02df", "\1\u02e0", "\1\u02e1", "\1\u02e2", "\1\u02e3", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u02e5", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\1\u02e7", "\1\u02e8", "\1\u02e9", "\1\u02eb\7\uffff\1\u02ea", "\1\u02ec", "\1\u02ed", "\12\64\7\uffff\16\64\1\u02ee\3\64\1\u02ef\7\64\4\uffff\1\64"
                                                                                                                      + "\1\uffff\32\64", "\1\u02f1", "\1\u02f2", "\1\u02f3", "\1\u02f4", "\1\u02f5", "\1\u02f6", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\1\u02f8", "\12\64\7\uffff\22\64\1\u02f9\7\64\4\uffff\1\64\1\uffff\32\64", "\1\u02fb", "\1\u02fc", "\1\u02fd", "\1\u02fe", "\1\u02ff", "\1\u0301\23\uffff\1\u0300", "\1\u0302", "\1\u0303", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0305", "\1\u0306", "\1\u0307", "\1\u0308", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u030c", "\1\u030d", "\1\u030e", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0311", "\1\u0312", "", "\1\u0313", "\1\u0314", "\1\u0315", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0317", "\1\u0318", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\12\u024a", "\12\u024a\10\uffff\1\u0174", "\12\u024b\10\uffff\1\u0174", "\1\u0177", "", "\1\u031b", "\1\u031d\4\uffff\1\u031c", "\1\u031e", "\12\64\7\uffff\22\64\1\u031f\7\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0322", "\1\u0323", "\1\u0324", "\1\u0325", "\1\u0326", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "", "\1\u0328", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u032a", "\1\u032b", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u032d", "\1\u032e", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0331", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0334", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0336", "", "\1\u0337", "\1\u0338", "", "\1\u0339", "\1\u033a", "\1\u033b", "\1\u033c", "", "\1\u033d", "\1\u033e", "\1\u033f", "\1\u0340", "\1\u0341", "\1\u0342", "\1\u0343", "\1\u0344", "\1\u0345", "", "\1\u0346", "", "", "\1\u0347", "\1\u0348", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u034a", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u034c", "\1\u034d", "\1\u034e", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0353", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "", "\1\u0355", "", "\1\u0356", "\1\u0357", "\12\64\7\uffff\4\64\1\u0358\25\64\4\uffff\1\64\1\uffff\32\64", "\1\u035a", "\1\u035c\1\uffff\1\u035b", "", "\1\u035d", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u035f", "\1\u0360", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0362", "\1\u0363", "\1\u0364", "\1\u0365", "\1\u0366", "\1\u0367", "\1\u0368", "\1\u0369", "\1\u036a", "\1\u036b", "", "\12\64\7\uffff\10\64\1\u036c\21\64\4\uffff\1\64\1\uffff\32"
                                                                                                                          + "\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u036f", "", "\1\u0370", "\1\u0371", "\1\u0372", "\1\u0373", "\1\u0374", "\1\u0375", "\1\u0376", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\1\u0379", "\1\u037a", "\12\64\7\uffff\17\64\1\u037b\12\64\4\uffff\1\64\1\uffff\32"
                                                                                                                              + "\64", "\1\u037d", "", "", "\1\u037e", "", "\1\u037f", "\1\u0380", "", "\1\u0381", "\1\u0382", "\1\u0383", "\1\u0384", "\1\u0385", "\1\u0386", "\1\u0387", "\1\u0388", "\1\u0389", "\12\64\7\uffff\23\64\1\u038a\6\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u038d", "", "\1\u038e", "\1\u038f", "\1\u0390", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\1\u0392", "\1\u0393", "\1\u0394", "\1\u0395", "\1\u0396", "\1\u0397", "\1\u0398", "\1\u0399", "\1\u039b\22\uffff\1\u039a", "\1\u039c", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u039f", "\1\u03a0", "\1\u03a1", "\1\u03a2", "\1\u03a3", "\1\u03a4", "\1\u03a5", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\1\u03a7", "\1\u03a8", "\1\u03a9", "\1\u03aa", "\1\u03ab", "\1\u03ac", "", "\1\u03ad", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u03b1", "\1\u03b2", "\1\u03b3", "\1\u03b4", "\1\u03b5", "\1\u03b6", "\1\u03b7", "", "\1\u03b8", "\1\u03b9", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u03bb", "", "", "", "\1\u03bc", "\1\u03bd", "\1\u03be", "", "", "\1\u03bf", "\1\u03c0", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u03c2", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\1\u03c4", "\1\u03c5", "", "", "\1\u03c6", "\1\u03c7", "\1\u03c8", "\1\u03c9", "\12\64\7\uffff\1\u03ca\31\64\4\uffff\1\64\1\uffff\32\64", "", "", "\1\u03cc", "\1\u03cd", "\1\u03ce", "\1\u03cf", "\1\u03d0", "", "\1\u03d1", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u03d3", "", "\12\64\7\uffff\23\64\1\u03d4\6\64\4\uffff\1\64\1\uffff\32\64", "\1\u03d6", "", "", "\1\u03d7", "", "", "\1\u03d8", "", "\1\u03d9", "\1\u03da", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u03dc", "\1\u03dd", "\1\u03de", "\1\u03df", "\1\u03e0", "\1\u03e1", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u03e3", "\1\u03e4", "\1\u03e5", "\1\u03e6", "\1\u03e7", "\1\u03e8", "\1\u03e9", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u03eb", "", "\1\u03ec", "", "\1\u03ee\1\uffff\1\u03ed", "\1\u03ef", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "", "", "", "\1\u03f1", "", "\1\u03f2", "\1\u03f3", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u03f5", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u03f7", "\1\u03f8", "\1\u03f9", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\1\u03fc", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u03ff", "\1\u0400", "\1\u0401", "\1\u0402", "\1\u0403", "\1\u0404", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0406", "", "", "\1\u0407", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\22\64\1\u040a\7\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u040d", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u040f", "", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0412", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0414", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0417", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u041a", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u041c", "\12\64\7\uffff\22\64\1\u041d\7\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0420", "\1\u0421", "", "", "\1\u0422", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0424", "\1\u0425", "", "\1\u0426", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0428", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u042a", "\1\u042b", "\1\u042c", "\1\u042d", "\1\u042e", "\1\u042f", "\1\u0430", "", "", "\1\u0431", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0433", "\1\u0434", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0437", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u043a\4\uffff\1\u043b", "\1\u043c", "\1\u043d", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "", "", "\12\64\7\uffff\22\64\1\u0440\7\64\4\uffff\1\64\1\uffff\32\64", "\1\u0442", "\1\u0443", "\1\u0444", "\1\u0445", "\1\u0446", "\1\u0447", "\1\u0448", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u044b", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u044d", "\1\u044e", "\1\u044f", "", "\1\u0450", "", "\1\u0451", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0453", "\1\u0454", "\1\u0455", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0457", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0459", "\1\u045a", "\1\u045b", "\1\u045c", "\1\u045d", "", "\1\u045e", "\1\u045f", "", "\1\u0460", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0463", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0466", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0468", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u046a", "", "\1\u046b", "\1\u046c", "\1\u046d", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u046f", "\1\u0470", "\1\u0471", "", "\1\u0472", "\1\u0473", "\1\u0474", "\1\u0475", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\1\u0477", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\1\u047b", "\1\u047c", "\1\u047d", "", "", "\1\u047e", "", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0480", "\1\u0481", "\1\u0482", "\1\u0483", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\1\u0485", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\1\u0489", "", "", "\1\u048a", "", "\1\u048b", "", "", "\1\u048c", "", "", "\1\u048d", "", "\1\u048e", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "", "\1\u0490", "\1\u0491", "\1\u0492", "", "\1\u0493", "\1\u0494", "\1\u0495", "", "\1\u0496", "", "\1\u0497", "\1\u0498", "\1\u0499", "\1\u049a", "\1\u049b", "\1\u049c", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\1\u049f", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "", "\1\u04a1", "", "", "\1\u04a2", "\1\u04a3", "\1\u04a4", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\1\u04a7", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\10\64\1\u04aa\21\64\4\uffff\1\64\1\uffff\32"
                                                                                                                                  + "\64", "\1\u04ac", "\1\u04ad", "\12\64\7\uffff\4\64\1\u04ae\15\64\1\u04af\7\64\4\uffff\1\64"
                                                                                                                                      + "\1\uffff\32\64", "", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\12\64\7\uffff\32\64\4\uffff\1\u04b2\1\uffff\32\64", "\1\u04b4", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u04b6", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u04b9", "\1\u04ba", "", "\1\u04bb", "", "\1\u04bc", "\1\u04bd", "\1\u04be", "\1\u04bf", "\12\64\7\uffff\22\64\1\u04c0\7\64\4\uffff\1\64\1\uffff\32\64", "\1\u04c2", "\1\u04c3", "\1\u04c4", "", "", "\1\u04c5", "", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\1\u04c8", "\1\u04c9", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u04cb", "", "\1\u04cd\17\uffff\1\u04cc", "\12\64\7\uffff\22\64\1\u04ce\7\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u04d1", "\1\u04d2", "\1\u04d3", "\1\u04d4", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "", "", "\1\u04d6", "\1\u04d7", "\1\u04d8", "\1\u04d9", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u04dc", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "", "", "\1\u04df", "\1\u04e0", "\1\u04e1", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u04e3", "\1\u04e4", "", "\1\u04e5", "\1\u04e6", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u04e8", "\1\u04e9", "\1\u04ea", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u04ec", "\1\u04ed", "\1\u04ee", "\1\u04ef", "\1\u04f0", "\1\u04f1", "", "", "\1\u04f2", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u04f4", "\1\u04f5", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "", "\1\u04f7", "", "", "\1\u04f8", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u04fa", "\1\u04fb", "\1\u04fc", "", "", "\1\u04fd\17\uffff\1\u04fe", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\1\u0500", "", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0502", "\1\u0503", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0505", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0507", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\1\u0509", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u050c", "", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u050e", "", "\1\u050f", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0511", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "", "\1\u0513", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0515", "\1\u0516", "", "\1\u0517", "\1\u0518", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u051a", "", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "", "\1\u051c", "\1\u051d", "\1\u051e", "", "\1\u051f", "\1\u0520", "\1\u0521", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0525", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0527", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0529", "\12\64\7\uffff\4\64\1\u052b\15\64\1\u052a\7\64\4\uffff\1\64"
                                                                                                                                          + "\1\uffff\32\64", "\1\u052d", "\1\u052e", "", "\1\u052f", "\1\u0530", "", "\1\u0531", "\1\u0532", "", "\1\u0533", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0535", "\1\u0536", "\1\u0537", "", "\1\u0538", "", "\1\u0539", "\1\u053a", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\1\u053c", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "", "\1\u053e", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\1\u0541", "", "\1\u0542", "", "\1\u0543", "\1\u0544", "\1\u0545", "\1\u0546", "", "\1\u0547", "", "\1\u0548", "\1\u0549", "\1\u054a", "\1\u054b", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "", "", "\1\u054e", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0552", "", "\1\u0553", "\1\u0554", "\1\u0555", "\1\u0556", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0558", "\1\u0559", "", "\1\u055a", "\1\u055b", "\1\u055c", "\1\u055d", "\1\u055e", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\1\u0560", "", "\1\u0561", "", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0563", "\1\u0564", "\1\u0565", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0568", "\1\u0569", "\1\u056a", "\1\u056b", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "", "\1\u056d", "", "", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0571", "\1\u0572", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0575", "\1\u0576", "\1\u0577", "\1\u0578", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\1\u057a", "\1\u057b", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "", "\1\u057f", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0581", "\1\u0582", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "", "\1\u0586", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0588", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\1\u058d", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "", "", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "", "\1\u0590", "", "", "", "", "\1\u0591", "", "", "\1\u0592", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "\1\u0594", "", "\1\u0595", "\12\64\7\uffff\32\64\4\uffff\1\64\1\uffff\32\64", "" };

  static final short[] DFA25_eot = DFA.unpackEncodedString(DFA25_eotS);
  static final short[] DFA25_eof = DFA.unpackEncodedString(DFA25_eofS);
  static final char[] DFA25_min = DFA.unpackEncodedStringToUnsignedChars(DFA25_minS);
  static final char[] DFA25_max = DFA.unpackEncodedStringToUnsignedChars(DFA25_maxS);
  static final short[] DFA25_accept = DFA.unpackEncodedString(DFA25_acceptS);
  static final short[] DFA25_special = DFA.unpackEncodedString(DFA25_specialS);
  static final short[][] DFA25_transition;

  static {
    int numStates = DFA25_transitionS.length;
    DFA25_transition = new short[numStates][];
    for (int i = 0; i < numStates; i++) {
      DFA25_transition[i] = DFA.unpackEncodedString(DFA25_transitionS[i]);
    }
  }

  class DFA25 extends DFA {

    public DFA25(BaseRecognizer recognizer) {
      this.recognizer = recognizer;
      this.decisionNumber = 25;
      this.eot = DFA25_eot;
      this.eof = DFA25_eof;
      this.min = DFA25_min;
      this.max = DFA25_max;
      this.accept = DFA25_accept;
      this.special = DFA25_special;
      this.transition = DFA25_transition;
    }

    public String getDescription() {
      return "1:1: Tokens : ( KW_TRUE | KW_FALSE | KW_ALL | KW_NONE | KW_DEFAULT | KW_AND | KW_OR | KW_NOT | KW_LIKE | KW_IF | KW_EXISTS | KW_ASC | KW_DESC | KW_ORDER | KW_GROUP | KW_BY | KW_HAVING | KW_WHERE | KW_FROM | KW_AS | KW_SELECT | KW_DISTINCT | KW_INSERT | KW_OVERWRITE | KW_OUTER | KW_UNIQUEJOIN | KW_PRESERVE | KW_JOIN | KW_LEFT | KW_RIGHT | KW_FULL | KW_ON | KW_PARTITION | KW_PARTITIONS | KW_TABLE | KW_TABLES | KW_COLUMNS | KW_INDEX | KW_INDEXES | KW_REBUILD | KW_FUNCTIONS | KW_SHOW | KW_MSCK | KW_REPAIR | KW_DIRECTORY | KW_LOCAL | KW_TRANSFORM | KW_USING | KW_CLUSTER | KW_DISTRIBUTE | KW_SORT | KW_UNION | KW_LOAD | KW_EXPORT | KW_IMPORT | KW_REPLICATION | KW_METADATA | KW_DATA | KW_INPATH | KW_IS | KW_NULL | KW_CREATE | KW_EXTERNAL | KW_ALTER | KW_CHANGE | KW_COLUMN | KW_FIRST | KW_AFTER | KW_DESCRIBE | KW_DROP | KW_RENAME | KW_IGNORE | KW_PROTECTION | KW_TO | KW_COMMENT | KW_BOOLEAN | KW_TINYINT | KW_SMALLINT | KW_INT | KW_BIGINT | KW_FLOAT | KW_DOUBLE | KW_DATE | KW_DATETIME | KW_TIMESTAMP | KW_DECIMAL | KW_STRING | KW_CHAR | KW_VARCHAR | KW_ARRAY | KW_STRUCT | KW_MAP | KW_UNIONTYPE | KW_REDUCE | KW_PARTITIONED | KW_CLUSTERED | KW_SORTED | KW_INTO | KW_BUCKETS | KW_ROW | KW_ROWS | KW_FORMAT | KW_DELIMITED | KW_FIELDS | KW_TERMINATED | KW_ESCAPED | KW_COLLECTION | KW_ITEMS | KW_KEYS | KW_KEY_TYPE | KW_LINES | KW_STORED | KW_FILEFORMAT | KW_INPUTFORMAT | KW_OUTPUTFORMAT | KW_INPUTDRIVER | KW_OUTPUTDRIVER | KW_OFFLINE | KW_ENABLE | KW_DISABLE | KW_READONLY | KW_NO_DROP | KW_LOCATION | KW_TABLESAMPLE | KW_BUCKET | KW_OUT | KW_OF | KW_PERCENT | KW_CAST | KW_ADD | KW_REPLACE | KW_RLIKE | KW_REGEXP | KW_TEMPORARY | KW_FUNCTION | KW_MACRO | KW_FILE | KW_JAR | KW_EXPLAIN | KW_EXTENDED | KW_FORMATTED | KW_PRETTY | KW_DEPENDENCY | KW_LOGICAL | KW_SERDE | KW_WITH | KW_DEFERRED | KW_SERDEPROPERTIES | KW_DBPROPERTIES | KW_LIMIT | KW_SET | KW_UNSET | KW_TBLPROPERTIES | KW_IDXPROPERTIES | KW_VALUE_TYPE | KW_ELEM_TYPE | KW_DEFINED | KW_CASE | KW_WHEN | KW_THEN | KW_ELSE | KW_END | KW_MAPJOIN | KW_STREAMTABLE | KW_HOLD_DDLTIME | KW_CLUSTERSTATUS | KW_UTC | KW_UTCTIMESTAMP | KW_LONG | KW_DELETE | KW_PLUS | KW_MINUS | KW_FETCH | KW_INTERSECT | KW_VIEW | KW_IN | KW_DATABASE | KW_DATABASES | KW_MATERIALIZED | KW_SCHEMA | KW_SCHEMAS | KW_GRANT | KW_REVOKE | KW_SSL | KW_UNDO | KW_LOCK | KW_LOCKS | KW_UNLOCK | KW_SHARED | KW_EXCLUSIVE | KW_PROCEDURE | KW_UNSIGNED | KW_WHILE | KW_READ | KW_READS | KW_PURGE | KW_RANGE | KW_ANALYZE | KW_BEFORE | KW_BETWEEN | KW_BOTH | KW_BINARY | KW_CROSS | KW_CONTINUE | KW_CURSOR | KW_TRIGGER | KW_RECORDREADER | KW_RECORDWRITER | KW_SEMI | KW_LATERAL | KW_TOUCH | KW_ARCHIVE | KW_UNARCHIVE | KW_COMPUTE | KW_STATISTICS | KW_USE | KW_OPTION | KW_CONCATENATE | KW_SHOW_DATABASE | KW_UPDATE | KW_RESTRICT | KW_CASCADE | KW_SKEWED | KW_ROLLUP | KW_CUBE | KW_DIRECTORIES | KW_FOR | KW_WINDOW | KW_UNBOUNDED | KW_PRECEDING | KW_FOLLOWING | KW_CURRENT | KW_CURRENT_DATE | KW_CURRENT_TIMESTAMP | KW_LESS | KW_MORE | KW_OVER | KW_GROUPING | KW_SETS | KW_TRUNCATE | KW_NOSCAN | KW_PARTIALSCAN | KW_USER | KW_ROLE | KW_ROLES | KW_INNER | KW_EXCHANGE | KW_URI | KW_SERVER | KW_ADMIN | KW_OWNER | KW_PRINCIPALS | KW_COMPACT | KW_COMPACTIONS | KW_TRANSACTIONS | KW_REWRITE | KW_AUTHORIZATION | KW_CONF | KW_VALUES | KW_RELOAD | DOT | COLON | COMMA | SEMICOLON | LPAREN | RPAREN | LSQUARE | RSQUARE | LCURLY | RCURLY | EQUAL | EQUAL_NS | NOTEQUAL | LESSTHANOREQUALTO | LESSTHAN | GREATERTHANOREQUALTO | GREATERTHAN | DIVIDE | PLUS | MINUS | STAR | MOD | DIV | AMPERSAND | TILDE | BITWISEOR | BITWISEXOR | QUESTION | DOLLAR | StringLiteral | CharSetLiteral | BigintLiteral | SmallintLiteral | TinyintLiteral | DecimalLiteral | ByteLengthLiteral | Number | Identifier | CharSetName | WS | COMMENT );";
    }

    public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
      IntStream input = _input;
      int _s = s;
      switch (s) {
        case 0:
          int LA25_168 = input.LA(1);

          s = -1;
          if (((LA25_168 >= '\u0000' && LA25_168 <= '\uFFFF'))) {
            s = 363;
          }

          if (s >= 0) {
            return s;
          }
          break;
        case 1:
          int LA25_171 = input.LA(1);

          s = -1;
          if (((LA25_171 >= '\u0000' && LA25_171 <= '\uFFFF'))) {
            s = 365;
          }

          if (s >= 0) {
            return s;
          }
          break;
        case 2:
          int LA25_167 = input.LA(1);

          s = -1;
          if ((LA25_167 == '\'')) {
            s = 169;
          } else if (((LA25_167 >= '\u0000' && LA25_167 <= '&') || (LA25_167 >= '(' && LA25_167 <= '[')
              || (LA25_167 >= ']' && LA25_167 <= '\uFFFF'))) {
            s = 167;
          } else if ((LA25_167 == '\\')) {
            s = 168;
          }

          if (s >= 0) {
            return s;
          }
          break;
        case 3:
          int LA25_363 = input.LA(1);

          s = -1;
          if ((LA25_363 == '\'')) {
            s = 169;
          } else if (((LA25_363 >= '\u0000' && LA25_363 <= '&') || (LA25_363 >= '(' && LA25_363 <= '[')
              || (LA25_363 >= ']' && LA25_363 <= '\uFFFF'))) {
            s = 167;
          } else if ((LA25_363 == '\\')) {
            s = 168;
          }

          if (s >= 0) {
            return s;
          }
          break;
        case 4:
          int LA25_170 = input.LA(1);

          s = -1;
          if ((LA25_170 == '\"')) {
            s = 172;
          } else if (((LA25_170 >= '\u0000' && LA25_170 <= '!') || (LA25_170 >= '#' && LA25_170 <= '[')
              || (LA25_170 >= ']' && LA25_170 <= '\uFFFF'))) {
            s = 170;
          } else if ((LA25_170 == '\\')) {
            s = 171;
          }

          if (s >= 0) {
            return s;
          }
          break;
        case 5:
          int LA25_365 = input.LA(1);

          s = -1;
          if ((LA25_365 == '\"')) {
            s = 172;
          } else if (((LA25_365 >= '\u0000' && LA25_365 <= '!') || (LA25_365 >= '#' && LA25_365 <= '[')
              || (LA25_365 >= ']' && LA25_365 <= '\uFFFF'))) {
            s = 170;
          } else if ((LA25_365 == '\\')) {
            s = 171;
          }

          if (s >= 0) {
            return s;
          }
          break;
        case 6:
          int LA25_48 = input.LA(1);

          s = -1;
          if (((LA25_48 >= '\u0000' && LA25_48 <= '&') || (LA25_48 >= '(' && LA25_48 <= '[')
              || (LA25_48 >= ']' && LA25_48 <= '\uFFFF'))) {
            s = 167;
          } else if ((LA25_48 == '\\')) {
            s = 168;
          } else if ((LA25_48 == '\'')) {
            s = 169;
          }

          if (s >= 0) {
            return s;
          }
          break;
        case 7:
          int LA25_49 = input.LA(1);

          s = -1;
          if (((LA25_49 >= '\u0000' && LA25_49 <= '!') || (LA25_49 >= '#' && LA25_49 <= '[')
              || (LA25_49 >= ']' && LA25_49 <= '\uFFFF'))) {
            s = 170;
          } else if ((LA25_49 == '\\')) {
            s = 171;
          } else if ((LA25_49 == '\"')) {
            s = 172;
          }

          if (s >= 0) {
            return s;
          }
          break;
      }
      NoViableAltException nvae = new NoViableAltException(getDescription(), 25, _s, input);
      error(nvae);
      throw nvae;
    }
  }
}
//spotless:on
