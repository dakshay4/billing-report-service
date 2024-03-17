package com.moveinsync.billingreportservice.services;

import com.moveinsync.billingreportservice.Utils.DateFormatReader;
import com.moveinsync.billingreportservice.Utils.DateUtils;
import com.moveinsync.billingreportservice.clientservice.TripsheetDomainServiceImpl;
import com.moveinsync.billingreportservice.clientservice.VmsClientImpl;
import com.moveinsync.billingreportservice.dto.BillingReportRequestDTO;
import com.moveinsync.billingreportservice.dto.ReportDataDTO;
import com.moveinsync.billingreportservice.dto.VendorResponseDTO;
import com.moveinsync.billingreportservice.enums.VendorHeaders;
import com.moveinsync.tripsheetdomain.models.BillingCycleVO;
import com.moveinsync.tripsheetdomain.response.VendorBillingFrozenStatusDTO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class VendorReport<T extends Enum<T>> extends ReportBook<VendorHeaders> {

  private final VmsClientImpl vmsClient;
  private final TripsheetDomainServiceImpl tripsheetDomainService;

  public VendorReport(VmsClientImpl vmsClient, TripsheetDomainServiceImpl tripsheetDomainService) {
    this.vmsClient = vmsClient;
    this.tripsheetDomainService = tripsheetDomainService;
  }

  @Override
  public VendorHeaders[] getHeaders() {
    return VendorHeaders.values();
  }

  @Override
  public ReportDataDTO generateReport(BillingReportRequestDTO billingReportRequestDTO, ReportDataDTO reportDataDTO) {
    List<List<String>> table = reportDataDTO.getTable();
    table = filterIncomingTableHeadersAndData(table);
    int frozenRowIndex = VendorHeaders.FROZEN.getIndex();
    if(frozenRowIndex < getHeaderRow(table).size()) getHeaderRow(table).set(frozenRowIndex, VendorHeaders.FROZEN.getKey());
    for (int i = 1; i < table.size(); i++) {
      List<String> dataRows = table.get(i);
      String vendorName = dataRows.get(VendorHeaders.VENDOR.getIndex());
      VendorResponseDTO vendorResponseDTO = vmsClient.fetchVendorByVendorNameCached(vendorName);
      String vendorId = null;
      if (vendorResponseDTO != null)
        vendorId = vendorResponseDTO.getVendorId();
      DateFormatReader.readDateFormatFromAnnotation(BillingReportRequestDTO.class,
          BillingReportRequestDTO.Fields.cycleStart);
      Date start = DateUtils.convert(billingReportRequestDTO.getCycleStart(), new SimpleDateFormat(DateFormatReader
          .readDateFormatFromAnnotation(BillingReportRequestDTO.class, BillingReportRequestDTO.Fields.cycleStart)));
      Date end = DateUtils.convert(billingReportRequestDTO.getCycleStart(), new SimpleDateFormat(DateFormatReader
          .readDateFormatFromAnnotation(BillingReportRequestDTO.class, BillingReportRequestDTO.Fields.cycleEnd)));
      BillingCycleVO billingCycleVO = tripsheetDomainService.fetchBillingCycle(start, end);
      if(billingCycleVO!=null) {
        VendorBillingFrozenStatusDTO vendorBillingFrozenStatusDTO = tripsheetDomainService
                .findVendorBillingFrozenStatusById(billingCycleVO.getBillingCycleId(), Integer.parseInt(vendorId));
        dataRows.set(frozenRowIndex, billingCycleVO.getIsFrozen().toString());
      }
    }
    List<String> totalRow = totalRow(table);
    table.add(totalRow);
    reportDataDTO.setTable(table);
    return reportDataDTO;
  }
}
