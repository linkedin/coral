// $ANTLR 3.4 FromClauseParser.g 2021-01-01 01:23:45

package com.linkedin.coral.hive.hive2rel.parsetree.parser;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;


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
@SuppressWarnings({"all", "warnings", "unchecked"})
public class HiveParser_FromClauseParser extends Parser {
    public static final int EOF=-1;
    public static final int AMPERSAND=4;
    public static final int BITWISEOR=5;
    public static final int BITWISEXOR=6;
    public static final int BigintLiteral=7;
    public static final int ByteLengthLiteral=8;
    public static final int COLON=9;
    public static final int COMMA=10;
    public static final int COMMENT=11;
    public static final int CharSetLiteral=12;
    public static final int CharSetName=13;
    public static final int DIV=14;
    public static final int DIVIDE=15;
    public static final int DOLLAR=16;
    public static final int DOT=17;
    public static final int DecimalLiteral=18;
    public static final int Digit=19;
    public static final int EQUAL=20;
    public static final int EQUAL_NS=21;
    public static final int Exponent=22;
    public static final int GREATERTHAN=23;
    public static final int GREATERTHANOREQUALTO=24;
    public static final int HexDigit=25;
    public static final int Identifier=26;
    public static final int KW_ADD=27;
    public static final int KW_ADMIN=28;
    public static final int KW_AFTER=29;
    public static final int KW_ALL=30;
    public static final int KW_ALTER=31;
    public static final int KW_ANALYZE=32;
    public static final int KW_AND=33;
    public static final int KW_ARCHIVE=34;
    public static final int KW_ARRAY=35;
    public static final int KW_AS=36;
    public static final int KW_ASC=37;
    public static final int KW_AUTHORIZATION=38;
    public static final int KW_BEFORE=39;
    public static final int KW_BETWEEN=40;
    public static final int KW_BIGINT=41;
    public static final int KW_BINARY=42;
    public static final int KW_BOOLEAN=43;
    public static final int KW_BOTH=44;
    public static final int KW_BUCKET=45;
    public static final int KW_BUCKETS=46;
    public static final int KW_BY=47;
    public static final int KW_CASCADE=48;
    public static final int KW_CASE=49;
    public static final int KW_CAST=50;
    public static final int KW_CHANGE=51;
    public static final int KW_CHAR=52;
    public static final int KW_CLUSTER=53;
    public static final int KW_CLUSTERED=54;
    public static final int KW_CLUSTERSTATUS=55;
    public static final int KW_COLLECTION=56;
    public static final int KW_COLUMN=57;
    public static final int KW_COLUMNS=58;
    public static final int KW_COMMENT=59;
    public static final int KW_COMPACT=60;
    public static final int KW_COMPACTIONS=61;
    public static final int KW_COMPUTE=62;
    public static final int KW_CONCATENATE=63;
    public static final int KW_CONF=64;
    public static final int KW_CONTINUE=65;
    public static final int KW_CREATE=66;
    public static final int KW_CROSS=67;
    public static final int KW_CUBE=68;
    public static final int KW_CURRENT=69;
    public static final int KW_CURRENT_DATE=70;
    public static final int KW_CURRENT_TIMESTAMP=71;
    public static final int KW_CURSOR=72;
    public static final int KW_DATA=73;
    public static final int KW_DATABASE=74;
    public static final int KW_DATABASES=75;
    public static final int KW_DATE=76;
    public static final int KW_DATETIME=77;
    public static final int KW_DAY=78;
    public static final int KW_DBPROPERTIES=79;
    public static final int KW_DECIMAL=80;
    public static final int KW_DEFERRED=81;
    public static final int KW_DEFINED=82;
    public static final int KW_DELETE=83;
    public static final int KW_DELIMITED=84;
    public static final int KW_DEPENDENCY=85;
    public static final int KW_DESC=86;
    public static final int KW_DESCRIBE=87;
    public static final int KW_DIRECTORIES=88;
    public static final int KW_DIRECTORY=89;
    public static final int KW_DISABLE=90;
    public static final int KW_DISTINCT=91;
    public static final int KW_DISTRIBUTE=92;
    public static final int KW_DOUBLE=93;
    public static final int KW_DROP=94;
    public static final int KW_ELEM_TYPE=95;
    public static final int KW_ELSE=96;
    public static final int KW_ENABLE=97;
    public static final int KW_END=98;
    public static final int KW_ESCAPED=99;
    public static final int KW_EXCHANGE=100;
    public static final int KW_EXCLUSIVE=101;
    public static final int KW_EXISTS=102;
    public static final int KW_EXPLAIN=103;
    public static final int KW_EXPORT=104;
    public static final int KW_EXTENDED=105;
    public static final int KW_EXTERNAL=106;
    public static final int KW_FALSE=107;
    public static final int KW_FETCH=108;
    public static final int KW_FIELDS=109;
    public static final int KW_FILE=110;
    public static final int KW_FILEFORMAT=111;
    public static final int KW_FIRST=112;
    public static final int KW_FLOAT=113;
    public static final int KW_FOLLOWING=114;
    public static final int KW_FOR=115;
    public static final int KW_FORMAT=116;
    public static final int KW_FORMATTED=117;
    public static final int KW_FROM=118;
    public static final int KW_FULL=119;
    public static final int KW_FUNCTION=120;
    public static final int KW_FUNCTIONS=121;
    public static final int KW_GRANT=122;
    public static final int KW_GROUP=123;
    public static final int KW_GROUPING=124;
    public static final int KW_HAVING=125;
    public static final int KW_HOLD_DDLTIME=126;
    public static final int KW_HOUR=127;
    public static final int KW_IDXPROPERTIES=128;
    public static final int KW_IF=129;
    public static final int KW_IGNORE=130;
    public static final int KW_IMPORT=131;
    public static final int KW_IN=132;
    public static final int KW_INDEX=133;
    public static final int KW_INDEXES=134;
    public static final int KW_INNER=135;
    public static final int KW_INPATH=136;
    public static final int KW_INPUTDRIVER=137;
    public static final int KW_INPUTFORMAT=138;
    public static final int KW_INSERT=139;
    public static final int KW_INT=140;
    public static final int KW_INTERSECT=141;
    public static final int KW_INTERVAL=142;
    public static final int KW_INTO=143;
    public static final int KW_IS=144;
    public static final int KW_ITEMS=145;
    public static final int KW_JAR=146;
    public static final int KW_JOIN=147;
    public static final int KW_KEYS=148;
    public static final int KW_KEY_TYPE=149;
    public static final int KW_LATERAL=150;
    public static final int KW_LEFT=151;
    public static final int KW_LESS=152;
    public static final int KW_LIKE=153;
    public static final int KW_LIMIT=154;
    public static final int KW_LINES=155;
    public static final int KW_LOAD=156;
    public static final int KW_LOCAL=157;
    public static final int KW_LOCATION=158;
    public static final int KW_LOCK=159;
    public static final int KW_LOCKS=160;
    public static final int KW_LOGICAL=161;
    public static final int KW_LONG=162;
    public static final int KW_MACRO=163;
    public static final int KW_MAP=164;
    public static final int KW_MAPJOIN=165;
    public static final int KW_MATERIALIZED=166;
    public static final int KW_METADATA=167;
    public static final int KW_MINUS=168;
    public static final int KW_MINUTE=169;
    public static final int KW_MONTH=170;
    public static final int KW_MORE=171;
    public static final int KW_MSCK=172;
    public static final int KW_NONE=173;
    public static final int KW_NOSCAN=174;
    public static final int KW_NOT=175;
    public static final int KW_NO_DROP=176;
    public static final int KW_NULL=177;
    public static final int KW_OF=178;
    public static final int KW_OFFLINE=179;
    public static final int KW_ON=180;
    public static final int KW_OPTION=181;
    public static final int KW_OR=182;
    public static final int KW_ORDER=183;
    public static final int KW_OUT=184;
    public static final int KW_OUTER=185;
    public static final int KW_OUTPUTDRIVER=186;
    public static final int KW_OUTPUTFORMAT=187;
    public static final int KW_OVER=188;
    public static final int KW_OVERWRITE=189;
    public static final int KW_OWNER=190;
    public static final int KW_PARTIALSCAN=191;
    public static final int KW_PARTITION=192;
    public static final int KW_PARTITIONED=193;
    public static final int KW_PARTITIONS=194;
    public static final int KW_PERCENT=195;
    public static final int KW_PLUS=196;
    public static final int KW_PRECEDING=197;
    public static final int KW_PRESERVE=198;
    public static final int KW_PRETTY=199;
    public static final int KW_PRINCIPALS=200;
    public static final int KW_PROCEDURE=201;
    public static final int KW_PROTECTION=202;
    public static final int KW_PURGE=203;
    public static final int KW_RANGE=204;
    public static final int KW_READ=205;
    public static final int KW_READONLY=206;
    public static final int KW_READS=207;
    public static final int KW_REBUILD=208;
    public static final int KW_RECORDREADER=209;
    public static final int KW_RECORDWRITER=210;
    public static final int KW_REDUCE=211;
    public static final int KW_REGEXP=212;
    public static final int KW_RELOAD=213;
    public static final int KW_RENAME=214;
    public static final int KW_REPAIR=215;
    public static final int KW_REPLACE=216;
    public static final int KW_REPLICATION=217;
    public static final int KW_RESTRICT=218;
    public static final int KW_REVOKE=219;
    public static final int KW_REWRITE=220;
    public static final int KW_RIGHT=221;
    public static final int KW_RLIKE=222;
    public static final int KW_ROLE=223;
    public static final int KW_ROLES=224;
    public static final int KW_ROLLUP=225;
    public static final int KW_ROW=226;
    public static final int KW_ROWS=227;
    public static final int KW_SCHEMA=228;
    public static final int KW_SCHEMAS=229;
    public static final int KW_SECOND=230;
    public static final int KW_SELECT=231;
    public static final int KW_SEMI=232;
    public static final int KW_SERDE=233;
    public static final int KW_SERDEPROPERTIES=234;
    public static final int KW_SERVER=235;
    public static final int KW_SET=236;
    public static final int KW_SETS=237;
    public static final int KW_SHARED=238;
    public static final int KW_SHOW=239;
    public static final int KW_SHOW_DATABASE=240;
    public static final int KW_SKEWED=241;
    public static final int KW_SMALLINT=242;
    public static final int KW_SORT=243;
    public static final int KW_SORTED=244;
    public static final int KW_SSL=245;
    public static final int KW_STATISTICS=246;
    public static final int KW_STORED=247;
    public static final int KW_STREAMTABLE=248;
    public static final int KW_STRING=249;
    public static final int KW_STRUCT=250;
    public static final int KW_TABLE=251;
    public static final int KW_TABLES=252;
    public static final int KW_TABLESAMPLE=253;
    public static final int KW_TBLPROPERTIES=254;
    public static final int KW_TEMPORARY=255;
    public static final int KW_TERMINATED=256;
    public static final int KW_THEN=257;
    public static final int KW_TIMESTAMP=258;
    public static final int KW_TINYINT=259;
    public static final int KW_TO=260;
    public static final int KW_TOUCH=261;
    public static final int KW_TRANSACTIONS=262;
    public static final int KW_TRANSFORM=263;
    public static final int KW_TRIGGER=264;
    public static final int KW_TRUE=265;
    public static final int KW_TRUNCATE=266;
    public static final int KW_UNARCHIVE=267;
    public static final int KW_UNBOUNDED=268;
    public static final int KW_UNDO=269;
    public static final int KW_UNION=270;
    public static final int KW_UNIONTYPE=271;
    public static final int KW_UNIQUEJOIN=272;
    public static final int KW_UNLOCK=273;
    public static final int KW_UNSET=274;
    public static final int KW_UNSIGNED=275;
    public static final int KW_UPDATE=276;
    public static final int KW_URI=277;
    public static final int KW_USE=278;
    public static final int KW_USER=279;
    public static final int KW_USING=280;
    public static final int KW_UTC=281;
    public static final int KW_UTCTIMESTAMP=282;
    public static final int KW_VALUES=283;
    public static final int KW_VALUE_TYPE=284;
    public static final int KW_VARCHAR=285;
    public static final int KW_VIEW=286;
    public static final int KW_WHEN=287;
    public static final int KW_WHERE=288;
    public static final int KW_WHILE=289;
    public static final int KW_WINDOW=290;
    public static final int KW_WITH=291;
    public static final int KW_YEAR=292;
    public static final int LCURLY=293;
    public static final int LESSTHAN=294;
    public static final int LESSTHANOREQUALTO=295;
    public static final int LPAREN=296;
    public static final int LSQUARE=297;
    public static final int Letter=298;
    public static final int MINUS=299;
    public static final int MOD=300;
    public static final int NOTEQUAL=301;
    public static final int Number=302;
    public static final int PLUS=303;
    public static final int QUESTION=304;
    public static final int QuotedIdentifier=305;
    public static final int RCURLY=306;
    public static final int RPAREN=307;
    public static final int RSQUARE=308;
    public static final int RegexComponent=309;
    public static final int SEMICOLON=310;
    public static final int STAR=311;
    public static final int SmallintLiteral=312;
    public static final int StringLiteral=313;
    public static final int TILDE=314;
    public static final int TinyintLiteral=315;
    public static final int WS=316;
    public static final int TOK_ADMIN_OPTION_FOR=604;
    public static final int TOK_ALIASLIST=605;
    public static final int TOK_ALLCOLREF=606;
    public static final int TOK_ALTERDATABASE_OWNER=607;
    public static final int TOK_ALTERDATABASE_PROPERTIES=608;
    public static final int TOK_ALTERINDEX_PROPERTIES=609;
    public static final int TOK_ALTERINDEX_REBUILD=610;
    public static final int TOK_ALTERTABLE=611;
    public static final int TOK_ALTERTABLE_ADDCOLS=612;
    public static final int TOK_ALTERTABLE_ADDPARTS=613;
    public static final int TOK_ALTERTABLE_ARCHIVE=614;
    public static final int TOK_ALTERTABLE_BUCKETS=615;
    public static final int TOK_ALTERTABLE_CHANGECOL_AFTER_POSITION=616;
    public static final int TOK_ALTERTABLE_CLUSTER_SORT=617;
    public static final int TOK_ALTERTABLE_COMPACT=618;
    public static final int TOK_ALTERTABLE_DROPPARTS=619;
    public static final int TOK_ALTERTABLE_DROPPROPERTIES=620;
    public static final int TOK_ALTERTABLE_EXCHANGEPARTITION=621;
    public static final int TOK_ALTERTABLE_FILEFORMAT=622;
    public static final int TOK_ALTERTABLE_LOCATION=623;
    public static final int TOK_ALTERTABLE_MERGEFILES=624;
    public static final int TOK_ALTERTABLE_PARTCOLTYPE=625;
    public static final int TOK_ALTERTABLE_PROPERTIES=626;
    public static final int TOK_ALTERTABLE_PROTECTMODE=627;
    public static final int TOK_ALTERTABLE_RENAME=628;
    public static final int TOK_ALTERTABLE_RENAMECOL=629;
    public static final int TOK_ALTERTABLE_RENAMEPART=630;
    public static final int TOK_ALTERTABLE_REPLACECOLS=631;
    public static final int TOK_ALTERTABLE_SERDEPROPERTIES=632;
    public static final int TOK_ALTERTABLE_SERIALIZER=633;
    public static final int TOK_ALTERTABLE_SKEWED=634;
    public static final int TOK_ALTERTABLE_SKEWED_LOCATION=635;
    public static final int TOK_ALTERTABLE_TOUCH=636;
    public static final int TOK_ALTERTABLE_UNARCHIVE=637;
    public static final int TOK_ALTERTABLE_UPDATECOLSTATS=638;
    public static final int TOK_ALTERVIEW=639;
    public static final int TOK_ALTERVIEW_ADDPARTS=640;
    public static final int TOK_ALTERVIEW_DROPPARTS=641;
    public static final int TOK_ALTERVIEW_DROPPROPERTIES=642;
    public static final int TOK_ALTERVIEW_PROPERTIES=643;
    public static final int TOK_ALTERVIEW_RENAME=644;
    public static final int TOK_ANALYZE=645;
    public static final int TOK_ANONYMOUS=646;
    public static final int TOK_ARCHIVE=647;
    public static final int TOK_BIGINT=648;
    public static final int TOK_BINARY=649;
    public static final int TOK_BOOLEAN=650;
    public static final int TOK_CASCADE=651;
    public static final int TOK_CHAR=652;
    public static final int TOK_CHARSETLITERAL=653;
    public static final int TOK_CLUSTERBY=654;
    public static final int TOK_COLTYPELIST=655;
    public static final int TOK_COL_NAME=656;
    public static final int TOK_CREATEDATABASE=657;
    public static final int TOK_CREATEFUNCTION=658;
    public static final int TOK_CREATEINDEX=659;
    public static final int TOK_CREATEINDEX_INDEXTBLNAME=660;
    public static final int TOK_CREATEMACRO=661;
    public static final int TOK_CREATEROLE=662;
    public static final int TOK_CREATETABLE=663;
    public static final int TOK_CREATEVIEW=664;
    public static final int TOK_CROSSJOIN=665;
    public static final int TOK_CTE=666;
    public static final int TOK_CUBE_GROUPBY=667;
    public static final int TOK_DATABASECOMMENT=668;
    public static final int TOK_DATABASELOCATION=669;
    public static final int TOK_DATABASEPROPERTIES=670;
    public static final int TOK_DATE=671;
    public static final int TOK_DATELITERAL=672;
    public static final int TOK_DATETIME=673;
    public static final int TOK_DBPROPLIST=674;
    public static final int TOK_DB_TYPE=675;
    public static final int TOK_DECIMAL=676;
    public static final int TOK_DEFERRED_REBUILDINDEX=677;
    public static final int TOK_DELETE_FROM=678;
    public static final int TOK_DESCDATABASE=679;
    public static final int TOK_DESCFUNCTION=680;
    public static final int TOK_DESCTABLE=681;
    public static final int TOK_DESTINATION=682;
    public static final int TOK_DIR=683;
    public static final int TOK_DISABLE=684;
    public static final int TOK_DISTRIBUTEBY=685;
    public static final int TOK_DOUBLE=686;
    public static final int TOK_DROPDATABASE=687;
    public static final int TOK_DROPFUNCTION=688;
    public static final int TOK_DROPINDEX=689;
    public static final int TOK_DROPMACRO=690;
    public static final int TOK_DROPROLE=691;
    public static final int TOK_DROPTABLE=692;
    public static final int TOK_DROPVIEW=693;
    public static final int TOK_ENABLE=694;
    public static final int TOK_EXPLAIN=695;
    public static final int TOK_EXPLAIN_SQ_REWRITE=696;
    public static final int TOK_EXPLIST=697;
    public static final int TOK_EXPORT=698;
    public static final int TOK_FALSE=699;
    public static final int TOK_FILE=700;
    public static final int TOK_FILEFORMAT_GENERIC=701;
    public static final int TOK_FLOAT=702;
    public static final int TOK_FROM=703;
    public static final int TOK_FULLOUTERJOIN=704;
    public static final int TOK_FUNCTION=705;
    public static final int TOK_FUNCTIONDI=706;
    public static final int TOK_FUNCTIONSTAR=707;
    public static final int TOK_GRANT=708;
    public static final int TOK_GRANT_OPTION_FOR=709;
    public static final int TOK_GRANT_ROLE=710;
    public static final int TOK_GRANT_WITH_ADMIN_OPTION=711;
    public static final int TOK_GRANT_WITH_OPTION=712;
    public static final int TOK_GROUP=713;
    public static final int TOK_GROUPBY=714;
    public static final int TOK_GROUPING_SETS=715;
    public static final int TOK_GROUPING_SETS_EXPRESSION=716;
    public static final int TOK_HAVING=717;
    public static final int TOK_HINT=718;
    public static final int TOK_HINTARGLIST=719;
    public static final int TOK_HINTLIST=720;
    public static final int TOK_HOLD_DDLTIME=721;
    public static final int TOK_IFEXISTS=722;
    public static final int TOK_IFNOTEXISTS=723;
    public static final int TOK_IGNOREPROTECTION=724;
    public static final int TOK_IMPORT=725;
    public static final int TOK_INDEXCOMMENT=726;
    public static final int TOK_INDEXPROPERTIES=727;
    public static final int TOK_INDEXPROPLIST=728;
    public static final int TOK_INSERT=729;
    public static final int TOK_INSERT_INTO=730;
    public static final int TOK_INT=731;
    public static final int TOK_INTERVAL_DAY_LITERAL=732;
    public static final int TOK_INTERVAL_DAY_TIME=733;
    public static final int TOK_INTERVAL_DAY_TIME_LITERAL=734;
    public static final int TOK_INTERVAL_HOUR_LITERAL=735;
    public static final int TOK_INTERVAL_MINUTE_LITERAL=736;
    public static final int TOK_INTERVAL_MONTH_LITERAL=737;
    public static final int TOK_INTERVAL_SECOND_LITERAL=738;
    public static final int TOK_INTERVAL_YEAR_LITERAL=739;
    public static final int TOK_INTERVAL_YEAR_MONTH=740;
    public static final int TOK_INTERVAL_YEAR_MONTH_LITERAL=741;
    public static final int TOK_ISNOTNULL=742;
    public static final int TOK_ISNULL=743;
    public static final int TOK_JAR=744;
    public static final int TOK_JOIN=745;
    public static final int TOK_LATERAL_VIEW=746;
    public static final int TOK_LATERAL_VIEW_OUTER=747;
    public static final int TOK_LEFTOUTERJOIN=748;
    public static final int TOK_LEFTSEMIJOIN=749;
    public static final int TOK_LENGTH=750;
    public static final int TOK_LIKETABLE=751;
    public static final int TOK_LIMIT=752;
    public static final int TOK_LIST=753;
    public static final int TOK_LOAD=754;
    public static final int TOK_LOCKDB=755;
    public static final int TOK_LOCKTABLE=756;
    public static final int TOK_MAP=757;
    public static final int TOK_MAPJOIN=758;
    public static final int TOK_METADATA=759;
    public static final int TOK_MSCK=760;
    public static final int TOK_NOT_CLUSTERED=761;
    public static final int TOK_NOT_SORTED=762;
    public static final int TOK_NO_DROP=763;
    public static final int TOK_NULL=764;
    public static final int TOK_OFFLINE=765;
    public static final int TOK_OP_ADD=766;
    public static final int TOK_OP_AND=767;
    public static final int TOK_OP_BITAND=768;
    public static final int TOK_OP_BITNOT=769;
    public static final int TOK_OP_BITOR=770;
    public static final int TOK_OP_BITXOR=771;
    public static final int TOK_OP_DIV=772;
    public static final int TOK_OP_EQ=773;
    public static final int TOK_OP_GE=774;
    public static final int TOK_OP_GT=775;
    public static final int TOK_OP_LE=776;
    public static final int TOK_OP_LIKE=777;
    public static final int TOK_OP_LT=778;
    public static final int TOK_OP_MOD=779;
    public static final int TOK_OP_MUL=780;
    public static final int TOK_OP_NE=781;
    public static final int TOK_OP_NOT=782;
    public static final int TOK_OP_OR=783;
    public static final int TOK_OP_SUB=784;
    public static final int TOK_ORDERBY=785;
    public static final int TOK_ORREPLACE=786;
    public static final int TOK_PARTITIONINGSPEC=787;
    public static final int TOK_PARTITIONLOCATION=788;
    public static final int TOK_PARTSPEC=789;
    public static final int TOK_PARTVAL=790;
    public static final int TOK_PERCENT=791;
    public static final int TOK_PRINCIPAL_NAME=792;
    public static final int TOK_PRIVILEGE=793;
    public static final int TOK_PRIVILEGE_LIST=794;
    public static final int TOK_PRIV_ALL=795;
    public static final int TOK_PRIV_ALTER_DATA=796;
    public static final int TOK_PRIV_ALTER_METADATA=797;
    public static final int TOK_PRIV_CREATE=798;
    public static final int TOK_PRIV_DELETE=799;
    public static final int TOK_PRIV_DROP=800;
    public static final int TOK_PRIV_INDEX=801;
    public static final int TOK_PRIV_INSERT=802;
    public static final int TOK_PRIV_LOCK=803;
    public static final int TOK_PRIV_OBJECT=804;
    public static final int TOK_PRIV_OBJECT_COL=805;
    public static final int TOK_PRIV_SELECT=806;
    public static final int TOK_PRIV_SHOW_DATABASE=807;
    public static final int TOK_PTBLFUNCTION=808;
    public static final int TOK_QUERY=809;
    public static final int TOK_READONLY=810;
    public static final int TOK_RECORDREADER=811;
    public static final int TOK_RECORDWRITER=812;
    public static final int TOK_RELOADFUNCTION=813;
    public static final int TOK_REPLICATION=814;
    public static final int TOK_RESOURCE_ALL=815;
    public static final int TOK_RESOURCE_LIST=816;
    public static final int TOK_RESOURCE_URI=817;
    public static final int TOK_RESTRICT=818;
    public static final int TOK_REVOKE=819;
    public static final int TOK_REVOKE_ROLE=820;
    public static final int TOK_RIGHTOUTERJOIN=821;
    public static final int TOK_ROLE=822;
    public static final int TOK_ROLLUP_GROUPBY=823;
    public static final int TOK_ROWCOUNT=824;
    public static final int TOK_SELECT=825;
    public static final int TOK_SELECTDI=826;
    public static final int TOK_SELEXPR=827;
    public static final int TOK_SERDE=828;
    public static final int TOK_SERDENAME=829;
    public static final int TOK_SERDEPROPS=830;
    public static final int TOK_SERVER_TYPE=831;
    public static final int TOK_SET_COLUMNS_CLAUSE=832;
    public static final int TOK_SHOWCOLUMNS=833;
    public static final int TOK_SHOWCONF=834;
    public static final int TOK_SHOWDATABASES=835;
    public static final int TOK_SHOWDBLOCKS=836;
    public static final int TOK_SHOWFUNCTIONS=837;
    public static final int TOK_SHOWINDEXES=838;
    public static final int TOK_SHOWLOCKS=839;
    public static final int TOK_SHOWPARTITIONS=840;
    public static final int TOK_SHOWTABLES=841;
    public static final int TOK_SHOW_COMPACTIONS=842;
    public static final int TOK_SHOW_CREATETABLE=843;
    public static final int TOK_SHOW_GRANT=844;
    public static final int TOK_SHOW_ROLES=845;
    public static final int TOK_SHOW_ROLE_GRANT=846;
    public static final int TOK_SHOW_ROLE_PRINCIPALS=847;
    public static final int TOK_SHOW_SET_ROLE=848;
    public static final int TOK_SHOW_TABLESTATUS=849;
    public static final int TOK_SHOW_TBLPROPERTIES=850;
    public static final int TOK_SHOW_TRANSACTIONS=851;
    public static final int TOK_SKEWED_LOCATIONS=852;
    public static final int TOK_SKEWED_LOCATION_LIST=853;
    public static final int TOK_SKEWED_LOCATION_MAP=854;
    public static final int TOK_SMALLINT=855;
    public static final int TOK_SORTBY=856;
    public static final int TOK_STORAGEHANDLER=857;
    public static final int TOK_STOREDASDIRS=858;
    public static final int TOK_STREAMTABLE=859;
    public static final int TOK_STRING=860;
    public static final int TOK_STRINGLITERALSEQUENCE=861;
    public static final int TOK_STRUCT=862;
    public static final int TOK_SUBQUERY=863;
    public static final int TOK_SUBQUERY_EXPR=864;
    public static final int TOK_SUBQUERY_OP=865;
    public static final int TOK_SUBQUERY_OP_NOTEXISTS=866;
    public static final int TOK_SUBQUERY_OP_NOTIN=867;
    public static final int TOK_SWITCHDATABASE=868;
    public static final int TOK_TAB=869;
    public static final int TOK_TABALIAS=870;
    public static final int TOK_TABCOL=871;
    public static final int TOK_TABCOLLIST=872;
    public static final int TOK_TABCOLNAME=873;
    public static final int TOK_TABCOLVALUE=874;
    public static final int TOK_TABCOLVALUES=875;
    public static final int TOK_TABCOLVALUE_PAIR=876;
    public static final int TOK_TABLEBUCKETSAMPLE=877;
    public static final int TOK_TABLECOMMENT=878;
    public static final int TOK_TABLEFILEFORMAT=879;
    public static final int TOK_TABLELOCATION=880;
    public static final int TOK_TABLEPARTCOLS=881;
    public static final int TOK_TABLEPROPERTIES=882;
    public static final int TOK_TABLEPROPERTY=883;
    public static final int TOK_TABLEPROPLIST=884;
    public static final int TOK_TABLEROWFORMAT=885;
    public static final int TOK_TABLEROWFORMATCOLLITEMS=886;
    public static final int TOK_TABLEROWFORMATFIELD=887;
    public static final int TOK_TABLEROWFORMATLINES=888;
    public static final int TOK_TABLEROWFORMATMAPKEYS=889;
    public static final int TOK_TABLEROWFORMATNULL=890;
    public static final int TOK_TABLESERIALIZER=891;
    public static final int TOK_TABLESKEWED=892;
    public static final int TOK_TABLESPLITSAMPLE=893;
    public static final int TOK_TABLE_OR_COL=894;
    public static final int TOK_TABLE_PARTITION=895;
    public static final int TOK_TABLE_TYPE=896;
    public static final int TOK_TABNAME=897;
    public static final int TOK_TABREF=898;
    public static final int TOK_TABSORTCOLNAMEASC=899;
    public static final int TOK_TABSORTCOLNAMEDESC=900;
    public static final int TOK_TABSRC=901;
    public static final int TOK_TABTYPE=902;
    public static final int TOK_TEMPORARY=903;
    public static final int TOK_TIMESTAMP=904;
    public static final int TOK_TIMESTAMPLITERAL=905;
    public static final int TOK_TINYINT=906;
    public static final int TOK_TMP_FILE=907;
    public static final int TOK_TRANSFORM=908;
    public static final int TOK_TRUE=909;
    public static final int TOK_TRUNCATETABLE=910;
    public static final int TOK_UNIONALL=911;
    public static final int TOK_UNIONDISTINCT=912;
    public static final int TOK_UNIONTYPE=913;
    public static final int TOK_UNIQUEJOIN=914;
    public static final int TOK_UNLOCKDB=915;
    public static final int TOK_UNLOCKTABLE=916;
    public static final int TOK_UPDATE_TABLE=917;
    public static final int TOK_URI_TYPE=918;
    public static final int TOK_USER=919;
    public static final int TOK_USERSCRIPTCOLNAMES=920;
    public static final int TOK_USERSCRIPTCOLSCHEMA=921;
    public static final int TOK_VALUES_TABLE=922;
    public static final int TOK_VALUE_ROW=923;
    public static final int TOK_VARCHAR=924;
    public static final int TOK_VIEWPARTCOLS=925;
    public static final int TOK_VIRTUAL_TABLE=926;
    public static final int TOK_VIRTUAL_TABREF=927;
    public static final int TOK_WHERE=928;
    public static final int TOK_WINDOWDEF=929;
    public static final int TOK_WINDOWRANGE=930;
    public static final int TOK_WINDOWSPEC=931;
    public static final int TOK_WINDOWVALUES=932;

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
    public String[] getTokenNames() { return HiveParser.tokenNames; }
    public String getGrammarFileName() { return "FromClauseParser.g"; }


      @Override
      public Object recoverFromMismatchedSet(IntStream input,
          RecognitionException re, BitSet follow) throws RecognitionException {
        throw re;
      }
      @Override
      public void displayRecognitionError(String[] tokenNames,
          RecognitionException e) {
        gParent.errors.add(new ParseError(gParent, e, tokenNames));
      }
      protected boolean useSQL11ReservedKeywordsForIdentifier() {
        return gParent.useSQL11ReservedKeywordsForIdentifier();
      }


