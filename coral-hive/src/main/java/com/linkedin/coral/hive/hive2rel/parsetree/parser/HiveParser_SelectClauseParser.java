// $ANTLR 3.4 SelectClauseParser.g 2017-10-10 09:21:01

package com.linkedin.coral.hive.hive2rel.parsetree.parser;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.BitSet;
import org.antlr.runtime.DFA;
import org.antlr.runtime.IntStream;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.RewriteEarlyExitException;
import org.antlr.runtime.tree.RewriteRuleSubtreeStream;
import org.antlr.runtime.tree.RewriteRuleTokenStream;
import org.antlr.runtime.tree.TreeAdaptor;


/**
 Licensed to the Apache Software Foundation (ASF) under one or more
 contributor license agreements.  See the NOTICE file distributed with
 this work for additional information regarding copyright ownership.
 The ASF licenses this file to You under the Apache License, Version 2.0
 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
//CHECKSTYLE:OFF
@SuppressWarnings({"all", "warnings", "unchecked"})
public class HiveParser_SelectClauseParser extends Parser {
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
  public static final int TOK_ADMIN_OPTION_FOR = 592;
  public static final int TOK_ALIASLIST = 593;
  public static final int TOK_ALLCOLREF = 594;
  public static final int TOK_ALTERDATABASE_OWNER = 595;
  public static final int TOK_ALTERDATABASE_PROPERTIES = 596;
  public static final int TOK_ALTERINDEX_PROPERTIES = 597;
  public static final int TOK_ALTERINDEX_REBUILD = 598;
  public static final int TOK_ALTERTABLE = 599;
  public static final int TOK_ALTERTABLE_ADDCOLS = 600;
  public static final int TOK_ALTERTABLE_ADDPARTS = 601;
  public static final int TOK_ALTERTABLE_ARCHIVE = 602;
  public static final int TOK_ALTERTABLE_BUCKETS = 603;
  public static final int TOK_ALTERTABLE_CHANGECOL_AFTER_POSITION = 604;
  public static final int TOK_ALTERTABLE_CLUSTER_SORT = 605;
  public static final int TOK_ALTERTABLE_COMPACT = 606;
  public static final int TOK_ALTERTABLE_DROPPARTS = 607;
  public static final int TOK_ALTERTABLE_DROPPROPERTIES = 608;
  public static final int TOK_ALTERTABLE_EXCHANGEPARTITION = 609;
  public static final int TOK_ALTERTABLE_FILEFORMAT = 610;
  public static final int TOK_ALTERTABLE_MERGEFILES = 611;
  public static final int TOK_ALTERTABLE_PARTCOLTYPE = 612;
  public static final int TOK_ALTERTABLE_PROPERTIES = 613;
  public static final int TOK_ALTERTABLE_PROTECTMODE = 614;
  public static final int TOK_ALTERTABLE_RENAME = 615;
  public static final int TOK_ALTERTABLE_RENAMECOL = 616;
  public static final int TOK_ALTERTABLE_RENAMEPART = 617;
  public static final int TOK_ALTERTABLE_REPLACECOLS = 618;
  public static final int TOK_ALTERTABLE_SERDEPROPERTIES = 619;
  public static final int TOK_ALTERTABLE_SERIALIZER = 620;
  public static final int TOK_ALTERTABLE_SKEWED = 621;
  public static final int TOK_ALTERTABLE_SKEWED_LOCATION = 622;
  public static final int TOK_ALTERTABLE_TOUCH = 623;
  public static final int TOK_ALTERTABLE_UNARCHIVE = 624;
  public static final int TOK_ALTERTABLE_UPDATECOLSTATS = 625;
  public static final int TOK_ALTERVIEW = 626;
  public static final int TOK_ALTERVIEW_ADDPARTS = 627;
  public static final int TOK_ALTERVIEW_DROPPARTS = 628;
  public static final int TOK_ALTERVIEW_DROPPROPERTIES = 629;
  public static final int TOK_ALTERVIEW_PROPERTIES = 630;
  public static final int TOK_ALTERVIEW_RENAME = 631;
  public static final int TOK_ANALYZE = 632;
  public static final int TOK_ANONYMOUS = 633;
  public static final int TOK_ARCHIVE = 634;
  public static final int TOK_BIGINT = 635;
  public static final int TOK_BINARY = 636;
  public static final int TOK_BOOLEAN = 637;
  public static final int TOK_CASCADE = 638;
  public static final int TOK_CHAR = 639;
  public static final int TOK_CHARSETLITERAL = 640;
  public static final int TOK_CLUSTERBY = 641;
  public static final int TOK_COLTYPELIST = 642;
  public static final int TOK_COL_NAME = 643;
  public static final int TOK_CREATEDATABASE = 644;
  public static final int TOK_CREATEFUNCTION = 645;
  public static final int TOK_CREATEINDEX = 646;
  public static final int TOK_CREATEINDEX_INDEXTBLNAME = 647;
  public static final int TOK_CREATEMACRO = 648;
  public static final int TOK_CREATEROLE = 649;
  public static final int TOK_CREATETABLE = 650;
  public static final int TOK_CREATEVIEW = 651;
  public static final int TOK_CROSSJOIN = 652;
  public static final int TOK_CTE = 653;
  public static final int TOK_CUBE_GROUPBY = 654;
  public static final int TOK_DATABASECOMMENT = 655;
  public static final int TOK_DATABASEPROPERTIES = 656;
  public static final int TOK_DATE = 657;
  public static final int TOK_DATELITERAL = 658;
  public static final int TOK_DATETIME = 659;
  public static final int TOK_DBPROPLIST = 660;
  public static final int TOK_DB_TYPE = 661;
  public static final int TOK_DECIMAL = 662;
  public static final int TOK_DEFERRED_REBUILDINDEX = 663;
  public static final int TOK_DELETE_FROM = 664;
  public static final int TOK_DESCDATABASE = 665;
  public static final int TOK_DESCFUNCTION = 666;
  public static final int TOK_DESCTABLE = 667;
  public static final int TOK_DESTINATION = 668;
  public static final int TOK_DIR = 669;
  public static final int TOK_DISABLE = 670;
  public static final int TOK_DISTRIBUTEBY = 671;
  public static final int TOK_DOUBLE = 672;
  public static final int TOK_DROPDATABASE = 673;
  public static final int TOK_DROPFUNCTION = 674;
  public static final int TOK_DROPINDEX = 675;
  public static final int TOK_DROPMACRO = 676;
  public static final int TOK_DROPROLE = 677;
  public static final int TOK_DROPTABLE = 678;
  public static final int TOK_DROPVIEW = 679;
  public static final int TOK_ENABLE = 680;
  public static final int TOK_EXPLAIN = 681;
  public static final int TOK_EXPLAIN_SQ_REWRITE = 682;
  public static final int TOK_EXPLIST = 683;
  public static final int TOK_EXPORT = 684;
  public static final int TOK_FALSE = 685;
  public static final int TOK_FILE = 686;
  public static final int TOK_FILEFORMAT_GENERIC = 687;
  public static final int TOK_FLOAT = 688;
  public static final int TOK_FROM = 689;
  public static final int TOK_FULLOUTERJOIN = 690;
  public static final int TOK_FUNCTION = 691;
  public static final int TOK_FUNCTIONDI = 692;
  public static final int TOK_FUNCTIONSTAR = 693;
  public static final int TOK_GRANT = 694;
  public static final int TOK_GRANT_OPTION_FOR = 695;
  public static final int TOK_GRANT_ROLE = 696;
  public static final int TOK_GRANT_WITH_ADMIN_OPTION = 697;
  public static final int TOK_GRANT_WITH_OPTION = 698;
  public static final int TOK_GROUP = 699;
  public static final int TOK_GROUPBY = 700;
  public static final int TOK_GROUPING_SETS = 701;
  public static final int TOK_GROUPING_SETS_EXPRESSION = 702;
  public static final int TOK_HAVING = 703;
  public static final int TOK_HINT = 704;
  public static final int TOK_HINTARGLIST = 705;
  public static final int TOK_HINTLIST = 706;
  public static final int TOK_HOLD_DDLTIME = 707;
  public static final int TOK_IFEXISTS = 708;
  public static final int TOK_IFNOTEXISTS = 709;
  public static final int TOK_IGNOREPROTECTION = 710;
  public static final int TOK_IMPORT = 711;
  public static final int TOK_INDEXCOMMENT = 712;
  public static final int TOK_INDEXPROPERTIES = 713;
  public static final int TOK_INDEXPROPLIST = 714;
  public static final int TOK_INSERT = 715;
  public static final int TOK_INSERT_INTO = 716;
  public static final int TOK_INT = 717;
  public static final int TOK_ISNOTNULL = 718;
  public static final int TOK_ISNULL = 719;
  public static final int TOK_JAR = 720;
  public static final int TOK_JOIN = 721;
  public static final int TOK_LATERAL_VIEW = 722;
  public static final int TOK_LATERAL_VIEW_OUTER = 723;
  public static final int TOK_LEFTOUTERJOIN = 724;
  public static final int TOK_LEFTSEMIJOIN = 725;
  public static final int TOK_LENGTH = 726;
  public static final int TOK_LIKETABLE = 727;
  public static final int TOK_LIMIT = 728;
  public static final int TOK_LIST = 729;
  public static final int TOK_LOAD = 730;
  public static final int TOK_LOCATION = 731;
  public static final int TOK_LOCKDB = 732;
  public static final int TOK_LOCKTABLE = 733;
  public static final int TOK_MAP = 734;
  public static final int TOK_MAPJOIN = 735;
  public static final int TOK_METADATA = 736;
  public static final int TOK_MSCK = 737;
  public static final int TOK_NOT_CLUSTERED = 738;
  public static final int TOK_NOT_SORTED = 739;
  public static final int TOK_NO_DROP = 740;
  public static final int TOK_NULL = 741;
  public static final int TOK_OFFLINE = 742;
  public static final int TOK_OP_ADD = 743;
  public static final int TOK_OP_AND = 744;
  public static final int TOK_OP_BITAND = 745;
  public static final int TOK_OP_BITNOT = 746;
  public static final int TOK_OP_BITOR = 747;
  public static final int TOK_OP_BITXOR = 748;
  public static final int TOK_OP_DIV = 749;
  public static final int TOK_OP_EQ = 750;
  public static final int TOK_OP_GE = 751;
  public static final int TOK_OP_GT = 752;
  public static final int TOK_OP_LE = 753;
  public static final int TOK_OP_LIKE = 754;
  public static final int TOK_OP_LT = 755;
  public static final int TOK_OP_MOD = 756;
  public static final int TOK_OP_MUL = 757;
  public static final int TOK_OP_NE = 758;
  public static final int TOK_OP_NOT = 759;
  public static final int TOK_OP_OR = 760;
  public static final int TOK_OP_SUB = 761;
  public static final int TOK_ORDERBY = 762;
  public static final int TOK_ORREPLACE = 763;
  public static final int TOK_PARTITIONFILEFORMAT = 764;
  public static final int TOK_PARTITIONINGSPEC = 765;
  public static final int TOK_PARTITIONSERDEPROPERTIES = 766;
  public static final int TOK_PARTSPEC = 767;
  public static final int TOK_PARTVAL = 768;
  public static final int TOK_PERCENT = 769;
  public static final int TOK_PRINCIPAL_NAME = 770;
  public static final int TOK_PRIVILEGE = 771;
  public static final int TOK_PRIVILEGE_LIST = 772;
  public static final int TOK_PRIV_ALL = 773;
  public static final int TOK_PRIV_ALTER_DATA = 774;
  public static final int TOK_PRIV_ALTER_METADATA = 775;
  public static final int TOK_PRIV_CREATE = 776;
  public static final int TOK_PRIV_DELETE = 777;
  public static final int TOK_PRIV_DROP = 778;
  public static final int TOK_PRIV_INDEX = 779;
  public static final int TOK_PRIV_INSERT = 780;
  public static final int TOK_PRIV_LOCK = 781;
  public static final int TOK_PRIV_OBJECT = 782;
  public static final int TOK_PRIV_OBJECT_COL = 783;
  public static final int TOK_PRIV_SELECT = 784;
  public static final int TOK_PRIV_SHOW_DATABASE = 785;
  public static final int TOK_PTBLFUNCTION = 786;
  public static final int TOK_QUERY = 787;
  public static final int TOK_READONLY = 788;
  public static final int TOK_RECORDREADER = 789;
  public static final int TOK_RECORDWRITER = 790;
  public static final int TOK_RELOADFUNCTION = 791;
  public static final int TOK_REPLICATION = 792;
  public static final int TOK_RESOURCE_ALL = 793;
  public static final int TOK_RESOURCE_LIST = 794;
  public static final int TOK_RESOURCE_URI = 795;
  public static final int TOK_RESTRICT = 796;
  public static final int TOK_REVOKE = 797;
  public static final int TOK_REVOKE_ROLE = 798;
  public static final int TOK_RIGHTOUTERJOIN = 799;
  public static final int TOK_ROLE = 800;
  public static final int TOK_ROLLUP_GROUPBY = 801;
  public static final int TOK_ROWCOUNT = 802;
  public static final int TOK_SELECT = 803;
  public static final int TOK_SELECTDI = 804;
  public static final int TOK_SELEXPR = 805;
  public static final int TOK_SERDE = 806;
  public static final int TOK_SERDENAME = 807;
  public static final int TOK_SERDEPROPS = 808;
  public static final int TOK_SERVER_TYPE = 809;
  public static final int TOK_SET_COLUMNS_CLAUSE = 810;
  public static final int TOK_SHOWCOLUMNS = 811;
  public static final int TOK_SHOWCONF = 812;
  public static final int TOK_SHOWDATABASES = 813;
  public static final int TOK_SHOWDBLOCKS = 814;
  public static final int TOK_SHOWFUNCTIONS = 815;
  public static final int TOK_SHOWINDEXES = 816;
  public static final int TOK_SHOWLOCKS = 817;
  public static final int TOK_SHOWPARTITIONS = 818;
  public static final int TOK_SHOWTABLES = 819;
  public static final int TOK_SHOW_COMPACTIONS = 820;
  public static final int TOK_SHOW_CREATETABLE = 821;
  public static final int TOK_SHOW_GRANT = 822;
  public static final int TOK_SHOW_ROLES = 823;
  public static final int TOK_SHOW_ROLE_GRANT = 824;
  public static final int TOK_SHOW_ROLE_PRINCIPALS = 825;
  public static final int TOK_SHOW_SET_ROLE = 826;
  public static final int TOK_SHOW_TABLESTATUS = 827;
  public static final int TOK_SHOW_TBLPROPERTIES = 828;
  public static final int TOK_SHOW_TRANSACTIONS = 829;
  public static final int TOK_SKEWED_LOCATIONS = 830;
  public static final int TOK_SKEWED_LOCATION_LIST = 831;
  public static final int TOK_SKEWED_LOCATION_MAP = 832;
  public static final int TOK_SMALLINT = 833;
  public static final int TOK_SORTBY = 834;
  public static final int TOK_STORAGEHANDLER = 835;
  public static final int TOK_STOREDASDIRS = 836;
  public static final int TOK_STREAMTABLE = 837;
  public static final int TOK_STRING = 838;
  public static final int TOK_STRINGLITERALSEQUENCE = 839;
  public static final int TOK_STRUCT = 840;
  public static final int TOK_SUBQUERY = 841;
  public static final int TOK_SUBQUERY_EXPR = 842;
  public static final int TOK_SUBQUERY_OP = 843;
  public static final int TOK_SUBQUERY_OP_NOTEXISTS = 844;
  public static final int TOK_SUBQUERY_OP_NOTIN = 845;
  public static final int TOK_SWITCHDATABASE = 846;
  public static final int TOK_TAB = 847;
  public static final int TOK_TABALIAS = 848;
  public static final int TOK_TABCOL = 849;
  public static final int TOK_TABCOLLIST = 850;
  public static final int TOK_TABCOLNAME = 851;
  public static final int TOK_TABCOLVALUE = 852;
  public static final int TOK_TABCOLVALUES = 853;
  public static final int TOK_TABCOLVALUE_PAIR = 854;
  public static final int TOK_TABLEBUCKETSAMPLE = 855;
  public static final int TOK_TABLECOMMENT = 856;
  public static final int TOK_TABLEFILEFORMAT = 857;
  public static final int TOK_TABLEPARTCOLS = 858;
  public static final int TOK_TABLEPROPERTIES = 859;
  public static final int TOK_TABLEPROPERTY = 860;
  public static final int TOK_TABLEPROPLIST = 861;
  public static final int TOK_TABLEROWFORMAT = 862;
  public static final int TOK_TABLEROWFORMATCOLLITEMS = 863;
  public static final int TOK_TABLEROWFORMATFIELD = 864;
  public static final int TOK_TABLEROWFORMATLINES = 865;
  public static final int TOK_TABLEROWFORMATMAPKEYS = 866;
  public static final int TOK_TABLEROWFORMATNULL = 867;
  public static final int TOK_TABLESERIALIZER = 868;
  public static final int TOK_TABLESKEWED = 869;
  public static final int TOK_TABLESPLITSAMPLE = 870;
  public static final int TOK_TABLE_OR_COL = 871;
  public static final int TOK_TABLE_PARTITION = 872;
  public static final int TOK_TABLE_TYPE = 873;
  public static final int TOK_TABNAME = 874;
  public static final int TOK_TABREF = 875;
  public static final int TOK_TABSORTCOLNAMEASC = 876;
  public static final int TOK_TABSORTCOLNAMEDESC = 877;
  public static final int TOK_TABSRC = 878;
  public static final int TOK_TABTYPE = 879;
  public static final int TOK_TEMPORARY = 880;
  public static final int TOK_TIMESTAMP = 881;
  public static final int TOK_TIMESTAMPLITERAL = 882;
  public static final int TOK_TINYINT = 883;
  public static final int TOK_TMP_FILE = 884;
  public static final int TOK_TRANSFORM = 885;
  public static final int TOK_TRUE = 886;
  public static final int TOK_TRUNCATETABLE = 887;
  public static final int TOK_UNION = 888;
  public static final int TOK_UNIONTYPE = 889;
  public static final int TOK_UNIQUEJOIN = 890;
  public static final int TOK_UNLOCKDB = 891;
  public static final int TOK_UNLOCKTABLE = 892;
  public static final int TOK_UPDATE_TABLE = 893;
  public static final int TOK_URI_TYPE = 894;
  public static final int TOK_USER = 895;
  public static final int TOK_USERSCRIPTCOLNAMES = 896;
  public static final int TOK_USERSCRIPTCOLSCHEMA = 897;
  public static final int TOK_VALUES_TABLE = 898;
  public static final int TOK_VALUE_ROW = 899;
  public static final int TOK_VARCHAR = 900;
  public static final int TOK_VIEWPARTCOLS = 901;
  public static final int TOK_VIRTUAL_TABLE = 902;
  public static final int TOK_VIRTUAL_TABREF = 903;
  public static final int TOK_WHERE = 904;
  public static final int TOK_WINDOWDEF = 905;
  public static final int TOK_WINDOWRANGE = 906;
  public static final int TOK_WINDOWSPEC = 907;
  public static final int TOK_WINDOWVALUES = 908;

  // delegates
  public Parser[] getDelegates() {
    return new Parser[]{};
  }

  // delegators
  public HiveParser gHiveParser;
  public HiveParser gParent;

  public HiveParser_SelectClauseParser(TokenStream input, HiveParser gHiveParser) {
    this(input, new RecognizerSharedState(), gHiveParser);
  }

  public HiveParser_SelectClauseParser(TokenStream input, RecognizerSharedState state, HiveParser gHiveParser) {
    super(input, state);
    this.gHiveParser = gHiveParser;
    gParent = gHiveParser;
  }

  protected TreeAdaptor adaptor = new CommonTreeAdaptor();

  public void setTreeAdaptor(TreeAdaptor adaptor) {
    this.adaptor = adaptor;
  }

  public TreeAdaptor getTreeAdaptor() {
    return adaptor;
  }

  public String[] getTokenNames() {
    return HiveParser.tokenNames;
  }

  public String getGrammarFileName() {
    return "SelectClauseParser.g";
  }

  @Override
  public Object recoverFromMismatchedSet(IntStream input, RecognitionException re, BitSet follow)
      throws RecognitionException {
    throw re;
  }

  @Override
  public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
    gParent.errors.add(new ParseError(gParent, e, tokenNames));
  }

  public static class selectClause_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "selectClause"
  // SelectClauseParser.g:48:1: selectClause : ( KW_SELECT ( hintClause )? ( ( ( KW_ALL |dist= KW_DISTINCT )? selectList ) | (transform= KW_TRANSFORM selectTrfmClause ) ) -> {$transform == null && $dist == null}? ^( TOK_SELECT ( hintClause )? selectList ) -> {$transform == null && $dist != null}? ^( TOK_SELECTDI ( hintClause )? selectList ) -> ^( TOK_SELECT ( hintClause )? ^( TOK_SELEXPR selectTrfmClause ) ) | trfmClause -> ^( TOK_SELECT ^( TOK_SELEXPR trfmClause ) ) );
  public final HiveParser_SelectClauseParser.selectClause_return selectClause() throws RecognitionException {
    HiveParser_SelectClauseParser.selectClause_return retval = new HiveParser_SelectClauseParser.selectClause_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token dist = null;
    Token transform = null;
    Token KW_SELECT1 = null;
    Token KW_ALL3 = null;
    HiveParser_SelectClauseParser.hintClause_return hintClause2 = null;

    HiveParser_SelectClauseParser.selectList_return selectList4 = null;

    HiveParser_SelectClauseParser.selectTrfmClause_return selectTrfmClause5 = null;

    HiveParser_SelectClauseParser.trfmClause_return trfmClause6 = null;

    CommonTree dist_tree = null;
    CommonTree transform_tree = null;
    CommonTree KW_SELECT1_tree = null;
    CommonTree KW_ALL3_tree = null;
    RewriteRuleTokenStream stream_KW_TRANSFORM = new RewriteRuleTokenStream(adaptor, "token KW_TRANSFORM");
    RewriteRuleTokenStream stream_KW_DISTINCT = new RewriteRuleTokenStream(adaptor, "token KW_DISTINCT");
    RewriteRuleTokenStream stream_KW_SELECT = new RewriteRuleTokenStream(adaptor, "token KW_SELECT");
    RewriteRuleTokenStream stream_KW_ALL = new RewriteRuleTokenStream(adaptor, "token KW_ALL");
    RewriteRuleSubtreeStream stream_trfmClause = new RewriteRuleSubtreeStream(adaptor, "rule trfmClause");
    RewriteRuleSubtreeStream stream_selectList = new RewriteRuleSubtreeStream(adaptor, "rule selectList");
    RewriteRuleSubtreeStream stream_selectTrfmClause = new RewriteRuleSubtreeStream(adaptor, "rule selectTrfmClause");
    RewriteRuleSubtreeStream stream_hintClause = new RewriteRuleSubtreeStream(adaptor, "rule hintClause");
    gParent.pushMsg("select clause", state);
    try {
      // SelectClauseParser.g:51:5: ( KW_SELECT ( hintClause )? ( ( ( KW_ALL |dist= KW_DISTINCT )? selectList ) | (transform= KW_TRANSFORM selectTrfmClause ) ) -> {$transform == null && $dist == null}? ^( TOK_SELECT ( hintClause )? selectList ) -> {$transform == null && $dist != null}? ^( TOK_SELECTDI ( hintClause )? selectList ) -> ^( TOK_SELECT ( hintClause )? ^( TOK_SELEXPR selectTrfmClause ) ) | trfmClause -> ^( TOK_SELECT ^( TOK_SELEXPR trfmClause ) ) )
      int alt4 = 2;
      switch (input.LA(1)) {
        case KW_SELECT: {
          alt4 = 1;
        }
        break;
        case KW_MAP:
        case KW_REDUCE: {
          alt4 = 2;
        }
        break;
        default:
          NoViableAltException nvae = new NoViableAltException("", 4, 0, input);

          throw nvae;
      }

      switch (alt4) {
        case 1:
          // SelectClauseParser.g:52:5: KW_SELECT ( hintClause )? ( ( ( KW_ALL |dist= KW_DISTINCT )? selectList ) | (transform= KW_TRANSFORM selectTrfmClause ) )
        {
          KW_SELECT1 = (Token) match(input, KW_SELECT, FOLLOW_KW_SELECT_in_selectClause71);
          stream_KW_SELECT.add(KW_SELECT1);

          // SelectClauseParser.g:52:15: ( hintClause )?
          int alt1 = 2;
          switch (input.LA(1)) {
            case DIVIDE: {
              alt1 = 1;
            }
            break;
          }

          switch (alt1) {
            case 1:
              // SelectClauseParser.g:52:15: hintClause
            {
              pushFollow(FOLLOW_hintClause_in_selectClause73);
              hintClause2 = hintClause();

              state._fsp--;

              stream_hintClause.add(hintClause2.getTree());
            }
            break;
          }

          // SelectClauseParser.g:52:27: ( ( ( KW_ALL |dist= KW_DISTINCT )? selectList ) | (transform= KW_TRANSFORM selectTrfmClause ) )
          int alt3 = 2;
          switch (input.LA(1)) {
            case BigintLiteral:
            case CharSetName:
            case DecimalLiteral:
            case Identifier:
            case KW_ADD:
            case KW_ADMIN:
            case KW_AFTER:
            case KW_ALL:
            case KW_ALTER:
            case KW_ANALYZE:
            case KW_ARCHIVE:
            case KW_ARRAY:
            case KW_AS:
            case KW_ASC:
            case KW_AUTHORIZATION:
            case KW_BEFORE:
            case KW_BETWEEN:
            case KW_BIGINT:
            case KW_BINARY:
            case KW_BOOLEAN:
            case KW_BOTH:
            case KW_BUCKET:
            case KW_BUCKETS:
            case KW_BY:
            case KW_CASCADE:
            case KW_CASE:
            case KW_CAST:
            case KW_CHANGE:
            case KW_CLUSTER:
            case KW_CLUSTERED:
            case KW_CLUSTERSTATUS:
            case KW_COLLECTION:
            case KW_COLUMNS:
            case KW_COMMENT:
            case KW_COMPACT:
            case KW_COMPACTIONS:
            case KW_COMPUTE:
            case KW_CONCATENATE:
            case KW_CONTINUE:
            case KW_CREATE:
            case KW_CUBE:
            case KW_CURRENT_DATE:
            case KW_CURRENT_TIMESTAMP:
            case KW_CURSOR:
            case KW_DATA:
            case KW_DATABASES:
            case KW_DATE:
            case KW_DATETIME:
            case KW_DBPROPERTIES:
            case KW_DECIMAL:
            case KW_DEFAULT:
            case KW_DEFERRED:
            case KW_DEFINED:
            case KW_DELETE:
            case KW_DELIMITED:
            case KW_DEPENDENCY:
            case KW_DESC:
            case KW_DESCRIBE:
            case KW_DIRECTORIES:
            case KW_DIRECTORY:
            case KW_DISABLE:
            case KW_DISTINCT:
            case KW_DISTRIBUTE:
            case KW_DOUBLE:
            case KW_DROP:
            case KW_ELEM_TYPE:
            case KW_ENABLE:
            case KW_ESCAPED:
            case KW_EXCLUSIVE:
            case KW_EXISTS:
            case KW_EXPLAIN:
            case KW_EXPORT:
            case KW_EXTERNAL:
            case KW_FALSE:
            case KW_FETCH:
            case KW_FIELDS:
            case KW_FILE:
            case KW_FILEFORMAT:
            case KW_FIRST:
            case KW_FLOAT:
            case KW_FOR:
            case KW_FORMAT:
            case KW_FORMATTED:
            case KW_FULL:
            case KW_FUNCTIONS:
            case KW_GRANT:
            case KW_GROUP:
            case KW_GROUPING:
            case KW_HOLD_DDLTIME:
            case KW_IDXPROPERTIES:
            case KW_IF:
            case KW_IGNORE:
            case KW_IMPORT:
            case KW_IN:
            case KW_INDEX:
            case KW_INDEXES:
            case KW_INNER:
            case KW_INPATH:
            case KW_INPUTDRIVER:
            case KW_INPUTFORMAT:
            case KW_INSERT:
            case KW_INT:
            case KW_INTERSECT:
            case KW_INTO:
            case KW_IS:
            case KW_ITEMS:
            case KW_JAR:
            case KW_KEYS:
            case KW_KEY_TYPE:
            case KW_LATERAL:
            case KW_LEFT:
            case KW_LIKE:
            case KW_LIMIT:
            case KW_LINES:
            case KW_LOAD:
            case KW_LOCAL:
            case KW_LOCATION:
            case KW_LOCK:
            case KW_LOCKS:
            case KW_LOGICAL:
            case KW_LONG:
            case KW_MAP:
            case KW_MAPJOIN:
            case KW_MATERIALIZED:
            case KW_METADATA:
            case KW_MINUS:
            case KW_MSCK:
            case KW_NONE:
            case KW_NOSCAN:
            case KW_NOT:
            case KW_NO_DROP:
            case KW_NULL:
            case KW_OF:
            case KW_OFFLINE:
            case KW_OPTION:
            case KW_ORDER:
            case KW_OUT:
            case KW_OUTER:
            case KW_OUTPUTDRIVER:
            case KW_OUTPUTFORMAT:
            case KW_OVERWRITE:
            case KW_OWNER:
            case KW_PARTITION:
            case KW_PARTITIONED:
            case KW_PARTITIONS:
            case KW_PERCENT:
            case KW_PLUS:
            case KW_PRETTY:
            case KW_PRINCIPALS:
            case KW_PROCEDURE:
            case KW_PROTECTION:
            case KW_PURGE:
            case KW_RANGE:
            case KW_READ:
            case KW_READONLY:
            case KW_READS:
            case KW_REBUILD:
            case KW_RECORDREADER:
            case KW_RECORDWRITER:
            case KW_REGEXP:
            case KW_RELOAD:
            case KW_RENAME:
            case KW_REPAIR:
            case KW_REPLACE:
            case KW_REPLICATION:
            case KW_RESTRICT:
            case KW_REVOKE:
            case KW_REWRITE:
            case KW_RIGHT:
            case KW_RLIKE:
            case KW_ROLE:
            case KW_ROLES:
            case KW_ROLLUP:
            case KW_ROW:
            case KW_ROWS:
            case KW_SCHEMA:
            case KW_SCHEMAS:
            case KW_SEMI:
            case KW_SERDE:
            case KW_SERDEPROPERTIES:
            case KW_SERVER:
            case KW_SET:
            case KW_SETS:
            case KW_SHARED:
            case KW_SHOW:
            case KW_SHOW_DATABASE:
            case KW_SKEWED:
            case KW_SMALLINT:
            case KW_SORT:
            case KW_SORTED:
            case KW_SSL:
            case KW_STATISTICS:
            case KW_STORED:
            case KW_STREAMTABLE:
            case KW_STRING:
            case KW_STRUCT:
            case KW_TABLE:
            case KW_TABLES:
            case KW_TBLPROPERTIES:
            case KW_TEMPORARY:
            case KW_TERMINATED:
            case KW_TIMESTAMP:
            case KW_TINYINT:
            case KW_TO:
            case KW_TOUCH:
            case KW_TRANSACTIONS:
            case KW_TRIGGER:
            case KW_TRUE:
            case KW_TRUNCATE:
            case KW_UNARCHIVE:
            case KW_UNDO:
            case KW_UNION:
            case KW_UNIONTYPE:
            case KW_UNLOCK:
            case KW_UNSET:
            case KW_UNSIGNED:
            case KW_UPDATE:
            case KW_URI:
            case KW_USE:
            case KW_USER:
            case KW_USING:
            case KW_UTC:
            case KW_UTCTIMESTAMP:
            case KW_VALUES:
            case KW_VALUE_TYPE:
            case KW_VIEW:
            case KW_WHILE:
            case KW_WITH:
            case LPAREN:
            case MINUS:
            case Number:
            case PLUS:
            case STAR:
            case SmallintLiteral:
            case StringLiteral:
            case TILDE:
            case TinyintLiteral: {
              alt3 = 1;
            }
            break;
            case KW_TRANSFORM: {
              alt3 = 2;
            }
            break;
            default:
              NoViableAltException nvae = new NoViableAltException("", 3, 0, input);

              throw nvae;
          }

          switch (alt3) {
            case 1:
              // SelectClauseParser.g:52:28: ( ( KW_ALL |dist= KW_DISTINCT )? selectList )
            {
              // SelectClauseParser.g:52:28: ( ( KW_ALL |dist= KW_DISTINCT )? selectList )
              // SelectClauseParser.g:52:29: ( KW_ALL |dist= KW_DISTINCT )? selectList
              {
                // SelectClauseParser.g:52:29: ( KW_ALL |dist= KW_DISTINCT )?
                int alt2 = 3;
                alt2 = dfa2.predict(input);
                switch (alt2) {
                  case 1:
                    // SelectClauseParser.g:52:30: KW_ALL
                  {
                    KW_ALL3 = (Token) match(input, KW_ALL, FOLLOW_KW_ALL_in_selectClause79);
                    stream_KW_ALL.add(KW_ALL3);
                  }
                  break;
                  case 2:
                    // SelectClauseParser.g:52:39: dist= KW_DISTINCT
                  {
                    dist = (Token) match(input, KW_DISTINCT, FOLLOW_KW_DISTINCT_in_selectClause85);
                    stream_KW_DISTINCT.add(dist);
                  }
                  break;
                }

                pushFollow(FOLLOW_selectList_in_selectClause89);
                selectList4 = selectList();

                state._fsp--;

                stream_selectList.add(selectList4.getTree());
              }
            }
            break;
            case 2:
              // SelectClauseParser.g:53:29: (transform= KW_TRANSFORM selectTrfmClause )
            {
              // SelectClauseParser.g:53:29: (transform= KW_TRANSFORM selectTrfmClause )
              // SelectClauseParser.g:53:30: transform= KW_TRANSFORM selectTrfmClause
              {
                transform = (Token) match(input, KW_TRANSFORM, FOLLOW_KW_TRANSFORM_in_selectClause123);
                stream_KW_TRANSFORM.add(transform);

                pushFollow(FOLLOW_selectTrfmClause_in_selectClause125);
                selectTrfmClause5 = selectTrfmClause();

                state._fsp--;

                stream_selectTrfmClause.add(selectTrfmClause5.getTree());
              }
            }
            break;
          }

          // AST REWRITE
          // elements: hintClause, hintClause, selectList, selectList, hintClause, selectTrfmClause
          // token labels:
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 54:6: -> {$transform == null && $dist == null}? ^( TOK_SELECT ( hintClause )? selectList )
          if (transform == null && dist == null) {
            // SelectClauseParser.g:54:48: ^( TOK_SELECT ( hintClause )? selectList )
            {
              CommonTree root_1 = (CommonTree) adaptor.nil();
              root_1 = (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_SELECT, "TOK_SELECT"), root_1);

              // SelectClauseParser.g:54:61: ( hintClause )?
              if (stream_hintClause.hasNext()) {
                adaptor.addChild(root_1, stream_hintClause.nextTree());
              }
              stream_hintClause.reset();

              adaptor.addChild(root_1, stream_selectList.nextTree());

              adaptor.addChild(root_0, root_1);
            }
          } else // 55:6: -> {$transform == null && $dist != null}? ^( TOK_SELECTDI ( hintClause )? selectList )
            if (transform == null && dist != null) {
              // SelectClauseParser.g:55:48: ^( TOK_SELECTDI ( hintClause )? selectList )
              {
                CommonTree root_1 = (CommonTree) adaptor.nil();
                root_1 =
                    (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_SELECTDI, "TOK_SELECTDI"), root_1);

                // SelectClauseParser.g:55:63: ( hintClause )?
                if (stream_hintClause.hasNext()) {
                  adaptor.addChild(root_1, stream_hintClause.nextTree());
                }
                stream_hintClause.reset();

                adaptor.addChild(root_1, stream_selectList.nextTree());

                adaptor.addChild(root_0, root_1);
              }
            } else // 56:6: -> ^( TOK_SELECT ( hintClause )? ^( TOK_SELEXPR selectTrfmClause ) )
            {
              // SelectClauseParser.g:56:9: ^( TOK_SELECT ( hintClause )? ^( TOK_SELEXPR selectTrfmClause ) )
              {
                CommonTree root_1 = (CommonTree) adaptor.nil();
                root_1 = (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_SELECT, "TOK_SELECT"), root_1);

                // SelectClauseParser.g:56:22: ( hintClause )?
                if (stream_hintClause.hasNext()) {
                  adaptor.addChild(root_1, stream_hintClause.nextTree());
                }
                stream_hintClause.reset();

                // SelectClauseParser.g:56:34: ^( TOK_SELEXPR selectTrfmClause )
                {
                  CommonTree root_2 = (CommonTree) adaptor.nil();
                  root_2 =
                      (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_SELEXPR, "TOK_SELEXPR"), root_2);

                  adaptor.addChild(root_2, stream_selectTrfmClause.nextTree());

                  adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
              }
            }

          retval.tree = root_0;
        }
        break;
        case 2:
          // SelectClauseParser.g:58:5: trfmClause
        {
          pushFollow(FOLLOW_trfmClause_in_selectClause196);
          trfmClause6 = trfmClause();

          state._fsp--;

          stream_trfmClause.add(trfmClause6.getTree());

          // AST REWRITE
          // elements: trfmClause
          // token labels:
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 58:17: -> ^( TOK_SELECT ^( TOK_SELEXPR trfmClause ) )
          {
            // SelectClauseParser.g:58:19: ^( TOK_SELECT ^( TOK_SELEXPR trfmClause ) )
            {
              CommonTree root_1 = (CommonTree) adaptor.nil();
              root_1 = (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_SELECT, "TOK_SELECT"), root_1);

              // SelectClauseParser.g:58:32: ^( TOK_SELEXPR trfmClause )
              {
                CommonTree root_2 = (CommonTree) adaptor.nil();
                root_2 =
                    (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_SELEXPR, "TOK_SELEXPR"), root_2);

                adaptor.addChild(root_2, stream_trfmClause.nextTree());

                adaptor.addChild(root_1, root_2);
              }

              adaptor.addChild(root_0, root_1);
            }
          }

          retval.tree = root_0;
        }
        break;
      }
      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      gParent.popMsg(state);
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "selectClause"

  public static class selectList_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "selectList"
  // SelectClauseParser.g:61:1: selectList : selectItem ( COMMA selectItem )* -> ( selectItem )+ ;
  public final HiveParser_SelectClauseParser.selectList_return selectList() throws RecognitionException {
    HiveParser_SelectClauseParser.selectList_return retval = new HiveParser_SelectClauseParser.selectList_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token COMMA8 = null;
    HiveParser_SelectClauseParser.selectItem_return selectItem7 = null;

    HiveParser_SelectClauseParser.selectItem_return selectItem9 = null;

    CommonTree COMMA8_tree = null;
    RewriteRuleTokenStream stream_COMMA = new RewriteRuleTokenStream(adaptor, "token COMMA");
    RewriteRuleSubtreeStream stream_selectItem = new RewriteRuleSubtreeStream(adaptor, "rule selectItem");
    gParent.pushMsg("select list", state);
    try {
      // SelectClauseParser.g:64:5: ( selectItem ( COMMA selectItem )* -> ( selectItem )+ )
      // SelectClauseParser.g:65:5: selectItem ( COMMA selectItem )*
      {
        pushFollow(FOLLOW_selectItem_in_selectList239);
        selectItem7 = selectItem();

        state._fsp--;

        stream_selectItem.add(selectItem7.getTree());

        // SelectClauseParser.g:65:16: ( COMMA selectItem )*
        loop5:
        do {
          int alt5 = 2;
          switch (input.LA(1)) {
            case COMMA: {
              alt5 = 1;
            }
            break;
          }

          switch (alt5) {
            case 1:
              // SelectClauseParser.g:65:18: COMMA selectItem
            {
              COMMA8 = (Token) match(input, COMMA, FOLLOW_COMMA_in_selectList243);
              stream_COMMA.add(COMMA8);

              pushFollow(FOLLOW_selectItem_in_selectList246);
              selectItem9 = selectItem();

              state._fsp--;

              stream_selectItem.add(selectItem9.getTree());
            }
            break;

            default:
              break loop5;
          }
        } while (true);

        // AST REWRITE
        // elements: selectItem
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        RewriteRuleSubtreeStream stream_retval =
            new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 65:39: -> ( selectItem )+
        {
          if (!(stream_selectItem.hasNext())) {
            throw new RewriteEarlyExitException();
          }
          while (stream_selectItem.hasNext()) {
            adaptor.addChild(root_0, stream_selectItem.nextTree());
          }
          stream_selectItem.reset();
        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      gParent.popMsg(state);
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "selectList"

  public static class selectTrfmClause_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "selectTrfmClause"
  // SelectClauseParser.g:68:1: selectTrfmClause : LPAREN selectExpressionList RPAREN inSerde= rowFormat inRec= recordWriter KW_USING StringLiteral ( KW_AS ( ( LPAREN ( aliasList | columnNameTypeList ) RPAREN ) | ( aliasList | columnNameTypeList ) ) )? outSerde= rowFormat outRec= recordReader -> ^( TOK_TRANSFORM selectExpressionList $inSerde $inRec StringLiteral $outSerde $outRec ( aliasList )? ( columnNameTypeList )? ) ;
  public final HiveParser_SelectClauseParser.selectTrfmClause_return selectTrfmClause() throws RecognitionException {
    HiveParser_SelectClauseParser.selectTrfmClause_return retval =
        new HiveParser_SelectClauseParser.selectTrfmClause_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token LPAREN10 = null;
    Token RPAREN12 = null;
    Token KW_USING13 = null;
    Token StringLiteral14 = null;
    Token KW_AS15 = null;
    Token LPAREN16 = null;
    Token RPAREN19 = null;
    HiveParser.rowFormat_return inSerde = null;

    HiveParser.recordWriter_return inRec = null;

    HiveParser.rowFormat_return outSerde = null;

    HiveParser.recordReader_return outRec = null;

    HiveParser_SelectClauseParser.selectExpressionList_return selectExpressionList11 = null;

    HiveParser_FromClauseParser.aliasList_return aliasList17 = null;

    HiveParser.columnNameTypeList_return columnNameTypeList18 = null;

    HiveParser_FromClauseParser.aliasList_return aliasList20 = null;

    HiveParser.columnNameTypeList_return columnNameTypeList21 = null;

    CommonTree LPAREN10_tree = null;
    CommonTree RPAREN12_tree = null;
    CommonTree KW_USING13_tree = null;
    CommonTree StringLiteral14_tree = null;
    CommonTree KW_AS15_tree = null;
    CommonTree LPAREN16_tree = null;
    CommonTree RPAREN19_tree = null;
    RewriteRuleTokenStream stream_StringLiteral = new RewriteRuleTokenStream(adaptor, "token StringLiteral");
    RewriteRuleTokenStream stream_KW_USING = new RewriteRuleTokenStream(adaptor, "token KW_USING");
    RewriteRuleTokenStream stream_LPAREN = new RewriteRuleTokenStream(adaptor, "token LPAREN");
    RewriteRuleTokenStream stream_RPAREN = new RewriteRuleTokenStream(adaptor, "token RPAREN");
    RewriteRuleTokenStream stream_KW_AS = new RewriteRuleTokenStream(adaptor, "token KW_AS");
    RewriteRuleSubtreeStream stream_aliasList = new RewriteRuleSubtreeStream(adaptor, "rule aliasList");
    RewriteRuleSubtreeStream stream_rowFormat = new RewriteRuleSubtreeStream(adaptor, "rule rowFormat");
    RewriteRuleSubtreeStream stream_columnNameTypeList =
        new RewriteRuleSubtreeStream(adaptor, "rule columnNameTypeList");
    RewriteRuleSubtreeStream stream_recordReader = new RewriteRuleSubtreeStream(adaptor, "rule recordReader");
    RewriteRuleSubtreeStream stream_selectExpressionList =
        new RewriteRuleSubtreeStream(adaptor, "rule selectExpressionList");
    RewriteRuleSubtreeStream stream_recordWriter = new RewriteRuleSubtreeStream(adaptor, "rule recordWriter");
    gParent.pushMsg("transform clause", state);
    try {
      // SelectClauseParser.g:71:5: ( LPAREN selectExpressionList RPAREN inSerde= rowFormat inRec= recordWriter KW_USING StringLiteral ( KW_AS ( ( LPAREN ( aliasList | columnNameTypeList ) RPAREN ) | ( aliasList | columnNameTypeList ) ) )? outSerde= rowFormat outRec= recordReader -> ^( TOK_TRANSFORM selectExpressionList $inSerde $inRec StringLiteral $outSerde $outRec ( aliasList )? ( columnNameTypeList )? ) )
      // SelectClauseParser.g:72:5: LPAREN selectExpressionList RPAREN inSerde= rowFormat inRec= recordWriter KW_USING StringLiteral ( KW_AS ( ( LPAREN ( aliasList | columnNameTypeList ) RPAREN ) | ( aliasList | columnNameTypeList ) ) )? outSerde= rowFormat outRec= recordReader
      {
        LPAREN10 = (Token) match(input, LPAREN, FOLLOW_LPAREN_in_selectTrfmClause285);
        stream_LPAREN.add(LPAREN10);

        pushFollow(FOLLOW_selectExpressionList_in_selectTrfmClause287);
        selectExpressionList11 = selectExpressionList();

        state._fsp--;

        stream_selectExpressionList.add(selectExpressionList11.getTree());

        RPAREN12 = (Token) match(input, RPAREN, FOLLOW_RPAREN_in_selectTrfmClause289);
        stream_RPAREN.add(RPAREN12);

        pushFollow(FOLLOW_rowFormat_in_selectTrfmClause297);
        inSerde = gHiveParser.rowFormat();

        state._fsp--;

        stream_rowFormat.add(inSerde.getTree());

        pushFollow(FOLLOW_recordWriter_in_selectTrfmClause301);
        inRec = gHiveParser.recordWriter();

        state._fsp--;

        stream_recordWriter.add(inRec.getTree());

        KW_USING13 = (Token) match(input, KW_USING, FOLLOW_KW_USING_in_selectTrfmClause307);
        stream_KW_USING.add(KW_USING13);

        StringLiteral14 = (Token) match(input, StringLiteral, FOLLOW_StringLiteral_in_selectTrfmClause309);
        stream_StringLiteral.add(StringLiteral14);

        // SelectClauseParser.g:75:5: ( KW_AS ( ( LPAREN ( aliasList | columnNameTypeList ) RPAREN ) | ( aliasList | columnNameTypeList ) ) )?
        int alt9 = 2;
        switch (input.LA(1)) {
          case KW_AS: {
            alt9 = 1;
          }
          break;
        }

        switch (alt9) {
          case 1:
            // SelectClauseParser.g:75:7: KW_AS ( ( LPAREN ( aliasList | columnNameTypeList ) RPAREN ) | ( aliasList | columnNameTypeList ) )
          {
            KW_AS15 = (Token) match(input, KW_AS, FOLLOW_KW_AS_in_selectTrfmClause317);
            stream_KW_AS.add(KW_AS15);

            // SelectClauseParser.g:75:13: ( ( LPAREN ( aliasList | columnNameTypeList ) RPAREN ) | ( aliasList | columnNameTypeList ) )
            int alt8 = 2;
            switch (input.LA(1)) {
              case LPAREN: {
                alt8 = 1;
              }
              break;
              case Identifier:
              case KW_ADD:
              case KW_ADMIN:
              case KW_AFTER:
              case KW_ALL:
              case KW_ALTER:
              case KW_ANALYZE:
              case KW_ARCHIVE:
              case KW_ARRAY:
              case KW_AS:
              case KW_ASC:
              case KW_AUTHORIZATION:
              case KW_BEFORE:
              case KW_BETWEEN:
              case KW_BIGINT:
              case KW_BINARY:
              case KW_BOOLEAN:
              case KW_BOTH:
              case KW_BUCKET:
              case KW_BUCKETS:
              case KW_BY:
              case KW_CASCADE:
              case KW_CHANGE:
              case KW_CLUSTER:
              case KW_CLUSTERED:
              case KW_CLUSTERSTATUS:
              case KW_COLLECTION:
              case KW_COLUMNS:
              case KW_COMMENT:
              case KW_COMPACT:
              case KW_COMPACTIONS:
              case KW_COMPUTE:
              case KW_CONCATENATE:
              case KW_CONTINUE:
              case KW_CREATE:
              case KW_CUBE:
              case KW_CURSOR:
              case KW_DATA:
              case KW_DATABASES:
              case KW_DATE:
              case KW_DATETIME:
              case KW_DBPROPERTIES:
              case KW_DECIMAL:
              case KW_DEFAULT:
              case KW_DEFERRED:
              case KW_DEFINED:
              case KW_DELETE:
              case KW_DELIMITED:
              case KW_DEPENDENCY:
              case KW_DESC:
              case KW_DESCRIBE:
              case KW_DIRECTORIES:
              case KW_DIRECTORY:
              case KW_DISABLE:
              case KW_DISTRIBUTE:
              case KW_DOUBLE:
              case KW_DROP:
              case KW_ELEM_TYPE:
              case KW_ENABLE:
              case KW_ESCAPED:
              case KW_EXCLUSIVE:
              case KW_EXISTS:
              case KW_EXPLAIN:
              case KW_EXPORT:
              case KW_EXTERNAL:
              case KW_FALSE:
              case KW_FETCH:
              case KW_FIELDS:
              case KW_FILE:
              case KW_FILEFORMAT:
              case KW_FIRST:
              case KW_FLOAT:
              case KW_FOR:
              case KW_FORMAT:
              case KW_FORMATTED:
              case KW_FULL:
              case KW_FUNCTIONS:
              case KW_GRANT:
              case KW_GROUP:
              case KW_GROUPING:
              case KW_HOLD_DDLTIME:
              case KW_IDXPROPERTIES:
              case KW_IGNORE:
              case KW_IMPORT:
              case KW_IN:
              case KW_INDEX:
              case KW_INDEXES:
              case KW_INNER:
              case KW_INPATH:
              case KW_INPUTDRIVER:
              case KW_INPUTFORMAT:
              case KW_INSERT:
              case KW_INT:
              case KW_INTERSECT:
              case KW_INTO:
              case KW_IS:
              case KW_ITEMS:
              case KW_JAR:
              case KW_KEYS:
              case KW_KEY_TYPE:
              case KW_LATERAL:
              case KW_LEFT:
              case KW_LIKE:
              case KW_LIMIT:
              case KW_LINES:
              case KW_LOAD:
              case KW_LOCAL:
              case KW_LOCATION:
              case KW_LOCK:
              case KW_LOCKS:
              case KW_LOGICAL:
              case KW_LONG:
              case KW_MAPJOIN:
              case KW_MATERIALIZED:
              case KW_METADATA:
              case KW_MINUS:
              case KW_MSCK:
              case KW_NONE:
              case KW_NOSCAN:
              case KW_NO_DROP:
              case KW_NULL:
              case KW_OF:
              case KW_OFFLINE:
              case KW_OPTION:
              case KW_ORDER:
              case KW_OUT:
              case KW_OUTER:
              case KW_OUTPUTDRIVER:
              case KW_OUTPUTFORMAT:
              case KW_OVERWRITE:
              case KW_OWNER:
              case KW_PARTITION:
              case KW_PARTITIONED:
              case KW_PARTITIONS:
              case KW_PERCENT:
              case KW_PLUS:
              case KW_PRETTY:
              case KW_PRINCIPALS:
              case KW_PROCEDURE:
              case KW_PROTECTION:
              case KW_PURGE:
              case KW_RANGE:
              case KW_READ:
              case KW_READONLY:
              case KW_READS:
              case KW_REBUILD:
              case KW_RECORDREADER:
              case KW_RECORDWRITER:
              case KW_REGEXP:
              case KW_RELOAD:
              case KW_RENAME:
              case KW_REPAIR:
              case KW_REPLACE:
              case KW_REPLICATION:
              case KW_RESTRICT:
              case KW_REVOKE:
              case KW_REWRITE:
              case KW_RIGHT:
              case KW_RLIKE:
              case KW_ROLE:
              case KW_ROLES:
              case KW_ROLLUP:
              case KW_ROW:
              case KW_ROWS:
              case KW_SCHEMA:
              case KW_SCHEMAS:
              case KW_SEMI:
              case KW_SERDE:
              case KW_SERDEPROPERTIES:
              case KW_SERVER:
              case KW_SET:
              case KW_SETS:
              case KW_SHARED:
              case KW_SHOW:
              case KW_SHOW_DATABASE:
              case KW_SKEWED:
              case KW_SMALLINT:
              case KW_SORT:
              case KW_SORTED:
              case KW_SSL:
              case KW_STATISTICS:
              case KW_STORED:
              case KW_STREAMTABLE:
              case KW_STRING:
              case KW_STRUCT:
              case KW_TABLE:
              case KW_TABLES:
              case KW_TBLPROPERTIES:
              case KW_TEMPORARY:
              case KW_TERMINATED:
              case KW_TIMESTAMP:
              case KW_TINYINT:
              case KW_TO:
              case KW_TOUCH:
              case KW_TRANSACTIONS:
              case KW_TRIGGER:
              case KW_TRUE:
              case KW_TRUNCATE:
              case KW_UNARCHIVE:
              case KW_UNDO:
              case KW_UNION:
              case KW_UNIONTYPE:
              case KW_UNLOCK:
              case KW_UNSET:
              case KW_UNSIGNED:
              case KW_UPDATE:
              case KW_URI:
              case KW_USE:
              case KW_USER:
              case KW_USING:
              case KW_UTC:
              case KW_UTCTIMESTAMP:
              case KW_VALUES:
              case KW_VALUE_TYPE:
              case KW_VIEW:
              case KW_WHILE:
              case KW_WITH: {
                alt8 = 2;
              }
              break;
              default:
                NoViableAltException nvae = new NoViableAltException("", 8, 0, input);

                throw nvae;
            }

            switch (alt8) {
              case 1:
                // SelectClauseParser.g:75:14: ( LPAREN ( aliasList | columnNameTypeList ) RPAREN )
              {
                // SelectClauseParser.g:75:14: ( LPAREN ( aliasList | columnNameTypeList ) RPAREN )
                // SelectClauseParser.g:75:15: LPAREN ( aliasList | columnNameTypeList ) RPAREN
                {
                  LPAREN16 = (Token) match(input, LPAREN, FOLLOW_LPAREN_in_selectTrfmClause321);
                  stream_LPAREN.add(LPAREN16);

                  // SelectClauseParser.g:75:22: ( aliasList | columnNameTypeList )
                  int alt6 = 2;
                  switch (input.LA(1)) {
                    case Identifier: {
                      switch (input.LA(2)) {
                        case COMMA:
                        case RPAREN: {
                          alt6 = 1;
                        }
                        break;
                        case KW_ARRAY:
                        case KW_BIGINT:
                        case KW_BINARY:
                        case KW_BOOLEAN:
                        case KW_CHAR:
                        case KW_DATE:
                        case KW_DATETIME:
                        case KW_DECIMAL:
                        case KW_DOUBLE:
                        case KW_FLOAT:
                        case KW_INT:
                        case KW_MAP:
                        case KW_SMALLINT:
                        case KW_STRING:
                        case KW_STRUCT:
                        case KW_TIMESTAMP:
                        case KW_TINYINT:
                        case KW_UNIONTYPE:
                        case KW_VARCHAR: {
                          alt6 = 2;
                        }
                        break;
                        default:
                          NoViableAltException nvae = new NoViableAltException("", 6, 1, input);

                          throw nvae;
                      }
                    }
                    break;
                    case KW_ADD:
                    case KW_ADMIN:
                    case KW_AFTER:
                    case KW_ALL:
                    case KW_ALTER:
                    case KW_ANALYZE:
                    case KW_ARCHIVE:
                    case KW_ARRAY:
                    case KW_AS:
                    case KW_ASC:
                    case KW_AUTHORIZATION:
                    case KW_BEFORE:
                    case KW_BETWEEN:
                    case KW_BIGINT:
                    case KW_BINARY:
                    case KW_BOOLEAN:
                    case KW_BOTH:
                    case KW_BUCKET:
                    case KW_BUCKETS:
                    case KW_BY:
                    case KW_CASCADE:
                    case KW_CHANGE:
                    case KW_CLUSTER:
                    case KW_CLUSTERED:
                    case KW_CLUSTERSTATUS:
                    case KW_COLLECTION:
                    case KW_COLUMNS:
                    case KW_COMMENT:
                    case KW_COMPACT:
                    case KW_COMPACTIONS:
                    case KW_COMPUTE:
                    case KW_CONCATENATE:
                    case KW_CONTINUE:
                    case KW_CREATE:
                    case KW_CUBE:
                    case KW_CURSOR:
                    case KW_DATA:
                    case KW_DATABASES:
                    case KW_DATE:
                    case KW_DATETIME:
                    case KW_DBPROPERTIES:
                    case KW_DECIMAL:
                    case KW_DEFAULT:
                    case KW_DEFERRED:
                    case KW_DEFINED:
                    case KW_DELETE:
                    case KW_DELIMITED:
                    case KW_DEPENDENCY:
                    case KW_DESC:
                    case KW_DESCRIBE:
                    case KW_DIRECTORIES:
                    case KW_DIRECTORY:
                    case KW_DISABLE:
                    case KW_DISTRIBUTE:
                    case KW_DOUBLE:
                    case KW_DROP:
                    case KW_ELEM_TYPE:
                    case KW_ENABLE:
                    case KW_ESCAPED:
                    case KW_EXCLUSIVE:
                    case KW_EXISTS:
                    case KW_EXPLAIN:
                    case KW_EXPORT:
                    case KW_EXTERNAL:
                    case KW_FALSE:
                    case KW_FETCH:
                    case KW_FIELDS:
                    case KW_FILE:
                    case KW_FILEFORMAT:
                    case KW_FIRST:
                    case KW_FLOAT:
                    case KW_FOR:
                    case KW_FORMAT:
                    case KW_FORMATTED:
                    case KW_FULL:
                    case KW_FUNCTIONS:
                    case KW_GRANT:
                    case KW_GROUP:
                    case KW_GROUPING:
                    case KW_HOLD_DDLTIME:
                    case KW_IDXPROPERTIES:
                    case KW_IGNORE:
                    case KW_IMPORT:
                    case KW_IN:
                    case KW_INDEX:
                    case KW_INDEXES:
                    case KW_INNER:
                    case KW_INPATH:
                    case KW_INPUTDRIVER:
                    case KW_INPUTFORMAT:
                    case KW_INSERT:
                    case KW_INT:
                    case KW_INTERSECT:
                    case KW_INTO:
                    case KW_IS:
                    case KW_ITEMS:
                    case KW_JAR:
                    case KW_KEYS:
                    case KW_KEY_TYPE:
                    case KW_LATERAL:
                    case KW_LEFT:
                    case KW_LIKE:
                    case KW_LIMIT:
                    case KW_LINES:
                    case KW_LOAD:
                    case KW_LOCAL:
                    case KW_LOCATION:
                    case KW_LOCK:
                    case KW_LOCKS:
                    case KW_LOGICAL:
                    case KW_LONG:
                    case KW_MAPJOIN:
                    case KW_MATERIALIZED:
                    case KW_METADATA:
                    case KW_MINUS:
                    case KW_MSCK:
                    case KW_NONE:
                    case KW_NOSCAN:
                    case KW_NO_DROP:
                    case KW_NULL:
                    case KW_OF:
                    case KW_OFFLINE:
                    case KW_OPTION:
                    case KW_ORDER:
                    case KW_OUT:
                    case KW_OUTER:
                    case KW_OUTPUTDRIVER:
                    case KW_OUTPUTFORMAT:
                    case KW_OVERWRITE:
                    case KW_OWNER:
                    case KW_PARTITION:
                    case KW_PARTITIONED:
                    case KW_PARTITIONS:
                    case KW_PERCENT:
                    case KW_PLUS:
                    case KW_PRETTY:
                    case KW_PRINCIPALS:
                    case KW_PROCEDURE:
                    case KW_PROTECTION:
                    case KW_PURGE:
                    case KW_RANGE:
                    case KW_READ:
                    case KW_READONLY:
                    case KW_READS:
                    case KW_REBUILD:
                    case KW_RECORDREADER:
                    case KW_RECORDWRITER:
                    case KW_REGEXP:
                    case KW_RELOAD:
                    case KW_RENAME:
                    case KW_REPAIR:
                    case KW_REPLACE:
                    case KW_REPLICATION:
                    case KW_RESTRICT:
                    case KW_REVOKE:
                    case KW_REWRITE:
                    case KW_RIGHT:
                    case KW_RLIKE:
                    case KW_ROLE:
                    case KW_ROLES:
                    case KW_ROLLUP:
                    case KW_ROW:
                    case KW_ROWS:
                    case KW_SCHEMA:
                    case KW_SCHEMAS:
                    case KW_SEMI:
                    case KW_SERDE:
                    case KW_SERDEPROPERTIES:
                    case KW_SERVER:
                    case KW_SET:
                    case KW_SETS:
                    case KW_SHARED:
                    case KW_SHOW:
                    case KW_SHOW_DATABASE:
                    case KW_SKEWED:
                    case KW_SMALLINT:
                    case KW_SORT:
                    case KW_SORTED:
                    case KW_SSL:
                    case KW_STATISTICS:
                    case KW_STORED:
                    case KW_STREAMTABLE:
                    case KW_STRING:
                    case KW_STRUCT:
                    case KW_TABLE:
                    case KW_TABLES:
                    case KW_TBLPROPERTIES:
                    case KW_TEMPORARY:
                    case KW_TERMINATED:
                    case KW_TIMESTAMP:
                    case KW_TINYINT:
                    case KW_TO:
                    case KW_TOUCH:
                    case KW_TRANSACTIONS:
                    case KW_TRIGGER:
                    case KW_TRUE:
                    case KW_TRUNCATE:
                    case KW_UNARCHIVE:
                    case KW_UNDO:
                    case KW_UNION:
                    case KW_UNIONTYPE:
                    case KW_UNLOCK:
                    case KW_UNSET:
                    case KW_UNSIGNED:
                    case KW_UPDATE:
                    case KW_URI:
                    case KW_USE:
                    case KW_USER:
                    case KW_USING:
                    case KW_UTC:
                    case KW_UTCTIMESTAMP:
                    case KW_VALUES:
                    case KW_VALUE_TYPE:
                    case KW_VIEW:
                    case KW_WHILE:
                    case KW_WITH: {
                      switch (input.LA(2)) {
                        case COMMA:
                        case RPAREN: {
                          alt6 = 1;
                        }
                        break;
                        case KW_ARRAY:
                        case KW_BIGINT:
                        case KW_BINARY:
                        case KW_BOOLEAN:
                        case KW_CHAR:
                        case KW_DATE:
                        case KW_DATETIME:
                        case KW_DECIMAL:
                        case KW_DOUBLE:
                        case KW_FLOAT:
                        case KW_INT:
                        case KW_MAP:
                        case KW_SMALLINT:
                        case KW_STRING:
                        case KW_STRUCT:
                        case KW_TIMESTAMP:
                        case KW_TINYINT:
                        case KW_UNIONTYPE:
                        case KW_VARCHAR: {
                          alt6 = 2;
                        }
                        break;
                        default:
                          NoViableAltException nvae = new NoViableAltException("", 6, 2, input);

                          throw nvae;
                      }
                    }
                    break;
                    default:
                      NoViableAltException nvae = new NoViableAltException("", 6, 0, input);

                      throw nvae;
                  }

                  switch (alt6) {
                    case 1:
                      // SelectClauseParser.g:75:23: aliasList
                    {
                      pushFollow(FOLLOW_aliasList_in_selectTrfmClause324);
                      aliasList17 = gHiveParser.aliasList();

                      state._fsp--;

                      stream_aliasList.add(aliasList17.getTree());
                    }
                    break;
                    case 2:
                      // SelectClauseParser.g:75:35: columnNameTypeList
                    {
                      pushFollow(FOLLOW_columnNameTypeList_in_selectTrfmClause328);
                      columnNameTypeList18 = gHiveParser.columnNameTypeList();

                      state._fsp--;

                      stream_columnNameTypeList.add(columnNameTypeList18.getTree());
                    }
                    break;
                  }

                  RPAREN19 = (Token) match(input, RPAREN, FOLLOW_RPAREN_in_selectTrfmClause331);
                  stream_RPAREN.add(RPAREN19);
                }
              }
              break;
              case 2:
                // SelectClauseParser.g:75:65: ( aliasList | columnNameTypeList )
              {
                // SelectClauseParser.g:75:65: ( aliasList | columnNameTypeList )
                int alt7 = 2;
                alt7 = dfa7.predict(input);
                switch (alt7) {
                  case 1:
                    // SelectClauseParser.g:75:66: aliasList
                  {
                    pushFollow(FOLLOW_aliasList_in_selectTrfmClause337);
                    aliasList20 = gHiveParser.aliasList();

                    state._fsp--;

                    stream_aliasList.add(aliasList20.getTree());
                  }
                  break;
                  case 2:
                    // SelectClauseParser.g:75:78: columnNameTypeList
                  {
                    pushFollow(FOLLOW_columnNameTypeList_in_selectTrfmClause341);
                    columnNameTypeList21 = gHiveParser.columnNameTypeList();

                    state._fsp--;

                    stream_columnNameTypeList.add(columnNameTypeList21.getTree());
                  }
                  break;
                }
              }
              break;
            }
          }
          break;
        }

        pushFollow(FOLLOW_rowFormat_in_selectTrfmClause353);
        outSerde = gHiveParser.rowFormat();

        state._fsp--;

        stream_rowFormat.add(outSerde.getTree());

        pushFollow(FOLLOW_recordReader_in_selectTrfmClause357);
        outRec = gHiveParser.recordReader();

        state._fsp--;

        stream_recordReader.add(outRec.getTree());

        // AST REWRITE
        // elements: inRec, aliasList, selectExpressionList, inSerde, columnNameTypeList, outSerde, outRec, StringLiteral
        // token labels:
        // rule labels: inRec, outRec, inSerde, outSerde, retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        RewriteRuleSubtreeStream stream_inRec =
            new RewriteRuleSubtreeStream(adaptor, "rule inRec", inRec != null ? inRec.tree : null);
        RewriteRuleSubtreeStream stream_outRec =
            new RewriteRuleSubtreeStream(adaptor, "rule outRec", outRec != null ? outRec.tree : null);
        RewriteRuleSubtreeStream stream_inSerde =
            new RewriteRuleSubtreeStream(adaptor, "rule inSerde", inSerde != null ? inSerde.tree : null);
        RewriteRuleSubtreeStream stream_outSerde =
            new RewriteRuleSubtreeStream(adaptor, "rule outSerde", outSerde != null ? outSerde.tree : null);
        RewriteRuleSubtreeStream stream_retval =
            new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 77:5: -> ^( TOK_TRANSFORM selectExpressionList $inSerde $inRec StringLiteral $outSerde $outRec ( aliasList )? ( columnNameTypeList )? )
        {
          // SelectClauseParser.g:77:8: ^( TOK_TRANSFORM selectExpressionList $inSerde $inRec StringLiteral $outSerde $outRec ( aliasList )? ( columnNameTypeList )? )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 =
                (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_TRANSFORM, "TOK_TRANSFORM"), root_1);

            adaptor.addChild(root_1, stream_selectExpressionList.nextTree());

            adaptor.addChild(root_1, stream_inSerde.nextTree());

            adaptor.addChild(root_1, stream_inRec.nextTree());

            adaptor.addChild(root_1, stream_StringLiteral.nextNode());

            adaptor.addChild(root_1, stream_outSerde.nextTree());

            adaptor.addChild(root_1, stream_outRec.nextTree());

            // SelectClauseParser.g:77:93: ( aliasList )?
            if (stream_aliasList.hasNext()) {
              adaptor.addChild(root_1, stream_aliasList.nextTree());
            }
            stream_aliasList.reset();

            // SelectClauseParser.g:77:104: ( columnNameTypeList )?
            if (stream_columnNameTypeList.hasNext()) {
              adaptor.addChild(root_1, stream_columnNameTypeList.nextTree());
            }
            stream_columnNameTypeList.reset();

            adaptor.addChild(root_0, root_1);
          }
        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      gParent.popMsg(state);
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "selectTrfmClause"

  public static class hintClause_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "hintClause"
  // SelectClauseParser.g:80:1: hintClause : DIVIDE STAR PLUS hintList STAR DIVIDE -> ^( TOK_HINTLIST hintList ) ;
  public final HiveParser_SelectClauseParser.hintClause_return hintClause() throws RecognitionException {
    HiveParser_SelectClauseParser.hintClause_return retval = new HiveParser_SelectClauseParser.hintClause_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token DIVIDE22 = null;
    Token STAR23 = null;
    Token PLUS24 = null;
    Token STAR26 = null;
    Token DIVIDE27 = null;
    HiveParser_SelectClauseParser.hintList_return hintList25 = null;

    CommonTree DIVIDE22_tree = null;
    CommonTree STAR23_tree = null;
    CommonTree PLUS24_tree = null;
    CommonTree STAR26_tree = null;
    CommonTree DIVIDE27_tree = null;
    RewriteRuleTokenStream stream_STAR = new RewriteRuleTokenStream(adaptor, "token STAR");
    RewriteRuleTokenStream stream_DIVIDE = new RewriteRuleTokenStream(adaptor, "token DIVIDE");
    RewriteRuleTokenStream stream_PLUS = new RewriteRuleTokenStream(adaptor, "token PLUS");
    RewriteRuleSubtreeStream stream_hintList = new RewriteRuleSubtreeStream(adaptor, "rule hintList");
    gParent.pushMsg("hint clause", state);
    try {
      // SelectClauseParser.g:83:5: ( DIVIDE STAR PLUS hintList STAR DIVIDE -> ^( TOK_HINTLIST hintList ) )
      // SelectClauseParser.g:84:5: DIVIDE STAR PLUS hintList STAR DIVIDE
      {
        DIVIDE22 = (Token) match(input, DIVIDE, FOLLOW_DIVIDE_in_hintClause420);
        stream_DIVIDE.add(DIVIDE22);

        STAR23 = (Token) match(input, STAR, FOLLOW_STAR_in_hintClause422);
        stream_STAR.add(STAR23);

        PLUS24 = (Token) match(input, PLUS, FOLLOW_PLUS_in_hintClause424);
        stream_PLUS.add(PLUS24);

        pushFollow(FOLLOW_hintList_in_hintClause426);
        hintList25 = hintList();

        state._fsp--;

        stream_hintList.add(hintList25.getTree());

        STAR26 = (Token) match(input, STAR, FOLLOW_STAR_in_hintClause428);
        stream_STAR.add(STAR26);

        DIVIDE27 = (Token) match(input, DIVIDE, FOLLOW_DIVIDE_in_hintClause430);
        stream_DIVIDE.add(DIVIDE27);

        // AST REWRITE
        // elements: hintList
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        RewriteRuleSubtreeStream stream_retval =
            new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 84:43: -> ^( TOK_HINTLIST hintList )
        {
          // SelectClauseParser.g:84:46: ^( TOK_HINTLIST hintList )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_HINTLIST, "TOK_HINTLIST"), root_1);

            adaptor.addChild(root_1, stream_hintList.nextTree());

            adaptor.addChild(root_0, root_1);
          }
        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      gParent.popMsg(state);
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "hintClause"

  public static class hintList_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "hintList"
  // SelectClauseParser.g:87:1: hintList : hintItem ( COMMA hintItem )* -> ( hintItem )+ ;
  public final HiveParser_SelectClauseParser.hintList_return hintList() throws RecognitionException {
    HiveParser_SelectClauseParser.hintList_return retval = new HiveParser_SelectClauseParser.hintList_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token COMMA29 = null;
    HiveParser_SelectClauseParser.hintItem_return hintItem28 = null;

    HiveParser_SelectClauseParser.hintItem_return hintItem30 = null;

    CommonTree COMMA29_tree = null;
    RewriteRuleTokenStream stream_COMMA = new RewriteRuleTokenStream(adaptor, "token COMMA");
    RewriteRuleSubtreeStream stream_hintItem = new RewriteRuleSubtreeStream(adaptor, "rule hintItem");
    gParent.pushMsg("hint list", state);
    try {
      // SelectClauseParser.g:90:5: ( hintItem ( COMMA hintItem )* -> ( hintItem )+ )
      // SelectClauseParser.g:91:5: hintItem ( COMMA hintItem )*
      {
        pushFollow(FOLLOW_hintItem_in_hintList469);
        hintItem28 = hintItem();

        state._fsp--;

        stream_hintItem.add(hintItem28.getTree());

        // SelectClauseParser.g:91:14: ( COMMA hintItem )*
        loop10:
        do {
          int alt10 = 2;
          switch (input.LA(1)) {
            case COMMA: {
              alt10 = 1;
            }
            break;
          }

          switch (alt10) {
            case 1:
              // SelectClauseParser.g:91:15: COMMA hintItem
            {
              COMMA29 = (Token) match(input, COMMA, FOLLOW_COMMA_in_hintList472);
              stream_COMMA.add(COMMA29);

              pushFollow(FOLLOW_hintItem_in_hintList474);
              hintItem30 = hintItem();

              state._fsp--;

              stream_hintItem.add(hintItem30.getTree());
            }
            break;

            default:
              break loop10;
          }
        } while (true);

        // AST REWRITE
        // elements: hintItem
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        RewriteRuleSubtreeStream stream_retval =
            new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 91:32: -> ( hintItem )+
        {
          if (!(stream_hintItem.hasNext())) {
            throw new RewriteEarlyExitException();
          }
          while (stream_hintItem.hasNext()) {
            adaptor.addChild(root_0, stream_hintItem.nextTree());
          }
          stream_hintItem.reset();
        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      gParent.popMsg(state);
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "hintList"

  public static class hintItem_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "hintItem"
  // SelectClauseParser.g:94:1: hintItem : hintName ( LPAREN hintArgs RPAREN )? -> ^( TOK_HINT hintName ( hintArgs )? ) ;
  public final HiveParser_SelectClauseParser.hintItem_return hintItem() throws RecognitionException {
    HiveParser_SelectClauseParser.hintItem_return retval = new HiveParser_SelectClauseParser.hintItem_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token LPAREN32 = null;
    Token RPAREN34 = null;
    HiveParser_SelectClauseParser.hintName_return hintName31 = null;

    HiveParser_SelectClauseParser.hintArgs_return hintArgs33 = null;

    CommonTree LPAREN32_tree = null;
    CommonTree RPAREN34_tree = null;
    RewriteRuleTokenStream stream_LPAREN = new RewriteRuleTokenStream(adaptor, "token LPAREN");
    RewriteRuleTokenStream stream_RPAREN = new RewriteRuleTokenStream(adaptor, "token RPAREN");
    RewriteRuleSubtreeStream stream_hintName = new RewriteRuleSubtreeStream(adaptor, "rule hintName");
    RewriteRuleSubtreeStream stream_hintArgs = new RewriteRuleSubtreeStream(adaptor, "rule hintArgs");
    gParent.pushMsg("hint item", state);
    try {
      // SelectClauseParser.g:97:5: ( hintName ( LPAREN hintArgs RPAREN )? -> ^( TOK_HINT hintName ( hintArgs )? ) )
      // SelectClauseParser.g:98:5: hintName ( LPAREN hintArgs RPAREN )?
      {
        pushFollow(FOLLOW_hintName_in_hintItem512);
        hintName31 = hintName();

        state._fsp--;

        stream_hintName.add(hintName31.getTree());

        // SelectClauseParser.g:98:14: ( LPAREN hintArgs RPAREN )?
        int alt11 = 2;
        switch (input.LA(1)) {
          case LPAREN: {
            alt11 = 1;
          }
          break;
        }

        switch (alt11) {
          case 1:
            // SelectClauseParser.g:98:15: LPAREN hintArgs RPAREN
          {
            LPAREN32 = (Token) match(input, LPAREN, FOLLOW_LPAREN_in_hintItem515);
            stream_LPAREN.add(LPAREN32);

            pushFollow(FOLLOW_hintArgs_in_hintItem517);
            hintArgs33 = hintArgs();

            state._fsp--;

            stream_hintArgs.add(hintArgs33.getTree());

            RPAREN34 = (Token) match(input, RPAREN, FOLLOW_RPAREN_in_hintItem519);
            stream_RPAREN.add(RPAREN34);
          }
          break;
        }

        // AST REWRITE
        // elements: hintName, hintArgs
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        RewriteRuleSubtreeStream stream_retval =
            new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 98:40: -> ^( TOK_HINT hintName ( hintArgs )? )
        {
          // SelectClauseParser.g:98:43: ^( TOK_HINT hintName ( hintArgs )? )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_HINT, "TOK_HINT"), root_1);

            adaptor.addChild(root_1, stream_hintName.nextTree());

            // SelectClauseParser.g:98:63: ( hintArgs )?
            if (stream_hintArgs.hasNext()) {
              adaptor.addChild(root_1, stream_hintArgs.nextTree());
            }
            stream_hintArgs.reset();

            adaptor.addChild(root_0, root_1);
          }
        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      gParent.popMsg(state);
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "hintItem"

  public static class hintName_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "hintName"
  // SelectClauseParser.g:101:1: hintName : ( KW_MAPJOIN -> TOK_MAPJOIN | KW_STREAMTABLE -> TOK_STREAMTABLE | KW_HOLD_DDLTIME -> TOK_HOLD_DDLTIME );
  public final HiveParser_SelectClauseParser.hintName_return hintName() throws RecognitionException {
    HiveParser_SelectClauseParser.hintName_return retval = new HiveParser_SelectClauseParser.hintName_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token KW_MAPJOIN35 = null;
    Token KW_STREAMTABLE36 = null;
    Token KW_HOLD_DDLTIME37 = null;

    CommonTree KW_MAPJOIN35_tree = null;
    CommonTree KW_STREAMTABLE36_tree = null;
    CommonTree KW_HOLD_DDLTIME37_tree = null;
    RewriteRuleTokenStream stream_KW_MAPJOIN = new RewriteRuleTokenStream(adaptor, "token KW_MAPJOIN");
    RewriteRuleTokenStream stream_KW_STREAMTABLE = new RewriteRuleTokenStream(adaptor, "token KW_STREAMTABLE");
    RewriteRuleTokenStream stream_KW_HOLD_DDLTIME = new RewriteRuleTokenStream(adaptor, "token KW_HOLD_DDLTIME");

    gParent.pushMsg("hint name", state);
    try {
      // SelectClauseParser.g:104:5: ( KW_MAPJOIN -> TOK_MAPJOIN | KW_STREAMTABLE -> TOK_STREAMTABLE | KW_HOLD_DDLTIME -> TOK_HOLD_DDLTIME )
      int alt12 = 3;
      switch (input.LA(1)) {
        case KW_MAPJOIN: {
          alt12 = 1;
        }
        break;
        case KW_STREAMTABLE: {
          alt12 = 2;
        }
        break;
        case KW_HOLD_DDLTIME: {
          alt12 = 3;
        }
        break;
        default:
          NoViableAltException nvae = new NoViableAltException("", 12, 0, input);

          throw nvae;
      }

      switch (alt12) {
        case 1:
          // SelectClauseParser.g:105:5: KW_MAPJOIN
        {
          KW_MAPJOIN35 = (Token) match(input, KW_MAPJOIN, FOLLOW_KW_MAPJOIN_in_hintName563);
          stream_KW_MAPJOIN.add(KW_MAPJOIN35);

          // AST REWRITE
          // elements:
          // token labels:
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 105:16: -> TOK_MAPJOIN
          {
            adaptor.addChild(root_0, (CommonTree) adaptor.create(TOK_MAPJOIN, "TOK_MAPJOIN"));
          }

          retval.tree = root_0;
        }
        break;
        case 2:
          // SelectClauseParser.g:106:7: KW_STREAMTABLE
        {
          KW_STREAMTABLE36 = (Token) match(input, KW_STREAMTABLE, FOLLOW_KW_STREAMTABLE_in_hintName575);
          stream_KW_STREAMTABLE.add(KW_STREAMTABLE36);

          // AST REWRITE
          // elements:
          // token labels:
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 106:22: -> TOK_STREAMTABLE
          {
            adaptor.addChild(root_0, (CommonTree) adaptor.create(TOK_STREAMTABLE, "TOK_STREAMTABLE"));
          }

          retval.tree = root_0;
        }
        break;
        case 3:
          // SelectClauseParser.g:107:7: KW_HOLD_DDLTIME
        {
          KW_HOLD_DDLTIME37 = (Token) match(input, KW_HOLD_DDLTIME, FOLLOW_KW_HOLD_DDLTIME_in_hintName587);
          stream_KW_HOLD_DDLTIME.add(KW_HOLD_DDLTIME37);

          // AST REWRITE
          // elements:
          // token labels:
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 107:23: -> TOK_HOLD_DDLTIME
          {
            adaptor.addChild(root_0, (CommonTree) adaptor.create(TOK_HOLD_DDLTIME, "TOK_HOLD_DDLTIME"));
          }

          retval.tree = root_0;
        }
        break;
      }
      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      gParent.popMsg(state);
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "hintName"

  public static class hintArgs_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "hintArgs"
  // SelectClauseParser.g:110:1: hintArgs : hintArgName ( COMMA hintArgName )* -> ^( TOK_HINTARGLIST ( hintArgName )+ ) ;
  public final HiveParser_SelectClauseParser.hintArgs_return hintArgs() throws RecognitionException {
    HiveParser_SelectClauseParser.hintArgs_return retval = new HiveParser_SelectClauseParser.hintArgs_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token COMMA39 = null;
    HiveParser_SelectClauseParser.hintArgName_return hintArgName38 = null;

    HiveParser_SelectClauseParser.hintArgName_return hintArgName40 = null;

    CommonTree COMMA39_tree = null;
    RewriteRuleTokenStream stream_COMMA = new RewriteRuleTokenStream(adaptor, "token COMMA");
    RewriteRuleSubtreeStream stream_hintArgName = new RewriteRuleSubtreeStream(adaptor, "rule hintArgName");
    gParent.pushMsg("hint arguments", state);
    try {
      // SelectClauseParser.g:113:5: ( hintArgName ( COMMA hintArgName )* -> ^( TOK_HINTARGLIST ( hintArgName )+ ) )
      // SelectClauseParser.g:114:5: hintArgName ( COMMA hintArgName )*
      {
        pushFollow(FOLLOW_hintArgName_in_hintArgs622);
        hintArgName38 = hintArgName();

        state._fsp--;

        stream_hintArgName.add(hintArgName38.getTree());

        // SelectClauseParser.g:114:17: ( COMMA hintArgName )*
        loop13:
        do {
          int alt13 = 2;
          switch (input.LA(1)) {
            case COMMA: {
              alt13 = 1;
            }
            break;
          }

          switch (alt13) {
            case 1:
              // SelectClauseParser.g:114:18: COMMA hintArgName
            {
              COMMA39 = (Token) match(input, COMMA, FOLLOW_COMMA_in_hintArgs625);
              stream_COMMA.add(COMMA39);

              pushFollow(FOLLOW_hintArgName_in_hintArgs627);
              hintArgName40 = hintArgName();

              state._fsp--;

              stream_hintArgName.add(hintArgName40.getTree());
            }
            break;

            default:
              break loop13;
          }
        } while (true);

        // AST REWRITE
        // elements: hintArgName
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        RewriteRuleSubtreeStream stream_retval =
            new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 114:38: -> ^( TOK_HINTARGLIST ( hintArgName )+ )
        {
          // SelectClauseParser.g:114:41: ^( TOK_HINTARGLIST ( hintArgName )+ )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_HINTARGLIST, "TOK_HINTARGLIST"),
                root_1);

            if (!(stream_hintArgName.hasNext())) {
              throw new RewriteEarlyExitException();
            }
            while (stream_hintArgName.hasNext()) {
              adaptor.addChild(root_1, stream_hintArgName.nextTree());
            }
            stream_hintArgName.reset();

            adaptor.addChild(root_0, root_1);
          }
        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      gParent.popMsg(state);
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "hintArgs"

  public static class hintArgName_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "hintArgName"
  // SelectClauseParser.g:117:1: hintArgName : identifier ;
  public final HiveParser_SelectClauseParser.hintArgName_return hintArgName() throws RecognitionException {
    HiveParser_SelectClauseParser.hintArgName_return retval = new HiveParser_SelectClauseParser.hintArgName_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    HiveParser_IdentifiersParser.identifier_return identifier41 = null;

    gParent.pushMsg("hint argument name", state);
    try {
      // SelectClauseParser.g:120:5: ( identifier )
      // SelectClauseParser.g:121:5: identifier
      {
        root_0 = (CommonTree) adaptor.nil();

        pushFollow(FOLLOW_identifier_in_hintArgName669);
        identifier41 = gHiveParser.identifier();

        state._fsp--;

        adaptor.addChild(root_0, identifier41.getTree());
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      gParent.popMsg(state);
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "hintArgName"

  public static class selectItem_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "selectItem"
  // SelectClauseParser.g:124:1: selectItem : ( ( expression ( ( ( KW_AS )? identifier ) | ( KW_AS LPAREN identifier ( COMMA identifier )* RPAREN ) )? ) -> ^( TOK_SELEXPR expression ( identifier )* ) | tableAllColumns -> ^( TOK_SELEXPR tableAllColumns ) );
  public final HiveParser_SelectClauseParser.selectItem_return selectItem() throws RecognitionException {
    HiveParser_SelectClauseParser.selectItem_return retval = new HiveParser_SelectClauseParser.selectItem_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token KW_AS43 = null;
    Token KW_AS45 = null;
    Token LPAREN46 = null;
    Token COMMA48 = null;
    Token RPAREN50 = null;
    HiveParser_IdentifiersParser.expression_return expression42 = null;

    HiveParser_IdentifiersParser.identifier_return identifier44 = null;

    HiveParser_IdentifiersParser.identifier_return identifier47 = null;

    HiveParser_IdentifiersParser.identifier_return identifier49 = null;

    HiveParser_FromClauseParser.tableAllColumns_return tableAllColumns51 = null;

    CommonTree KW_AS43_tree = null;
    CommonTree KW_AS45_tree = null;
    CommonTree LPAREN46_tree = null;
    CommonTree COMMA48_tree = null;
    CommonTree RPAREN50_tree = null;
    RewriteRuleTokenStream stream_COMMA = new RewriteRuleTokenStream(adaptor, "token COMMA");
    RewriteRuleTokenStream stream_LPAREN = new RewriteRuleTokenStream(adaptor, "token LPAREN");
    RewriteRuleTokenStream stream_RPAREN = new RewriteRuleTokenStream(adaptor, "token RPAREN");
    RewriteRuleTokenStream stream_KW_AS = new RewriteRuleTokenStream(adaptor, "token KW_AS");
    RewriteRuleSubtreeStream stream_identifier = new RewriteRuleSubtreeStream(adaptor, "rule identifier");
    RewriteRuleSubtreeStream stream_expression = new RewriteRuleSubtreeStream(adaptor, "rule expression");
    RewriteRuleSubtreeStream stream_tableAllColumns = new RewriteRuleSubtreeStream(adaptor, "rule tableAllColumns");
    gParent.pushMsg("selection target", state);
    try {
      // SelectClauseParser.g:127:5: ( ( expression ( ( ( KW_AS )? identifier ) | ( KW_AS LPAREN identifier ( COMMA identifier )* RPAREN ) )? ) -> ^( TOK_SELEXPR expression ( identifier )* ) | tableAllColumns -> ^( TOK_SELEXPR tableAllColumns ) )
      int alt17 = 2;
      alt17 = dfa17.predict(input);
      switch (alt17) {
        case 1:
          // SelectClauseParser.g:128:5: ( expression ( ( ( KW_AS )? identifier ) | ( KW_AS LPAREN identifier ( COMMA identifier )* RPAREN ) )? )
        {
          // SelectClauseParser.g:128:5: ( expression ( ( ( KW_AS )? identifier ) | ( KW_AS LPAREN identifier ( COMMA identifier )* RPAREN ) )? )
          // SelectClauseParser.g:128:7: expression ( ( ( KW_AS )? identifier ) | ( KW_AS LPAREN identifier ( COMMA identifier )* RPAREN ) )?
          {
            pushFollow(FOLLOW_expression_in_selectItem702);
            expression42 = gHiveParser.expression();

            state._fsp--;

            stream_expression.add(expression42.getTree());

            // SelectClauseParser.g:129:7: ( ( ( KW_AS )? identifier ) | ( KW_AS LPAREN identifier ( COMMA identifier )* RPAREN ) )?
            int alt16 = 3;
            alt16 = dfa16.predict(input);
            switch (alt16) {
              case 1:
                // SelectClauseParser.g:129:8: ( ( KW_AS )? identifier )
              {
                // SelectClauseParser.g:129:8: ( ( KW_AS )? identifier )
                // SelectClauseParser.g:129:9: ( KW_AS )? identifier
                {
                  // SelectClauseParser.g:129:9: ( KW_AS )?
                  int alt14 = 2;
                  alt14 = dfa14.predict(input);
                  switch (alt14) {
                    case 1:
                      // SelectClauseParser.g:129:9: KW_AS
                    {
                      KW_AS43 = (Token) match(input, KW_AS, FOLLOW_KW_AS_in_selectItem712);
                      stream_KW_AS.add(KW_AS43);
                    }
                    break;
                  }

                  pushFollow(FOLLOW_identifier_in_selectItem715);
                  identifier44 = gHiveParser.identifier();

                  state._fsp--;

                  stream_identifier.add(identifier44.getTree());
                }
              }
              break;
              case 2:
                // SelectClauseParser.g:129:30: ( KW_AS LPAREN identifier ( COMMA identifier )* RPAREN )
              {
                // SelectClauseParser.g:129:30: ( KW_AS LPAREN identifier ( COMMA identifier )* RPAREN )
                // SelectClauseParser.g:129:31: KW_AS LPAREN identifier ( COMMA identifier )* RPAREN
                {
                  KW_AS45 = (Token) match(input, KW_AS, FOLLOW_KW_AS_in_selectItem721);
                  stream_KW_AS.add(KW_AS45);

                  LPAREN46 = (Token) match(input, LPAREN, FOLLOW_LPAREN_in_selectItem723);
                  stream_LPAREN.add(LPAREN46);

                  pushFollow(FOLLOW_identifier_in_selectItem725);
                  identifier47 = gHiveParser.identifier();

                  state._fsp--;

                  stream_identifier.add(identifier47.getTree());

                  // SelectClauseParser.g:129:55: ( COMMA identifier )*
                  loop15:
                  do {
                    int alt15 = 2;
                    switch (input.LA(1)) {
                      case COMMA: {
                        alt15 = 1;
                      }
                      break;
                    }

                    switch (alt15) {
                      case 1:
                        // SelectClauseParser.g:129:56: COMMA identifier
                      {
                        COMMA48 = (Token) match(input, COMMA, FOLLOW_COMMA_in_selectItem728);
                        stream_COMMA.add(COMMA48);

                        pushFollow(FOLLOW_identifier_in_selectItem730);
                        identifier49 = gHiveParser.identifier();

                        state._fsp--;

                        stream_identifier.add(identifier49.getTree());
                      }
                      break;

                      default:
                        break loop15;
                    }
                  } while (true);

                  RPAREN50 = (Token) match(input, RPAREN, FOLLOW_RPAREN_in_selectItem734);
                  stream_RPAREN.add(RPAREN50);
                }
              }
              break;
            }
          }

          // AST REWRITE
          // elements: expression, identifier
          // token labels:
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 130:7: -> ^( TOK_SELEXPR expression ( identifier )* )
          {
            // SelectClauseParser.g:130:10: ^( TOK_SELEXPR expression ( identifier )* )
            {
              CommonTree root_1 = (CommonTree) adaptor.nil();
              root_1 = (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_SELEXPR, "TOK_SELEXPR"), root_1);

              adaptor.addChild(root_1, stream_expression.nextTree());

              // SelectClauseParser.g:130:35: ( identifier )*
              while (stream_identifier.hasNext()) {
                adaptor.addChild(root_1, stream_identifier.nextTree());
              }
              stream_identifier.reset();

              adaptor.addChild(root_0, root_1);
            }
          }

          retval.tree = root_0;
        }
        break;
        case 2:
          // SelectClauseParser.g:131:7: tableAllColumns
        {
          pushFollow(FOLLOW_tableAllColumns_in_selectItem762);
          tableAllColumns51 = gHiveParser.tableAllColumns();

          state._fsp--;

          stream_tableAllColumns.add(tableAllColumns51.getTree());

          // AST REWRITE
          // elements: tableAllColumns
          // token labels:
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 131:23: -> ^( TOK_SELEXPR tableAllColumns )
          {
            // SelectClauseParser.g:131:26: ^( TOK_SELEXPR tableAllColumns )
            {
              CommonTree root_1 = (CommonTree) adaptor.nil();
              root_1 = (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_SELEXPR, "TOK_SELEXPR"), root_1);

              adaptor.addChild(root_1, stream_tableAllColumns.nextTree());

              adaptor.addChild(root_0, root_1);
            }
          }

          retval.tree = root_0;
        }
        break;
      }
      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      gParent.popMsg(state);
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "selectItem"

  public static class trfmClause_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "trfmClause"
  // SelectClauseParser.g:134:1: trfmClause : ( KW_MAP selectExpressionList | KW_REDUCE selectExpressionList ) inSerde= rowFormat inRec= recordWriter KW_USING StringLiteral ( KW_AS ( ( LPAREN ( aliasList | columnNameTypeList ) RPAREN ) | ( aliasList | columnNameTypeList ) ) )? outSerde= rowFormat outRec= recordReader -> ^( TOK_TRANSFORM selectExpressionList $inSerde $inRec StringLiteral $outSerde $outRec ( aliasList )? ( columnNameTypeList )? ) ;
  public final HiveParser_SelectClauseParser.trfmClause_return trfmClause() throws RecognitionException {
    HiveParser_SelectClauseParser.trfmClause_return retval = new HiveParser_SelectClauseParser.trfmClause_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token KW_MAP52 = null;
    Token KW_REDUCE54 = null;
    Token KW_USING56 = null;
    Token StringLiteral57 = null;
    Token KW_AS58 = null;
    Token LPAREN59 = null;
    Token RPAREN62 = null;
    HiveParser.rowFormat_return inSerde = null;

    HiveParser.recordWriter_return inRec = null;

    HiveParser.rowFormat_return outSerde = null;

    HiveParser.recordReader_return outRec = null;

    HiveParser_SelectClauseParser.selectExpressionList_return selectExpressionList53 = null;

    HiveParser_SelectClauseParser.selectExpressionList_return selectExpressionList55 = null;

    HiveParser_FromClauseParser.aliasList_return aliasList60 = null;

    HiveParser.columnNameTypeList_return columnNameTypeList61 = null;

    HiveParser_FromClauseParser.aliasList_return aliasList63 = null;

    HiveParser.columnNameTypeList_return columnNameTypeList64 = null;

    CommonTree KW_MAP52_tree = null;
    CommonTree KW_REDUCE54_tree = null;
    CommonTree KW_USING56_tree = null;
    CommonTree StringLiteral57_tree = null;
    CommonTree KW_AS58_tree = null;
    CommonTree LPAREN59_tree = null;
    CommonTree RPAREN62_tree = null;
    RewriteRuleTokenStream stream_StringLiteral = new RewriteRuleTokenStream(adaptor, "token StringLiteral");
    RewriteRuleTokenStream stream_KW_REDUCE = new RewriteRuleTokenStream(adaptor, "token KW_REDUCE");
    RewriteRuleTokenStream stream_KW_USING = new RewriteRuleTokenStream(adaptor, "token KW_USING");
    RewriteRuleTokenStream stream_KW_MAP = new RewriteRuleTokenStream(adaptor, "token KW_MAP");
    RewriteRuleTokenStream stream_LPAREN = new RewriteRuleTokenStream(adaptor, "token LPAREN");
    RewriteRuleTokenStream stream_RPAREN = new RewriteRuleTokenStream(adaptor, "token RPAREN");
    RewriteRuleTokenStream stream_KW_AS = new RewriteRuleTokenStream(adaptor, "token KW_AS");
    RewriteRuleSubtreeStream stream_aliasList = new RewriteRuleSubtreeStream(adaptor, "rule aliasList");
    RewriteRuleSubtreeStream stream_rowFormat = new RewriteRuleSubtreeStream(adaptor, "rule rowFormat");
    RewriteRuleSubtreeStream stream_columnNameTypeList =
        new RewriteRuleSubtreeStream(adaptor, "rule columnNameTypeList");
    RewriteRuleSubtreeStream stream_recordReader = new RewriteRuleSubtreeStream(adaptor, "rule recordReader");
    RewriteRuleSubtreeStream stream_selectExpressionList =
        new RewriteRuleSubtreeStream(adaptor, "rule selectExpressionList");
    RewriteRuleSubtreeStream stream_recordWriter = new RewriteRuleSubtreeStream(adaptor, "rule recordWriter");
    gParent.pushMsg("transform clause", state);
    try {
      // SelectClauseParser.g:137:5: ( ( KW_MAP selectExpressionList | KW_REDUCE selectExpressionList ) inSerde= rowFormat inRec= recordWriter KW_USING StringLiteral ( KW_AS ( ( LPAREN ( aliasList | columnNameTypeList ) RPAREN ) | ( aliasList | columnNameTypeList ) ) )? outSerde= rowFormat outRec= recordReader -> ^( TOK_TRANSFORM selectExpressionList $inSerde $inRec StringLiteral $outSerde $outRec ( aliasList )? ( columnNameTypeList )? ) )
      // SelectClauseParser.g:138:5: ( KW_MAP selectExpressionList | KW_REDUCE selectExpressionList ) inSerde= rowFormat inRec= recordWriter KW_USING StringLiteral ( KW_AS ( ( LPAREN ( aliasList | columnNameTypeList ) RPAREN ) | ( aliasList | columnNameTypeList ) ) )? outSerde= rowFormat outRec= recordReader
      {
        // SelectClauseParser.g:138:5: ( KW_MAP selectExpressionList | KW_REDUCE selectExpressionList )
        int alt18 = 2;
        switch (input.LA(1)) {
          case KW_MAP: {
            alt18 = 1;
          }
          break;
          case KW_REDUCE: {
            alt18 = 2;
          }
          break;
          default:
            NoViableAltException nvae = new NoViableAltException("", 18, 0, input);

            throw nvae;
        }

        switch (alt18) {
          case 1:
            // SelectClauseParser.g:138:9: KW_MAP selectExpressionList
          {
            KW_MAP52 = (Token) match(input, KW_MAP, FOLLOW_KW_MAP_in_trfmClause805);
            stream_KW_MAP.add(KW_MAP52);

            pushFollow(FOLLOW_selectExpressionList_in_trfmClause810);
            selectExpressionList53 = selectExpressionList();

            state._fsp--;

            stream_selectExpressionList.add(selectExpressionList53.getTree());
          }
          break;
          case 2:
            // SelectClauseParser.g:139:9: KW_REDUCE selectExpressionList
          {
            KW_REDUCE54 = (Token) match(input, KW_REDUCE, FOLLOW_KW_REDUCE_in_trfmClause820);
            stream_KW_REDUCE.add(KW_REDUCE54);

            pushFollow(FOLLOW_selectExpressionList_in_trfmClause822);
            selectExpressionList55 = selectExpressionList();

            state._fsp--;

            stream_selectExpressionList.add(selectExpressionList55.getTree());
          }
          break;
        }

        pushFollow(FOLLOW_rowFormat_in_trfmClause832);
        inSerde = gHiveParser.rowFormat();

        state._fsp--;

        stream_rowFormat.add(inSerde.getTree());

        pushFollow(FOLLOW_recordWriter_in_trfmClause836);
        inRec = gHiveParser.recordWriter();

        state._fsp--;

        stream_recordWriter.add(inRec.getTree());

        KW_USING56 = (Token) match(input, KW_USING, FOLLOW_KW_USING_in_trfmClause842);
        stream_KW_USING.add(KW_USING56);

        StringLiteral57 = (Token) match(input, StringLiteral, FOLLOW_StringLiteral_in_trfmClause844);
        stream_StringLiteral.add(StringLiteral57);

        // SelectClauseParser.g:142:5: ( KW_AS ( ( LPAREN ( aliasList | columnNameTypeList ) RPAREN ) | ( aliasList | columnNameTypeList ) ) )?
        int alt22 = 2;
        switch (input.LA(1)) {
          case KW_AS: {
            alt22 = 1;
          }
          break;
        }

        switch (alt22) {
          case 1:
            // SelectClauseParser.g:142:7: KW_AS ( ( LPAREN ( aliasList | columnNameTypeList ) RPAREN ) | ( aliasList | columnNameTypeList ) )
          {
            KW_AS58 = (Token) match(input, KW_AS, FOLLOW_KW_AS_in_trfmClause852);
            stream_KW_AS.add(KW_AS58);

            // SelectClauseParser.g:142:13: ( ( LPAREN ( aliasList | columnNameTypeList ) RPAREN ) | ( aliasList | columnNameTypeList ) )
            int alt21 = 2;
            switch (input.LA(1)) {
              case LPAREN: {
                alt21 = 1;
              }
              break;
              case Identifier:
              case KW_ADD:
              case KW_ADMIN:
              case KW_AFTER:
              case KW_ALL:
              case KW_ALTER:
              case KW_ANALYZE:
              case KW_ARCHIVE:
              case KW_ARRAY:
              case KW_AS:
              case KW_ASC:
              case KW_AUTHORIZATION:
              case KW_BEFORE:
              case KW_BETWEEN:
              case KW_BIGINT:
              case KW_BINARY:
              case KW_BOOLEAN:
              case KW_BOTH:
              case KW_BUCKET:
              case KW_BUCKETS:
              case KW_BY:
              case KW_CASCADE:
              case KW_CHANGE:
              case KW_CLUSTER:
              case KW_CLUSTERED:
              case KW_CLUSTERSTATUS:
              case KW_COLLECTION:
              case KW_COLUMNS:
              case KW_COMMENT:
              case KW_COMPACT:
              case KW_COMPACTIONS:
              case KW_COMPUTE:
              case KW_CONCATENATE:
              case KW_CONTINUE:
              case KW_CREATE:
              case KW_CUBE:
              case KW_CURSOR:
              case KW_DATA:
              case KW_DATABASES:
              case KW_DATE:
              case KW_DATETIME:
              case KW_DBPROPERTIES:
              case KW_DECIMAL:
              case KW_DEFAULT:
              case KW_DEFERRED:
              case KW_DEFINED:
              case KW_DELETE:
              case KW_DELIMITED:
              case KW_DEPENDENCY:
              case KW_DESC:
              case KW_DESCRIBE:
              case KW_DIRECTORIES:
              case KW_DIRECTORY:
              case KW_DISABLE:
              case KW_DISTRIBUTE:
              case KW_DOUBLE:
              case KW_DROP:
              case KW_ELEM_TYPE:
              case KW_ENABLE:
              case KW_ESCAPED:
              case KW_EXCLUSIVE:
              case KW_EXISTS:
              case KW_EXPLAIN:
              case KW_EXPORT:
              case KW_EXTERNAL:
              case KW_FALSE:
              case KW_FETCH:
              case KW_FIELDS:
              case KW_FILE:
              case KW_FILEFORMAT:
              case KW_FIRST:
              case KW_FLOAT:
              case KW_FOR:
              case KW_FORMAT:
              case KW_FORMATTED:
              case KW_FULL:
              case KW_FUNCTIONS:
              case KW_GRANT:
              case KW_GROUP:
              case KW_GROUPING:
              case KW_HOLD_DDLTIME:
              case KW_IDXPROPERTIES:
              case KW_IGNORE:
              case KW_IMPORT:
              case KW_IN:
              case KW_INDEX:
              case KW_INDEXES:
              case KW_INNER:
              case KW_INPATH:
              case KW_INPUTDRIVER:
              case KW_INPUTFORMAT:
              case KW_INSERT:
              case KW_INT:
              case KW_INTERSECT:
              case KW_INTO:
              case KW_IS:
              case KW_ITEMS:
              case KW_JAR:
              case KW_KEYS:
              case KW_KEY_TYPE:
              case KW_LATERAL:
              case KW_LEFT:
              case KW_LIKE:
              case KW_LIMIT:
              case KW_LINES:
              case KW_LOAD:
              case KW_LOCAL:
              case KW_LOCATION:
              case KW_LOCK:
              case KW_LOCKS:
              case KW_LOGICAL:
              case KW_LONG:
              case KW_MAPJOIN:
              case KW_MATERIALIZED:
              case KW_METADATA:
              case KW_MINUS:
              case KW_MSCK:
              case KW_NONE:
              case KW_NOSCAN:
              case KW_NO_DROP:
              case KW_NULL:
              case KW_OF:
              case KW_OFFLINE:
              case KW_OPTION:
              case KW_ORDER:
              case KW_OUT:
              case KW_OUTER:
              case KW_OUTPUTDRIVER:
              case KW_OUTPUTFORMAT:
              case KW_OVERWRITE:
              case KW_OWNER:
              case KW_PARTITION:
              case KW_PARTITIONED:
              case KW_PARTITIONS:
              case KW_PERCENT:
              case KW_PLUS:
              case KW_PRETTY:
              case KW_PRINCIPALS:
              case KW_PROCEDURE:
              case KW_PROTECTION:
              case KW_PURGE:
              case KW_RANGE:
              case KW_READ:
              case KW_READONLY:
              case KW_READS:
              case KW_REBUILD:
              case KW_RECORDREADER:
              case KW_RECORDWRITER:
              case KW_REGEXP:
              case KW_RELOAD:
              case KW_RENAME:
              case KW_REPAIR:
              case KW_REPLACE:
              case KW_REPLICATION:
              case KW_RESTRICT:
              case KW_REVOKE:
              case KW_REWRITE:
              case KW_RIGHT:
              case KW_RLIKE:
              case KW_ROLE:
              case KW_ROLES:
              case KW_ROLLUP:
              case KW_ROW:
              case KW_ROWS:
              case KW_SCHEMA:
              case KW_SCHEMAS:
              case KW_SEMI:
              case KW_SERDE:
              case KW_SERDEPROPERTIES:
              case KW_SERVER:
              case KW_SET:
              case KW_SETS:
              case KW_SHARED:
              case KW_SHOW:
              case KW_SHOW_DATABASE:
              case KW_SKEWED:
              case KW_SMALLINT:
              case KW_SORT:
              case KW_SORTED:
              case KW_SSL:
              case KW_STATISTICS:
              case KW_STORED:
              case KW_STREAMTABLE:
              case KW_STRING:
              case KW_STRUCT:
              case KW_TABLE:
              case KW_TABLES:
              case KW_TBLPROPERTIES:
              case KW_TEMPORARY:
              case KW_TERMINATED:
              case KW_TIMESTAMP:
              case KW_TINYINT:
              case KW_TO:
              case KW_TOUCH:
              case KW_TRANSACTIONS:
              case KW_TRIGGER:
              case KW_TRUE:
              case KW_TRUNCATE:
              case KW_UNARCHIVE:
              case KW_UNDO:
              case KW_UNION:
              case KW_UNIONTYPE:
              case KW_UNLOCK:
              case KW_UNSET:
              case KW_UNSIGNED:
              case KW_UPDATE:
              case KW_URI:
              case KW_USE:
              case KW_USER:
              case KW_USING:
              case KW_UTC:
              case KW_UTCTIMESTAMP:
              case KW_VALUES:
              case KW_VALUE_TYPE:
              case KW_VIEW:
              case KW_WHILE:
              case KW_WITH: {
                alt21 = 2;
              }
              break;
              default:
                NoViableAltException nvae = new NoViableAltException("", 21, 0, input);

                throw nvae;
            }

            switch (alt21) {
              case 1:
                // SelectClauseParser.g:142:14: ( LPAREN ( aliasList | columnNameTypeList ) RPAREN )
              {
                // SelectClauseParser.g:142:14: ( LPAREN ( aliasList | columnNameTypeList ) RPAREN )
                // SelectClauseParser.g:142:15: LPAREN ( aliasList | columnNameTypeList ) RPAREN
                {
                  LPAREN59 = (Token) match(input, LPAREN, FOLLOW_LPAREN_in_trfmClause856);
                  stream_LPAREN.add(LPAREN59);

                  // SelectClauseParser.g:142:22: ( aliasList | columnNameTypeList )
                  int alt19 = 2;
                  switch (input.LA(1)) {
                    case Identifier: {
                      switch (input.LA(2)) {
                        case COMMA:
                        case RPAREN: {
                          alt19 = 1;
                        }
                        break;
                        case KW_ARRAY:
                        case KW_BIGINT:
                        case KW_BINARY:
                        case KW_BOOLEAN:
                        case KW_CHAR:
                        case KW_DATE:
                        case KW_DATETIME:
                        case KW_DECIMAL:
                        case KW_DOUBLE:
                        case KW_FLOAT:
                        case KW_INT:
                        case KW_MAP:
                        case KW_SMALLINT:
                        case KW_STRING:
                        case KW_STRUCT:
                        case KW_TIMESTAMP:
                        case KW_TINYINT:
                        case KW_UNIONTYPE:
                        case KW_VARCHAR: {
                          alt19 = 2;
                        }
                        break;
                        default:
                          NoViableAltException nvae = new NoViableAltException("", 19, 1, input);

                          throw nvae;
                      }
                    }
                    break;
                    case KW_ADD:
                    case KW_ADMIN:
                    case KW_AFTER:
                    case KW_ALL:
                    case KW_ALTER:
                    case KW_ANALYZE:
                    case KW_ARCHIVE:
                    case KW_ARRAY:
                    case KW_AS:
                    case KW_ASC:
                    case KW_AUTHORIZATION:
                    case KW_BEFORE:
                    case KW_BETWEEN:
                    case KW_BIGINT:
                    case KW_BINARY:
                    case KW_BOOLEAN:
                    case KW_BOTH:
                    case KW_BUCKET:
                    case KW_BUCKETS:
                    case KW_BY:
                    case KW_CASCADE:
                    case KW_CHANGE:
                    case KW_CLUSTER:
                    case KW_CLUSTERED:
                    case KW_CLUSTERSTATUS:
                    case KW_COLLECTION:
                    case KW_COLUMNS:
                    case KW_COMMENT:
                    case KW_COMPACT:
                    case KW_COMPACTIONS:
                    case KW_COMPUTE:
                    case KW_CONCATENATE:
                    case KW_CONTINUE:
                    case KW_CREATE:
                    case KW_CUBE:
                    case KW_CURSOR:
                    case KW_DATA:
                    case KW_DATABASES:
                    case KW_DATE:
                    case KW_DATETIME:
                    case KW_DBPROPERTIES:
                    case KW_DECIMAL:
                    case KW_DEFAULT:
                    case KW_DEFERRED:
                    case KW_DEFINED:
                    case KW_DELETE:
                    case KW_DELIMITED:
                    case KW_DEPENDENCY:
                    case KW_DESC:
                    case KW_DESCRIBE:
                    case KW_DIRECTORIES:
                    case KW_DIRECTORY:
                    case KW_DISABLE:
                    case KW_DISTRIBUTE:
                    case KW_DOUBLE:
                    case KW_DROP:
                    case KW_ELEM_TYPE:
                    case KW_ENABLE:
                    case KW_ESCAPED:
                    case KW_EXCLUSIVE:
                    case KW_EXISTS:
                    case KW_EXPLAIN:
                    case KW_EXPORT:
                    case KW_EXTERNAL:
                    case KW_FALSE:
                    case KW_FETCH:
                    case KW_FIELDS:
                    case KW_FILE:
                    case KW_FILEFORMAT:
                    case KW_FIRST:
                    case KW_FLOAT:
                    case KW_FOR:
                    case KW_FORMAT:
                    case KW_FORMATTED:
                    case KW_FULL:
                    case KW_FUNCTIONS:
                    case KW_GRANT:
                    case KW_GROUP:
                    case KW_GROUPING:
                    case KW_HOLD_DDLTIME:
                    case KW_IDXPROPERTIES:
                    case KW_IGNORE:
                    case KW_IMPORT:
                    case KW_IN:
                    case KW_INDEX:
                    case KW_INDEXES:
                    case KW_INNER:
                    case KW_INPATH:
                    case KW_INPUTDRIVER:
                    case KW_INPUTFORMAT:
                    case KW_INSERT:
                    case KW_INT:
                    case KW_INTERSECT:
                    case KW_INTO:
                    case KW_IS:
                    case KW_ITEMS:
                    case KW_JAR:
                    case KW_KEYS:
                    case KW_KEY_TYPE:
                    case KW_LATERAL:
                    case KW_LEFT:
                    case KW_LIKE:
                    case KW_LIMIT:
                    case KW_LINES:
                    case KW_LOAD:
                    case KW_LOCAL:
                    case KW_LOCATION:
                    case KW_LOCK:
                    case KW_LOCKS:
                    case KW_LOGICAL:
                    case KW_LONG:
                    case KW_MAPJOIN:
                    case KW_MATERIALIZED:
                    case KW_METADATA:
                    case KW_MINUS:
                    case KW_MSCK:
                    case KW_NONE:
                    case KW_NOSCAN:
                    case KW_NO_DROP:
                    case KW_NULL:
                    case KW_OF:
                    case KW_OFFLINE:
                    case KW_OPTION:
                    case KW_ORDER:
                    case KW_OUT:
                    case KW_OUTER:
                    case KW_OUTPUTDRIVER:
                    case KW_OUTPUTFORMAT:
                    case KW_OVERWRITE:
                    case KW_OWNER:
                    case KW_PARTITION:
                    case KW_PARTITIONED:
                    case KW_PARTITIONS:
                    case KW_PERCENT:
                    case KW_PLUS:
                    case KW_PRETTY:
                    case KW_PRINCIPALS:
                    case KW_PROCEDURE:
                    case KW_PROTECTION:
                    case KW_PURGE:
                    case KW_RANGE:
                    case KW_READ:
                    case KW_READONLY:
                    case KW_READS:
                    case KW_REBUILD:
                    case KW_RECORDREADER:
                    case KW_RECORDWRITER:
                    case KW_REGEXP:
                    case KW_RELOAD:
                    case KW_RENAME:
                    case KW_REPAIR:
                    case KW_REPLACE:
                    case KW_REPLICATION:
                    case KW_RESTRICT:
                    case KW_REVOKE:
                    case KW_REWRITE:
                    case KW_RIGHT:
                    case KW_RLIKE:
                    case KW_ROLE:
                    case KW_ROLES:
                    case KW_ROLLUP:
                    case KW_ROW:
                    case KW_ROWS:
                    case KW_SCHEMA:
                    case KW_SCHEMAS:
                    case KW_SEMI:
                    case KW_SERDE:
                    case KW_SERDEPROPERTIES:
                    case KW_SERVER:
                    case KW_SET:
                    case KW_SETS:
                    case KW_SHARED:
                    case KW_SHOW:
                    case KW_SHOW_DATABASE:
                    case KW_SKEWED:
                    case KW_SMALLINT:
                    case KW_SORT:
                    case KW_SORTED:
                    case KW_SSL:
                    case KW_STATISTICS:
                    case KW_STORED:
                    case KW_STREAMTABLE:
                    case KW_STRING:
                    case KW_STRUCT:
                    case KW_TABLE:
                    case KW_TABLES:
                    case KW_TBLPROPERTIES:
                    case KW_TEMPORARY:
                    case KW_TERMINATED:
                    case KW_TIMESTAMP:
                    case KW_TINYINT:
                    case KW_TO:
                    case KW_TOUCH:
                    case KW_TRANSACTIONS:
                    case KW_TRIGGER:
                    case KW_TRUE:
                    case KW_TRUNCATE:
                    case KW_UNARCHIVE:
                    case KW_UNDO:
                    case KW_UNION:
                    case KW_UNIONTYPE:
                    case KW_UNLOCK:
                    case KW_UNSET:
                    case KW_UNSIGNED:
                    case KW_UPDATE:
                    case KW_URI:
                    case KW_USE:
                    case KW_USER:
                    case KW_USING:
                    case KW_UTC:
                    case KW_UTCTIMESTAMP:
                    case KW_VALUES:
                    case KW_VALUE_TYPE:
                    case KW_VIEW:
                    case KW_WHILE:
                    case KW_WITH: {
                      switch (input.LA(2)) {
                        case COMMA:
                        case RPAREN: {
                          alt19 = 1;
                        }
                        break;
                        case KW_ARRAY:
                        case KW_BIGINT:
                        case KW_BINARY:
                        case KW_BOOLEAN:
                        case KW_CHAR:
                        case KW_DATE:
                        case KW_DATETIME:
                        case KW_DECIMAL:
                        case KW_DOUBLE:
                        case KW_FLOAT:
                        case KW_INT:
                        case KW_MAP:
                        case KW_SMALLINT:
                        case KW_STRING:
                        case KW_STRUCT:
                        case KW_TIMESTAMP:
                        case KW_TINYINT:
                        case KW_UNIONTYPE:
                        case KW_VARCHAR: {
                          alt19 = 2;
                        }
                        break;
                        default:
                          NoViableAltException nvae = new NoViableAltException("", 19, 2, input);

                          throw nvae;
                      }
                    }
                    break;
                    default:
                      NoViableAltException nvae = new NoViableAltException("", 19, 0, input);

                      throw nvae;
                  }

                  switch (alt19) {
                    case 1:
                      // SelectClauseParser.g:142:23: aliasList
                    {
                      pushFollow(FOLLOW_aliasList_in_trfmClause859);
                      aliasList60 = gHiveParser.aliasList();

                      state._fsp--;

                      stream_aliasList.add(aliasList60.getTree());
                    }
                    break;
                    case 2:
                      // SelectClauseParser.g:142:35: columnNameTypeList
                    {
                      pushFollow(FOLLOW_columnNameTypeList_in_trfmClause863);
                      columnNameTypeList61 = gHiveParser.columnNameTypeList();

                      state._fsp--;

                      stream_columnNameTypeList.add(columnNameTypeList61.getTree());
                    }
                    break;
                  }

                  RPAREN62 = (Token) match(input, RPAREN, FOLLOW_RPAREN_in_trfmClause866);
                  stream_RPAREN.add(RPAREN62);
                }
              }
              break;
              case 2:
                // SelectClauseParser.g:142:65: ( aliasList | columnNameTypeList )
              {
                // SelectClauseParser.g:142:65: ( aliasList | columnNameTypeList )
                int alt20 = 2;
                alt20 = dfa20.predict(input);
                switch (alt20) {
                  case 1:
                    // SelectClauseParser.g:142:66: aliasList
                  {
                    pushFollow(FOLLOW_aliasList_in_trfmClause872);
                    aliasList63 = gHiveParser.aliasList();

                    state._fsp--;

                    stream_aliasList.add(aliasList63.getTree());
                  }
                  break;
                  case 2:
                    // SelectClauseParser.g:142:78: columnNameTypeList
                  {
                    pushFollow(FOLLOW_columnNameTypeList_in_trfmClause876);
                    columnNameTypeList64 = gHiveParser.columnNameTypeList();

                    state._fsp--;

                    stream_columnNameTypeList.add(columnNameTypeList64.getTree());
                  }
                  break;
                }
              }
              break;
            }
          }
          break;
        }

        pushFollow(FOLLOW_rowFormat_in_trfmClause888);
        outSerde = gHiveParser.rowFormat();

        state._fsp--;

        stream_rowFormat.add(outSerde.getTree());

        pushFollow(FOLLOW_recordReader_in_trfmClause892);
        outRec = gHiveParser.recordReader();

        state._fsp--;

        stream_recordReader.add(outRec.getTree());

        // AST REWRITE
        // elements: outRec, outSerde, inRec, selectExpressionList, inSerde, columnNameTypeList, aliasList, StringLiteral
        // token labels:
        // rule labels: inRec, outRec, inSerde, outSerde, retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        RewriteRuleSubtreeStream stream_inRec =
            new RewriteRuleSubtreeStream(adaptor, "rule inRec", inRec != null ? inRec.tree : null);
        RewriteRuleSubtreeStream stream_outRec =
            new RewriteRuleSubtreeStream(adaptor, "rule outRec", outRec != null ? outRec.tree : null);
        RewriteRuleSubtreeStream stream_inSerde =
            new RewriteRuleSubtreeStream(adaptor, "rule inSerde", inSerde != null ? inSerde.tree : null);
        RewriteRuleSubtreeStream stream_outSerde =
            new RewriteRuleSubtreeStream(adaptor, "rule outSerde", outSerde != null ? outSerde.tree : null);
        RewriteRuleSubtreeStream stream_retval =
            new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 144:5: -> ^( TOK_TRANSFORM selectExpressionList $inSerde $inRec StringLiteral $outSerde $outRec ( aliasList )? ( columnNameTypeList )? )
        {
          // SelectClauseParser.g:144:8: ^( TOK_TRANSFORM selectExpressionList $inSerde $inRec StringLiteral $outSerde $outRec ( aliasList )? ( columnNameTypeList )? )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 =
                (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_TRANSFORM, "TOK_TRANSFORM"), root_1);

            adaptor.addChild(root_1, stream_selectExpressionList.nextTree());

            adaptor.addChild(root_1, stream_inSerde.nextTree());

            adaptor.addChild(root_1, stream_inRec.nextTree());

            adaptor.addChild(root_1, stream_StringLiteral.nextNode());

            adaptor.addChild(root_1, stream_outSerde.nextTree());

            adaptor.addChild(root_1, stream_outRec.nextTree());

            // SelectClauseParser.g:144:93: ( aliasList )?
            if (stream_aliasList.hasNext()) {
              adaptor.addChild(root_1, stream_aliasList.nextTree());
            }
            stream_aliasList.reset();

            // SelectClauseParser.g:144:104: ( columnNameTypeList )?
            if (stream_columnNameTypeList.hasNext()) {
              adaptor.addChild(root_1, stream_columnNameTypeList.nextTree());
            }
            stream_columnNameTypeList.reset();

            adaptor.addChild(root_0, root_1);
          }
        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      gParent.popMsg(state);
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "trfmClause"

  public static class selectExpression_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "selectExpression"
  // SelectClauseParser.g:147:1: selectExpression : ( expression | tableAllColumns );
  public final HiveParser_SelectClauseParser.selectExpression_return selectExpression() throws RecognitionException {
    HiveParser_SelectClauseParser.selectExpression_return retval =
        new HiveParser_SelectClauseParser.selectExpression_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    HiveParser_IdentifiersParser.expression_return expression65 = null;

    HiveParser_FromClauseParser.tableAllColumns_return tableAllColumns66 = null;

    gParent.pushMsg("select expression", state);
    try {
      // SelectClauseParser.g:150:5: ( expression | tableAllColumns )
      int alt23 = 2;
      alt23 = dfa23.predict(input);
      switch (alt23) {
        case 1:
          // SelectClauseParser.g:151:5: expression
        {
          root_0 = (CommonTree) adaptor.nil();

          pushFollow(FOLLOW_expression_in_selectExpression955);
          expression65 = gHiveParser.expression();

          state._fsp--;

          adaptor.addChild(root_0, expression65.getTree());
        }
        break;
        case 2:
          // SelectClauseParser.g:151:18: tableAllColumns
        {
          root_0 = (CommonTree) adaptor.nil();

          pushFollow(FOLLOW_tableAllColumns_in_selectExpression959);
          tableAllColumns66 = gHiveParser.tableAllColumns();

          state._fsp--;

          adaptor.addChild(root_0, tableAllColumns66.getTree());
        }
        break;
      }
      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      gParent.popMsg(state);
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "selectExpression"

  public static class selectExpressionList_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "selectExpressionList"
  // SelectClauseParser.g:154:1: selectExpressionList : selectExpression ( COMMA selectExpression )* -> ^( TOK_EXPLIST ( selectExpression )+ ) ;
  public final HiveParser_SelectClauseParser.selectExpressionList_return selectExpressionList()
      throws RecognitionException {
    HiveParser_SelectClauseParser.selectExpressionList_return retval =
        new HiveParser_SelectClauseParser.selectExpressionList_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token COMMA68 = null;
    HiveParser_SelectClauseParser.selectExpression_return selectExpression67 = null;

    HiveParser_SelectClauseParser.selectExpression_return selectExpression69 = null;

    CommonTree COMMA68_tree = null;
    RewriteRuleTokenStream stream_COMMA = new RewriteRuleTokenStream(adaptor, "token COMMA");
    RewriteRuleSubtreeStream stream_selectExpression = new RewriteRuleSubtreeStream(adaptor, "rule selectExpression");
    gParent.pushMsg("select expression list", state);
    try {
      // SelectClauseParser.g:157:5: ( selectExpression ( COMMA selectExpression )* -> ^( TOK_EXPLIST ( selectExpression )+ ) )
      // SelectClauseParser.g:158:5: selectExpression ( COMMA selectExpression )*
      {
        pushFollow(FOLLOW_selectExpression_in_selectExpressionList990);
        selectExpression67 = selectExpression();

        state._fsp--;

        stream_selectExpression.add(selectExpression67.getTree());

        // SelectClauseParser.g:158:22: ( COMMA selectExpression )*
        loop24:
        do {
          int alt24 = 2;
          switch (input.LA(1)) {
            case COMMA: {
              alt24 = 1;
            }
            break;
          }

          switch (alt24) {
            case 1:
              // SelectClauseParser.g:158:23: COMMA selectExpression
            {
              COMMA68 = (Token) match(input, COMMA, FOLLOW_COMMA_in_selectExpressionList993);
              stream_COMMA.add(COMMA68);

              pushFollow(FOLLOW_selectExpression_in_selectExpressionList995);
              selectExpression69 = selectExpression();

              state._fsp--;

              stream_selectExpression.add(selectExpression69.getTree());
            }
            break;

            default:
              break loop24;
          }
        } while (true);

        // AST REWRITE
        // elements: selectExpression
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        RewriteRuleSubtreeStream stream_retval =
            new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 158:48: -> ^( TOK_EXPLIST ( selectExpression )+ )
        {
          // SelectClauseParser.g:158:51: ^( TOK_EXPLIST ( selectExpression )+ )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_EXPLIST, "TOK_EXPLIST"), root_1);

            if (!(stream_selectExpression.hasNext())) {
              throw new RewriteEarlyExitException();
            }
            while (stream_selectExpression.hasNext()) {
              adaptor.addChild(root_1, stream_selectExpression.nextTree());
            }
            stream_selectExpression.reset();

            adaptor.addChild(root_0, root_1);
          }
        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      gParent.popMsg(state);
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "selectExpressionList"

  public static class window_clause_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "window_clause"
  // SelectClauseParser.g:162:1: window_clause : KW_WINDOW window_defn ( COMMA window_defn )* -> ^( KW_WINDOW ( window_defn )+ ) ;
  public final HiveParser_SelectClauseParser.window_clause_return window_clause() throws RecognitionException {
    HiveParser_SelectClauseParser.window_clause_return retval =
        new HiveParser_SelectClauseParser.window_clause_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token KW_WINDOW70 = null;
    Token COMMA72 = null;
    HiveParser_SelectClauseParser.window_defn_return window_defn71 = null;

    HiveParser_SelectClauseParser.window_defn_return window_defn73 = null;

    CommonTree KW_WINDOW70_tree = null;
    CommonTree COMMA72_tree = null;
    RewriteRuleTokenStream stream_COMMA = new RewriteRuleTokenStream(adaptor, "token COMMA");
    RewriteRuleTokenStream stream_KW_WINDOW = new RewriteRuleTokenStream(adaptor, "token KW_WINDOW");
    RewriteRuleSubtreeStream stream_window_defn = new RewriteRuleSubtreeStream(adaptor, "rule window_defn");
    gParent.pushMsg("window_clause", state);
    try {
      // SelectClauseParser.g:165:3: ( KW_WINDOW window_defn ( COMMA window_defn )* -> ^( KW_WINDOW ( window_defn )+ ) )
      // SelectClauseParser.g:166:3: KW_WINDOW window_defn ( COMMA window_defn )*
      {
        KW_WINDOW70 = (Token) match(input, KW_WINDOW, FOLLOW_KW_WINDOW_in_window_clause1034);
        stream_KW_WINDOW.add(KW_WINDOW70);

        pushFollow(FOLLOW_window_defn_in_window_clause1036);
        window_defn71 = window_defn();

        state._fsp--;

        stream_window_defn.add(window_defn71.getTree());

        // SelectClauseParser.g:166:25: ( COMMA window_defn )*
        loop25:
        do {
          int alt25 = 2;
          switch (input.LA(1)) {
            case COMMA: {
              alt25 = 1;
            }
            break;
          }

          switch (alt25) {
            case 1:
              // SelectClauseParser.g:166:26: COMMA window_defn
            {
              COMMA72 = (Token) match(input, COMMA, FOLLOW_COMMA_in_window_clause1039);
              stream_COMMA.add(COMMA72);

              pushFollow(FOLLOW_window_defn_in_window_clause1041);
              window_defn73 = window_defn();

              state._fsp--;

              stream_window_defn.add(window_defn73.getTree());
            }
            break;

            default:
              break loop25;
          }
        } while (true);

        // AST REWRITE
        // elements: KW_WINDOW, window_defn
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        RewriteRuleSubtreeStream stream_retval =
            new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 166:46: -> ^( KW_WINDOW ( window_defn )+ )
        {
          // SelectClauseParser.g:166:49: ^( KW_WINDOW ( window_defn )+ )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 = (CommonTree) adaptor.becomeRoot(stream_KW_WINDOW.nextNode(), root_1);

            if (!(stream_window_defn.hasNext())) {
              throw new RewriteEarlyExitException();
            }
            while (stream_window_defn.hasNext()) {
              adaptor.addChild(root_1, stream_window_defn.nextTree());
            }
            stream_window_defn.reset();

            adaptor.addChild(root_0, root_1);
          }
        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      gParent.popMsg(state);
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "window_clause"

  public static class window_defn_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "window_defn"
  // SelectClauseParser.g:169:1: window_defn : Identifier KW_AS window_specification -> ^( TOK_WINDOWDEF Identifier window_specification ) ;
  public final HiveParser_SelectClauseParser.window_defn_return window_defn() throws RecognitionException {
    HiveParser_SelectClauseParser.window_defn_return retval = new HiveParser_SelectClauseParser.window_defn_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token Identifier74 = null;
    Token KW_AS75 = null;
    HiveParser_SelectClauseParser.window_specification_return window_specification76 = null;

    CommonTree Identifier74_tree = null;
    CommonTree KW_AS75_tree = null;
    RewriteRuleTokenStream stream_Identifier = new RewriteRuleTokenStream(adaptor, "token Identifier");
    RewriteRuleTokenStream stream_KW_AS = new RewriteRuleTokenStream(adaptor, "token KW_AS");
    RewriteRuleSubtreeStream stream_window_specification =
        new RewriteRuleSubtreeStream(adaptor, "rule window_specification");
    gParent.pushMsg("window_defn", state);
    try {
      // SelectClauseParser.g:172:3: ( Identifier KW_AS window_specification -> ^( TOK_WINDOWDEF Identifier window_specification ) )
      // SelectClauseParser.g:173:3: Identifier KW_AS window_specification
      {
        Identifier74 = (Token) match(input, Identifier, FOLLOW_Identifier_in_window_defn1077);
        stream_Identifier.add(Identifier74);

        KW_AS75 = (Token) match(input, KW_AS, FOLLOW_KW_AS_in_window_defn1079);
        stream_KW_AS.add(KW_AS75);

        pushFollow(FOLLOW_window_specification_in_window_defn1081);
        window_specification76 = window_specification();

        state._fsp--;

        stream_window_specification.add(window_specification76.getTree());

        // AST REWRITE
        // elements: Identifier, window_specification
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        RewriteRuleSubtreeStream stream_retval =
            new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 173:41: -> ^( TOK_WINDOWDEF Identifier window_specification )
        {
          // SelectClauseParser.g:173:44: ^( TOK_WINDOWDEF Identifier window_specification )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 =
                (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_WINDOWDEF, "TOK_WINDOWDEF"), root_1);

            adaptor.addChild(root_1, stream_Identifier.nextNode());

            adaptor.addChild(root_1, stream_window_specification.nextTree());

            adaptor.addChild(root_0, root_1);
          }
        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      gParent.popMsg(state);
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "window_defn"

  public static class window_specification_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "window_specification"
  // SelectClauseParser.g:176:1: window_specification : ( Identifier | ( LPAREN ( Identifier )? ( partitioningSpec )? ( window_frame )? RPAREN ) ) -> ^( TOK_WINDOWSPEC ( Identifier )? ( partitioningSpec )? ( window_frame )? ) ;
  public final HiveParser_SelectClauseParser.window_specification_return window_specification()
      throws RecognitionException {
    HiveParser_SelectClauseParser.window_specification_return retval =
        new HiveParser_SelectClauseParser.window_specification_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token Identifier77 = null;
    Token LPAREN78 = null;
    Token Identifier79 = null;
    Token RPAREN82 = null;
    HiveParser_FromClauseParser.partitioningSpec_return partitioningSpec80 = null;

    HiveParser_SelectClauseParser.window_frame_return window_frame81 = null;

    CommonTree Identifier77_tree = null;
    CommonTree LPAREN78_tree = null;
    CommonTree Identifier79_tree = null;
    CommonTree RPAREN82_tree = null;
    RewriteRuleTokenStream stream_Identifier = new RewriteRuleTokenStream(adaptor, "token Identifier");
    RewriteRuleTokenStream stream_LPAREN = new RewriteRuleTokenStream(adaptor, "token LPAREN");
    RewriteRuleTokenStream stream_RPAREN = new RewriteRuleTokenStream(adaptor, "token RPAREN");
    RewriteRuleSubtreeStream stream_partitioningSpec = new RewriteRuleSubtreeStream(adaptor, "rule partitioningSpec");
    RewriteRuleSubtreeStream stream_window_frame = new RewriteRuleSubtreeStream(adaptor, "rule window_frame");
    gParent.pushMsg("window_specification", state);
    try {
      // SelectClauseParser.g:179:3: ( ( Identifier | ( LPAREN ( Identifier )? ( partitioningSpec )? ( window_frame )? RPAREN ) ) -> ^( TOK_WINDOWSPEC ( Identifier )? ( partitioningSpec )? ( window_frame )? ) )
      // SelectClauseParser.g:180:3: ( Identifier | ( LPAREN ( Identifier )? ( partitioningSpec )? ( window_frame )? RPAREN ) )
      {
        // SelectClauseParser.g:180:3: ( Identifier | ( LPAREN ( Identifier )? ( partitioningSpec )? ( window_frame )? RPAREN ) )
        int alt29 = 2;
        switch (input.LA(1)) {
          case Identifier: {
            alt29 = 1;
          }
          break;
          case LPAREN: {
            alt29 = 2;
          }
          break;
          default:
            NoViableAltException nvae = new NoViableAltException("", 29, 0, input);

            throw nvae;
        }

        switch (alt29) {
          case 1:
            // SelectClauseParser.g:180:4: Identifier
          {
            Identifier77 = (Token) match(input, Identifier, FOLLOW_Identifier_in_window_specification1117);
            stream_Identifier.add(Identifier77);
          }
          break;
          case 2:
            // SelectClauseParser.g:180:17: ( LPAREN ( Identifier )? ( partitioningSpec )? ( window_frame )? RPAREN )
          {
            // SelectClauseParser.g:180:17: ( LPAREN ( Identifier )? ( partitioningSpec )? ( window_frame )? RPAREN )
            // SelectClauseParser.g:180:19: LPAREN ( Identifier )? ( partitioningSpec )? ( window_frame )? RPAREN
            {
              LPAREN78 = (Token) match(input, LPAREN, FOLLOW_LPAREN_in_window_specification1123);
              stream_LPAREN.add(LPAREN78);

              // SelectClauseParser.g:180:26: ( Identifier )?
              int alt26 = 2;
              switch (input.LA(1)) {
                case Identifier: {
                  alt26 = 1;
                }
                break;
              }

              switch (alt26) {
                case 1:
                  // SelectClauseParser.g:180:26: Identifier
                {
                  Identifier79 = (Token) match(input, Identifier, FOLLOW_Identifier_in_window_specification1125);
                  stream_Identifier.add(Identifier79);
                }
                break;
              }

              // SelectClauseParser.g:180:38: ( partitioningSpec )?
              int alt27 = 2;
              switch (input.LA(1)) {
                case KW_CLUSTER:
                case KW_DISTRIBUTE:
                case KW_ORDER:
                case KW_PARTITION:
                case KW_SORT: {
                  alt27 = 1;
                }
                break;
              }

              switch (alt27) {
                case 1:
                  // SelectClauseParser.g:180:38: partitioningSpec
                {
                  pushFollow(FOLLOW_partitioningSpec_in_window_specification1128);
                  partitioningSpec80 = gHiveParser.partitioningSpec();

                  state._fsp--;

                  stream_partitioningSpec.add(partitioningSpec80.getTree());
                }
                break;
              }

              // SelectClauseParser.g:180:56: ( window_frame )?
              int alt28 = 2;
              switch (input.LA(1)) {
                case KW_RANGE:
                case KW_ROWS: {
                  alt28 = 1;
                }
                break;
              }

              switch (alt28) {
                case 1:
                  // SelectClauseParser.g:180:56: window_frame
                {
                  pushFollow(FOLLOW_window_frame_in_window_specification1131);
                  window_frame81 = window_frame();

                  state._fsp--;

                  stream_window_frame.add(window_frame81.getTree());
                }
                break;
              }

              RPAREN82 = (Token) match(input, RPAREN, FOLLOW_RPAREN_in_window_specification1134);
              stream_RPAREN.add(RPAREN82);
            }
          }
          break;
        }

        // AST REWRITE
        // elements: Identifier, window_frame, partitioningSpec
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        retval.tree = root_0;
        RewriteRuleSubtreeStream stream_retval =
            new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

        root_0 = (CommonTree) adaptor.nil();
        // 180:79: -> ^( TOK_WINDOWSPEC ( Identifier )? ( partitioningSpec )? ( window_frame )? )
        {
          // SelectClauseParser.g:180:82: ^( TOK_WINDOWSPEC ( Identifier )? ( partitioningSpec )? ( window_frame )? )
          {
            CommonTree root_1 = (CommonTree) adaptor.nil();
            root_1 =
                (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_WINDOWSPEC, "TOK_WINDOWSPEC"), root_1);

            // SelectClauseParser.g:180:99: ( Identifier )?
            if (stream_Identifier.hasNext()) {
              adaptor.addChild(root_1, stream_Identifier.nextNode());
            }
            stream_Identifier.reset();

            // SelectClauseParser.g:180:111: ( partitioningSpec )?
            if (stream_partitioningSpec.hasNext()) {
              adaptor.addChild(root_1, stream_partitioningSpec.nextTree());
            }
            stream_partitioningSpec.reset();

            // SelectClauseParser.g:180:129: ( window_frame )?
            if (stream_window_frame.hasNext()) {
              adaptor.addChild(root_1, stream_window_frame.nextTree());
            }
            stream_window_frame.reset();

            adaptor.addChild(root_0, root_1);
          }
        }

        retval.tree = root_0;
      }

      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      gParent.popMsg(state);
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "window_specification"

  public static class window_frame_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "window_frame"
  // SelectClauseParser.g:183:1: window_frame : ( window_range_expression | window_value_expression );
  public final HiveParser_SelectClauseParser.window_frame_return window_frame() throws RecognitionException {
    HiveParser_SelectClauseParser.window_frame_return retval = new HiveParser_SelectClauseParser.window_frame_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    HiveParser_SelectClauseParser.window_range_expression_return window_range_expression83 = null;

    HiveParser_SelectClauseParser.window_value_expression_return window_value_expression84 = null;

    try {
      // SelectClauseParser.g:183:14: ( window_range_expression | window_value_expression )
      int alt30 = 2;
      switch (input.LA(1)) {
        case KW_ROWS: {
          alt30 = 1;
        }
        break;
        case KW_RANGE: {
          alt30 = 2;
        }
        break;
        default:
          NoViableAltException nvae = new NoViableAltException("", 30, 0, input);

          throw nvae;
      }

      switch (alt30) {
        case 1:
          // SelectClauseParser.g:184:2: window_range_expression
        {
          root_0 = (CommonTree) adaptor.nil();

          pushFollow(FOLLOW_window_range_expression_in_window_frame1161);
          window_range_expression83 = window_range_expression();

          state._fsp--;

          adaptor.addChild(root_0, window_range_expression83.getTree());
        }
        break;
        case 2:
          // SelectClauseParser.g:185:2: window_value_expression
        {
          root_0 = (CommonTree) adaptor.nil();

          pushFollow(FOLLOW_window_value_expression_in_window_frame1166);
          window_value_expression84 = window_value_expression();

          state._fsp--;

          adaptor.addChild(root_0, window_value_expression84.getTree());
        }
        break;
      }
      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "window_frame"

  public static class window_range_expression_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "window_range_expression"
  // SelectClauseParser.g:188:1: window_range_expression : ( KW_ROWS sb= window_frame_start_boundary -> ^( TOK_WINDOWRANGE $sb) | KW_ROWS KW_BETWEEN s= window_frame_boundary KW_AND end= window_frame_boundary -> ^( TOK_WINDOWRANGE $s $end) );
  public final HiveParser_SelectClauseParser.window_range_expression_return window_range_expression()
      throws RecognitionException {
    HiveParser_SelectClauseParser.window_range_expression_return retval =
        new HiveParser_SelectClauseParser.window_range_expression_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token KW_ROWS85 = null;
    Token KW_ROWS86 = null;
    Token KW_BETWEEN87 = null;
    Token KW_AND88 = null;
    HiveParser_SelectClauseParser.window_frame_start_boundary_return sb = null;

    HiveParser_SelectClauseParser.window_frame_boundary_return s = null;

    HiveParser_SelectClauseParser.window_frame_boundary_return end = null;

    CommonTree KW_ROWS85_tree = null;
    CommonTree KW_ROWS86_tree = null;
    CommonTree KW_BETWEEN87_tree = null;
    CommonTree KW_AND88_tree = null;
    RewriteRuleTokenStream stream_KW_BETWEEN = new RewriteRuleTokenStream(adaptor, "token KW_BETWEEN");
    RewriteRuleTokenStream stream_KW_AND = new RewriteRuleTokenStream(adaptor, "token KW_AND");
    RewriteRuleTokenStream stream_KW_ROWS = new RewriteRuleTokenStream(adaptor, "token KW_ROWS");
    RewriteRuleSubtreeStream stream_window_frame_start_boundary =
        new RewriteRuleSubtreeStream(adaptor, "rule window_frame_start_boundary");
    RewriteRuleSubtreeStream stream_window_frame_boundary =
        new RewriteRuleSubtreeStream(adaptor, "rule window_frame_boundary");
    gParent.pushMsg("window_range_expression", state);
    try {
      // SelectClauseParser.g:191:2: ( KW_ROWS sb= window_frame_start_boundary -> ^( TOK_WINDOWRANGE $sb) | KW_ROWS KW_BETWEEN s= window_frame_boundary KW_AND end= window_frame_boundary -> ^( TOK_WINDOWRANGE $s $end) )
      int alt31 = 2;
      switch (input.LA(1)) {
        case KW_ROWS: {
          switch (input.LA(2)) {
            case KW_BETWEEN: {
              alt31 = 2;
            }
            break;
            case KW_CURRENT:
            case KW_UNBOUNDED:
            case Number: {
              alt31 = 1;
            }
            break;
            default:
              NoViableAltException nvae = new NoViableAltException("", 31, 1, input);

              throw nvae;
          }
        }
        break;
        default:
          NoViableAltException nvae = new NoViableAltException("", 31, 0, input);

          throw nvae;
      }

      switch (alt31) {
        case 1:
          // SelectClauseParser.g:192:2: KW_ROWS sb= window_frame_start_boundary
        {
          KW_ROWS85 = (Token) match(input, KW_ROWS, FOLLOW_KW_ROWS_in_window_range_expression1188);
          stream_KW_ROWS.add(KW_ROWS85);

          pushFollow(FOLLOW_window_frame_start_boundary_in_window_range_expression1192);
          sb = window_frame_start_boundary();

          state._fsp--;

          stream_window_frame_start_boundary.add(sb.getTree());

          // AST REWRITE
          // elements: sb
          // token labels:
          // rule labels: retval, sb
          // token list labels:
          // rule list labels:
          // wildcard labels:
          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);
          RewriteRuleSubtreeStream stream_sb =
              new RewriteRuleSubtreeStream(adaptor, "rule sb", sb != null ? sb.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 192:41: -> ^( TOK_WINDOWRANGE $sb)
          {
            // SelectClauseParser.g:192:44: ^( TOK_WINDOWRANGE $sb)
            {
              CommonTree root_1 = (CommonTree) adaptor.nil();
              root_1 = (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_WINDOWRANGE, "TOK_WINDOWRANGE"),
                  root_1);

              adaptor.addChild(root_1, stream_sb.nextTree());

              adaptor.addChild(root_0, root_1);
            }
          }

          retval.tree = root_0;
        }
        break;
        case 2:
          // SelectClauseParser.g:193:2: KW_ROWS KW_BETWEEN s= window_frame_boundary KW_AND end= window_frame_boundary
        {
          KW_ROWS86 = (Token) match(input, KW_ROWS, FOLLOW_KW_ROWS_in_window_range_expression1206);
          stream_KW_ROWS.add(KW_ROWS86);

          KW_BETWEEN87 = (Token) match(input, KW_BETWEEN, FOLLOW_KW_BETWEEN_in_window_range_expression1208);
          stream_KW_BETWEEN.add(KW_BETWEEN87);

          pushFollow(FOLLOW_window_frame_boundary_in_window_range_expression1212);
          s = window_frame_boundary();

          state._fsp--;

          stream_window_frame_boundary.add(s.getTree());

          KW_AND88 = (Token) match(input, KW_AND, FOLLOW_KW_AND_in_window_range_expression1214);
          stream_KW_AND.add(KW_AND88);

          pushFollow(FOLLOW_window_frame_boundary_in_window_range_expression1218);
          end = window_frame_boundary();

          state._fsp--;

          stream_window_frame_boundary.add(end.getTree());

          // AST REWRITE
          // elements: s, end
          // token labels:
          // rule labels: s, end, retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_s =
              new RewriteRuleSubtreeStream(adaptor, "rule s", s != null ? s.tree : null);
          RewriteRuleSubtreeStream stream_end =
              new RewriteRuleSubtreeStream(adaptor, "rule end", end != null ? end.tree : null);
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 193:78: -> ^( TOK_WINDOWRANGE $s $end)
          {
            // SelectClauseParser.g:193:81: ^( TOK_WINDOWRANGE $s $end)
            {
              CommonTree root_1 = (CommonTree) adaptor.nil();
              root_1 = (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_WINDOWRANGE, "TOK_WINDOWRANGE"),
                  root_1);

              adaptor.addChild(root_1, stream_s.nextTree());

              adaptor.addChild(root_1, stream_end.nextTree());

              adaptor.addChild(root_0, root_1);
            }
          }

          retval.tree = root_0;
        }
        break;
      }
      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      gParent.popMsg(state);
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "window_range_expression"

  public static class window_value_expression_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "window_value_expression"
  // SelectClauseParser.g:196:1: window_value_expression : ( KW_RANGE sb= window_frame_start_boundary -> ^( TOK_WINDOWVALUES $sb) | KW_RANGE KW_BETWEEN s= window_frame_boundary KW_AND end= window_frame_boundary -> ^( TOK_WINDOWVALUES $s $end) );
  public final HiveParser_SelectClauseParser.window_value_expression_return window_value_expression()
      throws RecognitionException {
    HiveParser_SelectClauseParser.window_value_expression_return retval =
        new HiveParser_SelectClauseParser.window_value_expression_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token KW_RANGE89 = null;
    Token KW_RANGE90 = null;
    Token KW_BETWEEN91 = null;
    Token KW_AND92 = null;
    HiveParser_SelectClauseParser.window_frame_start_boundary_return sb = null;

    HiveParser_SelectClauseParser.window_frame_boundary_return s = null;

    HiveParser_SelectClauseParser.window_frame_boundary_return end = null;

    CommonTree KW_RANGE89_tree = null;
    CommonTree KW_RANGE90_tree = null;
    CommonTree KW_BETWEEN91_tree = null;
    CommonTree KW_AND92_tree = null;
    RewriteRuleTokenStream stream_KW_BETWEEN = new RewriteRuleTokenStream(adaptor, "token KW_BETWEEN");
    RewriteRuleTokenStream stream_KW_AND = new RewriteRuleTokenStream(adaptor, "token KW_AND");
    RewriteRuleTokenStream stream_KW_RANGE = new RewriteRuleTokenStream(adaptor, "token KW_RANGE");
    RewriteRuleSubtreeStream stream_window_frame_start_boundary =
        new RewriteRuleSubtreeStream(adaptor, "rule window_frame_start_boundary");
    RewriteRuleSubtreeStream stream_window_frame_boundary =
        new RewriteRuleSubtreeStream(adaptor, "rule window_frame_boundary");
    gParent.pushMsg("window_value_expression", state);
    try {
      // SelectClauseParser.g:199:2: ( KW_RANGE sb= window_frame_start_boundary -> ^( TOK_WINDOWVALUES $sb) | KW_RANGE KW_BETWEEN s= window_frame_boundary KW_AND end= window_frame_boundary -> ^( TOK_WINDOWVALUES $s $end) )
      int alt32 = 2;
      switch (input.LA(1)) {
        case KW_RANGE: {
          switch (input.LA(2)) {
            case KW_BETWEEN: {
              alt32 = 2;
            }
            break;
            case KW_CURRENT:
            case KW_UNBOUNDED:
            case Number: {
              alt32 = 1;
            }
            break;
            default:
              NoViableAltException nvae = new NoViableAltException("", 32, 1, input);

              throw nvae;
          }
        }
        break;
        default:
          NoViableAltException nvae = new NoViableAltException("", 32, 0, input);

          throw nvae;
      }

      switch (alt32) {
        case 1:
          // SelectClauseParser.g:200:2: KW_RANGE sb= window_frame_start_boundary
        {
          KW_RANGE89 = (Token) match(input, KW_RANGE, FOLLOW_KW_RANGE_in_window_value_expression1252);
          stream_KW_RANGE.add(KW_RANGE89);

          pushFollow(FOLLOW_window_frame_start_boundary_in_window_value_expression1256);
          sb = window_frame_start_boundary();

          state._fsp--;

          stream_window_frame_start_boundary.add(sb.getTree());

          // AST REWRITE
          // elements: sb
          // token labels:
          // rule labels: retval, sb
          // token list labels:
          // rule list labels:
          // wildcard labels:
          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);
          RewriteRuleSubtreeStream stream_sb =
              new RewriteRuleSubtreeStream(adaptor, "rule sb", sb != null ? sb.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 200:42: -> ^( TOK_WINDOWVALUES $sb)
          {
            // SelectClauseParser.g:200:45: ^( TOK_WINDOWVALUES $sb)
            {
              CommonTree root_1 = (CommonTree) adaptor.nil();
              root_1 =
                  (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_WINDOWVALUES, "TOK_WINDOWVALUES"),
                      root_1);

              adaptor.addChild(root_1, stream_sb.nextTree());

              adaptor.addChild(root_0, root_1);
            }
          }

          retval.tree = root_0;
        }
        break;
        case 2:
          // SelectClauseParser.g:201:2: KW_RANGE KW_BETWEEN s= window_frame_boundary KW_AND end= window_frame_boundary
        {
          KW_RANGE90 = (Token) match(input, KW_RANGE, FOLLOW_KW_RANGE_in_window_value_expression1270);
          stream_KW_RANGE.add(KW_RANGE90);

          KW_BETWEEN91 = (Token) match(input, KW_BETWEEN, FOLLOW_KW_BETWEEN_in_window_value_expression1272);
          stream_KW_BETWEEN.add(KW_BETWEEN91);

          pushFollow(FOLLOW_window_frame_boundary_in_window_value_expression1276);
          s = window_frame_boundary();

          state._fsp--;

          stream_window_frame_boundary.add(s.getTree());

          KW_AND92 = (Token) match(input, KW_AND, FOLLOW_KW_AND_in_window_value_expression1278);
          stream_KW_AND.add(KW_AND92);

          pushFollow(FOLLOW_window_frame_boundary_in_window_value_expression1282);
          end = window_frame_boundary();

          state._fsp--;

          stream_window_frame_boundary.add(end.getTree());

          // AST REWRITE
          // elements: end, s
          // token labels:
          // rule labels: s, end, retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_s =
              new RewriteRuleSubtreeStream(adaptor, "rule s", s != null ? s.tree : null);
          RewriteRuleSubtreeStream stream_end =
              new RewriteRuleSubtreeStream(adaptor, "rule end", end != null ? end.tree : null);
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 201:79: -> ^( TOK_WINDOWVALUES $s $end)
          {
            // SelectClauseParser.g:201:82: ^( TOK_WINDOWVALUES $s $end)
            {
              CommonTree root_1 = (CommonTree) adaptor.nil();
              root_1 =
                  (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_WINDOWVALUES, "TOK_WINDOWVALUES"),
                      root_1);

              adaptor.addChild(root_1, stream_s.nextTree());

              adaptor.addChild(root_1, stream_end.nextTree());

              adaptor.addChild(root_0, root_1);
            }
          }

          retval.tree = root_0;
        }
        break;
      }
      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      gParent.popMsg(state);
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "window_value_expression"

  public static class window_frame_start_boundary_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "window_frame_start_boundary"
  // SelectClauseParser.g:204:1: window_frame_start_boundary : ( KW_UNBOUNDED KW_PRECEDING -> ^( KW_PRECEDING KW_UNBOUNDED ) | KW_CURRENT KW_ROW -> ^( KW_CURRENT ) | Number KW_PRECEDING -> ^( KW_PRECEDING Number ) );
  public final HiveParser_SelectClauseParser.window_frame_start_boundary_return window_frame_start_boundary()
      throws RecognitionException {
    HiveParser_SelectClauseParser.window_frame_start_boundary_return retval =
        new HiveParser_SelectClauseParser.window_frame_start_boundary_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token KW_UNBOUNDED93 = null;
    Token KW_PRECEDING94 = null;
    Token KW_CURRENT95 = null;
    Token KW_ROW96 = null;
    Token Number97 = null;
    Token KW_PRECEDING98 = null;

    CommonTree KW_UNBOUNDED93_tree = null;
    CommonTree KW_PRECEDING94_tree = null;
    CommonTree KW_CURRENT95_tree = null;
    CommonTree KW_ROW96_tree = null;
    CommonTree Number97_tree = null;
    CommonTree KW_PRECEDING98_tree = null;
    RewriteRuleTokenStream stream_Number = new RewriteRuleTokenStream(adaptor, "token Number");
    RewriteRuleTokenStream stream_KW_ROW = new RewriteRuleTokenStream(adaptor, "token KW_ROW");
    RewriteRuleTokenStream stream_KW_UNBOUNDED = new RewriteRuleTokenStream(adaptor, "token KW_UNBOUNDED");
    RewriteRuleTokenStream stream_KW_PRECEDING = new RewriteRuleTokenStream(adaptor, "token KW_PRECEDING");
    RewriteRuleTokenStream stream_KW_CURRENT = new RewriteRuleTokenStream(adaptor, "token KW_CURRENT");

    gParent.pushMsg("windowframestartboundary", state);
    try {
      // SelectClauseParser.g:207:3: ( KW_UNBOUNDED KW_PRECEDING -> ^( KW_PRECEDING KW_UNBOUNDED ) | KW_CURRENT KW_ROW -> ^( KW_CURRENT ) | Number KW_PRECEDING -> ^( KW_PRECEDING Number ) )
      int alt33 = 3;
      switch (input.LA(1)) {
        case KW_UNBOUNDED: {
          alt33 = 1;
        }
        break;
        case KW_CURRENT: {
          alt33 = 2;
        }
        break;
        case Number: {
          alt33 = 3;
        }
        break;
        default:
          NoViableAltException nvae = new NoViableAltException("", 33, 0, input);

          throw nvae;
      }

      switch (alt33) {
        case 1:
          // SelectClauseParser.g:208:3: KW_UNBOUNDED KW_PRECEDING
        {
          KW_UNBOUNDED93 = (Token) match(input, KW_UNBOUNDED, FOLLOW_KW_UNBOUNDED_in_window_frame_start_boundary1317);
          stream_KW_UNBOUNDED.add(KW_UNBOUNDED93);

          KW_PRECEDING94 = (Token) match(input, KW_PRECEDING, FOLLOW_KW_PRECEDING_in_window_frame_start_boundary1319);
          stream_KW_PRECEDING.add(KW_PRECEDING94);

          // AST REWRITE
          // elements: KW_UNBOUNDED, KW_PRECEDING
          // token labels:
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 208:30: -> ^( KW_PRECEDING KW_UNBOUNDED )
          {
            // SelectClauseParser.g:208:33: ^( KW_PRECEDING KW_UNBOUNDED )
            {
              CommonTree root_1 = (CommonTree) adaptor.nil();
              root_1 = (CommonTree) adaptor.becomeRoot(stream_KW_PRECEDING.nextNode(), root_1);

              adaptor.addChild(root_1, stream_KW_UNBOUNDED.nextNode());

              adaptor.addChild(root_0, root_1);
            }
          }

          retval.tree = root_0;
        }
        break;
        case 2:
          // SelectClauseParser.g:209:3: KW_CURRENT KW_ROW
        {
          KW_CURRENT95 = (Token) match(input, KW_CURRENT, FOLLOW_KW_CURRENT_in_window_frame_start_boundary1335);
          stream_KW_CURRENT.add(KW_CURRENT95);

          KW_ROW96 = (Token) match(input, KW_ROW, FOLLOW_KW_ROW_in_window_frame_start_boundary1337);
          stream_KW_ROW.add(KW_ROW96);

          // AST REWRITE
          // elements: KW_CURRENT
          // token labels:
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 209:22: -> ^( KW_CURRENT )
          {
            // SelectClauseParser.g:209:25: ^( KW_CURRENT )
            {
              CommonTree root_1 = (CommonTree) adaptor.nil();
              root_1 = (CommonTree) adaptor.becomeRoot(stream_KW_CURRENT.nextNode(), root_1);

              adaptor.addChild(root_0, root_1);
            }
          }

          retval.tree = root_0;
        }
        break;
        case 3:
          // SelectClauseParser.g:210:3: Number KW_PRECEDING
        {
          Number97 = (Token) match(input, Number, FOLLOW_Number_in_window_frame_start_boundary1350);
          stream_Number.add(Number97);

          KW_PRECEDING98 = (Token) match(input, KW_PRECEDING, FOLLOW_KW_PRECEDING_in_window_frame_start_boundary1352);
          stream_KW_PRECEDING.add(KW_PRECEDING98);

          // AST REWRITE
          // elements: Number, KW_PRECEDING
          // token labels:
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 210:23: -> ^( KW_PRECEDING Number )
          {
            // SelectClauseParser.g:210:26: ^( KW_PRECEDING Number )
            {
              CommonTree root_1 = (CommonTree) adaptor.nil();
              root_1 = (CommonTree) adaptor.becomeRoot(stream_KW_PRECEDING.nextNode(), root_1);

              adaptor.addChild(root_1, stream_Number.nextNode());

              adaptor.addChild(root_0, root_1);
            }
          }

          retval.tree = root_0;
        }
        break;
      }
      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      gParent.popMsg(state);
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "window_frame_start_boundary"

  public static class window_frame_boundary_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "window_frame_boundary"
  // SelectClauseParser.g:213:1: window_frame_boundary : ( KW_UNBOUNDED (r= KW_PRECEDING |r= KW_FOLLOWING ) -> ^( $r KW_UNBOUNDED ) | KW_CURRENT KW_ROW -> ^( KW_CURRENT ) | Number (d= KW_PRECEDING |d= KW_FOLLOWING ) -> ^( $d Number ) );
  public final HiveParser_SelectClauseParser.window_frame_boundary_return window_frame_boundary()
      throws RecognitionException {
    HiveParser_SelectClauseParser.window_frame_boundary_return retval =
        new HiveParser_SelectClauseParser.window_frame_boundary_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token r = null;
    Token d = null;
    Token KW_UNBOUNDED99 = null;
    Token KW_CURRENT100 = null;
    Token KW_ROW101 = null;
    Token Number102 = null;

    CommonTree r_tree = null;
    CommonTree d_tree = null;
    CommonTree KW_UNBOUNDED99_tree = null;
    CommonTree KW_CURRENT100_tree = null;
    CommonTree KW_ROW101_tree = null;
    CommonTree Number102_tree = null;
    RewriteRuleTokenStream stream_Number = new RewriteRuleTokenStream(adaptor, "token Number");
    RewriteRuleTokenStream stream_KW_ROW = new RewriteRuleTokenStream(adaptor, "token KW_ROW");
    RewriteRuleTokenStream stream_KW_UNBOUNDED = new RewriteRuleTokenStream(adaptor, "token KW_UNBOUNDED");
    RewriteRuleTokenStream stream_KW_PRECEDING = new RewriteRuleTokenStream(adaptor, "token KW_PRECEDING");
    RewriteRuleTokenStream stream_KW_FOLLOWING = new RewriteRuleTokenStream(adaptor, "token KW_FOLLOWING");
    RewriteRuleTokenStream stream_KW_CURRENT = new RewriteRuleTokenStream(adaptor, "token KW_CURRENT");

    gParent.pushMsg("windowframeboundary", state);
    try {
      // SelectClauseParser.g:216:3: ( KW_UNBOUNDED (r= KW_PRECEDING |r= KW_FOLLOWING ) -> ^( $r KW_UNBOUNDED ) | KW_CURRENT KW_ROW -> ^( KW_CURRENT ) | Number (d= KW_PRECEDING |d= KW_FOLLOWING ) -> ^( $d Number ) )
      int alt36 = 3;
      switch (input.LA(1)) {
        case KW_UNBOUNDED: {
          alt36 = 1;
        }
        break;
        case KW_CURRENT: {
          alt36 = 2;
        }
        break;
        case Number: {
          alt36 = 3;
        }
        break;
        default:
          NoViableAltException nvae = new NoViableAltException("", 36, 0, input);

          throw nvae;
      }

      switch (alt36) {
        case 1:
          // SelectClauseParser.g:217:3: KW_UNBOUNDED (r= KW_PRECEDING |r= KW_FOLLOWING )
        {
          KW_UNBOUNDED99 = (Token) match(input, KW_UNBOUNDED, FOLLOW_KW_UNBOUNDED_in_window_frame_boundary1383);
          stream_KW_UNBOUNDED.add(KW_UNBOUNDED99);

          // SelectClauseParser.g:217:16: (r= KW_PRECEDING |r= KW_FOLLOWING )
          int alt34 = 2;
          switch (input.LA(1)) {
            case KW_PRECEDING: {
              alt34 = 1;
            }
            break;
            case KW_FOLLOWING: {
              alt34 = 2;
            }
            break;
            default:
              NoViableAltException nvae = new NoViableAltException("", 34, 0, input);

              throw nvae;
          }

          switch (alt34) {
            case 1:
              // SelectClauseParser.g:217:17: r= KW_PRECEDING
            {
              r = (Token) match(input, KW_PRECEDING, FOLLOW_KW_PRECEDING_in_window_frame_boundary1388);
              stream_KW_PRECEDING.add(r);
            }
            break;
            case 2:
              // SelectClauseParser.g:217:32: r= KW_FOLLOWING
            {
              r = (Token) match(input, KW_FOLLOWING, FOLLOW_KW_FOLLOWING_in_window_frame_boundary1392);
              stream_KW_FOLLOWING.add(r);
            }
            break;
          }

          // AST REWRITE
          // elements: KW_UNBOUNDED, r
          // token labels: r
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          retval.tree = root_0;
          RewriteRuleTokenStream stream_r = new RewriteRuleTokenStream(adaptor, "token r", r);
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 217:49: -> ^( $r KW_UNBOUNDED )
          {
            // SelectClauseParser.g:217:52: ^( $r KW_UNBOUNDED )
            {
              CommonTree root_1 = (CommonTree) adaptor.nil();
              root_1 = (CommonTree) adaptor.becomeRoot(stream_r.nextNode(), root_1);

              adaptor.addChild(root_1, stream_KW_UNBOUNDED.nextNode());

              adaptor.addChild(root_0, root_1);
            }
          }

          retval.tree = root_0;
        }
        break;
        case 2:
          // SelectClauseParser.g:218:3: KW_CURRENT KW_ROW
        {
          KW_CURRENT100 = (Token) match(input, KW_CURRENT, FOLLOW_KW_CURRENT_in_window_frame_boundary1410);
          stream_KW_CURRENT.add(KW_CURRENT100);

          KW_ROW101 = (Token) match(input, KW_ROW, FOLLOW_KW_ROW_in_window_frame_boundary1412);
          stream_KW_ROW.add(KW_ROW101);

          // AST REWRITE
          // elements: KW_CURRENT
          // token labels:
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 218:22: -> ^( KW_CURRENT )
          {
            // SelectClauseParser.g:218:25: ^( KW_CURRENT )
            {
              CommonTree root_1 = (CommonTree) adaptor.nil();
              root_1 = (CommonTree) adaptor.becomeRoot(stream_KW_CURRENT.nextNode(), root_1);

              adaptor.addChild(root_0, root_1);
            }
          }

          retval.tree = root_0;
        }
        break;
        case 3:
          // SelectClauseParser.g:219:3: Number (d= KW_PRECEDING |d= KW_FOLLOWING )
        {
          Number102 = (Token) match(input, Number, FOLLOW_Number_in_window_frame_boundary1425);
          stream_Number.add(Number102);

          // SelectClauseParser.g:219:10: (d= KW_PRECEDING |d= KW_FOLLOWING )
          int alt35 = 2;
          switch (input.LA(1)) {
            case KW_PRECEDING: {
              alt35 = 1;
            }
            break;
            case KW_FOLLOWING: {
              alt35 = 2;
            }
            break;
            default:
              NoViableAltException nvae = new NoViableAltException("", 35, 0, input);

              throw nvae;
          }

          switch (alt35) {
            case 1:
              // SelectClauseParser.g:219:11: d= KW_PRECEDING
            {
              d = (Token) match(input, KW_PRECEDING, FOLLOW_KW_PRECEDING_in_window_frame_boundary1430);
              stream_KW_PRECEDING.add(d);
            }
            break;
            case 2:
              // SelectClauseParser.g:219:28: d= KW_FOLLOWING
            {
              d = (Token) match(input, KW_FOLLOWING, FOLLOW_KW_FOLLOWING_in_window_frame_boundary1436);
              stream_KW_FOLLOWING.add(d);
            }
            break;
          }

          // AST REWRITE
          // elements: Number, d
          // token labels: d
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          retval.tree = root_0;
          RewriteRuleTokenStream stream_d = new RewriteRuleTokenStream(adaptor, "token d", d);
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 219:45: -> ^( $d Number )
          {
            // SelectClauseParser.g:219:48: ^( $d Number )
            {
              CommonTree root_1 = (CommonTree) adaptor.nil();
              root_1 = (CommonTree) adaptor.becomeRoot(stream_d.nextNode(), root_1);

              adaptor.addChild(root_1, stream_Number.nextNode());

              adaptor.addChild(root_0, root_1);
            }
          }

          retval.tree = root_0;
        }
        break;
      }
      retval.stop = input.LT(-1);

      retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
      adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

      gParent.popMsg(state);
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "window_frame_boundary"

  // Delegated rules

  protected DFA2 dfa2 = new DFA2(this);
  protected DFA7 dfa7 = new DFA7(this);
  protected DFA17 dfa17 = new DFA17(this);
  protected DFA16 dfa16 = new DFA16(this);
  protected DFA14 dfa14 = new DFA14(this);
  protected DFA20 dfa20 = new DFA20(this);
  protected DFA23 dfa23 = new DFA23(this);
  static final String DFA2_eotS = "\u00f7\uffff";
  static final String DFA2_eofS = "\1\uffff\1\3\66\uffff\1\u0092\u00be\uffff";
  static final String DFA2_minS = "\1\7\1\4\34\uffff\2\7\21\uffff\1\7\4\uffff\1\7\1\uffff\1\7\u00be" + "\uffff";
  static final String DFA2_maxS =
      "\2\u0135\34\uffff\2\u0135\21\uffff\1\u0135\4\uffff\1\u0135\1\uffff" + "\1\u0135\u00be\uffff";
  static final String DFA2_acceptS = "\2\uffff\1\2\1\3\34\uffff\1\1\1\uffff\2\1\6\uffff\2\1\4\uffff\1"
      + "\1\1\uffff\4\1\1\uffff\1\1\4\uffff\1\1\5\uffff\1\1\7\uffff\2\1\2"
      + "\uffff\1\1\3\uffff\1\1\1\uffff\4\1\1\uffff\2\1\1\uffff\2\1\2\uffff"
      + "\2\1\30\uffff\1\1\2\uffff\31\1\32\uffff\32\1\7\uffff\1\1\1\uffff"
      + "\4\1\1\uffff\2\1\1\uffff\2\1\1\uffff\1\1\30\uffff";
  static final String DFA2_specialS = "\u00f7\uffff}>";
  static final String[] DFA2_transitionS = {"\1\3\5\uffff\1\3\4\uffff\1\3\7\uffff\4\3\1\1\2\3\1\uffff\22"
      + "\3\1\uffff\4\3\1\uffff\6\3\1\uffff\2\3\1\uffff\1\3\1\uffff\4"
      + "\3\1\uffff\20\3\1\2\4\3\1\uffff\1\3\1\uffff\1\3\1\uffff\4\3"
      + "\1\uffff\10\3\1\uffff\3\3\1\uffff\1\3\1\uffff\4\3\1\uffff\23"
      + "\3\1\uffff\4\3\1\uffff\12\3\1\uffff\5\3\1\uffff\10\3\1\uffff"
      + "\1\3\1\uffff\5\3\1\uffff\2\3\1\uffff\5\3\2\uffff\14\3\1\uffff"
      + "\22\3\1\uffff\25\3\1\uffff\3\3\1\uffff\5\3\1\uffff\4\3\1\uffff"
      + "\3\3\1\uffff\14\3\1\uffff\1\3\2\uffff\1\3\1\uffff\1\3\3\uffff" + "\1\3\2\uffff\1\3\2\uffff\2\3\7\uffff\5\3",
      "\3\3\1\43\2\uffff\1\3\2\uffff\1\43\2\3\1\uffff\1\3\1\43\1\uffff"
          + "\2\3\1\uffff\2\3\1\uffff\1\64\6\135\1\3\1\135\1\60\1\113\3\135"
          + "\1\112\10\135\2\53\1\135\1\uffff\1\124\3\135\1\uffff\6\135\1"
          + "\uffff\2\135\1\uffff\1\135\1\uffff\2\53\2\135\1\uffff\1\135"
          + "\1\42\16\135\1\uffff\1\125\3\135\1\uffff\1\135\1\uffff\1\135"
          + "\1\uffff\1\135\1\65\2\135\1\uffff\1\135\1\53\6\135\1\uffff\3"
          + "\135\1\3\1\135\1\uffff\2\135\1\116\1\135\1\3\2\135\1\53\2\135"
          + "\1\102\6\135\1\134\3\135\1\67\2\135\1\uffff\2\135\1\132\1\135"
          + "\1\uffff\1\74\1\127\10\135\1\uffff\1\61\4\135\1\uffff\3\135"
          + "\1\36\1\135\1\40\2\135\1\uffff\1\135\1\3\1\122\4\135\1\uffff"
          + "\2\135\1\uffff\5\135\2\uffff\14\135\1\3\1\74\11\135\1\74\7\135"
          + "\1\3\13\135\1\126\6\135\1\62\2\135\1\uffff\3\135\1\uffff\1\43"
          + "\4\135\1\uffff\1\135\1\52\2\135\1\uffff\1\135\1\131\1\63\1\uffff"
          + "\14\135\1\uffff\1\135\1\uffff\1\3\1\135\1\3\1\135\1\uffff\2"
          + "\3\1\66\1\3\1\uffff\1\37\2\3\1\40\1\37\3\uffff\1\3\3\uffff\1"
          + "\70\2\43\1\74\1\43", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "\1\135\5\uffff\1\135\4\uffff\1\135\7\uffff\7\135\1\uffff\6"
          + "\135\1\141\13\135\1\uffff\4\135\1\uffff\6\135\1\uffff\2\135"
          + "\1\uffff\1\135\1\uffff\4\135\1\uffff\20\135\1\uffff\4\135\1"
          + "\uffff\1\135\1\uffff\1\135\1\uffff\4\135\1\uffff\10\135\1\uffff"
          + "\3\135\1\uffff\1\135\1\uffff\4\135\1\uffff\5\135\1\140\15\135"
          + "\1\uffff\4\135\1\uffff\1\172\11\135\1\uffff\5\135\1\uffff\10"
          + "\135\1\uffff\1\135\1\uffff\5\135\1\uffff\2\135\1\uffff\5\135"
          + "\2\uffff\14\135\1\uffff\1\172\11\135\1\172\7\135\1\uffff\25"
          + "\135\1\uffff\3\135\1\uffff\5\135\1\uffff\4\135\1\uffff\3\135"
          + "\1\uffff\14\135\1\uffff\1\135\2\uffff\1\135\1\uffff\1\135\3"
          + "\uffff\1\135\2\uffff\1\135\2\uffff\2\135\10\uffff\4\135",
      "\1\u0082\5\uffff\1\u0086\4\uffff\1\u0085\7\uffff\1\u0091\6"
          + "\u0092\1\uffff\1\u0092\1\u008d\15\u0092\1\u008a\1\u0089\1\u0092"
          + "\1\uffff\4\u0092\1\uffff\6\u0092\1\uffff\2\u0092\1\uffff\1\u0092"
          + "\1\uffff\2\u008b\2\u0092\1\uffff\1\u0092\1\177\16\u0092\1\uffff"
          + "\4\u0092\1\uffff\1\u0092\1\uffff\1\u0092\1\uffff\4\u0092\1\uffff"
          + "\1\u0092\1\u0088\6\u0092\1\uffff\3\u0092\1\uffff\1\u0092\1\uffff"
          + "\4\u0092\1\uffff\2\u0092\1\u008c\20\u0092\1\uffff\4\u0092\1"
          + "\uffff\12\u0092\1\uffff\1\u008e\4\u0092\1\uffff\3\u0092\1\uffff"
          + "\1\u0092\1\175\2\u0092\1\uffff\1\u0092\1\uffff\5\u0092\1\uffff"
          + "\2\u0092\1\uffff\5\u0092\2\uffff\14\u0092\1\uffff\22\u0092\1"
          + "\uffff\22\u0092\1\u008f\2\u0092\1\uffff\3\u0092\1\uffff\1\u0080"
          + "\4\u0092\1\uffff\1\u0092\1\u0087\2\u0092\1\uffff\2\u0092\1\u0090"
          + "\1\uffff\14\u0092\1\uffff\1\u0092\2\uffff\1\u0092\1\uffff\1"
          + "\u0092\3\uffff\1\u0093\2\uffff\1\u0094\2\uffff\1\176\1\u0094"
          + "\10\uffff\1\u0083\1\u0081\1\u0094\1\u0084", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "\1\3\5\uffff\1\3\4\uffff\1\3\7\uffff\7\3\1\uffff\22\3\1\uffff"
          + "\4\3\1\uffff\6\3\1\uffff\2\3\1\uffff\1\3\1\uffff\4\3\1\uffff"
          + "\20\3\1\uffff\4\3\1\uffff\1\3\1\uffff\1\3\1\uffff\4\3\1\uffff"
          + "\10\3\1\uffff\3\3\1\uffff\1\3\1\uffff\4\3\1\uffff\23\3\1\uffff"
          + "\4\3\1\uffff\12\3\1\uffff\5\3\1\uffff\10\3\1\uffff\1\3\1\uffff"
          + "\5\3\1\uffff\2\3\1\uffff\5\3\2\uffff\14\3\1\uffff\22\3\1\uffff"
          + "\25\3\1\uffff\3\3\1\uffff\5\3\1\uffff\4\3\1\uffff\3\3\1\uffff"
          + "\14\3\1\uffff\1\3\2\uffff\1\3\1\uffff\1\3\3\uffff\1\u0095\2"
          + "\uffff\1\3\2\uffff\2\3\7\uffff\5\3", "", "", "", "",
      "\1\u00b7\5\uffff\1\u00bb\4\uffff\1\u00ba\7\uffff\1\u00c6\6"
          + "\u00c9\1\uffff\1\u00c9\1\u00c2\15\u00c9\1\u00bf\1\u00be\1\u00c9"
          + "\1\uffff\4\u00c9\1\uffff\6\u00c9\1\uffff\2\u00c9\1\uffff\1\u00c9"
          + "\1\uffff\2\u00c0\2\u00c9\1\uffff\1\u00c9\1\u00b4\16\u00c9\1"
          + "\3\4\u00c9\1\uffff\1\u00c9\1\uffff\1\u00c9\1\uffff\1\u00c9\1"
          + "\u00c7\2\u00c9\1\uffff\1\u00c9\1\u00bd\6\u00c9\1\uffff\3\u00c9"
          + "\1\uffff\1\u00c9\1\uffff\4\u00c9\1\uffff\2\u00c9\1\u00c1\20"
          + "\u00c9\1\uffff\4\u00c9\1\uffff\12\u00c9\1\uffff\1\u00c3\4\u00c9"
          + "\1\uffff\3\u00c9\1\u00b0\1\u00c9\1\u00b2\2\u00c9\1\uffff\1\u00c9"
          + "\1\uffff\5\u00c9\1\uffff\2\u00c9\1\uffff\5\u00c9\2\uffff\14"
          + "\u00c9\1\uffff\22\u00c9\1\uffff\22\u00c9\1\u00c4\2\u00c9\1\uffff"
          + "\3\u00c9\1\uffff\1\u00b5\4\u00c9\1\uffff\1\u00c9\1\u00bc\2\u00c9"
          + "\1\uffff\2\u00c9\1\u00c5\1\uffff\14\u00c9\1\uffff\1\u00c9\2"
          + "\uffff\1\u00c9\1\uffff\1\u00c9\3\uffff\1\u00c8\2\uffff\1\u00b1"
          + "\2\uffff\1\u00b3\1\u00b1\3\uffff\1\3\3\uffff\1\3\1\u00b8\1\u00b6" + "\1\u00b1\1\u00b9", "",
      "\1\3\2\uffff\1\u0092\2\uffff\1\3\4\uffff\1\3\7\uffff\7\3\1"
          + "\uffff\22\3\1\uffff\1\u00d4\3\3\1\uffff\6\3\1\uffff\2\3\1\uffff"
          + "\1\3\1\uffff\4\3\1\uffff\20\3\1\uffff\1\u00d5\3\3\1\uffff\1"
          + "\3\1\uffff\1\3\1\uffff\4\3\1\uffff\10\3\1\uffff\3\3\1\u0092"
          + "\1\3\1\uffff\2\3\1\u00d1\1\3\1\u0092\14\3\1\u00dc\6\3\1\uffff"
          + "\2\3\1\u00db\1\3\1\uffff\1\3\1\u00d8\10\3\1\uffff\1\u00de\4"
          + "\3\1\uffff\3\3\1\uffff\4\3\1\uffff\1\3\1\uffff\1\u00d3\4\3\1"
          + "\uffff\2\3\1\uffff\5\3\2\uffff\14\3\1\u0092\22\3\1\u0092\13"
          + "\3\1\u00d6\11\3\1\uffff\3\3\1\uffff\5\3\1\uffff\4\3\1\uffff"
          + "\1\3\1\u00d9\1\3\1\uffff\14\3\1\uffff\1\3\1\uffff\1\u0092\1"
          + "\3\1\u0092\1\3\3\uffff\1\3\2\uffff\1\3\2\uffff\2\3\3\uffff\1"
          + "\u0092\4\uffff\4\3", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};

  static final short[] DFA2_eot = DFA.unpackEncodedString(DFA2_eotS);
  static final short[] DFA2_eof = DFA.unpackEncodedString(DFA2_eofS);
  static final char[] DFA2_min = DFA.unpackEncodedStringToUnsignedChars(DFA2_minS);
  static final char[] DFA2_max = DFA.unpackEncodedStringToUnsignedChars(DFA2_maxS);
  static final short[] DFA2_accept = DFA.unpackEncodedString(DFA2_acceptS);
  static final short[] DFA2_special = DFA.unpackEncodedString(DFA2_specialS);
  static final short[][] DFA2_transition;

  static {
    int numStates = DFA2_transitionS.length;
    DFA2_transition = new short[numStates][];
    for (int i = 0; i < numStates; i++) {
      DFA2_transition[i] = DFA.unpackEncodedString(DFA2_transitionS[i]);
    }
  }

  class DFA2 extends DFA {

    public DFA2(BaseRecognizer recognizer) {
      this.recognizer = recognizer;
      this.decisionNumber = 2;
      this.eot = DFA2_eot;
      this.eof = DFA2_eof;
      this.min = DFA2_min;
      this.max = DFA2_max;
      this.accept = DFA2_accept;
      this.special = DFA2_special;
      this.transition = DFA2_transition;
    }

    public String getDescription() {
      return "52:29: ( KW_ALL |dist= KW_DISTINCT )?";
    }
  }

  static final String DFA7_eotS = "\u0089\uffff";
  static final String DFA7_eofS = "\1\uffff\2\3\u0086\uffff";
  static final String DFA7_minS = "\1\32\2\12\23\uffff\1\7\46\uffff\1\7\113\uffff";
  static final String DFA7_maxS = "\1\u011e\2\u012d\23\uffff\1\u0135\46\uffff\1\u0135\113\uffff";
  static final String DFA7_acceptS = "\3\uffff\1\1\24\uffff\1\2\160\uffff";
  static final String DFA7_specialS = "\u0089\uffff}>";
  static final String[] DFA7_transitionS = {"\1\1\6\2\1\uffff\17\2\2\uffff\1\2\1\uffff\4\2\1\uffff\6\2\1"
      + "\uffff\2\2\1\uffff\1\2\3\uffff\2\2\1\uffff\20\2\1\uffff\4\2"
      + "\1\uffff\1\2\1\uffff\1\2\1\uffff\4\2\1\uffff\10\2\1\uffff\3"
      + "\2\1\uffff\1\2\1\uffff\4\2\1\uffff\2\2\1\uffff\20\2\1\uffff"
      + "\4\2\1\uffff\12\2\2\uffff\4\2\1\uffff\3\2\1\uffff\4\2\1\uffff"
      + "\1\2\1\uffff\5\2\1\uffff\2\2\1\uffff\5\2\2\uffff\14\2\1\uffff"
      + "\22\2\1\uffff\25\2\1\uffff\3\2\1\uffff\5\2\1\uffff\4\2\1\uffff"
      + "\3\2\1\uffff\14\2\1\uffff\1\2\2\uffff\1\2\1\uffff\1\2",
      "\1\3\30\uffff\1\30\5\uffff\3\30\10\uffff\1\30\1\3\26\uffff"
          + "\2\30\1\uffff\1\30\14\uffff\1\3\1\30\23\uffff\1\30\4\uffff\1"
          + "\3\4\uffff\1\3\1\uffff\1\3\14\uffff\1\3\1\30\10\uffff\1\3\3"
          + "\uffff\1\3\11\uffff\1\26\20\uffff\1\3\31\uffff\1\3\1\uffff\1"
          + "\3\16\uffff\1\3\3\uffff\1\3\12\uffff\1\30\1\3\5\uffff\2\30\7"
          + "\uffff\2\30\12\uffff\1\3\1\30\15\uffff\1\30\2\uffff\1\3\1\uffff" + "\1\3\17\uffff\1\3",
      "\1\3\30\uffff\1\30\5\uffff\3\30\10\uffff\1\30\1\3\26\uffff"
          + "\2\30\1\uffff\1\30\14\uffff\1\3\1\30\23\uffff\1\30\4\uffff\1"
          + "\3\4\uffff\1\3\1\uffff\1\3\14\uffff\1\3\1\30\10\uffff\1\3\3"
          + "\uffff\1\3\11\uffff\1\75\20\uffff\1\3\31\uffff\1\3\1\uffff\1"
          + "\3\16\uffff\1\3\3\uffff\1\3\12\uffff\1\30\1\3\5\uffff\2\30\7"
          + "\uffff\2\30\12\uffff\1\3\1\30\15\uffff\1\30\2\uffff\1\3\1\uffff"
          + "\1\3\17\uffff\1\3", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "\1\3\5\uffff\1\3\4\uffff\1\3\7\uffff\7\3\1\uffff\22\3\1\uffff"
          + "\4\3\1\uffff\6\3\1\uffff\2\3\1\uffff\1\3\1\uffff\4\3\1\uffff"
          + "\20\3\1\uffff\4\3\1\uffff\1\3\1\uffff\1\3\1\uffff\4\3\1\uffff"
          + "\10\3\1\uffff\3\3\1\uffff\1\3\1\uffff\4\3\1\uffff\23\3\1\uffff"
          + "\4\3\1\uffff\12\3\1\uffff\5\3\1\uffff\10\3\1\uffff\1\3\1\uffff"
          + "\5\3\1\uffff\2\3\1\uffff\5\3\2\uffff\14\3\1\uffff\22\3\1\uffff"
          + "\25\3\1\uffff\3\3\1\uffff\5\3\1\uffff\4\3\1\uffff\3\3\1\uffff"
          + "\14\3\1\uffff\1\3\2\uffff\1\3\1\uffff\1\3\1\uffff\1\30\1\uffff"
          + "\1\3\2\uffff\1\3\2\uffff\2\3\7\uffff\5\3", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "\1\3\5\uffff\1\3\4\uffff\1\3\7\uffff\7\3\1\uffff\22\3\1\uffff"
          + "\4\3\1\uffff\6\3\1\uffff\2\3\1\uffff\1\3\1\uffff\4\3\1\uffff"
          + "\20\3\1\uffff\4\3\1\uffff\1\3\1\uffff\1\3\1\uffff\4\3\1\uffff"
          + "\10\3\1\uffff\3\3\1\uffff\1\3\1\uffff\4\3\1\uffff\23\3\1\uffff"
          + "\4\3\1\uffff\12\3\1\uffff\5\3\1\uffff\10\3\1\uffff\1\3\1\uffff"
          + "\5\3\1\uffff\2\3\1\uffff\5\3\2\uffff\14\3\1\uffff\22\3\1\uffff"
          + "\25\3\1\uffff\3\3\1\uffff\5\3\1\uffff\4\3\1\uffff\3\3\1\uffff"
          + "\14\3\1\uffff\1\3\2\uffff\1\3\1\uffff\1\3\1\uffff\1\30\1\uffff"
          + "\1\3\2\uffff\1\3\2\uffff\2\3\7\uffff\5\3", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};

  static final short[] DFA7_eot = DFA.unpackEncodedString(DFA7_eotS);
  static final short[] DFA7_eof = DFA.unpackEncodedString(DFA7_eofS);
  static final char[] DFA7_min = DFA.unpackEncodedStringToUnsignedChars(DFA7_minS);
  static final char[] DFA7_max = DFA.unpackEncodedStringToUnsignedChars(DFA7_maxS);
  static final short[] DFA7_accept = DFA.unpackEncodedString(DFA7_acceptS);
  static final short[] DFA7_special = DFA.unpackEncodedString(DFA7_specialS);
  static final short[][] DFA7_transition;

  static {
    int numStates = DFA7_transitionS.length;
    DFA7_transition = new short[numStates][];
    for (int i = 0; i < numStates; i++) {
      DFA7_transition[i] = DFA.unpackEncodedString(DFA7_transitionS[i]);
    }
  }

  class DFA7 extends DFA {

    public DFA7(BaseRecognizer recognizer) {
      this.recognizer = recognizer;
      this.decisionNumber = 7;
      this.eot = DFA7_eot;
      this.eof = DFA7_eof;
      this.min = DFA7_min;
      this.max = DFA7_max;
      this.accept = DFA7_accept;
      this.special = DFA7_special;
      this.transition = DFA7_transition;
    }

    public String getDescription() {
      return "75:65: ( aliasList | columnNameTypeList )";
    }
  }

  static final String DFA17_eotS = "\u0223\uffff";
  static final String DFA17_eofS =
      "\3\uffff\1\1\1\uffff\2\1\6\uffff\2\1\4\uffff\1\1\1\uffff\4\1\1\uffff" + "\1\1\u0208\uffff";
  static final String DFA17_minS = "\1\7\2\uffff\1\4\1\uffff\2\4\6\uffff\2\4\4\uffff\1\4\1\uffff\4\4"
      + "\1\uffff\1\4\2\uffff\1\32\53\uffff\1\32\54\uffff\1\32\54\uffff\1"
      + "\32\53\uffff\1\32\53\uffff\1\32\53\uffff\1\32\53\uffff\1\32\52\uffff"
      + "\1\32\53\uffff\1\32\53\uffff\1\32\114\uffff";
  static final String DFA17_maxS = "\1\u0135\2\uffff\1\u0131\1\uffff\2\u0133\6\uffff\2\u0131\4\uffff"
      + "\1\u0131\1\uffff\4\u0131\1\uffff\1\u0131\2\uffff\1\u0131\53\uffff"
      + "\1\u0131\54\uffff\1\u0131\54\uffff\1\u0131\53\uffff\1\u0131\53\uffff"
      + "\1\u0131\53\uffff\1\u0131\53\uffff\1\u0131\52\uffff\1\u0131\53\uffff" + "\1\u0131\53\uffff\1\u0131\114\uffff";
  static final String DFA17_acceptS = "\1\uffff\1\1\31\uffff\1\2\u01e7\uffff\2\1\1\uffff\2\1\1\uffff\2"
      + "\1\1\uffff\2\1\1\uffff\2\1\1\uffff\2\1\1\uffff\2\1\1\uffff\2\1\1" + "\uffff\2\1\1\uffff\2\1\1\uffff\2\1";
  static final String DFA17_specialS = "\u0223\uffff}>";
  static final String[] DFA17_transitionS = {"\1\1\5\uffff\1\1\4\uffff\1\1\7\uffff\1\27\6\32\1\uffff\1\32"
      + "\1\23\15\32\2\1\1\32\1\uffff\4\32\1\uffff\6\32\1\uffff\2\32"
      + "\1\uffff\1\32\1\uffff\2\1\2\32\1\uffff\1\32\1\5\16\32\1\uffff"
      + "\4\32\1\uffff\1\32\1\uffff\1\32\1\uffff\1\32\1\30\2\32\1\uffff"
      + "\1\32\1\16\6\32\1\uffff\3\32\1\uffff\1\32\1\uffff\4\32\1\uffff"
      + "\2\32\1\1\20\32\1\uffff\4\32\1\uffff\12\32\1\uffff\1\1\4\32"
      + "\1\uffff\3\32\1\1\1\32\1\3\2\32\1\uffff\1\32\1\uffff\5\32\1"
      + "\uffff\2\32\1\uffff\5\32\2\uffff\14\32\1\uffff\22\32\1\uffff"
      + "\22\32\1\25\2\32\1\uffff\3\32\1\uffff\1\6\4\32\1\uffff\1\32"
      + "\1\15\2\32\1\uffff\2\32\1\26\1\uffff\14\32\1\uffff\1\32\2\uffff"
      + "\1\32\1\uffff\1\32\3\uffff\1\1\2\uffff\1\1\2\uffff\2\1\7\uffff" + "\1\33\4\1", "", "",
      "\3\1\3\uffff\1\1\3\uffff\2\1\1\uffff\1\35\2\uffff\2\1\1\uffff"
          + "\2\1\1\uffff\27\1\2\uffff\1\1\1\uffff\4\1\1\uffff\6\1\1\uffff"
          + "\2\1\1\uffff\1\1\3\uffff\2\1\1\uffff\20\1\1\uffff\4\1\1\uffff"
          + "\1\1\1\uffff\1\1\1\uffff\4\1\1\uffff\10\1\1\uffff\5\1\1\uffff"
          + "\7\1\1\uffff\20\1\1\uffff\4\1\1\uffff\12\1\1\uffff\5\1\1\uffff"
          + "\10\1\1\uffff\7\1\1\uffff\2\1\1\uffff\5\1\2\uffff\65\1\1\uffff"
          + "\3\1\1\uffff\5\1\1\uffff\4\1\1\uffff\3\1\1\uffff\14\1\1\uffff"
          + "\1\1\1\uffff\4\1\1\uffff\4\1\1\uffff\3\1\1\uffff\1\1\3\uffff" + "\1\1\3\uffff\1\1", "",
      "\3\1\3\uffff\1\1\3\uffff\2\1\1\uffff\1\111\2\uffff\2\1\1\uffff"
          + "\2\1\1\uffff\27\1\2\uffff\1\1\1\uffff\4\1\1\uffff\6\1\1\uffff"
          + "\2\1\1\uffff\1\1\3\uffff\2\1\1\uffff\20\1\1\uffff\4\1\1\uffff"
          + "\1\1\1\uffff\1\1\1\uffff\4\1\1\uffff\10\1\1\uffff\5\1\1\uffff"
          + "\7\1\1\uffff\20\1\1\uffff\4\1\1\uffff\12\1\1\uffff\5\1\1\uffff"
          + "\10\1\1\uffff\7\1\1\uffff\2\1\1\uffff\5\1\2\uffff\65\1\1\uffff"
          + "\3\1\1\uffff\5\1\1\uffff\4\1\1\uffff\3\1\1\uffff\14\1\1\uffff"
          + "\1\1\1\uffff\4\1\1\uffff\4\1\1\uffff\3\1\1\uffff\1\1\3\uffff" + "\1\1\3\uffff\1\1\1\uffff\1\1",
      "\3\1\3\uffff\1\1\3\uffff\2\1\1\uffff\1\166\2\uffff\2\1\1\uffff"
          + "\2\1\1\uffff\27\1\2\uffff\1\1\1\uffff\4\1\1\uffff\6\1\1\uffff"
          + "\2\1\1\uffff\1\1\3\uffff\2\1\1\uffff\20\1\1\uffff\4\1\1\uffff"
          + "\1\1\1\uffff\1\1\1\uffff\4\1\1\uffff\10\1\1\uffff\5\1\1\uffff"
          + "\7\1\1\uffff\20\1\1\uffff\4\1\1\uffff\12\1\1\uffff\5\1\1\uffff"
          + "\10\1\1\uffff\7\1\1\uffff\2\1\1\uffff\5\1\2\uffff\65\1\1\uffff"
          + "\3\1\1\uffff\5\1\1\uffff\4\1\1\uffff\3\1\1\uffff\14\1\1\uffff"
          + "\1\1\1\uffff\4\1\1\uffff\4\1\1\uffff\3\1\1\uffff\1\1\3\uffff"
          + "\1\1\3\uffff\1\1\1\uffff\1\1", "", "", "", "", "", "",
      "\3\1\3\uffff\1\1\3\uffff\2\1\1\uffff\1\u00a3\2\uffff\2\1\1"
          + "\uffff\2\1\1\uffff\27\1\2\uffff\1\1\1\uffff\4\1\1\uffff\6\1"
          + "\1\uffff\2\1\1\uffff\1\1\3\uffff\2\1\1\uffff\20\1\1\uffff\4"
          + "\1\1\uffff\1\1\1\uffff\1\1\1\uffff\4\1\1\uffff\10\1\1\uffff"
          + "\5\1\1\uffff\7\1\1\uffff\20\1\1\uffff\4\1\1\uffff\12\1\1\uffff"
          + "\5\1\1\uffff\10\1\1\uffff\7\1\1\uffff\2\1\1\uffff\5\1\2\uffff"
          + "\65\1\1\uffff\3\1\1\uffff\5\1\1\uffff\4\1\1\uffff\3\1\1\uffff"
          + "\14\1\1\uffff\1\1\1\uffff\4\1\1\uffff\4\1\1\uffff\3\1\1\uffff" + "\1\1\3\uffff\1\1\3\uffff\1\1",
      "\3\1\3\uffff\1\1\3\uffff\2\1\1\uffff\1\u00cf\2\uffff\2\1\1"
          + "\uffff\2\1\1\uffff\27\1\2\uffff\1\1\1\uffff\4\1\1\uffff\6\1"
          + "\1\uffff\2\1\1\uffff\1\1\3\uffff\2\1\1\uffff\20\1\1\uffff\4"
          + "\1\1\uffff\1\1\1\uffff\1\1\1\uffff\4\1\1\uffff\10\1\1\uffff"
          + "\5\1\1\uffff\7\1\1\uffff\20\1\1\uffff\4\1\1\uffff\12\1\1\uffff"
          + "\5\1\1\uffff\10\1\1\uffff\7\1\1\uffff\2\1\1\uffff\5\1\2\uffff"
          + "\65\1\1\uffff\3\1\1\uffff\5\1\1\uffff\4\1\1\uffff\3\1\1\uffff"
          + "\14\1\1\uffff\1\1\1\uffff\4\1\1\uffff\4\1\1\uffff\3\1\1\uffff"
          + "\1\1\3\uffff\1\1\3\uffff\1\1", "", "", "", "", "\3\1\3\uffff\1\1\3\uffff\2\1\1\uffff\1\u00fb\2\uffff\2\1\1"
      + "\uffff\2\1\1\uffff\27\1\2\uffff\1\1\1\uffff\4\1\1\uffff\6\1"
      + "\1\uffff\2\1\1\uffff\1\1\3\uffff\2\1\1\uffff\20\1\1\uffff\4"
      + "\1\1\uffff\1\1\1\uffff\1\1\1\uffff\4\1\1\uffff\10\1\1\uffff"
      + "\5\1\1\uffff\7\1\1\uffff\20\1\1\uffff\4\1\1\uffff\12\1\1\uffff"
      + "\5\1\1\uffff\10\1\1\uffff\7\1\1\uffff\2\1\1\uffff\5\1\2\uffff"
      + "\65\1\1\uffff\3\1\1\uffff\5\1\1\uffff\4\1\1\uffff\3\1\1\uffff"
      + "\14\1\1\uffff\1\1\1\uffff\4\1\1\uffff\4\1\1\uffff\3\1\1\uffff" + "\1\1\3\uffff\1\1\3\uffff\1\1", "",
      "\3\1\3\uffff\1\1\3\uffff\2\1\1\uffff\1\u0127\2\uffff\2\1\1"
          + "\uffff\2\1\1\uffff\27\1\2\uffff\1\1\1\uffff\4\1\1\uffff\6\1"
          + "\1\uffff\2\1\1\uffff\1\1\3\uffff\2\1\1\uffff\20\1\1\uffff\4"
          + "\1\1\uffff\1\1\1\uffff\1\1\1\uffff\4\1\1\uffff\10\1\1\uffff"
          + "\5\1\1\uffff\7\1\1\uffff\20\1\1\uffff\4\1\1\uffff\12\1\1\uffff"
          + "\5\1\1\uffff\10\1\1\uffff\7\1\1\uffff\2\1\1\uffff\5\1\2\uffff"
          + "\65\1\1\uffff\3\1\1\uffff\5\1\1\uffff\4\1\1\uffff\3\1\1\uffff"
          + "\14\1\1\uffff\1\1\1\uffff\4\1\1\uffff\4\1\1\uffff\3\1\1\uffff" + "\1\1\3\uffff\1\1\3\uffff\1\1",
      "\3\1\3\uffff\1\1\3\uffff\2\1\1\uffff\1\u0153\2\uffff\2\1\1"
          + "\uffff\2\1\1\uffff\27\1\2\uffff\1\1\1\uffff\4\1\1\uffff\6\1"
          + "\1\uffff\2\1\1\uffff\1\1\3\uffff\2\1\1\uffff\20\1\1\uffff\4"
          + "\1\1\uffff\1\1\1\uffff\1\1\1\uffff\4\1\1\uffff\10\1\1\uffff"
          + "\5\1\1\uffff\7\1\1\uffff\20\1\1\uffff\4\1\1\uffff\12\1\1\uffff"
          + "\5\1\1\uffff\10\1\1\uffff\7\1\1\uffff\2\1\1\uffff\5\1\2\uffff"
          + "\65\1\1\uffff\3\1\1\uffff\5\1\1\uffff\4\1\1\uffff\3\1\1\uffff"
          + "\14\1\1\uffff\1\1\1\uffff\4\1\1\uffff\4\1\1\uffff\3\1\1\uffff" + "\1\1\3\uffff\1\1\3\uffff\1\1",
      "\3\1\3\uffff\1\1\3\uffff\2\1\1\uffff\1\u017e\2\uffff\2\1\1"
          + "\uffff\2\1\1\uffff\27\1\2\uffff\1\1\1\uffff\4\1\1\uffff\6\1"
          + "\1\uffff\2\1\1\uffff\1\1\3\uffff\2\1\1\uffff\20\1\1\uffff\4"
          + "\1\1\uffff\1\1\1\uffff\1\1\1\uffff\4\1\1\uffff\10\1\1\uffff"
          + "\5\1\1\uffff\7\1\1\uffff\20\1\1\uffff\4\1\1\uffff\12\1\1\uffff"
          + "\5\1\1\uffff\10\1\1\uffff\7\1\1\uffff\2\1\1\uffff\5\1\2\uffff"
          + "\65\1\1\uffff\3\1\1\uffff\5\1\1\uffff\4\1\1\uffff\3\1\1\uffff"
          + "\14\1\1\uffff\1\1\1\uffff\4\1\1\uffff\4\1\1\uffff\3\1\1\uffff" + "\1\1\3\uffff\1\1\3\uffff\1\1",
      "\3\1\3\uffff\1\1\3\uffff\2\1\1\uffff\1\u01aa\2\uffff\2\1\1"
          + "\uffff\2\1\1\uffff\27\1\2\uffff\1\1\1\uffff\4\1\1\uffff\6\1"
          + "\1\uffff\2\1\1\uffff\1\1\3\uffff\2\1\1\uffff\20\1\1\uffff\4"
          + "\1\1\uffff\1\1\1\uffff\1\1\1\uffff\4\1\1\uffff\10\1\1\uffff"
          + "\5\1\1\uffff\7\1\1\uffff\20\1\1\uffff\4\1\1\uffff\12\1\1\uffff"
          + "\5\1\1\uffff\10\1\1\uffff\7\1\1\uffff\2\1\1\uffff\5\1\2\uffff"
          + "\65\1\1\uffff\3\1\1\uffff\5\1\1\uffff\4\1\1\uffff\3\1\1\uffff"
          + "\14\1\1\uffff\1\1\1\uffff\4\1\1\uffff\4\1\1\uffff\3\1\1\uffff" + "\1\1\3\uffff\1\1\3\uffff\1\1", "",
      "\3\1\3\uffff\1\1\3\uffff\2\1\1\uffff\1\u01d6\2\uffff\2\1\1"
          + "\uffff\2\1\1\uffff\27\1\2\uffff\1\1\1\uffff\4\1\1\uffff\6\1"
          + "\1\uffff\2\1\1\uffff\1\1\3\uffff\2\1\1\uffff\20\1\1\uffff\4"
          + "\1\1\uffff\1\1\1\uffff\1\1\1\uffff\4\1\1\uffff\10\1\1\uffff"
          + "\5\1\1\uffff\7\1\1\uffff\20\1\1\uffff\4\1\1\uffff\12\1\1\uffff"
          + "\5\1\1\uffff\10\1\1\uffff\7\1\1\uffff\2\1\1\uffff\5\1\2\uffff"
          + "\65\1\1\uffff\3\1\1\uffff\5\1\1\uffff\4\1\1\uffff\3\1\1\uffff"
          + "\14\1\1\uffff\1\1\1\uffff\4\1\1\uffff\4\1\1\uffff\3\1\1\uffff" + "\1\1\3\uffff\1\1\3\uffff\1\1", "", "",
      "\1\u0203\6\u0204\1\uffff\17\u0204\2\uffff\1\u0204\1\uffff\4"
          + "\u0204\1\uffff\6\u0204\1\uffff\2\u0204\1\uffff\1\u0204\3\uffff"
          + "\2\u0204\1\uffff\20\u0204\1\uffff\4\u0204\1\uffff\1\u0204\1"
          + "\uffff\1\u0204\1\uffff\4\u0204\1\uffff\10\u0204\1\uffff\3\u0204"
          + "\1\uffff\1\u0204\1\uffff\4\u0204\1\uffff\2\u0204\1\uffff\20"
          + "\u0204\1\uffff\4\u0204\1\uffff\12\u0204\2\uffff\4\u0204\1\uffff"
          + "\3\u0204\1\uffff\4\u0204\1\uffff\1\u0204\1\uffff\5\u0204\1\uffff"
          + "\2\u0204\1\uffff\5\u0204\2\uffff\14\u0204\1\uffff\22\u0204\1"
          + "\uffff\25\u0204\1\uffff\3\u0204\1\uffff\5\u0204\1\uffff\4\u0204"
          + "\1\uffff\3\u0204\1\uffff\14\u0204\1\uffff\1\u0204\2\uffff\1"
          + "\u0204\1\uffff\1\u0204\22\uffff\1\33", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "\1\u0206\6\u0207\1\uffff\17\u0207\2\uffff\1\u0207\1\uffff\4"
          + "\u0207\1\uffff\6\u0207\1\uffff\2\u0207\1\uffff\1\u0207\3\uffff"
          + "\2\u0207\1\uffff\20\u0207\1\uffff\4\u0207\1\uffff\1\u0207\1"
          + "\uffff\1\u0207\1\uffff\4\u0207\1\uffff\10\u0207\1\uffff\3\u0207"
          + "\1\uffff\1\u0207\1\uffff\4\u0207\1\uffff\2\u0207\1\uffff\20"
          + "\u0207\1\uffff\4\u0207\1\uffff\12\u0207\2\uffff\4\u0207\1\uffff"
          + "\3\u0207\1\uffff\4\u0207\1\uffff\1\u0207\1\uffff\5\u0207\1\uffff"
          + "\2\u0207\1\uffff\5\u0207\2\uffff\14\u0207\1\uffff\22\u0207\1"
          + "\uffff\25\u0207\1\uffff\3\u0207\1\uffff\5\u0207\1\uffff\4\u0207"
          + "\1\uffff\3\u0207\1\uffff\14\u0207\1\uffff\1\u0207\2\uffff\1"
          + "\u0207\1\uffff\1\u0207\22\uffff\1\33", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "\1\u0209\6\u020a\1\uffff\17\u020a\2\uffff\1\u020a\1\uffff\4"
          + "\u020a\1\uffff\6\u020a\1\uffff\2\u020a\1\uffff\1\u020a\3\uffff"
          + "\2\u020a\1\uffff\20\u020a\1\uffff\4\u020a\1\uffff\1\u020a\1"
          + "\uffff\1\u020a\1\uffff\4\u020a\1\uffff\10\u020a\1\uffff\3\u020a"
          + "\1\uffff\1\u020a\1\uffff\4\u020a\1\uffff\2\u020a\1\uffff\20"
          + "\u020a\1\uffff\4\u020a\1\uffff\12\u020a\2\uffff\4\u020a\1\uffff"
          + "\3\u020a\1\uffff\4\u020a\1\uffff\1\u020a\1\uffff\5\u020a\1\uffff"
          + "\2\u020a\1\uffff\5\u020a\2\uffff\14\u020a\1\uffff\22\u020a\1"
          + "\uffff\25\u020a\1\uffff\3\u020a\1\uffff\5\u020a\1\uffff\4\u020a"
          + "\1\uffff\3\u020a\1\uffff\14\u020a\1\uffff\1\u020a\2\uffff\1"
          + "\u020a\1\uffff\1\u020a\22\uffff\1\33", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "\1\u020c\6\u020d\1\uffff\17\u020d\2\uffff\1\u020d\1\uffff\4"
          + "\u020d\1\uffff\6\u020d\1\uffff\2\u020d\1\uffff\1\u020d\3\uffff"
          + "\2\u020d\1\uffff\20\u020d\1\uffff\4\u020d\1\uffff\1\u020d\1"
          + "\uffff\1\u020d\1\uffff\4\u020d\1\uffff\10\u020d\1\uffff\3\u020d"
          + "\1\uffff\1\u020d\1\uffff\4\u020d\1\uffff\2\u020d\1\uffff\20"
          + "\u020d\1\uffff\4\u020d\1\uffff\12\u020d\2\uffff\4\u020d\1\uffff"
          + "\3\u020d\1\uffff\4\u020d\1\uffff\1\u020d\1\uffff\5\u020d\1\uffff"
          + "\2\u020d\1\uffff\5\u020d\2\uffff\14\u020d\1\uffff\22\u020d\1"
          + "\uffff\25\u020d\1\uffff\3\u020d\1\uffff\5\u020d\1\uffff\4\u020d"
          + "\1\uffff\3\u020d\1\uffff\14\u020d\1\uffff\1\u020d\2\uffff\1"
          + "\u020d\1\uffff\1\u020d\22\uffff\1\33", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "\1\u020f\6\u0210\1\uffff\17\u0210\2\uffff\1\u0210\1\uffff\4"
          + "\u0210\1\uffff\6\u0210\1\uffff\2\u0210\1\uffff\1\u0210\3\uffff"
          + "\2\u0210\1\uffff\20\u0210\1\uffff\4\u0210\1\uffff\1\u0210\1"
          + "\uffff\1\u0210\1\uffff\4\u0210\1\uffff\10\u0210\1\uffff\3\u0210"
          + "\1\uffff\1\u0210\1\uffff\4\u0210\1\uffff\2\u0210\1\uffff\20"
          + "\u0210\1\uffff\4\u0210\1\uffff\12\u0210\2\uffff\4\u0210\1\uffff"
          + "\3\u0210\1\uffff\4\u0210\1\uffff\1\u0210\1\uffff\5\u0210\1\uffff"
          + "\2\u0210\1\uffff\5\u0210\2\uffff\14\u0210\1\uffff\22\u0210\1"
          + "\uffff\25\u0210\1\uffff\3\u0210\1\uffff\5\u0210\1\uffff\4\u0210"
          + "\1\uffff\3\u0210\1\uffff\14\u0210\1\uffff\1\u0210\2\uffff\1"
          + "\u0210\1\uffff\1\u0210\22\uffff\1\33", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "\1\u0212\6\u0213\1\uffff\17\u0213\2\uffff\1\u0213\1\uffff\4"
          + "\u0213\1\uffff\6\u0213\1\uffff\2\u0213\1\uffff\1\u0213\3\uffff"
          + "\2\u0213\1\uffff\20\u0213\1\uffff\4\u0213\1\uffff\1\u0213\1"
          + "\uffff\1\u0213\1\uffff\4\u0213\1\uffff\10\u0213\1\uffff\3\u0213"
          + "\1\uffff\1\u0213\1\uffff\4\u0213\1\uffff\2\u0213\1\uffff\20"
          + "\u0213\1\uffff\4\u0213\1\uffff\12\u0213\2\uffff\4\u0213\1\uffff"
          + "\3\u0213\1\uffff\4\u0213\1\uffff\1\u0213\1\uffff\5\u0213\1\uffff"
          + "\2\u0213\1\uffff\5\u0213\2\uffff\14\u0213\1\uffff\22\u0213\1"
          + "\uffff\25\u0213\1\uffff\3\u0213\1\uffff\5\u0213\1\uffff\4\u0213"
          + "\1\uffff\3\u0213\1\uffff\14\u0213\1\uffff\1\u0213\2\uffff\1"
          + "\u0213\1\uffff\1\u0213\22\uffff\1\33", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "\1\u0215\6\u0216\1\uffff\17\u0216\2\uffff\1\u0216\1\uffff\4"
          + "\u0216\1\uffff\6\u0216\1\uffff\2\u0216\1\uffff\1\u0216\3\uffff"
          + "\2\u0216\1\uffff\20\u0216\1\uffff\4\u0216\1\uffff\1\u0216\1"
          + "\uffff\1\u0216\1\uffff\4\u0216\1\uffff\10\u0216\1\uffff\3\u0216"
          + "\1\uffff\1\u0216\1\uffff\4\u0216\1\uffff\2\u0216\1\uffff\20"
          + "\u0216\1\uffff\4\u0216\1\uffff\12\u0216\2\uffff\4\u0216\1\uffff"
          + "\3\u0216\1\uffff\4\u0216\1\uffff\1\u0216\1\uffff\5\u0216\1\uffff"
          + "\2\u0216\1\uffff\5\u0216\2\uffff\14\u0216\1\uffff\22\u0216\1"
          + "\uffff\25\u0216\1\uffff\3\u0216\1\uffff\5\u0216\1\uffff\4\u0216"
          + "\1\uffff\3\u0216\1\uffff\14\u0216\1\uffff\1\u0216\2\uffff\1"
          + "\u0216\1\uffff\1\u0216\22\uffff\1\33", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "\1\u0218\6\u0219\1\uffff\17\u0219\2\uffff\1\u0219\1\uffff\4"
          + "\u0219\1\uffff\6\u0219\1\uffff\2\u0219\1\uffff\1\u0219\3\uffff"
          + "\2\u0219\1\uffff\20\u0219\1\uffff\4\u0219\1\uffff\1\u0219\1"
          + "\uffff\1\u0219\1\uffff\4\u0219\1\uffff\10\u0219\1\uffff\3\u0219"
          + "\1\uffff\1\u0219\1\uffff\4\u0219\1\uffff\2\u0219\1\uffff\20"
          + "\u0219\1\uffff\4\u0219\1\uffff\12\u0219\2\uffff\4\u0219\1\uffff"
          + "\3\u0219\1\uffff\4\u0219\1\uffff\1\u0219\1\uffff\5\u0219\1\uffff"
          + "\2\u0219\1\uffff\5\u0219\2\uffff\14\u0219\1\uffff\22\u0219\1"
          + "\uffff\25\u0219\1\uffff\3\u0219\1\uffff\5\u0219\1\uffff\4\u0219"
          + "\1\uffff\3\u0219\1\uffff\14\u0219\1\uffff\1\u0219\2\uffff\1"
          + "\u0219\1\uffff\1\u0219\22\uffff\1\33", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "\1\u021b\6\u021c\1\uffff\17\u021c\2\uffff\1\u021c\1\uffff\4"
          + "\u021c\1\uffff\6\u021c\1\uffff\2\u021c\1\uffff\1\u021c\3\uffff"
          + "\2\u021c\1\uffff\20\u021c\1\uffff\4\u021c\1\uffff\1\u021c\1"
          + "\uffff\1\u021c\1\uffff\4\u021c\1\uffff\10\u021c\1\uffff\3\u021c"
          + "\1\uffff\1\u021c\1\uffff\4\u021c\1\uffff\2\u021c\1\uffff\20"
          + "\u021c\1\uffff\4\u021c\1\uffff\12\u021c\2\uffff\4\u021c\1\uffff"
          + "\3\u021c\1\uffff\4\u021c\1\uffff\1\u021c\1\uffff\5\u021c\1\uffff"
          + "\2\u021c\1\uffff\5\u021c\2\uffff\14\u021c\1\uffff\22\u021c\1"
          + "\uffff\25\u021c\1\uffff\3\u021c\1\uffff\5\u021c\1\uffff\4\u021c"
          + "\1\uffff\3\u021c\1\uffff\14\u021c\1\uffff\1\u021c\2\uffff\1"
          + "\u021c\1\uffff\1\u021c\22\uffff\1\33", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "\1\u021e\6\u021f\1\uffff\17\u021f\2\uffff\1\u021f\1\uffff\4"
          + "\u021f\1\uffff\6\u021f\1\uffff\2\u021f\1\uffff\1\u021f\3\uffff"
          + "\2\u021f\1\uffff\20\u021f\1\uffff\4\u021f\1\uffff\1\u021f\1"
          + "\uffff\1\u021f\1\uffff\4\u021f\1\uffff\10\u021f\1\uffff\3\u021f"
          + "\1\uffff\1\u021f\1\uffff\4\u021f\1\uffff\2\u021f\1\uffff\20"
          + "\u021f\1\uffff\4\u021f\1\uffff\12\u021f\2\uffff\4\u021f\1\uffff"
          + "\3\u021f\1\uffff\4\u021f\1\uffff\1\u021f\1\uffff\5\u021f\1\uffff"
          + "\2\u021f\1\uffff\5\u021f\2\uffff\14\u021f\1\uffff\22\u021f\1"
          + "\uffff\25\u021f\1\uffff\3\u021f\1\uffff\5\u021f\1\uffff\4\u021f"
          + "\1\uffff\3\u021f\1\uffff\14\u021f\1\uffff\1\u021f\2\uffff\1"
          + "\u021f\1\uffff\1\u021f\22\uffff\1\33", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "\1\u0221\6\u0222\1\uffff\17\u0222\2\uffff\1\u0222\1\uffff\4"
          + "\u0222\1\uffff\6\u0222\1\uffff\2\u0222\1\uffff\1\u0222\3\uffff"
          + "\2\u0222\1\uffff\20\u0222\1\uffff\4\u0222\1\uffff\1\u0222\1"
          + "\uffff\1\u0222\1\uffff\4\u0222\1\uffff\10\u0222\1\uffff\3\u0222"
          + "\1\uffff\1\u0222\1\uffff\4\u0222\1\uffff\2\u0222\1\uffff\20"
          + "\u0222\1\uffff\4\u0222\1\uffff\12\u0222\2\uffff\4\u0222\1\uffff"
          + "\3\u0222\1\uffff\4\u0222\1\uffff\1\u0222\1\uffff\5\u0222\1\uffff"
          + "\2\u0222\1\uffff\5\u0222\2\uffff\14\u0222\1\uffff\22\u0222\1"
          + "\uffff\25\u0222\1\uffff\3\u0222\1\uffff\5\u0222\1\uffff\4\u0222"
          + "\1\uffff\3\u0222\1\uffff\14\u0222\1\uffff\1\u0222\2\uffff\1"
          + "\u0222\1\uffff\1\u0222\22\uffff\1\33", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};

  static final short[] DFA17_eot = DFA.unpackEncodedString(DFA17_eotS);
  static final short[] DFA17_eof = DFA.unpackEncodedString(DFA17_eofS);
  static final char[] DFA17_min = DFA.unpackEncodedStringToUnsignedChars(DFA17_minS);
  static final char[] DFA17_max = DFA.unpackEncodedStringToUnsignedChars(DFA17_maxS);
  static final short[] DFA17_accept = DFA.unpackEncodedString(DFA17_acceptS);
  static final short[] DFA17_special = DFA.unpackEncodedString(DFA17_specialS);
  static final short[][] DFA17_transition;

  static {
    int numStates = DFA17_transitionS.length;
    DFA17_transition = new short[numStates][];
    for (int i = 0; i < numStates; i++) {
      DFA17_transition[i] = DFA.unpackEncodedString(DFA17_transitionS[i]);
    }
  }

  class DFA17 extends DFA {

    public DFA17(BaseRecognizer recognizer) {
      this.recognizer = recognizer;
      this.decisionNumber = 17;
      this.eot = DFA17_eot;
      this.eof = DFA17_eof;
      this.min = DFA17_min;
      this.max = DFA17_max;
      this.accept = DFA17_accept;
      this.special = DFA17_special;
      this.transition = DFA17_transition;
    }

    public String getDescription() {
      return "124:1: selectItem : ( ( expression ( ( ( KW_AS )? identifier ) | ( KW_AS LPAREN identifier ( COMMA identifier )* RPAREN ) )? ) -> ^( TOK_SELEXPR expression ( identifier )* ) | tableAllColumns -> ^( TOK_SELEXPR tableAllColumns ) );";
    }
  }

  static final String DFA16_eotS = "\u00e2\uffff";
  static final String DFA16_eofS =
      "\1\4\1\2\1\uffff\1\2\4\uffff\1\2\1\uffff\4\2\1\uffff\2\2\1\uffff" + "\1\2\u00cf\uffff";
  static final String DFA16_minS =
      "\2\12\1\uffff\1\12\4\uffff\1\12\1\uffff\4\12\1\uffff\2\12\1\uffff" + "\1\12\u00cf\uffff";
  static final String DFA16_maxS =
      "\2\u012d\1\uffff\1\u012d\4\uffff\1\u012d\1\uffff\4\u012d\1\uffff" + "\2\u012d\1\uffff\1\u012d\u00cf\uffff";
  static final String DFA16_acceptS = "\2\uffff\1\1\1\uffff\1\3\22\uffff\1\2\u00ca\uffff";
  static final String DFA16_specialS = "\u00e2\uffff}>";
  static final String[] DFA16_transitionS = {"\1\4\17\uffff\7\2\1\uffff\2\2\1\1\14\2\2\uffff\1\2\1\uffff\1"
      + "\12\3\2\1\uffff\6\2\1\uffff\2\2\1\uffff\1\2\3\uffff\2\2\1\uffff"
      + "\20\2\1\uffff\1\13\3\2\1\uffff\1\2\1\uffff\1\2\1\uffff\4\2\1"
      + "\uffff\10\2\1\uffff\3\2\1\4\1\2\1\uffff\2\2\1\3\1\2\1\4\2\2"
      + "\1\uffff\11\2\1\22\6\2\1\uffff\2\2\1\20\1\2\1\uffff\1\2\1\15"
      + "\10\2\1\uffff\1\4\4\2\1\uffff\3\2\1\uffff\4\2\1\uffff\1\2\1"
      + "\uffff\1\10\4\2\1\uffff\2\2\1\uffff\5\2\2\uffff\14\2\1\4\22"
      + "\2\1\4\13\2\1\14\11\2\1\uffff\3\2\1\uffff\5\2\1\uffff\4\2\1"
      + "\uffff\1\2\1\17\1\2\1\uffff\14\2\1\uffff\1\2\1\uffff\1\4\1\2" + "\1\4\1\2\16\uffff\1\4",
      "\1\2\17\uffff\7\2\1\uffff\17\2\2\uffff\1\2\1\uffff\4\2\1\uffff"
          + "\6\2\1\uffff\2\2\1\uffff\1\2\3\uffff\2\2\1\uffff\20\2\1\uffff"
          + "\4\2\1\uffff\1\2\1\uffff\1\2\1\uffff\4\2\1\uffff\10\2\1\uffff"
          + "\5\2\1\uffff\7\2\1\uffff\20\2\1\uffff\4\2\1\uffff\12\2\1\uffff"
          + "\5\2\1\uffff\3\2\1\uffff\4\2\1\uffff\1\2\1\uffff\5\2\1\uffff"
          + "\2\2\1\uffff\5\2\2\uffff\65\2\1\uffff\3\2\1\uffff\5\2\1\uffff"
          + "\4\2\1\uffff\3\2\1\uffff\14\2\1\uffff\1\2\1\uffff\4\2\3\uffff" + "\1\27\12\uffff\1\2", "",
      "\1\2\44\uffff\1\4\5\uffff\1\2\46\uffff\1\2\31\uffff\1\2\4\uffff"
          + "\1\2\1\uffff\1\2\14\uffff\1\2\11\uffff\1\2\3\uffff\1\2\11\uffff"
          + "\1\2\20\uffff\1\2\33\uffff\1\2\22\uffff\1\2\13\uffff\1\2\32"
          + "\uffff\1\2\21\uffff\1\2\1\uffff\1\2\17\uffff\1\2", "", "", "", "",
      "\1\2\44\uffff\1\4\5\uffff\1\2\46\uffff\1\2\31\uffff\1\2\4\uffff"
          + "\1\2\1\uffff\1\2\14\uffff\1\2\11\uffff\1\2\3\uffff\1\2\11\uffff"
          + "\1\2\20\uffff\1\2\33\uffff\1\2\22\uffff\1\2\13\uffff\1\2\32"
          + "\uffff\1\2\21\uffff\1\2\1\uffff\1\2\17\uffff\1\2", "",
      "\1\2\44\uffff\1\4\5\uffff\1\2\46\uffff\1\2\31\uffff\1\2\4\uffff"
          + "\1\2\1\uffff\1\2\14\uffff\1\2\11\uffff\1\2\3\uffff\1\2\11\uffff"
          + "\1\2\20\uffff\1\2\33\uffff\1\2\22\uffff\1\2\13\uffff\1\2\32"
          + "\uffff\1\2\21\uffff\1\2\1\uffff\1\2\17\uffff\1\2",
      "\1\2\44\uffff\1\4\5\uffff\1\2\46\uffff\1\2\31\uffff\1\2\4\uffff"
          + "\1\2\1\uffff\1\2\14\uffff\1\2\11\uffff\1\2\3\uffff\1\2\11\uffff"
          + "\1\2\20\uffff\1\2\33\uffff\1\2\22\uffff\1\2\13\uffff\1\2\32"
          + "\uffff\1\2\21\uffff\1\2\1\uffff\1\2\17\uffff\1\2",
      "\1\2\44\uffff\1\4\5\uffff\1\2\46\uffff\1\2\31\uffff\1\2\4\uffff"
          + "\1\2\1\uffff\1\2\14\uffff\1\2\11\uffff\1\2\3\uffff\1\2\11\uffff"
          + "\1\2\20\uffff\1\2\33\uffff\1\2\22\uffff\1\2\13\uffff\1\2\32"
          + "\uffff\1\2\21\uffff\1\2\1\uffff\1\2\17\uffff\1\2",
      "\1\2\52\uffff\1\2\46\uffff\1\2\31\uffff\1\2\4\uffff\1\2\1\uffff"
          + "\1\2\14\uffff\1\2\11\uffff\1\2\3\uffff\1\2\11\uffff\1\2\20\uffff"
          + "\1\2\33\uffff\1\2\22\uffff\1\2\13\uffff\1\2\32\uffff\1\2\21"
          + "\uffff\1\2\1\uffff\1\2\12\uffff\1\4\4\uffff\1\2", "",
      "\1\2\23\uffff\1\4\26\uffff\1\2\46\uffff\1\2\31\uffff\1\2\4"
          + "\uffff\1\2\1\uffff\1\2\14\uffff\1\2\11\uffff\1\2\3\uffff\1\2"
          + "\11\uffff\1\2\20\uffff\1\2\33\uffff\1\2\22\uffff\1\2\13\uffff"
          + "\1\2\32\uffff\1\2\21\uffff\1\2\1\uffff\1\2\17\uffff\1\2",
      "\1\2\52\uffff\1\2\46\uffff\1\2\31\uffff\1\2\4\uffff\1\2\1\uffff"
          + "\1\2\14\uffff\1\2\11\uffff\1\2\3\uffff\1\2\11\uffff\1\2\20\uffff"
          + "\1\2\33\uffff\1\2\22\uffff\1\2\13\uffff\1\2\32\uffff\1\2\17"
          + "\uffff\1\4\1\uffff\1\2\1\uffff\1\2\17\uffff\1\2", "",
      "\1\2\52\uffff\1\2\46\uffff\1\2\31\uffff\1\2\4\uffff\1\2\1\uffff"
          + "\1\2\14\uffff\1\2\2\uffff\1\4\6\uffff\1\2\3\uffff\1\2\11\uffff"
          + "\1\2\20\uffff\1\2\5\uffff\1\4\25\uffff\1\2\22\uffff\1\2\13\uffff"
          + "\1\2\32\uffff\1\2\21\uffff\1\2\1\uffff\1\2\17\uffff\1\2", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};

  static final short[] DFA16_eot = DFA.unpackEncodedString(DFA16_eotS);
  static final short[] DFA16_eof = DFA.unpackEncodedString(DFA16_eofS);
  static final char[] DFA16_min = DFA.unpackEncodedStringToUnsignedChars(DFA16_minS);
  static final char[] DFA16_max = DFA.unpackEncodedStringToUnsignedChars(DFA16_maxS);
  static final short[] DFA16_accept = DFA.unpackEncodedString(DFA16_acceptS);
  static final short[] DFA16_special = DFA.unpackEncodedString(DFA16_specialS);
  static final short[][] DFA16_transition;

  static {
    int numStates = DFA16_transitionS.length;
    DFA16_transition = new short[numStates][];
    for (int i = 0; i < numStates; i++) {
      DFA16_transition[i] = DFA.unpackEncodedString(DFA16_transitionS[i]);
    }
  }

  class DFA16 extends DFA {

    public DFA16(BaseRecognizer recognizer) {
      this.recognizer = recognizer;
      this.decisionNumber = 16;
      this.eot = DFA16_eot;
      this.eof = DFA16_eof;
      this.min = DFA16_min;
      this.max = DFA16_max;
      this.accept = DFA16_accept;
      this.special = DFA16_special;
      this.transition = DFA16_transition;
    }

    public String getDescription() {
      return "129:7: ( ( ( KW_AS )? identifier ) | ( KW_AS LPAREN identifier ( COMMA identifier )* RPAREN ) )?";
    }
  }

  static final String DFA14_eotS = "\u00ce\uffff";
  static final String DFA14_eofS =
      "\1\uffff\1\2\3\uffff\1\4\4\uffff\1\4\1\uffff\4\4\1\uffff\2\4\1\uffff" + "\1\4\u00b9\uffff";
  static final String DFA14_minS =
      "\1\32\1\12\3\uffff\1\12\4\uffff\1\12\1\uffff\4\12\1\uffff\2\12\1" + "\uffff\1\12\u00b9\uffff";
  static final String DFA14_maxS = "\1\u011e\1\u012d\3\uffff\1\u012d\4\uffff\1\u012d\1\uffff\4\u012d"
      + "\1\uffff\2\u012d\1\uffff\1\u012d\u00b9\uffff";
  static final String DFA14_acceptS = "\2\uffff\1\2\1\uffff\1\1\u00c9\uffff";
  static final String DFA14_specialS = "\u00ce\uffff}>";
  static final String[] DFA14_transitionS = {"\7\2\1\uffff\2\2\1\1\14\2\2\uffff\1\2\1\uffff\4\2\1\uffff\6"
      + "\2\1\uffff\2\2\1\uffff\1\2\3\uffff\2\2\1\uffff\20\2\1\uffff"
      + "\4\2\1\uffff\1\2\1\uffff\1\2\1\uffff\4\2\1\uffff\10\2\1\uffff"
      + "\3\2\1\uffff\1\2\1\uffff\4\2\1\uffff\2\2\1\uffff\20\2\1\uffff"
      + "\4\2\1\uffff\12\2\2\uffff\4\2\1\uffff\3\2\1\uffff\4\2\1\uffff"
      + "\1\2\1\uffff\5\2\1\uffff\2\2\1\uffff\5\2\2\uffff\14\2\1\uffff"
      + "\22\2\1\uffff\25\2\1\uffff\3\2\1\uffff\5\2\1\uffff\4\2\1\uffff"
      + "\3\2\1\uffff\14\2\1\uffff\1\2\2\uffff\1\2\1\uffff\1\2",
      "\1\2\17\uffff\7\4\1\uffff\17\4\2\uffff\1\4\1\uffff\1\14\3\4"
          + "\1\uffff\6\4\1\uffff\2\4\1\uffff\1\4\3\uffff\2\4\1\uffff\20"
          + "\4\1\uffff\1\15\3\4\1\uffff\1\4\1\uffff\1\4\1\uffff\4\4\1\uffff"
          + "\10\4\1\uffff\3\4\1\2\1\4\1\uffff\2\4\1\5\1\4\1\2\2\4\1\uffff"
          + "\11\4\1\24\6\4\1\uffff\2\4\1\22\1\4\1\uffff\1\4\1\17\10\4\1"
          + "\uffff\1\2\4\4\1\uffff\3\4\1\uffff\4\4\1\uffff\1\4\1\uffff\1"
          + "\12\4\4\1\uffff\2\4\1\uffff\5\4\2\uffff\14\4\1\2\22\4\1\2\13"
          + "\4\1\16\11\4\1\uffff\3\4\1\uffff\5\4\1\uffff\4\4\1\uffff\1\4"
          + "\1\21\1\4\1\uffff\14\4\1\uffff\1\4\1\uffff\1\2\1\4\1\2\1\4\16" + "\uffff\1\2", "", "", "",
      "\1\4\44\uffff\1\2\5\uffff\1\4\46\uffff\1\4\31\uffff\1\4\4\uffff"
          + "\1\4\1\uffff\1\4\14\uffff\1\4\11\uffff\1\4\3\uffff\1\4\11\uffff"
          + "\1\4\20\uffff\1\4\33\uffff\1\4\22\uffff\1\4\13\uffff\1\4\32"
          + "\uffff\1\4\21\uffff\1\4\1\uffff\1\4\17\uffff\1\4", "", "", "", "",
      "\1\4\44\uffff\1\2\5\uffff\1\4\46\uffff\1\4\31\uffff\1\4\4\uffff"
          + "\1\4\1\uffff\1\4\14\uffff\1\4\11\uffff\1\4\3\uffff\1\4\11\uffff"
          + "\1\4\20\uffff\1\4\33\uffff\1\4\22\uffff\1\4\13\uffff\1\4\32"
          + "\uffff\1\4\21\uffff\1\4\1\uffff\1\4\17\uffff\1\4", "",
      "\1\4\44\uffff\1\2\5\uffff\1\4\46\uffff\1\4\31\uffff\1\4\4\uffff"
          + "\1\4\1\uffff\1\4\14\uffff\1\4\11\uffff\1\4\3\uffff\1\4\11\uffff"
          + "\1\4\20\uffff\1\4\33\uffff\1\4\22\uffff\1\4\13\uffff\1\4\32"
          + "\uffff\1\4\21\uffff\1\4\1\uffff\1\4\17\uffff\1\4",
      "\1\4\44\uffff\1\2\5\uffff\1\4\46\uffff\1\4\31\uffff\1\4\4\uffff"
          + "\1\4\1\uffff\1\4\14\uffff\1\4\11\uffff\1\4\3\uffff\1\4\11\uffff"
          + "\1\4\20\uffff\1\4\33\uffff\1\4\22\uffff\1\4\13\uffff\1\4\32"
          + "\uffff\1\4\21\uffff\1\4\1\uffff\1\4\17\uffff\1\4",
      "\1\4\44\uffff\1\2\5\uffff\1\4\46\uffff\1\4\31\uffff\1\4\4\uffff"
          + "\1\4\1\uffff\1\4\14\uffff\1\4\11\uffff\1\4\3\uffff\1\4\11\uffff"
          + "\1\4\20\uffff\1\4\33\uffff\1\4\22\uffff\1\4\13\uffff\1\4\32"
          + "\uffff\1\4\21\uffff\1\4\1\uffff\1\4\17\uffff\1\4",
      "\1\4\52\uffff\1\4\46\uffff\1\4\31\uffff\1\4\4\uffff\1\4\1\uffff"
          + "\1\4\14\uffff\1\4\11\uffff\1\4\3\uffff\1\4\11\uffff\1\4\20\uffff"
          + "\1\4\33\uffff\1\4\22\uffff\1\4\13\uffff\1\4\32\uffff\1\4\21"
          + "\uffff\1\4\1\uffff\1\4\12\uffff\1\2\4\uffff\1\4", "",
      "\1\4\23\uffff\1\2\26\uffff\1\4\46\uffff\1\4\31\uffff\1\4\4"
          + "\uffff\1\4\1\uffff\1\4\14\uffff\1\4\11\uffff\1\4\3\uffff\1\4"
          + "\11\uffff\1\4\20\uffff\1\4\33\uffff\1\4\22\uffff\1\4\13\uffff"
          + "\1\4\32\uffff\1\4\21\uffff\1\4\1\uffff\1\4\17\uffff\1\4",
      "\1\4\52\uffff\1\4\46\uffff\1\4\31\uffff\1\4\4\uffff\1\4\1\uffff"
          + "\1\4\14\uffff\1\4\11\uffff\1\4\3\uffff\1\4\11\uffff\1\4\20\uffff"
          + "\1\4\33\uffff\1\4\22\uffff\1\4\13\uffff\1\4\32\uffff\1\4\17"
          + "\uffff\1\2\1\uffff\1\4\1\uffff\1\4\17\uffff\1\4", "",
      "\1\4\52\uffff\1\4\46\uffff\1\4\31\uffff\1\4\4\uffff\1\4\1\uffff"
          + "\1\4\14\uffff\1\4\2\uffff\1\2\6\uffff\1\4\3\uffff\1\4\11\uffff"
          + "\1\4\20\uffff\1\4\5\uffff\1\2\25\uffff\1\4\22\uffff\1\4\13\uffff"
          + "\1\4\32\uffff\1\4\21\uffff\1\4\1\uffff\1\4\17\uffff\1\4", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};

  static final short[] DFA14_eot = DFA.unpackEncodedString(DFA14_eotS);
  static final short[] DFA14_eof = DFA.unpackEncodedString(DFA14_eofS);
  static final char[] DFA14_min = DFA.unpackEncodedStringToUnsignedChars(DFA14_minS);
  static final char[] DFA14_max = DFA.unpackEncodedStringToUnsignedChars(DFA14_maxS);
  static final short[] DFA14_accept = DFA.unpackEncodedString(DFA14_acceptS);
  static final short[] DFA14_special = DFA.unpackEncodedString(DFA14_specialS);
  static final short[][] DFA14_transition;

  static {
    int numStates = DFA14_transitionS.length;
    DFA14_transition = new short[numStates][];
    for (int i = 0; i < numStates; i++) {
      DFA14_transition[i] = DFA.unpackEncodedString(DFA14_transitionS[i]);
    }
  }

  class DFA14 extends DFA {

    public DFA14(BaseRecognizer recognizer) {
      this.recognizer = recognizer;
      this.decisionNumber = 14;
      this.eot = DFA14_eot;
      this.eof = DFA14_eof;
      this.min = DFA14_min;
      this.max = DFA14_max;
      this.accept = DFA14_accept;
      this.special = DFA14_special;
      this.transition = DFA14_transition;
    }

    public String getDescription() {
      return "129:9: ( KW_AS )?";
    }
  }

  static final String DFA20_eotS = "\u0089\uffff";
  static final String DFA20_eofS = "\1\uffff\2\3\u0086\uffff";
  static final String DFA20_minS = "\1\32\2\12\23\uffff\1\7\46\uffff\1\7\113\uffff";
  static final String DFA20_maxS = "\1\u011e\2\u012d\23\uffff\1\u0135\46\uffff\1\u0135\113\uffff";
  static final String DFA20_acceptS = "\3\uffff\1\1\24\uffff\1\2\160\uffff";
  static final String DFA20_specialS = "\u0089\uffff}>";
  static final String[] DFA20_transitionS = {"\1\1\6\2\1\uffff\17\2\2\uffff\1\2\1\uffff\4\2\1\uffff\6\2\1"
      + "\uffff\2\2\1\uffff\1\2\3\uffff\2\2\1\uffff\20\2\1\uffff\4\2"
      + "\1\uffff\1\2\1\uffff\1\2\1\uffff\4\2\1\uffff\10\2\1\uffff\3"
      + "\2\1\uffff\1\2\1\uffff\4\2\1\uffff\2\2\1\uffff\20\2\1\uffff"
      + "\4\2\1\uffff\12\2\2\uffff\4\2\1\uffff\3\2\1\uffff\4\2\1\uffff"
      + "\1\2\1\uffff\5\2\1\uffff\2\2\1\uffff\5\2\2\uffff\14\2\1\uffff"
      + "\22\2\1\uffff\25\2\1\uffff\3\2\1\uffff\5\2\1\uffff\4\2\1\uffff"
      + "\3\2\1\uffff\14\2\1\uffff\1\2\2\uffff\1\2\1\uffff\1\2",
      "\1\3\30\uffff\1\30\5\uffff\3\30\10\uffff\1\30\1\3\26\uffff"
          + "\2\30\1\uffff\1\30\14\uffff\1\3\1\30\23\uffff\1\30\4\uffff\1"
          + "\3\4\uffff\1\3\1\uffff\1\3\14\uffff\1\3\1\30\10\uffff\1\3\3"
          + "\uffff\1\3\11\uffff\1\26\20\uffff\1\3\31\uffff\1\3\1\uffff\1"
          + "\3\16\uffff\1\3\3\uffff\1\3\12\uffff\1\30\1\3\5\uffff\2\30\7"
          + "\uffff\2\30\12\uffff\1\3\1\30\15\uffff\1\30\2\uffff\1\3\1\uffff" + "\1\3\17\uffff\1\3",
      "\1\3\30\uffff\1\30\5\uffff\3\30\10\uffff\1\30\1\3\26\uffff"
          + "\2\30\1\uffff\1\30\14\uffff\1\3\1\30\23\uffff\1\30\4\uffff\1"
          + "\3\4\uffff\1\3\1\uffff\1\3\14\uffff\1\3\1\30\10\uffff\1\3\3"
          + "\uffff\1\3\11\uffff\1\75\20\uffff\1\3\31\uffff\1\3\1\uffff\1"
          + "\3\16\uffff\1\3\3\uffff\1\3\12\uffff\1\30\1\3\5\uffff\2\30\7"
          + "\uffff\2\30\12\uffff\1\3\1\30\15\uffff\1\30\2\uffff\1\3\1\uffff"
          + "\1\3\17\uffff\1\3", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "\1\3\5\uffff\1\3\4\uffff\1\3\7\uffff\7\3\1\uffff\22\3\1\uffff"
          + "\4\3\1\uffff\6\3\1\uffff\2\3\1\uffff\1\3\1\uffff\4\3\1\uffff"
          + "\20\3\1\uffff\4\3\1\uffff\1\3\1\uffff\1\3\1\uffff\4\3\1\uffff"
          + "\10\3\1\uffff\3\3\1\uffff\1\3\1\uffff\4\3\1\uffff\23\3\1\uffff"
          + "\4\3\1\uffff\12\3\1\uffff\5\3\1\uffff\10\3\1\uffff\1\3\1\uffff"
          + "\5\3\1\uffff\2\3\1\uffff\5\3\2\uffff\14\3\1\uffff\22\3\1\uffff"
          + "\25\3\1\uffff\3\3\1\uffff\5\3\1\uffff\4\3\1\uffff\3\3\1\uffff"
          + "\14\3\1\uffff\1\3\2\uffff\1\3\1\uffff\1\3\1\uffff\1\30\1\uffff"
          + "\1\3\2\uffff\1\3\2\uffff\2\3\7\uffff\5\3", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "\1\3\5\uffff\1\3\4\uffff\1\3\7\uffff\7\3\1\uffff\22\3\1\uffff"
          + "\4\3\1\uffff\6\3\1\uffff\2\3\1\uffff\1\3\1\uffff\4\3\1\uffff"
          + "\20\3\1\uffff\4\3\1\uffff\1\3\1\uffff\1\3\1\uffff\4\3\1\uffff"
          + "\10\3\1\uffff\3\3\1\uffff\1\3\1\uffff\4\3\1\uffff\23\3\1\uffff"
          + "\4\3\1\uffff\12\3\1\uffff\5\3\1\uffff\10\3\1\uffff\1\3\1\uffff"
          + "\5\3\1\uffff\2\3\1\uffff\5\3\2\uffff\14\3\1\uffff\22\3\1\uffff"
          + "\25\3\1\uffff\3\3\1\uffff\5\3\1\uffff\4\3\1\uffff\3\3\1\uffff"
          + "\14\3\1\uffff\1\3\2\uffff\1\3\1\uffff\1\3\1\uffff\1\30\1\uffff"
          + "\1\3\2\uffff\1\3\2\uffff\2\3\7\uffff\5\3", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};

  static final short[] DFA20_eot = DFA.unpackEncodedString(DFA20_eotS);
  static final short[] DFA20_eof = DFA.unpackEncodedString(DFA20_eofS);
  static final char[] DFA20_min = DFA.unpackEncodedStringToUnsignedChars(DFA20_minS);
  static final char[] DFA20_max = DFA.unpackEncodedStringToUnsignedChars(DFA20_maxS);
  static final short[] DFA20_accept = DFA.unpackEncodedString(DFA20_acceptS);
  static final short[] DFA20_special = DFA.unpackEncodedString(DFA20_specialS);
  static final short[][] DFA20_transition;

  static {
    int numStates = DFA20_transitionS.length;
    DFA20_transition = new short[numStates][];
    for (int i = 0; i < numStates; i++) {
      DFA20_transition[i] = DFA.unpackEncodedString(DFA20_transitionS[i]);
    }
  }

  class DFA20 extends DFA {

    public DFA20(BaseRecognizer recognizer) {
      this.recognizer = recognizer;
      this.decisionNumber = 20;
      this.eot = DFA20_eot;
      this.eof = DFA20_eof;
      this.min = DFA20_min;
      this.max = DFA20_max;
      this.accept = DFA20_accept;
      this.special = DFA20_special;
      this.transition = DFA20_transition;
    }

    public String getDescription() {
      return "142:65: ( aliasList | columnNameTypeList )";
    }
  }

  static final String DFA23_eotS = "\u0173\uffff";
  static final String DFA23_eofS = "\u0173\uffff";
  static final String DFA23_minS = "\1\7\2\uffff\1\4\1\uffff\2\4\6\uffff\2\4\4\uffff\1\4\1\uffff\4\4"
      + "\1\uffff\1\4\2\uffff\1\32\33\uffff\1\32\34\uffff\1\32\34\uffff\1"
      + "\32\33\uffff\1\32\33\uffff\1\32\33\uffff\1\32\33\uffff\1\32\32\uffff"
      + "\1\32\33\uffff\1\32\33\uffff\1\32\74\uffff";
  static final String DFA23_maxS = "\1\u0135\2\uffff\1\u0131\1\uffff\2\u0133\6\uffff\2\u0131\4\uffff"
      + "\1\u0131\1\uffff\4\u0131\1\uffff\1\u0131\2\uffff\1\u0131\33\uffff"
      + "\1\u0131\34\uffff\1\u0131\34\uffff\1\u0131\33\uffff\1\u0131\33\uffff"
      + "\1\u0131\33\uffff\1\u0131\33\uffff\1\u0131\32\uffff\1\u0131\33\uffff" + "\1\u0131\33\uffff\1\u0131\74\uffff";
  static final String DFA23_acceptS = "\1\uffff\1\1\31\uffff\1\2\u0137\uffff\2\1\1\uffff\2\1\1\uffff\2"
      + "\1\1\uffff\2\1\1\uffff\2\1\1\uffff\2\1\1\uffff\2\1\1\uffff\2\1\1" + "\uffff\2\1\1\uffff\2\1\1\uffff\2\1";
  static final String DFA23_specialS = "\u0173\uffff}>";
  static final String[] DFA23_transitionS = {"\1\1\5\uffff\1\1\4\uffff\1\1\7\uffff\1\27\6\32\1\uffff\1\32"
      + "\1\23\15\32\2\1\1\32\1\uffff\4\32\1\uffff\6\32\1\uffff\2\32"
      + "\1\uffff\1\32\1\uffff\2\1\2\32\1\uffff\1\32\1\5\16\32\1\uffff"
      + "\4\32\1\uffff\1\32\1\uffff\1\32\1\uffff\1\32\1\30\2\32\1\uffff"
      + "\1\32\1\16\6\32\1\uffff\3\32\1\uffff\1\32\1\uffff\4\32\1\uffff"
      + "\2\32\1\1\20\32\1\uffff\4\32\1\uffff\12\32\1\uffff\1\1\4\32"
      + "\1\uffff\3\32\1\1\1\32\1\3\2\32\1\uffff\1\32\1\uffff\5\32\1"
      + "\uffff\2\32\1\uffff\5\32\2\uffff\14\32\1\uffff\22\32\1\uffff"
      + "\22\32\1\25\2\32\1\uffff\3\32\1\uffff\1\6\4\32\1\uffff\1\32"
      + "\1\15\2\32\1\uffff\2\32\1\26\1\uffff\14\32\1\uffff\1\32\2\uffff"
      + "\1\32\1\uffff\1\32\3\uffff\1\1\2\uffff\1\1\2\uffff\2\1\7\uffff" + "\1\33\4\1", "", "",
      "\3\1\3\uffff\1\1\3\uffff\2\1\1\uffff\1\35\2\uffff\2\1\1\uffff"
          + "\2\1\10\uffff\1\1\6\uffff\1\1\132\uffff\1\1\12\uffff\1\1\10"
          + "\uffff\1\1\23\uffff\1\1\6\uffff\1\1\33\uffff\1\1\1\uffff\1\1"
          + "\11\uffff\1\1\3\uffff\1\1\64\uffff\1\1\14\uffff\4\1\1\uffff"
          + "\3\1\1\uffff\1\1\3\uffff\1\1\3\uffff\1\1", "",
      "\3\1\3\uffff\1\1\3\uffff\2\1\1\uffff\1\71\2\uffff\2\1\1\uffff"
          + "\2\1\10\uffff\1\1\6\uffff\1\1\132\uffff\1\1\12\uffff\1\1\10"
          + "\uffff\1\1\23\uffff\1\1\6\uffff\1\1\33\uffff\1\1\1\uffff\1\1"
          + "\11\uffff\1\1\3\uffff\1\1\64\uffff\1\1\14\uffff\4\1\1\uffff"
          + "\3\1\1\uffff\1\1\3\uffff\1\1\3\uffff\1\1\1\uffff\1\1",
      "\3\1\3\uffff\1\1\3\uffff\2\1\1\uffff\1\126\2\uffff\2\1\1\uffff"
          + "\2\1\10\uffff\1\1\6\uffff\1\1\132\uffff\1\1\12\uffff\1\1\10"
          + "\uffff\1\1\23\uffff\1\1\6\uffff\1\1\33\uffff\1\1\1\uffff\1\1"
          + "\11\uffff\1\1\3\uffff\1\1\64\uffff\1\1\14\uffff\4\1\1\uffff"
          + "\3\1\1\uffff\1\1\3\uffff\1\1\3\uffff\1\1\1\uffff\1\1", "", "", "", "", "", "",
      "\3\1\3\uffff\1\1\3\uffff\2\1\1\uffff\1\163\2\uffff\2\1\1\uffff"
          + "\2\1\10\uffff\1\1\6\uffff\1\1\132\uffff\1\1\12\uffff\1\1\10"
          + "\uffff\1\1\23\uffff\1\1\6\uffff\1\1\33\uffff\1\1\1\uffff\1\1"
          + "\11\uffff\1\1\3\uffff\1\1\64\uffff\1\1\14\uffff\4\1\1\uffff" + "\3\1\1\uffff\1\1\3\uffff\1\1\3\uffff\1\1",
      "\3\1\3\uffff\1\1\3\uffff\2\1\1\uffff\1\u008f\2\uffff\2\1\1"
          + "\uffff\2\1\10\uffff\1\1\6\uffff\1\1\132\uffff\1\1\12\uffff\1"
          + "\1\10\uffff\1\1\23\uffff\1\1\6\uffff\1\1\33\uffff\1\1\1\uffff"
          + "\1\1\11\uffff\1\1\3\uffff\1\1\64\uffff\1\1\14\uffff\4\1\1\uffff"
          + "\3\1\1\uffff\1\1\3\uffff\1\1\3\uffff\1\1", "", "", "", "",
      "\3\1\3\uffff\1\1\3\uffff\2\1\1\uffff\1\u00ab\2\uffff\2\1\1"
          + "\uffff\2\1\10\uffff\1\1\6\uffff\1\1\132\uffff\1\1\12\uffff\1"
          + "\1\10\uffff\1\1\23\uffff\1\1\6\uffff\1\1\33\uffff\1\1\1\uffff"
          + "\1\1\11\uffff\1\1\3\uffff\1\1\64\uffff\1\1\14\uffff\4\1\1\uffff"
          + "\3\1\1\uffff\1\1\3\uffff\1\1\3\uffff\1\1", "", "\3\1\3\uffff\1\1\3\uffff\2\1\1\uffff\1\u00c7\2\uffff\2\1\1"
      + "\uffff\2\1\10\uffff\1\1\6\uffff\1\1\132\uffff\1\1\12\uffff\1"
      + "\1\10\uffff\1\1\23\uffff\1\1\6\uffff\1\1\33\uffff\1\1\1\uffff"
      + "\1\1\11\uffff\1\1\3\uffff\1\1\64\uffff\1\1\14\uffff\4\1\1\uffff" + "\3\1\1\uffff\1\1\3\uffff\1\1\3\uffff\1\1",
      "\3\1\3\uffff\1\1\3\uffff\2\1\1\uffff\1\u00e3\2\uffff\2\1\1"
          + "\uffff\2\1\10\uffff\1\1\6\uffff\1\1\132\uffff\1\1\12\uffff\1"
          + "\1\10\uffff\1\1\23\uffff\1\1\6\uffff\1\1\33\uffff\1\1\1\uffff"
          + "\1\1\11\uffff\1\1\3\uffff\1\1\64\uffff\1\1\14\uffff\4\1\1\uffff"
          + "\3\1\1\uffff\1\1\3\uffff\1\1\3\uffff\1\1", "\3\1\3\uffff\1\1\3\uffff\2\1\1\uffff\1\u00fe\2\uffff\2\1\1"
      + "\uffff\2\1\10\uffff\1\1\6\uffff\1\1\132\uffff\1\1\12\uffff\1"
      + "\1\10\uffff\1\1\23\uffff\1\1\6\uffff\1\1\33\uffff\1\1\1\uffff"
      + "\1\1\11\uffff\1\1\3\uffff\1\1\64\uffff\1\1\14\uffff\4\1\1\uffff" + "\3\1\1\uffff\1\1\3\uffff\1\1\3\uffff\1\1",
      "\3\1\3\uffff\1\1\3\uffff\2\1\1\uffff\1\u011a\2\uffff\2\1\1"
          + "\uffff\2\1\10\uffff\1\1\6\uffff\1\1\132\uffff\1\1\12\uffff\1"
          + "\1\10\uffff\1\1\23\uffff\1\1\6\uffff\1\1\33\uffff\1\1\1\uffff"
          + "\1\1\11\uffff\1\1\3\uffff\1\1\64\uffff\1\1\14\uffff\4\1\1\uffff"
          + "\3\1\1\uffff\1\1\3\uffff\1\1\3\uffff\1\1", "", "\3\1\3\uffff\1\1\3\uffff\2\1\1\uffff\1\u0136\2\uffff\2\1\1"
      + "\uffff\2\1\10\uffff\1\1\6\uffff\1\1\132\uffff\1\1\12\uffff\1"
      + "\1\10\uffff\1\1\23\uffff\1\1\6\uffff\1\1\33\uffff\1\1\1\uffff"
      + "\1\1\11\uffff\1\1\3\uffff\1\1\64\uffff\1\1\14\uffff\4\1\1\uffff"
      + "\3\1\1\uffff\1\1\3\uffff\1\1\3\uffff\1\1", "", "",
      "\1\u0153\6\u0154\1\uffff\17\u0154\2\uffff\1\u0154\1\uffff\4"
          + "\u0154\1\uffff\6\u0154\1\uffff\2\u0154\1\uffff\1\u0154\3\uffff"
          + "\2\u0154\1\uffff\20\u0154\1\uffff\4\u0154\1\uffff\1\u0154\1"
          + "\uffff\1\u0154\1\uffff\4\u0154\1\uffff\10\u0154\1\uffff\3\u0154"
          + "\1\uffff\1\u0154\1\uffff\4\u0154\1\uffff\2\u0154\1\uffff\20"
          + "\u0154\1\uffff\4\u0154\1\uffff\12\u0154\2\uffff\4\u0154\1\uffff"
          + "\3\u0154\1\uffff\4\u0154\1\uffff\1\u0154\1\uffff\5\u0154\1\uffff"
          + "\2\u0154\1\uffff\5\u0154\2\uffff\14\u0154\1\uffff\22\u0154\1"
          + "\uffff\25\u0154\1\uffff\3\u0154\1\uffff\5\u0154\1\uffff\4\u0154"
          + "\1\uffff\3\u0154\1\uffff\14\u0154\1\uffff\1\u0154\2\uffff\1"
          + "\u0154\1\uffff\1\u0154\22\uffff\1\33", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "\1\u0156\6\u0157\1\uffff\17\u0157\2\uffff\1\u0157\1\uffff\4"
          + "\u0157\1\uffff\6\u0157\1\uffff\2\u0157\1\uffff\1\u0157\3\uffff"
          + "\2\u0157\1\uffff\20\u0157\1\uffff\4\u0157\1\uffff\1\u0157\1"
          + "\uffff\1\u0157\1\uffff\4\u0157\1\uffff\10\u0157\1\uffff\3\u0157"
          + "\1\uffff\1\u0157\1\uffff\4\u0157\1\uffff\2\u0157\1\uffff\20"
          + "\u0157\1\uffff\4\u0157\1\uffff\12\u0157\2\uffff\4\u0157\1\uffff"
          + "\3\u0157\1\uffff\4\u0157\1\uffff\1\u0157\1\uffff\5\u0157\1\uffff"
          + "\2\u0157\1\uffff\5\u0157\2\uffff\14\u0157\1\uffff\22\u0157\1"
          + "\uffff\25\u0157\1\uffff\3\u0157\1\uffff\5\u0157\1\uffff\4\u0157"
          + "\1\uffff\3\u0157\1\uffff\14\u0157\1\uffff\1\u0157\2\uffff\1"
          + "\u0157\1\uffff\1\u0157\22\uffff\1\33", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "\1\u0159\6\u015a\1\uffff\17\u015a\2\uffff\1\u015a\1\uffff\4"
          + "\u015a\1\uffff\6\u015a\1\uffff\2\u015a\1\uffff\1\u015a\3\uffff"
          + "\2\u015a\1\uffff\20\u015a\1\uffff\4\u015a\1\uffff\1\u015a\1"
          + "\uffff\1\u015a\1\uffff\4\u015a\1\uffff\10\u015a\1\uffff\3\u015a"
          + "\1\uffff\1\u015a\1\uffff\4\u015a\1\uffff\2\u015a\1\uffff\20"
          + "\u015a\1\uffff\4\u015a\1\uffff\12\u015a\2\uffff\4\u015a\1\uffff"
          + "\3\u015a\1\uffff\4\u015a\1\uffff\1\u015a\1\uffff\5\u015a\1\uffff"
          + "\2\u015a\1\uffff\5\u015a\2\uffff\14\u015a\1\uffff\22\u015a\1"
          + "\uffff\25\u015a\1\uffff\3\u015a\1\uffff\5\u015a\1\uffff\4\u015a"
          + "\1\uffff\3\u015a\1\uffff\14\u015a\1\uffff\1\u015a\2\uffff\1"
          + "\u015a\1\uffff\1\u015a\22\uffff\1\33", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "\1\u015c\6\u015d\1\uffff\17\u015d\2\uffff\1\u015d\1\uffff\4"
          + "\u015d\1\uffff\6\u015d\1\uffff\2\u015d\1\uffff\1\u015d\3\uffff"
          + "\2\u015d\1\uffff\20\u015d\1\uffff\4\u015d\1\uffff\1\u015d\1"
          + "\uffff\1\u015d\1\uffff\4\u015d\1\uffff\10\u015d\1\uffff\3\u015d"
          + "\1\uffff\1\u015d\1\uffff\4\u015d\1\uffff\2\u015d\1\uffff\20"
          + "\u015d\1\uffff\4\u015d\1\uffff\12\u015d\2\uffff\4\u015d\1\uffff"
          + "\3\u015d\1\uffff\4\u015d\1\uffff\1\u015d\1\uffff\5\u015d\1\uffff"
          + "\2\u015d\1\uffff\5\u015d\2\uffff\14\u015d\1\uffff\22\u015d\1"
          + "\uffff\25\u015d\1\uffff\3\u015d\1\uffff\5\u015d\1\uffff\4\u015d"
          + "\1\uffff\3\u015d\1\uffff\14\u015d\1\uffff\1\u015d\2\uffff\1"
          + "\u015d\1\uffff\1\u015d\22\uffff\1\33", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "\1\u015f\6\u0160\1\uffff\17\u0160\2\uffff\1\u0160\1\uffff\4"
          + "\u0160\1\uffff\6\u0160\1\uffff\2\u0160\1\uffff\1\u0160\3\uffff"
          + "\2\u0160\1\uffff\20\u0160\1\uffff\4\u0160\1\uffff\1\u0160\1"
          + "\uffff\1\u0160\1\uffff\4\u0160\1\uffff\10\u0160\1\uffff\3\u0160"
          + "\1\uffff\1\u0160\1\uffff\4\u0160\1\uffff\2\u0160\1\uffff\20"
          + "\u0160\1\uffff\4\u0160\1\uffff\12\u0160\2\uffff\4\u0160\1\uffff"
          + "\3\u0160\1\uffff\4\u0160\1\uffff\1\u0160\1\uffff\5\u0160\1\uffff"
          + "\2\u0160\1\uffff\5\u0160\2\uffff\14\u0160\1\uffff\22\u0160\1"
          + "\uffff\25\u0160\1\uffff\3\u0160\1\uffff\5\u0160\1\uffff\4\u0160"
          + "\1\uffff\3\u0160\1\uffff\14\u0160\1\uffff\1\u0160\2\uffff\1"
          + "\u0160\1\uffff\1\u0160\22\uffff\1\33", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "\1\u0162\6\u0163\1\uffff\17\u0163\2\uffff\1\u0163\1\uffff\4"
          + "\u0163\1\uffff\6\u0163\1\uffff\2\u0163\1\uffff\1\u0163\3\uffff"
          + "\2\u0163\1\uffff\20\u0163\1\uffff\4\u0163\1\uffff\1\u0163\1"
          + "\uffff\1\u0163\1\uffff\4\u0163\1\uffff\10\u0163\1\uffff\3\u0163"
          + "\1\uffff\1\u0163\1\uffff\4\u0163\1\uffff\2\u0163\1\uffff\20"
          + "\u0163\1\uffff\4\u0163\1\uffff\12\u0163\2\uffff\4\u0163\1\uffff"
          + "\3\u0163\1\uffff\4\u0163\1\uffff\1\u0163\1\uffff\5\u0163\1\uffff"
          + "\2\u0163\1\uffff\5\u0163\2\uffff\14\u0163\1\uffff\22\u0163\1"
          + "\uffff\25\u0163\1\uffff\3\u0163\1\uffff\5\u0163\1\uffff\4\u0163"
          + "\1\uffff\3\u0163\1\uffff\14\u0163\1\uffff\1\u0163\2\uffff\1"
          + "\u0163\1\uffff\1\u0163\22\uffff\1\33", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "\1\u0165\6\u0166\1\uffff\17\u0166\2\uffff\1\u0166\1\uffff\4"
          + "\u0166\1\uffff\6\u0166\1\uffff\2\u0166\1\uffff\1\u0166\3\uffff"
          + "\2\u0166\1\uffff\20\u0166\1\uffff\4\u0166\1\uffff\1\u0166\1"
          + "\uffff\1\u0166\1\uffff\4\u0166\1\uffff\10\u0166\1\uffff\3\u0166"
          + "\1\uffff\1\u0166\1\uffff\4\u0166\1\uffff\2\u0166\1\uffff\20"
          + "\u0166\1\uffff\4\u0166\1\uffff\12\u0166\2\uffff\4\u0166\1\uffff"
          + "\3\u0166\1\uffff\4\u0166\1\uffff\1\u0166\1\uffff\5\u0166\1\uffff"
          + "\2\u0166\1\uffff\5\u0166\2\uffff\14\u0166\1\uffff\22\u0166\1"
          + "\uffff\25\u0166\1\uffff\3\u0166\1\uffff\5\u0166\1\uffff\4\u0166"
          + "\1\uffff\3\u0166\1\uffff\14\u0166\1\uffff\1\u0166\2\uffff\1"
          + "\u0166\1\uffff\1\u0166\22\uffff\1\33", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "\1\u0168\6\u0169\1\uffff\17\u0169\2\uffff\1\u0169\1\uffff\4"
          + "\u0169\1\uffff\6\u0169\1\uffff\2\u0169\1\uffff\1\u0169\3\uffff"
          + "\2\u0169\1\uffff\20\u0169\1\uffff\4\u0169\1\uffff\1\u0169\1"
          + "\uffff\1\u0169\1\uffff\4\u0169\1\uffff\10\u0169\1\uffff\3\u0169"
          + "\1\uffff\1\u0169\1\uffff\4\u0169\1\uffff\2\u0169\1\uffff\20"
          + "\u0169\1\uffff\4\u0169\1\uffff\12\u0169\2\uffff\4\u0169\1\uffff"
          + "\3\u0169\1\uffff\4\u0169\1\uffff\1\u0169\1\uffff\5\u0169\1\uffff"
          + "\2\u0169\1\uffff\5\u0169\2\uffff\14\u0169\1\uffff\22\u0169\1"
          + "\uffff\25\u0169\1\uffff\3\u0169\1\uffff\5\u0169\1\uffff\4\u0169"
          + "\1\uffff\3\u0169\1\uffff\14\u0169\1\uffff\1\u0169\2\uffff\1"
          + "\u0169\1\uffff\1\u0169\22\uffff\1\33", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "\1\u016b\6\u016c\1\uffff\17\u016c\2\uffff\1\u016c\1\uffff\4"
          + "\u016c\1\uffff\6\u016c\1\uffff\2\u016c\1\uffff\1\u016c\3\uffff"
          + "\2\u016c\1\uffff\20\u016c\1\uffff\4\u016c\1\uffff\1\u016c\1"
          + "\uffff\1\u016c\1\uffff\4\u016c\1\uffff\10\u016c\1\uffff\3\u016c"
          + "\1\uffff\1\u016c\1\uffff\4\u016c\1\uffff\2\u016c\1\uffff\20"
          + "\u016c\1\uffff\4\u016c\1\uffff\12\u016c\2\uffff\4\u016c\1\uffff"
          + "\3\u016c\1\uffff\4\u016c\1\uffff\1\u016c\1\uffff\5\u016c\1\uffff"
          + "\2\u016c\1\uffff\5\u016c\2\uffff\14\u016c\1\uffff\22\u016c\1"
          + "\uffff\25\u016c\1\uffff\3\u016c\1\uffff\5\u016c\1\uffff\4\u016c"
          + "\1\uffff\3\u016c\1\uffff\14\u016c\1\uffff\1\u016c\2\uffff\1"
          + "\u016c\1\uffff\1\u016c\22\uffff\1\33", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "\1\u016e\6\u016f\1\uffff\17\u016f\2\uffff\1\u016f\1\uffff\4"
          + "\u016f\1\uffff\6\u016f\1\uffff\2\u016f\1\uffff\1\u016f\3\uffff"
          + "\2\u016f\1\uffff\20\u016f\1\uffff\4\u016f\1\uffff\1\u016f\1"
          + "\uffff\1\u016f\1\uffff\4\u016f\1\uffff\10\u016f\1\uffff\3\u016f"
          + "\1\uffff\1\u016f\1\uffff\4\u016f\1\uffff\2\u016f\1\uffff\20"
          + "\u016f\1\uffff\4\u016f\1\uffff\12\u016f\2\uffff\4\u016f\1\uffff"
          + "\3\u016f\1\uffff\4\u016f\1\uffff\1\u016f\1\uffff\5\u016f\1\uffff"
          + "\2\u016f\1\uffff\5\u016f\2\uffff\14\u016f\1\uffff\22\u016f\1"
          + "\uffff\25\u016f\1\uffff\3\u016f\1\uffff\5\u016f\1\uffff\4\u016f"
          + "\1\uffff\3\u016f\1\uffff\14\u016f\1\uffff\1\u016f\2\uffff\1"
          + "\u016f\1\uffff\1\u016f\22\uffff\1\33", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "\1\u0171\6\u0172\1\uffff\17\u0172\2\uffff\1\u0172\1\uffff\4"
          + "\u0172\1\uffff\6\u0172\1\uffff\2\u0172\1\uffff\1\u0172\3\uffff"
          + "\2\u0172\1\uffff\20\u0172\1\uffff\4\u0172\1\uffff\1\u0172\1"
          + "\uffff\1\u0172\1\uffff\4\u0172\1\uffff\10\u0172\1\uffff\3\u0172"
          + "\1\uffff\1\u0172\1\uffff\4\u0172\1\uffff\2\u0172\1\uffff\20"
          + "\u0172\1\uffff\4\u0172\1\uffff\12\u0172\2\uffff\4\u0172\1\uffff"
          + "\3\u0172\1\uffff\4\u0172\1\uffff\1\u0172\1\uffff\5\u0172\1\uffff"
          + "\2\u0172\1\uffff\5\u0172\2\uffff\14\u0172\1\uffff\22\u0172\1"
          + "\uffff\25\u0172\1\uffff\3\u0172\1\uffff\5\u0172\1\uffff\4\u0172"
          + "\1\uffff\3\u0172\1\uffff\14\u0172\1\uffff\1\u0172\2\uffff\1"
          + "\u0172\1\uffff\1\u0172\22\uffff\1\33", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};

  static final short[] DFA23_eot = DFA.unpackEncodedString(DFA23_eotS);
  static final short[] DFA23_eof = DFA.unpackEncodedString(DFA23_eofS);
  static final char[] DFA23_min = DFA.unpackEncodedStringToUnsignedChars(DFA23_minS);
  static final char[] DFA23_max = DFA.unpackEncodedStringToUnsignedChars(DFA23_maxS);
  static final short[] DFA23_accept = DFA.unpackEncodedString(DFA23_acceptS);
  static final short[] DFA23_special = DFA.unpackEncodedString(DFA23_specialS);
  static final short[][] DFA23_transition;

  static {
    int numStates = DFA23_transitionS.length;
    DFA23_transition = new short[numStates][];
    for (int i = 0; i < numStates; i++) {
      DFA23_transition[i] = DFA.unpackEncodedString(DFA23_transitionS[i]);
    }
  }

  class DFA23 extends DFA {

    public DFA23(BaseRecognizer recognizer) {
      this.recognizer = recognizer;
      this.decisionNumber = 23;
      this.eot = DFA23_eot;
      this.eof = DFA23_eof;
      this.min = DFA23_min;
      this.max = DFA23_max;
      this.accept = DFA23_accept;
      this.special = DFA23_special;
      this.transition = DFA23_transition;
    }

    public String getDescription() {
      return "147:1: selectExpression : ( expression | tableAllColumns );";
    }
  }

  public static final BitSet FOLLOW_KW_SELECT_in_selectClause71 = new BitSet(
      new long[]{0xFDEFFFFDFC04A080L, 0xDEBBFDEAFFFFFBD6L, 0xF6FAFF7DFFBDFFFFL, 0xEEFFFFFBFFFF7FF9L, 0x003E032452FFF77FL});
  public static final BitSet FOLLOW_hintClause_in_selectClause73 = new BitSet(
      new long[]{0xFDEFFFFDFC042080L, 0xDEBBFDEAFFFFFBD6L, 0xF6FAFF7DFFBDFFFFL, 0xEEFFFFFBFFFF7FF9L, 0x003E032452FFF77FL});
  public static final BitSet FOLLOW_KW_ALL_in_selectClause79 = new BitSet(
      new long[]{0xFDEFFFFDFC042080L, 0xDEBBFDEAF7FFFBD6L, 0xF6FAFF7DFFBDFFFFL, 0xEEFFFFFBFFFF7FF9L, 0x003E032452FFF77BL});
  public static final BitSet FOLLOW_KW_DISTINCT_in_selectClause85 = new BitSet(
      new long[]{0xFDEFFFFDFC042080L, 0xDEBBFDEAF7FFFBD6L, 0xF6FAFF7DFFBDFFFFL, 0xEEFFFFFBFFFF7FF9L, 0x003E032452FFF77BL});
  public static final BitSet FOLLOW_selectList_in_selectClause89 = new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_KW_TRANSFORM_in_selectClause123 = new BitSet(
      new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000400000000L});
  public static final BitSet FOLLOW_selectTrfmClause_in_selectClause125 = new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_trfmClause_in_selectClause196 = new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_selectItem_in_selectList239 = new BitSet(new long[]{0x0000000000000402L});
  public static final BitSet FOLLOW_COMMA_in_selectList243 = new BitSet(
      new long[]{0xFDEFFFFDFC042080L, 0xDEBBFDEAF7FFFBD6L, 0xF6FAFF7DFFBDFFFFL, 0xEEFFFFFBFFFF7FF9L, 0x003E032452FFF77BL});
  public static final BitSet FOLLOW_selectItem_in_selectList246 = new BitSet(new long[]{0x0000000000000402L});
  public static final BitSet FOLLOW_LPAREN_in_selectTrfmClause285 = new BitSet(
      new long[]{0xFDEFFFFDFC042080L, 0xDEBBFDEAF7FFFBD6L, 0xF6FAFF7DFFBDFFFFL, 0xEEFFFFFBFFFF7FF9L, 0x003E032452FFF77BL});
  public static final BitSet FOLLOW_selectExpressionList_in_selectTrfmClause287 = new BitSet(
      new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000200000000000L});
  public static final BitSet FOLLOW_RPAREN_in_selectTrfmClause289 = new BitSet(
      new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000040004000L, 0x0000000000080000L});
  public static final BitSet FOLLOW_rowFormat_in_selectTrfmClause297 = new BitSet(
      new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000004000L, 0x0000000000080000L});
  public static final BitSet FOLLOW_recordWriter_in_selectTrfmClause301 = new BitSet(
      new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000080000L});
  public static final BitSet FOLLOW_KW_USING_in_selectTrfmClause307 = new BitSet(
      new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0008000000000000L});
  public static final BitSet FOLLOW_StringLiteral_in_selectTrfmClause309 =
      new BitSet(new long[]{0x0000001000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000040002000L});
  public static final BitSet FOLLOW_KW_AS_in_selectTrfmClause317 = new BitSet(
      new long[]{0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFB16L, 0xF6FAF779FFBDFFFEL, 0xEEFFFFFBFFFF7FF9L, 0x0000000452FFF77BL});
  public static final BitSet FOLLOW_LPAREN_in_selectTrfmClause321 = new BitSet(
      new long[]{0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFB16L, 0xF6FAF779FFBDFFFEL, 0xEEFFFFFBFFFF7FF9L, 0x0000000052FFF77BL});
  public static final BitSet FOLLOW_aliasList_in_selectTrfmClause324 = new BitSet(
      new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000200000000000L});
  public static final BitSet FOLLOW_columnNameTypeList_in_selectTrfmClause328 = new BitSet(
      new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000200000000000L});
  public static final BitSet FOLLOW_RPAREN_in_selectTrfmClause331 =
      new BitSet(new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000040002000L});
  public static final BitSet FOLLOW_aliasList_in_selectTrfmClause337 =
      new BitSet(new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000040002000L});
  public static final BitSet FOLLOW_columnNameTypeList_in_selectTrfmClause341 =
      new BitSet(new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000040002000L});
  public static final BitSet FOLLOW_rowFormat_in_selectTrfmClause353 =
      new BitSet(new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000002000L});
  public static final BitSet FOLLOW_recordReader_in_selectTrfmClause357 = new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_DIVIDE_in_hintClause420 = new BitSet(
      new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0002000000000000L});
  public static final BitSet FOLLOW_STAR_in_hintClause422 = new BitSet(
      new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000020000000000L});
  public static final BitSet FOLLOW_PLUS_in_hintClause424 =
      new BitSet(new long[]{0x0000000000000000L, 0x4000000000000000L, 0x0000000800000000L, 0x0008000000000000L});
  public static final BitSet FOLLOW_hintList_in_hintClause426 = new BitSet(
      new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0002000000000000L});
  public static final BitSet FOLLOW_STAR_in_hintClause428 = new BitSet(new long[]{0x0000000000008000L});
  public static final BitSet FOLLOW_DIVIDE_in_hintClause430 = new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_hintItem_in_hintList469 = new BitSet(new long[]{0x0000000000000402L});
  public static final BitSet FOLLOW_COMMA_in_hintList472 =
      new BitSet(new long[]{0x0000000000000000L, 0x4000000000000000L, 0x0000000800000000L, 0x0008000000000000L});
  public static final BitSet FOLLOW_hintItem_in_hintList474 = new BitSet(new long[]{0x0000000000000402L});
  public static final BitSet FOLLOW_hintName_in_hintItem512 = new BitSet(
      new long[]{0x0000000000000002L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000400000000L});
  public static final BitSet FOLLOW_LPAREN_in_hintItem515 = new BitSet(
      new long[]{0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFB16L, 0xF6FAF779FFBDFFFEL, 0xEEFFFFFBFFFF7FF9L, 0x0000000052FFF77BL});
  public static final BitSet FOLLOW_hintArgs_in_hintItem517 = new BitSet(
      new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000200000000000L});
  public static final BitSet FOLLOW_RPAREN_in_hintItem519 = new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_KW_MAPJOIN_in_hintName563 = new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_KW_STREAMTABLE_in_hintName575 = new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_KW_HOLD_DDLTIME_in_hintName587 = new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_hintArgName_in_hintArgs622 = new BitSet(new long[]{0x0000000000000402L});
  public static final BitSet FOLLOW_COMMA_in_hintArgs625 = new BitSet(
      new long[]{0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFB16L, 0xF6FAF779FFBDFFFEL, 0xEEFFFFFBFFFF7FF9L, 0x0000000052FFF77BL});
  public static final BitSet FOLLOW_hintArgName_in_hintArgs627 = new BitSet(new long[]{0x0000000000000402L});
  public static final BitSet FOLLOW_identifier_in_hintArgName669 = new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_expression_in_selectItem702 = new BitSet(
      new long[]{0xFDE9FFFDFC000002L, 0xDEBBFDEAF7FFFB16L, 0xF6FAF779FFBDFFFEL, 0xEEFFFFFBFFFF7FF9L, 0x0000000052FFF77BL});
  public static final BitSet FOLLOW_KW_AS_in_selectItem712 = new BitSet(
      new long[]{0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFB16L, 0xF6FAF779FFBDFFFEL, 0xEEFFFFFBFFFF7FF9L, 0x0000000052FFF77BL});
  public static final BitSet FOLLOW_identifier_in_selectItem715 = new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_KW_AS_in_selectItem721 = new BitSet(
      new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000400000000L});
  public static final BitSet FOLLOW_LPAREN_in_selectItem723 = new BitSet(
      new long[]{0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFB16L, 0xF6FAF779FFBDFFFEL, 0xEEFFFFFBFFFF7FF9L, 0x0000000052FFF77BL});
  public static final BitSet FOLLOW_identifier_in_selectItem725 = new BitSet(
      new long[]{0x0000000000000400L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000200000000000L});
  public static final BitSet FOLLOW_COMMA_in_selectItem728 = new BitSet(
      new long[]{0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFB16L, 0xF6FAF779FFBDFFFEL, 0xEEFFFFFBFFFF7FF9L, 0x0000000052FFF77BL});
  public static final BitSet FOLLOW_identifier_in_selectItem730 = new BitSet(
      new long[]{0x0000000000000400L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000200000000000L});
  public static final BitSet FOLLOW_RPAREN_in_selectItem734 = new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_tableAllColumns_in_selectItem762 = new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_KW_MAP_in_trfmClause805 = new BitSet(
      new long[]{0xFDEFFFFDFC042080L, 0xDEBBFDEAF7FFFBD6L, 0xF6FAFF7DFFBDFFFFL, 0xEEFFFFFBFFFF7FF9L, 0x003E032452FFF77BL});
  public static final BitSet FOLLOW_selectExpressionList_in_trfmClause810 = new BitSet(
      new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000040004000L, 0x0000000000080000L});
  public static final BitSet FOLLOW_KW_REDUCE_in_trfmClause820 = new BitSet(
      new long[]{0xFDEFFFFDFC042080L, 0xDEBBFDEAF7FFFBD6L, 0xF6FAFF7DFFBDFFFFL, 0xEEFFFFFBFFFF7FF9L, 0x003E032452FFF77BL});
  public static final BitSet FOLLOW_selectExpressionList_in_trfmClause822 = new BitSet(
      new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000040004000L, 0x0000000000080000L});
  public static final BitSet FOLLOW_rowFormat_in_trfmClause832 = new BitSet(
      new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000004000L, 0x0000000000080000L});
  public static final BitSet FOLLOW_recordWriter_in_trfmClause836 = new BitSet(
      new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000080000L});
  public static final BitSet FOLLOW_KW_USING_in_trfmClause842 = new BitSet(
      new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0008000000000000L});
  public static final BitSet FOLLOW_StringLiteral_in_trfmClause844 =
      new BitSet(new long[]{0x0000001000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000040002000L});
  public static final BitSet FOLLOW_KW_AS_in_trfmClause852 = new BitSet(
      new long[]{0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFB16L, 0xF6FAF779FFBDFFFEL, 0xEEFFFFFBFFFF7FF9L, 0x0000000452FFF77BL});
  public static final BitSet FOLLOW_LPAREN_in_trfmClause856 = new BitSet(
      new long[]{0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFB16L, 0xF6FAF779FFBDFFFEL, 0xEEFFFFFBFFFF7FF9L, 0x0000000052FFF77BL});
  public static final BitSet FOLLOW_aliasList_in_trfmClause859 = new BitSet(
      new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000200000000000L});
  public static final BitSet FOLLOW_columnNameTypeList_in_trfmClause863 = new BitSet(
      new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000200000000000L});
  public static final BitSet FOLLOW_RPAREN_in_trfmClause866 =
      new BitSet(new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000040002000L});
  public static final BitSet FOLLOW_aliasList_in_trfmClause872 =
      new BitSet(new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000040002000L});
  public static final BitSet FOLLOW_columnNameTypeList_in_trfmClause876 =
      new BitSet(new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000040002000L});
  public static final BitSet FOLLOW_rowFormat_in_trfmClause888 =
      new BitSet(new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000002000L});
  public static final BitSet FOLLOW_recordReader_in_trfmClause892 = new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_expression_in_selectExpression955 = new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_tableAllColumns_in_selectExpression959 =
      new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_selectExpression_in_selectExpressionList990 =
      new BitSet(new long[]{0x0000000000000402L});
  public static final BitSet FOLLOW_COMMA_in_selectExpressionList993 = new BitSet(
      new long[]{0xFDEFFFFDFC042080L, 0xDEBBFDEAF7FFFBD6L, 0xF6FAFF7DFFBDFFFFL, 0xEEFFFFFBFFFF7FF9L, 0x003E032452FFF77BL});
  public static final BitSet FOLLOW_selectExpression_in_selectExpressionList995 =
      new BitSet(new long[]{0x0000000000000402L});
  public static final BitSet FOLLOW_KW_WINDOW_in_window_clause1034 = new BitSet(new long[]{0x0000000004000000L});
  public static final BitSet FOLLOW_window_defn_in_window_clause1036 = new BitSet(new long[]{0x0000000000000402L});
  public static final BitSet FOLLOW_COMMA_in_window_clause1039 = new BitSet(new long[]{0x0000000004000000L});
  public static final BitSet FOLLOW_window_defn_in_window_clause1041 = new BitSet(new long[]{0x0000000000000402L});
  public static final BitSet FOLLOW_Identifier_in_window_defn1077 = new BitSet(new long[]{0x0000001000000000L});
  public static final BitSet FOLLOW_KW_AS_in_window_defn1079 = new BitSet(
      new long[]{0x0000000004000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000400000000L});
  public static final BitSet FOLLOW_window_specification_in_window_defn1081 =
      new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_Identifier_in_window_specification1117 =
      new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_LPAREN_in_window_specification1123 = new BitSet(
      new long[]{0x0020000004000000L, 0x0000000010000000L, 0x1008000000000000L, 0x0000400080000100L, 0x0000200000000000L});
  public static final BitSet FOLLOW_Identifier_in_window_specification1125 = new BitSet(
      new long[]{0x0020000000000000L, 0x0000000010000000L, 0x1008000000000000L, 0x0000400080000100L, 0x0000200000000000L});
  public static final BitSet FOLLOW_partitioningSpec_in_window_specification1128 = new BitSet(
      new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000080000100L, 0x0000200000000000L});
  public static final BitSet FOLLOW_window_frame_in_window_specification1131 = new BitSet(
      new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000200000000000L});
  public static final BitSet FOLLOW_RPAREN_in_window_specification1134 = new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_window_range_expression_in_window_frame1161 =
      new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_window_value_expression_in_window_frame1166 =
      new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_KW_ROWS_in_window_range_expression1188 = new BitSet(
      new long[]{0x0000000000000000L, 0x0000000000000020L, 0x0000000000000000L, 0x0000000000000000L, 0x0000010000000080L});
  public static final BitSet FOLLOW_window_frame_start_boundary_in_window_range_expression1192 =
      new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_KW_ROWS_in_window_range_expression1206 =
      new BitSet(new long[]{0x0000010000000000L});
  public static final BitSet FOLLOW_KW_BETWEEN_in_window_range_expression1208 = new BitSet(
      new long[]{0x0000000000000000L, 0x0000000000000020L, 0x0000000000000000L, 0x0000000000000000L, 0x0000010000000080L});
  public static final BitSet FOLLOW_window_frame_boundary_in_window_range_expression1212 =
      new BitSet(new long[]{0x0000000200000000L});
  public static final BitSet FOLLOW_KW_AND_in_window_range_expression1214 = new BitSet(
      new long[]{0x0000000000000000L, 0x0000000000000020L, 0x0000000000000000L, 0x0000000000000000L, 0x0000010000000080L});
  public static final BitSet FOLLOW_window_frame_boundary_in_window_range_expression1218 =
      new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_KW_RANGE_in_window_value_expression1252 = new BitSet(
      new long[]{0x0000000000000000L, 0x0000000000000020L, 0x0000000000000000L, 0x0000000000000000L, 0x0000010000000080L});
  public static final BitSet FOLLOW_window_frame_start_boundary_in_window_value_expression1256 =
      new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_KW_RANGE_in_window_value_expression1270 =
      new BitSet(new long[]{0x0000010000000000L});
  public static final BitSet FOLLOW_KW_BETWEEN_in_window_value_expression1272 = new BitSet(
      new long[]{0x0000000000000000L, 0x0000000000000020L, 0x0000000000000000L, 0x0000000000000000L, 0x0000010000000080L});
  public static final BitSet FOLLOW_window_frame_boundary_in_window_value_expression1276 =
      new BitSet(new long[]{0x0000000200000000L});
  public static final BitSet FOLLOW_KW_AND_in_window_value_expression1278 = new BitSet(
      new long[]{0x0000000000000000L, 0x0000000000000020L, 0x0000000000000000L, 0x0000000000000000L, 0x0000010000000080L});
  public static final BitSet FOLLOW_window_frame_boundary_in_window_value_expression1282 =
      new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_KW_UNBOUNDED_in_window_frame_start_boundary1317 =
      new BitSet(new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000002L});
  public static final BitSet FOLLOW_KW_PRECEDING_in_window_frame_start_boundary1319 =
      new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_KW_CURRENT_in_window_frame_start_boundary1335 =
      new BitSet(new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000040000000L});
  public static final BitSet FOLLOW_KW_ROW_in_window_frame_start_boundary1337 =
      new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_Number_in_window_frame_start_boundary1350 =
      new BitSet(new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000002L});
  public static final BitSet FOLLOW_KW_PRECEDING_in_window_frame_start_boundary1352 =
      new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_KW_UNBOUNDED_in_window_frame_boundary1383 =
      new BitSet(new long[]{0x0000000000000000L, 0x0004000000000000L, 0x0000000000000000L, 0x0000000000000002L});
  public static final BitSet FOLLOW_KW_PRECEDING_in_window_frame_boundary1388 =
      new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_KW_FOLLOWING_in_window_frame_boundary1392 =
      new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_KW_CURRENT_in_window_frame_boundary1410 =
      new BitSet(new long[]{0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000040000000L});
  public static final BitSet FOLLOW_KW_ROW_in_window_frame_boundary1412 = new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_Number_in_window_frame_boundary1425 =
      new BitSet(new long[]{0x0000000000000000L, 0x0004000000000000L, 0x0000000000000000L, 0x0000000000000002L});
  public static final BitSet FOLLOW_KW_PRECEDING_in_window_frame_boundary1430 =
      new BitSet(new long[]{0x0000000000000002L});
  public static final BitSet FOLLOW_KW_FOLLOWING_in_window_frame_boundary1436 =
      new BitSet(new long[]{0x0000000000000002L});
}
