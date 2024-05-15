package com.moveinsync.billingreportservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;


@JsonIgnoreProperties(ignoreUnknown = true)
public record VendorFreezeBillingAuditDTO(String changedBy, Date timeStamp, String oldValue, String newValue,
                                          String vendorName) {
}
