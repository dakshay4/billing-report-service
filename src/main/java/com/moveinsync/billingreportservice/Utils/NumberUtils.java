package com.moveinsync.billingreportservice.Utils;

import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtils {

  private static final int scale = 2;
  private static final Logger log = LoggerFactory.getLogger(NumberUtils.class);

  public static BigDecimal roundOffAndAnd(String val1, String val2) {
    if (val1 == null || val1.isEmpty())
      val1 = "0";
    if (val2 == null || val2.isEmpty())
      val2 = "0";
    BigDecimal addend = new BigDecimal(val1);
    BigDecimal augend = new BigDecimal(val2);
    return addend.add(augend).setScale(scale, RoundingMode.HALF_EVEN);
  }

  public static BigDecimal roundOff(String val1) {
    if(val1 == null) return BigDecimal.ZERO;
    try {
      if (val1 == null || val1.isEmpty())
        val1 = "0";
      BigDecimal val = new BigDecimal(val1);
      return val.setScale(scale, RoundingMode.HALF_EVEN);
    }catch (NumberFormatException ex) {log.warn("Unable to Parse to BigDecimal {}", val1);}
    return BigDecimal.ZERO;
  }

  public static BigDecimal roundOff(Double val1) {
    if(val1 == null) return BigDecimal.ZERO;
    return BigDecimal.valueOf(val1);
  }
  
  public static Integer parseInteger(String val) {
    Integer res = 0;
    try {
      res = Integer.parseInt(val);
    }catch (NumberFormatException ex) {log.warn("Unable to Parse to Integer {}", val);}
    return res;
  }

}
