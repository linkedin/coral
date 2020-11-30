/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.planner;

import com.google.common.collect.Sets;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.linkedin.beam.excution.BeamExecUtil;
import com.linkedin.beam.operators.BeamArrayFlatten;
import com.linkedin.beam.operators.BeamJoin;
import com.linkedin.beam.operators.BeamNode;
import com.linkedin.beam.utils.PigUDFUtils;
import com.linkedin.beam.utils.RelDataTypeToAvro;
import com.linkedin.beam.utils.RexBeamUtils;
import com.linkedin.beam.utils.RexToBeamConverter;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.avro.Schema;
import org.apache.calcite.linq4j.tree.ConstantExpression;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.linq4j.tree.FieldDeclaration;
import org.apache.calcite.linq4j.tree.MemberDeclaration;
import org.apache.calcite.linq4j.tree.ParameterExpression;
import org.apache.calcite.linq4j.tree.Statement;
import org.apache.calcite.piglet.PigConverter;
import org.apache.calcite.piglet.PigUserDefinedFunction;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Aggregate;
import org.apache.calcite.rel.core.Join;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rel.type.DynamicRecordType;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.schema.impl.ScalarFunctionImpl;
import org.apache.calcite.tools.Frameworks;
import org.apache.pig.builtin.RANDOM;
import org.apache.pig.scripting.jython.JythonFunction;

import static com.linkedin.beam.utils.MethodNames.*;


/**
 * Code generator to generate Beam Java code from Calcite plans.
 */
@SuppressWarnings("ALL")
public abstract class CodeGenerator {
  private static final Set<Class> EXCLUDED_PIG_UDFS = Sets.newHashSet(RANDOM.class);
  protected final CalciteBeamConfig _calciteBeamConfig;
  protected final Map<String, PigUserDefinedFunction> pigUdfMap;
  private final Set<String> visitedNodes;
  private int recordIndex = 0;

  protected CodeGenerator(CalciteBeamConfig calciteBeamConfig) {
    this._calciteBeamConfig = calciteBeamConfig;
    pigUdfMap = new HashMap<>();
    visitedNodes = new HashSet<>();
  }

  public String generateJavaCodeFromPigFile(String pigFile, Map<String, String> params) throws Exception {
    return generateJavaCodeFromRelPlan(getRelNodeFromPigFile(pigFile, params));
  }

  public String generateJavaCodeFromPigQuery(String pigFile) throws Exception {
    return generateJavaCodeFromRelPlan(getRelNodeFromPigQuery(pigFile));
  }

  public RelNode getBeamPhysicalPlan(RelNode rootNode) {
    final BeamPlanner planner = BeamPlanner.createPlanner(
        rootNode.getCluster().getPlanner(), _calciteBeamConfig, pigUdfMap, _calciteBeamConfig.planOptimized);
    final RelNode physicalPlan = planner.toBeamPlan(rootNode);

    pigUdfMap.clear();
    PigUDFUtils.collectPigUDFs(physicalPlan, pigUdfMap);
    return physicalPlan;
  }

  public String generateJavaCodeFromRelPlan(RelNode rootNode) {
    return generateJavaCodeFromBeamPhysicalPlan(getBeamPhysicalPlan(rootNode));
  }

  public String generateJavaCodeFromBeamPhysicalPlan(RelNode beamPlan) {
    final String  rawCode = fixVarName(toJavaCode(beamPlan));
    final Formatter formatter = new Formatter();
    try {
      return formatter.formatSource(extractPathForImport(rawCode));
    } catch (FormatterException e) {
      throw new IllegalArgumentException("Unable to parse auto-gen code", e);
    }
  }

  /**
   * Declares all required schema constants.
   */
  List<MemberDeclaration> declareSchemas(RelNode relNode) {
    final List<MemberDeclaration> results = new ArrayList<>();
    final Map<String, RelDataType> schemaMaps = new LinkedHashMap<>();

    // Get schemas for all required relational operators
    collectRelOpSchemas(relNode, schemaMaps);

    // Get schemas for all required relational expressions
    final RexToBeamConverter rexTranslator = ((BeamPlanner) relNode.getCluster().getPlanner()).getRexTranslator();
    schemaMaps.putAll(rexTranslator.getRegisteredSchemas());

    // Now declare those schemas
    for (Map.Entry<String, RelDataType> schema : schemaMaps.entrySet()) {
      results.add(makeSchemaDeclaration(RexBeamUtils.getSchemaParam(schema.getKey()), schema.getValue()));
    }

    return results;
  }

