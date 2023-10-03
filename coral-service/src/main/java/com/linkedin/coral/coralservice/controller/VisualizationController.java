/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.coralservice.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.linkedin.coral.coralservice.entity.VisualizationRequestBody;
import com.linkedin.coral.coralservice.entity.VisualizationResponseBody;
import com.linkedin.coral.coralservice.utils.RewriteType;
import com.linkedin.coral.coralservice.utils.VisualizationUtils;

import static com.linkedin.coral.coralservice.utils.CommonUtils.*;
import static com.linkedin.coral.coralservice.utils.CoralProvider.*;
import static com.linkedin.coral.coralservice.utils.VisualizationUtils.*;


@RestController
@RequestMapping("/api/visualizations")
@CrossOrigin(origins = CORAL_SERVICE_FRONTEND_URL)
public class VisualizationController {
  private File imageDir = getImageDir();
  private VisualizationUtils visualizationUtils = new VisualizationUtils();

  @PostMapping("/generategraphs")
  public ResponseEntity getIRVisualizations(@RequestBody VisualizationRequestBody visualizationRequestBody) {
    final String sourceLanguage = visualizationRequestBody.getSourceLanguage();
    final String query = visualizationRequestBody.getQuery();
    final RewriteType rewriteType = visualizationRequestBody.getRewriteType();

    if (!isValidSourceLanguage(sourceLanguage)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
          "Currently, only Hive, Spark, and Trino SQL are supported as source languages for Coral IR visualization. \n");
    }

    // A list of UUIDs in this order of:
    // 1. Image ID of pre/no rewrite relNode
    // 2. Image ID of pre/no rewrite sqlNode
    // If a rewrite was requested:
    // 3. Image ID of post rewrite relNode
    // 4. Image ID of post rewrite sqlNode
    ArrayList<UUID> imageIdList;
    try {
      imageIdList = visualizationUtils.generateIRVisualizations(query, sourceLanguage, imageDir, rewriteType);
    } catch (Throwable t) {
      // TODO: use logger
      t.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(t.getMessage());
    }

    assert imageIdList.size() > 0;

    // Build response body
    VisualizationResponseBody responseBody = new VisualizationResponseBody();
    responseBody.setRelNodeImageID(imageIdList.get(0));
    responseBody.setSqlNodeImageID(imageIdList.get(1));
    if (imageIdList.size() >= 4) {
      // Rewrite was requested
      responseBody.setPostRewriteRelNodeImageID(imageIdList.get(2));
      responseBody.setPostRewriteSqlNodeImageID(imageIdList.get(3));
    }

    return ResponseEntity.status(HttpStatus.OK).body(responseBody);
  }

  @GetMapping("/{imageId}")
  public ResponseEntity<FileSystemResource> getImage(@PathVariable String imageId) {
    String imagePath = imageDir + File.separator + imageId + ".svg";

    if (isValidImage(imagePath)) {
      try {
        Path path = new File(imagePath).toPath();
        String contentType = Files.probeContentType(path);

        if (contentType == null) {
          contentType = "image/svg+xml";
        }
        FileSystemResource resource = new FileSystemResource(path);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);

      } catch (IOException e) {
        e.printStackTrace();
        return ResponseEntity.status(500).build(); // 500 Internal Server Error response
      }

    } else {
      return ResponseEntity.notFound().build();
    }
  }

  private boolean isValidImage(String imagePath) {
    // Check if the file exists and is a regular file (not a directory)
    File imageFile = new File(imagePath);
    return imageFile.exists() && imageFile.isFile();
  }
}
