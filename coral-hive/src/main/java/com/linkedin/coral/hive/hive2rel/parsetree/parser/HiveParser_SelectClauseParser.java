// $ANTLR 3.4 SelectClauseParser.g 2021-01-01 01:23:45

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
public class HiveParser_SelectClauseParser extends Parser {
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
    public String[] getTokenNames() { return HiveParser.tokenNames; }
    public String getGrammarFileName() { return "SelectClauseParser.g"; }


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


    public static class selectClause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "selectClause"
    // SelectClauseParser.g:51:1: selectClause : ( KW_SELECT ( hintClause )? ( ( ( KW_ALL |dist= KW_DISTINCT )? selectList ) | (transform= KW_TRANSFORM selectTrfmClause ) ) -> {$transform == null && $dist == null}? ^( TOK_SELECT ( hintClause )? selectList ) -> {$transform == null && $dist != null}? ^( TOK_SELECTDI ( hintClause )? selectList ) -> ^( TOK_SELECT ( hintClause )? ^( TOK_SELEXPR selectTrfmClause ) ) | trfmClause -> ^( TOK_SELECT ^( TOK_SELEXPR trfmClause ) ) );
    public final HiveParser_SelectClauseParser.selectClause_return selectClause() throws RecognitionException {
        HiveParser_SelectClauseParser.selectClause_return retval = new HiveParser_SelectClauseParser.selectClause_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token dist=null;
        Token transform=null;
        Token KW_SELECT1=null;
        Token KW_ALL3=null;
        HiveParser_SelectClauseParser.hintClause_return hintClause2 =null;

        HiveParser_SelectClauseParser.selectList_return selectList4 =null;

        HiveParser_SelectClauseParser.selectTrfmClause_return selectTrfmClause5 =null;

        HiveParser_SelectClauseParser.trfmClause_return trfmClause6 =null;


        CommonTree dist_tree=null;
        CommonTree transform_tree=null;
        CommonTree KW_SELECT1_tree=null;
        CommonTree KW_ALL3_tree=null;
        RewriteRuleTokenStream stream_KW_TRANSFORM=new RewriteRuleTokenStream(adaptor,"token KW_TRANSFORM");
        RewriteRuleTokenStream stream_KW_DISTINCT=new RewriteRuleTokenStream(adaptor,"token KW_DISTINCT");
        RewriteRuleTokenStream stream_KW_SELECT=new RewriteRuleTokenStream(adaptor,"token KW_SELECT");
        RewriteRuleTokenStream stream_KW_ALL=new RewriteRuleTokenStream(adaptor,"token KW_ALL");
        RewriteRuleSubtreeStream stream_trfmClause=new RewriteRuleSubtreeStream(adaptor,"rule trfmClause");
        RewriteRuleSubtreeStream stream_selectList=new RewriteRuleSubtreeStream(adaptor,"rule selectList");
        RewriteRuleSubtreeStream stream_selectTrfmClause=new RewriteRuleSubtreeStream(adaptor,"rule selectTrfmClause");
        RewriteRuleSubtreeStream stream_hintClause=new RewriteRuleSubtreeStream(adaptor,"rule hintClause");
         gParent.pushMsg("select clause", state); 
        try {
            // SelectClauseParser.g:54:5: ( KW_SELECT ( hintClause )? ( ( ( KW_ALL |dist= KW_DISTINCT )? selectList ) | (transform= KW_TRANSFORM selectTrfmClause ) ) -> {$transform == null && $dist == null}? ^( TOK_SELECT ( hintClause )? selectList ) -> {$transform == null && $dist != null}? ^( TOK_SELECTDI ( hintClause )? selectList ) -> ^( TOK_SELECT ( hintClause )? ^( TOK_SELEXPR selectTrfmClause ) ) | trfmClause -> ^( TOK_SELECT ^( TOK_SELEXPR trfmClause ) ) )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==KW_SELECT) ) {
                alt4=1;
            }
            else if ( (LA4_0==KW_MAP||LA4_0==KW_REDUCE) ) {
                alt4=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;

            }
            switch (alt4) {
                case 1 :
                    // SelectClauseParser.g:55:5: KW_SELECT ( hintClause )? ( ( ( KW_ALL |dist= KW_DISTINCT )? selectList ) | (transform= KW_TRANSFORM selectTrfmClause ) )
                    {
                    KW_SELECT1=(Token)match(input,KW_SELECT,FOLLOW_KW_SELECT_in_selectClause71); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_SELECT.add(KW_SELECT1);


                    // SelectClauseParser.g:55:15: ( hintClause )?
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==DIVIDE) ) {
                        alt1=1;
                    }
                    switch (alt1) {
                        case 1 :
                            // SelectClauseParser.g:55:15: hintClause
                            {
                            pushFollow(FOLLOW_hintClause_in_selectClause73);
                            hintClause2=hintClause();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_hintClause.add(hintClause2.getTree());

                            }
                            break;

                    }


                    // SelectClauseParser.g:55:27: ( ( ( KW_ALL |dist= KW_DISTINCT )? selectList ) | (transform= KW_TRANSFORM selectTrfmClause ) )
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==BigintLiteral||LA3_0==CharSetName||LA3_0==DecimalLiteral||(LA3_0 >= Identifier && LA3_0 <= KW_ANALYZE)||(LA3_0 >= KW_ARCHIVE && LA3_0 <= KW_CHANGE)||(LA3_0 >= KW_CLUSTER && LA3_0 <= KW_COLLECTION)||(LA3_0 >= KW_COLUMNS && LA3_0 <= KW_CONCATENATE)||(LA3_0 >= KW_CONTINUE && LA3_0 <= KW_CREATE)||LA3_0==KW_CUBE||(LA3_0 >= KW_CURRENT_DATE && LA3_0 <= KW_DATA)||(LA3_0 >= KW_DATABASES && LA3_0 <= KW_ELEM_TYPE)||LA3_0==KW_ENABLE||LA3_0==KW_ESCAPED||(LA3_0 >= KW_EXCLUSIVE && LA3_0 <= KW_EXPORT)||(LA3_0 >= KW_EXTERNAL && LA3_0 <= KW_FLOAT)||(LA3_0 >= KW_FOR && LA3_0 <= KW_FORMATTED)||LA3_0==KW_FULL||(LA3_0 >= KW_FUNCTIONS && LA3_0 <= KW_GROUPING)||(LA3_0 >= KW_HOLD_DDLTIME && LA3_0 <= KW_JAR)||(LA3_0 >= KW_KEYS && LA3_0 <= KW_LEFT)||(LA3_0 >= KW_LIKE && LA3_0 <= KW_LONG)||(LA3_0 >= KW_MAP && LA3_0 <= KW_MONTH)||(LA3_0 >= KW_MSCK && LA3_0 <= KW_OFFLINE)||LA3_0==KW_OPTION||(LA3_0 >= KW_ORDER && LA3_0 <= KW_OUTPUTFORMAT)||(LA3_0 >= KW_OVERWRITE && LA3_0 <= KW_OWNER)||(LA3_0 >= KW_PARTITION && LA3_0 <= KW_PLUS)||(LA3_0 >= KW_PRETTY && LA3_0 <= KW_RECORDWRITER)||(LA3_0 >= KW_REGEXP && LA3_0 <= KW_SECOND)||(LA3_0 >= KW_SEMI && LA3_0 <= KW_TABLES)||(LA3_0 >= KW_TBLPROPERTIES && LA3_0 <= KW_TERMINATED)||(LA3_0 >= KW_TIMESTAMP && LA3_0 <= KW_TRANSACTIONS)||(LA3_0 >= KW_TRIGGER && LA3_0 <= KW_UNARCHIVE)||(LA3_0 >= KW_UNDO && LA3_0 <= KW_UNIONTYPE)||(LA3_0 >= KW_UNLOCK && LA3_0 <= KW_VALUE_TYPE)||LA3_0==KW_VIEW||LA3_0==KW_WHILE||(LA3_0 >= KW_WITH && LA3_0 <= KW_YEAR)||LA3_0==LPAREN||LA3_0==MINUS||(LA3_0 >= Number && LA3_0 <= PLUS)||(LA3_0 >= STAR && LA3_0 <= TinyintLiteral)) ) {
                        alt3=1;
                    }
                    else if ( (LA3_0==KW_TRANSFORM) ) {
                        alt3=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 3, 0, input);

                        throw nvae;

                    }
                    switch (alt3) {
                        case 1 :
                            // SelectClauseParser.g:55:28: ( ( KW_ALL |dist= KW_DISTINCT )? selectList )
                            {
                            // SelectClauseParser.g:55:28: ( ( KW_ALL |dist= KW_DISTINCT )? selectList )
                            // SelectClauseParser.g:55:29: ( KW_ALL |dist= KW_DISTINCT )? selectList
                            {
                            // SelectClauseParser.g:55:29: ( KW_ALL |dist= KW_DISTINCT )?
                            int alt2=3;
                            alt2 = dfa2.predict(input);
                            switch (alt2) {
                                case 1 :
                                    // SelectClauseParser.g:55:30: KW_ALL
                                    {
                                    KW_ALL3=(Token)match(input,KW_ALL,FOLLOW_KW_ALL_in_selectClause79); if (state.failed) return retval; 
                                    if ( state.backtracking==0 ) stream_KW_ALL.add(KW_ALL3);


                                    }
                                    break;
                                case 2 :
                                    // SelectClauseParser.g:55:39: dist= KW_DISTINCT
                                    {
                                    dist=(Token)match(input,KW_DISTINCT,FOLLOW_KW_DISTINCT_in_selectClause85); if (state.failed) return retval; 
                                    if ( state.backtracking==0 ) stream_KW_DISTINCT.add(dist);


                                    }
                                    break;

                            }


                            pushFollow(FOLLOW_selectList_in_selectClause89);
                            selectList4=selectList();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_selectList.add(selectList4.getTree());

                            }


                            }
                            break;
                        case 2 :
                            // SelectClauseParser.g:56:29: (transform= KW_TRANSFORM selectTrfmClause )
                            {
                            // SelectClauseParser.g:56:29: (transform= KW_TRANSFORM selectTrfmClause )
                            // SelectClauseParser.g:56:30: transform= KW_TRANSFORM selectTrfmClause
                            {
                            transform=(Token)match(input,KW_TRANSFORM,FOLLOW_KW_TRANSFORM_in_selectClause123); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_KW_TRANSFORM.add(transform);


                            pushFollow(FOLLOW_selectTrfmClause_in_selectClause125);
                            selectTrfmClause5=selectTrfmClause();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_selectTrfmClause.add(selectTrfmClause5.getTree());

                            }


                            }
                            break;

                    }


                    // AST REWRITE
                    // elements: selectTrfmClause, selectList, hintClause, selectList, hintClause, hintClause
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 57:6: -> {$transform == null && $dist == null}? ^( TOK_SELECT ( hintClause )? selectList )
                    if (transform == null && dist == null) {
                        // SelectClauseParser.g:57:48: ^( TOK_SELECT ( hintClause )? selectList )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(TOK_SELECT, "TOK_SELECT")
                        , root_1);

                        // SelectClauseParser.g:57:61: ( hintClause )?
                        if ( stream_hintClause.hasNext() ) {
                            adaptor.addChild(root_1, stream_hintClause.nextTree());

                        }
                        stream_hintClause.reset();

                        adaptor.addChild(root_1, stream_selectList.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    else // 58:6: -> {$transform == null && $dist != null}? ^( TOK_SELECTDI ( hintClause )? selectList )
                    if (transform == null && dist != null) {
                        // SelectClauseParser.g:58:48: ^( TOK_SELECTDI ( hintClause )? selectList )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(TOK_SELECTDI, "TOK_SELECTDI")
                        , root_1);

                        // SelectClauseParser.g:58:63: ( hintClause )?
                        if ( stream_hintClause.hasNext() ) {
                            adaptor.addChild(root_1, stream_hintClause.nextTree());

                        }
                        stream_hintClause.reset();

                        adaptor.addChild(root_1, stream_selectList.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    else // 59:6: -> ^( TOK_SELECT ( hintClause )? ^( TOK_SELEXPR selectTrfmClause ) )
                    {
                        // SelectClauseParser.g:59:9: ^( TOK_SELECT ( hintClause )? ^( TOK_SELEXPR selectTrfmClause ) )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(TOK_SELECT, "TOK_SELECT")
                        , root_1);

                        // SelectClauseParser.g:59:22: ( hintClause )?
                        if ( stream_hintClause.hasNext() ) {
                            adaptor.addChild(root_1, stream_hintClause.nextTree());

                        }
                        stream_hintClause.reset();

                        // SelectClauseParser.g:59:34: ^( TOK_SELEXPR selectTrfmClause )
                        {
                        CommonTree root_2 = (CommonTree)adaptor.nil();
                        root_2 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(TOK_SELEXPR, "TOK_SELEXPR")
                        , root_2);

                        adaptor.addChild(root_2, stream_selectTrfmClause.nextTree());

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
                    // SelectClauseParser.g:61:5: trfmClause
                    {
                    pushFollow(FOLLOW_trfmClause_in_selectClause196);
                    trfmClause6=trfmClause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_trfmClause.add(trfmClause6.getTree());

                    // AST REWRITE
                    // elements: trfmClause
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 61:17: -> ^( TOK_SELECT ^( TOK_SELEXPR trfmClause ) )
                    {
                        // SelectClauseParser.g:61:19: ^( TOK_SELECT ^( TOK_SELEXPR trfmClause ) )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(TOK_SELECT, "TOK_SELECT")
                        , root_1);

                        // SelectClauseParser.g:61:32: ^( TOK_SELEXPR trfmClause )
                        {
                        CommonTree root_2 = (CommonTree)adaptor.nil();
                        root_2 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(TOK_SELEXPR, "TOK_SELEXPR")
                        , root_2);

                        adaptor.addChild(root_2, stream_trfmClause.nextTree());

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
    // $ANTLR end "selectClause"


    public static class selectList_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "selectList"
    // SelectClauseParser.g:64:1: selectList : selectItem ( COMMA selectItem )* -> ( selectItem )+ ;
    public final HiveParser_SelectClauseParser.selectList_return selectList() throws RecognitionException {
        HiveParser_SelectClauseParser.selectList_return retval = new HiveParser_SelectClauseParser.selectList_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token COMMA8=null;
        HiveParser_SelectClauseParser.selectItem_return selectItem7 =null;

        HiveParser_SelectClauseParser.selectItem_return selectItem9 =null;


        CommonTree COMMA8_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_selectItem=new RewriteRuleSubtreeStream(adaptor,"rule selectItem");
         gParent.pushMsg("select list", state); 
        try {
            // SelectClauseParser.g:67:5: ( selectItem ( COMMA selectItem )* -> ( selectItem )+ )
            // SelectClauseParser.g:68:5: selectItem ( COMMA selectItem )*
            {
            pushFollow(FOLLOW_selectItem_in_selectList239);
            selectItem7=selectItem();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_selectItem.add(selectItem7.getTree());

            // SelectClauseParser.g:68:16: ( COMMA selectItem )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==COMMA) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // SelectClauseParser.g:68:18: COMMA selectItem
            	    {
            	    COMMA8=(Token)match(input,COMMA,FOLLOW_COMMA_in_selectList243); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA8);


            	    pushFollow(FOLLOW_selectItem_in_selectList246);
            	    selectItem9=selectItem();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_selectItem.add(selectItem9.getTree());

            	    }
            	    break;

            	default :
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
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 68:39: -> ( selectItem )+
            {
                if ( !(stream_selectItem.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_selectItem.hasNext() ) {
                    adaptor.addChild(root_0, stream_selectItem.nextTree());

                }
                stream_selectItem.reset();

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
    // $ANTLR end "selectList"


    public static class selectTrfmClause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "selectTrfmClause"
    // SelectClauseParser.g:71:1: selectTrfmClause : LPAREN selectExpressionList RPAREN inSerde= rowFormat inRec= recordWriter KW_USING StringLiteral ( KW_AS ( ( LPAREN ( aliasList | columnNameTypeList ) RPAREN ) | ( aliasList | columnNameTypeList ) ) )? outSerde= rowFormat outRec= recordReader -> ^( TOK_TRANSFORM selectExpressionList $inSerde $inRec StringLiteral $outSerde $outRec ( aliasList )? ( columnNameTypeList )? ) ;
    public final HiveParser_SelectClauseParser.selectTrfmClause_return selectTrfmClause() throws RecognitionException {
        HiveParser_SelectClauseParser.selectTrfmClause_return retval = new HiveParser_SelectClauseParser.selectTrfmClause_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token LPAREN10=null;
        Token RPAREN12=null;
        Token KW_USING13=null;
        Token StringLiteral14=null;
        Token KW_AS15=null;
        Token LPAREN16=null;
        Token RPAREN19=null;
        HiveParser.rowFormat_return inSerde =null;

        HiveParser.recordWriter_return inRec =null;

        HiveParser.rowFormat_return outSerde =null;

        HiveParser.recordReader_return outRec =null;

        HiveParser_SelectClauseParser.selectExpressionList_return selectExpressionList11 =null;

        HiveParser_FromClauseParser.aliasList_return aliasList17 =null;

        HiveParser.columnNameTypeList_return columnNameTypeList18 =null;

        HiveParser_FromClauseParser.aliasList_return aliasList20 =null;

        HiveParser.columnNameTypeList_return columnNameTypeList21 =null;


        CommonTree LPAREN10_tree=null;
        CommonTree RPAREN12_tree=null;
        CommonTree KW_USING13_tree=null;
        CommonTree StringLiteral14_tree=null;
        CommonTree KW_AS15_tree=null;
        CommonTree LPAREN16_tree=null;
        CommonTree RPAREN19_tree=null;
        RewriteRuleTokenStream stream_StringLiteral=new RewriteRuleTokenStream(adaptor,"token StringLiteral");
        RewriteRuleTokenStream stream_KW_USING=new RewriteRuleTokenStream(adaptor,"token KW_USING");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_KW_AS=new RewriteRuleTokenStream(adaptor,"token KW_AS");
        RewriteRuleSubtreeStream stream_aliasList=new RewriteRuleSubtreeStream(adaptor,"rule aliasList");
        RewriteRuleSubtreeStream stream_rowFormat=new RewriteRuleSubtreeStream(adaptor,"rule rowFormat");
        RewriteRuleSubtreeStream stream_columnNameTypeList=new RewriteRuleSubtreeStream(adaptor,"rule columnNameTypeList");
        RewriteRuleSubtreeStream stream_recordReader=new RewriteRuleSubtreeStream(adaptor,"rule recordReader");
        RewriteRuleSubtreeStream stream_selectExpressionList=new RewriteRuleSubtreeStream(adaptor,"rule selectExpressionList");
        RewriteRuleSubtreeStream stream_recordWriter=new RewriteRuleSubtreeStream(adaptor,"rule recordWriter");
         gParent.pushMsg("transform clause", state); 
        try {
            // SelectClauseParser.g:74:5: ( LPAREN selectExpressionList RPAREN inSerde= rowFormat inRec= recordWriter KW_USING StringLiteral ( KW_AS ( ( LPAREN ( aliasList | columnNameTypeList ) RPAREN ) | ( aliasList | columnNameTypeList ) ) )? outSerde= rowFormat outRec= recordReader -> ^( TOK_TRANSFORM selectExpressionList $inSerde $inRec StringLiteral $outSerde $outRec ( aliasList )? ( columnNameTypeList )? ) )
            // SelectClauseParser.g:75:5: LPAREN selectExpressionList RPAREN inSerde= rowFormat inRec= recordWriter KW_USING StringLiteral ( KW_AS ( ( LPAREN ( aliasList | columnNameTypeList ) RPAREN ) | ( aliasList | columnNameTypeList ) ) )? outSerde= rowFormat outRec= recordReader
            {
            LPAREN10=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_selectTrfmClause285); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN10);


            pushFollow(FOLLOW_selectExpressionList_in_selectTrfmClause287);
            selectExpressionList11=selectExpressionList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_selectExpressionList.add(selectExpressionList11.getTree());

            RPAREN12=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_selectTrfmClause289); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN12);


            pushFollow(FOLLOW_rowFormat_in_selectTrfmClause297);
            inSerde=gHiveParser.rowFormat();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rowFormat.add(inSerde.getTree());

            pushFollow(FOLLOW_recordWriter_in_selectTrfmClause301);
            inRec=gHiveParser.recordWriter();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_recordWriter.add(inRec.getTree());

            KW_USING13=(Token)match(input,KW_USING,FOLLOW_KW_USING_in_selectTrfmClause307); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_KW_USING.add(KW_USING13);


            StringLiteral14=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_selectTrfmClause309); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_StringLiteral.add(StringLiteral14);


            // SelectClauseParser.g:78:5: ( KW_AS ( ( LPAREN ( aliasList | columnNameTypeList ) RPAREN ) | ( aliasList | columnNameTypeList ) ) )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==KW_AS) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // SelectClauseParser.g:78:7: KW_AS ( ( LPAREN ( aliasList | columnNameTypeList ) RPAREN ) | ( aliasList | columnNameTypeList ) )
                    {
                    KW_AS15=(Token)match(input,KW_AS,FOLLOW_KW_AS_in_selectTrfmClause317); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_AS.add(KW_AS15);


                    // SelectClauseParser.g:78:13: ( ( LPAREN ( aliasList | columnNameTypeList ) RPAREN ) | ( aliasList | columnNameTypeList ) )
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( (LA8_0==LPAREN) ) {
                        alt8=1;
                    }
                    else if ( ((LA8_0 >= Identifier && LA8_0 <= KW_ANALYZE)||(LA8_0 >= KW_ARCHIVE && LA8_0 <= KW_CASCADE)||LA8_0==KW_CHANGE||(LA8_0 >= KW_CLUSTER && LA8_0 <= KW_COLLECTION)||(LA8_0 >= KW_COLUMNS && LA8_0 <= KW_CONCATENATE)||(LA8_0 >= KW_CONTINUE && LA8_0 <= KW_CREATE)||LA8_0==KW_CUBE||(LA8_0 >= KW_CURRENT_DATE && LA8_0 <= KW_DATA)||(LA8_0 >= KW_DATABASES && LA8_0 <= KW_DISABLE)||(LA8_0 >= KW_DISTRIBUTE && LA8_0 <= KW_ELEM_TYPE)||LA8_0==KW_ENABLE||LA8_0==KW_ESCAPED||(LA8_0 >= KW_EXCLUSIVE && LA8_0 <= KW_EXPORT)||(LA8_0 >= KW_EXTERNAL && LA8_0 <= KW_FLOAT)||(LA8_0 >= KW_FOR && LA8_0 <= KW_FORMATTED)||LA8_0==KW_FULL||(LA8_0 >= KW_FUNCTIONS && LA8_0 <= KW_GROUPING)||(LA8_0 >= KW_HOLD_DDLTIME && LA8_0 <= KW_IDXPROPERTIES)||(LA8_0 >= KW_IGNORE && LA8_0 <= KW_INTERSECT)||(LA8_0 >= KW_INTO && LA8_0 <= KW_JAR)||(LA8_0 >= KW_KEYS && LA8_0 <= KW_LEFT)||(LA8_0 >= KW_LIKE && LA8_0 <= KW_LONG)||(LA8_0 >= KW_MAPJOIN && LA8_0 <= KW_MONTH)||(LA8_0 >= KW_MSCK && LA8_0 <= KW_NOSCAN)||(LA8_0 >= KW_NO_DROP && LA8_0 <= KW_OFFLINE)||LA8_0==KW_OPTION||(LA8_0 >= KW_ORDER && LA8_0 <= KW_OUTPUTFORMAT)||(LA8_0 >= KW_OVERWRITE && LA8_0 <= KW_OWNER)||(LA8_0 >= KW_PARTITION && LA8_0 <= KW_PLUS)||(LA8_0 >= KW_PRETTY && LA8_0 <= KW_RECORDWRITER)||(LA8_0 >= KW_REGEXP && LA8_0 <= KW_SECOND)||(LA8_0 >= KW_SEMI && LA8_0 <= KW_TABLES)||(LA8_0 >= KW_TBLPROPERTIES && LA8_0 <= KW_TERMINATED)||(LA8_0 >= KW_TIMESTAMP && LA8_0 <= KW_TRANSACTIONS)||(LA8_0 >= KW_TRIGGER && LA8_0 <= KW_UNARCHIVE)||(LA8_0 >= KW_UNDO && LA8_0 <= KW_UNIONTYPE)||(LA8_0 >= KW_UNLOCK && LA8_0 <= KW_VALUE_TYPE)||LA8_0==KW_VIEW||LA8_0==KW_WHILE||(LA8_0 >= KW_WITH && LA8_0 <= KW_YEAR)) ) {
                        alt8=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 8, 0, input);

                        throw nvae;

                    }
                    switch (alt8) {
                        case 1 :
                            // SelectClauseParser.g:78:14: ( LPAREN ( aliasList | columnNameTypeList ) RPAREN )
                            {
                            // SelectClauseParser.g:78:14: ( LPAREN ( aliasList | columnNameTypeList ) RPAREN )
                            // SelectClauseParser.g:78:15: LPAREN ( aliasList | columnNameTypeList ) RPAREN
                            {
                            LPAREN16=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_selectTrfmClause321); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN16);


                            // SelectClauseParser.g:78:22: ( aliasList | columnNameTypeList )
                            int alt6=2;
                            alt6 = dfa6.predict(input);
                            switch (alt6) {
                                case 1 :
                                    // SelectClauseParser.g:78:23: aliasList
                                    {
                                    pushFollow(FOLLOW_aliasList_in_selectTrfmClause324);
                                    aliasList17=gHiveParser.aliasList();

                                    state._fsp--;
                                    if (state.failed) return retval;
                                    if ( state.backtracking==0 ) stream_aliasList.add(aliasList17.getTree());

                                    }
                                    break;
                                case 2 :
                                    // SelectClauseParser.g:78:35: columnNameTypeList
                                    {
                                    pushFollow(FOLLOW_columnNameTypeList_in_selectTrfmClause328);
                                    columnNameTypeList18=gHiveParser.columnNameTypeList();

                                    state._fsp--;
                                    if (state.failed) return retval;
                                    if ( state.backtracking==0 ) stream_columnNameTypeList.add(columnNameTypeList18.getTree());

                                    }
                                    break;

                            }


                            RPAREN19=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_selectTrfmClause331); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN19);


                            }


                            }
                            break;
                        case 2 :
                            // SelectClauseParser.g:78:65: ( aliasList | columnNameTypeList )
                            {
                            // SelectClauseParser.g:78:65: ( aliasList | columnNameTypeList )
                            int alt7=2;
                            alt7 = dfa7.predict(input);
                            switch (alt7) {
                                case 1 :
                                    // SelectClauseParser.g:78:66: aliasList
                                    {
                                    pushFollow(FOLLOW_aliasList_in_selectTrfmClause337);
                                    aliasList20=gHiveParser.aliasList();

                                    state._fsp--;
                                    if (state.failed) return retval;
                                    if ( state.backtracking==0 ) stream_aliasList.add(aliasList20.getTree());

                                    }
                                    break;
                                case 2 :
                                    // SelectClauseParser.g:78:78: columnNameTypeList
                                    {
                                    pushFollow(FOLLOW_columnNameTypeList_in_selectTrfmClause341);
                                    columnNameTypeList21=gHiveParser.columnNameTypeList();

                                    state._fsp--;
                                    if (state.failed) return retval;
                                    if ( state.backtracking==0 ) stream_columnNameTypeList.add(columnNameTypeList21.getTree());

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
            outSerde=gHiveParser.rowFormat();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rowFormat.add(outSerde.getTree());

            pushFollow(FOLLOW_recordReader_in_selectTrfmClause357);
            outRec=gHiveParser.recordReader();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_recordReader.add(outRec.getTree());

            // AST REWRITE
            // elements: aliasList, inRec, selectExpressionList, columnNameTypeList, inSerde, outSerde, outRec, StringLiteral
            // token labels: 
            // rule labels: inRec, outRec, inSerde, outSerde, retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_inRec=new RewriteRuleSubtreeStream(adaptor,"rule inRec",inRec!=null?inRec.tree:null);
            RewriteRuleSubtreeStream stream_outRec=new RewriteRuleSubtreeStream(adaptor,"rule outRec",outRec!=null?outRec.tree:null);
            RewriteRuleSubtreeStream stream_inSerde=new RewriteRuleSubtreeStream(adaptor,"rule inSerde",inSerde!=null?inSerde.tree:null);
            RewriteRuleSubtreeStream stream_outSerde=new RewriteRuleSubtreeStream(adaptor,"rule outSerde",outSerde!=null?outSerde.tree:null);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 80:5: -> ^( TOK_TRANSFORM selectExpressionList $inSerde $inRec StringLiteral $outSerde $outRec ( aliasList )? ( columnNameTypeList )? )
            {
                // SelectClauseParser.g:80:8: ^( TOK_TRANSFORM selectExpressionList $inSerde $inRec StringLiteral $outSerde $outRec ( aliasList )? ( columnNameTypeList )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(TOK_TRANSFORM, "TOK_TRANSFORM")
                , root_1);

                adaptor.addChild(root_1, stream_selectExpressionList.nextTree());

                adaptor.addChild(root_1, stream_inSerde.nextTree());

                adaptor.addChild(root_1, stream_inRec.nextTree());

                adaptor.addChild(root_1, 
                stream_StringLiteral.nextNode()
                );

                adaptor.addChild(root_1, stream_outSerde.nextTree());

                adaptor.addChild(root_1, stream_outRec.nextTree());

                // SelectClauseParser.g:80:93: ( aliasList )?
                if ( stream_aliasList.hasNext() ) {
                    adaptor.addChild(root_1, stream_aliasList.nextTree());

                }
                stream_aliasList.reset();

                // SelectClauseParser.g:80:104: ( columnNameTypeList )?
                if ( stream_columnNameTypeList.hasNext() ) {
                    adaptor.addChild(root_1, stream_columnNameTypeList.nextTree());

                }
                stream_columnNameTypeList.reset();

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
    // $ANTLR end "selectTrfmClause"


    public static class hintClause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "hintClause"
    // SelectClauseParser.g:83:1: hintClause : DIVIDE STAR PLUS hintList STAR DIVIDE -> ^( TOK_HINTLIST hintList ) ;
    public final HiveParser_SelectClauseParser.hintClause_return hintClause() throws RecognitionException {
        HiveParser_SelectClauseParser.hintClause_return retval = new HiveParser_SelectClauseParser.hintClause_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token DIVIDE22=null;
        Token STAR23=null;
        Token PLUS24=null;
        Token STAR26=null;
        Token DIVIDE27=null;
        HiveParser_SelectClauseParser.hintList_return hintList25 =null;


        CommonTree DIVIDE22_tree=null;
        CommonTree STAR23_tree=null;
        CommonTree PLUS24_tree=null;
        CommonTree STAR26_tree=null;
        CommonTree DIVIDE27_tree=null;
        RewriteRuleTokenStream stream_STAR=new RewriteRuleTokenStream(adaptor,"token STAR");
        RewriteRuleTokenStream stream_DIVIDE=new RewriteRuleTokenStream(adaptor,"token DIVIDE");
        RewriteRuleTokenStream stream_PLUS=new RewriteRuleTokenStream(adaptor,"token PLUS");
        RewriteRuleSubtreeStream stream_hintList=new RewriteRuleSubtreeStream(adaptor,"rule hintList");
         gParent.pushMsg("hint clause", state); 
        try {
            // SelectClauseParser.g:86:5: ( DIVIDE STAR PLUS hintList STAR DIVIDE -> ^( TOK_HINTLIST hintList ) )
            // SelectClauseParser.g:87:5: DIVIDE STAR PLUS hintList STAR DIVIDE
            {
            DIVIDE22=(Token)match(input,DIVIDE,FOLLOW_DIVIDE_in_hintClause420); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_DIVIDE.add(DIVIDE22);


            STAR23=(Token)match(input,STAR,FOLLOW_STAR_in_hintClause422); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_STAR.add(STAR23);


            PLUS24=(Token)match(input,PLUS,FOLLOW_PLUS_in_hintClause424); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_PLUS.add(PLUS24);


            pushFollow(FOLLOW_hintList_in_hintClause426);
            hintList25=hintList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_hintList.add(hintList25.getTree());

            STAR26=(Token)match(input,STAR,FOLLOW_STAR_in_hintClause428); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_STAR.add(STAR26);


            DIVIDE27=(Token)match(input,DIVIDE,FOLLOW_DIVIDE_in_hintClause430); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_DIVIDE.add(DIVIDE27);


            // AST REWRITE
            // elements: hintList
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 87:43: -> ^( TOK_HINTLIST hintList )
            {
                // SelectClauseParser.g:87:46: ^( TOK_HINTLIST hintList )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(TOK_HINTLIST, "TOK_HINTLIST")
                , root_1);

                adaptor.addChild(root_1, stream_hintList.nextTree());

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
    // $ANTLR end "hintClause"


    public static class hintList_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "hintList"
    // SelectClauseParser.g:90:1: hintList : hintItem ( COMMA hintItem )* -> ( hintItem )+ ;
    public final HiveParser_SelectClauseParser.hintList_return hintList() throws RecognitionException {
        HiveParser_SelectClauseParser.hintList_return retval = new HiveParser_SelectClauseParser.hintList_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token COMMA29=null;
        HiveParser_SelectClauseParser.hintItem_return hintItem28 =null;

        HiveParser_SelectClauseParser.hintItem_return hintItem30 =null;


        CommonTree COMMA29_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_hintItem=new RewriteRuleSubtreeStream(adaptor,"rule hintItem");
         gParent.pushMsg("hint list", state); 
        try {
            // SelectClauseParser.g:93:5: ( hintItem ( COMMA hintItem )* -> ( hintItem )+ )
            // SelectClauseParser.g:94:5: hintItem ( COMMA hintItem )*
            {
            pushFollow(FOLLOW_hintItem_in_hintList469);
            hintItem28=hintItem();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_hintItem.add(hintItem28.getTree());

            // SelectClauseParser.g:94:14: ( COMMA hintItem )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==COMMA) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // SelectClauseParser.g:94:15: COMMA hintItem
            	    {
            	    COMMA29=(Token)match(input,COMMA,FOLLOW_COMMA_in_hintList472); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA29);


            	    pushFollow(FOLLOW_hintItem_in_hintList474);
            	    hintItem30=hintItem();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_hintItem.add(hintItem30.getTree());

            	    }
            	    break;

            	default :
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
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 94:32: -> ( hintItem )+
            {
                if ( !(stream_hintItem.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_hintItem.hasNext() ) {
                    adaptor.addChild(root_0, stream_hintItem.nextTree());

                }
                stream_hintItem.reset();

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
    // $ANTLR end "hintList"


    public static class hintItem_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "hintItem"
    // SelectClauseParser.g:97:1: hintItem : hintName ( LPAREN hintArgs RPAREN )? -> ^( TOK_HINT hintName ( hintArgs )? ) ;
    public final HiveParser_SelectClauseParser.hintItem_return hintItem() throws RecognitionException {
        HiveParser_SelectClauseParser.hintItem_return retval = new HiveParser_SelectClauseParser.hintItem_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token LPAREN32=null;
        Token RPAREN34=null;
        HiveParser_SelectClauseParser.hintName_return hintName31 =null;

        HiveParser_SelectClauseParser.hintArgs_return hintArgs33 =null;


        CommonTree LPAREN32_tree=null;
        CommonTree RPAREN34_tree=null;
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleSubtreeStream stream_hintName=new RewriteRuleSubtreeStream(adaptor,"rule hintName");
        RewriteRuleSubtreeStream stream_hintArgs=new RewriteRuleSubtreeStream(adaptor,"rule hintArgs");
         gParent.pushMsg("hint item", state); 
        try {
            // SelectClauseParser.g:100:5: ( hintName ( LPAREN hintArgs RPAREN )? -> ^( TOK_HINT hintName ( hintArgs )? ) )
            // SelectClauseParser.g:101:5: hintName ( LPAREN hintArgs RPAREN )?
            {
            pushFollow(FOLLOW_hintName_in_hintItem512);
            hintName31=hintName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_hintName.add(hintName31.getTree());

            // SelectClauseParser.g:101:14: ( LPAREN hintArgs RPAREN )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==LPAREN) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // SelectClauseParser.g:101:15: LPAREN hintArgs RPAREN
                    {
                    LPAREN32=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_hintItem515); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN32);


                    pushFollow(FOLLOW_hintArgs_in_hintItem517);
                    hintArgs33=hintArgs();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_hintArgs.add(hintArgs33.getTree());

                    RPAREN34=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_hintItem519); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN34);


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
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 101:40: -> ^( TOK_HINT hintName ( hintArgs )? )
            {
                // SelectClauseParser.g:101:43: ^( TOK_HINT hintName ( hintArgs )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(TOK_HINT, "TOK_HINT")
                , root_1);

                adaptor.addChild(root_1, stream_hintName.nextTree());

                // SelectClauseParser.g:101:63: ( hintArgs )?
                if ( stream_hintArgs.hasNext() ) {
                    adaptor.addChild(root_1, stream_hintArgs.nextTree());

                }
                stream_hintArgs.reset();

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
    // $ANTLR end "hintItem"


    public static class hintName_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "hintName"
    // SelectClauseParser.g:104:1: hintName : ( KW_MAPJOIN -> TOK_MAPJOIN | KW_STREAMTABLE -> TOK_STREAMTABLE | KW_HOLD_DDLTIME -> TOK_HOLD_DDLTIME );
    public final HiveParser_SelectClauseParser.hintName_return hintName() throws RecognitionException {
        HiveParser_SelectClauseParser.hintName_return retval = new HiveParser_SelectClauseParser.hintName_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token KW_MAPJOIN35=null;
        Token KW_STREAMTABLE36=null;
        Token KW_HOLD_DDLTIME37=null;

        CommonTree KW_MAPJOIN35_tree=null;
        CommonTree KW_STREAMTABLE36_tree=null;
        CommonTree KW_HOLD_DDLTIME37_tree=null;
        RewriteRuleTokenStream stream_KW_MAPJOIN=new RewriteRuleTokenStream(adaptor,"token KW_MAPJOIN");
        RewriteRuleTokenStream stream_KW_STREAMTABLE=new RewriteRuleTokenStream(adaptor,"token KW_STREAMTABLE");
        RewriteRuleTokenStream stream_KW_HOLD_DDLTIME=new RewriteRuleTokenStream(adaptor,"token KW_HOLD_DDLTIME");

         gParent.pushMsg("hint name", state); 
        try {
            // SelectClauseParser.g:107:5: ( KW_MAPJOIN -> TOK_MAPJOIN | KW_STREAMTABLE -> TOK_STREAMTABLE | KW_HOLD_DDLTIME -> TOK_HOLD_DDLTIME )
            int alt12=3;
            switch ( input.LA(1) ) {
            case KW_MAPJOIN:
                {
                alt12=1;
                }
                break;
            case KW_STREAMTABLE:
                {
                alt12=2;
                }
                break;
            case KW_HOLD_DDLTIME:
                {
                alt12=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;

            }

            switch (alt12) {
                case 1 :
                    // SelectClauseParser.g:108:5: KW_MAPJOIN
                    {
                    KW_MAPJOIN35=(Token)match(input,KW_MAPJOIN,FOLLOW_KW_MAPJOIN_in_hintName563); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_MAPJOIN.add(KW_MAPJOIN35);


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
                    // 108:16: -> TOK_MAPJOIN
                    {
                        adaptor.addChild(root_0, 
                        (CommonTree)adaptor.create(TOK_MAPJOIN, "TOK_MAPJOIN")
                        );

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 2 :
                    // SelectClauseParser.g:109:7: KW_STREAMTABLE
                    {
                    KW_STREAMTABLE36=(Token)match(input,KW_STREAMTABLE,FOLLOW_KW_STREAMTABLE_in_hintName575); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_STREAMTABLE.add(KW_STREAMTABLE36);


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
                    // 109:22: -> TOK_STREAMTABLE
                    {
                        adaptor.addChild(root_0, 
                        (CommonTree)adaptor.create(TOK_STREAMTABLE, "TOK_STREAMTABLE")
                        );

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 3 :
                    // SelectClauseParser.g:110:7: KW_HOLD_DDLTIME
                    {
                    KW_HOLD_DDLTIME37=(Token)match(input,KW_HOLD_DDLTIME,FOLLOW_KW_HOLD_DDLTIME_in_hintName587); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_HOLD_DDLTIME.add(KW_HOLD_DDLTIME37);


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
                    // 110:23: -> TOK_HOLD_DDLTIME
                    {
                        adaptor.addChild(root_0, 
                        (CommonTree)adaptor.create(TOK_HOLD_DDLTIME, "TOK_HOLD_DDLTIME")
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
    // $ANTLR end "hintName"


    public static class hintArgs_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "hintArgs"
    // SelectClauseParser.g:113:1: hintArgs : hintArgName ( COMMA hintArgName )* -> ^( TOK_HINTARGLIST ( hintArgName )+ ) ;
    public final HiveParser_SelectClauseParser.hintArgs_return hintArgs() throws RecognitionException {
        HiveParser_SelectClauseParser.hintArgs_return retval = new HiveParser_SelectClauseParser.hintArgs_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token COMMA39=null;
        HiveParser_SelectClauseParser.hintArgName_return hintArgName38 =null;

        HiveParser_SelectClauseParser.hintArgName_return hintArgName40 =null;


        CommonTree COMMA39_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_hintArgName=new RewriteRuleSubtreeStream(adaptor,"rule hintArgName");
         gParent.pushMsg("hint arguments", state); 
        try {
            // SelectClauseParser.g:116:5: ( hintArgName ( COMMA hintArgName )* -> ^( TOK_HINTARGLIST ( hintArgName )+ ) )
            // SelectClauseParser.g:117:5: hintArgName ( COMMA hintArgName )*
            {
            pushFollow(FOLLOW_hintArgName_in_hintArgs622);
            hintArgName38=hintArgName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_hintArgName.add(hintArgName38.getTree());

            // SelectClauseParser.g:117:17: ( COMMA hintArgName )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==COMMA) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // SelectClauseParser.g:117:18: COMMA hintArgName
            	    {
            	    COMMA39=(Token)match(input,COMMA,FOLLOW_COMMA_in_hintArgs625); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA39);


            	    pushFollow(FOLLOW_hintArgName_in_hintArgs627);
            	    hintArgName40=hintArgName();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_hintArgName.add(hintArgName40.getTree());

            	    }
            	    break;

            	default :
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
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 117:38: -> ^( TOK_HINTARGLIST ( hintArgName )+ )
            {
                // SelectClauseParser.g:117:41: ^( TOK_HINTARGLIST ( hintArgName )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(TOK_HINTARGLIST, "TOK_HINTARGLIST")
                , root_1);

                if ( !(stream_hintArgName.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_hintArgName.hasNext() ) {
                    adaptor.addChild(root_1, stream_hintArgName.nextTree());

                }
                stream_hintArgName.reset();

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
    // $ANTLR end "hintArgs"


    public static class hintArgName_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "hintArgName"
    // SelectClauseParser.g:120:1: hintArgName : identifier ;
    public final HiveParser_SelectClauseParser.hintArgName_return hintArgName() throws RecognitionException {
        HiveParser_SelectClauseParser.hintArgName_return retval = new HiveParser_SelectClauseParser.hintArgName_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        HiveParser_IdentifiersParser.identifier_return identifier41 =null;



         gParent.pushMsg("hint argument name", state); 
        try {
            // SelectClauseParser.g:123:5: ( identifier )
            // SelectClauseParser.g:124:5: identifier
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_identifier_in_hintArgName669);
            identifier41=gHiveParser.identifier();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, identifier41.getTree());

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
    // $ANTLR end "hintArgName"


    public static class selectItem_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "selectItem"
    // SelectClauseParser.g:127:1: selectItem : ( ( tableAllColumns )=> tableAllColumns -> ^( TOK_SELEXPR tableAllColumns ) | ( expression ( ( ( KW_AS )? identifier ) | ( KW_AS LPAREN identifier ( COMMA identifier )* RPAREN ) )? ) -> ^( TOK_SELEXPR expression ( identifier )* ) );
    public final HiveParser_SelectClauseParser.selectItem_return selectItem() throws RecognitionException {
        HiveParser_SelectClauseParser.selectItem_return retval = new HiveParser_SelectClauseParser.selectItem_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token KW_AS44=null;
        Token KW_AS46=null;
        Token LPAREN47=null;
        Token COMMA49=null;
        Token RPAREN51=null;
        HiveParser_FromClauseParser.tableAllColumns_return tableAllColumns42 =null;

        HiveParser_IdentifiersParser.expression_return expression43 =null;

        HiveParser_IdentifiersParser.identifier_return identifier45 =null;

        HiveParser_IdentifiersParser.identifier_return identifier48 =null;

        HiveParser_IdentifiersParser.identifier_return identifier50 =null;


        CommonTree KW_AS44_tree=null;
        CommonTree KW_AS46_tree=null;
        CommonTree LPAREN47_tree=null;
        CommonTree COMMA49_tree=null;
        CommonTree RPAREN51_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_KW_AS=new RewriteRuleTokenStream(adaptor,"token KW_AS");
        RewriteRuleSubtreeStream stream_identifier=new RewriteRuleSubtreeStream(adaptor,"rule identifier");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        RewriteRuleSubtreeStream stream_tableAllColumns=new RewriteRuleSubtreeStream(adaptor,"rule tableAllColumns");
         gParent.pushMsg("selection target", state); 
        try {
            // SelectClauseParser.g:130:5: ( ( tableAllColumns )=> tableAllColumns -> ^( TOK_SELEXPR tableAllColumns ) | ( expression ( ( ( KW_AS )? identifier ) | ( KW_AS LPAREN identifier ( COMMA identifier )* RPAREN ) )? ) -> ^( TOK_SELEXPR expression ( identifier )* ) )
            int alt17=2;
            alt17 = dfa17.predict(input);
            switch (alt17) {
                case 1 :
                    // SelectClauseParser.g:131:5: ( tableAllColumns )=> tableAllColumns
                    {
                    pushFollow(FOLLOW_tableAllColumns_in_selectItem706);
                    tableAllColumns42=gHiveParser.tableAllColumns();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_tableAllColumns.add(tableAllColumns42.getTree());

                    // AST REWRITE
                    // elements: tableAllColumns
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 131:42: -> ^( TOK_SELEXPR tableAllColumns )
                    {
                        // SelectClauseParser.g:131:45: ^( TOK_SELEXPR tableAllColumns )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(TOK_SELEXPR, "TOK_SELEXPR")
                        , root_1);

                        adaptor.addChild(root_1, stream_tableAllColumns.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 2 :
                    // SelectClauseParser.g:133:5: ( expression ( ( ( KW_AS )? identifier ) | ( KW_AS LPAREN identifier ( COMMA identifier )* RPAREN ) )? )
                    {
                    // SelectClauseParser.g:133:5: ( expression ( ( ( KW_AS )? identifier ) | ( KW_AS LPAREN identifier ( COMMA identifier )* RPAREN ) )? )
                    // SelectClauseParser.g:133:7: expression ( ( ( KW_AS )? identifier ) | ( KW_AS LPAREN identifier ( COMMA identifier )* RPAREN ) )?
                    {
                    pushFollow(FOLLOW_expression_in_selectItem728);
                    expression43=gHiveParser.expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression43.getTree());

                    // SelectClauseParser.g:134:7: ( ( ( KW_AS )? identifier ) | ( KW_AS LPAREN identifier ( COMMA identifier )* RPAREN ) )?
                    int alt16=3;
                    alt16 = dfa16.predict(input);
                    switch (alt16) {
                        case 1 :
                            // SelectClauseParser.g:134:8: ( ( KW_AS )? identifier )
                            {
                            // SelectClauseParser.g:134:8: ( ( KW_AS )? identifier )
                            // SelectClauseParser.g:134:9: ( KW_AS )? identifier
                            {
                            // SelectClauseParser.g:134:9: ( KW_AS )?
                            int alt14=2;
                            alt14 = dfa14.predict(input);
                            switch (alt14) {
                                case 1 :
                                    // SelectClauseParser.g:134:9: KW_AS
                                    {
                                    KW_AS44=(Token)match(input,KW_AS,FOLLOW_KW_AS_in_selectItem738); if (state.failed) return retval; 
                                    if ( state.backtracking==0 ) stream_KW_AS.add(KW_AS44);


                                    }
                                    break;

                            }


                            pushFollow(FOLLOW_identifier_in_selectItem741);
                            identifier45=gHiveParser.identifier();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_identifier.add(identifier45.getTree());

                            }


                            }
                            break;
                        case 2 :
                            // SelectClauseParser.g:134:30: ( KW_AS LPAREN identifier ( COMMA identifier )* RPAREN )
                            {
                            // SelectClauseParser.g:134:30: ( KW_AS LPAREN identifier ( COMMA identifier )* RPAREN )
                            // SelectClauseParser.g:134:31: KW_AS LPAREN identifier ( COMMA identifier )* RPAREN
                            {
                            KW_AS46=(Token)match(input,KW_AS,FOLLOW_KW_AS_in_selectItem747); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_KW_AS.add(KW_AS46);


                            LPAREN47=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_selectItem749); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN47);


                            pushFollow(FOLLOW_identifier_in_selectItem751);
                            identifier48=gHiveParser.identifier();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_identifier.add(identifier48.getTree());

                            // SelectClauseParser.g:134:55: ( COMMA identifier )*
                            loop15:
                            do {
                                int alt15=2;
                                int LA15_0 = input.LA(1);

                                if ( (LA15_0==COMMA) ) {
                                    alt15=1;
                                }


                                switch (alt15) {
                            	case 1 :
                            	    // SelectClauseParser.g:134:56: COMMA identifier
                            	    {
                            	    COMMA49=(Token)match(input,COMMA,FOLLOW_COMMA_in_selectItem754); if (state.failed) return retval; 
                            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA49);


                            	    pushFollow(FOLLOW_identifier_in_selectItem756);
                            	    identifier50=gHiveParser.identifier();

                            	    state._fsp--;
                            	    if (state.failed) return retval;
                            	    if ( state.backtracking==0 ) stream_identifier.add(identifier50.getTree());

                            	    }
                            	    break;

                            	default :
                            	    break loop15;
                                }
                            } while (true);


                            RPAREN51=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_selectItem760); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN51);


                            }


                            }
                            break;

                    }


                    }


                    // AST REWRITE
                    // elements: identifier, expression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 135:7: -> ^( TOK_SELEXPR expression ( identifier )* )
                    {
                        // SelectClauseParser.g:135:10: ^( TOK_SELEXPR expression ( identifier )* )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(TOK_SELEXPR, "TOK_SELEXPR")
                        , root_1);

                        adaptor.addChild(root_1, stream_expression.nextTree());

                        // SelectClauseParser.g:135:35: ( identifier )*
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
    // $ANTLR end "selectItem"


    public static class trfmClause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "trfmClause"
    // SelectClauseParser.g:138:1: trfmClause : ( KW_MAP selectExpressionList | KW_REDUCE selectExpressionList ) inSerde= rowFormat inRec= recordWriter KW_USING StringLiteral ( KW_AS ( ( LPAREN ( aliasList | columnNameTypeList ) RPAREN ) | ( aliasList | columnNameTypeList ) ) )? outSerde= rowFormat outRec= recordReader -> ^( TOK_TRANSFORM selectExpressionList $inSerde $inRec StringLiteral $outSerde $outRec ( aliasList )? ( columnNameTypeList )? ) ;
    public final HiveParser_SelectClauseParser.trfmClause_return trfmClause() throws RecognitionException {
        HiveParser_SelectClauseParser.trfmClause_return retval = new HiveParser_SelectClauseParser.trfmClause_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token KW_MAP52=null;
        Token KW_REDUCE54=null;
        Token KW_USING56=null;
        Token StringLiteral57=null;
        Token KW_AS58=null;
        Token LPAREN59=null;
        Token RPAREN62=null;
        HiveParser.rowFormat_return inSerde =null;

        HiveParser.recordWriter_return inRec =null;

        HiveParser.rowFormat_return outSerde =null;

        HiveParser.recordReader_return outRec =null;

        HiveParser_SelectClauseParser.selectExpressionList_return selectExpressionList53 =null;

        HiveParser_SelectClauseParser.selectExpressionList_return selectExpressionList55 =null;

        HiveParser_FromClauseParser.aliasList_return aliasList60 =null;

        HiveParser.columnNameTypeList_return columnNameTypeList61 =null;

        HiveParser_FromClauseParser.aliasList_return aliasList63 =null;

        HiveParser.columnNameTypeList_return columnNameTypeList64 =null;


        CommonTree KW_MAP52_tree=null;
        CommonTree KW_REDUCE54_tree=null;
        CommonTree KW_USING56_tree=null;
        CommonTree StringLiteral57_tree=null;
        CommonTree KW_AS58_tree=null;
        CommonTree LPAREN59_tree=null;
        CommonTree RPAREN62_tree=null;
        RewriteRuleTokenStream stream_StringLiteral=new RewriteRuleTokenStream(adaptor,"token StringLiteral");
        RewriteRuleTokenStream stream_KW_REDUCE=new RewriteRuleTokenStream(adaptor,"token KW_REDUCE");
        RewriteRuleTokenStream stream_KW_USING=new RewriteRuleTokenStream(adaptor,"token KW_USING");
        RewriteRuleTokenStream stream_KW_MAP=new RewriteRuleTokenStream(adaptor,"token KW_MAP");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_KW_AS=new RewriteRuleTokenStream(adaptor,"token KW_AS");
        RewriteRuleSubtreeStream stream_aliasList=new RewriteRuleSubtreeStream(adaptor,"rule aliasList");
        RewriteRuleSubtreeStream stream_rowFormat=new RewriteRuleSubtreeStream(adaptor,"rule rowFormat");
        RewriteRuleSubtreeStream stream_columnNameTypeList=new RewriteRuleSubtreeStream(adaptor,"rule columnNameTypeList");
        RewriteRuleSubtreeStream stream_recordReader=new RewriteRuleSubtreeStream(adaptor,"rule recordReader");
        RewriteRuleSubtreeStream stream_selectExpressionList=new RewriteRuleSubtreeStream(adaptor,"rule selectExpressionList");
        RewriteRuleSubtreeStream stream_recordWriter=new RewriteRuleSubtreeStream(adaptor,"rule recordWriter");
         gParent.pushMsg("transform clause", state); 
        try {
            // SelectClauseParser.g:141:5: ( ( KW_MAP selectExpressionList | KW_REDUCE selectExpressionList ) inSerde= rowFormat inRec= recordWriter KW_USING StringLiteral ( KW_AS ( ( LPAREN ( aliasList | columnNameTypeList ) RPAREN ) | ( aliasList | columnNameTypeList ) ) )? outSerde= rowFormat outRec= recordReader -> ^( TOK_TRANSFORM selectExpressionList $inSerde $inRec StringLiteral $outSerde $outRec ( aliasList )? ( columnNameTypeList )? ) )
            // SelectClauseParser.g:142:5: ( KW_MAP selectExpressionList | KW_REDUCE selectExpressionList ) inSerde= rowFormat inRec= recordWriter KW_USING StringLiteral ( KW_AS ( ( LPAREN ( aliasList | columnNameTypeList ) RPAREN ) | ( aliasList | columnNameTypeList ) ) )? outSerde= rowFormat outRec= recordReader
            {
            // SelectClauseParser.g:142:5: ( KW_MAP selectExpressionList | KW_REDUCE selectExpressionList )
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==KW_MAP) ) {
                alt18=1;
            }
            else if ( (LA18_0==KW_REDUCE) ) {
                alt18=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;

            }
            switch (alt18) {
                case 1 :
                    // SelectClauseParser.g:142:9: KW_MAP selectExpressionList
                    {
                    KW_MAP52=(Token)match(input,KW_MAP,FOLLOW_KW_MAP_in_trfmClause815); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_MAP.add(KW_MAP52);


                    pushFollow(FOLLOW_selectExpressionList_in_trfmClause820);
                    selectExpressionList53=selectExpressionList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_selectExpressionList.add(selectExpressionList53.getTree());

                    }
                    break;
                case 2 :
                    // SelectClauseParser.g:143:9: KW_REDUCE selectExpressionList
                    {
                    KW_REDUCE54=(Token)match(input,KW_REDUCE,FOLLOW_KW_REDUCE_in_trfmClause830); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_REDUCE.add(KW_REDUCE54);


                    pushFollow(FOLLOW_selectExpressionList_in_trfmClause832);
                    selectExpressionList55=selectExpressionList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_selectExpressionList.add(selectExpressionList55.getTree());

                    }
                    break;

            }


            pushFollow(FOLLOW_rowFormat_in_trfmClause842);
            inSerde=gHiveParser.rowFormat();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rowFormat.add(inSerde.getTree());

            pushFollow(FOLLOW_recordWriter_in_trfmClause846);
            inRec=gHiveParser.recordWriter();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_recordWriter.add(inRec.getTree());

            KW_USING56=(Token)match(input,KW_USING,FOLLOW_KW_USING_in_trfmClause852); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_KW_USING.add(KW_USING56);


            StringLiteral57=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_trfmClause854); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_StringLiteral.add(StringLiteral57);


            // SelectClauseParser.g:146:5: ( KW_AS ( ( LPAREN ( aliasList | columnNameTypeList ) RPAREN ) | ( aliasList | columnNameTypeList ) ) )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==KW_AS) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // SelectClauseParser.g:146:7: KW_AS ( ( LPAREN ( aliasList | columnNameTypeList ) RPAREN ) | ( aliasList | columnNameTypeList ) )
                    {
                    KW_AS58=(Token)match(input,KW_AS,FOLLOW_KW_AS_in_trfmClause862); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_AS.add(KW_AS58);


                    // SelectClauseParser.g:146:13: ( ( LPAREN ( aliasList | columnNameTypeList ) RPAREN ) | ( aliasList | columnNameTypeList ) )
                    int alt21=2;
                    int LA21_0 = input.LA(1);

                    if ( (LA21_0==LPAREN) ) {
                        alt21=1;
                    }
                    else if ( ((LA21_0 >= Identifier && LA21_0 <= KW_ANALYZE)||(LA21_0 >= KW_ARCHIVE && LA21_0 <= KW_CASCADE)||LA21_0==KW_CHANGE||(LA21_0 >= KW_CLUSTER && LA21_0 <= KW_COLLECTION)||(LA21_0 >= KW_COLUMNS && LA21_0 <= KW_CONCATENATE)||(LA21_0 >= KW_CONTINUE && LA21_0 <= KW_CREATE)||LA21_0==KW_CUBE||(LA21_0 >= KW_CURRENT_DATE && LA21_0 <= KW_DATA)||(LA21_0 >= KW_DATABASES && LA21_0 <= KW_DISABLE)||(LA21_0 >= KW_DISTRIBUTE && LA21_0 <= KW_ELEM_TYPE)||LA21_0==KW_ENABLE||LA21_0==KW_ESCAPED||(LA21_0 >= KW_EXCLUSIVE && LA21_0 <= KW_EXPORT)||(LA21_0 >= KW_EXTERNAL && LA21_0 <= KW_FLOAT)||(LA21_0 >= KW_FOR && LA21_0 <= KW_FORMATTED)||LA21_0==KW_FULL||(LA21_0 >= KW_FUNCTIONS && LA21_0 <= KW_GROUPING)||(LA21_0 >= KW_HOLD_DDLTIME && LA21_0 <= KW_IDXPROPERTIES)||(LA21_0 >= KW_IGNORE && LA21_0 <= KW_INTERSECT)||(LA21_0 >= KW_INTO && LA21_0 <= KW_JAR)||(LA21_0 >= KW_KEYS && LA21_0 <= KW_LEFT)||(LA21_0 >= KW_LIKE && LA21_0 <= KW_LONG)||(LA21_0 >= KW_MAPJOIN && LA21_0 <= KW_MONTH)||(LA21_0 >= KW_MSCK && LA21_0 <= KW_NOSCAN)||(LA21_0 >= KW_NO_DROP && LA21_0 <= KW_OFFLINE)||LA21_0==KW_OPTION||(LA21_0 >= KW_ORDER && LA21_0 <= KW_OUTPUTFORMAT)||(LA21_0 >= KW_OVERWRITE && LA21_0 <= KW_OWNER)||(LA21_0 >= KW_PARTITION && LA21_0 <= KW_PLUS)||(LA21_0 >= KW_PRETTY && LA21_0 <= KW_RECORDWRITER)||(LA21_0 >= KW_REGEXP && LA21_0 <= KW_SECOND)||(LA21_0 >= KW_SEMI && LA21_0 <= KW_TABLES)||(LA21_0 >= KW_TBLPROPERTIES && LA21_0 <= KW_TERMINATED)||(LA21_0 >= KW_TIMESTAMP && LA21_0 <= KW_TRANSACTIONS)||(LA21_0 >= KW_TRIGGER && LA21_0 <= KW_UNARCHIVE)||(LA21_0 >= KW_UNDO && LA21_0 <= KW_UNIONTYPE)||(LA21_0 >= KW_UNLOCK && LA21_0 <= KW_VALUE_TYPE)||LA21_0==KW_VIEW||LA21_0==KW_WHILE||(LA21_0 >= KW_WITH && LA21_0 <= KW_YEAR)) ) {
                        alt21=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 21, 0, input);

                        throw nvae;

                    }
                    switch (alt21) {
                        case 1 :
                            // SelectClauseParser.g:146:14: ( LPAREN ( aliasList | columnNameTypeList ) RPAREN )
                            {
                            // SelectClauseParser.g:146:14: ( LPAREN ( aliasList | columnNameTypeList ) RPAREN )
                            // SelectClauseParser.g:146:15: LPAREN ( aliasList | columnNameTypeList ) RPAREN
                            {
                            LPAREN59=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_trfmClause866); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN59);


                            // SelectClauseParser.g:146:22: ( aliasList | columnNameTypeList )
                            int alt19=2;
                            alt19 = dfa19.predict(input);
                            switch (alt19) {
                                case 1 :
                                    // SelectClauseParser.g:146:23: aliasList
                                    {
                                    pushFollow(FOLLOW_aliasList_in_trfmClause869);
                                    aliasList60=gHiveParser.aliasList();

                                    state._fsp--;
                                    if (state.failed) return retval;
                                    if ( state.backtracking==0 ) stream_aliasList.add(aliasList60.getTree());

                                    }
                                    break;
                                case 2 :
                                    // SelectClauseParser.g:146:35: columnNameTypeList
                                    {
                                    pushFollow(FOLLOW_columnNameTypeList_in_trfmClause873);
                                    columnNameTypeList61=gHiveParser.columnNameTypeList();

                                    state._fsp--;
                                    if (state.failed) return retval;
                                    if ( state.backtracking==0 ) stream_columnNameTypeList.add(columnNameTypeList61.getTree());

                                    }
                                    break;

                            }


                            RPAREN62=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_trfmClause876); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN62);


                            }


                            }
                            break;
                        case 2 :
                            // SelectClauseParser.g:146:65: ( aliasList | columnNameTypeList )
                            {
                            // SelectClauseParser.g:146:65: ( aliasList | columnNameTypeList )
                            int alt20=2;
                            alt20 = dfa20.predict(input);
                            switch (alt20) {
                                case 1 :
                                    // SelectClauseParser.g:146:66: aliasList
                                    {
                                    pushFollow(FOLLOW_aliasList_in_trfmClause882);
                                    aliasList63=gHiveParser.aliasList();

                                    state._fsp--;
                                    if (state.failed) return retval;
                                    if ( state.backtracking==0 ) stream_aliasList.add(aliasList63.getTree());

                                    }
                                    break;
                                case 2 :
                                    // SelectClauseParser.g:146:78: columnNameTypeList
                                    {
                                    pushFollow(FOLLOW_columnNameTypeList_in_trfmClause886);
                                    columnNameTypeList64=gHiveParser.columnNameTypeList();

                                    state._fsp--;
                                    if (state.failed) return retval;
                                    if ( state.backtracking==0 ) stream_columnNameTypeList.add(columnNameTypeList64.getTree());

                                    }
                                    break;

                            }


                            }
                            break;

                    }


                    }
                    break;

            }


            pushFollow(FOLLOW_rowFormat_in_trfmClause898);
            outSerde=gHiveParser.rowFormat();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rowFormat.add(outSerde.getTree());

            pushFollow(FOLLOW_recordReader_in_trfmClause902);
            outRec=gHiveParser.recordReader();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_recordReader.add(outRec.getTree());

            // AST REWRITE
            // elements: inRec, StringLiteral, outSerde, columnNameTypeList, aliasList, outRec, selectExpressionList, inSerde
            // token labels: 
            // rule labels: inRec, outRec, inSerde, outSerde, retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_inRec=new RewriteRuleSubtreeStream(adaptor,"rule inRec",inRec!=null?inRec.tree:null);
            RewriteRuleSubtreeStream stream_outRec=new RewriteRuleSubtreeStream(adaptor,"rule outRec",outRec!=null?outRec.tree:null);
            RewriteRuleSubtreeStream stream_inSerde=new RewriteRuleSubtreeStream(adaptor,"rule inSerde",inSerde!=null?inSerde.tree:null);
            RewriteRuleSubtreeStream stream_outSerde=new RewriteRuleSubtreeStream(adaptor,"rule outSerde",outSerde!=null?outSerde.tree:null);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 148:5: -> ^( TOK_TRANSFORM selectExpressionList $inSerde $inRec StringLiteral $outSerde $outRec ( aliasList )? ( columnNameTypeList )? )
            {
                // SelectClauseParser.g:148:8: ^( TOK_TRANSFORM selectExpressionList $inSerde $inRec StringLiteral $outSerde $outRec ( aliasList )? ( columnNameTypeList )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(TOK_TRANSFORM, "TOK_TRANSFORM")
                , root_1);

                adaptor.addChild(root_1, stream_selectExpressionList.nextTree());

                adaptor.addChild(root_1, stream_inSerde.nextTree());

                adaptor.addChild(root_1, stream_inRec.nextTree());

                adaptor.addChild(root_1, 
                stream_StringLiteral.nextNode()
                );

                adaptor.addChild(root_1, stream_outSerde.nextTree());

                adaptor.addChild(root_1, stream_outRec.nextTree());

                // SelectClauseParser.g:148:93: ( aliasList )?
                if ( stream_aliasList.hasNext() ) {
                    adaptor.addChild(root_1, stream_aliasList.nextTree());

                }
                stream_aliasList.reset();

                // SelectClauseParser.g:148:104: ( columnNameTypeList )?
                if ( stream_columnNameTypeList.hasNext() ) {
                    adaptor.addChild(root_1, stream_columnNameTypeList.nextTree());

                }
                stream_columnNameTypeList.reset();

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
    // $ANTLR end "trfmClause"


    public static class selectExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "selectExpression"
    // SelectClauseParser.g:151:1: selectExpression : ( ( tableAllColumns )=> tableAllColumns | expression );
    public final HiveParser_SelectClauseParser.selectExpression_return selectExpression() throws RecognitionException {
        HiveParser_SelectClauseParser.selectExpression_return retval = new HiveParser_SelectClauseParser.selectExpression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        HiveParser_FromClauseParser.tableAllColumns_return tableAllColumns65 =null;

        HiveParser_IdentifiersParser.expression_return expression66 =null;



         gParent.pushMsg("select expression", state); 
        try {
            // SelectClauseParser.g:154:5: ( ( tableAllColumns )=> tableAllColumns | expression )
            int alt23=2;
            alt23 = dfa23.predict(input);
            switch (alt23) {
                case 1 :
                    // SelectClauseParser.g:155:5: ( tableAllColumns )=> tableAllColumns
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_tableAllColumns_in_selectExpression971);
                    tableAllColumns65=gHiveParser.tableAllColumns();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, tableAllColumns65.getTree());

                    }
                    break;
                case 2 :
                    // SelectClauseParser.g:157:5: expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_expression_in_selectExpression983);
                    expression66=gHiveParser.expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression66.getTree());

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
    // $ANTLR end "selectExpression"


    public static class selectExpressionList_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "selectExpressionList"
    // SelectClauseParser.g:160:1: selectExpressionList : selectExpression ( COMMA selectExpression )* -> ^( TOK_EXPLIST ( selectExpression )+ ) ;
    public final HiveParser_SelectClauseParser.selectExpressionList_return selectExpressionList() throws RecognitionException {
        HiveParser_SelectClauseParser.selectExpressionList_return retval = new HiveParser_SelectClauseParser.selectExpressionList_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token COMMA68=null;
        HiveParser_SelectClauseParser.selectExpression_return selectExpression67 =null;

        HiveParser_SelectClauseParser.selectExpression_return selectExpression69 =null;


        CommonTree COMMA68_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_selectExpression=new RewriteRuleSubtreeStream(adaptor,"rule selectExpression");
         gParent.pushMsg("select expression list", state); 
        try {
            // SelectClauseParser.g:163:5: ( selectExpression ( COMMA selectExpression )* -> ^( TOK_EXPLIST ( selectExpression )+ ) )
            // SelectClauseParser.g:164:5: selectExpression ( COMMA selectExpression )*
            {
            pushFollow(FOLLOW_selectExpression_in_selectExpressionList1014);
            selectExpression67=selectExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_selectExpression.add(selectExpression67.getTree());

            // SelectClauseParser.g:164:22: ( COMMA selectExpression )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( (LA24_0==COMMA) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // SelectClauseParser.g:164:23: COMMA selectExpression
            	    {
            	    COMMA68=(Token)match(input,COMMA,FOLLOW_COMMA_in_selectExpressionList1017); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA68);


            	    pushFollow(FOLLOW_selectExpression_in_selectExpressionList1019);
            	    selectExpression69=selectExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_selectExpression.add(selectExpression69.getTree());

            	    }
            	    break;

            	default :
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
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 164:48: -> ^( TOK_EXPLIST ( selectExpression )+ )
            {
                // SelectClauseParser.g:164:51: ^( TOK_EXPLIST ( selectExpression )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(TOK_EXPLIST, "TOK_EXPLIST")
                , root_1);

                if ( !(stream_selectExpression.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_selectExpression.hasNext() ) {
                    adaptor.addChild(root_1, stream_selectExpression.nextTree());

                }
                stream_selectExpression.reset();

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
    // $ANTLR end "selectExpressionList"


    public static class window_clause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "window_clause"
    // SelectClauseParser.g:168:1: window_clause : KW_WINDOW window_defn ( COMMA window_defn )* -> ^( KW_WINDOW ( window_defn )+ ) ;
    public final HiveParser_SelectClauseParser.window_clause_return window_clause() throws RecognitionException {
        HiveParser_SelectClauseParser.window_clause_return retval = new HiveParser_SelectClauseParser.window_clause_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token KW_WINDOW70=null;
        Token COMMA72=null;
        HiveParser_SelectClauseParser.window_defn_return window_defn71 =null;

        HiveParser_SelectClauseParser.window_defn_return window_defn73 =null;


        CommonTree KW_WINDOW70_tree=null;
        CommonTree COMMA72_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_KW_WINDOW=new RewriteRuleTokenStream(adaptor,"token KW_WINDOW");
        RewriteRuleSubtreeStream stream_window_defn=new RewriteRuleSubtreeStream(adaptor,"rule window_defn");
         gParent.pushMsg("window_clause", state); 
        try {
            // SelectClauseParser.g:171:3: ( KW_WINDOW window_defn ( COMMA window_defn )* -> ^( KW_WINDOW ( window_defn )+ ) )
            // SelectClauseParser.g:172:3: KW_WINDOW window_defn ( COMMA window_defn )*
            {
            KW_WINDOW70=(Token)match(input,KW_WINDOW,FOLLOW_KW_WINDOW_in_window_clause1056); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_KW_WINDOW.add(KW_WINDOW70);


            pushFollow(FOLLOW_window_defn_in_window_clause1058);
            window_defn71=window_defn();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_window_defn.add(window_defn71.getTree());

            // SelectClauseParser.g:172:25: ( COMMA window_defn )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==COMMA) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // SelectClauseParser.g:172:26: COMMA window_defn
            	    {
            	    COMMA72=(Token)match(input,COMMA,FOLLOW_COMMA_in_window_clause1061); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA72);


            	    pushFollow(FOLLOW_window_defn_in_window_clause1063);
            	    window_defn73=window_defn();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_window_defn.add(window_defn73.getTree());

            	    }
            	    break;

            	default :
            	    break loop25;
                }
            } while (true);


            // AST REWRITE
            // elements: window_defn, KW_WINDOW
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 172:46: -> ^( KW_WINDOW ( window_defn )+ )
            {
                // SelectClauseParser.g:172:49: ^( KW_WINDOW ( window_defn )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                stream_KW_WINDOW.nextNode()
                , root_1);

                if ( !(stream_window_defn.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_window_defn.hasNext() ) {
                    adaptor.addChild(root_1, stream_window_defn.nextTree());

                }
                stream_window_defn.reset();

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
    // $ANTLR end "window_clause"


    public static class window_defn_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "window_defn"
    // SelectClauseParser.g:175:1: window_defn : Identifier KW_AS window_specification -> ^( TOK_WINDOWDEF Identifier window_specification ) ;
    public final HiveParser_SelectClauseParser.window_defn_return window_defn() throws RecognitionException {
        HiveParser_SelectClauseParser.window_defn_return retval = new HiveParser_SelectClauseParser.window_defn_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token Identifier74=null;
        Token KW_AS75=null;
        HiveParser_SelectClauseParser.window_specification_return window_specification76 =null;


        CommonTree Identifier74_tree=null;
        CommonTree KW_AS75_tree=null;
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleTokenStream stream_KW_AS=new RewriteRuleTokenStream(adaptor,"token KW_AS");
        RewriteRuleSubtreeStream stream_window_specification=new RewriteRuleSubtreeStream(adaptor,"rule window_specification");
         gParent.pushMsg("window_defn", state); 
        try {
            // SelectClauseParser.g:178:3: ( Identifier KW_AS window_specification -> ^( TOK_WINDOWDEF Identifier window_specification ) )
            // SelectClauseParser.g:179:3: Identifier KW_AS window_specification
            {
            Identifier74=(Token)match(input,Identifier,FOLLOW_Identifier_in_window_defn1095); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_Identifier.add(Identifier74);


            KW_AS75=(Token)match(input,KW_AS,FOLLOW_KW_AS_in_window_defn1097); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_KW_AS.add(KW_AS75);


            pushFollow(FOLLOW_window_specification_in_window_defn1099);
            window_specification76=window_specification();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_window_specification.add(window_specification76.getTree());

            // AST REWRITE
            // elements: Identifier, window_specification
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 179:41: -> ^( TOK_WINDOWDEF Identifier window_specification )
            {
                // SelectClauseParser.g:179:44: ^( TOK_WINDOWDEF Identifier window_specification )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(TOK_WINDOWDEF, "TOK_WINDOWDEF")
                , root_1);

                adaptor.addChild(root_1, 
                stream_Identifier.nextNode()
                );

                adaptor.addChild(root_1, stream_window_specification.nextTree());

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
    // $ANTLR end "window_defn"


    public static class window_specification_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "window_specification"
    // SelectClauseParser.g:182:1: window_specification : ( Identifier | ( LPAREN ( Identifier )? ( partitioningSpec )? ( window_frame )? RPAREN ) ) -> ^( TOK_WINDOWSPEC ( Identifier )? ( partitioningSpec )? ( window_frame )? ) ;
    public final HiveParser_SelectClauseParser.window_specification_return window_specification() throws RecognitionException {
        HiveParser_SelectClauseParser.window_specification_return retval = new HiveParser_SelectClauseParser.window_specification_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token Identifier77=null;
        Token LPAREN78=null;
        Token Identifier79=null;
        Token RPAREN82=null;
        HiveParser_FromClauseParser.partitioningSpec_return partitioningSpec80 =null;

        HiveParser_SelectClauseParser.window_frame_return window_frame81 =null;


        CommonTree Identifier77_tree=null;
        CommonTree LPAREN78_tree=null;
        CommonTree Identifier79_tree=null;
        CommonTree RPAREN82_tree=null;
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleSubtreeStream stream_partitioningSpec=new RewriteRuleSubtreeStream(adaptor,"rule partitioningSpec");
        RewriteRuleSubtreeStream stream_window_frame=new RewriteRuleSubtreeStream(adaptor,"rule window_frame");
         gParent.pushMsg("window_specification", state); 
        try {
            // SelectClauseParser.g:185:3: ( ( Identifier | ( LPAREN ( Identifier )? ( partitioningSpec )? ( window_frame )? RPAREN ) ) -> ^( TOK_WINDOWSPEC ( Identifier )? ( partitioningSpec )? ( window_frame )? ) )
            // SelectClauseParser.g:186:3: ( Identifier | ( LPAREN ( Identifier )? ( partitioningSpec )? ( window_frame )? RPAREN ) )
            {
            // SelectClauseParser.g:186:3: ( Identifier | ( LPAREN ( Identifier )? ( partitioningSpec )? ( window_frame )? RPAREN ) )
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==Identifier) ) {
                alt29=1;
            }
            else if ( (LA29_0==LPAREN) ) {
                alt29=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 29, 0, input);

                throw nvae;

            }
            switch (alt29) {
                case 1 :
                    // SelectClauseParser.g:186:4: Identifier
                    {
                    Identifier77=(Token)match(input,Identifier,FOLLOW_Identifier_in_window_specification1131); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_Identifier.add(Identifier77);


                    }
                    break;
                case 2 :
                    // SelectClauseParser.g:186:17: ( LPAREN ( Identifier )? ( partitioningSpec )? ( window_frame )? RPAREN )
                    {
                    // SelectClauseParser.g:186:17: ( LPAREN ( Identifier )? ( partitioningSpec )? ( window_frame )? RPAREN )
                    // SelectClauseParser.g:186:19: LPAREN ( Identifier )? ( partitioningSpec )? ( window_frame )? RPAREN
                    {
                    LPAREN78=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_window_specification1137); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN78);


                    // SelectClauseParser.g:186:26: ( Identifier )?
                    int alt26=2;
                    int LA26_0 = input.LA(1);

                    if ( (LA26_0==Identifier) ) {
                        alt26=1;
                    }
                    switch (alt26) {
                        case 1 :
                            // SelectClauseParser.g:186:26: Identifier
                            {
                            Identifier79=(Token)match(input,Identifier,FOLLOW_Identifier_in_window_specification1139); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_Identifier.add(Identifier79);


                            }
                            break;

                    }


                    // SelectClauseParser.g:186:38: ( partitioningSpec )?
                    int alt27=2;
                    int LA27_0 = input.LA(1);

                    if ( (LA27_0==KW_CLUSTER||LA27_0==KW_DISTRIBUTE||LA27_0==KW_ORDER||LA27_0==KW_PARTITION||LA27_0==KW_SORT) ) {
                        alt27=1;
                    }
                    switch (alt27) {
                        case 1 :
                            // SelectClauseParser.g:186:38: partitioningSpec
                            {
                            pushFollow(FOLLOW_partitioningSpec_in_window_specification1142);
                            partitioningSpec80=gHiveParser.partitioningSpec();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_partitioningSpec.add(partitioningSpec80.getTree());

                            }
                            break;

                    }


                    // SelectClauseParser.g:186:56: ( window_frame )?
                    int alt28=2;
                    int LA28_0 = input.LA(1);

                    if ( (LA28_0==KW_RANGE||LA28_0==KW_ROWS) ) {
                        alt28=1;
                    }
                    switch (alt28) {
                        case 1 :
                            // SelectClauseParser.g:186:56: window_frame
                            {
                            pushFollow(FOLLOW_window_frame_in_window_specification1145);
                            window_frame81=window_frame();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_window_frame.add(window_frame81.getTree());

                            }
                            break;

                    }


                    RPAREN82=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_window_specification1148); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN82);


                    }


                    }
                    break;

            }


            // AST REWRITE
            // elements: window_frame, Identifier, partitioningSpec
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 186:79: -> ^( TOK_WINDOWSPEC ( Identifier )? ( partitioningSpec )? ( window_frame )? )
            {
                // SelectClauseParser.g:186:82: ^( TOK_WINDOWSPEC ( Identifier )? ( partitioningSpec )? ( window_frame )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(TOK_WINDOWSPEC, "TOK_WINDOWSPEC")
                , root_1);

                // SelectClauseParser.g:186:99: ( Identifier )?
                if ( stream_Identifier.hasNext() ) {
                    adaptor.addChild(root_1, 
                    stream_Identifier.nextNode()
                    );

                }
                stream_Identifier.reset();

                // SelectClauseParser.g:186:111: ( partitioningSpec )?
                if ( stream_partitioningSpec.hasNext() ) {
                    adaptor.addChild(root_1, stream_partitioningSpec.nextTree());

                }
                stream_partitioningSpec.reset();

                // SelectClauseParser.g:186:129: ( window_frame )?
                if ( stream_window_frame.hasNext() ) {
                    adaptor.addChild(root_1, stream_window_frame.nextTree());

                }
                stream_window_frame.reset();

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
    // $ANTLR end "window_specification"


    public static class window_frame_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "window_frame"
    // SelectClauseParser.g:189:1: window_frame : ( window_range_expression | window_value_expression );
    public final HiveParser_SelectClauseParser.window_frame_return window_frame() throws RecognitionException {
        HiveParser_SelectClauseParser.window_frame_return retval = new HiveParser_SelectClauseParser.window_frame_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        HiveParser_SelectClauseParser.window_range_expression_return window_range_expression83 =null;

        HiveParser_SelectClauseParser.window_value_expression_return window_value_expression84 =null;



        try {
            // SelectClauseParser.g:189:14: ( window_range_expression | window_value_expression )
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==KW_ROWS) ) {
                alt30=1;
            }
            else if ( (LA30_0==KW_RANGE) ) {
                alt30=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 30, 0, input);

                throw nvae;

            }
            switch (alt30) {
                case 1 :
                    // SelectClauseParser.g:190:2: window_range_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_window_range_expression_in_window_frame1175);
                    window_range_expression83=window_range_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, window_range_expression83.getTree());

                    }
                    break;
                case 2 :
                    // SelectClauseParser.g:191:2: window_value_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_window_value_expression_in_window_frame1180);
                    window_value_expression84=window_value_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, window_value_expression84.getTree());

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
    // $ANTLR end "window_frame"


    public static class window_range_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "window_range_expression"
    // SelectClauseParser.g:194:1: window_range_expression : ( KW_ROWS sb= window_frame_start_boundary -> ^( TOK_WINDOWRANGE $sb) | KW_ROWS KW_BETWEEN s= window_frame_boundary KW_AND end= window_frame_boundary -> ^( TOK_WINDOWRANGE $s $end) );
    public final HiveParser_SelectClauseParser.window_range_expression_return window_range_expression() throws RecognitionException {
        HiveParser_SelectClauseParser.window_range_expression_return retval = new HiveParser_SelectClauseParser.window_range_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token KW_ROWS85=null;
        Token KW_ROWS86=null;
        Token KW_BETWEEN87=null;
        Token KW_AND88=null;
        HiveParser_SelectClauseParser.window_frame_start_boundary_return sb =null;

        HiveParser_SelectClauseParser.window_frame_boundary_return s =null;

        HiveParser_SelectClauseParser.window_frame_boundary_return end =null;


        CommonTree KW_ROWS85_tree=null;
        CommonTree KW_ROWS86_tree=null;
        CommonTree KW_BETWEEN87_tree=null;
        CommonTree KW_AND88_tree=null;
        RewriteRuleTokenStream stream_KW_BETWEEN=new RewriteRuleTokenStream(adaptor,"token KW_BETWEEN");
        RewriteRuleTokenStream stream_KW_AND=new RewriteRuleTokenStream(adaptor,"token KW_AND");
        RewriteRuleTokenStream stream_KW_ROWS=new RewriteRuleTokenStream(adaptor,"token KW_ROWS");
        RewriteRuleSubtreeStream stream_window_frame_start_boundary=new RewriteRuleSubtreeStream(adaptor,"rule window_frame_start_boundary");
        RewriteRuleSubtreeStream stream_window_frame_boundary=new RewriteRuleSubtreeStream(adaptor,"rule window_frame_boundary");
         gParent.pushMsg("window_range_expression", state); 
        try {
            // SelectClauseParser.g:197:2: ( KW_ROWS sb= window_frame_start_boundary -> ^( TOK_WINDOWRANGE $sb) | KW_ROWS KW_BETWEEN s= window_frame_boundary KW_AND end= window_frame_boundary -> ^( TOK_WINDOWRANGE $s $end) )
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==KW_ROWS) ) {
                int LA31_1 = input.LA(2);

                if ( (LA31_1==KW_BETWEEN) ) {
                    alt31=2;
                }
                else if ( (LA31_1==KW_CURRENT||LA31_1==KW_UNBOUNDED||LA31_1==Number) ) {
                    alt31=1;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 31, 1, input);

                    throw nvae;

                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 31, 0, input);

                throw nvae;

            }
            switch (alt31) {
                case 1 :
                    // SelectClauseParser.g:198:2: KW_ROWS sb= window_frame_start_boundary
                    {
                    KW_ROWS85=(Token)match(input,KW_ROWS,FOLLOW_KW_ROWS_in_window_range_expression1200); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_ROWS.add(KW_ROWS85);


                    pushFollow(FOLLOW_window_frame_start_boundary_in_window_range_expression1204);
                    sb=window_frame_start_boundary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_window_frame_start_boundary.add(sb.getTree());

                    // AST REWRITE
                    // elements: sb
                    // token labels: 
                    // rule labels: retval, sb
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
                    RewriteRuleSubtreeStream stream_sb=new RewriteRuleSubtreeStream(adaptor,"rule sb",sb!=null?sb.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 198:41: -> ^( TOK_WINDOWRANGE $sb)
                    {
                        // SelectClauseParser.g:198:44: ^( TOK_WINDOWRANGE $sb)
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(TOK_WINDOWRANGE, "TOK_WINDOWRANGE")
                        , root_1);

                        adaptor.addChild(root_1, stream_sb.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 2 :
                    // SelectClauseParser.g:199:2: KW_ROWS KW_BETWEEN s= window_frame_boundary KW_AND end= window_frame_boundary
                    {
                    KW_ROWS86=(Token)match(input,KW_ROWS,FOLLOW_KW_ROWS_in_window_range_expression1218); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_ROWS.add(KW_ROWS86);


                    KW_BETWEEN87=(Token)match(input,KW_BETWEEN,FOLLOW_KW_BETWEEN_in_window_range_expression1220); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_BETWEEN.add(KW_BETWEEN87);


                    pushFollow(FOLLOW_window_frame_boundary_in_window_range_expression1224);
                    s=window_frame_boundary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_window_frame_boundary.add(s.getTree());

                    KW_AND88=(Token)match(input,KW_AND,FOLLOW_KW_AND_in_window_range_expression1226); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_AND.add(KW_AND88);


                    pushFollow(FOLLOW_window_frame_boundary_in_window_range_expression1230);
                    end=window_frame_boundary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_window_frame_boundary.add(end.getTree());

                    // AST REWRITE
                    // elements: end, s
                    // token labels: 
                    // rule labels: s, end, retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_s=new RewriteRuleSubtreeStream(adaptor,"rule s",s!=null?s.tree:null);
                    RewriteRuleSubtreeStream stream_end=new RewriteRuleSubtreeStream(adaptor,"rule end",end!=null?end.tree:null);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 199:78: -> ^( TOK_WINDOWRANGE $s $end)
                    {
                        // SelectClauseParser.g:199:81: ^( TOK_WINDOWRANGE $s $end)
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(TOK_WINDOWRANGE, "TOK_WINDOWRANGE")
                        , root_1);

                        adaptor.addChild(root_1, stream_s.nextTree());

                        adaptor.addChild(root_1, stream_end.nextTree());

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
    // $ANTLR end "window_range_expression"


    public static class window_value_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "window_value_expression"
    // SelectClauseParser.g:202:1: window_value_expression : ( KW_RANGE sb= window_frame_start_boundary -> ^( TOK_WINDOWVALUES $sb) | KW_RANGE KW_BETWEEN s= window_frame_boundary KW_AND end= window_frame_boundary -> ^( TOK_WINDOWVALUES $s $end) );
    public final HiveParser_SelectClauseParser.window_value_expression_return window_value_expression() throws RecognitionException {
        HiveParser_SelectClauseParser.window_value_expression_return retval = new HiveParser_SelectClauseParser.window_value_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token KW_RANGE89=null;
        Token KW_RANGE90=null;
        Token KW_BETWEEN91=null;
        Token KW_AND92=null;
        HiveParser_SelectClauseParser.window_frame_start_boundary_return sb =null;

        HiveParser_SelectClauseParser.window_frame_boundary_return s =null;

        HiveParser_SelectClauseParser.window_frame_boundary_return end =null;


        CommonTree KW_RANGE89_tree=null;
        CommonTree KW_RANGE90_tree=null;
        CommonTree KW_BETWEEN91_tree=null;
        CommonTree KW_AND92_tree=null;
        RewriteRuleTokenStream stream_KW_BETWEEN=new RewriteRuleTokenStream(adaptor,"token KW_BETWEEN");
        RewriteRuleTokenStream stream_KW_AND=new RewriteRuleTokenStream(adaptor,"token KW_AND");
        RewriteRuleTokenStream stream_KW_RANGE=new RewriteRuleTokenStream(adaptor,"token KW_RANGE");
        RewriteRuleSubtreeStream stream_window_frame_start_boundary=new RewriteRuleSubtreeStream(adaptor,"rule window_frame_start_boundary");
        RewriteRuleSubtreeStream stream_window_frame_boundary=new RewriteRuleSubtreeStream(adaptor,"rule window_frame_boundary");
         gParent.pushMsg("window_value_expression", state); 
        try {
            // SelectClauseParser.g:205:2: ( KW_RANGE sb= window_frame_start_boundary -> ^( TOK_WINDOWVALUES $sb) | KW_RANGE KW_BETWEEN s= window_frame_boundary KW_AND end= window_frame_boundary -> ^( TOK_WINDOWVALUES $s $end) )
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==KW_RANGE) ) {
                int LA32_1 = input.LA(2);

                if ( (LA32_1==KW_BETWEEN) ) {
                    alt32=2;
                }
                else if ( (LA32_1==KW_CURRENT||LA32_1==KW_UNBOUNDED||LA32_1==Number) ) {
                    alt32=1;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 32, 1, input);

                    throw nvae;

                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                throw nvae;

            }
            switch (alt32) {
                case 1 :
                    // SelectClauseParser.g:206:2: KW_RANGE sb= window_frame_start_boundary
                    {
                    KW_RANGE89=(Token)match(input,KW_RANGE,FOLLOW_KW_RANGE_in_window_value_expression1262); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_RANGE.add(KW_RANGE89);


                    pushFollow(FOLLOW_window_frame_start_boundary_in_window_value_expression1266);
                    sb=window_frame_start_boundary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_window_frame_start_boundary.add(sb.getTree());

                    // AST REWRITE
                    // elements: sb
                    // token labels: 
                    // rule labels: retval, sb
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
                    RewriteRuleSubtreeStream stream_sb=new RewriteRuleSubtreeStream(adaptor,"rule sb",sb!=null?sb.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 206:42: -> ^( TOK_WINDOWVALUES $sb)
                    {
                        // SelectClauseParser.g:206:45: ^( TOK_WINDOWVALUES $sb)
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(TOK_WINDOWVALUES, "TOK_WINDOWVALUES")
                        , root_1);

                        adaptor.addChild(root_1, stream_sb.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 2 :
                    // SelectClauseParser.g:207:2: KW_RANGE KW_BETWEEN s= window_frame_boundary KW_AND end= window_frame_boundary
                    {
                    KW_RANGE90=(Token)match(input,KW_RANGE,FOLLOW_KW_RANGE_in_window_value_expression1280); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_RANGE.add(KW_RANGE90);


                    KW_BETWEEN91=(Token)match(input,KW_BETWEEN,FOLLOW_KW_BETWEEN_in_window_value_expression1282); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_BETWEEN.add(KW_BETWEEN91);


                    pushFollow(FOLLOW_window_frame_boundary_in_window_value_expression1286);
                    s=window_frame_boundary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_window_frame_boundary.add(s.getTree());

                    KW_AND92=(Token)match(input,KW_AND,FOLLOW_KW_AND_in_window_value_expression1288); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_AND.add(KW_AND92);


                    pushFollow(FOLLOW_window_frame_boundary_in_window_value_expression1292);
                    end=window_frame_boundary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_window_frame_boundary.add(end.getTree());

                    // AST REWRITE
                    // elements: s, end
                    // token labels: 
                    // rule labels: s, end, retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_s=new RewriteRuleSubtreeStream(adaptor,"rule s",s!=null?s.tree:null);
                    RewriteRuleSubtreeStream stream_end=new RewriteRuleSubtreeStream(adaptor,"rule end",end!=null?end.tree:null);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 207:79: -> ^( TOK_WINDOWVALUES $s $end)
                    {
                        // SelectClauseParser.g:207:82: ^( TOK_WINDOWVALUES $s $end)
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(TOK_WINDOWVALUES, "TOK_WINDOWVALUES")
                        , root_1);

                        adaptor.addChild(root_1, stream_s.nextTree());

                        adaptor.addChild(root_1, stream_end.nextTree());

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
    // $ANTLR end "window_value_expression"


    public static class window_frame_start_boundary_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "window_frame_start_boundary"
    // SelectClauseParser.g:210:1: window_frame_start_boundary : ( KW_UNBOUNDED KW_PRECEDING -> ^( KW_PRECEDING KW_UNBOUNDED ) | KW_CURRENT KW_ROW -> ^( KW_CURRENT ) | Number KW_PRECEDING -> ^( KW_PRECEDING Number ) );
    public final HiveParser_SelectClauseParser.window_frame_start_boundary_return window_frame_start_boundary() throws RecognitionException {
        HiveParser_SelectClauseParser.window_frame_start_boundary_return retval = new HiveParser_SelectClauseParser.window_frame_start_boundary_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token KW_UNBOUNDED93=null;
        Token KW_PRECEDING94=null;
        Token KW_CURRENT95=null;
        Token KW_ROW96=null;
        Token Number97=null;
        Token KW_PRECEDING98=null;

        CommonTree KW_UNBOUNDED93_tree=null;
        CommonTree KW_PRECEDING94_tree=null;
        CommonTree KW_CURRENT95_tree=null;
        CommonTree KW_ROW96_tree=null;
        CommonTree Number97_tree=null;
        CommonTree KW_PRECEDING98_tree=null;
        RewriteRuleTokenStream stream_Number=new RewriteRuleTokenStream(adaptor,"token Number");
        RewriteRuleTokenStream stream_KW_ROW=new RewriteRuleTokenStream(adaptor,"token KW_ROW");
        RewriteRuleTokenStream stream_KW_UNBOUNDED=new RewriteRuleTokenStream(adaptor,"token KW_UNBOUNDED");
        RewriteRuleTokenStream stream_KW_PRECEDING=new RewriteRuleTokenStream(adaptor,"token KW_PRECEDING");
        RewriteRuleTokenStream stream_KW_CURRENT=new RewriteRuleTokenStream(adaptor,"token KW_CURRENT");

         gParent.pushMsg("windowframestartboundary", state); 
        try {
            // SelectClauseParser.g:213:3: ( KW_UNBOUNDED KW_PRECEDING -> ^( KW_PRECEDING KW_UNBOUNDED ) | KW_CURRENT KW_ROW -> ^( KW_CURRENT ) | Number KW_PRECEDING -> ^( KW_PRECEDING Number ) )
            int alt33=3;
            switch ( input.LA(1) ) {
            case KW_UNBOUNDED:
                {
                alt33=1;
                }
                break;
            case KW_CURRENT:
                {
                alt33=2;
                }
                break;
            case Number:
                {
                alt33=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 33, 0, input);

                throw nvae;

            }

            switch (alt33) {
                case 1 :
                    // SelectClauseParser.g:214:3: KW_UNBOUNDED KW_PRECEDING
                    {
                    KW_UNBOUNDED93=(Token)match(input,KW_UNBOUNDED,FOLLOW_KW_UNBOUNDED_in_window_frame_start_boundary1325); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_UNBOUNDED.add(KW_UNBOUNDED93);


                    KW_PRECEDING94=(Token)match(input,KW_PRECEDING,FOLLOW_KW_PRECEDING_in_window_frame_start_boundary1327); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_PRECEDING.add(KW_PRECEDING94);


                    // AST REWRITE
                    // elements: KW_PRECEDING, KW_UNBOUNDED
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 214:30: -> ^( KW_PRECEDING KW_UNBOUNDED )
                    {
                        // SelectClauseParser.g:214:33: ^( KW_PRECEDING KW_UNBOUNDED )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        stream_KW_PRECEDING.nextNode()
                        , root_1);

                        adaptor.addChild(root_1, 
                        stream_KW_UNBOUNDED.nextNode()
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 2 :
                    // SelectClauseParser.g:215:3: KW_CURRENT KW_ROW
                    {
                    KW_CURRENT95=(Token)match(input,KW_CURRENT,FOLLOW_KW_CURRENT_in_window_frame_start_boundary1342); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_CURRENT.add(KW_CURRENT95);


                    KW_ROW96=(Token)match(input,KW_ROW,FOLLOW_KW_ROW_in_window_frame_start_boundary1344); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_ROW.add(KW_ROW96);


                    // AST REWRITE
                    // elements: KW_CURRENT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 215:22: -> ^( KW_CURRENT )
                    {
                        // SelectClauseParser.g:215:25: ^( KW_CURRENT )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        stream_KW_CURRENT.nextNode()
                        , root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 3 :
                    // SelectClauseParser.g:216:3: Number KW_PRECEDING
                    {
                    Number97=(Token)match(input,Number,FOLLOW_Number_in_window_frame_start_boundary1357); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_Number.add(Number97);


                    KW_PRECEDING98=(Token)match(input,KW_PRECEDING,FOLLOW_KW_PRECEDING_in_window_frame_start_boundary1359); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_PRECEDING.add(KW_PRECEDING98);


                    // AST REWRITE
                    // elements: Number, KW_PRECEDING
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 216:23: -> ^( KW_PRECEDING Number )
                    {
                        // SelectClauseParser.g:216:26: ^( KW_PRECEDING Number )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        stream_KW_PRECEDING.nextNode()
                        , root_1);

                        adaptor.addChild(root_1, 
                        stream_Number.nextNode()
                        );

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
    // $ANTLR end "window_frame_start_boundary"


    public static class window_frame_boundary_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "window_frame_boundary"
    // SelectClauseParser.g:219:1: window_frame_boundary : ( KW_UNBOUNDED (r= KW_PRECEDING |r= KW_FOLLOWING ) -> ^( $r KW_UNBOUNDED ) | KW_CURRENT KW_ROW -> ^( KW_CURRENT ) | Number (d= KW_PRECEDING |d= KW_FOLLOWING ) -> ^( $d Number ) );
    public final HiveParser_SelectClauseParser.window_frame_boundary_return window_frame_boundary() throws RecognitionException {
        HiveParser_SelectClauseParser.window_frame_boundary_return retval = new HiveParser_SelectClauseParser.window_frame_boundary_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token r=null;
        Token d=null;
        Token KW_UNBOUNDED99=null;
        Token KW_CURRENT100=null;
        Token KW_ROW101=null;
        Token Number102=null;

        CommonTree r_tree=null;
        CommonTree d_tree=null;
        CommonTree KW_UNBOUNDED99_tree=null;
        CommonTree KW_CURRENT100_tree=null;
        CommonTree KW_ROW101_tree=null;
        CommonTree Number102_tree=null;
        RewriteRuleTokenStream stream_Number=new RewriteRuleTokenStream(adaptor,"token Number");
        RewriteRuleTokenStream stream_KW_ROW=new RewriteRuleTokenStream(adaptor,"token KW_ROW");
        RewriteRuleTokenStream stream_KW_UNBOUNDED=new RewriteRuleTokenStream(adaptor,"token KW_UNBOUNDED");
        RewriteRuleTokenStream stream_KW_PRECEDING=new RewriteRuleTokenStream(adaptor,"token KW_PRECEDING");
        RewriteRuleTokenStream stream_KW_FOLLOWING=new RewriteRuleTokenStream(adaptor,"token KW_FOLLOWING");
        RewriteRuleTokenStream stream_KW_CURRENT=new RewriteRuleTokenStream(adaptor,"token KW_CURRENT");

         gParent.pushMsg("windowframeboundary", state); 
        try {
            // SelectClauseParser.g:222:3: ( KW_UNBOUNDED (r= KW_PRECEDING |r= KW_FOLLOWING ) -> ^( $r KW_UNBOUNDED ) | KW_CURRENT KW_ROW -> ^( KW_CURRENT ) | Number (d= KW_PRECEDING |d= KW_FOLLOWING ) -> ^( $d Number ) )
            int alt36=3;
            switch ( input.LA(1) ) {
            case KW_UNBOUNDED:
                {
                alt36=1;
                }
                break;
            case KW_CURRENT:
                {
                alt36=2;
                }
                break;
            case Number:
                {
                alt36=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 36, 0, input);

                throw nvae;

            }

            switch (alt36) {
                case 1 :
                    // SelectClauseParser.g:223:3: KW_UNBOUNDED (r= KW_PRECEDING |r= KW_FOLLOWING )
                    {
                    KW_UNBOUNDED99=(Token)match(input,KW_UNBOUNDED,FOLLOW_KW_UNBOUNDED_in_window_frame_boundary1388); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_UNBOUNDED.add(KW_UNBOUNDED99);


                    // SelectClauseParser.g:223:16: (r= KW_PRECEDING |r= KW_FOLLOWING )
                    int alt34=2;
                    int LA34_0 = input.LA(1);

                    if ( (LA34_0==KW_PRECEDING) ) {
                        alt34=1;
                    }
                    else if ( (LA34_0==KW_FOLLOWING) ) {
                        alt34=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 34, 0, input);

                        throw nvae;

                    }
                    switch (alt34) {
                        case 1 :
                            // SelectClauseParser.g:223:17: r= KW_PRECEDING
                            {
                            r=(Token)match(input,KW_PRECEDING,FOLLOW_KW_PRECEDING_in_window_frame_boundary1393); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_KW_PRECEDING.add(r);


                            }
                            break;
                        case 2 :
                            // SelectClauseParser.g:223:32: r= KW_FOLLOWING
                            {
                            r=(Token)match(input,KW_FOLLOWING,FOLLOW_KW_FOLLOWING_in_window_frame_boundary1397); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_KW_FOLLOWING.add(r);


                            }
                            break;

                    }


                    // AST REWRITE
                    // elements: r, KW_UNBOUNDED
                    // token labels: r
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_r=new RewriteRuleTokenStream(adaptor,"token r",r);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 223:49: -> ^( $r KW_UNBOUNDED )
                    {
                        // SelectClauseParser.g:223:52: ^( $r KW_UNBOUNDED )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(stream_r.nextNode(), root_1);

                        adaptor.addChild(root_1, 
                        stream_KW_UNBOUNDED.nextNode()
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 2 :
                    // SelectClauseParser.g:224:3: KW_CURRENT KW_ROW
                    {
                    KW_CURRENT100=(Token)match(input,KW_CURRENT,FOLLOW_KW_CURRENT_in_window_frame_boundary1414); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_CURRENT.add(KW_CURRENT100);


                    KW_ROW101=(Token)match(input,KW_ROW,FOLLOW_KW_ROW_in_window_frame_boundary1416); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_KW_ROW.add(KW_ROW101);


                    // AST REWRITE
                    // elements: KW_CURRENT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 224:22: -> ^( KW_CURRENT )
                    {
                        // SelectClauseParser.g:224:25: ^( KW_CURRENT )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        stream_KW_CURRENT.nextNode()
                        , root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 3 :
                    // SelectClauseParser.g:225:3: Number (d= KW_PRECEDING |d= KW_FOLLOWING )
                    {
                    Number102=(Token)match(input,Number,FOLLOW_Number_in_window_frame_boundary1429); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_Number.add(Number102);


                    // SelectClauseParser.g:225:10: (d= KW_PRECEDING |d= KW_FOLLOWING )
                    int alt35=2;
                    int LA35_0 = input.LA(1);

                    if ( (LA35_0==KW_PRECEDING) ) {
                        alt35=1;
                    }
                    else if ( (LA35_0==KW_FOLLOWING) ) {
                        alt35=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 35, 0, input);

                        throw nvae;

                    }
                    switch (alt35) {
                        case 1 :
                            // SelectClauseParser.g:225:11: d= KW_PRECEDING
                            {
                            d=(Token)match(input,KW_PRECEDING,FOLLOW_KW_PRECEDING_in_window_frame_boundary1434); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_KW_PRECEDING.add(d);


                            }
                            break;
                        case 2 :
                            // SelectClauseParser.g:225:28: d= KW_FOLLOWING
                            {
                            d=(Token)match(input,KW_FOLLOWING,FOLLOW_KW_FOLLOWING_in_window_frame_boundary1440); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_KW_FOLLOWING.add(d);


                            }
                            break;

                    }


                    // AST REWRITE
                    // elements: d, Number
                    // token labels: d
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_d=new RewriteRuleTokenStream(adaptor,"token d",d);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 225:45: -> ^( $d Number )
                    {
                        // SelectClauseParser.g:225:48: ^( $d Number )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(stream_d.nextNode(), root_1);

                        adaptor.addChild(root_1, 
                        stream_Number.nextNode()
                        );

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
    // $ANTLR end "window_frame_boundary"

    // $ANTLR start synpred1_SelectClauseParser
    public final void synpred1_SelectClauseParser_fragment() throws RecognitionException {
        // SelectClauseParser.g:131:5: ( tableAllColumns )
        // SelectClauseParser.g:131:6: tableAllColumns
        {
        pushFollow(FOLLOW_tableAllColumns_in_synpred1_SelectClauseParser701);
        gHiveParser.tableAllColumns();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred1_SelectClauseParser

    // $ANTLR start synpred2_SelectClauseParser
    public final void synpred2_SelectClauseParser_fragment() throws RecognitionException {
        // SelectClauseParser.g:155:5: ( tableAllColumns )
        // SelectClauseParser.g:155:6: tableAllColumns
        {
        pushFollow(FOLLOW_tableAllColumns_in_synpred2_SelectClauseParser966);
        gHiveParser.tableAllColumns();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred2_SelectClauseParser

    // Delegated rules

    public final boolean synpred2_SelectClauseParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_SelectClauseParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred1_SelectClauseParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_SelectClauseParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA2 dfa2 = new DFA2(this);
    protected DFA6 dfa6 = new DFA6(this);
    protected DFA7 dfa7 = new DFA7(this);
    protected DFA17 dfa17 = new DFA17(this);
    protected DFA16 dfa16 = new DFA16(this);
    protected DFA14 dfa14 = new DFA14(this);
    protected DFA19 dfa19 = new DFA19(this);
    protected DFA20 dfa20 = new DFA20(this);
    protected DFA23 dfa23 = new DFA23(this);
    static final String DFA2_eotS =
        "\u068f\uffff";
    static final String DFA2_eofS =
        "\1\uffff\1\3\36\uffff\1\47\u066e\uffff";
    static final String DFA2_minS =
        "\1\7\1\4\36\uffff\1\7\3\0\2\7\1\0\1\uffff\4\0\7\uffff\2\0\2\uffff"+
        "\2\0\1\7\1\0\1\7\1\0\3\uffff\1\0\5\uffff\2\0\7\uffff\2\0\2\uffff"+
        "\1\0\3\uffff\1\0\1\uffff\4\0\1\uffff\2\0\1\uffff\2\0\7\uffff\1\0"+
        "\1\uffff\4\0\1\uffff\2\0\1\uffff\2\0\1\uffff\1\0\u00a5\uffff\2\0"+
        "\25\uffff\1\0\4\uffff\1\0\3\uffff\32\0\u01b6\uffff\1\0\112\uffff"+
        "\34\0\u031b\uffff";
    static final String DFA2_maxS =
        "\2\u013b\36\uffff\1\u013b\3\0\2\u013b\1\0\1\uffff\4\0\7\uffff\2"+
        "\0\2\uffff\2\0\1\u013b\1\0\1\u013b\1\0\3\uffff\1\0\5\uffff\2\0\7"+
        "\uffff\2\0\2\uffff\1\0\3\uffff\1\0\1\uffff\4\0\1\uffff\2\0\1\uffff"+
        "\2\0\7\uffff\1\0\1\uffff\4\0\1\uffff\2\0\1\uffff\2\0\1\uffff\1\0"+
        "\u00a5\uffff\2\0\25\uffff\1\0\4\uffff\1\0\3\uffff\32\0\u01b6\uffff"+
        "\1\0\112\uffff\34\0\u031b\uffff";
    static final String DFA2_acceptS =
        "\2\uffff\1\2\1\3\43\uffff\1\1\u0667\uffff";
    static final String DFA2_specialS =
        "\41\uffff\1\0\1\1\1\2\2\uffff\1\3\1\uffff\1\4\1\5\1\6\1\7\7\uffff"+
        "\1\10\1\11\2\uffff\1\12\1\13\1\uffff\1\14\1\uffff\1\15\3\uffff\1"+
        "\16\5\uffff\1\17\1\20\7\uffff\1\21\1\22\2\uffff\1\23\3\uffff\1\24"+
        "\1\uffff\1\25\1\26\1\27\1\30\1\uffff\1\31\1\32\1\uffff\1\33\1\34"+
        "\7\uffff\1\35\1\uffff\1\36\1\37\1\40\1\41\1\uffff\1\42\1\43\1\uffff"+
        "\1\44\1\45\1\uffff\1\46\u00a5\uffff\1\47\1\50\25\uffff\1\51\4\uffff"+
        "\1\52\3\uffff\1\53\1\54\1\55\1\56\1\57\1\60\1\61\1\62\1\63\1\64"+
        "\1\65\1\66\1\67\1\70\1\71\1\72\1\73\1\74\1\75\1\76\1\77\1\100\1"+
        "\101\1\102\1\103\1\104\u01b6\uffff\1\105\112\uffff\1\106\1\107\1"+
        "\110\1\111\1\112\1\113\1\114\1\115\1\116\1\117\1\120\1\121\1\122"+
        "\1\123\1\124\1\125\1\126\1\127\1\130\1\131\1\132\1\133\1\134\1\135"+
        "\1\136\1\137\1\140\1\141\u031b\uffff}>";
    static final String[] DFA2_transitionS = {
            "\1\3\5\uffff\1\3\4\uffff\1\3\7\uffff\4\3\1\1\2\3\1\uffff\22"+
            "\3\1\uffff\4\3\1\uffff\6\3\1\uffff\2\3\1\uffff\1\3\1\uffff\4"+
            "\3\1\uffff\20\3\1\2\4\3\1\uffff\1\3\1\uffff\1\3\1\uffff\4\3"+
            "\1\uffff\10\3\1\uffff\3\3\1\uffff\1\3\1\uffff\4\3\1\uffff\25"+
            "\3\1\uffff\4\3\1\uffff\12\3\1\uffff\7\3\1\uffff\10\3\1\uffff"+
            "\1\3\1\uffff\5\3\1\uffff\2\3\1\uffff\5\3\2\uffff\14\3\1\uffff"+
            "\23\3\1\uffff\25\3\1\uffff\3\3\1\uffff\5\3\1\uffff\4\3\1\uffff"+
            "\3\3\1\uffff\14\3\1\uffff\1\3\2\uffff\1\3\1\uffff\2\3\3\uffff"+
            "\1\3\2\uffff\1\3\2\uffff\2\3\7\uffff\5\3",
            "\3\3\1\47\2\uffff\1\3\2\uffff\1\47\2\3\1\uffff\1\3\1\47\1\uffff"+
            "\2\3\1\uffff\2\3\1\uffff\1\41\3\136\2\142\1\136\1\3\1\136\1"+
            "\64\1\120\1\136\1\142\1\136\1\117\3\70\1\142\2\136\1\142\1\136"+
            "\2\47\1\136\1\uffff\1\106\3\136\1\uffff\6\136\1\uffff\1\136"+
            "\1\142\1\uffff\1\142\1\uffff\1\50\1\52\1\142\1\136\1\uffff\1"+
            "\136\1\46\3\136\1\142\2\136\1\142\3\136\1\142\3\136\1\uffff"+
            "\1\132\1\70\1\142\1\136\1\uffff\1\136\1\uffff\1\136\1\uffff"+
            "\1\136\1\72\2\136\1\uffff\1\142\1\63\1\142\4\136\1\70\1\uffff"+
            "\1\142\2\136\1\3\1\142\1\uffff\1\136\1\142\1\123\1\142\1\3\3"+
            "\136\1\47\1\136\1\142\1\107\2\136\1\142\3\136\1\141\1\70\1\142"+
            "\1\47\1\142\1\74\2\136\1\uffff\2\136\1\137\1\142\1\uffff\1\100"+
            "\1\134\2\136\1\142\5\136\1\uffff\1\71\6\136\1\uffff\1\136\1"+
            "\142\1\136\1\44\1\136\1\43\1\142\1\136\1\uffff\1\136\1\3\1\127"+
            "\2\142\2\136\1\uffff\2\136\1\uffff\1\142\2\136\1\142\1\136\2"+
            "\uffff\2\136\1\142\2\136\1\142\2\136\1\142\3\136\1\3\1\67\6"+
            "\136\1\142\1\136\1\142\1\67\2\136\3\142\3\136\1\3\4\136\1\142"+
            "\5\136\1\70\1\133\6\136\1\42\1\142\1\136\1\uffff\3\136\1\uffff"+
            "\1\51\1\136\1\142\2\136\1\uffff\1\142\1\53\1\142\1\136\1\uffff"+
            "\1\136\1\131\1\42\1\uffff\3\136\1\142\2\136\2\142\2\136\1\142"+
            "\1\136\1\uffff\1\136\1\uffff\1\3\1\136\1\3\1\142\1\136\1\uffff"+
            "\2\3\1\73\1\3\1\uffff\1\45\2\3\1\47\1\45\3\uffff\1\3\3\uffff"+
            "\1\40\4\47",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\3\2\uffff\1\47\2\uffff\1\3\4\uffff\1\3\7\uffff\7\3\1\uffff"+
            "\22\3\1\uffff\1\155\3\3\1\uffff\6\3\1\uffff\2\3\1\uffff\1\3"+
            "\1\uffff\4\3\1\uffff\20\3\1\uffff\1\156\3\3\1\uffff\1\3\1\uffff"+
            "\1\3\1\uffff\4\3\1\uffff\10\3\1\uffff\3\3\1\47\1\3\1\uffff\2"+
            "\3\1\152\1\3\1\47\15\3\1\165\7\3\1\uffff\2\3\1\164\1\3\1\uffff"+
            "\1\3\1\161\10\3\1\uffff\1\167\6\3\1\uffff\3\3\1\uffff\4\3\1"+
            "\uffff\1\3\1\uffff\1\154\4\3\1\uffff\2\3\1\uffff\5\3\2\uffff"+
            "\14\3\1\47\23\3\1\47\13\3\1\157\11\3\1\uffff\3\3\1\uffff\5\3"+
            "\1\uffff\4\3\1\uffff\1\3\1\162\1\3\1\uffff\14\3\1\uffff\1\3"+
            "\1\uffff\1\47\1\3\1\47\2\3\3\uffff\1\3\2\uffff\1\3\2\uffff\2"+
            "\3\3\uffff\1\47\4\uffff\4\3",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\47\5\uffff\1\47\4\uffff\1\47\7\uffff\7\47\1\uffff\6\47\1"+
            "\u011e\13\47\1\uffff\4\47\1\uffff\6\47\1\uffff\2\47\1\uffff"+
            "\1\47\1\uffff\4\47\1\uffff\20\47\1\uffff\4\47\1\uffff\1\47\1"+
            "\uffff\1\47\1\uffff\4\47\1\uffff\10\47\1\uffff\3\47\1\uffff"+
            "\1\47\1\uffff\4\47\1\uffff\6\47\1\u011d\16\47\1\uffff\4\47\1"+
            "\uffff\1\u0139\11\47\1\uffff\7\47\1\uffff\10\47\1\uffff\1\47"+
            "\1\uffff\5\47\1\uffff\2\47\1\uffff\5\47\2\uffff\14\47\1\uffff"+
            "\1\u0134\11\47\1\u0134\10\47\1\uffff\25\47\1\uffff\3\47\1\uffff"+
            "\5\47\1\uffff\4\47\1\uffff\3\47\1\uffff\14\47\1\uffff\1\47\2"+
            "\uffff\1\47\1\uffff\2\47\3\uffff\1\47\2\uffff\1\47\2\uffff\2"+
            "\47\10\uffff\4\47",
            "\1\u0145\5\uffff\1\u0149\4\uffff\1\u0148\7\uffff\1\u014f\3"+
            "\u0151\2\u0154\1\u0151\1\uffff\1\u0151\1\u0150\1\u0154\1\u0151"+
            "\1\u0154\1\u0151\1\u0154\3\u0153\1\u0154\2\u0151\1\u0154\1\u0151"+
            "\1\u014d\1\u014c\1\u0151\1\uffff\4\u0151\1\uffff\6\u0151\1\uffff"+
            "\1\u0151\1\u0154\1\uffff\1\u0154\1\uffff\1\u0140\1\u0142\1\u0154"+
            "\1\u0151\1\uffff\1\u0151\1\u013f\3\u0151\1\u0154\2\u0151\1\u0154"+
            "\3\u0151\1\u0154\3\u0151\1\uffff\1\u0151\1\u0153\1\u0154\1\u0151"+
            "\1\uffff\1\u0151\1\uffff\1\u0151\1\uffff\1\u0151\1\u0154\2\u0151"+
            "\1\uffff\1\u0154\1\u014b\1\u0154\4\u0151\1\u0153\1\uffff\1\u0154"+
            "\2\u0151\1\uffff\1\u0154\1\uffff\1\u0151\3\u0154\1\uffff\3\u0151"+
            "\1\u0152\1\u0151\2\u0154\2\u0151\1\u0154\3\u0151\1\u0154\1\u0153"+
            "\1\u0154\1\u0143\2\u0154\2\u0151\1\uffff\2\u0151\2\u0154\1\uffff"+
            "\1\u0154\3\u0151\1\u0154\5\u0151\1\uffff\1\u0152\6\u0151\1\uffff"+
            "\1\u0151\1\u0154\1\u0151\1\uffff\1\u0151\1\u013d\1\u0154\1\u0151"+
            "\1\uffff\1\u0151\1\uffff\3\u0154\2\u0151\1\uffff\2\u0151\1\uffff"+
            "\1\u0154\2\u0151\1\u0154\1\u0151\2\uffff\2\u0151\1\u0154\2\u0151"+
            "\1\u0154\2\u0151\1\u0154\3\u0151\1\uffff\7\u0151\1\u0154\1\u0151"+
            "\1\u0154\3\u0151\3\u0154\3\u0151\1\uffff\4\u0151\1\u0154\5\u0151"+
            "\1\u0153\7\u0151\1\u014e\1\u0154\1\u0151\1\uffff\3\u0151\1\uffff"+
            "\1\u0141\1\u0151\1\u0154\2\u0151\1\uffff\1\u0154\1\u014a\1\u0154"+
            "\1\u0151\1\uffff\1\u0151\1\u0154\1\u014e\1\uffff\3\u0151\1\u0154"+
            "\2\u0151\2\u0154\2\u0151\1\u0154\1\u0151\1\uffff\1\u0151\2\uffff"+
            "\1\u0151\1\uffff\1\u0154\1\u0151\3\uffff\1\u0155\2\uffff\1\u0156"+
            "\2\uffff\1\u013e\1\u0156\10\uffff\1\u0146\1\u0144\1\u0156\1"+
            "\u0147",
            "\1\uffff",
            "",
            "\1\uffff",
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
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\3\5\uffff\1\3\4\uffff\1\3\7\uffff\7\3\1\uffff\22\3\1\uffff"+
            "\4\3\1\uffff\6\3\1\uffff\2\3\1\uffff\1\3\1\uffff\4\3\1\uffff"+
            "\20\3\1\uffff\4\3\1\uffff\1\3\1\uffff\1\3\1\uffff\4\3\1\uffff"+
            "\10\3\1\uffff\3\3\1\uffff\1\3\1\uffff\4\3\1\uffff\25\3\1\uffff"+
            "\4\3\1\uffff\12\3\1\uffff\7\3\1\uffff\10\3\1\uffff\1\3\1\uffff"+
            "\5\3\1\uffff\2\3\1\uffff\5\3\2\uffff\14\3\1\uffff\23\3\1\uffff"+
            "\25\3\1\uffff\3\3\1\uffff\5\3\1\uffff\4\3\1\uffff\3\3\1\uffff"+
            "\14\3\1\uffff\1\3\2\uffff\1\3\1\uffff\2\3\3\uffff\1\u030d\2"+
            "\uffff\1\3\2\uffff\2\3\7\uffff\5\3",
            "\1\uffff",
            "\1\u0362\5\uffff\1\u0366\4\uffff\1\u0365\7\uffff\1\u036c\3"+
            "\u036e\2\u0373\1\u036e\1\uffff\1\u036e\1\u036d\1\u0373\1\u036e"+
            "\1\u0373\1\u036e\1\u0373\3\u0370\1\u0373\2\u036e\1\u0373\1\u036e"+
            "\1\u036a\1\u0369\1\u036e\1\uffff\4\u036e\1\uffff\6\u036e\1\uffff"+
            "\1\u036e\1\u0373\1\uffff\1\u0373\1\uffff\1\u035d\1\u035f\1\u0373"+
            "\1\u036e\1\uffff\1\u036e\1\u035c\3\u036e\1\u0373\2\u036e\1\u0373"+
            "\3\u036e\1\u0373\3\u036e\1\3\1\u036e\1\u0370\1\u0373\1\u036e"+
            "\1\uffff\1\u036e\1\uffff\1\u036e\1\uffff\1\u036e\1\u0371\2\u036e"+
            "\1\uffff\1\u0373\1\u0368\1\u0373\4\u036e\1\u0370\1\uffff\1\u0373"+
            "\2\u036e\1\uffff\1\u0373\1\uffff\1\u036e\3\u0373\1\uffff\3\u036e"+
            "\1\u036f\1\u036e\2\u0373\2\u036e\1\u0373\3\u036e\1\u0373\1\u0370"+
            "\1\u0373\1\u0360\2\u0373\2\u036e\1\uffff\2\u036e\2\u0373\1\uffff"+
            "\1\u0373\3\u036e\1\u0373\5\u036e\1\uffff\1\u036f\6\u036e\1\uffff"+
            "\1\u036e\1\u0373\1\u036e\1\u0358\1\u036e\1\u035a\1\u0373\1\u036e"+
            "\1\uffff\1\u036e\1\uffff\3\u0373\2\u036e\1\uffff\2\u036e\1\uffff"+
            "\1\u0373\2\u036e\1\u0373\1\u036e\2\uffff\2\u036e\1\u0373\2\u036e"+
            "\1\u0373\2\u036e\1\u0373\3\u036e\1\uffff\7\u036e\1\u0373\1\u036e"+
            "\1\u0373\3\u036e\3\u0373\3\u036e\1\uffff\4\u036e\1\u0373\5\u036e"+
            "\1\u0370\7\u036e\1\u036b\1\u0373\1\u036e\1\uffff\3\u036e\1\uffff"+
            "\1\u035e\1\u036e\1\u0373\2\u036e\1\uffff\1\u0373\1\u0367\1\u0373"+
            "\1\u036e\1\uffff\1\u036e\1\u0373\1\u036b\1\uffff\3\u036e\1\u0373"+
            "\2\u036e\2\u0373\2\u036e\1\u0373\1\u036e\1\uffff\1\u036e\2\uffff"+
            "\1\u036e\1\uffff\1\u0373\1\u036e\3\uffff\1\u0372\2\uffff\1\u0359"+
            "\2\uffff\1\u035b\1\u0359\3\uffff\1\3\3\uffff\1\3\1\u0363\1\u0361"+
            "\1\u0359\1\u0364",
            "\1\uffff",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
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
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            "\1\uffff",
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
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
        for (int i=0; i<numStates; i++) {
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
            return "55:29: ( KW_ALL |dist= KW_DISTINCT )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA2_33 = input.LA(1);

                         
                        int index2_33 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_33);

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA2_34 = input.LA(1);

                         
                        int index2_34 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_34);

                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA2_35 = input.LA(1);

                         
                        int index2_35 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_35);

                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA2_38 = input.LA(1);

                         
                        int index2_38 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_38);

                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA2_40 = input.LA(1);

                         
                        int index2_40 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_40);

                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA2_41 = input.LA(1);

                         
                        int index2_41 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_41);

                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA2_42 = input.LA(1);

                         
                        int index2_42 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_42);

                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA2_43 = input.LA(1);

                         
                        int index2_43 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_43);

                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA2_51 = input.LA(1);

                         
                        int index2_51 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_51);

                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA2_52 = input.LA(1);

                         
                        int index2_52 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_52);

                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA2_55 = input.LA(1);

                         
                        int index2_55 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_55);

                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA2_56 = input.LA(1);

                         
                        int index2_56 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_56);

                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA2_58 = input.LA(1);

                         
                        int index2_58 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_58);

                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA2_60 = input.LA(1);

                         
                        int index2_60 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_60);

                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA2_64 = input.LA(1);

                         
                        int index2_64 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_64);

                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA2_70 = input.LA(1);

                         
                        int index2_70 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_70);

                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA2_71 = input.LA(1);

                         
                        int index2_71 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_71);

                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA2_79 = input.LA(1);

                         
                        int index2_79 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_79);

                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA2_80 = input.LA(1);

                         
                        int index2_80 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_80);

                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA2_83 = input.LA(1);

                         
                        int index2_83 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_83);

                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA2_87 = input.LA(1);

                         
                        int index2_87 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_87);

                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA2_89 = input.LA(1);

                         
                        int index2_89 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_89);

                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA2_90 = input.LA(1);

                         
                        int index2_90 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_90);

                        if ( s>=0 ) return s;
                        break;
                    case 23 : 
                        int LA2_91 = input.LA(1);

                         
                        int index2_91 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_91);

                        if ( s>=0 ) return s;
                        break;
                    case 24 : 
                        int LA2_92 = input.LA(1);

                         
                        int index2_92 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_92);

                        if ( s>=0 ) return s;
                        break;
                    case 25 : 
                        int LA2_94 = input.LA(1);

                         
                        int index2_94 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_94);

                        if ( s>=0 ) return s;
                        break;
                    case 26 : 
                        int LA2_95 = input.LA(1);

                         
                        int index2_95 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_95);

                        if ( s>=0 ) return s;
                        break;
                    case 27 : 
                        int LA2_97 = input.LA(1);

                         
                        int index2_97 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_97);

                        if ( s>=0 ) return s;
                        break;
                    case 28 : 
                        int LA2_98 = input.LA(1);

                         
                        int index2_98 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_98);

                        if ( s>=0 ) return s;
                        break;
                    case 29 : 
                        int LA2_106 = input.LA(1);

                         
                        int index2_106 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_106);

                        if ( s>=0 ) return s;
                        break;
                    case 30 : 
                        int LA2_108 = input.LA(1);

                         
                        int index2_108 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_108);

                        if ( s>=0 ) return s;
                        break;
                    case 31 : 
                        int LA2_109 = input.LA(1);

                         
                        int index2_109 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_109);

                        if ( s>=0 ) return s;
                        break;
                    case 32 : 
                        int LA2_110 = input.LA(1);

                         
                        int index2_110 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_110);

                        if ( s>=0 ) return s;
                        break;
                    case 33 : 
                        int LA2_111 = input.LA(1);

                         
                        int index2_111 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_111);

                        if ( s>=0 ) return s;
                        break;
                    case 34 : 
                        int LA2_113 = input.LA(1);

                         
                        int index2_113 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_113);

                        if ( s>=0 ) return s;
                        break;
                    case 35 : 
                        int LA2_114 = input.LA(1);

                         
                        int index2_114 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_114);

                        if ( s>=0 ) return s;
                        break;
                    case 36 : 
                        int LA2_116 = input.LA(1);

                         
                        int index2_116 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_116);

                        if ( s>=0 ) return s;
                        break;
                    case 37 : 
                        int LA2_117 = input.LA(1);

                         
                        int index2_117 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_117);

                        if ( s>=0 ) return s;
                        break;
                    case 38 : 
                        int LA2_119 = input.LA(1);

                         
                        int index2_119 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_119);

                        if ( s>=0 ) return s;
                        break;
                    case 39 : 
                        int LA2_285 = input.LA(1);

                         
                        int index2_285 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_285);

                        if ( s>=0 ) return s;
                        break;
                    case 40 : 
                        int LA2_286 = input.LA(1);

                         
                        int index2_286 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_286);

                        if ( s>=0 ) return s;
                        break;
                    case 41 : 
                        int LA2_308 = input.LA(1);

                         
                        int index2_308 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_308);

                        if ( s>=0 ) return s;
                        break;
                    case 42 : 
                        int LA2_313 = input.LA(1);

                         
                        int index2_313 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_313);

                        if ( s>=0 ) return s;
                        break;
                    case 43 : 
                        int LA2_317 = input.LA(1);

                         
                        int index2_317 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_317);

                        if ( s>=0 ) return s;
                        break;
                    case 44 : 
                        int LA2_318 = input.LA(1);

                         
                        int index2_318 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_318);

                        if ( s>=0 ) return s;
                        break;
                    case 45 : 
                        int LA2_319 = input.LA(1);

                         
                        int index2_319 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_319);

                        if ( s>=0 ) return s;
                        break;
                    case 46 : 
                        int LA2_320 = input.LA(1);

                         
                        int index2_320 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_320);

                        if ( s>=0 ) return s;
                        break;
                    case 47 : 
                        int LA2_321 = input.LA(1);

                         
                        int index2_321 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_321);

                        if ( s>=0 ) return s;
                        break;
                    case 48 : 
                        int LA2_322 = input.LA(1);

                         
                        int index2_322 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_322);

                        if ( s>=0 ) return s;
                        break;
                    case 49 : 
                        int LA2_323 = input.LA(1);

                         
                        int index2_323 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_323);

                        if ( s>=0 ) return s;
                        break;
                    case 50 : 
                        int LA2_324 = input.LA(1);

                         
                        int index2_324 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_324);

                        if ( s>=0 ) return s;
                        break;
                    case 51 : 
                        int LA2_325 = input.LA(1);

                         
                        int index2_325 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_325);

                        if ( s>=0 ) return s;
                        break;
                    case 52 : 
                        int LA2_326 = input.LA(1);

                         
                        int index2_326 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_326);

                        if ( s>=0 ) return s;
                        break;
                    case 53 : 
                        int LA2_327 = input.LA(1);

                         
                        int index2_327 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_327);

                        if ( s>=0 ) return s;
                        break;
                    case 54 : 
                        int LA2_328 = input.LA(1);

                         
                        int index2_328 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_328);

                        if ( s>=0 ) return s;
                        break;
                    case 55 : 
                        int LA2_329 = input.LA(1);

                         
                        int index2_329 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_329);

                        if ( s>=0 ) return s;
                        break;
                    case 56 : 
                        int LA2_330 = input.LA(1);

                         
                        int index2_330 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_330);

                        if ( s>=0 ) return s;
                        break;
                    case 57 : 
                        int LA2_331 = input.LA(1);

                         
                        int index2_331 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_331);

                        if ( s>=0 ) return s;
                        break;
                    case 58 : 
                        int LA2_332 = input.LA(1);

                         
                        int index2_332 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_332);

                        if ( s>=0 ) return s;
                        break;
                    case 59 : 
                        int LA2_333 = input.LA(1);

                         
                        int index2_333 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_333);

                        if ( s>=0 ) return s;
                        break;
                    case 60 : 
                        int LA2_334 = input.LA(1);

                         
                        int index2_334 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_334);

                        if ( s>=0 ) return s;
                        break;
                    case 61 : 
                        int LA2_335 = input.LA(1);

                         
                        int index2_335 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_335);

                        if ( s>=0 ) return s;
                        break;
                    case 62 : 
                        int LA2_336 = input.LA(1);

                         
                        int index2_336 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_336);

                        if ( s>=0 ) return s;
                        break;
                    case 63 : 
                        int LA2_337 = input.LA(1);

                         
                        int index2_337 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_337);

                        if ( s>=0 ) return s;
                        break;
                    case 64 : 
                        int LA2_338 = input.LA(1);

                         
                        int index2_338 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_338);

                        if ( s>=0 ) return s;
                        break;
                    case 65 : 
                        int LA2_339 = input.LA(1);

                         
                        int index2_339 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_339);

                        if ( s>=0 ) return s;
                        break;
                    case 66 : 
                        int LA2_340 = input.LA(1);

                         
                        int index2_340 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_340);

                        if ( s>=0 ) return s;
                        break;
                    case 67 : 
                        int LA2_341 = input.LA(1);

                         
                        int index2_341 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_341);

                        if ( s>=0 ) return s;
                        break;
                    case 68 : 
                        int LA2_342 = input.LA(1);

                         
                        int index2_342 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_342);

                        if ( s>=0 ) return s;
                        break;
                    case 69 : 
                        int LA2_781 = input.LA(1);

                         
                        int index2_781 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_781);

                        if ( s>=0 ) return s;
                        break;
                    case 70 : 
                        int LA2_856 = input.LA(1);

                         
                        int index2_856 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_856);

                        if ( s>=0 ) return s;
                        break;
                    case 71 : 
                        int LA2_857 = input.LA(1);

                         
                        int index2_857 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_857);

                        if ( s>=0 ) return s;
                        break;
                    case 72 : 
                        int LA2_858 = input.LA(1);

                         
                        int index2_858 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_858);

                        if ( s>=0 ) return s;
                        break;
                    case 73 : 
                        int LA2_859 = input.LA(1);

                         
                        int index2_859 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_859);

                        if ( s>=0 ) return s;
                        break;
                    case 74 : 
                        int LA2_860 = input.LA(1);

                         
                        int index2_860 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_860);

                        if ( s>=0 ) return s;
                        break;
                    case 75 : 
                        int LA2_861 = input.LA(1);

                         
                        int index2_861 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_861);

                        if ( s>=0 ) return s;
                        break;
                    case 76 : 
                        int LA2_862 = input.LA(1);

                         
                        int index2_862 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_862);

                        if ( s>=0 ) return s;
                        break;
                    case 77 : 
                        int LA2_863 = input.LA(1);

                         
                        int index2_863 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_863);

                        if ( s>=0 ) return s;
                        break;
                    case 78 : 
                        int LA2_864 = input.LA(1);

                         
                        int index2_864 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_864);

                        if ( s>=0 ) return s;
                        break;
                    case 79 : 
                        int LA2_865 = input.LA(1);

                         
                        int index2_865 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_865);

                        if ( s>=0 ) return s;
                        break;
                    case 80 : 
                        int LA2_866 = input.LA(1);

                         
                        int index2_866 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_866);

                        if ( s>=0 ) return s;
                        break;
                    case 81 : 
                        int LA2_867 = input.LA(1);

                         
                        int index2_867 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_867);

                        if ( s>=0 ) return s;
                        break;
                    case 82 : 
                        int LA2_868 = input.LA(1);

                         
                        int index2_868 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_868);

                        if ( s>=0 ) return s;
                        break;
                    case 83 : 
                        int LA2_869 = input.LA(1);

                         
                        int index2_869 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_869);

                        if ( s>=0 ) return s;
                        break;
                    case 84 : 
                        int LA2_870 = input.LA(1);

                         
                        int index2_870 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_870);

                        if ( s>=0 ) return s;
                        break;
                    case 85 : 
                        int LA2_871 = input.LA(1);

                         
                        int index2_871 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_871);

                        if ( s>=0 ) return s;
                        break;
                    case 86 : 
                        int LA2_872 = input.LA(1);

                         
                        int index2_872 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_872);

                        if ( s>=0 ) return s;
                        break;
                    case 87 : 
                        int LA2_873 = input.LA(1);

                         
                        int index2_873 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_873);

                        if ( s>=0 ) return s;
                        break;
                    case 88 : 
                        int LA2_874 = input.LA(1);

                         
                        int index2_874 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_874);

                        if ( s>=0 ) return s;
                        break;
                    case 89 : 
                        int LA2_875 = input.LA(1);

                         
                        int index2_875 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_875);

                        if ( s>=0 ) return s;
                        break;
                    case 90 : 
                        int LA2_876 = input.LA(1);

                         
                        int index2_876 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_876);

                        if ( s>=0 ) return s;
                        break;
                    case 91 : 
                        int LA2_877 = input.LA(1);

                         
                        int index2_877 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_877);

                        if ( s>=0 ) return s;
                        break;
                    case 92 : 
                        int LA2_878 = input.LA(1);

                         
                        int index2_878 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_878);

                        if ( s>=0 ) return s;
                        break;
                    case 93 : 
                        int LA2_879 = input.LA(1);

                         
                        int index2_879 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_879);

                        if ( s>=0 ) return s;
                        break;
                    case 94 : 
                        int LA2_880 = input.LA(1);

                         
                        int index2_880 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_880);

                        if ( s>=0 ) return s;
                        break;
                    case 95 : 
                        int LA2_881 = input.LA(1);

                         
                        int index2_881 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_881);

                        if ( s>=0 ) return s;
                        break;
                    case 96 : 
                        int LA2_882 = input.LA(1);

                         
                        int index2_882 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_882);

                        if ( s>=0 ) return s;
                        break;
                    case 97 : 
                        int LA2_883 = input.LA(1);

                         
                        int index2_883 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 39;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 3;}

                         
                        input.seek(index2_883);

                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}

            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 2, _s, input);
            error(nvae);
            throw nvae;
        }

    }
    static final String DFA6_eotS =
        "\103\uffff";
    static final String DFA6_eofS =
        "\103\uffff";
    static final String DFA6_minS =
        "\1\32\3\12\77\uffff";
    static final String DFA6_maxS =
        "\1\u0124\3\u0133\77\uffff";
    static final String DFA6_acceptS =
        "\4\uffff\1\1\1\uffff\1\2\74\uffff";
    static final String DFA6_specialS =
        "\103\uffff}>";
    static final String[] DFA6_transitionS = {
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
            "\1\4\30\uffff\1\6\5\uffff\3\6\10\uffff\1\6\27\uffff\2\6\2\uffff"+
            "\1\6\14\uffff\1\6\23\uffff\1\6\32\uffff\1\6\27\uffff\1\6\115"+
            "\uffff\1\6\6\uffff\2\6\7\uffff\2\6\13\uffff\1\6\15\uffff\1\6"+
            "\25\uffff\1\4",
            "\1\4\30\uffff\1\6\5\uffff\3\6\10\uffff\1\6\27\uffff\2\6\2\uffff"+
            "\1\6\14\uffff\1\6\23\uffff\1\6\32\uffff\1\6\27\uffff\1\6\115"+
            "\uffff\1\6\6\uffff\2\6\7\uffff\2\6\13\uffff\1\6\15\uffff\1\6"+
            "\25\uffff\1\4",
            "\1\4\30\uffff\1\6\5\uffff\3\6\10\uffff\1\6\27\uffff\2\6\2\uffff"+
            "\1\6\14\uffff\1\6\23\uffff\1\6\32\uffff\1\6\27\uffff\1\6\115"+
            "\uffff\1\6\6\uffff\2\6\7\uffff\2\6\13\uffff\1\6\15\uffff\1\6"+
            "\25\uffff\1\4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA6_eot = DFA.unpackEncodedString(DFA6_eotS);
    static final short[] DFA6_eof = DFA.unpackEncodedString(DFA6_eofS);
    static final char[] DFA6_min = DFA.unpackEncodedStringToUnsignedChars(DFA6_minS);
    static final char[] DFA6_max = DFA.unpackEncodedStringToUnsignedChars(DFA6_maxS);
    static final short[] DFA6_accept = DFA.unpackEncodedString(DFA6_acceptS);
    static final short[] DFA6_special = DFA.unpackEncodedString(DFA6_specialS);
    static final short[][] DFA6_transition;

    static {
        int numStates = DFA6_transitionS.length;
        DFA6_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA6_transition[i] = DFA.unpackEncodedString(DFA6_transitionS[i]);
        }
    }

    class DFA6 extends DFA {

        public DFA6(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 6;
            this.eot = DFA6_eot;
            this.eof = DFA6_eof;
            this.min = DFA6_min;
            this.max = DFA6_max;
            this.accept = DFA6_accept;
            this.special = DFA6_special;
            this.transition = DFA6_transition;
        }
        public String getDescription() {
            return "78:22: ( aliasList | columnNameTypeList )";
        }
    }
    static final String DFA7_eotS =
        "\u00d3\uffff";
    static final String DFA7_eofS =
        "\1\uffff\3\4\u00cf\uffff";
    static final String DFA7_minS =
        "\1\32\3\12\23\uffff\1\7\46\uffff\1\7\46\uffff\1\7\155\uffff";
    static final String DFA7_maxS =
        "\1\u0124\3\u0133\23\uffff\1\u013b\46\uffff\1\u013b\46\uffff\1\u013b"+
        "\155\uffff";
    static final String DFA7_acceptS =
        "\4\uffff\1\1\24\uffff\1\2\u00b9\uffff";
    static final String DFA7_specialS =
        "\u00d3\uffff}>";
    static final String[] DFA7_transitionS = {
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
            "\1\4\30\uffff\1\31\5\uffff\3\31\10\uffff\1\31\1\4\26\uffff"+
            "\2\31\2\uffff\1\31\13\uffff\1\4\1\31\23\uffff\1\31\4\uffff\1"+
            "\4\4\uffff\1\4\1\uffff\1\4\15\uffff\1\4\1\31\11\uffff\1\4\3"+
            "\uffff\1\4\11\uffff\1\27\22\uffff\1\4\31\uffff\1\4\1\uffff\1"+
            "\4\16\uffff\1\4\4\uffff\1\4\12\uffff\1\31\1\4\5\uffff\2\31\7"+
            "\uffff\2\31\12\uffff\1\4\1\31\15\uffff\1\31\2\uffff\1\4\1\uffff"+
            "\1\4\20\uffff\1\4",
            "\1\4\30\uffff\1\31\5\uffff\3\31\10\uffff\1\31\1\4\26\uffff"+
            "\2\31\2\uffff\1\31\13\uffff\1\4\1\31\23\uffff\1\31\4\uffff\1"+
            "\4\4\uffff\1\4\1\uffff\1\4\15\uffff\1\4\1\31\11\uffff\1\4\3"+
            "\uffff\1\4\11\uffff\1\76\22\uffff\1\4\31\uffff\1\4\1\uffff\1"+
            "\4\16\uffff\1\4\4\uffff\1\4\12\uffff\1\31\1\4\5\uffff\2\31\7"+
            "\uffff\2\31\12\uffff\1\4\1\31\15\uffff\1\31\2\uffff\1\4\1\uffff"+
            "\1\4\20\uffff\1\4",
            "\1\4\30\uffff\1\31\5\uffff\3\31\10\uffff\1\31\1\4\26\uffff"+
            "\2\31\2\uffff\1\31\13\uffff\1\4\1\31\23\uffff\1\31\4\uffff\1"+
            "\4\4\uffff\1\4\1\uffff\1\4\15\uffff\1\4\1\31\11\uffff\1\4\3"+
            "\uffff\1\4\11\uffff\1\145\22\uffff\1\4\31\uffff\1\4\1\uffff"+
            "\1\4\16\uffff\1\4\4\uffff\1\4\12\uffff\1\31\1\4\5\uffff\2\31"+
            "\7\uffff\2\31\12\uffff\1\4\1\31\15\uffff\1\31\2\uffff\1\4\1"+
            "\uffff\1\4\20\uffff\1\4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\4\5\uffff\1\4\4\uffff\1\4\7\uffff\7\4\1\uffff\22\4\1\uffff"+
            "\4\4\1\uffff\6\4\1\uffff\2\4\1\uffff\1\4\1\uffff\4\4\1\uffff"+
            "\20\4\1\uffff\4\4\1\uffff\1\4\1\uffff\1\4\1\uffff\4\4\1\uffff"+
            "\10\4\1\uffff\3\4\1\uffff\1\4\1\uffff\4\4\1\uffff\25\4\1\uffff"+
            "\4\4\1\uffff\12\4\1\uffff\7\4\1\uffff\10\4\1\uffff\1\4\1\uffff"+
            "\5\4\1\uffff\2\4\1\uffff\5\4\2\uffff\14\4\1\uffff\23\4\1\uffff"+
            "\25\4\1\uffff\3\4\1\uffff\5\4\1\uffff\4\4\1\uffff\3\4\1\uffff"+
            "\14\4\1\uffff\1\4\2\uffff\1\4\1\uffff\2\4\1\uffff\1\31\1\uffff"+
            "\1\4\2\uffff\1\4\2\uffff\2\4\7\uffff\5\4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\4\5\uffff\1\4\4\uffff\1\4\7\uffff\7\4\1\uffff\22\4\1\uffff"+
            "\4\4\1\uffff\6\4\1\uffff\2\4\1\uffff\1\4\1\uffff\4\4\1\uffff"+
            "\20\4\1\uffff\4\4\1\uffff\1\4\1\uffff\1\4\1\uffff\4\4\1\uffff"+
            "\10\4\1\uffff\3\4\1\uffff\1\4\1\uffff\4\4\1\uffff\25\4\1\uffff"+
            "\4\4\1\uffff\12\4\1\uffff\7\4\1\uffff\10\4\1\uffff\1\4\1\uffff"+
            "\5\4\1\uffff\2\4\1\uffff\5\4\2\uffff\14\4\1\uffff\23\4\1\uffff"+
            "\25\4\1\uffff\3\4\1\uffff\5\4\1\uffff\4\4\1\uffff\3\4\1\uffff"+
            "\14\4\1\uffff\1\4\2\uffff\1\4\1\uffff\2\4\1\uffff\1\31\1\uffff"+
            "\1\4\2\uffff\1\4\2\uffff\2\4\7\uffff\5\4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\4\5\uffff\1\4\4\uffff\1\4\7\uffff\7\4\1\uffff\22\4\1\uffff"+
            "\4\4\1\uffff\6\4\1\uffff\2\4\1\uffff\1\4\1\uffff\4\4\1\uffff"+
            "\20\4\1\uffff\4\4\1\uffff\1\4\1\uffff\1\4\1\uffff\4\4\1\uffff"+
            "\10\4\1\uffff\3\4\1\uffff\1\4\1\uffff\4\4\1\uffff\25\4\1\uffff"+
            "\4\4\1\uffff\12\4\1\uffff\7\4\1\uffff\10\4\1\uffff\1\4\1\uffff"+
            "\5\4\1\uffff\2\4\1\uffff\5\4\2\uffff\14\4\1\uffff\23\4\1\uffff"+
            "\25\4\1\uffff\3\4\1\uffff\5\4\1\uffff\4\4\1\uffff\3\4\1\uffff"+
            "\14\4\1\uffff\1\4\2\uffff\1\4\1\uffff\2\4\1\uffff\1\31\1\uffff"+
            "\1\4\2\uffff\1\4\2\uffff\2\4\7\uffff\5\4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
        for (int i=0; i<numStates; i++) {
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
            return "78:65: ( aliasList | columnNameTypeList )";
        }
    }
    static final String DFA17_eotS =
        "\u02dc\uffff";
    static final String DFA17_eofS =
        "\2\uffff\3\5\2\uffff\1\5\1\uffff\4\5\7\uffff\2\5\2\uffff\2\5\1\uffff"+
        "\1\5\1\uffff\1\5\u02be\uffff";
    static final String DFA17_minS =
        "\1\7\1\uffff\3\4\2\uffff\1\4\1\uffff\4\4\7\uffff\2\4\2\uffff\2\4"+
        "\1\uffff\1\4\1\uffff\1\4\1\32\55\uffff\1\32\55\uffff\1\32\56\uffff"+
        "\1\32\55\uffff\1\32\56\uffff\1\32\55\uffff\1\32\55\uffff\1\32\55"+
        "\uffff\1\32\55\uffff\1\32\55\uffff\1\32\55\uffff\1\32\55\uffff\1"+
        "\32\55\uffff\1\32\56\uffff\3\0\1\uffff\3\0\1\uffff\3\0\1\uffff\3"+
        "\0\1\uffff\3\0\1\uffff\3\0\1\uffff\3\0\1\uffff\3\0\1\uffff\3\0\1"+
        "\uffff\3\0\1\uffff\3\0\1\uffff\3\0\1\uffff\3\0\1\uffff\3\0";
    static final String DFA17_maxS =
        "\1\u013b\1\uffff\3\u0137\2\uffff\1\u0139\1\uffff\1\u0137\1\u0139"+
        "\2\u0137\7\uffff\2\u0137\2\uffff\2\u0137\1\uffff\1\u0137\1\uffff"+
        "\2\u0137\55\uffff\1\u0137\55\uffff\1\u0137\56\uffff\1\u0137\55\uffff"+
        "\1\u0137\56\uffff\1\u0137\55\uffff\1\u0137\55\uffff\1\u0137\55\uffff"+
        "\1\u0137\55\uffff\1\u0137\55\uffff\1\u0137\55\uffff\1\u0137\55\uffff"+
        "\1\u0137\55\uffff\1\u0137\56\uffff\3\0\1\uffff\3\0\1\uffff\3\0\1"+
        "\uffff\3\0\1\uffff\3\0\1\uffff\3\0\1\uffff\3\0\1\uffff\3\0\1\uffff"+
        "\3\0\1\uffff\3\0\1\uffff\3\0\1\uffff\3\0\1\uffff\3\0\1\uffff\3\0";
    static final String DFA17_acceptS =
        "\1\uffff\1\1\3\uffff\1\2\u029e\uffff\1\1\3\uffff\1\1\3\uffff\1\1"+
        "\3\uffff\1\1\3\uffff\1\1\3\uffff\1\1\3\uffff\1\1\3\uffff\1\1\3\uffff"+
        "\1\1\3\uffff\1\1\3\uffff\1\1\3\uffff\1\1\3\uffff\1\1\3\uffff\1\1"+
        "\3\uffff";
    static final String DFA17_specialS =
        "\1\0\35\uffff\1\1\55\uffff\1\2\55\uffff\1\3\56\uffff\1\4\55\uffff"+
        "\1\5\56\uffff\1\6\55\uffff\1\7\55\uffff\1\10\55\uffff\1\11\55\uffff"+
        "\1\12\55\uffff\1\13\55\uffff\1\14\55\uffff\1\15\55\uffff\1\16\56"+
        "\uffff\1\17\1\20\1\21\1\uffff\1\22\1\23\1\24\1\uffff\1\25\1\26\1"+
        "\27\1\uffff\1\30\1\31\1\32\1\uffff\1\33\1\34\1\35\1\uffff\1\36\1"+
        "\37\1\40\1\uffff\1\41\1\42\1\43\1\uffff\1\44\1\45\1\46\1\uffff\1"+
        "\47\1\50\1\51\1\uffff\1\52\1\53\1\54\1\uffff\1\55\1\56\1\57\1\uffff"+
        "\1\60\1\61\1\62\1\uffff\1\63\1\64\1\65\1\uffff\1\66\1\67\1\70}>";
    static final String[] DFA17_transitionS = {
            "\1\5\5\uffff\1\5\4\uffff\1\5\7\uffff\1\2\3\30\2\35\1\30\1\uffff"+
            "\1\30\1\25\1\35\1\30\1\35\1\30\1\35\3\31\1\35\2\30\1\35\1\30"+
            "\2\5\1\30\1\uffff\4\30\1\uffff\6\30\1\uffff\1\30\1\35\1\uffff"+
            "\1\35\1\uffff\1\11\1\13\1\35\1\30\1\uffff\1\30\1\7\3\30\1\35"+
            "\2\30\1\35\3\30\1\35\3\30\1\uffff\1\30\1\31\1\35\1\30\1\uffff"+
            "\1\30\1\uffff\1\30\1\uffff\1\30\1\33\2\30\1\uffff\1\35\1\24"+
            "\1\35\4\30\1\31\1\uffff\1\35\2\30\1\uffff\1\35\1\uffff\1\30"+
            "\3\35\1\uffff\3\30\1\5\1\30\2\35\2\30\1\35\3\30\1\35\1\31\1"+
            "\35\1\5\2\35\2\30\1\uffff\2\30\2\35\1\uffff\1\35\3\30\1\35\5"+
            "\30\1\uffff\1\5\6\30\1\uffff\1\30\1\35\1\30\1\5\1\30\1\4\1\35"+
            "\1\30\1\uffff\1\30\1\uffff\3\35\2\30\1\uffff\2\30\1\uffff\1"+
            "\35\2\30\1\35\1\30\2\uffff\2\30\1\35\2\30\1\35\2\30\1\35\3\30"+
            "\1\uffff\7\30\1\35\1\30\1\35\3\30\3\35\3\30\1\uffff\4\30\1\35"+
            "\5\30\1\31\7\30\1\3\1\35\1\30\1\uffff\3\30\1\uffff\1\12\1\30"+
            "\1\35\2\30\1\uffff\1\35\1\14\1\35\1\30\1\uffff\1\30\1\35\1\3"+
            "\1\uffff\3\30\1\35\2\30\2\35\2\30\1\35\1\30\1\uffff\1\30\2\uffff"+
            "\1\30\1\uffff\1\35\1\30\3\uffff\1\5\2\uffff\1\5\2\uffff\2\5"+
            "\7\uffff\1\1\4\5",
            "",
            "\3\5\3\uffff\1\5\3\uffff\2\5\1\uffff\1\36\2\uffff\2\5\1\uffff"+
            "\2\5\1\uffff\27\5\2\uffff\1\5\1\uffff\4\5\1\uffff\6\5\1\uffff"+
            "\2\5\1\uffff\1\5\1\uffff\4\5\1\uffff\20\5\1\uffff\4\5\1\uffff"+
            "\1\5\1\uffff\1\5\1\uffff\4\5\1\uffff\10\5\1\uffff\5\5\1\uffff"+
            "\10\5\1\uffff\14\5\1\uffff\4\5\1\uffff\4\5\1\uffff\12\5\1\uffff"+
            "\7\5\1\uffff\10\5\1\uffff\7\5\1\uffff\2\5\1\uffff\5\5\2\uffff"+
            "\66\5\1\uffff\3\5\1\uffff\5\5\1\uffff\4\5\1\uffff\3\5\1\uffff"+
            "\14\5\1\uffff\1\5\1\uffff\5\5\1\uffff\4\5\1\uffff\3\5\1\uffff"+
            "\1\5\3\uffff\1\5\3\uffff\1\5",
            "\3\5\3\uffff\1\5\3\uffff\2\5\1\uffff\1\114\2\uffff\2\5\1\uffff"+
            "\2\5\1\uffff\27\5\2\uffff\1\5\1\uffff\4\5\1\uffff\6\5\1\uffff"+
            "\2\5\1\uffff\1\5\1\uffff\4\5\1\uffff\20\5\1\uffff\4\5\1\uffff"+
            "\1\5\1\uffff\1\5\1\uffff\4\5\1\uffff\10\5\1\uffff\5\5\1\uffff"+
            "\10\5\1\uffff\14\5\1\uffff\4\5\1\uffff\4\5\1\uffff\12\5\1\uffff"+
            "\7\5\1\uffff\10\5\1\uffff\7\5\1\uffff\2\5\1\uffff\5\5\2\uffff"+
            "\66\5\1\uffff\3\5\1\uffff\5\5\1\uffff\4\5\1\uffff\3\5\1\uffff"+
            "\14\5\1\uffff\1\5\1\uffff\5\5\1\uffff\4\5\1\uffff\3\5\1\uffff"+
            "\1\5\3\uffff\1\5\3\uffff\1\5",
            "\3\5\3\uffff\1\5\3\uffff\2\5\1\uffff\1\172\2\uffff\2\5\1\uffff"+
            "\2\5\1\uffff\27\5\2\uffff\1\5\1\uffff\4\5\1\uffff\6\5\1\uffff"+
            "\2\5\1\uffff\1\5\1\uffff\4\5\1\uffff\20\5\1\uffff\4\5\1\uffff"+
            "\1\5\1\uffff\1\5\1\uffff\4\5\1\uffff\10\5\1\uffff\5\5\1\uffff"+
            "\10\5\1\uffff\14\5\1\uffff\4\5\1\uffff\4\5\1\uffff\12\5\1\uffff"+
            "\7\5\1\uffff\10\5\1\uffff\7\5\1\uffff\2\5\1\uffff\5\5\2\uffff"+
            "\66\5\1\uffff\3\5\1\uffff\5\5\1\uffff\4\5\1\uffff\3\5\1\uffff"+
            "\14\5\1\uffff\1\5\1\uffff\5\5\1\uffff\4\5\1\uffff\3\5\1\uffff"+
            "\1\5\3\uffff\1\5\3\uffff\1\5",
            "",
            "",
            "\3\5\3\uffff\1\5\3\uffff\2\5\1\uffff\1\u00a9\2\uffff\2\5\1"+
            "\uffff\2\5\1\uffff\27\5\2\uffff\1\5\1\uffff\4\5\1\uffff\6\5"+
            "\1\uffff\2\5\1\uffff\1\5\1\uffff\4\5\1\uffff\20\5\1\uffff\4"+
            "\5\1\uffff\1\5\1\uffff\1\5\1\uffff\4\5\1\uffff\10\5\1\uffff"+
            "\5\5\1\uffff\10\5\1\uffff\14\5\1\uffff\4\5\1\uffff\4\5\1\uffff"+
            "\12\5\1\uffff\7\5\1\uffff\10\5\1\uffff\7\5\1\uffff\2\5\1\uffff"+
            "\5\5\2\uffff\66\5\1\uffff\3\5\1\uffff\5\5\1\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\14\5\1\uffff\1\5\1\uffff\5\5\1\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\1\5\3\uffff\1\5\3\uffff\1\5\1\uffff\1\5",
            "",
            "\3\5\3\uffff\1\5\3\uffff\2\5\1\uffff\1\u00d7\2\uffff\2\5\1"+
            "\uffff\2\5\1\uffff\27\5\2\uffff\1\5\1\uffff\4\5\1\uffff\6\5"+
            "\1\uffff\2\5\1\uffff\1\5\1\uffff\4\5\1\uffff\20\5\1\uffff\4"+
            "\5\1\uffff\1\5\1\uffff\1\5\1\uffff\4\5\1\uffff\10\5\1\uffff"+
            "\5\5\1\uffff\10\5\1\uffff\14\5\1\uffff\4\5\1\uffff\4\5\1\uffff"+
            "\12\5\1\uffff\7\5\1\uffff\10\5\1\uffff\7\5\1\uffff\2\5\1\uffff"+
            "\5\5\2\uffff\66\5\1\uffff\3\5\1\uffff\5\5\1\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\14\5\1\uffff\1\5\1\uffff\5\5\1\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\1\5\3\uffff\1\5\3\uffff\1\5",
            "\3\5\3\uffff\1\5\3\uffff\2\5\1\uffff\1\u0106\2\uffff\2\5\1"+
            "\uffff\2\5\1\uffff\27\5\2\uffff\1\5\1\uffff\4\5\1\uffff\6\5"+
            "\1\uffff\2\5\1\uffff\1\5\1\uffff\4\5\1\uffff\20\5\1\uffff\4"+
            "\5\1\uffff\1\5\1\uffff\1\5\1\uffff\4\5\1\uffff\10\5\1\uffff"+
            "\5\5\1\uffff\10\5\1\uffff\14\5\1\uffff\4\5\1\uffff\4\5\1\uffff"+
            "\12\5\1\uffff\7\5\1\uffff\10\5\1\uffff\7\5\1\uffff\2\5\1\uffff"+
            "\5\5\2\uffff\66\5\1\uffff\3\5\1\uffff\5\5\1\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\14\5\1\uffff\1\5\1\uffff\5\5\1\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\1\5\3\uffff\1\5\3\uffff\1\5\1\uffff\1\5",
            "\3\5\3\uffff\1\5\3\uffff\2\5\1\uffff\1\u0134\2\uffff\2\5\1"+
            "\uffff\2\5\1\uffff\27\5\2\uffff\1\5\1\uffff\4\5\1\uffff\6\5"+
            "\1\uffff\2\5\1\uffff\1\5\1\uffff\4\5\1\uffff\20\5\1\uffff\4"+
            "\5\1\uffff\1\5\1\uffff\1\5\1\uffff\4\5\1\uffff\10\5\1\uffff"+
            "\5\5\1\uffff\10\5\1\uffff\14\5\1\uffff\4\5\1\uffff\4\5\1\uffff"+
            "\12\5\1\uffff\7\5\1\uffff\10\5\1\uffff\7\5\1\uffff\2\5\1\uffff"+
            "\5\5\2\uffff\66\5\1\uffff\3\5\1\uffff\5\5\1\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\14\5\1\uffff\1\5\1\uffff\5\5\1\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\1\5\3\uffff\1\5\3\uffff\1\5",
            "\3\5\3\uffff\1\5\3\uffff\2\5\1\uffff\1\u0162\2\uffff\2\5\1"+
            "\uffff\2\5\1\uffff\27\5\2\uffff\1\5\1\uffff\4\5\1\uffff\6\5"+
            "\1\uffff\2\5\1\uffff\1\5\1\uffff\4\5\1\uffff\20\5\1\uffff\4"+
            "\5\1\uffff\1\5\1\uffff\1\5\1\uffff\4\5\1\uffff\10\5\1\uffff"+
            "\5\5\1\uffff\10\5\1\uffff\14\5\1\uffff\4\5\1\uffff\4\5\1\uffff"+
            "\12\5\1\uffff\7\5\1\uffff\10\5\1\uffff\7\5\1\uffff\2\5\1\uffff"+
            "\5\5\2\uffff\66\5\1\uffff\3\5\1\uffff\5\5\1\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\14\5\1\uffff\1\5\1\uffff\5\5\1\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\1\5\3\uffff\1\5\3\uffff\1\5",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\3\5\3\uffff\1\5\3\uffff\2\5\1\uffff\1\u0190\2\uffff\2\5\1"+
            "\uffff\2\5\1\uffff\27\5\2\uffff\1\5\1\uffff\4\5\1\uffff\6\5"+
            "\1\uffff\2\5\1\uffff\1\5\1\uffff\4\5\1\uffff\20\5\1\uffff\4"+
            "\5\1\uffff\1\5\1\uffff\1\5\1\uffff\4\5\1\uffff\10\5\1\uffff"+
            "\5\5\1\uffff\10\5\1\uffff\14\5\1\uffff\4\5\1\uffff\4\5\1\uffff"+
            "\12\5\1\uffff\7\5\1\uffff\10\5\1\uffff\7\5\1\uffff\2\5\1\uffff"+
            "\5\5\2\uffff\66\5\1\uffff\3\5\1\uffff\5\5\1\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\14\5\1\uffff\1\5\1\uffff\5\5\1\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\1\5\3\uffff\1\5\3\uffff\1\5",
            "\3\5\3\uffff\1\5\3\uffff\2\5\1\uffff\1\u01be\2\uffff\2\5\1"+
            "\uffff\2\5\1\uffff\27\5\2\uffff\1\5\1\uffff\4\5\1\uffff\6\5"+
            "\1\uffff\2\5\1\uffff\1\5\1\uffff\4\5\1\uffff\20\5\1\uffff\4"+
            "\5\1\uffff\1\5\1\uffff\1\5\1\uffff\4\5\1\uffff\10\5\1\uffff"+
            "\5\5\1\uffff\10\5\1\uffff\14\5\1\uffff\4\5\1\uffff\4\5\1\uffff"+
            "\12\5\1\uffff\7\5\1\uffff\10\5\1\uffff\7\5\1\uffff\2\5\1\uffff"+
            "\5\5\2\uffff\66\5\1\uffff\3\5\1\uffff\5\5\1\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\14\5\1\uffff\1\5\1\uffff\5\5\1\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\1\5\3\uffff\1\5\3\uffff\1\5",
            "",
            "",
            "\3\5\3\uffff\1\5\3\uffff\2\5\1\uffff\1\u01ec\2\uffff\2\5\1"+
            "\uffff\2\5\1\uffff\27\5\2\uffff\1\5\1\uffff\4\5\1\uffff\6\5"+
            "\1\uffff\2\5\1\uffff\1\5\1\uffff\4\5\1\uffff\20\5\1\uffff\4"+
            "\5\1\uffff\1\5\1\uffff\1\5\1\uffff\4\5\1\uffff\10\5\1\uffff"+
            "\5\5\1\uffff\10\5\1\uffff\14\5\1\uffff\4\5\1\uffff\4\5\1\uffff"+
            "\12\5\1\uffff\7\5\1\uffff\10\5\1\uffff\7\5\1\uffff\2\5\1\uffff"+
            "\5\5\2\uffff\66\5\1\uffff\3\5\1\uffff\5\5\1\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\14\5\1\uffff\1\5\1\uffff\5\5\1\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\1\5\3\uffff\1\5\3\uffff\1\5",
            "\3\5\3\uffff\1\5\3\uffff\2\5\1\uffff\1\u021a\2\uffff\2\5\1"+
            "\uffff\2\5\1\uffff\27\5\2\uffff\1\5\1\uffff\4\5\1\uffff\6\5"+
            "\1\uffff\2\5\1\uffff\1\5\1\uffff\4\5\1\uffff\20\5\1\uffff\4"+
            "\5\1\uffff\1\5\1\uffff\1\5\1\uffff\4\5\1\uffff\10\5\1\uffff"+
            "\5\5\1\uffff\10\5\1\uffff\14\5\1\uffff\4\5\1\uffff\4\5\1\uffff"+
            "\12\5\1\uffff\7\5\1\uffff\10\5\1\uffff\7\5\1\uffff\2\5\1\uffff"+
            "\5\5\2\uffff\66\5\1\uffff\3\5\1\uffff\5\5\1\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\14\5\1\uffff\1\5\1\uffff\5\5\1\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\1\5\3\uffff\1\5\3\uffff\1\5",
            "",
            "\3\5\3\uffff\1\5\3\uffff\2\5\1\uffff\1\u0248\2\uffff\2\5\1"+
            "\uffff\2\5\1\uffff\27\5\2\uffff\1\5\1\uffff\4\5\1\uffff\6\5"+
            "\1\uffff\2\5\1\uffff\1\5\1\uffff\4\5\1\uffff\20\5\1\uffff\4"+
            "\5\1\uffff\1\5\1\uffff\1\5\1\uffff\4\5\1\uffff\10\5\1\uffff"+
            "\5\5\1\uffff\10\5\1\uffff\14\5\1\uffff\4\5\1\uffff\4\5\1\uffff"+
            "\12\5\1\uffff\7\5\1\uffff\10\5\1\uffff\7\5\1\uffff\2\5\1\uffff"+
            "\5\5\2\uffff\66\5\1\uffff\3\5\1\uffff\5\5\1\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\14\5\1\uffff\1\5\1\uffff\5\5\1\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\1\5\3\uffff\1\5\3\uffff\1\5",
            "",
            "\3\5\3\uffff\1\5\3\uffff\2\5\1\uffff\1\u0276\2\uffff\2\5\1"+
            "\uffff\2\5\1\uffff\27\5\2\uffff\1\5\1\uffff\4\5\1\uffff\6\5"+
            "\1\uffff\2\5\1\uffff\1\5\1\uffff\4\5\1\uffff\20\5\1\uffff\4"+
            "\5\1\uffff\1\5\1\uffff\1\5\1\uffff\4\5\1\uffff\10\5\1\uffff"+
            "\5\5\1\uffff\10\5\1\uffff\14\5\1\uffff\4\5\1\uffff\4\5\1\uffff"+
            "\12\5\1\uffff\7\5\1\uffff\10\5\1\uffff\7\5\1\uffff\2\5\1\uffff"+
            "\5\5\2\uffff\66\5\1\uffff\3\5\1\uffff\5\5\1\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\14\5\1\uffff\1\5\1\uffff\5\5\1\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\1\5\3\uffff\1\5\3\uffff\1\5",
            "\1\u02a5\3\u02a6\2\u02a7\1\u02a6\1\uffff\1\u02a6\2\u02a7\1"+
            "\u02a6\1\u02a7\1\u02a6\5\u02a7\2\u02a6\1\u02a7\1\u02a6\2\uffff"+
            "\1\u02a6\1\uffff\4\u02a6\1\uffff\6\u02a6\1\uffff\1\u02a6\1\u02a7"+
            "\1\uffff\1\u02a7\1\uffff\3\u02a7\1\u02a6\1\uffff\1\u02a6\1\u02a7"+
            "\3\u02a6\1\u02a7\2\u02a6\1\u02a7\3\u02a6\1\u02a7\3\u02a6\1\uffff"+
            "\1\u02a6\2\u02a7\1\u02a6\1\uffff\1\u02a6\1\uffff\1\u02a6\1\uffff"+
            "\1\u02a6\1\u02a7\2\u02a6\1\uffff\3\u02a7\4\u02a6\1\u02a7\1\uffff"+
            "\1\u02a7\2\u02a6\1\uffff\1\u02a7\1\uffff\1\u02a6\3\u02a7\1\uffff"+
            "\3\u02a6\1\uffff\1\u02a6\2\u02a7\2\u02a6\1\u02a7\3\u02a6\3\u02a7"+
            "\1\uffff\2\u02a7\2\u02a6\1\uffff\2\u02a6\2\u02a7\1\uffff\1\u02a7"+
            "\3\u02a6\1\u02a7\5\u02a6\2\uffff\6\u02a6\1\uffff\1\u02a6\1\u02a7"+
            "\1\u02a6\1\uffff\1\u02a6\2\u02a7\1\u02a6\1\uffff\1\u02a6\1\uffff"+
            "\3\u02a7\2\u02a6\1\uffff\2\u02a6\1\uffff\1\u02a7\2\u02a6\1\u02a7"+
            "\1\u02a6\2\uffff\2\u02a6\1\u02a7\2\u02a6\1\u02a7\2\u02a6\1\u02a7"+
            "\3\u02a6\1\uffff\7\u02a6\1\u02a7\1\u02a6\1\u02a7\3\u02a6\3\u02a7"+
            "\3\u02a6\1\uffff\4\u02a6\1\u02a7\5\u02a6\1\u02a7\10\u02a6\1"+
            "\u02a7\1\u02a6\1\uffff\3\u02a6\1\uffff\1\u02a7\1\u02a6\1\u02a7"+
            "\2\u02a6\1\uffff\3\u02a7\1\u02a6\1\uffff\1\u02a6\1\u02a7\1\u02a6"+
            "\1\uffff\3\u02a6\1\u02a7\2\u02a6\2\u02a7\2\u02a6\1\u02a7\1\u02a6"+
            "\1\uffff\1\u02a6\2\uffff\1\u02a6\1\uffff\1\u02a7\1\u02a6\22"+
            "\uffff\1\u02a4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u02a9\3\u02aa\2\u02ab\1\u02aa\1\uffff\1\u02aa\2\u02ab\1"+
            "\u02aa\1\u02ab\1\u02aa\5\u02ab\2\u02aa\1\u02ab\1\u02aa\2\uffff"+
            "\1\u02aa\1\uffff\4\u02aa\1\uffff\6\u02aa\1\uffff\1\u02aa\1\u02ab"+
            "\1\uffff\1\u02ab\1\uffff\3\u02ab\1\u02aa\1\uffff\1\u02aa\1\u02ab"+
            "\3\u02aa\1\u02ab\2\u02aa\1\u02ab\3\u02aa\1\u02ab\3\u02aa\1\uffff"+
            "\1\u02aa\2\u02ab\1\u02aa\1\uffff\1\u02aa\1\uffff\1\u02aa\1\uffff"+
            "\1\u02aa\1\u02ab\2\u02aa\1\uffff\3\u02ab\4\u02aa\1\u02ab\1\uffff"+
            "\1\u02ab\2\u02aa\1\uffff\1\u02ab\1\uffff\1\u02aa\3\u02ab\1\uffff"+
            "\3\u02aa\1\uffff\1\u02aa\2\u02ab\2\u02aa\1\u02ab\3\u02aa\3\u02ab"+
            "\1\uffff\2\u02ab\2\u02aa\1\uffff\2\u02aa\2\u02ab\1\uffff\1\u02ab"+
            "\3\u02aa\1\u02ab\5\u02aa\2\uffff\6\u02aa\1\uffff\1\u02aa\1\u02ab"+
            "\1\u02aa\1\uffff\1\u02aa\2\u02ab\1\u02aa\1\uffff\1\u02aa\1\uffff"+
            "\3\u02ab\2\u02aa\1\uffff\2\u02aa\1\uffff\1\u02ab\2\u02aa\1\u02ab"+
            "\1\u02aa\2\uffff\2\u02aa\1\u02ab\2\u02aa\1\u02ab\2\u02aa\1\u02ab"+
            "\3\u02aa\1\uffff\7\u02aa\1\u02ab\1\u02aa\1\u02ab\3\u02aa\3\u02ab"+
            "\3\u02aa\1\uffff\4\u02aa\1\u02ab\5\u02aa\1\u02ab\10\u02aa\1"+
            "\u02ab\1\u02aa\1\uffff\3\u02aa\1\uffff\1\u02ab\1\u02aa\1\u02ab"+
            "\2\u02aa\1\uffff\3\u02ab\1\u02aa\1\uffff\1\u02aa\1\u02ab\1\u02aa"+
            "\1\uffff\3\u02aa\1\u02ab\2\u02aa\2\u02ab\2\u02aa\1\u02ab\1\u02aa"+
            "\1\uffff\1\u02aa\2\uffff\1\u02aa\1\uffff\1\u02ab\1\u02aa\22"+
            "\uffff\1\u02a8",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u02ad\3\u02ae\2\u02af\1\u02ae\1\uffff\1\u02ae\2\u02af\1"+
            "\u02ae\1\u02af\1\u02ae\5\u02af\2\u02ae\1\u02af\1\u02ae\2\uffff"+
            "\1\u02ae\1\uffff\4\u02ae\1\uffff\6\u02ae\1\uffff\1\u02ae\1\u02af"+
            "\1\uffff\1\u02af\1\uffff\3\u02af\1\u02ae\1\uffff\1\u02ae\1\u02af"+
            "\3\u02ae\1\u02af\2\u02ae\1\u02af\3\u02ae\1\u02af\3\u02ae\1\uffff"+
            "\1\u02ae\2\u02af\1\u02ae\1\uffff\1\u02ae\1\uffff\1\u02ae\1\uffff"+
            "\1\u02ae\1\u02af\2\u02ae\1\uffff\3\u02af\4\u02ae\1\u02af\1\uffff"+
            "\1\u02af\2\u02ae\1\uffff\1\u02af\1\uffff\1\u02ae\3\u02af\1\uffff"+
            "\3\u02ae\1\uffff\1\u02ae\2\u02af\2\u02ae\1\u02af\3\u02ae\3\u02af"+
            "\1\uffff\2\u02af\2\u02ae\1\uffff\2\u02ae\2\u02af\1\uffff\1\u02af"+
            "\3\u02ae\1\u02af\5\u02ae\2\uffff\6\u02ae\1\uffff\1\u02ae\1\u02af"+
            "\1\u02ae\1\uffff\1\u02ae\2\u02af\1\u02ae\1\uffff\1\u02ae\1\uffff"+
            "\3\u02af\2\u02ae\1\uffff\2\u02ae\1\uffff\1\u02af\2\u02ae\1\u02af"+
            "\1\u02ae\2\uffff\2\u02ae\1\u02af\2\u02ae\1\u02af\2\u02ae\1\u02af"+
            "\3\u02ae\1\uffff\7\u02ae\1\u02af\1\u02ae\1\u02af\3\u02ae\3\u02af"+
            "\3\u02ae\1\uffff\4\u02ae\1\u02af\5\u02ae\1\u02af\10\u02ae\1"+
            "\u02af\1\u02ae\1\uffff\3\u02ae\1\uffff\1\u02af\1\u02ae\1\u02af"+
            "\2\u02ae\1\uffff\3\u02af\1\u02ae\1\uffff\1\u02ae\1\u02af\1\u02ae"+
            "\1\uffff\3\u02ae\1\u02af\2\u02ae\2\u02af\2\u02ae\1\u02af\1\u02ae"+
            "\1\uffff\1\u02ae\2\uffff\1\u02ae\1\uffff\1\u02af\1\u02ae\22"+
            "\uffff\1\u02ac",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u02b1\3\u02b2\2\u02b3\1\u02b2\1\uffff\1\u02b2\2\u02b3\1"+
            "\u02b2\1\u02b3\1\u02b2\5\u02b3\2\u02b2\1\u02b3\1\u02b2\2\uffff"+
            "\1\u02b2\1\uffff\4\u02b2\1\uffff\6\u02b2\1\uffff\1\u02b2\1\u02b3"+
            "\1\uffff\1\u02b3\1\uffff\3\u02b3\1\u02b2\1\uffff\1\u02b2\1\u02b3"+
            "\3\u02b2\1\u02b3\2\u02b2\1\u02b3\3\u02b2\1\u02b3\3\u02b2\1\uffff"+
            "\1\u02b2\2\u02b3\1\u02b2\1\uffff\1\u02b2\1\uffff\1\u02b2\1\uffff"+
            "\1\u02b2\1\u02b3\2\u02b2\1\uffff\3\u02b3\4\u02b2\1\u02b3\1\uffff"+
            "\1\u02b3\2\u02b2\1\uffff\1\u02b3\1\uffff\1\u02b2\3\u02b3\1\uffff"+
            "\3\u02b2\1\uffff\1\u02b2\2\u02b3\2\u02b2\1\u02b3\3\u02b2\3\u02b3"+
            "\1\uffff\2\u02b3\2\u02b2\1\uffff\2\u02b2\2\u02b3\1\uffff\1\u02b3"+
            "\3\u02b2\1\u02b3\5\u02b2\2\uffff\6\u02b2\1\uffff\1\u02b2\1\u02b3"+
            "\1\u02b2\1\uffff\1\u02b2\2\u02b3\1\u02b2\1\uffff\1\u02b2\1\uffff"+
            "\3\u02b3\2\u02b2\1\uffff\2\u02b2\1\uffff\1\u02b3\2\u02b2\1\u02b3"+
            "\1\u02b2\2\uffff\2\u02b2\1\u02b3\2\u02b2\1\u02b3\2\u02b2\1\u02b3"+
            "\3\u02b2\1\uffff\7\u02b2\1\u02b3\1\u02b2\1\u02b3\3\u02b2\3\u02b3"+
            "\3\u02b2\1\uffff\4\u02b2\1\u02b3\5\u02b2\1\u02b3\10\u02b2\1"+
            "\u02b3\1\u02b2\1\uffff\3\u02b2\1\uffff\1\u02b3\1\u02b2\1\u02b3"+
            "\2\u02b2\1\uffff\3\u02b3\1\u02b2\1\uffff\1\u02b2\1\u02b3\1\u02b2"+
            "\1\uffff\3\u02b2\1\u02b3\2\u02b2\2\u02b3\2\u02b2\1\u02b3\1\u02b2"+
            "\1\uffff\1\u02b2\2\uffff\1\u02b2\1\uffff\1\u02b3\1\u02b2\22"+
            "\uffff\1\u02b0",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u02b5\3\u02b6\2\u02b7\1\u02b6\1\uffff\1\u02b6\2\u02b7\1"+
            "\u02b6\1\u02b7\1\u02b6\5\u02b7\2\u02b6\1\u02b7\1\u02b6\2\uffff"+
            "\1\u02b6\1\uffff\4\u02b6\1\uffff\6\u02b6\1\uffff\1\u02b6\1\u02b7"+
            "\1\uffff\1\u02b7\1\uffff\3\u02b7\1\u02b6\1\uffff\1\u02b6\1\u02b7"+
            "\3\u02b6\1\u02b7\2\u02b6\1\u02b7\3\u02b6\1\u02b7\3\u02b6\1\uffff"+
            "\1\u02b6\2\u02b7\1\u02b6\1\uffff\1\u02b6\1\uffff\1\u02b6\1\uffff"+
            "\1\u02b6\1\u02b7\2\u02b6\1\uffff\3\u02b7\4\u02b6\1\u02b7\1\uffff"+
            "\1\u02b7\2\u02b6\1\uffff\1\u02b7\1\uffff\1\u02b6\3\u02b7\1\uffff"+
            "\3\u02b6\1\uffff\1\u02b6\2\u02b7\2\u02b6\1\u02b7\3\u02b6\3\u02b7"+
            "\1\uffff\2\u02b7\2\u02b6\1\uffff\2\u02b6\2\u02b7\1\uffff\1\u02b7"+
            "\3\u02b6\1\u02b7\5\u02b6\2\uffff\6\u02b6\1\uffff\1\u02b6\1\u02b7"+
            "\1\u02b6\1\uffff\1\u02b6\2\u02b7\1\u02b6\1\uffff\1\u02b6\1\uffff"+
            "\3\u02b7\2\u02b6\1\uffff\2\u02b6\1\uffff\1\u02b7\2\u02b6\1\u02b7"+
            "\1\u02b6\2\uffff\2\u02b6\1\u02b7\2\u02b6\1\u02b7\2\u02b6\1\u02b7"+
            "\3\u02b6\1\uffff\7\u02b6\1\u02b7\1\u02b6\1\u02b7\3\u02b6\3\u02b7"+
            "\3\u02b6\1\uffff\4\u02b6\1\u02b7\5\u02b6\1\u02b7\10\u02b6\1"+
            "\u02b7\1\u02b6\1\uffff\3\u02b6\1\uffff\1\u02b7\1\u02b6\1\u02b7"+
            "\2\u02b6\1\uffff\3\u02b7\1\u02b6\1\uffff\1\u02b6\1\u02b7\1\u02b6"+
            "\1\uffff\3\u02b6\1\u02b7\2\u02b6\2\u02b7\2\u02b6\1\u02b7\1\u02b6"+
            "\1\uffff\1\u02b6\2\uffff\1\u02b6\1\uffff\1\u02b7\1\u02b6\22"+
            "\uffff\1\u02b4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u02b9\3\u02ba\2\u02bb\1\u02ba\1\uffff\1\u02ba\2\u02bb\1"+
            "\u02ba\1\u02bb\1\u02ba\5\u02bb\2\u02ba\1\u02bb\1\u02ba\2\uffff"+
            "\1\u02ba\1\uffff\4\u02ba\1\uffff\6\u02ba\1\uffff\1\u02ba\1\u02bb"+
            "\1\uffff\1\u02bb\1\uffff\3\u02bb\1\u02ba\1\uffff\1\u02ba\1\u02bb"+
            "\3\u02ba\1\u02bb\2\u02ba\1\u02bb\3\u02ba\1\u02bb\3\u02ba\1\uffff"+
            "\1\u02ba\2\u02bb\1\u02ba\1\uffff\1\u02ba\1\uffff\1\u02ba\1\uffff"+
            "\1\u02ba\1\u02bb\2\u02ba\1\uffff\3\u02bb\4\u02ba\1\u02bb\1\uffff"+
            "\1\u02bb\2\u02ba\1\uffff\1\u02bb\1\uffff\1\u02ba\3\u02bb\1\uffff"+
            "\3\u02ba\1\uffff\1\u02ba\2\u02bb\2\u02ba\1\u02bb\3\u02ba\3\u02bb"+
            "\1\uffff\2\u02bb\2\u02ba\1\uffff\2\u02ba\2\u02bb\1\uffff\1\u02bb"+
            "\3\u02ba\1\u02bb\5\u02ba\2\uffff\6\u02ba\1\uffff\1\u02ba\1\u02bb"+
            "\1\u02ba\1\uffff\1\u02ba\2\u02bb\1\u02ba\1\uffff\1\u02ba\1\uffff"+
            "\3\u02bb\2\u02ba\1\uffff\2\u02ba\1\uffff\1\u02bb\2\u02ba\1\u02bb"+
            "\1\u02ba\2\uffff\2\u02ba\1\u02bb\2\u02ba\1\u02bb\2\u02ba\1\u02bb"+
            "\3\u02ba\1\uffff\7\u02ba\1\u02bb\1\u02ba\1\u02bb\3\u02ba\3\u02bb"+
            "\3\u02ba\1\uffff\4\u02ba\1\u02bb\5\u02ba\1\u02bb\10\u02ba\1"+
            "\u02bb\1\u02ba\1\uffff\3\u02ba\1\uffff\1\u02bb\1\u02ba\1\u02bb"+
            "\2\u02ba\1\uffff\3\u02bb\1\u02ba\1\uffff\1\u02ba\1\u02bb\1\u02ba"+
            "\1\uffff\3\u02ba\1\u02bb\2\u02ba\2\u02bb\2\u02ba\1\u02bb\1\u02ba"+
            "\1\uffff\1\u02ba\2\uffff\1\u02ba\1\uffff\1\u02bb\1\u02ba\22"+
            "\uffff\1\u02b8",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u02bd\3\u02be\2\u02bf\1\u02be\1\uffff\1\u02be\2\u02bf\1"+
            "\u02be\1\u02bf\1\u02be\5\u02bf\2\u02be\1\u02bf\1\u02be\2\uffff"+
            "\1\u02be\1\uffff\4\u02be\1\uffff\6\u02be\1\uffff\1\u02be\1\u02bf"+
            "\1\uffff\1\u02bf\1\uffff\3\u02bf\1\u02be\1\uffff\1\u02be\1\u02bf"+
            "\3\u02be\1\u02bf\2\u02be\1\u02bf\3\u02be\1\u02bf\3\u02be\1\uffff"+
            "\1\u02be\2\u02bf\1\u02be\1\uffff\1\u02be\1\uffff\1\u02be\1\uffff"+
            "\1\u02be\1\u02bf\2\u02be\1\uffff\3\u02bf\4\u02be\1\u02bf\1\uffff"+
            "\1\u02bf\2\u02be\1\uffff\1\u02bf\1\uffff\1\u02be\3\u02bf\1\uffff"+
            "\3\u02be\1\uffff\1\u02be\2\u02bf\2\u02be\1\u02bf\3\u02be\3\u02bf"+
            "\1\uffff\2\u02bf\2\u02be\1\uffff\2\u02be\2\u02bf\1\uffff\1\u02bf"+
            "\3\u02be\1\u02bf\5\u02be\2\uffff\6\u02be\1\uffff\1\u02be\1\u02bf"+
            "\1\u02be\1\uffff\1\u02be\2\u02bf\1\u02be\1\uffff\1\u02be\1\uffff"+
            "\3\u02bf\2\u02be\1\uffff\2\u02be\1\uffff\1\u02bf\2\u02be\1\u02bf"+
            "\1\u02be\2\uffff\2\u02be\1\u02bf\2\u02be\1\u02bf\2\u02be\1\u02bf"+
            "\3\u02be\1\uffff\7\u02be\1\u02bf\1\u02be\1\u02bf\3\u02be\3\u02bf"+
            "\3\u02be\1\uffff\4\u02be\1\u02bf\5\u02be\1\u02bf\10\u02be\1"+
            "\u02bf\1\u02be\1\uffff\3\u02be\1\uffff\1\u02bf\1\u02be\1\u02bf"+
            "\2\u02be\1\uffff\3\u02bf\1\u02be\1\uffff\1\u02be\1\u02bf\1\u02be"+
            "\1\uffff\3\u02be\1\u02bf\2\u02be\2\u02bf\2\u02be\1\u02bf\1\u02be"+
            "\1\uffff\1\u02be\2\uffff\1\u02be\1\uffff\1\u02bf\1\u02be\22"+
            "\uffff\1\u02bc",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u02c1\3\u02c2\2\u02c3\1\u02c2\1\uffff\1\u02c2\2\u02c3\1"+
            "\u02c2\1\u02c3\1\u02c2\5\u02c3\2\u02c2\1\u02c3\1\u02c2\2\uffff"+
            "\1\u02c2\1\uffff\4\u02c2\1\uffff\6\u02c2\1\uffff\1\u02c2\1\u02c3"+
            "\1\uffff\1\u02c3\1\uffff\3\u02c3\1\u02c2\1\uffff\1\u02c2\1\u02c3"+
            "\3\u02c2\1\u02c3\2\u02c2\1\u02c3\3\u02c2\1\u02c3\3\u02c2\1\uffff"+
            "\1\u02c2\2\u02c3\1\u02c2\1\uffff\1\u02c2\1\uffff\1\u02c2\1\uffff"+
            "\1\u02c2\1\u02c3\2\u02c2\1\uffff\3\u02c3\4\u02c2\1\u02c3\1\uffff"+
            "\1\u02c3\2\u02c2\1\uffff\1\u02c3\1\uffff\1\u02c2\3\u02c3\1\uffff"+
            "\3\u02c2\1\uffff\1\u02c2\2\u02c3\2\u02c2\1\u02c3\3\u02c2\3\u02c3"+
            "\1\uffff\2\u02c3\2\u02c2\1\uffff\2\u02c2\2\u02c3\1\uffff\1\u02c3"+
            "\3\u02c2\1\u02c3\5\u02c2\2\uffff\6\u02c2\1\uffff\1\u02c2\1\u02c3"+
            "\1\u02c2\1\uffff\1\u02c2\2\u02c3\1\u02c2\1\uffff\1\u02c2\1\uffff"+
            "\3\u02c3\2\u02c2\1\uffff\2\u02c2\1\uffff\1\u02c3\2\u02c2\1\u02c3"+
            "\1\u02c2\2\uffff\2\u02c2\1\u02c3\2\u02c2\1\u02c3\2\u02c2\1\u02c3"+
            "\3\u02c2\1\uffff\7\u02c2\1\u02c3\1\u02c2\1\u02c3\3\u02c2\3\u02c3"+
            "\3\u02c2\1\uffff\4\u02c2\1\u02c3\5\u02c2\1\u02c3\10\u02c2\1"+
            "\u02c3\1\u02c2\1\uffff\3\u02c2\1\uffff\1\u02c3\1\u02c2\1\u02c3"+
            "\2\u02c2\1\uffff\3\u02c3\1\u02c2\1\uffff\1\u02c2\1\u02c3\1\u02c2"+
            "\1\uffff\3\u02c2\1\u02c3\2\u02c2\2\u02c3\2\u02c2\1\u02c3\1\u02c2"+
            "\1\uffff\1\u02c2\2\uffff\1\u02c2\1\uffff\1\u02c3\1\u02c2\22"+
            "\uffff\1\u02c0",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u02c5\3\u02c6\2\u02c7\1\u02c6\1\uffff\1\u02c6\2\u02c7\1"+
            "\u02c6\1\u02c7\1\u02c6\5\u02c7\2\u02c6\1\u02c7\1\u02c6\2\uffff"+
            "\1\u02c6\1\uffff\4\u02c6\1\uffff\6\u02c6\1\uffff\1\u02c6\1\u02c7"+
            "\1\uffff\1\u02c7\1\uffff\3\u02c7\1\u02c6\1\uffff\1\u02c6\1\u02c7"+
            "\3\u02c6\1\u02c7\2\u02c6\1\u02c7\3\u02c6\1\u02c7\3\u02c6\1\uffff"+
            "\1\u02c6\2\u02c7\1\u02c6\1\uffff\1\u02c6\1\uffff\1\u02c6\1\uffff"+
            "\1\u02c6\1\u02c7\2\u02c6\1\uffff\3\u02c7\4\u02c6\1\u02c7\1\uffff"+
            "\1\u02c7\2\u02c6\1\uffff\1\u02c7\1\uffff\1\u02c6\3\u02c7\1\uffff"+
            "\3\u02c6\1\uffff\1\u02c6\2\u02c7\2\u02c6\1\u02c7\3\u02c6\3\u02c7"+
            "\1\uffff\2\u02c7\2\u02c6\1\uffff\2\u02c6\2\u02c7\1\uffff\1\u02c7"+
            "\3\u02c6\1\u02c7\5\u02c6\2\uffff\6\u02c6\1\uffff\1\u02c6\1\u02c7"+
            "\1\u02c6\1\uffff\1\u02c6\2\u02c7\1\u02c6\1\uffff\1\u02c6\1\uffff"+
            "\3\u02c7\2\u02c6\1\uffff\2\u02c6\1\uffff\1\u02c7\2\u02c6\1\u02c7"+
            "\1\u02c6\2\uffff\2\u02c6\1\u02c7\2\u02c6\1\u02c7\2\u02c6\1\u02c7"+
            "\3\u02c6\1\uffff\7\u02c6\1\u02c7\1\u02c6\1\u02c7\3\u02c6\3\u02c7"+
            "\3\u02c6\1\uffff\4\u02c6\1\u02c7\5\u02c6\1\u02c7\10\u02c6\1"+
            "\u02c7\1\u02c6\1\uffff\3\u02c6\1\uffff\1\u02c7\1\u02c6\1\u02c7"+
            "\2\u02c6\1\uffff\3\u02c7\1\u02c6\1\uffff\1\u02c6\1\u02c7\1\u02c6"+
            "\1\uffff\3\u02c6\1\u02c7\2\u02c6\2\u02c7\2\u02c6\1\u02c7\1\u02c6"+
            "\1\uffff\1\u02c6\2\uffff\1\u02c6\1\uffff\1\u02c7\1\u02c6\22"+
            "\uffff\1\u02c4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u02c9\3\u02ca\2\u02cb\1\u02ca\1\uffff\1\u02ca\2\u02cb\1"+
            "\u02ca\1\u02cb\1\u02ca\5\u02cb\2\u02ca\1\u02cb\1\u02ca\2\uffff"+
            "\1\u02ca\1\uffff\4\u02ca\1\uffff\6\u02ca\1\uffff\1\u02ca\1\u02cb"+
            "\1\uffff\1\u02cb\1\uffff\3\u02cb\1\u02ca\1\uffff\1\u02ca\1\u02cb"+
            "\3\u02ca\1\u02cb\2\u02ca\1\u02cb\3\u02ca\1\u02cb\3\u02ca\1\uffff"+
            "\1\u02ca\2\u02cb\1\u02ca\1\uffff\1\u02ca\1\uffff\1\u02ca\1\uffff"+
            "\1\u02ca\1\u02cb\2\u02ca\1\uffff\3\u02cb\4\u02ca\1\u02cb\1\uffff"+
            "\1\u02cb\2\u02ca\1\uffff\1\u02cb\1\uffff\1\u02ca\3\u02cb\1\uffff"+
            "\3\u02ca\1\uffff\1\u02ca\2\u02cb\2\u02ca\1\u02cb\3\u02ca\3\u02cb"+
            "\1\uffff\2\u02cb\2\u02ca\1\uffff\2\u02ca\2\u02cb\1\uffff\1\u02cb"+
            "\3\u02ca\1\u02cb\5\u02ca\2\uffff\6\u02ca\1\uffff\1\u02ca\1\u02cb"+
            "\1\u02ca\1\uffff\1\u02ca\2\u02cb\1\u02ca\1\uffff\1\u02ca\1\uffff"+
            "\3\u02cb\2\u02ca\1\uffff\2\u02ca\1\uffff\1\u02cb\2\u02ca\1\u02cb"+
            "\1\u02ca\2\uffff\2\u02ca\1\u02cb\2\u02ca\1\u02cb\2\u02ca\1\u02cb"+
            "\3\u02ca\1\uffff\7\u02ca\1\u02cb\1\u02ca\1\u02cb\3\u02ca\3\u02cb"+
            "\3\u02ca\1\uffff\4\u02ca\1\u02cb\5\u02ca\1\u02cb\10\u02ca\1"+
            "\u02cb\1\u02ca\1\uffff\3\u02ca\1\uffff\1\u02cb\1\u02ca\1\u02cb"+
            "\2\u02ca\1\uffff\3\u02cb\1\u02ca\1\uffff\1\u02ca\1\u02cb\1\u02ca"+
            "\1\uffff\3\u02ca\1\u02cb\2\u02ca\2\u02cb\2\u02ca\1\u02cb\1\u02ca"+
            "\1\uffff\1\u02ca\2\uffff\1\u02ca\1\uffff\1\u02cb\1\u02ca\22"+
            "\uffff\1\u02c8",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u02cd\3\u02ce\2\u02cf\1\u02ce\1\uffff\1\u02ce\2\u02cf\1"+
            "\u02ce\1\u02cf\1\u02ce\5\u02cf\2\u02ce\1\u02cf\1\u02ce\2\uffff"+
            "\1\u02ce\1\uffff\4\u02ce\1\uffff\6\u02ce\1\uffff\1\u02ce\1\u02cf"+
            "\1\uffff\1\u02cf\1\uffff\3\u02cf\1\u02ce\1\uffff\1\u02ce\1\u02cf"+
            "\3\u02ce\1\u02cf\2\u02ce\1\u02cf\3\u02ce\1\u02cf\3\u02ce\1\uffff"+
            "\1\u02ce\2\u02cf\1\u02ce\1\uffff\1\u02ce\1\uffff\1\u02ce\1\uffff"+
            "\1\u02ce\1\u02cf\2\u02ce\1\uffff\3\u02cf\4\u02ce\1\u02cf\1\uffff"+
            "\1\u02cf\2\u02ce\1\uffff\1\u02cf\1\uffff\1\u02ce\3\u02cf\1\uffff"+
            "\3\u02ce\1\uffff\1\u02ce\2\u02cf\2\u02ce\1\u02cf\3\u02ce\3\u02cf"+
            "\1\uffff\2\u02cf\2\u02ce\1\uffff\2\u02ce\2\u02cf\1\uffff\1\u02cf"+
            "\3\u02ce\1\u02cf\5\u02ce\2\uffff\6\u02ce\1\uffff\1\u02ce\1\u02cf"+
            "\1\u02ce\1\uffff\1\u02ce\2\u02cf\1\u02ce\1\uffff\1\u02ce\1\uffff"+
            "\3\u02cf\2\u02ce\1\uffff\2\u02ce\1\uffff\1\u02cf\2\u02ce\1\u02cf"+
            "\1\u02ce\2\uffff\2\u02ce\1\u02cf\2\u02ce\1\u02cf\2\u02ce\1\u02cf"+
            "\3\u02ce\1\uffff\7\u02ce\1\u02cf\1\u02ce\1\u02cf\3\u02ce\3\u02cf"+
            "\3\u02ce\1\uffff\4\u02ce\1\u02cf\5\u02ce\1\u02cf\10\u02ce\1"+
            "\u02cf\1\u02ce\1\uffff\3\u02ce\1\uffff\1\u02cf\1\u02ce\1\u02cf"+
            "\2\u02ce\1\uffff\3\u02cf\1\u02ce\1\uffff\1\u02ce\1\u02cf\1\u02ce"+
            "\1\uffff\3\u02ce\1\u02cf\2\u02ce\2\u02cf\2\u02ce\1\u02cf\1\u02ce"+
            "\1\uffff\1\u02ce\2\uffff\1\u02ce\1\uffff\1\u02cf\1\u02ce\22"+
            "\uffff\1\u02cc",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u02d1\3\u02d2\2\u02d3\1\u02d2\1\uffff\1\u02d2\2\u02d3\1"+
            "\u02d2\1\u02d3\1\u02d2\5\u02d3\2\u02d2\1\u02d3\1\u02d2\2\uffff"+
            "\1\u02d2\1\uffff\4\u02d2\1\uffff\6\u02d2\1\uffff\1\u02d2\1\u02d3"+
            "\1\uffff\1\u02d3\1\uffff\3\u02d3\1\u02d2\1\uffff\1\u02d2\1\u02d3"+
            "\3\u02d2\1\u02d3\2\u02d2\1\u02d3\3\u02d2\1\u02d3\3\u02d2\1\uffff"+
            "\1\u02d2\2\u02d3\1\u02d2\1\uffff\1\u02d2\1\uffff\1\u02d2\1\uffff"+
            "\1\u02d2\1\u02d3\2\u02d2\1\uffff\3\u02d3\4\u02d2\1\u02d3\1\uffff"+
            "\1\u02d3\2\u02d2\1\uffff\1\u02d3\1\uffff\1\u02d2\3\u02d3\1\uffff"+
            "\3\u02d2\1\uffff\1\u02d2\2\u02d3\2\u02d2\1\u02d3\3\u02d2\3\u02d3"+
            "\1\uffff\2\u02d3\2\u02d2\1\uffff\2\u02d2\2\u02d3\1\uffff\1\u02d3"+
            "\3\u02d2\1\u02d3\5\u02d2\2\uffff\6\u02d2\1\uffff\1\u02d2\1\u02d3"+
            "\1\u02d2\1\uffff\1\u02d2\2\u02d3\1\u02d2\1\uffff\1\u02d2\1\uffff"+
            "\3\u02d3\2\u02d2\1\uffff\2\u02d2\1\uffff\1\u02d3\2\u02d2\1\u02d3"+
            "\1\u02d2\2\uffff\2\u02d2\1\u02d3\2\u02d2\1\u02d3\2\u02d2\1\u02d3"+
            "\3\u02d2\1\uffff\7\u02d2\1\u02d3\1\u02d2\1\u02d3\3\u02d2\3\u02d3"+
            "\3\u02d2\1\uffff\4\u02d2\1\u02d3\5\u02d2\1\u02d3\10\u02d2\1"+
            "\u02d3\1\u02d2\1\uffff\3\u02d2\1\uffff\1\u02d3\1\u02d2\1\u02d3"+
            "\2\u02d2\1\uffff\3\u02d3\1\u02d2\1\uffff\1\u02d2\1\u02d3\1\u02d2"+
            "\1\uffff\3\u02d2\1\u02d3\2\u02d2\2\u02d3\2\u02d2\1\u02d3\1\u02d2"+
            "\1\uffff\1\u02d2\2\uffff\1\u02d2\1\uffff\1\u02d3\1\u02d2\22"+
            "\uffff\1\u02d0",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u02d5\3\u02d6\2\u02d7\1\u02d6\1\uffff\1\u02d6\2\u02d7\1"+
            "\u02d6\1\u02d7\1\u02d6\5\u02d7\2\u02d6\1\u02d7\1\u02d6\2\uffff"+
            "\1\u02d6\1\uffff\4\u02d6\1\uffff\6\u02d6\1\uffff\1\u02d6\1\u02d7"+
            "\1\uffff\1\u02d7\1\uffff\3\u02d7\1\u02d6\1\uffff\1\u02d6\1\u02d7"+
            "\3\u02d6\1\u02d7\2\u02d6\1\u02d7\3\u02d6\1\u02d7\3\u02d6\1\uffff"+
            "\1\u02d6\2\u02d7\1\u02d6\1\uffff\1\u02d6\1\uffff\1\u02d6\1\uffff"+
            "\1\u02d6\1\u02d7\2\u02d6\1\uffff\3\u02d7\4\u02d6\1\u02d7\1\uffff"+
            "\1\u02d7\2\u02d6\1\uffff\1\u02d7\1\uffff\1\u02d6\3\u02d7\1\uffff"+
            "\3\u02d6\1\uffff\1\u02d6\2\u02d7\2\u02d6\1\u02d7\3\u02d6\3\u02d7"+
            "\1\uffff\2\u02d7\2\u02d6\1\uffff\2\u02d6\2\u02d7\1\uffff\1\u02d7"+
            "\3\u02d6\1\u02d7\5\u02d6\2\uffff\6\u02d6\1\uffff\1\u02d6\1\u02d7"+
            "\1\u02d6\1\uffff\1\u02d6\2\u02d7\1\u02d6\1\uffff\1\u02d6\1\uffff"+
            "\3\u02d7\2\u02d6\1\uffff\2\u02d6\1\uffff\1\u02d7\2\u02d6\1\u02d7"+
            "\1\u02d6\2\uffff\2\u02d6\1\u02d7\2\u02d6\1\u02d7\2\u02d6\1\u02d7"+
            "\3\u02d6\1\uffff\7\u02d6\1\u02d7\1\u02d6\1\u02d7\3\u02d6\3\u02d7"+
            "\3\u02d6\1\uffff\4\u02d6\1\u02d7\5\u02d6\1\u02d7\10\u02d6\1"+
            "\u02d7\1\u02d6\1\uffff\3\u02d6\1\uffff\1\u02d7\1\u02d6\1\u02d7"+
            "\2\u02d6\1\uffff\3\u02d7\1\u02d6\1\uffff\1\u02d6\1\u02d7\1\u02d6"+
            "\1\uffff\3\u02d6\1\u02d7\2\u02d6\2\u02d7\2\u02d6\1\u02d7\1\u02d6"+
            "\1\uffff\1\u02d6\2\uffff\1\u02d6\1\uffff\1\u02d7\1\u02d6\22"+
            "\uffff\1\u02d4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u02d9\3\u02da\2\u02db\1\u02da\1\uffff\1\u02da\2\u02db\1"+
            "\u02da\1\u02db\1\u02da\5\u02db\2\u02da\1\u02db\1\u02da\2\uffff"+
            "\1\u02da\1\uffff\4\u02da\1\uffff\6\u02da\1\uffff\1\u02da\1\u02db"+
            "\1\uffff\1\u02db\1\uffff\3\u02db\1\u02da\1\uffff\1\u02da\1\u02db"+
            "\3\u02da\1\u02db\2\u02da\1\u02db\3\u02da\1\u02db\3\u02da\1\uffff"+
            "\1\u02da\2\u02db\1\u02da\1\uffff\1\u02da\1\uffff\1\u02da\1\uffff"+
            "\1\u02da\1\u02db\2\u02da\1\uffff\3\u02db\4\u02da\1\u02db\1\uffff"+
            "\1\u02db\2\u02da\1\uffff\1\u02db\1\uffff\1\u02da\3\u02db\1\uffff"+
            "\3\u02da\1\uffff\1\u02da\2\u02db\2\u02da\1\u02db\3\u02da\3\u02db"+
            "\1\uffff\2\u02db\2\u02da\1\uffff\2\u02da\2\u02db\1\uffff\1\u02db"+
            "\3\u02da\1\u02db\5\u02da\2\uffff\6\u02da\1\uffff\1\u02da\1\u02db"+
            "\1\u02da\1\uffff\1\u02da\2\u02db\1\u02da\1\uffff\1\u02da\1\uffff"+
            "\3\u02db\2\u02da\1\uffff\2\u02da\1\uffff\1\u02db\2\u02da\1\u02db"+
            "\1\u02da\2\uffff\2\u02da\1\u02db\2\u02da\1\u02db\2\u02da\1\u02db"+
            "\3\u02da\1\uffff\7\u02da\1\u02db\1\u02da\1\u02db\3\u02da\3\u02db"+
            "\3\u02da\1\uffff\4\u02da\1\u02db\5\u02da\1\u02db\10\u02da\1"+
            "\u02db\1\u02da\1\uffff\3\u02da\1\uffff\1\u02db\1\u02da\1\u02db"+
            "\2\u02da\1\uffff\3\u02db\1\u02da\1\uffff\1\u02da\1\u02db\1\u02da"+
            "\1\uffff\3\u02da\1\u02db\2\u02da\2\u02db\2\u02da\1\u02db\1\u02da"+
            "\1\uffff\1\u02da\2\uffff\1\u02da\1\uffff\1\u02db\1\u02da\22"+
            "\uffff\1\u02d8",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff"
    };

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
        for (int i=0; i<numStates; i++) {
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
            return "127:1: selectItem : ( ( tableAllColumns )=> tableAllColumns -> ^( TOK_SELEXPR tableAllColumns ) | ( expression ( ( ( KW_AS )? identifier ) | ( KW_AS LPAREN identifier ( COMMA identifier )* RPAREN ) )? ) -> ^( TOK_SELEXPR expression ( identifier )* ) );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA17_0 = input.LA(1);

                         
                        int index17_0 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA17_0==STAR) && (synpred1_SelectClauseParser())) {s = 1;}

                        else if ( (LA17_0==Identifier) ) {s = 2;}

                        else if ( (LA17_0==KW_STRUCT||LA17_0==KW_UNIONTYPE) ) {s = 3;}

                        else if ( (LA17_0==KW_NULL) ) {s = 4;}

                        else if ( (LA17_0==BigintLiteral||LA17_0==CharSetName||LA17_0==DecimalLiteral||(LA17_0 >= KW_CASE && LA17_0 <= KW_CAST)||LA17_0==KW_IF||LA17_0==KW_INTERVAL||LA17_0==KW_MAP||LA17_0==KW_NOT||LA17_0==LPAREN||LA17_0==MINUS||(LA17_0 >= Number && LA17_0 <= PLUS)||(LA17_0 >= SmallintLiteral && LA17_0 <= TinyintLiteral)) ) {s = 5;}

                        else if ( (LA17_0==KW_DATE) ) {s = 7;}

                        else if ( (LA17_0==KW_CURRENT_DATE) ) {s = 9;}

                        else if ( (LA17_0==KW_TIMESTAMP) ) {s = 10;}

                        else if ( (LA17_0==KW_CURRENT_TIMESTAMP) ) {s = 11;}

                        else if ( (LA17_0==KW_TRUE) ) {s = 12;}

                        else if ( (LA17_0==KW_FALSE) ) {s = 20;}

                        else if ( (LA17_0==KW_ARRAY) ) {s = 21;}

                        else if ( ((LA17_0 >= KW_ADD && LA17_0 <= KW_AFTER)||LA17_0==KW_ANALYZE||LA17_0==KW_ARCHIVE||LA17_0==KW_ASC||LA17_0==KW_BEFORE||(LA17_0 >= KW_BUCKET && LA17_0 <= KW_BUCKETS)||LA17_0==KW_CASCADE||LA17_0==KW_CHANGE||(LA17_0 >= KW_CLUSTER && LA17_0 <= KW_COLLECTION)||(LA17_0 >= KW_COLUMNS && LA17_0 <= KW_CONCATENATE)||LA17_0==KW_CONTINUE||LA17_0==KW_DATA||LA17_0==KW_DATABASES||(LA17_0 >= KW_DATETIME && LA17_0 <= KW_DBPROPERTIES)||(LA17_0 >= KW_DEFERRED && LA17_0 <= KW_DEFINED)||(LA17_0 >= KW_DELIMITED && LA17_0 <= KW_DESC)||(LA17_0 >= KW_DIRECTORIES && LA17_0 <= KW_DISABLE)||LA17_0==KW_DISTRIBUTE||LA17_0==KW_ELEM_TYPE||LA17_0==KW_ENABLE||LA17_0==KW_ESCAPED||LA17_0==KW_EXCLUSIVE||(LA17_0 >= KW_EXPLAIN && LA17_0 <= KW_EXPORT)||(LA17_0 >= KW_FIELDS && LA17_0 <= KW_FIRST)||(LA17_0 >= KW_FORMAT && LA17_0 <= KW_FORMATTED)||LA17_0==KW_FUNCTIONS||(LA17_0 >= KW_HOLD_DDLTIME && LA17_0 <= KW_IDXPROPERTIES)||LA17_0==KW_IGNORE||(LA17_0 >= KW_INDEX && LA17_0 <= KW_INDEXES)||(LA17_0 >= KW_INPATH && LA17_0 <= KW_INPUTFORMAT)||(LA17_0 >= KW_ITEMS && LA17_0 <= KW_JAR)||(LA17_0 >= KW_KEYS && LA17_0 <= KW_KEY_TYPE)||(LA17_0 >= KW_LIMIT && LA17_0 <= KW_LOAD)||(LA17_0 >= KW_LOCATION && LA17_0 <= KW_LONG)||(LA17_0 >= KW_MAPJOIN && LA17_0 <= KW_MONTH)||LA17_0==KW_MSCK||LA17_0==KW_NOSCAN||LA17_0==KW_NO_DROP||LA17_0==KW_OFFLINE||LA17_0==KW_OPTION||(LA17_0 >= KW_OUTPUTDRIVER && LA17_0 <= KW_OUTPUTFORMAT)||(LA17_0 >= KW_OVERWRITE && LA17_0 <= KW_OWNER)||(LA17_0 >= KW_PARTITIONED && LA17_0 <= KW_PARTITIONS)||LA17_0==KW_PLUS||(LA17_0 >= KW_PRETTY && LA17_0 <= KW_PRINCIPALS)||(LA17_0 >= KW_PROTECTION && LA17_0 <= KW_PURGE)||(LA17_0 >= KW_READ && LA17_0 <= KW_READONLY)||(LA17_0 >= KW_REBUILD && LA17_0 <= KW_RECORDWRITER)||(LA17_0 >= KW_REGEXP && LA17_0 <= KW_RESTRICT)||LA17_0==KW_REWRITE||(LA17_0 >= KW_RLIKE && LA17_0 <= KW_ROLES)||(LA17_0 >= KW_SCHEMA && LA17_0 <= KW_SECOND)||(LA17_0 >= KW_SEMI && LA17_0 <= KW_SERVER)||(LA17_0 >= KW_SETS && LA17_0 <= KW_SKEWED)||(LA17_0 >= KW_SORT && LA17_0 <= KW_STRING)||LA17_0==KW_TABLES||(LA17_0 >= KW_TBLPROPERTIES && LA17_0 <= KW_TERMINATED)||LA17_0==KW_TINYINT||(LA17_0 >= KW_TOUCH && LA17_0 <= KW_TRANSACTIONS)||LA17_0==KW_UNARCHIVE||LA17_0==KW_UNDO||(LA17_0 >= KW_UNLOCK && LA17_0 <= KW_UNSIGNED)||(LA17_0 >= KW_URI && LA17_0 <= KW_USE)||(LA17_0 >= KW_UTC && LA17_0 <= KW_UTCTIMESTAMP)||LA17_0==KW_VALUE_TYPE||LA17_0==KW_VIEW||LA17_0==KW_WHILE||LA17_0==KW_YEAR) ) {s = 24;}

                        else if ( ((LA17_0 >= KW_BIGINT && LA17_0 <= KW_BOOLEAN)||LA17_0==KW_DOUBLE||LA17_0==KW_FLOAT||LA17_0==KW_INT||LA17_0==KW_SMALLINT) ) {s = 25;}

                        else if ( (LA17_0==KW_EXISTS) ) {s = 27;}

                        else if ( ((LA17_0 >= KW_ALL && LA17_0 <= KW_ALTER)||LA17_0==KW_AS||LA17_0==KW_AUTHORIZATION||LA17_0==KW_BETWEEN||LA17_0==KW_BOTH||LA17_0==KW_BY||LA17_0==KW_CREATE||LA17_0==KW_CUBE||LA17_0==KW_CURSOR||LA17_0==KW_DECIMAL||LA17_0==KW_DELETE||LA17_0==KW_DESCRIBE||LA17_0==KW_DROP||LA17_0==KW_EXTERNAL||LA17_0==KW_FETCH||LA17_0==KW_FOR||LA17_0==KW_FULL||(LA17_0 >= KW_GRANT && LA17_0 <= KW_GROUPING)||(LA17_0 >= KW_IMPORT && LA17_0 <= KW_IN)||LA17_0==KW_INNER||LA17_0==KW_INSERT||LA17_0==KW_INTERSECT||(LA17_0 >= KW_INTO && LA17_0 <= KW_IS)||(LA17_0 >= KW_LATERAL && LA17_0 <= KW_LEFT)||LA17_0==KW_LIKE||LA17_0==KW_LOCAL||LA17_0==KW_NONE||LA17_0==KW_OF||(LA17_0 >= KW_ORDER && LA17_0 <= KW_OUTER)||LA17_0==KW_PARTITION||LA17_0==KW_PERCENT||LA17_0==KW_PROCEDURE||LA17_0==KW_RANGE||LA17_0==KW_READS||LA17_0==KW_REVOKE||LA17_0==KW_RIGHT||(LA17_0 >= KW_ROLLUP && LA17_0 <= KW_ROWS)||LA17_0==KW_SET||LA17_0==KW_TABLE||LA17_0==KW_TO||LA17_0==KW_TRIGGER||LA17_0==KW_TRUNCATE||LA17_0==KW_UNION||LA17_0==KW_UPDATE||(LA17_0 >= KW_USER && LA17_0 <= KW_USING)||LA17_0==KW_VALUES||LA17_0==KW_WITH) ) {s = 29;}

                         
                        input.seek(index17_0);

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA17_30 = input.LA(1);

                         
                        int index17_30 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA17_30==STAR) && (synpred1_SelectClauseParser())) {s = 676;}

                        else if ( (LA17_30==Identifier) ) {s = 677;}

                        else if ( ((LA17_30 >= KW_ADD && LA17_30 <= KW_AFTER)||LA17_30==KW_ANALYZE||LA17_30==KW_ARCHIVE||LA17_30==KW_ASC||LA17_30==KW_BEFORE||(LA17_30 >= KW_BUCKET && LA17_30 <= KW_BUCKETS)||LA17_30==KW_CASCADE||LA17_30==KW_CHANGE||(LA17_30 >= KW_CLUSTER && LA17_30 <= KW_COLLECTION)||(LA17_30 >= KW_COLUMNS && LA17_30 <= KW_CONCATENATE)||LA17_30==KW_CONTINUE||LA17_30==KW_DATA||LA17_30==KW_DATABASES||(LA17_30 >= KW_DATETIME && LA17_30 <= KW_DBPROPERTIES)||(LA17_30 >= KW_DEFERRED && LA17_30 <= KW_DEFINED)||(LA17_30 >= KW_DELIMITED && LA17_30 <= KW_DESC)||(LA17_30 >= KW_DIRECTORIES && LA17_30 <= KW_DISABLE)||LA17_30==KW_DISTRIBUTE||LA17_30==KW_ELEM_TYPE||LA17_30==KW_ENABLE||LA17_30==KW_ESCAPED||LA17_30==KW_EXCLUSIVE||(LA17_30 >= KW_EXPLAIN && LA17_30 <= KW_EXPORT)||(LA17_30 >= KW_FIELDS && LA17_30 <= KW_FIRST)||(LA17_30 >= KW_FORMAT && LA17_30 <= KW_FORMATTED)||LA17_30==KW_FUNCTIONS||(LA17_30 >= KW_HOLD_DDLTIME && LA17_30 <= KW_IDXPROPERTIES)||LA17_30==KW_IGNORE||(LA17_30 >= KW_INDEX && LA17_30 <= KW_INDEXES)||(LA17_30 >= KW_INPATH && LA17_30 <= KW_INPUTFORMAT)||(LA17_30 >= KW_ITEMS && LA17_30 <= KW_JAR)||(LA17_30 >= KW_KEYS && LA17_30 <= KW_KEY_TYPE)||(LA17_30 >= KW_LIMIT && LA17_30 <= KW_LOAD)||(LA17_30 >= KW_LOCATION && LA17_30 <= KW_LONG)||(LA17_30 >= KW_MAPJOIN && LA17_30 <= KW_MONTH)||LA17_30==KW_MSCK||LA17_30==KW_NOSCAN||LA17_30==KW_NO_DROP||LA17_30==KW_OFFLINE||LA17_30==KW_OPTION||(LA17_30 >= KW_OUTPUTDRIVER && LA17_30 <= KW_OUTPUTFORMAT)||(LA17_30 >= KW_OVERWRITE && LA17_30 <= KW_OWNER)||(LA17_30 >= KW_PARTITIONED && LA17_30 <= KW_PARTITIONS)||LA17_30==KW_PLUS||(LA17_30 >= KW_PRETTY && LA17_30 <= KW_PRINCIPALS)||(LA17_30 >= KW_PROTECTION && LA17_30 <= KW_PURGE)||(LA17_30 >= KW_READ && LA17_30 <= KW_READONLY)||(LA17_30 >= KW_REBUILD && LA17_30 <= KW_RECORDWRITER)||(LA17_30 >= KW_REGEXP && LA17_30 <= KW_RESTRICT)||LA17_30==KW_REWRITE||(LA17_30 >= KW_RLIKE && LA17_30 <= KW_ROLES)||(LA17_30 >= KW_SCHEMA && LA17_30 <= KW_SECOND)||(LA17_30 >= KW_SEMI && LA17_30 <= KW_SERVER)||(LA17_30 >= KW_SETS && LA17_30 <= KW_SKEWED)||(LA17_30 >= KW_SORT && LA17_30 <= KW_STRUCT)||LA17_30==KW_TABLES||(LA17_30 >= KW_TBLPROPERTIES && LA17_30 <= KW_TERMINATED)||LA17_30==KW_TINYINT||(LA17_30 >= KW_TOUCH && LA17_30 <= KW_TRANSACTIONS)||LA17_30==KW_UNARCHIVE||LA17_30==KW_UNDO||LA17_30==KW_UNIONTYPE||(LA17_30 >= KW_UNLOCK && LA17_30 <= KW_UNSIGNED)||(LA17_30 >= KW_URI && LA17_30 <= KW_USE)||(LA17_30 >= KW_UTC && LA17_30 <= KW_UTCTIMESTAMP)||LA17_30==KW_VALUE_TYPE||LA17_30==KW_VIEW||LA17_30==KW_WHILE||LA17_30==KW_YEAR) ) {s = 678;}

                        else if ( ((LA17_30 >= KW_ALL && LA17_30 <= KW_ALTER)||(LA17_30 >= KW_ARRAY && LA17_30 <= KW_AS)||LA17_30==KW_AUTHORIZATION||(LA17_30 >= KW_BETWEEN && LA17_30 <= KW_BOTH)||LA17_30==KW_BY||LA17_30==KW_CREATE||LA17_30==KW_CUBE||(LA17_30 >= KW_CURRENT_DATE && LA17_30 <= KW_CURSOR)||LA17_30==KW_DATE||LA17_30==KW_DECIMAL||LA17_30==KW_DELETE||LA17_30==KW_DESCRIBE||(LA17_30 >= KW_DOUBLE && LA17_30 <= KW_DROP)||LA17_30==KW_EXISTS||(LA17_30 >= KW_EXTERNAL && LA17_30 <= KW_FETCH)||LA17_30==KW_FLOAT||LA17_30==KW_FOR||LA17_30==KW_FULL||(LA17_30 >= KW_GRANT && LA17_30 <= KW_GROUPING)||(LA17_30 >= KW_IMPORT && LA17_30 <= KW_IN)||LA17_30==KW_INNER||(LA17_30 >= KW_INSERT && LA17_30 <= KW_INTERSECT)||(LA17_30 >= KW_INTO && LA17_30 <= KW_IS)||(LA17_30 >= KW_LATERAL && LA17_30 <= KW_LEFT)||LA17_30==KW_LIKE||LA17_30==KW_LOCAL||LA17_30==KW_NONE||(LA17_30 >= KW_NULL && LA17_30 <= KW_OF)||(LA17_30 >= KW_ORDER && LA17_30 <= KW_OUTER)||LA17_30==KW_PARTITION||LA17_30==KW_PERCENT||LA17_30==KW_PROCEDURE||LA17_30==KW_RANGE||LA17_30==KW_READS||LA17_30==KW_REVOKE||LA17_30==KW_RIGHT||(LA17_30 >= KW_ROLLUP && LA17_30 <= KW_ROWS)||LA17_30==KW_SET||LA17_30==KW_SMALLINT||LA17_30==KW_TABLE||LA17_30==KW_TIMESTAMP||LA17_30==KW_TO||(LA17_30 >= KW_TRIGGER && LA17_30 <= KW_TRUNCATE)||LA17_30==KW_UNION||LA17_30==KW_UPDATE||(LA17_30 >= KW_USER && LA17_30 <= KW_USING)||LA17_30==KW_VALUES||LA17_30==KW_WITH) ) {s = 679;}

                         
                        input.seek(index17_30);

                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA17_76 = input.LA(1);

                         
                        int index17_76 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA17_76==STAR) && (synpred1_SelectClauseParser())) {s = 680;}

                        else if ( (LA17_76==Identifier) ) {s = 681;}

                        else if ( ((LA17_76 >= KW_ADD && LA17_76 <= KW_AFTER)||LA17_76==KW_ANALYZE||LA17_76==KW_ARCHIVE||LA17_76==KW_ASC||LA17_76==KW_BEFORE||(LA17_76 >= KW_BUCKET && LA17_76 <= KW_BUCKETS)||LA17_76==KW_CASCADE||LA17_76==KW_CHANGE||(LA17_76 >= KW_CLUSTER && LA17_76 <= KW_COLLECTION)||(LA17_76 >= KW_COLUMNS && LA17_76 <= KW_CONCATENATE)||LA17_76==KW_CONTINUE||LA17_76==KW_DATA||LA17_76==KW_DATABASES||(LA17_76 >= KW_DATETIME && LA17_76 <= KW_DBPROPERTIES)||(LA17_76 >= KW_DEFERRED && LA17_76 <= KW_DEFINED)||(LA17_76 >= KW_DELIMITED && LA17_76 <= KW_DESC)||(LA17_76 >= KW_DIRECTORIES && LA17_76 <= KW_DISABLE)||LA17_76==KW_DISTRIBUTE||LA17_76==KW_ELEM_TYPE||LA17_76==KW_ENABLE||LA17_76==KW_ESCAPED||LA17_76==KW_EXCLUSIVE||(LA17_76 >= KW_EXPLAIN && LA17_76 <= KW_EXPORT)||(LA17_76 >= KW_FIELDS && LA17_76 <= KW_FIRST)||(LA17_76 >= KW_FORMAT && LA17_76 <= KW_FORMATTED)||LA17_76==KW_FUNCTIONS||(LA17_76 >= KW_HOLD_DDLTIME && LA17_76 <= KW_IDXPROPERTIES)||LA17_76==KW_IGNORE||(LA17_76 >= KW_INDEX && LA17_76 <= KW_INDEXES)||(LA17_76 >= KW_INPATH && LA17_76 <= KW_INPUTFORMAT)||(LA17_76 >= KW_ITEMS && LA17_76 <= KW_JAR)||(LA17_76 >= KW_KEYS && LA17_76 <= KW_KEY_TYPE)||(LA17_76 >= KW_LIMIT && LA17_76 <= KW_LOAD)||(LA17_76 >= KW_LOCATION && LA17_76 <= KW_LONG)||(LA17_76 >= KW_MAPJOIN && LA17_76 <= KW_MONTH)||LA17_76==KW_MSCK||LA17_76==KW_NOSCAN||LA17_76==KW_NO_DROP||LA17_76==KW_OFFLINE||LA17_76==KW_OPTION||(LA17_76 >= KW_OUTPUTDRIVER && LA17_76 <= KW_OUTPUTFORMAT)||(LA17_76 >= KW_OVERWRITE && LA17_76 <= KW_OWNER)||(LA17_76 >= KW_PARTITIONED && LA17_76 <= KW_PARTITIONS)||LA17_76==KW_PLUS||(LA17_76 >= KW_PRETTY && LA17_76 <= KW_PRINCIPALS)||(LA17_76 >= KW_PROTECTION && LA17_76 <= KW_PURGE)||(LA17_76 >= KW_READ && LA17_76 <= KW_READONLY)||(LA17_76 >= KW_REBUILD && LA17_76 <= KW_RECORDWRITER)||(LA17_76 >= KW_REGEXP && LA17_76 <= KW_RESTRICT)||LA17_76==KW_REWRITE||(LA17_76 >= KW_RLIKE && LA17_76 <= KW_ROLES)||(LA17_76 >= KW_SCHEMA && LA17_76 <= KW_SECOND)||(LA17_76 >= KW_SEMI && LA17_76 <= KW_SERVER)||(LA17_76 >= KW_SETS && LA17_76 <= KW_SKEWED)||(LA17_76 >= KW_SORT && LA17_76 <= KW_STRUCT)||LA17_76==KW_TABLES||(LA17_76 >= KW_TBLPROPERTIES && LA17_76 <= KW_TERMINATED)||LA17_76==KW_TINYINT||(LA17_76 >= KW_TOUCH && LA17_76 <= KW_TRANSACTIONS)||LA17_76==KW_UNARCHIVE||LA17_76==KW_UNDO||LA17_76==KW_UNIONTYPE||(LA17_76 >= KW_UNLOCK && LA17_76 <= KW_UNSIGNED)||(LA17_76 >= KW_URI && LA17_76 <= KW_USE)||(LA17_76 >= KW_UTC && LA17_76 <= KW_UTCTIMESTAMP)||LA17_76==KW_VALUE_TYPE||LA17_76==KW_VIEW||LA17_76==KW_WHILE||LA17_76==KW_YEAR) ) {s = 682;}

                        else if ( ((LA17_76 >= KW_ALL && LA17_76 <= KW_ALTER)||(LA17_76 >= KW_ARRAY && LA17_76 <= KW_AS)||LA17_76==KW_AUTHORIZATION||(LA17_76 >= KW_BETWEEN && LA17_76 <= KW_BOTH)||LA17_76==KW_BY||LA17_76==KW_CREATE||LA17_76==KW_CUBE||(LA17_76 >= KW_CURRENT_DATE && LA17_76 <= KW_CURSOR)||LA17_76==KW_DATE||LA17_76==KW_DECIMAL||LA17_76==KW_DELETE||LA17_76==KW_DESCRIBE||(LA17_76 >= KW_DOUBLE && LA17_76 <= KW_DROP)||LA17_76==KW_EXISTS||(LA17_76 >= KW_EXTERNAL && LA17_76 <= KW_FETCH)||LA17_76==KW_FLOAT||LA17_76==KW_FOR||LA17_76==KW_FULL||(LA17_76 >= KW_GRANT && LA17_76 <= KW_GROUPING)||(LA17_76 >= KW_IMPORT && LA17_76 <= KW_IN)||LA17_76==KW_INNER||(LA17_76 >= KW_INSERT && LA17_76 <= KW_INTERSECT)||(LA17_76 >= KW_INTO && LA17_76 <= KW_IS)||(LA17_76 >= KW_LATERAL && LA17_76 <= KW_LEFT)||LA17_76==KW_LIKE||LA17_76==KW_LOCAL||LA17_76==KW_NONE||(LA17_76 >= KW_NULL && LA17_76 <= KW_OF)||(LA17_76 >= KW_ORDER && LA17_76 <= KW_OUTER)||LA17_76==KW_PARTITION||LA17_76==KW_PERCENT||LA17_76==KW_PROCEDURE||LA17_76==KW_RANGE||LA17_76==KW_READS||LA17_76==KW_REVOKE||LA17_76==KW_RIGHT||(LA17_76 >= KW_ROLLUP && LA17_76 <= KW_ROWS)||LA17_76==KW_SET||LA17_76==KW_SMALLINT||LA17_76==KW_TABLE||LA17_76==KW_TIMESTAMP||LA17_76==KW_TO||(LA17_76 >= KW_TRIGGER && LA17_76 <= KW_TRUNCATE)||LA17_76==KW_UNION||LA17_76==KW_UPDATE||(LA17_76 >= KW_USER && LA17_76 <= KW_USING)||LA17_76==KW_VALUES||LA17_76==KW_WITH) ) {s = 683;}

                         
                        input.seek(index17_76);

                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA17_122 = input.LA(1);

                         
                        int index17_122 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA17_122==STAR) && (synpred1_SelectClauseParser())) {s = 684;}

                        else if ( (LA17_122==Identifier) ) {s = 685;}

                        else if ( ((LA17_122 >= KW_ADD && LA17_122 <= KW_AFTER)||LA17_122==KW_ANALYZE||LA17_122==KW_ARCHIVE||LA17_122==KW_ASC||LA17_122==KW_BEFORE||(LA17_122 >= KW_BUCKET && LA17_122 <= KW_BUCKETS)||LA17_122==KW_CASCADE||LA17_122==KW_CHANGE||(LA17_122 >= KW_CLUSTER && LA17_122 <= KW_COLLECTION)||(LA17_122 >= KW_COLUMNS && LA17_122 <= KW_CONCATENATE)||LA17_122==KW_CONTINUE||LA17_122==KW_DATA||LA17_122==KW_DATABASES||(LA17_122 >= KW_DATETIME && LA17_122 <= KW_DBPROPERTIES)||(LA17_122 >= KW_DEFERRED && LA17_122 <= KW_DEFINED)||(LA17_122 >= KW_DELIMITED && LA17_122 <= KW_DESC)||(LA17_122 >= KW_DIRECTORIES && LA17_122 <= KW_DISABLE)||LA17_122==KW_DISTRIBUTE||LA17_122==KW_ELEM_TYPE||LA17_122==KW_ENABLE||LA17_122==KW_ESCAPED||LA17_122==KW_EXCLUSIVE||(LA17_122 >= KW_EXPLAIN && LA17_122 <= KW_EXPORT)||(LA17_122 >= KW_FIELDS && LA17_122 <= KW_FIRST)||(LA17_122 >= KW_FORMAT && LA17_122 <= KW_FORMATTED)||LA17_122==KW_FUNCTIONS||(LA17_122 >= KW_HOLD_DDLTIME && LA17_122 <= KW_IDXPROPERTIES)||LA17_122==KW_IGNORE||(LA17_122 >= KW_INDEX && LA17_122 <= KW_INDEXES)||(LA17_122 >= KW_INPATH && LA17_122 <= KW_INPUTFORMAT)||(LA17_122 >= KW_ITEMS && LA17_122 <= KW_JAR)||(LA17_122 >= KW_KEYS && LA17_122 <= KW_KEY_TYPE)||(LA17_122 >= KW_LIMIT && LA17_122 <= KW_LOAD)||(LA17_122 >= KW_LOCATION && LA17_122 <= KW_LONG)||(LA17_122 >= KW_MAPJOIN && LA17_122 <= KW_MONTH)||LA17_122==KW_MSCK||LA17_122==KW_NOSCAN||LA17_122==KW_NO_DROP||LA17_122==KW_OFFLINE||LA17_122==KW_OPTION||(LA17_122 >= KW_OUTPUTDRIVER && LA17_122 <= KW_OUTPUTFORMAT)||(LA17_122 >= KW_OVERWRITE && LA17_122 <= KW_OWNER)||(LA17_122 >= KW_PARTITIONED && LA17_122 <= KW_PARTITIONS)||LA17_122==KW_PLUS||(LA17_122 >= KW_PRETTY && LA17_122 <= KW_PRINCIPALS)||(LA17_122 >= KW_PROTECTION && LA17_122 <= KW_PURGE)||(LA17_122 >= KW_READ && LA17_122 <= KW_READONLY)||(LA17_122 >= KW_REBUILD && LA17_122 <= KW_RECORDWRITER)||(LA17_122 >= KW_REGEXP && LA17_122 <= KW_RESTRICT)||LA17_122==KW_REWRITE||(LA17_122 >= KW_RLIKE && LA17_122 <= KW_ROLES)||(LA17_122 >= KW_SCHEMA && LA17_122 <= KW_SECOND)||(LA17_122 >= KW_SEMI && LA17_122 <= KW_SERVER)||(LA17_122 >= KW_SETS && LA17_122 <= KW_SKEWED)||(LA17_122 >= KW_SORT && LA17_122 <= KW_STRUCT)||LA17_122==KW_TABLES||(LA17_122 >= KW_TBLPROPERTIES && LA17_122 <= KW_TERMINATED)||LA17_122==KW_TINYINT||(LA17_122 >= KW_TOUCH && LA17_122 <= KW_TRANSACTIONS)||LA17_122==KW_UNARCHIVE||LA17_122==KW_UNDO||LA17_122==KW_UNIONTYPE||(LA17_122 >= KW_UNLOCK && LA17_122 <= KW_UNSIGNED)||(LA17_122 >= KW_URI && LA17_122 <= KW_USE)||(LA17_122 >= KW_UTC && LA17_122 <= KW_UTCTIMESTAMP)||LA17_122==KW_VALUE_TYPE||LA17_122==KW_VIEW||LA17_122==KW_WHILE||LA17_122==KW_YEAR) ) {s = 686;}

                        else if ( ((LA17_122 >= KW_ALL && LA17_122 <= KW_ALTER)||(LA17_122 >= KW_ARRAY && LA17_122 <= KW_AS)||LA17_122==KW_AUTHORIZATION||(LA17_122 >= KW_BETWEEN && LA17_122 <= KW_BOTH)||LA17_122==KW_BY||LA17_122==KW_CREATE||LA17_122==KW_CUBE||(LA17_122 >= KW_CURRENT_DATE && LA17_122 <= KW_CURSOR)||LA17_122==KW_DATE||LA17_122==KW_DECIMAL||LA17_122==KW_DELETE||LA17_122==KW_DESCRIBE||(LA17_122 >= KW_DOUBLE && LA17_122 <= KW_DROP)||LA17_122==KW_EXISTS||(LA17_122 >= KW_EXTERNAL && LA17_122 <= KW_FETCH)||LA17_122==KW_FLOAT||LA17_122==KW_FOR||LA17_122==KW_FULL||(LA17_122 >= KW_GRANT && LA17_122 <= KW_GROUPING)||(LA17_122 >= KW_IMPORT && LA17_122 <= KW_IN)||LA17_122==KW_INNER||(LA17_122 >= KW_INSERT && LA17_122 <= KW_INTERSECT)||(LA17_122 >= KW_INTO && LA17_122 <= KW_IS)||(LA17_122 >= KW_LATERAL && LA17_122 <= KW_LEFT)||LA17_122==KW_LIKE||LA17_122==KW_LOCAL||LA17_122==KW_NONE||(LA17_122 >= KW_NULL && LA17_122 <= KW_OF)||(LA17_122 >= KW_ORDER && LA17_122 <= KW_OUTER)||LA17_122==KW_PARTITION||LA17_122==KW_PERCENT||LA17_122==KW_PROCEDURE||LA17_122==KW_RANGE||LA17_122==KW_READS||LA17_122==KW_REVOKE||LA17_122==KW_RIGHT||(LA17_122 >= KW_ROLLUP && LA17_122 <= KW_ROWS)||LA17_122==KW_SET||LA17_122==KW_SMALLINT||LA17_122==KW_TABLE||LA17_122==KW_TIMESTAMP||LA17_122==KW_TO||(LA17_122 >= KW_TRIGGER && LA17_122 <= KW_TRUNCATE)||LA17_122==KW_UNION||LA17_122==KW_UPDATE||(LA17_122 >= KW_USER && LA17_122 <= KW_USING)||LA17_122==KW_VALUES||LA17_122==KW_WITH) ) {s = 687;}

                         
                        input.seek(index17_122);

                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA17_169 = input.LA(1);

                         
                        int index17_169 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA17_169==STAR) && (synpred1_SelectClauseParser())) {s = 688;}

                        else if ( (LA17_169==Identifier) ) {s = 689;}

                        else if ( ((LA17_169 >= KW_ADD && LA17_169 <= KW_AFTER)||LA17_169==KW_ANALYZE||LA17_169==KW_ARCHIVE||LA17_169==KW_ASC||LA17_169==KW_BEFORE||(LA17_169 >= KW_BUCKET && LA17_169 <= KW_BUCKETS)||LA17_169==KW_CASCADE||LA17_169==KW_CHANGE||(LA17_169 >= KW_CLUSTER && LA17_169 <= KW_COLLECTION)||(LA17_169 >= KW_COLUMNS && LA17_169 <= KW_CONCATENATE)||LA17_169==KW_CONTINUE||LA17_169==KW_DATA||LA17_169==KW_DATABASES||(LA17_169 >= KW_DATETIME && LA17_169 <= KW_DBPROPERTIES)||(LA17_169 >= KW_DEFERRED && LA17_169 <= KW_DEFINED)||(LA17_169 >= KW_DELIMITED && LA17_169 <= KW_DESC)||(LA17_169 >= KW_DIRECTORIES && LA17_169 <= KW_DISABLE)||LA17_169==KW_DISTRIBUTE||LA17_169==KW_ELEM_TYPE||LA17_169==KW_ENABLE||LA17_169==KW_ESCAPED||LA17_169==KW_EXCLUSIVE||(LA17_169 >= KW_EXPLAIN && LA17_169 <= KW_EXPORT)||(LA17_169 >= KW_FIELDS && LA17_169 <= KW_FIRST)||(LA17_169 >= KW_FORMAT && LA17_169 <= KW_FORMATTED)||LA17_169==KW_FUNCTIONS||(LA17_169 >= KW_HOLD_DDLTIME && LA17_169 <= KW_IDXPROPERTIES)||LA17_169==KW_IGNORE||(LA17_169 >= KW_INDEX && LA17_169 <= KW_INDEXES)||(LA17_169 >= KW_INPATH && LA17_169 <= KW_INPUTFORMAT)||(LA17_169 >= KW_ITEMS && LA17_169 <= KW_JAR)||(LA17_169 >= KW_KEYS && LA17_169 <= KW_KEY_TYPE)||(LA17_169 >= KW_LIMIT && LA17_169 <= KW_LOAD)||(LA17_169 >= KW_LOCATION && LA17_169 <= KW_LONG)||(LA17_169 >= KW_MAPJOIN && LA17_169 <= KW_MONTH)||LA17_169==KW_MSCK||LA17_169==KW_NOSCAN||LA17_169==KW_NO_DROP||LA17_169==KW_OFFLINE||LA17_169==KW_OPTION||(LA17_169 >= KW_OUTPUTDRIVER && LA17_169 <= KW_OUTPUTFORMAT)||(LA17_169 >= KW_OVERWRITE && LA17_169 <= KW_OWNER)||(LA17_169 >= KW_PARTITIONED && LA17_169 <= KW_PARTITIONS)||LA17_169==KW_PLUS||(LA17_169 >= KW_PRETTY && LA17_169 <= KW_PRINCIPALS)||(LA17_169 >= KW_PROTECTION && LA17_169 <= KW_PURGE)||(LA17_169 >= KW_READ && LA17_169 <= KW_READONLY)||(LA17_169 >= KW_REBUILD && LA17_169 <= KW_RECORDWRITER)||(LA17_169 >= KW_REGEXP && LA17_169 <= KW_RESTRICT)||LA17_169==KW_REWRITE||(LA17_169 >= KW_RLIKE && LA17_169 <= KW_ROLES)||(LA17_169 >= KW_SCHEMA && LA17_169 <= KW_SECOND)||(LA17_169 >= KW_SEMI && LA17_169 <= KW_SERVER)||(LA17_169 >= KW_SETS && LA17_169 <= KW_SKEWED)||(LA17_169 >= KW_SORT && LA17_169 <= KW_STRUCT)||LA17_169==KW_TABLES||(LA17_169 >= KW_TBLPROPERTIES && LA17_169 <= KW_TERMINATED)||LA17_169==KW_TINYINT||(LA17_169 >= KW_TOUCH && LA17_169 <= KW_TRANSACTIONS)||LA17_169==KW_UNARCHIVE||LA17_169==KW_UNDO||LA17_169==KW_UNIONTYPE||(LA17_169 >= KW_UNLOCK && LA17_169 <= KW_UNSIGNED)||(LA17_169 >= KW_URI && LA17_169 <= KW_USE)||(LA17_169 >= KW_UTC && LA17_169 <= KW_UTCTIMESTAMP)||LA17_169==KW_VALUE_TYPE||LA17_169==KW_VIEW||LA17_169==KW_WHILE||LA17_169==KW_YEAR) ) {s = 690;}

                        else if ( ((LA17_169 >= KW_ALL && LA17_169 <= KW_ALTER)||(LA17_169 >= KW_ARRAY && LA17_169 <= KW_AS)||LA17_169==KW_AUTHORIZATION||(LA17_169 >= KW_BETWEEN && LA17_169 <= KW_BOTH)||LA17_169==KW_BY||LA17_169==KW_CREATE||LA17_169==KW_CUBE||(LA17_169 >= KW_CURRENT_DATE && LA17_169 <= KW_CURSOR)||LA17_169==KW_DATE||LA17_169==KW_DECIMAL||LA17_169==KW_DELETE||LA17_169==KW_DESCRIBE||(LA17_169 >= KW_DOUBLE && LA17_169 <= KW_DROP)||LA17_169==KW_EXISTS||(LA17_169 >= KW_EXTERNAL && LA17_169 <= KW_FETCH)||LA17_169==KW_FLOAT||LA17_169==KW_FOR||LA17_169==KW_FULL||(LA17_169 >= KW_GRANT && LA17_169 <= KW_GROUPING)||(LA17_169 >= KW_IMPORT && LA17_169 <= KW_IN)||LA17_169==KW_INNER||(LA17_169 >= KW_INSERT && LA17_169 <= KW_INTERSECT)||(LA17_169 >= KW_INTO && LA17_169 <= KW_IS)||(LA17_169 >= KW_LATERAL && LA17_169 <= KW_LEFT)||LA17_169==KW_LIKE||LA17_169==KW_LOCAL||LA17_169==KW_NONE||(LA17_169 >= KW_NULL && LA17_169 <= KW_OF)||(LA17_169 >= KW_ORDER && LA17_169 <= KW_OUTER)||LA17_169==KW_PARTITION||LA17_169==KW_PERCENT||LA17_169==KW_PROCEDURE||LA17_169==KW_RANGE||LA17_169==KW_READS||LA17_169==KW_REVOKE||LA17_169==KW_RIGHT||(LA17_169 >= KW_ROLLUP && LA17_169 <= KW_ROWS)||LA17_169==KW_SET||LA17_169==KW_SMALLINT||LA17_169==KW_TABLE||LA17_169==KW_TIMESTAMP||LA17_169==KW_TO||(LA17_169 >= KW_TRIGGER && LA17_169 <= KW_TRUNCATE)||LA17_169==KW_UNION||LA17_169==KW_UPDATE||(LA17_169 >= KW_USER && LA17_169 <= KW_USING)||LA17_169==KW_VALUES||LA17_169==KW_WITH) ) {s = 691;}

                         
                        input.seek(index17_169);

                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA17_215 = input.LA(1);

                         
                        int index17_215 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA17_215==STAR) && (synpred1_SelectClauseParser())) {s = 692;}

                        else if ( (LA17_215==Identifier) ) {s = 693;}

                        else if ( ((LA17_215 >= KW_ADD && LA17_215 <= KW_AFTER)||LA17_215==KW_ANALYZE||LA17_215==KW_ARCHIVE||LA17_215==KW_ASC||LA17_215==KW_BEFORE||(LA17_215 >= KW_BUCKET && LA17_215 <= KW_BUCKETS)||LA17_215==KW_CASCADE||LA17_215==KW_CHANGE||(LA17_215 >= KW_CLUSTER && LA17_215 <= KW_COLLECTION)||(LA17_215 >= KW_COLUMNS && LA17_215 <= KW_CONCATENATE)||LA17_215==KW_CONTINUE||LA17_215==KW_DATA||LA17_215==KW_DATABASES||(LA17_215 >= KW_DATETIME && LA17_215 <= KW_DBPROPERTIES)||(LA17_215 >= KW_DEFERRED && LA17_215 <= KW_DEFINED)||(LA17_215 >= KW_DELIMITED && LA17_215 <= KW_DESC)||(LA17_215 >= KW_DIRECTORIES && LA17_215 <= KW_DISABLE)||LA17_215==KW_DISTRIBUTE||LA17_215==KW_ELEM_TYPE||LA17_215==KW_ENABLE||LA17_215==KW_ESCAPED||LA17_215==KW_EXCLUSIVE||(LA17_215 >= KW_EXPLAIN && LA17_215 <= KW_EXPORT)||(LA17_215 >= KW_FIELDS && LA17_215 <= KW_FIRST)||(LA17_215 >= KW_FORMAT && LA17_215 <= KW_FORMATTED)||LA17_215==KW_FUNCTIONS||(LA17_215 >= KW_HOLD_DDLTIME && LA17_215 <= KW_IDXPROPERTIES)||LA17_215==KW_IGNORE||(LA17_215 >= KW_INDEX && LA17_215 <= KW_INDEXES)||(LA17_215 >= KW_INPATH && LA17_215 <= KW_INPUTFORMAT)||(LA17_215 >= KW_ITEMS && LA17_215 <= KW_JAR)||(LA17_215 >= KW_KEYS && LA17_215 <= KW_KEY_TYPE)||(LA17_215 >= KW_LIMIT && LA17_215 <= KW_LOAD)||(LA17_215 >= KW_LOCATION && LA17_215 <= KW_LONG)||(LA17_215 >= KW_MAPJOIN && LA17_215 <= KW_MONTH)||LA17_215==KW_MSCK||LA17_215==KW_NOSCAN||LA17_215==KW_NO_DROP||LA17_215==KW_OFFLINE||LA17_215==KW_OPTION||(LA17_215 >= KW_OUTPUTDRIVER && LA17_215 <= KW_OUTPUTFORMAT)||(LA17_215 >= KW_OVERWRITE && LA17_215 <= KW_OWNER)||(LA17_215 >= KW_PARTITIONED && LA17_215 <= KW_PARTITIONS)||LA17_215==KW_PLUS||(LA17_215 >= KW_PRETTY && LA17_215 <= KW_PRINCIPALS)||(LA17_215 >= KW_PROTECTION && LA17_215 <= KW_PURGE)||(LA17_215 >= KW_READ && LA17_215 <= KW_READONLY)||(LA17_215 >= KW_REBUILD && LA17_215 <= KW_RECORDWRITER)||(LA17_215 >= KW_REGEXP && LA17_215 <= KW_RESTRICT)||LA17_215==KW_REWRITE||(LA17_215 >= KW_RLIKE && LA17_215 <= KW_ROLES)||(LA17_215 >= KW_SCHEMA && LA17_215 <= KW_SECOND)||(LA17_215 >= KW_SEMI && LA17_215 <= KW_SERVER)||(LA17_215 >= KW_SETS && LA17_215 <= KW_SKEWED)||(LA17_215 >= KW_SORT && LA17_215 <= KW_STRUCT)||LA17_215==KW_TABLES||(LA17_215 >= KW_TBLPROPERTIES && LA17_215 <= KW_TERMINATED)||LA17_215==KW_TINYINT||(LA17_215 >= KW_TOUCH && LA17_215 <= KW_TRANSACTIONS)||LA17_215==KW_UNARCHIVE||LA17_215==KW_UNDO||LA17_215==KW_UNIONTYPE||(LA17_215 >= KW_UNLOCK && LA17_215 <= KW_UNSIGNED)||(LA17_215 >= KW_URI && LA17_215 <= KW_USE)||(LA17_215 >= KW_UTC && LA17_215 <= KW_UTCTIMESTAMP)||LA17_215==KW_VALUE_TYPE||LA17_215==KW_VIEW||LA17_215==KW_WHILE||LA17_215==KW_YEAR) ) {s = 694;}

                        else if ( ((LA17_215 >= KW_ALL && LA17_215 <= KW_ALTER)||(LA17_215 >= KW_ARRAY && LA17_215 <= KW_AS)||LA17_215==KW_AUTHORIZATION||(LA17_215 >= KW_BETWEEN && LA17_215 <= KW_BOTH)||LA17_215==KW_BY||LA17_215==KW_CREATE||LA17_215==KW_CUBE||(LA17_215 >= KW_CURRENT_DATE && LA17_215 <= KW_CURSOR)||LA17_215==KW_DATE||LA17_215==KW_DECIMAL||LA17_215==KW_DELETE||LA17_215==KW_DESCRIBE||(LA17_215 >= KW_DOUBLE && LA17_215 <= KW_DROP)||LA17_215==KW_EXISTS||(LA17_215 >= KW_EXTERNAL && LA17_215 <= KW_FETCH)||LA17_215==KW_FLOAT||LA17_215==KW_FOR||LA17_215==KW_FULL||(LA17_215 >= KW_GRANT && LA17_215 <= KW_GROUPING)||(LA17_215 >= KW_IMPORT && LA17_215 <= KW_IN)||LA17_215==KW_INNER||(LA17_215 >= KW_INSERT && LA17_215 <= KW_INTERSECT)||(LA17_215 >= KW_INTO && LA17_215 <= KW_IS)||(LA17_215 >= KW_LATERAL && LA17_215 <= KW_LEFT)||LA17_215==KW_LIKE||LA17_215==KW_LOCAL||LA17_215==KW_NONE||(LA17_215 >= KW_NULL && LA17_215 <= KW_OF)||(LA17_215 >= KW_ORDER && LA17_215 <= KW_OUTER)||LA17_215==KW_PARTITION||LA17_215==KW_PERCENT||LA17_215==KW_PROCEDURE||LA17_215==KW_RANGE||LA17_215==KW_READS||LA17_215==KW_REVOKE||LA17_215==KW_RIGHT||(LA17_215 >= KW_ROLLUP && LA17_215 <= KW_ROWS)||LA17_215==KW_SET||LA17_215==KW_SMALLINT||LA17_215==KW_TABLE||LA17_215==KW_TIMESTAMP||LA17_215==KW_TO||(LA17_215 >= KW_TRIGGER && LA17_215 <= KW_TRUNCATE)||LA17_215==KW_UNION||LA17_215==KW_UPDATE||(LA17_215 >= KW_USER && LA17_215 <= KW_USING)||LA17_215==KW_VALUES||LA17_215==KW_WITH) ) {s = 695;}

                         
                        input.seek(index17_215);

                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA17_262 = input.LA(1);

                         
                        int index17_262 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA17_262==STAR) && (synpred1_SelectClauseParser())) {s = 696;}

                        else if ( (LA17_262==Identifier) ) {s = 697;}

                        else if ( ((LA17_262 >= KW_ADD && LA17_262 <= KW_AFTER)||LA17_262==KW_ANALYZE||LA17_262==KW_ARCHIVE||LA17_262==KW_ASC||LA17_262==KW_BEFORE||(LA17_262 >= KW_BUCKET && LA17_262 <= KW_BUCKETS)||LA17_262==KW_CASCADE||LA17_262==KW_CHANGE||(LA17_262 >= KW_CLUSTER && LA17_262 <= KW_COLLECTION)||(LA17_262 >= KW_COLUMNS && LA17_262 <= KW_CONCATENATE)||LA17_262==KW_CONTINUE||LA17_262==KW_DATA||LA17_262==KW_DATABASES||(LA17_262 >= KW_DATETIME && LA17_262 <= KW_DBPROPERTIES)||(LA17_262 >= KW_DEFERRED && LA17_262 <= KW_DEFINED)||(LA17_262 >= KW_DELIMITED && LA17_262 <= KW_DESC)||(LA17_262 >= KW_DIRECTORIES && LA17_262 <= KW_DISABLE)||LA17_262==KW_DISTRIBUTE||LA17_262==KW_ELEM_TYPE||LA17_262==KW_ENABLE||LA17_262==KW_ESCAPED||LA17_262==KW_EXCLUSIVE||(LA17_262 >= KW_EXPLAIN && LA17_262 <= KW_EXPORT)||(LA17_262 >= KW_FIELDS && LA17_262 <= KW_FIRST)||(LA17_262 >= KW_FORMAT && LA17_262 <= KW_FORMATTED)||LA17_262==KW_FUNCTIONS||(LA17_262 >= KW_HOLD_DDLTIME && LA17_262 <= KW_IDXPROPERTIES)||LA17_262==KW_IGNORE||(LA17_262 >= KW_INDEX && LA17_262 <= KW_INDEXES)||(LA17_262 >= KW_INPATH && LA17_262 <= KW_INPUTFORMAT)||(LA17_262 >= KW_ITEMS && LA17_262 <= KW_JAR)||(LA17_262 >= KW_KEYS && LA17_262 <= KW_KEY_TYPE)||(LA17_262 >= KW_LIMIT && LA17_262 <= KW_LOAD)||(LA17_262 >= KW_LOCATION && LA17_262 <= KW_LONG)||(LA17_262 >= KW_MAPJOIN && LA17_262 <= KW_MONTH)||LA17_262==KW_MSCK||LA17_262==KW_NOSCAN||LA17_262==KW_NO_DROP||LA17_262==KW_OFFLINE||LA17_262==KW_OPTION||(LA17_262 >= KW_OUTPUTDRIVER && LA17_262 <= KW_OUTPUTFORMAT)||(LA17_262 >= KW_OVERWRITE && LA17_262 <= KW_OWNER)||(LA17_262 >= KW_PARTITIONED && LA17_262 <= KW_PARTITIONS)||LA17_262==KW_PLUS||(LA17_262 >= KW_PRETTY && LA17_262 <= KW_PRINCIPALS)||(LA17_262 >= KW_PROTECTION && LA17_262 <= KW_PURGE)||(LA17_262 >= KW_READ && LA17_262 <= KW_READONLY)||(LA17_262 >= KW_REBUILD && LA17_262 <= KW_RECORDWRITER)||(LA17_262 >= KW_REGEXP && LA17_262 <= KW_RESTRICT)||LA17_262==KW_REWRITE||(LA17_262 >= KW_RLIKE && LA17_262 <= KW_ROLES)||(LA17_262 >= KW_SCHEMA && LA17_262 <= KW_SECOND)||(LA17_262 >= KW_SEMI && LA17_262 <= KW_SERVER)||(LA17_262 >= KW_SETS && LA17_262 <= KW_SKEWED)||(LA17_262 >= KW_SORT && LA17_262 <= KW_STRUCT)||LA17_262==KW_TABLES||(LA17_262 >= KW_TBLPROPERTIES && LA17_262 <= KW_TERMINATED)||LA17_262==KW_TINYINT||(LA17_262 >= KW_TOUCH && LA17_262 <= KW_TRANSACTIONS)||LA17_262==KW_UNARCHIVE||LA17_262==KW_UNDO||LA17_262==KW_UNIONTYPE||(LA17_262 >= KW_UNLOCK && LA17_262 <= KW_UNSIGNED)||(LA17_262 >= KW_URI && LA17_262 <= KW_USE)||(LA17_262 >= KW_UTC && LA17_262 <= KW_UTCTIMESTAMP)||LA17_262==KW_VALUE_TYPE||LA17_262==KW_VIEW||LA17_262==KW_WHILE||LA17_262==KW_YEAR) ) {s = 698;}

                        else if ( ((LA17_262 >= KW_ALL && LA17_262 <= KW_ALTER)||(LA17_262 >= KW_ARRAY && LA17_262 <= KW_AS)||LA17_262==KW_AUTHORIZATION||(LA17_262 >= KW_BETWEEN && LA17_262 <= KW_BOTH)||LA17_262==KW_BY||LA17_262==KW_CREATE||LA17_262==KW_CUBE||(LA17_262 >= KW_CURRENT_DATE && LA17_262 <= KW_CURSOR)||LA17_262==KW_DATE||LA17_262==KW_DECIMAL||LA17_262==KW_DELETE||LA17_262==KW_DESCRIBE||(LA17_262 >= KW_DOUBLE && LA17_262 <= KW_DROP)||LA17_262==KW_EXISTS||(LA17_262 >= KW_EXTERNAL && LA17_262 <= KW_FETCH)||LA17_262==KW_FLOAT||LA17_262==KW_FOR||LA17_262==KW_FULL||(LA17_262 >= KW_GRANT && LA17_262 <= KW_GROUPING)||(LA17_262 >= KW_IMPORT && LA17_262 <= KW_IN)||LA17_262==KW_INNER||(LA17_262 >= KW_INSERT && LA17_262 <= KW_INTERSECT)||(LA17_262 >= KW_INTO && LA17_262 <= KW_IS)||(LA17_262 >= KW_LATERAL && LA17_262 <= KW_LEFT)||LA17_262==KW_LIKE||LA17_262==KW_LOCAL||LA17_262==KW_NONE||(LA17_262 >= KW_NULL && LA17_262 <= KW_OF)||(LA17_262 >= KW_ORDER && LA17_262 <= KW_OUTER)||LA17_262==KW_PARTITION||LA17_262==KW_PERCENT||LA17_262==KW_PROCEDURE||LA17_262==KW_RANGE||LA17_262==KW_READS||LA17_262==KW_REVOKE||LA17_262==KW_RIGHT||(LA17_262 >= KW_ROLLUP && LA17_262 <= KW_ROWS)||LA17_262==KW_SET||LA17_262==KW_SMALLINT||LA17_262==KW_TABLE||LA17_262==KW_TIMESTAMP||LA17_262==KW_TO||(LA17_262 >= KW_TRIGGER && LA17_262 <= KW_TRUNCATE)||LA17_262==KW_UNION||LA17_262==KW_UPDATE||(LA17_262 >= KW_USER && LA17_262 <= KW_USING)||LA17_262==KW_VALUES||LA17_262==KW_WITH) ) {s = 699;}

                         
                        input.seek(index17_262);

                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA17_308 = input.LA(1);

                         
                        int index17_308 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA17_308==STAR) && (synpred1_SelectClauseParser())) {s = 700;}

                        else if ( (LA17_308==Identifier) ) {s = 701;}

                        else if ( ((LA17_308 >= KW_ADD && LA17_308 <= KW_AFTER)||LA17_308==KW_ANALYZE||LA17_308==KW_ARCHIVE||LA17_308==KW_ASC||LA17_308==KW_BEFORE||(LA17_308 >= KW_BUCKET && LA17_308 <= KW_BUCKETS)||LA17_308==KW_CASCADE||LA17_308==KW_CHANGE||(LA17_308 >= KW_CLUSTER && LA17_308 <= KW_COLLECTION)||(LA17_308 >= KW_COLUMNS && LA17_308 <= KW_CONCATENATE)||LA17_308==KW_CONTINUE||LA17_308==KW_DATA||LA17_308==KW_DATABASES||(LA17_308 >= KW_DATETIME && LA17_308 <= KW_DBPROPERTIES)||(LA17_308 >= KW_DEFERRED && LA17_308 <= KW_DEFINED)||(LA17_308 >= KW_DELIMITED && LA17_308 <= KW_DESC)||(LA17_308 >= KW_DIRECTORIES && LA17_308 <= KW_DISABLE)||LA17_308==KW_DISTRIBUTE||LA17_308==KW_ELEM_TYPE||LA17_308==KW_ENABLE||LA17_308==KW_ESCAPED||LA17_308==KW_EXCLUSIVE||(LA17_308 >= KW_EXPLAIN && LA17_308 <= KW_EXPORT)||(LA17_308 >= KW_FIELDS && LA17_308 <= KW_FIRST)||(LA17_308 >= KW_FORMAT && LA17_308 <= KW_FORMATTED)||LA17_308==KW_FUNCTIONS||(LA17_308 >= KW_HOLD_DDLTIME && LA17_308 <= KW_IDXPROPERTIES)||LA17_308==KW_IGNORE||(LA17_308 >= KW_INDEX && LA17_308 <= KW_INDEXES)||(LA17_308 >= KW_INPATH && LA17_308 <= KW_INPUTFORMAT)||(LA17_308 >= KW_ITEMS && LA17_308 <= KW_JAR)||(LA17_308 >= KW_KEYS && LA17_308 <= KW_KEY_TYPE)||(LA17_308 >= KW_LIMIT && LA17_308 <= KW_LOAD)||(LA17_308 >= KW_LOCATION && LA17_308 <= KW_LONG)||(LA17_308 >= KW_MAPJOIN && LA17_308 <= KW_MONTH)||LA17_308==KW_MSCK||LA17_308==KW_NOSCAN||LA17_308==KW_NO_DROP||LA17_308==KW_OFFLINE||LA17_308==KW_OPTION||(LA17_308 >= KW_OUTPUTDRIVER && LA17_308 <= KW_OUTPUTFORMAT)||(LA17_308 >= KW_OVERWRITE && LA17_308 <= KW_OWNER)||(LA17_308 >= KW_PARTITIONED && LA17_308 <= KW_PARTITIONS)||LA17_308==KW_PLUS||(LA17_308 >= KW_PRETTY && LA17_308 <= KW_PRINCIPALS)||(LA17_308 >= KW_PROTECTION && LA17_308 <= KW_PURGE)||(LA17_308 >= KW_READ && LA17_308 <= KW_READONLY)||(LA17_308 >= KW_REBUILD && LA17_308 <= KW_RECORDWRITER)||(LA17_308 >= KW_REGEXP && LA17_308 <= KW_RESTRICT)||LA17_308==KW_REWRITE||(LA17_308 >= KW_RLIKE && LA17_308 <= KW_ROLES)||(LA17_308 >= KW_SCHEMA && LA17_308 <= KW_SECOND)||(LA17_308 >= KW_SEMI && LA17_308 <= KW_SERVER)||(LA17_308 >= KW_SETS && LA17_308 <= KW_SKEWED)||(LA17_308 >= KW_SORT && LA17_308 <= KW_STRUCT)||LA17_308==KW_TABLES||(LA17_308 >= KW_TBLPROPERTIES && LA17_308 <= KW_TERMINATED)||LA17_308==KW_TINYINT||(LA17_308 >= KW_TOUCH && LA17_308 <= KW_TRANSACTIONS)||LA17_308==KW_UNARCHIVE||LA17_308==KW_UNDO||LA17_308==KW_UNIONTYPE||(LA17_308 >= KW_UNLOCK && LA17_308 <= KW_UNSIGNED)||(LA17_308 >= KW_URI && LA17_308 <= KW_USE)||(LA17_308 >= KW_UTC && LA17_308 <= KW_UTCTIMESTAMP)||LA17_308==KW_VALUE_TYPE||LA17_308==KW_VIEW||LA17_308==KW_WHILE||LA17_308==KW_YEAR) ) {s = 702;}

                        else if ( ((LA17_308 >= KW_ALL && LA17_308 <= KW_ALTER)||(LA17_308 >= KW_ARRAY && LA17_308 <= KW_AS)||LA17_308==KW_AUTHORIZATION||(LA17_308 >= KW_BETWEEN && LA17_308 <= KW_BOTH)||LA17_308==KW_BY||LA17_308==KW_CREATE||LA17_308==KW_CUBE||(LA17_308 >= KW_CURRENT_DATE && LA17_308 <= KW_CURSOR)||LA17_308==KW_DATE||LA17_308==KW_DECIMAL||LA17_308==KW_DELETE||LA17_308==KW_DESCRIBE||(LA17_308 >= KW_DOUBLE && LA17_308 <= KW_DROP)||LA17_308==KW_EXISTS||(LA17_308 >= KW_EXTERNAL && LA17_308 <= KW_FETCH)||LA17_308==KW_FLOAT||LA17_308==KW_FOR||LA17_308==KW_FULL||(LA17_308 >= KW_GRANT && LA17_308 <= KW_GROUPING)||(LA17_308 >= KW_IMPORT && LA17_308 <= KW_IN)||LA17_308==KW_INNER||(LA17_308 >= KW_INSERT && LA17_308 <= KW_INTERSECT)||(LA17_308 >= KW_INTO && LA17_308 <= KW_IS)||(LA17_308 >= KW_LATERAL && LA17_308 <= KW_LEFT)||LA17_308==KW_LIKE||LA17_308==KW_LOCAL||LA17_308==KW_NONE||(LA17_308 >= KW_NULL && LA17_308 <= KW_OF)||(LA17_308 >= KW_ORDER && LA17_308 <= KW_OUTER)||LA17_308==KW_PARTITION||LA17_308==KW_PERCENT||LA17_308==KW_PROCEDURE||LA17_308==KW_RANGE||LA17_308==KW_READS||LA17_308==KW_REVOKE||LA17_308==KW_RIGHT||(LA17_308 >= KW_ROLLUP && LA17_308 <= KW_ROWS)||LA17_308==KW_SET||LA17_308==KW_SMALLINT||LA17_308==KW_TABLE||LA17_308==KW_TIMESTAMP||LA17_308==KW_TO||(LA17_308 >= KW_TRIGGER && LA17_308 <= KW_TRUNCATE)||LA17_308==KW_UNION||LA17_308==KW_UPDATE||(LA17_308 >= KW_USER && LA17_308 <= KW_USING)||LA17_308==KW_VALUES||LA17_308==KW_WITH) ) {s = 703;}

                         
                        input.seek(index17_308);

                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA17_354 = input.LA(1);

                         
                        int index17_354 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA17_354==STAR) && (synpred1_SelectClauseParser())) {s = 704;}

                        else if ( (LA17_354==Identifier) ) {s = 705;}

                        else if ( ((LA17_354 >= KW_ADD && LA17_354 <= KW_AFTER)||LA17_354==KW_ANALYZE||LA17_354==KW_ARCHIVE||LA17_354==KW_ASC||LA17_354==KW_BEFORE||(LA17_354 >= KW_BUCKET && LA17_354 <= KW_BUCKETS)||LA17_354==KW_CASCADE||LA17_354==KW_CHANGE||(LA17_354 >= KW_CLUSTER && LA17_354 <= KW_COLLECTION)||(LA17_354 >= KW_COLUMNS && LA17_354 <= KW_CONCATENATE)||LA17_354==KW_CONTINUE||LA17_354==KW_DATA||LA17_354==KW_DATABASES||(LA17_354 >= KW_DATETIME && LA17_354 <= KW_DBPROPERTIES)||(LA17_354 >= KW_DEFERRED && LA17_354 <= KW_DEFINED)||(LA17_354 >= KW_DELIMITED && LA17_354 <= KW_DESC)||(LA17_354 >= KW_DIRECTORIES && LA17_354 <= KW_DISABLE)||LA17_354==KW_DISTRIBUTE||LA17_354==KW_ELEM_TYPE||LA17_354==KW_ENABLE||LA17_354==KW_ESCAPED||LA17_354==KW_EXCLUSIVE||(LA17_354 >= KW_EXPLAIN && LA17_354 <= KW_EXPORT)||(LA17_354 >= KW_FIELDS && LA17_354 <= KW_FIRST)||(LA17_354 >= KW_FORMAT && LA17_354 <= KW_FORMATTED)||LA17_354==KW_FUNCTIONS||(LA17_354 >= KW_HOLD_DDLTIME && LA17_354 <= KW_IDXPROPERTIES)||LA17_354==KW_IGNORE||(LA17_354 >= KW_INDEX && LA17_354 <= KW_INDEXES)||(LA17_354 >= KW_INPATH && LA17_354 <= KW_INPUTFORMAT)||(LA17_354 >= KW_ITEMS && LA17_354 <= KW_JAR)||(LA17_354 >= KW_KEYS && LA17_354 <= KW_KEY_TYPE)||(LA17_354 >= KW_LIMIT && LA17_354 <= KW_LOAD)||(LA17_354 >= KW_LOCATION && LA17_354 <= KW_LONG)||(LA17_354 >= KW_MAPJOIN && LA17_354 <= KW_MONTH)||LA17_354==KW_MSCK||LA17_354==KW_NOSCAN||LA17_354==KW_NO_DROP||LA17_354==KW_OFFLINE||LA17_354==KW_OPTION||(LA17_354 >= KW_OUTPUTDRIVER && LA17_354 <= KW_OUTPUTFORMAT)||(LA17_354 >= KW_OVERWRITE && LA17_354 <= KW_OWNER)||(LA17_354 >= KW_PARTITIONED && LA17_354 <= KW_PARTITIONS)||LA17_354==KW_PLUS||(LA17_354 >= KW_PRETTY && LA17_354 <= KW_PRINCIPALS)||(LA17_354 >= KW_PROTECTION && LA17_354 <= KW_PURGE)||(LA17_354 >= KW_READ && LA17_354 <= KW_READONLY)||(LA17_354 >= KW_REBUILD && LA17_354 <= KW_RECORDWRITER)||(LA17_354 >= KW_REGEXP && LA17_354 <= KW_RESTRICT)||LA17_354==KW_REWRITE||(LA17_354 >= KW_RLIKE && LA17_354 <= KW_ROLES)||(LA17_354 >= KW_SCHEMA && LA17_354 <= KW_SECOND)||(LA17_354 >= KW_SEMI && LA17_354 <= KW_SERVER)||(LA17_354 >= KW_SETS && LA17_354 <= KW_SKEWED)||(LA17_354 >= KW_SORT && LA17_354 <= KW_STRUCT)||LA17_354==KW_TABLES||(LA17_354 >= KW_TBLPROPERTIES && LA17_354 <= KW_TERMINATED)||LA17_354==KW_TINYINT||(LA17_354 >= KW_TOUCH && LA17_354 <= KW_TRANSACTIONS)||LA17_354==KW_UNARCHIVE||LA17_354==KW_UNDO||LA17_354==KW_UNIONTYPE||(LA17_354 >= KW_UNLOCK && LA17_354 <= KW_UNSIGNED)||(LA17_354 >= KW_URI && LA17_354 <= KW_USE)||(LA17_354 >= KW_UTC && LA17_354 <= KW_UTCTIMESTAMP)||LA17_354==KW_VALUE_TYPE||LA17_354==KW_VIEW||LA17_354==KW_WHILE||LA17_354==KW_YEAR) ) {s = 706;}

                        else if ( ((LA17_354 >= KW_ALL && LA17_354 <= KW_ALTER)||(LA17_354 >= KW_ARRAY && LA17_354 <= KW_AS)||LA17_354==KW_AUTHORIZATION||(LA17_354 >= KW_BETWEEN && LA17_354 <= KW_BOTH)||LA17_354==KW_BY||LA17_354==KW_CREATE||LA17_354==KW_CUBE||(LA17_354 >= KW_CURRENT_DATE && LA17_354 <= KW_CURSOR)||LA17_354==KW_DATE||LA17_354==KW_DECIMAL||LA17_354==KW_DELETE||LA17_354==KW_DESCRIBE||(LA17_354 >= KW_DOUBLE && LA17_354 <= KW_DROP)||LA17_354==KW_EXISTS||(LA17_354 >= KW_EXTERNAL && LA17_354 <= KW_FETCH)||LA17_354==KW_FLOAT||LA17_354==KW_FOR||LA17_354==KW_FULL||(LA17_354 >= KW_GRANT && LA17_354 <= KW_GROUPING)||(LA17_354 >= KW_IMPORT && LA17_354 <= KW_IN)||LA17_354==KW_INNER||(LA17_354 >= KW_INSERT && LA17_354 <= KW_INTERSECT)||(LA17_354 >= KW_INTO && LA17_354 <= KW_IS)||(LA17_354 >= KW_LATERAL && LA17_354 <= KW_LEFT)||LA17_354==KW_LIKE||LA17_354==KW_LOCAL||LA17_354==KW_NONE||(LA17_354 >= KW_NULL && LA17_354 <= KW_OF)||(LA17_354 >= KW_ORDER && LA17_354 <= KW_OUTER)||LA17_354==KW_PARTITION||LA17_354==KW_PERCENT||LA17_354==KW_PROCEDURE||LA17_354==KW_RANGE||LA17_354==KW_READS||LA17_354==KW_REVOKE||LA17_354==KW_RIGHT||(LA17_354 >= KW_ROLLUP && LA17_354 <= KW_ROWS)||LA17_354==KW_SET||LA17_354==KW_SMALLINT||LA17_354==KW_TABLE||LA17_354==KW_TIMESTAMP||LA17_354==KW_TO||(LA17_354 >= KW_TRIGGER && LA17_354 <= KW_TRUNCATE)||LA17_354==KW_UNION||LA17_354==KW_UPDATE||(LA17_354 >= KW_USER && LA17_354 <= KW_USING)||LA17_354==KW_VALUES||LA17_354==KW_WITH) ) {s = 707;}

                         
                        input.seek(index17_354);

                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA17_400 = input.LA(1);

                         
                        int index17_400 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA17_400==STAR) && (synpred1_SelectClauseParser())) {s = 708;}

                        else if ( (LA17_400==Identifier) ) {s = 709;}

                        else if ( ((LA17_400 >= KW_ADD && LA17_400 <= KW_AFTER)||LA17_400==KW_ANALYZE||LA17_400==KW_ARCHIVE||LA17_400==KW_ASC||LA17_400==KW_BEFORE||(LA17_400 >= KW_BUCKET && LA17_400 <= KW_BUCKETS)||LA17_400==KW_CASCADE||LA17_400==KW_CHANGE||(LA17_400 >= KW_CLUSTER && LA17_400 <= KW_COLLECTION)||(LA17_400 >= KW_COLUMNS && LA17_400 <= KW_CONCATENATE)||LA17_400==KW_CONTINUE||LA17_400==KW_DATA||LA17_400==KW_DATABASES||(LA17_400 >= KW_DATETIME && LA17_400 <= KW_DBPROPERTIES)||(LA17_400 >= KW_DEFERRED && LA17_400 <= KW_DEFINED)||(LA17_400 >= KW_DELIMITED && LA17_400 <= KW_DESC)||(LA17_400 >= KW_DIRECTORIES && LA17_400 <= KW_DISABLE)||LA17_400==KW_DISTRIBUTE||LA17_400==KW_ELEM_TYPE||LA17_400==KW_ENABLE||LA17_400==KW_ESCAPED||LA17_400==KW_EXCLUSIVE||(LA17_400 >= KW_EXPLAIN && LA17_400 <= KW_EXPORT)||(LA17_400 >= KW_FIELDS && LA17_400 <= KW_FIRST)||(LA17_400 >= KW_FORMAT && LA17_400 <= KW_FORMATTED)||LA17_400==KW_FUNCTIONS||(LA17_400 >= KW_HOLD_DDLTIME && LA17_400 <= KW_IDXPROPERTIES)||LA17_400==KW_IGNORE||(LA17_400 >= KW_INDEX && LA17_400 <= KW_INDEXES)||(LA17_400 >= KW_INPATH && LA17_400 <= KW_INPUTFORMAT)||(LA17_400 >= KW_ITEMS && LA17_400 <= KW_JAR)||(LA17_400 >= KW_KEYS && LA17_400 <= KW_KEY_TYPE)||(LA17_400 >= KW_LIMIT && LA17_400 <= KW_LOAD)||(LA17_400 >= KW_LOCATION && LA17_400 <= KW_LONG)||(LA17_400 >= KW_MAPJOIN && LA17_400 <= KW_MONTH)||LA17_400==KW_MSCK||LA17_400==KW_NOSCAN||LA17_400==KW_NO_DROP||LA17_400==KW_OFFLINE||LA17_400==KW_OPTION||(LA17_400 >= KW_OUTPUTDRIVER && LA17_400 <= KW_OUTPUTFORMAT)||(LA17_400 >= KW_OVERWRITE && LA17_400 <= KW_OWNER)||(LA17_400 >= KW_PARTITIONED && LA17_400 <= KW_PARTITIONS)||LA17_400==KW_PLUS||(LA17_400 >= KW_PRETTY && LA17_400 <= KW_PRINCIPALS)||(LA17_400 >= KW_PROTECTION && LA17_400 <= KW_PURGE)||(LA17_400 >= KW_READ && LA17_400 <= KW_READONLY)||(LA17_400 >= KW_REBUILD && LA17_400 <= KW_RECORDWRITER)||(LA17_400 >= KW_REGEXP && LA17_400 <= KW_RESTRICT)||LA17_400==KW_REWRITE||(LA17_400 >= KW_RLIKE && LA17_400 <= KW_ROLES)||(LA17_400 >= KW_SCHEMA && LA17_400 <= KW_SECOND)||(LA17_400 >= KW_SEMI && LA17_400 <= KW_SERVER)||(LA17_400 >= KW_SETS && LA17_400 <= KW_SKEWED)||(LA17_400 >= KW_SORT && LA17_400 <= KW_STRUCT)||LA17_400==KW_TABLES||(LA17_400 >= KW_TBLPROPERTIES && LA17_400 <= KW_TERMINATED)||LA17_400==KW_TINYINT||(LA17_400 >= KW_TOUCH && LA17_400 <= KW_TRANSACTIONS)||LA17_400==KW_UNARCHIVE||LA17_400==KW_UNDO||LA17_400==KW_UNIONTYPE||(LA17_400 >= KW_UNLOCK && LA17_400 <= KW_UNSIGNED)||(LA17_400 >= KW_URI && LA17_400 <= KW_USE)||(LA17_400 >= KW_UTC && LA17_400 <= KW_UTCTIMESTAMP)||LA17_400==KW_VALUE_TYPE||LA17_400==KW_VIEW||LA17_400==KW_WHILE||LA17_400==KW_YEAR) ) {s = 710;}

                        else if ( ((LA17_400 >= KW_ALL && LA17_400 <= KW_ALTER)||(LA17_400 >= KW_ARRAY && LA17_400 <= KW_AS)||LA17_400==KW_AUTHORIZATION||(LA17_400 >= KW_BETWEEN && LA17_400 <= KW_BOTH)||LA17_400==KW_BY||LA17_400==KW_CREATE||LA17_400==KW_CUBE||(LA17_400 >= KW_CURRENT_DATE && LA17_400 <= KW_CURSOR)||LA17_400==KW_DATE||LA17_400==KW_DECIMAL||LA17_400==KW_DELETE||LA17_400==KW_DESCRIBE||(LA17_400 >= KW_DOUBLE && LA17_400 <= KW_DROP)||LA17_400==KW_EXISTS||(LA17_400 >= KW_EXTERNAL && LA17_400 <= KW_FETCH)||LA17_400==KW_FLOAT||LA17_400==KW_FOR||LA17_400==KW_FULL||(LA17_400 >= KW_GRANT && LA17_400 <= KW_GROUPING)||(LA17_400 >= KW_IMPORT && LA17_400 <= KW_IN)||LA17_400==KW_INNER||(LA17_400 >= KW_INSERT && LA17_400 <= KW_INTERSECT)||(LA17_400 >= KW_INTO && LA17_400 <= KW_IS)||(LA17_400 >= KW_LATERAL && LA17_400 <= KW_LEFT)||LA17_400==KW_LIKE||LA17_400==KW_LOCAL||LA17_400==KW_NONE||(LA17_400 >= KW_NULL && LA17_400 <= KW_OF)||(LA17_400 >= KW_ORDER && LA17_400 <= KW_OUTER)||LA17_400==KW_PARTITION||LA17_400==KW_PERCENT||LA17_400==KW_PROCEDURE||LA17_400==KW_RANGE||LA17_400==KW_READS||LA17_400==KW_REVOKE||LA17_400==KW_RIGHT||(LA17_400 >= KW_ROLLUP && LA17_400 <= KW_ROWS)||LA17_400==KW_SET||LA17_400==KW_SMALLINT||LA17_400==KW_TABLE||LA17_400==KW_TIMESTAMP||LA17_400==KW_TO||(LA17_400 >= KW_TRIGGER && LA17_400 <= KW_TRUNCATE)||LA17_400==KW_UNION||LA17_400==KW_UPDATE||(LA17_400 >= KW_USER && LA17_400 <= KW_USING)||LA17_400==KW_VALUES||LA17_400==KW_WITH) ) {s = 711;}

                         
                        input.seek(index17_400);

                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA17_446 = input.LA(1);

                         
                        int index17_446 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA17_446==STAR) && (synpred1_SelectClauseParser())) {s = 712;}

                        else if ( (LA17_446==Identifier) ) {s = 713;}

                        else if ( ((LA17_446 >= KW_ADD && LA17_446 <= KW_AFTER)||LA17_446==KW_ANALYZE||LA17_446==KW_ARCHIVE||LA17_446==KW_ASC||LA17_446==KW_BEFORE||(LA17_446 >= KW_BUCKET && LA17_446 <= KW_BUCKETS)||LA17_446==KW_CASCADE||LA17_446==KW_CHANGE||(LA17_446 >= KW_CLUSTER && LA17_446 <= KW_COLLECTION)||(LA17_446 >= KW_COLUMNS && LA17_446 <= KW_CONCATENATE)||LA17_446==KW_CONTINUE||LA17_446==KW_DATA||LA17_446==KW_DATABASES||(LA17_446 >= KW_DATETIME && LA17_446 <= KW_DBPROPERTIES)||(LA17_446 >= KW_DEFERRED && LA17_446 <= KW_DEFINED)||(LA17_446 >= KW_DELIMITED && LA17_446 <= KW_DESC)||(LA17_446 >= KW_DIRECTORIES && LA17_446 <= KW_DISABLE)||LA17_446==KW_DISTRIBUTE||LA17_446==KW_ELEM_TYPE||LA17_446==KW_ENABLE||LA17_446==KW_ESCAPED||LA17_446==KW_EXCLUSIVE||(LA17_446 >= KW_EXPLAIN && LA17_446 <= KW_EXPORT)||(LA17_446 >= KW_FIELDS && LA17_446 <= KW_FIRST)||(LA17_446 >= KW_FORMAT && LA17_446 <= KW_FORMATTED)||LA17_446==KW_FUNCTIONS||(LA17_446 >= KW_HOLD_DDLTIME && LA17_446 <= KW_IDXPROPERTIES)||LA17_446==KW_IGNORE||(LA17_446 >= KW_INDEX && LA17_446 <= KW_INDEXES)||(LA17_446 >= KW_INPATH && LA17_446 <= KW_INPUTFORMAT)||(LA17_446 >= KW_ITEMS && LA17_446 <= KW_JAR)||(LA17_446 >= KW_KEYS && LA17_446 <= KW_KEY_TYPE)||(LA17_446 >= KW_LIMIT && LA17_446 <= KW_LOAD)||(LA17_446 >= KW_LOCATION && LA17_446 <= KW_LONG)||(LA17_446 >= KW_MAPJOIN && LA17_446 <= KW_MONTH)||LA17_446==KW_MSCK||LA17_446==KW_NOSCAN||LA17_446==KW_NO_DROP||LA17_446==KW_OFFLINE||LA17_446==KW_OPTION||(LA17_446 >= KW_OUTPUTDRIVER && LA17_446 <= KW_OUTPUTFORMAT)||(LA17_446 >= KW_OVERWRITE && LA17_446 <= KW_OWNER)||(LA17_446 >= KW_PARTITIONED && LA17_446 <= KW_PARTITIONS)||LA17_446==KW_PLUS||(LA17_446 >= KW_PRETTY && LA17_446 <= KW_PRINCIPALS)||(LA17_446 >= KW_PROTECTION && LA17_446 <= KW_PURGE)||(LA17_446 >= KW_READ && LA17_446 <= KW_READONLY)||(LA17_446 >= KW_REBUILD && LA17_446 <= KW_RECORDWRITER)||(LA17_446 >= KW_REGEXP && LA17_446 <= KW_RESTRICT)||LA17_446==KW_REWRITE||(LA17_446 >= KW_RLIKE && LA17_446 <= KW_ROLES)||(LA17_446 >= KW_SCHEMA && LA17_446 <= KW_SECOND)||(LA17_446 >= KW_SEMI && LA17_446 <= KW_SERVER)||(LA17_446 >= KW_SETS && LA17_446 <= KW_SKEWED)||(LA17_446 >= KW_SORT && LA17_446 <= KW_STRUCT)||LA17_446==KW_TABLES||(LA17_446 >= KW_TBLPROPERTIES && LA17_446 <= KW_TERMINATED)||LA17_446==KW_TINYINT||(LA17_446 >= KW_TOUCH && LA17_446 <= KW_TRANSACTIONS)||LA17_446==KW_UNARCHIVE||LA17_446==KW_UNDO||LA17_446==KW_UNIONTYPE||(LA17_446 >= KW_UNLOCK && LA17_446 <= KW_UNSIGNED)||(LA17_446 >= KW_URI && LA17_446 <= KW_USE)||(LA17_446 >= KW_UTC && LA17_446 <= KW_UTCTIMESTAMP)||LA17_446==KW_VALUE_TYPE||LA17_446==KW_VIEW||LA17_446==KW_WHILE||LA17_446==KW_YEAR) ) {s = 714;}

                        else if ( ((LA17_446 >= KW_ALL && LA17_446 <= KW_ALTER)||(LA17_446 >= KW_ARRAY && LA17_446 <= KW_AS)||LA17_446==KW_AUTHORIZATION||(LA17_446 >= KW_BETWEEN && LA17_446 <= KW_BOTH)||LA17_446==KW_BY||LA17_446==KW_CREATE||LA17_446==KW_CUBE||(LA17_446 >= KW_CURRENT_DATE && LA17_446 <= KW_CURSOR)||LA17_446==KW_DATE||LA17_446==KW_DECIMAL||LA17_446==KW_DELETE||LA17_446==KW_DESCRIBE||(LA17_446 >= KW_DOUBLE && LA17_446 <= KW_DROP)||LA17_446==KW_EXISTS||(LA17_446 >= KW_EXTERNAL && LA17_446 <= KW_FETCH)||LA17_446==KW_FLOAT||LA17_446==KW_FOR||LA17_446==KW_FULL||(LA17_446 >= KW_GRANT && LA17_446 <= KW_GROUPING)||(LA17_446 >= KW_IMPORT && LA17_446 <= KW_IN)||LA17_446==KW_INNER||(LA17_446 >= KW_INSERT && LA17_446 <= KW_INTERSECT)||(LA17_446 >= KW_INTO && LA17_446 <= KW_IS)||(LA17_446 >= KW_LATERAL && LA17_446 <= KW_LEFT)||LA17_446==KW_LIKE||LA17_446==KW_LOCAL||LA17_446==KW_NONE||(LA17_446 >= KW_NULL && LA17_446 <= KW_OF)||(LA17_446 >= KW_ORDER && LA17_446 <= KW_OUTER)||LA17_446==KW_PARTITION||LA17_446==KW_PERCENT||LA17_446==KW_PROCEDURE||LA17_446==KW_RANGE||LA17_446==KW_READS||LA17_446==KW_REVOKE||LA17_446==KW_RIGHT||(LA17_446 >= KW_ROLLUP && LA17_446 <= KW_ROWS)||LA17_446==KW_SET||LA17_446==KW_SMALLINT||LA17_446==KW_TABLE||LA17_446==KW_TIMESTAMP||LA17_446==KW_TO||(LA17_446 >= KW_TRIGGER && LA17_446 <= KW_TRUNCATE)||LA17_446==KW_UNION||LA17_446==KW_UPDATE||(LA17_446 >= KW_USER && LA17_446 <= KW_USING)||LA17_446==KW_VALUES||LA17_446==KW_WITH) ) {s = 715;}

                         
                        input.seek(index17_446);

                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA17_492 = input.LA(1);

                         
                        int index17_492 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA17_492==STAR) && (synpred1_SelectClauseParser())) {s = 716;}

                        else if ( (LA17_492==Identifier) ) {s = 717;}

                        else if ( ((LA17_492 >= KW_ADD && LA17_492 <= KW_AFTER)||LA17_492==KW_ANALYZE||LA17_492==KW_ARCHIVE||LA17_492==KW_ASC||LA17_492==KW_BEFORE||(LA17_492 >= KW_BUCKET && LA17_492 <= KW_BUCKETS)||LA17_492==KW_CASCADE||LA17_492==KW_CHANGE||(LA17_492 >= KW_CLUSTER && LA17_492 <= KW_COLLECTION)||(LA17_492 >= KW_COLUMNS && LA17_492 <= KW_CONCATENATE)||LA17_492==KW_CONTINUE||LA17_492==KW_DATA||LA17_492==KW_DATABASES||(LA17_492 >= KW_DATETIME && LA17_492 <= KW_DBPROPERTIES)||(LA17_492 >= KW_DEFERRED && LA17_492 <= KW_DEFINED)||(LA17_492 >= KW_DELIMITED && LA17_492 <= KW_DESC)||(LA17_492 >= KW_DIRECTORIES && LA17_492 <= KW_DISABLE)||LA17_492==KW_DISTRIBUTE||LA17_492==KW_ELEM_TYPE||LA17_492==KW_ENABLE||LA17_492==KW_ESCAPED||LA17_492==KW_EXCLUSIVE||(LA17_492 >= KW_EXPLAIN && LA17_492 <= KW_EXPORT)||(LA17_492 >= KW_FIELDS && LA17_492 <= KW_FIRST)||(LA17_492 >= KW_FORMAT && LA17_492 <= KW_FORMATTED)||LA17_492==KW_FUNCTIONS||(LA17_492 >= KW_HOLD_DDLTIME && LA17_492 <= KW_IDXPROPERTIES)||LA17_492==KW_IGNORE||(LA17_492 >= KW_INDEX && LA17_492 <= KW_INDEXES)||(LA17_492 >= KW_INPATH && LA17_492 <= KW_INPUTFORMAT)||(LA17_492 >= KW_ITEMS && LA17_492 <= KW_JAR)||(LA17_492 >= KW_KEYS && LA17_492 <= KW_KEY_TYPE)||(LA17_492 >= KW_LIMIT && LA17_492 <= KW_LOAD)||(LA17_492 >= KW_LOCATION && LA17_492 <= KW_LONG)||(LA17_492 >= KW_MAPJOIN && LA17_492 <= KW_MONTH)||LA17_492==KW_MSCK||LA17_492==KW_NOSCAN||LA17_492==KW_NO_DROP||LA17_492==KW_OFFLINE||LA17_492==KW_OPTION||(LA17_492 >= KW_OUTPUTDRIVER && LA17_492 <= KW_OUTPUTFORMAT)||(LA17_492 >= KW_OVERWRITE && LA17_492 <= KW_OWNER)||(LA17_492 >= KW_PARTITIONED && LA17_492 <= KW_PARTITIONS)||LA17_492==KW_PLUS||(LA17_492 >= KW_PRETTY && LA17_492 <= KW_PRINCIPALS)||(LA17_492 >= KW_PROTECTION && LA17_492 <= KW_PURGE)||(LA17_492 >= KW_READ && LA17_492 <= KW_READONLY)||(LA17_492 >= KW_REBUILD && LA17_492 <= KW_RECORDWRITER)||(LA17_492 >= KW_REGEXP && LA17_492 <= KW_RESTRICT)||LA17_492==KW_REWRITE||(LA17_492 >= KW_RLIKE && LA17_492 <= KW_ROLES)||(LA17_492 >= KW_SCHEMA && LA17_492 <= KW_SECOND)||(LA17_492 >= KW_SEMI && LA17_492 <= KW_SERVER)||(LA17_492 >= KW_SETS && LA17_492 <= KW_SKEWED)||(LA17_492 >= KW_SORT && LA17_492 <= KW_STRUCT)||LA17_492==KW_TABLES||(LA17_492 >= KW_TBLPROPERTIES && LA17_492 <= KW_TERMINATED)||LA17_492==KW_TINYINT||(LA17_492 >= KW_TOUCH && LA17_492 <= KW_TRANSACTIONS)||LA17_492==KW_UNARCHIVE||LA17_492==KW_UNDO||LA17_492==KW_UNIONTYPE||(LA17_492 >= KW_UNLOCK && LA17_492 <= KW_UNSIGNED)||(LA17_492 >= KW_URI && LA17_492 <= KW_USE)||(LA17_492 >= KW_UTC && LA17_492 <= KW_UTCTIMESTAMP)||LA17_492==KW_VALUE_TYPE||LA17_492==KW_VIEW||LA17_492==KW_WHILE||LA17_492==KW_YEAR) ) {s = 718;}

                        else if ( ((LA17_492 >= KW_ALL && LA17_492 <= KW_ALTER)||(LA17_492 >= KW_ARRAY && LA17_492 <= KW_AS)||LA17_492==KW_AUTHORIZATION||(LA17_492 >= KW_BETWEEN && LA17_492 <= KW_BOTH)||LA17_492==KW_BY||LA17_492==KW_CREATE||LA17_492==KW_CUBE||(LA17_492 >= KW_CURRENT_DATE && LA17_492 <= KW_CURSOR)||LA17_492==KW_DATE||LA17_492==KW_DECIMAL||LA17_492==KW_DELETE||LA17_492==KW_DESCRIBE||(LA17_492 >= KW_DOUBLE && LA17_492 <= KW_DROP)||LA17_492==KW_EXISTS||(LA17_492 >= KW_EXTERNAL && LA17_492 <= KW_FETCH)||LA17_492==KW_FLOAT||LA17_492==KW_FOR||LA17_492==KW_FULL||(LA17_492 >= KW_GRANT && LA17_492 <= KW_GROUPING)||(LA17_492 >= KW_IMPORT && LA17_492 <= KW_IN)||LA17_492==KW_INNER||(LA17_492 >= KW_INSERT && LA17_492 <= KW_INTERSECT)||(LA17_492 >= KW_INTO && LA17_492 <= KW_IS)||(LA17_492 >= KW_LATERAL && LA17_492 <= KW_LEFT)||LA17_492==KW_LIKE||LA17_492==KW_LOCAL||LA17_492==KW_NONE||(LA17_492 >= KW_NULL && LA17_492 <= KW_OF)||(LA17_492 >= KW_ORDER && LA17_492 <= KW_OUTER)||LA17_492==KW_PARTITION||LA17_492==KW_PERCENT||LA17_492==KW_PROCEDURE||LA17_492==KW_RANGE||LA17_492==KW_READS||LA17_492==KW_REVOKE||LA17_492==KW_RIGHT||(LA17_492 >= KW_ROLLUP && LA17_492 <= KW_ROWS)||LA17_492==KW_SET||LA17_492==KW_SMALLINT||LA17_492==KW_TABLE||LA17_492==KW_TIMESTAMP||LA17_492==KW_TO||(LA17_492 >= KW_TRIGGER && LA17_492 <= KW_TRUNCATE)||LA17_492==KW_UNION||LA17_492==KW_UPDATE||(LA17_492 >= KW_USER && LA17_492 <= KW_USING)||LA17_492==KW_VALUES||LA17_492==KW_WITH) ) {s = 719;}

                         
                        input.seek(index17_492);

                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA17_538 = input.LA(1);

                         
                        int index17_538 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA17_538==STAR) && (synpred1_SelectClauseParser())) {s = 720;}

                        else if ( (LA17_538==Identifier) ) {s = 721;}

                        else if ( ((LA17_538 >= KW_ADD && LA17_538 <= KW_AFTER)||LA17_538==KW_ANALYZE||LA17_538==KW_ARCHIVE||LA17_538==KW_ASC||LA17_538==KW_BEFORE||(LA17_538 >= KW_BUCKET && LA17_538 <= KW_BUCKETS)||LA17_538==KW_CASCADE||LA17_538==KW_CHANGE||(LA17_538 >= KW_CLUSTER && LA17_538 <= KW_COLLECTION)||(LA17_538 >= KW_COLUMNS && LA17_538 <= KW_CONCATENATE)||LA17_538==KW_CONTINUE||LA17_538==KW_DATA||LA17_538==KW_DATABASES||(LA17_538 >= KW_DATETIME && LA17_538 <= KW_DBPROPERTIES)||(LA17_538 >= KW_DEFERRED && LA17_538 <= KW_DEFINED)||(LA17_538 >= KW_DELIMITED && LA17_538 <= KW_DESC)||(LA17_538 >= KW_DIRECTORIES && LA17_538 <= KW_DISABLE)||LA17_538==KW_DISTRIBUTE||LA17_538==KW_ELEM_TYPE||LA17_538==KW_ENABLE||LA17_538==KW_ESCAPED||LA17_538==KW_EXCLUSIVE||(LA17_538 >= KW_EXPLAIN && LA17_538 <= KW_EXPORT)||(LA17_538 >= KW_FIELDS && LA17_538 <= KW_FIRST)||(LA17_538 >= KW_FORMAT && LA17_538 <= KW_FORMATTED)||LA17_538==KW_FUNCTIONS||(LA17_538 >= KW_HOLD_DDLTIME && LA17_538 <= KW_IDXPROPERTIES)||LA17_538==KW_IGNORE||(LA17_538 >= KW_INDEX && LA17_538 <= KW_INDEXES)||(LA17_538 >= KW_INPATH && LA17_538 <= KW_INPUTFORMAT)||(LA17_538 >= KW_ITEMS && LA17_538 <= KW_JAR)||(LA17_538 >= KW_KEYS && LA17_538 <= KW_KEY_TYPE)||(LA17_538 >= KW_LIMIT && LA17_538 <= KW_LOAD)||(LA17_538 >= KW_LOCATION && LA17_538 <= KW_LONG)||(LA17_538 >= KW_MAPJOIN && LA17_538 <= KW_MONTH)||LA17_538==KW_MSCK||LA17_538==KW_NOSCAN||LA17_538==KW_NO_DROP||LA17_538==KW_OFFLINE||LA17_538==KW_OPTION||(LA17_538 >= KW_OUTPUTDRIVER && LA17_538 <= KW_OUTPUTFORMAT)||(LA17_538 >= KW_OVERWRITE && LA17_538 <= KW_OWNER)||(LA17_538 >= KW_PARTITIONED && LA17_538 <= KW_PARTITIONS)||LA17_538==KW_PLUS||(LA17_538 >= KW_PRETTY && LA17_538 <= KW_PRINCIPALS)||(LA17_538 >= KW_PROTECTION && LA17_538 <= KW_PURGE)||(LA17_538 >= KW_READ && LA17_538 <= KW_READONLY)||(LA17_538 >= KW_REBUILD && LA17_538 <= KW_RECORDWRITER)||(LA17_538 >= KW_REGEXP && LA17_538 <= KW_RESTRICT)||LA17_538==KW_REWRITE||(LA17_538 >= KW_RLIKE && LA17_538 <= KW_ROLES)||(LA17_538 >= KW_SCHEMA && LA17_538 <= KW_SECOND)||(LA17_538 >= KW_SEMI && LA17_538 <= KW_SERVER)||(LA17_538 >= KW_SETS && LA17_538 <= KW_SKEWED)||(LA17_538 >= KW_SORT && LA17_538 <= KW_STRUCT)||LA17_538==KW_TABLES||(LA17_538 >= KW_TBLPROPERTIES && LA17_538 <= KW_TERMINATED)||LA17_538==KW_TINYINT||(LA17_538 >= KW_TOUCH && LA17_538 <= KW_TRANSACTIONS)||LA17_538==KW_UNARCHIVE||LA17_538==KW_UNDO||LA17_538==KW_UNIONTYPE||(LA17_538 >= KW_UNLOCK && LA17_538 <= KW_UNSIGNED)||(LA17_538 >= KW_URI && LA17_538 <= KW_USE)||(LA17_538 >= KW_UTC && LA17_538 <= KW_UTCTIMESTAMP)||LA17_538==KW_VALUE_TYPE||LA17_538==KW_VIEW||LA17_538==KW_WHILE||LA17_538==KW_YEAR) ) {s = 722;}

                        else if ( ((LA17_538 >= KW_ALL && LA17_538 <= KW_ALTER)||(LA17_538 >= KW_ARRAY && LA17_538 <= KW_AS)||LA17_538==KW_AUTHORIZATION||(LA17_538 >= KW_BETWEEN && LA17_538 <= KW_BOTH)||LA17_538==KW_BY||LA17_538==KW_CREATE||LA17_538==KW_CUBE||(LA17_538 >= KW_CURRENT_DATE && LA17_538 <= KW_CURSOR)||LA17_538==KW_DATE||LA17_538==KW_DECIMAL||LA17_538==KW_DELETE||LA17_538==KW_DESCRIBE||(LA17_538 >= KW_DOUBLE && LA17_538 <= KW_DROP)||LA17_538==KW_EXISTS||(LA17_538 >= KW_EXTERNAL && LA17_538 <= KW_FETCH)||LA17_538==KW_FLOAT||LA17_538==KW_FOR||LA17_538==KW_FULL||(LA17_538 >= KW_GRANT && LA17_538 <= KW_GROUPING)||(LA17_538 >= KW_IMPORT && LA17_538 <= KW_IN)||LA17_538==KW_INNER||(LA17_538 >= KW_INSERT && LA17_538 <= KW_INTERSECT)||(LA17_538 >= KW_INTO && LA17_538 <= KW_IS)||(LA17_538 >= KW_LATERAL && LA17_538 <= KW_LEFT)||LA17_538==KW_LIKE||LA17_538==KW_LOCAL||LA17_538==KW_NONE||(LA17_538 >= KW_NULL && LA17_538 <= KW_OF)||(LA17_538 >= KW_ORDER && LA17_538 <= KW_OUTER)||LA17_538==KW_PARTITION||LA17_538==KW_PERCENT||LA17_538==KW_PROCEDURE||LA17_538==KW_RANGE||LA17_538==KW_READS||LA17_538==KW_REVOKE||LA17_538==KW_RIGHT||(LA17_538 >= KW_ROLLUP && LA17_538 <= KW_ROWS)||LA17_538==KW_SET||LA17_538==KW_SMALLINT||LA17_538==KW_TABLE||LA17_538==KW_TIMESTAMP||LA17_538==KW_TO||(LA17_538 >= KW_TRIGGER && LA17_538 <= KW_TRUNCATE)||LA17_538==KW_UNION||LA17_538==KW_UPDATE||(LA17_538 >= KW_USER && LA17_538 <= KW_USING)||LA17_538==KW_VALUES||LA17_538==KW_WITH) ) {s = 723;}

                         
                        input.seek(index17_538);

                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA17_584 = input.LA(1);

                         
                        int index17_584 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA17_584==STAR) && (synpred1_SelectClauseParser())) {s = 724;}

                        else if ( (LA17_584==Identifier) ) {s = 725;}

                        else if ( ((LA17_584 >= KW_ADD && LA17_584 <= KW_AFTER)||LA17_584==KW_ANALYZE||LA17_584==KW_ARCHIVE||LA17_584==KW_ASC||LA17_584==KW_BEFORE||(LA17_584 >= KW_BUCKET && LA17_584 <= KW_BUCKETS)||LA17_584==KW_CASCADE||LA17_584==KW_CHANGE||(LA17_584 >= KW_CLUSTER && LA17_584 <= KW_COLLECTION)||(LA17_584 >= KW_COLUMNS && LA17_584 <= KW_CONCATENATE)||LA17_584==KW_CONTINUE||LA17_584==KW_DATA||LA17_584==KW_DATABASES||(LA17_584 >= KW_DATETIME && LA17_584 <= KW_DBPROPERTIES)||(LA17_584 >= KW_DEFERRED && LA17_584 <= KW_DEFINED)||(LA17_584 >= KW_DELIMITED && LA17_584 <= KW_DESC)||(LA17_584 >= KW_DIRECTORIES && LA17_584 <= KW_DISABLE)||LA17_584==KW_DISTRIBUTE||LA17_584==KW_ELEM_TYPE||LA17_584==KW_ENABLE||LA17_584==KW_ESCAPED||LA17_584==KW_EXCLUSIVE||(LA17_584 >= KW_EXPLAIN && LA17_584 <= KW_EXPORT)||(LA17_584 >= KW_FIELDS && LA17_584 <= KW_FIRST)||(LA17_584 >= KW_FORMAT && LA17_584 <= KW_FORMATTED)||LA17_584==KW_FUNCTIONS||(LA17_584 >= KW_HOLD_DDLTIME && LA17_584 <= KW_IDXPROPERTIES)||LA17_584==KW_IGNORE||(LA17_584 >= KW_INDEX && LA17_584 <= KW_INDEXES)||(LA17_584 >= KW_INPATH && LA17_584 <= KW_INPUTFORMAT)||(LA17_584 >= KW_ITEMS && LA17_584 <= KW_JAR)||(LA17_584 >= KW_KEYS && LA17_584 <= KW_KEY_TYPE)||(LA17_584 >= KW_LIMIT && LA17_584 <= KW_LOAD)||(LA17_584 >= KW_LOCATION && LA17_584 <= KW_LONG)||(LA17_584 >= KW_MAPJOIN && LA17_584 <= KW_MONTH)||LA17_584==KW_MSCK||LA17_584==KW_NOSCAN||LA17_584==KW_NO_DROP||LA17_584==KW_OFFLINE||LA17_584==KW_OPTION||(LA17_584 >= KW_OUTPUTDRIVER && LA17_584 <= KW_OUTPUTFORMAT)||(LA17_584 >= KW_OVERWRITE && LA17_584 <= KW_OWNER)||(LA17_584 >= KW_PARTITIONED && LA17_584 <= KW_PARTITIONS)||LA17_584==KW_PLUS||(LA17_584 >= KW_PRETTY && LA17_584 <= KW_PRINCIPALS)||(LA17_584 >= KW_PROTECTION && LA17_584 <= KW_PURGE)||(LA17_584 >= KW_READ && LA17_584 <= KW_READONLY)||(LA17_584 >= KW_REBUILD && LA17_584 <= KW_RECORDWRITER)||(LA17_584 >= KW_REGEXP && LA17_584 <= KW_RESTRICT)||LA17_584==KW_REWRITE||(LA17_584 >= KW_RLIKE && LA17_584 <= KW_ROLES)||(LA17_584 >= KW_SCHEMA && LA17_584 <= KW_SECOND)||(LA17_584 >= KW_SEMI && LA17_584 <= KW_SERVER)||(LA17_584 >= KW_SETS && LA17_584 <= KW_SKEWED)||(LA17_584 >= KW_SORT && LA17_584 <= KW_STRUCT)||LA17_584==KW_TABLES||(LA17_584 >= KW_TBLPROPERTIES && LA17_584 <= KW_TERMINATED)||LA17_584==KW_TINYINT||(LA17_584 >= KW_TOUCH && LA17_584 <= KW_TRANSACTIONS)||LA17_584==KW_UNARCHIVE||LA17_584==KW_UNDO||LA17_584==KW_UNIONTYPE||(LA17_584 >= KW_UNLOCK && LA17_584 <= KW_UNSIGNED)||(LA17_584 >= KW_URI && LA17_584 <= KW_USE)||(LA17_584 >= KW_UTC && LA17_584 <= KW_UTCTIMESTAMP)||LA17_584==KW_VALUE_TYPE||LA17_584==KW_VIEW||LA17_584==KW_WHILE||LA17_584==KW_YEAR) ) {s = 726;}

                        else if ( ((LA17_584 >= KW_ALL && LA17_584 <= KW_ALTER)||(LA17_584 >= KW_ARRAY && LA17_584 <= KW_AS)||LA17_584==KW_AUTHORIZATION||(LA17_584 >= KW_BETWEEN && LA17_584 <= KW_BOTH)||LA17_584==KW_BY||LA17_584==KW_CREATE||LA17_584==KW_CUBE||(LA17_584 >= KW_CURRENT_DATE && LA17_584 <= KW_CURSOR)||LA17_584==KW_DATE||LA17_584==KW_DECIMAL||LA17_584==KW_DELETE||LA17_584==KW_DESCRIBE||(LA17_584 >= KW_DOUBLE && LA17_584 <= KW_DROP)||LA17_584==KW_EXISTS||(LA17_584 >= KW_EXTERNAL && LA17_584 <= KW_FETCH)||LA17_584==KW_FLOAT||LA17_584==KW_FOR||LA17_584==KW_FULL||(LA17_584 >= KW_GRANT && LA17_584 <= KW_GROUPING)||(LA17_584 >= KW_IMPORT && LA17_584 <= KW_IN)||LA17_584==KW_INNER||(LA17_584 >= KW_INSERT && LA17_584 <= KW_INTERSECT)||(LA17_584 >= KW_INTO && LA17_584 <= KW_IS)||(LA17_584 >= KW_LATERAL && LA17_584 <= KW_LEFT)||LA17_584==KW_LIKE||LA17_584==KW_LOCAL||LA17_584==KW_NONE||(LA17_584 >= KW_NULL && LA17_584 <= KW_OF)||(LA17_584 >= KW_ORDER && LA17_584 <= KW_OUTER)||LA17_584==KW_PARTITION||LA17_584==KW_PERCENT||LA17_584==KW_PROCEDURE||LA17_584==KW_RANGE||LA17_584==KW_READS||LA17_584==KW_REVOKE||LA17_584==KW_RIGHT||(LA17_584 >= KW_ROLLUP && LA17_584 <= KW_ROWS)||LA17_584==KW_SET||LA17_584==KW_SMALLINT||LA17_584==KW_TABLE||LA17_584==KW_TIMESTAMP||LA17_584==KW_TO||(LA17_584 >= KW_TRIGGER && LA17_584 <= KW_TRUNCATE)||LA17_584==KW_UNION||LA17_584==KW_UPDATE||(LA17_584 >= KW_USER && LA17_584 <= KW_USING)||LA17_584==KW_VALUES||LA17_584==KW_WITH) ) {s = 727;}

                         
                        input.seek(index17_584);

                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA17_630 = input.LA(1);

                         
                        int index17_630 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA17_630==STAR) && (synpred1_SelectClauseParser())) {s = 728;}

                        else if ( (LA17_630==Identifier) ) {s = 729;}

                        else if ( ((LA17_630 >= KW_ADD && LA17_630 <= KW_AFTER)||LA17_630==KW_ANALYZE||LA17_630==KW_ARCHIVE||LA17_630==KW_ASC||LA17_630==KW_BEFORE||(LA17_630 >= KW_BUCKET && LA17_630 <= KW_BUCKETS)||LA17_630==KW_CASCADE||LA17_630==KW_CHANGE||(LA17_630 >= KW_CLUSTER && LA17_630 <= KW_COLLECTION)||(LA17_630 >= KW_COLUMNS && LA17_630 <= KW_CONCATENATE)||LA17_630==KW_CONTINUE||LA17_630==KW_DATA||LA17_630==KW_DATABASES||(LA17_630 >= KW_DATETIME && LA17_630 <= KW_DBPROPERTIES)||(LA17_630 >= KW_DEFERRED && LA17_630 <= KW_DEFINED)||(LA17_630 >= KW_DELIMITED && LA17_630 <= KW_DESC)||(LA17_630 >= KW_DIRECTORIES && LA17_630 <= KW_DISABLE)||LA17_630==KW_DISTRIBUTE||LA17_630==KW_ELEM_TYPE||LA17_630==KW_ENABLE||LA17_630==KW_ESCAPED||LA17_630==KW_EXCLUSIVE||(LA17_630 >= KW_EXPLAIN && LA17_630 <= KW_EXPORT)||(LA17_630 >= KW_FIELDS && LA17_630 <= KW_FIRST)||(LA17_630 >= KW_FORMAT && LA17_630 <= KW_FORMATTED)||LA17_630==KW_FUNCTIONS||(LA17_630 >= KW_HOLD_DDLTIME && LA17_630 <= KW_IDXPROPERTIES)||LA17_630==KW_IGNORE||(LA17_630 >= KW_INDEX && LA17_630 <= KW_INDEXES)||(LA17_630 >= KW_INPATH && LA17_630 <= KW_INPUTFORMAT)||(LA17_630 >= KW_ITEMS && LA17_630 <= KW_JAR)||(LA17_630 >= KW_KEYS && LA17_630 <= KW_KEY_TYPE)||(LA17_630 >= KW_LIMIT && LA17_630 <= KW_LOAD)||(LA17_630 >= KW_LOCATION && LA17_630 <= KW_LONG)||(LA17_630 >= KW_MAPJOIN && LA17_630 <= KW_MONTH)||LA17_630==KW_MSCK||LA17_630==KW_NOSCAN||LA17_630==KW_NO_DROP||LA17_630==KW_OFFLINE||LA17_630==KW_OPTION||(LA17_630 >= KW_OUTPUTDRIVER && LA17_630 <= KW_OUTPUTFORMAT)||(LA17_630 >= KW_OVERWRITE && LA17_630 <= KW_OWNER)||(LA17_630 >= KW_PARTITIONED && LA17_630 <= KW_PARTITIONS)||LA17_630==KW_PLUS||(LA17_630 >= KW_PRETTY && LA17_630 <= KW_PRINCIPALS)||(LA17_630 >= KW_PROTECTION && LA17_630 <= KW_PURGE)||(LA17_630 >= KW_READ && LA17_630 <= KW_READONLY)||(LA17_630 >= KW_REBUILD && LA17_630 <= KW_RECORDWRITER)||(LA17_630 >= KW_REGEXP && LA17_630 <= KW_RESTRICT)||LA17_630==KW_REWRITE||(LA17_630 >= KW_RLIKE && LA17_630 <= KW_ROLES)||(LA17_630 >= KW_SCHEMA && LA17_630 <= KW_SECOND)||(LA17_630 >= KW_SEMI && LA17_630 <= KW_SERVER)||(LA17_630 >= KW_SETS && LA17_630 <= KW_SKEWED)||(LA17_630 >= KW_SORT && LA17_630 <= KW_STRUCT)||LA17_630==KW_TABLES||(LA17_630 >= KW_TBLPROPERTIES && LA17_630 <= KW_TERMINATED)||LA17_630==KW_TINYINT||(LA17_630 >= KW_TOUCH && LA17_630 <= KW_TRANSACTIONS)||LA17_630==KW_UNARCHIVE||LA17_630==KW_UNDO||LA17_630==KW_UNIONTYPE||(LA17_630 >= KW_UNLOCK && LA17_630 <= KW_UNSIGNED)||(LA17_630 >= KW_URI && LA17_630 <= KW_USE)||(LA17_630 >= KW_UTC && LA17_630 <= KW_UTCTIMESTAMP)||LA17_630==KW_VALUE_TYPE||LA17_630==KW_VIEW||LA17_630==KW_WHILE||LA17_630==KW_YEAR) ) {s = 730;}

                        else if ( ((LA17_630 >= KW_ALL && LA17_630 <= KW_ALTER)||(LA17_630 >= KW_ARRAY && LA17_630 <= KW_AS)||LA17_630==KW_AUTHORIZATION||(LA17_630 >= KW_BETWEEN && LA17_630 <= KW_BOTH)||LA17_630==KW_BY||LA17_630==KW_CREATE||LA17_630==KW_CUBE||(LA17_630 >= KW_CURRENT_DATE && LA17_630 <= KW_CURSOR)||LA17_630==KW_DATE||LA17_630==KW_DECIMAL||LA17_630==KW_DELETE||LA17_630==KW_DESCRIBE||(LA17_630 >= KW_DOUBLE && LA17_630 <= KW_DROP)||LA17_630==KW_EXISTS||(LA17_630 >= KW_EXTERNAL && LA17_630 <= KW_FETCH)||LA17_630==KW_FLOAT||LA17_630==KW_FOR||LA17_630==KW_FULL||(LA17_630 >= KW_GRANT && LA17_630 <= KW_GROUPING)||(LA17_630 >= KW_IMPORT && LA17_630 <= KW_IN)||LA17_630==KW_INNER||(LA17_630 >= KW_INSERT && LA17_630 <= KW_INTERSECT)||(LA17_630 >= KW_INTO && LA17_630 <= KW_IS)||(LA17_630 >= KW_LATERAL && LA17_630 <= KW_LEFT)||LA17_630==KW_LIKE||LA17_630==KW_LOCAL||LA17_630==KW_NONE||(LA17_630 >= KW_NULL && LA17_630 <= KW_OF)||(LA17_630 >= KW_ORDER && LA17_630 <= KW_OUTER)||LA17_630==KW_PARTITION||LA17_630==KW_PERCENT||LA17_630==KW_PROCEDURE||LA17_630==KW_RANGE||LA17_630==KW_READS||LA17_630==KW_REVOKE||LA17_630==KW_RIGHT||(LA17_630 >= KW_ROLLUP && LA17_630 <= KW_ROWS)||LA17_630==KW_SET||LA17_630==KW_SMALLINT||LA17_630==KW_TABLE||LA17_630==KW_TIMESTAMP||LA17_630==KW_TO||(LA17_630 >= KW_TRIGGER && LA17_630 <= KW_TRUNCATE)||LA17_630==KW_UNION||LA17_630==KW_UPDATE||(LA17_630 >= KW_USER && LA17_630 <= KW_USING)||LA17_630==KW_VALUES||LA17_630==KW_WITH) ) {s = 731;}

                         
                        input.seek(index17_630);

                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA17_677 = input.LA(1);

                         
                        int index17_677 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred1_SelectClauseParser()) ) {s = 728;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index17_677);

                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA17_678 = input.LA(1);

                         
                        int index17_678 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred1_SelectClauseParser()) ) {s = 728;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index17_678);

                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA17_679 = input.LA(1);

                         
                        int index17_679 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred1_SelectClauseParser()) ) {s = 728;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index17_679);

                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA17_681 = input.LA(1);

                         
                        int index17_681 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred1_SelectClauseParser()) ) {s = 728;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index17_681);

                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA17_682 = input.LA(1);

                         
                        int index17_682 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred1_SelectClauseParser()) ) {s = 728;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index17_682);

                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA17_683 = input.LA(1);

                         
                        int index17_683 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred1_SelectClauseParser()) ) {s = 728;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index17_683);

                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA17_685 = input.LA(1);

                         
                        int index17_685 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index17_685);

                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA17_686 = input.LA(1);

                         
                        int index17_686 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index17_686);

                        if ( s>=0 ) return s;
                        break;
                    case 23 : 
                        int LA17_687 = input.LA(1);

                         
                        int index17_687 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index17_687);

                        if ( s>=0 ) return s;
                        break;
                    case 24 : 
                        int LA17_689 = input.LA(1);

                         
                        int index17_689 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index17_689);

                        if ( s>=0 ) return s;
                        break;
                    case 25 : 
                        int LA17_690 = input.LA(1);

                         
                        int index17_690 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index17_690);

                        if ( s>=0 ) return s;
                        break;
                    case 26 : 
                        int LA17_691 = input.LA(1);

                         
                        int index17_691 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index17_691);

                        if ( s>=0 ) return s;
                        break;
                    case 27 : 
                        int LA17_693 = input.LA(1);

                         
                        int index17_693 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index17_693);

                        if ( s>=0 ) return s;
                        break;
                    case 28 : 
                        int LA17_694 = input.LA(1);

                         
                        int index17_694 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index17_694);

                        if ( s>=0 ) return s;
                        break;
                    case 29 : 
                        int LA17_695 = input.LA(1);

                         
                        int index17_695 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index17_695);

                        if ( s>=0 ) return s;
                        break;
                    case 30 : 
                        int LA17_697 = input.LA(1);

                         
                        int index17_697 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index17_697);

                        if ( s>=0 ) return s;
                        break;
                    case 31 : 
                        int LA17_698 = input.LA(1);

                         
                        int index17_698 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index17_698);

                        if ( s>=0 ) return s;
                        break;
                    case 32 : 
                        int LA17_699 = input.LA(1);

                         
                        int index17_699 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index17_699);

                        if ( s>=0 ) return s;
                        break;
                    case 33 : 
                        int LA17_701 = input.LA(1);

                         
                        int index17_701 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index17_701);

                        if ( s>=0 ) return s;
                        break;
                    case 34 : 
                        int LA17_702 = input.LA(1);

                         
                        int index17_702 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index17_702);

                        if ( s>=0 ) return s;
                        break;
                    case 35 : 
                        int LA17_703 = input.LA(1);

                         
                        int index17_703 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index17_703);

                        if ( s>=0 ) return s;
                        break;
                    case 36 : 
                        int LA17_705 = input.LA(1);

                         
                        int index17_705 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index17_705);

                        if ( s>=0 ) return s;
                        break;
                    case 37 : 
                        int LA17_706 = input.LA(1);

                         
                        int index17_706 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index17_706);

                        if ( s>=0 ) return s;
                        break;
                    case 38 : 
                        int LA17_707 = input.LA(1);

                         
                        int index17_707 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index17_707);

                        if ( s>=0 ) return s;
                        break;
                    case 39 : 
                        int LA17_709 = input.LA(1);

                         
                        int index17_709 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index17_709);

                        if ( s>=0 ) return s;
                        break;
                    case 40 : 
                        int LA17_710 = input.LA(1);

                         
                        int index17_710 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index17_710);

                        if ( s>=0 ) return s;
                        break;
                    case 41 : 
                        int LA17_711 = input.LA(1);

                         
                        int index17_711 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index17_711);

                        if ( s>=0 ) return s;
                        break;
                    case 42 : 
                        int LA17_713 = input.LA(1);

                         
                        int index17_713 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index17_713);

                        if ( s>=0 ) return s;
                        break;
                    case 43 : 
                        int LA17_714 = input.LA(1);

                         
                        int index17_714 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index17_714);

                        if ( s>=0 ) return s;
                        break;
                    case 44 : 
                        int LA17_715 = input.LA(1);

                         
                        int index17_715 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index17_715);

                        if ( s>=0 ) return s;
                        break;
                    case 45 : 
                        int LA17_717 = input.LA(1);

                         
                        int index17_717 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred1_SelectClauseParser()) ) {s = 728;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index17_717);

                        if ( s>=0 ) return s;
                        break;
                    case 46 : 
                        int LA17_718 = input.LA(1);

                         
                        int index17_718 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred1_SelectClauseParser()) ) {s = 728;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index17_718);

                        if ( s>=0 ) return s;
                        break;
                    case 47 : 
                        int LA17_719 = input.LA(1);

                         
                        int index17_719 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred1_SelectClauseParser()) ) {s = 728;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index17_719);

                        if ( s>=0 ) return s;
                        break;
                    case 48 : 
                        int LA17_721 = input.LA(1);

                         
                        int index17_721 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index17_721);

                        if ( s>=0 ) return s;
                        break;
                    case 49 : 
                        int LA17_722 = input.LA(1);

                         
                        int index17_722 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index17_722);

                        if ( s>=0 ) return s;
                        break;
                    case 50 : 
                        int LA17_723 = input.LA(1);

                         
                        int index17_723 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index17_723);

                        if ( s>=0 ) return s;
                        break;
                    case 51 : 
                        int LA17_725 = input.LA(1);

                         
                        int index17_725 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index17_725);

                        if ( s>=0 ) return s;
                        break;
                    case 52 : 
                        int LA17_726 = input.LA(1);

                         
                        int index17_726 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index17_726);

                        if ( s>=0 ) return s;
                        break;
                    case 53 : 
                        int LA17_727 = input.LA(1);

                         
                        int index17_727 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index17_727);

                        if ( s>=0 ) return s;
                        break;
                    case 54 : 
                        int LA17_729 = input.LA(1);

                         
                        int index17_729 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index17_729);

                        if ( s>=0 ) return s;
                        break;
                    case 55 : 
                        int LA17_730 = input.LA(1);

                         
                        int index17_730 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index17_730);

                        if ( s>=0 ) return s;
                        break;
                    case 56 : 
                        int LA17_731 = input.LA(1);

                         
                        int index17_731 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((((useSQL11ReservedKeywordsForIdentifier())&&(useSQL11ReservedKeywordsForIdentifier()))&&synpred1_SelectClauseParser())) ) {s = 728;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index17_731);

                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}

            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 17, _s, input);
            error(nvae);
            throw nvae;
        }

    }
    static final String DFA16_eotS =
        "\u0145\uffff";
    static final String DFA16_eofS =
        "\1\5\1\2\1\uffff\2\2\4\uffff\1\2\1\uffff\4\2\2\uffff\1\2\1\uffff"+
        "\1\2\u0131\uffff";
    static final String DFA16_minS =
        "\2\12\1\uffff\2\12\4\uffff\1\12\1\uffff\4\12\2\uffff\1\12\1\uffff"+
        "\1\12\132\uffff\1\32\15\uffff\3\7\146\uffff\140\0";
    static final String DFA16_maxS =
        "\2\u0133\1\uffff\2\u0133\4\uffff\1\u0133\1\uffff\4\u0133\2\uffff"+
        "\1\u0133\1\uffff\1\u0133\132\uffff\1\u0128\15\uffff\3\u013b\146"+
        "\uffff\140\0";
    static final String DFA16_acceptS =
        "\2\uffff\1\1\2\uffff\1\3\22\uffff\1\2\u012c\uffff";
    static final String DFA16_specialS =
        "\u00e5\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13"+
        "\1\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\1\27\1\30"+
        "\1\31\1\32\1\33\1\34\1\35\1\36\1\37\1\40\1\41\1\42\1\43\1\44\1\45"+
        "\1\46\1\47\1\50\1\51\1\52\1\53\1\54\1\55\1\56\1\57\1\60\1\61\1\62"+
        "\1\63\1\64\1\65\1\66\1\67\1\70\1\71\1\72\1\73\1\74\1\75\1\76\1\77"+
        "\1\100\1\101\1\102\1\103\1\104\1\105\1\106\1\107\1\110\1\111\1\112"+
        "\1\113\1\114\1\115\1\116\1\117\1\120\1\121\1\122\1\123\1\124\1\125"+
        "\1\126\1\127\1\130\1\131\1\132\1\133\1\134\1\135\1\136\1\137}>";
    static final String[] DFA16_transitionS = {
            "\1\5\17\uffff\7\2\1\uffff\2\2\1\1\14\2\2\uffff\1\2\1\uffff\1"+
            "\3\3\2\1\uffff\6\2\1\uffff\2\2\1\uffff\1\2\1\uffff\4\2\1\uffff"+
            "\20\2\1\uffff\1\14\3\2\1\uffff\1\2\1\uffff\1\2\1\uffff\4\2\1"+
            "\uffff\10\2\1\uffff\3\2\1\5\1\2\1\uffff\2\2\1\4\1\2\1\5\3\2"+
            "\1\uffff\11\2\1\23\2\2\1\uffff\4\2\1\uffff\2\2\1\21\1\2\1\uffff"+
            "\1\2\1\16\10\2\1\uffff\1\5\6\2\1\uffff\3\2\1\uffff\4\2\1\uffff"+
            "\1\2\1\uffff\1\11\4\2\1\uffff\2\2\1\uffff\5\2\2\uffff\14\2\1"+
            "\5\23\2\1\5\13\2\1\15\11\2\1\uffff\3\2\1\uffff\5\2\1\uffff\4"+
            "\2\1\uffff\1\2\1\13\1\2\1\uffff\14\2\1\uffff\1\2\1\uffff\1\5"+
            "\1\2\1\5\2\2\16\uffff\1\5",
            "\1\2\17\uffff\7\2\1\uffff\17\2\2\uffff\1\2\1\uffff\4\2\1\uffff"+
            "\6\2\1\uffff\2\2\1\uffff\1\2\1\uffff\4\2\1\uffff\20\2\1\uffff"+
            "\4\2\1\uffff\1\2\1\uffff\1\2\1\uffff\4\2\1\uffff\10\2\1\uffff"+
            "\5\2\1\uffff\10\2\1\uffff\14\2\1\uffff\4\2\1\uffff\4\2\1\uffff"+
            "\12\2\1\uffff\7\2\1\uffff\3\2\1\uffff\4\2\1\uffff\1\2\1\uffff"+
            "\5\2\1\uffff\2\2\1\uffff\5\2\2\uffff\66\2\1\uffff\3\2\1\uffff"+
            "\5\2\1\uffff\4\2\1\uffff\3\2\1\uffff\14\2\1\uffff\1\2\1\uffff"+
            "\5\2\3\uffff\1\30\12\uffff\1\2",
            "",
            "\1\2\44\uffff\1\5\5\uffff\1\2\46\uffff\1\2\31\uffff\1\2\4\uffff"+
            "\1\2\1\uffff\1\2\15\uffff\1\2\12\uffff\1\2\3\uffff\1\2\11\uffff"+
            "\1\2\22\uffff\1\2\33\uffff\1\2\23\uffff\1\2\13\uffff\1\2\32"+
            "\uffff\1\2\21\uffff\1\2\1\uffff\1\2\20\uffff\1\2",
            "\1\2\44\uffff\1\5\5\uffff\1\2\46\uffff\1\2\31\uffff\1\2\4\uffff"+
            "\1\2\1\uffff\1\2\15\uffff\1\2\12\uffff\1\2\3\uffff\1\2\11\uffff"+
            "\1\2\22\uffff\1\2\33\uffff\1\2\23\uffff\1\2\13\uffff\1\2\32"+
            "\uffff\1\2\21\uffff\1\2\1\uffff\1\2\20\uffff\1\2",
            "",
            "",
            "",
            "",
            "\1\2\44\uffff\1\5\5\uffff\1\2\46\uffff\1\2\31\uffff\1\2\4\uffff"+
            "\1\2\1\uffff\1\2\15\uffff\1\2\12\uffff\1\2\3\uffff\1\2\11\uffff"+
            "\1\2\22\uffff\1\2\33\uffff\1\2\23\uffff\1\2\13\uffff\1\2\32"+
            "\uffff\1\2\21\uffff\1\2\1\uffff\1\2\20\uffff\1\2",
            "",
            "\1\2\23\uffff\1\5\26\uffff\1\2\45\uffff\1\5\1\2\31\uffff\1"+
            "\156\4\uffff\1\2\1\uffff\1\2\15\uffff\1\2\12\uffff\1\2\3\uffff"+
            "\1\2\11\uffff\1\175\22\uffff\1\2\33\uffff\1\176\23\uffff\1\174"+
            "\13\uffff\1\2\32\uffff\1\2\21\uffff\1\2\1\uffff\1\2\20\uffff"+
            "\1\2",
            "\1\2\44\uffff\1\5\5\uffff\1\2\46\uffff\1\2\31\uffff\1\2\4\uffff"+
            "\1\2\1\uffff\1\2\15\uffff\1\2\12\uffff\1\2\3\uffff\1\2\11\uffff"+
            "\1\2\22\uffff\1\2\33\uffff\1\2\23\uffff\1\2\13\uffff\1\2\32"+
            "\uffff\1\2\21\uffff\1\2\1\uffff\1\2\20\uffff\1\2",
            "\1\2\44\uffff\1\5\5\uffff\1\2\46\uffff\1\2\31\uffff\1\2\4\uffff"+
            "\1\2\1\uffff\1\2\15\uffff\1\2\12\uffff\1\2\3\uffff\1\2\11\uffff"+
            "\1\2\22\uffff\1\2\33\uffff\1\2\23\uffff\1\2\13\uffff\1\2\32"+
            "\uffff\1\2\21\uffff\1\2\1\uffff\1\2\20\uffff\1\2",
            "\1\2\52\uffff\1\2\46\uffff\1\2\31\uffff\1\2\4\uffff\1\2\1\uffff"+
            "\1\2\15\uffff\1\2\12\uffff\1\2\3\uffff\1\2\11\uffff\1\2\22\uffff"+
            "\1\2\33\uffff\1\2\23\uffff\1\2\13\uffff\1\2\32\uffff\1\2\21"+
            "\uffff\1\2\1\uffff\1\2\13\uffff\1\5\4\uffff\1\2",
            "",
            "",
            "\1\2\52\uffff\1\2\46\uffff\1\2\31\uffff\1\2\4\uffff\1\2\1\uffff"+
            "\1\2\15\uffff\1\2\12\uffff\1\2\3\uffff\1\2\11\uffff\1\2\22\uffff"+
            "\1\2\33\uffff\1\2\23\uffff\1\2\13\uffff\1\2\32\uffff\1\2\17"+
            "\uffff\1\5\1\uffff\1\2\1\uffff\1\2\20\uffff\1\2",
            "",
            "\1\2\52\uffff\1\2\46\uffff\1\2\31\uffff\1\2\4\uffff\1\2\1\uffff"+
            "\1\2\15\uffff\1\2\3\uffff\1\5\6\uffff\1\2\3\uffff\1\2\11\uffff"+
            "\1\2\22\uffff\1\2\5\uffff\1\5\25\uffff\1\2\23\uffff\1\2\13\uffff"+
            "\1\2\32\uffff\1\2\21\uffff\1\2\1\uffff\1\2\20\uffff\1\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u00e5\3\u00e6\2\u00e7\1\u00e6\1\uffff\1\u00e6\2\u00e7\1"+
            "\u00e6\1\u00e7\1\u00e6\5\u00e7\2\u00e6\1\u00e7\1\u00e6\2\uffff"+
            "\1\u00e6\1\uffff\4\u00e6\1\uffff\6\u00e6\1\uffff\1\u00e6\1\u00e7"+
            "\1\uffff\1\u00e7\1\uffff\3\u00e7\1\u00e6\1\uffff\1\u00e6\1\u00e7"+
            "\3\u00e6\1\u00e7\2\u00e6\1\u00e7\3\u00e6\1\u00e7\3\u00e6\1\uffff"+
            "\1\u00e6\2\u00e7\1\u00e6\1\uffff\1\u00e6\1\uffff\1\u00e6\1\uffff"+
            "\1\u00e6\1\u00e7\2\u00e6\1\uffff\3\u00e7\4\u00e6\1\u00e7\1\uffff"+
            "\1\u00e7\2\u00e6\1\uffff\1\u00e7\1\uffff\1\u00e6\3\u00e7\1\uffff"+
            "\3\u00e6\1\uffff\1\u00e6\2\u00e7\2\u00e6\1\u00e7\3\u00e6\3\u00e7"+
            "\1\uffff\2\u00e7\2\u00e6\1\uffff\2\u00e6\2\u00e7\1\uffff\1\u00e7"+
            "\3\u00e6\1\u00e7\5\u00e6\2\uffff\6\u00e6\1\uffff\1\u00e6\1\u00e7"+
            "\1\u00e6\1\uffff\1\u00e6\2\u00e7\1\u00e6\1\uffff\1\u00e6\1\uffff"+
            "\3\u00e7\2\u00e6\1\uffff\2\u00e6\1\uffff\1\u00e7\2\u00e6\1\u00e7"+
            "\1\u00e6\2\uffff\2\u00e6\1\u00e7\2\u00e6\1\u00e7\2\u00e6\1\u00e7"+
            "\3\u00e6\1\uffff\7\u00e6\1\u00e7\1\u00e6\1\u00e7\3\u00e6\3\u00e7"+
            "\3\u00e6\1\uffff\4\u00e6\1\u00e7\5\u00e6\1\u00e7\10\u00e6\1"+
            "\u00e7\1\u00e6\1\uffff\3\u00e6\1\uffff\1\u00e7\1\u00e6\1\u00e7"+
            "\2\u00e6\1\uffff\3\u00e7\1\u00e6\1\uffff\1\u00e6\1\u00e7\1\u00e6"+
            "\1\u00e9\3\u00e6\1\u00e7\2\u00e6\2\u00e7\2\u00e6\1\u00e7\1\u00e6"+
            "\1\uffff\1\u00e6\2\uffff\1\u00e6\1\uffff\1\u00e7\1\u00e6\3\uffff"+
            "\1\u00e8",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u00fb\5\uffff\1\u00ff\1\uffff\1\u00ea\2\uffff\1\u00fe\7"+
            "\uffff\1\u00ee\3\u0104\1\u00eb\1\u0109\1\u0104\1\uffff\1\u0104"+
            "\1\u0101\1\u0109\1\u0104\1\u0109\1\u0104\1\u0109\3\u0105\1\u0109"+
            "\2\u0104\1\u0109\1\u0104\1\u0103\1\u0102\1\u0104\1\uffff\4\u0104"+
            "\1\uffff\6\u0104\1\uffff\1\u0104\1\u0109\1\uffff\1\u0109\1\uffff"+
            "\1\u00f5\1\u00f7\1\u0109\1\u0104\1\uffff\1\u0104\1\u00f3\3\u0104"+
            "\1\u0109\2\u0104\1\u0109\3\u0104\1\u0109\3\u0104\1\u00ec\1\u0104"+
            "\1\u0105\1\u0109\1\u0104\1\uffff\1\u0104\1\uffff\1\u0104\1\uffff"+
            "\1\u0104\1\u0107\2\u0104\1\uffff\1\u0109\1\u0100\1\u0109\4\u0104"+
            "\1\u0105\1\uffff\1\u0109\2\u0104\1\uffff\1\u0109\1\uffff\1\u0104"+
            "\3\u0109\1\uffff\3\u0104\1\u0106\1\u0104\2\u0109\2\u0104\1\u0109"+
            "\3\u0104\1\u0109\1\u0105\1\u0109\1\u00f9\2\u0109\2\u0104\1\uffff"+
            "\2\u0104\2\u0109\1\uffff\1\u0109\3\u0104\1\u0109\5\u0104\1\uffff"+
            "\1\u0106\6\u0104\1\uffff\1\u0104\1\u0109\1\u0104\1\u00f1\1\u0104"+
            "\1\u00f0\1\u0109\1\u0104\1\uffff\1\u0104\1\uffff\3\u0109\2\u0104"+
            "\1\uffff\2\u0104\1\uffff\1\u0109\2\u0104\1\u0109\1\u0104\2\uffff"+
            "\2\u0104\1\u0109\2\u0104\1\u0109\2\u0104\1\u0109\3\u0104\1\uffff"+
            "\7\u0104\1\u0109\1\u0104\1\u0109\3\u0104\3\u0109\3\u0104\1\uffff"+
            "\4\u0104\1\u0109\5\u0104\1\u0105\7\u0104\1\u00ef\1\u0109\1\u0104"+
            "\1\uffff\3\u0104\1\uffff\1\u00f6\1\u0104\1\u0109\2\u0104\1\u010a"+
            "\1\u0109\1\u00f8\1\u0109\1\u0104\1\uffff\1\u0104\1\u0109\1\u00ef"+
            "\1\uffff\3\u0104\1\u0109\2\u0104\2\u0109\2\u0104\1\u0109\1\u0104"+
            "\1\uffff\1\u0104\2\uffff\1\u0104\1\uffff\1\u0109\1\u0104\3\uffff"+
            "\1\u0108\2\uffff\1\u00f2\2\uffff\1\u00f4\1\u00f2\7\uffff\1\u00ed"+
            "\1\u00fc\1\u00fa\1\u00f2\1\u00fd",
            "\1\u0119\5\uffff\1\u011d\4\uffff\1\u011c\7\uffff\1\u010c\3"+
            "\u0122\2\u0127\1\u0122\1\uffff\1\u0122\1\u011f\1\u0127\1\u0122"+
            "\1\u0127\1\u0122\1\u0127\3\u0123\1\u0127\2\u0122\1\u0127\1\u0122"+
            "\1\u0121\1\u0120\1\u0122\1\uffff\4\u0122\1\uffff\6\u0122\1\uffff"+
            "\1\u0122\1\u0127\1\uffff\1\u0127\1\uffff\1\u0113\1\u0115\1\u0127"+
            "\1\u0122\1\uffff\1\u0122\1\u0111\3\u0122\1\u0127\2\u0122\1\u0127"+
            "\3\u0122\1\u0127\3\u0122\1\uffff\1\u0122\1\u0123\1\u0127\1\u0122"+
            "\1\uffff\1\u0122\1\uffff\1\u0122\1\uffff\1\u0122\1\u0125\2\u0122"+
            "\1\uffff\1\u0127\1\u011e\1\u0127\4\u0122\1\u0123\1\uffff\1\u0127"+
            "\2\u0122\1\uffff\1\u0127\1\uffff\1\u0122\3\u0127\1\uffff\3\u0122"+
            "\1\u0124\1\u0122\2\u0127\2\u0122\1\u0127\3\u0122\1\u0127\1\u0123"+
            "\1\u0127\1\u0117\2\u0127\2\u0122\1\uffff\2\u0122\2\u0127\1\uffff"+
            "\1\u0127\3\u0122\1\u0127\5\u0122\1\uffff\1\u0124\6\u0122\1\uffff"+
            "\1\u0122\1\u0127\1\u0122\1\u010f\1\u0122\1\u010e\1\u0127\1\u0122"+
            "\1\uffff\1\u0122\1\uffff\3\u0127\2\u0122\1\uffff\2\u0122\1\uffff"+
            "\1\u0127\2\u0122\1\u0127\1\u0122\2\uffff\2\u0122\1\u0127\2\u0122"+
            "\1\u0127\2\u0122\1\u0127\3\u0122\1\uffff\7\u0122\1\u0127\1\u0122"+
            "\1\u0127\3\u0122\3\u0127\3\u0122\1\uffff\4\u0122\1\u0127\5\u0122"+
            "\1\u0123\7\u0122\1\u010d\1\u0127\1\u0122\1\uffff\3\u0122\1\uffff"+
            "\1\u0114\1\u0122\1\u0127\2\u0122\1\uffff\1\u0127\1\u0116\1\u0127"+
            "\1\u0122\1\uffff\1\u0122\1\u0127\1\u010d\1\uffff\3\u0122\1\u0127"+
            "\2\u0122\2\u0127\2\u0122\1\u0127\1\u0122\1\uffff\1\u0122\2\uffff"+
            "\1\u0122\1\uffff\1\u0127\1\u0122\3\uffff\1\u0126\2\uffff\1\u0110"+
            "\2\uffff\1\u0112\1\u0110\7\uffff\1\u010b\1\u011a\1\u0118\1\u0110"+
            "\1\u011b",
            "\1\u0136\5\uffff\1\u013a\4\uffff\1\u0139\7\uffff\1\u0129\3"+
            "\u013f\2\u0144\1\u013f\1\uffff\1\u013f\1\u013c\1\u0144\1\u013f"+
            "\1\u0144\1\u013f\1\u0144\3\u0140\1\u0144\2\u013f\1\u0144\1\u013f"+
            "\1\u013e\1\u013d\1\u013f\1\uffff\4\u013f\1\uffff\6\u013f\1\uffff"+
            "\1\u013f\1\u0144\1\uffff\1\u0144\1\uffff\1\u0130\1\u0132\1\u0144"+
            "\1\u013f\1\uffff\1\u013f\1\u012e\3\u013f\1\u0144\2\u013f\1\u0144"+
            "\3\u013f\1\u0144\3\u013f\1\uffff\1\u013f\1\u0140\1\u0144\1\u013f"+
            "\1\uffff\1\u013f\1\uffff\1\u013f\1\uffff\1\u013f\1\u0142\2\u013f"+
            "\1\uffff\1\u0144\1\u013b\1\u0144\4\u013f\1\u0140\1\uffff\1\u0144"+
            "\2\u013f\1\uffff\1\u0144\1\uffff\1\u013f\3\u0144\1\uffff\3\u013f"+
            "\1\u0141\1\u013f\2\u0144\2\u013f\1\u0144\3\u013f\1\u0144\1\u0140"+
            "\1\u0144\1\u0134\2\u0144\2\u013f\1\uffff\2\u013f\2\u0144\1\uffff"+
            "\1\u0144\3\u013f\1\u0144\5\u013f\1\uffff\1\u0141\6\u013f\1\uffff"+
            "\1\u013f\1\u0144\1\u013f\1\u012c\1\u013f\1\u012b\1\u0144\1\u013f"+
            "\1\uffff\1\u013f\1\uffff\3\u0144\2\u013f\1\uffff\2\u013f\1\uffff"+
            "\1\u0144\2\u013f\1\u0144\1\u013f\2\uffff\2\u013f\1\u0144\2\u013f"+
            "\1\u0144\2\u013f\1\u0144\3\u013f\1\uffff\7\u013f\1\u0144\1\u013f"+
            "\1\u0144\3\u013f\3\u0144\3\u013f\1\uffff\4\u013f\1\u0144\5\u013f"+
            "\1\u0140\7\u013f\1\u012a\1\u0144\1\u013f\1\uffff\3\u013f\1\uffff"+
            "\1\u0131\1\u013f\1\u0144\2\u013f\1\uffff\1\u0144\1\u0133\1\u0144"+
            "\1\u013f\1\uffff\1\u013f\1\u0144\1\u012a\1\uffff\3\u013f\1\u0144"+
            "\2\u013f\2\u0144\2\u013f\1\u0144\1\u013f\1\uffff\1\u013f\2\uffff"+
            "\1\u013f\1\uffff\1\u0144\1\u013f\3\uffff\1\u0143\2\uffff\1\u012d"+
            "\2\uffff\1\u012f\1\u012d\7\uffff\1\u0128\1\u0137\1\u0135\1\u012d"+
            "\1\u0138",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            return "134:7: ( ( ( KW_AS )? identifier ) | ( KW_AS LPAREN identifier ( COMMA identifier )* RPAREN ) )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA16_229 = input.LA(1);

                         
                        int index16_229 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_229);

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA16_230 = input.LA(1);

                         
                        int index16_230 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_230);

                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA16_231 = input.LA(1);

                         
                        int index16_231 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_231);

                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA16_232 = input.LA(1);

                         
                        int index16_232 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_232);

                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA16_233 = input.LA(1);

                         
                        int index16_233 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_233);

                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA16_234 = input.LA(1);

                         
                        int index16_234 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_234);

                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA16_235 = input.LA(1);

                         
                        int index16_235 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_235);

                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA16_236 = input.LA(1);

                         
                        int index16_236 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_236);

                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA16_237 = input.LA(1);

                         
                        int index16_237 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_237);

                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA16_238 = input.LA(1);

                         
                        int index16_238 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_238);

                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA16_239 = input.LA(1);

                         
                        int index16_239 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_239);

                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA16_240 = input.LA(1);

                         
                        int index16_240 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_240);

                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA16_241 = input.LA(1);

                         
                        int index16_241 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_241);

                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA16_242 = input.LA(1);

                         
                        int index16_242 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_242);

                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA16_243 = input.LA(1);

                         
                        int index16_243 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_243);

                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA16_244 = input.LA(1);

                         
                        int index16_244 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_244);

                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA16_245 = input.LA(1);

                         
                        int index16_245 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_245);

                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA16_246 = input.LA(1);

                         
                        int index16_246 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_246);

                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA16_247 = input.LA(1);

                         
                        int index16_247 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_247);

                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA16_248 = input.LA(1);

                         
                        int index16_248 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_248);

                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA16_249 = input.LA(1);

                         
                        int index16_249 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_249);

                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA16_250 = input.LA(1);

                         
                        int index16_250 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_250);

                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA16_251 = input.LA(1);

                         
                        int index16_251 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_251);

                        if ( s>=0 ) return s;
                        break;
                    case 23 : 
                        int LA16_252 = input.LA(1);

                         
                        int index16_252 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_252);

                        if ( s>=0 ) return s;
                        break;
                    case 24 : 
                        int LA16_253 = input.LA(1);

                         
                        int index16_253 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_253);

                        if ( s>=0 ) return s;
                        break;
                    case 25 : 
                        int LA16_254 = input.LA(1);

                         
                        int index16_254 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_254);

                        if ( s>=0 ) return s;
                        break;
                    case 26 : 
                        int LA16_255 = input.LA(1);

                         
                        int index16_255 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_255);

                        if ( s>=0 ) return s;
                        break;
                    case 27 : 
                        int LA16_256 = input.LA(1);

                         
                        int index16_256 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_256);

                        if ( s>=0 ) return s;
                        break;
                    case 28 : 
                        int LA16_257 = input.LA(1);

                         
                        int index16_257 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_257);

                        if ( s>=0 ) return s;
                        break;
                    case 29 : 
                        int LA16_258 = input.LA(1);

                         
                        int index16_258 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_258);

                        if ( s>=0 ) return s;
                        break;
                    case 30 : 
                        int LA16_259 = input.LA(1);

                         
                        int index16_259 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_259);

                        if ( s>=0 ) return s;
                        break;
                    case 31 : 
                        int LA16_260 = input.LA(1);

                         
                        int index16_260 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_260);

                        if ( s>=0 ) return s;
                        break;
                    case 32 : 
                        int LA16_261 = input.LA(1);

                         
                        int index16_261 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_261);

                        if ( s>=0 ) return s;
                        break;
                    case 33 : 
                        int LA16_262 = input.LA(1);

                         
                        int index16_262 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_262);

                        if ( s>=0 ) return s;
                        break;
                    case 34 : 
                        int LA16_263 = input.LA(1);

                         
                        int index16_263 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_263);

                        if ( s>=0 ) return s;
                        break;
                    case 35 : 
                        int LA16_264 = input.LA(1);

                         
                        int index16_264 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_264);

                        if ( s>=0 ) return s;
                        break;
                    case 36 : 
                        int LA16_265 = input.LA(1);

                         
                        int index16_265 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_265);

                        if ( s>=0 ) return s;
                        break;
                    case 37 : 
                        int LA16_266 = input.LA(1);

                         
                        int index16_266 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_266);

                        if ( s>=0 ) return s;
                        break;
                    case 38 : 
                        int LA16_267 = input.LA(1);

                         
                        int index16_267 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_267);

                        if ( s>=0 ) return s;
                        break;
                    case 39 : 
                        int LA16_268 = input.LA(1);

                         
                        int index16_268 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_268);

                        if ( s>=0 ) return s;
                        break;
                    case 40 : 
                        int LA16_269 = input.LA(1);

                         
                        int index16_269 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_269);

                        if ( s>=0 ) return s;
                        break;
                    case 41 : 
                        int LA16_270 = input.LA(1);

                         
                        int index16_270 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_270);

                        if ( s>=0 ) return s;
                        break;
                    case 42 : 
                        int LA16_271 = input.LA(1);

                         
                        int index16_271 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_271);

                        if ( s>=0 ) return s;
                        break;
                    case 43 : 
                        int LA16_272 = input.LA(1);

                         
                        int index16_272 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_272);

                        if ( s>=0 ) return s;
                        break;
                    case 44 : 
                        int LA16_273 = input.LA(1);

                         
                        int index16_273 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_273);

                        if ( s>=0 ) return s;
                        break;
                    case 45 : 
                        int LA16_274 = input.LA(1);

                         
                        int index16_274 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_274);

                        if ( s>=0 ) return s;
                        break;
                    case 46 : 
                        int LA16_275 = input.LA(1);

                         
                        int index16_275 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_275);

                        if ( s>=0 ) return s;
                        break;
                    case 47 : 
                        int LA16_276 = input.LA(1);

                         
                        int index16_276 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_276);

                        if ( s>=0 ) return s;
                        break;
                    case 48 : 
                        int LA16_277 = input.LA(1);

                         
                        int index16_277 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_277);

                        if ( s>=0 ) return s;
                        break;
                    case 49 : 
                        int LA16_278 = input.LA(1);

                         
                        int index16_278 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_278);

                        if ( s>=0 ) return s;
                        break;
                    case 50 : 
                        int LA16_279 = input.LA(1);

                         
                        int index16_279 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_279);

                        if ( s>=0 ) return s;
                        break;
                    case 51 : 
                        int LA16_280 = input.LA(1);

                         
                        int index16_280 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_280);

                        if ( s>=0 ) return s;
                        break;
                    case 52 : 
                        int LA16_281 = input.LA(1);

                         
                        int index16_281 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_281);

                        if ( s>=0 ) return s;
                        break;
                    case 53 : 
                        int LA16_282 = input.LA(1);

                         
                        int index16_282 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_282);

                        if ( s>=0 ) return s;
                        break;
                    case 54 : 
                        int LA16_283 = input.LA(1);

                         
                        int index16_283 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_283);

                        if ( s>=0 ) return s;
                        break;
                    case 55 : 
                        int LA16_284 = input.LA(1);

                         
                        int index16_284 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_284);

                        if ( s>=0 ) return s;
                        break;
                    case 56 : 
                        int LA16_285 = input.LA(1);

                         
                        int index16_285 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_285);

                        if ( s>=0 ) return s;
                        break;
                    case 57 : 
                        int LA16_286 = input.LA(1);

                         
                        int index16_286 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_286);

                        if ( s>=0 ) return s;
                        break;
                    case 58 : 
                        int LA16_287 = input.LA(1);

                         
                        int index16_287 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_287);

                        if ( s>=0 ) return s;
                        break;
                    case 59 : 
                        int LA16_288 = input.LA(1);

                         
                        int index16_288 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_288);

                        if ( s>=0 ) return s;
                        break;
                    case 60 : 
                        int LA16_289 = input.LA(1);

                         
                        int index16_289 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_289);

                        if ( s>=0 ) return s;
                        break;
                    case 61 : 
                        int LA16_290 = input.LA(1);

                         
                        int index16_290 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_290);

                        if ( s>=0 ) return s;
                        break;
                    case 62 : 
                        int LA16_291 = input.LA(1);

                         
                        int index16_291 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_291);

                        if ( s>=0 ) return s;
                        break;
                    case 63 : 
                        int LA16_292 = input.LA(1);

                         
                        int index16_292 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_292);

                        if ( s>=0 ) return s;
                        break;
                    case 64 : 
                        int LA16_293 = input.LA(1);

                         
                        int index16_293 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_293);

                        if ( s>=0 ) return s;
                        break;
                    case 65 : 
                        int LA16_294 = input.LA(1);

                         
                        int index16_294 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_294);

                        if ( s>=0 ) return s;
                        break;
                    case 66 : 
                        int LA16_295 = input.LA(1);

                         
                        int index16_295 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_295);

                        if ( s>=0 ) return s;
                        break;
                    case 67 : 
                        int LA16_296 = input.LA(1);

                         
                        int index16_296 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_296);

                        if ( s>=0 ) return s;
                        break;
                    case 68 : 
                        int LA16_297 = input.LA(1);

                         
                        int index16_297 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_297);

                        if ( s>=0 ) return s;
                        break;
                    case 69 : 
                        int LA16_298 = input.LA(1);

                         
                        int index16_298 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_298);

                        if ( s>=0 ) return s;
                        break;
                    case 70 : 
                        int LA16_299 = input.LA(1);

                         
                        int index16_299 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_299);

                        if ( s>=0 ) return s;
                        break;
                    case 71 : 
                        int LA16_300 = input.LA(1);

                         
                        int index16_300 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_300);

                        if ( s>=0 ) return s;
                        break;
                    case 72 : 
                        int LA16_301 = input.LA(1);

                         
                        int index16_301 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_301);

                        if ( s>=0 ) return s;
                        break;
                    case 73 : 
                        int LA16_302 = input.LA(1);

                         
                        int index16_302 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_302);

                        if ( s>=0 ) return s;
                        break;
                    case 74 : 
                        int LA16_303 = input.LA(1);

                         
                        int index16_303 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_303);

                        if ( s>=0 ) return s;
                        break;
                    case 75 : 
                        int LA16_304 = input.LA(1);

                         
                        int index16_304 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_304);

                        if ( s>=0 ) return s;
                        break;
                    case 76 : 
                        int LA16_305 = input.LA(1);

                         
                        int index16_305 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_305);

                        if ( s>=0 ) return s;
                        break;
                    case 77 : 
                        int LA16_306 = input.LA(1);

                         
                        int index16_306 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_306);

                        if ( s>=0 ) return s;
                        break;
                    case 78 : 
                        int LA16_307 = input.LA(1);

                         
                        int index16_307 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_307);

                        if ( s>=0 ) return s;
                        break;
                    case 79 : 
                        int LA16_308 = input.LA(1);

                         
                        int index16_308 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_308);

                        if ( s>=0 ) return s;
                        break;
                    case 80 : 
                        int LA16_309 = input.LA(1);

                         
                        int index16_309 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_309);

                        if ( s>=0 ) return s;
                        break;
                    case 81 : 
                        int LA16_310 = input.LA(1);

                         
                        int index16_310 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_310);

                        if ( s>=0 ) return s;
                        break;
                    case 82 : 
                        int LA16_311 = input.LA(1);

                         
                        int index16_311 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_311);

                        if ( s>=0 ) return s;
                        break;
                    case 83 : 
                        int LA16_312 = input.LA(1);

                         
                        int index16_312 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_312);

                        if ( s>=0 ) return s;
                        break;
                    case 84 : 
                        int LA16_313 = input.LA(1);

                         
                        int index16_313 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_313);

                        if ( s>=0 ) return s;
                        break;
                    case 85 : 
                        int LA16_314 = input.LA(1);

                         
                        int index16_314 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_314);

                        if ( s>=0 ) return s;
                        break;
                    case 86 : 
                        int LA16_315 = input.LA(1);

                         
                        int index16_315 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_315);

                        if ( s>=0 ) return s;
                        break;
                    case 87 : 
                        int LA16_316 = input.LA(1);

                         
                        int index16_316 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_316);

                        if ( s>=0 ) return s;
                        break;
                    case 88 : 
                        int LA16_317 = input.LA(1);

                         
                        int index16_317 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_317);

                        if ( s>=0 ) return s;
                        break;
                    case 89 : 
                        int LA16_318 = input.LA(1);

                         
                        int index16_318 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_318);

                        if ( s>=0 ) return s;
                        break;
                    case 90 : 
                        int LA16_319 = input.LA(1);

                         
                        int index16_319 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_319);

                        if ( s>=0 ) return s;
                        break;
                    case 91 : 
                        int LA16_320 = input.LA(1);

                         
                        int index16_320 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_320);

                        if ( s>=0 ) return s;
                        break;
                    case 92 : 
                        int LA16_321 = input.LA(1);

                         
                        int index16_321 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_321);

                        if ( s>=0 ) return s;
                        break;
                    case 93 : 
                        int LA16_322 = input.LA(1);

                         
                        int index16_322 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_322);

                        if ( s>=0 ) return s;
                        break;
                    case 94 : 
                        int LA16_323 = input.LA(1);

                         
                        int index16_323 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_323);

                        if ( s>=0 ) return s;
                        break;
                    case 95 : 
                        int LA16_324 = input.LA(1);

                         
                        int index16_324 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index16_324);

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
    static final String DFA14_eotS =
        "\u00d1\uffff";
    static final String DFA14_eofS =
        "\1\uffff\1\2\4\uffff\2\5\4\uffff\1\5\1\uffff\4\5\2\uffff\1\5\1\uffff"+
        "\1\5\u00ba\uffff";
    static final String DFA14_minS =
        "\1\32\1\12\4\uffff\2\12\4\uffff\1\12\1\uffff\4\12\2\uffff\1\12\1"+
        "\uffff\1\12\103\uffff\1\0\15\uffff\3\0\146\uffff";
    static final String DFA14_maxS =
        "\1\u0124\1\u0133\4\uffff\2\u0133\4\uffff\1\u0133\1\uffff\4\u0133"+
        "\2\uffff\1\u0133\1\uffff\1\u0133\103\uffff\1\0\15\uffff\3\0\146"+
        "\uffff";
    static final String DFA14_acceptS =
        "\2\uffff\1\2\2\uffff\1\1\u00cb\uffff";
    static final String DFA14_specialS =
        "\132\uffff\1\0\15\uffff\1\1\1\2\1\3\146\uffff}>";
    static final String[] DFA14_transitionS = {
            "\7\2\1\uffff\2\2\1\1\14\2\2\uffff\1\2\1\uffff\4\2\1\uffff\6"+
            "\2\1\uffff\2\2\1\uffff\1\2\1\uffff\4\2\1\uffff\20\2\1\uffff"+
            "\4\2\1\uffff\1\2\1\uffff\1\2\1\uffff\4\2\1\uffff\10\2\1\uffff"+
            "\3\2\1\uffff\1\2\1\uffff\4\2\1\uffff\3\2\1\uffff\14\2\1\uffff"+
            "\4\2\1\uffff\4\2\1\uffff\12\2\2\uffff\6\2\1\uffff\3\2\1\uffff"+
            "\4\2\1\uffff\1\2\1\uffff\5\2\1\uffff\2\2\1\uffff\5\2\2\uffff"+
            "\14\2\1\uffff\23\2\1\uffff\25\2\1\uffff\3\2\1\uffff\5\2\1\uffff"+
            "\4\2\1\uffff\3\2\1\uffff\14\2\1\uffff\1\2\2\uffff\1\2\1\uffff"+
            "\2\2",
            "\1\2\17\uffff\7\5\1\uffff\17\5\2\uffff\1\5\1\uffff\1\6\3\5"+
            "\1\uffff\6\5\1\uffff\2\5\1\uffff\1\5\1\uffff\4\5\1\uffff\20"+
            "\5\1\uffff\1\17\3\5\1\uffff\1\5\1\uffff\1\5\1\uffff\4\5\1\uffff"+
            "\10\5\1\uffff\3\5\1\2\1\5\1\uffff\2\5\1\7\1\5\1\2\3\5\1\uffff"+
            "\11\5\1\26\2\5\1\uffff\4\5\1\uffff\2\5\1\24\1\5\1\uffff\1\5"+
            "\1\21\10\5\1\uffff\1\2\6\5\1\uffff\3\5\1\uffff\4\5\1\uffff\1"+
            "\5\1\uffff\1\14\4\5\1\uffff\2\5\1\uffff\5\5\2\uffff\14\5\1\2"+
            "\23\5\1\2\13\5\1\20\11\5\1\uffff\3\5\1\uffff\5\5\1\uffff\4\5"+
            "\1\uffff\1\5\1\16\1\5\1\uffff\14\5\1\uffff\1\5\1\uffff\1\2\1"+
            "\5\1\2\2\5\16\uffff\1\2",
            "",
            "",
            "",
            "",
            "\1\5\44\uffff\1\2\5\uffff\1\5\46\uffff\1\5\31\uffff\1\5\4\uffff"+
            "\1\5\1\uffff\1\5\15\uffff\1\5\12\uffff\1\5\3\uffff\1\5\11\uffff"+
            "\1\5\22\uffff\1\5\33\uffff\1\5\23\uffff\1\5\13\uffff\1\5\32"+
            "\uffff\1\5\21\uffff\1\5\1\uffff\1\5\20\uffff\1\5",
            "\1\5\44\uffff\1\2\5\uffff\1\5\46\uffff\1\5\31\uffff\1\5\4\uffff"+
            "\1\5\1\uffff\1\5\15\uffff\1\5\12\uffff\1\5\3\uffff\1\5\11\uffff"+
            "\1\5\22\uffff\1\5\33\uffff\1\5\23\uffff\1\5\13\uffff\1\5\32"+
            "\uffff\1\5\21\uffff\1\5\1\uffff\1\5\20\uffff\1\5",
            "",
            "",
            "",
            "",
            "\1\5\44\uffff\1\2\5\uffff\1\5\46\uffff\1\5\31\uffff\1\5\4\uffff"+
            "\1\5\1\uffff\1\5\15\uffff\1\5\12\uffff\1\5\3\uffff\1\5\11\uffff"+
            "\1\5\22\uffff\1\5\33\uffff\1\5\23\uffff\1\5\13\uffff\1\5\32"+
            "\uffff\1\5\21\uffff\1\5\1\uffff\1\5\20\uffff\1\5",
            "",
            "\1\5\23\uffff\1\2\26\uffff\1\5\45\uffff\1\2\1\5\31\uffff\1"+
            "\132\4\uffff\1\5\1\uffff\1\5\15\uffff\1\5\12\uffff\1\5\3\uffff"+
            "\1\5\11\uffff\1\151\22\uffff\1\5\33\uffff\1\152\23\uffff\1\150"+
            "\13\uffff\1\5\32\uffff\1\5\21\uffff\1\5\1\uffff\1\5\20\uffff"+
            "\1\5",
            "\1\5\44\uffff\1\2\5\uffff\1\5\46\uffff\1\5\31\uffff\1\5\4\uffff"+
            "\1\5\1\uffff\1\5\15\uffff\1\5\12\uffff\1\5\3\uffff\1\5\11\uffff"+
            "\1\5\22\uffff\1\5\33\uffff\1\5\23\uffff\1\5\13\uffff\1\5\32"+
            "\uffff\1\5\21\uffff\1\5\1\uffff\1\5\20\uffff\1\5",
            "\1\5\44\uffff\1\2\5\uffff\1\5\46\uffff\1\5\31\uffff\1\5\4\uffff"+
            "\1\5\1\uffff\1\5\15\uffff\1\5\12\uffff\1\5\3\uffff\1\5\11\uffff"+
            "\1\5\22\uffff\1\5\33\uffff\1\5\23\uffff\1\5\13\uffff\1\5\32"+
            "\uffff\1\5\21\uffff\1\5\1\uffff\1\5\20\uffff\1\5",
            "\1\5\52\uffff\1\5\46\uffff\1\5\31\uffff\1\5\4\uffff\1\5\1\uffff"+
            "\1\5\15\uffff\1\5\12\uffff\1\5\3\uffff\1\5\11\uffff\1\5\22\uffff"+
            "\1\5\33\uffff\1\5\23\uffff\1\5\13\uffff\1\5\32\uffff\1\5\21"+
            "\uffff\1\5\1\uffff\1\5\13\uffff\1\2\4\uffff\1\5",
            "",
            "",
            "\1\5\52\uffff\1\5\46\uffff\1\5\31\uffff\1\5\4\uffff\1\5\1\uffff"+
            "\1\5\15\uffff\1\5\12\uffff\1\5\3\uffff\1\5\11\uffff\1\5\22\uffff"+
            "\1\5\33\uffff\1\5\23\uffff\1\5\13\uffff\1\5\32\uffff\1\5\17"+
            "\uffff\1\2\1\uffff\1\5\1\uffff\1\5\20\uffff\1\5",
            "",
            "\1\5\52\uffff\1\5\46\uffff\1\5\31\uffff\1\5\4\uffff\1\5\1\uffff"+
            "\1\5\15\uffff\1\5\3\uffff\1\2\6\uffff\1\5\3\uffff\1\5\11\uffff"+
            "\1\5\22\uffff\1\5\5\uffff\1\2\25\uffff\1\5\23\uffff\1\5\13\uffff"+
            "\1\5\32\uffff\1\5\21\uffff\1\5\1\uffff\1\5\20\uffff\1\5",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            return "134:9: ( KW_AS )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA14_90 = input.LA(1);

                         
                        int index14_90 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 5;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                         
                        input.seek(index14_90);

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA14_104 = input.LA(1);

                         
                        int index14_104 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 5;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                         
                        input.seek(index14_104);

                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA14_105 = input.LA(1);

                         
                        int index14_105 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 5;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                         
                        input.seek(index14_105);

                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA14_106 = input.LA(1);

                         
                        int index14_106 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (!(((useSQL11ReservedKeywordsForIdentifier())))) ) {s = 5;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 2;}

                         
                        input.seek(index14_106);

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
    static final String DFA19_eotS =
        "\103\uffff";
    static final String DFA19_eofS =
        "\103\uffff";
    static final String DFA19_minS =
        "\1\32\3\12\77\uffff";
    static final String DFA19_maxS =
        "\1\u0124\3\u0133\77\uffff";
    static final String DFA19_acceptS =
        "\4\uffff\1\1\1\uffff\1\2\74\uffff";
    static final String DFA19_specialS =
        "\103\uffff}>";
    static final String[] DFA19_transitionS = {
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
            "\1\4\30\uffff\1\6\5\uffff\3\6\10\uffff\1\6\27\uffff\2\6\2\uffff"+
            "\1\6\14\uffff\1\6\23\uffff\1\6\32\uffff\1\6\27\uffff\1\6\115"+
            "\uffff\1\6\6\uffff\2\6\7\uffff\2\6\13\uffff\1\6\15\uffff\1\6"+
            "\25\uffff\1\4",
            "\1\4\30\uffff\1\6\5\uffff\3\6\10\uffff\1\6\27\uffff\2\6\2\uffff"+
            "\1\6\14\uffff\1\6\23\uffff\1\6\32\uffff\1\6\27\uffff\1\6\115"+
            "\uffff\1\6\6\uffff\2\6\7\uffff\2\6\13\uffff\1\6\15\uffff\1\6"+
            "\25\uffff\1\4",
            "\1\4\30\uffff\1\6\5\uffff\3\6\10\uffff\1\6\27\uffff\2\6\2\uffff"+
            "\1\6\14\uffff\1\6\23\uffff\1\6\32\uffff\1\6\27\uffff\1\6\115"+
            "\uffff\1\6\6\uffff\2\6\7\uffff\2\6\13\uffff\1\6\15\uffff\1\6"+
            "\25\uffff\1\4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            return "146:22: ( aliasList | columnNameTypeList )";
        }
    }
    static final String DFA20_eotS =
        "\u00d3\uffff";
    static final String DFA20_eofS =
        "\1\uffff\3\4\u00cf\uffff";
    static final String DFA20_minS =
        "\1\32\3\12\23\uffff\1\7\46\uffff\1\7\46\uffff\1\7\155\uffff";
    static final String DFA20_maxS =
        "\1\u0124\3\u0133\23\uffff\1\u013b\46\uffff\1\u013b\46\uffff\1\u013b"+
        "\155\uffff";
    static final String DFA20_acceptS =
        "\4\uffff\1\1\24\uffff\1\2\u00b9\uffff";
    static final String DFA20_specialS =
        "\u00d3\uffff}>";
    static final String[] DFA20_transitionS = {
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
            "\1\4\30\uffff\1\31\5\uffff\3\31\10\uffff\1\31\1\4\26\uffff"+
            "\2\31\2\uffff\1\31\13\uffff\1\4\1\31\23\uffff\1\31\4\uffff\1"+
            "\4\4\uffff\1\4\1\uffff\1\4\15\uffff\1\4\1\31\11\uffff\1\4\3"+
            "\uffff\1\4\11\uffff\1\27\22\uffff\1\4\31\uffff\1\4\1\uffff\1"+
            "\4\16\uffff\1\4\4\uffff\1\4\12\uffff\1\31\1\4\5\uffff\2\31\7"+
            "\uffff\2\31\12\uffff\1\4\1\31\15\uffff\1\31\2\uffff\1\4\1\uffff"+
            "\1\4\20\uffff\1\4",
            "\1\4\30\uffff\1\31\5\uffff\3\31\10\uffff\1\31\1\4\26\uffff"+
            "\2\31\2\uffff\1\31\13\uffff\1\4\1\31\23\uffff\1\31\4\uffff\1"+
            "\4\4\uffff\1\4\1\uffff\1\4\15\uffff\1\4\1\31\11\uffff\1\4\3"+
            "\uffff\1\4\11\uffff\1\76\22\uffff\1\4\31\uffff\1\4\1\uffff\1"+
            "\4\16\uffff\1\4\4\uffff\1\4\12\uffff\1\31\1\4\5\uffff\2\31\7"+
            "\uffff\2\31\12\uffff\1\4\1\31\15\uffff\1\31\2\uffff\1\4\1\uffff"+
            "\1\4\20\uffff\1\4",
            "\1\4\30\uffff\1\31\5\uffff\3\31\10\uffff\1\31\1\4\26\uffff"+
            "\2\31\2\uffff\1\31\13\uffff\1\4\1\31\23\uffff\1\31\4\uffff\1"+
            "\4\4\uffff\1\4\1\uffff\1\4\15\uffff\1\4\1\31\11\uffff\1\4\3"+
            "\uffff\1\4\11\uffff\1\145\22\uffff\1\4\31\uffff\1\4\1\uffff"+
            "\1\4\16\uffff\1\4\4\uffff\1\4\12\uffff\1\31\1\4\5\uffff\2\31"+
            "\7\uffff\2\31\12\uffff\1\4\1\31\15\uffff\1\31\2\uffff\1\4\1"+
            "\uffff\1\4\20\uffff\1\4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\4\5\uffff\1\4\4\uffff\1\4\7\uffff\7\4\1\uffff\22\4\1\uffff"+
            "\4\4\1\uffff\6\4\1\uffff\2\4\1\uffff\1\4\1\uffff\4\4\1\uffff"+
            "\20\4\1\uffff\4\4\1\uffff\1\4\1\uffff\1\4\1\uffff\4\4\1\uffff"+
            "\10\4\1\uffff\3\4\1\uffff\1\4\1\uffff\4\4\1\uffff\25\4\1\uffff"+
            "\4\4\1\uffff\12\4\1\uffff\7\4\1\uffff\10\4\1\uffff\1\4\1\uffff"+
            "\5\4\1\uffff\2\4\1\uffff\5\4\2\uffff\14\4\1\uffff\23\4\1\uffff"+
            "\25\4\1\uffff\3\4\1\uffff\5\4\1\uffff\4\4\1\uffff\3\4\1\uffff"+
            "\14\4\1\uffff\1\4\2\uffff\1\4\1\uffff\2\4\1\uffff\1\31\1\uffff"+
            "\1\4\2\uffff\1\4\2\uffff\2\4\7\uffff\5\4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\4\5\uffff\1\4\4\uffff\1\4\7\uffff\7\4\1\uffff\22\4\1\uffff"+
            "\4\4\1\uffff\6\4\1\uffff\2\4\1\uffff\1\4\1\uffff\4\4\1\uffff"+
            "\20\4\1\uffff\4\4\1\uffff\1\4\1\uffff\1\4\1\uffff\4\4\1\uffff"+
            "\10\4\1\uffff\3\4\1\uffff\1\4\1\uffff\4\4\1\uffff\25\4\1\uffff"+
            "\4\4\1\uffff\12\4\1\uffff\7\4\1\uffff\10\4\1\uffff\1\4\1\uffff"+
            "\5\4\1\uffff\2\4\1\uffff\5\4\2\uffff\14\4\1\uffff\23\4\1\uffff"+
            "\25\4\1\uffff\3\4\1\uffff\5\4\1\uffff\4\4\1\uffff\3\4\1\uffff"+
            "\14\4\1\uffff\1\4\2\uffff\1\4\1\uffff\2\4\1\uffff\1\31\1\uffff"+
            "\1\4\2\uffff\1\4\2\uffff\2\4\7\uffff\5\4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\4\5\uffff\1\4\4\uffff\1\4\7\uffff\7\4\1\uffff\22\4\1\uffff"+
            "\4\4\1\uffff\6\4\1\uffff\2\4\1\uffff\1\4\1\uffff\4\4\1\uffff"+
            "\20\4\1\uffff\4\4\1\uffff\1\4\1\uffff\1\4\1\uffff\4\4\1\uffff"+
            "\10\4\1\uffff\3\4\1\uffff\1\4\1\uffff\4\4\1\uffff\25\4\1\uffff"+
            "\4\4\1\uffff\12\4\1\uffff\7\4\1\uffff\10\4\1\uffff\1\4\1\uffff"+
            "\5\4\1\uffff\2\4\1\uffff\5\4\2\uffff\14\4\1\uffff\23\4\1\uffff"+
            "\25\4\1\uffff\3\4\1\uffff\5\4\1\uffff\4\4\1\uffff\3\4\1\uffff"+
            "\14\4\1\uffff\1\4\2\uffff\1\4\1\uffff\2\4\1\uffff\1\31\1\uffff"+
            "\1\4\2\uffff\1\4\2\uffff\2\4\7\uffff\5\4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
        for (int i=0; i<numStates; i++) {
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
            return "146:65: ( aliasList | columnNameTypeList )";
        }
    }
    static final String DFA23_eotS =
        "\u01e0\uffff";
    static final String DFA23_eofS =
        "\u01e0\uffff";
    static final String DFA23_minS =
        "\1\7\1\uffff\3\4\2\uffff\1\4\1\uffff\4\4\7\uffff\2\4\2\uffff\2\4"+
        "\1\uffff\1\4\1\uffff\1\4\1\32\33\uffff\1\32\33\uffff\1\32\34\uffff"+
        "\1\32\33\uffff\1\32\34\uffff\1\32\33\uffff\1\32\33\uffff\1\32\33"+
        "\uffff\1\32\33\uffff\1\32\33\uffff\1\32\33\uffff\1\32\33\uffff\1"+
        "\32\33\uffff\1\32\34\uffff\3\0\1\uffff\3\0\1\uffff\3\0\1\uffff\3"+
        "\0\1\uffff\3\0\1\uffff\3\0\1\uffff\3\0\1\uffff\3\0\1\uffff\3\0\1"+
        "\uffff\3\0\1\uffff\3\0\1\uffff\3\0\1\uffff\3\0\1\uffff\3\0";
    static final String DFA23_maxS =
        "\1\u013b\1\uffff\3\u0137\2\uffff\1\u0139\1\uffff\1\u0137\1\u0139"+
        "\2\u0137\7\uffff\2\u0137\2\uffff\2\u0137\1\uffff\1\u0137\1\uffff"+
        "\2\u0137\33\uffff\1\u0137\33\uffff\1\u0137\34\uffff\1\u0137\33\uffff"+
        "\1\u0137\34\uffff\1\u0137\33\uffff\1\u0137\33\uffff\1\u0137\33\uffff"+
        "\1\u0137\33\uffff\1\u0137\33\uffff\1\u0137\33\uffff\1\u0137\33\uffff"+
        "\1\u0137\33\uffff\1\u0137\34\uffff\3\0\1\uffff\3\0\1\uffff\3\0\1"+
        "\uffff\3\0\1\uffff\3\0\1\uffff\3\0\1\uffff\3\0\1\uffff\3\0\1\uffff"+
        "\3\0\1\uffff\3\0\1\uffff\3\0\1\uffff\3\0\1\uffff\3\0\1\uffff\3\0";
    static final String DFA23_acceptS =
        "\1\uffff\1\1\3\uffff\1\2\u01a2\uffff\1\1\3\uffff\1\1\3\uffff\1\1"+
        "\3\uffff\1\1\3\uffff\1\1\3\uffff\1\1\3\uffff\1\1\3\uffff\1\1\3\uffff"+
        "\1\1\3\uffff\1\1\3\uffff\1\1\3\uffff\1\1\3\uffff\1\1\3\uffff\1\1"+
        "\3\uffff";
    static final String DFA23_specialS =
        "\1\0\35\uffff\1\1\33\uffff\1\2\33\uffff\1\3\34\uffff\1\4\33\uffff"+
        "\1\5\34\uffff\1\6\33\uffff\1\7\33\uffff\1\10\33\uffff\1\11\33\uffff"+
        "\1\12\33\uffff\1\13\33\uffff\1\14\33\uffff\1\15\33\uffff\1\16\34"+
        "\uffff\1\17\1\20\1\21\1\uffff\1\22\1\23\1\24\1\uffff\1\25\1\26\1"+
        "\27\1\uffff\1\30\1\31\1\32\1\uffff\1\33\1\34\1\35\1\uffff\1\36\1"+
        "\37\1\40\1\uffff\1\41\1\42\1\43\1\uffff\1\44\1\45\1\46\1\uffff\1"+
        "\47\1\50\1\51\1\uffff\1\52\1\53\1\54\1\uffff\1\55\1\56\1\57\1\uffff"+
        "\1\60\1\61\1\62\1\uffff\1\63\1\64\1\65\1\uffff\1\66\1\67\1\70}>";
    static final String[] DFA23_transitionS = {
            "\1\5\5\uffff\1\5\4\uffff\1\5\7\uffff\1\2\3\30\2\35\1\30\1\uffff"+
            "\1\30\1\25\1\35\1\30\1\35\1\30\1\35\3\31\1\35\2\30\1\35\1\30"+
            "\2\5\1\30\1\uffff\4\30\1\uffff\6\30\1\uffff\1\30\1\35\1\uffff"+
            "\1\35\1\uffff\1\11\1\13\1\35\1\30\1\uffff\1\30\1\7\3\30\1\35"+
            "\2\30\1\35\3\30\1\35\3\30\1\uffff\1\30\1\31\1\35\1\30\1\uffff"+
            "\1\30\1\uffff\1\30\1\uffff\1\30\1\33\2\30\1\uffff\1\35\1\24"+
            "\1\35\4\30\1\31\1\uffff\1\35\2\30\1\uffff\1\35\1\uffff\1\30"+
            "\3\35\1\uffff\3\30\1\5\1\30\2\35\2\30\1\35\3\30\1\35\1\31\1"+
            "\35\1\5\2\35\2\30\1\uffff\2\30\2\35\1\uffff\1\35\3\30\1\35\5"+
            "\30\1\uffff\1\5\6\30\1\uffff\1\30\1\35\1\30\1\5\1\30\1\4\1\35"+
            "\1\30\1\uffff\1\30\1\uffff\3\35\2\30\1\uffff\2\30\1\uffff\1"+
            "\35\2\30\1\35\1\30\2\uffff\2\30\1\35\2\30\1\35\2\30\1\35\3\30"+
            "\1\uffff\7\30\1\35\1\30\1\35\3\30\3\35\3\30\1\uffff\4\30\1\35"+
            "\5\30\1\31\7\30\1\3\1\35\1\30\1\uffff\3\30\1\uffff\1\12\1\30"+
            "\1\35\2\30\1\uffff\1\35\1\14\1\35\1\30\1\uffff\1\30\1\35\1\3"+
            "\1\uffff\3\30\1\35\2\30\2\35\2\30\1\35\1\30\1\uffff\1\30\2\uffff"+
            "\1\30\1\uffff\1\35\1\30\3\uffff\1\5\2\uffff\1\5\2\uffff\2\5"+
            "\7\uffff\1\1\4\5",
            "",
            "\3\5\3\uffff\1\5\3\uffff\2\5\1\uffff\1\36\2\uffff\2\5\1\uffff"+
            "\2\5\10\uffff\1\5\6\uffff\1\5\133\uffff\1\5\13\uffff\1\5\10"+
            "\uffff\1\5\25\uffff\1\5\6\uffff\1\5\33\uffff\1\5\1\uffff\1\5"+
            "\11\uffff\1\5\3\uffff\1\5\65\uffff\1\5\15\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\1\5\3\uffff\1\5\3\uffff\1\5",
            "\3\5\3\uffff\1\5\3\uffff\2\5\1\uffff\1\72\2\uffff\2\5\1\uffff"+
            "\2\5\10\uffff\1\5\6\uffff\1\5\133\uffff\1\5\13\uffff\1\5\10"+
            "\uffff\1\5\25\uffff\1\5\6\uffff\1\5\33\uffff\1\5\1\uffff\1\5"+
            "\11\uffff\1\5\3\uffff\1\5\65\uffff\1\5\15\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\1\5\3\uffff\1\5\3\uffff\1\5",
            "\3\5\3\uffff\1\5\3\uffff\2\5\1\uffff\1\126\2\uffff\2\5\1\uffff"+
            "\2\5\10\uffff\1\5\6\uffff\1\5\133\uffff\1\5\13\uffff\1\5\10"+
            "\uffff\1\5\25\uffff\1\5\6\uffff\1\5\33\uffff\1\5\1\uffff\1\5"+
            "\11\uffff\1\5\3\uffff\1\5\65\uffff\1\5\15\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\1\5\3\uffff\1\5\3\uffff\1\5",
            "",
            "",
            "\3\5\3\uffff\1\5\3\uffff\2\5\1\uffff\1\163\2\uffff\2\5\1\uffff"+
            "\2\5\10\uffff\1\5\6\uffff\1\5\133\uffff\1\5\13\uffff\1\5\10"+
            "\uffff\1\5\25\uffff\1\5\6\uffff\1\5\33\uffff\1\5\1\uffff\1\5"+
            "\11\uffff\1\5\3\uffff\1\5\65\uffff\1\5\15\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\1\5\3\uffff\1\5\3\uffff\1\5\1\uffff\1\5",
            "",
            "\3\5\3\uffff\1\5\3\uffff\2\5\1\uffff\1\u008f\2\uffff\2\5\1"+
            "\uffff\2\5\10\uffff\1\5\6\uffff\1\5\133\uffff\1\5\13\uffff\1"+
            "\5\10\uffff\1\5\25\uffff\1\5\6\uffff\1\5\33\uffff\1\5\1\uffff"+
            "\1\5\11\uffff\1\5\3\uffff\1\5\65\uffff\1\5\15\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\1\5\3\uffff\1\5\3\uffff\1\5",
            "\3\5\3\uffff\1\5\3\uffff\2\5\1\uffff\1\u00ac\2\uffff\2\5\1"+
            "\uffff\2\5\10\uffff\1\5\6\uffff\1\5\133\uffff\1\5\13\uffff\1"+
            "\5\10\uffff\1\5\25\uffff\1\5\6\uffff\1\5\33\uffff\1\5\1\uffff"+
            "\1\5\11\uffff\1\5\3\uffff\1\5\65\uffff\1\5\15\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\1\5\3\uffff\1\5\3\uffff\1\5\1\uffff\1\5",
            "\3\5\3\uffff\1\5\3\uffff\2\5\1\uffff\1\u00c8\2\uffff\2\5\1"+
            "\uffff\2\5\10\uffff\1\5\6\uffff\1\5\133\uffff\1\5\13\uffff\1"+
            "\5\10\uffff\1\5\25\uffff\1\5\6\uffff\1\5\33\uffff\1\5\1\uffff"+
            "\1\5\11\uffff\1\5\3\uffff\1\5\65\uffff\1\5\15\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\1\5\3\uffff\1\5\3\uffff\1\5",
            "\3\5\3\uffff\1\5\3\uffff\2\5\1\uffff\1\u00e4\2\uffff\2\5\1"+
            "\uffff\2\5\10\uffff\1\5\6\uffff\1\5\133\uffff\1\5\13\uffff\1"+
            "\5\10\uffff\1\5\25\uffff\1\5\6\uffff\1\5\33\uffff\1\5\1\uffff"+
            "\1\5\11\uffff\1\5\3\uffff\1\5\65\uffff\1\5\15\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\1\5\3\uffff\1\5\3\uffff\1\5",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\3\5\3\uffff\1\5\3\uffff\2\5\1\uffff\1\u0100\2\uffff\2\5\1"+
            "\uffff\2\5\10\uffff\1\5\6\uffff\1\5\133\uffff\1\5\13\uffff\1"+
            "\5\10\uffff\1\5\25\uffff\1\5\6\uffff\1\5\33\uffff\1\5\1\uffff"+
            "\1\5\11\uffff\1\5\3\uffff\1\5\65\uffff\1\5\15\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\1\5\3\uffff\1\5\3\uffff\1\5",
            "\3\5\3\uffff\1\5\3\uffff\2\5\1\uffff\1\u011c\2\uffff\2\5\1"+
            "\uffff\2\5\10\uffff\1\5\6\uffff\1\5\133\uffff\1\5\13\uffff\1"+
            "\5\10\uffff\1\5\25\uffff\1\5\6\uffff\1\5\33\uffff\1\5\1\uffff"+
            "\1\5\11\uffff\1\5\3\uffff\1\5\65\uffff\1\5\15\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\1\5\3\uffff\1\5\3\uffff\1\5",
            "",
            "",
            "\3\5\3\uffff\1\5\3\uffff\2\5\1\uffff\1\u0138\2\uffff\2\5\1"+
            "\uffff\2\5\10\uffff\1\5\6\uffff\1\5\133\uffff\1\5\13\uffff\1"+
            "\5\10\uffff\1\5\25\uffff\1\5\6\uffff\1\5\33\uffff\1\5\1\uffff"+
            "\1\5\11\uffff\1\5\3\uffff\1\5\65\uffff\1\5\15\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\1\5\3\uffff\1\5\3\uffff\1\5",
            "\3\5\3\uffff\1\5\3\uffff\2\5\1\uffff\1\u0154\2\uffff\2\5\1"+
            "\uffff\2\5\10\uffff\1\5\6\uffff\1\5\133\uffff\1\5\13\uffff\1"+
            "\5\10\uffff\1\5\25\uffff\1\5\6\uffff\1\5\33\uffff\1\5\1\uffff"+
            "\1\5\11\uffff\1\5\3\uffff\1\5\65\uffff\1\5\15\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\1\5\3\uffff\1\5\3\uffff\1\5",
            "",
            "\3\5\3\uffff\1\5\3\uffff\2\5\1\uffff\1\u0170\2\uffff\2\5\1"+
            "\uffff\2\5\10\uffff\1\5\6\uffff\1\5\133\uffff\1\5\13\uffff\1"+
            "\5\10\uffff\1\5\25\uffff\1\5\6\uffff\1\5\33\uffff\1\5\1\uffff"+
            "\1\5\11\uffff\1\5\3\uffff\1\5\65\uffff\1\5\15\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\1\5\3\uffff\1\5\3\uffff\1\5",
            "",
            "\3\5\3\uffff\1\5\3\uffff\2\5\1\uffff\1\u018c\2\uffff\2\5\1"+
            "\uffff\2\5\10\uffff\1\5\6\uffff\1\5\133\uffff\1\5\13\uffff\1"+
            "\5\10\uffff\1\5\25\uffff\1\5\6\uffff\1\5\33\uffff\1\5\1\uffff"+
            "\1\5\11\uffff\1\5\3\uffff\1\5\65\uffff\1\5\15\uffff\4\5\1\uffff"+
            "\3\5\1\uffff\1\5\3\uffff\1\5\3\uffff\1\5",
            "\1\u01a9\3\u01aa\2\u01ab\1\u01aa\1\uffff\1\u01aa\2\u01ab\1"+
            "\u01aa\1\u01ab\1\u01aa\5\u01ab\2\u01aa\1\u01ab\1\u01aa\2\uffff"+
            "\1\u01aa\1\uffff\4\u01aa\1\uffff\6\u01aa\1\uffff\1\u01aa\1\u01ab"+
            "\1\uffff\1\u01ab\1\uffff\3\u01ab\1\u01aa\1\uffff\1\u01aa\1\u01ab"+
            "\3\u01aa\1\u01ab\2\u01aa\1\u01ab\3\u01aa\1\u01ab\3\u01aa\1\uffff"+
            "\1\u01aa\2\u01ab\1\u01aa\1\uffff\1\u01aa\1\uffff\1\u01aa\1\uffff"+
            "\1\u01aa\1\u01ab\2\u01aa\1\uffff\3\u01ab\4\u01aa\1\u01ab\1\uffff"+
            "\1\u01ab\2\u01aa\1\uffff\1\u01ab\1\uffff\1\u01aa\3\u01ab\1\uffff"+
            "\3\u01aa\1\uffff\1\u01aa\2\u01ab\2\u01aa\1\u01ab\3\u01aa\3\u01ab"+
            "\1\uffff\2\u01ab\2\u01aa\1\uffff\2\u01aa\2\u01ab\1\uffff\1\u01ab"+
            "\3\u01aa\1\u01ab\5\u01aa\2\uffff\6\u01aa\1\uffff\1\u01aa\1\u01ab"+
            "\1\u01aa\1\uffff\1\u01aa\2\u01ab\1\u01aa\1\uffff\1\u01aa\1\uffff"+
            "\3\u01ab\2\u01aa\1\uffff\2\u01aa\1\uffff\1\u01ab\2\u01aa\1\u01ab"+
            "\1\u01aa\2\uffff\2\u01aa\1\u01ab\2\u01aa\1\u01ab\2\u01aa\1\u01ab"+
            "\3\u01aa\1\uffff\7\u01aa\1\u01ab\1\u01aa\1\u01ab\3\u01aa\3\u01ab"+
            "\3\u01aa\1\uffff\4\u01aa\1\u01ab\5\u01aa\1\u01ab\10\u01aa\1"+
            "\u01ab\1\u01aa\1\uffff\3\u01aa\1\uffff\1\u01ab\1\u01aa\1\u01ab"+
            "\2\u01aa\1\uffff\3\u01ab\1\u01aa\1\uffff\1\u01aa\1\u01ab\1\u01aa"+
            "\1\uffff\3\u01aa\1\u01ab\2\u01aa\2\u01ab\2\u01aa\1\u01ab\1\u01aa"+
            "\1\uffff\1\u01aa\2\uffff\1\u01aa\1\uffff\1\u01ab\1\u01aa\22"+
            "\uffff\1\u01a8",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u01ad\3\u01ae\2\u01af\1\u01ae\1\uffff\1\u01ae\2\u01af\1"+
            "\u01ae\1\u01af\1\u01ae\5\u01af\2\u01ae\1\u01af\1\u01ae\2\uffff"+
            "\1\u01ae\1\uffff\4\u01ae\1\uffff\6\u01ae\1\uffff\1\u01ae\1\u01af"+
            "\1\uffff\1\u01af\1\uffff\3\u01af\1\u01ae\1\uffff\1\u01ae\1\u01af"+
            "\3\u01ae\1\u01af\2\u01ae\1\u01af\3\u01ae\1\u01af\3\u01ae\1\uffff"+
            "\1\u01ae\2\u01af\1\u01ae\1\uffff\1\u01ae\1\uffff\1\u01ae\1\uffff"+
            "\1\u01ae\1\u01af\2\u01ae\1\uffff\3\u01af\4\u01ae\1\u01af\1\uffff"+
            "\1\u01af\2\u01ae\1\uffff\1\u01af\1\uffff\1\u01ae\3\u01af\1\uffff"+
            "\3\u01ae\1\uffff\1\u01ae\2\u01af\2\u01ae\1\u01af\3\u01ae\3\u01af"+
            "\1\uffff\2\u01af\2\u01ae\1\uffff\2\u01ae\2\u01af\1\uffff\1\u01af"+
            "\3\u01ae\1\u01af\5\u01ae\2\uffff\6\u01ae\1\uffff\1\u01ae\1\u01af"+
            "\1\u01ae\1\uffff\1\u01ae\2\u01af\1\u01ae\1\uffff\1\u01ae\1\uffff"+
            "\3\u01af\2\u01ae\1\uffff\2\u01ae\1\uffff\1\u01af\2\u01ae\1\u01af"+
            "\1\u01ae\2\uffff\2\u01ae\1\u01af\2\u01ae\1\u01af\2\u01ae\1\u01af"+
            "\3\u01ae\1\uffff\7\u01ae\1\u01af\1\u01ae\1\u01af\3\u01ae\3\u01af"+
            "\3\u01ae\1\uffff\4\u01ae\1\u01af\5\u01ae\1\u01af\10\u01ae\1"+
            "\u01af\1\u01ae\1\uffff\3\u01ae\1\uffff\1\u01af\1\u01ae\1\u01af"+
            "\2\u01ae\1\uffff\3\u01af\1\u01ae\1\uffff\1\u01ae\1\u01af\1\u01ae"+
            "\1\uffff\3\u01ae\1\u01af\2\u01ae\2\u01af\2\u01ae\1\u01af\1\u01ae"+
            "\1\uffff\1\u01ae\2\uffff\1\u01ae\1\uffff\1\u01af\1\u01ae\22"+
            "\uffff\1\u01ac",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u01b1\3\u01b2\2\u01b3\1\u01b2\1\uffff\1\u01b2\2\u01b3\1"+
            "\u01b2\1\u01b3\1\u01b2\5\u01b3\2\u01b2\1\u01b3\1\u01b2\2\uffff"+
            "\1\u01b2\1\uffff\4\u01b2\1\uffff\6\u01b2\1\uffff\1\u01b2\1\u01b3"+
            "\1\uffff\1\u01b3\1\uffff\3\u01b3\1\u01b2\1\uffff\1\u01b2\1\u01b3"+
            "\3\u01b2\1\u01b3\2\u01b2\1\u01b3\3\u01b2\1\u01b3\3\u01b2\1\uffff"+
            "\1\u01b2\2\u01b3\1\u01b2\1\uffff\1\u01b2\1\uffff\1\u01b2\1\uffff"+
            "\1\u01b2\1\u01b3\2\u01b2\1\uffff\3\u01b3\4\u01b2\1\u01b3\1\uffff"+
            "\1\u01b3\2\u01b2\1\uffff\1\u01b3\1\uffff\1\u01b2\3\u01b3\1\uffff"+
            "\3\u01b2\1\uffff\1\u01b2\2\u01b3\2\u01b2\1\u01b3\3\u01b2\3\u01b3"+
            "\1\uffff\2\u01b3\2\u01b2\1\uffff\2\u01b2\2\u01b3\1\uffff\1\u01b3"+
            "\3\u01b2\1\u01b3\5\u01b2\2\uffff\6\u01b2\1\uffff\1\u01b2\1\u01b3"+
            "\1\u01b2\1\uffff\1\u01b2\2\u01b3\1\u01b2\1\uffff\1\u01b2\1\uffff"+
            "\3\u01b3\2\u01b2\1\uffff\2\u01b2\1\uffff\1\u01b3\2\u01b2\1\u01b3"+
            "\1\u01b2\2\uffff\2\u01b2\1\u01b3\2\u01b2\1\u01b3\2\u01b2\1\u01b3"+
            "\3\u01b2\1\uffff\7\u01b2\1\u01b3\1\u01b2\1\u01b3\3\u01b2\3\u01b3"+
            "\3\u01b2\1\uffff\4\u01b2\1\u01b3\5\u01b2\1\u01b3\10\u01b2\1"+
            "\u01b3\1\u01b2\1\uffff\3\u01b2\1\uffff\1\u01b3\1\u01b2\1\u01b3"+
            "\2\u01b2\1\uffff\3\u01b3\1\u01b2\1\uffff\1\u01b2\1\u01b3\1\u01b2"+
            "\1\uffff\3\u01b2\1\u01b3\2\u01b2\2\u01b3\2\u01b2\1\u01b3\1\u01b2"+
            "\1\uffff\1\u01b2\2\uffff\1\u01b2\1\uffff\1\u01b3\1\u01b2\22"+
            "\uffff\1\u01b0",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u01b5\3\u01b6\2\u01b7\1\u01b6\1\uffff\1\u01b6\2\u01b7\1"+
            "\u01b6\1\u01b7\1\u01b6\5\u01b7\2\u01b6\1\u01b7\1\u01b6\2\uffff"+
            "\1\u01b6\1\uffff\4\u01b6\1\uffff\6\u01b6\1\uffff\1\u01b6\1\u01b7"+
            "\1\uffff\1\u01b7\1\uffff\3\u01b7\1\u01b6\1\uffff\1\u01b6\1\u01b7"+
            "\3\u01b6\1\u01b7\2\u01b6\1\u01b7\3\u01b6\1\u01b7\3\u01b6\1\uffff"+
            "\1\u01b6\2\u01b7\1\u01b6\1\uffff\1\u01b6\1\uffff\1\u01b6\1\uffff"+
            "\1\u01b6\1\u01b7\2\u01b6\1\uffff\3\u01b7\4\u01b6\1\u01b7\1\uffff"+
            "\1\u01b7\2\u01b6\1\uffff\1\u01b7\1\uffff\1\u01b6\3\u01b7\1\uffff"+
            "\3\u01b6\1\uffff\1\u01b6\2\u01b7\2\u01b6\1\u01b7\3\u01b6\3\u01b7"+
            "\1\uffff\2\u01b7\2\u01b6\1\uffff\2\u01b6\2\u01b7\1\uffff\1\u01b7"+
            "\3\u01b6\1\u01b7\5\u01b6\2\uffff\6\u01b6\1\uffff\1\u01b6\1\u01b7"+
            "\1\u01b6\1\uffff\1\u01b6\2\u01b7\1\u01b6\1\uffff\1\u01b6\1\uffff"+
            "\3\u01b7\2\u01b6\1\uffff\2\u01b6\1\uffff\1\u01b7\2\u01b6\1\u01b7"+
            "\1\u01b6\2\uffff\2\u01b6\1\u01b7\2\u01b6\1\u01b7\2\u01b6\1\u01b7"+
            "\3\u01b6\1\uffff\7\u01b6\1\u01b7\1\u01b6\1\u01b7\3\u01b6\3\u01b7"+
            "\3\u01b6\1\uffff\4\u01b6\1\u01b7\5\u01b6\1\u01b7\10\u01b6\1"+
            "\u01b7\1\u01b6\1\uffff\3\u01b6\1\uffff\1\u01b7\1\u01b6\1\u01b7"+
            "\2\u01b6\1\uffff\3\u01b7\1\u01b6\1\uffff\1\u01b6\1\u01b7\1\u01b6"+
            "\1\uffff\3\u01b6\1\u01b7\2\u01b6\2\u01b7\2\u01b6\1\u01b7\1\u01b6"+
            "\1\uffff\1\u01b6\2\uffff\1\u01b6\1\uffff\1\u01b7\1\u01b6\22"+
            "\uffff\1\u01b4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u01b9\3\u01ba\2\u01bb\1\u01ba\1\uffff\1\u01ba\2\u01bb\1"+
            "\u01ba\1\u01bb\1\u01ba\5\u01bb\2\u01ba\1\u01bb\1\u01ba\2\uffff"+
            "\1\u01ba\1\uffff\4\u01ba\1\uffff\6\u01ba\1\uffff\1\u01ba\1\u01bb"+
            "\1\uffff\1\u01bb\1\uffff\3\u01bb\1\u01ba\1\uffff\1\u01ba\1\u01bb"+
            "\3\u01ba\1\u01bb\2\u01ba\1\u01bb\3\u01ba\1\u01bb\3\u01ba\1\uffff"+
            "\1\u01ba\2\u01bb\1\u01ba\1\uffff\1\u01ba\1\uffff\1\u01ba\1\uffff"+
            "\1\u01ba\1\u01bb\2\u01ba\1\uffff\3\u01bb\4\u01ba\1\u01bb\1\uffff"+
            "\1\u01bb\2\u01ba\1\uffff\1\u01bb\1\uffff\1\u01ba\3\u01bb\1\uffff"+
            "\3\u01ba\1\uffff\1\u01ba\2\u01bb\2\u01ba\1\u01bb\3\u01ba\3\u01bb"+
            "\1\uffff\2\u01bb\2\u01ba\1\uffff\2\u01ba\2\u01bb\1\uffff\1\u01bb"+
            "\3\u01ba\1\u01bb\5\u01ba\2\uffff\6\u01ba\1\uffff\1\u01ba\1\u01bb"+
            "\1\u01ba\1\uffff\1\u01ba\2\u01bb\1\u01ba\1\uffff\1\u01ba\1\uffff"+
            "\3\u01bb\2\u01ba\1\uffff\2\u01ba\1\uffff\1\u01bb\2\u01ba\1\u01bb"+
            "\1\u01ba\2\uffff\2\u01ba\1\u01bb\2\u01ba\1\u01bb\2\u01ba\1\u01bb"+
            "\3\u01ba\1\uffff\7\u01ba\1\u01bb\1\u01ba\1\u01bb\3\u01ba\3\u01bb"+
            "\3\u01ba\1\uffff\4\u01ba\1\u01bb\5\u01ba\1\u01bb\10\u01ba\1"+
            "\u01bb\1\u01ba\1\uffff\3\u01ba\1\uffff\1\u01bb\1\u01ba\1\u01bb"+
            "\2\u01ba\1\uffff\3\u01bb\1\u01ba\1\uffff\1\u01ba\1\u01bb\1\u01ba"+
            "\1\uffff\3\u01ba\1\u01bb\2\u01ba\2\u01bb\2\u01ba\1\u01bb\1\u01ba"+
            "\1\uffff\1\u01ba\2\uffff\1\u01ba\1\uffff\1\u01bb\1\u01ba\22"+
            "\uffff\1\u01b8",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u01bd\3\u01be\2\u01bf\1\u01be\1\uffff\1\u01be\2\u01bf\1"+
            "\u01be\1\u01bf\1\u01be\5\u01bf\2\u01be\1\u01bf\1\u01be\2\uffff"+
            "\1\u01be\1\uffff\4\u01be\1\uffff\6\u01be\1\uffff\1\u01be\1\u01bf"+
            "\1\uffff\1\u01bf\1\uffff\3\u01bf\1\u01be\1\uffff\1\u01be\1\u01bf"+
            "\3\u01be\1\u01bf\2\u01be\1\u01bf\3\u01be\1\u01bf\3\u01be\1\uffff"+
            "\1\u01be\2\u01bf\1\u01be\1\uffff\1\u01be\1\uffff\1\u01be\1\uffff"+
            "\1\u01be\1\u01bf\2\u01be\1\uffff\3\u01bf\4\u01be\1\u01bf\1\uffff"+
            "\1\u01bf\2\u01be\1\uffff\1\u01bf\1\uffff\1\u01be\3\u01bf\1\uffff"+
            "\3\u01be\1\uffff\1\u01be\2\u01bf\2\u01be\1\u01bf\3\u01be\3\u01bf"+
            "\1\uffff\2\u01bf\2\u01be\1\uffff\2\u01be\2\u01bf\1\uffff\1\u01bf"+
            "\3\u01be\1\u01bf\5\u01be\2\uffff\6\u01be\1\uffff\1\u01be\1\u01bf"+
            "\1\u01be\1\uffff\1\u01be\2\u01bf\1\u01be\1\uffff\1\u01be\1\uffff"+
            "\3\u01bf\2\u01be\1\uffff\2\u01be\1\uffff\1\u01bf\2\u01be\1\u01bf"+
            "\1\u01be\2\uffff\2\u01be\1\u01bf\2\u01be\1\u01bf\2\u01be\1\u01bf"+
            "\3\u01be\1\uffff\7\u01be\1\u01bf\1\u01be\1\u01bf\3\u01be\3\u01bf"+
            "\3\u01be\1\uffff\4\u01be\1\u01bf\5\u01be\1\u01bf\10\u01be\1"+
            "\u01bf\1\u01be\1\uffff\3\u01be\1\uffff\1\u01bf\1\u01be\1\u01bf"+
            "\2\u01be\1\uffff\3\u01bf\1\u01be\1\uffff\1\u01be\1\u01bf\1\u01be"+
            "\1\uffff\3\u01be\1\u01bf\2\u01be\2\u01bf\2\u01be\1\u01bf\1\u01be"+
            "\1\uffff\1\u01be\2\uffff\1\u01be\1\uffff\1\u01bf\1\u01be\22"+
            "\uffff\1\u01bc",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u01c1\3\u01c2\2\u01c3\1\u01c2\1\uffff\1\u01c2\2\u01c3\1"+
            "\u01c2\1\u01c3\1\u01c2\5\u01c3\2\u01c2\1\u01c3\1\u01c2\2\uffff"+
            "\1\u01c2\1\uffff\4\u01c2\1\uffff\6\u01c2\1\uffff\1\u01c2\1\u01c3"+
            "\1\uffff\1\u01c3\1\uffff\3\u01c3\1\u01c2\1\uffff\1\u01c2\1\u01c3"+
            "\3\u01c2\1\u01c3\2\u01c2\1\u01c3\3\u01c2\1\u01c3\3\u01c2\1\uffff"+
            "\1\u01c2\2\u01c3\1\u01c2\1\uffff\1\u01c2\1\uffff\1\u01c2\1\uffff"+
            "\1\u01c2\1\u01c3\2\u01c2\1\uffff\3\u01c3\4\u01c2\1\u01c3\1\uffff"+
            "\1\u01c3\2\u01c2\1\uffff\1\u01c3\1\uffff\1\u01c2\3\u01c3\1\uffff"+
            "\3\u01c2\1\uffff\1\u01c2\2\u01c3\2\u01c2\1\u01c3\3\u01c2\3\u01c3"+
            "\1\uffff\2\u01c3\2\u01c2\1\uffff\2\u01c2\2\u01c3\1\uffff\1\u01c3"+
            "\3\u01c2\1\u01c3\5\u01c2\2\uffff\6\u01c2\1\uffff\1\u01c2\1\u01c3"+
            "\1\u01c2\1\uffff\1\u01c2\2\u01c3\1\u01c2\1\uffff\1\u01c2\1\uffff"+
            "\3\u01c3\2\u01c2\1\uffff\2\u01c2\1\uffff\1\u01c3\2\u01c2\1\u01c3"+
            "\1\u01c2\2\uffff\2\u01c2\1\u01c3\2\u01c2\1\u01c3\2\u01c2\1\u01c3"+
            "\3\u01c2\1\uffff\7\u01c2\1\u01c3\1\u01c2\1\u01c3\3\u01c2\3\u01c3"+
            "\3\u01c2\1\uffff\4\u01c2\1\u01c3\5\u01c2\1\u01c3\10\u01c2\1"+
            "\u01c3\1\u01c2\1\uffff\3\u01c2\1\uffff\1\u01c3\1\u01c2\1\u01c3"+
            "\2\u01c2\1\uffff\3\u01c3\1\u01c2\1\uffff\1\u01c2\1\u01c3\1\u01c2"+
            "\1\uffff\3\u01c2\1\u01c3\2\u01c2\2\u01c3\2\u01c2\1\u01c3\1\u01c2"+
            "\1\uffff\1\u01c2\2\uffff\1\u01c2\1\uffff\1\u01c3\1\u01c2\22"+
            "\uffff\1\u01c0",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u01c5\3\u01c6\2\u01c7\1\u01c6\1\uffff\1\u01c6\2\u01c7\1"+
            "\u01c6\1\u01c7\1\u01c6\5\u01c7\2\u01c6\1\u01c7\1\u01c6\2\uffff"+
            "\1\u01c6\1\uffff\4\u01c6\1\uffff\6\u01c6\1\uffff\1\u01c6\1\u01c7"+
            "\1\uffff\1\u01c7\1\uffff\3\u01c7\1\u01c6\1\uffff\1\u01c6\1\u01c7"+
            "\3\u01c6\1\u01c7\2\u01c6\1\u01c7\3\u01c6\1\u01c7\3\u01c6\1\uffff"+
            "\1\u01c6\2\u01c7\1\u01c6\1\uffff\1\u01c6\1\uffff\1\u01c6\1\uffff"+
            "\1\u01c6\1\u01c7\2\u01c6\1\uffff\3\u01c7\4\u01c6\1\u01c7\1\uffff"+
            "\1\u01c7\2\u01c6\1\uffff\1\u01c7\1\uffff\1\u01c6\3\u01c7\1\uffff"+
            "\3\u01c6\1\uffff\1\u01c6\2\u01c7\2\u01c6\1\u01c7\3\u01c6\3\u01c7"+
            "\1\uffff\2\u01c7\2\u01c6\1\uffff\2\u01c6\2\u01c7\1\uffff\1\u01c7"+
            "\3\u01c6\1\u01c7\5\u01c6\2\uffff\6\u01c6\1\uffff\1\u01c6\1\u01c7"+
            "\1\u01c6\1\uffff\1\u01c6\2\u01c7\1\u01c6\1\uffff\1\u01c6\1\uffff"+
            "\3\u01c7\2\u01c6\1\uffff\2\u01c6\1\uffff\1\u01c7\2\u01c6\1\u01c7"+
            "\1\u01c6\2\uffff\2\u01c6\1\u01c7\2\u01c6\1\u01c7\2\u01c6\1\u01c7"+
            "\3\u01c6\1\uffff\7\u01c6\1\u01c7\1\u01c6\1\u01c7\3\u01c6\3\u01c7"+
            "\3\u01c6\1\uffff\4\u01c6\1\u01c7\5\u01c6\1\u01c7\10\u01c6\1"+
            "\u01c7\1\u01c6\1\uffff\3\u01c6\1\uffff\1\u01c7\1\u01c6\1\u01c7"+
            "\2\u01c6\1\uffff\3\u01c7\1\u01c6\1\uffff\1\u01c6\1\u01c7\1\u01c6"+
            "\1\uffff\3\u01c6\1\u01c7\2\u01c6\2\u01c7\2\u01c6\1\u01c7\1\u01c6"+
            "\1\uffff\1\u01c6\2\uffff\1\u01c6\1\uffff\1\u01c7\1\u01c6\22"+
            "\uffff\1\u01c4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u01c9\3\u01ca\2\u01cb\1\u01ca\1\uffff\1\u01ca\2\u01cb\1"+
            "\u01ca\1\u01cb\1\u01ca\5\u01cb\2\u01ca\1\u01cb\1\u01ca\2\uffff"+
            "\1\u01ca\1\uffff\4\u01ca\1\uffff\6\u01ca\1\uffff\1\u01ca\1\u01cb"+
            "\1\uffff\1\u01cb\1\uffff\3\u01cb\1\u01ca\1\uffff\1\u01ca\1\u01cb"+
            "\3\u01ca\1\u01cb\2\u01ca\1\u01cb\3\u01ca\1\u01cb\3\u01ca\1\uffff"+
            "\1\u01ca\2\u01cb\1\u01ca\1\uffff\1\u01ca\1\uffff\1\u01ca\1\uffff"+
            "\1\u01ca\1\u01cb\2\u01ca\1\uffff\3\u01cb\4\u01ca\1\u01cb\1\uffff"+
            "\1\u01cb\2\u01ca\1\uffff\1\u01cb\1\uffff\1\u01ca\3\u01cb\1\uffff"+
            "\3\u01ca\1\uffff\1\u01ca\2\u01cb\2\u01ca\1\u01cb\3\u01ca\3\u01cb"+
            "\1\uffff\2\u01cb\2\u01ca\1\uffff\2\u01ca\2\u01cb\1\uffff\1\u01cb"+
            "\3\u01ca\1\u01cb\5\u01ca\2\uffff\6\u01ca\1\uffff\1\u01ca\1\u01cb"+
            "\1\u01ca\1\uffff\1\u01ca\2\u01cb\1\u01ca\1\uffff\1\u01ca\1\uffff"+
            "\3\u01cb\2\u01ca\1\uffff\2\u01ca\1\uffff\1\u01cb\2\u01ca\1\u01cb"+
            "\1\u01ca\2\uffff\2\u01ca\1\u01cb\2\u01ca\1\u01cb\2\u01ca\1\u01cb"+
            "\3\u01ca\1\uffff\7\u01ca\1\u01cb\1\u01ca\1\u01cb\3\u01ca\3\u01cb"+
            "\3\u01ca\1\uffff\4\u01ca\1\u01cb\5\u01ca\1\u01cb\10\u01ca\1"+
            "\u01cb\1\u01ca\1\uffff\3\u01ca\1\uffff\1\u01cb\1\u01ca\1\u01cb"+
            "\2\u01ca\1\uffff\3\u01cb\1\u01ca\1\uffff\1\u01ca\1\u01cb\1\u01ca"+
            "\1\uffff\3\u01ca\1\u01cb\2\u01ca\2\u01cb\2\u01ca\1\u01cb\1\u01ca"+
            "\1\uffff\1\u01ca\2\uffff\1\u01ca\1\uffff\1\u01cb\1\u01ca\22"+
            "\uffff\1\u01c8",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u01cd\3\u01ce\2\u01cf\1\u01ce\1\uffff\1\u01ce\2\u01cf\1"+
            "\u01ce\1\u01cf\1\u01ce\5\u01cf\2\u01ce\1\u01cf\1\u01ce\2\uffff"+
            "\1\u01ce\1\uffff\4\u01ce\1\uffff\6\u01ce\1\uffff\1\u01ce\1\u01cf"+
            "\1\uffff\1\u01cf\1\uffff\3\u01cf\1\u01ce\1\uffff\1\u01ce\1\u01cf"+
            "\3\u01ce\1\u01cf\2\u01ce\1\u01cf\3\u01ce\1\u01cf\3\u01ce\1\uffff"+
            "\1\u01ce\2\u01cf\1\u01ce\1\uffff\1\u01ce\1\uffff\1\u01ce\1\uffff"+
            "\1\u01ce\1\u01cf\2\u01ce\1\uffff\3\u01cf\4\u01ce\1\u01cf\1\uffff"+
            "\1\u01cf\2\u01ce\1\uffff\1\u01cf\1\uffff\1\u01ce\3\u01cf\1\uffff"+
            "\3\u01ce\1\uffff\1\u01ce\2\u01cf\2\u01ce\1\u01cf\3\u01ce\3\u01cf"+
            "\1\uffff\2\u01cf\2\u01ce\1\uffff\2\u01ce\2\u01cf\1\uffff\1\u01cf"+
            "\3\u01ce\1\u01cf\5\u01ce\2\uffff\6\u01ce\1\uffff\1\u01ce\1\u01cf"+
            "\1\u01ce\1\uffff\1\u01ce\2\u01cf\1\u01ce\1\uffff\1\u01ce\1\uffff"+
            "\3\u01cf\2\u01ce\1\uffff\2\u01ce\1\uffff\1\u01cf\2\u01ce\1\u01cf"+
            "\1\u01ce\2\uffff\2\u01ce\1\u01cf\2\u01ce\1\u01cf\2\u01ce\1\u01cf"+
            "\3\u01ce\1\uffff\7\u01ce\1\u01cf\1\u01ce\1\u01cf\3\u01ce\3\u01cf"+
            "\3\u01ce\1\uffff\4\u01ce\1\u01cf\5\u01ce\1\u01cf\10\u01ce\1"+
            "\u01cf\1\u01ce\1\uffff\3\u01ce\1\uffff\1\u01cf\1\u01ce\1\u01cf"+
            "\2\u01ce\1\uffff\3\u01cf\1\u01ce\1\uffff\1\u01ce\1\u01cf\1\u01ce"+
            "\1\uffff\3\u01ce\1\u01cf\2\u01ce\2\u01cf\2\u01ce\1\u01cf\1\u01ce"+
            "\1\uffff\1\u01ce\2\uffff\1\u01ce\1\uffff\1\u01cf\1\u01ce\22"+
            "\uffff\1\u01cc",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u01d1\3\u01d2\2\u01d3\1\u01d2\1\uffff\1\u01d2\2\u01d3\1"+
            "\u01d2\1\u01d3\1\u01d2\5\u01d3\2\u01d2\1\u01d3\1\u01d2\2\uffff"+
            "\1\u01d2\1\uffff\4\u01d2\1\uffff\6\u01d2\1\uffff\1\u01d2\1\u01d3"+
            "\1\uffff\1\u01d3\1\uffff\3\u01d3\1\u01d2\1\uffff\1\u01d2\1\u01d3"+
            "\3\u01d2\1\u01d3\2\u01d2\1\u01d3\3\u01d2\1\u01d3\3\u01d2\1\uffff"+
            "\1\u01d2\2\u01d3\1\u01d2\1\uffff\1\u01d2\1\uffff\1\u01d2\1\uffff"+
            "\1\u01d2\1\u01d3\2\u01d2\1\uffff\3\u01d3\4\u01d2\1\u01d3\1\uffff"+
            "\1\u01d3\2\u01d2\1\uffff\1\u01d3\1\uffff\1\u01d2\3\u01d3\1\uffff"+
            "\3\u01d2\1\uffff\1\u01d2\2\u01d3\2\u01d2\1\u01d3\3\u01d2\3\u01d3"+
            "\1\uffff\2\u01d3\2\u01d2\1\uffff\2\u01d2\2\u01d3\1\uffff\1\u01d3"+
            "\3\u01d2\1\u01d3\5\u01d2\2\uffff\6\u01d2\1\uffff\1\u01d2\1\u01d3"+
            "\1\u01d2\1\uffff\1\u01d2\2\u01d3\1\u01d2\1\uffff\1\u01d2\1\uffff"+
            "\3\u01d3\2\u01d2\1\uffff\2\u01d2\1\uffff\1\u01d3\2\u01d2\1\u01d3"+
            "\1\u01d2\2\uffff\2\u01d2\1\u01d3\2\u01d2\1\u01d3\2\u01d2\1\u01d3"+
            "\3\u01d2\1\uffff\7\u01d2\1\u01d3\1\u01d2\1\u01d3\3\u01d2\3\u01d3"+
            "\3\u01d2\1\uffff\4\u01d2\1\u01d3\5\u01d2\1\u01d3\10\u01d2\1"+
            "\u01d3\1\u01d2\1\uffff\3\u01d2\1\uffff\1\u01d3\1\u01d2\1\u01d3"+
            "\2\u01d2\1\uffff\3\u01d3\1\u01d2\1\uffff\1\u01d2\1\u01d3\1\u01d2"+
            "\1\uffff\3\u01d2\1\u01d3\2\u01d2\2\u01d3\2\u01d2\1\u01d3\1\u01d2"+
            "\1\uffff\1\u01d2\2\uffff\1\u01d2\1\uffff\1\u01d3\1\u01d2\22"+
            "\uffff\1\u01d0",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u01d5\3\u01d6\2\u01d7\1\u01d6\1\uffff\1\u01d6\2\u01d7\1"+
            "\u01d6\1\u01d7\1\u01d6\5\u01d7\2\u01d6\1\u01d7\1\u01d6\2\uffff"+
            "\1\u01d6\1\uffff\4\u01d6\1\uffff\6\u01d6\1\uffff\1\u01d6\1\u01d7"+
            "\1\uffff\1\u01d7\1\uffff\3\u01d7\1\u01d6\1\uffff\1\u01d6\1\u01d7"+
            "\3\u01d6\1\u01d7\2\u01d6\1\u01d7\3\u01d6\1\u01d7\3\u01d6\1\uffff"+
            "\1\u01d6\2\u01d7\1\u01d6\1\uffff\1\u01d6\1\uffff\1\u01d6\1\uffff"+
            "\1\u01d6\1\u01d7\2\u01d6\1\uffff\3\u01d7\4\u01d6\1\u01d7\1\uffff"+
            "\1\u01d7\2\u01d6\1\uffff\1\u01d7\1\uffff\1\u01d6\3\u01d7\1\uffff"+
            "\3\u01d6\1\uffff\1\u01d6\2\u01d7\2\u01d6\1\u01d7\3\u01d6\3\u01d7"+
            "\1\uffff\2\u01d7\2\u01d6\1\uffff\2\u01d6\2\u01d7\1\uffff\1\u01d7"+
            "\3\u01d6\1\u01d7\5\u01d6\2\uffff\6\u01d6\1\uffff\1\u01d6\1\u01d7"+
            "\1\u01d6\1\uffff\1\u01d6\2\u01d7\1\u01d6\1\uffff\1\u01d6\1\uffff"+
            "\3\u01d7\2\u01d6\1\uffff\2\u01d6\1\uffff\1\u01d7\2\u01d6\1\u01d7"+
            "\1\u01d6\2\uffff\2\u01d6\1\u01d7\2\u01d6\1\u01d7\2\u01d6\1\u01d7"+
            "\3\u01d6\1\uffff\7\u01d6\1\u01d7\1\u01d6\1\u01d7\3\u01d6\3\u01d7"+
            "\3\u01d6\1\uffff\4\u01d6\1\u01d7\5\u01d6\1\u01d7\10\u01d6\1"+
            "\u01d7\1\u01d6\1\uffff\3\u01d6\1\uffff\1\u01d7\1\u01d6\1\u01d7"+
            "\2\u01d6\1\uffff\3\u01d7\1\u01d6\1\uffff\1\u01d6\1\u01d7\1\u01d6"+
            "\1\uffff\3\u01d6\1\u01d7\2\u01d6\2\u01d7\2\u01d6\1\u01d7\1\u01d6"+
            "\1\uffff\1\u01d6\2\uffff\1\u01d6\1\uffff\1\u01d7\1\u01d6\22"+
            "\uffff\1\u01d4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u01d9\3\u01da\2\u01db\1\u01da\1\uffff\1\u01da\2\u01db\1"+
            "\u01da\1\u01db\1\u01da\5\u01db\2\u01da\1\u01db\1\u01da\2\uffff"+
            "\1\u01da\1\uffff\4\u01da\1\uffff\6\u01da\1\uffff\1\u01da\1\u01db"+
            "\1\uffff\1\u01db\1\uffff\3\u01db\1\u01da\1\uffff\1\u01da\1\u01db"+
            "\3\u01da\1\u01db\2\u01da\1\u01db\3\u01da\1\u01db\3\u01da\1\uffff"+
            "\1\u01da\2\u01db\1\u01da\1\uffff\1\u01da\1\uffff\1\u01da\1\uffff"+
            "\1\u01da\1\u01db\2\u01da\1\uffff\3\u01db\4\u01da\1\u01db\1\uffff"+
            "\1\u01db\2\u01da\1\uffff\1\u01db\1\uffff\1\u01da\3\u01db\1\uffff"+
            "\3\u01da\1\uffff\1\u01da\2\u01db\2\u01da\1\u01db\3\u01da\3\u01db"+
            "\1\uffff\2\u01db\2\u01da\1\uffff\2\u01da\2\u01db\1\uffff\1\u01db"+
            "\3\u01da\1\u01db\5\u01da\2\uffff\6\u01da\1\uffff\1\u01da\1\u01db"+
            "\1\u01da\1\uffff\1\u01da\2\u01db\1\u01da\1\uffff\1\u01da\1\uffff"+
            "\3\u01db\2\u01da\1\uffff\2\u01da\1\uffff\1\u01db\2\u01da\1\u01db"+
            "\1\u01da\2\uffff\2\u01da\1\u01db\2\u01da\1\u01db\2\u01da\1\u01db"+
            "\3\u01da\1\uffff\7\u01da\1\u01db\1\u01da\1\u01db\3\u01da\3\u01db"+
            "\3\u01da\1\uffff\4\u01da\1\u01db\5\u01da\1\u01db\10\u01da\1"+
            "\u01db\1\u01da\1\uffff\3\u01da\1\uffff\1\u01db\1\u01da\1\u01db"+
            "\2\u01da\1\uffff\3\u01db\1\u01da\1\uffff\1\u01da\1\u01db\1\u01da"+
            "\1\uffff\3\u01da\1\u01db\2\u01da\2\u01db\2\u01da\1\u01db\1\u01da"+
            "\1\uffff\1\u01da\2\uffff\1\u01da\1\uffff\1\u01db\1\u01da\22"+
            "\uffff\1\u01d8",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u01dd\3\u01de\2\u01df\1\u01de\1\uffff\1\u01de\2\u01df\1"+
            "\u01de\1\u01df\1\u01de\5\u01df\2\u01de\1\u01df\1\u01de\2\uffff"+
            "\1\u01de\1\uffff\4\u01de\1\uffff\6\u01de\1\uffff\1\u01de\1\u01df"+
            "\1\uffff\1\u01df\1\uffff\3\u01df\1\u01de\1\uffff\1\u01de\1\u01df"+
            "\3\u01de\1\u01df\2\u01de\1\u01df\3\u01de\1\u01df\3\u01de\1\uffff"+
            "\1\u01de\2\u01df\1\u01de\1\uffff\1\u01de\1\uffff\1\u01de\1\uffff"+
            "\1\u01de\1\u01df\2\u01de\1\uffff\3\u01df\4\u01de\1\u01df\1\uffff"+
            "\1\u01df\2\u01de\1\uffff\1\u01df\1\uffff\1\u01de\3\u01df\1\uffff"+
            "\3\u01de\1\uffff\1\u01de\2\u01df\2\u01de\1\u01df\3\u01de\3\u01df"+
            "\1\uffff\2\u01df\2\u01de\1\uffff\2\u01de\2\u01df\1\uffff\1\u01df"+
            "\3\u01de\1\u01df\5\u01de\2\uffff\6\u01de\1\uffff\1\u01de\1\u01df"+
            "\1\u01de\1\uffff\1\u01de\2\u01df\1\u01de\1\uffff\1\u01de\1\uffff"+
            "\3\u01df\2\u01de\1\uffff\2\u01de\1\uffff\1\u01df\2\u01de\1\u01df"+
            "\1\u01de\2\uffff\2\u01de\1\u01df\2\u01de\1\u01df\2\u01de\1\u01df"+
            "\3\u01de\1\uffff\7\u01de\1\u01df\1\u01de\1\u01df\3\u01de\3\u01df"+
            "\3\u01de\1\uffff\4\u01de\1\u01df\5\u01de\1\u01df\10\u01de\1"+
            "\u01df\1\u01de\1\uffff\3\u01de\1\uffff\1\u01df\1\u01de\1\u01df"+
            "\2\u01de\1\uffff\3\u01df\1\u01de\1\uffff\1\u01de\1\u01df\1\u01de"+
            "\1\uffff\3\u01de\1\u01df\2\u01de\2\u01df\2\u01de\1\u01df\1\u01de"+
            "\1\uffff\1\u01de\2\uffff\1\u01de\1\uffff\1\u01df\1\u01de\22"+
            "\uffff\1\u01dc",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff"
    };

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
        for (int i=0; i<numStates; i++) {
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
            return "151:1: selectExpression : ( ( tableAllColumns )=> tableAllColumns | expression );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA23_0 = input.LA(1);

                         
                        int index23_0 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA23_0==STAR) && (synpred2_SelectClauseParser())) {s = 1;}

                        else if ( (LA23_0==Identifier) ) {s = 2;}

                        else if ( (LA23_0==KW_STRUCT||LA23_0==KW_UNIONTYPE) ) {s = 3;}

                        else if ( (LA23_0==KW_NULL) ) {s = 4;}

                        else if ( (LA23_0==BigintLiteral||LA23_0==CharSetName||LA23_0==DecimalLiteral||(LA23_0 >= KW_CASE && LA23_0 <= KW_CAST)||LA23_0==KW_IF||LA23_0==KW_INTERVAL||LA23_0==KW_MAP||LA23_0==KW_NOT||LA23_0==LPAREN||LA23_0==MINUS||(LA23_0 >= Number && LA23_0 <= PLUS)||(LA23_0 >= SmallintLiteral && LA23_0 <= TinyintLiteral)) ) {s = 5;}

                        else if ( (LA23_0==KW_DATE) ) {s = 7;}

                        else if ( (LA23_0==KW_CURRENT_DATE) ) {s = 9;}

                        else if ( (LA23_0==KW_TIMESTAMP) ) {s = 10;}

                        else if ( (LA23_0==KW_CURRENT_TIMESTAMP) ) {s = 11;}

                        else if ( (LA23_0==KW_TRUE) ) {s = 12;}

                        else if ( (LA23_0==KW_FALSE) ) {s = 20;}

                        else if ( (LA23_0==KW_ARRAY) ) {s = 21;}

                        else if ( ((LA23_0 >= KW_ADD && LA23_0 <= KW_AFTER)||LA23_0==KW_ANALYZE||LA23_0==KW_ARCHIVE||LA23_0==KW_ASC||LA23_0==KW_BEFORE||(LA23_0 >= KW_BUCKET && LA23_0 <= KW_BUCKETS)||LA23_0==KW_CASCADE||LA23_0==KW_CHANGE||(LA23_0 >= KW_CLUSTER && LA23_0 <= KW_COLLECTION)||(LA23_0 >= KW_COLUMNS && LA23_0 <= KW_CONCATENATE)||LA23_0==KW_CONTINUE||LA23_0==KW_DATA||LA23_0==KW_DATABASES||(LA23_0 >= KW_DATETIME && LA23_0 <= KW_DBPROPERTIES)||(LA23_0 >= KW_DEFERRED && LA23_0 <= KW_DEFINED)||(LA23_0 >= KW_DELIMITED && LA23_0 <= KW_DESC)||(LA23_0 >= KW_DIRECTORIES && LA23_0 <= KW_DISABLE)||LA23_0==KW_DISTRIBUTE||LA23_0==KW_ELEM_TYPE||LA23_0==KW_ENABLE||LA23_0==KW_ESCAPED||LA23_0==KW_EXCLUSIVE||(LA23_0 >= KW_EXPLAIN && LA23_0 <= KW_EXPORT)||(LA23_0 >= KW_FIELDS && LA23_0 <= KW_FIRST)||(LA23_0 >= KW_FORMAT && LA23_0 <= KW_FORMATTED)||LA23_0==KW_FUNCTIONS||(LA23_0 >= KW_HOLD_DDLTIME && LA23_0 <= KW_IDXPROPERTIES)||LA23_0==KW_IGNORE||(LA23_0 >= KW_INDEX && LA23_0 <= KW_INDEXES)||(LA23_0 >= KW_INPATH && LA23_0 <= KW_INPUTFORMAT)||(LA23_0 >= KW_ITEMS && LA23_0 <= KW_JAR)||(LA23_0 >= KW_KEYS && LA23_0 <= KW_KEY_TYPE)||(LA23_0 >= KW_LIMIT && LA23_0 <= KW_LOAD)||(LA23_0 >= KW_LOCATION && LA23_0 <= KW_LONG)||(LA23_0 >= KW_MAPJOIN && LA23_0 <= KW_MONTH)||LA23_0==KW_MSCK||LA23_0==KW_NOSCAN||LA23_0==KW_NO_DROP||LA23_0==KW_OFFLINE||LA23_0==KW_OPTION||(LA23_0 >= KW_OUTPUTDRIVER && LA23_0 <= KW_OUTPUTFORMAT)||(LA23_0 >= KW_OVERWRITE && LA23_0 <= KW_OWNER)||(LA23_0 >= KW_PARTITIONED && LA23_0 <= KW_PARTITIONS)||LA23_0==KW_PLUS||(LA23_0 >= KW_PRETTY && LA23_0 <= KW_PRINCIPALS)||(LA23_0 >= KW_PROTECTION && LA23_0 <= KW_PURGE)||(LA23_0 >= KW_READ && LA23_0 <= KW_READONLY)||(LA23_0 >= KW_REBUILD && LA23_0 <= KW_RECORDWRITER)||(LA23_0 >= KW_REGEXP && LA23_0 <= KW_RESTRICT)||LA23_0==KW_REWRITE||(LA23_0 >= KW_RLIKE && LA23_0 <= KW_ROLES)||(LA23_0 >= KW_SCHEMA && LA23_0 <= KW_SECOND)||(LA23_0 >= KW_SEMI && LA23_0 <= KW_SERVER)||(LA23_0 >= KW_SETS && LA23_0 <= KW_SKEWED)||(LA23_0 >= KW_SORT && LA23_0 <= KW_STRING)||LA23_0==KW_TABLES||(LA23_0 >= KW_TBLPROPERTIES && LA23_0 <= KW_TERMINATED)||LA23_0==KW_TINYINT||(LA23_0 >= KW_TOUCH && LA23_0 <= KW_TRANSACTIONS)||LA23_0==KW_UNARCHIVE||LA23_0==KW_UNDO||(LA23_0 >= KW_UNLOCK && LA23_0 <= KW_UNSIGNED)||(LA23_0 >= KW_URI && LA23_0 <= KW_USE)||(LA23_0 >= KW_UTC && LA23_0 <= KW_UTCTIMESTAMP)||LA23_0==KW_VALUE_TYPE||LA23_0==KW_VIEW||LA23_0==KW_WHILE||LA23_0==KW_YEAR) ) {s = 24;}

                        else if ( ((LA23_0 >= KW_BIGINT && LA23_0 <= KW_BOOLEAN)||LA23_0==KW_DOUBLE||LA23_0==KW_FLOAT||LA23_0==KW_INT||LA23_0==KW_SMALLINT) ) {s = 25;}

                        else if ( (LA23_0==KW_EXISTS) ) {s = 27;}

                        else if ( ((LA23_0 >= KW_ALL && LA23_0 <= KW_ALTER)||LA23_0==KW_AS||LA23_0==KW_AUTHORIZATION||LA23_0==KW_BETWEEN||LA23_0==KW_BOTH||LA23_0==KW_BY||LA23_0==KW_CREATE||LA23_0==KW_CUBE||LA23_0==KW_CURSOR||LA23_0==KW_DECIMAL||LA23_0==KW_DELETE||LA23_0==KW_DESCRIBE||LA23_0==KW_DROP||LA23_0==KW_EXTERNAL||LA23_0==KW_FETCH||LA23_0==KW_FOR||LA23_0==KW_FULL||(LA23_0 >= KW_GRANT && LA23_0 <= KW_GROUPING)||(LA23_0 >= KW_IMPORT && LA23_0 <= KW_IN)||LA23_0==KW_INNER||LA23_0==KW_INSERT||LA23_0==KW_INTERSECT||(LA23_0 >= KW_INTO && LA23_0 <= KW_IS)||(LA23_0 >= KW_LATERAL && LA23_0 <= KW_LEFT)||LA23_0==KW_LIKE||LA23_0==KW_LOCAL||LA23_0==KW_NONE||LA23_0==KW_OF||(LA23_0 >= KW_ORDER && LA23_0 <= KW_OUTER)||LA23_0==KW_PARTITION||LA23_0==KW_PERCENT||LA23_0==KW_PROCEDURE||LA23_0==KW_RANGE||LA23_0==KW_READS||LA23_0==KW_REVOKE||LA23_0==KW_RIGHT||(LA23_0 >= KW_ROLLUP && LA23_0 <= KW_ROWS)||LA23_0==KW_SET||LA23_0==KW_TABLE||LA23_0==KW_TO||LA23_0==KW_TRIGGER||LA23_0==KW_TRUNCATE||LA23_0==KW_UNION||LA23_0==KW_UPDATE||(LA23_0 >= KW_USER && LA23_0 <= KW_USING)||LA23_0==KW_VALUES||LA23_0==KW_WITH) ) {s = 29;}

                         
                        input.seek(index23_0);

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA23_30 = input.LA(1);

                         
                        int index23_30 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA23_30==STAR) && (synpred2_SelectClauseParser())) {s = 424;}

                        else if ( (LA23_30==Identifier) ) {s = 425;}

                        else if ( ((LA23_30 >= KW_ADD && LA23_30 <= KW_AFTER)||LA23_30==KW_ANALYZE||LA23_30==KW_ARCHIVE||LA23_30==KW_ASC||LA23_30==KW_BEFORE||(LA23_30 >= KW_BUCKET && LA23_30 <= KW_BUCKETS)||LA23_30==KW_CASCADE||LA23_30==KW_CHANGE||(LA23_30 >= KW_CLUSTER && LA23_30 <= KW_COLLECTION)||(LA23_30 >= KW_COLUMNS && LA23_30 <= KW_CONCATENATE)||LA23_30==KW_CONTINUE||LA23_30==KW_DATA||LA23_30==KW_DATABASES||(LA23_30 >= KW_DATETIME && LA23_30 <= KW_DBPROPERTIES)||(LA23_30 >= KW_DEFERRED && LA23_30 <= KW_DEFINED)||(LA23_30 >= KW_DELIMITED && LA23_30 <= KW_DESC)||(LA23_30 >= KW_DIRECTORIES && LA23_30 <= KW_DISABLE)||LA23_30==KW_DISTRIBUTE||LA23_30==KW_ELEM_TYPE||LA23_30==KW_ENABLE||LA23_30==KW_ESCAPED||LA23_30==KW_EXCLUSIVE||(LA23_30 >= KW_EXPLAIN && LA23_30 <= KW_EXPORT)||(LA23_30 >= KW_FIELDS && LA23_30 <= KW_FIRST)||(LA23_30 >= KW_FORMAT && LA23_30 <= KW_FORMATTED)||LA23_30==KW_FUNCTIONS||(LA23_30 >= KW_HOLD_DDLTIME && LA23_30 <= KW_IDXPROPERTIES)||LA23_30==KW_IGNORE||(LA23_30 >= KW_INDEX && LA23_30 <= KW_INDEXES)||(LA23_30 >= KW_INPATH && LA23_30 <= KW_INPUTFORMAT)||(LA23_30 >= KW_ITEMS && LA23_30 <= KW_JAR)||(LA23_30 >= KW_KEYS && LA23_30 <= KW_KEY_TYPE)||(LA23_30 >= KW_LIMIT && LA23_30 <= KW_LOAD)||(LA23_30 >= KW_LOCATION && LA23_30 <= KW_LONG)||(LA23_30 >= KW_MAPJOIN && LA23_30 <= KW_MONTH)||LA23_30==KW_MSCK||LA23_30==KW_NOSCAN||LA23_30==KW_NO_DROP||LA23_30==KW_OFFLINE||LA23_30==KW_OPTION||(LA23_30 >= KW_OUTPUTDRIVER && LA23_30 <= KW_OUTPUTFORMAT)||(LA23_30 >= KW_OVERWRITE && LA23_30 <= KW_OWNER)||(LA23_30 >= KW_PARTITIONED && LA23_30 <= KW_PARTITIONS)||LA23_30==KW_PLUS||(LA23_30 >= KW_PRETTY && LA23_30 <= KW_PRINCIPALS)||(LA23_30 >= KW_PROTECTION && LA23_30 <= KW_PURGE)||(LA23_30 >= KW_READ && LA23_30 <= KW_READONLY)||(LA23_30 >= KW_REBUILD && LA23_30 <= KW_RECORDWRITER)||(LA23_30 >= KW_REGEXP && LA23_30 <= KW_RESTRICT)||LA23_30==KW_REWRITE||(LA23_30 >= KW_RLIKE && LA23_30 <= KW_ROLES)||(LA23_30 >= KW_SCHEMA && LA23_30 <= KW_SECOND)||(LA23_30 >= KW_SEMI && LA23_30 <= KW_SERVER)||(LA23_30 >= KW_SETS && LA23_30 <= KW_SKEWED)||(LA23_30 >= KW_SORT && LA23_30 <= KW_STRUCT)||LA23_30==KW_TABLES||(LA23_30 >= KW_TBLPROPERTIES && LA23_30 <= KW_TERMINATED)||LA23_30==KW_TINYINT||(LA23_30 >= KW_TOUCH && LA23_30 <= KW_TRANSACTIONS)||LA23_30==KW_UNARCHIVE||LA23_30==KW_UNDO||LA23_30==KW_UNIONTYPE||(LA23_30 >= KW_UNLOCK && LA23_30 <= KW_UNSIGNED)||(LA23_30 >= KW_URI && LA23_30 <= KW_USE)||(LA23_30 >= KW_UTC && LA23_30 <= KW_UTCTIMESTAMP)||LA23_30==KW_VALUE_TYPE||LA23_30==KW_VIEW||LA23_30==KW_WHILE||LA23_30==KW_YEAR) ) {s = 426;}

                        else if ( ((LA23_30 >= KW_ALL && LA23_30 <= KW_ALTER)||(LA23_30 >= KW_ARRAY && LA23_30 <= KW_AS)||LA23_30==KW_AUTHORIZATION||(LA23_30 >= KW_BETWEEN && LA23_30 <= KW_BOTH)||LA23_30==KW_BY||LA23_30==KW_CREATE||LA23_30==KW_CUBE||(LA23_30 >= KW_CURRENT_DATE && LA23_30 <= KW_CURSOR)||LA23_30==KW_DATE||LA23_30==KW_DECIMAL||LA23_30==KW_DELETE||LA23_30==KW_DESCRIBE||(LA23_30 >= KW_DOUBLE && LA23_30 <= KW_DROP)||LA23_30==KW_EXISTS||(LA23_30 >= KW_EXTERNAL && LA23_30 <= KW_FETCH)||LA23_30==KW_FLOAT||LA23_30==KW_FOR||LA23_30==KW_FULL||(LA23_30 >= KW_GRANT && LA23_30 <= KW_GROUPING)||(LA23_30 >= KW_IMPORT && LA23_30 <= KW_IN)||LA23_30==KW_INNER||(LA23_30 >= KW_INSERT && LA23_30 <= KW_INTERSECT)||(LA23_30 >= KW_INTO && LA23_30 <= KW_IS)||(LA23_30 >= KW_LATERAL && LA23_30 <= KW_LEFT)||LA23_30==KW_LIKE||LA23_30==KW_LOCAL||LA23_30==KW_NONE||(LA23_30 >= KW_NULL && LA23_30 <= KW_OF)||(LA23_30 >= KW_ORDER && LA23_30 <= KW_OUTER)||LA23_30==KW_PARTITION||LA23_30==KW_PERCENT||LA23_30==KW_PROCEDURE||LA23_30==KW_RANGE||LA23_30==KW_READS||LA23_30==KW_REVOKE||LA23_30==KW_RIGHT||(LA23_30 >= KW_ROLLUP && LA23_30 <= KW_ROWS)||LA23_30==KW_SET||LA23_30==KW_SMALLINT||LA23_30==KW_TABLE||LA23_30==KW_TIMESTAMP||LA23_30==KW_TO||(LA23_30 >= KW_TRIGGER && LA23_30 <= KW_TRUNCATE)||LA23_30==KW_UNION||LA23_30==KW_UPDATE||(LA23_30 >= KW_USER && LA23_30 <= KW_USING)||LA23_30==KW_VALUES||LA23_30==KW_WITH) ) {s = 427;}

                         
                        input.seek(index23_30);

                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA23_58 = input.LA(1);

                         
                        int index23_58 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA23_58==STAR) && (synpred2_SelectClauseParser())) {s = 428;}

                        else if ( (LA23_58==Identifier) ) {s = 429;}

                        else if ( ((LA23_58 >= KW_ADD && LA23_58 <= KW_AFTER)||LA23_58==KW_ANALYZE||LA23_58==KW_ARCHIVE||LA23_58==KW_ASC||LA23_58==KW_BEFORE||(LA23_58 >= KW_BUCKET && LA23_58 <= KW_BUCKETS)||LA23_58==KW_CASCADE||LA23_58==KW_CHANGE||(LA23_58 >= KW_CLUSTER && LA23_58 <= KW_COLLECTION)||(LA23_58 >= KW_COLUMNS && LA23_58 <= KW_CONCATENATE)||LA23_58==KW_CONTINUE||LA23_58==KW_DATA||LA23_58==KW_DATABASES||(LA23_58 >= KW_DATETIME && LA23_58 <= KW_DBPROPERTIES)||(LA23_58 >= KW_DEFERRED && LA23_58 <= KW_DEFINED)||(LA23_58 >= KW_DELIMITED && LA23_58 <= KW_DESC)||(LA23_58 >= KW_DIRECTORIES && LA23_58 <= KW_DISABLE)||LA23_58==KW_DISTRIBUTE||LA23_58==KW_ELEM_TYPE||LA23_58==KW_ENABLE||LA23_58==KW_ESCAPED||LA23_58==KW_EXCLUSIVE||(LA23_58 >= KW_EXPLAIN && LA23_58 <= KW_EXPORT)||(LA23_58 >= KW_FIELDS && LA23_58 <= KW_FIRST)||(LA23_58 >= KW_FORMAT && LA23_58 <= KW_FORMATTED)||LA23_58==KW_FUNCTIONS||(LA23_58 >= KW_HOLD_DDLTIME && LA23_58 <= KW_IDXPROPERTIES)||LA23_58==KW_IGNORE||(LA23_58 >= KW_INDEX && LA23_58 <= KW_INDEXES)||(LA23_58 >= KW_INPATH && LA23_58 <= KW_INPUTFORMAT)||(LA23_58 >= KW_ITEMS && LA23_58 <= KW_JAR)||(LA23_58 >= KW_KEYS && LA23_58 <= KW_KEY_TYPE)||(LA23_58 >= KW_LIMIT && LA23_58 <= KW_LOAD)||(LA23_58 >= KW_LOCATION && LA23_58 <= KW_LONG)||(LA23_58 >= KW_MAPJOIN && LA23_58 <= KW_MONTH)||LA23_58==KW_MSCK||LA23_58==KW_NOSCAN||LA23_58==KW_NO_DROP||LA23_58==KW_OFFLINE||LA23_58==KW_OPTION||(LA23_58 >= KW_OUTPUTDRIVER && LA23_58 <= KW_OUTPUTFORMAT)||(LA23_58 >= KW_OVERWRITE && LA23_58 <= KW_OWNER)||(LA23_58 >= KW_PARTITIONED && LA23_58 <= KW_PARTITIONS)||LA23_58==KW_PLUS||(LA23_58 >= KW_PRETTY && LA23_58 <= KW_PRINCIPALS)||(LA23_58 >= KW_PROTECTION && LA23_58 <= KW_PURGE)||(LA23_58 >= KW_READ && LA23_58 <= KW_READONLY)||(LA23_58 >= KW_REBUILD && LA23_58 <= KW_RECORDWRITER)||(LA23_58 >= KW_REGEXP && LA23_58 <= KW_RESTRICT)||LA23_58==KW_REWRITE||(LA23_58 >= KW_RLIKE && LA23_58 <= KW_ROLES)||(LA23_58 >= KW_SCHEMA && LA23_58 <= KW_SECOND)||(LA23_58 >= KW_SEMI && LA23_58 <= KW_SERVER)||(LA23_58 >= KW_SETS && LA23_58 <= KW_SKEWED)||(LA23_58 >= KW_SORT && LA23_58 <= KW_STRUCT)||LA23_58==KW_TABLES||(LA23_58 >= KW_TBLPROPERTIES && LA23_58 <= KW_TERMINATED)||LA23_58==KW_TINYINT||(LA23_58 >= KW_TOUCH && LA23_58 <= KW_TRANSACTIONS)||LA23_58==KW_UNARCHIVE||LA23_58==KW_UNDO||LA23_58==KW_UNIONTYPE||(LA23_58 >= KW_UNLOCK && LA23_58 <= KW_UNSIGNED)||(LA23_58 >= KW_URI && LA23_58 <= KW_USE)||(LA23_58 >= KW_UTC && LA23_58 <= KW_UTCTIMESTAMP)||LA23_58==KW_VALUE_TYPE||LA23_58==KW_VIEW||LA23_58==KW_WHILE||LA23_58==KW_YEAR) ) {s = 430;}

                        else if ( ((LA23_58 >= KW_ALL && LA23_58 <= KW_ALTER)||(LA23_58 >= KW_ARRAY && LA23_58 <= KW_AS)||LA23_58==KW_AUTHORIZATION||(LA23_58 >= KW_BETWEEN && LA23_58 <= KW_BOTH)||LA23_58==KW_BY||LA23_58==KW_CREATE||LA23_58==KW_CUBE||(LA23_58 >= KW_CURRENT_DATE && LA23_58 <= KW_CURSOR)||LA23_58==KW_DATE||LA23_58==KW_DECIMAL||LA23_58==KW_DELETE||LA23_58==KW_DESCRIBE||(LA23_58 >= KW_DOUBLE && LA23_58 <= KW_DROP)||LA23_58==KW_EXISTS||(LA23_58 >= KW_EXTERNAL && LA23_58 <= KW_FETCH)||LA23_58==KW_FLOAT||LA23_58==KW_FOR||LA23_58==KW_FULL||(LA23_58 >= KW_GRANT && LA23_58 <= KW_GROUPING)||(LA23_58 >= KW_IMPORT && LA23_58 <= KW_IN)||LA23_58==KW_INNER||(LA23_58 >= KW_INSERT && LA23_58 <= KW_INTERSECT)||(LA23_58 >= KW_INTO && LA23_58 <= KW_IS)||(LA23_58 >= KW_LATERAL && LA23_58 <= KW_LEFT)||LA23_58==KW_LIKE||LA23_58==KW_LOCAL||LA23_58==KW_NONE||(LA23_58 >= KW_NULL && LA23_58 <= KW_OF)||(LA23_58 >= KW_ORDER && LA23_58 <= KW_OUTER)||LA23_58==KW_PARTITION||LA23_58==KW_PERCENT||LA23_58==KW_PROCEDURE||LA23_58==KW_RANGE||LA23_58==KW_READS||LA23_58==KW_REVOKE||LA23_58==KW_RIGHT||(LA23_58 >= KW_ROLLUP && LA23_58 <= KW_ROWS)||LA23_58==KW_SET||LA23_58==KW_SMALLINT||LA23_58==KW_TABLE||LA23_58==KW_TIMESTAMP||LA23_58==KW_TO||(LA23_58 >= KW_TRIGGER && LA23_58 <= KW_TRUNCATE)||LA23_58==KW_UNION||LA23_58==KW_UPDATE||(LA23_58 >= KW_USER && LA23_58 <= KW_USING)||LA23_58==KW_VALUES||LA23_58==KW_WITH) ) {s = 431;}

                         
                        input.seek(index23_58);

                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA23_86 = input.LA(1);

                         
                        int index23_86 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA23_86==STAR) && (synpred2_SelectClauseParser())) {s = 432;}

                        else if ( (LA23_86==Identifier) ) {s = 433;}

                        else if ( ((LA23_86 >= KW_ADD && LA23_86 <= KW_AFTER)||LA23_86==KW_ANALYZE||LA23_86==KW_ARCHIVE||LA23_86==KW_ASC||LA23_86==KW_BEFORE||(LA23_86 >= KW_BUCKET && LA23_86 <= KW_BUCKETS)||LA23_86==KW_CASCADE||LA23_86==KW_CHANGE||(LA23_86 >= KW_CLUSTER && LA23_86 <= KW_COLLECTION)||(LA23_86 >= KW_COLUMNS && LA23_86 <= KW_CONCATENATE)||LA23_86==KW_CONTINUE||LA23_86==KW_DATA||LA23_86==KW_DATABASES||(LA23_86 >= KW_DATETIME && LA23_86 <= KW_DBPROPERTIES)||(LA23_86 >= KW_DEFERRED && LA23_86 <= KW_DEFINED)||(LA23_86 >= KW_DELIMITED && LA23_86 <= KW_DESC)||(LA23_86 >= KW_DIRECTORIES && LA23_86 <= KW_DISABLE)||LA23_86==KW_DISTRIBUTE||LA23_86==KW_ELEM_TYPE||LA23_86==KW_ENABLE||LA23_86==KW_ESCAPED||LA23_86==KW_EXCLUSIVE||(LA23_86 >= KW_EXPLAIN && LA23_86 <= KW_EXPORT)||(LA23_86 >= KW_FIELDS && LA23_86 <= KW_FIRST)||(LA23_86 >= KW_FORMAT && LA23_86 <= KW_FORMATTED)||LA23_86==KW_FUNCTIONS||(LA23_86 >= KW_HOLD_DDLTIME && LA23_86 <= KW_IDXPROPERTIES)||LA23_86==KW_IGNORE||(LA23_86 >= KW_INDEX && LA23_86 <= KW_INDEXES)||(LA23_86 >= KW_INPATH && LA23_86 <= KW_INPUTFORMAT)||(LA23_86 >= KW_ITEMS && LA23_86 <= KW_JAR)||(LA23_86 >= KW_KEYS && LA23_86 <= KW_KEY_TYPE)||(LA23_86 >= KW_LIMIT && LA23_86 <= KW_LOAD)||(LA23_86 >= KW_LOCATION && LA23_86 <= KW_LONG)||(LA23_86 >= KW_MAPJOIN && LA23_86 <= KW_MONTH)||LA23_86==KW_MSCK||LA23_86==KW_NOSCAN||LA23_86==KW_NO_DROP||LA23_86==KW_OFFLINE||LA23_86==KW_OPTION||(LA23_86 >= KW_OUTPUTDRIVER && LA23_86 <= KW_OUTPUTFORMAT)||(LA23_86 >= KW_OVERWRITE && LA23_86 <= KW_OWNER)||(LA23_86 >= KW_PARTITIONED && LA23_86 <= KW_PARTITIONS)||LA23_86==KW_PLUS||(LA23_86 >= KW_PRETTY && LA23_86 <= KW_PRINCIPALS)||(LA23_86 >= KW_PROTECTION && LA23_86 <= KW_PURGE)||(LA23_86 >= KW_READ && LA23_86 <= KW_READONLY)||(LA23_86 >= KW_REBUILD && LA23_86 <= KW_RECORDWRITER)||(LA23_86 >= KW_REGEXP && LA23_86 <= KW_RESTRICT)||LA23_86==KW_REWRITE||(LA23_86 >= KW_RLIKE && LA23_86 <= KW_ROLES)||(LA23_86 >= KW_SCHEMA && LA23_86 <= KW_SECOND)||(LA23_86 >= KW_SEMI && LA23_86 <= KW_SERVER)||(LA23_86 >= KW_SETS && LA23_86 <= KW_SKEWED)||(LA23_86 >= KW_SORT && LA23_86 <= KW_STRUCT)||LA23_86==KW_TABLES||(LA23_86 >= KW_TBLPROPERTIES && LA23_86 <= KW_TERMINATED)||LA23_86==KW_TINYINT||(LA23_86 >= KW_TOUCH && LA23_86 <= KW_TRANSACTIONS)||LA23_86==KW_UNARCHIVE||LA23_86==KW_UNDO||LA23_86==KW_UNIONTYPE||(LA23_86 >= KW_UNLOCK && LA23_86 <= KW_UNSIGNED)||(LA23_86 >= KW_URI && LA23_86 <= KW_USE)||(LA23_86 >= KW_UTC && LA23_86 <= KW_UTCTIMESTAMP)||LA23_86==KW_VALUE_TYPE||LA23_86==KW_VIEW||LA23_86==KW_WHILE||LA23_86==KW_YEAR) ) {s = 434;}

                        else if ( ((LA23_86 >= KW_ALL && LA23_86 <= KW_ALTER)||(LA23_86 >= KW_ARRAY && LA23_86 <= KW_AS)||LA23_86==KW_AUTHORIZATION||(LA23_86 >= KW_BETWEEN && LA23_86 <= KW_BOTH)||LA23_86==KW_BY||LA23_86==KW_CREATE||LA23_86==KW_CUBE||(LA23_86 >= KW_CURRENT_DATE && LA23_86 <= KW_CURSOR)||LA23_86==KW_DATE||LA23_86==KW_DECIMAL||LA23_86==KW_DELETE||LA23_86==KW_DESCRIBE||(LA23_86 >= KW_DOUBLE && LA23_86 <= KW_DROP)||LA23_86==KW_EXISTS||(LA23_86 >= KW_EXTERNAL && LA23_86 <= KW_FETCH)||LA23_86==KW_FLOAT||LA23_86==KW_FOR||LA23_86==KW_FULL||(LA23_86 >= KW_GRANT && LA23_86 <= KW_GROUPING)||(LA23_86 >= KW_IMPORT && LA23_86 <= KW_IN)||LA23_86==KW_INNER||(LA23_86 >= KW_INSERT && LA23_86 <= KW_INTERSECT)||(LA23_86 >= KW_INTO && LA23_86 <= KW_IS)||(LA23_86 >= KW_LATERAL && LA23_86 <= KW_LEFT)||LA23_86==KW_LIKE||LA23_86==KW_LOCAL||LA23_86==KW_NONE||(LA23_86 >= KW_NULL && LA23_86 <= KW_OF)||(LA23_86 >= KW_ORDER && LA23_86 <= KW_OUTER)||LA23_86==KW_PARTITION||LA23_86==KW_PERCENT||LA23_86==KW_PROCEDURE||LA23_86==KW_RANGE||LA23_86==KW_READS||LA23_86==KW_REVOKE||LA23_86==KW_RIGHT||(LA23_86 >= KW_ROLLUP && LA23_86 <= KW_ROWS)||LA23_86==KW_SET||LA23_86==KW_SMALLINT||LA23_86==KW_TABLE||LA23_86==KW_TIMESTAMP||LA23_86==KW_TO||(LA23_86 >= KW_TRIGGER && LA23_86 <= KW_TRUNCATE)||LA23_86==KW_UNION||LA23_86==KW_UPDATE||(LA23_86 >= KW_USER && LA23_86 <= KW_USING)||LA23_86==KW_VALUES||LA23_86==KW_WITH) ) {s = 435;}

                         
                        input.seek(index23_86);

                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA23_115 = input.LA(1);

                         
                        int index23_115 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA23_115==STAR) && (synpred2_SelectClauseParser())) {s = 436;}

                        else if ( (LA23_115==Identifier) ) {s = 437;}

                        else if ( ((LA23_115 >= KW_ADD && LA23_115 <= KW_AFTER)||LA23_115==KW_ANALYZE||LA23_115==KW_ARCHIVE||LA23_115==KW_ASC||LA23_115==KW_BEFORE||(LA23_115 >= KW_BUCKET && LA23_115 <= KW_BUCKETS)||LA23_115==KW_CASCADE||LA23_115==KW_CHANGE||(LA23_115 >= KW_CLUSTER && LA23_115 <= KW_COLLECTION)||(LA23_115 >= KW_COLUMNS && LA23_115 <= KW_CONCATENATE)||LA23_115==KW_CONTINUE||LA23_115==KW_DATA||LA23_115==KW_DATABASES||(LA23_115 >= KW_DATETIME && LA23_115 <= KW_DBPROPERTIES)||(LA23_115 >= KW_DEFERRED && LA23_115 <= KW_DEFINED)||(LA23_115 >= KW_DELIMITED && LA23_115 <= KW_DESC)||(LA23_115 >= KW_DIRECTORIES && LA23_115 <= KW_DISABLE)||LA23_115==KW_DISTRIBUTE||LA23_115==KW_ELEM_TYPE||LA23_115==KW_ENABLE||LA23_115==KW_ESCAPED||LA23_115==KW_EXCLUSIVE||(LA23_115 >= KW_EXPLAIN && LA23_115 <= KW_EXPORT)||(LA23_115 >= KW_FIELDS && LA23_115 <= KW_FIRST)||(LA23_115 >= KW_FORMAT && LA23_115 <= KW_FORMATTED)||LA23_115==KW_FUNCTIONS||(LA23_115 >= KW_HOLD_DDLTIME && LA23_115 <= KW_IDXPROPERTIES)||LA23_115==KW_IGNORE||(LA23_115 >= KW_INDEX && LA23_115 <= KW_INDEXES)||(LA23_115 >= KW_INPATH && LA23_115 <= KW_INPUTFORMAT)||(LA23_115 >= KW_ITEMS && LA23_115 <= KW_JAR)||(LA23_115 >= KW_KEYS && LA23_115 <= KW_KEY_TYPE)||(LA23_115 >= KW_LIMIT && LA23_115 <= KW_LOAD)||(LA23_115 >= KW_LOCATION && LA23_115 <= KW_LONG)||(LA23_115 >= KW_MAPJOIN && LA23_115 <= KW_MONTH)||LA23_115==KW_MSCK||LA23_115==KW_NOSCAN||LA23_115==KW_NO_DROP||LA23_115==KW_OFFLINE||LA23_115==KW_OPTION||(LA23_115 >= KW_OUTPUTDRIVER && LA23_115 <= KW_OUTPUTFORMAT)||(LA23_115 >= KW_OVERWRITE && LA23_115 <= KW_OWNER)||(LA23_115 >= KW_PARTITIONED && LA23_115 <= KW_PARTITIONS)||LA23_115==KW_PLUS||(LA23_115 >= KW_PRETTY && LA23_115 <= KW_PRINCIPALS)||(LA23_115 >= KW_PROTECTION && LA23_115 <= KW_PURGE)||(LA23_115 >= KW_READ && LA23_115 <= KW_READONLY)||(LA23_115 >= KW_REBUILD && LA23_115 <= KW_RECORDWRITER)||(LA23_115 >= KW_REGEXP && LA23_115 <= KW_RESTRICT)||LA23_115==KW_REWRITE||(LA23_115 >= KW_RLIKE && LA23_115 <= KW_ROLES)||(LA23_115 >= KW_SCHEMA && LA23_115 <= KW_SECOND)||(LA23_115 >= KW_SEMI && LA23_115 <= KW_SERVER)||(LA23_115 >= KW_SETS && LA23_115 <= KW_SKEWED)||(LA23_115 >= KW_SORT && LA23_115 <= KW_STRUCT)||LA23_115==KW_TABLES||(LA23_115 >= KW_TBLPROPERTIES && LA23_115 <= KW_TERMINATED)||LA23_115==KW_TINYINT||(LA23_115 >= KW_TOUCH && LA23_115 <= KW_TRANSACTIONS)||LA23_115==KW_UNARCHIVE||LA23_115==KW_UNDO||LA23_115==KW_UNIONTYPE||(LA23_115 >= KW_UNLOCK && LA23_115 <= KW_UNSIGNED)||(LA23_115 >= KW_URI && LA23_115 <= KW_USE)||(LA23_115 >= KW_UTC && LA23_115 <= KW_UTCTIMESTAMP)||LA23_115==KW_VALUE_TYPE||LA23_115==KW_VIEW||LA23_115==KW_WHILE||LA23_115==KW_YEAR) ) {s = 438;}

                        else if ( ((LA23_115 >= KW_ALL && LA23_115 <= KW_ALTER)||(LA23_115 >= KW_ARRAY && LA23_115 <= KW_AS)||LA23_115==KW_AUTHORIZATION||(LA23_115 >= KW_BETWEEN && LA23_115 <= KW_BOTH)||LA23_115==KW_BY||LA23_115==KW_CREATE||LA23_115==KW_CUBE||(LA23_115 >= KW_CURRENT_DATE && LA23_115 <= KW_CURSOR)||LA23_115==KW_DATE||LA23_115==KW_DECIMAL||LA23_115==KW_DELETE||LA23_115==KW_DESCRIBE||(LA23_115 >= KW_DOUBLE && LA23_115 <= KW_DROP)||LA23_115==KW_EXISTS||(LA23_115 >= KW_EXTERNAL && LA23_115 <= KW_FETCH)||LA23_115==KW_FLOAT||LA23_115==KW_FOR||LA23_115==KW_FULL||(LA23_115 >= KW_GRANT && LA23_115 <= KW_GROUPING)||(LA23_115 >= KW_IMPORT && LA23_115 <= KW_IN)||LA23_115==KW_INNER||(LA23_115 >= KW_INSERT && LA23_115 <= KW_INTERSECT)||(LA23_115 >= KW_INTO && LA23_115 <= KW_IS)||(LA23_115 >= KW_LATERAL && LA23_115 <= KW_LEFT)||LA23_115==KW_LIKE||LA23_115==KW_LOCAL||LA23_115==KW_NONE||(LA23_115 >= KW_NULL && LA23_115 <= KW_OF)||(LA23_115 >= KW_ORDER && LA23_115 <= KW_OUTER)||LA23_115==KW_PARTITION||LA23_115==KW_PERCENT||LA23_115==KW_PROCEDURE||LA23_115==KW_RANGE||LA23_115==KW_READS||LA23_115==KW_REVOKE||LA23_115==KW_RIGHT||(LA23_115 >= KW_ROLLUP && LA23_115 <= KW_ROWS)||LA23_115==KW_SET||LA23_115==KW_SMALLINT||LA23_115==KW_TABLE||LA23_115==KW_TIMESTAMP||LA23_115==KW_TO||(LA23_115 >= KW_TRIGGER && LA23_115 <= KW_TRUNCATE)||LA23_115==KW_UNION||LA23_115==KW_UPDATE||(LA23_115 >= KW_USER && LA23_115 <= KW_USING)||LA23_115==KW_VALUES||LA23_115==KW_WITH) ) {s = 439;}

                         
                        input.seek(index23_115);

                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA23_143 = input.LA(1);

                         
                        int index23_143 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA23_143==STAR) && (synpred2_SelectClauseParser())) {s = 440;}

                        else if ( (LA23_143==Identifier) ) {s = 441;}

                        else if ( ((LA23_143 >= KW_ADD && LA23_143 <= KW_AFTER)||LA23_143==KW_ANALYZE||LA23_143==KW_ARCHIVE||LA23_143==KW_ASC||LA23_143==KW_BEFORE||(LA23_143 >= KW_BUCKET && LA23_143 <= KW_BUCKETS)||LA23_143==KW_CASCADE||LA23_143==KW_CHANGE||(LA23_143 >= KW_CLUSTER && LA23_143 <= KW_COLLECTION)||(LA23_143 >= KW_COLUMNS && LA23_143 <= KW_CONCATENATE)||LA23_143==KW_CONTINUE||LA23_143==KW_DATA||LA23_143==KW_DATABASES||(LA23_143 >= KW_DATETIME && LA23_143 <= KW_DBPROPERTIES)||(LA23_143 >= KW_DEFERRED && LA23_143 <= KW_DEFINED)||(LA23_143 >= KW_DELIMITED && LA23_143 <= KW_DESC)||(LA23_143 >= KW_DIRECTORIES && LA23_143 <= KW_DISABLE)||LA23_143==KW_DISTRIBUTE||LA23_143==KW_ELEM_TYPE||LA23_143==KW_ENABLE||LA23_143==KW_ESCAPED||LA23_143==KW_EXCLUSIVE||(LA23_143 >= KW_EXPLAIN && LA23_143 <= KW_EXPORT)||(LA23_143 >= KW_FIELDS && LA23_143 <= KW_FIRST)||(LA23_143 >= KW_FORMAT && LA23_143 <= KW_FORMATTED)||LA23_143==KW_FUNCTIONS||(LA23_143 >= KW_HOLD_DDLTIME && LA23_143 <= KW_IDXPROPERTIES)||LA23_143==KW_IGNORE||(LA23_143 >= KW_INDEX && LA23_143 <= KW_INDEXES)||(LA23_143 >= KW_INPATH && LA23_143 <= KW_INPUTFORMAT)||(LA23_143 >= KW_ITEMS && LA23_143 <= KW_JAR)||(LA23_143 >= KW_KEYS && LA23_143 <= KW_KEY_TYPE)||(LA23_143 >= KW_LIMIT && LA23_143 <= KW_LOAD)||(LA23_143 >= KW_LOCATION && LA23_143 <= KW_LONG)||(LA23_143 >= KW_MAPJOIN && LA23_143 <= KW_MONTH)||LA23_143==KW_MSCK||LA23_143==KW_NOSCAN||LA23_143==KW_NO_DROP||LA23_143==KW_OFFLINE||LA23_143==KW_OPTION||(LA23_143 >= KW_OUTPUTDRIVER && LA23_143 <= KW_OUTPUTFORMAT)||(LA23_143 >= KW_OVERWRITE && LA23_143 <= KW_OWNER)||(LA23_143 >= KW_PARTITIONED && LA23_143 <= KW_PARTITIONS)||LA23_143==KW_PLUS||(LA23_143 >= KW_PRETTY && LA23_143 <= KW_PRINCIPALS)||(LA23_143 >= KW_PROTECTION && LA23_143 <= KW_PURGE)||(LA23_143 >= KW_READ && LA23_143 <= KW_READONLY)||(LA23_143 >= KW_REBUILD && LA23_143 <= KW_RECORDWRITER)||(LA23_143 >= KW_REGEXP && LA23_143 <= KW_RESTRICT)||LA23_143==KW_REWRITE||(LA23_143 >= KW_RLIKE && LA23_143 <= KW_ROLES)||(LA23_143 >= KW_SCHEMA && LA23_143 <= KW_SECOND)||(LA23_143 >= KW_SEMI && LA23_143 <= KW_SERVER)||(LA23_143 >= KW_SETS && LA23_143 <= KW_SKEWED)||(LA23_143 >= KW_SORT && LA23_143 <= KW_STRUCT)||LA23_143==KW_TABLES||(LA23_143 >= KW_TBLPROPERTIES && LA23_143 <= KW_TERMINATED)||LA23_143==KW_TINYINT||(LA23_143 >= KW_TOUCH && LA23_143 <= KW_TRANSACTIONS)||LA23_143==KW_UNARCHIVE||LA23_143==KW_UNDO||LA23_143==KW_UNIONTYPE||(LA23_143 >= KW_UNLOCK && LA23_143 <= KW_UNSIGNED)||(LA23_143 >= KW_URI && LA23_143 <= KW_USE)||(LA23_143 >= KW_UTC && LA23_143 <= KW_UTCTIMESTAMP)||LA23_143==KW_VALUE_TYPE||LA23_143==KW_VIEW||LA23_143==KW_WHILE||LA23_143==KW_YEAR) ) {s = 442;}

                        else if ( ((LA23_143 >= KW_ALL && LA23_143 <= KW_ALTER)||(LA23_143 >= KW_ARRAY && LA23_143 <= KW_AS)||LA23_143==KW_AUTHORIZATION||(LA23_143 >= KW_BETWEEN && LA23_143 <= KW_BOTH)||LA23_143==KW_BY||LA23_143==KW_CREATE||LA23_143==KW_CUBE||(LA23_143 >= KW_CURRENT_DATE && LA23_143 <= KW_CURSOR)||LA23_143==KW_DATE||LA23_143==KW_DECIMAL||LA23_143==KW_DELETE||LA23_143==KW_DESCRIBE||(LA23_143 >= KW_DOUBLE && LA23_143 <= KW_DROP)||LA23_143==KW_EXISTS||(LA23_143 >= KW_EXTERNAL && LA23_143 <= KW_FETCH)||LA23_143==KW_FLOAT||LA23_143==KW_FOR||LA23_143==KW_FULL||(LA23_143 >= KW_GRANT && LA23_143 <= KW_GROUPING)||(LA23_143 >= KW_IMPORT && LA23_143 <= KW_IN)||LA23_143==KW_INNER||(LA23_143 >= KW_INSERT && LA23_143 <= KW_INTERSECT)||(LA23_143 >= KW_INTO && LA23_143 <= KW_IS)||(LA23_143 >= KW_LATERAL && LA23_143 <= KW_LEFT)||LA23_143==KW_LIKE||LA23_143==KW_LOCAL||LA23_143==KW_NONE||(LA23_143 >= KW_NULL && LA23_143 <= KW_OF)||(LA23_143 >= KW_ORDER && LA23_143 <= KW_OUTER)||LA23_143==KW_PARTITION||LA23_143==KW_PERCENT||LA23_143==KW_PROCEDURE||LA23_143==KW_RANGE||LA23_143==KW_READS||LA23_143==KW_REVOKE||LA23_143==KW_RIGHT||(LA23_143 >= KW_ROLLUP && LA23_143 <= KW_ROWS)||LA23_143==KW_SET||LA23_143==KW_SMALLINT||LA23_143==KW_TABLE||LA23_143==KW_TIMESTAMP||LA23_143==KW_TO||(LA23_143 >= KW_TRIGGER && LA23_143 <= KW_TRUNCATE)||LA23_143==KW_UNION||LA23_143==KW_UPDATE||(LA23_143 >= KW_USER && LA23_143 <= KW_USING)||LA23_143==KW_VALUES||LA23_143==KW_WITH) ) {s = 443;}

                         
                        input.seek(index23_143);

                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA23_172 = input.LA(1);

                         
                        int index23_172 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA23_172==STAR) && (synpred2_SelectClauseParser())) {s = 444;}

                        else if ( (LA23_172==Identifier) ) {s = 445;}

                        else if ( ((LA23_172 >= KW_ADD && LA23_172 <= KW_AFTER)||LA23_172==KW_ANALYZE||LA23_172==KW_ARCHIVE||LA23_172==KW_ASC||LA23_172==KW_BEFORE||(LA23_172 >= KW_BUCKET && LA23_172 <= KW_BUCKETS)||LA23_172==KW_CASCADE||LA23_172==KW_CHANGE||(LA23_172 >= KW_CLUSTER && LA23_172 <= KW_COLLECTION)||(LA23_172 >= KW_COLUMNS && LA23_172 <= KW_CONCATENATE)||LA23_172==KW_CONTINUE||LA23_172==KW_DATA||LA23_172==KW_DATABASES||(LA23_172 >= KW_DATETIME && LA23_172 <= KW_DBPROPERTIES)||(LA23_172 >= KW_DEFERRED && LA23_172 <= KW_DEFINED)||(LA23_172 >= KW_DELIMITED && LA23_172 <= KW_DESC)||(LA23_172 >= KW_DIRECTORIES && LA23_172 <= KW_DISABLE)||LA23_172==KW_DISTRIBUTE||LA23_172==KW_ELEM_TYPE||LA23_172==KW_ENABLE||LA23_172==KW_ESCAPED||LA23_172==KW_EXCLUSIVE||(LA23_172 >= KW_EXPLAIN && LA23_172 <= KW_EXPORT)||(LA23_172 >= KW_FIELDS && LA23_172 <= KW_FIRST)||(LA23_172 >= KW_FORMAT && LA23_172 <= KW_FORMATTED)||LA23_172==KW_FUNCTIONS||(LA23_172 >= KW_HOLD_DDLTIME && LA23_172 <= KW_IDXPROPERTIES)||LA23_172==KW_IGNORE||(LA23_172 >= KW_INDEX && LA23_172 <= KW_INDEXES)||(LA23_172 >= KW_INPATH && LA23_172 <= KW_INPUTFORMAT)||(LA23_172 >= KW_ITEMS && LA23_172 <= KW_JAR)||(LA23_172 >= KW_KEYS && LA23_172 <= KW_KEY_TYPE)||(LA23_172 >= KW_LIMIT && LA23_172 <= KW_LOAD)||(LA23_172 >= KW_LOCATION && LA23_172 <= KW_LONG)||(LA23_172 >= KW_MAPJOIN && LA23_172 <= KW_MONTH)||LA23_172==KW_MSCK||LA23_172==KW_NOSCAN||LA23_172==KW_NO_DROP||LA23_172==KW_OFFLINE||LA23_172==KW_OPTION||(LA23_172 >= KW_OUTPUTDRIVER && LA23_172 <= KW_OUTPUTFORMAT)||(LA23_172 >= KW_OVERWRITE && LA23_172 <= KW_OWNER)||(LA23_172 >= KW_PARTITIONED && LA23_172 <= KW_PARTITIONS)||LA23_172==KW_PLUS||(LA23_172 >= KW_PRETTY && LA23_172 <= KW_PRINCIPALS)||(LA23_172 >= KW_PROTECTION && LA23_172 <= KW_PURGE)||(LA23_172 >= KW_READ && LA23_172 <= KW_READONLY)||(LA23_172 >= KW_REBUILD && LA23_172 <= KW_RECORDWRITER)||(LA23_172 >= KW_REGEXP && LA23_172 <= KW_RESTRICT)||LA23_172==KW_REWRITE||(LA23_172 >= KW_RLIKE && LA23_172 <= KW_ROLES)||(LA23_172 >= KW_SCHEMA && LA23_172 <= KW_SECOND)||(LA23_172 >= KW_SEMI && LA23_172 <= KW_SERVER)||(LA23_172 >= KW_SETS && LA23_172 <= KW_SKEWED)||(LA23_172 >= KW_SORT && LA23_172 <= KW_STRUCT)||LA23_172==KW_TABLES||(LA23_172 >= KW_TBLPROPERTIES && LA23_172 <= KW_TERMINATED)||LA23_172==KW_TINYINT||(LA23_172 >= KW_TOUCH && LA23_172 <= KW_TRANSACTIONS)||LA23_172==KW_UNARCHIVE||LA23_172==KW_UNDO||LA23_172==KW_UNIONTYPE||(LA23_172 >= KW_UNLOCK && LA23_172 <= KW_UNSIGNED)||(LA23_172 >= KW_URI && LA23_172 <= KW_USE)||(LA23_172 >= KW_UTC && LA23_172 <= KW_UTCTIMESTAMP)||LA23_172==KW_VALUE_TYPE||LA23_172==KW_VIEW||LA23_172==KW_WHILE||LA23_172==KW_YEAR) ) {s = 446;}

                        else if ( ((LA23_172 >= KW_ALL && LA23_172 <= KW_ALTER)||(LA23_172 >= KW_ARRAY && LA23_172 <= KW_AS)||LA23_172==KW_AUTHORIZATION||(LA23_172 >= KW_BETWEEN && LA23_172 <= KW_BOTH)||LA23_172==KW_BY||LA23_172==KW_CREATE||LA23_172==KW_CUBE||(LA23_172 >= KW_CURRENT_DATE && LA23_172 <= KW_CURSOR)||LA23_172==KW_DATE||LA23_172==KW_DECIMAL||LA23_172==KW_DELETE||LA23_172==KW_DESCRIBE||(LA23_172 >= KW_DOUBLE && LA23_172 <= KW_DROP)||LA23_172==KW_EXISTS||(LA23_172 >= KW_EXTERNAL && LA23_172 <= KW_FETCH)||LA23_172==KW_FLOAT||LA23_172==KW_FOR||LA23_172==KW_FULL||(LA23_172 >= KW_GRANT && LA23_172 <= KW_GROUPING)||(LA23_172 >= KW_IMPORT && LA23_172 <= KW_IN)||LA23_172==KW_INNER||(LA23_172 >= KW_INSERT && LA23_172 <= KW_INTERSECT)||(LA23_172 >= KW_INTO && LA23_172 <= KW_IS)||(LA23_172 >= KW_LATERAL && LA23_172 <= KW_LEFT)||LA23_172==KW_LIKE||LA23_172==KW_LOCAL||LA23_172==KW_NONE||(LA23_172 >= KW_NULL && LA23_172 <= KW_OF)||(LA23_172 >= KW_ORDER && LA23_172 <= KW_OUTER)||LA23_172==KW_PARTITION||LA23_172==KW_PERCENT||LA23_172==KW_PROCEDURE||LA23_172==KW_RANGE||LA23_172==KW_READS||LA23_172==KW_REVOKE||LA23_172==KW_RIGHT||(LA23_172 >= KW_ROLLUP && LA23_172 <= KW_ROWS)||LA23_172==KW_SET||LA23_172==KW_SMALLINT||LA23_172==KW_TABLE||LA23_172==KW_TIMESTAMP||LA23_172==KW_TO||(LA23_172 >= KW_TRIGGER && LA23_172 <= KW_TRUNCATE)||LA23_172==KW_UNION||LA23_172==KW_UPDATE||(LA23_172 >= KW_USER && LA23_172 <= KW_USING)||LA23_172==KW_VALUES||LA23_172==KW_WITH) ) {s = 447;}

                         
                        input.seek(index23_172);

                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA23_200 = input.LA(1);

                         
                        int index23_200 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA23_200==STAR) && (synpred2_SelectClauseParser())) {s = 448;}

                        else if ( (LA23_200==Identifier) ) {s = 449;}

                        else if ( ((LA23_200 >= KW_ADD && LA23_200 <= KW_AFTER)||LA23_200==KW_ANALYZE||LA23_200==KW_ARCHIVE||LA23_200==KW_ASC||LA23_200==KW_BEFORE||(LA23_200 >= KW_BUCKET && LA23_200 <= KW_BUCKETS)||LA23_200==KW_CASCADE||LA23_200==KW_CHANGE||(LA23_200 >= KW_CLUSTER && LA23_200 <= KW_COLLECTION)||(LA23_200 >= KW_COLUMNS && LA23_200 <= KW_CONCATENATE)||LA23_200==KW_CONTINUE||LA23_200==KW_DATA||LA23_200==KW_DATABASES||(LA23_200 >= KW_DATETIME && LA23_200 <= KW_DBPROPERTIES)||(LA23_200 >= KW_DEFERRED && LA23_200 <= KW_DEFINED)||(LA23_200 >= KW_DELIMITED && LA23_200 <= KW_DESC)||(LA23_200 >= KW_DIRECTORIES && LA23_200 <= KW_DISABLE)||LA23_200==KW_DISTRIBUTE||LA23_200==KW_ELEM_TYPE||LA23_200==KW_ENABLE||LA23_200==KW_ESCAPED||LA23_200==KW_EXCLUSIVE||(LA23_200 >= KW_EXPLAIN && LA23_200 <= KW_EXPORT)||(LA23_200 >= KW_FIELDS && LA23_200 <= KW_FIRST)||(LA23_200 >= KW_FORMAT && LA23_200 <= KW_FORMATTED)||LA23_200==KW_FUNCTIONS||(LA23_200 >= KW_HOLD_DDLTIME && LA23_200 <= KW_IDXPROPERTIES)||LA23_200==KW_IGNORE||(LA23_200 >= KW_INDEX && LA23_200 <= KW_INDEXES)||(LA23_200 >= KW_INPATH && LA23_200 <= KW_INPUTFORMAT)||(LA23_200 >= KW_ITEMS && LA23_200 <= KW_JAR)||(LA23_200 >= KW_KEYS && LA23_200 <= KW_KEY_TYPE)||(LA23_200 >= KW_LIMIT && LA23_200 <= KW_LOAD)||(LA23_200 >= KW_LOCATION && LA23_200 <= KW_LONG)||(LA23_200 >= KW_MAPJOIN && LA23_200 <= KW_MONTH)||LA23_200==KW_MSCK||LA23_200==KW_NOSCAN||LA23_200==KW_NO_DROP||LA23_200==KW_OFFLINE||LA23_200==KW_OPTION||(LA23_200 >= KW_OUTPUTDRIVER && LA23_200 <= KW_OUTPUTFORMAT)||(LA23_200 >= KW_OVERWRITE && LA23_200 <= KW_OWNER)||(LA23_200 >= KW_PARTITIONED && LA23_200 <= KW_PARTITIONS)||LA23_200==KW_PLUS||(LA23_200 >= KW_PRETTY && LA23_200 <= KW_PRINCIPALS)||(LA23_200 >= KW_PROTECTION && LA23_200 <= KW_PURGE)||(LA23_200 >= KW_READ && LA23_200 <= KW_READONLY)||(LA23_200 >= KW_REBUILD && LA23_200 <= KW_RECORDWRITER)||(LA23_200 >= KW_REGEXP && LA23_200 <= KW_RESTRICT)||LA23_200==KW_REWRITE||(LA23_200 >= KW_RLIKE && LA23_200 <= KW_ROLES)||(LA23_200 >= KW_SCHEMA && LA23_200 <= KW_SECOND)||(LA23_200 >= KW_SEMI && LA23_200 <= KW_SERVER)||(LA23_200 >= KW_SETS && LA23_200 <= KW_SKEWED)||(LA23_200 >= KW_SORT && LA23_200 <= KW_STRUCT)||LA23_200==KW_TABLES||(LA23_200 >= KW_TBLPROPERTIES && LA23_200 <= KW_TERMINATED)||LA23_200==KW_TINYINT||(LA23_200 >= KW_TOUCH && LA23_200 <= KW_TRANSACTIONS)||LA23_200==KW_UNARCHIVE||LA23_200==KW_UNDO||LA23_200==KW_UNIONTYPE||(LA23_200 >= KW_UNLOCK && LA23_200 <= KW_UNSIGNED)||(LA23_200 >= KW_URI && LA23_200 <= KW_USE)||(LA23_200 >= KW_UTC && LA23_200 <= KW_UTCTIMESTAMP)||LA23_200==KW_VALUE_TYPE||LA23_200==KW_VIEW||LA23_200==KW_WHILE||LA23_200==KW_YEAR) ) {s = 450;}

                        else if ( ((LA23_200 >= KW_ALL && LA23_200 <= KW_ALTER)||(LA23_200 >= KW_ARRAY && LA23_200 <= KW_AS)||LA23_200==KW_AUTHORIZATION||(LA23_200 >= KW_BETWEEN && LA23_200 <= KW_BOTH)||LA23_200==KW_BY||LA23_200==KW_CREATE||LA23_200==KW_CUBE||(LA23_200 >= KW_CURRENT_DATE && LA23_200 <= KW_CURSOR)||LA23_200==KW_DATE||LA23_200==KW_DECIMAL||LA23_200==KW_DELETE||LA23_200==KW_DESCRIBE||(LA23_200 >= KW_DOUBLE && LA23_200 <= KW_DROP)||LA23_200==KW_EXISTS||(LA23_200 >= KW_EXTERNAL && LA23_200 <= KW_FETCH)||LA23_200==KW_FLOAT||LA23_200==KW_FOR||LA23_200==KW_FULL||(LA23_200 >= KW_GRANT && LA23_200 <= KW_GROUPING)||(LA23_200 >= KW_IMPORT && LA23_200 <= KW_IN)||LA23_200==KW_INNER||(LA23_200 >= KW_INSERT && LA23_200 <= KW_INTERSECT)||(LA23_200 >= KW_INTO && LA23_200 <= KW_IS)||(LA23_200 >= KW_LATERAL && LA23_200 <= KW_LEFT)||LA23_200==KW_LIKE||LA23_200==KW_LOCAL||LA23_200==KW_NONE||(LA23_200 >= KW_NULL && LA23_200 <= KW_OF)||(LA23_200 >= KW_ORDER && LA23_200 <= KW_OUTER)||LA23_200==KW_PARTITION||LA23_200==KW_PERCENT||LA23_200==KW_PROCEDURE||LA23_200==KW_RANGE||LA23_200==KW_READS||LA23_200==KW_REVOKE||LA23_200==KW_RIGHT||(LA23_200 >= KW_ROLLUP && LA23_200 <= KW_ROWS)||LA23_200==KW_SET||LA23_200==KW_SMALLINT||LA23_200==KW_TABLE||LA23_200==KW_TIMESTAMP||LA23_200==KW_TO||(LA23_200 >= KW_TRIGGER && LA23_200 <= KW_TRUNCATE)||LA23_200==KW_UNION||LA23_200==KW_UPDATE||(LA23_200 >= KW_USER && LA23_200 <= KW_USING)||LA23_200==KW_VALUES||LA23_200==KW_WITH) ) {s = 451;}

                         
                        input.seek(index23_200);

                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA23_228 = input.LA(1);

                         
                        int index23_228 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA23_228==STAR) && (synpred2_SelectClauseParser())) {s = 452;}

                        else if ( (LA23_228==Identifier) ) {s = 453;}

                        else if ( ((LA23_228 >= KW_ADD && LA23_228 <= KW_AFTER)||LA23_228==KW_ANALYZE||LA23_228==KW_ARCHIVE||LA23_228==KW_ASC||LA23_228==KW_BEFORE||(LA23_228 >= KW_BUCKET && LA23_228 <= KW_BUCKETS)||LA23_228==KW_CASCADE||LA23_228==KW_CHANGE||(LA23_228 >= KW_CLUSTER && LA23_228 <= KW_COLLECTION)||(LA23_228 >= KW_COLUMNS && LA23_228 <= KW_CONCATENATE)||LA23_228==KW_CONTINUE||LA23_228==KW_DATA||LA23_228==KW_DATABASES||(LA23_228 >= KW_DATETIME && LA23_228 <= KW_DBPROPERTIES)||(LA23_228 >= KW_DEFERRED && LA23_228 <= KW_DEFINED)||(LA23_228 >= KW_DELIMITED && LA23_228 <= KW_DESC)||(LA23_228 >= KW_DIRECTORIES && LA23_228 <= KW_DISABLE)||LA23_228==KW_DISTRIBUTE||LA23_228==KW_ELEM_TYPE||LA23_228==KW_ENABLE||LA23_228==KW_ESCAPED||LA23_228==KW_EXCLUSIVE||(LA23_228 >= KW_EXPLAIN && LA23_228 <= KW_EXPORT)||(LA23_228 >= KW_FIELDS && LA23_228 <= KW_FIRST)||(LA23_228 >= KW_FORMAT && LA23_228 <= KW_FORMATTED)||LA23_228==KW_FUNCTIONS||(LA23_228 >= KW_HOLD_DDLTIME && LA23_228 <= KW_IDXPROPERTIES)||LA23_228==KW_IGNORE||(LA23_228 >= KW_INDEX && LA23_228 <= KW_INDEXES)||(LA23_228 >= KW_INPATH && LA23_228 <= KW_INPUTFORMAT)||(LA23_228 >= KW_ITEMS && LA23_228 <= KW_JAR)||(LA23_228 >= KW_KEYS && LA23_228 <= KW_KEY_TYPE)||(LA23_228 >= KW_LIMIT && LA23_228 <= KW_LOAD)||(LA23_228 >= KW_LOCATION && LA23_228 <= KW_LONG)||(LA23_228 >= KW_MAPJOIN && LA23_228 <= KW_MONTH)||LA23_228==KW_MSCK||LA23_228==KW_NOSCAN||LA23_228==KW_NO_DROP||LA23_228==KW_OFFLINE||LA23_228==KW_OPTION||(LA23_228 >= KW_OUTPUTDRIVER && LA23_228 <= KW_OUTPUTFORMAT)||(LA23_228 >= KW_OVERWRITE && LA23_228 <= KW_OWNER)||(LA23_228 >= KW_PARTITIONED && LA23_228 <= KW_PARTITIONS)||LA23_228==KW_PLUS||(LA23_228 >= KW_PRETTY && LA23_228 <= KW_PRINCIPALS)||(LA23_228 >= KW_PROTECTION && LA23_228 <= KW_PURGE)||(LA23_228 >= KW_READ && LA23_228 <= KW_READONLY)||(LA23_228 >= KW_REBUILD && LA23_228 <= KW_RECORDWRITER)||(LA23_228 >= KW_REGEXP && LA23_228 <= KW_RESTRICT)||LA23_228==KW_REWRITE||(LA23_228 >= KW_RLIKE && LA23_228 <= KW_ROLES)||(LA23_228 >= KW_SCHEMA && LA23_228 <= KW_SECOND)||(LA23_228 >= KW_SEMI && LA23_228 <= KW_SERVER)||(LA23_228 >= KW_SETS && LA23_228 <= KW_SKEWED)||(LA23_228 >= KW_SORT && LA23_228 <= KW_STRUCT)||LA23_228==KW_TABLES||(LA23_228 >= KW_TBLPROPERTIES && LA23_228 <= KW_TERMINATED)||LA23_228==KW_TINYINT||(LA23_228 >= KW_TOUCH && LA23_228 <= KW_TRANSACTIONS)||LA23_228==KW_UNARCHIVE||LA23_228==KW_UNDO||LA23_228==KW_UNIONTYPE||(LA23_228 >= KW_UNLOCK && LA23_228 <= KW_UNSIGNED)||(LA23_228 >= KW_URI && LA23_228 <= KW_USE)||(LA23_228 >= KW_UTC && LA23_228 <= KW_UTCTIMESTAMP)||LA23_228==KW_VALUE_TYPE||LA23_228==KW_VIEW||LA23_228==KW_WHILE||LA23_228==KW_YEAR) ) {s = 454;}

                        else if ( ((LA23_228 >= KW_ALL && LA23_228 <= KW_ALTER)||(LA23_228 >= KW_ARRAY && LA23_228 <= KW_AS)||LA23_228==KW_AUTHORIZATION||(LA23_228 >= KW_BETWEEN && LA23_228 <= KW_BOTH)||LA23_228==KW_BY||LA23_228==KW_CREATE||LA23_228==KW_CUBE||(LA23_228 >= KW_CURRENT_DATE && LA23_228 <= KW_CURSOR)||LA23_228==KW_DATE||LA23_228==KW_DECIMAL||LA23_228==KW_DELETE||LA23_228==KW_DESCRIBE||(LA23_228 >= KW_DOUBLE && LA23_228 <= KW_DROP)||LA23_228==KW_EXISTS||(LA23_228 >= KW_EXTERNAL && LA23_228 <= KW_FETCH)||LA23_228==KW_FLOAT||LA23_228==KW_FOR||LA23_228==KW_FULL||(LA23_228 >= KW_GRANT && LA23_228 <= KW_GROUPING)||(LA23_228 >= KW_IMPORT && LA23_228 <= KW_IN)||LA23_228==KW_INNER||(LA23_228 >= KW_INSERT && LA23_228 <= KW_INTERSECT)||(LA23_228 >= KW_INTO && LA23_228 <= KW_IS)||(LA23_228 >= KW_LATERAL && LA23_228 <= KW_LEFT)||LA23_228==KW_LIKE||LA23_228==KW_LOCAL||LA23_228==KW_NONE||(LA23_228 >= KW_NULL && LA23_228 <= KW_OF)||(LA23_228 >= KW_ORDER && LA23_228 <= KW_OUTER)||LA23_228==KW_PARTITION||LA23_228==KW_PERCENT||LA23_228==KW_PROCEDURE||LA23_228==KW_RANGE||LA23_228==KW_READS||LA23_228==KW_REVOKE||LA23_228==KW_RIGHT||(LA23_228 >= KW_ROLLUP && LA23_228 <= KW_ROWS)||LA23_228==KW_SET||LA23_228==KW_SMALLINT||LA23_228==KW_TABLE||LA23_228==KW_TIMESTAMP||LA23_228==KW_TO||(LA23_228 >= KW_TRIGGER && LA23_228 <= KW_TRUNCATE)||LA23_228==KW_UNION||LA23_228==KW_UPDATE||(LA23_228 >= KW_USER && LA23_228 <= KW_USING)||LA23_228==KW_VALUES||LA23_228==KW_WITH) ) {s = 455;}

                         
                        input.seek(index23_228);

                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA23_256 = input.LA(1);

                         
                        int index23_256 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA23_256==STAR) && (synpred2_SelectClauseParser())) {s = 456;}

                        else if ( (LA23_256==Identifier) ) {s = 457;}

                        else if ( ((LA23_256 >= KW_ADD && LA23_256 <= KW_AFTER)||LA23_256==KW_ANALYZE||LA23_256==KW_ARCHIVE||LA23_256==KW_ASC||LA23_256==KW_BEFORE||(LA23_256 >= KW_BUCKET && LA23_256 <= KW_BUCKETS)||LA23_256==KW_CASCADE||LA23_256==KW_CHANGE||(LA23_256 >= KW_CLUSTER && LA23_256 <= KW_COLLECTION)||(LA23_256 >= KW_COLUMNS && LA23_256 <= KW_CONCATENATE)||LA23_256==KW_CONTINUE||LA23_256==KW_DATA||LA23_256==KW_DATABASES||(LA23_256 >= KW_DATETIME && LA23_256 <= KW_DBPROPERTIES)||(LA23_256 >= KW_DEFERRED && LA23_256 <= KW_DEFINED)||(LA23_256 >= KW_DELIMITED && LA23_256 <= KW_DESC)||(LA23_256 >= KW_DIRECTORIES && LA23_256 <= KW_DISABLE)||LA23_256==KW_DISTRIBUTE||LA23_256==KW_ELEM_TYPE||LA23_256==KW_ENABLE||LA23_256==KW_ESCAPED||LA23_256==KW_EXCLUSIVE||(LA23_256 >= KW_EXPLAIN && LA23_256 <= KW_EXPORT)||(LA23_256 >= KW_FIELDS && LA23_256 <= KW_FIRST)||(LA23_256 >= KW_FORMAT && LA23_256 <= KW_FORMATTED)||LA23_256==KW_FUNCTIONS||(LA23_256 >= KW_HOLD_DDLTIME && LA23_256 <= KW_IDXPROPERTIES)||LA23_256==KW_IGNORE||(LA23_256 >= KW_INDEX && LA23_256 <= KW_INDEXES)||(LA23_256 >= KW_INPATH && LA23_256 <= KW_INPUTFORMAT)||(LA23_256 >= KW_ITEMS && LA23_256 <= KW_JAR)||(LA23_256 >= KW_KEYS && LA23_256 <= KW_KEY_TYPE)||(LA23_256 >= KW_LIMIT && LA23_256 <= KW_LOAD)||(LA23_256 >= KW_LOCATION && LA23_256 <= KW_LONG)||(LA23_256 >= KW_MAPJOIN && LA23_256 <= KW_MONTH)||LA23_256==KW_MSCK||LA23_256==KW_NOSCAN||LA23_256==KW_NO_DROP||LA23_256==KW_OFFLINE||LA23_256==KW_OPTION||(LA23_256 >= KW_OUTPUTDRIVER && LA23_256 <= KW_OUTPUTFORMAT)||(LA23_256 >= KW_OVERWRITE && LA23_256 <= KW_OWNER)||(LA23_256 >= KW_PARTITIONED && LA23_256 <= KW_PARTITIONS)||LA23_256==KW_PLUS||(LA23_256 >= KW_PRETTY && LA23_256 <= KW_PRINCIPALS)||(LA23_256 >= KW_PROTECTION && LA23_256 <= KW_PURGE)||(LA23_256 >= KW_READ && LA23_256 <= KW_READONLY)||(LA23_256 >= KW_REBUILD && LA23_256 <= KW_RECORDWRITER)||(LA23_256 >= KW_REGEXP && LA23_256 <= KW_RESTRICT)||LA23_256==KW_REWRITE||(LA23_256 >= KW_RLIKE && LA23_256 <= KW_ROLES)||(LA23_256 >= KW_SCHEMA && LA23_256 <= KW_SECOND)||(LA23_256 >= KW_SEMI && LA23_256 <= KW_SERVER)||(LA23_256 >= KW_SETS && LA23_256 <= KW_SKEWED)||(LA23_256 >= KW_SORT && LA23_256 <= KW_STRUCT)||LA23_256==KW_TABLES||(LA23_256 >= KW_TBLPROPERTIES && LA23_256 <= KW_TERMINATED)||LA23_256==KW_TINYINT||(LA23_256 >= KW_TOUCH && LA23_256 <= KW_TRANSACTIONS)||LA23_256==KW_UNARCHIVE||LA23_256==KW_UNDO||LA23_256==KW_UNIONTYPE||(LA23_256 >= KW_UNLOCK && LA23_256 <= KW_UNSIGNED)||(LA23_256 >= KW_URI && LA23_256 <= KW_USE)||(LA23_256 >= KW_UTC && LA23_256 <= KW_UTCTIMESTAMP)||LA23_256==KW_VALUE_TYPE||LA23_256==KW_VIEW||LA23_256==KW_WHILE||LA23_256==KW_YEAR) ) {s = 458;}

                        else if ( ((LA23_256 >= KW_ALL && LA23_256 <= KW_ALTER)||(LA23_256 >= KW_ARRAY && LA23_256 <= KW_AS)||LA23_256==KW_AUTHORIZATION||(LA23_256 >= KW_BETWEEN && LA23_256 <= KW_BOTH)||LA23_256==KW_BY||LA23_256==KW_CREATE||LA23_256==KW_CUBE||(LA23_256 >= KW_CURRENT_DATE && LA23_256 <= KW_CURSOR)||LA23_256==KW_DATE||LA23_256==KW_DECIMAL||LA23_256==KW_DELETE||LA23_256==KW_DESCRIBE||(LA23_256 >= KW_DOUBLE && LA23_256 <= KW_DROP)||LA23_256==KW_EXISTS||(LA23_256 >= KW_EXTERNAL && LA23_256 <= KW_FETCH)||LA23_256==KW_FLOAT||LA23_256==KW_FOR||LA23_256==KW_FULL||(LA23_256 >= KW_GRANT && LA23_256 <= KW_GROUPING)||(LA23_256 >= KW_IMPORT && LA23_256 <= KW_IN)||LA23_256==KW_INNER||(LA23_256 >= KW_INSERT && LA23_256 <= KW_INTERSECT)||(LA23_256 >= KW_INTO && LA23_256 <= KW_IS)||(LA23_256 >= KW_LATERAL && LA23_256 <= KW_LEFT)||LA23_256==KW_LIKE||LA23_256==KW_LOCAL||LA23_256==KW_NONE||(LA23_256 >= KW_NULL && LA23_256 <= KW_OF)||(LA23_256 >= KW_ORDER && LA23_256 <= KW_OUTER)||LA23_256==KW_PARTITION||LA23_256==KW_PERCENT||LA23_256==KW_PROCEDURE||LA23_256==KW_RANGE||LA23_256==KW_READS||LA23_256==KW_REVOKE||LA23_256==KW_RIGHT||(LA23_256 >= KW_ROLLUP && LA23_256 <= KW_ROWS)||LA23_256==KW_SET||LA23_256==KW_SMALLINT||LA23_256==KW_TABLE||LA23_256==KW_TIMESTAMP||LA23_256==KW_TO||(LA23_256 >= KW_TRIGGER && LA23_256 <= KW_TRUNCATE)||LA23_256==KW_UNION||LA23_256==KW_UPDATE||(LA23_256 >= KW_USER && LA23_256 <= KW_USING)||LA23_256==KW_VALUES||LA23_256==KW_WITH) ) {s = 459;}

                         
                        input.seek(index23_256);

                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA23_284 = input.LA(1);

                         
                        int index23_284 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA23_284==STAR) && (synpred2_SelectClauseParser())) {s = 460;}

                        else if ( (LA23_284==Identifier) ) {s = 461;}

                        else if ( ((LA23_284 >= KW_ADD && LA23_284 <= KW_AFTER)||LA23_284==KW_ANALYZE||LA23_284==KW_ARCHIVE||LA23_284==KW_ASC||LA23_284==KW_BEFORE||(LA23_284 >= KW_BUCKET && LA23_284 <= KW_BUCKETS)||LA23_284==KW_CASCADE||LA23_284==KW_CHANGE||(LA23_284 >= KW_CLUSTER && LA23_284 <= KW_COLLECTION)||(LA23_284 >= KW_COLUMNS && LA23_284 <= KW_CONCATENATE)||LA23_284==KW_CONTINUE||LA23_284==KW_DATA||LA23_284==KW_DATABASES||(LA23_284 >= KW_DATETIME && LA23_284 <= KW_DBPROPERTIES)||(LA23_284 >= KW_DEFERRED && LA23_284 <= KW_DEFINED)||(LA23_284 >= KW_DELIMITED && LA23_284 <= KW_DESC)||(LA23_284 >= KW_DIRECTORIES && LA23_284 <= KW_DISABLE)||LA23_284==KW_DISTRIBUTE||LA23_284==KW_ELEM_TYPE||LA23_284==KW_ENABLE||LA23_284==KW_ESCAPED||LA23_284==KW_EXCLUSIVE||(LA23_284 >= KW_EXPLAIN && LA23_284 <= KW_EXPORT)||(LA23_284 >= KW_FIELDS && LA23_284 <= KW_FIRST)||(LA23_284 >= KW_FORMAT && LA23_284 <= KW_FORMATTED)||LA23_284==KW_FUNCTIONS||(LA23_284 >= KW_HOLD_DDLTIME && LA23_284 <= KW_IDXPROPERTIES)||LA23_284==KW_IGNORE||(LA23_284 >= KW_INDEX && LA23_284 <= KW_INDEXES)||(LA23_284 >= KW_INPATH && LA23_284 <= KW_INPUTFORMAT)||(LA23_284 >= KW_ITEMS && LA23_284 <= KW_JAR)||(LA23_284 >= KW_KEYS && LA23_284 <= KW_KEY_TYPE)||(LA23_284 >= KW_LIMIT && LA23_284 <= KW_LOAD)||(LA23_284 >= KW_LOCATION && LA23_284 <= KW_LONG)||(LA23_284 >= KW_MAPJOIN && LA23_284 <= KW_MONTH)||LA23_284==KW_MSCK||LA23_284==KW_NOSCAN||LA23_284==KW_NO_DROP||LA23_284==KW_OFFLINE||LA23_284==KW_OPTION||(LA23_284 >= KW_OUTPUTDRIVER && LA23_284 <= KW_OUTPUTFORMAT)||(LA23_284 >= KW_OVERWRITE && LA23_284 <= KW_OWNER)||(LA23_284 >= KW_PARTITIONED && LA23_284 <= KW_PARTITIONS)||LA23_284==KW_PLUS||(LA23_284 >= KW_PRETTY && LA23_284 <= KW_PRINCIPALS)||(LA23_284 >= KW_PROTECTION && LA23_284 <= KW_PURGE)||(LA23_284 >= KW_READ && LA23_284 <= KW_READONLY)||(LA23_284 >= KW_REBUILD && LA23_284 <= KW_RECORDWRITER)||(LA23_284 >= KW_REGEXP && LA23_284 <= KW_RESTRICT)||LA23_284==KW_REWRITE||(LA23_284 >= KW_RLIKE && LA23_284 <= KW_ROLES)||(LA23_284 >= KW_SCHEMA && LA23_284 <= KW_SECOND)||(LA23_284 >= KW_SEMI && LA23_284 <= KW_SERVER)||(LA23_284 >= KW_SETS && LA23_284 <= KW_SKEWED)||(LA23_284 >= KW_SORT && LA23_284 <= KW_STRUCT)||LA23_284==KW_TABLES||(LA23_284 >= KW_TBLPROPERTIES && LA23_284 <= KW_TERMINATED)||LA23_284==KW_TINYINT||(LA23_284 >= KW_TOUCH && LA23_284 <= KW_TRANSACTIONS)||LA23_284==KW_UNARCHIVE||LA23_284==KW_UNDO||LA23_284==KW_UNIONTYPE||(LA23_284 >= KW_UNLOCK && LA23_284 <= KW_UNSIGNED)||(LA23_284 >= KW_URI && LA23_284 <= KW_USE)||(LA23_284 >= KW_UTC && LA23_284 <= KW_UTCTIMESTAMP)||LA23_284==KW_VALUE_TYPE||LA23_284==KW_VIEW||LA23_284==KW_WHILE||LA23_284==KW_YEAR) ) {s = 462;}

                        else if ( ((LA23_284 >= KW_ALL && LA23_284 <= KW_ALTER)||(LA23_284 >= KW_ARRAY && LA23_284 <= KW_AS)||LA23_284==KW_AUTHORIZATION||(LA23_284 >= KW_BETWEEN && LA23_284 <= KW_BOTH)||LA23_284==KW_BY||LA23_284==KW_CREATE||LA23_284==KW_CUBE||(LA23_284 >= KW_CURRENT_DATE && LA23_284 <= KW_CURSOR)||LA23_284==KW_DATE||LA23_284==KW_DECIMAL||LA23_284==KW_DELETE||LA23_284==KW_DESCRIBE||(LA23_284 >= KW_DOUBLE && LA23_284 <= KW_DROP)||LA23_284==KW_EXISTS||(LA23_284 >= KW_EXTERNAL && LA23_284 <= KW_FETCH)||LA23_284==KW_FLOAT||LA23_284==KW_FOR||LA23_284==KW_FULL||(LA23_284 >= KW_GRANT && LA23_284 <= KW_GROUPING)||(LA23_284 >= KW_IMPORT && LA23_284 <= KW_IN)||LA23_284==KW_INNER||(LA23_284 >= KW_INSERT && LA23_284 <= KW_INTERSECT)||(LA23_284 >= KW_INTO && LA23_284 <= KW_IS)||(LA23_284 >= KW_LATERAL && LA23_284 <= KW_LEFT)||LA23_284==KW_LIKE||LA23_284==KW_LOCAL||LA23_284==KW_NONE||(LA23_284 >= KW_NULL && LA23_284 <= KW_OF)||(LA23_284 >= KW_ORDER && LA23_284 <= KW_OUTER)||LA23_284==KW_PARTITION||LA23_284==KW_PERCENT||LA23_284==KW_PROCEDURE||LA23_284==KW_RANGE||LA23_284==KW_READS||LA23_284==KW_REVOKE||LA23_284==KW_RIGHT||(LA23_284 >= KW_ROLLUP && LA23_284 <= KW_ROWS)||LA23_284==KW_SET||LA23_284==KW_SMALLINT||LA23_284==KW_TABLE||LA23_284==KW_TIMESTAMP||LA23_284==KW_TO||(LA23_284 >= KW_TRIGGER && LA23_284 <= KW_TRUNCATE)||LA23_284==KW_UNION||LA23_284==KW_UPDATE||(LA23_284 >= KW_USER && LA23_284 <= KW_USING)||LA23_284==KW_VALUES||LA23_284==KW_WITH) ) {s = 463;}

                         
                        input.seek(index23_284);

                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA23_312 = input.LA(1);

                         
                        int index23_312 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA23_312==STAR) && (synpred2_SelectClauseParser())) {s = 464;}

                        else if ( (LA23_312==Identifier) ) {s = 465;}

                        else if ( ((LA23_312 >= KW_ADD && LA23_312 <= KW_AFTER)||LA23_312==KW_ANALYZE||LA23_312==KW_ARCHIVE||LA23_312==KW_ASC||LA23_312==KW_BEFORE||(LA23_312 >= KW_BUCKET && LA23_312 <= KW_BUCKETS)||LA23_312==KW_CASCADE||LA23_312==KW_CHANGE||(LA23_312 >= KW_CLUSTER && LA23_312 <= KW_COLLECTION)||(LA23_312 >= KW_COLUMNS && LA23_312 <= KW_CONCATENATE)||LA23_312==KW_CONTINUE||LA23_312==KW_DATA||LA23_312==KW_DATABASES||(LA23_312 >= KW_DATETIME && LA23_312 <= KW_DBPROPERTIES)||(LA23_312 >= KW_DEFERRED && LA23_312 <= KW_DEFINED)||(LA23_312 >= KW_DELIMITED && LA23_312 <= KW_DESC)||(LA23_312 >= KW_DIRECTORIES && LA23_312 <= KW_DISABLE)||LA23_312==KW_DISTRIBUTE||LA23_312==KW_ELEM_TYPE||LA23_312==KW_ENABLE||LA23_312==KW_ESCAPED||LA23_312==KW_EXCLUSIVE||(LA23_312 >= KW_EXPLAIN && LA23_312 <= KW_EXPORT)||(LA23_312 >= KW_FIELDS && LA23_312 <= KW_FIRST)||(LA23_312 >= KW_FORMAT && LA23_312 <= KW_FORMATTED)||LA23_312==KW_FUNCTIONS||(LA23_312 >= KW_HOLD_DDLTIME && LA23_312 <= KW_IDXPROPERTIES)||LA23_312==KW_IGNORE||(LA23_312 >= KW_INDEX && LA23_312 <= KW_INDEXES)||(LA23_312 >= KW_INPATH && LA23_312 <= KW_INPUTFORMAT)||(LA23_312 >= KW_ITEMS && LA23_312 <= KW_JAR)||(LA23_312 >= KW_KEYS && LA23_312 <= KW_KEY_TYPE)||(LA23_312 >= KW_LIMIT && LA23_312 <= KW_LOAD)||(LA23_312 >= KW_LOCATION && LA23_312 <= KW_LONG)||(LA23_312 >= KW_MAPJOIN && LA23_312 <= KW_MONTH)||LA23_312==KW_MSCK||LA23_312==KW_NOSCAN||LA23_312==KW_NO_DROP||LA23_312==KW_OFFLINE||LA23_312==KW_OPTION||(LA23_312 >= KW_OUTPUTDRIVER && LA23_312 <= KW_OUTPUTFORMAT)||(LA23_312 >= KW_OVERWRITE && LA23_312 <= KW_OWNER)||(LA23_312 >= KW_PARTITIONED && LA23_312 <= KW_PARTITIONS)||LA23_312==KW_PLUS||(LA23_312 >= KW_PRETTY && LA23_312 <= KW_PRINCIPALS)||(LA23_312 >= KW_PROTECTION && LA23_312 <= KW_PURGE)||(LA23_312 >= KW_READ && LA23_312 <= KW_READONLY)||(LA23_312 >= KW_REBUILD && LA23_312 <= KW_RECORDWRITER)||(LA23_312 >= KW_REGEXP && LA23_312 <= KW_RESTRICT)||LA23_312==KW_REWRITE||(LA23_312 >= KW_RLIKE && LA23_312 <= KW_ROLES)||(LA23_312 >= KW_SCHEMA && LA23_312 <= KW_SECOND)||(LA23_312 >= KW_SEMI && LA23_312 <= KW_SERVER)||(LA23_312 >= KW_SETS && LA23_312 <= KW_SKEWED)||(LA23_312 >= KW_SORT && LA23_312 <= KW_STRUCT)||LA23_312==KW_TABLES||(LA23_312 >= KW_TBLPROPERTIES && LA23_312 <= KW_TERMINATED)||LA23_312==KW_TINYINT||(LA23_312 >= KW_TOUCH && LA23_312 <= KW_TRANSACTIONS)||LA23_312==KW_UNARCHIVE||LA23_312==KW_UNDO||LA23_312==KW_UNIONTYPE||(LA23_312 >= KW_UNLOCK && LA23_312 <= KW_UNSIGNED)||(LA23_312 >= KW_URI && LA23_312 <= KW_USE)||(LA23_312 >= KW_UTC && LA23_312 <= KW_UTCTIMESTAMP)||LA23_312==KW_VALUE_TYPE||LA23_312==KW_VIEW||LA23_312==KW_WHILE||LA23_312==KW_YEAR) ) {s = 466;}

                        else if ( ((LA23_312 >= KW_ALL && LA23_312 <= KW_ALTER)||(LA23_312 >= KW_ARRAY && LA23_312 <= KW_AS)||LA23_312==KW_AUTHORIZATION||(LA23_312 >= KW_BETWEEN && LA23_312 <= KW_BOTH)||LA23_312==KW_BY||LA23_312==KW_CREATE||LA23_312==KW_CUBE||(LA23_312 >= KW_CURRENT_DATE && LA23_312 <= KW_CURSOR)||LA23_312==KW_DATE||LA23_312==KW_DECIMAL||LA23_312==KW_DELETE||LA23_312==KW_DESCRIBE||(LA23_312 >= KW_DOUBLE && LA23_312 <= KW_DROP)||LA23_312==KW_EXISTS||(LA23_312 >= KW_EXTERNAL && LA23_312 <= KW_FETCH)||LA23_312==KW_FLOAT||LA23_312==KW_FOR||LA23_312==KW_FULL||(LA23_312 >= KW_GRANT && LA23_312 <= KW_GROUPING)||(LA23_312 >= KW_IMPORT && LA23_312 <= KW_IN)||LA23_312==KW_INNER||(LA23_312 >= KW_INSERT && LA23_312 <= KW_INTERSECT)||(LA23_312 >= KW_INTO && LA23_312 <= KW_IS)||(LA23_312 >= KW_LATERAL && LA23_312 <= KW_LEFT)||LA23_312==KW_LIKE||LA23_312==KW_LOCAL||LA23_312==KW_NONE||(LA23_312 >= KW_NULL && LA23_312 <= KW_OF)||(LA23_312 >= KW_ORDER && LA23_312 <= KW_OUTER)||LA23_312==KW_PARTITION||LA23_312==KW_PERCENT||LA23_312==KW_PROCEDURE||LA23_312==KW_RANGE||LA23_312==KW_READS||LA23_312==KW_REVOKE||LA23_312==KW_RIGHT||(LA23_312 >= KW_ROLLUP && LA23_312 <= KW_ROWS)||LA23_312==KW_SET||LA23_312==KW_SMALLINT||LA23_312==KW_TABLE||LA23_312==KW_TIMESTAMP||LA23_312==KW_TO||(LA23_312 >= KW_TRIGGER && LA23_312 <= KW_TRUNCATE)||LA23_312==KW_UNION||LA23_312==KW_UPDATE||(LA23_312 >= KW_USER && LA23_312 <= KW_USING)||LA23_312==KW_VALUES||LA23_312==KW_WITH) ) {s = 467;}

                         
                        input.seek(index23_312);

                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA23_340 = input.LA(1);

                         
                        int index23_340 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA23_340==STAR) && (synpred2_SelectClauseParser())) {s = 468;}

                        else if ( (LA23_340==Identifier) ) {s = 469;}

                        else if ( ((LA23_340 >= KW_ADD && LA23_340 <= KW_AFTER)||LA23_340==KW_ANALYZE||LA23_340==KW_ARCHIVE||LA23_340==KW_ASC||LA23_340==KW_BEFORE||(LA23_340 >= KW_BUCKET && LA23_340 <= KW_BUCKETS)||LA23_340==KW_CASCADE||LA23_340==KW_CHANGE||(LA23_340 >= KW_CLUSTER && LA23_340 <= KW_COLLECTION)||(LA23_340 >= KW_COLUMNS && LA23_340 <= KW_CONCATENATE)||LA23_340==KW_CONTINUE||LA23_340==KW_DATA||LA23_340==KW_DATABASES||(LA23_340 >= KW_DATETIME && LA23_340 <= KW_DBPROPERTIES)||(LA23_340 >= KW_DEFERRED && LA23_340 <= KW_DEFINED)||(LA23_340 >= KW_DELIMITED && LA23_340 <= KW_DESC)||(LA23_340 >= KW_DIRECTORIES && LA23_340 <= KW_DISABLE)||LA23_340==KW_DISTRIBUTE||LA23_340==KW_ELEM_TYPE||LA23_340==KW_ENABLE||LA23_340==KW_ESCAPED||LA23_340==KW_EXCLUSIVE||(LA23_340 >= KW_EXPLAIN && LA23_340 <= KW_EXPORT)||(LA23_340 >= KW_FIELDS && LA23_340 <= KW_FIRST)||(LA23_340 >= KW_FORMAT && LA23_340 <= KW_FORMATTED)||LA23_340==KW_FUNCTIONS||(LA23_340 >= KW_HOLD_DDLTIME && LA23_340 <= KW_IDXPROPERTIES)||LA23_340==KW_IGNORE||(LA23_340 >= KW_INDEX && LA23_340 <= KW_INDEXES)||(LA23_340 >= KW_INPATH && LA23_340 <= KW_INPUTFORMAT)||(LA23_340 >= KW_ITEMS && LA23_340 <= KW_JAR)||(LA23_340 >= KW_KEYS && LA23_340 <= KW_KEY_TYPE)||(LA23_340 >= KW_LIMIT && LA23_340 <= KW_LOAD)||(LA23_340 >= KW_LOCATION && LA23_340 <= KW_LONG)||(LA23_340 >= KW_MAPJOIN && LA23_340 <= KW_MONTH)||LA23_340==KW_MSCK||LA23_340==KW_NOSCAN||LA23_340==KW_NO_DROP||LA23_340==KW_OFFLINE||LA23_340==KW_OPTION||(LA23_340 >= KW_OUTPUTDRIVER && LA23_340 <= KW_OUTPUTFORMAT)||(LA23_340 >= KW_OVERWRITE && LA23_340 <= KW_OWNER)||(LA23_340 >= KW_PARTITIONED && LA23_340 <= KW_PARTITIONS)||LA23_340==KW_PLUS||(LA23_340 >= KW_PRETTY && LA23_340 <= KW_PRINCIPALS)||(LA23_340 >= KW_PROTECTION && LA23_340 <= KW_PURGE)||(LA23_340 >= KW_READ && LA23_340 <= KW_READONLY)||(LA23_340 >= KW_REBUILD && LA23_340 <= KW_RECORDWRITER)||(LA23_340 >= KW_REGEXP && LA23_340 <= KW_RESTRICT)||LA23_340==KW_REWRITE||(LA23_340 >= KW_RLIKE && LA23_340 <= KW_ROLES)||(LA23_340 >= KW_SCHEMA && LA23_340 <= KW_SECOND)||(LA23_340 >= KW_SEMI && LA23_340 <= KW_SERVER)||(LA23_340 >= KW_SETS && LA23_340 <= KW_SKEWED)||(LA23_340 >= KW_SORT && LA23_340 <= KW_STRUCT)||LA23_340==KW_TABLES||(LA23_340 >= KW_TBLPROPERTIES && LA23_340 <= KW_TERMINATED)||LA23_340==KW_TINYINT||(LA23_340 >= KW_TOUCH && LA23_340 <= KW_TRANSACTIONS)||LA23_340==KW_UNARCHIVE||LA23_340==KW_UNDO||LA23_340==KW_UNIONTYPE||(LA23_340 >= KW_UNLOCK && LA23_340 <= KW_UNSIGNED)||(LA23_340 >= KW_URI && LA23_340 <= KW_USE)||(LA23_340 >= KW_UTC && LA23_340 <= KW_UTCTIMESTAMP)||LA23_340==KW_VALUE_TYPE||LA23_340==KW_VIEW||LA23_340==KW_WHILE||LA23_340==KW_YEAR) ) {s = 470;}

                        else if ( ((LA23_340 >= KW_ALL && LA23_340 <= KW_ALTER)||(LA23_340 >= KW_ARRAY && LA23_340 <= KW_AS)||LA23_340==KW_AUTHORIZATION||(LA23_340 >= KW_BETWEEN && LA23_340 <= KW_BOTH)||LA23_340==KW_BY||LA23_340==KW_CREATE||LA23_340==KW_CUBE||(LA23_340 >= KW_CURRENT_DATE && LA23_340 <= KW_CURSOR)||LA23_340==KW_DATE||LA23_340==KW_DECIMAL||LA23_340==KW_DELETE||LA23_340==KW_DESCRIBE||(LA23_340 >= KW_DOUBLE && LA23_340 <= KW_DROP)||LA23_340==KW_EXISTS||(LA23_340 >= KW_EXTERNAL && LA23_340 <= KW_FETCH)||LA23_340==KW_FLOAT||LA23_340==KW_FOR||LA23_340==KW_FULL||(LA23_340 >= KW_GRANT && LA23_340 <= KW_GROUPING)||(LA23_340 >= KW_IMPORT && LA23_340 <= KW_IN)||LA23_340==KW_INNER||(LA23_340 >= KW_INSERT && LA23_340 <= KW_INTERSECT)||(LA23_340 >= KW_INTO && LA23_340 <= KW_IS)||(LA23_340 >= KW_LATERAL && LA23_340 <= KW_LEFT)||LA23_340==KW_LIKE||LA23_340==KW_LOCAL||LA23_340==KW_NONE||(LA23_340 >= KW_NULL && LA23_340 <= KW_OF)||(LA23_340 >= KW_ORDER && LA23_340 <= KW_OUTER)||LA23_340==KW_PARTITION||LA23_340==KW_PERCENT||LA23_340==KW_PROCEDURE||LA23_340==KW_RANGE||LA23_340==KW_READS||LA23_340==KW_REVOKE||LA23_340==KW_RIGHT||(LA23_340 >= KW_ROLLUP && LA23_340 <= KW_ROWS)||LA23_340==KW_SET||LA23_340==KW_SMALLINT||LA23_340==KW_TABLE||LA23_340==KW_TIMESTAMP||LA23_340==KW_TO||(LA23_340 >= KW_TRIGGER && LA23_340 <= KW_TRUNCATE)||LA23_340==KW_UNION||LA23_340==KW_UPDATE||(LA23_340 >= KW_USER && LA23_340 <= KW_USING)||LA23_340==KW_VALUES||LA23_340==KW_WITH) ) {s = 471;}

                         
                        input.seek(index23_340);

                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA23_368 = input.LA(1);

                         
                        int index23_368 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA23_368==STAR) && (synpred2_SelectClauseParser())) {s = 472;}

                        else if ( (LA23_368==Identifier) ) {s = 473;}

                        else if ( ((LA23_368 >= KW_ADD && LA23_368 <= KW_AFTER)||LA23_368==KW_ANALYZE||LA23_368==KW_ARCHIVE||LA23_368==KW_ASC||LA23_368==KW_BEFORE||(LA23_368 >= KW_BUCKET && LA23_368 <= KW_BUCKETS)||LA23_368==KW_CASCADE||LA23_368==KW_CHANGE||(LA23_368 >= KW_CLUSTER && LA23_368 <= KW_COLLECTION)||(LA23_368 >= KW_COLUMNS && LA23_368 <= KW_CONCATENATE)||LA23_368==KW_CONTINUE||LA23_368==KW_DATA||LA23_368==KW_DATABASES||(LA23_368 >= KW_DATETIME && LA23_368 <= KW_DBPROPERTIES)||(LA23_368 >= KW_DEFERRED && LA23_368 <= KW_DEFINED)||(LA23_368 >= KW_DELIMITED && LA23_368 <= KW_DESC)||(LA23_368 >= KW_DIRECTORIES && LA23_368 <= KW_DISABLE)||LA23_368==KW_DISTRIBUTE||LA23_368==KW_ELEM_TYPE||LA23_368==KW_ENABLE||LA23_368==KW_ESCAPED||LA23_368==KW_EXCLUSIVE||(LA23_368 >= KW_EXPLAIN && LA23_368 <= KW_EXPORT)||(LA23_368 >= KW_FIELDS && LA23_368 <= KW_FIRST)||(LA23_368 >= KW_FORMAT && LA23_368 <= KW_FORMATTED)||LA23_368==KW_FUNCTIONS||(LA23_368 >= KW_HOLD_DDLTIME && LA23_368 <= KW_IDXPROPERTIES)||LA23_368==KW_IGNORE||(LA23_368 >= KW_INDEX && LA23_368 <= KW_INDEXES)||(LA23_368 >= KW_INPATH && LA23_368 <= KW_INPUTFORMAT)||(LA23_368 >= KW_ITEMS && LA23_368 <= KW_JAR)||(LA23_368 >= KW_KEYS && LA23_368 <= KW_KEY_TYPE)||(LA23_368 >= KW_LIMIT && LA23_368 <= KW_LOAD)||(LA23_368 >= KW_LOCATION && LA23_368 <= KW_LONG)||(LA23_368 >= KW_MAPJOIN && LA23_368 <= KW_MONTH)||LA23_368==KW_MSCK||LA23_368==KW_NOSCAN||LA23_368==KW_NO_DROP||LA23_368==KW_OFFLINE||LA23_368==KW_OPTION||(LA23_368 >= KW_OUTPUTDRIVER && LA23_368 <= KW_OUTPUTFORMAT)||(LA23_368 >= KW_OVERWRITE && LA23_368 <= KW_OWNER)||(LA23_368 >= KW_PARTITIONED && LA23_368 <= KW_PARTITIONS)||LA23_368==KW_PLUS||(LA23_368 >= KW_PRETTY && LA23_368 <= KW_PRINCIPALS)||(LA23_368 >= KW_PROTECTION && LA23_368 <= KW_PURGE)||(LA23_368 >= KW_READ && LA23_368 <= KW_READONLY)||(LA23_368 >= KW_REBUILD && LA23_368 <= KW_RECORDWRITER)||(LA23_368 >= KW_REGEXP && LA23_368 <= KW_RESTRICT)||LA23_368==KW_REWRITE||(LA23_368 >= KW_RLIKE && LA23_368 <= KW_ROLES)||(LA23_368 >= KW_SCHEMA && LA23_368 <= KW_SECOND)||(LA23_368 >= KW_SEMI && LA23_368 <= KW_SERVER)||(LA23_368 >= KW_SETS && LA23_368 <= KW_SKEWED)||(LA23_368 >= KW_SORT && LA23_368 <= KW_STRUCT)||LA23_368==KW_TABLES||(LA23_368 >= KW_TBLPROPERTIES && LA23_368 <= KW_TERMINATED)||LA23_368==KW_TINYINT||(LA23_368 >= KW_TOUCH && LA23_368 <= KW_TRANSACTIONS)||LA23_368==KW_UNARCHIVE||LA23_368==KW_UNDO||LA23_368==KW_UNIONTYPE||(LA23_368 >= KW_UNLOCK && LA23_368 <= KW_UNSIGNED)||(LA23_368 >= KW_URI && LA23_368 <= KW_USE)||(LA23_368 >= KW_UTC && LA23_368 <= KW_UTCTIMESTAMP)||LA23_368==KW_VALUE_TYPE||LA23_368==KW_VIEW||LA23_368==KW_WHILE||LA23_368==KW_YEAR) ) {s = 474;}

                        else if ( ((LA23_368 >= KW_ALL && LA23_368 <= KW_ALTER)||(LA23_368 >= KW_ARRAY && LA23_368 <= KW_AS)||LA23_368==KW_AUTHORIZATION||(LA23_368 >= KW_BETWEEN && LA23_368 <= KW_BOTH)||LA23_368==KW_BY||LA23_368==KW_CREATE||LA23_368==KW_CUBE||(LA23_368 >= KW_CURRENT_DATE && LA23_368 <= KW_CURSOR)||LA23_368==KW_DATE||LA23_368==KW_DECIMAL||LA23_368==KW_DELETE||LA23_368==KW_DESCRIBE||(LA23_368 >= KW_DOUBLE && LA23_368 <= KW_DROP)||LA23_368==KW_EXISTS||(LA23_368 >= KW_EXTERNAL && LA23_368 <= KW_FETCH)||LA23_368==KW_FLOAT||LA23_368==KW_FOR||LA23_368==KW_FULL||(LA23_368 >= KW_GRANT && LA23_368 <= KW_GROUPING)||(LA23_368 >= KW_IMPORT && LA23_368 <= KW_IN)||LA23_368==KW_INNER||(LA23_368 >= KW_INSERT && LA23_368 <= KW_INTERSECT)||(LA23_368 >= KW_INTO && LA23_368 <= KW_IS)||(LA23_368 >= KW_LATERAL && LA23_368 <= KW_LEFT)||LA23_368==KW_LIKE||LA23_368==KW_LOCAL||LA23_368==KW_NONE||(LA23_368 >= KW_NULL && LA23_368 <= KW_OF)||(LA23_368 >= KW_ORDER && LA23_368 <= KW_OUTER)||LA23_368==KW_PARTITION||LA23_368==KW_PERCENT||LA23_368==KW_PROCEDURE||LA23_368==KW_RANGE||LA23_368==KW_READS||LA23_368==KW_REVOKE||LA23_368==KW_RIGHT||(LA23_368 >= KW_ROLLUP && LA23_368 <= KW_ROWS)||LA23_368==KW_SET||LA23_368==KW_SMALLINT||LA23_368==KW_TABLE||LA23_368==KW_TIMESTAMP||LA23_368==KW_TO||(LA23_368 >= KW_TRIGGER && LA23_368 <= KW_TRUNCATE)||LA23_368==KW_UNION||LA23_368==KW_UPDATE||(LA23_368 >= KW_USER && LA23_368 <= KW_USING)||LA23_368==KW_VALUES||LA23_368==KW_WITH) ) {s = 475;}

                         
                        input.seek(index23_368);

                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA23_396 = input.LA(1);

                         
                        int index23_396 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA23_396==STAR) && (synpred2_SelectClauseParser())) {s = 476;}

                        else if ( (LA23_396==Identifier) ) {s = 477;}

                        else if ( ((LA23_396 >= KW_ADD && LA23_396 <= KW_AFTER)||LA23_396==KW_ANALYZE||LA23_396==KW_ARCHIVE||LA23_396==KW_ASC||LA23_396==KW_BEFORE||(LA23_396 >= KW_BUCKET && LA23_396 <= KW_BUCKETS)||LA23_396==KW_CASCADE||LA23_396==KW_CHANGE||(LA23_396 >= KW_CLUSTER && LA23_396 <= KW_COLLECTION)||(LA23_396 >= KW_COLUMNS && LA23_396 <= KW_CONCATENATE)||LA23_396==KW_CONTINUE||LA23_396==KW_DATA||LA23_396==KW_DATABASES||(LA23_396 >= KW_DATETIME && LA23_396 <= KW_DBPROPERTIES)||(LA23_396 >= KW_DEFERRED && LA23_396 <= KW_DEFINED)||(LA23_396 >= KW_DELIMITED && LA23_396 <= KW_DESC)||(LA23_396 >= KW_DIRECTORIES && LA23_396 <= KW_DISABLE)||LA23_396==KW_DISTRIBUTE||LA23_396==KW_ELEM_TYPE||LA23_396==KW_ENABLE||LA23_396==KW_ESCAPED||LA23_396==KW_EXCLUSIVE||(LA23_396 >= KW_EXPLAIN && LA23_396 <= KW_EXPORT)||(LA23_396 >= KW_FIELDS && LA23_396 <= KW_FIRST)||(LA23_396 >= KW_FORMAT && LA23_396 <= KW_FORMATTED)||LA23_396==KW_FUNCTIONS||(LA23_396 >= KW_HOLD_DDLTIME && LA23_396 <= KW_IDXPROPERTIES)||LA23_396==KW_IGNORE||(LA23_396 >= KW_INDEX && LA23_396 <= KW_INDEXES)||(LA23_396 >= KW_INPATH && LA23_396 <= KW_INPUTFORMAT)||(LA23_396 >= KW_ITEMS && LA23_396 <= KW_JAR)||(LA23_396 >= KW_KEYS && LA23_396 <= KW_KEY_TYPE)||(LA23_396 >= KW_LIMIT && LA23_396 <= KW_LOAD)||(LA23_396 >= KW_LOCATION && LA23_396 <= KW_LONG)||(LA23_396 >= KW_MAPJOIN && LA23_396 <= KW_MONTH)||LA23_396==KW_MSCK||LA23_396==KW_NOSCAN||LA23_396==KW_NO_DROP||LA23_396==KW_OFFLINE||LA23_396==KW_OPTION||(LA23_396 >= KW_OUTPUTDRIVER && LA23_396 <= KW_OUTPUTFORMAT)||(LA23_396 >= KW_OVERWRITE && LA23_396 <= KW_OWNER)||(LA23_396 >= KW_PARTITIONED && LA23_396 <= KW_PARTITIONS)||LA23_396==KW_PLUS||(LA23_396 >= KW_PRETTY && LA23_396 <= KW_PRINCIPALS)||(LA23_396 >= KW_PROTECTION && LA23_396 <= KW_PURGE)||(LA23_396 >= KW_READ && LA23_396 <= KW_READONLY)||(LA23_396 >= KW_REBUILD && LA23_396 <= KW_RECORDWRITER)||(LA23_396 >= KW_REGEXP && LA23_396 <= KW_RESTRICT)||LA23_396==KW_REWRITE||(LA23_396 >= KW_RLIKE && LA23_396 <= KW_ROLES)||(LA23_396 >= KW_SCHEMA && LA23_396 <= KW_SECOND)||(LA23_396 >= KW_SEMI && LA23_396 <= KW_SERVER)||(LA23_396 >= KW_SETS && LA23_396 <= KW_SKEWED)||(LA23_396 >= KW_SORT && LA23_396 <= KW_STRUCT)||LA23_396==KW_TABLES||(LA23_396 >= KW_TBLPROPERTIES && LA23_396 <= KW_TERMINATED)||LA23_396==KW_TINYINT||(LA23_396 >= KW_TOUCH && LA23_396 <= KW_TRANSACTIONS)||LA23_396==KW_UNARCHIVE||LA23_396==KW_UNDO||LA23_396==KW_UNIONTYPE||(LA23_396 >= KW_UNLOCK && LA23_396 <= KW_UNSIGNED)||(LA23_396 >= KW_URI && LA23_396 <= KW_USE)||(LA23_396 >= KW_UTC && LA23_396 <= KW_UTCTIMESTAMP)||LA23_396==KW_VALUE_TYPE||LA23_396==KW_VIEW||LA23_396==KW_WHILE||LA23_396==KW_YEAR) ) {s = 478;}

                        else if ( ((LA23_396 >= KW_ALL && LA23_396 <= KW_ALTER)||(LA23_396 >= KW_ARRAY && LA23_396 <= KW_AS)||LA23_396==KW_AUTHORIZATION||(LA23_396 >= KW_BETWEEN && LA23_396 <= KW_BOTH)||LA23_396==KW_BY||LA23_396==KW_CREATE||LA23_396==KW_CUBE||(LA23_396 >= KW_CURRENT_DATE && LA23_396 <= KW_CURSOR)||LA23_396==KW_DATE||LA23_396==KW_DECIMAL||LA23_396==KW_DELETE||LA23_396==KW_DESCRIBE||(LA23_396 >= KW_DOUBLE && LA23_396 <= KW_DROP)||LA23_396==KW_EXISTS||(LA23_396 >= KW_EXTERNAL && LA23_396 <= KW_FETCH)||LA23_396==KW_FLOAT||LA23_396==KW_FOR||LA23_396==KW_FULL||(LA23_396 >= KW_GRANT && LA23_396 <= KW_GROUPING)||(LA23_396 >= KW_IMPORT && LA23_396 <= KW_IN)||LA23_396==KW_INNER||(LA23_396 >= KW_INSERT && LA23_396 <= KW_INTERSECT)||(LA23_396 >= KW_INTO && LA23_396 <= KW_IS)||(LA23_396 >= KW_LATERAL && LA23_396 <= KW_LEFT)||LA23_396==KW_LIKE||LA23_396==KW_LOCAL||LA23_396==KW_NONE||(LA23_396 >= KW_NULL && LA23_396 <= KW_OF)||(LA23_396 >= KW_ORDER && LA23_396 <= KW_OUTER)||LA23_396==KW_PARTITION||LA23_396==KW_PERCENT||LA23_396==KW_PROCEDURE||LA23_396==KW_RANGE||LA23_396==KW_READS||LA23_396==KW_REVOKE||LA23_396==KW_RIGHT||(LA23_396 >= KW_ROLLUP && LA23_396 <= KW_ROWS)||LA23_396==KW_SET||LA23_396==KW_SMALLINT||LA23_396==KW_TABLE||LA23_396==KW_TIMESTAMP||LA23_396==KW_TO||(LA23_396 >= KW_TRIGGER && LA23_396 <= KW_TRUNCATE)||LA23_396==KW_UNION||LA23_396==KW_UPDATE||(LA23_396 >= KW_USER && LA23_396 <= KW_USING)||LA23_396==KW_VALUES||LA23_396==KW_WITH) ) {s = 479;}

                         
                        input.seek(index23_396);

                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA23_425 = input.LA(1);

                         
                        int index23_425 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred2_SelectClauseParser()) ) {s = 476;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index23_425);

                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA23_426 = input.LA(1);

                         
                        int index23_426 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred2_SelectClauseParser()) ) {s = 476;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index23_426);

                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA23_427 = input.LA(1);

                         
                        int index23_427 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred2_SelectClauseParser()) ) {s = 476;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index23_427);

                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA23_429 = input.LA(1);

                         
                        int index23_429 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred2_SelectClauseParser()) ) {s = 476;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index23_429);

                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA23_430 = input.LA(1);

                         
                        int index23_430 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred2_SelectClauseParser()) ) {s = 476;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index23_430);

                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA23_431 = input.LA(1);

                         
                        int index23_431 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred2_SelectClauseParser()) ) {s = 476;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index23_431);

                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA23_433 = input.LA(1);

                         
                        int index23_433 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index23_433);

                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA23_434 = input.LA(1);

                         
                        int index23_434 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index23_434);

                        if ( s>=0 ) return s;
                        break;
                    case 23 : 
                        int LA23_435 = input.LA(1);

                         
                        int index23_435 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index23_435);

                        if ( s>=0 ) return s;
                        break;
                    case 24 : 
                        int LA23_437 = input.LA(1);

                         
                        int index23_437 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index23_437);

                        if ( s>=0 ) return s;
                        break;
                    case 25 : 
                        int LA23_438 = input.LA(1);

                         
                        int index23_438 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index23_438);

                        if ( s>=0 ) return s;
                        break;
                    case 26 : 
                        int LA23_439 = input.LA(1);

                         
                        int index23_439 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index23_439);

                        if ( s>=0 ) return s;
                        break;
                    case 27 : 
                        int LA23_441 = input.LA(1);

                         
                        int index23_441 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index23_441);

                        if ( s>=0 ) return s;
                        break;
                    case 28 : 
                        int LA23_442 = input.LA(1);

                         
                        int index23_442 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index23_442);

                        if ( s>=0 ) return s;
                        break;
                    case 29 : 
                        int LA23_443 = input.LA(1);

                         
                        int index23_443 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index23_443);

                        if ( s>=0 ) return s;
                        break;
                    case 30 : 
                        int LA23_445 = input.LA(1);

                         
                        int index23_445 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index23_445);

                        if ( s>=0 ) return s;
                        break;
                    case 31 : 
                        int LA23_446 = input.LA(1);

                         
                        int index23_446 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index23_446);

                        if ( s>=0 ) return s;
                        break;
                    case 32 : 
                        int LA23_447 = input.LA(1);

                         
                        int index23_447 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index23_447);

                        if ( s>=0 ) return s;
                        break;
                    case 33 : 
                        int LA23_449 = input.LA(1);

                         
                        int index23_449 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index23_449);

                        if ( s>=0 ) return s;
                        break;
                    case 34 : 
                        int LA23_450 = input.LA(1);

                         
                        int index23_450 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index23_450);

                        if ( s>=0 ) return s;
                        break;
                    case 35 : 
                        int LA23_451 = input.LA(1);

                         
                        int index23_451 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index23_451);

                        if ( s>=0 ) return s;
                        break;
                    case 36 : 
                        int LA23_453 = input.LA(1);

                         
                        int index23_453 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index23_453);

                        if ( s>=0 ) return s;
                        break;
                    case 37 : 
                        int LA23_454 = input.LA(1);

                         
                        int index23_454 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index23_454);

                        if ( s>=0 ) return s;
                        break;
                    case 38 : 
                        int LA23_455 = input.LA(1);

                         
                        int index23_455 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index23_455);

                        if ( s>=0 ) return s;
                        break;
                    case 39 : 
                        int LA23_457 = input.LA(1);

                         
                        int index23_457 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index23_457);

                        if ( s>=0 ) return s;
                        break;
                    case 40 : 
                        int LA23_458 = input.LA(1);

                         
                        int index23_458 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index23_458);

                        if ( s>=0 ) return s;
                        break;
                    case 41 : 
                        int LA23_459 = input.LA(1);

                         
                        int index23_459 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index23_459);

                        if ( s>=0 ) return s;
                        break;
                    case 42 : 
                        int LA23_461 = input.LA(1);

                         
                        int index23_461 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index23_461);

                        if ( s>=0 ) return s;
                        break;
                    case 43 : 
                        int LA23_462 = input.LA(1);

                         
                        int index23_462 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index23_462);

                        if ( s>=0 ) return s;
                        break;
                    case 44 : 
                        int LA23_463 = input.LA(1);

                         
                        int index23_463 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index23_463);

                        if ( s>=0 ) return s;
                        break;
                    case 45 : 
                        int LA23_465 = input.LA(1);

                         
                        int index23_465 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred2_SelectClauseParser()) ) {s = 476;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index23_465);

                        if ( s>=0 ) return s;
                        break;
                    case 46 : 
                        int LA23_466 = input.LA(1);

                         
                        int index23_466 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred2_SelectClauseParser()) ) {s = 476;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index23_466);

                        if ( s>=0 ) return s;
                        break;
                    case 47 : 
                        int LA23_467 = input.LA(1);

                         
                        int index23_467 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred2_SelectClauseParser()) ) {s = 476;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index23_467);

                        if ( s>=0 ) return s;
                        break;
                    case 48 : 
                        int LA23_469 = input.LA(1);

                         
                        int index23_469 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index23_469);

                        if ( s>=0 ) return s;
                        break;
                    case 49 : 
                        int LA23_470 = input.LA(1);

                         
                        int index23_470 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index23_470);

                        if ( s>=0 ) return s;
                        break;
                    case 50 : 
                        int LA23_471 = input.LA(1);

                         
                        int index23_471 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index23_471);

                        if ( s>=0 ) return s;
                        break;
                    case 51 : 
                        int LA23_473 = input.LA(1);

                         
                        int index23_473 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index23_473);

                        if ( s>=0 ) return s;
                        break;
                    case 52 : 
                        int LA23_474 = input.LA(1);

                         
                        int index23_474 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index23_474);

                        if ( s>=0 ) return s;
                        break;
                    case 53 : 
                        int LA23_475 = input.LA(1);

                         
                        int index23_475 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index23_475);

                        if ( s>=0 ) return s;
                        break;
                    case 54 : 
                        int LA23_477 = input.LA(1);

                         
                        int index23_477 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index23_477);

                        if ( s>=0 ) return s;
                        break;
                    case 55 : 
                        int LA23_478 = input.LA(1);

                         
                        int index23_478 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index23_478);

                        if ( s>=0 ) return s;
                        break;
                    case 56 : 
                        int LA23_479 = input.LA(1);

                         
                        int index23_479 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (((synpred2_SelectClauseParser()&&synpred2_SelectClauseParser())&&(useSQL11ReservedKeywordsForIdentifier()))) ) {s = 476;}

                        else if ( ((useSQL11ReservedKeywordsForIdentifier())) ) {s = 5;}

                         
                        input.seek(index23_479);

                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}

            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 23, _s, input);
            error(nvae);
            throw nvae;
        }

    }
 

    public static final BitSet FOLLOW_KW_SELECT_in_selectClause71 = new BitSet(new long[]{0xFDEFFFFDFC04A080L,0xDEBBFDEAFFFFFBD6L,0x6FAFF7F7FEF7FFFFL,0xDFFFFF7FFFF7FF9FL,0x0F80C91A5FFEEFFDL});
    public static final BitSet FOLLOW_hintClause_in_selectClause73 = new BitSet(new long[]{0xFDEFFFFDFC042080L,0xDEBBFDEAFFFFFBD6L,0x6FAFF7F7FEF7FFFFL,0xDFFFFF7FFFF7FF9FL,0x0F80C91A5FFEEFFDL});
    public static final BitSet FOLLOW_KW_ALL_in_selectClause79 = new BitSet(new long[]{0xFDEFFFFDFC042080L,0xDEBBFDEAF7FFFBD6L,0x6FAFF7F7FEF7FFFFL,0xDFFFFF7FFFF7FF9FL,0x0F80C91A5FFEEF7DL});
    public static final BitSet FOLLOW_KW_DISTINCT_in_selectClause85 = new BitSet(new long[]{0xFDEFFFFDFC042080L,0xDEBBFDEAF7FFFBD6L,0x6FAFF7F7FEF7FFFFL,0xDFFFFF7FFFF7FF9FL,0x0F80C91A5FFEEF7DL});
    public static final BitSet FOLLOW_selectList_in_selectClause89 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_TRANSFORM_in_selectClause123 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_selectTrfmClause_in_selectClause125 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_trfmClause_in_selectClause196 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_selectItem_in_selectList239 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_COMMA_in_selectList243 = new BitSet(new long[]{0xFDEFFFFDFC042080L,0xDEBBFDEAF7FFFBD6L,0x6FAFF7F7FEF7FFFFL,0xDFFFFF7FFFF7FF9FL,0x0F80C91A5FFEEF7DL});
    public static final BitSet FOLLOW_selectItem_in_selectList246 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_LPAREN_in_selectTrfmClause285 = new BitSet(new long[]{0xFDEFFFFDFC042080L,0xDEBBFDEAF7FFFBD6L,0x6FAFF7F7FEF7FFFFL,0xDFFFFF7FFFF7FF9FL,0x0F80C91A5FFEEF7DL});
    public static final BitSet FOLLOW_selectExpressionList_in_selectTrfmClause287 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_RPAREN_in_selectTrfmClause289 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000400040000L,0x0000000001000000L});
    public static final BitSet FOLLOW_rowFormat_in_selectTrfmClause297 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000040000L,0x0000000001000000L});
    public static final BitSet FOLLOW_recordWriter_in_selectTrfmClause301 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_KW_USING_in_selectTrfmClause307 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0200000000000000L});
    public static final BitSet FOLLOW_StringLiteral_in_selectTrfmClause309 = new BitSet(new long[]{0x0000001000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000400020000L});
    public static final BitSet FOLLOW_KW_AS_in_selectTrfmClause317 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77E7FEF7BFFDL,0xDFFFFF7FFFF7FF9FL,0x0000011A5FFEEF7DL});
    public static final BitSet FOLLOW_LPAREN_in_selectTrfmClause321 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77E7FEF7BFFDL,0xDFFFFF7FFFF7FF9FL,0x0000001A5FFEEF7DL});
    public static final BitSet FOLLOW_aliasList_in_selectTrfmClause324 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_columnNameTypeList_in_selectTrfmClause328 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_RPAREN_in_selectTrfmClause331 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000400020000L});
    public static final BitSet FOLLOW_aliasList_in_selectTrfmClause337 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000400020000L});
    public static final BitSet FOLLOW_columnNameTypeList_in_selectTrfmClause341 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000400020000L});
    public static final BitSet FOLLOW_rowFormat_in_selectTrfmClause353 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_recordReader_in_selectTrfmClause357 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DIVIDE_in_hintClause420 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_STAR_in_hintClause422 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000800000000000L});
    public static final BitSet FOLLOW_PLUS_in_hintClause424 = new BitSet(new long[]{0x0000000000000000L,0x4000000000000000L,0x0000002000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_hintList_in_hintClause426 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_STAR_in_hintClause428 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_DIVIDE_in_hintClause430 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_hintItem_in_hintList469 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_COMMA_in_hintList472 = new BitSet(new long[]{0x0000000000000000L,0x4000000000000000L,0x0000002000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_hintItem_in_hintList474 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_hintName_in_hintItem512 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_LPAREN_in_hintItem515 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77E7FEF7BFFDL,0xDFFFFF7FFFF7FF9FL,0x0000001A5FFEEF7DL});
    public static final BitSet FOLLOW_hintArgs_in_hintItem517 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_RPAREN_in_hintItem519 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_MAPJOIN_in_hintName563 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_STREAMTABLE_in_hintName575 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_HOLD_DDLTIME_in_hintName587 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_hintArgName_in_hintArgs622 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_COMMA_in_hintArgs625 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77E7FEF7BFFDL,0xDFFFFF7FFFF7FF9FL,0x0000001A5FFEEF7DL});
    public static final BitSet FOLLOW_hintArgName_in_hintArgs627 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_identifier_in_hintArgName669 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tableAllColumns_in_selectItem706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_selectItem728 = new BitSet(new long[]{0xFDE9FFFDFC000002L,0xDEBBFDEAF7FFFBD6L,0x6FAF77E7FEF7BFFDL,0xDFFFFF7FFFF7FF9FL,0x0000001A5FFEEF7DL});
    public static final BitSet FOLLOW_KW_AS_in_selectItem738 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77E7FEF7BFFDL,0xDFFFFF7FFFF7FF9FL,0x0000001A5FFEEF7DL});
    public static final BitSet FOLLOW_identifier_in_selectItem741 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_AS_in_selectItem747 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_LPAREN_in_selectItem749 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77E7FEF7BFFDL,0xDFFFFF7FFFF7FF9FL,0x0000001A5FFEEF7DL});
    public static final BitSet FOLLOW_identifier_in_selectItem751 = new BitSet(new long[]{0x0000000000000400L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_COMMA_in_selectItem754 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77E7FEF7BFFDL,0xDFFFFF7FFFF7FF9FL,0x0000001A5FFEEF7DL});
    public static final BitSet FOLLOW_identifier_in_selectItem756 = new BitSet(new long[]{0x0000000000000400L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_RPAREN_in_selectItem760 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_MAP_in_trfmClause815 = new BitSet(new long[]{0xFDEFFFFDFC042080L,0xDEBBFDEAF7FFFBD6L,0x6FAFF7F7FEF7FFFFL,0xDFFFFF7FFFF7FF9FL,0x0F80C91A5FFEEF7DL});
    public static final BitSet FOLLOW_selectExpressionList_in_trfmClause820 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000400040000L,0x0000000001000000L});
    public static final BitSet FOLLOW_KW_REDUCE_in_trfmClause830 = new BitSet(new long[]{0xFDEFFFFDFC042080L,0xDEBBFDEAF7FFFBD6L,0x6FAFF7F7FEF7FFFFL,0xDFFFFF7FFFF7FF9FL,0x0F80C91A5FFEEF7DL});
    public static final BitSet FOLLOW_selectExpressionList_in_trfmClause832 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000400040000L,0x0000000001000000L});
    public static final BitSet FOLLOW_rowFormat_in_trfmClause842 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000040000L,0x0000000001000000L});
    public static final BitSet FOLLOW_recordWriter_in_trfmClause846 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_KW_USING_in_trfmClause852 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0200000000000000L});
    public static final BitSet FOLLOW_StringLiteral_in_trfmClause854 = new BitSet(new long[]{0x0000001000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000400020000L});
    public static final BitSet FOLLOW_KW_AS_in_trfmClause862 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77E7FEF7BFFDL,0xDFFFFF7FFFF7FF9FL,0x0000011A5FFEEF7DL});
    public static final BitSet FOLLOW_LPAREN_in_trfmClause866 = new BitSet(new long[]{0xFDE9FFFDFC000000L,0xDEBBFDEAF7FFFBD6L,0x6FAF77E7FEF7BFFDL,0xDFFFFF7FFFF7FF9FL,0x0000001A5FFEEF7DL});
    public static final BitSet FOLLOW_aliasList_in_trfmClause869 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_columnNameTypeList_in_trfmClause873 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_RPAREN_in_trfmClause876 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000400020000L});
    public static final BitSet FOLLOW_aliasList_in_trfmClause882 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000400020000L});
    public static final BitSet FOLLOW_columnNameTypeList_in_trfmClause886 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000400020000L});
    public static final BitSet FOLLOW_rowFormat_in_trfmClause898 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_recordReader_in_trfmClause902 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tableAllColumns_in_selectExpression971 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_selectExpression983 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_selectExpression_in_selectExpressionList1014 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_COMMA_in_selectExpressionList1017 = new BitSet(new long[]{0xFDEFFFFDFC042080L,0xDEBBFDEAF7FFFBD6L,0x6FAFF7F7FEF7FFFFL,0xDFFFFF7FFFF7FF9FL,0x0F80C91A5FFEEF7DL});
    public static final BitSet FOLLOW_selectExpression_in_selectExpressionList1019 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_KW_WINDOW_in_window_clause1056 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_window_defn_in_window_clause1058 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_COMMA_in_window_clause1061 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_window_defn_in_window_clause1063 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_Identifier_in_window_defn1095 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_KW_AS_in_window_defn1097 = new BitSet(new long[]{0x0000000004000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_window_specification_in_window_defn1099 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_window_specification1131 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_window_specification1137 = new BitSet(new long[]{0x0020000004000000L,0x0000000010000000L,0x0080000000000000L,0x0008000800001001L,0x0008000000000000L});
    public static final BitSet FOLLOW_Identifier_in_window_specification1139 = new BitSet(new long[]{0x0020000000000000L,0x0000000010000000L,0x0080000000000000L,0x0008000800001001L,0x0008000000000000L});
    public static final BitSet FOLLOW_partitioningSpec_in_window_specification1142 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000800001000L,0x0008000000000000L});
    public static final BitSet FOLLOW_window_frame_in_window_specification1145 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_RPAREN_in_window_specification1148 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_window_range_expression_in_window_frame1175 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_window_value_expression_in_window_frame1180 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_ROWS_in_window_range_expression1200 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L,0x0000000000000000L,0x0000000000000000L,0x0000400000001000L});
    public static final BitSet FOLLOW_window_frame_start_boundary_in_window_range_expression1204 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_ROWS_in_window_range_expression1218 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_KW_BETWEEN_in_window_range_expression1220 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L,0x0000000000000000L,0x0000000000000000L,0x0000400000001000L});
    public static final BitSet FOLLOW_window_frame_boundary_in_window_range_expression1224 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_KW_AND_in_window_range_expression1226 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L,0x0000000000000000L,0x0000000000000000L,0x0000400000001000L});
    public static final BitSet FOLLOW_window_frame_boundary_in_window_range_expression1230 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_RANGE_in_window_value_expression1262 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L,0x0000000000000000L,0x0000000000000000L,0x0000400000001000L});
    public static final BitSet FOLLOW_window_frame_start_boundary_in_window_value_expression1266 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_RANGE_in_window_value_expression1280 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_KW_BETWEEN_in_window_value_expression1282 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L,0x0000000000000000L,0x0000000000000000L,0x0000400000001000L});
    public static final BitSet FOLLOW_window_frame_boundary_in_window_value_expression1286 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_KW_AND_in_window_value_expression1288 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L,0x0000000000000000L,0x0000000000000000L,0x0000400000001000L});
    public static final BitSet FOLLOW_window_frame_boundary_in_window_value_expression1292 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_UNBOUNDED_in_window_frame_start_boundary1325 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_KW_PRECEDING_in_window_frame_start_boundary1327 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_CURRENT_in_window_frame_start_boundary1342 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000400000000L});
    public static final BitSet FOLLOW_KW_ROW_in_window_frame_start_boundary1344 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Number_in_window_frame_start_boundary1357 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_KW_PRECEDING_in_window_frame_start_boundary1359 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_UNBOUNDED_in_window_frame_boundary1388 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_KW_PRECEDING_in_window_frame_boundary1393 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_FOLLOWING_in_window_frame_boundary1397 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_CURRENT_in_window_frame_boundary1414 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000400000000L});
    public static final BitSet FOLLOW_KW_ROW_in_window_frame_boundary1416 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Number_in_window_frame_boundary1429 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_KW_PRECEDING_in_window_frame_boundary1434 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KW_FOLLOWING_in_window_frame_boundary1440 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tableAllColumns_in_synpred1_SelectClauseParser701 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tableAllColumns_in_synpred2_SelectClauseParser966 = new BitSet(new long[]{0x0000000000000002L});

}