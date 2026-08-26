/**
 * Copyright 2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.common.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.BasicSqlType;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.validate.SqlValidator;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


public class TypeDerivationUtilTest {

  private static final String TOP_SQL = "SELECT a FROM foo";

  /**
   * When type derivation genuinely fails, the RuntimeException must carry the underlying failure as its
   * cause. Without it, unrelated root causes (for example an authorization denial raised while loading a
   * base table's schema) are silently discarded and resurface only as an opaque type derivation error.
   */
  @Test
  public void testFailedDerivationPropagatesCause() throws Exception {
    RuntimeException sentinel = new RuntimeException("access denied while loading base table schema");
    TypeDerivationUtil typeDerivationUtil =
        new TypeDerivationUtil(failingValidator(sentinel), SqlParser.create(TOP_SQL).parseQuery());

    try {
      typeDerivationUtil.getRelDataType(new SqlIdentifier("a", SqlParserPos.ZERO));
      fail("Expected type derivation to fail");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().startsWith("Failed to derive the RelDataType for SqlNode:"),
          "Unexpected message: " + e.getMessage());
      assertNotNull(e.getCause(), "RuntimeException must chain the underlying derivation failure");
      assertSame(e.getCause(), sentinel);
    }
  }

  /**
   * Capturing the failures must not perturb the success path: a type already known to the validator is
   * still returned directly, without any exception.
   */
  @Test
  public void testCachedTypeIsReturnedWithoutFailure() throws Exception {
    RelDataType expectedType = new BasicSqlType(RelDataTypeSystem.DEFAULT, SqlTypeName.INTEGER);
    TypeDerivationUtil typeDerivationUtil =
        new TypeDerivationUtil(cachingValidator(expectedType), SqlParser.create(TOP_SQL).parseQuery());

    assertSame(typeDerivationUtil.getRelDataType(new SqlIdentifier("a", SqlParserPos.ZERO)), expectedType);
  }

  /**
   * A SqlValidator whose validate() always fails, so every derivation attempt is exhausted.
   */
  private static SqlValidator failingValidator(final RuntimeException sentinel) {
    return newValidatorProxy(new InvocationHandler() {
      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("validate".equals(method.getName())) {
          throw sentinel;
        }
        return defaultValue(proxy, method, args);
      }
    });
  }

  /**
   * A SqlValidator that already knows the type of the requested node.
   */
  private static SqlValidator cachingValidator(final RelDataType cachedType) {
    return newValidatorProxy(new InvocationHandler() {
      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("getValidatedNodeTypeIfKnown".equals(method.getName())) {
          return cachedType;
        }
        return defaultValue(proxy, method, args);
      }
    });
  }

  private static SqlValidator newValidatorProxy(InvocationHandler handler) {
    return (SqlValidator) Proxy.newProxyInstance(TypeDerivationUtilTest.class.getClassLoader(),
        new Class<?>[] { SqlValidator.class }, handler);
  }

  /**
   * Returns a benign value for any method the tests do not care about, so unrelated SqlValidator calls
   * neither fail nor unbox a null primitive.
   */
  private static Object defaultValue(Object proxy, Method method, Object[] args) {
    String name = method.getName();
    if ("toString".equals(name)) {
      return "TestSqlValidator";
    }
    if ("hashCode".equals(name)) {
      return System.identityHashCode(proxy);
    }
    if ("equals".equals(name)) {
      return proxy == args[0];
    }

    Class<?> returnType = method.getReturnType();
    if (!returnType.isPrimitive()) {
      return null;
    }
    if (returnType == boolean.class) {
      return false;
    }
    if (returnType == char.class) {
      return (char) 0;
    }
    if (returnType == byte.class) {
      return (byte) 0;
    }
    if (returnType == short.class) {
      return (short) 0;
    }
    if (returnType == int.class) {
      return 0;
    }
    if (returnType == long.class) {
      return 0L;
    }
    if (returnType == float.class) {
      return 0.0f;
    }
    if (returnType == double.class) {
      return 0.0d;
    }
    return null;
  }
}
