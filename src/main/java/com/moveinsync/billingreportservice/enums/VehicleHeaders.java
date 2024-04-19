package com.moveinsync.billingreportservice.enums;

public enum VehicleHeaders implements TableHeaders {

    ENTITY_ID(0, GlobalColumnLabels.ENTITY_ID, ReportDataType.STRING),
    VEHICLE_NUMBER(1, GlobalColumnLabels.VEHICLE_NUMBER, ReportDataType.STRING),
    CONTRACT(2, GlobalColumnLabels.CONTRACT, ReportDataType.STRING),
    NO_OF_DUTIES(3, GlobalColumnLabels.DUTY_COUNT, ReportDataType.INTEGER),
    TOTAL_TRIP_COUNT(4, GlobalColumnLabels.TRIP_COUNT, ReportDataType.INTEGER),
    TOTAL_TRIP_KM(5, GlobalColumnLabels.TOTAL_TRIP_KM, ReportDataType.BIGDECIMAL),
    TOTAL_KM(6, GlobalColumnLabels.TOTAL_KM, ReportDataType.BIGDECIMAL),
    CONTRACT_PRICE(7, GlobalColumnLabels.BASE_COST, ReportDataType.BIGDECIMAL),
    MISC_ADJUSTMENT(8, GlobalColumnLabels.ADJUSTMENTS, ReportDataType.BIGDECIMAL),
    AC_COST(9, GlobalColumnLabels.AC_COST, ReportDataType.BIGDECIMAL),
    EXPENSE_COST(10, GlobalColumnLabels.EXPENSE_COST, ReportDataType.BIGDECIMAL),
    ESCORT_COUNT(11, GlobalColumnLabels.ESCORT_COUNT, ReportDataType.INTEGER),
    ESCORT_COST(12, GlobalColumnLabels.ESCORT_COST, ReportDataType.BIGDECIMAL),
    EXTRA_KM_COST(13, GlobalColumnLabels.EXTRA_KM_COST, ReportDataType.BIGDECIMAL),
    EXTRA_DUTY_COST(14, GlobalColumnLabels.EXTRA_DUTY_COST, ReportDataType.BIGDECIMAL),
    EXTRA_HOUR_COST(15, GlobalColumnLabels.EXTRA_HOUR_COST, ReportDataType.BIGDECIMAL),
    DRIVER_ALLOWANCE(16, GlobalColumnLabels.DRIVER_ALLOWANCE, ReportDataType.BIGDECIMAL),
    GST(17, GlobalColumnLabels.GST, ReportDataType.BIGDECIMAL),
    GRAND_TOTAL(18, GlobalColumnLabels.GRAND_TOTAL_GST, ReportDataType.BIGDECIMAL),
    ACTION(19, GlobalColumnLabels.ACTION, ReportDataType.STRING);

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
