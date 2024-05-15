package com.moveinsync.billingreportservice.enums;

public enum OfficeHeaders implements TableHeaders {

    OFFICE(0, GlobalColumnLabels.OFFICE, ReportDataType.STRING),
    TRIP_COUNT(1, GlobalColumnLabels.TRIP_COUNT, ReportDataType.INTEGER),
    TOTAL_TRIP_KM(2, GlobalColumnLabels.TOTAL_TRIP_KM, ReportDataType.BIGDECIMAL),
    TOTAL_KM(3, GlobalColumnLabels.TOTAL_KM, ReportDataType.BIGDECIMAL),
    MISC_ADJUSTMENT(4, GlobalColumnLabels.ADJUSTMENTS, ReportDataType.BIGDECIMAL),
    ESCORT_COUNT(5, GlobalColumnLabels.ESCORT_COUNT, ReportDataType.INTEGER),
    ESCORT_COST(6, GlobalColumnLabels.ESCORT_COST, ReportDataType.BIGDECIMAL),
    BASE_COST(7, GlobalColumnLabels.BASE_COST, ReportDataType.BIGDECIMAL),
    EXTRA_KM_COST(8, GlobalColumnLabels.EXTRA_KM_COST, ReportDataType.BIGDECIMAL),
    EXTRA_DUTY_COST(9, GlobalColumnLabels.EXTRA_DUTY_COST, ReportDataType.BIGDECIMAL),
    EXTRA_HOUR_COST(10, GlobalColumnLabels.EXTRA_HOUR_COST, ReportDataType.BIGDECIMAL),
    DRIVER_ALLOWANCE(11, GlobalColumnLabels.DRIVER_ALLOWANCE, ReportDataType.BIGDECIMAL),
    AC_COST(12, GlobalColumnLabels.AC_COST, ReportDataType.BIGDECIMAL),
    EXPENSE_COST(13, GlobalColumnLabels.EXPENSE_COST, ReportDataType.BIGDECIMAL),
    GRAND_TOTAL(14, GlobalColumnLabels.GRAND_TOTAL, ReportDataType.BIGDECIMAL),
    GST(15, GlobalColumnLabels.GST, ReportDataType.BIGDECIMAL),
    GRAND_TOTAL_GST(16, GlobalColumnLabels.GRAND_TOTAL_GST, ReportDataType.BIGDECIMAL),
    FROZEN(17, GlobalColumnLabels.FROZEN, ReportDataType.BOOLEAN);


    private final int index;
    private final GlobalColumnLabels key;
    private final ReportDataType dataType;

    OfficeHeaders(int index, GlobalColumnLabels key, ReportDataType dataType) {
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
