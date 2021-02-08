package org.folio.domain;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;

import static org.junit.Assert.*;

@RunWith(JUnitParamsRunner.class)
public class MonetaryValueTest {

  @Test(expected = NullPointerException.class)
  public void stringConstructorThrowsExceptionWhenAmountIsNull() {
    new MonetaryValue((String) null);
  }

  @Test(expected = NullPointerException.class)
  public void doubleConstructorThrowsExceptionWhenAmountIsNull() {
    new MonetaryValue((Double) null);
  }

  @Test(expected = NullPointerException.class)
  public void bigDecimalConstructorThrowsExceptionWhenAmountIsNull() {
    new MonetaryValue((BigDecimal) null);
  }

  @Test
  @Parameters({ "0", "0.0", "0.00", "0.000", "0.005", "0.000000000000001" })
  public void monetaryValueIsZero(String value) {
    assertTrue(new MonetaryValue(value).isZero());
    assertTrue(new MonetaryValue("-" + value).isZero());
  }

  @Test
  @Parameters({ "1", "0.006", "0.0051", "0.0050000000000001" })
  public void monetaryValueIsNotZero(String value) {
    assertFalse(new MonetaryValue(value).isZero());
    assertFalse(new MonetaryValue("-" + value).isZero());
  }

  @Test
  @Parameters({ "1", "0.1", "0.01", "0.006", "0.0051", "0.0050000000000001" })
  public void monetaryValueIsPositive(String value) {
    assertTrue(new MonetaryValue(value).isPositive());
  }

  @Test
  @Parameters({ "-1", "0", "0.00", "0.000", "0.005", "0.000999999" })
  public void monetaryValueIsNotPositive(String value) {
    assertFalse(new MonetaryValue(value).isPositive());
  }

  @Test
  @Parameters({ "-1", "-0.1", "-0.01", "-0.006", "-0.0051", "-0.0050000000000001" })
  public void monetaryValueIsNegative(String value) {
    assertTrue(new MonetaryValue(value).isNegative());
  }

  @Test
  @Parameters({ "1", "0", "0.00", "0.000", "0.005", "-0.005", "0.000000000001", "-0.000000000001" })
  public void monetaryValueIsNotNegative(String value) {
    assertFalse(new MonetaryValue(value).isNegative());
  }

  @Test
  @Parameters({
    "0, 0.00",
    "0.0, 0.00",
    "0.00, 0.00",
    "0.000, 0.00",

    "-0, 0.00",
    "-0.0, 0.00",
    "-0.00, 0.00",
    "-0.000, 0.00",

    "1, 1.00",
    "0.1, 0.10",
    "0.01, 0.01",
    "0.001, 0.00",

    "-1, -1.00",
    "-0.1, -0.10",
    "-0.01, -0.01",
    "-0.001, 0.00",

    "0.005, 0.00",
    "0.0051, 0.01",
    "0.0050000000001, 0.01",

    "-0.005, 0.00",
    "-0.0051, -0.01",
    "-0.0050000000001, -0.01",

    "0.015, 0.02",
    "0.0149, 0.01",
    "0.0150000000001, 0.02",

    "-0.015, -0.02",
    "-0.0149, -0.01",
    "-0.0150000000001, -0.02",
  })
  public void toStringTest(String source, String expectedResult) {
    assertEquals(expectedResult, new MonetaryValue(source).toString());
  }

}
