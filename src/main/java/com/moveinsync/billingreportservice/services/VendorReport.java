package com.moveinsync.billingreportservice.services;

import com.moveinsync.billingreportservice.dto.ReportDataDTO;
import com.moveinsync.billingreportservice.enums.BillingReportAggregatedTypes;
import com.moveinsync.billingreportservice.enums.TableHeaders;
import com.moveinsync.billingreportservice.enums.VendorHeaders;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

import java.util.List;

public class VendorReport<T extends Enum<T>> extends ReportBook<VendorHeaders>  {


  @Override
  public VendorHeaders[] getHeaders() {
    return VendorHeaders.values();
  }

/*  @Override
  public Class<VendorHeaders> getEnumClass() {
    return VendorHeaders.class;
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
