/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.linkedin.beam.excution.BeamExecUtil;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.util.Utf8;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.transforms.WithKeys;
import org.apache.beam.sdk.values.TypeDescriptors;
import org.apache.calcite.linq4j.tree.BlockStatement;
import org.apache.calcite.linq4j.tree.Blocks;
import org.apache.calcite.linq4j.tree.CatchBlock;
import org.apache.calcite.linq4j.tree.ConstantExpression;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.linq4j.tree.ExpressionType;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.linq4j.tree.MemberDeclaration;
import org.apache.calcite.linq4j.tree.ParameterExpression;
import org.apache.calcite.linq4j.tree.Primitive;
import org.apache.calcite.linq4j.tree.Statement;
import org.apache.calcite.linq4j.tree.Types;
import org.apache.calcite.linq4j.tree.UnaryExpression;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.type.SqlTypeFamily;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.commons.lang.ObjectUtils;

import static com.linkedin.beam.utils.MethodNames.*;


public class RexBeamUtils {
  private static final Set<Class> SUPPORTED_STRING_CONVERSION_CLASSES = Sets.newHashSet(
      String.class,
      int.class,
      Integer.class,
      double.class,
      Double.class,
      long.class,
      Long.class,
      boolean.class,
      Boolean.class,
      Utf8.class,
      Map.class,
      ByteBuffer.class,
      GenericRecord.class,
      Object.class);


  private RexBeamUtils() {
  }

  public static Expression makeLambdaFunction(Type functionType, Type returnType, List<ParameterExpression> arguments,
      List<Statement> bodyStatements, Set<Class> exceptions) {
    return Expressions.new_(
        functionType,
        Collections.<Expression>emptyList(),
        Arrays.<MemberDeclaration>asList(
            Expressions.methodDecl(
                Modifier.PUBLIC,
                returnType,
                MethodNames.APPLY,
                arguments,
                wrapTryCatch(bodyStatements, exceptions))));
  }

  public static BlockStatement wrapTryCatch(List<Statement> bodyStatements, Set<Class> exceptions) {
    final BlockStatement body = Expressions.block(bodyStatements);
    if (exceptions != null && !exceptions.isEmpty()) {
      final List<CatchBlock> catches = new ArrayList<>();
      int id = 1;
      final List<Class> sortedExceptions = sortException(exceptions);
      for (Type exceptionType : sortedExceptions) {
        final ParameterExpression exceptionName = Expressions.parameter(exceptionType, "e" + (id++));
        final List<Statement> catchStatements = new ArrayList<>();
        catchStatements.add(Expressions.statement(Expressions.call(exceptionName, MethodNames.PRINT_STACK_TRACE)));
        final String message = "Encounter " + exceptionType.getTypeName() + " during execution. Please check the stack trace";
        catchStatements.add(Expressions.throw_(Expressions.new_(Error.class,
            Expressions.constant(message, String.class), exceptionName)));
        catches.add(Expressions.catch_(exceptionName, Expressions.block(catchStatements)));
      }
      return Expressions.block(Expressions.tryCatch(body, catches));
    }
    return body;
  }

  private static List<Class> sortException(Set<Class> origins) {
    List<Class> sortedList = new ArrayList<>(origins);
    sortedList.sort((o1, o2) -> {
      if (o1.equals(o2)) {
        return 0;
      }
      if (o1.isAssignableFrom(o2)) {
        return 1;
      }

      if (o2.isAssignableFrom(o1)) {
        return -1;
      }
      return o1.getName().compareTo(o2.getName());
    });
    return sortedList;
  }

  public static Expression stripCasting(Expression expr) {
    Expression targetExpr = expr;
    // Remove any casting before to get the original expression
    while (targetExpr.getNodeType() == ExpressionType.Convert) {
      targetExpr = ((UnaryExpression) targetExpr).expression;
    }
    return targetExpr;
  }

