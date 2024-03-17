package com.moveinsync.billingreportservice.enums;

import java.util.Arrays;

public enum VendorHeaders implements TableHeaders {
    VENDOR(0, "Entity Id", ReportDataType.STRING),
    TOTAL_TRIP_COUNT(1, "Total Trip Count", ReportDataType.INTEGER),
    TOTAL_TRIP_KM(2, "Total Trip Km", ReportDataType.BIGDECIMAL),
    TOTAL_KM(3, "Total km", ReportDataType.BIGDECIMAL),
    ADJUSTMENT(4, "Misc Adjustments", ReportDataType.BIGDECIMAL),
    ESCORT_COUNT(5, "Escort Count", ReportDataType.INTEGER),
    ESCORT_COST(6, "Escort Cost", ReportDataType.BIGDECIMAL),
    BASE_COST(7, "Contract Price", ReportDataType.BIGDECIMAL),
    EXTRA_KM_COST(8, "Extra Km Cost", ReportDataType.BIGDECIMAL),
    EXTRA_DUTY_COST(9, "Extra Duty Cost", ReportDataType.BIGDECIMAL),
    EXTRA_HOUR_COST(10, "Extra Hour Cost", ReportDataType.BIGDECIMAL),
    DRIVER_ALLOWANCE(11, "Driver Allowance", ReportDataType.BIGDECIMAL),
    AC_COST(12, "Ac Cost", ReportDataType.BIGDECIMAL),
    EXPENSE_COST(13, "Expense Cost", ReportDataType.BIGDECIMAL),
    GST(14, "GST", ReportDataType.BIGDECIMAL),
    TOTAL(15, "Total", ReportDataType.BIGDECIMAL),
    FROZEN(16, "Frozen", ReportDataType.BOOLEAN);

    private final int index;
    private final String key;
    private final ReportDataType dataType;

    VendorHeaders(int index, String key, ReportDataType dataType) {
        this.index = index;
        this.key = key;
        this.dataType = dataType;
    }

    public int getIndex() {
        return index;
    }

    public String getKey() {
        return key;
    }

    public ReportDataType getDataType() {
        return dataType;
    }

    public static VendorHeaders getFromLabelName(String columnLabel) {
        return Arrays.stream(values())
                .filter(e -> e.getKey().equals(columnLabel))
                .findFirst()
                .orElse(null);
    }
}