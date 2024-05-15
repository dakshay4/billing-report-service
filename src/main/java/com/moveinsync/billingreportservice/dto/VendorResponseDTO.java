package com.moveinsync.billingreportservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VendorResponseDTO {

  private Integer id;
  private String vendorKey;
  private String vendorId;
  private String vendorName;
  private String pointOfContact;
  private String emailId;
  private String address;
  private String phoneNumber;
  private String businessUnitId;
  private boolean status;

}
