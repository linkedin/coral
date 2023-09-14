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
import java.util.UUID;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.linkedin.coral.coralservice.entity.VisualizationRequestBody;
import com.linkedin.coral.coralservice.entity.VisualizationResponseBody;
import com.linkedin.coral.coralservice.utils.RewriteType;

import static com.linkedin.coral.coralservice.utils.VisualizationUtils.*;


@RestController
@RequestMapping("/api/visualizations")
public class VisualizationController {
  private File imageDir = createImageDir();

  @PostMapping("/generategraphs")
  public ResponseEntity getIRVisualizations(@RequestBody VisualizationRequestBody visualizationRequestBody) {
    final String fromLanguage = visualizationRequestBody.getFromLanguage();
    final String query = visualizationRequestBody.getQuery();
    final RewriteType rewriteType = visualizationRequestBody.getRewriteType();

    UUID sqlNodeImageID, relNodeImageID;
    UUID postRewriteSqlNodeImageID = null;
    UUID postRewriteRelNodeImageID = null;

    try {
      // Always generate the pre/no rewrite images first
      sqlNodeImageID = generateSqlNodeVisualization(query, fromLanguage, imageDir, RewriteType.NONE);
      relNodeImageID = generateRelNodeVisualization(query, fromLanguage, imageDir, RewriteType.NONE);
      assert !sqlNodeImageID.equals(relNodeImageID);

      if (rewriteType != RewriteType.NONE && rewriteType != null) {
        // A rewrite was requested
        postRewriteRelNodeImageID = generateRelNodeVisualization(query, fromLanguage, imageDir, rewriteType);
        postRewriteSqlNodeImageID = generateSqlNodeVisualization(query, fromLanguage, imageDir, rewriteType);
        assert !postRewriteSqlNodeImageID.equals(postRewriteRelNodeImageID);
      }

    } catch (Throwable t) {
      t.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(t.getMessage());
    }

    // Build response body
    VisualizationResponseBody responseBody = new VisualizationResponseBody();
    responseBody.setSqlNodeImageID(sqlNodeImageID);
    responseBody.setRelNodeImageID(relNodeImageID);
    responseBody.setPostRewriteSqlNodeImageID(postRewriteSqlNodeImageID);
    responseBody.setPostRewriteRelNodeImageID(postRewriteRelNodeImageID);

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
