package com.moveinsync.billingreportservice.enums;

public enum DutyHeaders implements TableHeaders {

    DATE(0, "Date", ReportDataType.STRING),
    DUTIES(1, "No of Duties", ReportDataType.INTEGER),
    TRIPS(2, "Total Trip Count", ReportDataType.INTEGER),
    TRIP_KM(3, "Total Trip Km", ReportDataType.BIGDECIMAL),
    TOTAL_KM(4, "Total km", ReportDataType.BIGDECIMAL),
    BASE_COST(5, "Contract Price", ReportDataType.BIGDECIMAL),
    MISC_ADJUSTMENTS(6, "Misc Adjustments", ReportDataType.BIGDECIMAL),
    AC_CHARGES(7, "Ac Cost", ReportDataType.BIGDECIMAL),
    EXPENSE_COST(8, "Expense Cost", ReportDataType.BIGDECIMAL),
    ESCORT_COST(9, "Escort Cost", ReportDataType.BIGDECIMAL),
    EXTRA_KM_AMOUNT(10, "Extra Km Cost", ReportDataType.BIGDECIMAL),
    EXTRA_DUTY_AMOUNT(11, "Extra Duty Cost", ReportDataType.BIGDECIMAL),
    EXTRA_HOUR_AMOUNT(12, "Extra Hour Cost", ReportDataType.BIGDECIMAL),
    DRIVER_BATA(13, "Driver Allowance", ReportDataType.BIGDECIMAL),
    GST(14, "GST", ReportDataType.BIGDECIMAL),
    GRAND_TOTAL(15, "Grand Total", ReportDataType.BIGDECIMAL),
    ACTION(16, "Action", ReportDataType.STRING);

    private final int index;
    private final String key;
    private final ReportDataType dataType;

    DutyHeaders(int index, String key, ReportDataType dataType) {
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

}
