/**
 * Copyright 2025 LinkedIn Corporation. All rights reserved.
 * Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
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
