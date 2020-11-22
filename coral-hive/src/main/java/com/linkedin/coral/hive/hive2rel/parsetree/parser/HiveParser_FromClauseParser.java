/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// $ANTLR 3.4 FromClauseParser.g 2017-10-10 09:21:01

package com.linkedin.coral.hive.hive2rel.parsetree.parser;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.BitSet;
import org.antlr.runtime.DFA;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.FailedPredicateException;
import org.antlr.runtime.IntStream;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.RuleReturnScope;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.RewriteEarlyExitException;
import org.antlr.runtime.tree.RewriteRuleSubtreeStream;
import org.antlr.runtime.tree.RewriteRuleTokenStream;
import org.antlr.runtime.tree.TreeAdaptor;


//CHECKSTYLE:OFF
@SuppressWarnings({"all", "warnings", "unchecked"})
public class HiveParser_FromClauseParser extends Parser {
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
    return new Parser[] {};
  }

  // delegators
  public HiveParser gHiveParser;
  public HiveParser gParent;

  public HiveParser_FromClauseParser(TokenStream input, HiveParser gHiveParser) {
    this(input, new RecognizerSharedState(), gHiveParser);
  }

  public HiveParser_FromClauseParser(TokenStream input, RecognizerSharedState state, HiveParser gHiveParser) {
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
    return "FromClauseParser.g";
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

  public static class tableAllColumns_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "tableAllColumns"
  // FromClauseParser.g:48:1: tableAllColumns : ( STAR -> ^( TOK_ALLCOLREF ) | tableName DOT STAR -> ^( TOK_ALLCOLREF tableName ) );
  public final HiveParser_FromClauseParser.tableAllColumns_return tableAllColumns() throws RecognitionException {
    HiveParser_FromClauseParser.tableAllColumns_return retval =
        new HiveParser_FromClauseParser.tableAllColumns_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token STAR1 = null;
    Token DOT3 = null;
    Token STAR4 = null;
    HiveParser_FromClauseParser.tableName_return tableName2 = null;

    CommonTree STAR1_tree = null;
    CommonTree DOT3_tree = null;
    CommonTree STAR4_tree = null;
    RewriteRuleTokenStream stream_STAR = new RewriteRuleTokenStream(adaptor, "token STAR");
    RewriteRuleTokenStream stream_DOT = new RewriteRuleTokenStream(adaptor, "token DOT");
    RewriteRuleSubtreeStream stream_tableName = new RewriteRuleSubtreeStream(adaptor, "rule tableName");
    try {
      // FromClauseParser.g:49:5: ( STAR -> ^( TOK_ALLCOLREF ) | tableName DOT STAR -> ^( TOK_ALLCOLREF tableName ) )
      int alt1 = 2;
      switch (input.LA(1)) {
        case STAR: {
          alt1 = 1;
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
          alt1 = 2;
        }
        break;
        default:
          if (state.backtracking > 0) {
            state.failed = true;
            return retval;
          }
          NoViableAltException nvae = new NoViableAltException("", 1, 0, input);

          throw nvae;
      }

      switch (alt1) {
        case 1:
          // FromClauseParser.g:49:7: STAR
        {
          STAR1 = (Token) match(input, STAR, FOLLOW_STAR_in_tableAllColumns57);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_STAR.add(STAR1);
          }

          // AST REWRITE
          // elements:
          // token labels:
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          if (state.backtracking == 0) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval =
                new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

            root_0 = (CommonTree) adaptor.nil();
            // 50:9: -> ^( TOK_ALLCOLREF )
            {
              // FromClauseParser.g:50:12: ^( TOK_ALLCOLREF )
              {
                CommonTree root_1 = (CommonTree) adaptor.nil();
                root_1 = (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_ALLCOLREF, "TOK_ALLCOLREF"),
                    root_1);

                adaptor.addChild(root_0, root_1);
              }
            }

            retval.tree = root_0;
          }
        }
        break;
        case 2:
          // FromClauseParser.g:51:7: tableName DOT STAR
        {
          pushFollow(FOLLOW_tableName_in_tableAllColumns79);
          tableName2 = tableName();

          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_tableName.add(tableName2.getTree());
          }

          DOT3 = (Token) match(input, DOT, FOLLOW_DOT_in_tableAllColumns81);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_DOT.add(DOT3);
          }

          STAR4 = (Token) match(input, STAR, FOLLOW_STAR_in_tableAllColumns83);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_STAR.add(STAR4);
          }

          // AST REWRITE
          // elements: tableName
          // token labels:
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          if (state.backtracking == 0) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval =
                new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

            root_0 = (CommonTree) adaptor.nil();
            // 52:9: -> ^( TOK_ALLCOLREF tableName )
            {
              // FromClauseParser.g:52:12: ^( TOK_ALLCOLREF tableName )
              {
                CommonTree root_1 = (CommonTree) adaptor.nil();
                root_1 = (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_ALLCOLREF, "TOK_ALLCOLREF"),
                    root_1);

                adaptor.addChild(root_1, stream_tableName.nextTree());

                adaptor.addChild(root_0, root_1);
              }
            }

            retval.tree = root_0;
          }
        }
        break;
      }
      retval.stop = input.LT(-1);

      if (state.backtracking == 0) {

        retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "tableAllColumns"

  public static class tableOrColumn_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "tableOrColumn"
  // FromClauseParser.g:56:1: tableOrColumn : identifier -> ^( TOK_TABLE_OR_COL identifier ) ;
  public final HiveParser_FromClauseParser.tableOrColumn_return tableOrColumn() throws RecognitionException {
    HiveParser_FromClauseParser.tableOrColumn_return retval = new HiveParser_FromClauseParser.tableOrColumn_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    HiveParser_IdentifiersParser.identifier_return identifier5 = null;

    RewriteRuleSubtreeStream stream_identifier = new RewriteRuleSubtreeStream(adaptor, "rule identifier");
    gParent.pushMsg("table or column identifier", state);
    try {
      // FromClauseParser.g:59:5: ( identifier -> ^( TOK_TABLE_OR_COL identifier ) )
      // FromClauseParser.g:60:5: identifier
      {
        pushFollow(FOLLOW_identifier_in_tableOrColumn131);
        identifier5 = gHiveParser.identifier();

        state._fsp--;
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_identifier.add(identifier5.getTree());
        }

        // AST REWRITE
        // elements: identifier
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        if (state.backtracking == 0) {

          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 60:16: -> ^( TOK_TABLE_OR_COL identifier )
          {
            // FromClauseParser.g:60:19: ^( TOK_TABLE_OR_COL identifier )
            {
              CommonTree root_1 = (CommonTree) adaptor.nil();
              root_1 =
                  (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_TABLE_OR_COL, "TOK_TABLE_OR_COL"),
                      root_1);

              adaptor.addChild(root_1, stream_identifier.nextTree());

              adaptor.addChild(root_0, root_1);
            }
          }

          retval.tree = root_0;
        }
      }

      retval.stop = input.LT(-1);

      if (state.backtracking == 0) {

        retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
      if (state.backtracking == 0) {
        gParent.popMsg(state);
      }
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "tableOrColumn"

  public static class expressionList_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "expressionList"
  // FromClauseParser.g:63:1: expressionList : expression ( COMMA expression )* -> ^( TOK_EXPLIST ( expression )+ ) ;
  public final HiveParser_FromClauseParser.expressionList_return expressionList() throws RecognitionException {
    HiveParser_FromClauseParser.expressionList_return retval = new HiveParser_FromClauseParser.expressionList_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token COMMA7 = null;
    HiveParser_IdentifiersParser.expression_return expression6 = null;

    HiveParser_IdentifiersParser.expression_return expression8 = null;

    CommonTree COMMA7_tree = null;
    RewriteRuleTokenStream stream_COMMA = new RewriteRuleTokenStream(adaptor, "token COMMA");
    RewriteRuleSubtreeStream stream_expression = new RewriteRuleSubtreeStream(adaptor, "rule expression");
    gParent.pushMsg("expression list", state);
    try {
      // FromClauseParser.g:66:5: ( expression ( COMMA expression )* -> ^( TOK_EXPLIST ( expression )+ ) )
      // FromClauseParser.g:67:5: expression ( COMMA expression )*
      {
        pushFollow(FOLLOW_expression_in_expressionList170);
        expression6 = gHiveParser.expression();

        state._fsp--;
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_expression.add(expression6.getTree());
        }

        // FromClauseParser.g:67:16: ( COMMA expression )*
        loop2:
        do {
          int alt2 = 2;
          switch (input.LA(1)) {
            case COMMA: {
              alt2 = 1;
            }
            break;
          }

          switch (alt2) {
            case 1:
              // FromClauseParser.g:67:17: COMMA expression
            {
              COMMA7 = (Token) match(input, COMMA, FOLLOW_COMMA_in_expressionList173);
              if (state.failed) {
                return retval;
              }
              if (state.backtracking == 0) {
                stream_COMMA.add(COMMA7);
              }

              pushFollow(FOLLOW_expression_in_expressionList175);
              expression8 = gHiveParser.expression();

              state._fsp--;
              if (state.failed) {
                return retval;
              }
              if (state.backtracking == 0) {
                stream_expression.add(expression8.getTree());
              }
            }
            break;

            default:
              break loop2;
          }
        } while (true);

        // AST REWRITE
        // elements: expression
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        if (state.backtracking == 0) {

          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 67:36: -> ^( TOK_EXPLIST ( expression )+ )
          {
            // FromClauseParser.g:67:39: ^( TOK_EXPLIST ( expression )+ )
            {
              CommonTree root_1 = (CommonTree) adaptor.nil();
              root_1 = (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_EXPLIST, "TOK_EXPLIST"), root_1);

              if (!(stream_expression.hasNext())) {
                throw new RewriteEarlyExitException();
              }
              while (stream_expression.hasNext()) {
                adaptor.addChild(root_1, stream_expression.nextTree());
              }
              stream_expression.reset();

              adaptor.addChild(root_0, root_1);
            }
          }

          retval.tree = root_0;
        }
      }

      retval.stop = input.LT(-1);

      if (state.backtracking == 0) {

        retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
      if (state.backtracking == 0) {
        gParent.popMsg(state);
      }
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "expressionList"

  public static class aliasList_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "aliasList"
  // FromClauseParser.g:70:1: aliasList : identifier ( COMMA identifier )* -> ^( TOK_ALIASLIST ( identifier )+ ) ;
  public final HiveParser_FromClauseParser.aliasList_return aliasList() throws RecognitionException {
    HiveParser_FromClauseParser.aliasList_return retval = new HiveParser_FromClauseParser.aliasList_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token COMMA10 = null;
    HiveParser_IdentifiersParser.identifier_return identifier9 = null;

    HiveParser_IdentifiersParser.identifier_return identifier11 = null;

    CommonTree COMMA10_tree = null;
    RewriteRuleTokenStream stream_COMMA = new RewriteRuleTokenStream(adaptor, "token COMMA");
    RewriteRuleSubtreeStream stream_identifier = new RewriteRuleSubtreeStream(adaptor, "rule identifier");
    gParent.pushMsg("alias list", state);
    try {
      // FromClauseParser.g:73:5: ( identifier ( COMMA identifier )* -> ^( TOK_ALIASLIST ( identifier )+ ) )
      // FromClauseParser.g:74:5: identifier ( COMMA identifier )*
      {
        pushFollow(FOLLOW_identifier_in_aliasList217);
        identifier9 = gHiveParser.identifier();

        state._fsp--;
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_identifier.add(identifier9.getTree());
        }

        // FromClauseParser.g:74:16: ( COMMA identifier )*
        loop3:
        do {
          int alt3 = 2;
          switch (input.LA(1)) {
            case COMMA: {
              alt3 = 1;
            }
            break;
          }

          switch (alt3) {
            case 1:
              // FromClauseParser.g:74:17: COMMA identifier
            {
              COMMA10 = (Token) match(input, COMMA, FOLLOW_COMMA_in_aliasList220);
              if (state.failed) {
                return retval;
              }
              if (state.backtracking == 0) {
                stream_COMMA.add(COMMA10);
              }

              pushFollow(FOLLOW_identifier_in_aliasList222);
              identifier11 = gHiveParser.identifier();

              state._fsp--;
              if (state.failed) {
                return retval;
              }
              if (state.backtracking == 0) {
                stream_identifier.add(identifier11.getTree());
              }
            }
            break;

            default:
              break loop3;
          }
        } while (true);

        // AST REWRITE
        // elements: identifier
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        if (state.backtracking == 0) {

          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 74:36: -> ^( TOK_ALIASLIST ( identifier )+ )
          {
            // FromClauseParser.g:74:39: ^( TOK_ALIASLIST ( identifier )+ )
            {
              CommonTree root_1 = (CommonTree) adaptor.nil();
              root_1 =
                  (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_ALIASLIST, "TOK_ALIASLIST"), root_1);

              if (!(stream_identifier.hasNext())) {
                throw new RewriteEarlyExitException();
              }
              while (stream_identifier.hasNext()) {
                adaptor.addChild(root_1, stream_identifier.nextTree());
              }
              stream_identifier.reset();

              adaptor.addChild(root_0, root_1);
            }
          }

          retval.tree = root_0;
        }
      }

      retval.stop = input.LT(-1);

      if (state.backtracking == 0) {

        retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
      if (state.backtracking == 0) {
        gParent.popMsg(state);
      }
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "aliasList"

  public static class fromClause_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "fromClause"
  // FromClauseParser.g:79:1: fromClause : KW_FROM joinSource -> ^( TOK_FROM joinSource ) ;
  public final HiveParser_FromClauseParser.fromClause_return fromClause() throws RecognitionException {
    HiveParser_FromClauseParser.fromClause_return retval = new HiveParser_FromClauseParser.fromClause_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token KW_FROM12 = null;
    HiveParser_FromClauseParser.joinSource_return joinSource13 = null;

    CommonTree KW_FROM12_tree = null;
    RewriteRuleTokenStream stream_KW_FROM = new RewriteRuleTokenStream(adaptor, "token KW_FROM");
    RewriteRuleSubtreeStream stream_joinSource = new RewriteRuleSubtreeStream(adaptor, "rule joinSource");
    gParent.pushMsg("from clause", state);
    try {
      // FromClauseParser.g:82:5: ( KW_FROM joinSource -> ^( TOK_FROM joinSource ) )
      // FromClauseParser.g:83:5: KW_FROM joinSource
      {
        KW_FROM12 = (Token) match(input, KW_FROM, FOLLOW_KW_FROM_in_fromClause266);
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_KW_FROM.add(KW_FROM12);
        }

        pushFollow(FOLLOW_joinSource_in_fromClause268);
        joinSource13 = joinSource();

        state._fsp--;
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_joinSource.add(joinSource13.getTree());
        }

        // AST REWRITE
        // elements: joinSource
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        if (state.backtracking == 0) {

          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 83:24: -> ^( TOK_FROM joinSource )
          {
            // FromClauseParser.g:83:27: ^( TOK_FROM joinSource )
            {
              CommonTree root_1 = (CommonTree) adaptor.nil();
              root_1 = (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_FROM, "TOK_FROM"), root_1);

              adaptor.addChild(root_1, stream_joinSource.nextTree());

              adaptor.addChild(root_0, root_1);
            }
          }

          retval.tree = root_0;
        }
      }

      retval.stop = input.LT(-1);

      if (state.backtracking == 0) {

        retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
      if (state.backtracking == 0) {
        gParent.popMsg(state);
      }
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "fromClause"

  public static class joinSource_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "joinSource"
  // FromClauseParser.g:86:1: joinSource : ( fromSource ( joinToken ^ fromSource ( KW_ON ! expression {...}?)? )* | uniqueJoinToken ^ uniqueJoinSource ( COMMA ! uniqueJoinSource )+ );
  public final HiveParser_FromClauseParser.joinSource_return joinSource() throws RecognitionException {
    HiveParser_FromClauseParser.joinSource_return retval = new HiveParser_FromClauseParser.joinSource_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token KW_ON17 = null;
    Token COMMA21 = null;
    HiveParser_FromClauseParser.fromSource_return fromSource14 = null;

    HiveParser_FromClauseParser.joinToken_return joinToken15 = null;

    HiveParser_FromClauseParser.fromSource_return fromSource16 = null;

    HiveParser_IdentifiersParser.expression_return expression18 = null;

    HiveParser_FromClauseParser.uniqueJoinToken_return uniqueJoinToken19 = null;

    HiveParser_FromClauseParser.uniqueJoinSource_return uniqueJoinSource20 = null;

    HiveParser_FromClauseParser.uniqueJoinSource_return uniqueJoinSource22 = null;

    CommonTree KW_ON17_tree = null;
    CommonTree COMMA21_tree = null;

    gParent.pushMsg("join source", state);
    try {
      // FromClauseParser.g:89:5: ( fromSource ( joinToken ^ fromSource ( KW_ON ! expression {...}?)? )* | uniqueJoinToken ^ uniqueJoinSource ( COMMA ! uniqueJoinSource )+ )
      int alt7 = 2;
      switch (input.LA(1)) {
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
        case KW_WITH:
        case LPAREN: {
          alt7 = 1;
        }
        break;
        case KW_UNIQUEJOIN: {
          alt7 = 2;
        }
        break;
        default:
          if (state.backtracking > 0) {
            state.failed = true;
            return retval;
          }
          NoViableAltException nvae = new NoViableAltException("", 7, 0, input);

          throw nvae;
      }

      switch (alt7) {
        case 1:
          // FromClauseParser.g:89:7: fromSource ( joinToken ^ fromSource ( KW_ON ! expression {...}?)? )*
        {
          root_0 = (CommonTree) adaptor.nil();

          pushFollow(FOLLOW_fromSource_in_joinSource303);
          fromSource14 = fromSource();

          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            adaptor.addChild(root_0, fromSource14.getTree());
          }

          // FromClauseParser.g:89:18: ( joinToken ^ fromSource ( KW_ON ! expression {...}?)? )*
          loop5:
          do {
            int alt5 = 2;
            switch (input.LA(1)) {
              case COMMA:
              case KW_CROSS:
              case KW_FULL:
              case KW_INNER:
              case KW_JOIN:
              case KW_LEFT:
              case KW_RIGHT: {
                alt5 = 1;
              }
              break;
            }

            switch (alt5) {
              case 1:
                // FromClauseParser.g:89:20: joinToken ^ fromSource ( KW_ON ! expression {...}?)?
              {
                pushFollow(FOLLOW_joinToken_in_joinSource307);
                joinToken15 = joinToken();

                state._fsp--;
                if (state.failed) {
                  return retval;
                }
                if (state.backtracking == 0) {
                  root_0 = (CommonTree) adaptor.becomeRoot(joinToken15.getTree(), root_0);
                }

                pushFollow(FOLLOW_fromSource_in_joinSource310);
                fromSource16 = fromSource();

                state._fsp--;
                if (state.failed) {
                  return retval;
                }
                if (state.backtracking == 0) {
                  adaptor.addChild(root_0, fromSource16.getTree());
                }

                // FromClauseParser.g:89:42: ( KW_ON ! expression {...}?)?
                int alt4 = 2;
                switch (input.LA(1)) {
                  case KW_ON: {
                    alt4 = 1;
                  }
                  break;
                }

                switch (alt4) {
                  case 1:
                    // FromClauseParser.g:89:44: KW_ON ! expression {...}?
                  {
                    KW_ON17 = (Token) match(input, KW_ON, FOLLOW_KW_ON_in_joinSource314);
                    if (state.failed) {
                      return retval;
                    }

                    pushFollow(FOLLOW_expression_in_joinSource317);
                    expression18 = gHiveParser.expression();

                    state._fsp--;
                    if (state.failed) {
                      return retval;
                    }
                    if (state.backtracking == 0) {
                      adaptor.addChild(root_0, expression18.getTree());
                    }

                    if (!(((joinToken15 != null ? ((Token) joinToken15.start) : null).getType() != COMMA))) {
                      if (state.backtracking > 0) {
                        state.failed = true;
                        return retval;
                      }
                      throw new FailedPredicateException(input, "joinSource", "$joinToken.start.getType() != COMMA");
                    }
                  }
                  break;
                }
              }
              break;

              default:
                break loop5;
            }
          } while (true);
        }
        break;
        case 2:
          // FromClauseParser.g:90:7: uniqueJoinToken ^ uniqueJoinSource ( COMMA ! uniqueJoinSource )+
        {
          root_0 = (CommonTree) adaptor.nil();

          pushFollow(FOLLOW_uniqueJoinToken_in_joinSource333);
          uniqueJoinToken19 = uniqueJoinToken();

          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            root_0 = (CommonTree) adaptor.becomeRoot(uniqueJoinToken19.getTree(), root_0);
          }

          pushFollow(FOLLOW_uniqueJoinSource_in_joinSource336);
          uniqueJoinSource20 = uniqueJoinSource();

          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            adaptor.addChild(root_0, uniqueJoinSource20.getTree());
          }

          // FromClauseParser.g:90:41: ( COMMA ! uniqueJoinSource )+
          int cnt6 = 0;
          loop6:
          do {
            int alt6 = 2;
            switch (input.LA(1)) {
              case COMMA: {
                alt6 = 1;
              }
              break;
            }

            switch (alt6) {
              case 1:
                // FromClauseParser.g:90:42: COMMA ! uniqueJoinSource
              {
                COMMA21 = (Token) match(input, COMMA, FOLLOW_COMMA_in_joinSource339);
                if (state.failed) {
                  return retval;
                }

                pushFollow(FOLLOW_uniqueJoinSource_in_joinSource342);
                uniqueJoinSource22 = uniqueJoinSource();

                state._fsp--;
                if (state.failed) {
                  return retval;
                }
                if (state.backtracking == 0) {
                  adaptor.addChild(root_0, uniqueJoinSource22.getTree());
                }
              }
              break;

              default:
                if (cnt6 >= 1) {
                  break loop6;
                }
                if (state.backtracking > 0) {
                  state.failed = true;
                  return retval;
                }
                EarlyExitException eee = new EarlyExitException(6, input);
                throw eee;
            }
            cnt6++;
          } while (true);
        }
        break;
      }
      retval.stop = input.LT(-1);

      if (state.backtracking == 0) {

        retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
      if (state.backtracking == 0) {
        gParent.popMsg(state);
      }
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "joinSource"

  public static class uniqueJoinSource_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "uniqueJoinSource"
  // FromClauseParser.g:93:1: uniqueJoinSource : ( KW_PRESERVE )? fromSource uniqueJoinExpr ;
  public final HiveParser_FromClauseParser.uniqueJoinSource_return uniqueJoinSource() throws RecognitionException {
    HiveParser_FromClauseParser.uniqueJoinSource_return retval =
        new HiveParser_FromClauseParser.uniqueJoinSource_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token KW_PRESERVE23 = null;
    HiveParser_FromClauseParser.fromSource_return fromSource24 = null;

    HiveParser_FromClauseParser.uniqueJoinExpr_return uniqueJoinExpr25 = null;

    CommonTree KW_PRESERVE23_tree = null;

    gParent.pushMsg("join source", state);
    try {
      // FromClauseParser.g:96:5: ( ( KW_PRESERVE )? fromSource uniqueJoinExpr )
      // FromClauseParser.g:96:7: ( KW_PRESERVE )? fromSource uniqueJoinExpr
      {
        root_0 = (CommonTree) adaptor.nil();

        // FromClauseParser.g:96:7: ( KW_PRESERVE )?
        int alt8 = 2;
        switch (input.LA(1)) {
          case KW_PRESERVE: {
            alt8 = 1;
          }
          break;
        }

        switch (alt8) {
          case 1:
            // FromClauseParser.g:96:7: KW_PRESERVE
          {
            KW_PRESERVE23 = (Token) match(input, KW_PRESERVE, FOLLOW_KW_PRESERVE_in_uniqueJoinSource371);
            if (state.failed) {
              return retval;
            }
            if (state.backtracking == 0) {
              KW_PRESERVE23_tree = (CommonTree) adaptor.create(KW_PRESERVE23);
              adaptor.addChild(root_0, KW_PRESERVE23_tree);
            }
          }
          break;
        }

        pushFollow(FOLLOW_fromSource_in_uniqueJoinSource374);
        fromSource24 = fromSource();

        state._fsp--;
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          adaptor.addChild(root_0, fromSource24.getTree());
        }

        pushFollow(FOLLOW_uniqueJoinExpr_in_uniqueJoinSource376);
        uniqueJoinExpr25 = uniqueJoinExpr();

        state._fsp--;
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          adaptor.addChild(root_0, uniqueJoinExpr25.getTree());
        }
      }

      retval.stop = input.LT(-1);

      if (state.backtracking == 0) {

        retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
      if (state.backtracking == 0) {
        gParent.popMsg(state);
      }
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "uniqueJoinSource"

  public static class uniqueJoinExpr_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "uniqueJoinExpr"
  // FromClauseParser.g:99:1: uniqueJoinExpr : LPAREN e1+= expression ( COMMA e1+= expression )* RPAREN -> ^( TOK_EXPLIST ( $e1)* ) ;
  public final HiveParser_FromClauseParser.uniqueJoinExpr_return uniqueJoinExpr() throws RecognitionException {
    HiveParser_FromClauseParser.uniqueJoinExpr_return retval = new HiveParser_FromClauseParser.uniqueJoinExpr_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token LPAREN26 = null;
    Token COMMA27 = null;
    Token RPAREN28 = null;
    List list_e1 = null;
    RuleReturnScope e1 = null;
    CommonTree LPAREN26_tree = null;
    CommonTree COMMA27_tree = null;
    CommonTree RPAREN28_tree = null;
    RewriteRuleTokenStream stream_COMMA = new RewriteRuleTokenStream(adaptor, "token COMMA");
    RewriteRuleTokenStream stream_LPAREN = new RewriteRuleTokenStream(adaptor, "token LPAREN");
    RewriteRuleTokenStream stream_RPAREN = new RewriteRuleTokenStream(adaptor, "token RPAREN");
    RewriteRuleSubtreeStream stream_expression = new RewriteRuleSubtreeStream(adaptor, "rule expression");
    gParent.pushMsg("unique join expression list", state);
    try {
      // FromClauseParser.g:102:5: ( LPAREN e1+= expression ( COMMA e1+= expression )* RPAREN -> ^( TOK_EXPLIST ( $e1)* ) )
      // FromClauseParser.g:102:7: LPAREN e1+= expression ( COMMA e1+= expression )* RPAREN
      {
        LPAREN26 = (Token) match(input, LPAREN, FOLLOW_LPAREN_in_uniqueJoinExpr403);
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_LPAREN.add(LPAREN26);
        }

        pushFollow(FOLLOW_expression_in_uniqueJoinExpr407);
        e1 = gHiveParser.expression();

        state._fsp--;
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_expression.add(e1.getTree());
        }
        if (list_e1 == null) {
          list_e1 = new ArrayList();
        }
        list_e1.add(e1.getTree());

        // FromClauseParser.g:102:29: ( COMMA e1+= expression )*
        loop9:
        do {
          int alt9 = 2;
          switch (input.LA(1)) {
            case COMMA: {
              alt9 = 1;
            }
            break;
          }

          switch (alt9) {
            case 1:
              // FromClauseParser.g:102:30: COMMA e1+= expression
            {
              COMMA27 = (Token) match(input, COMMA, FOLLOW_COMMA_in_uniqueJoinExpr410);
              if (state.failed) {
                return retval;
              }
              if (state.backtracking == 0) {
                stream_COMMA.add(COMMA27);
              }

              pushFollow(FOLLOW_expression_in_uniqueJoinExpr414);
              e1 = gHiveParser.expression();

              state._fsp--;
              if (state.failed) {
                return retval;
              }
              if (state.backtracking == 0) {
                stream_expression.add(e1.getTree());
              }
              if (list_e1 == null) {
                list_e1 = new ArrayList();
              }
              list_e1.add(e1.getTree());
            }
            break;

            default:
              break loop9;
          }
        } while (true);

        RPAREN28 = (Token) match(input, RPAREN, FOLLOW_RPAREN_in_uniqueJoinExpr418);
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_RPAREN.add(RPAREN28);
        }

        // AST REWRITE
        // elements: e1
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels: e1
        // wildcard labels:
        if (state.backtracking == 0) {

          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);
          RewriteRuleSubtreeStream stream_e1 = new RewriteRuleSubtreeStream(adaptor, "token e1", list_e1);
          root_0 = (CommonTree) adaptor.nil();
          // 103:7: -> ^( TOK_EXPLIST ( $e1)* )
          {
            // FromClauseParser.g:103:10: ^( TOK_EXPLIST ( $e1)* )
            {
              CommonTree root_1 = (CommonTree) adaptor.nil();
              root_1 = (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_EXPLIST, "TOK_EXPLIST"), root_1);

              // FromClauseParser.g:103:25: ( $e1)*
              while (stream_e1.hasNext()) {
                adaptor.addChild(root_1, stream_e1.nextTree());
              }
              stream_e1.reset();

              adaptor.addChild(root_0, root_1);
            }
          }

          retval.tree = root_0;
        }
      }

      retval.stop = input.LT(-1);

      if (state.backtracking == 0) {

        retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
      if (state.backtracking == 0) {
        gParent.popMsg(state);
      }
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "uniqueJoinExpr"

  public static class uniqueJoinToken_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "uniqueJoinToken"
  // FromClauseParser.g:106:1: uniqueJoinToken : KW_UNIQUEJOIN -> TOK_UNIQUEJOIN ;
  public final HiveParser_FromClauseParser.uniqueJoinToken_return uniqueJoinToken() throws RecognitionException {
    HiveParser_FromClauseParser.uniqueJoinToken_return retval =
        new HiveParser_FromClauseParser.uniqueJoinToken_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token KW_UNIQUEJOIN29 = null;

    CommonTree KW_UNIQUEJOIN29_tree = null;
    RewriteRuleTokenStream stream_KW_UNIQUEJOIN = new RewriteRuleTokenStream(adaptor, "token KW_UNIQUEJOIN");

    gParent.pushMsg("unique join", state);
    try {
      // FromClauseParser.g:109:5: ( KW_UNIQUEJOIN -> TOK_UNIQUEJOIN )
      // FromClauseParser.g:109:7: KW_UNIQUEJOIN
      {
        KW_UNIQUEJOIN29 = (Token) match(input, KW_UNIQUEJOIN, FOLLOW_KW_UNIQUEJOIN_in_uniqueJoinToken461);
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_KW_UNIQUEJOIN.add(KW_UNIQUEJOIN29);
        }

        // AST REWRITE
        // elements:
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        if (state.backtracking == 0) {

          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 109:21: -> TOK_UNIQUEJOIN
          {
            adaptor.addChild(root_0, (CommonTree) adaptor.create(TOK_UNIQUEJOIN, "TOK_UNIQUEJOIN"));
          }

          retval.tree = root_0;
        }
      }

      retval.stop = input.LT(-1);

      if (state.backtracking == 0) {

        retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
      if (state.backtracking == 0) {
        gParent.popMsg(state);
      }
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "uniqueJoinToken"

  public static class joinToken_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "joinToken"
  // FromClauseParser.g:111:1: joinToken : ( KW_JOIN -> TOK_JOIN | KW_INNER KW_JOIN -> TOK_JOIN | COMMA -> TOK_JOIN | KW_CROSS KW_JOIN -> TOK_CROSSJOIN | KW_LEFT ( KW_OUTER )? KW_JOIN -> TOK_LEFTOUTERJOIN | KW_RIGHT ( KW_OUTER )? KW_JOIN -> TOK_RIGHTOUTERJOIN | KW_FULL ( KW_OUTER )? KW_JOIN -> TOK_FULLOUTERJOIN | KW_LEFT KW_SEMI KW_JOIN -> TOK_LEFTSEMIJOIN );
  public final HiveParser_FromClauseParser.joinToken_return joinToken() throws RecognitionException {
    HiveParser_FromClauseParser.joinToken_return retval = new HiveParser_FromClauseParser.joinToken_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token KW_JOIN30 = null;
    Token KW_INNER31 = null;
    Token KW_JOIN32 = null;
    Token COMMA33 = null;
    Token KW_CROSS34 = null;
    Token KW_JOIN35 = null;
    Token KW_LEFT36 = null;
    Token KW_OUTER37 = null;
    Token KW_JOIN38 = null;
    Token KW_RIGHT39 = null;
    Token KW_OUTER40 = null;
    Token KW_JOIN41 = null;
    Token KW_FULL42 = null;
    Token KW_OUTER43 = null;
    Token KW_JOIN44 = null;
    Token KW_LEFT45 = null;
    Token KW_SEMI46 = null;
    Token KW_JOIN47 = null;

    CommonTree KW_JOIN30_tree = null;
    CommonTree KW_INNER31_tree = null;
    CommonTree KW_JOIN32_tree = null;
    CommonTree COMMA33_tree = null;
    CommonTree KW_CROSS34_tree = null;
    CommonTree KW_JOIN35_tree = null;
    CommonTree KW_LEFT36_tree = null;
    CommonTree KW_OUTER37_tree = null;
    CommonTree KW_JOIN38_tree = null;
    CommonTree KW_RIGHT39_tree = null;
    CommonTree KW_OUTER40_tree = null;
    CommonTree KW_JOIN41_tree = null;
    CommonTree KW_FULL42_tree = null;
    CommonTree KW_OUTER43_tree = null;
    CommonTree KW_JOIN44_tree = null;
    CommonTree KW_LEFT45_tree = null;
    CommonTree KW_SEMI46_tree = null;
    CommonTree KW_JOIN47_tree = null;
    RewriteRuleTokenStream stream_COMMA = new RewriteRuleTokenStream(adaptor, "token COMMA");
    RewriteRuleTokenStream stream_KW_RIGHT = new RewriteRuleTokenStream(adaptor, "token KW_RIGHT");
    RewriteRuleTokenStream stream_KW_CROSS = new RewriteRuleTokenStream(adaptor, "token KW_CROSS");
    RewriteRuleTokenStream stream_KW_FULL = new RewriteRuleTokenStream(adaptor, "token KW_FULL");
    RewriteRuleTokenStream stream_KW_JOIN = new RewriteRuleTokenStream(adaptor, "token KW_JOIN");
    RewriteRuleTokenStream stream_KW_OUTER = new RewriteRuleTokenStream(adaptor, "token KW_OUTER");
    RewriteRuleTokenStream stream_KW_SEMI = new RewriteRuleTokenStream(adaptor, "token KW_SEMI");
    RewriteRuleTokenStream stream_KW_LEFT = new RewriteRuleTokenStream(adaptor, "token KW_LEFT");
    RewriteRuleTokenStream stream_KW_INNER = new RewriteRuleTokenStream(adaptor, "token KW_INNER");

    gParent.pushMsg("join type specifier", state);
    try {
      // FromClauseParser.g:114:5: ( KW_JOIN -> TOK_JOIN | KW_INNER KW_JOIN -> TOK_JOIN | COMMA -> TOK_JOIN | KW_CROSS KW_JOIN -> TOK_CROSSJOIN | KW_LEFT ( KW_OUTER )? KW_JOIN -> TOK_LEFTOUTERJOIN | KW_RIGHT ( KW_OUTER )? KW_JOIN -> TOK_RIGHTOUTERJOIN | KW_FULL ( KW_OUTER )? KW_JOIN -> TOK_FULLOUTERJOIN | KW_LEFT KW_SEMI KW_JOIN -> TOK_LEFTSEMIJOIN )
      int alt13 = 8;
      switch (input.LA(1)) {
        case KW_JOIN: {
          alt13 = 1;
        }
        break;
        case KW_INNER: {
          alt13 = 2;
        }
        break;
        case COMMA: {
          alt13 = 3;
        }
        break;
        case KW_CROSS: {
          alt13 = 4;
        }
        break;
        case KW_LEFT: {
          switch (input.LA(2)) {
            case KW_SEMI: {
              alt13 = 8;
            }
            break;
            case KW_JOIN:
            case KW_OUTER: {
              alt13 = 5;
            }
            break;
            default:
              if (state.backtracking > 0) {
                state.failed = true;
                return retval;
              }
              NoViableAltException nvae = new NoViableAltException("", 13, 5, input);

              throw nvae;
          }
        }
        break;
        case KW_RIGHT: {
          alt13 = 6;
        }
        break;
        case KW_FULL: {
          alt13 = 7;
        }
        break;
        default:
          if (state.backtracking > 0) {
            state.failed = true;
            return retval;
          }
          NoViableAltException nvae = new NoViableAltException("", 13, 0, input);

          throw nvae;
      }

      switch (alt13) {
        case 1:
          // FromClauseParser.g:115:7: KW_JOIN
        {
          KW_JOIN30 = (Token) match(input, KW_JOIN, FOLLOW_KW_JOIN_in_joinToken493);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_KW_JOIN.add(KW_JOIN30);
          }

          // AST REWRITE
          // elements:
          // token labels:
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          if (state.backtracking == 0) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval =
                new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

            root_0 = (CommonTree) adaptor.nil();
            // 115:36: -> TOK_JOIN
            {
              adaptor.addChild(root_0, (CommonTree) adaptor.create(TOK_JOIN, "TOK_JOIN"));
            }

            retval.tree = root_0;
          }
        }
        break;
        case 2:
          // FromClauseParser.g:116:7: KW_INNER KW_JOIN
        {
          KW_INNER31 = (Token) match(input, KW_INNER, FOLLOW_KW_INNER_in_joinToken526);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_KW_INNER.add(KW_INNER31);
          }

          KW_JOIN32 = (Token) match(input, KW_JOIN, FOLLOW_KW_JOIN_in_joinToken528);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_KW_JOIN.add(KW_JOIN32);
          }

          // AST REWRITE
          // elements:
          // token labels:
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          if (state.backtracking == 0) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval =
                new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

            root_0 = (CommonTree) adaptor.nil();
            // 116:36: -> TOK_JOIN
            {
              adaptor.addChild(root_0, (CommonTree) adaptor.create(TOK_JOIN, "TOK_JOIN"));
            }

            retval.tree = root_0;
          }
        }
        break;
        case 3:
          // FromClauseParser.g:117:7: COMMA
        {
          COMMA33 = (Token) match(input, COMMA, FOLLOW_COMMA_in_joinToken552);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_COMMA.add(COMMA33);
          }

          // AST REWRITE
          // elements:
          // token labels:
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          if (state.backtracking == 0) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval =
                new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

            root_0 = (CommonTree) adaptor.nil();
            // 117:36: -> TOK_JOIN
            {
              adaptor.addChild(root_0, (CommonTree) adaptor.create(TOK_JOIN, "TOK_JOIN"));
            }

            retval.tree = root_0;
          }
        }
        break;
        case 4:
          // FromClauseParser.g:118:7: KW_CROSS KW_JOIN
        {
          KW_CROSS34 = (Token) match(input, KW_CROSS, FOLLOW_KW_CROSS_in_joinToken587);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_KW_CROSS.add(KW_CROSS34);
          }

          KW_JOIN35 = (Token) match(input, KW_JOIN, FOLLOW_KW_JOIN_in_joinToken589);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_KW_JOIN.add(KW_JOIN35);
          }

          // AST REWRITE
          // elements:
          // token labels:
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          if (state.backtracking == 0) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval =
                new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

            root_0 = (CommonTree) adaptor.nil();
            // 118:36: -> TOK_CROSSJOIN
            {
              adaptor.addChild(root_0, (CommonTree) adaptor.create(TOK_CROSSJOIN, "TOK_CROSSJOIN"));
            }

            retval.tree = root_0;
          }
        }
        break;
        case 5:
          // FromClauseParser.g:119:7: KW_LEFT ( KW_OUTER )? KW_JOIN
        {
          KW_LEFT36 = (Token) match(input, KW_LEFT, FOLLOW_KW_LEFT_in_joinToken613);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_KW_LEFT.add(KW_LEFT36);
          }

          // FromClauseParser.g:119:16: ( KW_OUTER )?
          int alt10 = 2;
          switch (input.LA(1)) {
            case KW_OUTER: {
              alt10 = 1;
            }
            break;
          }

          switch (alt10) {
            case 1:
              // FromClauseParser.g:119:17: KW_OUTER
            {
              KW_OUTER37 = (Token) match(input, KW_OUTER, FOLLOW_KW_OUTER_in_joinToken617);
              if (state.failed) {
                return retval;
              }
              if (state.backtracking == 0) {
                stream_KW_OUTER.add(KW_OUTER37);
              }
            }
            break;
          }

          KW_JOIN38 = (Token) match(input, KW_JOIN, FOLLOW_KW_JOIN_in_joinToken621);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_KW_JOIN.add(KW_JOIN38);
          }

          // AST REWRITE
          // elements:
          // token labels:
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          if (state.backtracking == 0) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval =
                new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

            root_0 = (CommonTree) adaptor.nil();
            // 119:36: -> TOK_LEFTOUTERJOIN
            {
              adaptor.addChild(root_0, (CommonTree) adaptor.create(TOK_LEFTOUTERJOIN, "TOK_LEFTOUTERJOIN"));
            }

            retval.tree = root_0;
          }
        }
        break;
        case 6:
          // FromClauseParser.g:120:7: KW_RIGHT ( KW_OUTER )? KW_JOIN
        {
          KW_RIGHT39 = (Token) match(input, KW_RIGHT, FOLLOW_KW_RIGHT_in_joinToken633);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_KW_RIGHT.add(KW_RIGHT39);
          }

          // FromClauseParser.g:120:16: ( KW_OUTER )?
          int alt11 = 2;
          switch (input.LA(1)) {
            case KW_OUTER: {
              alt11 = 1;
            }
            break;
          }

          switch (alt11) {
            case 1:
              // FromClauseParser.g:120:17: KW_OUTER
            {
              KW_OUTER40 = (Token) match(input, KW_OUTER, FOLLOW_KW_OUTER_in_joinToken636);
              if (state.failed) {
                return retval;
              }
              if (state.backtracking == 0) {
                stream_KW_OUTER.add(KW_OUTER40);
              }
            }
            break;
          }

          KW_JOIN41 = (Token) match(input, KW_JOIN, FOLLOW_KW_JOIN_in_joinToken640);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_KW_JOIN.add(KW_JOIN41);
          }

          // AST REWRITE
          // elements:
          // token labels:
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          if (state.backtracking == 0) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval =
                new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

            root_0 = (CommonTree) adaptor.nil();
            // 120:36: -> TOK_RIGHTOUTERJOIN
            {
              adaptor.addChild(root_0, (CommonTree) adaptor.create(TOK_RIGHTOUTERJOIN, "TOK_RIGHTOUTERJOIN"));
            }

            retval.tree = root_0;
          }
        }
        break;
        case 7:
          // FromClauseParser.g:121:7: KW_FULL ( KW_OUTER )? KW_JOIN
        {
          KW_FULL42 = (Token) match(input, KW_FULL, FOLLOW_KW_FULL_in_joinToken652);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_KW_FULL.add(KW_FULL42);
          }

          // FromClauseParser.g:121:16: ( KW_OUTER )?
          int alt12 = 2;
          switch (input.LA(1)) {
            case KW_OUTER: {
              alt12 = 1;
            }
            break;
          }

          switch (alt12) {
            case 1:
              // FromClauseParser.g:121:17: KW_OUTER
            {
              KW_OUTER43 = (Token) match(input, KW_OUTER, FOLLOW_KW_OUTER_in_joinToken656);
              if (state.failed) {
                return retval;
              }
              if (state.backtracking == 0) {
                stream_KW_OUTER.add(KW_OUTER43);
              }
            }
            break;
          }

          KW_JOIN44 = (Token) match(input, KW_JOIN, FOLLOW_KW_JOIN_in_joinToken660);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_KW_JOIN.add(KW_JOIN44);
          }

          // AST REWRITE
          // elements:
          // token labels:
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          if (state.backtracking == 0) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval =
                new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

            root_0 = (CommonTree) adaptor.nil();
            // 121:36: -> TOK_FULLOUTERJOIN
            {
              adaptor.addChild(root_0, (CommonTree) adaptor.create(TOK_FULLOUTERJOIN, "TOK_FULLOUTERJOIN"));
            }

            retval.tree = root_0;
          }
        }
        break;
        case 8:
          // FromClauseParser.g:122:7: KW_LEFT KW_SEMI KW_JOIN
        {
          KW_LEFT45 = (Token) match(input, KW_LEFT, FOLLOW_KW_LEFT_in_joinToken672);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_KW_LEFT.add(KW_LEFT45);
          }

          KW_SEMI46 = (Token) match(input, KW_SEMI, FOLLOW_KW_SEMI_in_joinToken674);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_KW_SEMI.add(KW_SEMI46);
          }

          KW_JOIN47 = (Token) match(input, KW_JOIN, FOLLOW_KW_JOIN_in_joinToken676);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_KW_JOIN.add(KW_JOIN47);
          }

          // AST REWRITE
          // elements:
          // token labels:
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          if (state.backtracking == 0) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval =
                new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

            root_0 = (CommonTree) adaptor.nil();
            // 122:36: -> TOK_LEFTSEMIJOIN
            {
              adaptor.addChild(root_0, (CommonTree) adaptor.create(TOK_LEFTSEMIJOIN, "TOK_LEFTSEMIJOIN"));
            }

            retval.tree = root_0;
          }
        }
        break;
      }
      retval.stop = input.LT(-1);

      if (state.backtracking == 0) {

        retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
      if (state.backtracking == 0) {
        gParent.popMsg(state);
      }
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "joinToken"

  public static class lateralView_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "lateralView"
  // FromClauseParser.g:125:1: lateralView : ( KW_LATERAL KW_VIEW KW_OUTER function tableAlias ( KW_AS identifier ( ( COMMA )=> COMMA identifier )* )? -> ^( TOK_LATERAL_VIEW_OUTER ^( TOK_SELECT ^( TOK_SELEXPR function ( identifier )* tableAlias ) ) ) | KW_LATERAL KW_VIEW function tableAlias ( KW_AS identifier ( ( COMMA )=> COMMA identifier )* )? -> ^( TOK_LATERAL_VIEW ^( TOK_SELECT ^( TOK_SELEXPR function ( identifier )* tableAlias ) ) ) );
  public final HiveParser_FromClauseParser.lateralView_return lateralView() throws RecognitionException {
    HiveParser_FromClauseParser.lateralView_return retval = new HiveParser_FromClauseParser.lateralView_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token KW_LATERAL48 = null;
    Token KW_VIEW49 = null;
    Token KW_OUTER50 = null;
    Token KW_AS53 = null;
    Token COMMA55 = null;
    Token KW_LATERAL57 = null;
    Token KW_VIEW58 = null;
    Token KW_AS61 = null;
    Token COMMA63 = null;
    HiveParser_IdentifiersParser.function_return function51 = null;

    HiveParser_FromClauseParser.tableAlias_return tableAlias52 = null;

    HiveParser_IdentifiersParser.identifier_return identifier54 = null;

    HiveParser_IdentifiersParser.identifier_return identifier56 = null;

    HiveParser_IdentifiersParser.function_return function59 = null;

    HiveParser_FromClauseParser.tableAlias_return tableAlias60 = null;

    HiveParser_IdentifiersParser.identifier_return identifier62 = null;

    HiveParser_IdentifiersParser.identifier_return identifier64 = null;

    CommonTree KW_LATERAL48_tree = null;
    CommonTree KW_VIEW49_tree = null;
    CommonTree KW_OUTER50_tree = null;
    CommonTree KW_AS53_tree = null;
    CommonTree COMMA55_tree = null;
    CommonTree KW_LATERAL57_tree = null;
    CommonTree KW_VIEW58_tree = null;
    CommonTree KW_AS61_tree = null;
    CommonTree COMMA63_tree = null;
    RewriteRuleTokenStream stream_COMMA = new RewriteRuleTokenStream(adaptor, "token COMMA");
    RewriteRuleTokenStream stream_KW_VIEW = new RewriteRuleTokenStream(adaptor, "token KW_VIEW");
    RewriteRuleTokenStream stream_KW_OUTER = new RewriteRuleTokenStream(adaptor, "token KW_OUTER");
    RewriteRuleTokenStream stream_KW_AS = new RewriteRuleTokenStream(adaptor, "token KW_AS");
    RewriteRuleTokenStream stream_KW_LATERAL = new RewriteRuleTokenStream(adaptor, "token KW_LATERAL");
    RewriteRuleSubtreeStream stream_identifier = new RewriteRuleSubtreeStream(adaptor, "rule identifier");
    RewriteRuleSubtreeStream stream_function = new RewriteRuleSubtreeStream(adaptor, "rule function");
    RewriteRuleSubtreeStream stream_tableAlias = new RewriteRuleSubtreeStream(adaptor, "rule tableAlias");
    gParent.pushMsg("lateral view", state);
    try {
      // FromClauseParser.g:128:2: ( KW_LATERAL KW_VIEW KW_OUTER function tableAlias ( KW_AS identifier ( ( COMMA )=> COMMA identifier )* )? -> ^( TOK_LATERAL_VIEW_OUTER ^( TOK_SELECT ^( TOK_SELEXPR function ( identifier )* tableAlias ) ) ) | KW_LATERAL KW_VIEW function tableAlias ( KW_AS identifier ( ( COMMA )=> COMMA identifier )* )? -> ^( TOK_LATERAL_VIEW ^( TOK_SELECT ^( TOK_SELEXPR function ( identifier )* tableAlias ) ) ) )
      int alt18 = 2;
      switch (input.LA(1)) {
        case KW_LATERAL: {
          switch (input.LA(2)) {
            case KW_VIEW: {
              switch (input.LA(3)) {
                case KW_OUTER: {
                  alt18 = 1;
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
                case KW_NO_DROP:
                case KW_NULL:
                case KW_OF:
                case KW_OFFLINE:
                case KW_OPTION:
                case KW_ORDER:
                case KW_OUT:
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
                  alt18 = 2;
                }
                break;
                default:
                  if (state.backtracking > 0) {
                    state.failed = true;
                    return retval;
                  }
                  NoViableAltException nvae = new NoViableAltException("", 18, 2, input);

                  throw nvae;
              }
            }
            break;
            default:
              if (state.backtracking > 0) {
                state.failed = true;
                return retval;
              }
              NoViableAltException nvae = new NoViableAltException("", 18, 1, input);

              throw nvae;
          }
        }
        break;
        default:
          if (state.backtracking > 0) {
            state.failed = true;
            return retval;
          }
          NoViableAltException nvae = new NoViableAltException("", 18, 0, input);

          throw nvae;
      }

      switch (alt18) {
        case 1:
          // FromClauseParser.g:129:2: KW_LATERAL KW_VIEW KW_OUTER function tableAlias ( KW_AS identifier ( ( COMMA )=> COMMA identifier )* )?
        {
          KW_LATERAL48 = (Token) match(input, KW_LATERAL, FOLLOW_KW_LATERAL_in_lateralView710);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_KW_LATERAL.add(KW_LATERAL48);
          }

          KW_VIEW49 = (Token) match(input, KW_VIEW, FOLLOW_KW_VIEW_in_lateralView712);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_KW_VIEW.add(KW_VIEW49);
          }

          KW_OUTER50 = (Token) match(input, KW_OUTER, FOLLOW_KW_OUTER_in_lateralView714);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_KW_OUTER.add(KW_OUTER50);
          }

          pushFollow(FOLLOW_function_in_lateralView716);
          function51 = gHiveParser.function();

          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_function.add(function51.getTree());
          }

          pushFollow(FOLLOW_tableAlias_in_lateralView718);
          tableAlias52 = tableAlias();

          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_tableAlias.add(tableAlias52.getTree());
          }

          // FromClauseParser.g:129:50: ( KW_AS identifier ( ( COMMA )=> COMMA identifier )* )?
          int alt15 = 2;
          switch (input.LA(1)) {
            case KW_AS: {
              alt15 = 1;
            }
            break;
          }

          switch (alt15) {
            case 1:
              // FromClauseParser.g:129:51: KW_AS identifier ( ( COMMA )=> COMMA identifier )*
            {
              KW_AS53 = (Token) match(input, KW_AS, FOLLOW_KW_AS_in_lateralView721);
              if (state.failed) {
                return retval;
              }
              if (state.backtracking == 0) {
                stream_KW_AS.add(KW_AS53);
              }

              pushFollow(FOLLOW_identifier_in_lateralView723);
              identifier54 = gHiveParser.identifier();

              state._fsp--;
              if (state.failed) {
                return retval;
              }
              if (state.backtracking == 0) {
                stream_identifier.add(identifier54.getTree());
              }

              // FromClauseParser.g:129:68: ( ( COMMA )=> COMMA identifier )*
              loop14:
              do {
                int alt14 = 2;
                alt14 = dfa14.predict(input);
                switch (alt14) {
                  case 1:
                    // FromClauseParser.g:129:69: ( COMMA )=> COMMA identifier
                  {
                    COMMA55 = (Token) match(input, COMMA, FOLLOW_COMMA_in_lateralView731);
                    if (state.failed) {
                      return retval;
                    }
                    if (state.backtracking == 0) {
                      stream_COMMA.add(COMMA55);
                    }

                    pushFollow(FOLLOW_identifier_in_lateralView733);
                    identifier56 = gHiveParser.identifier();

                    state._fsp--;
                    if (state.failed) {
                      return retval;
                    }
                    if (state.backtracking == 0) {
                      stream_identifier.add(identifier56.getTree());
                    }
                  }
                  break;

                  default:
                    break loop14;
                }
              } while (true);
            }
            break;
          }

          // AST REWRITE
          // elements: tableAlias, identifier, function
          // token labels:
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          if (state.backtracking == 0) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval =
                new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

            root_0 = (CommonTree) adaptor.nil();
            // 130:2: -> ^( TOK_LATERAL_VIEW_OUTER ^( TOK_SELECT ^( TOK_SELEXPR function ( identifier )* tableAlias ) ) )
            {
              // FromClauseParser.g:130:5: ^( TOK_LATERAL_VIEW_OUTER ^( TOK_SELECT ^( TOK_SELEXPR function ( identifier )* tableAlias ) ) )
              {
                CommonTree root_1 = (CommonTree) adaptor.nil();
                root_1 = (CommonTree) adaptor.becomeRoot(
                    (CommonTree) adaptor.create(TOK_LATERAL_VIEW_OUTER, "TOK_LATERAL_VIEW_OUTER"), root_1);

                // FromClauseParser.g:130:30: ^( TOK_SELECT ^( TOK_SELEXPR function ( identifier )* tableAlias ) )
                {
                  CommonTree root_2 = (CommonTree) adaptor.nil();
                  root_2 =
                      (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_SELECT, "TOK_SELECT"), root_2);

                  // FromClauseParser.g:130:43: ^( TOK_SELEXPR function ( identifier )* tableAlias )
                  {
                    CommonTree root_3 = (CommonTree) adaptor.nil();
                    root_3 = (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_SELEXPR, "TOK_SELEXPR"),
                        root_3);

                    adaptor.addChild(root_3, stream_function.nextTree());

                    // FromClauseParser.g:130:66: ( identifier )*
                    while (stream_identifier.hasNext()) {
                      adaptor.addChild(root_3, stream_identifier.nextTree());
                    }
                    stream_identifier.reset();

                    adaptor.addChild(root_3, stream_tableAlias.nextTree());

                    adaptor.addChild(root_2, root_3);
                  }

                  adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
              }
            }

            retval.tree = root_0;
          }
        }
        break;
        case 2:
          // FromClauseParser.g:132:2: KW_LATERAL KW_VIEW function tableAlias ( KW_AS identifier ( ( COMMA )=> COMMA identifier )* )?
        {
          KW_LATERAL57 = (Token) match(input, KW_LATERAL, FOLLOW_KW_LATERAL_in_lateralView765);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_KW_LATERAL.add(KW_LATERAL57);
          }

          KW_VIEW58 = (Token) match(input, KW_VIEW, FOLLOW_KW_VIEW_in_lateralView767);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_KW_VIEW.add(KW_VIEW58);
          }

          pushFollow(FOLLOW_function_in_lateralView769);
          function59 = gHiveParser.function();

          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_function.add(function59.getTree());
          }

          pushFollow(FOLLOW_tableAlias_in_lateralView771);
          tableAlias60 = tableAlias();

          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_tableAlias.add(tableAlias60.getTree());
          }

          // FromClauseParser.g:132:41: ( KW_AS identifier ( ( COMMA )=> COMMA identifier )* )?
          int alt17 = 2;
          switch (input.LA(1)) {
            case KW_AS: {
              alt17 = 1;
            }
            break;
          }

          switch (alt17) {
            case 1:
              // FromClauseParser.g:132:42: KW_AS identifier ( ( COMMA )=> COMMA identifier )*
            {
              KW_AS61 = (Token) match(input, KW_AS, FOLLOW_KW_AS_in_lateralView774);
              if (state.failed) {
                return retval;
              }
              if (state.backtracking == 0) {
                stream_KW_AS.add(KW_AS61);
              }

              pushFollow(FOLLOW_identifier_in_lateralView776);
              identifier62 = gHiveParser.identifier();

              state._fsp--;
              if (state.failed) {
                return retval;
              }
              if (state.backtracking == 0) {
                stream_identifier.add(identifier62.getTree());
              }

              // FromClauseParser.g:132:59: ( ( COMMA )=> COMMA identifier )*
              loop16:
              do {
                int alt16 = 2;
                alt16 = dfa16.predict(input);
                switch (alt16) {
                  case 1:
                    // FromClauseParser.g:132:60: ( COMMA )=> COMMA identifier
                  {
                    COMMA63 = (Token) match(input, COMMA, FOLLOW_COMMA_in_lateralView784);
                    if (state.failed) {
                      return retval;
                    }
                    if (state.backtracking == 0) {
                      stream_COMMA.add(COMMA63);
                    }

                    pushFollow(FOLLOW_identifier_in_lateralView786);
                    identifier64 = gHiveParser.identifier();

                    state._fsp--;
                    if (state.failed) {
                      return retval;
                    }
                    if (state.backtracking == 0) {
                      stream_identifier.add(identifier64.getTree());
                    }
                  }
                  break;

                  default:
                    break loop16;
                }
              } while (true);
            }
            break;
          }

          // AST REWRITE
          // elements: function, identifier, tableAlias
          // token labels:
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          if (state.backtracking == 0) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval =
                new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

            root_0 = (CommonTree) adaptor.nil();
            // 133:2: -> ^( TOK_LATERAL_VIEW ^( TOK_SELECT ^( TOK_SELEXPR function ( identifier )* tableAlias ) ) )
            {
              // FromClauseParser.g:133:5: ^( TOK_LATERAL_VIEW ^( TOK_SELECT ^( TOK_SELEXPR function ( identifier )* tableAlias ) ) )
              {
                CommonTree root_1 = (CommonTree) adaptor.nil();
                root_1 =
                    (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_LATERAL_VIEW, "TOK_LATERAL_VIEW"),
                        root_1);

                // FromClauseParser.g:133:24: ^( TOK_SELECT ^( TOK_SELEXPR function ( identifier )* tableAlias ) )
                {
                  CommonTree root_2 = (CommonTree) adaptor.nil();
                  root_2 =
                      (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_SELECT, "TOK_SELECT"), root_2);

                  // FromClauseParser.g:133:37: ^( TOK_SELEXPR function ( identifier )* tableAlias )
                  {
                    CommonTree root_3 = (CommonTree) adaptor.nil();
                    root_3 = (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_SELEXPR, "TOK_SELEXPR"),
                        root_3);

                    adaptor.addChild(root_3, stream_function.nextTree());

                    // FromClauseParser.g:133:60: ( identifier )*
                    while (stream_identifier.hasNext()) {
                      adaptor.addChild(root_3, stream_identifier.nextTree());
                    }
                    stream_identifier.reset();

                    adaptor.addChild(root_3, stream_tableAlias.nextTree());

                    adaptor.addChild(root_2, root_3);
                  }

                  adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
              }
            }

            retval.tree = root_0;
          }
        }
        break;
      }
      retval.stop = input.LT(-1);

      if (state.backtracking == 0) {

        retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
      if (state.backtracking == 0) {
        gParent.popMsg(state);
      }
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "lateralView"

  public static class tableAlias_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "tableAlias"
  // FromClauseParser.g:136:1: tableAlias : identifier -> ^( TOK_TABALIAS identifier ) ;
  public final HiveParser_FromClauseParser.tableAlias_return tableAlias() throws RecognitionException {
    HiveParser_FromClauseParser.tableAlias_return retval = new HiveParser_FromClauseParser.tableAlias_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    HiveParser_IdentifiersParser.identifier_return identifier65 = null;

    RewriteRuleSubtreeStream stream_identifier = new RewriteRuleSubtreeStream(adaptor, "rule identifier");
    gParent.pushMsg("table alias", state);
    try {
      // FromClauseParser.g:139:5: ( identifier -> ^( TOK_TABALIAS identifier ) )
      // FromClauseParser.g:140:5: identifier
      {
        pushFollow(FOLLOW_identifier_in_tableAlias840);
        identifier65 = gHiveParser.identifier();

        state._fsp--;
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_identifier.add(identifier65.getTree());
        }

        // AST REWRITE
        // elements: identifier
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        if (state.backtracking == 0) {

          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 140:16: -> ^( TOK_TABALIAS identifier )
          {
            // FromClauseParser.g:140:19: ^( TOK_TABALIAS identifier )
            {
              CommonTree root_1 = (CommonTree) adaptor.nil();
              root_1 =
                  (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_TABALIAS, "TOK_TABALIAS"), root_1);

              adaptor.addChild(root_1, stream_identifier.nextTree());

              adaptor.addChild(root_0, root_1);
            }
          }

          retval.tree = root_0;
        }
      }

      retval.stop = input.LT(-1);

      if (state.backtracking == 0) {

        retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
      if (state.backtracking == 0) {
        gParent.popMsg(state);
      }
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "tableAlias"

  public static class fromSource_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "fromSource"
  // FromClauseParser.g:143:1: fromSource : ( ( Identifier LPAREN )=> partitionedTableFunction | tableSource | subQuerySource | virtualTableSource ) ( lateralView ^)* ;
  public final HiveParser_FromClauseParser.fromSource_return fromSource() throws RecognitionException {
    HiveParser_FromClauseParser.fromSource_return retval = new HiveParser_FromClauseParser.fromSource_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    HiveParser_FromClauseParser.partitionedTableFunction_return partitionedTableFunction66 = null;

    HiveParser_FromClauseParser.tableSource_return tableSource67 = null;

    HiveParser_FromClauseParser.subQuerySource_return subQuerySource68 = null;

    HiveParser_FromClauseParser.virtualTableSource_return virtualTableSource69 = null;

    HiveParser_FromClauseParser.lateralView_return lateralView70 = null;

    gParent.pushMsg("from source", state);
    try {
      // FromClauseParser.g:146:5: ( ( ( Identifier LPAREN )=> partitionedTableFunction | tableSource | subQuerySource | virtualTableSource ) ( lateralView ^)* )
      // FromClauseParser.g:147:5: ( ( Identifier LPAREN )=> partitionedTableFunction | tableSource | subQuerySource | virtualTableSource ) ( lateralView ^)*
      {
        root_0 = (CommonTree) adaptor.nil();

        // FromClauseParser.g:147:5: ( ( Identifier LPAREN )=> partitionedTableFunction | tableSource | subQuerySource | virtualTableSource )
        int alt19 = 4;
        alt19 = dfa19.predict(input);
        switch (alt19) {
          case 1:
            // FromClauseParser.g:147:6: ( Identifier LPAREN )=> partitionedTableFunction
          {
            pushFollow(FOLLOW_partitionedTableFunction_in_fromSource887);
            partitionedTableFunction66 = partitionedTableFunction();

            state._fsp--;
            if (state.failed) {
              return retval;
            }
            if (state.backtracking == 0) {
              adaptor.addChild(root_0, partitionedTableFunction66.getTree());
            }
          }
          break;
          case 2:
            // FromClauseParser.g:147:55: tableSource
          {
            pushFollow(FOLLOW_tableSource_in_fromSource891);
            tableSource67 = tableSource();

            state._fsp--;
            if (state.failed) {
              return retval;
            }
            if (state.backtracking == 0) {
              adaptor.addChild(root_0, tableSource67.getTree());
            }
          }
          break;
          case 3:
            // FromClauseParser.g:147:69: subQuerySource
          {
            pushFollow(FOLLOW_subQuerySource_in_fromSource895);
            subQuerySource68 = subQuerySource();

            state._fsp--;
            if (state.failed) {
              return retval;
            }
            if (state.backtracking == 0) {
              adaptor.addChild(root_0, subQuerySource68.getTree());
            }
          }
          break;
          case 4:
            // FromClauseParser.g:147:86: virtualTableSource
          {
            pushFollow(FOLLOW_virtualTableSource_in_fromSource899);
            virtualTableSource69 = virtualTableSource();

            state._fsp--;
            if (state.failed) {
              return retval;
            }
            if (state.backtracking == 0) {
              adaptor.addChild(root_0, virtualTableSource69.getTree());
            }
          }
          break;
        }

        // FromClauseParser.g:147:106: ( lateralView ^)*
        loop20:
        do {
          int alt20 = 2;
          switch (input.LA(1)) {
            case KW_LATERAL: {
              alt20 = 1;
            }
            break;
          }

          switch (alt20) {
            case 1:
              // FromClauseParser.g:147:107: lateralView ^
            {
              pushFollow(FOLLOW_lateralView_in_fromSource903);
              lateralView70 = lateralView();

              state._fsp--;
              if (state.failed) {
                return retval;
              }
              if (state.backtracking == 0) {
                root_0 = (CommonTree) adaptor.becomeRoot(lateralView70.getTree(), root_0);
              }
            }
            break;

            default:
              break loop20;
          }
        } while (true);
      }

      retval.stop = input.LT(-1);

      if (state.backtracking == 0) {

        retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
      if (state.backtracking == 0) {
        gParent.popMsg(state);
      }
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "fromSource"

  public static class tableBucketSample_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "tableBucketSample"
  // FromClauseParser.g:150:1: tableBucketSample : KW_TABLESAMPLE LPAREN KW_BUCKET (numerator= Number ) KW_OUT KW_OF (denominator= Number ) ( KW_ON expr+= expression ( COMMA expr+= expression )* )? RPAREN -> ^( TOK_TABLEBUCKETSAMPLE $numerator $denominator ( $expr)* ) ;
  public final HiveParser_FromClauseParser.tableBucketSample_return tableBucketSample() throws RecognitionException {
    HiveParser_FromClauseParser.tableBucketSample_return retval =
        new HiveParser_FromClauseParser.tableBucketSample_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token numerator = null;
    Token denominator = null;
    Token KW_TABLESAMPLE71 = null;
    Token LPAREN72 = null;
    Token KW_BUCKET73 = null;
    Token KW_OUT74 = null;
    Token KW_OF75 = null;
    Token KW_ON76 = null;
    Token COMMA77 = null;
    Token RPAREN78 = null;
    List list_expr = null;
    RuleReturnScope expr = null;
    CommonTree numerator_tree = null;
    CommonTree denominator_tree = null;
    CommonTree KW_TABLESAMPLE71_tree = null;
    CommonTree LPAREN72_tree = null;
    CommonTree KW_BUCKET73_tree = null;
    CommonTree KW_OUT74_tree = null;
    CommonTree KW_OF75_tree = null;
    CommonTree KW_ON76_tree = null;
    CommonTree COMMA77_tree = null;
    CommonTree RPAREN78_tree = null;
    RewriteRuleTokenStream stream_COMMA = new RewriteRuleTokenStream(adaptor, "token COMMA");
    RewriteRuleTokenStream stream_KW_TABLESAMPLE = new RewriteRuleTokenStream(adaptor, "token KW_TABLESAMPLE");
    RewriteRuleTokenStream stream_KW_OF = new RewriteRuleTokenStream(adaptor, "token KW_OF");
    RewriteRuleTokenStream stream_Number = new RewriteRuleTokenStream(adaptor, "token Number");
    RewriteRuleTokenStream stream_LPAREN = new RewriteRuleTokenStream(adaptor, "token LPAREN");
    RewriteRuleTokenStream stream_KW_OUT = new RewriteRuleTokenStream(adaptor, "token KW_OUT");
    RewriteRuleTokenStream stream_KW_ON = new RewriteRuleTokenStream(adaptor, "token KW_ON");
    RewriteRuleTokenStream stream_RPAREN = new RewriteRuleTokenStream(adaptor, "token RPAREN");
    RewriteRuleTokenStream stream_KW_BUCKET = new RewriteRuleTokenStream(adaptor, "token KW_BUCKET");
    RewriteRuleSubtreeStream stream_expression = new RewriteRuleSubtreeStream(adaptor, "rule expression");
    gParent.pushMsg("table bucket sample specification", state);
    try {
      // FromClauseParser.g:153:5: ( KW_TABLESAMPLE LPAREN KW_BUCKET (numerator= Number ) KW_OUT KW_OF (denominator= Number ) ( KW_ON expr+= expression ( COMMA expr+= expression )* )? RPAREN -> ^( TOK_TABLEBUCKETSAMPLE $numerator $denominator ( $expr)* ) )
      // FromClauseParser.g:154:5: KW_TABLESAMPLE LPAREN KW_BUCKET (numerator= Number ) KW_OUT KW_OF (denominator= Number ) ( KW_ON expr+= expression ( COMMA expr+= expression )* )? RPAREN
      {
        KW_TABLESAMPLE71 = (Token) match(input, KW_TABLESAMPLE, FOLLOW_KW_TABLESAMPLE_in_tableBucketSample937);
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_KW_TABLESAMPLE.add(KW_TABLESAMPLE71);
        }

        LPAREN72 = (Token) match(input, LPAREN, FOLLOW_LPAREN_in_tableBucketSample939);
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_LPAREN.add(LPAREN72);
        }

        KW_BUCKET73 = (Token) match(input, KW_BUCKET, FOLLOW_KW_BUCKET_in_tableBucketSample941);
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_KW_BUCKET.add(KW_BUCKET73);
        }

        // FromClauseParser.g:154:37: (numerator= Number )
        // FromClauseParser.g:154:38: numerator= Number
        {
          numerator = (Token) match(input, Number, FOLLOW_Number_in_tableBucketSample946);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_Number.add(numerator);
          }
        }

        KW_OUT74 = (Token) match(input, KW_OUT, FOLLOW_KW_OUT_in_tableBucketSample949);
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_KW_OUT.add(KW_OUT74);
        }

        KW_OF75 = (Token) match(input, KW_OF, FOLLOW_KW_OF_in_tableBucketSample951);
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_KW_OF.add(KW_OF75);
        }

        // FromClauseParser.g:154:69: (denominator= Number )
        // FromClauseParser.g:154:70: denominator= Number
        {
          denominator = (Token) match(input, Number, FOLLOW_Number_in_tableBucketSample956);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_Number.add(denominator);
          }
        }

        // FromClauseParser.g:154:90: ( KW_ON expr+= expression ( COMMA expr+= expression )* )?
        int alt22 = 2;
        switch (input.LA(1)) {
          case KW_ON: {
            alt22 = 1;
          }
          break;
        }

        switch (alt22) {
          case 1:
            // FromClauseParser.g:154:91: KW_ON expr+= expression ( COMMA expr+= expression )*
          {
            KW_ON76 = (Token) match(input, KW_ON, FOLLOW_KW_ON_in_tableBucketSample960);
            if (state.failed) {
              return retval;
            }
            if (state.backtracking == 0) {
              stream_KW_ON.add(KW_ON76);
            }

            pushFollow(FOLLOW_expression_in_tableBucketSample964);
            expr = gHiveParser.expression();

            state._fsp--;
            if (state.failed) {
              return retval;
            }
            if (state.backtracking == 0) {
              stream_expression.add(expr.getTree());
            }
            if (list_expr == null) {
              list_expr = new ArrayList();
            }
            list_expr.add(expr.getTree());

            // FromClauseParser.g:154:114: ( COMMA expr+= expression )*
            loop21:
            do {
              int alt21 = 2;
              switch (input.LA(1)) {
                case COMMA: {
                  alt21 = 1;
                }
                break;
              }

              switch (alt21) {
                case 1:
                  // FromClauseParser.g:154:115: COMMA expr+= expression
                {
                  COMMA77 = (Token) match(input, COMMA, FOLLOW_COMMA_in_tableBucketSample967);
                  if (state.failed) {
                    return retval;
                  }
                  if (state.backtracking == 0) {
                    stream_COMMA.add(COMMA77);
                  }

                  pushFollow(FOLLOW_expression_in_tableBucketSample971);
                  expr = gHiveParser.expression();

                  state._fsp--;
                  if (state.failed) {
                    return retval;
                  }
                  if (state.backtracking == 0) {
                    stream_expression.add(expr.getTree());
                  }
                  if (list_expr == null) {
                    list_expr = new ArrayList();
                  }
                  list_expr.add(expr.getTree());
                }
                break;

                default:
                  break loop21;
              }
            } while (true);
          }
          break;
        }

        RPAREN78 = (Token) match(input, RPAREN, FOLLOW_RPAREN_in_tableBucketSample977);
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_RPAREN.add(RPAREN78);
        }

        // AST REWRITE
        // elements: numerator, expr, denominator
        // token labels: numerator, denominator
        // rule labels: retval
        // token list labels:
        // rule list labels: expr
        // wildcard labels:
        if (state.backtracking == 0) {

          retval.tree = root_0;
          RewriteRuleTokenStream stream_numerator = new RewriteRuleTokenStream(adaptor, "token numerator", numerator);
          RewriteRuleTokenStream stream_denominator =
              new RewriteRuleTokenStream(adaptor, "token denominator", denominator);
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);
          RewriteRuleSubtreeStream stream_expr = new RewriteRuleSubtreeStream(adaptor, "token expr", list_expr);
          root_0 = (CommonTree) adaptor.nil();
          // 154:149: -> ^( TOK_TABLEBUCKETSAMPLE $numerator $denominator ( $expr)* )
          {
            // FromClauseParser.g:154:152: ^( TOK_TABLEBUCKETSAMPLE $numerator $denominator ( $expr)* )
            {
              CommonTree root_1 = (CommonTree) adaptor.nil();
              root_1 = (CommonTree) adaptor.becomeRoot(
                  (CommonTree) adaptor.create(TOK_TABLEBUCKETSAMPLE, "TOK_TABLEBUCKETSAMPLE"), root_1);

              adaptor.addChild(root_1, stream_numerator.nextNode());

              adaptor.addChild(root_1, stream_denominator.nextNode());

              // FromClauseParser.g:154:201: ( $expr)*
              while (stream_expr.hasNext()) {
                adaptor.addChild(root_1, stream_expr.nextTree());
              }
              stream_expr.reset();

              adaptor.addChild(root_0, root_1);
            }
          }

          retval.tree = root_0;
        }
      }

      retval.stop = input.LT(-1);

      if (state.backtracking == 0) {

        retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
      if (state.backtracking == 0) {
        gParent.popMsg(state);
      }
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "tableBucketSample"

  public static class splitSample_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "splitSample"
  // FromClauseParser.g:157:1: splitSample : ( KW_TABLESAMPLE LPAREN (numerator= Number ) (percent= KW_PERCENT | KW_ROWS ) RPAREN -> {percent != null}? ^( TOK_TABLESPLITSAMPLE TOK_PERCENT $numerator) -> ^( TOK_TABLESPLITSAMPLE TOK_ROWCOUNT $numerator) | KW_TABLESAMPLE LPAREN (numerator= ByteLengthLiteral ) RPAREN -> ^( TOK_TABLESPLITSAMPLE TOK_LENGTH $numerator) );
  public final HiveParser_FromClauseParser.splitSample_return splitSample() throws RecognitionException {
    HiveParser_FromClauseParser.splitSample_return retval = new HiveParser_FromClauseParser.splitSample_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token numerator = null;
    Token percent = null;
    Token KW_TABLESAMPLE79 = null;
    Token LPAREN80 = null;
    Token KW_ROWS81 = null;
    Token RPAREN82 = null;
    Token KW_TABLESAMPLE83 = null;
    Token LPAREN84 = null;
    Token RPAREN85 = null;

    CommonTree numerator_tree = null;
    CommonTree percent_tree = null;
    CommonTree KW_TABLESAMPLE79_tree = null;
    CommonTree LPAREN80_tree = null;
    CommonTree KW_ROWS81_tree = null;
    CommonTree RPAREN82_tree = null;
    CommonTree KW_TABLESAMPLE83_tree = null;
    CommonTree LPAREN84_tree = null;
    CommonTree RPAREN85_tree = null;
    RewriteRuleTokenStream stream_KW_TABLESAMPLE = new RewriteRuleTokenStream(adaptor, "token KW_TABLESAMPLE");
    RewriteRuleTokenStream stream_Number = new RewriteRuleTokenStream(adaptor, "token Number");
    RewriteRuleTokenStream stream_KW_ROWS = new RewriteRuleTokenStream(adaptor, "token KW_ROWS");
    RewriteRuleTokenStream stream_LPAREN = new RewriteRuleTokenStream(adaptor, "token LPAREN");
    RewriteRuleTokenStream stream_KW_PERCENT = new RewriteRuleTokenStream(adaptor, "token KW_PERCENT");
    RewriteRuleTokenStream stream_RPAREN = new RewriteRuleTokenStream(adaptor, "token RPAREN");
    RewriteRuleTokenStream stream_ByteLengthLiteral = new RewriteRuleTokenStream(adaptor, "token ByteLengthLiteral");

    gParent.pushMsg("table split sample specification", state);
    try {
      // FromClauseParser.g:160:5: ( KW_TABLESAMPLE LPAREN (numerator= Number ) (percent= KW_PERCENT | KW_ROWS ) RPAREN -> {percent != null}? ^( TOK_TABLESPLITSAMPLE TOK_PERCENT $numerator) -> ^( TOK_TABLESPLITSAMPLE TOK_ROWCOUNT $numerator) | KW_TABLESAMPLE LPAREN (numerator= ByteLengthLiteral ) RPAREN -> ^( TOK_TABLESPLITSAMPLE TOK_LENGTH $numerator) )
      int alt24 = 2;
      switch (input.LA(1)) {
        case KW_TABLESAMPLE: {
          switch (input.LA(2)) {
            case LPAREN: {
              switch (input.LA(3)) {
                case Number: {
                  alt24 = 1;
                }
                break;
                case ByteLengthLiteral: {
                  alt24 = 2;
                }
                break;
                default:
                  if (state.backtracking > 0) {
                    state.failed = true;
                    return retval;
                  }
                  NoViableAltException nvae = new NoViableAltException("", 24, 2, input);

                  throw nvae;
              }
            }
            break;
            default:
              if (state.backtracking > 0) {
                state.failed = true;
                return retval;
              }
              NoViableAltException nvae = new NoViableAltException("", 24, 1, input);

              throw nvae;
          }
        }
        break;
        default:
          if (state.backtracking > 0) {
            state.failed = true;
            return retval;
          }
          NoViableAltException nvae = new NoViableAltException("", 24, 0, input);

          throw nvae;
      }

      switch (alt24) {
        case 1:
          // FromClauseParser.g:161:5: KW_TABLESAMPLE LPAREN (numerator= Number ) (percent= KW_PERCENT | KW_ROWS ) RPAREN
        {
          KW_TABLESAMPLE79 = (Token) match(input, KW_TABLESAMPLE, FOLLOW_KW_TABLESAMPLE_in_splitSample1024);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_KW_TABLESAMPLE.add(KW_TABLESAMPLE79);
          }

          LPAREN80 = (Token) match(input, LPAREN, FOLLOW_LPAREN_in_splitSample1026);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_LPAREN.add(LPAREN80);
          }

          // FromClauseParser.g:161:28: (numerator= Number )
          // FromClauseParser.g:161:29: numerator= Number
          {
            numerator = (Token) match(input, Number, FOLLOW_Number_in_splitSample1032);
            if (state.failed) {
              return retval;
            }
            if (state.backtracking == 0) {
              stream_Number.add(numerator);
            }
          }

          // FromClauseParser.g:161:47: (percent= KW_PERCENT | KW_ROWS )
          int alt23 = 2;
          switch (input.LA(1)) {
            case KW_PERCENT: {
              alt23 = 1;
            }
            break;
            case KW_ROWS: {
              alt23 = 2;
            }
            break;
            default:
              if (state.backtracking > 0) {
                state.failed = true;
                return retval;
              }
              NoViableAltException nvae = new NoViableAltException("", 23, 0, input);

              throw nvae;
          }

          switch (alt23) {
            case 1:
              // FromClauseParser.g:161:48: percent= KW_PERCENT
            {
              percent = (Token) match(input, KW_PERCENT, FOLLOW_KW_PERCENT_in_splitSample1038);
              if (state.failed) {
                return retval;
              }
              if (state.backtracking == 0) {
                stream_KW_PERCENT.add(percent);
              }
            }
            break;
            case 2:
              // FromClauseParser.g:161:67: KW_ROWS
            {
              KW_ROWS81 = (Token) match(input, KW_ROWS, FOLLOW_KW_ROWS_in_splitSample1040);
              if (state.failed) {
                return retval;
              }
              if (state.backtracking == 0) {
                stream_KW_ROWS.add(KW_ROWS81);
              }
            }
            break;
          }

          RPAREN82 = (Token) match(input, RPAREN, FOLLOW_RPAREN_in_splitSample1043);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_RPAREN.add(RPAREN82);
          }

          // AST REWRITE
          // elements: numerator, numerator
          // token labels: numerator
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          if (state.backtracking == 0) {

            retval.tree = root_0;
            RewriteRuleTokenStream stream_numerator = new RewriteRuleTokenStream(adaptor, "token numerator", numerator);
            RewriteRuleSubtreeStream stream_retval =
                new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

            root_0 = (CommonTree) adaptor.nil();
            // 162:5: -> {percent != null}? ^( TOK_TABLESPLITSAMPLE TOK_PERCENT $numerator)
            if (percent != null) {
              // FromClauseParser.g:162:27: ^( TOK_TABLESPLITSAMPLE TOK_PERCENT $numerator)
              {
                CommonTree root_1 = (CommonTree) adaptor.nil();
                root_1 = (CommonTree) adaptor.becomeRoot(
                    (CommonTree) adaptor.create(TOK_TABLESPLITSAMPLE, "TOK_TABLESPLITSAMPLE"), root_1);

                adaptor.addChild(root_1, (CommonTree) adaptor.create(TOK_PERCENT, "TOK_PERCENT"));

                adaptor.addChild(root_1, stream_numerator.nextNode());

                adaptor.addChild(root_0, root_1);
              }
            } else // 163:5: -> ^( TOK_TABLESPLITSAMPLE TOK_ROWCOUNT $numerator)
            {
              // FromClauseParser.g:163:8: ^( TOK_TABLESPLITSAMPLE TOK_ROWCOUNT $numerator)
              {
                CommonTree root_1 = (CommonTree) adaptor.nil();
                root_1 = (CommonTree) adaptor.becomeRoot(
                    (CommonTree) adaptor.create(TOK_TABLESPLITSAMPLE, "TOK_TABLESPLITSAMPLE"), root_1);

                adaptor.addChild(root_1, (CommonTree) adaptor.create(TOK_ROWCOUNT, "TOK_ROWCOUNT"));

                adaptor.addChild(root_1, stream_numerator.nextNode());

                adaptor.addChild(root_0, root_1);
              }
            }

            retval.tree = root_0;
          }
        }
        break;
        case 2:
          // FromClauseParser.g:165:5: KW_TABLESAMPLE LPAREN (numerator= ByteLengthLiteral ) RPAREN
        {
          KW_TABLESAMPLE83 = (Token) match(input, KW_TABLESAMPLE, FOLLOW_KW_TABLESAMPLE_in_splitSample1087);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_KW_TABLESAMPLE.add(KW_TABLESAMPLE83);
          }

          LPAREN84 = (Token) match(input, LPAREN, FOLLOW_LPAREN_in_splitSample1089);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_LPAREN.add(LPAREN84);
          }

          // FromClauseParser.g:165:28: (numerator= ByteLengthLiteral )
          // FromClauseParser.g:165:29: numerator= ByteLengthLiteral
          {
            numerator = (Token) match(input, ByteLengthLiteral, FOLLOW_ByteLengthLiteral_in_splitSample1095);
            if (state.failed) {
              return retval;
            }
            if (state.backtracking == 0) {
              stream_ByteLengthLiteral.add(numerator);
            }
          }

          RPAREN85 = (Token) match(input, RPAREN, FOLLOW_RPAREN_in_splitSample1098);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_RPAREN.add(RPAREN85);
          }

          // AST REWRITE
          // elements: numerator
          // token labels: numerator
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          if (state.backtracking == 0) {

            retval.tree = root_0;
            RewriteRuleTokenStream stream_numerator = new RewriteRuleTokenStream(adaptor, "token numerator", numerator);
            RewriteRuleSubtreeStream stream_retval =
                new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

            root_0 = (CommonTree) adaptor.nil();
            // 166:5: -> ^( TOK_TABLESPLITSAMPLE TOK_LENGTH $numerator)
            {
              // FromClauseParser.g:166:8: ^( TOK_TABLESPLITSAMPLE TOK_LENGTH $numerator)
              {
                CommonTree root_1 = (CommonTree) adaptor.nil();
                root_1 = (CommonTree) adaptor.becomeRoot(
                    (CommonTree) adaptor.create(TOK_TABLESPLITSAMPLE, "TOK_TABLESPLITSAMPLE"), root_1);

                adaptor.addChild(root_1, (CommonTree) adaptor.create(TOK_LENGTH, "TOK_LENGTH"));

                adaptor.addChild(root_1, stream_numerator.nextNode());

                adaptor.addChild(root_0, root_1);
              }
            }

            retval.tree = root_0;
          }
        }
        break;
      }
      retval.stop = input.LT(-1);

      if (state.backtracking == 0) {

        retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
      if (state.backtracking == 0) {
        gParent.popMsg(state);
      }
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "splitSample"

  public static class tableSample_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "tableSample"
  // FromClauseParser.g:169:1: tableSample : ( tableBucketSample | splitSample );
  public final HiveParser_FromClauseParser.tableSample_return tableSample() throws RecognitionException {
    HiveParser_FromClauseParser.tableSample_return retval = new HiveParser_FromClauseParser.tableSample_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    HiveParser_FromClauseParser.tableBucketSample_return tableBucketSample86 = null;

    HiveParser_FromClauseParser.splitSample_return splitSample87 = null;

    gParent.pushMsg("table sample specification", state);
    try {
      // FromClauseParser.g:172:5: ( tableBucketSample | splitSample )
      int alt25 = 2;
      switch (input.LA(1)) {
        case KW_TABLESAMPLE: {
          switch (input.LA(2)) {
            case LPAREN: {
              switch (input.LA(3)) {
                case KW_BUCKET: {
                  alt25 = 1;
                }
                break;
                case ByteLengthLiteral:
                case Number: {
                  alt25 = 2;
                }
                break;
                default:
                  if (state.backtracking > 0) {
                    state.failed = true;
                    return retval;
                  }
                  NoViableAltException nvae = new NoViableAltException("", 25, 2, input);

                  throw nvae;
              }
            }
            break;
            default:
              if (state.backtracking > 0) {
                state.failed = true;
                return retval;
              }
              NoViableAltException nvae = new NoViableAltException("", 25, 1, input);

              throw nvae;
          }
        }
        break;
        default:
          if (state.backtracking > 0) {
            state.failed = true;
            return retval;
          }
          NoViableAltException nvae = new NoViableAltException("", 25, 0, input);

          throw nvae;
      }

      switch (alt25) {
        case 1:
          // FromClauseParser.g:173:5: tableBucketSample
        {
          root_0 = (CommonTree) adaptor.nil();

          pushFollow(FOLLOW_tableBucketSample_in_tableSample1144);
          tableBucketSample86 = tableBucketSample();

          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            adaptor.addChild(root_0, tableBucketSample86.getTree());
          }
        }
        break;
        case 2:
          // FromClauseParser.g:174:5: splitSample
        {
          root_0 = (CommonTree) adaptor.nil();

          pushFollow(FOLLOW_splitSample_in_tableSample1152);
          splitSample87 = splitSample();

          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            adaptor.addChild(root_0, splitSample87.getTree());
          }
        }
        break;
      }
      retval.stop = input.LT(-1);

      if (state.backtracking == 0) {

        retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
      if (state.backtracking == 0) {
        gParent.popMsg(state);
      }
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "tableSample"

  public static class tableSource_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "tableSource"
  // FromClauseParser.g:177:1: tableSource : tabname= tableName (props= tableProperties )? (ts= tableSample )? ( ( KW_AS )? alias= Identifier )? -> ^( TOK_TABREF $tabname ( $props)? ( $ts)? ( $alias)? ) ;
  public final HiveParser_FromClauseParser.tableSource_return tableSource() throws RecognitionException {
    HiveParser_FromClauseParser.tableSource_return retval = new HiveParser_FromClauseParser.tableSource_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token alias = null;
    Token KW_AS88 = null;
    HiveParser_FromClauseParser.tableName_return tabname = null;

    HiveParser.tableProperties_return props = null;

    HiveParser_FromClauseParser.tableSample_return ts = null;

    CommonTree alias_tree = null;
    CommonTree KW_AS88_tree = null;
    RewriteRuleTokenStream stream_Identifier = new RewriteRuleTokenStream(adaptor, "token Identifier");
    RewriteRuleTokenStream stream_KW_AS = new RewriteRuleTokenStream(adaptor, "token KW_AS");
    RewriteRuleSubtreeStream stream_tableSample = new RewriteRuleSubtreeStream(adaptor, "rule tableSample");
    RewriteRuleSubtreeStream stream_tableProperties = new RewriteRuleSubtreeStream(adaptor, "rule tableProperties");
    RewriteRuleSubtreeStream stream_tableName = new RewriteRuleSubtreeStream(adaptor, "rule tableName");
    gParent.pushMsg("table source", state);
    try {
      // FromClauseParser.g:180:5: (tabname= tableName (props= tableProperties )? (ts= tableSample )? ( ( KW_AS )? alias= Identifier )? -> ^( TOK_TABREF $tabname ( $props)? ( $ts)? ( $alias)? ) )
      // FromClauseParser.g:180:7: tabname= tableName (props= tableProperties )? (ts= tableSample )? ( ( KW_AS )? alias= Identifier )?
      {
        pushFollow(FOLLOW_tableName_in_tableSource1181);
        tabname = tableName();

        state._fsp--;
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_tableName.add(tabname.getTree());
        }

        // FromClauseParser.g:180:25: (props= tableProperties )?
        int alt26 = 2;
        alt26 = dfa26.predict(input);
        switch (alt26) {
          case 1:
            // FromClauseParser.g:180:26: props= tableProperties
          {
            pushFollow(FOLLOW_tableProperties_in_tableSource1186);
            props = gHiveParser.tableProperties();

            state._fsp--;
            if (state.failed) {
              return retval;
            }
            if (state.backtracking == 0) {
              stream_tableProperties.add(props.getTree());
            }
          }
          break;
        }

        // FromClauseParser.g:180:50: (ts= tableSample )?
        int alt27 = 2;
        switch (input.LA(1)) {
          case KW_TABLESAMPLE: {
            alt27 = 1;
          }
          break;
        }

        switch (alt27) {
          case 1:
            // FromClauseParser.g:180:51: ts= tableSample
          {
            pushFollow(FOLLOW_tableSample_in_tableSource1193);
            ts = tableSample();

            state._fsp--;
            if (state.failed) {
              return retval;
            }
            if (state.backtracking == 0) {
              stream_tableSample.add(ts.getTree());
            }
          }
          break;
        }

        // FromClauseParser.g:180:68: ( ( KW_AS )? alias= Identifier )?
        int alt29 = 2;
        alt29 = dfa29.predict(input);
        switch (alt29) {
          case 1:
            // FromClauseParser.g:180:69: ( KW_AS )? alias= Identifier
          {
            // FromClauseParser.g:180:69: ( KW_AS )?
            int alt28 = 2;
            switch (input.LA(1)) {
              case KW_AS: {
                alt28 = 1;
              }
              break;
            }

            switch (alt28) {
              case 1:
                // FromClauseParser.g:180:69: KW_AS
              {
                KW_AS88 = (Token) match(input, KW_AS, FOLLOW_KW_AS_in_tableSource1198);
                if (state.failed) {
                  return retval;
                }
                if (state.backtracking == 0) {
                  stream_KW_AS.add(KW_AS88);
                }
              }
              break;
            }

            alias = (Token) match(input, Identifier, FOLLOW_Identifier_in_tableSource1203);
            if (state.failed) {
              return retval;
            }
            if (state.backtracking == 0) {
              stream_Identifier.add(alias);
            }
          }
          break;
        }

        // AST REWRITE
        // elements: alias, tabname, props, ts
        // token labels: alias
        // rule labels: tabname, retval, props, ts
        // token list labels:
        // rule list labels:
        // wildcard labels:
        if (state.backtracking == 0) {

          retval.tree = root_0;
          RewriteRuleTokenStream stream_alias = new RewriteRuleTokenStream(adaptor, "token alias", alias);
          RewriteRuleSubtreeStream stream_tabname =
              new RewriteRuleSubtreeStream(adaptor, "rule tabname", tabname != null ? tabname.tree : null);
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);
          RewriteRuleSubtreeStream stream_props =
              new RewriteRuleSubtreeStream(adaptor, "rule props", props != null ? props.tree : null);
          RewriteRuleSubtreeStream stream_ts =
              new RewriteRuleSubtreeStream(adaptor, "rule ts", ts != null ? ts.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 181:5: -> ^( TOK_TABREF $tabname ( $props)? ( $ts)? ( $alias)? )
          {
            // FromClauseParser.g:181:8: ^( TOK_TABREF $tabname ( $props)? ( $ts)? ( $alias)? )
            {
              CommonTree root_1 = (CommonTree) adaptor.nil();
              root_1 = (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_TABREF, "TOK_TABREF"), root_1);

              adaptor.addChild(root_1, stream_tabname.nextTree());

              // FromClauseParser.g:181:31: ( $props)?
              if (stream_props.hasNext()) {
                adaptor.addChild(root_1, stream_props.nextTree());
              }
              stream_props.reset();

              // FromClauseParser.g:181:39: ( $ts)?
              if (stream_ts.hasNext()) {
                adaptor.addChild(root_1, stream_ts.nextTree());
              }
              stream_ts.reset();

              // FromClauseParser.g:181:44: ( $alias)?
              if (stream_alias.hasNext()) {
                adaptor.addChild(root_1, stream_alias.nextNode());
              }
              stream_alias.reset();

              adaptor.addChild(root_0, root_1);
            }
          }

          retval.tree = root_0;
        }
      }

      retval.stop = input.LT(-1);

      if (state.backtracking == 0) {

        retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
      if (state.backtracking == 0) {
        gParent.popMsg(state);
      }
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "tableSource"

  public static class tableName_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "tableName"
  // FromClauseParser.g:184:1: tableName : (db= identifier DOT tab= identifier -> ^( TOK_TABNAME $db $tab) |tab= identifier -> ^( TOK_TABNAME $tab) );
  public final HiveParser_FromClauseParser.tableName_return tableName() throws RecognitionException {
    HiveParser_FromClauseParser.tableName_return retval = new HiveParser_FromClauseParser.tableName_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token DOT89 = null;
    HiveParser_IdentifiersParser.identifier_return db = null;

    HiveParser_IdentifiersParser.identifier_return tab = null;

    CommonTree DOT89_tree = null;
    RewriteRuleTokenStream stream_DOT = new RewriteRuleTokenStream(adaptor, "token DOT");
    RewriteRuleSubtreeStream stream_identifier = new RewriteRuleSubtreeStream(adaptor, "rule identifier");
    gParent.pushMsg("table name", state);
    try {
      // FromClauseParser.g:187:5: (db= identifier DOT tab= identifier -> ^( TOK_TABNAME $db $tab) |tab= identifier -> ^( TOK_TABNAME $tab) )
      int alt30 = 2;
      alt30 = dfa30.predict(input);
      switch (alt30) {
        case 1:
          // FromClauseParser.g:188:5: db= identifier DOT tab= identifier
        {
          pushFollow(FOLLOW_identifier_in_tableName1263);
          db = gHiveParser.identifier();

          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_identifier.add(db.getTree());
          }

          DOT89 = (Token) match(input, DOT, FOLLOW_DOT_in_tableName1265);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_DOT.add(DOT89);
          }

          pushFollow(FOLLOW_identifier_in_tableName1269);
          tab = gHiveParser.identifier();

          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_identifier.add(tab.getTree());
          }

          // AST REWRITE
          // elements: db, tab
          // token labels:
          // rule labels: tab, db, retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          if (state.backtracking == 0) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_tab =
                new RewriteRuleSubtreeStream(adaptor, "rule tab", tab != null ? tab.tree : null);
            RewriteRuleSubtreeStream stream_db =
                new RewriteRuleSubtreeStream(adaptor, "rule db", db != null ? db.tree : null);
            RewriteRuleSubtreeStream stream_retval =
                new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

            root_0 = (CommonTree) adaptor.nil();
            // 189:5: -> ^( TOK_TABNAME $db $tab)
            {
              // FromClauseParser.g:189:8: ^( TOK_TABNAME $db $tab)
              {
                CommonTree root_1 = (CommonTree) adaptor.nil();
                root_1 =
                    (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_TABNAME, "TOK_TABNAME"), root_1);

                adaptor.addChild(root_1, stream_db.nextTree());

                adaptor.addChild(root_1, stream_tab.nextTree());

                adaptor.addChild(root_0, root_1);
              }
            }

            retval.tree = root_0;
          }
        }
        break;
        case 2:
          // FromClauseParser.g:191:5: tab= identifier
        {
          pushFollow(FOLLOW_identifier_in_tableName1299);
          tab = gHiveParser.identifier();

          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_identifier.add(tab.getTree());
          }

          // AST REWRITE
          // elements: tab
          // token labels:
          // rule labels: tab, retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          if (state.backtracking == 0) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_tab =
                new RewriteRuleSubtreeStream(adaptor, "rule tab", tab != null ? tab.tree : null);
            RewriteRuleSubtreeStream stream_retval =
                new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

            root_0 = (CommonTree) adaptor.nil();
            // 192:5: -> ^( TOK_TABNAME $tab)
            {
              // FromClauseParser.g:192:8: ^( TOK_TABNAME $tab)
              {
                CommonTree root_1 = (CommonTree) adaptor.nil();
                root_1 =
                    (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_TABNAME, "TOK_TABNAME"), root_1);

                adaptor.addChild(root_1, stream_tab.nextTree());

                adaptor.addChild(root_0, root_1);
              }
            }

            retval.tree = root_0;
          }
        }
        break;
      }
      retval.stop = input.LT(-1);

      if (state.backtracking == 0) {

        retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
      if (state.backtracking == 0) {
        gParent.popMsg(state);
      }
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "tableName"

  public static class viewName_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "viewName"
  // FromClauseParser.g:195:1: viewName : (db= identifier DOT )? view= identifier -> ^( TOK_TABNAME ( $db)? $view) ;
  public final HiveParser_FromClauseParser.viewName_return viewName() throws RecognitionException {
    HiveParser_FromClauseParser.viewName_return retval = new HiveParser_FromClauseParser.viewName_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token DOT90 = null;
    HiveParser_IdentifiersParser.identifier_return db = null;

    HiveParser_IdentifiersParser.identifier_return view = null;

    CommonTree DOT90_tree = null;
    RewriteRuleTokenStream stream_DOT = new RewriteRuleTokenStream(adaptor, "token DOT");
    RewriteRuleSubtreeStream stream_identifier = new RewriteRuleSubtreeStream(adaptor, "rule identifier");
    gParent.pushMsg("view name", state);
    try {
      // FromClauseParser.g:198:5: ( (db= identifier DOT )? view= identifier -> ^( TOK_TABNAME ( $db)? $view) )
      // FromClauseParser.g:199:5: (db= identifier DOT )? view= identifier
      {
        // FromClauseParser.g:199:5: (db= identifier DOT )?
        int alt31 = 2;
        switch (input.LA(1)) {
          case Identifier: {
            switch (input.LA(2)) {
              case DOT: {
                alt31 = 1;
              }
              break;
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
              case DOT: {
                alt31 = 1;
              }
              break;
            }
          }
          break;
        }

        switch (alt31) {
          case 1:
            // FromClauseParser.g:199:6: db= identifier DOT
          {
            pushFollow(FOLLOW_identifier_in_viewName1346);
            db = gHiveParser.identifier();

            state._fsp--;
            if (state.failed) {
              return retval;
            }
            if (state.backtracking == 0) {
              stream_identifier.add(db.getTree());
            }

            DOT90 = (Token) match(input, DOT, FOLLOW_DOT_in_viewName1348);
            if (state.failed) {
              return retval;
            }
            if (state.backtracking == 0) {
              stream_DOT.add(DOT90);
            }
          }
          break;
        }

        pushFollow(FOLLOW_identifier_in_viewName1354);
        view = gHiveParser.identifier();

        state._fsp--;
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_identifier.add(view.getTree());
        }

        // AST REWRITE
        // elements: db, view
        // token labels:
        // rule labels: view, db, retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        if (state.backtracking == 0) {

          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_view =
              new RewriteRuleSubtreeStream(adaptor, "rule view", view != null ? view.tree : null);
          RewriteRuleSubtreeStream stream_db =
              new RewriteRuleSubtreeStream(adaptor, "rule db", db != null ? db.tree : null);
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 200:5: -> ^( TOK_TABNAME ( $db)? $view)
          {
            // FromClauseParser.g:200:8: ^( TOK_TABNAME ( $db)? $view)
            {
              CommonTree root_1 = (CommonTree) adaptor.nil();
              root_1 = (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_TABNAME, "TOK_TABNAME"), root_1);

              // FromClauseParser.g:200:23: ( $db)?
              if (stream_db.hasNext()) {
                adaptor.addChild(root_1, stream_db.nextTree());
              }
              stream_db.reset();

              adaptor.addChild(root_1, stream_view.nextTree());

              adaptor.addChild(root_0, root_1);
            }
          }

          retval.tree = root_0;
        }
      }

      retval.stop = input.LT(-1);

      if (state.backtracking == 0) {

        retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
      if (state.backtracking == 0) {
        gParent.popMsg(state);
      }
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "viewName"

  public static class subQuerySource_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "subQuerySource"
  // FromClauseParser.g:203:1: subQuerySource : LPAREN queryStatementExpression[false] RPAREN ( KW_AS )? identifier -> ^( TOK_SUBQUERY queryStatementExpression identifier ) ;
  public final HiveParser_FromClauseParser.subQuerySource_return subQuerySource() throws RecognitionException {
    HiveParser_FromClauseParser.subQuerySource_return retval = new HiveParser_FromClauseParser.subQuerySource_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token LPAREN91 = null;
    Token RPAREN93 = null;
    Token KW_AS94 = null;
    HiveParser.queryStatementExpression_return queryStatementExpression92 = null;

    HiveParser_IdentifiersParser.identifier_return identifier95 = null;

    CommonTree LPAREN91_tree = null;
    CommonTree RPAREN93_tree = null;
    CommonTree KW_AS94_tree = null;
    RewriteRuleTokenStream stream_LPAREN = new RewriteRuleTokenStream(adaptor, "token LPAREN");
    RewriteRuleTokenStream stream_RPAREN = new RewriteRuleTokenStream(adaptor, "token RPAREN");
    RewriteRuleTokenStream stream_KW_AS = new RewriteRuleTokenStream(adaptor, "token KW_AS");
    RewriteRuleSubtreeStream stream_identifier = new RewriteRuleSubtreeStream(adaptor, "rule identifier");
    RewriteRuleSubtreeStream stream_queryStatementExpression =
        new RewriteRuleSubtreeStream(adaptor, "rule queryStatementExpression");
    gParent.pushMsg("subquery source", state);
    try {
      // FromClauseParser.g:206:5: ( LPAREN queryStatementExpression[false] RPAREN ( KW_AS )? identifier -> ^( TOK_SUBQUERY queryStatementExpression identifier ) )
      // FromClauseParser.g:207:5: LPAREN queryStatementExpression[false] RPAREN ( KW_AS )? identifier
      {
        LPAREN91 = (Token) match(input, LPAREN, FOLLOW_LPAREN_in_subQuerySource1402);
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_LPAREN.add(LPAREN91);
        }

        pushFollow(FOLLOW_queryStatementExpression_in_subQuerySource1404);
        queryStatementExpression92 = gHiveParser.queryStatementExpression(false);

        state._fsp--;
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_queryStatementExpression.add(queryStatementExpression92.getTree());
        }

        RPAREN93 = (Token) match(input, RPAREN, FOLLOW_RPAREN_in_subQuerySource1407);
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_RPAREN.add(RPAREN93);
        }

        // FromClauseParser.g:207:51: ( KW_AS )?
        int alt32 = 2;
        alt32 = dfa32.predict(input);
        switch (alt32) {
          case 1:
            // FromClauseParser.g:207:51: KW_AS
          {
            KW_AS94 = (Token) match(input, KW_AS, FOLLOW_KW_AS_in_subQuerySource1409);
            if (state.failed) {
              return retval;
            }
            if (state.backtracking == 0) {
              stream_KW_AS.add(KW_AS94);
            }
          }
          break;
        }

        pushFollow(FOLLOW_identifier_in_subQuerySource1412);
        identifier95 = gHiveParser.identifier();

        state._fsp--;
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_identifier.add(identifier95.getTree());
        }

        // AST REWRITE
        // elements: identifier, queryStatementExpression
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        if (state.backtracking == 0) {

          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 207:69: -> ^( TOK_SUBQUERY queryStatementExpression identifier )
          {
            // FromClauseParser.g:207:72: ^( TOK_SUBQUERY queryStatementExpression identifier )
            {
              CommonTree root_1 = (CommonTree) adaptor.nil();
              root_1 =
                  (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_SUBQUERY, "TOK_SUBQUERY"), root_1);

              adaptor.addChild(root_1, stream_queryStatementExpression.nextTree());

              adaptor.addChild(root_1, stream_identifier.nextTree());

              adaptor.addChild(root_0, root_1);
            }
          }

          retval.tree = root_0;
        }
      }

      retval.stop = input.LT(-1);

      if (state.backtracking == 0) {

        retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
      if (state.backtracking == 0) {
        gParent.popMsg(state);
      }
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "subQuerySource"

  public static class partitioningSpec_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "partitioningSpec"
  // FromClauseParser.g:211:1: partitioningSpec : ( partitionByClause ( orderByClause )? -> ^( TOK_PARTITIONINGSPEC partitionByClause ( orderByClause )? ) | orderByClause -> ^( TOK_PARTITIONINGSPEC orderByClause ) | distributeByClause ( sortByClause )? -> ^( TOK_PARTITIONINGSPEC distributeByClause ( sortByClause )? ) | sortByClause -> ^( TOK_PARTITIONINGSPEC sortByClause ) | clusterByClause -> ^( TOK_PARTITIONINGSPEC clusterByClause ) );
  public final HiveParser_FromClauseParser.partitioningSpec_return partitioningSpec() throws RecognitionException {
    HiveParser_FromClauseParser.partitioningSpec_return retval =
        new HiveParser_FromClauseParser.partitioningSpec_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    HiveParser_IdentifiersParser.partitionByClause_return partitionByClause96 = null;

    HiveParser_IdentifiersParser.orderByClause_return orderByClause97 = null;

    HiveParser_IdentifiersParser.orderByClause_return orderByClause98 = null;

    HiveParser_IdentifiersParser.distributeByClause_return distributeByClause99 = null;

    HiveParser_IdentifiersParser.sortByClause_return sortByClause100 = null;

    HiveParser_IdentifiersParser.sortByClause_return sortByClause101 = null;

    HiveParser_IdentifiersParser.clusterByClause_return clusterByClause102 = null;

    RewriteRuleSubtreeStream stream_clusterByClause = new RewriteRuleSubtreeStream(adaptor, "rule clusterByClause");
    RewriteRuleSubtreeStream stream_sortByClause = new RewriteRuleSubtreeStream(adaptor, "rule sortByClause");
    RewriteRuleSubtreeStream stream_partitionByClause = new RewriteRuleSubtreeStream(adaptor, "rule partitionByClause");
    RewriteRuleSubtreeStream stream_distributeByClause =
        new RewriteRuleSubtreeStream(adaptor, "rule distributeByClause");
    RewriteRuleSubtreeStream stream_orderByClause = new RewriteRuleSubtreeStream(adaptor, "rule orderByClause");
    gParent.pushMsg("partitioningSpec clause", state);
    try {
      // FromClauseParser.g:214:4: ( partitionByClause ( orderByClause )? -> ^( TOK_PARTITIONINGSPEC partitionByClause ( orderByClause )? ) | orderByClause -> ^( TOK_PARTITIONINGSPEC orderByClause ) | distributeByClause ( sortByClause )? -> ^( TOK_PARTITIONINGSPEC distributeByClause ( sortByClause )? ) | sortByClause -> ^( TOK_PARTITIONINGSPEC sortByClause ) | clusterByClause -> ^( TOK_PARTITIONINGSPEC clusterByClause ) )
      int alt35 = 5;
      switch (input.LA(1)) {
        case KW_PARTITION: {
          alt35 = 1;
        }
        break;
        case KW_ORDER: {
          alt35 = 2;
        }
        break;
        case KW_DISTRIBUTE: {
          alt35 = 3;
        }
        break;
        case KW_SORT: {
          alt35 = 4;
        }
        break;
        case KW_CLUSTER: {
          alt35 = 5;
        }
        break;
        default:
          if (state.backtracking > 0) {
            state.failed = true;
            return retval;
          }
          NoViableAltException nvae = new NoViableAltException("", 35, 0, input);

          throw nvae;
      }

      switch (alt35) {
        case 1:
          // FromClauseParser.g:215:4: partitionByClause ( orderByClause )?
        {
          pushFollow(FOLLOW_partitionByClause_in_partitioningSpec1453);
          partitionByClause96 = gHiveParser.partitionByClause();

          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_partitionByClause.add(partitionByClause96.getTree());
          }

          // FromClauseParser.g:215:22: ( orderByClause )?
          int alt33 = 2;
          switch (input.LA(1)) {
            case KW_ORDER: {
              alt33 = 1;
            }
            break;
          }

          switch (alt33) {
            case 1:
              // FromClauseParser.g:215:22: orderByClause
            {
              pushFollow(FOLLOW_orderByClause_in_partitioningSpec1455);
              orderByClause97 = gHiveParser.orderByClause();

              state._fsp--;
              if (state.failed) {
                return retval;
              }
              if (state.backtracking == 0) {
                stream_orderByClause.add(orderByClause97.getTree());
              }
            }
            break;
          }

          // AST REWRITE
          // elements: partitionByClause, orderByClause
          // token labels:
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          if (state.backtracking == 0) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval =
                new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

            root_0 = (CommonTree) adaptor.nil();
            // 215:37: -> ^( TOK_PARTITIONINGSPEC partitionByClause ( orderByClause )? )
            {
              // FromClauseParser.g:215:40: ^( TOK_PARTITIONINGSPEC partitionByClause ( orderByClause )? )
              {
                CommonTree root_1 = (CommonTree) adaptor.nil();
                root_1 = (CommonTree) adaptor.becomeRoot(
                    (CommonTree) adaptor.create(TOK_PARTITIONINGSPEC, "TOK_PARTITIONINGSPEC"), root_1);

                adaptor.addChild(root_1, stream_partitionByClause.nextTree());

                // FromClauseParser.g:215:81: ( orderByClause )?
                if (stream_orderByClause.hasNext()) {
                  adaptor.addChild(root_1, stream_orderByClause.nextTree());
                }
                stream_orderByClause.reset();

                adaptor.addChild(root_0, root_1);
              }
            }

            retval.tree = root_0;
          }
        }
        break;
        case 2:
          // FromClauseParser.g:216:4: orderByClause
        {
          pushFollow(FOLLOW_orderByClause_in_partitioningSpec1474);
          orderByClause98 = gHiveParser.orderByClause();

          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_orderByClause.add(orderByClause98.getTree());
          }

          // AST REWRITE
          // elements: orderByClause
          // token labels:
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          if (state.backtracking == 0) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval =
                new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

            root_0 = (CommonTree) adaptor.nil();
            // 216:18: -> ^( TOK_PARTITIONINGSPEC orderByClause )
            {
              // FromClauseParser.g:216:21: ^( TOK_PARTITIONINGSPEC orderByClause )
              {
                CommonTree root_1 = (CommonTree) adaptor.nil();
                root_1 = (CommonTree) adaptor.becomeRoot(
                    (CommonTree) adaptor.create(TOK_PARTITIONINGSPEC, "TOK_PARTITIONINGSPEC"), root_1);

                adaptor.addChild(root_1, stream_orderByClause.nextTree());

                adaptor.addChild(root_0, root_1);
              }
            }

            retval.tree = root_0;
          }
        }
        break;
        case 3:
          // FromClauseParser.g:217:4: distributeByClause ( sortByClause )?
        {
          pushFollow(FOLLOW_distributeByClause_in_partitioningSpec1489);
          distributeByClause99 = gHiveParser.distributeByClause();

          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_distributeByClause.add(distributeByClause99.getTree());
          }

          // FromClauseParser.g:217:23: ( sortByClause )?
          int alt34 = 2;
          switch (input.LA(1)) {
            case KW_SORT: {
              alt34 = 1;
            }
            break;
          }

          switch (alt34) {
            case 1:
              // FromClauseParser.g:217:23: sortByClause
            {
              pushFollow(FOLLOW_sortByClause_in_partitioningSpec1491);
              sortByClause100 = gHiveParser.sortByClause();

              state._fsp--;
              if (state.failed) {
                return retval;
              }
              if (state.backtracking == 0) {
                stream_sortByClause.add(sortByClause100.getTree());
              }
            }
            break;
          }

          // AST REWRITE
          // elements: distributeByClause, sortByClause
          // token labels:
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          if (state.backtracking == 0) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval =
                new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

            root_0 = (CommonTree) adaptor.nil();
            // 217:37: -> ^( TOK_PARTITIONINGSPEC distributeByClause ( sortByClause )? )
            {
              // FromClauseParser.g:217:40: ^( TOK_PARTITIONINGSPEC distributeByClause ( sortByClause )? )
              {
                CommonTree root_1 = (CommonTree) adaptor.nil();
                root_1 = (CommonTree) adaptor.becomeRoot(
                    (CommonTree) adaptor.create(TOK_PARTITIONINGSPEC, "TOK_PARTITIONINGSPEC"), root_1);

                adaptor.addChild(root_1, stream_distributeByClause.nextTree());

                // FromClauseParser.g:217:82: ( sortByClause )?
                if (stream_sortByClause.hasNext()) {
                  adaptor.addChild(root_1, stream_sortByClause.nextTree());
                }
                stream_sortByClause.reset();

                adaptor.addChild(root_0, root_1);
              }
            }

            retval.tree = root_0;
          }
        }
        break;
        case 4:
          // FromClauseParser.g:218:4: sortByClause
        {
          pushFollow(FOLLOW_sortByClause_in_partitioningSpec1510);
          sortByClause101 = gHiveParser.sortByClause();

          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_sortByClause.add(sortByClause101.getTree());
          }

          // AST REWRITE
          // elements: sortByClause
          // token labels:
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          if (state.backtracking == 0) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval =
                new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

            root_0 = (CommonTree) adaptor.nil();
            // 218:17: -> ^( TOK_PARTITIONINGSPEC sortByClause )
            {
              // FromClauseParser.g:218:20: ^( TOK_PARTITIONINGSPEC sortByClause )
              {
                CommonTree root_1 = (CommonTree) adaptor.nil();
                root_1 = (CommonTree) adaptor.becomeRoot(
                    (CommonTree) adaptor.create(TOK_PARTITIONINGSPEC, "TOK_PARTITIONINGSPEC"), root_1);

                adaptor.addChild(root_1, stream_sortByClause.nextTree());

                adaptor.addChild(root_0, root_1);
              }
            }

            retval.tree = root_0;
          }
        }
        break;
        case 5:
          // FromClauseParser.g:219:4: clusterByClause
        {
          pushFollow(FOLLOW_clusterByClause_in_partitioningSpec1525);
          clusterByClause102 = gHiveParser.clusterByClause();

          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_clusterByClause.add(clusterByClause102.getTree());
          }

          // AST REWRITE
          // elements: clusterByClause
          // token labels:
          // rule labels: retval
          // token list labels:
          // rule list labels:
          // wildcard labels:
          if (state.backtracking == 0) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval =
                new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

            root_0 = (CommonTree) adaptor.nil();
            // 219:20: -> ^( TOK_PARTITIONINGSPEC clusterByClause )
            {
              // FromClauseParser.g:219:23: ^( TOK_PARTITIONINGSPEC clusterByClause )
              {
                CommonTree root_1 = (CommonTree) adaptor.nil();
                root_1 = (CommonTree) adaptor.becomeRoot(
                    (CommonTree) adaptor.create(TOK_PARTITIONINGSPEC, "TOK_PARTITIONINGSPEC"), root_1);

                adaptor.addChild(root_1, stream_clusterByClause.nextTree());

                adaptor.addChild(root_0, root_1);
              }
            }

            retval.tree = root_0;
          }
        }
        break;
      }
      retval.stop = input.LT(-1);

      if (state.backtracking == 0) {

        retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
      if (state.backtracking == 0) {
        gParent.popMsg(state);
      }
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "partitioningSpec"

  public static class partitionTableFunctionSource_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "partitionTableFunctionSource"
  // FromClauseParser.g:222:1: partitionTableFunctionSource : ( subQuerySource | tableSource | partitionedTableFunction );
  public final HiveParser_FromClauseParser.partitionTableFunctionSource_return partitionTableFunctionSource()
      throws RecognitionException {
    HiveParser_FromClauseParser.partitionTableFunctionSource_return retval =
        new HiveParser_FromClauseParser.partitionTableFunctionSource_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    HiveParser_FromClauseParser.subQuerySource_return subQuerySource103 = null;

    HiveParser_FromClauseParser.tableSource_return tableSource104 = null;

    HiveParser_FromClauseParser.partitionedTableFunction_return partitionedTableFunction105 = null;

    gParent.pushMsg("partitionTableFunctionSource clause", state);
    try {
      // FromClauseParser.g:225:4: ( subQuerySource | tableSource | partitionedTableFunction )
      int alt36 = 3;
      switch (input.LA(1)) {
        case LPAREN: {
          alt36 = 1;
        }
        break;
        case Identifier: {
          switch (input.LA(2)) {
            case LPAREN: {
              switch (input.LA(3)) {
                case KW_ON: {
                  alt36 = 3;
                }
                break;
                case StringLiteral: {
                  alt36 = 2;
                }
                break;
                default:
                  if (state.backtracking > 0) {
                    state.failed = true;
                    return retval;
                  }
                  NoViableAltException nvae = new NoViableAltException("", 36, 4, input);

                  throw nvae;
              }
            }
            break;
            case DOT:
            case Identifier:
            case KW_AS:
            case KW_CLUSTER:
            case KW_DISTRIBUTE:
            case KW_ORDER:
            case KW_PARTITION:
            case KW_SORT:
            case KW_TABLESAMPLE:
            case RPAREN: {
              alt36 = 2;
            }
            break;
            default:
              if (state.backtracking > 0) {
                state.failed = true;
                return retval;
              }
              NoViableAltException nvae = new NoViableAltException("", 36, 2, input);

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
          alt36 = 2;
        }
        break;
        default:
          if (state.backtracking > 0) {
            state.failed = true;
            return retval;
          }
          NoViableAltException nvae = new NoViableAltException("", 36, 0, input);

          throw nvae;
      }

      switch (alt36) {
        case 1:
          // FromClauseParser.g:226:4: subQuerySource
        {
          root_0 = (CommonTree) adaptor.nil();

          pushFollow(FOLLOW_subQuerySource_in_partitionTableFunctionSource1562);
          subQuerySource103 = subQuerySource();

          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            adaptor.addChild(root_0, subQuerySource103.getTree());
          }
        }
        break;
        case 2:
          // FromClauseParser.g:227:4: tableSource
        {
          root_0 = (CommonTree) adaptor.nil();

          pushFollow(FOLLOW_tableSource_in_partitionTableFunctionSource1569);
          tableSource104 = tableSource();

          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            adaptor.addChild(root_0, tableSource104.getTree());
          }
        }
        break;
        case 3:
          // FromClauseParser.g:228:4: partitionedTableFunction
        {
          root_0 = (CommonTree) adaptor.nil();

          pushFollow(FOLLOW_partitionedTableFunction_in_partitionTableFunctionSource1576);
          partitionedTableFunction105 = partitionedTableFunction();

          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            adaptor.addChild(root_0, partitionedTableFunction105.getTree());
          }
        }
        break;
      }
      retval.stop = input.LT(-1);

      if (state.backtracking == 0) {

        retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
      if (state.backtracking == 0) {
        gParent.popMsg(state);
      }
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "partitionTableFunctionSource"

  public static class partitionedTableFunction_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "partitionedTableFunction"
  // FromClauseParser.g:231:1: partitionedTableFunction : name= Identifier LPAREN KW_ON ptfsrc= partitionTableFunctionSource ( partitioningSpec )? ( ( Identifier LPAREN expression RPAREN )=> Identifier LPAREN expression RPAREN ( COMMA Identifier LPAREN expression RPAREN )* )? RPAREN (alias= Identifier )? -> ^( TOK_PTBLFUNCTION $name ( $alias)? partitionTableFunctionSource ( partitioningSpec )? ( expression )* ) ;
  public final HiveParser_FromClauseParser.partitionedTableFunction_return partitionedTableFunction()
      throws RecognitionException {
    HiveParser_FromClauseParser.partitionedTableFunction_return retval =
        new HiveParser_FromClauseParser.partitionedTableFunction_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token name = null;
    Token alias = null;
    Token LPAREN106 = null;
    Token KW_ON107 = null;
    Token Identifier109 = null;
    Token LPAREN110 = null;
    Token RPAREN112 = null;
    Token COMMA113 = null;
    Token Identifier114 = null;
    Token LPAREN115 = null;
    Token RPAREN117 = null;
    Token RPAREN118 = null;
    HiveParser_FromClauseParser.partitionTableFunctionSource_return ptfsrc = null;

    HiveParser_FromClauseParser.partitioningSpec_return partitioningSpec108 = null;

    HiveParser_IdentifiersParser.expression_return expression111 = null;

    HiveParser_IdentifiersParser.expression_return expression116 = null;

    CommonTree name_tree = null;
    CommonTree alias_tree = null;
    CommonTree LPAREN106_tree = null;
    CommonTree KW_ON107_tree = null;
    CommonTree Identifier109_tree = null;
    CommonTree LPAREN110_tree = null;
    CommonTree RPAREN112_tree = null;
    CommonTree COMMA113_tree = null;
    CommonTree Identifier114_tree = null;
    CommonTree LPAREN115_tree = null;
    CommonTree RPAREN117_tree = null;
    CommonTree RPAREN118_tree = null;
    RewriteRuleTokenStream stream_COMMA = new RewriteRuleTokenStream(adaptor, "token COMMA");
    RewriteRuleTokenStream stream_Identifier = new RewriteRuleTokenStream(adaptor, "token Identifier");
    RewriteRuleTokenStream stream_LPAREN = new RewriteRuleTokenStream(adaptor, "token LPAREN");
    RewriteRuleTokenStream stream_KW_ON = new RewriteRuleTokenStream(adaptor, "token KW_ON");
    RewriteRuleTokenStream stream_RPAREN = new RewriteRuleTokenStream(adaptor, "token RPAREN");
    RewriteRuleSubtreeStream stream_expression = new RewriteRuleSubtreeStream(adaptor, "rule expression");
    RewriteRuleSubtreeStream stream_partitionTableFunctionSource =
        new RewriteRuleSubtreeStream(adaptor, "rule partitionTableFunctionSource");
    RewriteRuleSubtreeStream stream_partitioningSpec = new RewriteRuleSubtreeStream(adaptor, "rule partitioningSpec");
    gParent.pushMsg("ptf clause", state);
    try {
      // FromClauseParser.g:234:4: (name= Identifier LPAREN KW_ON ptfsrc= partitionTableFunctionSource ( partitioningSpec )? ( ( Identifier LPAREN expression RPAREN )=> Identifier LPAREN expression RPAREN ( COMMA Identifier LPAREN expression RPAREN )* )? RPAREN (alias= Identifier )? -> ^( TOK_PTBLFUNCTION $name ( $alias)? partitionTableFunctionSource ( partitioningSpec )? ( expression )* ) )
      // FromClauseParser.g:235:4: name= Identifier LPAREN KW_ON ptfsrc= partitionTableFunctionSource ( partitioningSpec )? ( ( Identifier LPAREN expression RPAREN )=> Identifier LPAREN expression RPAREN ( COMMA Identifier LPAREN expression RPAREN )* )? RPAREN (alias= Identifier )?
      {
        name = (Token) match(input, Identifier, FOLLOW_Identifier_in_partitionedTableFunction1607);
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_Identifier.add(name);
        }

        LPAREN106 = (Token) match(input, LPAREN, FOLLOW_LPAREN_in_partitionedTableFunction1612);
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_LPAREN.add(LPAREN106);
        }

        KW_ON107 = (Token) match(input, KW_ON, FOLLOW_KW_ON_in_partitionedTableFunction1614);
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_KW_ON.add(KW_ON107);
        }

        pushFollow(FOLLOW_partitionTableFunctionSource_in_partitionedTableFunction1618);
        ptfsrc = partitionTableFunctionSource();

        state._fsp--;
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_partitionTableFunctionSource.add(ptfsrc.getTree());
        }

        // FromClauseParser.g:236:53: ( partitioningSpec )?
        int alt37 = 2;
        switch (input.LA(1)) {
          case KW_CLUSTER:
          case KW_DISTRIBUTE:
          case KW_ORDER:
          case KW_PARTITION:
          case KW_SORT: {
            alt37 = 1;
          }
          break;
        }

        switch (alt37) {
          case 1:
            // FromClauseParser.g:236:53: partitioningSpec
          {
            pushFollow(FOLLOW_partitioningSpec_in_partitionedTableFunction1620);
            partitioningSpec108 = partitioningSpec();

            state._fsp--;
            if (state.failed) {
              return retval;
            }
            if (state.backtracking == 0) {
              stream_partitioningSpec.add(partitioningSpec108.getTree());
            }
          }
          break;
        }

        // FromClauseParser.g:237:6: ( ( Identifier LPAREN expression RPAREN )=> Identifier LPAREN expression RPAREN ( COMMA Identifier LPAREN expression RPAREN )* )?
        int alt39 = 2;
        int LA39_0 = input.LA(1);

        if ((LA39_0 == Identifier) && (synpred4_FromClauseParser())) {
          alt39 = 1;
        }
        switch (alt39) {
          case 1:
            // FromClauseParser.g:237:7: ( Identifier LPAREN expression RPAREN )=> Identifier LPAREN expression RPAREN ( COMMA Identifier LPAREN expression RPAREN )*
          {
            Identifier109 = (Token) match(input, Identifier, FOLLOW_Identifier_in_partitionedTableFunction1642);
            if (state.failed) {
              return retval;
            }
            if (state.backtracking == 0) {
              stream_Identifier.add(Identifier109);
            }

            LPAREN110 = (Token) match(input, LPAREN, FOLLOW_LPAREN_in_partitionedTableFunction1644);
            if (state.failed) {
              return retval;
            }
            if (state.backtracking == 0) {
              stream_LPAREN.add(LPAREN110);
            }

            pushFollow(FOLLOW_expression_in_partitionedTableFunction1646);
            expression111 = gHiveParser.expression();

            state._fsp--;
            if (state.failed) {
              return retval;
            }
            if (state.backtracking == 0) {
              stream_expression.add(expression111.getTree());
            }

            RPAREN112 = (Token) match(input, RPAREN, FOLLOW_RPAREN_in_partitionedTableFunction1648);
            if (state.failed) {
              return retval;
            }
            if (state.backtracking == 0) {
              stream_RPAREN.add(RPAREN112);
            }

            // FromClauseParser.g:237:85: ( COMMA Identifier LPAREN expression RPAREN )*
            loop38:
            do {
              int alt38 = 2;
              switch (input.LA(1)) {
                case COMMA: {
                  alt38 = 1;
                }
                break;
              }

              switch (alt38) {
                case 1:
                  // FromClauseParser.g:237:87: COMMA Identifier LPAREN expression RPAREN
                {
                  COMMA113 = (Token) match(input, COMMA, FOLLOW_COMMA_in_partitionedTableFunction1652);
                  if (state.failed) {
                    return retval;
                  }
                  if (state.backtracking == 0) {
                    stream_COMMA.add(COMMA113);
                  }

                  Identifier114 = (Token) match(input, Identifier, FOLLOW_Identifier_in_partitionedTableFunction1654);
                  if (state.failed) {
                    return retval;
                  }
                  if (state.backtracking == 0) {
                    stream_Identifier.add(Identifier114);
                  }

                  LPAREN115 = (Token) match(input, LPAREN, FOLLOW_LPAREN_in_partitionedTableFunction1656);
                  if (state.failed) {
                    return retval;
                  }
                  if (state.backtracking == 0) {
                    stream_LPAREN.add(LPAREN115);
                  }

                  pushFollow(FOLLOW_expression_in_partitionedTableFunction1658);
                  expression116 = gHiveParser.expression();

                  state._fsp--;
                  if (state.failed) {
                    return retval;
                  }
                  if (state.backtracking == 0) {
                    stream_expression.add(expression116.getTree());
                  }

                  RPAREN117 = (Token) match(input, RPAREN, FOLLOW_RPAREN_in_partitionedTableFunction1660);
                  if (state.failed) {
                    return retval;
                  }
                  if (state.backtracking == 0) {
                    stream_RPAREN.add(RPAREN117);
                  }
                }
                break;

                default:
                  break loop38;
              }
            } while (true);
          }
          break;
        }

        RPAREN118 = (Token) match(input, RPAREN, FOLLOW_RPAREN_in_partitionedTableFunction1670);
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_RPAREN.add(RPAREN118);
        }

        // FromClauseParser.g:238:16: (alias= Identifier )?
        int alt40 = 2;
        alt40 = dfa40.predict(input);
        switch (alt40) {
          case 1:
            // FromClauseParser.g:238:16: alias= Identifier
          {
            alias = (Token) match(input, Identifier, FOLLOW_Identifier_in_partitionedTableFunction1674);
            if (state.failed) {
              return retval;
            }
            if (state.backtracking == 0) {
              stream_Identifier.add(alias);
            }
          }
          break;
        }

        // AST REWRITE
        // elements: expression, name, alias, partitioningSpec, partitionTableFunctionSource
        // token labels: name, alias
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        if (state.backtracking == 0) {

          retval.tree = root_0;
          RewriteRuleTokenStream stream_name = new RewriteRuleTokenStream(adaptor, "token name", name);
          RewriteRuleTokenStream stream_alias = new RewriteRuleTokenStream(adaptor, "token alias", alias);
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 239:4: -> ^( TOK_PTBLFUNCTION $name ( $alias)? partitionTableFunctionSource ( partitioningSpec )? ( expression )* )
          {
            // FromClauseParser.g:239:9: ^( TOK_PTBLFUNCTION $name ( $alias)? partitionTableFunctionSource ( partitioningSpec )? ( expression )* )
            {
              CommonTree root_1 = (CommonTree) adaptor.nil();
              root_1 =
                  (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_PTBLFUNCTION, "TOK_PTBLFUNCTION"),
                      root_1);

              adaptor.addChild(root_1, stream_name.nextNode());

              // FromClauseParser.g:239:35: ( $alias)?
              if (stream_alias.hasNext()) {
                adaptor.addChild(root_1, stream_alias.nextNode());
              }
              stream_alias.reset();

              adaptor.addChild(root_1, stream_partitionTableFunctionSource.nextTree());

              // FromClauseParser.g:239:71: ( partitioningSpec )?
              if (stream_partitioningSpec.hasNext()) {
                adaptor.addChild(root_1, stream_partitioningSpec.nextTree());
              }
              stream_partitioningSpec.reset();

              // FromClauseParser.g:239:89: ( expression )*
              while (stream_expression.hasNext()) {
                adaptor.addChild(root_1, stream_expression.nextTree());
              }
              stream_expression.reset();

              adaptor.addChild(root_0, root_1);
            }
          }

          retval.tree = root_0;
        }
      }

      retval.stop = input.LT(-1);

      if (state.backtracking == 0) {

        retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
      if (state.backtracking == 0) {
        gParent.popMsg(state);
      }
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "partitionedTableFunction"

  public static class whereClause_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "whereClause"
  // FromClauseParser.g:244:1: whereClause : KW_WHERE searchCondition -> ^( TOK_WHERE searchCondition ) ;
  public final HiveParser_FromClauseParser.whereClause_return whereClause() throws RecognitionException {
    HiveParser_FromClauseParser.whereClause_return retval = new HiveParser_FromClauseParser.whereClause_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token KW_WHERE119 = null;
    HiveParser_FromClauseParser.searchCondition_return searchCondition120 = null;

    CommonTree KW_WHERE119_tree = null;
    RewriteRuleTokenStream stream_KW_WHERE = new RewriteRuleTokenStream(adaptor, "token KW_WHERE");
    RewriteRuleSubtreeStream stream_searchCondition = new RewriteRuleSubtreeStream(adaptor, "rule searchCondition");
    gParent.pushMsg("where clause", state);
    try {
      // FromClauseParser.g:247:5: ( KW_WHERE searchCondition -> ^( TOK_WHERE searchCondition ) )
      // FromClauseParser.g:248:5: KW_WHERE searchCondition
      {
        KW_WHERE119 = (Token) match(input, KW_WHERE, FOLLOW_KW_WHERE_in_whereClause1734);
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_KW_WHERE.add(KW_WHERE119);
        }

        pushFollow(FOLLOW_searchCondition_in_whereClause1736);
        searchCondition120 = searchCondition();

        state._fsp--;
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_searchCondition.add(searchCondition120.getTree());
        }

        // AST REWRITE
        // elements: searchCondition
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        if (state.backtracking == 0) {

          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 248:30: -> ^( TOK_WHERE searchCondition )
          {
            // FromClauseParser.g:248:33: ^( TOK_WHERE searchCondition )
            {
              CommonTree root_1 = (CommonTree) adaptor.nil();
              root_1 = (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_WHERE, "TOK_WHERE"), root_1);

              adaptor.addChild(root_1, stream_searchCondition.nextTree());

              adaptor.addChild(root_0, root_1);
            }
          }

          retval.tree = root_0;
        }
      }

      retval.stop = input.LT(-1);

      if (state.backtracking == 0) {

        retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
      if (state.backtracking == 0) {
        gParent.popMsg(state);
      }
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "whereClause"

  public static class searchCondition_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "searchCondition"
  // FromClauseParser.g:251:1: searchCondition : expression ;
  public final HiveParser_FromClauseParser.searchCondition_return searchCondition() throws RecognitionException {
    HiveParser_FromClauseParser.searchCondition_return retval =
        new HiveParser_FromClauseParser.searchCondition_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    HiveParser_IdentifiersParser.expression_return expression121 = null;

    gParent.pushMsg("search condition", state);
    try {
      // FromClauseParser.g:254:5: ( expression )
      // FromClauseParser.g:255:5: expression
      {
        root_0 = (CommonTree) adaptor.nil();

        pushFollow(FOLLOW_expression_in_searchCondition1775);
        expression121 = gHiveParser.expression();

        state._fsp--;
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          adaptor.addChild(root_0, expression121.getTree());
        }
      }

      retval.stop = input.LT(-1);

      if (state.backtracking == 0) {

        retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
      if (state.backtracking == 0) {
        gParent.popMsg(state);
      }
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "searchCondition"

  public static class valueRowConstructor_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "valueRowConstructor"
  // FromClauseParser.g:264:1: valueRowConstructor : LPAREN precedenceUnaryPrefixExpression ( COMMA precedenceUnaryPrefixExpression )* RPAREN -> ^( TOK_VALUE_ROW ( precedenceUnaryPrefixExpression )+ ) ;
  public final HiveParser_FromClauseParser.valueRowConstructor_return valueRowConstructor()
      throws RecognitionException {
    HiveParser_FromClauseParser.valueRowConstructor_return retval =
        new HiveParser_FromClauseParser.valueRowConstructor_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token LPAREN122 = null;
    Token COMMA124 = null;
    Token RPAREN126 = null;
    HiveParser_IdentifiersParser.precedenceUnaryPrefixExpression_return precedenceUnaryPrefixExpression123 = null;

    HiveParser_IdentifiersParser.precedenceUnaryPrefixExpression_return precedenceUnaryPrefixExpression125 = null;

    CommonTree LPAREN122_tree = null;
    CommonTree COMMA124_tree = null;
    CommonTree RPAREN126_tree = null;
    RewriteRuleTokenStream stream_COMMA = new RewriteRuleTokenStream(adaptor, "token COMMA");
    RewriteRuleTokenStream stream_LPAREN = new RewriteRuleTokenStream(adaptor, "token LPAREN");
    RewriteRuleTokenStream stream_RPAREN = new RewriteRuleTokenStream(adaptor, "token RPAREN");
    RewriteRuleSubtreeStream stream_precedenceUnaryPrefixExpression =
        new RewriteRuleSubtreeStream(adaptor, "rule precedenceUnaryPrefixExpression");
    try {
      // FromClauseParser.g:265:5: ( LPAREN precedenceUnaryPrefixExpression ( COMMA precedenceUnaryPrefixExpression )* RPAREN -> ^( TOK_VALUE_ROW ( precedenceUnaryPrefixExpression )+ ) )
      // FromClauseParser.g:266:5: LPAREN precedenceUnaryPrefixExpression ( COMMA precedenceUnaryPrefixExpression )* RPAREN
      {
        LPAREN122 = (Token) match(input, LPAREN, FOLLOW_LPAREN_in_valueRowConstructor1802);
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_LPAREN.add(LPAREN122);
        }

        pushFollow(FOLLOW_precedenceUnaryPrefixExpression_in_valueRowConstructor1804);
        precedenceUnaryPrefixExpression123 = gHiveParser.precedenceUnaryPrefixExpression();

        state._fsp--;
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_precedenceUnaryPrefixExpression.add(precedenceUnaryPrefixExpression123.getTree());
        }

        // FromClauseParser.g:266:44: ( COMMA precedenceUnaryPrefixExpression )*
        loop41:
        do {
          int alt41 = 2;
          switch (input.LA(1)) {
            case COMMA: {
              alt41 = 1;
            }
            break;
          }

          switch (alt41) {
            case 1:
              // FromClauseParser.g:266:45: COMMA precedenceUnaryPrefixExpression
            {
              COMMA124 = (Token) match(input, COMMA, FOLLOW_COMMA_in_valueRowConstructor1807);
              if (state.failed) {
                return retval;
              }
              if (state.backtracking == 0) {
                stream_COMMA.add(COMMA124);
              }

              pushFollow(FOLLOW_precedenceUnaryPrefixExpression_in_valueRowConstructor1809);
              precedenceUnaryPrefixExpression125 = gHiveParser.precedenceUnaryPrefixExpression();

              state._fsp--;
              if (state.failed) {
                return retval;
              }
              if (state.backtracking == 0) {
                stream_precedenceUnaryPrefixExpression.add(precedenceUnaryPrefixExpression125.getTree());
              }
            }
            break;

            default:
              break loop41;
          }
        } while (true);

        RPAREN126 = (Token) match(input, RPAREN, FOLLOW_RPAREN_in_valueRowConstructor1813);
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_RPAREN.add(RPAREN126);
        }

        // AST REWRITE
        // elements: precedenceUnaryPrefixExpression
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        if (state.backtracking == 0) {

          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 266:92: -> ^( TOK_VALUE_ROW ( precedenceUnaryPrefixExpression )+ )
          {
            // FromClauseParser.g:266:95: ^( TOK_VALUE_ROW ( precedenceUnaryPrefixExpression )+ )
            {
              CommonTree root_1 = (CommonTree) adaptor.nil();
              root_1 =
                  (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_VALUE_ROW, "TOK_VALUE_ROW"), root_1);

              if (!(stream_precedenceUnaryPrefixExpression.hasNext())) {
                throw new RewriteEarlyExitException();
              }
              while (stream_precedenceUnaryPrefixExpression.hasNext()) {
                adaptor.addChild(root_1, stream_precedenceUnaryPrefixExpression.nextTree());
              }
              stream_precedenceUnaryPrefixExpression.reset();

              adaptor.addChild(root_0, root_1);
            }
          }

          retval.tree = root_0;
        }
      }

      retval.stop = input.LT(-1);

      if (state.backtracking == 0) {

        retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "valueRowConstructor"

  public static class valuesTableConstructor_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "valuesTableConstructor"
  // FromClauseParser.g:269:1: valuesTableConstructor : valueRowConstructor ( COMMA valueRowConstructor )* -> ^( TOK_VALUES_TABLE ( valueRowConstructor )+ ) ;
  public final HiveParser_FromClauseParser.valuesTableConstructor_return valuesTableConstructor()
      throws RecognitionException {
    HiveParser_FromClauseParser.valuesTableConstructor_return retval =
        new HiveParser_FromClauseParser.valuesTableConstructor_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token COMMA128 = null;
    HiveParser_FromClauseParser.valueRowConstructor_return valueRowConstructor127 = null;

    HiveParser_FromClauseParser.valueRowConstructor_return valueRowConstructor129 = null;

    CommonTree COMMA128_tree = null;
    RewriteRuleTokenStream stream_COMMA = new RewriteRuleTokenStream(adaptor, "token COMMA");
    RewriteRuleSubtreeStream stream_valueRowConstructor =
        new RewriteRuleSubtreeStream(adaptor, "rule valueRowConstructor");
    try {
      // FromClauseParser.g:270:5: ( valueRowConstructor ( COMMA valueRowConstructor )* -> ^( TOK_VALUES_TABLE ( valueRowConstructor )+ ) )
      // FromClauseParser.g:271:5: valueRowConstructor ( COMMA valueRowConstructor )*
      {
        pushFollow(FOLLOW_valueRowConstructor_in_valuesTableConstructor1843);
        valueRowConstructor127 = valueRowConstructor();

        state._fsp--;
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_valueRowConstructor.add(valueRowConstructor127.getTree());
        }

        // FromClauseParser.g:271:25: ( COMMA valueRowConstructor )*
        loop42:
        do {
          int alt42 = 2;
          switch (input.LA(1)) {
            case COMMA: {
              alt42 = 1;
            }
            break;
          }

          switch (alt42) {
            case 1:
              // FromClauseParser.g:271:26: COMMA valueRowConstructor
            {
              COMMA128 = (Token) match(input, COMMA, FOLLOW_COMMA_in_valuesTableConstructor1846);
              if (state.failed) {
                return retval;
              }
              if (state.backtracking == 0) {
                stream_COMMA.add(COMMA128);
              }

              pushFollow(FOLLOW_valueRowConstructor_in_valuesTableConstructor1848);
              valueRowConstructor129 = valueRowConstructor();

              state._fsp--;
              if (state.failed) {
                return retval;
              }
              if (state.backtracking == 0) {
                stream_valueRowConstructor.add(valueRowConstructor129.getTree());
              }
            }
            break;

            default:
              break loop42;
          }
        } while (true);

        // AST REWRITE
        // elements: valueRowConstructor
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        if (state.backtracking == 0) {

          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 271:54: -> ^( TOK_VALUES_TABLE ( valueRowConstructor )+ )
          {
            // FromClauseParser.g:271:57: ^( TOK_VALUES_TABLE ( valueRowConstructor )+ )
            {
              CommonTree root_1 = (CommonTree) adaptor.nil();
              root_1 =
                  (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_VALUES_TABLE, "TOK_VALUES_TABLE"),
                      root_1);

              if (!(stream_valueRowConstructor.hasNext())) {
                throw new RewriteEarlyExitException();
              }
              while (stream_valueRowConstructor.hasNext()) {
                adaptor.addChild(root_1, stream_valueRowConstructor.nextTree());
              }
              stream_valueRowConstructor.reset();

              adaptor.addChild(root_0, root_1);
            }
          }

          retval.tree = root_0;
        }
      }

      retval.stop = input.LT(-1);

      if (state.backtracking == 0) {

        retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "valuesTableConstructor"

  public static class valuesClause_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "valuesClause"
  // FromClauseParser.g:279:1: valuesClause : KW_VALUES valuesTableConstructor -> valuesTableConstructor ;
  public final HiveParser_FromClauseParser.valuesClause_return valuesClause() throws RecognitionException {
    HiveParser_FromClauseParser.valuesClause_return retval = new HiveParser_FromClauseParser.valuesClause_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token KW_VALUES130 = null;
    HiveParser_FromClauseParser.valuesTableConstructor_return valuesTableConstructor131 = null;

    CommonTree KW_VALUES130_tree = null;
    RewriteRuleTokenStream stream_KW_VALUES = new RewriteRuleTokenStream(adaptor, "token KW_VALUES");
    RewriteRuleSubtreeStream stream_valuesTableConstructor =
        new RewriteRuleSubtreeStream(adaptor, "rule valuesTableConstructor");
    try {
      // FromClauseParser.g:280:5: ( KW_VALUES valuesTableConstructor -> valuesTableConstructor )
      // FromClauseParser.g:281:5: KW_VALUES valuesTableConstructor
      {
        KW_VALUES130 = (Token) match(input, KW_VALUES, FOLLOW_KW_VALUES_in_valuesClause1882);
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_KW_VALUES.add(KW_VALUES130);
        }

        pushFollow(FOLLOW_valuesTableConstructor_in_valuesClause1884);
        valuesTableConstructor131 = valuesTableConstructor();

        state._fsp--;
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_valuesTableConstructor.add(valuesTableConstructor131.getTree());
        }

        // AST REWRITE
        // elements: valuesTableConstructor
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        if (state.backtracking == 0) {

          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 281:38: -> valuesTableConstructor
          {
            adaptor.addChild(root_0, stream_valuesTableConstructor.nextTree());
          }

          retval.tree = root_0;
        }
      }

      retval.stop = input.LT(-1);

      if (state.backtracking == 0) {

        retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "valuesClause"

  public static class virtualTableSource_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "virtualTableSource"
  // FromClauseParser.g:288:1: virtualTableSource : LPAREN valuesClause RPAREN tableNameColList -> ^( TOK_VIRTUAL_TABLE tableNameColList valuesClause ) ;
  public final HiveParser_FromClauseParser.virtualTableSource_return virtualTableSource() throws RecognitionException {
    HiveParser_FromClauseParser.virtualTableSource_return retval =
        new HiveParser_FromClauseParser.virtualTableSource_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token LPAREN132 = null;
    Token RPAREN134 = null;
    HiveParser_FromClauseParser.valuesClause_return valuesClause133 = null;

    HiveParser_FromClauseParser.tableNameColList_return tableNameColList135 = null;

    CommonTree LPAREN132_tree = null;
    CommonTree RPAREN134_tree = null;
    RewriteRuleTokenStream stream_LPAREN = new RewriteRuleTokenStream(adaptor, "token LPAREN");
    RewriteRuleTokenStream stream_RPAREN = new RewriteRuleTokenStream(adaptor, "token RPAREN");
    RewriteRuleSubtreeStream stream_valuesClause = new RewriteRuleSubtreeStream(adaptor, "rule valuesClause");
    RewriteRuleSubtreeStream stream_tableNameColList = new RewriteRuleSubtreeStream(adaptor, "rule tableNameColList");
    try {
      // FromClauseParser.g:289:5: ( LPAREN valuesClause RPAREN tableNameColList -> ^( TOK_VIRTUAL_TABLE tableNameColList valuesClause ) )
      // FromClauseParser.g:290:5: LPAREN valuesClause RPAREN tableNameColList
      {
        LPAREN132 = (Token) match(input, LPAREN, FOLLOW_LPAREN_in_virtualTableSource1911);
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_LPAREN.add(LPAREN132);
        }

        pushFollow(FOLLOW_valuesClause_in_virtualTableSource1913);
        valuesClause133 = valuesClause();

        state._fsp--;
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_valuesClause.add(valuesClause133.getTree());
        }

        RPAREN134 = (Token) match(input, RPAREN, FOLLOW_RPAREN_in_virtualTableSource1915);
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_RPAREN.add(RPAREN134);
        }

        pushFollow(FOLLOW_tableNameColList_in_virtualTableSource1917);
        tableNameColList135 = tableNameColList();

        state._fsp--;
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_tableNameColList.add(tableNameColList135.getTree());
        }

        // AST REWRITE
        // elements: tableNameColList, valuesClause
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        if (state.backtracking == 0) {

          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 290:49: -> ^( TOK_VIRTUAL_TABLE tableNameColList valuesClause )
          {
            // FromClauseParser.g:290:52: ^( TOK_VIRTUAL_TABLE tableNameColList valuesClause )
            {
              CommonTree root_1 = (CommonTree) adaptor.nil();
              root_1 =
                  (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_VIRTUAL_TABLE, "TOK_VIRTUAL_TABLE"),
                      root_1);

              adaptor.addChild(root_1, stream_tableNameColList.nextTree());

              adaptor.addChild(root_1, stream_valuesClause.nextTree());

              adaptor.addChild(root_0, root_1);
            }
          }

          retval.tree = root_0;
        }
      }

      retval.stop = input.LT(-1);

      if (state.backtracking == 0) {

        retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "virtualTableSource"

  public static class tableNameColList_return extends ParserRuleReturnScope {
    CommonTree tree;

    public Object getTree() {
      return tree;
    }
  }

  ;

  // $ANTLR start "tableNameColList"
  // FromClauseParser.g:296:1: tableNameColList : ( KW_AS )? identifier LPAREN identifier ( COMMA identifier )* RPAREN -> ^( TOK_VIRTUAL_TABREF ^( TOK_TABNAME identifier ) ^( TOK_COL_NAME ( identifier )+ ) ) ;
  public final HiveParser_FromClauseParser.tableNameColList_return tableNameColList() throws RecognitionException {
    HiveParser_FromClauseParser.tableNameColList_return retval =
        new HiveParser_FromClauseParser.tableNameColList_return();
    retval.start = input.LT(1);

    CommonTree root_0 = null;

    Token KW_AS136 = null;
    Token LPAREN138 = null;
    Token COMMA140 = null;
    Token RPAREN142 = null;
    HiveParser_IdentifiersParser.identifier_return identifier137 = null;

    HiveParser_IdentifiersParser.identifier_return identifier139 = null;

    HiveParser_IdentifiersParser.identifier_return identifier141 = null;

    CommonTree KW_AS136_tree = null;
    CommonTree LPAREN138_tree = null;
    CommonTree COMMA140_tree = null;
    CommonTree RPAREN142_tree = null;
    RewriteRuleTokenStream stream_COMMA = new RewriteRuleTokenStream(adaptor, "token COMMA");
    RewriteRuleTokenStream stream_LPAREN = new RewriteRuleTokenStream(adaptor, "token LPAREN");
    RewriteRuleTokenStream stream_RPAREN = new RewriteRuleTokenStream(adaptor, "token RPAREN");
    RewriteRuleTokenStream stream_KW_AS = new RewriteRuleTokenStream(adaptor, "token KW_AS");
    RewriteRuleSubtreeStream stream_identifier = new RewriteRuleSubtreeStream(adaptor, "rule identifier");
    try {
      // FromClauseParser.g:297:5: ( ( KW_AS )? identifier LPAREN identifier ( COMMA identifier )* RPAREN -> ^( TOK_VIRTUAL_TABREF ^( TOK_TABNAME identifier ) ^( TOK_COL_NAME ( identifier )+ ) ) )
      // FromClauseParser.g:298:5: ( KW_AS )? identifier LPAREN identifier ( COMMA identifier )* RPAREN
      {
        // FromClauseParser.g:298:5: ( KW_AS )?
        int alt43 = 2;
        switch (input.LA(1)) {
          case KW_AS: {
            switch (input.LA(2)) {
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
                alt43 = 1;
              }
              break;
            }
          }
          break;
        }

        switch (alt43) {
          case 1:
            // FromClauseParser.g:298:5: KW_AS
          {
            KW_AS136 = (Token) match(input, KW_AS, FOLLOW_KW_AS_in_tableNameColList1949);
            if (state.failed) {
              return retval;
            }
            if (state.backtracking == 0) {
              stream_KW_AS.add(KW_AS136);
            }
          }
          break;
        }

        pushFollow(FOLLOW_identifier_in_tableNameColList1952);
        identifier137 = gHiveParser.identifier();

        state._fsp--;
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_identifier.add(identifier137.getTree());
        }

        LPAREN138 = (Token) match(input, LPAREN, FOLLOW_LPAREN_in_tableNameColList1954);
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_LPAREN.add(LPAREN138);
        }

        pushFollow(FOLLOW_identifier_in_tableNameColList1956);
        identifier139 = gHiveParser.identifier();

        state._fsp--;
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_identifier.add(identifier139.getTree());
        }

        // FromClauseParser.g:298:41: ( COMMA identifier )*
        loop44:
        do {
          int alt44 = 2;
          switch (input.LA(1)) {
            case COMMA: {
              alt44 = 1;
            }
            break;
          }

          switch (alt44) {
            case 1:
              // FromClauseParser.g:298:42: COMMA identifier
            {
              COMMA140 = (Token) match(input, COMMA, FOLLOW_COMMA_in_tableNameColList1959);
              if (state.failed) {
                return retval;
              }
              if (state.backtracking == 0) {
                stream_COMMA.add(COMMA140);
              }

              pushFollow(FOLLOW_identifier_in_tableNameColList1961);
              identifier141 = gHiveParser.identifier();

              state._fsp--;
              if (state.failed) {
                return retval;
              }
              if (state.backtracking == 0) {
                stream_identifier.add(identifier141.getTree());
              }
            }
            break;

            default:
              break loop44;
          }
        } while (true);

        RPAREN142 = (Token) match(input, RPAREN, FOLLOW_RPAREN_in_tableNameColList1965);
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_RPAREN.add(RPAREN142);
        }

        // AST REWRITE
        // elements: identifier, identifier
        // token labels:
        // rule labels: retval
        // token list labels:
        // rule list labels:
        // wildcard labels:
        if (state.backtracking == 0) {

          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_retval =
              new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);

          root_0 = (CommonTree) adaptor.nil();
          // 298:68: -> ^( TOK_VIRTUAL_TABREF ^( TOK_TABNAME identifier ) ^( TOK_COL_NAME ( identifier )+ ) )
          {
            // FromClauseParser.g:298:71: ^( TOK_VIRTUAL_TABREF ^( TOK_TABNAME identifier ) ^( TOK_COL_NAME ( identifier )+ ) )
            {
              CommonTree root_1 = (CommonTree) adaptor.nil();
              root_1 =
                  (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_VIRTUAL_TABREF, "TOK_VIRTUAL_TABREF"),
                      root_1);

              // FromClauseParser.g:298:92: ^( TOK_TABNAME identifier )
              {
                CommonTree root_2 = (CommonTree) adaptor.nil();
                root_2 =
                    (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_TABNAME, "TOK_TABNAME"), root_2);

                adaptor.addChild(root_2, stream_identifier.nextTree());

                adaptor.addChild(root_1, root_2);
              }

              // FromClauseParser.g:298:118: ^( TOK_COL_NAME ( identifier )+ )
              {
                CommonTree root_2 = (CommonTree) adaptor.nil();
                root_2 =
                    (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(TOK_COL_NAME, "TOK_COL_NAME"), root_2);

                if (!(stream_identifier.hasNext())) {
                  throw new RewriteEarlyExitException();
                }
                while (stream_identifier.hasNext()) {
                  adaptor.addChild(root_2, stream_identifier.nextTree());
                }
                stream_identifier.reset();

                adaptor.addChild(root_1, root_2);
              }

              adaptor.addChild(root_0, root_1);
            }
          }

          retval.tree = root_0;
        }
      }

      retval.stop = input.LT(-1);

      if (state.backtracking == 0) {

        retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
    } catch (RecognitionException e) {
      throw e;
    } finally {
      // do for sure before leaving
    }
    return retval;
  }
  // $ANTLR end "tableNameColList"

  // $ANTLR start synpred1_FromClauseParser
  public final void synpred1_FromClauseParser_fragment() throws RecognitionException {
    // FromClauseParser.g:129:69: ( COMMA )
    // FromClauseParser.g:129:70: COMMA
    {
      match(input, COMMA, FOLLOW_COMMA_in_synpred1_FromClauseParser727);
      if (state.failed) {
        return;
      }
    }
  }
  // $ANTLR end synpred1_FromClauseParser

  // $ANTLR start synpred2_FromClauseParser
  public final void synpred2_FromClauseParser_fragment() throws RecognitionException {
    // FromClauseParser.g:132:60: ( COMMA )
    // FromClauseParser.g:132:61: COMMA
    {
      match(input, COMMA, FOLLOW_COMMA_in_synpred2_FromClauseParser780);
      if (state.failed) {
        return;
      }
    }
  }
  // $ANTLR end synpred2_FromClauseParser

  // $ANTLR start synpred3_FromClauseParser
  public final void synpred3_FromClauseParser_fragment() throws RecognitionException {
    // FromClauseParser.g:147:6: ( Identifier LPAREN )
    // FromClauseParser.g:147:7: Identifier LPAREN
    {
      match(input, Identifier, FOLLOW_Identifier_in_synpred3_FromClauseParser881);
      if (state.failed) {
        return;
      }

      match(input, LPAREN, FOLLOW_LPAREN_in_synpred3_FromClauseParser883);
      if (state.failed) {
        return;
      }
    }
  }
  // $ANTLR end synpred3_FromClauseParser

  // $ANTLR start synpred4_FromClauseParser
  public final void synpred4_FromClauseParser_fragment() throws RecognitionException {
    // FromClauseParser.g:237:7: ( Identifier LPAREN expression RPAREN )
    // FromClauseParser.g:237:8: Identifier LPAREN expression RPAREN
    {
      match(input, Identifier, FOLLOW_Identifier_in_synpred4_FromClauseParser1630);
      if (state.failed) {
        return;
      }

      match(input, LPAREN, FOLLOW_LPAREN_in_synpred4_FromClauseParser1632);
      if (state.failed) {
        return;
      }

      pushFollow(FOLLOW_expression_in_synpred4_FromClauseParser1634);
      gHiveParser.expression();

      state._fsp--;
      if (state.failed) {
        return;
      }

      match(input, RPAREN, FOLLOW_RPAREN_in_synpred4_FromClauseParser1636);
      if (state.failed) {
        return;
      }
    }
  }
  // $ANTLR end synpred4_FromClauseParser

  // Delegated rules

  public final boolean synpred3_FromClauseParser() {
    state.backtracking++;
    int start = input.mark();
    try {
      synpred3_FromClauseParser_fragment(); // can never throw exception
    } catch (RecognitionException re) {
      System.err.println("impossible: " + re);
    }
    boolean success = !state.failed;
    input.rewind(start);
    state.backtracking--;
    state.failed = false;
    return success;
  }

  public final boolean synpred4_FromClauseParser() {
    state.backtracking++;
    int start = input.mark();
    try {
      synpred4_FromClauseParser_fragment(); // can never throw exception
    } catch (RecognitionException re) {
      System.err.println("impossible: " + re);
    }
    boolean success = !state.failed;
    input.rewind(start);
    state.backtracking--;
    state.failed = false;
    return success;
  }

  public final boolean synpred2_FromClauseParser() {
    state.backtracking++;
    int start = input.mark();
    try {
      synpred2_FromClauseParser_fragment(); // can never throw exception
    } catch (RecognitionException re) {
      System.err.println("impossible: " + re);
    }
    boolean success = !state.failed;
    input.rewind(start);
    state.backtracking--;
    state.failed = false;
    return success;
  }

  public final boolean synpred1_FromClauseParser() {
    state.backtracking++;
    int start = input.mark();
    try {
      synpred1_FromClauseParser_fragment(); // can never throw exception
    } catch (RecognitionException re) {
      System.err.println("impossible: " + re);
    }
    boolean success = !state.failed;
    input.rewind(start);
    state.backtracking--;
    state.failed = false;
    return success;
  }

  protected DFA14 dfa14 = new DFA14(this);
  protected DFA16 dfa16 = new DFA16(this);
  protected DFA19 dfa19 = new DFA19(this);
  protected DFA26 dfa26 = new DFA26(this);
  protected DFA29 dfa29 = new DFA29(this);
  protected DFA30 dfa30 = new DFA30(this);
  protected DFA32 dfa32 = new DFA32(this);
  protected DFA40 dfa40 = new DFA40(this);
  static final String DFA14_eotS = "\133\uffff";
  static final String DFA14_eofS = "\1\1\132\uffff";
  static final String DFA14_minS = "\1\12\2\uffff\1\32\27\uffff\2\0\76\uffff";
  static final String DFA14_maxS = "\1\u012d\2\uffff\1\u0122\27\uffff\2\0\76\uffff";
  static final String DFA14_acceptS = "\1\uffff\1\2\72\uffff\1\1\36\uffff";
  static final String DFA14_specialS = "\33\uffff\1\0\1\1\76\uffff}>";
  static final String[] DFA14_transitionS = {"\1\3\52\uffff\1\1\15\uffff\1\1\30\uffff\1\1\32\uffff\1\1\3\uffff"
      + "\1\1\1\uffff\1\1\10\uffff\1\1\3\uffff\1\1\6\uffff\1\1\2\uffff"
      + "\2\1\2\uffff\1\1\11\uffff\1\1\15\uffff\1\1\2\uffff\1\1\33\uffff"
      + "\1\1\11\uffff\1\1\10\uffff\1\1\13\uffff\1\1\32\uffff\1\1\21"
      + "\uffff\1\1\1\uffff\1\1\4\uffff\1\1\12\uffff\1\1", "", "",
      "\1\33\6\34\1\uffff\17\34\2\uffff\1\34\1\uffff\4\34\1\uffff"
          + "\6\34\1\uffff\2\34\1\uffff\1\34\3\uffff\2\34\1\uffff\20\34\1"
          + "\uffff\4\34\1\uffff\1\34\1\uffff\1\34\1\uffff\4\34\1\uffff\10"
          + "\34\1\uffff\3\34\1\uffff\1\34\1\uffff\4\34\1\uffff\2\34\1\uffff"
          + "\20\34\1\uffff\4\34\1\uffff\12\34\2\uffff\4\34\1\uffff\3\34"
          + "\1\uffff\4\34\1\uffff\1\34\1\uffff\5\34\1\uffff\2\34\1\uffff"
          + "\5\34\2\uffff\14\34\1\uffff\22\34\1\uffff\25\34\1\uffff\3\34"
          + "\1\uffff\5\34\1\uffff\4\34\1\uffff\3\34\1\uffff\14\34\1\uffff"
          + "\1\34\2\uffff\1\34\1\uffff\1\34\3\uffff\1\1", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "\1\uffff", "\1\uffff", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};

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
      return "()* loopback of 129:68: ( ( COMMA )=> COMMA identifier )*";
    }

    public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
      TokenStream input = (TokenStream) _input;
      int _s = s;
      switch (s) {
        case 0:
          int LA14_27 = input.LA(1);

          int index14_27 = input.index();
          input.rewind();

          s = -1;
          if ((synpred1_FromClauseParser())) {
            s = 60;
          } else if ((true)) {
            s = 1;
          }

          input.seek(index14_27);

          if (s >= 0) {
            return s;
          }
          break;
        case 1:
          int LA14_28 = input.LA(1);

          int index14_28 = input.index();
          input.rewind();

          s = -1;
          if ((synpred1_FromClauseParser())) {
            s = 60;
          } else if ((true)) {
            s = 1;
          }

          input.seek(index14_28);

          if (s >= 0) {
            return s;
          }
          break;
      }
      if (state.backtracking > 0) {
        state.failed = true;
        return -1;
      }

      NoViableAltException nvae = new NoViableAltException(getDescription(), 14, _s, input);
      error(nvae);
      throw nvae;
    }
  }

  static final String DFA16_eotS = "\133\uffff";
  static final String DFA16_eofS = "\1\1\132\uffff";
  static final String DFA16_minS = "\1\12\2\uffff\1\32\27\uffff\2\0\76\uffff";
  static final String DFA16_maxS = "\1\u012d\2\uffff\1\u0122\27\uffff\2\0\76\uffff";
  static final String DFA16_acceptS = "\1\uffff\1\2\72\uffff\1\1\36\uffff";
  static final String DFA16_specialS = "\33\uffff\1\0\1\1\76\uffff}>";
  static final String[] DFA16_transitionS = {"\1\3\52\uffff\1\1\15\uffff\1\1\30\uffff\1\1\32\uffff\1\1\3\uffff"
      + "\1\1\1\uffff\1\1\10\uffff\1\1\3\uffff\1\1\6\uffff\1\1\2\uffff"
      + "\2\1\2\uffff\1\1\11\uffff\1\1\15\uffff\1\1\2\uffff\1\1\33\uffff"
      + "\1\1\11\uffff\1\1\10\uffff\1\1\13\uffff\1\1\32\uffff\1\1\21"
      + "\uffff\1\1\1\uffff\1\1\4\uffff\1\1\12\uffff\1\1", "", "",
      "\1\33\6\34\1\uffff\17\34\2\uffff\1\34\1\uffff\4\34\1\uffff"
          + "\6\34\1\uffff\2\34\1\uffff\1\34\3\uffff\2\34\1\uffff\20\34\1"
          + "\uffff\4\34\1\uffff\1\34\1\uffff\1\34\1\uffff\4\34\1\uffff\10"
          + "\34\1\uffff\3\34\1\uffff\1\34\1\uffff\4\34\1\uffff\2\34\1\uffff"
          + "\20\34\1\uffff\4\34\1\uffff\12\34\2\uffff\4\34\1\uffff\3\34"
          + "\1\uffff\4\34\1\uffff\1\34\1\uffff\5\34\1\uffff\2\34\1\uffff"
          + "\5\34\2\uffff\14\34\1\uffff\22\34\1\uffff\25\34\1\uffff\3\34"
          + "\1\uffff\5\34\1\uffff\4\34\1\uffff\3\34\1\uffff\14\34\1\uffff"
          + "\1\34\2\uffff\1\34\1\uffff\1\34\3\uffff\1\1", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "\1\uffff", "\1\uffff", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};

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
      return "()* loopback of 132:59: ( ( COMMA )=> COMMA identifier )*";
    }

    public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
      TokenStream input = (TokenStream) _input;
      int _s = s;
      switch (s) {
        case 0:
          int LA16_27 = input.LA(1);

          int index16_27 = input.index();
          input.rewind();

          s = -1;
          if ((synpred2_FromClauseParser())) {
            s = 60;
          } else if ((true)) {
            s = 1;
          }

          input.seek(index16_27);

          if (s >= 0) {
            return s;
          }
          break;
        case 1:
          int LA16_28 = input.LA(1);

          int index16_28 = input.index();
          input.rewind();

          s = -1;
          if ((synpred2_FromClauseParser())) {
            s = 60;
          } else if ((true)) {
            s = 1;
          }

          input.seek(index16_28);

          if (s >= 0) {
            return s;
          }
          break;
      }
      if (state.backtracking > 0) {
        state.failed = true;
        return -1;
      }

      NoViableAltException nvae = new NoViableAltException(getDescription(), 16, _s, input);
      error(nvae);
      throw nvae;
    }
  }

  static final String DFA19_eotS = "\104\uffff";
  static final String DFA19_eofS = "\1\uffff\1\2\102\uffff";
  static final String DFA19_minS = "\1\32\1\12\1\uffff\1\166\1\7\77\uffff";
  static final String DFA19_maxS = "\1\u0122\1\u012d\1\uffff\1\u011e\1\u0135\77\uffff";
  static final String DFA19_acceptS = "\2\uffff\1\2\37\uffff\1\3\5\uffff\1\4\1\1\32\uffff";
  static final String DFA19_specialS = "\4\uffff\1\0\77\uffff}>";
  static final String[] DFA19_transitionS = {"\1\1\6\2\1\uffff\17\2\2\uffff\1\2\1\uffff\4\2\1\uffff\6\2\1"
      + "\uffff\2\2\1\uffff\1\2\3\uffff\2\2\1\uffff\20\2\1\uffff\4\2"
      + "\1\uffff\1\2\1\uffff\1\2\1\uffff\4\2\1\uffff\10\2\1\uffff\3"
      + "\2\1\uffff\1\2\1\uffff\4\2\1\uffff\2\2\1\uffff\20\2\1\uffff"
      + "\4\2\1\uffff\12\2\2\uffff\4\2\1\uffff\3\2\1\uffff\4\2\1\uffff"
      + "\1\2\1\uffff\5\2\1\uffff\2\2\1\uffff\5\2\2\uffff\14\2\1\uffff"
      + "\22\2\1\uffff\25\2\1\uffff\3\2\1\uffff\5\2\1\uffff\4\2\1\uffff"
      + "\3\2\1\uffff\14\2\1\uffff\1\2\2\uffff\1\2\1\uffff\1\2\3\uffff" + "\1\3",
      "\1\2\6\uffff\1\2\10\uffff\1\2\11\uffff\1\2\20\uffff\1\2\15"
          + "\uffff\1\2\30\uffff\1\2\32\uffff\1\2\3\uffff\1\2\1\uffff\1\2"
          + "\10\uffff\1\2\3\uffff\1\2\6\uffff\1\2\2\uffff\2\2\2\uffff\1"
          + "\2\11\uffff\1\2\15\uffff\1\2\2\uffff\1\2\33\uffff\1\2\11\uffff"
          + "\1\2\10\uffff\1\2\13\uffff\1\2\11\uffff\1\2\20\uffff\1\2\21"
          + "\uffff\1\2\1\uffff\1\2\4\uffff\1\4\12\uffff\1\2", "",
      "\1\42\23\uffff\1\42\27\uffff\1\42\54\uffff\1\42\22\uffff\1" + "\42\63\uffff\1\50\7\uffff\1\42",
      "\1\2\5\uffff\1\2\4\uffff\1\2\7\uffff\7\2\1\uffff\22\2\1\uffff"
          + "\4\2\1\uffff\6\2\1\uffff\2\2\1\uffff\1\2\1\uffff\4\2\1\uffff"
          + "\20\2\1\uffff\4\2\1\uffff\1\2\1\uffff\1\2\1\uffff\4\2\1\uffff"
          + "\10\2\1\uffff\3\2\1\uffff\1\2\1\uffff\4\2\1\uffff\23\2\1\uffff"
          + "\4\2\1\uffff\12\2\1\uffff\5\2\1\uffff\10\2\1\51\1\2\1\uffff"
          + "\5\2\1\uffff\2\2\1\uffff\5\2\2\uffff\14\2\1\uffff\22\2\1\uffff"
          + "\25\2\1\uffff\3\2\1\uffff\5\2\1\uffff\4\2\1\uffff\3\2\1\uffff"
          + "\14\2\1\uffff\1\2\2\uffff\1\2\1\uffff\1\2\3\uffff\1\2\2\uffff"
          + "\1\2\2\uffff\2\2\10\uffff\4\2", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};

  static final short[] DFA19_eot = DFA.unpackEncodedString(DFA19_eotS);
  static final short[] DFA19_eof = DFA.unpackEncodedString(DFA19_eofS);
  static final char[] DFA19_min = DFA.unpackEncodedStringToUnsignedChars(DFA19_minS);
  static final char[] DFA19_max = DFA.unpackEncodedStringToUnsignedChars(DFA19_maxS);
  static final short[] DFA19_accept = DFA.unpackEncodedString(DFA19_acceptS);
  static final short[] DFA19_special = DFA.unpackEncodedString(DFA19_specialS);
  static final short[][] DFA19_transition;

  static {
    int numStates = DFA19_transitionS.length;
    DFA19_transition = new short[numStates][];
    for (int i = 0; i < numStates; i++) {
      DFA19_transition[i] = DFA.unpackEncodedString(DFA19_transitionS[i]);
    }
  }

  class DFA19 extends DFA {

    public DFA19(BaseRecognizer recognizer) {
      this.recognizer = recognizer;
      this.decisionNumber = 19;
      this.eot = DFA19_eot;
      this.eof = DFA19_eof;
      this.min = DFA19_min;
      this.max = DFA19_max;
      this.accept = DFA19_accept;
      this.special = DFA19_special;
      this.transition = DFA19_transition;
    }

    public String getDescription() {
      return "147:5: ( ( Identifier LPAREN )=> partitionedTableFunction | tableSource | subQuerySource | virtualTableSource )";
    }

    public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
      TokenStream input = (TokenStream) _input;
      int _s = s;
      switch (s) {
        case 0:
          int LA19_4 = input.LA(1);

          int index19_4 = input.index();
          input.rewind();

          s = -1;
          if ((LA19_4 == KW_ON) && (synpred3_FromClauseParser())) {
            s = 41;
          } else if ((LA19_4 == BigintLiteral || LA19_4 == CharSetName || LA19_4 == DecimalLiteral || (
              LA19_4 >= Identifier && LA19_4 <= KW_ANALYZE) || (LA19_4 >= KW_ARCHIVE && LA19_4 <= KW_CHANGE) || (
              LA19_4 >= KW_CLUSTER && LA19_4 <= KW_COLLECTION) || (LA19_4 >= KW_COLUMNS && LA19_4 <= KW_CONCATENATE)
              || (LA19_4 >= KW_CONTINUE && LA19_4 <= KW_CREATE) || LA19_4 == KW_CUBE || (LA19_4 >= KW_CURRENT_DATE
              && LA19_4 <= KW_DATA) || (LA19_4 >= KW_DATABASES && LA19_4 <= KW_DISABLE) || (LA19_4 >= KW_DISTRIBUTE
              && LA19_4 <= KW_ELEM_TYPE) || LA19_4 == KW_ENABLE || LA19_4 == KW_ESCAPED || (LA19_4 >= KW_EXCLUSIVE
              && LA19_4 <= KW_EXPORT) || (LA19_4 >= KW_EXTERNAL && LA19_4 <= KW_FLOAT) || (LA19_4 >= KW_FOR
              && LA19_4 <= KW_FORMATTED) || LA19_4 == KW_FULL || (LA19_4 >= KW_FUNCTIONS && LA19_4 <= KW_GROUPING) || (
              LA19_4 >= KW_HOLD_DDLTIME && LA19_4 <= KW_JAR) || (LA19_4 >= KW_KEYS && LA19_4 <= KW_LEFT) || (
              LA19_4 >= KW_LIKE && LA19_4 <= KW_LONG) || (LA19_4 >= KW_MAP && LA19_4 <= KW_MINUS) || (LA19_4 >= KW_MSCK
              && LA19_4 <= KW_OFFLINE) || LA19_4 == KW_OPTION || (LA19_4 >= KW_ORDER && LA19_4 <= KW_OUTPUTFORMAT) || (
              LA19_4 >= KW_OVERWRITE && LA19_4 <= KW_OWNER) || (LA19_4 >= KW_PARTITION && LA19_4 <= KW_PLUS) || (
              LA19_4 >= KW_PRETTY && LA19_4 <= KW_RECORDWRITER) || (LA19_4 >= KW_REGEXP && LA19_4 <= KW_SCHEMAS) || (
              LA19_4 >= KW_SEMI && LA19_4 <= KW_TABLES) || (LA19_4 >= KW_TBLPROPERTIES && LA19_4 <= KW_TERMINATED) || (
              LA19_4 >= KW_TIMESTAMP && LA19_4 <= KW_TRANSACTIONS) || (LA19_4 >= KW_TRIGGER && LA19_4 <= KW_UNARCHIVE)
              || (LA19_4 >= KW_UNDO && LA19_4 <= KW_UNIONTYPE) || (LA19_4 >= KW_UNLOCK && LA19_4 <= KW_VALUE_TYPE)
              || LA19_4 == KW_VIEW || LA19_4 == KW_WHILE || LA19_4 == KW_WITH || LA19_4 == LPAREN || LA19_4 == MINUS
              || (LA19_4 >= Number && LA19_4 <= PLUS) || (LA19_4 >= SmallintLiteral && LA19_4 <= TinyintLiteral))) {
            s = 2;
          }

          input.seek(index19_4);

          if (s >= 0) {
            return s;
          }
          break;
      }
      if (state.backtracking > 0) {
        state.failed = true;
        return -1;
      }

      NoViableAltException nvae = new NoViableAltException(getDescription(), 19, _s, input);
      error(nvae);
      throw nvae;
    }
  }

  static final String DFA26_eotS = "\121\uffff";
  static final String DFA26_eofS = "\1\2\120\uffff";
  static final String DFA26_minS = "\1\12\1\7\35\uffff\1\4\61\uffff";
  static final String DFA26_maxS = "\1\u012d\1\u0135\35\uffff\1\u0133\61\uffff";
  static final String DFA26_acceptS = "\2\uffff\1\2\66\uffff\3\1\25\uffff";
  static final String DFA26_specialS = "\121\uffff}>";
  static final String[] DFA26_transitionS = {"\1\2\17\uffff\1\2\11\uffff\1\2\20\uffff\1\2\15\uffff\1\2\30"
      + "\uffff\1\2\32\uffff\1\2\3\uffff\1\2\1\uffff\1\2\10\uffff\1\2"
      + "\3\uffff\1\2\6\uffff\1\2\2\uffff\2\2\2\uffff\1\2\11\uffff\1"
      + "\2\15\uffff\1\2\2\uffff\1\2\10\uffff\1\2\22\uffff\1\2\11\uffff"
      + "\1\2\10\uffff\1\2\13\uffff\1\2\11\uffff\1\2\20\uffff\1\2\21"
      + "\uffff\1\2\1\uffff\1\2\4\uffff\1\1\12\uffff\1\2",
      "\1\2\5\uffff\1\2\4\uffff\1\2\7\uffff\7\2\1\uffff\22\2\1\uffff"
          + "\4\2\1\uffff\6\2\1\uffff\2\2\1\uffff\1\2\1\uffff\4\2\1\uffff"
          + "\20\2\1\uffff\4\2\1\uffff\1\2\1\uffff\1\2\1\uffff\4\2\1\uffff"
          + "\10\2\1\uffff\3\2\1\uffff\1\2\1\uffff\4\2\1\uffff\23\2\1\uffff"
          + "\4\2\1\uffff\12\2\1\uffff\5\2\1\uffff\10\2\1\uffff\1\2\1\uffff"
          + "\5\2\1\uffff\2\2\1\uffff\5\2\2\uffff\14\2\1\uffff\22\2\1\uffff"
          + "\25\2\1\uffff\3\2\1\uffff\5\2\1\uffff\4\2\1\uffff\3\2\1\uffff"
          + "\14\2\1\uffff\1\2\2\uffff\1\2\1\uffff\1\2\3\uffff\1\2\2\uffff"
          + "\1\2\2\uffff\2\2\10\uffff\1\2\1\37\2\2", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "", "", "", "", "",
      "\3\2\3\uffff\1\72\3\uffff\2\2\1\uffff\1\2\2\uffff\1\71\1\2"
          + "\1\uffff\2\2\10\uffff\1\2\6\uffff\1\2\132\uffff\1\2\12\uffff"
          + "\1\2\10\uffff\1\2\23\uffff\1\2\6\uffff\1\2\35\uffff\1\2\11\uffff"
          + "\1\2\105\uffff\2\2\1\uffff\1\2\1\uffff\3\2\1\uffff\1\2\3\uffff"
          + "\1\73\3\uffff\1\2\1\uffff\1\2", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};

  static final short[] DFA26_eot = DFA.unpackEncodedString(DFA26_eotS);
  static final short[] DFA26_eof = DFA.unpackEncodedString(DFA26_eofS);
  static final char[] DFA26_min = DFA.unpackEncodedStringToUnsignedChars(DFA26_minS);
  static final char[] DFA26_max = DFA.unpackEncodedStringToUnsignedChars(DFA26_maxS);
  static final short[] DFA26_accept = DFA.unpackEncodedString(DFA26_acceptS);
  static final short[] DFA26_special = DFA.unpackEncodedString(DFA26_specialS);
  static final short[][] DFA26_transition;

  static {
    int numStates = DFA26_transitionS.length;
    DFA26_transition = new short[numStates][];
    for (int i = 0; i < numStates; i++) {
      DFA26_transition[i] = DFA.unpackEncodedString(DFA26_transitionS[i]);
    }
  }

  class DFA26 extends DFA {

    public DFA26(BaseRecognizer recognizer) {
      this.recognizer = recognizer;
      this.decisionNumber = 26;
      this.eot = DFA26_eot;
      this.eof = DFA26_eof;
      this.min = DFA26_min;
      this.max = DFA26_max;
      this.accept = DFA26_accept;
      this.special = DFA26_special;
      this.transition = DFA26_transition;
    }

    public String getDescription() {
      return "180:25: (props= tableProperties )?";
    }
  }

  static final String DFA29_eotS = "\124\uffff";
  static final String DFA29_eofS = "\1\3\1\uffff\1\1\121\uffff";
  static final String DFA29_minS = "\1\12\1\uffff\1\12\33\uffff\1\7\65\uffff";
  static final String DFA29_maxS = "\1\u012d\1\uffff\1\u012d\33\uffff\1\u0135\65\uffff";
  static final String DFA29_acceptS = "\1\uffff\1\1\1\uffff\1\2\66\uffff\32\1";
  static final String DFA29_specialS = "\124\uffff}>";
  static final String[] DFA29_transitionS = {"\1\3\17\uffff\1\2\11\uffff\1\1\20\uffff\1\3\15\uffff\1\3\30"
      + "\uffff\1\3\32\uffff\1\3\3\uffff\1\3\1\uffff\1\3\10\uffff\1\3"
      + "\3\uffff\1\3\6\uffff\1\3\2\uffff\2\3\2\uffff\1\3\11\uffff\1"
      + "\3\15\uffff\1\3\2\uffff\1\3\10\uffff\1\3\22\uffff\1\3\11\uffff"
      + "\1\3\10\uffff\1\3\13\uffff\1\3\32\uffff\1\3\21\uffff\1\3\1\uffff" + "\1\3\4\uffff\1\3\12\uffff\1\3", "",
      "\1\1\17\uffff\1\1\32\uffff\1\1\15\uffff\1\1\30\uffff\1\1\32"
          + "\uffff\1\1\3\uffff\1\1\1\uffff\1\1\10\uffff\1\1\3\uffff\1\1"
          + "\6\uffff\1\1\2\uffff\2\1\2\uffff\1\1\11\uffff\1\1\15\uffff\1"
          + "\1\2\uffff\1\1\10\uffff\1\1\22\uffff\1\1\11\uffff\1\1\10\uffff"
          + "\1\1\13\uffff\1\1\32\uffff\1\1\21\uffff\1\1\1\uffff\1\1\4\uffff"
          + "\1\36\12\uffff\1\1", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "\1\101\5\uffff\1\105\4\uffff\1\104\7\uffff\1\120\6\123\1\uffff"
          + "\1\123\1\114\15\123\1\111\1\110\1\123\1\uffff\4\123\1\uffff"
          + "\6\123\1\uffff\2\123\1\uffff\1\123\1\uffff\2\112\2\123\1\uffff"
          + "\1\123\1\76\16\123\1\uffff\4\123\1\uffff\1\123\1\uffff\1\123"
          + "\1\uffff\1\123\1\121\2\123\1\uffff\1\123\1\107\6\123\1\uffff"
          + "\3\123\1\uffff\1\123\1\uffff\4\123\1\uffff\2\123\1\113\20\123"
          + "\1\uffff\4\123\1\uffff\12\123\1\uffff\1\115\4\123\1\uffff\3"
          + "\123\1\72\1\123\1\74\2\123\1\uffff\1\123\1\uffff\5\123\1\uffff"
          + "\2\123\1\uffff\5\123\2\uffff\14\123\1\uffff\22\123\1\uffff\22"
          + "\123\1\116\2\123\1\uffff\3\123\1\uffff\1\77\4\123\1\uffff\1"
          + "\123\1\106\2\123\1\uffff\2\123\1\117\1\uffff\14\123\1\uffff"
          + "\1\123\2\uffff\1\123\1\uffff\1\123\3\uffff\1\122\2\uffff\1\73"
          + "\2\uffff\1\75\1\73\10\uffff\1\102\1\100\1\73\1\103", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};

  static final short[] DFA29_eot = DFA.unpackEncodedString(DFA29_eotS);
  static final short[] DFA29_eof = DFA.unpackEncodedString(DFA29_eofS);
  static final char[] DFA29_min = DFA.unpackEncodedStringToUnsignedChars(DFA29_minS);
  static final char[] DFA29_max = DFA.unpackEncodedStringToUnsignedChars(DFA29_maxS);
  static final short[] DFA29_accept = DFA.unpackEncodedString(DFA29_acceptS);
  static final short[] DFA29_special = DFA.unpackEncodedString(DFA29_specialS);
  static final short[][] DFA29_transition;

  static {
    int numStates = DFA29_transitionS.length;
    DFA29_transition = new short[numStates][];
    for (int i = 0; i < numStates; i++) {
      DFA29_transition[i] = DFA.unpackEncodedString(DFA29_transitionS[i]);
    }
  }

  class DFA29 extends DFA {

    public DFA29(BaseRecognizer recognizer) {
      this.recognizer = recognizer;
      this.decisionNumber = 29;
      this.eot = DFA29_eot;
      this.eof = DFA29_eof;
      this.min = DFA29_min;
      this.max = DFA29_max;
      this.accept = DFA29_accept;
      this.special = DFA29_special;
      this.transition = DFA29_transition;
    }

    public String getDescription() {
      return "180:68: ( ( KW_AS )? alias= Identifier )?";
    }
  }

  static final String DFA30_eotS = "\u0095\uffff";
  static final String DFA30_eofS = "\1\uffff\2\4\u0092\uffff";
  static final String DFA30_minS = "\1\32\2\12\1\32\105\uffff\1\32\113\uffff";
  static final String DFA30_maxS = "\1\u011e\2\u012d\1\u0131\105\uffff\1\u0131\113\uffff";
  static final String DFA30_acceptS = "\4\uffff\1\2\u008b\uffff\1\1\4\uffff";
  static final String DFA30_specialS = "\u0095\uffff}>";
  static final String[] DFA30_transitionS = {"\1\1\6\2\1\uffff\17\2\2\uffff\1\2\1\uffff\4\2\1\uffff\6\2\1"
      + "\uffff\2\2\1\uffff\1\2\3\uffff\2\2\1\uffff\20\2\1\uffff\4\2"
      + "\1\uffff\1\2\1\uffff\1\2\1\uffff\4\2\1\uffff\10\2\1\uffff\3"
      + "\2\1\uffff\1\2\1\uffff\4\2\1\uffff\2\2\1\uffff\20\2\1\uffff"
      + "\4\2\1\uffff\12\2\2\uffff\4\2\1\uffff\3\2\1\uffff\4\2\1\uffff"
      + "\1\2\1\uffff\5\2\1\uffff\2\2\1\uffff\5\2\2\uffff\14\2\1\uffff"
      + "\22\2\1\uffff\25\2\1\uffff\3\2\1\uffff\5\2\1\uffff\4\2\1\uffff"
      + "\3\2\1\uffff\14\2\1\uffff\1\2\2\uffff\1\2\1\uffff\1\2",
      "\1\4\6\uffff\1\3\10\uffff\2\4\6\uffff\1\4\1\uffff\1\4\16\uffff"
          + "\1\4\1\uffff\2\4\3\uffff\3\4\1\uffff\2\4\3\uffff\1\4\26\uffff"
          + "\1\4\1\uffff\1\4\1\uffff\1\4\2\uffff\1\4\2\uffff\2\4\15\uffff"
          + "\1\4\2\uffff\2\4\3\uffff\1\4\1\uffff\1\4\2\uffff\1\4\2\uffff"
          + "\1\4\2\uffff\1\4\3\uffff\1\4\2\uffff\1\4\3\uffff\1\4\2\uffff"
          + "\2\4\1\uffff\2\4\3\uffff\1\4\5\uffff\1\4\10\uffff\1\4\4\uffff"
          + "\1\4\2\uffff\1\4\10\uffff\2\4\11\uffff\1\4\4\uffff\1\4\2\uffff"
          + "\1\4\2\uffff\1\4\1\uffff\1\4\4\uffff\1\4\4\uffff\1\4\3\uffff"
          + "\1\4\4\uffff\1\4\1\uffff\1\4\2\uffff\1\4\1\uffff\1\4\3\uffff"
          + "\1\4\5\uffff\2\4\5\uffff\2\4\5\uffff\1\4\2\uffff\1\4\3\uffff"
          + "\1\4\1\uffff\1\4\6\uffff\1\4\4\uffff\1\4\1\uffff\2\4\3\uffff" + "\1\4\12\uffff\1\4",
      "\1\4\6\uffff\1\111\10\uffff\2\4\6\uffff\1\4\1\uffff\1\4\16"
          + "\uffff\1\4\1\uffff\2\4\3\uffff\3\4\1\uffff\2\4\3\uffff\1\4\26"
          + "\uffff\1\4\1\uffff\1\4\1\uffff\1\4\2\uffff\1\4\2\uffff\2\4\15"
          + "\uffff\1\4\2\uffff\2\4\3\uffff\1\4\1\uffff\1\4\2\uffff\1\4\2"
          + "\uffff\1\4\2\uffff\1\4\3\uffff\1\4\2\uffff\1\4\3\uffff\1\4\2"
          + "\uffff\2\4\1\uffff\2\4\3\uffff\1\4\5\uffff\1\4\10\uffff\1\4"
          + "\4\uffff\1\4\2\uffff\1\4\10\uffff\2\4\11\uffff\1\4\4\uffff\1"
          + "\4\2\uffff\1\4\2\uffff\1\4\1\uffff\1\4\4\uffff\1\4\4\uffff\1"
          + "\4\3\uffff\1\4\4\uffff\1\4\1\uffff\1\4\2\uffff\1\4\1\uffff\1"
          + "\4\3\uffff\1\4\5\uffff\2\4\5\uffff\2\4\5\uffff\1\4\2\uffff\1"
          + "\4\3\uffff\1\4\1\uffff\1\4\6\uffff\1\4\4\uffff\1\4\1\uffff\2" + "\4\3\uffff\1\4\12\uffff\1\4",
      "\7\u0090\1\uffff\17\u0090\2\uffff\1\u0090\1\uffff\4\u0090\1"
          + "\uffff\6\u0090\1\uffff\2\u0090\1\uffff\1\u0090\3\uffff\2\u0090"
          + "\1\uffff\20\u0090\1\uffff\4\u0090\1\uffff\1\u0090\1\uffff\1"
          + "\u0090\1\uffff\4\u0090\1\uffff\10\u0090\1\uffff\3\u0090\1\uffff"
          + "\1\u0090\1\uffff\4\u0090\1\uffff\2\u0090\1\uffff\20\u0090\1"
          + "\uffff\4\u0090\1\uffff\12\u0090\2\uffff\4\u0090\1\uffff\3\u0090"
          + "\1\uffff\4\u0090\1\uffff\1\u0090\1\uffff\5\u0090\1\uffff\2\u0090"
          + "\1\uffff\5\u0090\2\uffff\14\u0090\1\uffff\22\u0090\1\uffff\25"
          + "\u0090\1\uffff\3\u0090\1\uffff\5\u0090\1\uffff\4\u0090\1\uffff"
          + "\3\u0090\1\uffff\14\u0090\1\uffff\1\u0090\2\uffff\1\u0090\1"
          + "\uffff\1\u0090\22\uffff\1\4", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "", "", "", "", "",
      "\7\u0090\1\uffff\17\u0090\2\uffff\1\u0090\1\uffff\4\u0090\1"
          + "\uffff\6\u0090\1\uffff\2\u0090\1\uffff\1\u0090\3\uffff\2\u0090"
          + "\1\uffff\20\u0090\1\uffff\4\u0090\1\uffff\1\u0090\1\uffff\1"
          + "\u0090\1\uffff\4\u0090\1\uffff\10\u0090\1\uffff\3\u0090\1\uffff"
          + "\1\u0090\1\uffff\4\u0090\1\uffff\2\u0090\1\uffff\20\u0090\1"
          + "\uffff\4\u0090\1\uffff\12\u0090\2\uffff\4\u0090\1\uffff\3\u0090"
          + "\1\uffff\4\u0090\1\uffff\1\u0090\1\uffff\5\u0090\1\uffff\2\u0090"
          + "\1\uffff\5\u0090\2\uffff\14\u0090\1\uffff\22\u0090\1\uffff\25"
          + "\u0090\1\uffff\3\u0090\1\uffff\5\u0090\1\uffff\4\u0090\1\uffff"
          + "\3\u0090\1\uffff\14\u0090\1\uffff\1\u0090\2\uffff\1\u0090\1"
          + "\uffff\1\u0090\22\uffff\1\4", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "", "", "", "", "", "", "", "", "", "", ""};

  static final short[] DFA30_eot = DFA.unpackEncodedString(DFA30_eotS);
  static final short[] DFA30_eof = DFA.unpackEncodedString(DFA30_eofS);
  static final char[] DFA30_min = DFA.unpackEncodedStringToUnsignedChars(DFA30_minS);
  static final char[] DFA30_max = DFA.unpackEncodedStringToUnsignedChars(DFA30_maxS);
  static final short[] DFA30_accept = DFA.unpackEncodedString(DFA30_acceptS);
  static final short[] DFA30_special = DFA.unpackEncodedString(DFA30_specialS);
  static final short[][] DFA30_transition;

  static {
    int numStates = DFA30_transitionS.length;
    DFA30_transition = new short[numStates][];
    for (int i = 0; i < numStates; i++) {
      DFA30_transition[i] = DFA.unpackEncodedString(DFA30_transitionS[i]);
    }
  }

  class DFA30 extends DFA {

    public DFA30(BaseRecognizer recognizer) {
      this.recognizer = recognizer;
      this.decisionNumber = 30;
      this.eot = DFA30_eot;
      this.eof = DFA30_eof;
      this.min = DFA30_min;
      this.max = DFA30_max;
      this.accept = DFA30_accept;
      this.special = DFA30_special;
      this.transition = DFA30_transition;
    }

    public String getDescription() {
      return "184:1: tableName : (db= identifier DOT tab= identifier -> ^( TOK_TABNAME $db $tab) |tab= identifier -> ^( TOK_TABNAME $tab) );";
    }
  }

  static final String DFA32_eotS = "\u01d4\uffff";
  static final String DFA32_eofS = "\1\uffff\1\2\2\uffff\2\40\1\132\1\uffff\1\170\2\uffff\1\u0095\2"
      + "\u00b2\1\uffff\1\u00b2\4\uffff\1\u00b2\1\uffff\4\u00b2\1\uffff\2" + "\u00b2\u01b7\uffff";
  static final String DFA32_minS = "\1\32\1\12\2\uffff\3\12\1\uffff\1\12\2\uffff\3\12\1\uffff\1\12\4"
      + "\uffff\1\12\1\uffff\4\12\1\uffff\2\12\u01b7\uffff";
  static final String DFA32_maxS = "\1\u011e\1\u012d\2\uffff\3\u012d\1\uffff\1\u012d\2\uffff\3\u012d"
      + "\1\uffff\1\u012d\4\uffff\1\u012d\1\uffff\4\u012d\1\uffff\2\u012d" + "\u01b7\uffff";
  static final String DFA32_acceptS =
      "\2\uffff\1\2\35\uffff\2\1\70\uffff\1\1\35\uffff\1\1\34\uffff\1\1" + "\34\uffff\1\1\u0121\uffff";
  static final String DFA32_specialS = "\u01d4\uffff}>";
  static final String[] DFA32_transitionS = {"\7\2\1\uffff\2\2\1\1\14\2\2\uffff\1\2\1\uffff\4\2\1\uffff\6"
      + "\2\1\uffff\2\2\1\uffff\1\2\3\uffff\2\2\1\uffff\20\2\1\uffff"
      + "\4\2\1\uffff\1\2\1\uffff\1\2\1\uffff\4\2\1\uffff\10\2\1\uffff"
      + "\3\2\1\uffff\1\2\1\uffff\4\2\1\uffff\2\2\1\uffff\20\2\1\uffff"
      + "\4\2\1\uffff\12\2\2\uffff\4\2\1\uffff\3\2\1\uffff\4\2\1\uffff"
      + "\1\2\1\uffff\5\2\1\uffff\2\2\1\uffff\5\2\2\uffff\14\2\1\uffff"
      + "\22\2\1\uffff\25\2\1\uffff\3\2\1\uffff\5\2\1\uffff\4\2\1\uffff"
      + "\3\2\1\uffff\14\2\1\uffff\1\2\2\uffff\1\2\1\uffff\1\2",
      "\1\2\17\uffff\1\4\6\40\1\uffff\17\40\2\uffff\1\40\1\uffff\1"
          + "\26\3\40\1\uffff\6\40\1\uffff\2\40\1\2\1\40\3\uffff\2\40\1\uffff"
          + "\20\40\1\uffff\1\27\3\40\1\uffff\1\40\1\uffff\1\40\1\uffff\4"
          + "\40\1\uffff\10\40\1\uffff\3\40\1\uffff\1\14\1\uffff\2\40\1\17"
          + "\1\40\1\2\2\40\1\uffff\5\40\1\6\3\40\1\15\6\40\1\2\2\40\1\5"
          + "\1\10\1\uffff\1\40\1\31\10\40\1\uffff\1\2\4\40\1\uffff\3\40"
          + "\1\uffff\4\40\1\2\1\40\1\uffff\1\24\4\40\1\uffff\2\40\1\uffff"
          + "\1\34\4\40\2\uffff\14\40\1\2\11\40\1\13\10\40\1\2\13\40\1\30"
          + "\11\40\1\uffff\3\40\1\uffff\5\40\1\uffff\4\40\1\uffff\1\40\1"
          + "\33\1\40\1\uffff\14\40\1\uffff\1\40\1\uffff\1\2\1\40\1\2\1\40" + "\3\uffff\1\2\12\uffff\1\2", "", "",
      "\1\40\17\uffff\1\40\32\uffff\1\40\15\uffff\1\40\30\uffff\1"
          + "\40\32\uffff\1\40\3\uffff\1\40\1\uffff\1\40\10\uffff\1\40\3"
          + "\uffff\1\40\6\uffff\1\40\2\uffff\2\40\2\uffff\1\40\11\uffff"
          + "\1\40\15\uffff\1\40\2\uffff\1\40\10\uffff\1\40\22\uffff\1\40"
          + "\11\uffff\1\40\10\uffff\1\40\13\uffff\1\40\32\uffff\1\40\21"
          + "\uffff\1\40\1\uffff\1\40\4\uffff\1\41\12\uffff\1\40",
      "\1\40\17\uffff\1\40\32\uffff\1\40\15\uffff\1\40\30\uffff\1"
          + "\40\32\uffff\1\40\3\uffff\1\40\1\uffff\1\40\10\uffff\1\40\3"
          + "\uffff\1\40\6\uffff\1\40\2\uffff\2\40\2\uffff\1\40\11\uffff"
          + "\1\40\15\uffff\1\40\2\uffff\1\40\10\uffff\1\40\22\uffff\1\40"
          + "\11\uffff\1\40\10\uffff\1\40\13\uffff\1\40\32\uffff\1\40\17"
          + "\uffff\1\2\1\uffff\1\40\1\uffff\1\40\4\uffff\1\40\12\uffff\1" + "\40",
      "\1\132\17\uffff\1\132\32\uffff\1\132\15\uffff\1\132\30\uffff"
          + "\1\132\32\uffff\1\132\3\uffff\1\132\1\uffff\1\132\10\uffff\1"
          + "\132\3\uffff\1\132\6\uffff\1\132\2\uffff\2\132\2\uffff\1\132"
          + "\11\uffff\1\132\15\uffff\1\132\2\uffff\1\132\10\uffff\1\132"
          + "\22\uffff\1\132\11\uffff\1\132\10\uffff\1\132\13\uffff\1\132"
          + "\32\uffff\1\132\21\uffff\1\132\1\uffff\1\132\4\uffff\1\132\12" + "\uffff\1\132", "",
      "\1\170\17\uffff\1\170\32\uffff\1\170\15\uffff\1\170\30\uffff"
          + "\1\170\32\uffff\1\170\3\uffff\1\170\1\uffff\1\170\10\uffff\1"
          + "\170\3\uffff\1\170\6\uffff\1\170\2\uffff\1\132\1\170\2\uffff"
          + "\1\170\11\uffff\1\170\15\uffff\1\170\2\uffff\1\170\1\uffff\1"
          + "\2\6\uffff\1\170\22\uffff\1\170\11\uffff\1\170\10\uffff\1\170"
          + "\1\2\12\uffff\1\170\32\uffff\1\170\21\uffff\1\170\1\uffff\1" + "\170\4\uffff\1\170\12\uffff\1\170", "", "",
      "\1\u0095\17\uffff\1\u0095\32\uffff\1\u0095\15\uffff\1\u0095"
          + "\30\uffff\1\u0095\32\uffff\1\u0095\3\uffff\1\u0095\1\uffff\1"
          + "\u0095\10\uffff\1\u0095\3\uffff\1\u0095\6\uffff\1\u0095\2\uffff"
          + "\1\170\1\u0095\2\uffff\1\u0095\11\uffff\1\u0095\15\uffff\1\u0095"
          + "\2\uffff\1\u0095\1\uffff\1\2\6\uffff\1\u0095\22\uffff\1\u0095"
          + "\11\uffff\1\u0095\10\uffff\1\u0095\13\uffff\1\u0095\32\uffff"
          + "\1\u0095\21\uffff\1\u0095\1\uffff\1\u0095\4\uffff\1\u0095\12" + "\uffff\1\u0095",
      "\1\u00b2\17\uffff\1\u00b2\32\uffff\1\u00b2\15\uffff\1\u00b2"
          + "\30\uffff\1\u00b2\32\uffff\1\u00b2\3\uffff\1\u00b2\1\uffff\1"
          + "\u00b2\10\uffff\1\u00b2\3\uffff\1\u00b2\6\uffff\1\u00b2\2\uffff"
          + "\1\u0095\1\u00b2\2\uffff\1\u00b2\11\uffff\1\u00b2\15\uffff\1"
          + "\u00b2\2\uffff\1\u00b2\1\uffff\1\2\6\uffff\1\u00b2\22\uffff"
          + "\1\u00b2\11\uffff\1\u00b2\10\uffff\1\u00b2\13\uffff\1\u00b2"
          + "\32\uffff\1\u00b2\21\uffff\1\u00b2\1\uffff\1\u00b2\4\uffff\1" + "\u00b2\12\uffff\1\u00b2",
      "\1\u00b2\17\uffff\1\u00b2\32\uffff\1\u00b2\15\uffff\1\u00b2"
          + "\30\uffff\1\u00b2\32\uffff\1\u00b2\3\uffff\1\u00b2\1\uffff\1"
          + "\u00b2\10\uffff\1\u00b2\3\uffff\1\u00b2\2\uffff\1\2\3\uffff"
          + "\1\u00b2\2\uffff\2\u00b2\2\uffff\1\u00b2\11\uffff\1\u00b2\15"
          + "\uffff\1\u00b2\2\uffff\1\u00b2\5\uffff\1\2\2\uffff\1\u00b2\22"
          + "\uffff\1\u00b2\11\uffff\1\u00b2\10\uffff\1\u00b2\13\uffff\1"
          + "\u00b2\32\uffff\1\u00b2\21\uffff\1\u00b2\1\uffff\1\u00b2\4\uffff" + "\1\u00b2\12\uffff\1\u00b2", "",
      "\1\u00b2\17\uffff\1\u00b2\24\uffff\1\2\5\uffff\1\u00b2\15\uffff"
          + "\1\u00b2\30\uffff\1\u00b2\32\uffff\1\u00b2\3\uffff\1\u00b2\1"
          + "\uffff\1\u00b2\10\uffff\1\u00b2\3\uffff\1\u00b2\6\uffff\1\u00b2"
          + "\2\uffff\2\u00b2\2\uffff\1\u00b2\11\uffff\1\u00b2\15\uffff\1"
          + "\u00b2\2\uffff\1\u00b2\10\uffff\1\u00b2\22\uffff\1\u00b2\11"
          + "\uffff\1\u00b2\10\uffff\1\u00b2\13\uffff\1\u00b2\32\uffff\1"
          + "\u00b2\21\uffff\1\u00b2\1\uffff\1\u00b2\4\uffff\1\u00b2\12\uffff" + "\1\u00b2", "", "", "", "",
      "\1\u00b2\17\uffff\1\u00b2\24\uffff\1\2\5\uffff\1\u00b2\15\uffff"
          + "\1\u00b2\30\uffff\1\u00b2\32\uffff\1\u00b2\3\uffff\1\u00b2\1"
          + "\uffff\1\u00b2\10\uffff\1\u00b2\3\uffff\1\u00b2\6\uffff\1\u00b2"
          + "\2\uffff\2\u00b2\2\uffff\1\u00b2\11\uffff\1\u00b2\15\uffff\1"
          + "\u00b2\2\uffff\1\u00b2\10\uffff\1\u00b2\22\uffff\1\u00b2\11"
          + "\uffff\1\u00b2\10\uffff\1\u00b2\13\uffff\1\u00b2\32\uffff\1"
          + "\u00b2\21\uffff\1\u00b2\1\uffff\1\u00b2\4\uffff\1\u00b2\12\uffff" + "\1\u00b2", "",
      "\1\u00b2\17\uffff\1\u00b2\24\uffff\1\2\5\uffff\1\u00b2\15\uffff"
          + "\1\u00b2\30\uffff\1\u00b2\32\uffff\1\u00b2\3\uffff\1\u00b2\1"
          + "\uffff\1\u00b2\10\uffff\1\u00b2\3\uffff\1\u00b2\6\uffff\1\u00b2"
          + "\2\uffff\2\u00b2\2\uffff\1\u00b2\11\uffff\1\u00b2\15\uffff\1"
          + "\u00b2\2\uffff\1\u00b2\10\uffff\1\u00b2\22\uffff\1\u00b2\11"
          + "\uffff\1\u00b2\10\uffff\1\u00b2\13\uffff\1\u00b2\32\uffff\1"
          + "\u00b2\21\uffff\1\u00b2\1\uffff\1\u00b2\4\uffff\1\u00b2\12\uffff" + "\1\u00b2",
      "\1\u00b2\17\uffff\1\u00b2\24\uffff\1\2\5\uffff\1\u00b2\15\uffff"
          + "\1\u00b2\30\uffff\1\u00b2\32\uffff\1\u00b2\3\uffff\1\u00b2\1"
          + "\uffff\1\u00b2\10\uffff\1\u00b2\3\uffff\1\u00b2\6\uffff\1\u00b2"
          + "\2\uffff\2\u00b2\2\uffff\1\u00b2\11\uffff\1\u00b2\15\uffff\1"
          + "\u00b2\2\uffff\1\u00b2\10\uffff\1\u00b2\22\uffff\1\u00b2\11"
          + "\uffff\1\u00b2\10\uffff\1\u00b2\13\uffff\1\u00b2\32\uffff\1"
          + "\u00b2\21\uffff\1\u00b2\1\uffff\1\u00b2\4\uffff\1\u00b2\12\uffff" + "\1\u00b2",
      "\1\u00b2\17\uffff\1\u00b2\24\uffff\1\2\5\uffff\1\u00b2\15\uffff"
          + "\1\u00b2\30\uffff\1\u00b2\32\uffff\1\u00b2\3\uffff\1\u00b2\1"
          + "\uffff\1\u00b2\10\uffff\1\u00b2\3\uffff\1\u00b2\6\uffff\1\u00b2"
          + "\2\uffff\2\u00b2\2\uffff\1\u00b2\11\uffff\1\u00b2\15\uffff\1"
          + "\u00b2\2\uffff\1\u00b2\10\uffff\1\u00b2\22\uffff\1\u00b2\11"
          + "\uffff\1\u00b2\10\uffff\1\u00b2\13\uffff\1\u00b2\32\uffff\1"
          + "\u00b2\21\uffff\1\u00b2\1\uffff\1\u00b2\4\uffff\1\u00b2\12\uffff" + "\1\u00b2",
      "\1\u00b2\17\uffff\1\u00b2\32\uffff\1\u00b2\15\uffff\1\u00b2"
          + "\30\uffff\1\u00b2\32\uffff\1\u00b2\3\uffff\1\u00b2\1\uffff\1"
          + "\u00b2\10\uffff\1\u00b2\3\uffff\1\u00b2\6\uffff\1\u00b2\2\uffff"
          + "\2\u00b2\2\uffff\1\u00b2\11\uffff\1\u00b2\15\uffff\1\u00b2\2"
          + "\uffff\1\u00b2\10\uffff\1\u00b2\22\uffff\1\u00b2\11\uffff\1"
          + "\u00b2\10\uffff\1\u00b2\13\uffff\1\u00b2\32\uffff\1\u00b2\21"
          + "\uffff\1\u00b2\1\uffff\1\u00b2\4\uffff\1\u00b2\5\uffff\1\2\4" + "\uffff\1\u00b2", "",
      "\1\u00b2\17\uffff\1\u00b2\3\uffff\1\2\26\uffff\1\u00b2\15\uffff"
          + "\1\u00b2\30\uffff\1\u00b2\32\uffff\1\u00b2\3\uffff\1\u00b2\1"
          + "\uffff\1\u00b2\10\uffff\1\u00b2\3\uffff\1\u00b2\6\uffff\1\u00b2"
          + "\2\uffff\2\u00b2\2\uffff\1\u00b2\11\uffff\1\u00b2\15\uffff\1"
          + "\u00b2\2\uffff\1\u00b2\10\uffff\1\u00b2\22\uffff\1\u00b2\11"
          + "\uffff\1\u00b2\10\uffff\1\u00b2\13\uffff\1\u00b2\32\uffff\1"
          + "\u00b2\21\uffff\1\u00b2\1\uffff\1\u00b2\4\uffff\1\u00b2\12\uffff" + "\1\u00b2",
      "\1\u00b2\17\uffff\1\u00b2\24\uffff\1\2\5\uffff\1\u00b2\15\uffff"
          + "\1\u00b2\30\uffff\1\u00b2\32\uffff\1\u00b2\3\uffff\1\u00b2\1"
          + "\uffff\1\u00b2\10\uffff\1\u00b2\3\uffff\1\u00b2\6\uffff\1\u00b2"
          + "\2\uffff\2\u00b2\2\uffff\1\u00b2\11\uffff\1\u00b2\15\uffff\1"
          + "\u00b2\2\uffff\1\u00b2\10\uffff\1\u00b2\22\uffff\1\u00b2\11"
          + "\uffff\1\u00b2\10\uffff\1\u00b2\13\uffff\1\u00b2\32\uffff\1"
          + "\u00b2\21\uffff\1\u00b2\1\uffff\1\u00b2\4\uffff\1\u00b2\12\uffff"
          + "\1\u00b2", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};

  static final short[] DFA32_eot = DFA.unpackEncodedString(DFA32_eotS);
  static final short[] DFA32_eof = DFA.unpackEncodedString(DFA32_eofS);
  static final char[] DFA32_min = DFA.unpackEncodedStringToUnsignedChars(DFA32_minS);
  static final char[] DFA32_max = DFA.unpackEncodedStringToUnsignedChars(DFA32_maxS);
  static final short[] DFA32_accept = DFA.unpackEncodedString(DFA32_acceptS);
  static final short[] DFA32_special = DFA.unpackEncodedString(DFA32_specialS);
  static final short[][] DFA32_transition;

  static {
    int numStates = DFA32_transitionS.length;
    DFA32_transition = new short[numStates][];
    for (int i = 0; i < numStates; i++) {
      DFA32_transition[i] = DFA.unpackEncodedString(DFA32_transitionS[i]);
    }
  }

  class DFA32 extends DFA {

    public DFA32(BaseRecognizer recognizer) {
      this.recognizer = recognizer;
      this.decisionNumber = 32;
      this.eot = DFA32_eot;
      this.eof = DFA32_eof;
      this.min = DFA32_min;
      this.max = DFA32_max;
      this.accept = DFA32_accept;
      this.special = DFA32_special;
      this.transition = DFA32_transition;
    }

    public String getDescription() {
      return "207:51: ( KW_AS )?";
    }
  }

  static final String DFA40_eotS = "\123\uffff";
  static final String DFA40_eofS = "\1\2\1\36\121\uffff";
  static final String DFA40_minS = "\2\12\33\uffff\1\7\65\uffff";
  static final String DFA40_maxS = "\2\u012d\33\uffff\1\u0135\65\uffff";
  static final String DFA40_acceptS = "\2\uffff\1\2\33\uffff\1\1\32\uffff\32\1";
  static final String DFA40_specialS = "\123\uffff}>";
  static final String[] DFA40_transitionS = {"\1\2\17\uffff\1\1\32\uffff\1\2\15\uffff\1\2\30\uffff\1\2\32"
      + "\uffff\1\2\3\uffff\1\2\1\uffff\1\2\10\uffff\1\2\3\uffff\1\2"
      + "\6\uffff\1\2\2\uffff\2\2\2\uffff\1\2\11\uffff\1\2\15\uffff\1"
      + "\2\2\uffff\1\2\10\uffff\1\2\22\uffff\1\2\11\uffff\1\2\10\uffff"
      + "\1\2\13\uffff\1\2\32\uffff\1\2\21\uffff\1\2\1\uffff\1\2\4\uffff" + "\1\2\12\uffff\1\2",
      "\1\36\17\uffff\1\36\32\uffff\1\36\15\uffff\1\36\30\uffff\1"
          + "\36\32\uffff\1\36\3\uffff\1\36\1\uffff\1\36\10\uffff\1\36\3"
          + "\uffff\1\36\6\uffff\1\36\2\uffff\2\36\2\uffff\1\36\11\uffff"
          + "\1\36\15\uffff\1\36\2\uffff\1\36\10\uffff\1\36\22\uffff\1\36"
          + "\11\uffff\1\36\10\uffff\1\36\13\uffff\1\36\32\uffff\1\36\21"
          + "\uffff\1\36\1\uffff\1\36\4\uffff\1\35\12\uffff\1\36", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "", "", "", "", "", "",
      "\1\100\5\uffff\1\104\4\uffff\1\103\7\uffff\1\117\6\122\1\uffff"
          + "\1\122\1\113\15\122\1\110\1\107\1\122\1\uffff\4\122\1\uffff"
          + "\6\122\1\uffff\2\122\1\uffff\1\122\1\uffff\2\111\2\122\1\uffff"
          + "\1\122\1\75\16\122\1\uffff\4\122\1\uffff\1\122\1\uffff\1\122"
          + "\1\uffff\1\122\1\120\2\122\1\uffff\1\122\1\106\6\122\1\uffff"
          + "\3\122\1\uffff\1\122\1\uffff\4\122\1\uffff\2\122\1\112\20\122"
          + "\1\uffff\4\122\1\uffff\12\122\1\uffff\1\114\4\122\1\uffff\3"
          + "\122\1\71\1\122\1\73\2\122\1\uffff\1\122\1\uffff\5\122\1\uffff"
          + "\2\122\1\uffff\5\122\2\uffff\14\122\1\uffff\22\122\1\uffff\22"
          + "\122\1\115\2\122\1\uffff\3\122\1\uffff\1\76\4\122\1\uffff\1"
          + "\122\1\105\2\122\1\uffff\2\122\1\116\1\uffff\14\122\1\uffff"
          + "\1\122\2\uffff\1\122\1\uffff\1\122\3\uffff\1\121\2\uffff\1\72"
          + "\2\uffff\1\74\1\72\10\uffff\1\101\1\77\1\72\1\102", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};

  static final short[] DFA40_eot = DFA.unpackEncodedString(DFA40_eotS);
  static final short[] DFA40_eof = DFA.unpackEncodedString(DFA40_eofS);
  static final char[] DFA40_min = DFA.unpackEncodedStringToUnsignedChars(DFA40_minS);
  static final char[] DFA40_max = DFA.unpackEncodedStringToUnsignedChars(DFA40_maxS);
  static final short[] DFA40_accept = DFA.unpackEncodedString(DFA40_acceptS);
  static final short[] DFA40_special = DFA.unpackEncodedString(DFA40_specialS);
  static final short[][] DFA40_transition;

  static {
    int numStates = DFA40_transitionS.length;
    DFA40_transition = new short[numStates][];
    for (int i = 0; i < numStates; i++) {
      DFA40_transition[i] = DFA.unpackEncodedString(DFA40_transitionS[i]);
    }
  }

  class DFA40 extends DFA {

    public DFA40(BaseRecognizer recognizer) {
      this.recognizer = recognizer;
      this.decisionNumber = 40;
      this.eot = DFA40_eot;
      this.eof = DFA40_eof;
      this.min = DFA40_min;
      this.max = DFA40_max;
      this.accept = DFA40_accept;
      this.special = DFA40_special;
      this.transition = DFA40_transition;
    }

    public String getDescription() {
      return "238:16: (alias= Identifier )?";
    }
  }

  public static final BitSet FOLLOW_STAR_in_tableAllColumns57 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_tableName_in_tableAllColumns79 = new BitSet(new long[] {0x0000000000020000L});
  public static final BitSet FOLLOW_DOT_in_tableAllColumns81 = new BitSet(
      new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0002000000000000L});
  public static final BitSet FOLLOW_STAR_in_tableAllColumns83 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_identifier_in_tableOrColumn131 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_expression_in_expressionList170 = new BitSet(new long[] {0x0000000000000402L});
  public static final BitSet FOLLOW_COMMA_in_expressionList173 = new BitSet(
      new long[] {0xFDEFFFFDFC042080L, 0xDEBBFDEAF7FFFBD6L, 0xF6FAFF7DFFBDFFFFL, 0xEEFFFFFBFFFF7FF9L, 0x003C032452FFF77BL});
  public static final BitSet FOLLOW_expression_in_expressionList175 = new BitSet(new long[] {0x0000000000000402L});
  public static final BitSet FOLLOW_identifier_in_aliasList217 = new BitSet(new long[] {0x0000000000000402L});
  public static final BitSet FOLLOW_COMMA_in_aliasList220 = new BitSet(
      new long[] {0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFB16L, 0xF6FAF779FFBDFFFEL, 0xEEFFFFFBFFFF7FF9L, 0x0000000052FFF77BL});
  public static final BitSet FOLLOW_identifier_in_aliasList222 = new BitSet(new long[] {0x0000000000000402L});
  public static final BitSet FOLLOW_KW_FROM_in_fromClause266 = new BitSet(
      new long[] {0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFB16L, 0xF6FAF779FFBDFFFEL, 0xEEFFFFFBFFFF7FF9L, 0x0000000452FFFF7BL});
  public static final BitSet FOLLOW_joinSource_in_fromClause268 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_fromSource_in_joinSource303 =
      new BitSet(new long[] {0x0000000000000402L, 0x0080000000000008L, 0x0000000000220040L, 0x0000000002000000L});
  public static final BitSet FOLLOW_joinToken_in_joinSource307 = new BitSet(
      new long[] {0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFB16L, 0xF6FAF779FFBDFFFEL, 0xEEFFFFFBFFFF7FF9L, 0x0000000452FFF77BL});
  public static final BitSet FOLLOW_fromSource_in_joinSource310 =
      new BitSet(new long[] {0x0000000000000402L, 0x0080000000000008L, 0x0001000000220040L, 0x0000000002000000L});
  public static final BitSet FOLLOW_KW_ON_in_joinSource314 = new BitSet(
      new long[] {0xFDEFFFFDFC042080L, 0xDEBBFDEAF7FFFBD6L, 0xF6FAFF7DFFBDFFFFL, 0xEEFFFFFBFFFF7FF9L, 0x003C032452FFF77BL});
  public static final BitSet FOLLOW_expression_in_joinSource317 =
      new BitSet(new long[] {0x0000000000000402L, 0x0080000000000008L, 0x0000000000220040L, 0x0000000002000000L});
  public static final BitSet FOLLOW_uniqueJoinToken_in_joinSource333 = new BitSet(
      new long[] {0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFB16L, 0xF6FAF779FFBDFFFEL, 0xEEFFFFFBFFFF7FFDL, 0x0000000452FFF77BL});
  public static final BitSet FOLLOW_uniqueJoinSource_in_joinSource336 = new BitSet(new long[] {0x0000000000000400L});
  public static final BitSet FOLLOW_COMMA_in_joinSource339 = new BitSet(
      new long[] {0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFB16L, 0xF6FAF779FFBDFFFEL, 0xEEFFFFFBFFFF7FFDL, 0x0000000452FFF77BL});
  public static final BitSet FOLLOW_uniqueJoinSource_in_joinSource342 = new BitSet(new long[] {0x0000000000000402L});
  public static final BitSet FOLLOW_KW_PRESERVE_in_uniqueJoinSource371 = new BitSet(
      new long[] {0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFB16L, 0xF6FAF779FFBDFFFEL, 0xEEFFFFFBFFFF7FF9L, 0x0000000452FFF77BL});
  public static final BitSet FOLLOW_fromSource_in_uniqueJoinSource374 = new BitSet(
      new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000400000000L});
  public static final BitSet FOLLOW_uniqueJoinExpr_in_uniqueJoinSource376 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_LPAREN_in_uniqueJoinExpr403 = new BitSet(
      new long[] {0xFDEFFFFDFC042080L, 0xDEBBFDEAF7FFFBD6L, 0xF6FAFF7DFFBDFFFFL, 0xEEFFFFFBFFFF7FF9L, 0x003C032452FFF77BL});
  public static final BitSet FOLLOW_expression_in_uniqueJoinExpr407 = new BitSet(
      new long[] {0x0000000000000400L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000200000000000L});
  public static final BitSet FOLLOW_COMMA_in_uniqueJoinExpr410 = new BitSet(
      new long[] {0xFDEFFFFDFC042080L, 0xDEBBFDEAF7FFFBD6L, 0xF6FAFF7DFFBDFFFFL, 0xEEFFFFFBFFFF7FF9L, 0x003C032452FFF77BL});
  public static final BitSet FOLLOW_expression_in_uniqueJoinExpr414 = new BitSet(
      new long[] {0x0000000000000400L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000200000000000L});
  public static final BitSet FOLLOW_RPAREN_in_uniqueJoinExpr418 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_KW_UNIQUEJOIN_in_uniqueJoinToken461 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_KW_JOIN_in_joinToken493 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_KW_INNER_in_joinToken526 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000020000L});
  public static final BitSet FOLLOW_KW_JOIN_in_joinToken528 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_COMMA_in_joinToken552 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_KW_CROSS_in_joinToken587 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000020000L});
  public static final BitSet FOLLOW_KW_JOIN_in_joinToken589 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_KW_LEFT_in_joinToken613 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0020000000020000L});
  public static final BitSet FOLLOW_KW_OUTER_in_joinToken617 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000020000L});
  public static final BitSet FOLLOW_KW_JOIN_in_joinToken621 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_KW_RIGHT_in_joinToken633 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0020000000020000L});
  public static final BitSet FOLLOW_KW_OUTER_in_joinToken636 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000020000L});
  public static final BitSet FOLLOW_KW_JOIN_in_joinToken640 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_KW_FULL_in_joinToken652 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0020000000020000L});
  public static final BitSet FOLLOW_KW_OUTER_in_joinToken656 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000020000L});
  public static final BitSet FOLLOW_KW_JOIN_in_joinToken660 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_KW_LEFT_in_joinToken672 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000800000000L});
  public static final BitSet FOLLOW_KW_SEMI_in_joinToken674 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000020000L});
  public static final BitSet FOLLOW_KW_JOIN_in_joinToken676 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_KW_LATERAL_in_lateralView710 = new BitSet(
      new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000002000000L});
  public static final BitSet FOLLOW_KW_VIEW_in_lateralView712 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0020000000000000L});
  public static final BitSet FOLLOW_KW_OUTER_in_lateralView714 = new BitSet(
      new long[] {0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFBD6L, 0xF6FAF77DFFBDFFFFL, 0xEEFFFFFBFFFF7FF9L, 0x0000000052FFF77BL});
  public static final BitSet FOLLOW_function_in_lateralView716 = new BitSet(
      new long[] {0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFB16L, 0xF6FAF779FFBDFFFEL, 0xEEFFFFFBFFFF7FF9L, 0x0000000052FFF77BL});
  public static final BitSet FOLLOW_tableAlias_in_lateralView718 = new BitSet(new long[] {0x0000001000000002L});
  public static final BitSet FOLLOW_KW_AS_in_lateralView721 = new BitSet(
      new long[] {0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFB16L, 0xF6FAF779FFBDFFFEL, 0xEEFFFFFBFFFF7FF9L, 0x0000000052FFF77BL});
  public static final BitSet FOLLOW_identifier_in_lateralView723 = new BitSet(new long[] {0x0000000000000402L});
  public static final BitSet FOLLOW_COMMA_in_lateralView731 = new BitSet(
      new long[] {0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFB16L, 0xF6FAF779FFBDFFFEL, 0xEEFFFFFBFFFF7FF9L, 0x0000000052FFF77BL});
  public static final BitSet FOLLOW_identifier_in_lateralView733 = new BitSet(new long[] {0x0000000000000402L});
  public static final BitSet FOLLOW_KW_LATERAL_in_lateralView765 = new BitSet(
      new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000002000000L});
  public static final BitSet FOLLOW_KW_VIEW_in_lateralView767 = new BitSet(
      new long[] {0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFBD6L, 0xF6FAF77DFFBDFFFFL, 0xEEFFFFFBFFFF7FF9L, 0x0000000052FFF77BL});
  public static final BitSet FOLLOW_function_in_lateralView769 = new BitSet(
      new long[] {0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFB16L, 0xF6FAF779FFBDFFFEL, 0xEEFFFFFBFFFF7FF9L, 0x0000000052FFF77BL});
  public static final BitSet FOLLOW_tableAlias_in_lateralView771 = new BitSet(new long[] {0x0000001000000002L});
  public static final BitSet FOLLOW_KW_AS_in_lateralView774 = new BitSet(
      new long[] {0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFB16L, 0xF6FAF779FFBDFFFEL, 0xEEFFFFFBFFFF7FF9L, 0x0000000052FFF77BL});
  public static final BitSet FOLLOW_identifier_in_lateralView776 = new BitSet(new long[] {0x0000000000000402L});
  public static final BitSet FOLLOW_COMMA_in_lateralView784 = new BitSet(
      new long[] {0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFB16L, 0xF6FAF779FFBDFFFEL, 0xEEFFFFFBFFFF7FF9L, 0x0000000052FFF77BL});
  public static final BitSet FOLLOW_identifier_in_lateralView786 = new BitSet(new long[] {0x0000000000000402L});
  public static final BitSet FOLLOW_identifier_in_tableAlias840 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_partitionedTableFunction_in_fromSource887 =
      new BitSet(new long[] {0x0000000000000002L, 0x0000000000000000L, 0x0000000000100000L});
  public static final BitSet FOLLOW_tableSource_in_fromSource891 =
      new BitSet(new long[] {0x0000000000000002L, 0x0000000000000000L, 0x0000000000100000L});
  public static final BitSet FOLLOW_subQuerySource_in_fromSource895 =
      new BitSet(new long[] {0x0000000000000002L, 0x0000000000000000L, 0x0000000000100000L});
  public static final BitSet FOLLOW_virtualTableSource_in_fromSource899 =
      new BitSet(new long[] {0x0000000000000002L, 0x0000000000000000L, 0x0000000000100000L});
  public static final BitSet FOLLOW_lateralView_in_fromSource903 =
      new BitSet(new long[] {0x0000000000000002L, 0x0000000000000000L, 0x0000000000100000L});
  public static final BitSet FOLLOW_KW_TABLESAMPLE_in_tableBucketSample937 = new BitSet(
      new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000400000000L});
  public static final BitSet FOLLOW_LPAREN_in_tableBucketSample939 = new BitSet(new long[] {0x0000200000000000L});
  public static final BitSet FOLLOW_KW_BUCKET_in_tableBucketSample941 = new BitSet(
      new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000010000000000L});
  public static final BitSet FOLLOW_Number_in_tableBucketSample946 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0010000000000000L});
  public static final BitSet FOLLOW_KW_OUT_in_tableBucketSample949 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000400000000000L});
  public static final BitSet FOLLOW_KW_OF_in_tableBucketSample951 = new BitSet(
      new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000010000000000L});
  public static final BitSet FOLLOW_Number_in_tableBucketSample956 = new BitSet(
      new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0001000000000000L, 0x0000000000000000L, 0x0000200000000000L});
  public static final BitSet FOLLOW_KW_ON_in_tableBucketSample960 = new BitSet(
      new long[] {0xFDEFFFFDFC042080L, 0xDEBBFDEAF7FFFBD6L, 0xF6FAFF7DFFBDFFFFL, 0xEEFFFFFBFFFF7FF9L, 0x003C032452FFF77BL});
  public static final BitSet FOLLOW_expression_in_tableBucketSample964 = new BitSet(
      new long[] {0x0000000000000400L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000200000000000L});
  public static final BitSet FOLLOW_COMMA_in_tableBucketSample967 = new BitSet(
      new long[] {0xFDEFFFFDFC042080L, 0xDEBBFDEAF7FFFBD6L, 0xF6FAFF7DFFBDFFFFL, 0xEEFFFFFBFFFF7FF9L, 0x003C032452FFF77BL});
  public static final BitSet FOLLOW_expression_in_tableBucketSample971 = new BitSet(
      new long[] {0x0000000000000400L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000200000000000L});
  public static final BitSet FOLLOW_RPAREN_in_tableBucketSample977 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_KW_TABLESAMPLE_in_splitSample1024 = new BitSet(
      new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000400000000L});
  public static final BitSet FOLLOW_LPAREN_in_splitSample1026 = new BitSet(
      new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000010000000000L});
  public static final BitSet FOLLOW_Number_in_splitSample1032 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000000L, 0x8000000000000000L, 0x0000000080000000L});
  public static final BitSet FOLLOW_KW_PERCENT_in_splitSample1038 = new BitSet(
      new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000200000000000L});
  public static final BitSet FOLLOW_KW_ROWS_in_splitSample1040 = new BitSet(
      new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000200000000000L});
  public static final BitSet FOLLOW_RPAREN_in_splitSample1043 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_KW_TABLESAMPLE_in_splitSample1087 = new BitSet(
      new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000400000000L});
  public static final BitSet FOLLOW_LPAREN_in_splitSample1089 = new BitSet(new long[] {0x0000000000000100L});
  public static final BitSet FOLLOW_ByteLengthLiteral_in_splitSample1095 = new BitSet(
      new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000200000000000L});
  public static final BitSet FOLLOW_RPAREN_in_splitSample1098 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_tableBucketSample_in_tableSample1144 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_splitSample_in_tableSample1152 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_tableName_in_tableSource1181 = new BitSet(
      new long[] {0x0000001004000002L, 0x0000000000000000L, 0x0000000000000000L, 0x0100000000000000L, 0x0000000400000000L});
  public static final BitSet FOLLOW_tableProperties_in_tableSource1186 =
      new BitSet(new long[] {0x0000001004000002L, 0x0000000000000000L, 0x0000000000000000L, 0x0100000000000000L});
  public static final BitSet FOLLOW_tableSample_in_tableSource1193 = new BitSet(new long[] {0x0000001004000002L});
  public static final BitSet FOLLOW_KW_AS_in_tableSource1198 = new BitSet(new long[] {0x0000000004000000L});
  public static final BitSet FOLLOW_Identifier_in_tableSource1203 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_identifier_in_tableName1263 = new BitSet(new long[] {0x0000000000020000L});
  public static final BitSet FOLLOW_DOT_in_tableName1265 = new BitSet(
      new long[] {0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFB16L, 0xF6FAF779FFBDFFFEL, 0xEEFFFFFBFFFF7FF9L, 0x0000000052FFF77BL});
  public static final BitSet FOLLOW_identifier_in_tableName1269 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_identifier_in_tableName1299 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_identifier_in_viewName1346 = new BitSet(new long[] {0x0000000000020000L});
  public static final BitSet FOLLOW_DOT_in_viewName1348 = new BitSet(
      new long[] {0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFB16L, 0xF6FAF779FFBDFFFEL, 0xEEFFFFFBFFFF7FF9L, 0x0000000052FFF77BL});
  public static final BitSet FOLLOW_identifier_in_viewName1354 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_LPAREN_in_subQuerySource1402 = new BitSet(
      new long[] {0x0000000000000000L, 0x0040000000000000L, 0x0000000400000400L, 0x0000000400008000L, 0x0000000040000000L});
  public static final BitSet FOLLOW_queryStatementExpression_in_subQuerySource1404 = new BitSet(
      new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000200000000000L});
  public static final BitSet FOLLOW_RPAREN_in_subQuerySource1407 = new BitSet(
      new long[] {0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFB16L, 0xF6FAF779FFBDFFFEL, 0xEEFFFFFBFFFF7FF9L, 0x0000000052FFF77BL});
  public static final BitSet FOLLOW_KW_AS_in_subQuerySource1409 = new BitSet(
      new long[] {0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFB16L, 0xF6FAF779FFBDFFFEL, 0xEEFFFFFBFFFF7FF9L, 0x0000000052FFF77BL});
  public static final BitSet FOLLOW_identifier_in_subQuerySource1412 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_partitionByClause_in_partitioningSpec1453 =
      new BitSet(new long[] {0x0000000000000002L, 0x0000000000000000L, 0x0008000000000000L});
  public static final BitSet FOLLOW_orderByClause_in_partitioningSpec1455 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_orderByClause_in_partitioningSpec1474 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_distributeByClause_in_partitioningSpec1489 =
      new BitSet(new long[] {0x0000000000000002L, 0x0000000000000000L, 0x0000000000000000L, 0x0000400000000000L});
  public static final BitSet FOLLOW_sortByClause_in_partitioningSpec1491 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_sortByClause_in_partitioningSpec1510 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_clusterByClause_in_partitioningSpec1525 =
      new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_subQuerySource_in_partitionTableFunctionSource1562 =
      new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_tableSource_in_partitionTableFunctionSource1569 =
      new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_partitionedTableFunction_in_partitionTableFunctionSource1576 =
      new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_Identifier_in_partitionedTableFunction1607 = new BitSet(
      new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000400000000L});
  public static final BitSet FOLLOW_LPAREN_in_partitionedTableFunction1612 =
      new BitSet(new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0001000000000000L});
  public static final BitSet FOLLOW_KW_ON_in_partitionedTableFunction1614 = new BitSet(
      new long[] {0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFB16L, 0xF6FAF779FFBDFFFEL, 0xEEFFFFFBFFFF7FF9L, 0x0000000452FFF77BL});
  public static final BitSet FOLLOW_partitionTableFunctionSource_in_partitionedTableFunction1618 = new BitSet(
      new long[] {0x0020000004000000L, 0x0000000010000000L, 0x1008000000000000L, 0x0000400000000000L, 0x0000200000000000L});
  public static final BitSet FOLLOW_partitioningSpec_in_partitionedTableFunction1620 = new BitSet(
      new long[] {0x0000000004000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000200000000000L});
  public static final BitSet FOLLOW_Identifier_in_partitionedTableFunction1642 = new BitSet(
      new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000400000000L});
  public static final BitSet FOLLOW_LPAREN_in_partitionedTableFunction1644 = new BitSet(
      new long[] {0xFDEFFFFDFC042080L, 0xDEBBFDEAF7FFFBD6L, 0xF6FAFF7DFFBDFFFFL, 0xEEFFFFFBFFFF7FF9L, 0x003C032452FFF77BL});
  public static final BitSet FOLLOW_expression_in_partitionedTableFunction1646 = new BitSet(
      new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000200000000000L});
  public static final BitSet FOLLOW_RPAREN_in_partitionedTableFunction1648 = new BitSet(
      new long[] {0x0000000000000400L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000200000000000L});
  public static final BitSet FOLLOW_COMMA_in_partitionedTableFunction1652 = new BitSet(new long[] {0x0000000004000000L});
  public static final BitSet FOLLOW_Identifier_in_partitionedTableFunction1654 = new BitSet(
      new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000400000000L});
  public static final BitSet FOLLOW_LPAREN_in_partitionedTableFunction1656 = new BitSet(
      new long[] {0xFDEFFFFDFC042080L, 0xDEBBFDEAF7FFFBD6L, 0xF6FAFF7DFFBDFFFFL, 0xEEFFFFFBFFFF7FF9L, 0x003C032452FFF77BL});
  public static final BitSet FOLLOW_expression_in_partitionedTableFunction1658 = new BitSet(
      new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000200000000000L});
  public static final BitSet FOLLOW_RPAREN_in_partitionedTableFunction1660 = new BitSet(
      new long[] {0x0000000000000400L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000200000000000L});
  public static final BitSet FOLLOW_RPAREN_in_partitionedTableFunction1670 =
      new BitSet(new long[] {0x0000000004000002L});
  public static final BitSet FOLLOW_Identifier_in_partitionedTableFunction1674 =
      new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_KW_WHERE_in_whereClause1734 = new BitSet(
      new long[] {0xFDEFFFFDFC042080L, 0xDEBBFDEAF7FFFBD6L, 0xF6FAFF7DFFBDFFFFL, 0xEEFFFFFBFFFF7FF9L, 0x003C032452FFF77BL});
  public static final BitSet FOLLOW_searchCondition_in_whereClause1736 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_expression_in_searchCondition1775 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_LPAREN_in_valueRowConstructor1802 = new BitSet(
      new long[] {0xFDEFFFFDFC042080L, 0xDEBBFDEAF7FFFBD6L, 0xF6FAF77DFFBDFFFFL, 0xEEFFFFFBFFFF7FF9L, 0x003C032452FFF77BL});
  public static final BitSet FOLLOW_precedenceUnaryPrefixExpression_in_valueRowConstructor1804 = new BitSet(
      new long[] {0x0000000000000400L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000200000000000L});
  public static final BitSet FOLLOW_COMMA_in_valueRowConstructor1807 = new BitSet(
      new long[] {0xFDEFFFFDFC042080L, 0xDEBBFDEAF7FFFBD6L, 0xF6FAF77DFFBDFFFFL, 0xEEFFFFFBFFFF7FF9L, 0x003C032452FFF77BL});
  public static final BitSet FOLLOW_precedenceUnaryPrefixExpression_in_valueRowConstructor1809 = new BitSet(
      new long[] {0x0000000000000400L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000200000000000L});
  public static final BitSet FOLLOW_RPAREN_in_valueRowConstructor1813 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_valueRowConstructor_in_valuesTableConstructor1843 =
      new BitSet(new long[] {0x0000000000000402L});
  public static final BitSet FOLLOW_COMMA_in_valuesTableConstructor1846 = new BitSet(
      new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000400000000L});
  public static final BitSet FOLLOW_valueRowConstructor_in_valuesTableConstructor1848 =
      new BitSet(new long[] {0x0000000000000402L});
  public static final BitSet FOLLOW_KW_VALUES_in_valuesClause1882 = new BitSet(
      new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000400000000L});
  public static final BitSet FOLLOW_valuesTableConstructor_in_valuesClause1884 =
      new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_LPAREN_in_virtualTableSource1911 = new BitSet(
      new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000400000L});
  public static final BitSet FOLLOW_valuesClause_in_virtualTableSource1913 = new BitSet(
      new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000200000000000L});
  public static final BitSet FOLLOW_RPAREN_in_virtualTableSource1915 = new BitSet(
      new long[] {0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFB16L, 0xF6FAF779FFBDFFFEL, 0xEEFFFFFBFFFF7FF9L, 0x0000000052FFF77BL});
  public static final BitSet FOLLOW_tableNameColList_in_virtualTableSource1917 =
      new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_KW_AS_in_tableNameColList1949 = new BitSet(
      new long[] {0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFB16L, 0xF6FAF779FFBDFFFEL, 0xEEFFFFFBFFFF7FF9L, 0x0000000052FFF77BL});
  public static final BitSet FOLLOW_identifier_in_tableNameColList1952 = new BitSet(
      new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000400000000L});
  public static final BitSet FOLLOW_LPAREN_in_tableNameColList1954 = new BitSet(
      new long[] {0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFB16L, 0xF6FAF779FFBDFFFEL, 0xEEFFFFFBFFFF7FF9L, 0x0000000052FFF77BL});
  public static final BitSet FOLLOW_identifier_in_tableNameColList1956 = new BitSet(
      new long[] {0x0000000000000400L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000200000000000L});
  public static final BitSet FOLLOW_COMMA_in_tableNameColList1959 = new BitSet(
      new long[] {0xFDE9FFFDFC000000L, 0xDEBBFDEAF7FFFB16L, 0xF6FAF779FFBDFFFEL, 0xEEFFFFFBFFFF7FF9L, 0x0000000052FFF77BL});
  public static final BitSet FOLLOW_identifier_in_tableNameColList1961 = new BitSet(
      new long[] {0x0000000000000400L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000200000000000L});
  public static final BitSet FOLLOW_RPAREN_in_tableNameColList1965 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_COMMA_in_synpred1_FromClauseParser727 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_COMMA_in_synpred2_FromClauseParser780 = new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_Identifier_in_synpred3_FromClauseParser881 = new BitSet(
      new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000400000000L});
  public static final BitSet FOLLOW_LPAREN_in_synpred3_FromClauseParser883 =
      new BitSet(new long[] {0x0000000000000002L});
  public static final BitSet FOLLOW_Identifier_in_synpred4_FromClauseParser1630 = new BitSet(
      new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000400000000L});
  public static final BitSet FOLLOW_LPAREN_in_synpred4_FromClauseParser1632 = new BitSet(
      new long[] {0xFDEFFFFDFC042080L, 0xDEBBFDEAF7FFFBD6L, 0xF6FAFF7DFFBDFFFFL, 0xEEFFFFFBFFFF7FF9L, 0x003C032452FFF77BL});
  public static final BitSet FOLLOW_expression_in_synpred4_FromClauseParser1634 = new BitSet(
      new long[] {0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000200000000000L});
  public static final BitSet FOLLOW_RPAREN_in_synpred4_FromClauseParser1636 =
      new BitSet(new long[] {0x0000000000000002L});
}
