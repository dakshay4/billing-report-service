package com.moveinsync.billingreportservice.enums;


import java.util.Arrays;

public enum ContractHeaders implements TableHeaders {
    CAPACITY(0, GlobalColumnLabels.CAPACITY, ReportDataType.INTEGER),
    VEHICLE_TYPE(1, GlobalColumnLabels.VEHICLE_TYPE, ReportDataType.STRING),
    CONTRACT(2, GlobalColumnLabels.CONTRACT, ReportDataType.STRING),
    TOTAL_TRIP_COUNT(3, GlobalColumnLabels.TRIP_COUNT, ReportDataType.INTEGER),
    TOTAL_TRIP_KM(4, GlobalColumnLabels.TOTAL_TRIP_KM, ReportDataType.BIGDECIMAL),
    TOTAL_KM(5, GlobalColumnLabels.TOTAL_KM, ReportDataType.BIGDECIMAL),
    MISC_ADJUSTMENTS(6, GlobalColumnLabels.ADJUSTMENTS, ReportDataType.BIGDECIMAL),
    ESCORT_COUNT(7, GlobalColumnLabels.ESCORT_COUNT, ReportDataType.INTEGER),
    ESCORT_COST(8, GlobalColumnLabels.ESCORT_COST, ReportDataType.BIGDECIMAL),
    BASE_COST(9, GlobalColumnLabels.BASE_COST, ReportDataType.BIGDECIMAL),
    EXTRA_KM_COST(10, GlobalColumnLabels.EXTRA_KM_COST, ReportDataType.BIGDECIMAL),
    EXTRA_DUTY_COST(11, GlobalColumnLabels.EXTRA_DUTY_COST, ReportDataType.BIGDECIMAL),
    EXTRA_HOUR_COST(12, GlobalColumnLabels.EXTRA_HOUR_COST, ReportDataType.BIGDECIMAL),
    DRIVER_ALLOWANCE(13, GlobalColumnLabels.DRIVER_ALLOWANCE, ReportDataType.BIGDECIMAL),
    AC_COST(14, GlobalColumnLabels.AC_COST, ReportDataType.BIGDECIMAL),
    EXPENSE_COST(15, GlobalColumnLabels.EXPENSE_COST, ReportDataType.BIGDECIMAL),
    GST(16, GlobalColumnLabels.GST, ReportDataType.BIGDECIMAL),
    GRAND_TOTAL(17, GlobalColumnLabels.GRAND_TOTAL_GST, ReportDataType.BIGDECIMAL);

    private final int index;
    private final GlobalColumnLabels key;
    private final ReportDataType dataType;

    ContractHeaders(int index, GlobalColumnLabels key, ReportDataType dataType) {
        this.index = index;
        this.key = key;
        this.dataType = dataType;
    }

    public static ContractHeaders getFromLabelName(String columnLabel) {
        return Arrays.stream(values())
                .filter(e -> e.getLabel().equals(columnLabel))
                .findFirst()
                .orElse(null);
    }

    @Override
    public int getIndex() {
        return index;
    }

    public String getLabel() {
        return key.getLabelIdentifier();
    }

    public ReportDataType getDataType() {
        return dataType;
    }
}
