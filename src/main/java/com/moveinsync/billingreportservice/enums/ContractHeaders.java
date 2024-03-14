package com.moveinsync.billingreportservice.enums;

import java.util.Arrays;

public enum ContractHeaders implements TableHeaders {
    CAPACITY(0, "Capacity", ReportDataType.INTEGER),
    VEHICLE_TYPE(1, "Vehicle Type", ReportDataType.STRING),
    CONTRACT(2, "Contract", ReportDataType.STRING),
    TOTAL_TRIP_COUNT(3, "Total Trip Count", ReportDataType.INTEGER),
    TOTAL_TRIP_KM(4, "Total Trip Km", ReportDataType.BIGDECIMAL),
    TOTAL_KM(5, "Total km", ReportDataType.BIGDECIMAL),
    MISC_ADJUSTMENTS(6, "Misc Adjustments", ReportDataType.BIGDECIMAL),
    ESCORT_COUNT(7, "Escort Count", ReportDataType.INTEGER),
    ESCORT_COST(8, "Escort Cost", ReportDataType.BIGDECIMAL),
    CONTRACT_PRICE(9, "Contract Price", ReportDataType.BIGDECIMAL),
    EXTRA_KM_COST(10, "Extra Km Cost", ReportDataType.BIGDECIMAL),
    EXTRA_DUTY_COST(11, "Extra Duty Cost", ReportDataType.BIGDECIMAL),
    EXTRA_HOUR_COST(12, "Extra Hour Cost", ReportDataType.BIGDECIMAL),
    DRIVER_ALLOWANCE(13, "Driver Allowance", ReportDataType.BIGDECIMAL),
    AC_COST(14, "Ac Cost", ReportDataType.BIGDECIMAL),
    EXPENSE_COST(15, "Expense Cost", ReportDataType.BIGDECIMAL),
    GST(16, "GST", ReportDataType.BIGDECIMAL),
    GRAND_TOTAL(17, "Grand Total", ReportDataType.BIGDECIMAL);

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
              .filter(e -> e.getColumnLabel().equals(columnLabel))
              .findFirst()
              .orElse(null);
  }

    @Override
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
