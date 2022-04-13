package com.linkedin.coral.coralservice.controller;

import static com.linkedin.coral.coralservice.utils.CoralUtils.*;
import static com.linkedin.coral.coralservice.utils.TranslationUtils.*;


import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * Class defining the REST endpoints to translate queries using Coral.
 */
@RestController
@Service
@Profile({"hiveMetastoreClient", "default"})
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
  public ResponseEntity translate(@RequestParam String query, @RequestParam String fromLanguage, @RequestParam String toLanguage) {
    if (fromLanguage.equalsIgnoreCase(toLanguage)) {
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body("Please choose different languages to translate between.");
    }

    String translatedSql = "Translation not supported yet.";

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

    String successMessage = String.format("Original query in " + fromLanguage + ": " + query + "   |   " + "Translated to " + toLanguage + ": " + translatedSql);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(successMessage);
  }
}
