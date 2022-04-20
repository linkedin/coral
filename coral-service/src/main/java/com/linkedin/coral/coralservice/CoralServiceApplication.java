package com.linkedin.coral.coralservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class CoralServiceApplication {
  public static final Logger LOGGER=LoggerFactory.getLogger(CoralServiceApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(CoralServiceApplication.class, args);
  }
}
