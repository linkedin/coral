package com.linkedin.coral.datagen.domain;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.type.SqlTypeName;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.testng.Assert.*;

/**
 * Tests for CastRegexTransformer, focusing on cross-domain conversions.
 * 
 * Key scenarios:
 * - CAST(string AS integer) with IntegerDomain output → RegexDomain input
 * - CAST(integer AS string) with RegexDomain output → IntegerDomain input
 * - Same-type casts
 */
public class CastRegexTransformerTest {
    
    private CastRegexTransformer transformer;
    private RexBuilder rexBuilder;
    private RelDataTypeFactory typeFactory;
    
    @BeforeMethod
    public void setUp() {
        transformer = new CastRegexTransformer();
        rexBuilder = TestHelper.createRexBuilder();
        typeFactory = rexBuilder.getTypeFactory();
    }
    
    // ========== String to Integer Conversion Tests ==========
    
    @Test
    public void testCastStringToInteger_IntegerDomainOutput() {
        // CAST(x AS INTEGER) where output is IntegerDomain [100, 200]
        // Input should be RegexDomain matching "100", "101", ..., "200"
        
        RexNode inputRef = rexBuilder.makeInputRef(
            typeFactory.createSqlType(SqlTypeName.VARCHAR), 0);
        RexNode castExpr = rexBuilder.makeCast(
            typeFactory.createSqlType(SqlTypeName.INTEGER), inputRef);
        
        IntegerDomain outputDomain = IntegerDomain.of(
            Collections.singletonList(new IntegerDomain.Interval(100, 200)));
        
        Domain<?, ?> inputDomain = transformer.refineInputDomain(castExpr, outputDomain);
        
        // Input should be RegexDomain
        assertTrue(inputDomain instanceof RegexDomain);
        RegexDomain regexInput = (RegexDomain) inputDomain;
        
        // Should match numeric values in the range
        assertFalse(regexInput.isEmpty());
    }
    
    @Test
    public void testCastStringToInteger_SmallRange() {
        // CAST(x AS INTEGER) where output is [10, 12]
        // Input should be RegexDomain matching "10|11|12"
        
        RexNode inputRef = rexBuilder.makeInputRef(
            typeFactory.createSqlType(SqlTypeName.VARCHAR), 0);
        RexNode castExpr = rexBuilder.makeCast(
            typeFactory.createSqlType(SqlTypeName.INTEGER), inputRef);
        
        IntegerDomain outputDomain = IntegerDomain.of(
            Collections.singletonList(new IntegerDomain.Interval(10, 12)));
        
        Domain<?, ?> inputDomain = transformer.refineInputDomain(castExpr, outputDomain);
        
        assertTrue(inputDomain instanceof RegexDomain);
        RegexDomain regexInput = (RegexDomain) inputDomain;
        assertFalse(regexInput.isEmpty());
    }
    
    @Test
    public void testCastStringToInteger_RegexDomainOutput() {
        // CAST(x AS INTEGER) where output is RegexDomain "^[0-9]{3}$"
        // This represents integers 0-999 as strings
        
        RexNode inputRef = rexBuilder.makeInputRef(
            typeFactory.createSqlType(SqlTypeName.VARCHAR), 0);
        RexNode castExpr = rexBuilder.makeCast(
            typeFactory.createSqlType(SqlTypeName.INTEGER), inputRef);
        
        RegexDomain outputDomain = new RegexDomain("^[0-9]{3}$");
        
        Domain<?, ?> inputDomain = transformer.refineInputDomain(castExpr, outputDomain);
        
        // Input should be RegexDomain (same or refined)
        assertTrue(inputDomain instanceof RegexDomain);
        RegexDomain regexInput = (RegexDomain) inputDomain;
        assertFalse(regexInput.isEmpty());
    }
    
    // ========== Integer to String Conversion Tests ==========
    
