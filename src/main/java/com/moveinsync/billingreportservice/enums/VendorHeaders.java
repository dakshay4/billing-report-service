package com.moveinsync.billingreportservice.enums;

import java.util.Arrays;

public enum VendorHeaders implements TableHeaders {
    VENDOR(0, GlobalColumnLabels.VENDOR, ReportDataType.STRING),
    ENTITY_ID(1, GlobalColumnLabels.ENTITY_ID, ReportDataType.STRING),
    TOTAL_TRIP_COUNT(2, GlobalColumnLabels.TRIP_COUNT, ReportDataType.INTEGER),
    TOTAL_TRIP_KM(3, GlobalColumnLabels.TOTAL_TRIP_KM, ReportDataType.BIGDECIMAL),
    TOTAL_KM(4, GlobalColumnLabels.TOTAL_KM, ReportDataType.BIGDECIMAL),
    ADJUSTMENT(5, GlobalColumnLabels.ADJUSTMENTS, ReportDataType.BIGDECIMAL),
    ESCORT_COUNT(6, GlobalColumnLabels.ESCORT_COUNT, ReportDataType.INTEGER),
    ESCORT_COST(7, GlobalColumnLabels.ESCORT_COST, ReportDataType.BIGDECIMAL),
    BASE_COST(8, GlobalColumnLabels.BASE_COST, ReportDataType.BIGDECIMAL),
    EXTRA_KM_COST(9, GlobalColumnLabels.EXTRA_KM_COST, ReportDataType.BIGDECIMAL),
    EXTRA_DUTY_COST(10, GlobalColumnLabels.EXTRA_DUTY_COST, ReportDataType.BIGDECIMAL),
    EXTRA_HOUR_COST(11, GlobalColumnLabels.EXTRA_HOUR_COST, ReportDataType.BIGDECIMAL),
    DRIVER_ALLOWANCE(12, GlobalColumnLabels.DRIVER_ALLOWANCE, ReportDataType.BIGDECIMAL),
    AC_COST(13, GlobalColumnLabels.AC_COST, ReportDataType.BIGDECIMAL),
    EXPENSE_COST(14, GlobalColumnLabels.EXPENSE_COST, ReportDataType.BIGDECIMAL),
    GRAND_TOTAL(15, GlobalColumnLabels.GRAND_TOTAL, ReportDataType.BIGDECIMAL),
    GST(16, GlobalColumnLabels.GST, ReportDataType.BIGDECIMAL),
    GRAND_TOTAL_GST(17, GlobalColumnLabels.GRAND_TOTAL_GST, ReportDataType.BIGDECIMAL),
    FROZEN(18, GlobalColumnLabels.FROZEN, ReportDataType.BOOLEAN);


    private final int index;
    private GlobalColumnLabels key;
    private final ReportDataType dataType;

    VendorHeaders(int index, GlobalColumnLabels key, ReportDataType dataType) {
        this.index = index;
        this.key = key;
        this.dataType = dataType;
    }

    public int getIndex() {
        return index;
    }

    public String getLabel() {
        return key.getLabelIdentifier();
    }

    public ReportDataType getDataType() {
        return dataType;
    }

    public static VendorHeaders getFromLabelName(String columnLabel) {
        return Arrays.stream(values())
                .filter(e -> e.getLabel().equals(columnLabel))
                .findFirst()
                .orElse(null);
    }
}