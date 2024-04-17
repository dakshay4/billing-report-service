package com.moveinsync.billingreportservice.services;

import com.moveinsync.billingreportservice.clientservice.TripsheetDomainServiceImpl;
import com.moveinsync.billingreportservice.clientservice.VmsClientImpl;
import com.moveinsync.billingreportservice.dto.BillingReportRequestDTO;
import com.moveinsync.billingreportservice.dto.ReportDataDTO;
import com.moveinsync.billingreportservice.enums.VendorHeaders;

import java.util.List;

public class VendorReport<T extends Enum<T>> extends ReportBook<VendorHeaders> {

  public VendorReport(VmsClientImpl vmsClient, TripsheetDomainServiceImpl tripsheetDomainService) {
    super(vmsClient, tripsheetDomainService);
  }

  @Override
  public VendorHeaders[] getHeaders() {
    return VendorHeaders.values();
  }

  @Override
  public ReportDataDTO generateReport(BillingReportRequestDTO billingReportRequestDTO, ReportDataDTO reportDataDTO) {
    List<List<String>> table = reportDataDTO.getTable();
    table = filterIncomingTableHeadersAndData(table);
    addFrozenColumn(billingReportRequestDTO, table, VendorHeaders.FROZEN, VendorHeaders.VENDOR);
    List<String> totalRow = totalRow(table);
    table.add(totalRow);
    reportDataDTO.setTable(table);
    List<String> header = table.get(0);
      for (int i=0; i< header.size(); i++) {
          if (header.get(i).equals(VendorHeaders.VENDOR.getKey())) header.set(i, "Vendor Name");
      }
      if(billingReportRequestDTO.isOfficePresent()) replaceEntityIdByVendorName(table);
      return reportDataDTO;
  }


}
