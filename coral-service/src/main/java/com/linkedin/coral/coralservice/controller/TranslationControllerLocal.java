package com.linkedin.coral.coralservice.controller;

import com.linkedin.coral.coralservice.utils.CoralUtils;
import org.apache.hadoop.hive.ql.session.SessionState;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.linkedin.coral.coralservice.utils.CoralUtils.*;


/**
 * Additional REST endpoints for interacting with the local metastore.
 */
@Service
@Profile("localMetastore")
public class TranslationControllerLocal extends TranslationController{

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    // runs after the Spring context has been initialized
    try {
      CoralUtils.initLocalMetastore();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @PostMapping("/create")
  public ResponseEntity createInLocalMetastore(@RequestParam String createQuery) {
    String[] splitQuery = createQuery.split("\\s+");
    if (!queryIsCreate(splitQuery)) {
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body("Only queries starting with \"CREATE DATABASE|TABLE|VIEW\" are accepted.");
    }

    SessionState.start(conf);
    run(driver, createQuery);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body("Creation successful");
  }

  /** Method to check that query starts with "CREATE DATABASE|TABLE|VIEW" */
  private boolean queryIsCreate(String[] splitQuery) {
    if (!splitQuery[0].equalsIgnoreCase("create")
        || (!splitQuery[1].equalsIgnoreCase("table") && !splitQuery[1].equalsIgnoreCase("database") && !splitQuery[1].equalsIgnoreCase("view"))) {
      return false;
    }
    return true;
  }
}
