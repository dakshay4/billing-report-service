package com.moveinsync.billingreportservice.clientservice;

import com.mis.serverdata.utils.GsonUtils;
import com.moveinsync.billingreportservice.constants.BeanConstants;
import com.moveinsync.billingreportservice.dto.ExternalReportRequestDTO;
import com.moveinsync.billingreportservice.dto.ReportDataDTO;
import com.moveinsync.billingreportservice.external.NrsReportRequest;
import com.moveinsync.billingreportservice.external.NrsReportResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ReportingService {
  private final WebClient reportingServiceClient;

  public ReportingService(@Qualifier(BeanConstants.REPORTING_SERVICE_CLIENT) WebClient reportingServiceClient) {
    this.reportingServiceClient = reportingServiceClient;
  }
  public ReportDataDTO getReportFromNrs(ExternalReportRequestDTO nrsReportRequest) {
    NrsReportResponse reportResponse = reportingServiceClient.post().uri("/billing-reports")
        .contentType(MediaType.APPLICATION_JSON).bodyValue(nrsReportRequest).accept(MediaType.APPLICATION_JSON).retrieve()
        .bodyToMono(NrsReportResponse.class).block();
    return ReportDataDTO.builder().table(reportResponse.getData().getTable()).build();
  }
}
