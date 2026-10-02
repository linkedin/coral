/**
 * Copyright 2025-2026 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.coral.datagen.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexFieldAccess;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlOperator;

import com.linkedin.coral.datagen.domain.transformer.AbsIntegerTransformer;
import com.linkedin.coral.datagen.domain.transformer.CastRegexTransformer;
import com.linkedin.coral.datagen.domain.transformer.ConcatRegexTransformer;
import com.linkedin.coral.datagen.domain.transformer.FieldAccessTransformer;
import com.linkedin.coral.datagen.domain.transformer.ItemTransformer;
import com.linkedin.coral.datagen.domain.transformer.LowerRegexTransformer;
import com.linkedin.coral.datagen.domain.transformer.MinusIntegerTransformer;
import com.linkedin.coral.datagen.domain.transformer.NegateIntegerTransformer;
import com.linkedin.coral.datagen.domain.transformer.PlusIntegerTransformer;
import com.linkedin.coral.datagen.domain.transformer.SubstringRegexTransformer;
import com.linkedin.coral.datagen.domain.transformer.TimesIntegerTransformer;
import com.linkedin.coral.datagen.domain.transformer.TrimRegexTransformer;
import com.linkedin.coral.datagen.domain.transformer.UpperRegexTransformer;


/**
 * Generic domain inference program supporting multiple domain types.
 * 
 * This class takes an expression tree and a constraint on the output (as any Domain type),
 * and derives the constraint on the input variable by traversing the expression tree inward,
 * refining the domain at each step.
 * 
 * Supports:
 * - RegexDomain: String constraints represented as regular expressions
 * - IntegerDomain: Numeric constraints represented as intervals
 * - Cross-domain conversions via CAST operations
 * 
 * Example 1 (String operations):
 *   Expression: LOWER(SUBSTRING(name, 1, 3)) = 'abc'
 *   Output: RegexDomain("^abc$")
 *   Result: RegexDomain("^[aA][bB][cC].*$")
 * 
 * Example 2 (Cross-domain with CAST):
 *   Expression: CAST(age * 2 AS STRING) = '50'
 *   Output: RegexDomain("^50$")
 *   Conversion: RegexDomain → IntegerDomain([50])
 *   Through TIMES: IntegerDomain([25])
 *   Result: IntegerDomain([25])
 */
public class DomainInferenceProgram {
  private final List<DomainTransformer> transformers;

  public DomainInferenceProgram(List<DomainTransformer> transformers) {
    this.transformers = transformers;
  }

  /**
   * Creates a DomainInferenceProgram with all built-in transformers.
   * This is the recommended way to create an instance for production use.
   */
  public static DomainInferenceProgram withDefaultTransformers() {
    return new DomainInferenceProgram(Arrays.asList(
        // String/regex transformers
        new LowerRegexTransformer(), new UpperRegexTransformer(), new SubstringRegexTransformer(),
        new ConcatRegexTransformer(), new TrimRegexTransformer(),
        // Integer transformers
        new PlusIntegerTransformer(), new MinusIntegerTransformer(), new TimesIntegerTransformer(),
        new NegateIntegerTransformer(), new AbsIntegerTransformer(),
        // Cross-domain
        new CastRegexTransformer(),
        // Structural pass-throughs
        new ItemTransformer(), new FieldAccessTransformer()));
  }

  /**
   * Derives the domain constraint on the input variable given an expression
   * and a constraint on the output.
   * 
   * @param expr the expression tree
   * @param outputDomain the domain constraint on the output (RegexDomain or IntegerDomain)
   * @return the refined domain constraint on the input variable
   */
  public Domain<?, ?> deriveInputDomain(RexNode expr, Domain<?, ?> outputDomain) {
    // Base case: if we've reached the input variable, return the output constraint
    if (expr instanceof RexInputRef) {
      return outputDomain;
    }

    // Base case: struct field access on a column ref (e.g., $3.name)
    if (expr instanceof RexFieldAccess) {
      RexFieldAccess fa = (RexFieldAccess) expr;
      if (fa.getReferenceExpr() instanceof RexInputRef) {
        return outputDomain;
      }
    }

    // Base case: ITEM access on a column ref (e.g., ITEM($2, 1) for arrays, ITEM($4, 'key') for maps)
    if (isTerminalItemAccess(expr)) {
      return outputDomain;
    }

    // Find a transformer that can handle this expression
    for (DomainTransformer transformer : transformers) {
      if (transformer.canHandle(expr) && transformer.isVariableOperandPositionValid(expr)) {
        // Refine the domain for the child expression
        Domain<?, ?> childDomain = transformer.refineInputDomain(expr, outputDomain);

        // Check for empty domain (contradiction)
        if (childDomain.isEmpty()) {
          return createEmptyDomain(outputDomain);
        }

        // Recursively derive the domain for the child
        RexNode child = transformer.getChildForVariable(expr);
        return deriveInputDomain(child, childDomain);
      }
    }

    // No applicable transformer found
    throw new IllegalStateException("No applicable transformer for expression: " + expr);
  }

