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

package de.knightsoftnet.validators.shared.impl;

import de.knightsoftnet.validators.server.data.CreateTinMapConstantsClass;
import de.knightsoftnet.validators.shared.Tin;
import de.knightsoftnet.validators.shared.data.TinMapSharedConstants;
import de.knightsoftnet.validators.shared.util.BeanPropertyReaderUtil;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Check if a Tax Identification Number (TIN) field is valid for the selected country.
 *
 * @author Manfred Tremmel
 *
 */
public class TinValidator implements ConstraintValidator<Tin, Object> {
  /**
   * modulo 11.
   */
  private static final int MODULO_11 = 11;

  /**
   * map of vat id regex values for the different countries.
   */
  private static final TinMapSharedConstants TIN_MAP = CreateTinMapConstantsClass.create();

  /**
   * error message key.
   */
  private String message;

  /**
   * field name of the country code field.
   */
  private String fieldCountryCode;

  /**
   * are lower case country codes allowed (true/false).
   */
  private boolean allowLowerCaseCountryCode;

  /**
   * field name of the vat id field.
   */
  private String fieldTin;

  /**
   * {@inheritDoc} initialize the validator.
   *
   * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
   */
  @Override
  public final void initialize(final Tin pconstraintAnnotation) {
    this.message = pconstraintAnnotation.message();
    this.fieldCountryCode = pconstraintAnnotation.fieldCountryCode();
    this.allowLowerCaseCountryCode = pconstraintAnnotation.allowLowerCaseCountryCode();
    this.fieldTin = pconstraintAnnotation.fieldTin();
  }

  @Override
  public final boolean isValid(final Object pvalue, final ConstraintValidatorContext pcontext) {
    if (pvalue == null) {
      return true;
    }
    try {
      String countryCode =
          BeanPropertyReaderUtil.getNullSaveStringProperty(pvalue, this.fieldCountryCode);
      if (StringUtils.isEmpty(countryCode)) {
        return true;
      }
      final String Tin = BeanPropertyReaderUtil.getNullSaveStringProperty(pvalue, this.fieldTin);
      if (StringUtils.isEmpty(Tin)) {
        return true;
      }

      if (this.allowLowerCaseCountryCode) {
        countryCode = StringUtils.upperCase(countryCode);
      }

      final String regExCheck = TIN_MAP.tins().get(countryCode);
      if (regExCheck == null) {
        return true;
      }
      if (Tin.matches(regExCheck) && this.checkSumTest(countryCode, Tin)) {
        return true;
      }
      this.switchContext(pcontext);
      return false;
    } catch (final Exception ignore) {
      this.switchContext(pcontext);
      return false;
    }
  }

  private void switchContext(final ConstraintValidatorContext pcontext) {
    pcontext.disableDefaultConstraintViolation();
    pcontext.buildConstraintViolationWithTemplate(this.message).addPropertyNode(this.fieldTin)
        .addConstraintViolation();
  }

  private boolean checkSumTest(final String pcountryCode, final String ptin) {
    boolean checkSumOk = false;
    switch (pcountryCode) {
      case "AT":
        checkSumOk = this.checkAtTin(ptin);
        break;
      case "DE":
        checkSumOk = this.checkDeTin(ptin);
        break;
      default:
        // for other countries, I haven't found checksum rules
        checkSumOk = true;
        break;
    }
    return checkSumOk;
  }

  /**
   * check the Tax Identification Number number, country version for Austria.
   *
   * @param ptin vat id to check
   * @return true if checksum is ok
   */
  private boolean checkAtTin(final String ptin) {
    final int checkSum = ptin.charAt(8) - '0';
    final int sum = ptin.charAt(0) - '0' + squareSum((ptin.charAt(1) - '0') * 2) + ptin.charAt(2)
        - '0' + squareSum((ptin.charAt(3) - '0') * 2) + ptin.charAt(4) - '0'
        + squareSum((ptin.charAt(5) - '0') * 2) + ptin.charAt(6) - '0'
        + squareSum((ptin.charAt(7) - '0') * 2);
    final int calculatedCheckSum = (80 - sum) % 10;
    return checkSum == calculatedCheckSum;
  }

  /**
   * check the Tax Identification Number number, country version for Germany.
   *
   * @param ptin vat id to check
   * @return true if checksum is ok
   */
  private boolean checkDeTin(final String ptin) {
    final int checkSum = ptin.charAt(10) - '0';
    int sum = 10;
    for (int i = 0; i < 10; i++) {
      int summe = (ptin.charAt(i) - '0' + sum) % 10;
      if (summe == 0) {
        summe = 10;
      }
      sum = 2 * summe % MODULO_11;
    }
    int calculatedCheckSum = MODULO_11 - sum;
    if (calculatedCheckSum == 10) {
      calculatedCheckSum = 0;
    }
    return checkSum == calculatedCheckSum;
  }

  /**
   * calculate square sum.
   *
   * @param pvalue value to calculate square sum for
   * @return square sum
   */
  private static int squareSum(final int pvalue) {
    int result = 0;
    for (final char valueDigit : String.valueOf(pvalue).toCharArray()) {
      result += Character.digit(valueDigit, 10);
    }
    return result;
  }
}
