/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.utils;

import com.google.common.collect.ImmutableList;
import com.linkedin.beam.operators.BeamNode;
import com.linkedin.beam.excution.BeamExecUtil;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.generic.GenericRecord;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.linq4j.tree.ExpressionType;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.linq4j.tree.MethodCallExpression;
import org.apache.calcite.linq4j.tree.ParameterExpression;
import org.apache.calcite.linq4j.tree.Statement;
import org.apache.calcite.linq4j.tree.Types;
import org.apache.calcite.piglet.PigUserDefinedFunction;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexFieldAccess;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.schema.ScalarFunction;
import org.apache.calcite.schema.impl.ScalarFunctionImpl;
import org.apache.calcite.sql.SqlFunction;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlSpecialOperator;
import org.apache.calcite.sql.fun.SqlRandFunction;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.type.SqlTypeFamily;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;
import org.apache.calcite.util.NlsString;
import org.apache.pig.data.DataByteArray;
import org.apache.pig.impl.util.avro.AvroBagWrapper;
import org.apache.pig.impl.util.avro.AvroTupleWrapper;


public class RexToBeamConverter {
  private static final Expression TRUE = Expressions.constant(true);
  // CONSTANT VARS:
  // Pig UDF map: map each unique Pig UDF name to @PigUserDefinedFunction.
  // Sever as constant input during lifetime of RexToBeam
  private final Map<String, PigUserDefinedFunction> pigUDFs;

  // GLOBAL VARS:
  // Map to store any avro schemas that need to be declared and used in augogen code.
  // This map may get changed after each call to convert() method. The final version of the map will
  // be used to declared avro schemas in autogen code.
  private Map<String, RelDataType> registeredSchemas;

  // LOCAL VARS:
  // Store all checked exceptions that can happen during evaluating an expression. Callers, after calling to
  // convert() method, must call getLocalExeptions() to include code to handle all checked exceptions.
  // Reset in each call to convert() method.
  private Set<Class> localExeptions;
  // Store map of temporary variable when evaluating an expression. That helps to reuse temporary results and make
  // the autogen code shorter and more readable.
  // Reset in each call to convert() method.
  private Map<RexNode, ParameterExpression> localVarMap;
  // Store declarations of temporary local variables when evaluating an expression. These local variables must be
  // declared before the main expression in autogen code. Callers, after calling to convert() method, must call
  // getLocalVarDeclarations() to get the list of local var declarations.
  // Reset in each call to convert() method.
  private List<Statement> localVarDeclarations;
  // Local variable counter, used to give different names for local vars.
  // Should be reset before using local variables within a same scope
  private int varCount;

  private boolean pigUDFContext = false;
  private int constCount;
  private String schemaName = null;

  public RexToBeamConverter(Map<String, PigUserDefinedFunction> pigUDFs) {
    this.pigUDFs = pigUDFs;
    registeredSchemas = new HashMap<>();
    localExeptions = new HashSet<>();
    localVarMap = new HashMap<>();
    localVarDeclarations = new ArrayList<>();
    varCount = 0;
    constCount = 0;
  }

  public Set<Class> getLocalExeptions() {
    return localExeptions;
  }

  public List<Statement> getLocalVarDeclarations() {
    return localVarDeclarations;
  }

  public Map<String, RelDataType> getRegisteredSchemas() {
    return registeredSchemas;
  }

  private void resetLocalEnv(String outputSchemaName) {
    localExeptions.clear();
    localVarMap.clear();
    localVarDeclarations.clear();
    schemaName = outputSchemaName;
  }

  public void resetVarCounter() {
    varCount = 0;
    constCount = 0;
  }

  /**
   * Converts an relational expression into Beam code expression.
   *
   * @param rexNode The relational expression
   * @param relNode The base relational operator for the relational expression
   * @return Beam code expression
   */
  public Expression convert(RexNode rexNode, RelNode relNode) {
    return convert(rexNode, relNode, null);
  }

  /**
   * Converts an relational expression into Beam code expression.
   *
   * @param rexNode The relational expression
   * @param relNode The base relational operator for the relational expression
   * @param outputSchemaName The schema name of the expression. Ignore for null.
   * @return Beam code expression
   */
  public Expression convert(RexNode rexNode, RelNode relNode, String outputSchemaName) {
    resetLocalEnv(outputSchemaName);
    return convertInternal(rexNode, relNode);
  }