  /**
   * Collects all required schemas that are used in the code.
   *
   * @param relNode Root node
   * @param schemaMaps Map to store required schemas
   */
  private void collectRelOpSchemas(RelNode relNode, Map<String, RelDataType> schemaMaps) {
    for (RelNode relNode1 : relNode.getInputs()) {
      collectRelOpSchemas(relNode1, schemaMaps);
    }
    if (relNode instanceof Project
        || relNode instanceof BeamArrayFlatten
        || relNode instanceof Join
        || relNode instanceof Aggregate && !((Aggregate) relNode).getAggCallList().isEmpty()) {
      schemaMaps.put(RexBeamUtils.getSchemaName(((BeamNode) relNode).getVariableName()), relNode.getRowType());
      if (relNode instanceof Join) {
        final BeamJoin join = (BeamJoin) relNode;
        schemaMaps.put(RexBeamUtils.getSchemaName(((BeamNode) join.getLeft()).getVariableName()),
            join.getLeft().getRowType());
        schemaMaps.put(RexBeamUtils.getSchemaName(((BeamNode) join.getRight()).getVariableName()),
            join.getRight().getRowType());
      }
    }
  }

  protected void relNodeToJava(RelNode relNode, List<Statement> statements) {
    final BeamNode beamNode = (BeamNode) relNode;
    final String nodeName = beamNode.getVariableName();
    if (visitedNodes.contains(nodeName)) {
      return;
    }

    for (RelNode relNode1 : relNode.getInputs()) {
      relNodeToJava(relNode1, statements);
    }
    beamNodeToStatement(beamNode, statements);
    visitedNodes.add(nodeName);
  }

  /**
   * Generates statements to initialize required static variables.
   */
  Statement initStaticVars(List<MemberDeclaration> memberDeclarations) {
    final List<Statement> ifBodyStatements = new ArrayList<>();
    final Set<Expression> resourceFilesExprSet = new LinkedHashSet<>();
    final List<Statement> udfStatements = initPigUDFs(memberDeclarations, resourceFilesExprSet);

    // Then add statements to initialize Pig UDFs
    ifBodyStatements.addAll(udfStatements);

    // Wrap them with a guard to make sure they get initialized just once
    if (!ifBodyStatements.isEmpty()) {
      final ParameterExpression udfStaticInitCopyResourceDone = Expressions.parameter(boolean.class, "staticVarInit");
      memberDeclarations.add(0,
          Expressions.fieldDecl(Modifier.PRIVATE | Modifier.STATIC, udfStaticInitCopyResourceDone,
              Expressions.constant(false, boolean.class)));
      ifBodyStatements.add(Expressions.statement(
          Expressions.assign(udfStaticInitCopyResourceDone, Expressions.constant(true, boolean.class))));
      return Expressions.ifThen(Expressions.not(udfStaticInitCopyResourceDone), Expressions.block(ifBodyStatements));
    }
    return null;
  }

  /**
   * Initializes Pig UDF and capture resource files needed to initialize these UDF objects.
   * @param memberDeclarations Output param for UDF object declaration
   * @param resourceFilesExprSet Output param for resource files needed
   */
  private List<Statement> initPigUDFs(List<MemberDeclaration> memberDeclarations, Set<Expression> resourceFilesExprSet) {
    final List<Statement> udfInits = new ArrayList<>();
    final Set<Class> exceptions = new HashSet<>();

    for (PigUserDefinedFunction pigUDF : pigUdfMap.values()) {
      final Class clazz = ((ScalarFunctionImpl) pigUDF.getFunction()).method.getDeclaringClass();
      if (EXCLUDED_PIG_UDFS.contains(clazz)) {
        continue;
      }

      List<ConstantExpression> constructorArgs = new ArrayList<>();
      if (pigUDF.funcSpec.getCtorArgs() != null) {
        final String[] ctorArgs = pigUDF.funcSpec.getCtorArgs();
        final Class[] ctorTypes = new Class[ctorArgs.length];
        for (int i = 0; i < ctorArgs.length; i++) {
          final String filename = ctorArgs[i].substring(ctorArgs[i].lastIndexOf("/") + 1);
          constructorArgs.add(Expressions.constant(filename, String.class));
          ctorTypes[i] = String.class;
        }
        try {
          Collections.addAll(exceptions, clazz.getConstructor(ctorTypes).getExceptionTypes());
        } catch (NoSuchMethodException e) {
          e.printStackTrace();
          StringBuilder message = new StringBuilder("Cannot find constructor for Pig UDF ").append(clazz.getName())
              .append(" with arguments: ")
              .append(Arrays.asList(ctorTypes));
          throw new RuntimeException(message.toString());
        }
        if (clazz == JythonFunction.class) {
          // If Python UDF, only need to copy the python source file, which is the first argument
          resourceFilesExprSet.add(constructorArgs.get(0));
        } else {
          for (ConstantExpression arg : constructorArgs) {
            resourceFilesExprSet.add(arg);
          }
        }
      }
      final ParameterExpression udfName = PigUDFUtils.getPigUDFParamExpr(pigUDF);
      memberDeclarations.add(Expressions.fieldDecl(Modifier.PRIVATE | Modifier.STATIC, udfName));
      udfInits.add(Expressions.statement(Expressions.assign(udfName, Expressions.new_(clazz, constructorArgs))));
    }

    if (exceptions.isEmpty()) {
      return udfInits;
    }
    return RexBeamUtils.wrapTryCatch(udfInits, exceptions).statements;
  }

  /**
   *
   * @param beamPlan Beam physical plan
   * @return Java source code string for the Beam application
   */
  protected abstract String toJavaCode(RelNode beamPlan);

