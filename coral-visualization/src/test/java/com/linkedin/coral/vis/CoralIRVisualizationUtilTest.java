/**
 * Copyright 2023 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.vis;

import java.io.File;
import java.util.UUID;

import org.testng.annotations.Test;

import static org.testng.Assert.*;


public class CoralIRVisualizationUtilTest {

  @Test
  public void testBasicQuery() {
    File imagesTempDir = new File(System.getProperty("java.io.tmpdir") + "/images" + UUID.randomUUID());
    CoraIlRVisualizationUtil visualizationUtil = CoraIlRVisualizationUtil.create(imagesTempDir);
    visualizationUtil.visualizeCoralSqlNodeToFile("SELECT * FROM foo, bar WHERE a = 1", "/test1.png");
    // For now we test the presence of the file.
    // In the future, we can add tests to validate the structure of the generated graph by inspecting the corresponding
    // JSON file.
    assertEquals(imagesTempDir.list().length, 1);
    imagesTempDir.delete();
  }
}