  /**
   * Derives the domain constraint on the input variable from a comparison predicate.
   *
   * <p>Handles {@code =}, {@code <}, {@code >}, {@code <=}, {@code >=} comparisons where
   * one side is a literal and the other is an expression containing the variable.
   *
   * @param predicate the comparison RexCall (e.g., {@code expr > 5})
   * @return the refined domain constraint on the input variable
   */
  public Domain<?, ?> deriveInputDomainFromPredicate(RexCall predicate) {
    SqlOperator op = predicate.getOperator();
    RexNode lhs = predicate.getOperands().get(0);
    RexNode rhs = predicate.getOperands().get(1);

    // Calcite often represents negative literals as RexCall(UNARY_MINUS, positiveLiteral).
    // Unwrap that here so the rest of the predicate handler can treat the RHS as a literal.
    boolean negateRhs = false;
    if (rhs instanceof RexCall) {
      RexCall rhsCall = (RexCall) rhs;
      if (rhsCall.getKind() == SqlKind.MINUS_PREFIX && rhsCall.getOperands().size() == 1
          && rhsCall.getOperands().get(0) instanceof RexLiteral) {
        rhs = rhsCall.getOperands().get(0);
        negateRhs = true;
      }
    }

    if (!(rhs instanceof RexLiteral)) {
      throw new IllegalArgumentException("RHS of comparison must be a literal, got: " + rhs);
    }

    RexLiteral literal = (RexLiteral) rhs;
    Domain<?, ?> outputDomain = createDomainFromComparison(op, literal, negateRhs);
    return deriveInputDomain(lhs, outputDomain);
  }

  /**
   * Resolves domains for all access paths constrained by a list of DNF disjuncts.
   *
   * Each disjunct may be a single comparison or a conjunction (AND) of comparisons.
   * Within a disjunct, predicates on the same path are intersected (AND semantics).
   * Across disjuncts, domains for the same path are unioned (OR semantics).
   *
   * Example: {@code (a > 5 AND a < 10) OR (a = 20 AND b = 3)}
   * produces: {@code {$0: [6,9] ∪ {20}, $1: {3}}}
   *
   * @param disjuncts the DNF disjuncts from {@link com.linkedin.coral.datagen.rel.DnfRewriter}
   * @return map from {@link AccessPath} (root column index plus nested struct/map/array accesses)
   *         to the resolved domain for that path
   */
  public Map<AccessPath, Domain<?, ?>> resolveAllPaths(List<RexNode> disjuncts) {
    Map<AccessPath, Domain<?, ?>> result = new HashMap<>();

    for (RexNode disjunct : disjuncts) {
      Map<AccessPath, Domain<?, ?>> disjunctDomains = resolveDisjunct(disjunct);

      // Union with existing domains (OR semantics across disjuncts)
      for (Map.Entry<AccessPath, Domain<?, ?>> entry : disjunctDomains.entrySet()) {
        AccessPath colPath = entry.getKey();
        Domain<?, ?> domain = entry.getValue();
        if (result.containsKey(colPath)) {
          result.put(colPath, unionDomains(result.get(colPath), domain));
        } else {
          result.put(colPath, domain);
        }
      }
    }

    return result;
  }

  /**
   * Resolves domains for all columns from a single disjunct.
   * A disjunct may be a conjunction (AND) of multiple comparisons, or a single comparison.
   * Predicates on the same column within a disjunct are intersected.
   */
  private Map<AccessPath, Domain<?, ?>> resolveDisjunct(RexNode disjunct) {
    List<RexNode> conjuncts = RelOptUtil.conjunctions(disjunct);
    Map<AccessPath, Domain<?, ?>> pathDomains = new HashMap<>();

    for (RexNode conjunct : conjuncts) {
      if (!(conjunct instanceof RexCall)) {
        continue;
      }
      RexCall call = (RexCall) conjunct;

      // Find which column this conjunct constrains
      RexNode lhs = call.getOperands().get(0);
      AccessPath colPath = findAccessPath(lhs);
      if (colPath == null) {
        continue;
      }

      try {
        Domain<?, ?> domain = deriveInputDomainFromPredicate(call);
        if (pathDomains.containsKey(colPath)) {
          // Intersect within the same disjunct (AND semantics)
          pathDomains.put(colPath, intersectDomains(pathDomains.get(colPath), domain));
        } else {
          pathDomains.put(colPath, domain);
        }
      } catch (UnsupportedOperationException | IllegalArgumentException | IllegalStateException e) {
        // Skip predicates we can't resolve
      }
    }

    return pathDomains;
  }

