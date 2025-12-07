package com.linkedin.coral.datagen.domain;

/**
 * Exception thrown when a RegexDomain cannot be converted to IntegerDomain.
 * 
 * This occurs when the regex pattern contains constructs that are not
 * compatible with numeric domain representation, such as:
 * - Unbounded repetition (*, +, ?, {n,})
 * - Non-digit characters
 * - Missing anchors
 * - Lookaheads/lookbehinds
 * - Backreferences
 */
public class NonConvertibleDomainException extends RuntimeException {
    
    public NonConvertibleDomainException(String message) {
        super(message);
    }
    
    public NonConvertibleDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