  protected abstract void beamNodeToStatement(BeamNode beamNode, List<Statement> statements);

  private String getRecordName(RelDataType recordType) {
    if (recordType instanceof DynamicRecordType) {
      return BeamExecUtil.DYNAMIC_RECORD_NAME_PREFIXED + (recordIndex++);
    }
    return BeamExecUtil.RECORD_NAME_PREFIXED;
  }

  private FieldDeclaration makeSchemaDeclaration(ParameterExpression schemaVar, RelDataType rowType) {
    final String schemaString = new RelDataTypeToAvro().convertRelDataType(rowType, getRecordName(rowType)).toString();
    return Expressions.fieldDecl(Modifier.PRIVATE | Modifier.FINAL | Modifier.STATIC, schemaVar,
        Expressions.call(Expressions.new_(Schema.Parser.class), PARSE, Expressions.constant(schemaString)));
  }

  // HACKED: Fixing two problems generated by Calcite code optimization:
  // 1. Declare static variable in anonymous class (in lambda functions) -> remove static
  // 2. Have invalid character '$' in the variable name -> replace '$' by '_'
  private static final Pattern STATIC_VAR_PATTERN = Pattern.compile(""
      + "(?im)"                      // case-insensitive, multi-line
      + "\\s*(static\\s+final\\s+"    // [static final ]Type varName
      + "[^\\s]+\\s+"                // static final [Type ]varName
      + "([^\\s]*))");                // static final Type [varName] ($1)
  private static String fixVarName(String code) {
    String newCode = code;
    final Matcher matcher = STATIC_VAR_PATTERN.matcher(newCode);
    while (matcher.find()) {
      String match1 = matcher.group(1);
      String match2 = matcher.group(2);
      if (match2.contains("$")) {
        // Cannot declare static var in an anonymous class, remove it.
        final String noStatic = match1.replace("static", "");
        newCode = newCode.replace(match1, noStatic);
        // Replace $ by _
        String varName = match2.replace("$", "_");
        newCode = newCode.replace(match2, varName);
      }
    }
    return newCode;
  }

  private static final Pattern PATH_PATTERN = Pattern.compile("(\\w+(\\.\\w+)+)");
  private static String extractPathForImport(String source) {
    Map<String, Integer> pathCount = new HashMap<>();
    final Matcher matcher = PATH_PATTERN.matcher(source);
    while (matcher.find()) {
      final String match = matcher.group(1);
      if (match != null) {
        pathCount.put(match, pathCount.getOrDefault(match, 0) + 1);
      }
    }
    final Map<String, String> replacements = new HashMap<>();
    pathCount.forEach((k, v) -> {
      String path = k;
      try {
        Class.forName(path);
      } catch (ClassNotFoundException e) {
        path = path.substring(0, path.lastIndexOf("."));
        try {
          Class.forName(path);
        } catch (ClassNotFoundException e1) {
          int dotIndex = path.lastIndexOf(".");
          if (dotIndex < 0) {
            return;
          }
          path = path.substring(0, path.lastIndexOf("."));
          try {
            Class.forName(path);
          } catch (ClassNotFoundException e2) {
            return;
          }
        }
      }
      final String className = path.substring(path.lastIndexOf('.') + 1);
      final String replacementName = replacements.get(className);
      final Integer replacementCount = pathCount.get(replacementName);
      if (replacementName == null || (replacementCount != null && replacementCount < v)) {
        replacements.put(className, path);
      }
    });
    final StringBuilder stringBuilder = new StringBuilder();
    replacements.values().stream().sorted().forEach(v -> stringBuilder.append("import ").append(v).append(";\n"));
    String newSource = source;
    for (String key : replacements.keySet()) {
      newSource = newSource.replace(replacements.get(key), key);
    }

    return stringBuilder.append("\n").toString() + newSource;
  }

  /**
   * Gets Calcite plan for a Pig script.
   *
   * @param pigFile Path to Pig script
   * @param params Pig params
   */
  private static RelNode getRelNodeFromPigFile(String pigFile, Map<String, String> params) throws Exception {
    PigConverter pigConverter = PigConverter.create(Frameworks.newConfigBuilder().build());
    pigConverter.setValidateEachStatement(true);
    List<RelNode> relNodes = pigConverter.pigScript2Rel(pigFile, params, false);
    assert relNodes.size() == 1 : "Expect only one store statement in the Pig script";
    return relNodes.get(0);
  }

  /**
   * Gets Calcite plan for a Pig script.
   *
   * @param pigQuery Pig query string
   */
  private static RelNode getRelNodeFromPigQuery(String pigQuery) throws Exception {
    PigConverter pigConverter = PigConverter.create(Frameworks.newConfigBuilder().build());
    pigConverter.setValidateEachStatement(true);
    List<RelNode> relNodes = pigConverter.pigQuery2Rel(pigQuery, false, true, false);
    assert relNodes.size() == 1 : "Expect only one store statement in the Pig script";
    return relNodes.get(0);
  }
}
