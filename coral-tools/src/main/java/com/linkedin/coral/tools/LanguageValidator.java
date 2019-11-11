package com.linkedin.coral.tools;

import com.linkedin.coral.hive.hive2rel.HiveMetastoreClient;
import java.io.PrintWriter;


/**
 * Validator that determines if a DaliView given by db.table can be translated to the validator's language.
 */
interface LanguageValidator {

  /**
   * Gets the name of the language in camel case.
   *
   * @return Camel cased name
   */
  String getCamelName();

  /**
   * Gets the name of the language in standard casing.
   *
   * @return Standard cased name
   */
  String getStandardName();

  /**
   * Converts the given db.table to the validators target language.
   * The translated query is translated and the translation results are written to the outputWriter.
   *
   * @param db Database name.
   * @param table Table name in the given database.
   * @param hiveMetastoreClient HiveMetastoreClient containing the given db.table.
   * @param outputWriter The writer which the results will be written to.
   */
  void convertAndValidate(String db, String table, HiveMetastoreClient hiveMetastoreClient, PrintWriter outputWriter);

}
