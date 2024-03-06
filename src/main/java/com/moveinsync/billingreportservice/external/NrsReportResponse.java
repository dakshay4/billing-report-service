package com.moveinsync.billingreportservice.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties
public class NrsReportResponse {
  private String status;
  private int statusCode;
  private String message;
  private String desc;
  private List<List<String>> table;
}
