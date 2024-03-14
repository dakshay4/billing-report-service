package com.moveinsync.billingreportservice.enums;

public enum VehicleHeaders implements TableHeaders {

    ENTITY_ID(0, "Entity Id", ReportDataType.STRING),
    CONTRACT(1, "Contract", ReportDataType.STRING),
    NO_OF_DUTIES(2, "No of Duties", ReportDataType.INTEGER),
    TOTAL_TRIP_COUNT(3, "Total Trip Count", ReportDataType.INTEGER),
    TOTAL_TRIP_KM(4, "Total Trip Km", ReportDataType.BIGDECIMAL),
    TOTAL_KM(5, "Total km", ReportDataType.BIGDECIMAL),
    CONTRACT_PRICE(6, "Contract Price", ReportDataType.BIGDECIMAL),
    MISC_ADJUSTMENT(7, "Misc Adjustments", ReportDataType.BIGDECIMAL),
    AC_COST(8, "Ac Cost", ReportDataType.BIGDECIMAL),
    PARKING(9, "Parking", ReportDataType.BIGDECIMAL),
    ESCORT_COUNT(10, "Escort Count", ReportDataType.INTEGER),
    ESCORT_COST(11, "Escort Cost", ReportDataType.BIGDECIMAL),
    EXTRA_KM_COST(12, "Extra Km Cost", ReportDataType.BIGDECIMAL),
    EXTRA_DUTY_COST(13, "Extra Duty Cost", ReportDataType.BIGDECIMAL),
    EXTRA_HOUR_COST(14, "Extra Hour Cost", ReportDataType.BIGDECIMAL),
    DRIVER_ALLOWANCE(15, "Driver Allowance", ReportDataType.BIGDECIMAL),
    GRAND_TOTAL_GST(16, "Grand Total GST", ReportDataType.BIGDECIMAL),
    GRAND_TOTAL(17, "Grand Total", ReportDataType.BIGDECIMAL),
    ACTION(18, "Action", ReportDataType.STRING);

    private final int index;
    private final String columnLabel;
    private final ReportDataType dataType;

    VehicleHeaders(int index, String columnLabel, ReportDataType dataType) {
        this.index = index;
        this.columnLabel = columnLabel;
        this.dataType = dataType;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public String getColumnLabel() {
        return columnLabel;
    }

    @Override
    public ReportDataType getDataType() {
        return dataType;
    }
}
