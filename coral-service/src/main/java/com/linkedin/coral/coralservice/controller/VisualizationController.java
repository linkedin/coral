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

import static com.linkedin.coral.coralservice.utils.VisualizationUtils.*;


@RestController
@RequestMapping("/api/visualization")
public class VisualizationController {
  private File imageDir = createImageDir();

  @PostMapping("/generategraphs")
  public ResponseEntity getIRVisualizations(@RequestBody VisualizationRequestBody visualizationRequestBody) {
    final String fromLanguage = visualizationRequestBody.getFromLanguage();
    final String query = visualizationRequestBody.getQuery();
    //    final VisualizationRequestBody.RewriteType rewriteType = visualizationRequestBody.getRewriteType();
    UUID sqlNodeImageID;
    UUID relNodeImageID;

    try {
      sqlNodeImageID = generateSqlNodeVisualization(query, fromLanguage, imageDir);
      relNodeImageID = generateRelNodeVisualization(query, fromLanguage, imageDir);
    } catch (Throwable t) {
      t.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(t.getMessage());
    }
    VisualizationResponseBody responseBody = new VisualizationResponseBody();
    responseBody.setSqlNodeImageID(sqlNodeImageID);
    responseBody.setRelNodeImageID(relNodeImageID);

    return ResponseEntity.status(HttpStatus.OK).body(responseBody);
  }

  @GetMapping("/{imageId}")
  public ResponseEntity<FileSystemResource> getImage(@PathVariable String imageId) {
    String imagePath = imageDir + File.separator + imageId + ".svg";

    if (isValidImageId(imagePath)) {
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

  private boolean isValidImageId(String imagePath) {
    File imageFile = new File(imagePath);
    // Check if the file exists and is a regular file (not a directory)
    return imageFile.exists() && imageFile.isFile();
  }
}
