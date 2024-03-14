package com.moveinsync.billingreportservice.services;

import com.google.common.collect.Lists;
import com.moveinsync.billing.model.BillingStatusVO;
import com.moveinsync.billing.model.ContractVO;
import com.moveinsync.billing.types.BillingCurrentStatus;
import com.moveinsync.billingreportservice.BillingreportserviceApplication;
import com.moveinsync.billingreportservice.Configurations.UserContextResolver;
import com.moveinsync.billingreportservice.Utils.DateUtils;
import com.moveinsync.billingreportservice.Utils.NumberUtils;
import com.moveinsync.billingreportservice.clientservice.ContractWebClientImpl;
import com.moveinsync.billingreportservice.clientservice.ReportingService;
import com.moveinsync.billingreportservice.clientservice.BillingCycleServiceImpl;
import com.moveinsync.billingreportservice.dto.BillingCycleDTO;
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

import com.moveinsync.tripsheetdomain.client.TripsheetDomainWebClient;
import com.moveinsync.tripsheetdomain.models.BillingCycleVO;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Component
public class BillingReportService {
  private final WebClient vmsClient;
  private final ReportingService reportingService;
  private final ContractWebClientImpl contractWebClient;
  private final BillingCycleServiceImpl tripsheetDomainClient;
  private final TripsheetDomainWebClient tripsheetDomainWebClient;

  private static final Logger logger = LoggerFactory.getLogger(BillingReportService.class);


