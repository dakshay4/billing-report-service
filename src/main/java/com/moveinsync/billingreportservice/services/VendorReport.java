package com.moveinsync.billingreportservice.services;

import com.moveinsync.billingreportservice.dto.ReportDataDTO;
import com.moveinsync.billingreportservice.enums.VendorHeaders;

import java.util.List;

public class VendorReport<T extends Enum<T>> extends ReportBook<VendorHeaders>  {


  @Override
  public VendorHeaders[] getHeaders() {
    return VendorHeaders.values();
  }

/*  @Override
  public VendorHeaders getEnumClass() {
    return VendorHeaders;
  }*/

  @Override
  public ReportDataDTO generateReport(ReportDataDTO reportDataDTO) {
    List<List<String>> table = reportDataDTO.getTable();
    table = filterIncomingTableHeadersAndData(table);
    List<String> totalRow = totalRow(table);
    table.add(totalRow);
    reportDataDTO.setTable(table);
    return reportDataDTO;
  }
}
