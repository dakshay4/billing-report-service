package com.moveinsync.billingreportservice.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class NumberUtils {

  private static final int ROUND_SCALE = 2;
  private static final Logger log = LoggerFactory.getLogger(NumberUtils.class);

  public static BigDecimal roundOffAndAnd(String val1, String val2) {
    if (StringUtils.isEmpty(val1))
      val1 = "0";
    if (StringUtils.isEmpty(val2))
      val2 = "0";
    BigDecimal addend = new BigDecimal(val1);
    BigDecimal augend = new BigDecimal(val2);
    return addend.add(augend).setScale(ROUND_SCALE, RoundingMode.HALF_EVEN);
  }

  public static BigDecimal roundOff(String val1) {
    try {
      if (StringUtils.isEmpty(val1)) val1 = "0";
      BigDecimal val = new BigDecimal(val1);
      return val.setScale(ROUND_SCALE, RoundingMode.HALF_EVEN);
    } catch (NumberFormatException ex) {
      log.warn("Unable to Parse to BigDecimal {}", val1);
    }
    return BigDecimal.ZERO;
  }

  public static BigDecimal roundOff(Double val1) {
    if (val1 == null) return BigDecimal.ZERO;
    return BigDecimal.valueOf(val1).setScale(ROUND_SCALE, RoundingMode.HALF_EVEN);
  }

  public static Integer parseInteger(String val) {
    int res = 0;
    try {
      res = Integer.parseInt(val);
    } catch (NumberFormatException ex) {
      log.warn("Unable to Parse to Integer {}", val);
    }
    return res;
  }

}
