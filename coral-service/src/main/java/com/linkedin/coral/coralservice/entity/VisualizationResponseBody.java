/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.coralservice.entity;

import java.util.UUID;


public class VisualizationResponseBody {

  private UUID sqlNodeImageID;
  private UUID relNodeImageID;
  private UUID postRewriteSqlNodeImageID;
  private UUID postRewriteRelNodeImageID;
  public VisualizationResponseBody() {
  }

  public UUID getSqlNodeImageID() {
    return sqlNodeImageID;
  }

  public UUID getRelNodeImageID() {
    return relNodeImageID;
  }

  public UUID getPostRewriteSqlNodeImageID() {
    return postRewriteSqlNodeImageID;
  }

  public void setPostRewriteSqlNodeImageID(UUID postRewriteSqlNodeImageID) {
    this.postRewriteSqlNodeImageID = postRewriteSqlNodeImageID;
  }

  public void setSqlNodeImageID(UUID sqlNodeImageID) {
    this.sqlNodeImageID = sqlNodeImageID;
  }

  public void setRelNodeImageID(UUID relNodeImageID) {
    this.relNodeImageID = relNodeImageID;
  }

  public UUID getPostRewriteRelNodeImageID() {
    return postRewriteRelNodeImageID;
  }

  public void setPostRewriteRelNodeImageID(UUID postRewriteRelNodeImageID) {
    this.postRewriteRelNodeImageID = postRewriteRelNodeImageID;
  }

}