  private Expression convertInternal(RexNode rexNode, RelNode relNode) {
    final ParameterExpression localVar = localVarMap.get(rexNode);
    if (localVar != null) {
      // If the expression is computed and stored in an existing local variable, just use the local variable.
      return localVar;
    }

    switch (rexNode.getKind()) {
      case FIELD_ACCESS: {
        // For expression record.filedName
        final RexFieldAccess fieldAccess = (RexFieldAccess) rexNode;
        final Expression inputExpr = convertInternal(fieldAccess.getReferenceExpr(), relNode);
        return declareLocalVar(rexNode, getInternalRecordField(inputExpr, fieldAccess));
      }
      case INPUT_REF: {
        final Expression outputExpr = RexBeamUtils.getFieldExpression(relNode.getRowType(), ((RexInputRef) rexNode).getIndex(),
            BeamNode.getRecordVar((BeamNode) relNode));
        return declareLocalVar(rexNode, outputExpr);
      }
      case LITERAL: {
        return convertLiteral((RexLiteral) rexNode, schemaName + "_CONST_" + constCount++);
      }
      case MINUS:
      case PLUS:
      case DIVIDE:
      case TIMES: {
        final List<Expression> expOperands =
            convertRexList(((RexCall) rexNode).getOperands(), relNode);
        // Check whether any operand is null
        final Expression nullSafeExpr = RexBeamUtils.buildNullSafeExpr(expOperands);
        final Expression mainExpr =
            Expressions.makeBinary(RexBeamUtils.sqlKindToExprType(rexNode.getKind()), expOperands.get(0), expOperands.get(1));
        final Expression outputExpr = Expressions.condition(nullSafeExpr, mainExpr, Expressions.constant(null));
        return declareLocalVar(rexNode, outputExpr);
      }
      case AND:
      case OR: {
        final List<Expression> operands =
            convertRexList(((RexCall) rexNode).getOperands(), relNode);
        Expression retExpr =
            Expressions.makeBinary(RexBeamUtils.sqlKindToExprType(rexNode.getKind()), operands.get(0), operands.get(1));
        for (int i = 2; i < operands.size(); i++) {
          retExpr = Expressions.makeBinary(RexBeamUtils.sqlKindToExprType(rexNode.getKind()), retExpr, operands.get(i));
        }
        return retExpr;
      }
      case EQUALS:
      case NOT_EQUALS: {
        final List<RexNode> operands = ((RexCall) rexNode).getOperands();
        final List<Expression> expOperands = convertRexList(operands, relNode);
        final Expression nullSafeExpr = RexBeamUtils.buildNullSafeExpr(expOperands);
        Expression comparisionExpr;
        if (operands.get(0).getType().getSqlTypeName().getFamily() == SqlTypeFamily.CHARACTER) {
          // String comparison
          final Method stringCompare = Types.lookupMethod(String.class, MethodNames.EQUALS, Object.class);
          comparisionExpr = Expressions.call(expOperands.get(0), stringCompare,
              RexBeamUtils.toJavaStringType(expOperands.get(1)));
          if (rexNode.getKind() == SqlKind.NOT_EQUALS) {
            comparisionExpr = Expressions.not(comparisionExpr);
          }
        } else {
          // Other numeric comparisons.
          if (rexNode.getKind() == SqlKind.EQUALS) {
            comparisionExpr = Expressions.equal(expOperands.get(0), expOperands.get(1));
          } else {
            comparisionExpr = Expressions.notEqual(expOperands.get(0), expOperands.get(1));
          }
        }
        if (comparisionExpr.equals(nullSafeExpr)) {
          // Comparison is just a null check, avoid duplicate expression
          return comparisionExpr;
        }
        return Expressions.andAlso(nullSafeExpr, comparisionExpr);
      }
      case LESS_THAN:
      case LESS_THAN_OR_EQUAL:
      case GREATER_THAN:
      case GREATER_THAN_OR_EQUAL: {
        final List<RexNode> operands = ((RexCall) rexNode).getOperands();
        final List<Expression> expOperands = convertRexList(operands, relNode);
        final Expression nullSafeExpr = RexBeamUtils.buildNullSafeExpr(expOperands);
        Expression op1 = expOperands.get(0);
        Expression op2 = expOperands.get(1);
        if (operands.get(0).getType().getSqlTypeName().getFamily() == SqlTypeFamily.CHARACTER) {
          op1 = RexBeamUtils.stringCompareExpr(expOperands.get(0), expOperands.get(1));
          op2 = Expressions.constant(0);
        }
        Expression comparisionExpr = null;
        switch (rexNode.getKind()) {
          case LESS_THAN:
            comparisionExpr = Expressions.lessThan(op1, op2);
            break;
          case LESS_THAN_OR_EQUAL:
            comparisionExpr = Expressions.lessThanOrEqual(op1, op2);
            break;
          case GREATER_THAN:
            comparisionExpr = Expressions.greaterThan(op1, op2);
            break;
          case GREATER_THAN_OR_EQUAL:
            comparisionExpr = Expressions.greaterThanOrEqual(op1, op2);
            break;
          default:
            throw new UnsupportedOperationException("Expression type: " + rexNode.getKind() + " not supported");
        }
        return Expressions.andAlso(nullSafeExpr, comparisionExpr);
      }
      case CASE: {
        final List<Expression> operands =
            convertRexList(((RexCall) rexNode).getOperands(), relNode);
        assert (operands.size() == 3);
        return Expressions.condition(operands.get(0), operands.get(1), operands.get(2));
      }
      case NOT: {
        final Expression expr = convertRexList(((RexCall) rexNode).getOperands(), relNode).get(0);
        final Expression nullSafeExpr = RexBeamUtils.buildNullSafeExpr(ImmutableList.of(expr));
        if (nullSafeExpr.equals(TRUE)) {
          return Expressions.not(expr);
        }
        return Expressions.andAlso(nullSafeExpr, Expressions.not(expr));
      }
      case IS_NULL: {
        final Expression operand =
            convertRexList(((RexCall) rexNode).getOperands(), relNode).get(0);
        return Expressions.equal(operand, Expressions.constant(null));
      }
      case IS_NOT_NULL: {
        return RexBeamUtils.notNull(convertRexList(((RexCall) rexNode).getOperands(), relNode).get(0));
      }
      case CAST: {
        final List<RexNode> inputOperands = ((RexCall) rexNode).getOperands();
        final Expression inputOperandExpr = convertRexList(inputOperands, relNode).get(0);
        final RelDataType targetType = rexNode.getType();

        if (schemaName != null
            && ((targetType.isStruct() || targetType.getComponentType() != null))) {
          final ParameterExpression schemaExpr = registerSchema(schemaName, rexNode.getType());
          if (rexNode.getType().isStruct()) {
            // Cast to record type
            return toAvroRecord(inputOperandExpr, schemaExpr);
          } else {
            // Cast to array/multiset type
            return toAvroArray(inputOperandExpr, schemaExpr);
          }
        }
        return declareLocalVar(rexNode, RexBeamUtils.convertType(inputOperandExpr, rexNode.getType()));
      }

      case OTHER_FUNCTION: {
        final RexCall call = (RexCall) rexNode;
        final SqlOperator op = call.getOperator();

        if (op instanceof PigUserDefinedFunction) {
          // Set Pig UDF context before converting UDF parameters
          pigUDFContext = true;
          final List<Expression> exOperands = convertRexList(call.getOperands(), relNode);
          final PigUserDefinedFunction pigFunc = (PigUserDefinedFunction) op;
          assert pigFunc.getFunction() instanceof ScalarFunction;
          final Method udfMethod = ((ScalarFunctionImpl) pigFunc.getFunction()).method;
          final String udfKeyName = PigUDFUtils.getUDFMapKey(pigFunc);
          final Expression udfCallExpr = callUDF(udfMethod, udfKeyName, exOperands);
          try {
            if (pigUDFs.containsKey(udfKeyName)) {
              // If the return type is a struct or array, we need to convert data types
              // from Pig tuple/databag to Avro record/array
              final RelDataType returnType = rexNode.getType();
              if (returnType.isStruct() || returnType.getComponentType() != null) {
                final ParameterExpression schemaExpr =
                    registerSchema(RexBeamUtils.getSchemaName(udfKeyName), returnType);
                final ParameterExpression udfCallVar = declareLocalVar(null, udfCallExpr);
                final Expression toAvroVar =
                    returnType.isStruct() ? toAvroRecord(udfCallVar, schemaExpr) : toAvroArray(udfCallVar, schemaExpr);
                return declareLocalVar(null, toAvroVar);
              }
            }
            return declareLocalVar(rexNode, udfCallExpr);
          } finally {
            pigUDFContext = false;
          }
        }

        final List<Expression> exOperands = convertRexList(call.getOperands(), relNode);
        if (op instanceof SqlSpecialOperator) {
          switch (op.getName()) {
            case "ITEM":
              return RexBeamUtils.convertType(
                  Expressions.call(exOperands.get(0), MethodNames.MAP_GET, Expressions.convert_(exOperands.get(1), Object.class)),
                  call.getType());
            default:
              throw new UnsupportedOperationException("SQL Operator: " + op.getName() + " not supported");
          }
        }

        if (op instanceof SqlRandFunction) {
          return declareLocalVar(null, Expressions.call(BeamExecUtil.class, MethodNames.RAND, ImmutableList.of()));
        }

        if (op instanceof SqlFunction) {
          final List<Expression> operands =
              convertRexList(((RexCall) rexNode).getOperands(), relNode);
          switch (op.getName()) {
            case "UPPER":
            case "LOWER": {
              final Expression nullSafeExpr = RexBeamUtils.buildNullSafeExpr(operands);
              final Expression toUpperCaseExpr = Expressions.call(operands.get(0),
                  op.getName().equals("UPPER") ? MethodNames.TO_UPPER : MethodNames.TO_LOWER);
              final Expression conditionExpr =
                  Expressions.condition(nullSafeExpr, toUpperCaseExpr, Expressions.constant(null));
              return declareLocalVar(null, conditionExpr);
            }
            case "TIME_ROUND_UP":
            case "DATE_FORMAT": {
              final SqlUserDefinedFunction func = (SqlUserDefinedFunction) op;
              final Method udfMethod = ((ScalarFunctionImpl) func.getFunction()).method;
              final Expression funcCallExpr = Expressions.call(udfMethod, operands);
              return declareLocalVar(null, funcCallExpr);
            }
            default:
              break;
          }
        }
        throw new UnsupportedOperationException("SQL Operator: " + op.getName() + " not supported");
      }
      case LIKE: {
        final List<Expression> operands =
            convertRexList(((RexCall) rexNode).getOperands(), relNode);
        assert operands.size() == 2;
        // Need to swap the operand order before calling Java Pattern.matches
        return Expressions.call(Pattern.class, MethodNames.MATCHES,
            ImmutableList.of(operands.get(1), operands.get(0)));
      }
      case OTHER: {
        if (rexNode instanceof RexCall) {
          final RexCall call = (RexCall) rexNode;
          final List<Expression> exOperands = convertRexList(call.getOperands(), relNode);
          final SqlOperator op = ((RexCall) rexNode).getOperator();
          if (op == SqlStdOperatorTable.CONCAT) {
            if (call.getType().getSqlTypeName().getFamily() == SqlTypeFamily.CHARACTER) {
              return Expressions.makeBinary(ExpressionType.Add, exOperands.get(0), exOperands.get(1));
            } else {
              throw new UnsupportedOperationException("CONCAT on type: " + call.getType().getSqlTypeName() + " not supported");
            }
          }
        }
        throw new UnsupportedOperationException("OTHER expression: " + rexNode.toString() + " not supported");
      }
      default:
        throw new UnsupportedOperationException("Expression type: " + rexNode.getKind() + " not supported");
    }
  }

