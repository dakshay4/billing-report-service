package com.moveinsync.billingreportservice.enums;

public enum DutyHeaders implements TableHeaders {

    DATE(0, GlobalColumnLabels.DATE, ReportDataType.DATE),
    DUTY_COUNT(1, GlobalColumnLabels.DUTY_COUNT, ReportDataType.INTEGER),
    TRIP_COUNT(2, GlobalColumnLabels.TRIP_COUNT, ReportDataType.INTEGER),
    TRIP_KM(3, GlobalColumnLabels.TOTAL_TRIP_KM, ReportDataType.BIGDECIMAL),
    TOTAL_KM(4, GlobalColumnLabels.TOTAL_KM, ReportDataType.BIGDECIMAL),
    BASE_COST(5, GlobalColumnLabels.BASE_COST, ReportDataType.BIGDECIMAL),
    MISC_ADJUSTMENTS(6, GlobalColumnLabels.ADJUSTMENTS, ReportDataType.BIGDECIMAL),
    AC_CHARGES(7, GlobalColumnLabels.AC_COST, ReportDataType.BIGDECIMAL),
    EXPENSE_COST(8, GlobalColumnLabels.EXPENSE_COST, ReportDataType.BIGDECIMAL),
    ESCORT_COST(9, GlobalColumnLabels.ESCORT_COST, ReportDataType.BIGDECIMAL),
    EXTRA_KM_AMOUNT(10, GlobalColumnLabels.EXTRA_KM_COST, ReportDataType.BIGDECIMAL),
    EXTRA_DUTY_AMOUNT(11, GlobalColumnLabels.EXTRA_DUTY_COST, ReportDataType.BIGDECIMAL),
    EXTRA_HOUR_AMOUNT(12, GlobalColumnLabels.EXTRA_HOUR_COST, ReportDataType.BIGDECIMAL),
    DRIVER_BATA(13, GlobalColumnLabels.DRIVER_ALLOWANCE, ReportDataType.BIGDECIMAL),
    GRAND_TOTAL(14, GlobalColumnLabels.GRAND_TOTAL, ReportDataType.BIGDECIMAL),
    GST(15, GlobalColumnLabels.GST, ReportDataType.BIGDECIMAL),
    GRAND_TOTAL_GST(16, GlobalColumnLabels.GRAND_TOTAL_GST, ReportDataType.BIGDECIMAL),
    ACTION(17, GlobalColumnLabels.ACTION, ReportDataType.STRING),
    ENTITY_ID(18, GlobalColumnLabels.ENTITY_ID, ReportDataType.STRING);

    private final int index;
    private final GlobalColumnLabels key;
    private final ReportDataType dataType;

    DutyHeaders(int index, GlobalColumnLabels key, ReportDataType dataType) {
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

}
