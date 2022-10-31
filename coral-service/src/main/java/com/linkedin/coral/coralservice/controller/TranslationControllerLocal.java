/**
 * Copyright 2022 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.coralservice.controller;

import org.apache.hadoop.hive.ql.session.SessionState;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.linkedin.coral.coralservice.utils.CoralProvider;

import static com.linkedin.coral.coralservice.utils.CoralProvider.*;


/**
 * Additional REST endpoints for interacting with the local metastore.
 */
@Service
@Profile("localMetastore")
public class TranslationControllerLocal extends TranslationController {

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    // runs after the Spring context has been initialized
    try {
      CoralProvider.initLocalMetastore();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @PostMapping("/api/catalog-ops/execute")
  public ResponseEntity createInLocalMetastore(@RequestBody String statement) {
    String[] splitQuery = statement.split("\\s+");
    if (!isCreateQuery(splitQuery)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Only queries starting with \"CREATE DATABASE|TABLE|VIEW\" are accepted.\n");
    }

    SessionState.start(conf);
    run(driver, statement);

    return ResponseEntity.status(HttpStatus.OK).body("Creation successful\n");
  }

  /** Method to check that query starts with "CREATE DATABASE|TABLE|VIEW" */
  private boolean isCreateQuery(String[] splitQuery) {
    if (splitQuery.length < 3) {
      return false;
    }

    return splitQuery[0].equalsIgnoreCase("create") && (splitQuery[1].equalsIgnoreCase("table")
        || splitQuery[1].equalsIgnoreCase("database") || splitQuery[1].equalsIgnoreCase("view"));
  }
}