  public Expression adjustConstant(Expression source, RelDataType type, String schemaName) {
    switch (type.getSqlTypeName()) {
      case ROW: {
        final ParameterExpression schemaExpr = registerSchema(schemaName, type);
        return toAvroRecord(source, schemaExpr);
      }
      case MULTISET: {
        final ParameterExpression schemaExpr = registerSchema(schemaName, type);
        if (type.getComponentType().isStruct()) {
          final MethodCallExpression callExpr = (MethodCallExpression) source;
          final List<Expression> newExprs = new ArrayList<>();
          for (Expression childExpr : callExpr.expressions) {
            newExprs.add(adjustConstant(childExpr, type.getComponentType(), schemaName + "_COMPONENT"));
          }
          callExpr.expressions.clear();
          callExpr.expressions.addAll(newExprs);
        }
        return toAvroArray(source, schemaExpr);
      }
      default:
        return source;
    }

  }

  /**
   * Converts a relational literal into a Java code constant expression
   *
   * TODO This method should be aware of whether the literal is for PigUDF (then Pig constant) or not (Avro constants)
   */
  Expression convertLiteral(RexLiteral literal, String schemaName) {
    if (literal.getValue() == null) {
      return Expressions.constant(null);
    }
    switch (literal.getType().getSqlTypeName()) {
      case CHAR:
      case VARCHAR:
        return Expressions.constant(((NlsString) literal.getValue()).getValue(), String.class);
      case INTEGER:
        return Expressions.constant(((BigDecimal) literal.getValue()).intValue(), int.class);
      case BIGINT:
        return Expressions.constant(((BigDecimal) literal.getValue()).longValue(), long.class);
      case DOUBLE:
        return Expressions.constant(((BigDecimal) literal.getValue()).doubleValue(), double.class);
      case BOOLEAN:
        return Expressions.constant(literal.getValue(), boolean.class);
      case ROW: {
        if (!(literal.getValue() instanceof List)) {
          throw new UnsupportedOperationException("Record constant must be a list");
        }
        final List<RexLiteral> literals = (List<RexLiteral>) literal.getValue();
        final List<Expression> literalExprs = new ArrayList<>();
        for (RexLiteral rexLiteral : literals) {
          literalExprs.add(convertLiteral(rexLiteral, schemaName));
        }
        final Expression pigTuple = Expressions.call(Methods.BUILD_TUPLE, literalExprs);
        if (pigUDFContext) {
          return pigTuple;
        }
        final ParameterExpression schemaExpr = registerSchema(schemaName, literal.getType());
        return toAvroRecord(pigTuple, schemaExpr);
      }
      case MULTISET: {
        if (!(literal.getValue() instanceof List)) {
          throw new UnsupportedOperationException("Multiset constant must be a list");
        }
        final List<RexLiteral> literals = (List<RexLiteral>) literal.getValue();
        final List<Expression> literalExprs = new ArrayList<>();
        final String componentSchema = schemaName + "_COMP";
        for (RexLiteral rexLiteral : literals) {
          literalExprs.add(convertLiteral(rexLiteral, componentSchema));
        }

        final Expression pigDatabag = Expressions.call(Methods.BUILD_BAG, literalExprs);
        if (pigUDFContext) {
          return pigDatabag;
        }
        final ParameterExpression schemaExpr = registerSchema(schemaName, literal.getType());
        return toAvroArray(pigDatabag, schemaExpr);
      }
      default:
        throw new UnsupportedOperationException(
            "constant type: " + literal.getType().getSqlTypeName() + " not supported");
    }
  }

