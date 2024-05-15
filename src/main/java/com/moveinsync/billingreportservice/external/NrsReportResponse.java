package com.moveinsync.billingreportservice.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.moveinsync.billingreportservice.dto.ReportDataDTO;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NrsReportResponse {
  private String status;
  private int statusCode;
  private String message;
  private ReportDataDTO data;
}
