package com.moveinsync.billingreportservice.services;

import com.moveinsync.billingreportservice.dto.ReportDataDTO;
import com.moveinsync.billingreportservice.enums.OfficeHeaders;
import com.moveinsync.billingreportservice.enums.VendorHeaders;

import java.util.List;

public class OfficeReport<T extends Enum<T>> extends ReportBook<OfficeHeaders>   {
    @Override
    public OfficeHeaders[] getHeaders() {
        return OfficeHeaders.values();
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