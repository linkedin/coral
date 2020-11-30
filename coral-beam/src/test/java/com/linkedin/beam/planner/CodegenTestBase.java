/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.planner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;


public class CodegenTestBase {
  private static final String ROOT_DIR = String.join(File.separator, System.getProperty("user.dir"), "src/test");
  private static final String BEAM_CODE_DIR = String.join(File.separator, ROOT_DIR, "beamGeneratedCode");
  private static final String TIMESTAMP_FIELD = "timestamp";
  private static final boolean TEST_RESET = false;

  public static CalciteBeamConfig makeBeamConfig(String appName, String outputName) {
    return new CalciteBeamConfig(appName, TIMESTAMP_FIELD, outputName);
  }

  public static void testBeamCodeGen(CalciteBeamConfig config, String pigQuery) throws Exception {
    String javaFileName = config.applicationName + ".java";
    String generatedCode = new BeamCodeGenerator(config).generateJavaCodeFromPigQuery(pigQuery);
    validateCode(javaFileName, generatedCode);
  }

  private static void validateCode(String javaFileName, String generatedCode) throws Exception {
    compileSource(generatedCode, javaFileName);
    String expectedCodeFile = String.join(File.separator, BEAM_CODE_DIR, javaFileName);
    checkResult(expectedCodeFile, generatedCode);
  }

  private static void compileSource(String source, String javaFileName) throws IOException {
    String tmpProperty = System.getProperty("java.io.tmpdir");
    Path javaFile = Paths.get(tmpProperty, javaFileName);
    Files.write(javaFile, source.getBytes(StandardCharsets.UTF_8));
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
    int status = compiler.run(null, null, errorStream, javaFile.toFile().getAbsolutePath());
    String errorMessage = errorStream.toString();
    Assert.assertEquals(0, status, errorMessage);
  }

  private static void checkResult(String expectedFile, String code) throws IOException {
    if (TEST_RESET) {
      FileUtils.write(new File(expectedFile), code);
    } else {
      Assert.assertEquals(code, FileUtils.readFileToString(new File(expectedFile)));
    }
  }
}