    public static class tableAllColumns_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "tableAllColumns"
    // FromClauseParser.g:51:1: tableAllColumns : ( STAR -> ^( TOK_ALLCOLREF ) | tableName DOT STAR -> ^( TOK_ALLCOLREF tableName ) );
    public final HiveParser_FromClauseParser.tableAllColumns_return tableAllColumns() throws RecognitionException {
        HiveParser_FromClauseParser.tableAllColumns_return retval = new HiveParser_FromClauseParser.tableAllColumns_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token STAR1=null;
        Token DOT3=null;
        Token STAR4=null;
        HiveParser_FromClauseParser.tableName_return tableName2 =null;


        CommonTree STAR1_tree=null;
        CommonTree DOT3_tree=null;
        CommonTree STAR4_tree=null;
        RewriteRuleTokenStream stream_STAR=new RewriteRuleTokenStream(adaptor,"token STAR");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_tableName=new RewriteRuleSubtreeStream(adaptor,"rule tableName");
        try {
            // FromClauseParser.g:52:5: ( STAR -> ^( TOK_ALLCOLREF ) | tableName DOT STAR -> ^( TOK_ALLCOLREF tableName ) )
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==STAR) ) {
                alt1=1;
            }
            else if ( ((LA1_0 >= Identifier && LA1_0 <= KW_ANALYZE)||(LA1_0 >= KW_ARCHIVE && LA1_0 <= KW_CASCADE)||LA1_0==KW_CHANGE||(LA1_0 >= KW_CLUSTER && LA1_0 <= KW_COLLECTION)||(LA1_0 >= KW_COLUMNS && LA1_0 <= KW_CONCATENATE)||(LA1_0 >= KW_CONTINUE && LA1_0 <= KW_CREATE)||LA1_0==KW_CUBE||(LA1_0 >= KW_CURRENT_DATE && LA1_0 <= KW_DATA)||(LA1_0 >= KW_DATABASES && LA1_0 <= KW_DISABLE)||(LA1_0 >= KW_DISTRIBUTE && LA1_0 <= KW_ELEM_TYPE)||LA1_0==KW_ENABLE||LA1_0==KW_ESCAPED||(LA1_0 >= KW_EXCLUSIVE && LA1_0 <= KW_EXPORT)||(LA1_0 >= KW_EXTERNAL && LA1_0 <= KW_FLOAT)||(LA1_0 >= KW_FOR && LA1_0 <= KW_FORMATTED)||LA1_0==KW_FULL||(LA1_0 >= KW_FUNCTIONS && LA1_0 <= KW_GROUPING)||(LA1_0 >= KW_HOLD_DDLTIME && LA1_0 <= KW_IDXPROPERTIES)||(LA1_0 >= KW_IGNORE && LA1_0 <= KW_INTERSECT)||(LA1_0 >= KW_INTO && LA1_0 <= KW_JAR)||(LA1_0 >= KW_KEYS && LA1_0 <= KW_LEFT)||(LA1_0 >= KW_LIKE && LA1_0 <= KW_LONG)||(LA1_0 >= KW_MAPJOIN && LA1_0 <= KW_MONTH)||(LA1_0 >= KW_MSCK && LA1_0 <= KW_NOSCAN)||(LA1_0 >= KW_NO_DROP && LA1_0 <= KW_OFFLINE)||LA1_0==KW_OPTION||(LA1_0 >= KW_ORDER && LA1_0 <= KW_OUTPUTFORMAT)||(LA1_0 >= KW_OVERWRITE && LA1_0 <= KW_OWNER)||(LA1_0 >= KW_PARTITION && LA1_0 <= KW_PLUS)||(LA1_0 >= KW_PRETTY && LA1_0 <= KW_RECORDWRITER)||(LA1_0 >= KW_REGEXP && LA1_0 <= KW_SECOND)||(LA1_0 >= KW_SEMI && LA1_0 <= KW_TABLES)||(LA1_0 >= KW_TBLPROPERTIES && LA1_0 <= KW_TERMINATED)||(LA1_0 >= KW_TIMESTAMP && LA1_0 <= KW_TRANSACTIONS)||(LA1_0 >= KW_TRIGGER && LA1_0 <= KW_UNARCHIVE)||(LA1_0 >= KW_UNDO && LA1_0 <= KW_UNIONTYPE)||(LA1_0 >= KW_UNLOCK && LA1_0 <= KW_VALUE_TYPE)||LA1_0==KW_VIEW||LA1_0==KW_WHILE||(LA1_0 >= KW_WITH && LA1_0 <= KW_YEAR)) ) {
                alt1=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;

            }
            switch (alt1) {
                case 1 :
                    // FromClauseParser.g:52:7: STAR
                    {
                    STAR1=(Token)match(input,STAR,FOLLOW_STAR_in_tableAllColumns57); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_STAR.add(STAR1);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 53:9: -> ^( TOK_ALLCOLREF )
                    {
                        // FromClauseParser.g:53:12: ^( TOK_ALLCOLREF )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(TOK_ALLCOLREF, "TOK_ALLCOLREF")
                        , root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 2 :
                    // FromClauseParser.g:54:7: tableName DOT STAR
                    {
                    pushFollow(FOLLOW_tableName_in_tableAllColumns79);
                    tableName2=tableName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_tableName.add(tableName2.getTree());

                    DOT3=(Token)match(input,DOT,FOLLOW_DOT_in_tableAllColumns81); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DOT.add(DOT3);


                    STAR4=(Token)match(input,STAR,FOLLOW_STAR_in_tableAllColumns83); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_STAR.add(STAR4);


                    // AST REWRITE
                    // elements: tableName
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 55:9: -> ^( TOK_ALLCOLREF tableName )
                    {
                        // FromClauseParser.g:55:12: ^( TOK_ALLCOLREF tableName )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(TOK_ALLCOLREF, "TOK_ALLCOLREF")
                        , root_1);

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


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "tableAllColumns"


    public static class tableOrColumn_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "tableOrColumn"
    // FromClauseParser.g:59:1: tableOrColumn : identifier -> ^( TOK_TABLE_OR_COL identifier ) ;
    public final HiveParser_FromClauseParser.tableOrColumn_return tableOrColumn() throws RecognitionException {
        HiveParser_FromClauseParser.tableOrColumn_return retval = new HiveParser_FromClauseParser.tableOrColumn_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        HiveParser_IdentifiersParser.identifier_return identifier5 =null;


        RewriteRuleSubtreeStream stream_identifier=new RewriteRuleSubtreeStream(adaptor,"rule identifier");
         gParent.pushMsg("table or column identifier", state); 
        try {
            // FromClauseParser.g:62:5: ( identifier -> ^( TOK_TABLE_OR_COL identifier ) )
            // FromClauseParser.g:63:5: identifier
            {
            pushFollow(FOLLOW_identifier_in_tableOrColumn131);
            identifier5=gHiveParser.identifier();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_identifier.add(identifier5.getTree());

            // AST REWRITE
            // elements: identifier
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 63:16: -> ^( TOK_TABLE_OR_COL identifier )
            {
                // FromClauseParser.g:63:19: ^( TOK_TABLE_OR_COL identifier )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(TOK_TABLE_OR_COL, "TOK_TABLE_OR_COL")
                , root_1);

                adaptor.addChild(root_1, stream_identifier.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) { gParent.popMsg(state); }
        }

        catch (RecognitionException e) {
          throw e;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "tableOrColumn"


    public static class expressionList_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "expressionList"
    // FromClauseParser.g:66:1: expressionList : expression ( COMMA expression )* -> ^( TOK_EXPLIST ( expression )+ ) ;
    public final HiveParser_FromClauseParser.expressionList_return expressionList() throws RecognitionException {
        HiveParser_FromClauseParser.expressionList_return retval = new HiveParser_FromClauseParser.expressionList_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token COMMA7=null;
        HiveParser_IdentifiersParser.expression_return expression6 =null;

        HiveParser_IdentifiersParser.expression_return expression8 =null;


        CommonTree COMMA7_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
         gParent.pushMsg("expression list", state); 
        try {
            // FromClauseParser.g:69:5: ( expression ( COMMA expression )* -> ^( TOK_EXPLIST ( expression )+ ) )
            // FromClauseParser.g:70:5: expression ( COMMA expression )*
            {
            pushFollow(FOLLOW_expression_in_expressionList170);
            expression6=gHiveParser.expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_expression.add(expression6.getTree());

            // FromClauseParser.g:70:16: ( COMMA expression )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==COMMA) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // FromClauseParser.g:70:17: COMMA expression
            	    {
            	    COMMA7=(Token)match(input,COMMA,FOLLOW_COMMA_in_expressionList173); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA7);


            	    pushFollow(FOLLOW_expression_in_expressionList175);
            	    expression8=gHiveParser.expression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_expression.add(expression8.getTree());

            	    }
            	    break;

            	default :
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
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 70:36: -> ^( TOK_EXPLIST ( expression )+ )
            {
                // FromClauseParser.g:70:39: ^( TOK_EXPLIST ( expression )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(TOK_EXPLIST, "TOK_EXPLIST")
                , root_1);

                if ( !(stream_expression.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_expression.hasNext() ) {
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


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) { gParent.popMsg(state); }
        }

        catch (RecognitionException e) {
          throw e;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "expressionList"


    public static class aliasList_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "aliasList"
    // FromClauseParser.g:73:1: aliasList : identifier ( COMMA identifier )* -> ^( TOK_ALIASLIST ( identifier )+ ) ;
    public final HiveParser_FromClauseParser.aliasList_return aliasList() throws RecognitionException {
        HiveParser_FromClauseParser.aliasList_return retval = new HiveParser_FromClauseParser.aliasList_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token COMMA10=null;
        HiveParser_IdentifiersParser.identifier_return identifier9 =null;

        HiveParser_IdentifiersParser.identifier_return identifier11 =null;


        CommonTree COMMA10_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_identifier=new RewriteRuleSubtreeStream(adaptor,"rule identifier");
         gParent.pushMsg("alias list", state); 
        try {
            // FromClauseParser.g:76:5: ( identifier ( COMMA identifier )* -> ^( TOK_ALIASLIST ( identifier )+ ) )
            // FromClauseParser.g:77:5: identifier ( COMMA identifier )*
            {
            pushFollow(FOLLOW_identifier_in_aliasList217);
            identifier9=gHiveParser.identifier();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_identifier.add(identifier9.getTree());

            // FromClauseParser.g:77:16: ( COMMA identifier )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==COMMA) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // FromClauseParser.g:77:17: COMMA identifier
            	    {
            	    COMMA10=(Token)match(input,COMMA,FOLLOW_COMMA_in_aliasList220); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA10);


            	    pushFollow(FOLLOW_identifier_in_aliasList222);
            	    identifier11=gHiveParser.identifier();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_identifier.add(identifier11.getTree());

            	    }
            	    break;

            	default :
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
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 77:36: -> ^( TOK_ALIASLIST ( identifier )+ )
            {
                // FromClauseParser.g:77:39: ^( TOK_ALIASLIST ( identifier )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(TOK_ALIASLIST, "TOK_ALIASLIST")
                , root_1);

                if ( !(stream_identifier.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_identifier.hasNext() ) {
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


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) { gParent.popMsg(state); }
        }

        catch (RecognitionException e) {
          throw e;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "aliasList"


    public static class fromClause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "fromClause"
    // FromClauseParser.g:82:1: fromClause : KW_FROM joinSource -> ^( TOK_FROM joinSource ) ;
    public final HiveParser_FromClauseParser.fromClause_return fromClause() throws RecognitionException {
        HiveParser_FromClauseParser.fromClause_return retval = new HiveParser_FromClauseParser.fromClause_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token KW_FROM12=null;
        HiveParser_FromClauseParser.joinSource_return joinSource13 =null;


        CommonTree KW_FROM12_tree=null;
        RewriteRuleTokenStream stream_KW_FROM=new RewriteRuleTokenStream(adaptor,"token KW_FROM");
        RewriteRuleSubtreeStream stream_joinSource=new RewriteRuleSubtreeStream(adaptor,"rule joinSource");
         gParent.pushMsg("from clause", state); 
        try {
            // FromClauseParser.g:85:5: ( KW_FROM joinSource -> ^( TOK_FROM joinSource ) )
            // FromClauseParser.g:86:5: KW_FROM joinSource
            {
            KW_FROM12=(Token)match(input,KW_FROM,FOLLOW_KW_FROM_in_fromClause266); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_KW_FROM.add(KW_FROM12);


            pushFollow(FOLLOW_joinSource_in_fromClause268);
            joinSource13=joinSource();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_joinSource.add(joinSource13.getTree());

            // AST REWRITE
            // elements: joinSource
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 86:24: -> ^( TOK_FROM joinSource )
            {
                // FromClauseParser.g:86:27: ^( TOK_FROM joinSource )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(TOK_FROM, "TOK_FROM")
                , root_1);

                adaptor.addChild(root_1, stream_joinSource.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) { gParent.popMsg(state); }
        }

        catch (RecognitionException e) {
          throw e;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "fromClause"


    public static class joinSource_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "joinSource"
    // FromClauseParser.g:89:1: joinSource : ( fromSource ( joinToken ^ fromSource ( KW_ON ! expression {...}?)? )* | uniqueJoinToken ^ uniqueJoinSource ( COMMA ! uniqueJoinSource )+ );
    public final HiveParser_FromClauseParser.joinSource_return joinSource() throws RecognitionException {
        HiveParser_FromClauseParser.joinSource_return retval = new HiveParser_FromClauseParser.joinSource_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token KW_ON17=null;
        Token COMMA21=null;
        HiveParser_FromClauseParser.fromSource_return fromSource14 =null;

        HiveParser_FromClauseParser.joinToken_return joinToken15 =null;

        HiveParser_FromClauseParser.fromSource_return fromSource16 =null;

        HiveParser_IdentifiersParser.expression_return expression18 =null;

        HiveParser_FromClauseParser.uniqueJoinToken_return uniqueJoinToken19 =null;

        HiveParser_FromClauseParser.uniqueJoinSource_return uniqueJoinSource20 =null;

        HiveParser_FromClauseParser.uniqueJoinSource_return uniqueJoinSource22 =null;


        CommonTree KW_ON17_tree=null;
        CommonTree COMMA21_tree=null;

         gParent.pushMsg("join source", state); 
        try {
            // FromClauseParser.g:92:5: ( fromSource ( joinToken ^ fromSource ( KW_ON ! expression {...}?)? )* | uniqueJoinToken ^ uniqueJoinSource ( COMMA ! uniqueJoinSource )+ )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( ((LA7_0 >= Identifier && LA7_0 <= KW_ANALYZE)||(LA7_0 >= KW_ARCHIVE && LA7_0 <= KW_CASCADE)||LA7_0==KW_CHANGE||(LA7_0 >= KW_CLUSTER && LA7_0 <= KW_COLLECTION)||(LA7_0 >= KW_COLUMNS && LA7_0 <= KW_CONCATENATE)||(LA7_0 >= KW_CONTINUE && LA7_0 <= KW_CREATE)||LA7_0==KW_CUBE||(LA7_0 >= KW_CURRENT_DATE && LA7_0 <= KW_DATA)||(LA7_0 >= KW_DATABASES && LA7_0 <= KW_DISABLE)||(LA7_0 >= KW_DISTRIBUTE && LA7_0 <= KW_ELEM_TYPE)||LA7_0==KW_ENABLE||LA7_0==KW_ESCAPED||(LA7_0 >= KW_EXCLUSIVE && LA7_0 <= KW_EXPORT)||(LA7_0 >= KW_EXTERNAL && LA7_0 <= KW_FLOAT)||(LA7_0 >= KW_FOR && LA7_0 <= KW_FORMATTED)||LA7_0==KW_FULL||(LA7_0 >= KW_FUNCTIONS && LA7_0 <= KW_GROUPING)||(LA7_0 >= KW_HOLD_DDLTIME && LA7_0 <= KW_IDXPROPERTIES)||(LA7_0 >= KW_IGNORE && LA7_0 <= KW_INTERSECT)||(LA7_0 >= KW_INTO && LA7_0 <= KW_JAR)||(LA7_0 >= KW_KEYS && LA7_0 <= KW_LEFT)||(LA7_0 >= KW_LIKE && LA7_0 <= KW_LONG)||(LA7_0 >= KW_MAPJOIN && LA7_0 <= KW_MONTH)||(LA7_0 >= KW_MSCK && LA7_0 <= KW_NOSCAN)||(LA7_0 >= KW_NO_DROP && LA7_0 <= KW_OFFLINE)||LA7_0==KW_OPTION||(LA7_0 >= KW_ORDER && LA7_0 <= KW_OUTPUTFORMAT)||(LA7_0 >= KW_OVERWRITE && LA7_0 <= KW_OWNER)||(LA7_0 >= KW_PARTITION && LA7_0 <= KW_PLUS)||(LA7_0 >= KW_PRETTY && LA7_0 <= KW_RECORDWRITER)||(LA7_0 >= KW_REGEXP && LA7_0 <= KW_SECOND)||(LA7_0 >= KW_SEMI && LA7_0 <= KW_TABLES)||(LA7_0 >= KW_TBLPROPERTIES && LA7_0 <= KW_TERMINATED)||(LA7_0 >= KW_TIMESTAMP && LA7_0 <= KW_TRANSACTIONS)||(LA7_0 >= KW_TRIGGER && LA7_0 <= KW_UNARCHIVE)||(LA7_0 >= KW_UNDO && LA7_0 <= KW_UNIONTYPE)||(LA7_0 >= KW_UNLOCK && LA7_0 <= KW_VALUE_TYPE)||LA7_0==KW_VIEW||LA7_0==KW_WHILE||(LA7_0 >= KW_WITH && LA7_0 <= KW_YEAR)||LA7_0==LPAREN) ) {
                alt7=1;
            }
            else if ( (LA7_0==KW_UNIQUEJOIN) ) {
                alt7=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;

            }
            switch (alt7) {
                case 1 :
                    // FromClauseParser.g:92:7: fromSource ( joinToken ^ fromSource ( KW_ON ! expression {...}?)? )*
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_fromSource_in_joinSource303);
                    fromSource14=fromSource();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, fromSource14.getTree());

                    // FromClauseParser.g:92:18: ( joinToken ^ fromSource ( KW_ON ! expression {...}?)? )*
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0==COMMA||LA5_0==KW_CROSS||LA5_0==KW_FULL||LA5_0==KW_INNER||LA5_0==KW_JOIN||LA5_0==KW_LEFT||LA5_0==KW_RIGHT) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // FromClauseParser.g:92:20: joinToken ^ fromSource ( KW_ON ! expression {...}?)?
                    	    {
                    	    pushFollow(FOLLOW_joinToken_in_joinSource307);
                    	    joinToken15=joinToken();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot(joinToken15.getTree(), root_0);

                    	    pushFollow(FOLLOW_fromSource_in_joinSource310);
                    	    fromSource16=fromSource();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, fromSource16.getTree());

                    	    // FromClauseParser.g:92:42: ( KW_ON ! expression {...}?)?
                    	    int alt4=2;
                    	    int LA4_0 = input.LA(1);

                    	    if ( (LA4_0==KW_ON) ) {
                    	        alt4=1;
                    	    }
                    	    switch (alt4) {
                    	        case 1 :
                    	            // FromClauseParser.g:92:44: KW_ON ! expression {...}?
                    	            {
                    	            KW_ON17=(Token)match(input,KW_ON,FOLLOW_KW_ON_in_joinSource314); if (state.failed) return retval;

                    	            pushFollow(FOLLOW_expression_in_joinSource317);
                    	            expression18=gHiveParser.expression();

                    	            state._fsp--;
                    	            if (state.failed) return retval;
                    	            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression18.getTree());

                    	            if ( !(((joinToken15!=null?((Token)joinToken15.start):null).getType() != COMMA)) ) {
                    	                if (state.backtracking>0) {state.failed=true; return retval;}
                    	                throw new FailedPredicateException(input, "joinSource", "$joinToken.start.getType() != COMMA");
                    	            }

                    	            }
                    	            break;

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop5;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // FromClauseParser.g:93:7: uniqueJoinToken ^ uniqueJoinSource ( COMMA ! uniqueJoinSource )+
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_uniqueJoinToken_in_joinSource333);
                    uniqueJoinToken19=uniqueJoinToken();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot(uniqueJoinToken19.getTree(), root_0);

                    pushFollow(FOLLOW_uniqueJoinSource_in_joinSource336);
                    uniqueJoinSource20=uniqueJoinSource();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, uniqueJoinSource20.getTree());

                    // FromClauseParser.g:93:41: ( COMMA ! uniqueJoinSource )+
                    int cnt6=0;
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==COMMA) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // FromClauseParser.g:93:42: COMMA ! uniqueJoinSource
                    	    {
                    	    COMMA21=(Token)match(input,COMMA,FOLLOW_COMMA_in_joinSource339); if (state.failed) return retval;

                    	    pushFollow(FOLLOW_uniqueJoinSource_in_joinSource342);
                    	    uniqueJoinSource22=uniqueJoinSource();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, uniqueJoinSource22.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt6 >= 1 ) break loop6;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(6, input);
                                throw eee;
                        }
                        cnt6++;
                    } while (true);


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) { gParent.popMsg(state); }
        }

        catch (RecognitionException e) {
          throw e;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "joinSource"


    public static class uniqueJoinSource_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "uniqueJoinSource"
    // FromClauseParser.g:96:1: uniqueJoinSource : ( KW_PRESERVE )? fromSource uniqueJoinExpr ;
    public final HiveParser_FromClauseParser.uniqueJoinSource_return uniqueJoinSource() throws RecognitionException {
        HiveParser_FromClauseParser.uniqueJoinSource_return retval = new HiveParser_FromClauseParser.uniqueJoinSource_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token KW_PRESERVE23=null;
        HiveParser_FromClauseParser.fromSource_return fromSource24 =null;

        HiveParser_FromClauseParser.uniqueJoinExpr_return uniqueJoinExpr25 =null;


        CommonTree KW_PRESERVE23_tree=null;

         gParent.pushMsg("join source", state); 
        try {
            // FromClauseParser.g:99:5: ( ( KW_PRESERVE )? fromSource uniqueJoinExpr )
            // FromClauseParser.g:99:7: ( KW_PRESERVE )? fromSource uniqueJoinExpr
            {
            root_0 = (CommonTree)adaptor.nil();


            // FromClauseParser.g:99:7: ( KW_PRESERVE )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==KW_PRESERVE) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // FromClauseParser.g:99:7: KW_PRESERVE
                    {
                    KW_PRESERVE23=(Token)match(input,KW_PRESERVE,FOLLOW_KW_PRESERVE_in_uniqueJoinSource371); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    KW_PRESERVE23_tree = 
                    (CommonTree)adaptor.create(KW_PRESERVE23)
                    ;
                    adaptor.addChild(root_0, KW_PRESERVE23_tree);
                    }

                    }
                    break;

            }


            pushFollow(FOLLOW_fromSource_in_uniqueJoinSource374);
            fromSource24=fromSource();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, fromSource24.getTree());

            pushFollow(FOLLOW_uniqueJoinExpr_in_uniqueJoinSource376);
            uniqueJoinExpr25=uniqueJoinExpr();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, uniqueJoinExpr25.getTree());

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) { gParent.popMsg(state); }
        }

        catch (RecognitionException e) {
          throw e;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "uniqueJoinSource"


    public static class uniqueJoinExpr_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "uniqueJoinExpr"
    // FromClauseParser.g:102:1: uniqueJoinExpr : LPAREN e1+= expression ( COMMA e1+= expression )* RPAREN -> ^( TOK_EXPLIST ( $e1)* ) ;
    public final HiveParser_FromClauseParser.uniqueJoinExpr_return uniqueJoinExpr() throws RecognitionException {
        HiveParser_FromClauseParser.uniqueJoinExpr_return retval = new HiveParser_FromClauseParser.uniqueJoinExpr_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token LPAREN26=null;
        Token COMMA27=null;
        Token RPAREN28=null;
        List list_e1=null;
        RuleReturnScope e1 = null;
        CommonTree LPAREN26_tree=null;
        CommonTree COMMA27_tree=null;
        CommonTree RPAREN28_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
         gParent.pushMsg("unique join expression list", state); 
        try {
            // FromClauseParser.g:105:5: ( LPAREN e1+= expression ( COMMA e1+= expression )* RPAREN -> ^( TOK_EXPLIST ( $e1)* ) )
            // FromClauseParser.g:105:7: LPAREN e1+= expression ( COMMA e1+= expression )* RPAREN
            {
            LPAREN26=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_uniqueJoinExpr403); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN26);


            pushFollow(FOLLOW_expression_in_uniqueJoinExpr407);
            e1=gHiveParser.expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_expression.add(e1.getTree());
            if (list_e1==null) list_e1=new ArrayList();
            list_e1.add(e1.getTree());


            // FromClauseParser.g:105:29: ( COMMA e1+= expression )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==COMMA) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // FromClauseParser.g:105:30: COMMA e1+= expression
            	    {
            	    COMMA27=(Token)match(input,COMMA,FOLLOW_COMMA_in_uniqueJoinExpr410); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA27);


            	    pushFollow(FOLLOW_expression_in_uniqueJoinExpr414);
            	    e1=gHiveParser.expression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_expression.add(e1.getTree());
            	    if (list_e1==null) list_e1=new ArrayList();
            	    list_e1.add(e1.getTree());


            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);


            RPAREN28=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_uniqueJoinExpr418); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN28);


            // AST REWRITE
            // elements: e1
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: e1
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_e1=new RewriteRuleSubtreeStream(adaptor,"token e1",list_e1);
            root_0 = (CommonTree)adaptor.nil();
            // 106:7: -> ^( TOK_EXPLIST ( $e1)* )
            {
                // FromClauseParser.g:106:10: ^( TOK_EXPLIST ( $e1)* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(TOK_EXPLIST, "TOK_EXPLIST")
                , root_1);

                // FromClauseParser.g:106:25: ( $e1)*
                while ( stream_e1.hasNext() ) {
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


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) { gParent.popMsg(state); }
        }

        catch (RecognitionException e) {
          throw e;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "uniqueJoinExpr"


    public static class uniqueJoinToken_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "uniqueJoinToken"
    // FromClauseParser.g:109:1: uniqueJoinToken : KW_UNIQUEJOIN -> TOK_UNIQUEJOIN ;
    public final HiveParser_FromClauseParser.uniqueJoinToken_return uniqueJoinToken() throws RecognitionException {
        HiveParser_FromClauseParser.uniqueJoinToken_return retval = new HiveParser_FromClauseParser.uniqueJoinToken_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token KW_UNIQUEJOIN29=null;

        CommonTree KW_UNIQUEJOIN29_tree=null;
        RewriteRuleTokenStream stream_KW_UNIQUEJOIN=new RewriteRuleTokenStream(adaptor,"token KW_UNIQUEJOIN");

         gParent.pushMsg("unique join", state); 
        try {
            // FromClauseParser.g:112:5: ( KW_UNIQUEJOIN -> TOK_UNIQUEJOIN )
            // FromClauseParser.g:112:7: KW_UNIQUEJOIN
            {
            KW_UNIQUEJOIN29=(Token)match(input,KW_UNIQUEJOIN,FOLLOW_KW_UNIQUEJOIN_in_uniqueJoinToken461); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_KW_UNIQUEJOIN.add(KW_UNIQUEJOIN29);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 112:21: -> TOK_UNIQUEJOIN
            {
                adaptor.addChild(root_0, 
                (CommonTree)adaptor.create(TOK_UNIQUEJOIN, "TOK_UNIQUEJOIN")
                );

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) { gParent.popMsg(state); }
        }

        catch (RecognitionException e) {
          throw e;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "uniqueJoinToken"


    public static class joinToken_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "joinToken"
    // FromClauseParser.g:114:1: joinToken : ( KW_JOIN -> TOK_JOIN | KW_INNER KW_JOIN -> TOK_JOIN | COMMA -> TOK_JOIN | KW_CROSS KW_JOIN -> TOK_CROSSJOIN | KW_LEFT ( KW_OUTER )? KW_JOIN -> TOK_LEFTOUTERJOIN | KW_RIGHT ( KW_OUTER )? KW_JOIN -> TOK_RIGHTOUTERJOIN | KW_FULL ( KW_OUTER )? KW_JOIN -> TOK_FULLOUTERJOIN | KW_LEFT KW_SEMI KW_JOIN -> TOK_LEFTSEMIJOIN );
    public final HiveParser_FromClauseParser.joinToken_return joinToken() throws RecognitionException {
        HiveParser_FromClauseParser.joinToken_return retval = new HiveParser_FromClauseParser.joinToken_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token KW_JOIN30=null;
        Token KW_INNER31=null;
        Token KW_JOIN32=null;
        Token COMMA33=null;
        Token KW_CROSS34=null;
        Token KW_JOIN35=null;
        Token KW_LEFT36=null;
        Token KW_OUTER37=null;
        Token KW_JOIN38=null;
        Token KW_RIGHT39=null;
        Token KW_OUTER40=null;
        Token KW_JOIN41=null;
        Token KW_FULL42=null;
        Token KW_OUTER43=null;
        Token KW_JOIN44=null;
        Token KW_LEFT45=null;
        Token KW_SEMI46=null;
        Token KW_JOIN47=null;

        CommonTree KW_JOIN30_tree=null;
        CommonTree KW_INNER31_tree=null;
        CommonTree KW_JOIN32_tree=null;
        CommonTree COMMA33_tree=null;
        CommonTree KW_CROSS34_tree=null;
        CommonTree KW_JOIN35_tree=null;
        CommonTree KW_LEFT36_tree=null;
        CommonTree KW_OUTER37_tree=null;
        CommonTree KW_JOIN38_tree=null;
        CommonTree KW_RIGHT39_tree=null;
        CommonTree KW_OUTER40_tree=null;
        CommonTree KW_JOIN41_tree=null;
        CommonTree KW_FULL42_tree=null;
        CommonTree KW_OUTER43_tree=null;
        CommonTree KW_JOIN44_tree=null;
        CommonTree KW_LEFT45_tree=null;
        CommonTree KW_SEMI46_tree=null;
        CommonTree KW_JOIN47_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_KW_RIGHT=new RewriteRuleTokenStream(adaptor,"token KW_RIGHT");
        RewriteRuleTokenStream stream_KW_CROSS=new RewriteRuleTokenStream(adaptor,"token KW_CROSS");
        RewriteRuleTokenStream stream_KW_FULL=new RewriteRuleTokenStream(adaptor,"token KW_FULL");
        RewriteRuleTokenStream stream_KW_JOIN=new RewriteRuleTokenStream(adaptor,"token KW_JOIN");
        RewriteRuleTokenStream stream_KW_OUTER=new RewriteRuleTokenStream(adaptor,"token KW_OUTER");
        RewriteRuleTokenStream stream_KW_SEMI=new RewriteRuleTokenStream(adaptor,"token KW_SEMI");
        RewriteRuleTokenStream stream_KW_LEFT=new RewriteRuleTokenStream(adaptor,"token KW_LEFT");
        RewriteRuleTokenStream stream_KW_INNER=new RewriteRuleTokenStream(adaptor,"token KW_INNER");

         gParent.pushMsg("join type specifier", state); 
        try {
            // FromClauseParser.g:117:5: ( KW_JOIN -> TOK_JOIN | KW_INNER KW_JOIN -> TOK_JOIN | COMMA -> TOK_JOIN | KW_CROSS KW_JOIN -> TOK_CROSSJOIN | KW_LEFT ( KW_OUTER )? KW_JOIN -> TOK_LEFTOUTERJOIN | KW_RIGHT ( KW_OUTER )? KW_JOIN -> TOK_RIGHTOUTERJOIN | KW_FULL ( KW_OUTER )? KW_JOIN -> TOK_FULLOUTERJOIN | KW_LEFT KW_SEMI KW_JOIN -> TOK_LEFTSEMIJOIN )
            int alt13=8;
            switch ( input.LA(1) ) {
            case KW_JOIN:
                {
                alt13=1;
                }
                break;
            case KW_INNER:
                {
                alt13=2;
                }
                break;
            case COMMA:
                {
                alt13=3;
                }
                break;
            case KW_CROSS:
                {
                alt13=4;
                }
                break;
            case KW_LEFT:
                {
                int LA13_5 = input.LA(2);

                if ( (LA13_5==KW_SEMI) ) {
                    alt13=8;
                }
                else if ( (LA13_5==KW_JOIN||LA13_5==KW_OUTER) ) {
                    alt13=5;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 13, 5, input);

                    throw nvae;

                }
                }
                break;
            case KW_RIGHT:
                {
                alt13=6;
                }
                break;
            case KW_FULL:
                {
                alt13=7;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;

            }

            switch (alt13) {
                case 1 :
                    // FromClauseParser.g:118:7: KW_JOIN
                    {
                    KW_JOIN30=(Token)match(input,KW_JOIN,FOLLOW_KW_JOIN_in_joinToken493); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_JOIN.add(KW_JOIN30);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 118:36: -> TOK_JOIN
                    {
                        adaptor.addChild(root_0, 
                        (CommonTree)adaptor.create(TOK_JOIN, "TOK_JOIN")
                        );

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 2 :
                    // FromClauseParser.g:119:7: KW_INNER KW_JOIN
                    {
                    KW_INNER31=(Token)match(input,KW_INNER,FOLLOW_KW_INNER_in_joinToken526); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_INNER.add(KW_INNER31);


                    KW_JOIN32=(Token)match(input,KW_JOIN,FOLLOW_KW_JOIN_in_joinToken528); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_JOIN.add(KW_JOIN32);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 119:36: -> TOK_JOIN
                    {
                        adaptor.addChild(root_0, 
                        (CommonTree)adaptor.create(TOK_JOIN, "TOK_JOIN")
                        );

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 3 :
                    // FromClauseParser.g:120:7: COMMA
                    {
                    COMMA33=(Token)match(input,COMMA,FOLLOW_COMMA_in_joinToken552); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_COMMA.add(COMMA33);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 120:36: -> TOK_JOIN
                    {
                        adaptor.addChild(root_0, 
                        (CommonTree)adaptor.create(TOK_JOIN, "TOK_JOIN")
                        );

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 4 :
                    // FromClauseParser.g:121:7: KW_CROSS KW_JOIN
                    {
                    KW_CROSS34=(Token)match(input,KW_CROSS,FOLLOW_KW_CROSS_in_joinToken587); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_CROSS.add(KW_CROSS34);


                    KW_JOIN35=(Token)match(input,KW_JOIN,FOLLOW_KW_JOIN_in_joinToken589); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_JOIN.add(KW_JOIN35);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 121:36: -> TOK_CROSSJOIN
                    {
                        adaptor.addChild(root_0, 
                        (CommonTree)adaptor.create(TOK_CROSSJOIN, "TOK_CROSSJOIN")
                        );

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 5 :
                    // FromClauseParser.g:122:7: KW_LEFT ( KW_OUTER )? KW_JOIN
                    {
                    KW_LEFT36=(Token)match(input,KW_LEFT,FOLLOW_KW_LEFT_in_joinToken613); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_LEFT.add(KW_LEFT36);


                    // FromClauseParser.g:122:16: ( KW_OUTER )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0==KW_OUTER) ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // FromClauseParser.g:122:17: KW_OUTER
                            {
                            KW_OUTER37=(Token)match(input,KW_OUTER,FOLLOW_KW_OUTER_in_joinToken617); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_KW_OUTER.add(KW_OUTER37);


                            }
                            break;

                    }


                    KW_JOIN38=(Token)match(input,KW_JOIN,FOLLOW_KW_JOIN_in_joinToken621); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_JOIN.add(KW_JOIN38);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 122:36: -> TOK_LEFTOUTERJOIN
                    {
                        adaptor.addChild(root_0, 
                        (CommonTree)adaptor.create(TOK_LEFTOUTERJOIN, "TOK_LEFTOUTERJOIN")
                        );

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 6 :
                    // FromClauseParser.g:123:7: KW_RIGHT ( KW_OUTER )? KW_JOIN
                    {
                    KW_RIGHT39=(Token)match(input,KW_RIGHT,FOLLOW_KW_RIGHT_in_joinToken633); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_RIGHT.add(KW_RIGHT39);


                    // FromClauseParser.g:123:16: ( KW_OUTER )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==KW_OUTER) ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // FromClauseParser.g:123:17: KW_OUTER
                            {
                            KW_OUTER40=(Token)match(input,KW_OUTER,FOLLOW_KW_OUTER_in_joinToken636); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_KW_OUTER.add(KW_OUTER40);


                            }
                            break;

                    }


                    KW_JOIN41=(Token)match(input,KW_JOIN,FOLLOW_KW_JOIN_in_joinToken640); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_JOIN.add(KW_JOIN41);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 123:36: -> TOK_RIGHTOUTERJOIN
                    {
                        adaptor.addChild(root_0, 
                        (CommonTree)adaptor.create(TOK_RIGHTOUTERJOIN, "TOK_RIGHTOUTERJOIN")
                        );

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 7 :
                    // FromClauseParser.g:124:7: KW_FULL ( KW_OUTER )? KW_JOIN
                    {
                    KW_FULL42=(Token)match(input,KW_FULL,FOLLOW_KW_FULL_in_joinToken652); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_FULL.add(KW_FULL42);


                    // FromClauseParser.g:124:16: ( KW_OUTER )?
                    int alt12=2;
                    int LA12_0 = input.LA(1);

                    if ( (LA12_0==KW_OUTER) ) {
                        alt12=1;
                    }
                    switch (alt12) {
                        case 1 :
                            // FromClauseParser.g:124:17: KW_OUTER
                            {
                            KW_OUTER43=(Token)match(input,KW_OUTER,FOLLOW_KW_OUTER_in_joinToken656); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_KW_OUTER.add(KW_OUTER43);


                            }
                            break;

                    }


                    KW_JOIN44=(Token)match(input,KW_JOIN,FOLLOW_KW_JOIN_in_joinToken660); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_JOIN.add(KW_JOIN44);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 124:36: -> TOK_FULLOUTERJOIN
                    {
                        adaptor.addChild(root_0, 
                        (CommonTree)adaptor.create(TOK_FULLOUTERJOIN, "TOK_FULLOUTERJOIN")
                        );

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 8 :
                    // FromClauseParser.g:125:7: KW_LEFT KW_SEMI KW_JOIN
                    {
                    KW_LEFT45=(Token)match(input,KW_LEFT,FOLLOW_KW_LEFT_in_joinToken672); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_LEFT.add(KW_LEFT45);


                    KW_SEMI46=(Token)match(input,KW_SEMI,FOLLOW_KW_SEMI_in_joinToken674); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_SEMI.add(KW_SEMI46);


                    KW_JOIN47=(Token)match(input,KW_JOIN,FOLLOW_KW_JOIN_in_joinToken676); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_JOIN.add(KW_JOIN47);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 125:36: -> TOK_LEFTSEMIJOIN
                    {
                        adaptor.addChild(root_0, 
                        (CommonTree)adaptor.create(TOK_LEFTSEMIJOIN, "TOK_LEFTSEMIJOIN")
                        );

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) { gParent.popMsg(state); }
        }

        catch (RecognitionException e) {
          throw e;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "joinToken"


    public static class lateralView_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "lateralView"
    // FromClauseParser.g:128:1: lateralView : ( ( KW_LATERAL KW_VIEW KW_OUTER )=> KW_LATERAL KW_VIEW KW_OUTER function tableAlias ( KW_AS identifier ( ( COMMA )=> COMMA identifier )* )? -> ^( TOK_LATERAL_VIEW_OUTER ^( TOK_SELECT ^( TOK_SELEXPR function ( identifier )* tableAlias ) ) ) | KW_LATERAL KW_VIEW function tableAlias ( KW_AS identifier ( ( COMMA )=> COMMA identifier )* )? -> ^( TOK_LATERAL_VIEW ^( TOK_SELECT ^( TOK_SELEXPR function ( identifier )* tableAlias ) ) ) );
    public final HiveParser_FromClauseParser.lateralView_return lateralView() throws RecognitionException {
        HiveParser_FromClauseParser.lateralView_return retval = new HiveParser_FromClauseParser.lateralView_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token KW_LATERAL48=null;
        Token KW_VIEW49=null;
        Token KW_OUTER50=null;
        Token KW_AS53=null;
        Token COMMA55=null;
        Token KW_LATERAL57=null;
        Token KW_VIEW58=null;
        Token KW_AS61=null;
        Token COMMA63=null;
        HiveParser_IdentifiersParser.function_return function51 =null;

        HiveParser_FromClauseParser.tableAlias_return tableAlias52 =null;

        HiveParser_IdentifiersParser.identifier_return identifier54 =null;

        HiveParser_IdentifiersParser.identifier_return identifier56 =null;

        HiveParser_IdentifiersParser.function_return function59 =null;

        HiveParser_FromClauseParser.tableAlias_return tableAlias60 =null;

        HiveParser_IdentifiersParser.identifier_return identifier62 =null;

        HiveParser_IdentifiersParser.identifier_return identifier64 =null;


        CommonTree KW_LATERAL48_tree=null;
        CommonTree KW_VIEW49_tree=null;
        CommonTree KW_OUTER50_tree=null;
        CommonTree KW_AS53_tree=null;
        CommonTree COMMA55_tree=null;
        CommonTree KW_LATERAL57_tree=null;
        CommonTree KW_VIEW58_tree=null;
        CommonTree KW_AS61_tree=null;
        CommonTree COMMA63_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_KW_VIEW=new RewriteRuleTokenStream(adaptor,"token KW_VIEW");
        RewriteRuleTokenStream stream_KW_OUTER=new RewriteRuleTokenStream(adaptor,"token KW_OUTER");
        RewriteRuleTokenStream stream_KW_AS=new RewriteRuleTokenStream(adaptor,"token KW_AS");
        RewriteRuleTokenStream stream_KW_LATERAL=new RewriteRuleTokenStream(adaptor,"token KW_LATERAL");
        RewriteRuleSubtreeStream stream_identifier=new RewriteRuleSubtreeStream(adaptor,"rule identifier");
        RewriteRuleSubtreeStream stream_function=new RewriteRuleSubtreeStream(adaptor,"rule function");
        RewriteRuleSubtreeStream stream_tableAlias=new RewriteRuleSubtreeStream(adaptor,"rule tableAlias");
        gParent.pushMsg("lateral view", state); 
        try {
            // FromClauseParser.g:131:2: ( ( KW_LATERAL KW_VIEW KW_OUTER )=> KW_LATERAL KW_VIEW KW_OUTER function tableAlias ( KW_AS identifier ( ( COMMA )=> COMMA identifier )* )? -> ^( TOK_LATERAL_VIEW_OUTER ^( TOK_SELECT ^( TOK_SELEXPR function ( identifier )* tableAlias ) ) ) | KW_LATERAL KW_VIEW function tableAlias ( KW_AS identifier ( ( COMMA )=> COMMA identifier )* )? -> ^( TOK_LATERAL_VIEW ^( TOK_SELECT ^( TOK_SELEXPR function ( identifier )* tableAlias ) ) ) )
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==KW_LATERAL) ) {
                int LA18_1 = input.LA(2);

                if ( (LA18_1==KW_VIEW) ) {
                    int LA18_2 = input.LA(3);

                    if ( (LA18_2==KW_OUTER) ) {
                        int LA18_3 = input.LA(4);

                        if ( (synpred1_FromClauseParser()) ) {
                            alt18=1;
                        }
                        else if ( (true) ) {
                            alt18=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 18, 3, input);

                            throw nvae;

                        }
                    }
                    else if ( ((LA18_2 >= Identifier && LA18_2 <= KW_ANALYZE)||(LA18_2 >= KW_ARCHIVE && LA18_2 <= KW_CASCADE)||LA18_2==KW_CHANGE||(LA18_2 >= KW_CLUSTER && LA18_2 <= KW_COLLECTION)||(LA18_2 >= KW_COLUMNS && LA18_2 <= KW_CONCATENATE)||(LA18_2 >= KW_CONTINUE && LA18_2 <= KW_CREATE)||LA18_2==KW_CUBE||(LA18_2 >= KW_CURRENT_DATE && LA18_2 <= KW_DATA)||(LA18_2 >= KW_DATABASES && LA18_2 <= KW_DISABLE)||(LA18_2 >= KW_DISTRIBUTE && LA18_2 <= KW_ELEM_TYPE)||LA18_2==KW_ENABLE||LA18_2==KW_ESCAPED||(LA18_2 >= KW_EXCLUSIVE && LA18_2 <= KW_EXPORT)||(LA18_2 >= KW_EXTERNAL && LA18_2 <= KW_FLOAT)||(LA18_2 >= KW_FOR && LA18_2 <= KW_FORMATTED)||LA18_2==KW_FULL||(LA18_2 >= KW_FUNCTIONS && LA18_2 <= KW_GROUPING)||(LA18_2 >= KW_HOLD_DDLTIME && LA18_2 <= KW_INTERSECT)||(LA18_2 >= KW_INTO && LA18_2 <= KW_JAR)||(LA18_2 >= KW_KEYS && LA18_2 <= KW_LEFT)||(LA18_2 >= KW_LIKE && LA18_2 <= KW_LONG)||(LA18_2 >= KW_MAP && LA18_2 <= KW_MONTH)||(LA18_2 >= KW_MSCK && LA18_2 <= KW_NOSCAN)||(LA18_2 >= KW_NO_DROP && LA18_2 <= KW_OFFLINE)||LA18_2==KW_OPTION||(LA18_2 >= KW_ORDER && LA18_2 <= KW_OUT)||(LA18_2 >= KW_OUTPUTDRIVER && LA18_2 <= KW_OUTPUTFORMAT)||(LA18_2 >= KW_OVERWRITE && LA18_2 <= KW_OWNER)||(LA18_2 >= KW_PARTITION && LA18_2 <= KW_PLUS)||(LA18_2 >= KW_PRETTY && LA18_2 <= KW_RECORDWRITER)||(LA18_2 >= KW_REGEXP && LA18_2 <= KW_SECOND)||(LA18_2 >= KW_SEMI && LA18_2 <= KW_TABLES)||(LA18_2 >= KW_TBLPROPERTIES && LA18_2 <= KW_TERMINATED)||(LA18_2 >= KW_TIMESTAMP && LA18_2 <= KW_TRANSACTIONS)||(LA18_2 >= KW_TRIGGER && LA18_2 <= KW_UNARCHIVE)||(LA18_2 >= KW_UNDO && LA18_2 <= KW_UNIONTYPE)||(LA18_2 >= KW_UNLOCK && LA18_2 <= KW_VALUE_TYPE)||LA18_2==KW_VIEW||LA18_2==KW_WHILE||(LA18_2 >= KW_WITH && LA18_2 <= KW_YEAR)) ) {
                        alt18=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 18, 2, input);

                        throw nvae;

                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 18, 1, input);

                    throw nvae;

                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;

            }
            switch (alt18) {
                case 1 :
                    // FromClauseParser.g:132:2: ( KW_LATERAL KW_VIEW KW_OUTER )=> KW_LATERAL KW_VIEW KW_OUTER function tableAlias ( KW_AS identifier ( ( COMMA )=> COMMA identifier )* )?
                    {
                    KW_LATERAL48=(Token)match(input,KW_LATERAL,FOLLOW_KW_LATERAL_in_lateralView720); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_LATERAL.add(KW_LATERAL48);


                    KW_VIEW49=(Token)match(input,KW_VIEW,FOLLOW_KW_VIEW_in_lateralView722); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_VIEW.add(KW_VIEW49);


                    KW_OUTER50=(Token)match(input,KW_OUTER,FOLLOW_KW_OUTER_in_lateralView724); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_OUTER.add(KW_OUTER50);


                    pushFollow(FOLLOW_function_in_lateralView726);
                    function51=gHiveParser.function();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_function.add(function51.getTree());

                    pushFollow(FOLLOW_tableAlias_in_lateralView728);
                    tableAlias52=tableAlias();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_tableAlias.add(tableAlias52.getTree());

                    // FromClauseParser.g:132:83: ( KW_AS identifier ( ( COMMA )=> COMMA identifier )* )?
                    int alt15=2;
                    int LA15_0 = input.LA(1);

                    if ( (LA15_0==KW_AS) ) {
                        alt15=1;
                    }
                    switch (alt15) {
                        case 1 :
                            // FromClauseParser.g:132:84: KW_AS identifier ( ( COMMA )=> COMMA identifier )*
                            {
                            KW_AS53=(Token)match(input,KW_AS,FOLLOW_KW_AS_in_lateralView731); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_KW_AS.add(KW_AS53);


                            pushFollow(FOLLOW_identifier_in_lateralView733);
                            identifier54=gHiveParser.identifier();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_identifier.add(identifier54.getTree());

                            // FromClauseParser.g:132:101: ( ( COMMA )=> COMMA identifier )*
                            loop14:
                            do {
                                int alt14=2;
                                alt14 = dfa14.predict(input);
                                switch (alt14) {
                            	case 1 :
                            	    // FromClauseParser.g:132:102: ( COMMA )=> COMMA identifier
                            	    {
                            	    COMMA55=(Token)match(input,COMMA,FOLLOW_COMMA_in_lateralView741); if (state.failed) return retval; 
                            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA55);


                            	    pushFollow(FOLLOW_identifier_in_lateralView743);
                            	    identifier56=gHiveParser.identifier();

                            	    state._fsp--;
                            	    if (state.failed) return retval;
                            	    if ( state.backtracking==0 ) stream_identifier.add(identifier56.getTree());

                            	    }
                            	    break;

                            	default :
                            	    break loop14;
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
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 133:2: -> ^( TOK_LATERAL_VIEW_OUTER ^( TOK_SELECT ^( TOK_SELEXPR function ( identifier )* tableAlias ) ) )
                    {
                        // FromClauseParser.g:133:5: ^( TOK_LATERAL_VIEW_OUTER ^( TOK_SELECT ^( TOK_SELEXPR function ( identifier )* tableAlias ) ) )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(TOK_LATERAL_VIEW_OUTER, "TOK_LATERAL_VIEW_OUTER")
                        , root_1);

                        // FromClauseParser.g:133:30: ^( TOK_SELECT ^( TOK_SELEXPR function ( identifier )* tableAlias ) )
                        {
                        CommonTree root_2 = (CommonTree)adaptor.nil();
                        root_2 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(TOK_SELECT, "TOK_SELECT")
                        , root_2);

                        // FromClauseParser.g:133:43: ^( TOK_SELEXPR function ( identifier )* tableAlias )
                        {
                        CommonTree root_3 = (CommonTree)adaptor.nil();
                        root_3 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(TOK_SELEXPR, "TOK_SELEXPR")
                        , root_3);

                        adaptor.addChild(root_3, stream_function.nextTree());

                        // FromClauseParser.g:133:66: ( identifier )*
                        while ( stream_identifier.hasNext() ) {
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
                case 2 :
                    // FromClauseParser.g:135:2: KW_LATERAL KW_VIEW function tableAlias ( KW_AS identifier ( ( COMMA )=> COMMA identifier )* )?
                    {
                    KW_LATERAL57=(Token)match(input,KW_LATERAL,FOLLOW_KW_LATERAL_in_lateralView775); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_LATERAL.add(KW_LATERAL57);


                    KW_VIEW58=(Token)match(input,KW_VIEW,FOLLOW_KW_VIEW_in_lateralView777); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_VIEW.add(KW_VIEW58);


                    pushFollow(FOLLOW_function_in_lateralView779);
                    function59=gHiveParser.function();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_function.add(function59.getTree());

                    pushFollow(FOLLOW_tableAlias_in_lateralView781);
                    tableAlias60=tableAlias();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_tableAlias.add(tableAlias60.getTree());

                    // FromClauseParser.g:135:41: ( KW_AS identifier ( ( COMMA )=> COMMA identifier )* )?
                    int alt17=2;
                    int LA17_0 = input.LA(1);

                    if ( (LA17_0==KW_AS) ) {
                        alt17=1;
                    }
                    switch (alt17) {
                        case 1 :
                            // FromClauseParser.g:135:42: KW_AS identifier ( ( COMMA )=> COMMA identifier )*
                            {
                            KW_AS61=(Token)match(input,KW_AS,FOLLOW_KW_AS_in_lateralView784); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_KW_AS.add(KW_AS61);


                            pushFollow(FOLLOW_identifier_in_lateralView786);
                            identifier62=gHiveParser.identifier();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_identifier.add(identifier62.getTree());

                            // FromClauseParser.g:135:59: ( ( COMMA )=> COMMA identifier )*
                            loop16:
                            do {
                                int alt16=2;
                                alt16 = dfa16.predict(input);
                                switch (alt16) {
                            	case 1 :
                            	    // FromClauseParser.g:135:60: ( COMMA )=> COMMA identifier
                            	    {
                            	    COMMA63=(Token)match(input,COMMA,FOLLOW_COMMA_in_lateralView794); if (state.failed) return retval; 
                            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA63);


                            	    pushFollow(FOLLOW_identifier_in_lateralView796);
                            	    identifier64=gHiveParser.identifier();

                            	    state._fsp--;
                            	    if (state.failed) return retval;
                            	    if ( state.backtracking==0 ) stream_identifier.add(identifier64.getTree());

                            	    }
                            	    break;

                            	default :
                            	    break loop16;
                                }
                            } while (true);


                            }
                            break;

                    }


                    // AST REWRITE
                    // elements: function, tableAlias, identifier
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 136:2: -> ^( TOK_LATERAL_VIEW ^( TOK_SELECT ^( TOK_SELEXPR function ( identifier )* tableAlias ) ) )
                    {
                        // FromClauseParser.g:136:5: ^( TOK_LATERAL_VIEW ^( TOK_SELECT ^( TOK_SELEXPR function ( identifier )* tableAlias ) ) )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(TOK_LATERAL_VIEW, "TOK_LATERAL_VIEW")
                        , root_1);

                        // FromClauseParser.g:136:24: ^( TOK_SELECT ^( TOK_SELEXPR function ( identifier )* tableAlias ) )
                        {
                        CommonTree root_2 = (CommonTree)adaptor.nil();
                        root_2 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(TOK_SELECT, "TOK_SELECT")
                        , root_2);

                        // FromClauseParser.g:136:37: ^( TOK_SELEXPR function ( identifier )* tableAlias )
                        {
                        CommonTree root_3 = (CommonTree)adaptor.nil();
                        root_3 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(TOK_SELEXPR, "TOK_SELEXPR")
                        , root_3);

                        adaptor.addChild(root_3, stream_function.nextTree());

                        // FromClauseParser.g:136:60: ( identifier )*
                        while ( stream_identifier.hasNext() ) {
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


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) {gParent.popMsg(state); }
        }

        catch (RecognitionException e) {
          throw e;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "lateralView"


    public static class tableAlias_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "tableAlias"
    // FromClauseParser.g:139:1: tableAlias : identifier -> ^( TOK_TABALIAS identifier ) ;
    public final HiveParser_FromClauseParser.tableAlias_return tableAlias() throws RecognitionException {
        HiveParser_FromClauseParser.tableAlias_return retval = new HiveParser_FromClauseParser.tableAlias_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        HiveParser_IdentifiersParser.identifier_return identifier65 =null;


        RewriteRuleSubtreeStream stream_identifier=new RewriteRuleSubtreeStream(adaptor,"rule identifier");
        gParent.pushMsg("table alias", state); 
        try {
            // FromClauseParser.g:142:5: ( identifier -> ^( TOK_TABALIAS identifier ) )
            // FromClauseParser.g:143:5: identifier
            {
            pushFollow(FOLLOW_identifier_in_tableAlias850);
            identifier65=gHiveParser.identifier();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_identifier.add(identifier65.getTree());

            // AST REWRITE
            // elements: identifier
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 143:16: -> ^( TOK_TABALIAS identifier )
            {
                // FromClauseParser.g:143:19: ^( TOK_TABALIAS identifier )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(TOK_TABALIAS, "TOK_TABALIAS")
                , root_1);

                adaptor.addChild(root_1, stream_identifier.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) {gParent.popMsg(state); }
        }

        catch (RecognitionException e) {
          throw e;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "tableAlias"


    public static class fromSource_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "fromSource"
    // FromClauseParser.g:146:1: fromSource : ( ( Identifier LPAREN )=> partitionedTableFunction | tableSource | subQuerySource | virtualTableSource ) ( lateralView ^)* ;
    public final HiveParser_FromClauseParser.fromSource_return fromSource() throws RecognitionException {
        HiveParser_FromClauseParser.fromSource_return retval = new HiveParser_FromClauseParser.fromSource_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        HiveParser_FromClauseParser.partitionedTableFunction_return partitionedTableFunction66 =null;

        HiveParser_FromClauseParser.tableSource_return tableSource67 =null;

        HiveParser_FromClauseParser.subQuerySource_return subQuerySource68 =null;

        HiveParser_FromClauseParser.virtualTableSource_return virtualTableSource69 =null;

        HiveParser_FromClauseParser.lateralView_return lateralView70 =null;



         gParent.pushMsg("from source", state); 
        try {
            // FromClauseParser.g:149:5: ( ( ( Identifier LPAREN )=> partitionedTableFunction | tableSource | subQuerySource | virtualTableSource ) ( lateralView ^)* )
            // FromClauseParser.g:150:5: ( ( Identifier LPAREN )=> partitionedTableFunction | tableSource | subQuerySource | virtualTableSource ) ( lateralView ^)*
            {
            root_0 = (CommonTree)adaptor.nil();


            // FromClauseParser.g:150:5: ( ( Identifier LPAREN )=> partitionedTableFunction | tableSource | subQuerySource | virtualTableSource )
            int alt19=4;
            alt19 = dfa19.predict(input);
            switch (alt19) {
                case 1 :
                    // FromClauseParser.g:150:6: ( Identifier LPAREN )=> partitionedTableFunction
                    {
                    pushFollow(FOLLOW_partitionedTableFunction_in_fromSource897);
                    partitionedTableFunction66=partitionedTableFunction();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, partitionedTableFunction66.getTree());

                    }
                    break;
                case 2 :
                    // FromClauseParser.g:150:55: tableSource
                    {
                    pushFollow(FOLLOW_tableSource_in_fromSource901);
                    tableSource67=tableSource();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, tableSource67.getTree());

                    }
                    break;
                case 3 :
                    // FromClauseParser.g:150:69: subQuerySource
                    {
                    pushFollow(FOLLOW_subQuerySource_in_fromSource905);
                    subQuerySource68=subQuerySource();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, subQuerySource68.getTree());

                    }
                    break;
                case 4 :
                    // FromClauseParser.g:150:86: virtualTableSource
                    {
                    pushFollow(FOLLOW_virtualTableSource_in_fromSource909);
                    virtualTableSource69=virtualTableSource();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, virtualTableSource69.getTree());

                    }
                    break;

            }


            // FromClauseParser.g:150:106: ( lateralView ^)*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==KW_LATERAL) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // FromClauseParser.g:150:107: lateralView ^
            	    {
            	    pushFollow(FOLLOW_lateralView_in_fromSource913);
            	    lateralView70=lateralView();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot(lateralView70.getTree(), root_0);

            	    }
            	    break;

            	default :
            	    break loop20;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) { gParent.popMsg(state); }
        }

        catch (RecognitionException e) {
          throw e;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "fromSource"


    public static class tableBucketSample_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "tableBucketSample"
    // FromClauseParser.g:153:1: tableBucketSample : KW_TABLESAMPLE LPAREN KW_BUCKET (numerator= Number ) KW_OUT KW_OF (denominator= Number ) ( KW_ON expr+= expression ( COMMA expr+= expression )* )? RPAREN -> ^( TOK_TABLEBUCKETSAMPLE $numerator $denominator ( $expr)* ) ;
    public final HiveParser_FromClauseParser.tableBucketSample_return tableBucketSample() throws RecognitionException {
        HiveParser_FromClauseParser.tableBucketSample_return retval = new HiveParser_FromClauseParser.tableBucketSample_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token numerator=null;
        Token denominator=null;
        Token KW_TABLESAMPLE71=null;
        Token LPAREN72=null;
        Token KW_BUCKET73=null;
        Token KW_OUT74=null;
        Token KW_OF75=null;
        Token KW_ON76=null;
        Token COMMA77=null;
        Token RPAREN78=null;
        List list_expr=null;
        RuleReturnScope expr = null;
        CommonTree numerator_tree=null;
        CommonTree denominator_tree=null;
        CommonTree KW_TABLESAMPLE71_tree=null;
        CommonTree LPAREN72_tree=null;
        CommonTree KW_BUCKET73_tree=null;
        CommonTree KW_OUT74_tree=null;
        CommonTree KW_OF75_tree=null;
        CommonTree KW_ON76_tree=null;
        CommonTree COMMA77_tree=null;
        CommonTree RPAREN78_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_KW_TABLESAMPLE=new RewriteRuleTokenStream(adaptor,"token KW_TABLESAMPLE");
        RewriteRuleTokenStream stream_KW_OF=new RewriteRuleTokenStream(adaptor,"token KW_OF");
        RewriteRuleTokenStream stream_Number=new RewriteRuleTokenStream(adaptor,"token Number");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_KW_OUT=new RewriteRuleTokenStream(adaptor,"token KW_OUT");
        RewriteRuleTokenStream stream_KW_ON=new RewriteRuleTokenStream(adaptor,"token KW_ON");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_KW_BUCKET=new RewriteRuleTokenStream(adaptor,"token KW_BUCKET");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
         gParent.pushMsg("table bucket sample specification", state); 
        try {
            // FromClauseParser.g:156:5: ( KW_TABLESAMPLE LPAREN KW_BUCKET (numerator= Number ) KW_OUT KW_OF (denominator= Number ) ( KW_ON expr+= expression ( COMMA expr+= expression )* )? RPAREN -> ^( TOK_TABLEBUCKETSAMPLE $numerator $denominator ( $expr)* ) )
            // FromClauseParser.g:157:5: KW_TABLESAMPLE LPAREN KW_BUCKET (numerator= Number ) KW_OUT KW_OF (denominator= Number ) ( KW_ON expr+= expression ( COMMA expr+= expression )* )? RPAREN
            {
            KW_TABLESAMPLE71=(Token)match(input,KW_TABLESAMPLE,FOLLOW_KW_TABLESAMPLE_in_tableBucketSample947); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_KW_TABLESAMPLE.add(KW_TABLESAMPLE71);


            LPAREN72=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_tableBucketSample949); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN72);


            KW_BUCKET73=(Token)match(input,KW_BUCKET,FOLLOW_KW_BUCKET_in_tableBucketSample951); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_KW_BUCKET.add(KW_BUCKET73);


            // FromClauseParser.g:157:37: (numerator= Number )
            // FromClauseParser.g:157:38: numerator= Number
            {
            numerator=(Token)match(input,Number,FOLLOW_Number_in_tableBucketSample956); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_Number.add(numerator);


            }


            KW_OUT74=(Token)match(input,KW_OUT,FOLLOW_KW_OUT_in_tableBucketSample959); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_KW_OUT.add(KW_OUT74);


            KW_OF75=(Token)match(input,KW_OF,FOLLOW_KW_OF_in_tableBucketSample961); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_KW_OF.add(KW_OF75);


            // FromClauseParser.g:157:69: (denominator= Number )
            // FromClauseParser.g:157:70: denominator= Number
            {
            denominator=(Token)match(input,Number,FOLLOW_Number_in_tableBucketSample966); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_Number.add(denominator);


            }


            // FromClauseParser.g:157:90: ( KW_ON expr+= expression ( COMMA expr+= expression )* )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==KW_ON) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // FromClauseParser.g:157:91: KW_ON expr+= expression ( COMMA expr+= expression )*
                    {
                    KW_ON76=(Token)match(input,KW_ON,FOLLOW_KW_ON_in_tableBucketSample970); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_ON.add(KW_ON76);


                    pushFollow(FOLLOW_expression_in_tableBucketSample974);
                    expr=gHiveParser.expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expr.getTree());
                    if (list_expr==null) list_expr=new ArrayList();
                    list_expr.add(expr.getTree());


                    // FromClauseParser.g:157:114: ( COMMA expr+= expression )*
                    loop21:
                    do {
                        int alt21=2;
                        int LA21_0 = input.LA(1);

                        if ( (LA21_0==COMMA) ) {
                            alt21=1;
                        }


                        switch (alt21) {
                    	case 1 :
                    	    // FromClauseParser.g:157:115: COMMA expr+= expression
                    	    {
                    	    COMMA77=(Token)match(input,COMMA,FOLLOW_COMMA_in_tableBucketSample977); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA77);


                    	    pushFollow(FOLLOW_expression_in_tableBucketSample981);
                    	    expr=gHiveParser.expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_expression.add(expr.getTree());
                    	    if (list_expr==null) list_expr=new ArrayList();
                    	    list_expr.add(expr.getTree());


                    	    }
                    	    break;

                    	default :
                    	    break loop21;
                        }
                    } while (true);


                    }
                    break;

            }


            RPAREN78=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_tableBucketSample987); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN78);


            // AST REWRITE
            // elements: numerator, expr, denominator
            // token labels: numerator, denominator
            // rule labels: retval
            // token list labels: 
            // rule list labels: expr
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleTokenStream stream_numerator=new RewriteRuleTokenStream(adaptor,"token numerator",numerator);
            RewriteRuleTokenStream stream_denominator=new RewriteRuleTokenStream(adaptor,"token denominator",denominator);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"token expr",list_expr);
            root_0 = (CommonTree)adaptor.nil();
            // 157:149: -> ^( TOK_TABLEBUCKETSAMPLE $numerator $denominator ( $expr)* )
            {
                // FromClauseParser.g:157:152: ^( TOK_TABLEBUCKETSAMPLE $numerator $denominator ( $expr)* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(TOK_TABLEBUCKETSAMPLE, "TOK_TABLEBUCKETSAMPLE")
                , root_1);

                adaptor.addChild(root_1, stream_numerator.nextNode());

                adaptor.addChild(root_1, stream_denominator.nextNode());

                // FromClauseParser.g:157:201: ( $expr)*
                while ( stream_expr.hasNext() ) {
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


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) { gParent.popMsg(state); }
        }

        catch (RecognitionException e) {
          throw e;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "tableBucketSample"


    public static class splitSample_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "splitSample"
    // FromClauseParser.g:160:1: splitSample : ( KW_TABLESAMPLE LPAREN (numerator= Number ) (percent= KW_PERCENT | KW_ROWS ) RPAREN -> {percent != null}? ^( TOK_TABLESPLITSAMPLE TOK_PERCENT $numerator) -> ^( TOK_TABLESPLITSAMPLE TOK_ROWCOUNT $numerator) | KW_TABLESAMPLE LPAREN (numerator= ByteLengthLiteral ) RPAREN -> ^( TOK_TABLESPLITSAMPLE TOK_LENGTH $numerator) );
    public final HiveParser_FromClauseParser.splitSample_return splitSample() throws RecognitionException {
        HiveParser_FromClauseParser.splitSample_return retval = new HiveParser_FromClauseParser.splitSample_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token numerator=null;
        Token percent=null;
        Token KW_TABLESAMPLE79=null;
        Token LPAREN80=null;
        Token KW_ROWS81=null;
        Token RPAREN82=null;
        Token KW_TABLESAMPLE83=null;
        Token LPAREN84=null;
        Token RPAREN85=null;

        CommonTree numerator_tree=null;
        CommonTree percent_tree=null;
        CommonTree KW_TABLESAMPLE79_tree=null;
        CommonTree LPAREN80_tree=null;
        CommonTree KW_ROWS81_tree=null;
        CommonTree RPAREN82_tree=null;
        CommonTree KW_TABLESAMPLE83_tree=null;
        CommonTree LPAREN84_tree=null;
        CommonTree RPAREN85_tree=null;
        RewriteRuleTokenStream stream_KW_TABLESAMPLE=new RewriteRuleTokenStream(adaptor,"token KW_TABLESAMPLE");
        RewriteRuleTokenStream stream_Number=new RewriteRuleTokenStream(adaptor,"token Number");
        RewriteRuleTokenStream stream_KW_ROWS=new RewriteRuleTokenStream(adaptor,"token KW_ROWS");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_KW_PERCENT=new RewriteRuleTokenStream(adaptor,"token KW_PERCENT");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_ByteLengthLiteral=new RewriteRuleTokenStream(adaptor,"token ByteLengthLiteral");

         gParent.pushMsg("table split sample specification", state); 
        try {
            // FromClauseParser.g:163:5: ( KW_TABLESAMPLE LPAREN (numerator= Number ) (percent= KW_PERCENT | KW_ROWS ) RPAREN -> {percent != null}? ^( TOK_TABLESPLITSAMPLE TOK_PERCENT $numerator) -> ^( TOK_TABLESPLITSAMPLE TOK_ROWCOUNT $numerator) | KW_TABLESAMPLE LPAREN (numerator= ByteLengthLiteral ) RPAREN -> ^( TOK_TABLESPLITSAMPLE TOK_LENGTH $numerator) )
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==KW_TABLESAMPLE) ) {
                int LA24_1 = input.LA(2);

                if ( (LA24_1==LPAREN) ) {
                    int LA24_2 = input.LA(3);

                    if ( (LA24_2==Number) ) {
                        alt24=1;
                    }
                    else if ( (LA24_2==ByteLengthLiteral) ) {
                        alt24=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 24, 2, input);

                        throw nvae;

                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 24, 1, input);

                    throw nvae;

                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 24, 0, input);

                throw nvae;

            }
            switch (alt24) {
                case 1 :
                    // FromClauseParser.g:164:5: KW_TABLESAMPLE LPAREN (numerator= Number ) (percent= KW_PERCENT | KW_ROWS ) RPAREN
                    {
                    KW_TABLESAMPLE79=(Token)match(input,KW_TABLESAMPLE,FOLLOW_KW_TABLESAMPLE_in_splitSample1034); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_TABLESAMPLE.add(KW_TABLESAMPLE79);


                    LPAREN80=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_splitSample1036); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN80);


                    // FromClauseParser.g:164:28: (numerator= Number )
                    // FromClauseParser.g:164:29: numerator= Number
                    {
                    numerator=(Token)match(input,Number,FOLLOW_Number_in_splitSample1042); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_Number.add(numerator);


                    }


                    // FromClauseParser.g:164:47: (percent= KW_PERCENT | KW_ROWS )
                    int alt23=2;
                    int LA23_0 = input.LA(1);

                    if ( (LA23_0==KW_PERCENT) ) {
                        alt23=1;
                    }
                    else if ( (LA23_0==KW_ROWS) ) {
                        alt23=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 23, 0, input);

                        throw nvae;

                    }
                    switch (alt23) {
                        case 1 :
                            // FromClauseParser.g:164:48: percent= KW_PERCENT
                            {
                            percent=(Token)match(input,KW_PERCENT,FOLLOW_KW_PERCENT_in_splitSample1048); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_KW_PERCENT.add(percent);


                            }
                            break;
                        case 2 :
                            // FromClauseParser.g:164:67: KW_ROWS
                            {
                            KW_ROWS81=(Token)match(input,KW_ROWS,FOLLOW_KW_ROWS_in_splitSample1050); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_KW_ROWS.add(KW_ROWS81);


                            }
                            break;

                    }


                    RPAREN82=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_splitSample1053); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN82);


                    // AST REWRITE
                    // elements: numerator, numerator
                    // token labels: numerator
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_numerator=new RewriteRuleTokenStream(adaptor,"token numerator",numerator);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 165:5: -> {percent != null}? ^( TOK_TABLESPLITSAMPLE TOK_PERCENT $numerator)
                    if (percent != null) {
                        // FromClauseParser.g:165:27: ^( TOK_TABLESPLITSAMPLE TOK_PERCENT $numerator)
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(TOK_TABLESPLITSAMPLE, "TOK_TABLESPLITSAMPLE")
                        , root_1);

                        adaptor.addChild(root_1, 
                        (CommonTree)adaptor.create(TOK_PERCENT, "TOK_PERCENT")
                        );

                        adaptor.addChild(root_1, stream_numerator.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    else // 166:5: -> ^( TOK_TABLESPLITSAMPLE TOK_ROWCOUNT $numerator)
                    {
                        // FromClauseParser.g:166:8: ^( TOK_TABLESPLITSAMPLE TOK_ROWCOUNT $numerator)
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(TOK_TABLESPLITSAMPLE, "TOK_TABLESPLITSAMPLE")
                        , root_1);

                        adaptor.addChild(root_1, 
                        (CommonTree)adaptor.create(TOK_ROWCOUNT, "TOK_ROWCOUNT")
                        );

                        adaptor.addChild(root_1, stream_numerator.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 2 :
                    // FromClauseParser.g:168:5: KW_TABLESAMPLE LPAREN (numerator= ByteLengthLiteral ) RPAREN
                    {
                    KW_TABLESAMPLE83=(Token)match(input,KW_TABLESAMPLE,FOLLOW_KW_TABLESAMPLE_in_splitSample1097); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_TABLESAMPLE.add(KW_TABLESAMPLE83);


                    LPAREN84=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_splitSample1099); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN84);


                    // FromClauseParser.g:168:28: (numerator= ByteLengthLiteral )
                    // FromClauseParser.g:168:29: numerator= ByteLengthLiteral
                    {
                    numerator=(Token)match(input,ByteLengthLiteral,FOLLOW_ByteLengthLiteral_in_splitSample1105); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ByteLengthLiteral.add(numerator);


                    }


                    RPAREN85=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_splitSample1108); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN85);


                    // AST REWRITE
                    // elements: numerator
                    // token labels: numerator
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_numerator=new RewriteRuleTokenStream(adaptor,"token numerator",numerator);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 169:5: -> ^( TOK_TABLESPLITSAMPLE TOK_LENGTH $numerator)
                    {
                        // FromClauseParser.g:169:8: ^( TOK_TABLESPLITSAMPLE TOK_LENGTH $numerator)
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(TOK_TABLESPLITSAMPLE, "TOK_TABLESPLITSAMPLE")
                        , root_1);

                        adaptor.addChild(root_1, 
                        (CommonTree)adaptor.create(TOK_LENGTH, "TOK_LENGTH")
                        );

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


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) { gParent.popMsg(state); }
        }

        catch (RecognitionException e) {
          throw e;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "splitSample"


    public static class tableSample_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "tableSample"
    // FromClauseParser.g:172:1: tableSample : ( tableBucketSample | splitSample );
    public final HiveParser_FromClauseParser.tableSample_return tableSample() throws RecognitionException {
        HiveParser_FromClauseParser.tableSample_return retval = new HiveParser_FromClauseParser.tableSample_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        HiveParser_FromClauseParser.tableBucketSample_return tableBucketSample86 =null;

        HiveParser_FromClauseParser.splitSample_return splitSample87 =null;



         gParent.pushMsg("table sample specification", state); 
        try {
            // FromClauseParser.g:175:5: ( tableBucketSample | splitSample )
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==KW_TABLESAMPLE) ) {
                int LA25_1 = input.LA(2);

                if ( (LA25_1==LPAREN) ) {
                    int LA25_2 = input.LA(3);

                    if ( (LA25_2==KW_BUCKET) ) {
                        alt25=1;
                    }
                    else if ( (LA25_2==ByteLengthLiteral||LA25_2==Number) ) {
                        alt25=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 25, 2, input);

                        throw nvae;

                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 25, 1, input);

                    throw nvae;

                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 25, 0, input);

                throw nvae;

            }
            switch (alt25) {
                case 1 :
                    // FromClauseParser.g:176:5: tableBucketSample
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_tableBucketSample_in_tableSample1154);
                    tableBucketSample86=tableBucketSample();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, tableBucketSample86.getTree());

                    }
                    break;
                case 2 :
                    // FromClauseParser.g:177:5: splitSample
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_splitSample_in_tableSample1162);
                    splitSample87=splitSample();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, splitSample87.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) { gParent.popMsg(state); }
        }

        catch (RecognitionException e) {
          throw e;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "tableSample"


    public static class tableSource_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "tableSource"
    // FromClauseParser.g:180:1: tableSource : tabname= tableName ( ( tableProperties )=>props= tableProperties )? ( ( tableSample )=>ts= tableSample )? ( ( KW_AS )=> ( KW_AS alias= Identifier ) | ( Identifier )=> (alias= Identifier ) )? -> ^( TOK_TABREF $tabname ( $props)? ( $ts)? ( $alias)? ) ;
    public final HiveParser_FromClauseParser.tableSource_return tableSource() throws RecognitionException {
        HiveParser_FromClauseParser.tableSource_return retval = new HiveParser_FromClauseParser.tableSource_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token alias=null;
        Token KW_AS88=null;
        HiveParser_FromClauseParser.tableName_return tabname =null;

        HiveParser.tableProperties_return props =null;

        HiveParser_FromClauseParser.tableSample_return ts =null;


        CommonTree alias_tree=null;
        CommonTree KW_AS88_tree=null;
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleTokenStream stream_KW_AS=new RewriteRuleTokenStream(adaptor,"token KW_AS");
        RewriteRuleSubtreeStream stream_tableSample=new RewriteRuleSubtreeStream(adaptor,"rule tableSample");
        RewriteRuleSubtreeStream stream_tableProperties=new RewriteRuleSubtreeStream(adaptor,"rule tableProperties");
        RewriteRuleSubtreeStream stream_tableName=new RewriteRuleSubtreeStream(adaptor,"rule tableName");
         gParent.pushMsg("table source", state); 
        try {
            // FromClauseParser.g:183:5: (tabname= tableName ( ( tableProperties )=>props= tableProperties )? ( ( tableSample )=>ts= tableSample )? ( ( KW_AS )=> ( KW_AS alias= Identifier ) | ( Identifier )=> (alias= Identifier ) )? -> ^( TOK_TABREF $tabname ( $props)? ( $ts)? ( $alias)? ) )
            // FromClauseParser.g:183:7: tabname= tableName ( ( tableProperties )=>props= tableProperties )? ( ( tableSample )=>ts= tableSample )? ( ( KW_AS )=> ( KW_AS alias= Identifier ) | ( Identifier )=> (alias= Identifier ) )?
            {
            pushFollow(FOLLOW_tableName_in_tableSource1191);
            tabname=tableName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_tableName.add(tabname.getTree());

            // FromClauseParser.g:184:5: ( ( tableProperties )=>props= tableProperties )?
            int alt26=2;
            alt26 = dfa26.predict(input);
            switch (alt26) {
                case 1 :
                    // FromClauseParser.g:184:6: ( tableProperties )=>props= tableProperties
                    {
                    pushFollow(FOLLOW_tableProperties_in_tableSource1206);
                    props=gHiveParser.tableProperties();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_tableProperties.add(props.getTree());

                    }
                    break;

            }


            // FromClauseParser.g:185:5: ( ( tableSample )=>ts= tableSample )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==KW_TABLESAMPLE) && (synpred6_FromClauseParser())) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // FromClauseParser.g:185:6: ( tableSample )=>ts= tableSample
                    {
                    pushFollow(FOLLOW_tableSample_in_tableSource1223);
                    ts=tableSample();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_tableSample.add(ts.getTree());

                    }
                    break;

            }


            // FromClauseParser.g:186:5: ( ( KW_AS )=> ( KW_AS alias= Identifier ) | ( Identifier )=> (alias= Identifier ) )?
            int alt28=3;
            alt28 = dfa28.predict(input);
            switch (alt28) {
                case 1 :
                    // FromClauseParser.g:186:6: ( KW_AS )=> ( KW_AS alias= Identifier )
                    {
                    // FromClauseParser.g:186:17: ( KW_AS alias= Identifier )
                    // FromClauseParser.g:186:18: KW_AS alias= Identifier
                    {
                    KW_AS88=(Token)match(input,KW_AS,FOLLOW_KW_AS_in_tableSource1239); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_AS.add(KW_AS88);


                    alias=(Token)match(input,Identifier,FOLLOW_Identifier_in_tableSource1243); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_Identifier.add(alias);


                    }


                    }
                    break;
                case 2 :
                    // FromClauseParser.g:188:5: ( Identifier )=> (alias= Identifier )
                    {
                    // FromClauseParser.g:188:21: (alias= Identifier )
                    // FromClauseParser.g:188:22: alias= Identifier
                    {
                    alias=(Token)match(input,Identifier,FOLLOW_Identifier_in_tableSource1265); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_Identifier.add(alias);


                    }


                    }
                    break;

            }


            // AST REWRITE
            // elements: tabname, ts, props, alias
            // token labels: alias
            // rule labels: tabname, retval, ts, props
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleTokenStream stream_alias=new RewriteRuleTokenStream(adaptor,"token alias",alias);
            RewriteRuleSubtreeStream stream_tabname=new RewriteRuleSubtreeStream(adaptor,"rule tabname",tabname!=null?tabname.tree:null);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_ts=new RewriteRuleSubtreeStream(adaptor,"rule ts",ts!=null?ts.tree:null);
            RewriteRuleSubtreeStream stream_props=new RewriteRuleSubtreeStream(adaptor,"rule props",props!=null?props.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 189:5: -> ^( TOK_TABREF $tabname ( $props)? ( $ts)? ( $alias)? )
            {
                // FromClauseParser.g:189:8: ^( TOK_TABREF $tabname ( $props)? ( $ts)? ( $alias)? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(TOK_TABREF, "TOK_TABREF")
                , root_1);

                adaptor.addChild(root_1, stream_tabname.nextTree());

                // FromClauseParser.g:189:31: ( $props)?
                if ( stream_props.hasNext() ) {
                    adaptor.addChild(root_1, stream_props.nextTree());

                }
                stream_props.reset();

                // FromClauseParser.g:189:39: ( $ts)?
                if ( stream_ts.hasNext() ) {
                    adaptor.addChild(root_1, stream_ts.nextTree());

                }
                stream_ts.reset();

                // FromClauseParser.g:189:44: ( $alias)?
                if ( stream_alias.hasNext() ) {
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


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) { gParent.popMsg(state); }
        }

        catch (RecognitionException e) {
          throw e;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "tableSource"


    public static class tableName_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "tableName"
    // FromClauseParser.g:192:1: tableName : (db= identifier DOT tab= identifier -> ^( TOK_TABNAME $db $tab) |tab= identifier -> ^( TOK_TABNAME $tab) );
    public final HiveParser_FromClauseParser.tableName_return tableName() throws RecognitionException {
        HiveParser_FromClauseParser.tableName_return retval = new HiveParser_FromClauseParser.tableName_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token DOT89=null;
        HiveParser_IdentifiersParser.identifier_return db =null;

        HiveParser_IdentifiersParser.identifier_return tab =null;


        CommonTree DOT89_tree=null;
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_identifier=new RewriteRuleSubtreeStream(adaptor,"rule identifier");
         gParent.pushMsg("table name", state); 
        try {
            // FromClauseParser.g:195:5: (db= identifier DOT tab= identifier -> ^( TOK_TABNAME $db $tab) |tab= identifier -> ^( TOK_TABNAME $tab) )
            int alt29=2;
            alt29 = dfa29.predict(input);
            switch (alt29) {
                case 1 :
                    // FromClauseParser.g:196:5: db= identifier DOT tab= identifier
                    {
                    pushFollow(FOLLOW_identifier_in_tableName1326);
                    db=gHiveParser.identifier();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_identifier.add(db.getTree());

                    DOT89=(Token)match(input,DOT,FOLLOW_DOT_in_tableName1328); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DOT.add(DOT89);


                    pushFollow(FOLLOW_identifier_in_tableName1332);
                    tab=gHiveParser.identifier();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_identifier.add(tab.getTree());

                    // AST REWRITE
                    // elements: db, tab
                    // token labels: 
                    // rule labels: tab, db, retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_tab=new RewriteRuleSubtreeStream(adaptor,"rule tab",tab!=null?tab.tree:null);
                    RewriteRuleSubtreeStream stream_db=new RewriteRuleSubtreeStream(adaptor,"rule db",db!=null?db.tree:null);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 197:5: -> ^( TOK_TABNAME $db $tab)
                    {
                        // FromClauseParser.g:197:8: ^( TOK_TABNAME $db $tab)
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(TOK_TABNAME, "TOK_TABNAME")
                        , root_1);

                        adaptor.addChild(root_1, stream_db.nextTree());

                        adaptor.addChild(root_1, stream_tab.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 2 :
                    // FromClauseParser.g:199:5: tab= identifier
                    {
                    pushFollow(FOLLOW_identifier_in_tableName1362);
                    tab=gHiveParser.identifier();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_identifier.add(tab.getTree());

                    // AST REWRITE
                    // elements: tab
                    // token labels: 
                    // rule labels: tab, retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_tab=new RewriteRuleSubtreeStream(adaptor,"rule tab",tab!=null?tab.tree:null);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 200:5: -> ^( TOK_TABNAME $tab)
                    {
                        // FromClauseParser.g:200:8: ^( TOK_TABNAME $tab)
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(TOK_TABNAME, "TOK_TABNAME")
                        , root_1);

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


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) { gParent.popMsg(state); }
        }

        catch (RecognitionException e) {
          throw e;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "tableName"


    public static class viewName_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "viewName"
    // FromClauseParser.g:203:1: viewName : (db= identifier DOT )? view= identifier -> ^( TOK_TABNAME ( $db)? $view) ;
    public final HiveParser_FromClauseParser.viewName_return viewName() throws RecognitionException {
        HiveParser_FromClauseParser.viewName_return retval = new HiveParser_FromClauseParser.viewName_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token DOT90=null;
        HiveParser_IdentifiersParser.identifier_return db =null;

        HiveParser_IdentifiersParser.identifier_return view =null;


        CommonTree DOT90_tree=null;
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_identifier=new RewriteRuleSubtreeStream(adaptor,"rule identifier");
         gParent.pushMsg("view name", state); 
        try {
            // FromClauseParser.g:206:5: ( (db= identifier DOT )? view= identifier -> ^( TOK_TABNAME ( $db)? $view) )
            // FromClauseParser.g:207:5: (db= identifier DOT )? view= identifier
            {
            // FromClauseParser.g:207:5: (db= identifier DOT )?
            int alt30=2;
            switch ( input.LA(1) ) {
                case Identifier:
                    {
                    int LA30_1 = input.LA(2);

                    if ( (LA30_1==DOT) ) {
                        alt30=1;
                    }
                    }
                    break;
                case KW_ADD:
                case KW_ADMIN:
                case KW_AFTER:
                case KW_ANALYZE:
                case KW_ARCHIVE:
                case KW_ASC:
                case KW_BEFORE:
                case KW_BUCKET:
                case KW_BUCKETS:
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
                case KW_DATA:
                case KW_DATABASES:
                case KW_DATETIME:
                case KW_DAY:
                case KW_DBPROPERTIES:
                case KW_DEFERRED:
                case KW_DEFINED:
                case KW_DELIMITED:
                case KW_DEPENDENCY:
                case KW_DESC:
                case KW_DIRECTORIES:
                case KW_DIRECTORY:
                case KW_DISABLE:
                case KW_DISTRIBUTE:
                case KW_ELEM_TYPE:
                case KW_ENABLE:
                case KW_ESCAPED:
                case KW_EXCLUSIVE:
                case KW_EXPLAIN:
                case KW_EXPORT:
                case KW_FIELDS:
                case KW_FILE:
                case KW_FILEFORMAT:
                case KW_FIRST:
                case KW_FORMAT:
                case KW_FORMATTED:
                case KW_FUNCTIONS:
                case KW_HOLD_DDLTIME:
                case KW_HOUR:
                case KW_IDXPROPERTIES:
                case KW_IGNORE:
                case KW_INDEX:
                case KW_INDEXES:
                case KW_INPATH:
                case KW_INPUTDRIVER:
                case KW_INPUTFORMAT:
                case KW_ITEMS:
                case KW_JAR:
                case KW_KEYS:
                case KW_KEY_TYPE:
                case KW_LIMIT:
                case KW_LINES:
                case KW_LOAD:
                case KW_LOCATION:
                case KW_LOCK:
                case KW_LOCKS:
                case KW_LOGICAL:
                case KW_LONG:
                case KW_MAPJOIN:
                case KW_MATERIALIZED:
                case KW_METADATA:
                case KW_MINUS:
                case KW_MINUTE:
                case KW_MONTH:
                case KW_MSCK:
                case KW_NOSCAN:
                case KW_NO_DROP:
                case KW_OFFLINE:
                case KW_OPTION:
                case KW_OUTPUTDRIVER:
                case KW_OUTPUTFORMAT:
                case KW_OVERWRITE:
                case KW_OWNER:
                case KW_PARTITIONED:
                case KW_PARTITIONS:
                case KW_PLUS:
                case KW_PRETTY:
                case KW_PRINCIPALS:
                case KW_PROTECTION:
                case KW_PURGE:
                case KW_READ:
                case KW_READONLY:
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
                case KW_REWRITE:
                case KW_RLIKE:
                case KW_ROLE:
                case KW_ROLES:
                case KW_SCHEMA:
                case KW_SCHEMAS:
                case KW_SECOND:
                case KW_SEMI:
                case KW_SERDE:
                case KW_SERDEPROPERTIES:
                case KW_SERVER:
                case KW_SETS:
                case KW_SHARED:
                case KW_SHOW:
                case KW_SHOW_DATABASE:
                case KW_SKEWED:
                case KW_SORT:
                case KW_SORTED:
                case KW_SSL:
                case KW_STATISTICS:
                case KW_STORED:
                case KW_STREAMTABLE:
                case KW_STRING:
                case KW_STRUCT:
                case KW_TABLES:
                case KW_TBLPROPERTIES:
                case KW_TEMPORARY:
                case KW_TERMINATED:
                case KW_TINYINT:
                case KW_TOUCH:
                case KW_TRANSACTIONS:
                case KW_UNARCHIVE:
                case KW_UNDO:
                case KW_UNIONTYPE:
                case KW_UNLOCK:
                case KW_UNSET:
                case KW_UNSIGNED:
                case KW_URI:
                case KW_USE:
                case KW_UTC:
                case KW_UTCTIMESTAMP:
                case KW_VALUE_TYPE:
                case KW_VIEW:
                case KW_WHILE:
                case KW_YEAR:
                    {
                    int LA30_2 = input.LA(2);

                    if ( (LA30_2==DOT) ) {
                        alt30=1;
                    }
                    }
                    break;
                case KW_ALL:
                case KW_ALTER:
                case KW_ARRAY:
                case KW_AS:
                case KW_AUTHORIZATION:
                case KW_BETWEEN:
                case KW_BIGINT:
                case KW_BINARY:
                case KW_BOOLEAN:
                case KW_BOTH:
                case KW_BY:
                case KW_CREATE:
                case KW_CUBE:
                case KW_CURRENT_DATE:
                case KW_CURRENT_TIMESTAMP:
                case KW_CURSOR:
                case KW_DATE:
                case KW_DECIMAL:
                case KW_DELETE:
                case KW_DESCRIBE:
                case KW_DOUBLE:
                case KW_DROP:
                case KW_EXISTS:
                case KW_EXTERNAL:
                case KW_FALSE:
                case KW_FETCH:
                case KW_FLOAT:
                case KW_FOR:
                case KW_FULL:
                case KW_GRANT:
                case KW_GROUP:
                case KW_GROUPING:
                case KW_IMPORT:
                case KW_IN:
                case KW_INNER:
                case KW_INSERT:
                case KW_INT:
                case KW_INTERSECT:
                case KW_INTO:
                case KW_IS:
                case KW_LATERAL:
                case KW_LEFT:
                case KW_LIKE:
                case KW_LOCAL:
                case KW_NONE:
                case KW_NULL:
                case KW_OF:
                case KW_ORDER:
                case KW_OUT:
                case KW_OUTER:
                case KW_PARTITION:
                case KW_PERCENT:
                case KW_PROCEDURE:
                case KW_RANGE:
                case KW_READS:
                case KW_REVOKE:
                case KW_RIGHT:
                case KW_ROLLUP:
                case KW_ROW:
                case KW_ROWS:
                case KW_SET:
                case KW_SMALLINT:
                case KW_TABLE:
                case KW_TIMESTAMP:
                case KW_TO:
                case KW_TRIGGER:
                case KW_TRUE:
                case KW_TRUNCATE:
                case KW_UNION:
                case KW_UPDATE:
                case KW_USER:
                case KW_USING:
                case KW_VALUES:
                case KW_WITH:
                    {
                    int LA30_3 = input.LA(2);

                    if ( (LA30_3==DOT) ) {
                        alt30=1;
                    }
                    }
                    break;
            }

            switch (alt30) {
                case 1 :
                    // FromClauseParser.g:207:6: db= identifier DOT
                    {
                    pushFollow(FOLLOW_identifier_in_viewName1409);
                    db=gHiveParser.identifier();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_identifier.add(db.getTree());

                    DOT90=(Token)match(input,DOT,FOLLOW_DOT_in_viewName1411); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DOT.add(DOT90);


                    }
                    break;

            }


            pushFollow(FOLLOW_identifier_in_viewName1417);
            view=gHiveParser.identifier();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_identifier.add(view.getTree());

            // AST REWRITE
            // elements: view, db
            // token labels: 
            // rule labels: view, db, retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_view=new RewriteRuleSubtreeStream(adaptor,"rule view",view!=null?view.tree:null);
            RewriteRuleSubtreeStream stream_db=new RewriteRuleSubtreeStream(adaptor,"rule db",db!=null?db.tree:null);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 208:5: -> ^( TOK_TABNAME ( $db)? $view)
            {
                // FromClauseParser.g:208:8: ^( TOK_TABNAME ( $db)? $view)
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(TOK_TABNAME, "TOK_TABNAME")
                , root_1);

                // FromClauseParser.g:208:23: ( $db)?
                if ( stream_db.hasNext() ) {
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


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) { gParent.popMsg(state); }
        }

        catch (RecognitionException e) {
          throw e;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "viewName"


    public static class subQuerySource_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "subQuerySource"
    // FromClauseParser.g:211:1: subQuerySource : LPAREN queryStatementExpression[false] RPAREN ( KW_AS )? identifier -> ^( TOK_SUBQUERY queryStatementExpression identifier ) ;
    public final HiveParser_FromClauseParser.subQuerySource_return subQuerySource() throws RecognitionException {
        HiveParser_FromClauseParser.subQuerySource_return retval = new HiveParser_FromClauseParser.subQuerySource_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token LPAREN91=null;
        Token RPAREN93=null;
        Token KW_AS94=null;
        HiveParser.queryStatementExpression_return queryStatementExpression92 =null;

        HiveParser_IdentifiersParser.identifier_return identifier95 =null;


        CommonTree LPAREN91_tree=null;
        CommonTree RPAREN93_tree=null;
        CommonTree KW_AS94_tree=null;
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_KW_AS=new RewriteRuleTokenStream(adaptor,"token KW_AS");
        RewriteRuleSubtreeStream stream_identifier=new RewriteRuleSubtreeStream(adaptor,"rule identifier");
        RewriteRuleSubtreeStream stream_queryStatementExpression=new RewriteRuleSubtreeStream(adaptor,"rule queryStatementExpression");
         gParent.pushMsg("subquery source", state); 
        try {
            // FromClauseParser.g:214:5: ( LPAREN queryStatementExpression[false] RPAREN ( KW_AS )? identifier -> ^( TOK_SUBQUERY queryStatementExpression identifier ) )
            // FromClauseParser.g:215:5: LPAREN queryStatementExpression[false] RPAREN ( KW_AS )? identifier
            {
            LPAREN91=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_subQuerySource1465); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN91);


            pushFollow(FOLLOW_queryStatementExpression_in_subQuerySource1467);
            queryStatementExpression92=gHiveParser.queryStatementExpression(false);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_queryStatementExpression.add(queryStatementExpression92.getTree());

            RPAREN93=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_subQuerySource1470); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN93);


            // FromClauseParser.g:215:51: ( KW_AS )?
            int alt31=2;
            alt31 = dfa31.predict(input);
            switch (alt31) {
                case 1 :
                    // FromClauseParser.g:215:51: KW_AS
                    {
                    KW_AS94=(Token)match(input,KW_AS,FOLLOW_KW_AS_in_subQuerySource1472); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_AS.add(KW_AS94);


                    }
                    break;

            }


            pushFollow(FOLLOW_identifier_in_subQuerySource1475);
            identifier95=gHiveParser.identifier();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_identifier.add(identifier95.getTree());

            // AST REWRITE
            // elements: identifier, queryStatementExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 215:69: -> ^( TOK_SUBQUERY queryStatementExpression identifier )
            {
                // FromClauseParser.g:215:72: ^( TOK_SUBQUERY queryStatementExpression identifier )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(TOK_SUBQUERY, "TOK_SUBQUERY")
                , root_1);

                adaptor.addChild(root_1, stream_queryStatementExpression.nextTree());

                adaptor.addChild(root_1, stream_identifier.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) { gParent.popMsg(state); }
        }

        catch (RecognitionException e) {
          throw e;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "subQuerySource"


    public static class partitioningSpec_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "partitioningSpec"
    // FromClauseParser.g:219:1: partitioningSpec : ( partitionByClause ( orderByClause )? -> ^( TOK_PARTITIONINGSPEC partitionByClause ( orderByClause )? ) | orderByClause -> ^( TOK_PARTITIONINGSPEC orderByClause ) | distributeByClause ( sortByClause )? -> ^( TOK_PARTITIONINGSPEC distributeByClause ( sortByClause )? ) | sortByClause -> ^( TOK_PARTITIONINGSPEC sortByClause ) | clusterByClause -> ^( TOK_PARTITIONINGSPEC clusterByClause ) );
    public final HiveParser_FromClauseParser.partitioningSpec_return partitioningSpec() throws RecognitionException {
        HiveParser_FromClauseParser.partitioningSpec_return retval = new HiveParser_FromClauseParser.partitioningSpec_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        HiveParser_IdentifiersParser.partitionByClause_return partitionByClause96 =null;

        HiveParser_IdentifiersParser.orderByClause_return orderByClause97 =null;

        HiveParser_IdentifiersParser.orderByClause_return orderByClause98 =null;

        HiveParser_IdentifiersParser.distributeByClause_return distributeByClause99 =null;

        HiveParser_IdentifiersParser.sortByClause_return sortByClause100 =null;

        HiveParser_IdentifiersParser.sortByClause_return sortByClause101 =null;

        HiveParser_IdentifiersParser.clusterByClause_return clusterByClause102 =null;


        RewriteRuleSubtreeStream stream_clusterByClause=new RewriteRuleSubtreeStream(adaptor,"rule clusterByClause");
        RewriteRuleSubtreeStream stream_sortByClause=new RewriteRuleSubtreeStream(adaptor,"rule sortByClause");
        RewriteRuleSubtreeStream stream_partitionByClause=new RewriteRuleSubtreeStream(adaptor,"rule partitionByClause");
        RewriteRuleSubtreeStream stream_distributeByClause=new RewriteRuleSubtreeStream(adaptor,"rule distributeByClause");
        RewriteRuleSubtreeStream stream_orderByClause=new RewriteRuleSubtreeStream(adaptor,"rule orderByClause");
         gParent.pushMsg("partitioningSpec clause", state); 
        try {
            // FromClauseParser.g:222:4: ( partitionByClause ( orderByClause )? -> ^( TOK_PARTITIONINGSPEC partitionByClause ( orderByClause )? ) | orderByClause -> ^( TOK_PARTITIONINGSPEC orderByClause ) | distributeByClause ( sortByClause )? -> ^( TOK_PARTITIONINGSPEC distributeByClause ( sortByClause )? ) | sortByClause -> ^( TOK_PARTITIONINGSPEC sortByClause ) | clusterByClause -> ^( TOK_PARTITIONINGSPEC clusterByClause ) )
            int alt34=5;
            switch ( input.LA(1) ) {
            case KW_PARTITION:
                {
                alt34=1;
                }
                break;
            case KW_ORDER:
                {
                alt34=2;
                }
                break;
            case KW_DISTRIBUTE:
                {
                alt34=3;
                }
                break;
            case KW_SORT:
                {
                alt34=4;
                }
                break;
            case KW_CLUSTER:
                {
                alt34=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 34, 0, input);

                throw nvae;

            }

            switch (alt34) {
                case 1 :
                    // FromClauseParser.g:223:4: partitionByClause ( orderByClause )?
                    {
                    pushFollow(FOLLOW_partitionByClause_in_partitioningSpec1515);
                    partitionByClause96=gHiveParser.partitionByClause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_partitionByClause.add(partitionByClause96.getTree());

                    // FromClauseParser.g:223:22: ( orderByClause )?
                    int alt32=2;
                    int LA32_0 = input.LA(1);

                    if ( (LA32_0==KW_ORDER) ) {
                        alt32=1;
                    }
                    switch (alt32) {
                        case 1 :
                            // FromClauseParser.g:223:22: orderByClause
                            {
                            pushFollow(FOLLOW_orderByClause_in_partitioningSpec1517);
                            orderByClause97=gHiveParser.orderByClause();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_orderByClause.add(orderByClause97.getTree());

                            }
                            break;

                    }


                    // AST REWRITE
                    // elements: orderByClause, partitionByClause
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 223:37: -> ^( TOK_PARTITIONINGSPEC partitionByClause ( orderByClause )? )
                    {
                        // FromClauseParser.g:223:40: ^( TOK_PARTITIONINGSPEC partitionByClause ( orderByClause )? )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(TOK_PARTITIONINGSPEC, "TOK_PARTITIONINGSPEC")
                        , root_1);

                        adaptor.addChild(root_1, stream_partitionByClause.nextTree());

                        // FromClauseParser.g:223:81: ( orderByClause )?
                        if ( stream_orderByClause.hasNext() ) {
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
                case 2 :
                    // FromClauseParser.g:224:4: orderByClause
                    {
                    pushFollow(FOLLOW_orderByClause_in_partitioningSpec1536);
                    orderByClause98=gHiveParser.orderByClause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_orderByClause.add(orderByClause98.getTree());

                    // AST REWRITE
                    // elements: orderByClause
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 224:18: -> ^( TOK_PARTITIONINGSPEC orderByClause )
                    {
                        // FromClauseParser.g:224:21: ^( TOK_PARTITIONINGSPEC orderByClause )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(TOK_PARTITIONINGSPEC, "TOK_PARTITIONINGSPEC")
                        , root_1);

                        adaptor.addChild(root_1, stream_orderByClause.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 3 :
                    // FromClauseParser.g:225:4: distributeByClause ( sortByClause )?
                    {
                    pushFollow(FOLLOW_distributeByClause_in_partitioningSpec1551);
                    distributeByClause99=gHiveParser.distributeByClause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_distributeByClause.add(distributeByClause99.getTree());

                    // FromClauseParser.g:225:23: ( sortByClause )?
                    int alt33=2;
                    int LA33_0 = input.LA(1);

                    if ( (LA33_0==KW_SORT) ) {
                        alt33=1;
                    }
                    switch (alt33) {
                        case 1 :
                            // FromClauseParser.g:225:23: sortByClause
                            {
                            pushFollow(FOLLOW_sortByClause_in_partitioningSpec1553);
                            sortByClause100=gHiveParser.sortByClause();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_sortByClause.add(sortByClause100.getTree());

                            }
                            break;

                    }


                    // AST REWRITE
                    // elements: sortByClause, distributeByClause
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 225:37: -> ^( TOK_PARTITIONINGSPEC distributeByClause ( sortByClause )? )
                    {
                        // FromClauseParser.g:225:40: ^( TOK_PARTITIONINGSPEC distributeByClause ( sortByClause )? )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(TOK_PARTITIONINGSPEC, "TOK_PARTITIONINGSPEC")
                        , root_1);

                        adaptor.addChild(root_1, stream_distributeByClause.nextTree());

                        // FromClauseParser.g:225:82: ( sortByClause )?
                        if ( stream_sortByClause.hasNext() ) {
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
                case 4 :
                    // FromClauseParser.g:226:4: sortByClause
                    {
                    pushFollow(FOLLOW_sortByClause_in_partitioningSpec1572);
                    sortByClause101=gHiveParser.sortByClause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_sortByClause.add(sortByClause101.getTree());

                    // AST REWRITE
                    // elements: sortByClause
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 226:17: -> ^( TOK_PARTITIONINGSPEC sortByClause )
                    {
                        // FromClauseParser.g:226:20: ^( TOK_PARTITIONINGSPEC sortByClause )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(TOK_PARTITIONINGSPEC, "TOK_PARTITIONINGSPEC")
                        , root_1);

                        adaptor.addChild(root_1, stream_sortByClause.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 5 :
                    // FromClauseParser.g:227:4: clusterByClause
                    {
                    pushFollow(FOLLOW_clusterByClause_in_partitioningSpec1587);
                    clusterByClause102=gHiveParser.clusterByClause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_clusterByClause.add(clusterByClause102.getTree());

                    // AST REWRITE
                    // elements: clusterByClause
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 227:20: -> ^( TOK_PARTITIONINGSPEC clusterByClause )
                    {
                        // FromClauseParser.g:227:23: ^( TOK_PARTITIONINGSPEC clusterByClause )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(TOK_PARTITIONINGSPEC, "TOK_PARTITIONINGSPEC")
                        , root_1);

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


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) { gParent.popMsg(state); }
        }

        catch (RecognitionException e) {
          throw e;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "partitioningSpec"


    public static class partitionTableFunctionSource_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "partitionTableFunctionSource"
    // FromClauseParser.g:230:1: partitionTableFunctionSource : ( subQuerySource | tableSource | partitionedTableFunction );
    public final HiveParser_FromClauseParser.partitionTableFunctionSource_return partitionTableFunctionSource() throws RecognitionException {
        HiveParser_FromClauseParser.partitionTableFunctionSource_return retval = new HiveParser_FromClauseParser.partitionTableFunctionSource_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        HiveParser_FromClauseParser.subQuerySource_return subQuerySource103 =null;

        HiveParser_FromClauseParser.tableSource_return tableSource104 =null;

        HiveParser_FromClauseParser.partitionedTableFunction_return partitionedTableFunction105 =null;



         gParent.pushMsg("partitionTableFunctionSource clause", state); 
        try {
            // FromClauseParser.g:233:4: ( subQuerySource | tableSource | partitionedTableFunction )
            int alt35=3;
            switch ( input.LA(1) ) {
            case LPAREN:
                {
                alt35=1;
                }
                break;
            case Identifier:
                {
                int LA35_2 = input.LA(2);

                if ( (LA35_2==LPAREN) ) {
                    int LA35_5 = input.LA(3);

                    if ( (LA35_5==KW_ON) ) {
                        alt35=3;
                    }
                    else if ( (LA35_5==StringLiteral) ) {
                        alt35=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 35, 5, input);

                        throw nvae;

                    }
                }
                else if ( (LA35_2==EOF||LA35_2==DOT||LA35_2==Identifier||LA35_2==KW_AS||LA35_2==KW_CLUSTER||LA35_2==KW_DISTRIBUTE||LA35_2==KW_ORDER||LA35_2==KW_PARTITION||LA35_2==KW_SORT||LA35_2==KW_TABLESAMPLE||LA35_2==RPAREN) ) {
                    alt35=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 35, 2, input);

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
            case KW_CURRENT_DATE:
            case KW_CURRENT_TIMESTAMP:
            case KW_CURSOR:
            case KW_DATA:
            case KW_DATABASES:
            case KW_DATE:
            case KW_DATETIME:
            case KW_DAY:
            case KW_DBPROPERTIES:
            case KW_DECIMAL:
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
            case KW_HOUR:
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
            case KW_MINUTE:
            case KW_MONTH:
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
            case KW_SECOND:
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
            case KW_YEAR:
                {
                alt35=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 35, 0, input);

                throw nvae;

            }

            switch (alt35) {
                case 1 :
                    // FromClauseParser.g:234:4: subQuerySource
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_subQuerySource_in_partitionTableFunctionSource1623);
                    subQuerySource103=subQuerySource();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, subQuerySource103.getTree());

                    }
                    break;
                case 2 :
                    // FromClauseParser.g:235:4: tableSource
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_tableSource_in_partitionTableFunctionSource1630);
                    tableSource104=tableSource();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, tableSource104.getTree());

                    }
                    break;
                case 3 :
                    // FromClauseParser.g:236:4: partitionedTableFunction
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_partitionedTableFunction_in_partitionTableFunctionSource1637);
                    partitionedTableFunction105=partitionedTableFunction();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, partitionedTableFunction105.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) { gParent.popMsg(state); }
        }

        catch (RecognitionException e) {
          throw e;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "partitionTableFunctionSource"


    public static class partitionedTableFunction_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "partitionedTableFunction"
    // FromClauseParser.g:239:1: partitionedTableFunction : name= Identifier LPAREN KW_ON ( ( partitionTableFunctionSource )=> (ptfsrc= partitionTableFunctionSource (spec= partitioningSpec )? ) ) ( ( Identifier LPAREN expression RPAREN )=> Identifier LPAREN expression RPAREN ( COMMA Identifier LPAREN expression RPAREN )* )? ( ( RPAREN )=> ( RPAREN ) ) ( ( Identifier )=>alias= Identifier )? -> ^( TOK_PTBLFUNCTION $name ( $alias)? $ptfsrc ( $spec)? ( expression )* ) ;
    public final HiveParser_FromClauseParser.partitionedTableFunction_return partitionedTableFunction() throws RecognitionException {
        HiveParser_FromClauseParser.partitionedTableFunction_return retval = new HiveParser_FromClauseParser.partitionedTableFunction_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token name=null;
        Token alias=null;
        Token LPAREN106=null;
        Token KW_ON107=null;
        Token Identifier108=null;
        Token LPAREN109=null;
        Token RPAREN111=null;
        Token COMMA112=null;
        Token Identifier113=null;
        Token LPAREN114=null;
        Token RPAREN116=null;
        Token RPAREN117=null;
        HiveParser_FromClauseParser.partitionTableFunctionSource_return ptfsrc =null;

        HiveParser_FromClauseParser.partitioningSpec_return spec =null;

        HiveParser_IdentifiersParser.expression_return expression110 =null;

        HiveParser_IdentifiersParser.expression_return expression115 =null;


        CommonTree name_tree=null;
        CommonTree alias_tree=null;
        CommonTree LPAREN106_tree=null;
        CommonTree KW_ON107_tree=null;
        CommonTree Identifier108_tree=null;
        CommonTree LPAREN109_tree=null;
        CommonTree RPAREN111_tree=null;
        CommonTree COMMA112_tree=null;
        CommonTree Identifier113_tree=null;
        CommonTree LPAREN114_tree=null;
        CommonTree RPAREN116_tree=null;
        CommonTree RPAREN117_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_KW_ON=new RewriteRuleTokenStream(adaptor,"token KW_ON");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        RewriteRuleSubtreeStream stream_partitionTableFunctionSource=new RewriteRuleSubtreeStream(adaptor,"rule partitionTableFunctionSource");
        RewriteRuleSubtreeStream stream_partitioningSpec=new RewriteRuleSubtreeStream(adaptor,"rule partitioningSpec");
         gParent.pushMsg("ptf clause", state); 
        try {
            // FromClauseParser.g:242:4: (name= Identifier LPAREN KW_ON ( ( partitionTableFunctionSource )=> (ptfsrc= partitionTableFunctionSource (spec= partitioningSpec )? ) ) ( ( Identifier LPAREN expression RPAREN )=> Identifier LPAREN expression RPAREN ( COMMA Identifier LPAREN expression RPAREN )* )? ( ( RPAREN )=> ( RPAREN ) ) ( ( Identifier )=>alias= Identifier )? -> ^( TOK_PTBLFUNCTION $name ( $alias)? $ptfsrc ( $spec)? ( expression )* ) )
            // FromClauseParser.g:243:4: name= Identifier LPAREN KW_ON ( ( partitionTableFunctionSource )=> (ptfsrc= partitionTableFunctionSource (spec= partitioningSpec )? ) ) ( ( Identifier LPAREN expression RPAREN )=> Identifier LPAREN expression RPAREN ( COMMA Identifier LPAREN expression RPAREN )* )? ( ( RPAREN )=> ( RPAREN ) ) ( ( Identifier )=>alias= Identifier )?
            {
            name=(Token)match(input,Identifier,FOLLOW_Identifier_in_partitionedTableFunction1667); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_Identifier.add(name);


            LPAREN106=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_partitionedTableFunction1669); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN106);


            KW_ON107=(Token)match(input,KW_ON,FOLLOW_KW_ON_in_partitionedTableFunction1671); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_KW_ON.add(KW_ON107);


            // FromClauseParser.g:244:4: ( ( partitionTableFunctionSource )=> (ptfsrc= partitionTableFunctionSource (spec= partitioningSpec )? ) )
            // FromClauseParser.g:244:5: ( partitionTableFunctionSource )=> (ptfsrc= partitionTableFunctionSource (spec= partitioningSpec )? )
            {
            // FromClauseParser.g:244:39: (ptfsrc= partitionTableFunctionSource (spec= partitioningSpec )? )
            // FromClauseParser.g:244:40: ptfsrc= partitionTableFunctionSource (spec= partitioningSpec )?
            {
            pushFollow(FOLLOW_partitionTableFunctionSource_in_partitionedTableFunction1686);
            ptfsrc=partitionTableFunctionSource();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_partitionTableFunctionSource.add(ptfsrc.getTree());

            // FromClauseParser.g:244:80: (spec= partitioningSpec )?
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==KW_CLUSTER||LA36_0==KW_DISTRIBUTE||LA36_0==KW_ORDER||LA36_0==KW_PARTITION||LA36_0==KW_SORT) ) {
                alt36=1;
            }
            switch (alt36) {
                case 1 :
                    // FromClauseParser.g:244:80: spec= partitioningSpec
                    {
                    pushFollow(FOLLOW_partitioningSpec_in_partitionedTableFunction1690);
                    spec=partitioningSpec();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_partitioningSpec.add(spec.getTree());

                    }
                    break;

            }


            }


            }


            // FromClauseParser.g:245:4: ( ( Identifier LPAREN expression RPAREN )=> Identifier LPAREN expression RPAREN ( COMMA Identifier LPAREN expression RPAREN )* )?
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==Identifier) && (synpred10_FromClauseParser())) {
                alt38=1;
            }
            switch (alt38) {
                case 1 :
                    // FromClauseParser.g:245:5: ( Identifier LPAREN expression RPAREN )=> Identifier LPAREN expression RPAREN ( COMMA Identifier LPAREN expression RPAREN )*
                    {
                    Identifier108=(Token)match(input,Identifier,FOLLOW_Identifier_in_partitionedTableFunction1712); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_Identifier.add(Identifier108);


                    LPAREN109=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_partitionedTableFunction1714); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN109);


                    pushFollow(FOLLOW_expression_in_partitionedTableFunction1716);
                    expression110=gHiveParser.expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression110.getTree());

                    RPAREN111=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_partitionedTableFunction1718); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN111);


                    // FromClauseParser.g:245:83: ( COMMA Identifier LPAREN expression RPAREN )*
                    loop37:
                    do {
                        int alt37=2;
                        int LA37_0 = input.LA(1);

                        if ( (LA37_0==COMMA) ) {
                            alt37=1;
                        }


                        switch (alt37) {
                    	case 1 :
                    	    // FromClauseParser.g:245:85: COMMA Identifier LPAREN expression RPAREN
                    	    {
                    	    COMMA112=(Token)match(input,COMMA,FOLLOW_COMMA_in_partitionedTableFunction1722); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA112);


                    	    Identifier113=(Token)match(input,Identifier,FOLLOW_Identifier_in_partitionedTableFunction1724); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_Identifier.add(Identifier113);


                    	    LPAREN114=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_partitionedTableFunction1726); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN114);


                    	    pushFollow(FOLLOW_expression_in_partitionedTableFunction1728);
                    	    expression115=gHiveParser.expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_expression.add(expression115.getTree());

                    	    RPAREN116=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_partitionedTableFunction1730); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN116);


                    	    }
                    	    break;

                    	default :
                    	    break loop37;
                        }
                    } while (true);


                    }
                    break;

            }


            // FromClauseParser.g:246:4: ( ( RPAREN )=> ( RPAREN ) )
            // FromClauseParser.g:246:5: ( RPAREN )=> ( RPAREN )
            {
            // FromClauseParser.g:246:17: ( RPAREN )
            // FromClauseParser.g:246:18: RPAREN
            {
            RPAREN117=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_partitionedTableFunction1747); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN117);


            }


            }


            // FromClauseParser.g:246:27: ( ( Identifier )=>alias= Identifier )?
            int alt39=2;
            alt39 = dfa39.predict(input);
            switch (alt39) {
                case 1 :
                    // FromClauseParser.g:246:28: ( Identifier )=>alias= Identifier
                    {
                    alias=(Token)match(input,Identifier,FOLLOW_Identifier_in_partitionedTableFunction1760); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_Identifier.add(alias);


                    }
                    break;

            }


            // AST REWRITE
            // elements: ptfsrc, alias, expression, spec, name
            // token labels: name, alias
            // rule labels: ptfsrc, spec, retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleTokenStream stream_name=new RewriteRuleTokenStream(adaptor,"token name",name);
            RewriteRuleTokenStream stream_alias=new RewriteRuleTokenStream(adaptor,"token alias",alias);
            RewriteRuleSubtreeStream stream_ptfsrc=new RewriteRuleSubtreeStream(adaptor,"rule ptfsrc",ptfsrc!=null?ptfsrc.tree:null);
            RewriteRuleSubtreeStream stream_spec=new RewriteRuleSubtreeStream(adaptor,"rule spec",spec!=null?spec.tree:null);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 247:4: -> ^( TOK_PTBLFUNCTION $name ( $alias)? $ptfsrc ( $spec)? ( expression )* )
            {
                // FromClauseParser.g:247:9: ^( TOK_PTBLFUNCTION $name ( $alias)? $ptfsrc ( $spec)? ( expression )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(TOK_PTBLFUNCTION, "TOK_PTBLFUNCTION")
                , root_1);

                adaptor.addChild(root_1, stream_name.nextNode());

                // FromClauseParser.g:247:35: ( $alias)?
                if ( stream_alias.hasNext() ) {
                    adaptor.addChild(root_1, stream_alias.nextNode());

                }
                stream_alias.reset();

                adaptor.addChild(root_1, stream_ptfsrc.nextTree());

                // FromClauseParser.g:247:51: ( $spec)?
                if ( stream_spec.hasNext() ) {
                    adaptor.addChild(root_1, stream_spec.nextTree());

                }
                stream_spec.reset();

                // FromClauseParser.g:247:57: ( expression )*
                while ( stream_expression.hasNext() ) {
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


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) { gParent.popMsg(state); }
        }

        catch (RecognitionException e) {
          throw e;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "partitionedTableFunction"


    public static class whereClause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "whereClause"
    // FromClauseParser.g:252:1: whereClause : KW_WHERE searchCondition -> ^( TOK_WHERE searchCondition ) ;
    public final HiveParser_FromClauseParser.whereClause_return whereClause() throws RecognitionException {
        HiveParser_FromClauseParser.whereClause_return retval = new HiveParser_FromClauseParser.whereClause_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token KW_WHERE118=null;
        HiveParser_FromClauseParser.searchCondition_return searchCondition119 =null;


        CommonTree KW_WHERE118_tree=null;
        RewriteRuleTokenStream stream_KW_WHERE=new RewriteRuleTokenStream(adaptor,"token KW_WHERE");
        RewriteRuleSubtreeStream stream_searchCondition=new RewriteRuleSubtreeStream(adaptor,"rule searchCondition");
         gParent.pushMsg("where clause", state); 
        try {
            // FromClauseParser.g:255:5: ( KW_WHERE searchCondition -> ^( TOK_WHERE searchCondition ) )
            // FromClauseParser.g:256:5: KW_WHERE searchCondition
            {
            KW_WHERE118=(Token)match(input,KW_WHERE,FOLLOW_KW_WHERE_in_whereClause1822); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_KW_WHERE.add(KW_WHERE118);


            pushFollow(FOLLOW_searchCondition_in_whereClause1824);
            searchCondition119=searchCondition();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_searchCondition.add(searchCondition119.getTree());

            // AST REWRITE
            // elements: searchCondition
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 256:30: -> ^( TOK_WHERE searchCondition )
            {
                // FromClauseParser.g:256:33: ^( TOK_WHERE searchCondition )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(TOK_WHERE, "TOK_WHERE")
                , root_1);

                adaptor.addChild(root_1, stream_searchCondition.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) { gParent.popMsg(state); }
        }

        catch (RecognitionException e) {
          throw e;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "whereClause"


    public static class searchCondition_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "searchCondition"
    // FromClauseParser.g:259:1: searchCondition : expression ;
    public final HiveParser_FromClauseParser.searchCondition_return searchCondition() throws RecognitionException {
        HiveParser_FromClauseParser.searchCondition_return retval = new HiveParser_FromClauseParser.searchCondition_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        HiveParser_IdentifiersParser.expression_return expression120 =null;



         gParent.pushMsg("search condition", state); 
        try {
            // FromClauseParser.g:262:5: ( expression )
            // FromClauseParser.g:263:5: expression
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_expression_in_searchCondition1863);
            expression120=gHiveParser.expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression120.getTree());

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) { gParent.popMsg(state); }
        }

        catch (RecognitionException e) {
          throw e;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "searchCondition"


    public static class valueRowConstructor_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "valueRowConstructor"
    // FromClauseParser.g:272:1: valueRowConstructor : LPAREN precedenceUnaryPrefixExpression ( COMMA precedenceUnaryPrefixExpression )* RPAREN -> ^( TOK_VALUE_ROW ( precedenceUnaryPrefixExpression )+ ) ;
    public final HiveParser_FromClauseParser.valueRowConstructor_return valueRowConstructor() throws RecognitionException {
        HiveParser_FromClauseParser.valueRowConstructor_return retval = new HiveParser_FromClauseParser.valueRowConstructor_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token LPAREN121=null;
        Token COMMA123=null;
        Token RPAREN125=null;
        HiveParser_IdentifiersParser.precedenceUnaryPrefixExpression_return precedenceUnaryPrefixExpression122 =null;

        HiveParser_IdentifiersParser.precedenceUnaryPrefixExpression_return precedenceUnaryPrefixExpression124 =null;


        CommonTree LPAREN121_tree=null;
        CommonTree COMMA123_tree=null;
        CommonTree RPAREN125_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleSubtreeStream stream_precedenceUnaryPrefixExpression=new RewriteRuleSubtreeStream(adaptor,"rule precedenceUnaryPrefixExpression");
        try {
            // FromClauseParser.g:273:5: ( LPAREN precedenceUnaryPrefixExpression ( COMMA precedenceUnaryPrefixExpression )* RPAREN -> ^( TOK_VALUE_ROW ( precedenceUnaryPrefixExpression )+ ) )
            // FromClauseParser.g:274:5: LPAREN precedenceUnaryPrefixExpression ( COMMA precedenceUnaryPrefixExpression )* RPAREN
            {
            LPAREN121=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_valueRowConstructor1890); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN121);


            pushFollow(FOLLOW_precedenceUnaryPrefixExpression_in_valueRowConstructor1892);
            precedenceUnaryPrefixExpression122=gHiveParser.precedenceUnaryPrefixExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_precedenceUnaryPrefixExpression.add(precedenceUnaryPrefixExpression122.getTree());

            // FromClauseParser.g:274:44: ( COMMA precedenceUnaryPrefixExpression )*
            loop40:
            do {
                int alt40=2;
                int LA40_0 = input.LA(1);

                if ( (LA40_0==COMMA) ) {
                    alt40=1;
                }


                switch (alt40) {
            	case 1 :
            	    // FromClauseParser.g:274:45: COMMA precedenceUnaryPrefixExpression
            	    {
            	    COMMA123=(Token)match(input,COMMA,FOLLOW_COMMA_in_valueRowConstructor1895); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA123);


            	    pushFollow(FOLLOW_precedenceUnaryPrefixExpression_in_valueRowConstructor1897);
            	    precedenceUnaryPrefixExpression124=gHiveParser.precedenceUnaryPrefixExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_precedenceUnaryPrefixExpression.add(precedenceUnaryPrefixExpression124.getTree());

            	    }
            	    break;

            	default :
            	    break loop40;
                }
            } while (true);


            RPAREN125=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_valueRowConstructor1901); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN125);


            // AST REWRITE
            // elements: precedenceUnaryPrefixExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 274:92: -> ^( TOK_VALUE_ROW ( precedenceUnaryPrefixExpression )+ )
            {
                // FromClauseParser.g:274:95: ^( TOK_VALUE_ROW ( precedenceUnaryPrefixExpression )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(TOK_VALUE_ROW, "TOK_VALUE_ROW")
                , root_1);

                if ( !(stream_precedenceUnaryPrefixExpression.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_precedenceUnaryPrefixExpression.hasNext() ) {
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


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "valueRowConstructor"


    public static class valuesTableConstructor_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "valuesTableConstructor"
    // FromClauseParser.g:277:1: valuesTableConstructor : valueRowConstructor ( COMMA valueRowConstructor )* -> ^( TOK_VALUES_TABLE ( valueRowConstructor )+ ) ;
    public final HiveParser_FromClauseParser.valuesTableConstructor_return valuesTableConstructor() throws RecognitionException {
        HiveParser_FromClauseParser.valuesTableConstructor_return retval = new HiveParser_FromClauseParser.valuesTableConstructor_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token COMMA127=null;
        HiveParser_FromClauseParser.valueRowConstructor_return valueRowConstructor126 =null;

        HiveParser_FromClauseParser.valueRowConstructor_return valueRowConstructor128 =null;


        CommonTree COMMA127_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_valueRowConstructor=new RewriteRuleSubtreeStream(adaptor,"rule valueRowConstructor");
        try {
            // FromClauseParser.g:278:5: ( valueRowConstructor ( COMMA valueRowConstructor )* -> ^( TOK_VALUES_TABLE ( valueRowConstructor )+ ) )
            // FromClauseParser.g:279:5: valueRowConstructor ( COMMA valueRowConstructor )*
            {
            pushFollow(FOLLOW_valueRowConstructor_in_valuesTableConstructor1931);
            valueRowConstructor126=valueRowConstructor();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_valueRowConstructor.add(valueRowConstructor126.getTree());

            // FromClauseParser.g:279:25: ( COMMA valueRowConstructor )*
            loop41:
            do {
                int alt41=2;
                int LA41_0 = input.LA(1);

                if ( (LA41_0==COMMA) ) {
                    alt41=1;
                }


                switch (alt41) {
            	case 1 :
            	    // FromClauseParser.g:279:26: COMMA valueRowConstructor
            	    {
            	    COMMA127=(Token)match(input,COMMA,FOLLOW_COMMA_in_valuesTableConstructor1934); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA127);


            	    pushFollow(FOLLOW_valueRowConstructor_in_valuesTableConstructor1936);
            	    valueRowConstructor128=valueRowConstructor();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_valueRowConstructor.add(valueRowConstructor128.getTree());

            	    }
            	    break;

            	default :
            	    break loop41;
                }
            } while (true);


            // AST REWRITE
            // elements: valueRowConstructor
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 279:54: -> ^( TOK_VALUES_TABLE ( valueRowConstructor )+ )
            {
                // FromClauseParser.g:279:57: ^( TOK_VALUES_TABLE ( valueRowConstructor )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(TOK_VALUES_TABLE, "TOK_VALUES_TABLE")
                , root_1);

                if ( !(stream_valueRowConstructor.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_valueRowConstructor.hasNext() ) {
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


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "valuesTableConstructor"


    public static class valuesClause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "valuesClause"
    // FromClauseParser.g:287:1: valuesClause : KW_VALUES valuesTableConstructor -> valuesTableConstructor ;
    public final HiveParser_FromClauseParser.valuesClause_return valuesClause() throws RecognitionException {
        HiveParser_FromClauseParser.valuesClause_return retval = new HiveParser_FromClauseParser.valuesClause_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token KW_VALUES129=null;
        HiveParser_FromClauseParser.valuesTableConstructor_return valuesTableConstructor130 =null;


        CommonTree KW_VALUES129_tree=null;
        RewriteRuleTokenStream stream_KW_VALUES=new RewriteRuleTokenStream(adaptor,"token KW_VALUES");
        RewriteRuleSubtreeStream stream_valuesTableConstructor=new RewriteRuleSubtreeStream(adaptor,"rule valuesTableConstructor");
        try {
            // FromClauseParser.g:288:5: ( KW_VALUES valuesTableConstructor -> valuesTableConstructor )
            // FromClauseParser.g:289:5: KW_VALUES valuesTableConstructor
            {
            KW_VALUES129=(Token)match(input,KW_VALUES,FOLLOW_KW_VALUES_in_valuesClause1970); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_KW_VALUES.add(KW_VALUES129);


            pushFollow(FOLLOW_valuesTableConstructor_in_valuesClause1972);
            valuesTableConstructor130=valuesTableConstructor();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_valuesTableConstructor.add(valuesTableConstructor130.getTree());

            // AST REWRITE
            // elements: valuesTableConstructor
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 289:38: -> valuesTableConstructor
            {
                adaptor.addChild(root_0, stream_valuesTableConstructor.nextTree());

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "valuesClause"


    public static class virtualTableSource_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "virtualTableSource"
    // FromClauseParser.g:296:1: virtualTableSource : LPAREN valuesClause RPAREN tableNameColList -> ^( TOK_VIRTUAL_TABLE tableNameColList valuesClause ) ;
    public final HiveParser_FromClauseParser.virtualTableSource_return virtualTableSource() throws RecognitionException {
        HiveParser_FromClauseParser.virtualTableSource_return retval = new HiveParser_FromClauseParser.virtualTableSource_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token LPAREN131=null;
        Token RPAREN133=null;
        HiveParser_FromClauseParser.valuesClause_return valuesClause132 =null;

        HiveParser_FromClauseParser.tableNameColList_return tableNameColList134 =null;


        CommonTree LPAREN131_tree=null;
        CommonTree RPAREN133_tree=null;
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleSubtreeStream stream_valuesClause=new RewriteRuleSubtreeStream(adaptor,"rule valuesClause");
        RewriteRuleSubtreeStream stream_tableNameColList=new RewriteRuleSubtreeStream(adaptor,"rule tableNameColList");
        try {
            // FromClauseParser.g:297:2: ( LPAREN valuesClause RPAREN tableNameColList -> ^( TOK_VIRTUAL_TABLE tableNameColList valuesClause ) )
            // FromClauseParser.g:298:2: LPAREN valuesClause RPAREN tableNameColList
            {
            LPAREN131=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_virtualTableSource1993); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN131);


            pushFollow(FOLLOW_valuesClause_in_virtualTableSource1995);
            valuesClause132=valuesClause();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_valuesClause.add(valuesClause132.getTree());

            RPAREN133=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_virtualTableSource1997); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN133);


            pushFollow(FOLLOW_tableNameColList_in_virtualTableSource1999);
            tableNameColList134=tableNameColList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_tableNameColList.add(tableNameColList134.getTree());

            // AST REWRITE
            // elements: tableNameColList, valuesClause
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 298:46: -> ^( TOK_VIRTUAL_TABLE tableNameColList valuesClause )
            {
                // FromClauseParser.g:298:49: ^( TOK_VIRTUAL_TABLE tableNameColList valuesClause )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(TOK_VIRTUAL_TABLE, "TOK_VIRTUAL_TABLE")
                , root_1);

                adaptor.addChild(root_1, stream_tableNameColList.nextTree());

                adaptor.addChild(root_1, stream_valuesClause.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "virtualTableSource"


    public static class tableNameColList_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "tableNameColList"
    // FromClauseParser.g:304:1: tableNameColList : ( KW_AS )? identifier LPAREN identifier ( COMMA identifier )* RPAREN -> ^( TOK_VIRTUAL_TABREF ^( TOK_TABNAME identifier ) ^( TOK_COL_NAME ( identifier )+ ) ) ;
    public final HiveParser_FromClauseParser.tableNameColList_return tableNameColList() throws RecognitionException {
        HiveParser_FromClauseParser.tableNameColList_return retval = new HiveParser_FromClauseParser.tableNameColList_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token KW_AS135=null;
        Token LPAREN137=null;
        Token COMMA139=null;
        Token RPAREN141=null;
        HiveParser_IdentifiersParser.identifier_return identifier136 =null;

        HiveParser_IdentifiersParser.identifier_return identifier138 =null;

        HiveParser_IdentifiersParser.identifier_return identifier140 =null;


        CommonTree KW_AS135_tree=null;
        CommonTree LPAREN137_tree=null;
        CommonTree COMMA139_tree=null;
        CommonTree RPAREN141_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_KW_AS=new RewriteRuleTokenStream(adaptor,"token KW_AS");
        RewriteRuleSubtreeStream stream_identifier=new RewriteRuleSubtreeStream(adaptor,"rule identifier");
        try {
            // FromClauseParser.g:305:5: ( ( KW_AS )? identifier LPAREN identifier ( COMMA identifier )* RPAREN -> ^( TOK_VIRTUAL_TABREF ^( TOK_TABNAME identifier ) ^( TOK_COL_NAME ( identifier )+ ) ) )
            // FromClauseParser.g:306:5: ( KW_AS )? identifier LPAREN identifier ( COMMA identifier )* RPAREN
            {
            // FromClauseParser.g:306:5: ( KW_AS )?
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( (LA42_0==KW_AS) ) {
                int LA42_1 = input.LA(2);

                if ( ((LA42_1 >= Identifier && LA42_1 <= KW_ANALYZE)||(LA42_1 >= KW_ARCHIVE && LA42_1 <= KW_CASCADE)||LA42_1==KW_CHANGE||(LA42_1 >= KW_CLUSTER && LA42_1 <= KW_COLLECTION)||(LA42_1 >= KW_COLUMNS && LA42_1 <= KW_CONCATENATE)||(LA42_1 >= KW_CONTINUE && LA42_1 <= KW_CREATE)||LA42_1==KW_CUBE||(LA42_1 >= KW_CURRENT_DATE && LA42_1 <= KW_DATA)||(LA42_1 >= KW_DATABASES && LA42_1 <= KW_DISABLE)||(LA42_1 >= KW_DISTRIBUTE && LA42_1 <= KW_ELEM_TYPE)||LA42_1==KW_ENABLE||LA42_1==KW_ESCAPED||(LA42_1 >= KW_EXCLUSIVE && LA42_1 <= KW_EXPORT)||(LA42_1 >= KW_EXTERNAL && LA42_1 <= KW_FLOAT)||(LA42_1 >= KW_FOR && LA42_1 <= KW_FORMATTED)||LA42_1==KW_FULL||(LA42_1 >= KW_FUNCTIONS && LA42_1 <= KW_GROUPING)||(LA42_1 >= KW_HOLD_DDLTIME && LA42_1 <= KW_IDXPROPERTIES)||(LA42_1 >= KW_IGNORE && LA42_1 <= KW_INTERSECT)||(LA42_1 >= KW_INTO && LA42_1 <= KW_JAR)||(LA42_1 >= KW_KEYS && LA42_1 <= KW_LEFT)||(LA42_1 >= KW_LIKE && LA42_1 <= KW_LONG)||(LA42_1 >= KW_MAPJOIN && LA42_1 <= KW_MONTH)||(LA42_1 >= KW_MSCK && LA42_1 <= KW_NOSCAN)||(LA42_1 >= KW_NO_DROP && LA42_1 <= KW_OFFLINE)||LA42_1==KW_OPTION||(LA42_1 >= KW_ORDER && LA42_1 <= KW_OUTPUTFORMAT)||(LA42_1 >= KW_OVERWRITE && LA42_1 <= KW_OWNER)||(LA42_1 >= KW_PARTITION && LA42_1 <= KW_PLUS)||(LA42_1 >= KW_PRETTY && LA42_1 <= KW_RECORDWRITER)||(LA42_1 >= KW_REGEXP && LA42_1 <= KW_SECOND)||(LA42_1 >= KW_SEMI && LA42_1 <= KW_TABLES)||(LA42_1 >= KW_TBLPROPERTIES && LA42_1 <= KW_TERMINATED)||(LA42_1 >= KW_TIMESTAMP && LA42_1 <= KW_TRANSACTIONS)||(LA42_1 >= KW_TRIGGER && LA42_1 <= KW_UNARCHIVE)||(LA42_1 >= KW_UNDO && LA42_1 <= KW_UNIONTYPE)||(LA42_1 >= KW_UNLOCK && LA42_1 <= KW_VALUE_TYPE)||LA42_1==KW_VIEW||LA42_1==KW_WHILE||(LA42_1 >= KW_WITH && LA42_1 <= KW_YEAR)) ) {
                    alt42=1;
                }
            }
            switch (alt42) {
                case 1 :
                    // FromClauseParser.g:306:5: KW_AS
                    {
                    KW_AS135=(Token)match(input,KW_AS,FOLLOW_KW_AS_in_tableNameColList2028); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_AS.add(KW_AS135);


                    }
                    break;

            }


            pushFollow(FOLLOW_identifier_in_tableNameColList2031);
            identifier136=gHiveParser.identifier();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_identifier.add(identifier136.getTree());

            LPAREN137=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_tableNameColList2033); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN137);


            pushFollow(FOLLOW_identifier_in_tableNameColList2035);
            identifier138=gHiveParser.identifier();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_identifier.add(identifier138.getTree());

            // FromClauseParser.g:306:41: ( COMMA identifier )*
            loop43:
            do {
                int alt43=2;
                int LA43_0 = input.LA(1);

                if ( (LA43_0==COMMA) ) {
                    alt43=1;
                }


                switch (alt43) {
            	case 1 :
            	    // FromClauseParser.g:306:42: COMMA identifier
            	    {
            	    COMMA139=(Token)match(input,COMMA,FOLLOW_COMMA_in_tableNameColList2038); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA139);


            	    pushFollow(FOLLOW_identifier_in_tableNameColList2040);
            	    identifier140=gHiveParser.identifier();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_identifier.add(identifier140.getTree());

            	    }
            	    break;

            	default :
            	    break loop43;
                }
            } while (true);


            RPAREN141=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_tableNameColList2044); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN141);


            // AST REWRITE
            // elements: identifier, identifier
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 306:68: -> ^( TOK_VIRTUAL_TABREF ^( TOK_TABNAME identifier ) ^( TOK_COL_NAME ( identifier )+ ) )
            {
                // FromClauseParser.g:306:71: ^( TOK_VIRTUAL_TABREF ^( TOK_TABNAME identifier ) ^( TOK_COL_NAME ( identifier )+ ) )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(TOK_VIRTUAL_TABREF, "TOK_VIRTUAL_TABREF")
                , root_1);

                // FromClauseParser.g:306:92: ^( TOK_TABNAME identifier )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(TOK_TABNAME, "TOK_TABNAME")
                , root_2);

                adaptor.addChild(root_2, stream_identifier.nextTree());

                adaptor.addChild(root_1, root_2);
                }

                // FromClauseParser.g:306:118: ^( TOK_COL_NAME ( identifier )+ )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(TOK_COL_NAME, "TOK_COL_NAME")
                , root_2);

                if ( !(stream_identifier.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_identifier.hasNext() ) {
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


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e) {
          throw e;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "tableNameColList"

    // $ANTLR start synpred1_FromClauseParser
    public final void synpred1_FromClauseParser_fragment() throws RecognitionException {
        // FromClauseParser.g:132:2: ( KW_LATERAL KW_VIEW KW_OUTER )
        // FromClauseParser.g:132:3: KW_LATERAL KW_VIEW KW_OUTER
        {
        match(input,KW_LATERAL,FOLLOW_KW_LATERAL_in_synpred1_FromClauseParser711); if (state.failed) return ;

        match(input,KW_VIEW,FOLLOW_KW_VIEW_in_synpred1_FromClauseParser713); if (state.failed) return ;

        match(input,KW_OUTER,FOLLOW_KW_OUTER_in_synpred1_FromClauseParser715); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred1_FromClauseParser

    // $ANTLR start synpred2_FromClauseParser
    public final void synpred2_FromClauseParser_fragment() throws RecognitionException {
        // FromClauseParser.g:132:102: ( COMMA )
        // FromClauseParser.g:132:103: COMMA
        {
        match(input,COMMA,FOLLOW_COMMA_in_synpred2_FromClauseParser737); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred2_FromClauseParser

    // $ANTLR start synpred3_FromClauseParser
    public final void synpred3_FromClauseParser_fragment() throws RecognitionException {
        // FromClauseParser.g:135:60: ( COMMA )
        // FromClauseParser.g:135:61: COMMA
        {
        match(input,COMMA,FOLLOW_COMMA_in_synpred3_FromClauseParser790); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred3_FromClauseParser

    // $ANTLR start synpred4_FromClauseParser
    public final void synpred4_FromClauseParser_fragment() throws RecognitionException {
        // FromClauseParser.g:150:6: ( Identifier LPAREN )
        // FromClauseParser.g:150:7: Identifier LPAREN
        {
        match(input,Identifier,FOLLOW_Identifier_in_synpred4_FromClauseParser891); if (state.failed) return ;

        match(input,LPAREN,FOLLOW_LPAREN_in_synpred4_FromClauseParser893); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred4_FromClauseParser

    // $ANTLR start synpred5_FromClauseParser
    public final void synpred5_FromClauseParser_fragment() throws RecognitionException {
        // FromClauseParser.g:184:6: ( tableProperties )
        // FromClauseParser.g:184:7: tableProperties
        {
        pushFollow(FOLLOW_tableProperties_in_synpred5_FromClauseParser1199);
        gHiveParser.tableProperties();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred5_FromClauseParser

    // $ANTLR start synpred6_FromClauseParser
    public final void synpred6_FromClauseParser_fragment() throws RecognitionException {
        // FromClauseParser.g:185:6: ( tableSample )
        // FromClauseParser.g:185:7: tableSample
        {
        pushFollow(FOLLOW_tableSample_in_synpred6_FromClauseParser1216);
        tableSample();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred6_FromClauseParser

    // $ANTLR start synpred7_FromClauseParser
    public final void synpred7_FromClauseParser_fragment() throws RecognitionException {
        // FromClauseParser.g:186:6: ( KW_AS )
        // FromClauseParser.g:186:7: KW_AS
        {
        match(input,KW_AS,FOLLOW_KW_AS_in_synpred7_FromClauseParser1233); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred7_FromClauseParser

    // $ANTLR start synpred8_FromClauseParser
    public final void synpred8_FromClauseParser_fragment() throws RecognitionException {
        // FromClauseParser.g:188:5: ( Identifier )
        // FromClauseParser.g:188:6: Identifier
        {
        match(input,Identifier,FOLLOW_Identifier_in_synpred8_FromClauseParser1257); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred8_FromClauseParser

    // $ANTLR start synpred10_FromClauseParser
    public final void synpred10_FromClauseParser_fragment() throws RecognitionException {
        // FromClauseParser.g:245:5: ( Identifier LPAREN expression RPAREN )
        // FromClauseParser.g:245:6: Identifier LPAREN expression RPAREN
        {
        match(input,Identifier,FOLLOW_Identifier_in_synpred10_FromClauseParser1700); if (state.failed) return ;

        match(input,LPAREN,FOLLOW_LPAREN_in_synpred10_FromClauseParser1702); if (state.failed) return ;

        pushFollow(FOLLOW_expression_in_synpred10_FromClauseParser1704);
        gHiveParser.expression();

        state._fsp--;
        if (state.failed) return ;

        match(input,RPAREN,FOLLOW_RPAREN_in_synpred10_FromClauseParser1706); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred10_FromClauseParser

    // $ANTLR start synpred12_FromClauseParser
    public final void synpred12_FromClauseParser_fragment() throws RecognitionException {
        // FromClauseParser.g:246:28: ( Identifier )
        // FromClauseParser.g:246:29: Identifier
        {
        match(input,Identifier,FOLLOW_Identifier_in_synpred12_FromClauseParser1753); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred12_FromClauseParser

    // Delegated rules

    public final boolean synpred3_FromClauseParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred3_FromClauseParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred10_FromClauseParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred10_FromClauseParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred4_FromClauseParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred4_FromClauseParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred2_FromClauseParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_FromClauseParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred1_FromClauseParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_FromClauseParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred7_FromClauseParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred7_FromClauseParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred5_FromClauseParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred5_FromClauseParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred6_FromClauseParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred6_FromClauseParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred8_FromClauseParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred8_FromClauseParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred12_FromClauseParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred12_FromClauseParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA14 dfa14 = new DFA14(this);
    protected DFA16 dfa16 = new DFA16(this);
    protected DFA19 dfa19 = new DFA19(this);
    protected DFA26 dfa26 = new DFA26(this);
    protected DFA28 dfa28 = new DFA28(this);
    protected DFA29 dfa29 = new DFA29(this);
    protected DFA31 dfa31 = new DFA31(this);
    protected DFA39 dfa39 = new DFA39(this);
    static final String DFA14_eotS =
        "\172\uffff";
    static final String DFA14_eofS =
        "\1\1\171\uffff";
    static final String DFA14_minS =
        "\1\12\2\uffff\1\32\27\uffff\3\0\134\uffff";
    static final String DFA14_maxS =
        "\1\u0133\2\uffff\1\u0128\27\uffff\3\0\134\uffff";
    static final String DFA14_acceptS =
        "\1\uffff\1\2\73\uffff\1\1\74\uffff";
    static final String DFA14_specialS =
        "\33\uffff\1\0\1\1\1\2\134\uffff}>";
    static final String[] DFA14_transitionS = {
            "\1\3\52\uffff\1\1\15\uffff\1\1\30\uffff\1\1\32\uffff\1\1\3\uffff"+
            "\1\1\1\uffff\1\1\11\uffff\1\1\3\uffff\1\1\7\uffff\1\1\2\uffff"+
            "\2\1\2\uffff\1\1\11\uffff\1\1\17\uffff\1\1\2\uffff\1\1\33\uffff"+
            "\1\1\11\uffff\1\1\11\uffff\1\1\13\uffff\1\1\32\uffff\1\1\21"+
            "\uffff\1\1\1\uffff\1\1\5\uffff\1\1\12\uffff\1\1",
            "",
            "",
            "\1\33\3\34\2\35\1\34\1\uffff\1\34\2\35\1\34\1\35\1\34\5\35"+
            "\2\34\1\35\1\34\2\uffff\1\34\1\uffff\4\34\1\uffff\6\34\1\uffff"+
            "\1\34\1\35\1\uffff\1\35\1\uffff\3\35\1\34\1\uffff\1\34\1\35"+
            "\3\34\1\35\2\34\1\35\3\34\1\35\3\34\1\uffff\1\34\2\35\1\34\1"+
            "\uffff\1\34\1\uffff\1\34\1\uffff\1\34\1\35\2\34\1\uffff\3\35"+
            "\4\34\1\35\1\uffff\1\35\2\34\1\uffff\1\35\1\uffff\1\34\3\35"+
            "\1\uffff\3\34\1\uffff\1\34\2\35\2\34\1\35\3\34\3\35\1\uffff"+
            "\2\35\2\34\1\uffff\2\34\2\35\1\uffff\1\35\3\34\1\35\5\34\2\uffff"+
            "\6\34\1\uffff\1\34\1\35\1\34\1\uffff\1\34\2\35\1\34\1\uffff"+
            "\1\34\1\uffff\3\35\2\34\1\uffff\2\34\1\uffff\1\35\2\34\1\35"+
            "\1\34\2\uffff\2\34\1\35\2\34\1\35\2\34\1\35\3\34\1\uffff\7\34"+
            "\1\35\1\34\1\35\3\34\3\35\3\34\1\uffff\4\34\1\35\5\34\1\35\10"+
            "\34\1\35\1\34\1\uffff\3\34\1\uffff\1\35\1\34\1\35\2\34\1\uffff"+
            "\3\35\1\34\1\uffff\1\34\1\35\1\34\1\uffff\3\34\1\35\2\34\2\35"+
            "\2\34\1\35\1\34\1\uffff\1\34\2\uffff\1\34\1\uffff\1\35\1\34"+
            "\3\uffff\1\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

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
        for (int i=0; i<numStates; i++) {
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
            return "()* loopback of 132:101: ( ( COMMA )=> COMMA identifier )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA14_27 = input.LA(1);

                         
                        int index14_27 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred2_FromClauseParser()) ) {s = 61;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index14_27);

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA14_28 = input.LA(1);

                         
                        int index14_28 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred2_FromClauseParser()) ) {s = 61;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index14_28);

                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA14_29 = input.LA(1);

                         
                        int index14_29 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred2_FromClauseParser()) ) {s = 61;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index14_29);

                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}

            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 14, _s, input);
            error(nvae);
            throw nvae;
        }

    }
    static final String DFA16_eotS =
        "\172\uffff";
    static final String DFA16_eofS =
        "\1\1\171\uffff";
    static final String DFA16_minS =
        "\1\12\2\uffff\1\32\27\uffff\3\0\134\uffff";
    static final String DFA16_maxS =
        "\1\u0133\2\uffff\1\u0128\27\uffff\3\0\134\uffff";
    static final String DFA16_acceptS =
        "\1\uffff\1\2\73\uffff\1\1\74\uffff";
    static final String DFA16_specialS =
        "\33\uffff\1\0\1\1\1\2\134\uffff}>";
    static final String[] DFA16_transitionS = {
            "\1\3\52\uffff\1\1\15\uffff\1\1\30\uffff\1\1\32\uffff\1\1\3\uffff"+
            "\1\1\1\uffff\1\1\11\uffff\1\1\3\uffff\1\1\7\uffff\1\1\2\uffff"+
            "\2\1\2\uffff\1\1\11\uffff\1\1\17\uffff\1\1\2\uffff\1\1\33\uffff"+
            "\1\1\11\uffff\1\1\11\uffff\1\1\13\uffff\1\1\32\uffff\1\1\21"+
            "\uffff\1\1\1\uffff\1\1\5\uffff\1\1\12\uffff\1\1",
            "",
            "",
            "\1\33\3\34\2\35\1\34\1\uffff\1\34\2\35\1\34\1\35\1\34\5\35"+
            "\2\34\1\35\1\34\2\uffff\1\34\1\uffff\4\34\1\uffff\6\34\1\uffff"+
            "\1\34\1\35\1\uffff\1\35\1\uffff\3\35\1\34\1\uffff\1\34\1\35"+
            "\3\34\1\35\2\34\1\35\3\34\1\35\3\34\1\uffff\1\34\2\35\1\34\1"+
            "\uffff\1\34\1\uffff\1\34\1\uffff\1\34\1\35\2\34\1\uffff\3\35"+
            "\4\34\1\35\1\uffff\1\35\2\34\1\uffff\1\35\1\uffff\1\34\3\35"+
            "\1\uffff\3\34\1\uffff\1\34\2\35\2\34\1\35\3\34\3\35\1\uffff"+
            "\2\35\2\34\1\uffff\2\34\2\35\1\uffff\1\35\3\34\1\35\5\34\2\uffff"+
            "\6\34\1\uffff\1\34\1\35\1\34\1\uffff\1\34\2\35\1\34\1\uffff"+
            "\1\34\1\uffff\3\35\2\34\1\uffff\2\34\1\uffff\1\35\2\34\1\35"+
            "\1\34\2\uffff\2\34\1\35\2\34\1\35\2\34\1\35\3\34\1\uffff\7\34"+
            "\1\35\1\34\1\35\3\34\3\35\3\34\1\uffff\4\34\1\35\5\34\1\35\10"+
            "\34\1\35\1\34\1\uffff\3\34\1\uffff\1\35\1\34\1\35\2\34\1\uffff"+
            "\3\35\1\34\1\uffff\1\34\1\35\1\34\1\uffff\3\34\1\35\2\34\2\35"+
            "\2\34\1\35\1\34\1\uffff\1\34\2\uffff\1\34\1\uffff\1\35\1\34"+
            "\3\uffff\1\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

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
        for (int i=0; i<numStates; i++) {
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
            return "()* loopback of 135:59: ( ( COMMA )=> COMMA identifier )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA16_27 = input.LA(1);

                         
                        int index16_27 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred3_FromClauseParser()) ) {s = 61;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index16_27);

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA16_28 = input.LA(1);

                         
                        int index16_28 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred3_FromClauseParser()) ) {s = 61;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index16_28);

                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA16_29 = input.LA(1);

                         
                        int index16_29 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred3_FromClauseParser()) ) {s = 61;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index16_29);

                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}

            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 16, _s, input);
            error(nvae);
            throw nvae;
        }

    }
    static final String DFA19_eotS =
        "\107\uffff";
    static final String DFA19_eofS =
        "\1\uffff\1\2\105\uffff";
    static final String DFA19_minS =
        "\1\32\1\12\2\uffff\1\166\1\7\101\uffff";
    static final String DFA19_maxS =
        "\1\u0128\1\u0133\2\uffff\1\u0123\1\u013b\101\uffff";
    static final String DFA19_acceptS =
        "\2\uffff\1\2\40\uffff\1\3\5\uffff\1\4\1\1\34\uffff";
    static final String DFA19_specialS =
        "\5\uffff\1\0\101\uffff}>";
    static final String[] DFA19_transitionS = {
            "\1\1\6\2\1\uffff\17\2\2\uffff\1\2\1\uffff\4\2\1\uffff\6\2\1"+
            "\uffff\2\2\1\uffff\1\2\1\uffff\4\2\1\uffff\20\2\1\uffff\4\2"+
            "\1\uffff\1\2\1\uffff\1\2\1\uffff\4\2\1\uffff\10\2\1\uffff\3"+
            "\2\1\uffff\1\2\1\uffff\4\2\1\uffff\3\2\1\uffff\14\2\1\uffff"+
            "\4\2\1\uffff\4\2\1\uffff\12\2\2\uffff\6\2\1\uffff\3\2\1\uffff"+
            "\4\2\1\uffff\1\2\1\uffff\5\2\1\uffff\2\2\1\uffff\5\2\2\uffff"+
            "\14\2\1\uffff\23\2\1\uffff\25\2\1\uffff\3\2\1\uffff\5\2\1\uffff"+
            "\4\2\1\uffff\3\2\1\uffff\14\2\1\uffff\1\2\2\uffff\1\2\1\uffff"+
            "\2\2\3\uffff\1\4",
            "\1\2\6\uffff\1\2\10\uffff\1\2\11\uffff\1\2\20\uffff\1\2\15"+
            "\uffff\1\2\30\uffff\1\2\32\uffff\1\2\3\uffff\1\2\1\uffff\1\2"+
            "\11\uffff\1\2\3\uffff\1\2\7\uffff\1\2\2\uffff\2\2\2\uffff\1"+
            "\2\11\uffff\1\2\17\uffff\1\2\2\uffff\1\2\33\uffff\1\2\11\uffff"+
            "\1\2\11\uffff\1\2\13\uffff\1\2\11\uffff\1\2\20\uffff\1\2\21"+
            "\uffff\1\2\1\uffff\1\2\5\uffff\1\5\12\uffff\1\2",
            "",
            "",
            "\1\43\24\uffff\1\43\30\uffff\1\43\56\uffff\1\43\23\uffff\1"+
            "\43\63\uffff\1\51\7\uffff\1\43",
            "\1\2\5\uffff\1\2\4\uffff\1\2\7\uffff\7\2\1\uffff\22\2\1\uffff"+
            "\4\2\1\uffff\6\2\1\uffff\2\2\1\uffff\1\2\1\uffff\4\2\1\uffff"+
            "\20\2\1\uffff\4\2\1\uffff\1\2\1\uffff\1\2\1\uffff\4\2\1\uffff"+
            "\10\2\1\uffff\3\2\1\uffff\1\2\1\uffff\4\2\1\uffff\25\2\1\uffff"+
            "\4\2\1\uffff\12\2\1\uffff\7\2\1\uffff\10\2\1\52\1\2\1\uffff"+
            "\5\2\1\uffff\2\2\1\uffff\5\2\2\uffff\14\2\1\uffff\23\2\1\uffff"+
            "\25\2\1\uffff\3\2\1\uffff\5\2\1\uffff\4\2\1\uffff\3\2\1\uffff"+
            "\14\2\1\uffff\1\2\2\uffff\1\2\1\uffff\2\2\3\uffff\1\2\2\uffff"+
            "\1\2\2\uffff\2\2\10\uffff\4\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

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
        for (int i=0; i<numStates; i++) {
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
            return "150:5: ( ( Identifier LPAREN )=> partitionedTableFunction | tableSource | subQuerySource | virtualTableSource )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA19_5 = input.LA(1);

                         
                        int index19_5 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA19_5==KW_ON) && (synpred4_FromClauseParser())) {s = 42;}

                        else if ( (LA19_5==BigintLiteral||LA19_5==CharSetName||LA19_5==DecimalLiteral||(LA19_5 >= Identifier && LA19_5 <= KW_ANALYZE)||(LA19_5 >= KW_ARCHIVE && LA19_5 <= KW_CHANGE)||(LA19_5 >= KW_CLUSTER && LA19_5 <= KW_COLLECTION)||(LA19_5 >= KW_COLUMNS && LA19_5 <= KW_CONCATENATE)||(LA19_5 >= KW_CONTINUE && LA19_5 <= KW_CREATE)||LA19_5==KW_CUBE||(LA19_5 >= KW_CURRENT_DATE && LA19_5 <= KW_DATA)||(LA19_5 >= KW_DATABASES && LA19_5 <= KW_DISABLE)||(LA19_5 >= KW_DISTRIBUTE && LA19_5 <= KW_ELEM_TYPE)||LA19_5==KW_ENABLE||LA19_5==KW_ESCAPED||(LA19_5 >= KW_EXCLUSIVE && LA19_5 <= KW_EXPORT)||(LA19_5 >= KW_EXTERNAL && LA19_5 <= KW_FLOAT)||(LA19_5 >= KW_FOR && LA19_5 <= KW_FORMATTED)||LA19_5==KW_FULL||(LA19_5 >= KW_FUNCTIONS && LA19_5 <= KW_GROUPING)||(LA19_5 >= KW_HOLD_DDLTIME && LA19_5 <= KW_JAR)||(LA19_5 >= KW_KEYS && LA19_5 <= KW_LEFT)||(LA19_5 >= KW_LIKE && LA19_5 <= KW_LONG)||(LA19_5 >= KW_MAP && LA19_5 <= KW_MONTH)||(LA19_5 >= KW_MSCK && LA19_5 <= KW_OFFLINE)||LA19_5==KW_OPTION||(LA19_5 >= KW_ORDER && LA19_5 <= KW_OUTPUTFORMAT)||(LA19_5 >= KW_OVERWRITE && LA19_5 <= KW_OWNER)||(LA19_5 >= KW_PARTITION && LA19_5 <= KW_PLUS)||(LA19_5 >= KW_PRETTY && LA19_5 <= KW_RECORDWRITER)||(LA19_5 >= KW_REGEXP && LA19_5 <= KW_SECOND)||(LA19_5 >= KW_SEMI && LA19_5 <= KW_TABLES)||(LA19_5 >= KW_TBLPROPERTIES && LA19_5 <= KW_TERMINATED)||(LA19_5 >= KW_TIMESTAMP && LA19_5 <= KW_TRANSACTIONS)||(LA19_5 >= KW_TRIGGER && LA19_5 <= KW_UNARCHIVE)||(LA19_5 >= KW_UNDO && LA19_5 <= KW_UNIONTYPE)||(LA19_5 >= KW_UNLOCK && LA19_5 <= KW_VALUE_TYPE)||LA19_5==KW_VIEW||LA19_5==KW_WHILE||(LA19_5 >= KW_WITH && LA19_5 <= KW_YEAR)||LA19_5==LPAREN||LA19_5==MINUS||(LA19_5 >= Number && LA19_5 <= PLUS)||(LA19_5 >= SmallintLiteral && LA19_5 <= TinyintLiteral)) ) {s = 2;}

                         
                        input.seek(index19_5);

                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}

            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 19, _s, input);
            error(nvae);
            throw nvae;
        }

    }
    static final String DFA26_eotS =
        "\124\uffff";
    static final String DFA26_eofS =
        "\1\2\123\uffff";
    static final String DFA26_minS =
        "\1\12\1\7\35\uffff\1\4\33\uffff\3\0\26\uffff";
    static final String DFA26_maxS =
        "\1\u0133\1\u013b\35\uffff\1\u0139\33\uffff\3\0\26\uffff";
    static final String DFA26_acceptS =
        "\2\uffff\1\2\120\uffff\1\1";
    static final String DFA26_specialS =
        "\73\uffff\1\0\1\1\1\2\26\uffff}>";
    static final String[] DFA26_transitionS = {
            "\1\2\17\uffff\1\2\11\uffff\1\2\20\uffff\1\2\15\uffff\1\2\30"+
            "\uffff\1\2\32\uffff\1\2\3\uffff\1\2\1\uffff\1\2\11\uffff\1\2"+
            "\3\uffff\1\2\7\uffff\1\2\2\uffff\2\2\2\uffff\1\2\11\uffff\1"+
            "\2\17\uffff\1\2\2\uffff\1\2\10\uffff\1\2\22\uffff\1\2\11\uffff"+
            "\1\2\11\uffff\1\2\13\uffff\1\2\11\uffff\1\2\20\uffff\1\2\21"+
            "\uffff\1\2\1\uffff\1\2\5\uffff\1\1\12\uffff\1\2",
            "\1\2\5\uffff\1\2\4\uffff\1\2\7\uffff\7\2\1\uffff\22\2\1\uffff"+
            "\4\2\1\uffff\6\2\1\uffff\2\2\1\uffff\1\2\1\uffff\4\2\1\uffff"+
            "\20\2\1\uffff\4\2\1\uffff\1\2\1\uffff\1\2\1\uffff\4\2\1\uffff"+
            "\10\2\1\uffff\3\2\1\uffff\1\2\1\uffff\4\2\1\uffff\25\2\1\uffff"+
            "\4\2\1\uffff\12\2\1\uffff\7\2\1\uffff\10\2\1\uffff\1\2\1\uffff"+
            "\5\2\1\uffff\2\2\1\uffff\5\2\2\uffff\14\2\1\uffff\23\2\1\uffff"+
            "\25\2\1\uffff\3\2\1\uffff\5\2\1\uffff\4\2\1\uffff\3\2\1\uffff"+
            "\14\2\1\uffff\1\2\2\uffff\1\2\1\uffff\2\2\3\uffff\1\2\2\uffff"+
            "\1\2\2\uffff\2\2\10\uffff\1\2\1\37\2\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\3\2\3\uffff\1\74\3\uffff\2\2\1\uffff\1\2\2\uffff\1\73\1\2"+
            "\1\uffff\2\2\10\uffff\1\2\6\uffff\1\2\133\uffff\1\2\13\uffff"+
            "\1\2\10\uffff\1\2\25\uffff\1\2\6\uffff\1\2\35\uffff\1\2\11\uffff"+
            "\1\2\107\uffff\2\2\1\uffff\1\2\1\uffff\3\2\1\uffff\1\2\3\uffff"+
            "\1\75\3\uffff\1\2\1\uffff\1\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

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
        for (int i=0; i<numStates; i++) {
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
            return "184:5: ( ( tableProperties )=>props= tableProperties )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA26_59 = input.LA(1);

                         
                        int index26_59 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred5_FromClauseParser()) ) {s = 83;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index26_59);

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA26_60 = input.LA(1);

                         
                        int index26_60 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred5_FromClauseParser()) ) {s = 83;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index26_60);

                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA26_61 = input.LA(1);

                         
                        int index26_61 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred5_FromClauseParser()) ) {s = 83;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index26_61);

                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}

            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 26, _s, input);
            error(nvae);
            throw nvae;
        }

    }
    static final String DFA28_eotS =
        "\126\uffff";
    static final String DFA28_eofS =
        "\1\3\1\uffff\1\47\123\uffff";
    static final String DFA28_minS =
        "\1\12\1\uffff\1\12\33\uffff\1\7\33\uffff\34\0";
    static final String DFA28_maxS =
        "\1\u0133\1\uffff\1\u0133\33\uffff\1\u013b\33\uffff\34\0";
    static final String DFA28_acceptS =
        "\1\uffff\1\1\1\uffff\1\3\33\uffff\33\2\34\uffff";
    static final String DFA28_specialS =
        "\1\0\1\uffff\1\1\67\uffff\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12"+
        "\1\13\1\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\1\27"+
        "\1\30\1\31\1\32\1\33\1\34\1\35}>";
    static final String[] DFA28_transitionS = {
            "\1\3\17\uffff\1\2\11\uffff\1\1\20\uffff\1\3\15\uffff\1\3\30"+
            "\uffff\1\3\32\uffff\1\3\3\uffff\1\3\1\uffff\1\3\11\uffff\1\3"+
            "\3\uffff\1\3\7\uffff\1\3\2\uffff\2\3\2\uffff\1\3\11\uffff\1"+
            "\3\17\uffff\1\3\2\uffff\1\3\10\uffff\1\3\22\uffff\1\3\11\uffff"+
            "\1\3\11\uffff\1\3\13\uffff\1\3\32\uffff\1\3\21\uffff\1\3\1\uffff"+
            "\1\3\5\uffff\1\3\12\uffff\1\3",
            "",
            "\1\42\17\uffff\1\71\32\uffff\1\60\15\uffff\1\43\30\uffff\1"+
            "\61\32\uffff\1\46\3\uffff\1\55\1\uffff\1\56\11\uffff\1\41\3"+
            "\uffff\1\50\7\uffff\1\40\2\uffff\1\37\1\44\2\uffff\1\64\11\uffff"+
            "\1\52\17\uffff\1\67\2\uffff\1\57\10\uffff\1\70\22\uffff\1\53"+
            "\11\uffff\1\45\11\uffff\1\51\13\uffff\1\62\32\uffff\1\65\21"+
            "\uffff\1\54\1\uffff\1\63\5\uffff\1\36\12\uffff\1\66",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\104\5\uffff\1\110\4\uffff\1\107\7\uffff\1\116\3\120\2\125"+
            "\1\120\1\uffff\1\120\1\117\1\125\1\120\1\125\1\120\1\125\3\122"+
            "\1\125\2\120\1\125\1\120\1\114\1\113\1\120\1\uffff\4\120\1\uffff"+
            "\6\120\1\uffff\1\120\1\125\1\uffff\1\125\1\uffff\1\77\1\101"+
            "\1\125\1\120\1\uffff\1\120\1\76\3\120\1\125\2\120\1\125\3\120"+
            "\1\125\3\120\1\uffff\1\120\1\122\1\125\1\120\1\uffff\1\120\1"+
            "\uffff\1\120\1\uffff\1\120\1\123\2\120\1\uffff\1\125\1\112\1"+
            "\125\4\120\1\122\1\uffff\1\125\2\120\1\uffff\1\125\1\uffff\1"+
            "\120\3\125\1\uffff\3\120\1\121\1\120\2\125\2\120\1\125\3\120"+
            "\1\125\1\122\1\125\1\102\2\125\2\120\1\uffff\2\120\2\125\1\uffff"+
            "\1\125\3\120\1\125\5\120\1\uffff\1\121\6\120\1\uffff\1\120\1"+
            "\125\1\120\1\72\1\120\1\74\1\125\1\120\1\uffff\1\120\1\uffff"+
            "\3\125\2\120\1\uffff\2\120\1\uffff\1\125\2\120\1\125\1\120\2"+
            "\uffff\2\120\1\125\2\120\1\125\2\120\1\125\3\120\1\uffff\7\120"+
            "\1\125\1\120\1\125\3\120\3\125\3\120\1\uffff\4\120\1\125\5\120"+
            "\1\122\7\120\1\115\1\125\1\120\1\uffff\3\120\1\uffff\1\100\1"+
            "\120\1\125\2\120\1\uffff\1\125\1\111\1\125\1\120\1\uffff\1\120"+
            "\1\125\1\115\1\uffff\3\120\1\125\2\120\2\125\2\120\1\125\1\120"+
            "\1\uffff\1\120\2\uffff\1\120\1\uffff\1\125\1\120\3\uffff\1\124"+
            "\2\uffff\1\73\2\uffff\1\75\1\73\10\uffff\1\105\1\103\1\73\1"+
            "\106",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff"
    };

    static final short[] DFA28_eot = DFA.unpackEncodedString(DFA28_eotS);
    static final short[] DFA28_eof = DFA.unpackEncodedString(DFA28_eofS);
    static final char[] DFA28_min = DFA.unpackEncodedStringToUnsignedChars(DFA28_minS);
    static final char[] DFA28_max = DFA.unpackEncodedStringToUnsignedChars(DFA28_maxS);
    static final short[] DFA28_accept = DFA.unpackEncodedString(DFA28_acceptS);
    static final short[] DFA28_special = DFA.unpackEncodedString(DFA28_specialS);
    static final short[][] DFA28_transition;

    static {
        int numStates = DFA28_transitionS.length;
        DFA28_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA28_transition[i] = DFA.unpackEncodedString(DFA28_transitionS[i]);
        }
    }

    class DFA28 extends DFA {

        public DFA28(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 28;
            this.eot = DFA28_eot;
            this.eof = DFA28_eof;
            this.min = DFA28_min;
            this.max = DFA28_max;
            this.accept = DFA28_accept;
            this.special = DFA28_special;
            this.transition = DFA28_transition;
        }
        public String getDescription() {
            return "186:5: ( ( KW_AS )=> ( KW_AS alias= Identifier ) | ( Identifier )=> (alias= Identifier ) )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA28_0 = input.LA(1);

                         
                        int index28_0 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA28_0==KW_AS) && (synpred7_FromClauseParser())) {s = 1;}

                        else if ( (LA28_0==Identifier) ) {s = 2;}

                        else if ( (LA28_0==EOF||LA28_0==COMMA||LA28_0==KW_CLUSTER||LA28_0==KW_CROSS||LA28_0==KW_DISTRIBUTE||LA28_0==KW_FULL||LA28_0==KW_GROUP||LA28_0==KW_HAVING||LA28_0==KW_INNER||LA28_0==KW_INSERT||LA28_0==KW_JOIN||(LA28_0 >= KW_LATERAL && LA28_0 <= KW_LEFT)||LA28_0==KW_LIMIT||LA28_0==KW_MAP||LA28_0==KW_ON||LA28_0==KW_ORDER||LA28_0==KW_PARTITION||LA28_0==KW_REDUCE||LA28_0==KW_RIGHT||LA28_0==KW_SELECT||LA28_0==KW_SORT||LA28_0==KW_UNION||LA28_0==KW_WHERE||LA28_0==KW_WINDOW||LA28_0==LPAREN||LA28_0==RPAREN) ) {s = 3;}

                         
                        input.seek(index28_0);

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA28_2 = input.LA(1);

                         
                        int index28_2 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA28_2==LPAREN) ) {s = 30;}

                        else if ( (LA28_2==KW_LATERAL) && (synpred8_FromClauseParser())) {s = 31;}

                        else if ( (LA28_2==KW_JOIN) && (synpred8_FromClauseParser())) {s = 32;}

                        else if ( (LA28_2==KW_INNER) && (synpred8_FromClauseParser())) {s = 33;}

                        else if ( (LA28_2==COMMA) && (synpred8_FromClauseParser())) {s = 34;}

                        else if ( (LA28_2==KW_CROSS) && (synpred8_FromClauseParser())) {s = 35;}

                        else if ( (LA28_2==KW_LEFT) && (synpred8_FromClauseParser())) {s = 36;}

                        else if ( (LA28_2==KW_RIGHT) && (synpred8_FromClauseParser())) {s = 37;}

                        else if ( (LA28_2==KW_FULL) && (synpred8_FromClauseParser())) {s = 38;}

                        else if ( (LA28_2==EOF) && (synpred8_FromClauseParser())) {s = 39;}

                        else if ( (LA28_2==KW_INSERT) && (synpred8_FromClauseParser())) {s = 40;}

                        else if ( (LA28_2==KW_SELECT) && (synpred8_FromClauseParser())) {s = 41;}

                        else if ( (LA28_2==KW_MAP) && (synpred8_FromClauseParser())) {s = 42;}

                        else if ( (LA28_2==KW_REDUCE) && (synpred8_FromClauseParser())) {s = 43;}

                        else if ( (LA28_2==KW_WHERE) && (synpred8_FromClauseParser())) {s = 44;}

                        else if ( (LA28_2==KW_GROUP) && (synpred8_FromClauseParser())) {s = 45;}

                        else if ( (LA28_2==KW_HAVING) && (synpred8_FromClauseParser())) {s = 46;}

                        else if ( (LA28_2==KW_ORDER) && (synpred8_FromClauseParser())) {s = 47;}

                        else if ( (LA28_2==KW_CLUSTER) && (synpred8_FromClauseParser())) {s = 48;}

                        else if ( (LA28_2==KW_DISTRIBUTE) && (synpred8_FromClauseParser())) {s = 49;}

                        else if ( (LA28_2==KW_SORT) && (synpred8_FromClauseParser())) {s = 50;}

                        else if ( (LA28_2==KW_WINDOW) && (synpred8_FromClauseParser())) {s = 51;}

                        else if ( (LA28_2==KW_LIMIT) && (synpred8_FromClauseParser())) {s = 52;}

                        else if ( (LA28_2==KW_UNION) && (synpred8_FromClauseParser())) {s = 53;}

                        else if ( (LA28_2==RPAREN) && (synpred8_FromClauseParser())) {s = 54;}

                        else if ( (LA28_2==KW_ON) && (synpred8_FromClauseParser())) {s = 55;}

                        else if ( (LA28_2==KW_PARTITION) && (synpred8_FromClauseParser())) {s = 56;}

                        else if ( (LA28_2==Identifier) && (synpred8_FromClauseParser())) {s = 57;}

                         
                        input.seek(index28_2);

                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA28_58 = input.LA(1);

                         
                        int index28_58 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred8_FromClauseParser()) ) {s = 57;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index28_58);

                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA28_59 = input.LA(1);

                         
                        int index28_59 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred8_FromClauseParser()) ) {s = 57;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index28_59);

                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA28_60 = input.LA(1);

                         
                        int index28_60 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred8_FromClauseParser()) ) {s = 57;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index28_60);

                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA28_61 = input.LA(1);

                         
                        int index28_61 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred8_FromClauseParser()) ) {s = 57;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index28_61);

                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA28_62 = input.LA(1);

                         
                        int index28_62 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred8_FromClauseParser()) ) {s = 57;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index28_62);

                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA28_63 = input.LA(1);

                         
                        int index28_63 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred8_FromClauseParser()) ) {s = 57;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index28_63);

                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA28_64 = input.LA(1);

                         
                        int index28_64 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred8_FromClauseParser()) ) {s = 57;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index28_64);

                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA28_65 = input.LA(1);

                         
                        int index28_65 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred8_FromClauseParser()) ) {s = 57;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index28_65);

                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA28_66 = input.LA(1);

                         
                        int index28_66 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred8_FromClauseParser()) ) {s = 57;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index28_66);

                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA28_67 = input.LA(1);

                         
                        int index28_67 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred8_FromClauseParser()) ) {s = 57;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index28_67);

                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA28_68 = input.LA(1);

                         
                        int index28_68 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred8_FromClauseParser()) ) {s = 57;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index28_68);

                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA28_69 = input.LA(1);

                         
                        int index28_69 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred8_FromClauseParser()) ) {s = 57;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index28_69);

                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA28_70 = input.LA(1);

                         
                        int index28_70 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred8_FromClauseParser()) ) {s = 57;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index28_70);

                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA28_71 = input.LA(1);

                         
                        int index28_71 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred8_FromClauseParser()) ) {s = 57;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index28_71);

                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA28_72 = input.LA(1);

                         
                        int index28_72 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred8_FromClauseParser()) ) {s = 57;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index28_72);

                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA28_73 = input.LA(1);

                         
                        int index28_73 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred8_FromClauseParser()) ) {s = 57;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index28_73);

                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA28_74 = input.LA(1);

                         
                        int index28_74 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred8_FromClauseParser()) ) {s = 57;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index28_74);

                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA28_75 = input.LA(1);

                         
                        int index28_75 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred8_FromClauseParser()) ) {s = 57;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index28_75);

                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA28_76 = input.LA(1);

                         
                        int index28_76 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred8_FromClauseParser()) ) {s = 57;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index28_76);

                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA28_77 = input.LA(1);

                         
                        int index28_77 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred8_FromClauseParser()) ) {s = 57;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index28_77);

                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA28_78 = input.LA(1);

                         
                        int index28_78 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred8_FromClauseParser()) ) {s = 57;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index28_78);

                        if ( s>=0 ) return s;
                        break;
                    case 23 : 
                        int LA28_79 = input.LA(1);

                         
                        int index28_79 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred8_FromClauseParser()) ) {s = 57;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index28_79);

                        if ( s>=0 ) return s;
                        break;
                    case 24 : 
                        int LA28_80 = input.LA(1);

                         
                        int index28_80 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred8_FromClauseParser()) ) {s = 57;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index28_80);

                        if ( s>=0 ) return s;
                        break;
                    case 25 : 
                        int LA28_81 = input.LA(1);

                         
                        int index28_81 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred8_FromClauseParser()) ) {s = 57;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index28_81);

                        if ( s>=0 ) return s;
                        break;
                    case 26 : 
                        int LA28_82 = input.LA(1);

                         
                        int index28_82 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred8_FromClauseParser()) ) {s = 57;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index28_82);

                        if ( s>=0 ) return s;
                        break;
                    case 27 : 
                        int LA28_83 = input.LA(1);

                         
                        int index28_83 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred8_FromClauseParser()) ) {s = 57;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index28_83);

                        if ( s>=0 ) return s;
                        break;
                    case 28 : 
                        int LA28_84 = input.LA(1);

                         
                        int index28_84 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred8_FromClauseParser()) ) {s = 57;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index28_84);

                        if ( s>=0 ) return s;
                        break;
                    case 29 : 
                        int LA28_85 = input.LA(1);

                         
                        int index28_85 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred8_FromClauseParser()) ) {s = 57;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index28_85);

                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}

            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 28, _s, input);
            error(nvae);
            throw nvae;
        }

    }
    static final String DFA29_eotS =
        "\u00e2\uffff";
    static final String DFA29_eofS =
        "\1\uffff\3\5\u00de\uffff";
    static final String DFA29_minS =
        "\1\32\3\12\1\32\105\uffff\1\32\105\uffff\1\32\121\uffff";
    static final String DFA29_maxS =
        "\1\u0124\3\u0133\1\u0137\105\uffff\1\u0137\105\uffff\1\u0137\121"+
        "\uffff";
    static final String DFA29_acceptS =
        "\5\uffff\1\2\u00d1\uffff\1\1\12\uffff";
    static final String DFA29_specialS =
        "\u00e2\uffff}>";
    static final String[] DFA29_transitionS = {
            "\1\1\3\2\2\3\1\2\1\uffff\1\2\2\3\1\2\1\3\1\2\5\3\2\2\1\3\1\2"+
            "\2\uffff\1\2\1\uffff\4\2\1\uffff\6\2\1\uffff\1\2\1\3\1\uffff"+
            "\1\3\1\uffff\3\3\1\2\1\uffff\1\2\1\3\3\2\1\3\2\2\1\3\3\2\1\3"+
            "\3\2\1\uffff\1\2\2\3\1\2\1\uffff\1\2\1\uffff\1\2\1\uffff\1\2"+
            "\1\3\2\2\1\uffff\3\3\4\2\1\3\1\uffff\1\3\2\2\1\uffff\1\3\1\uffff"+
            "\1\2\3\3\1\uffff\3\2\1\uffff\1\2\2\3\2\2\1\3\3\2\3\3\1\uffff"+
            "\2\3\2\2\1\uffff\2\2\2\3\1\uffff\1\3\3\2\1\3\5\2\2\uffff\6\2"+
            "\1\uffff\1\2\1\3\1\2\1\uffff\1\2\2\3\1\2\1\uffff\1\2\1\uffff"+
            "\3\3\2\2\1\uffff\2\2\1\uffff\1\3\2\2\1\3\1\2\2\uffff\2\2\1\3"+
            "\2\2\1\3\2\2\1\3\3\2\1\uffff\7\2\1\3\1\2\1\3\3\2\3\3\3\2\1\uffff"+
            "\4\2\1\3\5\2\1\3\10\2\1\3\1\2\1\uffff\3\2\1\uffff\1\3\1\2\1"+
            "\3\2\2\1\uffff\3\3\1\2\1\uffff\1\2\1\3\1\2\1\uffff\3\2\1\3\2"+
            "\2\2\3\2\2\1\3\1\2\1\uffff\1\2\2\uffff\1\2\1\uffff\1\3\1\2",
            "\1\5\6\uffff\1\4\10\uffff\2\5\6\uffff\1\5\1\uffff\1\5\16\uffff"+
            "\1\5\1\uffff\2\5\3\uffff\3\5\1\uffff\2\5\3\uffff\1\5\26\uffff"+
            "\1\5\1\uffff\1\5\1\uffff\1\5\2\uffff\1\5\2\uffff\2\5\15\uffff"+
            "\1\5\2\uffff\2\5\3\uffff\1\5\1\uffff\1\5\3\uffff\1\5\2\uffff"+
            "\1\5\2\uffff\1\5\3\uffff\1\5\3\uffff\1\5\3\uffff\1\5\2\uffff"+
            "\2\5\1\uffff\2\5\3\uffff\1\5\5\uffff\1\5\12\uffff\1\5\4\uffff"+
            "\1\5\2\uffff\1\5\10\uffff\2\5\11\uffff\1\5\4\uffff\1\5\2\uffff"+
            "\1\5\2\uffff\1\5\1\uffff\1\5\4\uffff\1\5\4\uffff\1\5\4\uffff"+
            "\1\5\4\uffff\1\5\1\uffff\1\5\2\uffff\1\5\1\uffff\1\5\3\uffff"+
            "\1\5\5\uffff\2\5\5\uffff\2\5\5\uffff\1\5\2\uffff\1\5\3\uffff"+
            "\1\5\1\uffff\1\5\6\uffff\1\5\4\uffff\1\5\1\uffff\2\5\4\uffff"+
            "\1\5\12\uffff\1\5",
            "\1\5\6\uffff\1\112\10\uffff\2\5\6\uffff\1\5\1\uffff\1\5\16"+
            "\uffff\1\5\1\uffff\2\5\3\uffff\3\5\1\uffff\2\5\3\uffff\1\5\26"+
            "\uffff\1\5\1\uffff\1\5\1\uffff\1\5\2\uffff\1\5\2\uffff\2\5\15"+
            "\uffff\1\5\2\uffff\2\5\3\uffff\1\5\1\uffff\1\5\3\uffff\1\5\2"+
            "\uffff\1\5\2\uffff\1\5\3\uffff\1\5\3\uffff\1\5\3\uffff\1\5\2"+
            "\uffff\2\5\1\uffff\2\5\3\uffff\1\5\5\uffff\1\5\12\uffff\1\5"+
            "\4\uffff\1\5\2\uffff\1\5\10\uffff\2\5\11\uffff\1\5\4\uffff\1"+
            "\5\2\uffff\1\5\2\uffff\1\5\1\uffff\1\5\4\uffff\1\5\4\uffff\1"+
            "\5\4\uffff\1\5\4\uffff\1\5\1\uffff\1\5\2\uffff\1\5\1\uffff\1"+
            "\5\3\uffff\1\5\5\uffff\2\5\5\uffff\2\5\5\uffff\1\5\2\uffff\1"+
            "\5\3\uffff\1\5\1\uffff\1\5\6\uffff\1\5\4\uffff\1\5\1\uffff\2"+
            "\5\4\uffff\1\5\12\uffff\1\5",
            "\1\5\6\uffff\1\u0090\10\uffff\2\5\6\uffff\1\5\1\uffff\1\5\16"+
            "\uffff\1\5\1\uffff\2\5\3\uffff\3\5\1\uffff\2\5\3\uffff\1\5\26"+
            "\uffff\1\5\1\uffff\1\5\1\uffff\1\5\2\uffff\1\5\2\uffff\2\5\15"+
            "\uffff\1\5\2\uffff\2\5\3\uffff\1\5\1\uffff\1\5\3\uffff\1\5\2"+
            "\uffff\1\5\2\uffff\1\5\3\uffff\1\5\3\uffff\1\5\3\uffff\1\5\2"+
            "\uffff\2\5\1\uffff\2\5\3\uffff\1\5\5\uffff\1\5\12\uffff\1\5"+
            "\4\uffff\1\5\2\uffff\1\5\10\uffff\2\5\11\uffff\1\5\4\uffff\1"+
            "\5\2\uffff\1\5\2\uffff\1\5\1\uffff\1\5\4\uffff\1\5\4\uffff\1"+
            "\5\4\uffff\1\5\4\uffff\1\5\1\uffff\1\5\2\uffff\1\5\1\uffff\1"+
            "\5\3\uffff\1\5\5\uffff\2\5\5\uffff\2\5\5\uffff\1\5\2\uffff\1"+
            "\5\3\uffff\1\5\1\uffff\1\5\6\uffff\1\5\4\uffff\1\5\1\uffff\2"+
            "\5\4\uffff\1\5\12\uffff\1\5",
            "\7\u00d7\1\uffff\17\u00d7\2\uffff\1\u00d7\1\uffff\4\u00d7\1"+
            "\uffff\6\u00d7\1\uffff\2\u00d7\1\uffff\1\u00d7\1\uffff\4\u00d7"+
            "\1\uffff\20\u00d7\1\uffff\4\u00d7\1\uffff\1\u00d7\1\uffff\1"+
            "\u00d7\1\uffff\4\u00d7\1\uffff\10\u00d7\1\uffff\3\u00d7\1\uffff"+
            "\1\u00d7\1\uffff\4\u00d7\1\uffff\3\u00d7\1\uffff\14\u00d7\1"+
            "\uffff\4\u00d7\1\uffff\4\u00d7\1\uffff\12\u00d7\2\uffff\6\u00d7"+
            "\1\uffff\3\u00d7\1\uffff\4\u00d7\1\uffff\1\u00d7\1\uffff\5\u00d7"+
            "\1\uffff\2\u00d7\1\uffff\5\u00d7\2\uffff\14\u00d7\1\uffff\23"+
            "\u00d7\1\uffff\25\u00d7\1\uffff\3\u00d7\1\uffff\5\u00d7\1\uffff"+
            "\4\u00d7\1\uffff\3\u00d7\1\uffff\14\u00d7\1\uffff\1\u00d7\2"+
            "\uffff\1\u00d7\1\uffff\2\u00d7\22\uffff\1\5",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\7\u00d7\1\uffff\17\u00d7\2\uffff\1\u00d7\1\uffff\4\u00d7\1"+
            "\uffff\6\u00d7\1\uffff\2\u00d7\1\uffff\1\u00d7\1\uffff\4\u00d7"+
            "\1\uffff\20\u00d7\1\uffff\4\u00d7\1\uffff\1\u00d7\1\uffff\1"+
            "\u00d7\1\uffff\4\u00d7\1\uffff\10\u00d7\1\uffff\3\u00d7\1\uffff"+
            "\1\u00d7\1\uffff\4\u00d7\1\uffff\3\u00d7\1\uffff\14\u00d7\1"+
            "\uffff\4\u00d7\1\uffff\4\u00d7\1\uffff\12\u00d7\2\uffff\6\u00d7"+
            "\1\uffff\3\u00d7\1\uffff\4\u00d7\1\uffff\1\u00d7\1\uffff\5\u00d7"+
            "\1\uffff\2\u00d7\1\uffff\5\u00d7\2\uffff\14\u00d7\1\uffff\23"+
            "\u00d7\1\uffff\25\u00d7\1\uffff\3\u00d7\1\uffff\5\u00d7\1\uffff"+
            "\4\u00d7\1\uffff\3\u00d7\1\uffff\14\u00d7\1\uffff\1\u00d7\2"+
            "\uffff\1\u00d7\1\uffff\2\u00d7\22\uffff\1\5",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\7\u00d7\1\uffff\17\u00d7\2\uffff\1\u00d7\1\uffff\4\u00d7\1"+
            "\uffff\6\u00d7\1\uffff\2\u00d7\1\uffff\1\u00d7\1\uffff\4\u00d7"+
            "\1\uffff\20\u00d7\1\uffff\4\u00d7\1\uffff\1\u00d7\1\uffff\1"+
            "\u00d7\1\uffff\4\u00d7\1\uffff\10\u00d7\1\uffff\3\u00d7\1\uffff"+
            "\1\u00d7\1\uffff\4\u00d7\1\uffff\3\u00d7\1\uffff\14\u00d7\1"+
            "\uffff\4\u00d7\1\uffff\4\u00d7\1\uffff\12\u00d7\2\uffff\6\u00d7"+
            "\1\uffff\3\u00d7\1\uffff\4\u00d7\1\uffff\1\u00d7\1\uffff\5\u00d7"+
            "\1\uffff\2\u00d7\1\uffff\5\u00d7\2\uffff\14\u00d7\1\uffff\23"+
            "\u00d7\1\uffff\25\u00d7\1\uffff\3\u00d7\1\uffff\5\u00d7\1\uffff"+
            "\4\u00d7\1\uffff\3\u00d7\1\uffff\14\u00d7\1\uffff\1\u00d7\2"+
            "\uffff\1\u00d7\1\uffff\2\u00d7\22\uffff\1\5",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

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
        for (int i=0; i<numStates; i++) {
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
            return "192:1: tableName : (db= identifier DOT tab= identifier -> ^( TOK_TABNAME $db $tab) |tab= identifier -> ^( TOK_TABNAME $tab) );";
        }
    }
    static final String DFA31_eotS =
        "\u01d7\uffff";
    static final String DFA31_eofS =
        "\1\uffff\1\2\3\uffff\4\35\1\uffff\1\35\2\uffff\3\35\1\uffff\1\35"+
        "\4\uffff\1\35\1\uffff\4\35\2\uffff\1\35\u01b8\uffff";
    static final String DFA31_minS =
        "\1\32\1\12\3\uffff\4\12\1\uffff\1\12\2\uffff\3\12\1\uffff\1\12\4"+
        "\uffff\1\12\1\uffff\4\12\2\uffff\1\12\4\uffff\1\0\125\uffff\1\0"+
        "\35\uffff\1\0\34\uffff\1\0\34\uffff\1\0\176\uffff\3\0\u0084\uffff";
    static final String DFA31_maxS =
        "\1\u0124\1\u0133\3\uffff\4\u0133\1\uffff\1\u0133\2\uffff\3\u0133"+
        "\1\uffff\1\u0133\4\uffff\1\u0133\1\uffff\4\u0133\2\uffff\1\u0133"+
        "\4\uffff\1\0\125\uffff\1\0\35\uffff\1\0\34\uffff\1\0\34\uffff\1"+
        "\0\176\uffff\3\0\u0084\uffff";
    static final String DFA31_acceptS =
        "\2\uffff\1\2\32\uffff\1\1\u01b9\uffff";
    static final String DFA31_specialS =
        "\43\uffff\1\0\125\uffff\1\1\35\uffff\1\2\34\uffff\1\3\34\uffff\1"+
        "\4\176\uffff\1\5\1\6\1\7\u0084\uffff}>";
    static final String[] DFA31_transitionS = {
            "\7\2\1\uffff\2\2\1\1\14\2\2\uffff\1\2\1\uffff\4\2\1\uffff\6"+
            "\2\1\uffff\2\2\1\uffff\1\2\1\uffff\4\2\1\uffff\20\2\1\uffff"+
            "\4\2\1\uffff\1\2\1\uffff\1\2\1\uffff\4\2\1\uffff\10\2\1\uffff"+
            "\3\2\1\uffff\1\2\1\uffff\4\2\1\uffff\3\2\1\uffff\14\2\1\uffff"+
            "\4\2\1\uffff\4\2\1\uffff\12\2\2\uffff\6\2\1\uffff\3\2\1\uffff"+
            "\4\2\1\uffff\1\2\1\uffff\5\2\1\uffff\2\2\1\uffff\5\2\2\uffff"+
            "\14\2\1\uffff\23\2\1\uffff\25\2\1\uffff\3\2\1\uffff\5\2\1\uffff"+
            "\4\2\1\uffff\3\2\1\uffff\14\2\1\uffff\1\2\2\uffff\1\2\1\uffff"+
            "\2\2",
            "\1\2\17\uffff\1\5\6\35\1\uffff\17\35\2\uffff\1\35\1\uffff\1"+
            "\6\3\35\1\uffff\6\35\1\uffff\2\35\1\2\1\35\1\uffff\4\35\1\uffff"+
            "\20\35\1\uffff\1\31\3\35\1\uffff\1\35\1\uffff\1\35\1\uffff\4"+
            "\35\1\uffff\10\35\1\uffff\3\35\1\uffff\1\16\1\uffff\2\35\1\21"+
            "\1\35\1\2\3\35\1\uffff\5\35\1\10\3\35\1\17\2\35\1\uffff\4\35"+
            "\1\2\2\35\1\7\1\12\1\uffff\1\35\1\33\10\35\1\uffff\1\2\6\35"+
            "\1\uffff\3\35\1\uffff\4\35\1\2\1\35\1\uffff\1\26\4\35\1\uffff"+
            "\2\35\1\uffff\1\36\4\35\2\uffff\14\35\1\2\11\35\1\15\11\35\1"+
            "\2\13\35\1\32\11\35\1\uffff\3\35\1\uffff\5\35\1\uffff\4\35\1"+
            "\uffff\1\35\1\30\1\35\1\uffff\14\35\1\uffff\1\35\1\uffff\1\2"+
            "\1\35\1\2\2\35\3\uffff\1\2\12\uffff\1\2",
            "",
            "",
            "",
            "\1\35\17\uffff\1\35\32\uffff\1\35\15\uffff\1\35\30\uffff\1"+
            "\35\32\uffff\1\35\3\uffff\1\35\1\uffff\1\35\11\uffff\1\35\3"+
            "\uffff\1\35\7\uffff\1\35\2\uffff\2\35\2\uffff\1\35\11\uffff"+
            "\1\35\17\uffff\1\35\2\uffff\1\35\10\uffff\1\35\22\uffff\1\35"+
            "\11\uffff\1\35\11\uffff\1\35\13\uffff\1\35\32\uffff\1\35\21"+
            "\uffff\1\35\1\uffff\1\35\5\uffff\1\43\12\uffff\1\35",
            "\1\35\17\uffff\1\35\24\uffff\1\2\5\uffff\1\35\15\uffff\1\35"+
            "\30\uffff\1\35\32\uffff\1\35\3\uffff\1\35\1\uffff\1\35\11\uffff"+
            "\1\35\3\uffff\1\35\7\uffff\1\35\2\uffff\2\35\2\uffff\1\35\11"+
            "\uffff\1\35\17\uffff\1\35\2\uffff\1\35\10\uffff\1\35\22\uffff"+
            "\1\35\11\uffff\1\35\11\uffff\1\35\13\uffff\1\35\32\uffff\1\35"+
            "\21\uffff\1\35\1\uffff\1\35\5\uffff\1\35\12\uffff\1\35",
            "\1\35\17\uffff\1\35\32\uffff\1\35\15\uffff\1\35\30\uffff\1"+
            "\35\32\uffff\1\35\3\uffff\1\35\1\uffff\1\35\11\uffff\1\35\3"+
            "\uffff\1\35\7\uffff\1\35\2\uffff\2\35\2\uffff\1\35\11\uffff"+
            "\1\35\17\uffff\1\35\2\uffff\1\35\10\uffff\1\35\22\uffff\1\35"+
            "\11\uffff\1\35\11\uffff\1\35\13\uffff\1\35\32\uffff\1\35\17"+
            "\uffff\1\2\1\uffff\1\35\1\uffff\1\35\5\uffff\1\35\12\uffff\1"+
            "\35",
            "\1\35\17\uffff\1\35\32\uffff\1\35\15\uffff\1\35\30\uffff\1"+
            "\35\32\uffff\1\35\3\uffff\1\35\1\uffff\1\35\11\uffff\1\35\3"+
            "\uffff\1\35\7\uffff\1\171\2\uffff\2\35\2\uffff\1\35\11\uffff"+
            "\1\35\17\uffff\1\35\2\uffff\1\35\10\uffff\1\35\22\uffff\1\35"+
            "\11\uffff\1\35\11\uffff\1\35\13\uffff\1\35\32\uffff\1\35\21"+
            "\uffff\1\35\1\uffff\1\35\5\uffff\1\35\12\uffff\1\35",
            "",
            "\1\35\17\uffff\1\35\32\uffff\1\35\15\uffff\1\35\30\uffff\1"+
            "\35\32\uffff\1\35\3\uffff\1\35\1\uffff\1\35\11\uffff\1\35\3"+
            "\uffff\1\35\7\uffff\1\u0097\2\uffff\2\35\2\uffff\1\35\11\uffff"+
            "\1\35\17\uffff\1\35\2\uffff\1\35\1\uffff\1\2\6\uffff\1\35\22"+
            "\uffff\1\35\11\uffff\1\35\11\uffff\1\35\1\2\12\uffff\1\35\32"+
            "\uffff\1\35\21\uffff\1\35\1\uffff\1\35\5\uffff\1\35\12\uffff"+
            "\1\35",
            "",
            "",
            "\1\35\17\uffff\1\35\32\uffff\1\35\15\uffff\1\35\30\uffff\1"+
            "\35\32\uffff\1\35\3\uffff\1\35\1\uffff\1\35\11\uffff\1\35\3"+
            "\uffff\1\35\7\uffff\1\u00b4\2\uffff\2\35\2\uffff\1\35\11\uffff"+
            "\1\35\17\uffff\1\35\2\uffff\1\35\1\uffff\1\2\6\uffff\1\35\22"+
            "\uffff\1\35\11\uffff\1\35\11\uffff\1\35\13\uffff\1\35\32\uffff"+
            "\1\35\21\uffff\1\35\1\uffff\1\35\5\uffff\1\35\12\uffff\1\35",
            "\1\35\17\uffff\1\35\32\uffff\1\35\15\uffff\1\35\30\uffff\1"+
            "\35\32\uffff\1\35\3\uffff\1\35\1\uffff\1\35\11\uffff\1\35\3"+
            "\uffff\1\35\7\uffff\1\u00d1\2\uffff\2\35\2\uffff\1\35\11\uffff"+
            "\1\35\17\uffff\1\35\2\uffff\1\35\1\uffff\1\2\6\uffff\1\35\22"+
            "\uffff\1\35\11\uffff\1\35\11\uffff\1\35\13\uffff\1\35\32\uffff"+
            "\1\35\21\uffff\1\35\1\uffff\1\35\5\uffff\1\35\12\uffff\1\35",
            "\1\35\17\uffff\1\35\32\uffff\1\35\15\uffff\1\35\30\uffff\1"+
            "\35\32\uffff\1\35\3\uffff\1\35\1\uffff\1\35\11\uffff\1\35\3"+
            "\uffff\1\35\3\uffff\1\2\3\uffff\1\35\2\uffff\2\35\2\uffff\1"+
            "\35\11\uffff\1\35\17\uffff\1\35\2\uffff\1\35\5\uffff\1\2\2\uffff"+
            "\1\35\22\uffff\1\35\11\uffff\1\35\11\uffff\1\35\13\uffff\1\35"+
            "\32\uffff\1\35\21\uffff\1\35\1\uffff\1\35\5\uffff\1\35\12\uffff"+
            "\1\35",
            "",
            "\1\35\17\uffff\1\35\24\uffff\1\2\5\uffff\1\35\15\uffff\1\35"+
            "\30\uffff\1\35\32\uffff\1\35\3\uffff\1\35\1\uffff\1\35\11\uffff"+
            "\1\35\3\uffff\1\35\7\uffff\1\35\2\uffff\2\35\2\uffff\1\35\11"+
            "\uffff\1\35\17\uffff\1\35\2\uffff\1\35\10\uffff\1\35\22\uffff"+
            "\1\35\11\uffff\1\35\11\uffff\1\35\13\uffff\1\35\32\uffff\1\35"+
            "\21\uffff\1\35\1\uffff\1\35\5\uffff\1\35\12\uffff\1\35",
            "",
            "",
            "",
            "",
            "\1\35\17\uffff\1\35\24\uffff\1\2\5\uffff\1\35\15\uffff\1\35"+
            "\30\uffff\1\35\32\uffff\1\35\3\uffff\1\35\1\uffff\1\35\11\uffff"+
            "\1\35\3\uffff\1\35\7\uffff\1\35\2\uffff\2\35\2\uffff\1\35\11"+
            "\uffff\1\35\17\uffff\1\35\2\uffff\1\35\10\uffff\1\35\22\uffff"+
            "\1\35\11\uffff\1\35\11\uffff\1\35\13\uffff\1\35\32\uffff\1\35"+
            "\21\uffff\1\35\1\uffff\1\35\5\uffff\1\35\12\uffff\1\35",
            "",
            "\1\35\17\uffff\1\35\3\uffff\1\2\26\uffff\1\35\15\uffff\1\35"+
            "\27\uffff\1\2\1\35\32\uffff\1\35\3\uffff\1\35\1\uffff\1\35\11"+
            "\uffff\1\35\3\uffff\1\35\7\uffff\1\35\2\uffff\2\35\2\uffff\1"+
            "\35\11\uffff\1\u0151\17\uffff\1\35\2\uffff\1\35\10\uffff\1\35"+
            "\22\uffff\1\u0152\11\uffff\1\35\11\uffff\1\u0150\13\uffff\1"+
            "\35\32\uffff\1\35\21\uffff\1\35\1\uffff\1\35\5\uffff\1\35\12"+
            "\uffff\1\35",
            "\1\35\17\uffff\1\35\24\uffff\1\2\5\uffff\1\35\15\uffff\1\35"+
            "\30\uffff\1\35\32\uffff\1\35\3\uffff\1\35\1\uffff\1\35\11\uffff"+
            "\1\35\3\uffff\1\35\7\uffff\1\35\2\uffff\2\35\2\uffff\1\35\11"+
            "\uffff\1\35\17\uffff\1\35\2\uffff\1\35\10\uffff\1\35\22\uffff"+
            "\1\35\11\uffff\1\35\11\uffff\1\35\13\uffff\1\35\32\uffff\1\35"+
            "\21\uffff\1\35\1\uffff\1\35\5\uffff\1\35\12\uffff\1\35",
            "\1\35\17\uffff\1\35\24\uffff\1\2\5\uffff\1\35\15\uffff\1\35"+
            "\30\uffff\1\35\32\uffff\1\35\3\uffff\1\35\1\uffff\1\35\11\uffff"+
            "\1\35\3\uffff\1\35\7\uffff\1\35\2\uffff\2\35\2\uffff\1\35\11"+
            "\uffff\1\35\17\uffff\1\35\2\uffff\1\35\10\uffff\1\35\22\uffff"+
            "\1\35\11\uffff\1\35\11\uffff\1\35\13\uffff\1\35\32\uffff\1\35"+
            "\21\uffff\1\35\1\uffff\1\35\5\uffff\1\35\12\uffff\1\35",
            "\1\35\17\uffff\1\35\32\uffff\1\35\15\uffff\1\35\30\uffff\1"+
            "\35\32\uffff\1\35\3\uffff\1\35\1\uffff\1\35\11\uffff\1\35\3"+
            "\uffff\1\35\7\uffff\1\35\2\uffff\2\35\2\uffff\1\35\11\uffff"+
            "\1\35\17\uffff\1\35\2\uffff\1\35\10\uffff\1\35\22\uffff\1\35"+
            "\11\uffff\1\35\11\uffff\1\35\13\uffff\1\35\32\uffff\1\35\21"+
            "\uffff\1\35\1\uffff\1\35\5\uffff\1\35\5\uffff\1\2\4\uffff\1"+
            "\35",
            "",
            "",
            "\1\35\17\uffff\1\35\24\uffff\1\2\5\uffff\1\35\15\uffff\1\35"+
            "\30\uffff\1\35\32\uffff\1\35\3\uffff\1\35\1\uffff\1\35\11\uffff"+
            "\1\35\3\uffff\1\35\7\uffff\1\35\2\uffff\2\35\2\uffff\1\35\11"+
            "\uffff\1\35\17\uffff\1\35\2\uffff\1\35\10\uffff\1\35\22\uffff"+
            "\1\35\11\uffff\1\35\11\uffff\1\35\13\uffff\1\35\32\uffff\1\35"+
            "\21\uffff\1\35\1\uffff\1\35\5\uffff\1\35\12\uffff\1\35",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA31_eot = DFA.unpackEncodedString(DFA31_eotS);
    static final short[] DFA31_eof = DFA.unpackEncodedString(DFA31_eofS);
    static final char[] DFA31_min = DFA.unpackEncodedStringToUnsignedChars(DFA31_minS);
    static final char[] DFA31_max = DFA.unpackEncodedStringToUnsignedChars(DFA31_maxS);
    static final short[] DFA31_accept = DFA.unpackEncodedString(DFA31_acceptS);
    static final short[] DFA31_special = DFA.unpackEncodedString(DFA31_specialS);
    static final short[][] DFA31_transition;

    static {
        int numStates = DFA31_transitionS.length;
        DFA31_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA31_transition[i] = DFA.unpackEncodedString(DFA31_transitionS[i]);
        }
    }

    class DFA31 extends DFA {

        public DFA31(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 31;
            this.eot = DFA31_eot;
            this.eof = DFA31_eof;
            this.min = DFA31_min;
            this.max = DFA31_max;
            this.accept = DFA31_accept;
            this.special = DFA31_special;
            this.transition = DFA31_transition;
        }
        public String getDescription() {
            return "215:51: ( KW_AS )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA31_35 = input.LA(1);

                         
                        int index31_35 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 29;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                         
                        input.seek(index31_35);

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA31_121 = input.LA(1);

                         
                        int index31_121 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 29;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                         
                        input.seek(index31_121);

                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA31_151 = input.LA(1);

                         
                        int index31_151 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 29;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                         
                        input.seek(index31_151);

                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA31_180 = input.LA(1);

                         
                        int index31_180 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 29;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                         
                        input.seek(index31_180);

                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA31_209 = input.LA(1);

                         
                        int index31_209 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 29;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                         
                        input.seek(index31_209);

                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA31_336 = input.LA(1);

                         
                        int index31_336 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 29;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                         
                        input.seek(index31_336);

                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA31_337 = input.LA(1);

                         
                        int index31_337 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 29;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                         
                        input.seek(index31_337);

                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA31_338 = input.LA(1);

                         
                        int index31_338 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 29;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                         
                        input.seek(index31_338);

                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}

            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 31, _s, input);
            error(nvae);
            throw nvae;
        }

    }
    static final String DFA39_eotS =
        "\125\uffff";
    static final String DFA39_eofS =
        "\1\2\1\46\123\uffff";
    static final String DFA39_minS =
        "\2\12\33\uffff\1\7\33\uffff\34\0";
    static final String DFA39_maxS =
        "\2\u0133\33\uffff\1\u013b\33\uffff\34\0";
    static final String DFA39_acceptS =
        "\2\uffff\1\2\33\uffff\33\1\34\uffff";
    static final String DFA39_specialS =
        "\1\uffff\1\0\67\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12"+
        "\1\13\1\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\1\27"+
        "\1\30\1\31\1\32\1\33\1\34}>";
    static final String[] DFA39_transitionS = {
            "\1\2\17\uffff\1\1\32\uffff\1\2\15\uffff\1\2\30\uffff\1\2\32"+
            "\uffff\1\2\3\uffff\1\2\1\uffff\1\2\11\uffff\1\2\3\uffff\1\2"+
            "\7\uffff\1\2\2\uffff\2\2\2\uffff\1\2\11\uffff\1\2\17\uffff\1"+
            "\2\2\uffff\1\2\10\uffff\1\2\22\uffff\1\2\11\uffff\1\2\11\uffff"+
            "\1\2\13\uffff\1\2\32\uffff\1\2\21\uffff\1\2\1\uffff\1\2\5\uffff"+
            "\1\2\12\uffff\1\2",
            "\1\41\17\uffff\1\70\32\uffff\1\57\15\uffff\1\42\30\uffff\1"+
            "\60\32\uffff\1\45\3\uffff\1\54\1\uffff\1\55\11\uffff\1\40\3"+
            "\uffff\1\47\7\uffff\1\37\2\uffff\1\36\1\43\2\uffff\1\63\11\uffff"+
            "\1\51\17\uffff\1\66\2\uffff\1\56\10\uffff\1\67\22\uffff\1\52"+
            "\11\uffff\1\44\11\uffff\1\50\13\uffff\1\61\32\uffff\1\64\21"+
            "\uffff\1\53\1\uffff\1\62\5\uffff\1\35\12\uffff\1\65",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\103\5\uffff\1\107\4\uffff\1\106\7\uffff\1\115\3\117\2\124"+
            "\1\117\1\uffff\1\117\1\116\1\124\1\117\1\124\1\117\1\124\3\121"+
            "\1\124\2\117\1\124\1\117\1\113\1\112\1\117\1\uffff\4\117\1\uffff"+
            "\6\117\1\uffff\1\117\1\124\1\uffff\1\124\1\uffff\1\76\1\100"+
            "\1\124\1\117\1\uffff\1\117\1\75\3\117\1\124\2\117\1\124\3\117"+
            "\1\124\3\117\1\uffff\1\117\1\121\1\124\1\117\1\uffff\1\117\1"+
            "\uffff\1\117\1\uffff\1\117\1\122\2\117\1\uffff\1\124\1\111\1"+
            "\124\4\117\1\121\1\uffff\1\124\2\117\1\uffff\1\124\1\uffff\1"+
            "\117\3\124\1\uffff\3\117\1\120\1\117\2\124\2\117\1\124\3\117"+
            "\1\124\1\121\1\124\1\101\2\124\2\117\1\uffff\2\117\2\124\1\uffff"+
            "\1\124\3\117\1\124\5\117\1\uffff\1\120\6\117\1\uffff\1\117\1"+
            "\124\1\117\1\71\1\117\1\73\1\124\1\117\1\uffff\1\117\1\uffff"+
            "\3\124\2\117\1\uffff\2\117\1\uffff\1\124\2\117\1\124\1\117\2"+
            "\uffff\2\117\1\124\2\117\1\124\2\117\1\124\3\117\1\uffff\7\117"+
            "\1\124\1\117\1\124\3\117\3\124\3\117\1\uffff\4\117\1\124\5\117"+
            "\1\121\7\117\1\114\1\124\1\117\1\uffff\3\117\1\uffff\1\77\1"+
            "\117\1\124\2\117\1\uffff\1\124\1\110\1\124\1\117\1\uffff\1\117"+
            "\1\124\1\114\1\uffff\3\117\1\124\2\117\2\124\2\117\1\124\1\117"+
            "\1\uffff\1\117\2\uffff\1\117\1\uffff\1\124\1\117\3\uffff\1\123"+
            "\2\uffff\1\72\2\uffff\1\74\1\72\10\uffff\1\104\1\102\1\72\1"+
            "\105",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff"
    };

    static final short[] DFA39_eot = DFA.unpackEncodedString(DFA39_eotS);
    static final short[] DFA39_eof = DFA.unpackEncodedString(DFA39_eofS);
    static final char[] DFA39_min = DFA.unpackEncodedStringToUnsignedChars(DFA39_minS);
    static final char[] DFA39_max = DFA.unpackEncodedStringToUnsignedChars(DFA39_maxS);
    static final short[] DFA39_accept = DFA.unpackEncodedString(DFA39_acceptS);
    static final short[] DFA39_special = DFA.unpackEncodedString(DFA39_specialS);
    static final short[][] DFA39_transition;

    static {
        int numStates = DFA39_transitionS.length;
        DFA39_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA39_transition[i] = DFA.unpackEncodedString(DFA39_transitionS[i]);
        }
    }

    class DFA39 extends DFA {

        public DFA39(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 39;
            this.eot = DFA39_eot;
            this.eof = DFA39_eof;
            this.min = DFA39_min;
            this.max = DFA39_max;
            this.accept = DFA39_accept;
            this.special = DFA39_special;
            this.transition = DFA39_transition;
        }
        public String getDescription() {
            return "246:27: ( ( Identifier )=>alias= Identifier )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA39_1 = input.LA(1);

                         
                        int index39_1 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA39_1==LPAREN) ) {s = 29;}

                        else if ( (LA39_1==KW_LATERAL) && (synpred12_FromClauseParser())) {s = 30;}

                        else if ( (LA39_1==KW_JOIN) && (synpred12_FromClauseParser())) {s = 31;}

                        else if ( (LA39_1==KW_INNER) && (synpred12_FromClauseParser())) {s = 32;}

                        else if ( (LA39_1==COMMA) && (synpred12_FromClauseParser())) {s = 33;}

                        else if ( (LA39_1==KW_CROSS) && (synpred12_FromClauseParser())) {s = 34;}

                        else if ( (LA39_1==KW_LEFT) && (synpred12_FromClauseParser())) {s = 35;}

                        else if ( (LA39_1==KW_RIGHT) && (synpred12_FromClauseParser())) {s = 36;}

                        else if ( (LA39_1==KW_FULL) && (synpred12_FromClauseParser())) {s = 37;}

                        else if ( (LA39_1==EOF) && (synpred12_FromClauseParser())) {s = 38;}

                        else if ( (LA39_1==KW_INSERT) && (synpred12_FromClauseParser())) {s = 39;}

                        else if ( (LA39_1==KW_SELECT) && (synpred12_FromClauseParser())) {s = 40;}

                        else if ( (LA39_1==KW_MAP) && (synpred12_FromClauseParser())) {s = 41;}

                        else if ( (LA39_1==KW_REDUCE) && (synpred12_FromClauseParser())) {s = 42;}

                        else if ( (LA39_1==KW_WHERE) && (synpred12_FromClauseParser())) {s = 43;}

                        else if ( (LA39_1==KW_GROUP) && (synpred12_FromClauseParser())) {s = 44;}

                        else if ( (LA39_1==KW_HAVING) && (synpred12_FromClauseParser())) {s = 45;}

                        else if ( (LA39_1==KW_ORDER) && (synpred12_FromClauseParser())) {s = 46;}

                        else if ( (LA39_1==KW_CLUSTER) && (synpred12_FromClauseParser())) {s = 47;}

                        else if ( (LA39_1==KW_DISTRIBUTE) && (synpred12_FromClauseParser())) {s = 48;}

                        else if ( (LA39_1==KW_SORT) && (synpred12_FromClauseParser())) {s = 49;}

                        else if ( (LA39_1==KW_WINDOW) && (synpred12_FromClauseParser())) {s = 50;}

                        else if ( (LA39_1==KW_LIMIT) && (synpred12_FromClauseParser())) {s = 51;}

                        else if ( (LA39_1==KW_UNION) && (synpred12_FromClauseParser())) {s = 52;}

                        else if ( (LA39_1==RPAREN) && (synpred12_FromClauseParser())) {s = 53;}

                        else if ( (LA39_1==KW_ON) && (synpred12_FromClauseParser())) {s = 54;}

                        else if ( (LA39_1==KW_PARTITION) && (synpred12_FromClauseParser())) {s = 55;}

                        else if ( (LA39_1==Identifier) && (synpred12_FromClauseParser())) {s = 56;}

                         
                        input.seek(index39_1);

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA39_57 = input.LA(1);

                         
                        int index39_57 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred12_FromClauseParser()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index39_57);

                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA39_58 = input.LA(1);

                         
                        int index39_58 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred12_FromClauseParser()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index39_58);

                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA39_59 = input.LA(1);

                         
                        int index39_59 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred12_FromClauseParser()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index39_59);

                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA39_60 = input.LA(1);

                         
                        int index39_60 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred12_FromClauseParser()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index39_60);

                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA39_61 = input.LA(1);

                         
                        int index39_61 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred12_FromClauseParser()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index39_61);

                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA39_62 = input.LA(1);

                         
                        int index39_62 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred12_FromClauseParser()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index39_62);

                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA39_63 = input.LA(1);

                         
                        int index39_63 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred12_FromClauseParser()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index39_63);

                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA39_64 = input.LA(1);

                         
                        int index39_64 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred12_FromClauseParser()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index39_64);

                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA39_65 = input.LA(1);

                         
                        int index39_65 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred12_FromClauseParser()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index39_65);

                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA39_66 = input.LA(1);

                         
                        int index39_66 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred12_FromClauseParser()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index39_66);

                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA39_67 = input.LA(1);

                         
                        int index39_67 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred12_FromClauseParser()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index39_67);

                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA39_68 = input.LA(1);

                         
                        int index39_68 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred12_FromClauseParser()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index39_68);

                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA39_69 = input.LA(1);

                         
                        int index39_69 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred12_FromClauseParser()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index39_69);

                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA39_70 = input.LA(1);

                         
                        int index39_70 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred12_FromClauseParser()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index39_70);

                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA39_71 = input.LA(1);

                         
                        int index39_71 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred12_FromClauseParser()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index39_71);

                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA39_72 = input.LA(1);

                         
                        int index39_72 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred12_FromClauseParser()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index39_72);

                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA39_73 = input.LA(1);

                         
                        int index39_73 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred12_FromClauseParser()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index39_73);

                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA39_74 = input.LA(1);

                         
                        int index39_74 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred12_FromClauseParser()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index39_74);

                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA39_75 = input.LA(1);

                         
                        int index39_75 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred12_FromClauseParser()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index39_75);

                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA39_76 = input.LA(1);

                         
                        int index39_76 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred12_FromClauseParser()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index39_76);

                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA39_77 = input.LA(1);

                         
                        int index39_77 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred12_FromClauseParser()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index39_77);

                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA39_78 = input.LA(1);

                         
                        int index39_78 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred12_FromClauseParser()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index39_78);

                        if ( s>=0 ) return s;
                        break;
                    case 23 : 
                        int LA39_79 = input.LA(1);

                         
                        int index39_79 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred12_FromClauseParser()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index39_79);

                        if ( s>=0 ) return s;
                        break;
                    case 24 : 
                        int LA39_80 = input.LA(1);

                         
                        int index39_80 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred12_FromClauseParser()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index39_80);

                        if ( s>=0 ) return s;
                        break;
                    case 25 : 
                        int LA39_81 = input.LA(1);

                         
                        int index39_81 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred12_FromClauseParser()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index39_81);

                        if ( s>=0 ) return s;
                        break;
                    case 26 : 
                        int LA39_82 = input.LA(1);

                         
                        int index39_82 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred12_FromClauseParser()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index39_82);

                        if ( s>=0 ) return s;
                        break;
                    case 27 : 
                        int LA39_83 = input.LA(1);

                         
                        int index39_83 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred12_FromClauseParser()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index39_83);

                        if ( s>=0 ) return s;
                        break;
                    case 28 : 
                        int LA39_84 = input.LA(1);

                         
                        int index39_84 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred12_FromClauseParser()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index39_84);

                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}

            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 39, _s, input);
            error(nvae);
            throw nvae;
        }

    }
 

    public static final BitSet FOLLOW_STAR_in_tableAllColumns57 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tableName_in_tableAllColumns79 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_DOT_in_tableAllColumns81 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_STAR_in_tableAllColumns83 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_tableOrColumn131 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_expressionList170 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_COMMA_in_expressionList173 = new BitSet(new long[]{0xFDEFFFFDFC042080L,0xDEBBFDEAF7FFFBD6L,0x6FAFF7F7FEF7FFFFL,0xDFFFFF7FFFF7FF9FL,0x0F00C91A5FFEEF7DL});
    public static final BitSet FOLLOW_expression_in_expressionList175 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_identifier_in_aliasList217 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_COMMA_in_aliasList220 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77E7FEF7BFFDL,0xDFFFFF7FFFF7FF9FL,0x0000001A5FFEEF7DL});
    public static final BitSet FOLLOW_identifier_in_aliasList222 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_KW_FROM_in_fromClause266 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77E7FEF7BFFDL,0xDFFFFF7FFFF7FF9FL,0x0000011A5FFFEF7DL});
    public static final BitSet FOLLOW_joinSource_in_fromClause268 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fromSource_in_joinSource303 = new BitSet(new long[]{0x0000000000000402L,0x0080000000000008L,0x0000000000880080L,0x0000000020000000L});
    public static final BitSet FOLLOW_joinToken_in_joinSource307 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77E7FEF7BFFDL,0xDFFFFF7FFFF7FF9FL,0x0000011A5FFEEF7DL});
    public static final BitSet FOLLOW_fromSource_in_joinSource310 = new BitSet(new long[]{0x0000000000000402L,0x0080000000000008L,0x0010000000880080L,0x0000000020000000L});
    public static final BitSet FOLLOW_KW_ON_in_joinSource314 = new BitSet(new long[]{0xFDEFFFFDFC042080L,0xDEBBFDEAF7FFFBD6L,0x6FAFF7F7FEF7FFFFL,0xDFFFFF7FFFF7FF9FL,0x0F00C91A5FFEEF7DL});
    public static final BitSet FOLLOW_expression_in_joinSource317 = new BitSet(new long[]{0x0000000000000402L,0x0080000000000008L,0x0000000000880080L,0x0000000020000000L});
    public static final BitSet FOLLOW_uniqueJoinToken_in_joinSource333 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77E7FEF7BFFDL,0xDFFFFF7FFFF7FFDFL,0x0000011A5FFEEF7DL});
    public static final BitSet FOLLOW_uniqueJoinSource_in_joinSource336 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_COMMA_in_joinSource339 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77E7FEF7BFFDL,0xDFFFFF7FFFF7FFDFL,0x0000011A5FFEEF7DL});
    public static final BitSet FOLLOW_uniqueJoinSource_in_joinSource342 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_KW_PRESERVE_in_uniqueJoinSource371 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77E7FEF7BFFDL,0xDFFFFF7FFFF7FF9FL,0x0000011A5FFEEF7DL});
    public static final BitSet FOLLOW_fromSource_in_uniqueJoinSource374 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_uniqueJoinExpr_in_uniqueJoinSource376 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_uniqueJoinExpr403 = new BitSet(new long[]{0xFDEFFFFDFC042080L,0xDEBBFDEAF7FFFBD6L,0x6FAFF7F7FEF7FFFFL,0xDFFFFF7FFFF7FF9FL,0x0F00C91A5FFEEF7DL});
    public static final BitSet FOLLOW_expression_in_uniqueJoinExpr407 = new BitSet(new long[]{0x0000000000000400L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_COMMA_in_uniqueJoinExpr410 = new BitSet(new long[]{0xFDEFFFFDFC042080L,0xDEBBFDEAF7FFFBD6L,0x6FAFF7F7FEF7FFFFL,0xDFFFFF7FFFF7FF9FL,0x0F00C91A5FFEEF7DL});
    public static final BitSet FOLLOW_expression_in_uniqueJoinExpr414 = new BitSet(new long[]{0x0000000000000400L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_RPAREN_in_uniqueJoinExpr418 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_UNIQUEJOIN_in_uniqueJoinToken461 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_JOIN_in_joinToken493 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_INNER_in_joinToken526 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_KW_JOIN_in_joinToken528 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COMMA_in_joinToken552 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_CROSS_in_joinToken587 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_KW_JOIN_in_joinToken589 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_LEFT_in_joinToken613 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0200000000080000L});
    public static final BitSet FOLLOW_KW_OUTER_in_joinToken617 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_KW_JOIN_in_joinToken621 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_RIGHT_in_joinToken633 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0200000000080000L});
    public static final BitSet FOLLOW_KW_OUTER_in_joinToken636 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_KW_JOIN_in_joinToken640 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_FULL_in_joinToken652 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0200000000080000L});
    public static final BitSet FOLLOW_KW_OUTER_in_joinToken656 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_KW_JOIN_in_joinToken660 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_LEFT_in_joinToken672 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_KW_SEMI_in_joinToken674 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_KW_JOIN_in_joinToken676 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_LATERAL_in_lateralView720 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000040000000L});
    public static final BitSet FOLLOW_KW_VIEW_in_lateralView722 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0200000000000000L});
    public static final BitSet FOLLOW_KW_OUTER_in_lateralView724 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77F7FEF7BFFFL,0xDFFFFF7FFFF7FF9FL,0x0000001A5FFEEF7DL});
    public static final BitSet FOLLOW_function_in_lateralView726 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77E7FEF7BFFDL,0xDFFFFF7FFFF7FF9FL,0x0000001A5FFEEF7DL});
    public static final BitSet FOLLOW_tableAlias_in_lateralView728 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_KW_AS_in_lateralView731 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77E7FEF7BFFDL,0xDFFFFF7FFFF7FF9FL,0x0000001A5FFEEF7DL});
    public static final BitSet FOLLOW_identifier_in_lateralView733 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_COMMA_in_lateralView741 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77E7FEF7BFFDL,0xDFFFFF7FFFF7FF9FL,0x0000001A5FFEEF7DL});
    public static final BitSet FOLLOW_identifier_in_lateralView743 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_KW_LATERAL_in_lateralView775 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000040000000L});
    public static final BitSet FOLLOW_KW_VIEW_in_lateralView777 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77F7FEF7BFFFL,0xDFFFFF7FFFF7FF9FL,0x0000001A5FFEEF7DL});
    public static final BitSet FOLLOW_function_in_lateralView779 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77E7FEF7BFFDL,0xDFFFFF7FFFF7FF9FL,0x0000001A5FFEEF7DL});
    public static final BitSet FOLLOW_tableAlias_in_lateralView781 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_KW_AS_in_lateralView784 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77E7FEF7BFFDL,0xDFFFFF7FFFF7FF9FL,0x0000001A5FFEEF7DL});
    public static final BitSet FOLLOW_identifier_in_lateralView786 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_COMMA_in_lateralView794 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77E7FEF7BFFDL,0xDFFFFF7FFFF7FF9FL,0x0000001A5FFEEF7DL});
    public static final BitSet FOLLOW_identifier_in_lateralView796 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_identifier_in_tableAlias850 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_partitionedTableFunction_in_fromSource897 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_tableSource_in_fromSource901 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_subQuerySource_in_fromSource905 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_virtualTableSource_in_fromSource909 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_lateralView_in_fromSource913 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_KW_TABLESAMPLE_in_tableBucketSample947 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_LPAREN_in_tableBucketSample949 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_KW_BUCKET_in_tableBucketSample951 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000400000000000L});
    public static final BitSet FOLLOW_Number_in_tableBucketSample956 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_KW_OUT_in_tableBucketSample959 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_KW_OF_in_tableBucketSample961 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000400000000000L});
    public static final BitSet FOLLOW_Number_in_tableBucketSample966 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0010000000000000L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_KW_ON_in_tableBucketSample970 = new BitSet(new long[]{0xFDEFFFFDFC042080L,0xDEBBFDEAF7FFFBD6L,0x6FAFF7F7FEF7FFFFL,0xDFFFFF7FFFF7FF9FL,0x0F00C91A5FFEEF7DL});
    public static final BitSet FOLLOW_expression_in_tableBucketSample974 = new BitSet(new long[]{0x0000000000000400L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_COMMA_in_tableBucketSample977 = new BitSet(new long[]{0xFDEFFFFDFC042080L,0xDEBBFDEAF7FFFBD6L,0x6FAFF7F7FEF7FFFFL,0xDFFFFF7FFFF7FF9FL,0x0F00C91A5FFEEF7DL});
    public static final BitSet FOLLOW_expression_in_tableBucketSample981 = new BitSet(new long[]{0x0000000000000400L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_RPAREN_in_tableBucketSample987 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_TABLESAMPLE_in_splitSample1034 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_LPAREN_in_splitSample1036 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000400000000000L});
    public static final BitSet FOLLOW_Number_in_splitSample1042 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000800000008L});
    public static final BitSet FOLLOW_KW_PERCENT_in_splitSample1048 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_KW_ROWS_in_splitSample1050 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_RPAREN_in_splitSample1053 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_TABLESAMPLE_in_splitSample1097 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_LPAREN_in_splitSample1099 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_ByteLengthLiteral_in_splitSample1105 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_RPAREN_in_splitSample1108 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tableBucketSample_in_tableSample1154 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_splitSample_in_tableSample1162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tableName_in_tableSource1191 = new BitSet(new long[]{0x0000001004000002L,0x0000000000000000L,0x0000000000000000L,0x2000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_tableProperties_in_tableSource1206 = new BitSet(new long[]{0x0000001004000002L,0x0000000000000000L,0x0000000000000000L,0x2000000000000000L});
    public static final BitSet FOLLOW_tableSample_in_tableSource1223 = new BitSet(new long[]{0x0000001004000002L});
    public static final BitSet FOLLOW_KW_AS_in_tableSource1239 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_Identifier_in_tableSource1243 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_tableSource1265 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_tableName1326 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_DOT_in_tableName1328 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77E7FEF7BFFDL,0xDFFFFF7FFFF7FF9FL,0x0000001A5FFEEF7DL});
    public static final BitSet FOLLOW_identifier_in_tableName1332 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_tableName1362 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_viewName1409 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_DOT_in_viewName1411 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77E7FEF7BFFDL,0xDFFFFF7FFFF7FF9FL,0x0000001A5FFEEF7DL});
    public static final BitSet FOLLOW_identifier_in_viewName1417 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_subQuerySource1465 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L,0x0000001000000800L,0x0000008000080000L,0x0000000800000000L});
    public static final BitSet FOLLOW_queryStatementExpression_in_subQuerySource1467 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_RPAREN_in_subQuerySource1470 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77E7FEF7BFFDL,0xDFFFFF7FFFF7FF9FL,0x0000001A5FFEEF7DL});
    public static final BitSet FOLLOW_KW_AS_in_subQuerySource1472 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77E7FEF7BFFDL,0xDFFFFF7FFFF7FF9FL,0x0000001A5FFEEF7DL});
    public static final BitSet FOLLOW_identifier_in_subQuerySource1475 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_partitionByClause_in_partitioningSpec1515 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_orderByClause_in_partitioningSpec1517 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_orderByClause_in_partitioningSpec1536 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_distributeByClause_in_partitioningSpec1551 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_sortByClause_in_partitioningSpec1553 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sortByClause_in_partitioningSpec1572 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_clusterByClause_in_partitioningSpec1587 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_subQuerySource_in_partitionTableFunctionSource1623 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tableSource_in_partitionTableFunctionSource1630 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_partitionedTableFunction_in_partitionTableFunctionSource1637 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_partitionedTableFunction1667 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_LPAREN_in_partitionedTableFunction1669 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0010000000000000L});
    public static final BitSet FOLLOW_KW_ON_in_partitionedTableFunction1671 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77E7FEF7BFFDL,0xDFFFFF7FFFF7FF9FL,0x0000011A5FFEEF7DL});
    public static final BitSet FOLLOW_partitionTableFunctionSource_in_partitionedTableFunction1686 = new BitSet(new long[]{0x0020000004000000L,0x0000000010000000L,0x0080000000000000L,0x0008000000000001L,0x0008000000000000L});
    public static final BitSet FOLLOW_partitioningSpec_in_partitionedTableFunction1690 = new BitSet(new long[]{0x0000000004000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_Identifier_in_partitionedTableFunction1712 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_LPAREN_in_partitionedTableFunction1714 = new BitSet(new long[]{0xFDEFFFFDFC042080L,0xDEBBFDEAF7FFFBD6L,0x6FAFF7F7FEF7FFFFL,0xDFFFFF7FFFF7FF9FL,0x0F00C91A5FFEEF7DL});
    public static final BitSet FOLLOW_expression_in_partitionedTableFunction1716 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_RPAREN_in_partitionedTableFunction1718 = new BitSet(new long[]{0x0000000000000400L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_COMMA_in_partitionedTableFunction1722 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_Identifier_in_partitionedTableFunction1724 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_LPAREN_in_partitionedTableFunction1726 = new BitSet(new long[]{0xFDEFFFFDFC042080L,0xDEBBFDEAF7FFFBD6L,0x6FAFF7F7FEF7FFFFL,0xDFFFFF7FFFF7FF9FL,0x0F00C91A5FFEEF7DL});
    public static final BitSet FOLLOW_expression_in_partitionedTableFunction1728 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_RPAREN_in_partitionedTableFunction1730 = new BitSet(new long[]{0x0000000000000400L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_RPAREN_in_partitionedTableFunction1747 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_Identifier_in_partitionedTableFunction1760 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_WHERE_in_whereClause1822 = new BitSet(new long[]{0xFDEFFFFDFC042080L,0xDEBBFDEAF7FFFBD6L,0x6FAFF7F7FEF7FFFFL,0xDFFFFF7FFFF7FF9FL,0x0F00C91A5FFEEF7DL});
    public static final BitSet FOLLOW_searchCondition_in_whereClause1824 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_searchCondition1863 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_valueRowConstructor1890 = new BitSet(new long[]{0xFDEFFFFDFC042080L,0xDEBBFDEAF7FFFBD6L,0x6FAF77F7FEF7FFFFL,0xDFFFFF7FFFF7FF9FL,0x0F00C91A5FFEEF7DL});
    public static final BitSet FOLLOW_precedenceUnaryPrefixExpression_in_valueRowConstructor1892 = new BitSet(new long[]{0x0000000000000400L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_COMMA_in_valueRowConstructor1895 = new BitSet(new long[]{0xFDEFFFFDFC042080L,0xDEBBFDEAF7FFFBD6L,0x6FAF77F7FEF7FFFFL,0xDFFFFF7FFFF7FF9FL,0x0F00C91A5FFEEF7DL});
    public static final BitSet FOLLOW_precedenceUnaryPrefixExpression_in_valueRowConstructor1897 = new BitSet(new long[]{0x0000000000000400L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_RPAREN_in_valueRowConstructor1901 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_valueRowConstructor_in_valuesTableConstructor1931 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_COMMA_in_valuesTableConstructor1934 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_valueRowConstructor_in_valuesTableConstructor1936 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_KW_VALUES_in_valuesClause1970 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_valuesTableConstructor_in_valuesClause1972 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_virtualTableSource1993 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_valuesClause_in_virtualTableSource1995 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_RPAREN_in_virtualTableSource1997 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77E7FEF7BFFDL,0xDFFFFF7FFFF7FF9FL,0x0000001A5FFEEF7DL});
    public static final BitSet FOLLOW_tableNameColList_in_virtualTableSource1999 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_AS_in_tableNameColList2028 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77E7FEF7BFFDL,0xDFFFFF7FFFF7FF9FL,0x0000001A5FFEEF7DL});
    public static final BitSet FOLLOW_identifier_in_tableNameColList2031 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_LPAREN_in_tableNameColList2033 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77E7FEF7BFFDL,0xDFFFFF7FFFF7FF9FL,0x0000001A5FFEEF7DL});
    public static final BitSet FOLLOW_identifier_in_tableNameColList2035 = new BitSet(new long[]{0x0000000000000400L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_COMMA_in_tableNameColList2038 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77E7FEF7BFFDL,0xDFFFFF7FFFF7FF9FL,0x0000001A5FFEEF7DL});
    public static final BitSet FOLLOW_identifier_in_tableNameColList2040 = new BitSet(new long[]{0x0000000000000400L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_RPAREN_in_tableNameColList2044 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_LATERAL_in_synpred1_FromClauseParser711 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000040000000L});
    public static final BitSet FOLLOW_KW_VIEW_in_synpred1_FromClauseParser713 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0200000000000000L});
    public static final BitSet FOLLOW_KW_OUTER_in_synpred1_FromClauseParser715 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COMMA_in_synpred2_FromClauseParser737 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COMMA_in_synpred3_FromClauseParser790 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_synpred4_FromClauseParser891 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_LPAREN_in_synpred4_FromClauseParser893 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tableProperties_in_synpred5_FromClauseParser1199 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tableSample_in_synpred6_FromClauseParser1216 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_AS_in_synpred7_FromClauseParser1233 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_synpred8_FromClauseParser1257 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_synpred10_FromClauseParser1700 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_LPAREN_in_synpred10_FromClauseParser1702 = new BitSet(new long[]{0xFDEFFFFDFC042080L,0xDEBBFDEAF7FFFBD6L,0x6FAFF7F7FEF7FFFFL,0xDFFFFF7FFFF7FF9FL,0x0F00C91A5FFEEF7DL});
    public static final BitSet FOLLOW_expression_in_synpred10_FromClauseParser1704 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_RPAREN_in_synpred10_FromClauseParser1706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_synpred12_FromClauseParser1753 = new BitSet(new long[]{0x0000000000000002L});

}