  public static Expression toObjectType(Expression expr) {
    Expression targetExpr = stripCasting(expr);
    if (expr instanceof ConstantExpression && Primitive.is(expr.getType())) {
      // Object cast for negative numeric like (Object) -1 does not compile in Java
      // HACK: convert the expression to (Object) Integer.valueOf(-1)
      final ConstantExpression constant = (ConstantExpression) expr;
      if (expr.getType() == int.class) {
        if ((int) constant.value < 0) {
          targetExpr = Expressions.call(Integer.class, VALUE_OF, constant);
        }
      } else if (expr.getType() == long.class) {
        if ((long) constant.value < 0) {
          targetExpr = Expressions.call(Long.class, VALUE_OF, constant);
        }
      } else if (expr.getType() == double.class) {
        if ((double) constant.value < 0) {
          targetExpr = Expressions.call(Double.class, VALUE_OF, constant);
        }
      }
    }

    if (targetExpr.getType() != Object.class) {
      return Expressions.convert_(targetExpr, Object.class);
    }
    return targetExpr;
  }

  public static Expression toJavaStringType(Expression expr) {
    if (SUPPORTED_STRING_CONVERSION_CLASSES.contains(expr.getType())) {
      if (expr.getType() == String.class) {
        return expr;
      }
      if (expr.getType() == int.class
          || expr.getType() == double.class
          || expr.getType() == long.class
          || expr.getType() == boolean.class) {
        return Expressions.call(String.class, MethodNames.VALUE_OF, expr);
      } else {
        return Expressions.call(Methods.OBJECTS_TO_STRING, ImmutableList.of(expr, Expressions.constant(null)));
      }
    }
    // Allow value of any type in a map to be converted into a String
    if (expr.getType() instanceof TypeVariable
        && ((TypeVariable) expr.getType()).getGenericDeclaration() == Map.class
        && ((TypeVariable) expr.getType()).getName().equals("V")) {
      return Expressions.call(Methods.OBJECTS_TO_STRING, ImmutableList.of(expr, Expressions.constant(null)));
    }
    throw new UnsupportedOperationException("Type " + expr.getType() + " conversion to JavaStringType not supported ");
  }

  public static Expression stringCompareExpr(Expression op1, Expression op2) {
    return Expressions.call(ObjectUtils.class, MethodNames.COMPARE, op1, op2);
  }

  public static String getSchemaName(String varName) {
    return varName.toUpperCase() + "_SCHEMA";
  }

  public static ParameterExpression getSchemaParam(String paramName) {
    return Expressions.parameter(Schema.class, paramName);
  }

  /**
   * Builds an exprssion to check whether any expression in an expression list is null.
   *
   * @param exprs List of expression
   * @return Condition expression for checking if any of the expressions is null.
   */
  static Expression buildNullSafeExpr(List<Expression> exprs) {
    Expression result = notNull(exprs.get(0));
    final Set<Expression> visited = new HashSet<>();
    visited.add(exprs.get(0));
    for (int i = 1; i < exprs.size(); i++) {
      if (exprs.get(i).getNodeType() != ExpressionType.Constant
          && !visited.contains(exprs.get(i))) {
        result = Expressions.andAlso(result, notNull(exprs.get(i)));
        visited.add(exprs.get(i));
      }
    }
    return result;
  }

  /**
   * Checks if an expression is not null.
   *
   * @return Condition expression for the check
   */
  static Expression notNull(Expression expr) {
    if (Primitive.is(expr.getType())) {
      return Expressions.constant(true);
    }
    return Expressions.notEqual(expr, Expressions.constant(null));
  }

  /**
   * Casts an expression to a type specified by a rel data type.
   *
   * @param expr Expression to cast
   * @param targetType The target rel data type
   * @return Cast expression
   */
  public static Expression convertType(Expression expr, RelDataType targetType) {
    // Target type should be nullable (or immutable) to be generic.
    final Type targetJavaType = AvroJavaTypeFactory.AVRO_TYPE_FACTORY.getImmutableJavaClass(targetType);
    final Type exprType = expr.getType();

    if (exprType == targetJavaType || exprType == Types.unbox(targetJavaType)) {
      // Same type, no cast needed
      return expr;
    }

    if (targetType.getSqlTypeName().getFamily() == SqlTypeFamily.CHARACTER) {
      // Convert to String type
      return toJavaStringType(expr);
    }

    if (targetType.getSqlTypeName() == SqlTypeName.BINARY) {
      // Convert to binary type
      return Expressions.call(BeamExecUtil.class, MethodNames.TO_BYTE_BUFFER, toObjectType(expr));
    }

    Expression castExpr;
    if (targetType.getSqlTypeName().getFamily() == SqlTypeFamily.NUMERIC && expr.getType() == String.class) {
      // Convert from String to numbers
      castExpr = Expressions.call(targetJavaType, MethodNames.VALUE_OF, expr);
    } else {
      // All remaining cases, rely on Calcite casting expression
      castExpr = Types.castIfNecessary(targetJavaType, expr);
    }

    final Primitive fromPrimitive = Primitive.ofBox(expr.getType());
    final Primitive toPrimitive = Primitive.ofBox(targetJavaType);
    if (fromPrimitive != null && fromPrimitive.isNumeric() && toPrimitive != null && toPrimitive.isNumeric()) {
      // If casting from one numeric to another numeric we need to have null safe protection for avoid
      // NullPointerException when accessing the value of the expression.
      // Example casting var1 of type Integer to Long:
      // Long var2 = var1 != null ? var1.longValue() : null;
      final Expression nullSafeExpr = buildNullSafeExpr(ImmutableList.of(expr));
      return Expressions.condition(nullSafeExpr, castExpr, Expressions.constant(null));
    }
    return castExpr;
  }

