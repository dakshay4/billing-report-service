package com.moveinsync.billingreportservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReportDataDTO {
  private List<List<String>> data;
}
