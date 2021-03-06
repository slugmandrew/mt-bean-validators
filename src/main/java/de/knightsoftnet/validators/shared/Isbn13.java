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

import de.knightsoftnet.validators.shared.impl.Isbn13Validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.OverridesAttribute;
import javax.validation.Payload;

/**
 * The annotated element must be a valid International Standard Book Number in the long (13 digits)
 * format.<br>
 * Supported types are Strings, other Objects are transfered to Strings, <code>null</code> elements
 * are considered valid. Minus signs as separators are allowed, when <code>ignoreSeparators</code>
 * is set to <code>true</code>.<br>
 * There are numeric, size and checksum tests by apache commons validation routines.<br>
 *
 * @author Manfred Tremmel
 *
 */
@Documented
@Constraint(validatedBy = Isbn13Validator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE,
    ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@SizeWithoutSeparators(min = Isbn13Validator.ISBN13_LENGTH, max = Isbn13Validator.ISBN13_LENGTH)
public @interface Isbn13 {
  /**
   * localized message.
   */
  String message() default "{deKnightsoftnetValidatorsSharedValidationIsbn13Message}";

  /**
   * groups to use.
   */
  Class<?>[] groups() default {};

  /**
   * payload whatever.
   */
  Class<? extends Payload>[] payload() default {};

  /**
   * should separating minus signs be ignored (true/false).
   */
  @OverridesAttribute(constraint = SizeWithoutSeparators.class, name = "ignoreMinus")
  boolean ignoreSeparators() default false;

  /**
   * Defines several {@code @Isbn13} annotations on the same element.
   */
  @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE,
      ElementType.CONSTRUCTOR, ElementType.PARAMETER})
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  public @interface List {
    /**
     * isbn value.
     */
    Isbn13[] value();
  }
}