  /**
   * Gets a field from an avro record.
   *
   * @param rowType Row type of the relational operator
   * @param index Index of the field to access
   * @param recordName Record variable name for the  relational operator
   * @return An {@link Expression} to represent a field of a record
   */
  public static Expression getFieldExpression(RelDataType rowType, int index, Expression recordName) {
    final String fieldName = RelDataTypeToAvro.toAvroQualifedName(rowType.getFieldNames().get(index));
    final Expression getExpr = Expressions.call(recordName, MethodNames.AVRO_GET, Expressions.constant(fieldName));
    return convertType(getExpr, rowType.getFieldList().get(index).getType());
  }

  /**
   * Gets the string key expression for a list of key columns of a record. This expression will
   * be used in key functions of Beam join or window APIs.
   *
   * @param keyColumns List of key columns
   * @param recordType Relational type of the record
   * @param recordName Record variable name
   * @return Beam code expression representing the string key
   */
  public static Expression getStringKeyExpr(Iterable<Integer> keyColumns, RelDataType recordType, Expression recordName) {
    if (!keyColumns.iterator().hasNext()) {
      return Expressions.constant(null);
    }
    final List<Expression> arguments = new ArrayList<>();
    arguments.add(recordName);
    for (int i : keyColumns) {
      final String fieldName = RelDataTypeToAvro.toAvroQualifedName(recordType.getFieldNames().get(i));
      arguments.add(Expressions.constant(fieldName));
    }
    if (arguments.size() == 1) {
      return arguments.get(0);
    }
    return Expressions.call(BeamExecUtil.class, MethodNames.BUILD_STRING_KEY, arguments);
  }

  /**
   * Constructs the key function for joins or aggregates.
   *
   * @param receiver PCollection expression to apply the key function
   * @param inputRecord Input record expression used to construct key for each record
   * @param keyExpr Function to compute key from inputRecord. Not that this function will use inputRecord as a param
   * @return A {@link Expression} to represent the key function for joins or aggregates.
   */
  public static Expression getBeamKeyFunc(Expression receiver, ParameterExpression inputRecord, Expression keyExpr) {
    final Expression keyFunc =
        RexBeamUtils.makeLambdaFunction(Types.of(SerializableFunction.class, GenericRecord.class, String.class),
            String.class, ImmutableList.of(inputRecord), ImmutableList.of(Blocks.toFunctionBlock(keyExpr)),
            new HashSet<>());
    final Expression withKeysExpr = Expressions.call(WithKeys.class, OF, keyFunc);
    final Expression keyType =
        Expressions.call(withKeysExpr, WITH_KEY_TYPE, Expressions.call(TypeDescriptors.class, STRINGS));
    return Expressions.call(receiver, Methods.P_COLLECTION_APPLY, keyType);
  }


  /**
   * Maps from sqlKind operator to Java expression type.
   *
   * @param sqlKind sqlKind operator
   * @return Java expression type
   */
  static ExpressionType sqlKindToExprType(SqlKind sqlKind) {
    switch (sqlKind) {
      case AND:
        return ExpressionType.AndAlso;
      case OR:
        return ExpressionType.OrElse;
      case PLUS:
        return ExpressionType.Add;
      case MINUS:
        return ExpressionType.Subtract;
      case DIVIDE:
        return ExpressionType.Divide;
      case TIMES:
        return ExpressionType.Multiply;
      default:
        throw new UnsupportedOperationException("Expression type: " + sqlKind + " not supported");
    }
  }
}
