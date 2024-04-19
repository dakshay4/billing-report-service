package com.moveinsync.billingreportservice.services;

import com.moveinsync.billingreportservice.Utils.NumberUtils;
import com.moveinsync.billingreportservice.clientservice.TripsheetDomainServiceImpl;
import com.moveinsync.billingreportservice.clientservice.VmsClientImpl;
import com.moveinsync.billingreportservice.dto.BillingReportRequestDTO;
import com.moveinsync.billingreportservice.dto.ReportDataDTO;
import com.moveinsync.billingreportservice.enums.ReportDataType;
import com.moveinsync.billingreportservice.enums.VendorHeaders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    if(billingReportRequestDTO.isOfficePresent()) {
      replaceEntityIdByVendorName(table, VendorHeaders.VENDOR.getIndex());
      Map<String, List<String>> vendorAggregation = groupReportByVendorName(table);
      List<String> headers = getHeaderRow(table);
      table = new ArrayList<>(); table.add(headers);
      for(Map.Entry aggregation : vendorAggregation.entrySet()) {
        List<String> aggregationRow = (List<String>)aggregation.getValue();
        table.add(aggregationRow);
      }
    }

    List<String> totalRow = totalRow(table);
    table.add(totalRow);
    reportDataDTO.setTable(table);
    List<String> header = table.get(0);
    for (int i=0; i< header.size(); i++) {
        if (header.get(i).equals(VendorHeaders.VENDOR.getLabel())) header.set(i, "Vendor Name");
    }

    return reportDataDTO;
  }

  public Map<String, List<String>> groupReportByVendorName(List<List<String>> table) {
    Map<String, List<String>> vendorNameAggregation = new HashMap<>();
    List<String> headerRow = getHeaderRow(table);
    for (int i = 1; i < table.size(); i++) {
      List<String> rowData = table.get(i);
      String vendorName = rowData.get(VendorHeaders.VENDOR.getIndex());
      int requiredColumns = VendorHeaders.values().length;
      List<String> vendorWiseSubTotalRow = vendorNameAggregation.getOrDefault(vendorName,
              new ArrayList<>(Collections.nCopies(requiredColumns, "")));
      for (int j = 0; j < requiredColumns; j++) {
        String value = vendorWiseSubTotalRow.get(j);
        VendorHeaders vendorHeaders = VendorHeaders.getFromLabelName(headerRow.get(j));
        ReportDataType dataType = vendorHeaders != null ? vendorHeaders.getDataType() : ReportDataType.STRING;
        switch (dataType) {
          case BIGDECIMAL:
            rowData.set(j, String.valueOf(NumberUtils.roundOff(rowData.get(j))));
            BigDecimal subTotal = NumberUtils.roundOffAndAnd(value, rowData.get(j));
            value = String.valueOf(subTotal);
            break;
          case INTEGER:
            value = String.valueOf((value.isBlank() ? 0 : Integer.parseInt(value)) + NumberUtils.parseInteger(rowData.get(j)));
            break;
          case STRING:
            value = rowData.get(j);
        }
        vendorWiseSubTotalRow.set(j, value);
      }
      vendorNameAggregation.put(vendorName, vendorWiseSubTotalRow);
    }
    return vendorNameAggregation;
  }

}
