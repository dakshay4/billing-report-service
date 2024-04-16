package com.moveinsync.billingreportservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.moveinsync.billingreportservice.enums.BillingEntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class ExternalReportRequestDTO {

  private ReportFilterDTO reportFilter;
  private String reportName;
  private String startDate;
  private String endDate;
  private String bunit;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class ReportFilterDTO {
    private List<String> vendor;
    private List<String> office;
    private String contract;
    private String entityId;
    private BillingEntityType entity;
    private String parentEntity;
  }
}
