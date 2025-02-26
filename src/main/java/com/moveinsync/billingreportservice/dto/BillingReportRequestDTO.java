package com.moveinsync.billingreportservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.moveinsync.billingreportservice.constants.Constants;
import com.moveinsync.billingreportservice.enums.DateFormatPattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldNameConstants
@NoArgsConstructor
public class BillingReportRequestDTO {

  private String bunitId;
  @JsonFormat(pattern = "dd/MM/yyyy HH/mm/ss")
  private String cycleStart;
  @JsonFormat(pattern = "dd/MM/yyyy HH/mm/ss")
  private String cycleEnd;
  private String vendor;
  private String contract;
  private String entityId;
  private String office;
  private String date;

  public boolean isOfficePresent() {
    return this.getOffice()!=null && !this.office.isBlank();
  }

  public boolean isContractPresent() {
    return this.getContract() != null && !this.contract.isBlank();
  }
}
