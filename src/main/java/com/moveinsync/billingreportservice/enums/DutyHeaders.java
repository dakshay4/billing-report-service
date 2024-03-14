package com.moveinsync.billingreportservice.enums;

public enum DutyHeaders implements TableHeaders {

    DATE(0, "Date", ReportDataType.STRING),
    DUTIES(1, "No of Duties", ReportDataType.INTEGER),
    TRIPS(2, "Total Trip Count", ReportDataType.INTEGER),
    TRIP_KM(3, "Total Trip Km", ReportDataType.BIGDECIMAL),
    TOTAL_KM(4, "Total km", ReportDataType.BIGDECIMAL),
    COST_BEFORE_ADJUSTMENTS(5, "Cost before adjustments", ReportDataType.BIGDECIMAL),
    ADJUSTMENTS(6, "Misc Adjustments", ReportDataType.BIGDECIMAL),
    AC_CHARGES(7, "Ac charges", ReportDataType.BIGDECIMAL),
    PARKING(8, "Parking", ReportDataType.BIGDECIMAL),
    ESCORT_COST(9, "Escort Cost", ReportDataType.BIGDECIMAL),
    EXTRA_KM_AMOUNT(10, "Extra Km Amount", ReportDataType.BIGDECIMAL),
    EXTRA_DUTY_AMOUNT(11, "Extra Duty Amount", ReportDataType.BIGDECIMAL),
    EXTRA_HOUR_AMOUNT(12, "Extra Hour Amount", ReportDataType.BIGDECIMAL),
    DRIVER_BATA(13, "Driver Bata", ReportDataType.BIGDECIMAL),
    GRAND_TOTAL_GST(14, "Grand Total Gst", ReportDataType.BIGDECIMAL),
    GRAND_TOTAL(15, "Grand Total", ReportDataType.BIGDECIMAL),
    ACTION(16, "Action", ReportDataType.STRING);

    private final int index;
    private final String columnLabel;
    private final ReportDataType dataType;

    DutyHeaders(int index, String columnLabel, ReportDataType dataType) {
        this.index = index;
        this.columnLabel = columnLabel;
        this.dataType = dataType;
    }

    public int getIndex() {
        return index;
    }

    public String getColumnLabel() {
        return columnLabel;
    }

    public ReportDataType getDataType() {
        return dataType;
    }

}
