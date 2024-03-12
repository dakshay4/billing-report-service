package com.moveinsync.billingreportservice.services;

import com.google.common.collect.Lists;
import com.moveinsync.billing.exception.UserDefinedException;
import com.moveinsync.billing.model.BillingStatusVO;
import com.moveinsync.billing.model.ContractVO;
import com.moveinsync.billingreportservice.Configurations.UserContextResolver;
import com.moveinsync.billingreportservice.Utils.DateUtils;
import com.moveinsync.billingreportservice.Utils.NumberUtils;
import com.moveinsync.billingreportservice.clientservice.ContractWebClientImpl;
import com.moveinsync.billingreportservice.clientservice.ReportingService;
import com.moveinsync.billingreportservice.dto.BillingReportRequestDTO;
import com.moveinsync.billingreportservice.dto.ExternalReportRequestDTO;
import com.moveinsync.billingreportservice.dto.ReportDataDTO;
import com.moveinsync.billingreportservice.dto.ReportGenerationTime;
import com.moveinsync.billingreportservice.dto.VendorResponseDTO;
import com.moveinsync.billingreportservice.enums.BillingReportAggregatedTypes;
import com.moveinsync.billingreportservice.enums.ContractHeaders;
import com.moveinsync.billingreportservice.enums.ReportDataType;
import com.moveinsync.billingreportservice.exceptions.MisCustomException;
import com.moveinsync.billingreportservice.exceptions.ReportErrors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BillingReportService {
  private final WebClient vmsClient;
  private final ReportingService reportingService;
  private final ContractWebClientImpl contractWebClient;

  public BillingReportService(WebClient vmsClient, ReportingService reportingService, ContractWebClientImpl contractWebClient) {
    this.vmsClient = vmsClient;
    this.reportingService = reportingService;
    this.contractWebClient = contractWebClient;
  }

  public ReportDataDTO getData(BillingReportAggregatedTypes reportName, BillingReportRequestDTO reportRequestDTO){
    String empGuid = UserContextResolver.getCurrentContext().getEmpGuid();
    VendorResponseDTO vendorResponseDTO = vmsClient.get().uri("vendors/id/" + empGuid).retrieve()
        .bodyToMono(VendorResponseDTO.class).block();
    String vendorName = reportRequestDTO.getVendor() != null ?
        reportRequestDTO.getVendor() :
        (vendorResponseDTO != null ? vendorResponseDTO.getVendorName() : null);
    ExternalReportRequestDTO externalReportRequestDTO = prepareNRSRequest(reportRequestDTO, vendorName, reportName);
    ReportDataDTO reportDataDTO = reportingService.getReportFromNrs(externalReportRequestDTO);
      switch (reportName) {
      case VENDOR,VEHICLE,OFFICE,DUTY -> {
        return reportDataDTO;
       }
      case CONTRACT -> {
        List<List<String>> table = reportDataDTO.getTable();
        List<String> header = table.get(0);

        Set<String> headerLabels = Arrays.stream(ContractHeaders.values()).map(ContractHeaders::getColumnLabel).collect(Collectors.toSet());
        List<Integer> validIndices = new ArrayList<>();
        for(int i=0 ; i< header.size(); i++) if(headerLabels.contains(header.get(i))) validIndices.add(i);

        table = table.stream().map(row ->
                validIndices.stream().map(row::get).collect(Collectors.toList())
        ).collect(Collectors.toList());

        table = getContractReportFromNrsResponse(table);
        reportDataDTO.setTable(table);
        return reportDataDTO;
      }
      default -> {
        throw new MisCustomException(ReportErrors.INVALID_REPORT_TYPE);
      }
    }
  }

  private ExternalReportRequestDTO prepareNRSRequest(BillingReportRequestDTO reportRequestDTO, String vendorName,
      BillingReportAggregatedTypes reportName) {
    ExternalReportRequestDTO.ReportFilterDTO reportFilterDTO = new ExternalReportRequestDTO.ReportFilterDTO();
    reportFilterDTO.setContract(reportRequestDTO.getContract());
    if(vendorName != null) {
      reportFilterDTO.setVendor(Lists.newArrayList(vendorName));
    }
    reportFilterDTO.setEntityId(reportRequestDTO.getEntityId());
    if(reportRequestDTO.getVendor() != null) {
      reportFilterDTO.setParentEntity("VENDOR:" + reportRequestDTO.getVendor());
    }
    return ExternalReportRequestDTO.builder().reportFilter(reportFilterDTO).reportName(reportName.getReportName())
        .bunit(reportRequestDTO.getBunitId()).startDate(DateUtils.formatDate(reportRequestDTO.getCycleStart()))
        .endDate(DateUtils.formatDate(reportRequestDTO.getCycleEnd().toString())).build();
  }

  private List<List<String>> getContractReportFromNrsResponse(List<List<String>> table){
    List<String> header = table.get(0);
    int contractIdx = header.indexOf(ContractHeaders.CONTRACT.getColumnLabel());
    header.add(0, ContractHeaders.VEHICLE_TYPE.getColumnLabel());
    header.add(0 ,ContractHeaders.CAPACITY.getColumnLabel());
    for(int i=1;i<table.size();i++){
      String contractName = table.get(i).get(contractIdx);
      ContractVO contractVO = contractWebClient.getContract(contractName);
      table.get(i).add(0,contractVO.getCabType());
      table.get(i).add(0,contractVO.getSeatCapacity().toString());
    }
    sortDataBasedOnCapacity(table);
    int capacityIdx = header.indexOf(ContractHeaders.CAPACITY.getColumnLabel());
    Map<Integer, List<String>> capacityBasedSubTotal = new HashMap<>();
    for(int i=1;i<table.size();i++){
      List<String> rowData = table.get(i);
      Integer capacity = Integer.parseInt(rowData.get(capacityIdx));
      int requiredColumns = ContractHeaders.values().length;
      List<String> capacityWiseSubTotalRow = capacityBasedSubTotal.getOrDefault(capacity, new ArrayList<>(Collections.nCopies(requiredColumns, "")));
      int aggregationIndex = 3;
      for(int j = aggregationIndex; j<requiredColumns; j++) {
        String value = capacityWiseSubTotalRow.get(j);
        ContractHeaders contractHeader = ContractHeaders.getFromLabelName(header.get(j));
        ReportDataType dataType = contractHeader!=null ? contractHeader.getDataType() : ReportDataType.STRING;
        switch (dataType) {
          case BIGDECIMAL:
            rowData.set(j, String.valueOf(NumberUtils.roundOff(rowData.get(j))));
            BigDecimal subTotal = NumberUtils.roundOffAndAnd(value, rowData.get(j));
            value = String.valueOf(subTotal);
            break;
          case INTEGER :
            value = String.valueOf((value.isEmpty() ? 0 : Integer.parseInt(value)) + Integer.parseInt(rowData.get(j)));
        }
        capacityWiseSubTotalRow.set(j,value);
      }
      capacityBasedSubTotal.put(capacity, capacityWiseSubTotalRow);
    }
    Integer capacityBreakPoint = null;
    for(int i=1;i<table.size();i++){
      List<String> rowData = table.get(i);
      Integer capacity = Integer.parseInt(rowData.get(capacityIdx));
      if(capacityBreakPoint==null) capacityBreakPoint = capacity;
      if(capacityBreakPoint != capacity) {
        capacityBreakPoint = capacity;
        table.add(i,capacityBasedSubTotal.get(capacity));
      }
    }
    return table;
  }

  public static void sortDataBasedOnCapacity(List<List<String>> data) {
    // Get the header and remove it from the list
    List<String> header = data.remove(0);

    // Sort the data based on the "Capacity" column
    data.sort((row1, row2) -> {
        // Assuming "Capacity" is at index 0
        int capacity1 = Integer.parseInt(row1.get(0));
        int capacity2 = Integer.parseInt(row2.get(0));
        return Integer.compare(capacity1, capacity2);
    });

    // Add the header back to the sorted data
    data.add(0, header);
  }


  public List<ReportGenerationTime> getReportGenerationTime(LocalDate startDate, LocalDate endDate) {
    ReportGenerationTime reportTimeSiteBill = new ReportGenerationTime(BillingStatusVO.BillType.SITE_BILL.name(), new Date());
    ReportGenerationTime reportTimeVendorBill = new ReportGenerationTime(BillingStatusVO.BillType.VENDOR_BILL.name(), new Date());
    return List.of(reportTimeSiteBill, reportTimeVendorBill);
  }
}