  /**
   * Converts a list of relational expressions into a list of Beam code expressions.
   *
   * @param rexNodes The list of relational expressions
   * @param relNode The base relational operator for all relational expressions
   */
  private List<Expression> convertRexList(List<RexNode> rexNodes, RelNode relNode) {
    List<Expression> expressions = new ArrayList<>();
    for (RexNode operand : rexNodes) {
      expressions.add(convertInternal(operand, relNode));
    }
    return expressions;
  }

  /**
   * Registers avro schema for record or array types.
   *
   * @param schemaName local variable name for the schema
   * @param relType Rel type of the schema
   * @return Parameter expression for the registered schema
   */
  public ParameterExpression registerSchema(String schemaName, RelDataType relType) {
    final RelDataType existingType = registeredSchemas.get(schemaName);
    String registeredName = schemaName;
    if (existingType != null && !existingType.equals(relType)) {
        registeredName = schemaName + (registeredSchemas.size() + 1);
    }
    registeredSchemas.put(registeredName, relType);
    return RexBeamUtils.getSchemaParam(registeredName);
  }

  /**
   * Converts an expression to avro array if required.
   *
   * @param expr input expression
   * @param schemaExpr schema for the input expression
   * @return Expression with avro array type
   */
  private Expression toAvroArray(Expression expr, ParameterExpression schemaExpr) {
    if (expr.getType() == GenericArray.class) {
      return expr;
    }
    return Expressions.call(BeamExecUtil.class, MethodNames.TO_AVRO_ARRAY, ImmutableList.of(expr, schemaExpr));
  }

