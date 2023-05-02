/**
 * Copyright 2022-2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.coralservice.controller;

import java.util.List;

import com.google.common.collect.ImmutableMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.linkedin.coral.coralservice.entity.IncrementalRequestBody;
import com.linkedin.coral.coralservice.entity.IncrementalResponseBody;
import com.linkedin.coral.coralservice.entity.TranslateRequestBody;

import static com.linkedin.coral.coralservice.utils.CoralProvider.*;
import static com.linkedin.coral.coralservice.utils.IncrementalUtils.*;
import static com.linkedin.coral.coralservice.utils.TranslationUtils.*;


/**
 * Class defining the REST endpoints to translate queries using Coral.
 */
@RestController
@Service
@Profile({ "remoteMetastore", "default" })
public class TranslationController implements ApplicationListener<ContextRefreshedEvent> {
  @Value("${hivePropsLocation:}")
  private String hivePropsLocation;

  private final static ImmutableMap<String, String> LANGUAGE_MAP =
      ImmutableMap.of("hive", "Hive QL", "trino", "Trino SQL", "spark", "Spark SQL");

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    // runs after the Spring context has been initialized
    try {
      initHiveMetastoreClient(hivePropsLocation);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @PostMapping("/api/translations/translate")
  public ResponseEntity translate(@RequestBody TranslateRequestBody translateRequestBody) {
    final String fromLanguage = translateRequestBody.getFromLanguage();
    final String toLanguage = translateRequestBody.getToLanguage();
    final String query = translateRequestBody.getQuery();

    if (fromLanguage.equalsIgnoreCase(toLanguage)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Please choose different languages to translate between.\n");
    }

    String translatedSql = null;

    try {
      // TODO: add more translations once n-to-one-to-n is completed
      // From Trino
      if (fromLanguage.equalsIgnoreCase("trino")) {
        // To Spark
        if (toLanguage.equalsIgnoreCase("spark")) {
          translatedSql = translateTrinoToSpark(query);
        }
      }
      // From Hive
      else if (fromLanguage.equalsIgnoreCase("hive")) {
        // To Spark
        if (toLanguage.equalsIgnoreCase("spark")) {
          translatedSql = translateHiveToSpark(query);
        }
        // To Trino
        else if (toLanguage.equalsIgnoreCase("trino")) {
          translatedSql = translateHiveToTrino(query);
        }
      }
    } catch (Throwable t) {
      t.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(t.getMessage());
    }

    String message;
    if (translatedSql == null) {
      message = "Translation from " + LANGUAGE_MAP.get(fromLanguage) + " to " + LANGUAGE_MAP.get(toLanguage)
          + " is not currently supported."
          + " Coral-Service only supports translation from Hive to Trino/Spark, or translation from Trino to Spark.\n";
    } else {
      message = "Original query in " + LANGUAGE_MAP.get(fromLanguage) + ":\n" + query + "\n" + "Translated to "
          + LANGUAGE_MAP.get(toLanguage) + ":\n" + translatedSql + "\n";
    }
    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  @PostMapping("/api/incremental/rewrite")
  public ResponseEntity getIncrementalInfo(@RequestBody IncrementalRequestBody incrementalRequestBody)
      throws JSONException {
    final String query = incrementalRequestBody.getQuery();
    final List<String> tableNames = incrementalRequestBody.getTableNames();
    final String language = incrementalRequestBody.getLanguage();

    // Response will contain incremental query and incremental table names
    IncrementalResponseBody incrementalResponseBody = new IncrementalResponseBody();
    incrementalResponseBody.setIncrementalQuery(null);
    try {
      if (language.equalsIgnoreCase("spark")) {
        String incrementalQuery = getSparkIncrementalQueryFromUserSql(query);
        for (String tableName : tableNames) {
          /* Generate underscore delimited and incremental table names
           Table name: db.t1
           Underscore delimited name: db_t1
           Incremental table name: db_t1_delta
          */
          String underscoreDelimitedName = tableName.replace('.', '_');
          String incrementalTableName = underscoreDelimitedName + "_delta";
          incrementalResponseBody.addUnderscoreDelimitedTableName(underscoreDelimitedName);
          incrementalResponseBody.addIncrementalTableName(incrementalTableName);

          // Replace the table names in the incremental query with temp view compatible names
          incrementalQuery = incrementalQuery.replaceAll(tableName, underscoreDelimitedName);
        }
        // Replace newlines with spaces for compatibility with code generation
        incrementalQuery = incrementalQuery.replace('\n', ' ');
        incrementalResponseBody.setIncrementalQuery(incrementalQuery);
      }
    } catch (Throwable t) {
      t.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(t.getMessage());
    }

    String message;
    if (incrementalResponseBody.getIncrementalQuery() == null) {
      message = "Incremental rewrite functionality is not currently supported for " + LANGUAGE_MAP.get(language)
          + ". At the moment, there is support for: Spark.";
    } else {
      // Create JSON object from response body
      JSONObject response = new JSONObject();
      response.put("incremental_maintenance_sql", incrementalResponseBody.getIncrementalQuery());
      response.put("underscore_delimited_table_names", incrementalResponseBody.getUnderscoreDelimitedTableNames());
      response.put("incremental_table_names", incrementalResponseBody.getIncrementalTableNames());

      message = response.toString();
    }
    return ResponseEntity.status(HttpStatus.OK).body(message);
  }
}
