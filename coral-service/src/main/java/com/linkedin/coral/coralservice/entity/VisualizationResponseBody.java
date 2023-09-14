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

  public VisualizationResponseBody() {
  }

  public UUID getSqlNodeImageID() {
    return sqlNodeImageID;
  }

  public UUID getRelNodeImageID() {
    return relNodeImageID;
  }

  public void setSqlNodeImageID(UUID sqlNodeImageID) {
    this.sqlNodeImageID = sqlNodeImageID;
  }

  public void setRelNodeImageID(UUID relNodeImageID) {
    this.relNodeImageID = relNodeImageID;
  }

}
