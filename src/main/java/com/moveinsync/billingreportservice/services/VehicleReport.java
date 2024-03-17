package com.moveinsync.billingreportservice.services;

import com.moveinsync.billingreportservice.dto.BillingReportRequestDTO;
import com.moveinsync.billingreportservice.dto.ReportDataDTO;
import com.moveinsync.billingreportservice.enums.OfficeHeaders;
import com.moveinsync.billingreportservice.enums.VehicleHeaders;

import java.util.List;

public class VehicleReport<T extends Enum<T>> extends ReportBook<VehicleHeaders>   {
    @Override
    public VehicleHeaders[] getHeaders() {
        return VehicleHeaders.values();
    }

/*  @Override
  public Class<VendorHeaders> getEnumClass() {
    return VendorHeaders.class;
  }*/

    @Override
    public ReportDataDTO generateReport(BillingReportRequestDTO billingReportRequestDTO, ReportDataDTO reportDataDTO) {
        List<List<String>> table = reportDataDTO.getTable();
        table = filterIncomingTableHeadersAndData(table);
        List<String> totalRow = totalRow(table);
        table.add(totalRow);
        reportDataDTO.setTable(table);
        return reportDataDTO;
    }
}