  /**
   * Converts an expression to avro record if required.
   *
   * @param expr input expression
   * @param schemaExpr schema for the input expression
   * @return Expression with avro record type
   */
  private Expression toAvroRecord(Expression expr, ParameterExpression schemaExpr) {
    if (expr.getType() == GenericRecord.class) {
      return expr;
    }
    return Expressions.call(BeamExecUtil.class, MethodNames.TO_AVRO_RECORD, ImmutableList.of(expr, schemaExpr));
  }

  /**
   * Declares a local variables for an expression.
   *
   * @param node Original relational expression
   * @param assignedExpression Beam code expression
   * @return Local variable expression representing the expression
   */
  private ParameterExpression declareLocalVar(RexNode node, Expression assignedExpression) {
    if (assignedExpression instanceof ParameterExpression) {
      // If the assigned expression is already a variable, no need to declare a new variable
      return (ParameterExpression) assignedExpression;
    }

    final ParameterExpression localVar = Expressions.parameter(assignedExpression.getType(), "var" + varCount++);
    localVarDeclarations.add(Expressions.declare(Modifier.FINAL, localVar, assignedExpression));
    if (node != null) {
      localVarMap.put(node, localVar);
    }
    return localVar;
  }

  /**
   * Calls an UDF.
   *
   * @param udfMethod Method implementing the UDF
   * @param pigUDFKey Key to lookup Pig UDF map for pre-initialized Pig UDF object
   * @param exOperands UDF operands
   */
  private Expression callUDF(Method udfMethod, String pigUDFKey, List<Expression> exOperands) {
    localExeptions.addAll(Arrays.asList(udfMethod.getExceptionTypes()));
    final Class udfClass = udfMethod.getDeclaringClass();
    final PigUserDefinedFunction pigFunc = pigUDFs.get(pigUDFKey);
    if (pigFunc != null) {
      // Pig UDF was already declared and initialized in the beginning of the code-gen file
      return Expressions.call(PigUDFUtils.getPigUDFParamExpr(pigFunc), MethodNames.EXEC, exOperands);
    } else if (Modifier.isStatic(udfMethod.getModifiers())) {
      List<Expression> pigExpOperands = exOperands;
      if (udfClass.getName().equals("org.apache.calcite.piglet.PigRelSqlUdfs") && udfMethod.getName()
          .equals(MethodNames.BUILD_TUPLE)) {
        // To buil Pig Tuple for the UDF, need to wrap operands into Pig types
        pigExpOperands = wrapPigOperands(exOperands);
      }
      return Expressions.call(udfMethod, pigExpOperands);
    } else {
      return Expressions.call(Expressions.new_(udfMethod.getDeclaringClass()), udfMethod.getName(), exOperands);
    }
  }

