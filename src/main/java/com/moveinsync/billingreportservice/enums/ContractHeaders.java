package com.moveinsync.billingreportservice.enums;


import java.util.Arrays;

public enum ContractHeaders {
    CAPACITY(0, "Capacity", ReportDataType.INTEGER),
    VEHICLE_TYPE(1, "Vehicle Type", ReportDataType.STRING),
    CONTRACT(2, "Contract", ReportDataType.STRING),
    TOTAL_TRIP_COUNT(3, "Total Trip Count", ReportDataType.INTEGER),
    TOTAL_TRIP_KM(4, "Total Trip Km", ReportDataType.DOUBLE),
    TOTAL_KM(5, "Total km", ReportDataType.DOUBLE),
    MISC_ADJUSTMENTS(6, "Misc Adjustments", ReportDataType.DOUBLE),
    ESCORT_COST(7, "Escort Cost", ReportDataType.DOUBLE),
    ESCORT_COUNT(8, "Escort Count", ReportDataType.INTEGER),
    CONTRACT_PRICE(9, "Contract Price", ReportDataType.DOUBLE),
    EXTRA_KM_COST(10, "Extra Km Cost", ReportDataType.DOUBLE),
    EXTRA_DUTY_COST(11, "Extra Duty Cost", ReportDataType.DOUBLE),
    EXTRA_HOUR_COST(12, "Extra Hour Cost", ReportDataType.DOUBLE),
    DRIVER_ALLOWANCE(13, "Driver Allowance", ReportDataType.DOUBLE),
    AC_COST(14, "Ac Cost", ReportDataType.DOUBLE),
    PARKING(15, "Parking", ReportDataType.DOUBLE),
    GRAND_TOTAL_GST(16, "Grand Total GST", ReportDataType.DOUBLE),
    GRAND_TOTAL(17, "Grand Total", ReportDataType.DOUBLE);

    private final int index;
    private final String columnLabel;
    private final ReportDataType dataType;

    ContractHeaders(int index, String columnLabel, ReportDataType dataType) {
        this.index = index;
        this.columnLabel = columnLabel;
        this.dataType = dataType;
    }

    public static ContractHeaders getFromLabelName(String columnLabel) {
        return Arrays.stream(values())
                .filter(e -> e.columnLabel.equals(columnLabel))
                .findFirst()
                .orElse(null);
    }

    public String getColumnLabel() {
        return columnLabel;
    }

    public ReportDataType getDataType() {
        return dataType;
    }
}
