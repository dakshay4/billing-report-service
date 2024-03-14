package com.moveinsync.billingreportservice.enums;

public enum OfficeHeaders {

    OFFICE(0, "Office", ReportDataType.STRING),
    TOTAL_TRIP_COUNT(1, "Total Trip Count", ReportDataType.INTEGER),
    TOTAL_TRIP_KM(2, "Total Trip Km", ReportDataType.BIGDECIMAL),
    TOTAL_KM(3, "Total km", ReportDataType.BIGDECIMAL),
    MISC_ADJUSTMENT(4, "Misc Adjustments", ReportDataType.BIGDECIMAL),
    ESCORT_COUNT(5, "Escort Count", ReportDataType.INTEGER),
    ESCORT_COST(6, "Escort Cost", ReportDataType.BIGDECIMAL),
    BASE_COST(7, "Contract Price", ReportDataType.BIGDECIMAL),
    EXTRA_KM_COST(8, "Extra Km Cost", ReportDataType.BIGDECIMAL),
    EXTRA_DUTY_COST(9, "Extra Duty Cost", ReportDataType.BIGDECIMAL),
    EXTRA_HOUR_COST(10, "Extra Hour Cost", ReportDataType.BIGDECIMAL),
    DRIVER_ALLOWANCE(11, "Driver Allowance", ReportDataType.BIGDECIMAL),
    AC_COST(12, "Ac Cost", ReportDataType.BIGDECIMAL),
    EXPENSE_COST(13, "Parking", ReportDataType.BIGDECIMAL),
    GRAND_TOTAL_GST(14, "Grand Total GST", ReportDataType.BIGDECIMAL),
    GRAND_TOTAL(15, "Grand Total", ReportDataType.BIGDECIMAL),
    FROZEN(16, "Frozen", ReportDataType.BOOLEAN);

    private final int index;
    private final String columnLabel;
    private final ReportDataType dataType;

    OfficeHeaders(int index, String columnLabel, ReportDataType dataType) {
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
