package com.moveinsync.billingreportservice.enums;

public enum VehicleHeaders implements TableHeaders {

    ENTITY_ID(0, GlobalColumnLabels.ENTITY_ID, ReportDataType.STRING),
    CONTRACT(1, GlobalColumnLabels.CONTRACT, ReportDataType.STRING),
    NO_OF_DUTIES(2, GlobalColumnLabels.DUTY_COUNT, ReportDataType.INTEGER),
    TOTAL_TRIP_COUNT(3, GlobalColumnLabels.TRIP_COUNT, ReportDataType.INTEGER),
    TOTAL_TRIP_KM(4, GlobalColumnLabels.TOTAL_TRIP_KM, ReportDataType.BIGDECIMAL),
    TOTAL_KM(5, GlobalColumnLabels.TOTAL_KM, ReportDataType.BIGDECIMAL),
    CONTRACT_PRICE(6, GlobalColumnLabels.BASE_COST, ReportDataType.BIGDECIMAL),
    MISC_ADJUSTMENT(7, GlobalColumnLabels.ADJUSTMENTS, ReportDataType.BIGDECIMAL),
    AC_COST(8, GlobalColumnLabels.AC_COST, ReportDataType.BIGDECIMAL),
    EXPENSE_COST(9, GlobalColumnLabels.EXPENSE_COST, ReportDataType.BIGDECIMAL),
    ESCORT_COUNT(10, GlobalColumnLabels.ESCORT_COUNT, ReportDataType.INTEGER),
    ESCORT_COST(11, GlobalColumnLabels.ESCORT_COST, ReportDataType.BIGDECIMAL),
    EXTRA_KM_COST(12, GlobalColumnLabels.EXTRA_KM_COST, ReportDataType.BIGDECIMAL),
    EXTRA_DUTY_COST(13, GlobalColumnLabels.EXTRA_DUTY_COST, ReportDataType.BIGDECIMAL),
    EXTRA_HOUR_COST(14, GlobalColumnLabels.EXTRA_HOUR_COST, ReportDataType.BIGDECIMAL),
    DRIVER_ALLOWANCE(15, GlobalColumnLabels.DRIVER_ALLOWANCE, ReportDataType.BIGDECIMAL),
    GST(16, GlobalColumnLabels.GST, ReportDataType.BIGDECIMAL),
    GRAND_TOTAL(17, GlobalColumnLabels.GRAND_TOTAL_GST, ReportDataType.BIGDECIMAL),
    ACTION(18, GlobalColumnLabels.ACTION, ReportDataType.STRING);

    private final int index;
    private final GlobalColumnLabels key;
    private final ReportDataType dataType;

    VehicleHeaders(int index, GlobalColumnLabels key, ReportDataType dataType) {
        this.index = index;
        this.key = key;
        this.dataType = dataType;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public String getLabel() {
        return key.getLabelIdentifier();
    }

    @Override
    public ReportDataType getDataType() {
        return dataType;
    }
}
