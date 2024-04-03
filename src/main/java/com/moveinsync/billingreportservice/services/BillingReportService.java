package com.moveinsync.billingreportservice.services;

import com.google.common.collect.Lists;
import com.mis.pc.utils.GsonUtils;
import com.mis.serverdata.exception.STWOperationNotAllowedException;
import com.moveinsync.billing.model.BillingStatusVO;
import com.moveinsync.billing.types.BillingCurrentStatus;
import com.moveinsync.billingreportservice.Configurations.UserContextResolver;
import com.moveinsync.billingreportservice.Utils.DateUtils;
import com.moveinsync.billingreportservice.clientservice.ContractWebClientImpl;
import com.moveinsync.billingreportservice.clientservice.ReportingService;
import com.moveinsync.billingreportservice.clientservice.TripsheetDomainServiceImpl;
import com.moveinsync.billingreportservice.clientservice.VmsClientImpl;
import com.moveinsync.billingreportservice.dto.BillingCycleDTO;
import com.moveinsync.billingreportservice.dto.BillingReportRequestDTO;
import com.moveinsync.billingreportservice.dto.ExternalReportRequestDTO;
import com.moveinsync.billingreportservice.dto.FreezeBillingDTO;
import com.moveinsync.billingreportservice.dto.ReportDataDTO;
import com.moveinsync.billingreportservice.dto.ReportGenerationTime;
import com.moveinsync.billingreportservice.dto.VendorResponseDTO;
import com.moveinsync.billingreportservice.enums.BillingReportAggregatedTypes;
import com.moveinsync.billingreportservice.enums.ContractHeaders;
import com.moveinsync.billingreportservice.exceptions.MisCustomException;
import com.moveinsync.billingreportservice.exceptions.ReportErrors;

import com.moveinsync.models.billing.BillingCycle;
import com.moveinsync.tripsheetdomain.client.TripsheetDomainWebClient;
import com.moveinsync.tripsheetdomain.models.BillingCycleVO;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BillingReportService {
  private final VmsClientImpl vmsClient;
  private final ReportingService reportingService;
  private final ContractWebClientImpl contractWebClient;
  private final TripsheetDomainServiceImpl tripsheetDomainClient;
  private final TripsheetDomainWebClient tripsheetDomainWebClient;

  private static final Logger logger = LoggerFactory.getLogger(BillingReportService.class);


  public BillingReportService(VmsClientImpl vmsClient, ReportingService reportingService,
                              ContractWebClientImpl contractWebClient, TripsheetDomainServiceImpl tripsheetDomainClient, TripsheetDomainWebClient tripsheetDomainWebClient) {
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
    VendorResponseDTO vendorResponseDTO = vmsClient.fetchVendorByEmpGuIdCached(empGuid);
    String vendorName = reportRequestDTO.getVendor() != null ? reportRequestDTO.getVendor()
        : (vendorResponseDTO != null ? vendorResponseDTO.getVendorName() : null);
    ExternalReportRequestDTO externalReportRequestDTO = prepareNRSRequest(reportRequestDTO, vendorName, reportName);
    ReportDataDTO reportDataDTO = reportingService.getReportFromNrs(externalReportRequestDTO);
    logger.info("Response from reportDataDTO {}", reportDataDTO);
    switch (reportName) {
    case VENDOR -> {
      ReportBook reportBook = new VendorReport(vmsClient, tripsheetDomainClient);
      reportDataDTO = reportBook.generateReport(reportRequestDTO, reportDataDTO);
      break;
    }

    case OFFICE -> {
      ReportBook reportBook = new OfficeReport(vmsClient, tripsheetDomainClient);
      reportDataDTO = reportBook.generateReport(reportRequestDTO, reportDataDTO);
      break;
    }

    case VEHICLE -> {
      ReportBook reportBook = new VehicleReport(vmsClient, tripsheetDomainClient);
      reportDataDTO = reportBook.generateReport(reportRequestDTO, reportDataDTO);
      break;
    }

    case DUTY -> {
      ReportBook reportBook = new DutyReport(vmsClient, tripsheetDomainClient);
      reportDataDTO = reportBook.generateReport(reportRequestDTO, reportDataDTO);
      break;
    }

    case CONTRACT -> {
      ReportBook reportBook = new ContractReport(vmsClient, tripsheetDomainClient, contractWebClient);
      reportDataDTO = reportBook.generateReport(reportRequestDTO, reportDataDTO);
      break;
    }
    default -> throw new MisCustomException(ReportErrors.INVALID_REPORT_TYPE);
    }
    return reportDataDTO;
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

  public void freezeBilling(FreezeBillingDTO freezeBillingDTO) {
    int vendorId = freezeBillingDTO.vendorId();
    boolean freezeStatus = freezeBillingDTO.frozen();
    Date startDate = freezeBillingDTO.startDate();
    Date endDate = freezeBillingDTO.endDate();

    if (
//            !unfreezePermission &&
            !freezeStatus) {
      throw new MisCustomException(ReportErrors.OPERATION_NOT_ALLOWED);
    }

    if (!freezeStatus) {
      List<BillingCycleDTO> billingCycles = fetchAllBillingCycles();
      BillingCycleDTO cycle = billingCycles.stream().filter(e-> e.startDate().equals(startDate)).findFirst().orElse(null);
//      cycle.freezeVendorBilling(vendorId, freezeStatus);
//      cycle.unfreezeBillingCycle();
//      vendorBillingManagementService.updateVendorBillingFreezeStatus(vendorId, BillingCycle
//              .getBillingCycleForDate(startDate).getId(), freezeStatus);
//      sendData(response, true);
      return;
    }



  }
}