  /**
   * If input for buildTuple() is in Avro types, need to wrap it into Pig types
   */
  private List<Expression> wrapPigOperands(List<Expression> exOperands) {
    final List<Expression> newOperands = new ArrayList<>();
    for (Expression operand : exOperands) {
      if (operand.getType() == GenericArray.class) {
        newOperands.add(declareLocalVar(null, Expressions.new_(AvroBagWrapper.class, operand)));
      } else if (operand.getType() == GenericRecord.class) {
        // TODO: GenericRecord to Pig tuple not tested
        newOperands.add(declareLocalVar(null, Expressions.new_(AvroTupleWrapper.class, operand)));
      } else if (operand.getType() == ByteBuffer.class) {
        final Expression nullSafeExpr = RexBeamUtils.buildNullSafeExpr(ImmutableList.of(operand));
        final Expression byteBufferArrayExpr =
            Expressions.new_(DataByteArray.class, Expressions.call(operand, MethodNames.ARRAY));
        final Expression dataByteArrayExpr =
            Expressions.condition(nullSafeExpr, byteBufferArrayExpr, Expressions.constant(null));
        newOperands.add(declareLocalVar(null, dataByteArrayExpr));
      } else {
        newOperands.add(operand);
      }
    }
    return newOperands;
  }

  /**
   * Gets a field of an internal record.
   *
   * @param inputExpr Expression representing the record
   * @param fieldAccess Field to access
   */
  private Expression getInternalRecordField(Expression inputExpr, RexFieldAccess fieldAccess) {
    final Expression nullSafeExpr = RexBeamUtils.buildNullSafeExpr(ImmutableList.of(inputExpr));
    final Expression result = RexBeamUtils.convertType(Expressions.call(inputExpr, MethodNames.AVRO_GET,
        Expressions.constant(RelDataTypeToAvro.toAvroQualifedName(fieldAccess.getField().getName()))),
        fieldAccess.getType());
    return Expressions.condition(nullSafeExpr, result, Expressions.constant(null));
  }
}