  public BillingReportService(WebClient vmsClient, ReportingService reportingService,
                              ContractWebClientImpl contractWebClient, BillingCycleServiceImpl tripsheetDomainClient, TripsheetDomainWebClient tripsheetDomainWebClient) {
    this.vmsClient = vmsClient;
    this.reportingService = reportingService;
    this.contractWebClient = contractWebClient;
    this.tripsheetDomainClient = tripsheetDomainClient;
      this.tripsheetDomainWebClient = tripsheetDomainWebClient;
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

  public ReportDataDTO getData(BillingReportAggregatedTypes reportName, BillingReportRequestDTO reportRequestDTO) {
    String empGuid = UserContextResolver.getCurrentContext().getEmpGuid();
    VendorResponseDTO vendorResponseDTO = vmsClient.get().uri("vendors/id/" + empGuid).retrieve()
        .bodyToMono(VendorResponseDTO.class).block();
    String vendorName = reportRequestDTO.getVendor() != null ? reportRequestDTO.getVendor()
        : (vendorResponseDTO != null ? vendorResponseDTO.getVendorName() : null);
    ExternalReportRequestDTO externalReportRequestDTO = prepareNRSRequest(reportRequestDTO, vendorName, reportName);
    ReportDataDTO reportDataDTO = reportingService.getReportFromNrs(externalReportRequestDTO);
    logger.info("Response from reportDataDTO {}", reportDataDTO);
    switch (reportName) {
    case VENDOR -> {
      ReportBook reportBook = new VendorReport();
      reportDataDTO = reportBook.generateReport(reportDataDTO);
      break;
    }

    case OFFICE -> {
      ReportBook reportBook = new OfficeReport();
      reportDataDTO = reportBook.generateReport(reportDataDTO);
      break;
    }

    case VEHICLE -> {
      ReportBook reportBook = new VehicleReport();
      reportDataDTO = reportBook.generateReport(reportDataDTO);
      break;
    }

    case DUTY -> {
      ReportBook reportBook = new DutyReport();
      reportDataDTO = reportBook.generateReport(reportDataDTO);
      break;
    }

    case CONTRACT -> {
      List<List<String>> table = reportDataDTO.getTable();
      table = filterIncomingTableHeadersAndData(table);
      List<String> totalRow = totalRow(table);
      table = getContractReportFromNrsResponse(table);
      table.add(totalRow);
      reportDataDTO
              .setTable(table);
      break;
    }
    default -> throw new MisCustomException(ReportErrors.INVALID_REPORT_TYPE);
    }
    return reportDataDTO;
  }

  @NotNull
  private static List<List<String>> filterIncomingTableHeadersAndData(List<List<String>> table) {
    List<String> header = table.get(0);
    Set<String> headerLabels = Arrays.stream(ContractHeaders.values()).map(ContractHeaders::getColumnLabel).collect(
            Collectors.toSet());
    List<Integer> validIndices = new ArrayList<>();
    for (int i = 0; i < header.size(); i++)
      if (headerLabels.contains(header.get(i)))
        validIndices.add(i);

    table = table.stream().map(row -> validIndices.stream().map(row::get).collect(Collectors.toList()))
            .collect(Collectors.toList());
    return table;
  }

  private ExternalReportRequestDTO prepareNRSRequest(BillingReportRequestDTO reportRequestDTO, String vendorName,
      BillingReportAggregatedTypes reportName) {
    ExternalReportRequestDTO.ReportFilterDTO reportFilterDTO = new ExternalReportRequestDTO.ReportFilterDTO();
    reportFilterDTO.setContract(reportRequestDTO.getContract());
    if (vendorName != null) {
      reportFilterDTO.setVendor(Lists.newArrayList(vendorName));
    }
    reportFilterDTO.setEntityId(reportRequestDTO.getEntityId());
    if (reportRequestDTO.getVendor() != null) {
      reportFilterDTO.setParentEntity("VENDOR:" + reportRequestDTO.getVendor());
    }
    return ExternalReportRequestDTO.builder().reportFilter(reportFilterDTO).reportName(reportName.getReportName())
        .bunit(reportRequestDTO.getBunitId()).startDate(DateUtils.formatDate(reportRequestDTO.getCycleStart()))
        .endDate(DateUtils.formatDate(reportRequestDTO.getCycleEnd().toString())).build();
  }

  private List<List<String>> getContractReportFromNrsResponse(List<List<String>> table) {
    List<String> header = table.get(0);
    int contractIdx = header.indexOf(ContractHeaders.CONTRACT.getColumnLabel());
    header.add(0, ContractHeaders.VEHICLE_TYPE.getColumnLabel());
    header.add(0, ContractHeaders.CAPACITY.getColumnLabel());
    for (int i = 1; i < table.size(); i++) {
      String contractName = table.get(i).get(contractIdx);
      ContractVO contractVO = contractWebClient.getContract(contractName);
      table.get(i).add(0, contractVO.getCabType());
      table.get(i).add(0, contractVO.getSeatCapacity().toString());
    }
    sortDataBasedOnCapacity(table);
    int capacityIdx = header.indexOf(ContractHeaders.CAPACITY.getColumnLabel());
    Map<Integer, List<String>> capacityBasedSubTotal = new HashMap<>();
    for (int i = 1; i < table.size(); i++) {
      List<String> rowData = table.get(i);
      Integer capacity = Integer.parseInt(rowData.get(capacityIdx));
      int requiredColumns = ContractHeaders.values().length;
      List<String> capacityWiseSubTotalRow = capacityBasedSubTotal.getOrDefault(capacity,
          new ArrayList<>(Collections.nCopies(requiredColumns, "")));
      // int aggregationIndex = 3;
      for (int j = 0; j < requiredColumns; j++) {
        String value = capacityWiseSubTotalRow.get(j);
        ContractHeaders contractHeader = ContractHeaders.getFromLabelName(header.get(j));
        ReportDataType dataType = contractHeader != null ? contractHeader.getDataType() : ReportDataType.STRING;
        switch (dataType) {
        case BIGDECIMAL:
          rowData.set(j, String.valueOf(NumberUtils.roundOff(rowData.get(j))));
          BigDecimal subTotal = NumberUtils.roundOffAndAnd(value, rowData.get(j));
          value = String.valueOf(subTotal);
          break;
        case INTEGER:
          value = String.valueOf((value.isEmpty() ? 0 : Integer.parseInt(value)) + Integer.parseInt(rowData.get(j)));
        }
        capacityWiseSubTotalRow.set(j, value);
      }
      capacityBasedSubTotal.put(capacity, capacityWiseSubTotalRow);
    }
    Integer capacityBreakPoint = null;
    Map<Integer, List<String>> indexWiseSubTotalRowPlacement = new TreeMap<>(Comparator.reverseOrder());
    for (int i = 1; i < table.size(); i++) {
      List<String> rowData = table.get(i);
      Integer capacity = Integer.parseInt(rowData.get(capacityIdx));
      if (capacityBreakPoint == null)
        capacityBreakPoint = capacity;
      if (capacityBreakPoint != capacity) {
        indexWiseSubTotalRowPlacement.put(i, capacityBasedSubTotal.get(capacityBreakPoint));
        capacityBreakPoint = capacity;
      }
    }
    indexWiseSubTotalRowPlacement.put(table.size(), capacityBasedSubTotal.get(capacityBreakPoint));
    indexWiseSubTotalRowPlacement.forEach((index, row) -> {
      table.add(index, row);
    });
    return table;
  }

  public List<String> totalRow(List<List<String>> table) {
    if (table == null || table.isEmpty())
      return new ArrayList<>();
    List<String> header = table.get(0);
    int requiredColumns = header.size();
    List<String> totalRow = new ArrayList<>(Collections.nCopies(requiredColumns, ""));
    for (int i = 1; i < table.size(); i++) {
      List<String> rowData = table.get(i);
      for (int j = 0; j < requiredColumns; j++) {
        String value = totalRow.get(j);
        ContractHeaders contractHeader = ContractHeaders.getFromLabelName(header.get(j));
        ReportDataType dataType = contractHeader != null ? contractHeader.getDataType() : ReportDataType.STRING;
        switch (dataType) {
        case BIGDECIMAL:
          rowData.set(j, String.valueOf(NumberUtils.roundOff(rowData.get(j))));
          BigDecimal subTotal = NumberUtils.roundOffAndAnd(value, rowData.get(j));
          value = String.valueOf(subTotal);
          break;
        case INTEGER:
          value = String.valueOf((value.isEmpty() ? 0 : Integer.parseInt(value)) + Integer.parseInt(rowData.get(j)));
        }
        totalRow.set(j, value);
      }
    }
    return totalRow;
  }

  public List<ReportGenerationTime> getReportGenerationTime(LocalDate startDate, LocalDate endDate) {
    List<BillingStatusVO> billingStatuses = contractWebClient.getBillingStatus(startDate, endDate);
    List<ReportGenerationTime> reportGenerationTimes = new ArrayList<>();
    for (BillingStatusVO billingStatus : billingStatuses) {
      if (billingStatus == null)
        continue;
      if (BillingCurrentStatus.GENERATED.equals(billingStatus.getBillingCurrentStatus()))
        reportGenerationTimes
            .add(new ReportGenerationTime(billingStatus.getBillingType(), billingStatus.getUpdatedDate()));
    }
    return reportGenerationTimes;
  }

  public List<BillingCycleDTO> fetchAllBillingCycles() {
    List<BillingCycleVO> cycleVOS = tripsheetDomainClient.fetchBillingCyclesCached();
    return cycleVOS.parallelStream().map(cycleVO -> convertToDTO(cycleVO)).collect(Collectors.toList());
  }

  private BillingCycleDTO convertToDTO(BillingCycleVO cycleVO) {
    return new BillingCycleDTO(cycleVO.getBillingCycleId(), cycleVO.getStartDate(), cycleVO.getEndDate(),
        cycleVO.getIsFrozen());
  }
}
