/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package de.knightsoftnet.validators.shared;

import de.knightsoftnet.validators.shared.impl.BicValidator;
import de.knightsoftnet.validators.shared.impl.BicValueValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.OverridesAttribute;
import javax.validation.Payload;

/**
 * The annotated element must be a valid BIC (Business Identifier Code in the past also known as
 * Bank Identifier Code).<br>
 * Supported types are Strings, other Objects are transfered to Strings, <code>null</code> elements
 * are considered valid.<br>
 * If <code>ignoreWhiteSpaces</code> is set to <code>true</code>, whitespaces are removed before
 * test is done.<br>
 * It's a formal test, against format definition, size and SWIFT country list.<br>
 * There is no test of validity of the BIC!
 *
 * @author Manfred Tremmel
 *
 */
@Documented
@Constraint(validatedBy = BicValueValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE,
    ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@AlternateSize(size1 = BicValidator.BIC_LENGTH_MIN, size2 = BicValidator.BIC_LENGTH_MAX)
public @interface BicValue {
  /**
   * localized message.
   */
  String message() default "{deKnightsoftnetValidatorsSharedValidationBicMessage}";

  /**
   * groups to use.
   */
  Class<?>[] groups() default {};

  /**
   * payload whatever.
   */
  Class<? extends Payload>[] payload() default {};

  /**
   * should whitespaces be ignored (true/false).
   */
  @OverridesAttribute(constraint = AlternateSize.class, name = "ignoreWhiteSpaces")
  boolean ignoreWhitspaces() default false;

  /**
   * Defines several {@code @BicValue} annotations on the same element.
   */
  @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE,
      ElementType.CONSTRUCTOR, ElementType.PARAMETER})
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  public @interface List {
    /**
     * bic value.
     */
    BicValue[] value();
  }
}
