package com.moveinsync.billingreportservice.services;

import com.google.common.collect.Lists;
import com.moveinsync.billing.exception.UserDefinedException;
import com.moveinsync.billingreportservice.Configurations.UserContextResolver;
import com.moveinsync.billingreportservice.clientservice.ReportingService;
import com.moveinsync.billingreportservice.dto.BillingReportRequestDTO;
import com.moveinsync.billingreportservice.dto.ExternalReportRequestDTO;
import com.moveinsync.billingreportservice.dto.ReportDataDTO;
import com.moveinsync.billingreportservice.dto.VendorResponseDTO;
import com.moveinsync.billingreportservice.enums.BillingReportAggregatedTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class BillingReportService {
  private final WebClient vmsClient;
  private final ReportingService reportingService;
  private final WebClient contractClient;

  public BillingReportService(WebClient vmsClient, ReportingService reportingService, WebClient contractClient) {
    this.vmsClient = vmsClient;
    this.reportingService = reportingService;
    this.contractClient = contractClient;
  }

  public ReportDataDTO getData(BillingReportAggregatedTypes reportName, BillingReportRequestDTO reportRequestDTO)
      throws UserDefinedException {
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
        return getContractReportFromNrsResponse(reportDataDTO);
      }
      default -> {
        throw new UserDefinedException("Invalid Report Type");
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
        .bunit(reportRequestDTO.getBunitId()).startDate(reportRequestDTO.getCycleStart())
        .endDate(reportRequestDTO.getCycleEnd()).build();
  }

  private ReportDataDTO getContractReportFromNrsResponse(ReportDataDTO reportDataDTO){

    return null;
  }


}