    @Test
    public void testCastIntegerToString_RegexDomainOutput() {
        // CAST(x AS VARCHAR) where output is RegexDomain "^[0-9]{3}$"
        // Input should be IntegerDomain [0, 999]
        
        RexNode inputRef = rexBuilder.makeInputRef(
            typeFactory.createSqlType(SqlTypeName.INTEGER), 0);
        RexNode castExpr = rexBuilder.makeCast(
            typeFactory.createSqlType(SqlTypeName.VARCHAR), inputRef);
        
        RegexDomain outputDomain = new RegexDomain("^[0-9]{3}$");
        
        Domain<?, ?> inputDomain = transformer.refineInputDomain(castExpr, outputDomain);
        
        // Input should be IntegerDomain (converted from regex)
        assertTrue(inputDomain instanceof IntegerDomain);
        IntegerDomain intInput = (IntegerDomain) inputDomain;
        
        assertTrue(intInput.contains(0));
        assertTrue(intInput.contains(500));
        assertTrue(intInput.contains(999));
        assertFalse(intInput.contains(1000));
    }
    
    @Test
    public void testCastIntegerToString_SpecificValues() {
        // CAST(x AS VARCHAR) where output is RegexDomain "^(10|20|30)$"
        // Input should be IntegerDomain with values 10, 20, 30
        
        RexNode inputRef = rexBuilder.makeInputRef(
            typeFactory.createSqlType(SqlTypeName.INTEGER), 0);
        RexNode castExpr = rexBuilder.makeCast(
            typeFactory.createSqlType(SqlTypeName.VARCHAR), inputRef);
        
        RegexDomain outputDomain = new RegexDomain("^(10|20|30)$");
        
        Domain<?, ?> inputDomain = transformer.refineInputDomain(castExpr, outputDomain);
        
        assertTrue(inputDomain instanceof IntegerDomain);
        IntegerDomain intInput = (IntegerDomain) inputDomain;
        
        assertTrue(intInput.contains(10));
        assertTrue(intInput.contains(20));
        assertTrue(intInput.contains(30));
        assertFalse(intInput.contains(15));
        assertFalse(intInput.contains(40));
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCastIntegerToString_IntegerDomainOutput() {
        // CAST(x AS VARCHAR) with IntegerDomain output doesn't make sense
        
        RexNode inputRef = rexBuilder.makeInputRef(
            typeFactory.createSqlType(SqlTypeName.INTEGER), 0);
        RexNode castExpr = rexBuilder.makeCast(
            typeFactory.createSqlType(SqlTypeName.VARCHAR), inputRef);
        
        IntegerDomain outputDomain = IntegerDomain.of(
            Collections.singletonList(new IntegerDomain.Interval(10, 20)));
        
        transformer.refineInputDomain(castExpr, outputDomain);
    }
    
    // ========== Same Type Casts ==========
    
    @Test
    public void testCastSameType_Integer() {
        // CAST(x AS INTEGER) where x is already INTEGER
        
        RexNode inputRef = rexBuilder.makeInputRef(
            typeFactory.createSqlType(SqlTypeName.INTEGER), 0);
        RexNode castExpr = rexBuilder.makeCast(
            typeFactory.createSqlType(SqlTypeName.INTEGER), inputRef);
        
        IntegerDomain outputDomain = IntegerDomain.of(
            Collections.singletonList(new IntegerDomain.Interval(10, 20)));
        
        Domain<?, ?> inputDomain = transformer.refineInputDomain(castExpr, outputDomain);
        
        // Should return same domain
        assertEquals(inputDomain, outputDomain);
    }
    
    @Test
    public void testCastSameType_String() {
        // CAST(x AS VARCHAR) where x is already VARCHAR
        
        RexNode inputRef = rexBuilder.makeInputRef(
            typeFactory.createSqlType(SqlTypeName.VARCHAR), 0);
        RexNode castExpr = rexBuilder.makeCast(
            typeFactory.createSqlType(SqlTypeName.VARCHAR), inputRef);
        
        RegexDomain outputDomain = new RegexDomain("^[0-9]{3}$");
        
        Domain<?, ?> inputDomain = transformer.refineInputDomain(castExpr, outputDomain);
        
        // Should return same domain
        assertEquals(inputDomain, outputDomain);
    }
    
    // ========== Numeric Type Conversions ==========
    
    @Test
    public void testCastIntegerToBigInt() {
        // CAST(x AS BIGINT) where x is INTEGER
        
        RexNode inputRef = rexBuilder.makeInputRef(
            typeFactory.createSqlType(SqlTypeName.INTEGER), 0);
        RexNode castExpr = rexBuilder.makeCast(
            typeFactory.createSqlType(SqlTypeName.BIGINT), inputRef);
        
        IntegerDomain outputDomain = IntegerDomain.of(
            Collections.singletonList(new IntegerDomain.Interval(1000, 2000)));
        
        Domain<?, ?> inputDomain = transformer.refineInputDomain(castExpr, outputDomain);
        
        // Should preserve IntegerDomain
        assertTrue(inputDomain instanceof IntegerDomain);
        assertEquals(inputDomain, outputDomain);
    }
    
    // ========== Edge Cases ==========
    
    @Test
    public void testCastStringToInteger_EmptyDomain() {
        RexNode inputRef = rexBuilder.makeInputRef(
            typeFactory.createSqlType(SqlTypeName.VARCHAR), 0);
        RexNode castExpr = rexBuilder.makeCast(
            typeFactory.createSqlType(SqlTypeName.INTEGER), inputRef);
        
        IntegerDomain outputDomain = IntegerDomain.empty();
        
        Domain<?, ?> inputDomain = transformer.refineInputDomain(castExpr, outputDomain);
        
        assertTrue(inputDomain instanceof RegexDomain);
        assertTrue(inputDomain.isEmpty());
    }
    
    @Test
    public void testCastStringToInteger_NonConvertibleRegex() {
        // Output regex contains non-numeric patterns
        RexNode inputRef = rexBuilder.makeInputRef(
            typeFactory.createSqlType(SqlTypeName.VARCHAR), 0);
        RexNode castExpr = rexBuilder.makeCast(
            typeFactory.createSqlType(SqlTypeName.INTEGER), inputRef);
        
        // This regex has wildcards, not convertible to IntegerDomain
        RegexDomain outputDomain = new RegexDomain("^.*$");
        
        Domain<?, ?> inputDomain = transformer.refineInputDomain(castExpr, outputDomain);
        
        // Should fall back to integer format constraint
        assertTrue(inputDomain instanceof RegexDomain);
        assertFalse(inputDomain.isEmpty());
    }
    
    // ========== canHandle Tests ==========
    
    @Test
    public void testCanHandle() {
        RexNode inputRef = rexBuilder.makeInputRef(
            typeFactory.createSqlType(SqlTypeName.INTEGER), 0);
        RexNode castExpr = rexBuilder.makeCast(
            typeFactory.createSqlType(SqlTypeName.VARCHAR), inputRef);
        
        assertTrue(transformer.canHandle(castExpr));
    }
    
    @Test
    public void testCannotHandleNonCast() {
        RexNode inputRef = rexBuilder.makeInputRef(
            typeFactory.createSqlType(SqlTypeName.INTEGER), 0);
        
        assertFalse(transformer.canHandle(inputRef));
    }
    
    @Test
    public void testIsVariableOperandPositionValid() {
        RexNode inputRef = rexBuilder.makeInputRef(
            typeFactory.createSqlType(SqlTypeName.INTEGER), 0);
        RexNode castExpr = rexBuilder.makeCast(
            typeFactory.createSqlType(SqlTypeName.VARCHAR), inputRef);
        
        assertTrue(transformer.isVariableOperandPositionValid(castExpr));
    }
    
    @Test
    public void testGetChildForVariable() {
        RexNode inputRef = rexBuilder.makeInputRef(
            typeFactory.createSqlType(SqlTypeName.INTEGER), 0);
        RexNode castExpr = rexBuilder.makeCast(
            typeFactory.createSqlType(SqlTypeName.VARCHAR), inputRef);
        
        RexNode child = transformer.getChildForVariable(castExpr);
        assertEquals(child, inputRef);
    }
}