  /**
   * Finds the AccessPath for the column referenced within an expression tree.
   * Handles flat columns (RexInputRef), struct field access (RexFieldAccess),
   * and array/map element access (ITEM operator).
   * Returns null if no column reference is found.
   */
  private AccessPath findAccessPath(RexNode expr) {
    if (expr instanceof RexInputRef) {
      return AccessPath.of(((RexInputRef) expr).getIndex());
    }

    // Struct field access: $3.name
    if (expr instanceof RexFieldAccess) {
      RexFieldAccess fa = (RexFieldAccess) expr;
      AccessPath inner = findAccessPath(fa.getReferenceExpr());
      if (inner != null) {
        return inner.append(AccessPath.PathElement.field(fa.getField().getName()));
      }
      return null;
    }

    if (expr instanceof RexCall) {
      RexCall call = (RexCall) expr;

      // ITEM access: ITEM($2, 1) for arrays, ITEM($4, 'key') for maps
      if (ItemTransformer.isItemOperator(call) && call.getOperands().size() == 2
          && call.getOperands().get(1) instanceof RexLiteral) {
        AccessPath inner = findAccessPath(call.getOperands().get(0));
        if (inner != null) {
          RexLiteral indexOrKey = (RexLiteral) call.getOperands().get(1);
          Object value = indexOrKey.getValue2();
          if (value instanceof Number) {
            return inner.append(AccessPath.PathElement.arrayIndex(((Number) value).intValue()));
          } else {
            return inner.append(AccessPath.PathElement.mapKey(value.toString()));
          }
        }
      }

      // General case: search operands for a column reference
      for (RexNode operand : call.getOperands()) {
        AccessPath path = findAccessPath(operand);
        if (path != null) {
          return path;
        }
      }
    }

    return null;
  }

  /**
   * Checks if an expression is a terminal ITEM access directly on a RexInputRef.
   */
  private boolean isTerminalItemAccess(RexNode expr) {
    if (!(expr instanceof RexCall)) {
      return false;
    }
    RexCall call = (RexCall) expr;
    return ItemTransformer.isItemOperator(call) && call.getOperands().size() == 2
        && call.getOperands().get(0) instanceof RexInputRef && call.getOperands().get(1) instanceof RexLiteral;
  }

  /**
   * Unions two domains of the same type.
   */
  @SuppressWarnings("unchecked")
  private Domain<?, ?> unionDomains(Domain<?, ?> a, Domain<?, ?> b) {
    if (a instanceof RegexDomain && b instanceof RegexDomain) {
      return ((RegexDomain) a).union((RegexDomain) b);
    } else if (a instanceof IntegerDomain && b instanceof IntegerDomain) {
      return ((IntegerDomain) a).union((IntegerDomain) b);
    }
    // Mixed domain types: return the broader one (can't union across types)
    return a;
  }

  /**
   * Intersects two domains of the same type.
   */
  @SuppressWarnings("unchecked")
  private Domain<?, ?> intersectDomains(Domain<?, ?> a, Domain<?, ?> b) {
    if (a instanceof RegexDomain && b instanceof RegexDomain) {
      return ((RegexDomain) a).intersect((RegexDomain) b);
    } else if (a instanceof IntegerDomain && b instanceof IntegerDomain) {
      return ((IntegerDomain) a).intersect((IntegerDomain) b);
    }
    // Mixed domain types: return the more constrained one
    return a;
  }

  /**
   * Creates an output domain from a comparison operator and literal value.
   * For EQUALS, produces a singleton domain.
   * For inequalities, produces a range domain.
   */
  private Domain<?, ?> createDomainFromComparison(SqlOperator op, RexLiteral literal, boolean negate) {
    String rhsValue = literal.getValue2().toString();
    SqlKind kind = op.getKind();

    if (isNumericType(literal.getType().getSqlTypeName())) {
      long numericValue = Long.parseLong(rhsValue);
      if (negate) {
        numericValue = -numericValue;
      }

      switch (kind) {
        case EQUALS:
          return IntegerDomain.of(numericValue);
        case GREATER_THAN:
          return IntegerDomain.of(numericValue + 1, Long.MAX_VALUE);
        case GREATER_THAN_OR_EQUAL:
          return IntegerDomain.of(numericValue, Long.MAX_VALUE);
        case LESS_THAN:
          return IntegerDomain.of(Long.MIN_VALUE, numericValue - 1);
        case LESS_THAN_OR_EQUAL:
          return IntegerDomain.of(Long.MIN_VALUE, numericValue);
        default:
          break;
      }
    }

    if (kind == SqlKind.EQUALS) {
      // String literals are never produced by the UNARY_MINUS unwrap above.
      return RegexDomain.literal(rhsValue);
    }

    throw new UnsupportedOperationException("Comparison operator " + op + " (kind=" + kind + ") not supported for type "
        + literal.getType().getSqlTypeName());
  }

  private boolean isNumericType(org.apache.calcite.sql.type.SqlTypeName typeName) {
    switch (typeName) {
      case TINYINT:
      case SMALLINT:
      case INTEGER:
      case BIGINT:
      case DECIMAL:
      case FLOAT:
      case REAL:
      case DOUBLE:
        return true;
      default:
        return false;
    }
  }

  /**
   * Creates an empty domain matching the type of the given domain.
   */
  private Domain<?, ?> createEmptyDomain(Domain<?, ?> referenceDomain) {
    if (referenceDomain instanceof RegexDomain) {
      return RegexDomain.empty();
    } else if (referenceDomain instanceof IntegerDomain) {
      return IntegerDomain.empty();
    } else {
      throw new IllegalArgumentException("Unknown domain type: " + referenceDomain.getClass().getSimpleName());
    }
  }
}
