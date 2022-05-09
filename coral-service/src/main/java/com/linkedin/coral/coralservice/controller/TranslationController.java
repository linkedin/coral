/**
 * Copyright 2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.coralservice.controller;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.linkedin.coral.coralservice.utils.CoralProvider.*;
import static com.linkedin.coral.coralservice.utils.TranslationUtils.*;


/**
 * Class defining the REST endpoints to translate queries using Coral.
 */
@RestController
@Service
@Profile({ "remoteMetastore", "default" })
public class TranslationController implements ApplicationListener<ContextRefreshedEvent> {

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    // runs after the Spring context has been initialized
    try {
      initHiveMetastoreClient();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @GetMapping("/translate")
  public ResponseEntity translate(@RequestParam String query, @RequestParam String fromLanguage,
      @RequestParam String toLanguage) {
    if (fromLanguage.equalsIgnoreCase(toLanguage)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Please choose different languages to translate between.");
    }

    String translatedSql = null;

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

    String message;
    if (translatedSql == null) {
      message = "Translation from " + fromLanguage + " to " + toLanguage + " is not currently supported."
          + " Coral-Service only supports translation from Hive to Trino/Spark, or translation from Trino to Spark.\n";
    } else {
      message = "Original query in " + fromLanguage + ": " + query + "\n" + "Translated to " + toLanguage + ": "
          + translatedSql + "\n";
    }
    return ResponseEntity.status(HttpStatus.OK).body(message);
  }
}
