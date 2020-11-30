/**
 * Copyright 2019 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.beam.utils;

import java.lang.reflect.Method;
import java.util.Map;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.linq4j.tree.ParameterExpression;
import org.apache.calcite.piglet.PigUserDefinedFunction;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelVisitor;
import org.apache.calcite.rel.core.Filter;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.rex.RexVisitorImpl;
import org.apache.calcite.schema.impl.ScalarFunctionImpl;
import org.apache.pig.scripting.jython.JythonFunction;


public class PigUDFUtils {

  private PigUDFUtils() {
  }

  /**
   * Collects all Pig UDF calls in a given Calcite logical plan.
   *
   * @param rel Root of the Calcite logical plan
   * @param udfMap Map to store all Pig UDF references in the plan
   */
  public static void collectPigUDFs(RelNode rel, Map<String, PigUserDefinedFunction> udfMap) {
    new RelUDFCollector(udfMap).go(rel);
  }


  /**
   * Gets the key in UDF map for a given Pig UDF. The key will be unique for each UDF.
   *
   * @param pigUDF Pig UDF
   * @return a string key to represent the Pig UDF
   */
  public static String getUDFMapKey(PigUserDefinedFunction pigUDF) {
    final Method udfMethod = ((ScalarFunctionImpl) pigUDF.getFunction()).method;
    final Class clazz = udfMethod.getDeclaringClass();
    final String className = clazz.getName().replace(".", "_");
    if (clazz == JythonFunction.class) {
      final String[] args = pigUDF.funcSpec.getCtorArgs();
      assert args != null && args.length == 2;
      final String fileName = args[0].substring(args[0].lastIndexOf("/") + 1, args[0].lastIndexOf(".py"));
      // key = [clas name]_[file name]_[function name]
      return className + "_" + fileName + "_" + args[1];
    }
    return className;
  }

  /**
   * Gets the variable represent PIG UDF object in autogen code.
   *
   * @param pigUDF Pig UDF.
   * @return A {@link ParameterExpression} to represent the Pig UDF object
   */
  public static ParameterExpression getPigUDFParamExpr(PigUserDefinedFunction pigUDF) {
    final Class clazz = ((ScalarFunctionImpl) pigUDF.getFunction()).method.getDeclaringClass();
    return Expressions.parameter(clazz, getUDFMapKey(pigUDF));
  }

  /**
   * Rel visitor to collect all Pig UDF calls in a relational plan.
   */
  static class RelUDFCollector extends RelVisitor {
    final Map<String, PigUserDefinedFunction> udfMap;
    RelUDFCollector(Map<String, PigUserDefinedFunction> udfMap) {
      this.udfMap = udfMap;
    }

    @Override
    public void visit(RelNode node, int ordinal, RelNode parent) {
      if (node instanceof Project) {
        for (RexNode rexNode : ((Project) node).getProjects()) {
          rexNode.accept(new RexUDFCollector(udfMap));
        }
      }
      if (node instanceof Filter) {
        ((Filter) node).getCondition().accept(new RexUDFCollector(udfMap));
      }
      super.visit(node, ordinal, parent);
    }
  }

  /**
   * Rex visitor to collect all Pig UDF calls in an relational expression.
   */
  private static class RexUDFCollector extends RexVisitorImpl<Void> {
    final Map<String, PigUserDefinedFunction> udfMap;

    RexUDFCollector(Map<String, PigUserDefinedFunction> udfMap) {
      super(true);
      this.udfMap = udfMap;
    }

    @Override
    public Void visitCall(RexCall call) {
      if (call.getOperator() instanceof PigUserDefinedFunction) {
        PigUserDefinedFunction pigFunc = (PigUserDefinedFunction) call.getOperator();
        if (pigFunc.funcSpec != null) {
          udfMap.put(getUDFMapKey(pigFunc), pigFunc);
        }
      }
      return super.visitCall(call);
    }
  }
}
