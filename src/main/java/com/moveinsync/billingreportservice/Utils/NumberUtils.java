package com.moveinsync.billingreportservice.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtils {

    private static final int scale = 2;

    public static BigDecimal roundOffAndAnd(String val1, String val2) {
        if(val1 == null || val1.isEmpty()) val1="0";
        if(val2 == null || val2.isEmpty()) val2="0";
        BigDecimal addend = new BigDecimal(val1);
        BigDecimal augend = new BigDecimal(val2);
        return addend.add(augend).setScale(scale, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal roundOff(String val1) {
        if(val1 == null || val1.isEmpty()) val1="0";
        BigDecimal val = new BigDecimal(val1);
        return val.setScale(scale, RoundingMode.HALF_EVEN);
    }
}
