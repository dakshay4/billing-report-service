package com.moveinsync.billingreportservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldNameConstants
public class BillingReportRequestDTO {

  private String bunitId;
  @JsonFormat(pattern = "dd/MM/yyyy HH/mm/ss")
  private String cycleStart;
  @JsonFormat(pattern = "dd/MM/yyyy HH/mm/ss")
  private String cycleEnd;
  private String vendor;
  private String contract;
  private String entityId;
  private String date;

}
