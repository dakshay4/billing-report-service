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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
      Map<String, List<String>> vendorAggregation = groupReportByVendorName(table);
      /* Reset Office -> Vendor = Drill column with group by result based on Vendor Name */
      List<String> headers = getHeaderRow(table);
      table = new ArrayList<>(); table.add(headers);
      table.addAll(
              vendorAggregation.values().parallelStream().toList()
      );
    } else {
      // For Simple VENDOR Report, Vendor is NA, and Entity Id is Vendor Name, so We need to delete vendor column and rename header
      removeColumn(table,0);
    }
    sortList(table, 1, VendorHeaders.VENDOR.getIndex());
    List<String> totalRow = totalRow(table);
    table.add(totalRow);
    reportDataDTO.setTable(table);
    return reportDataDTO;
  }

  public static void removeColumn(List<List<String>> table, int columnIndex) {
    for (List<String> row : table) {
      row.remove(columnIndex);
    }
  }

  public Map<String, List<String>> groupReportByVendorName(List<List<String>> table) {
    Map<String, List<String>> vendorNameAggregation = new TreeMap<>(String::compareTo);
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
