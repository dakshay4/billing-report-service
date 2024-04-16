package com.moveinsync.billingreportservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BillingCycleDTO(
        int id,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy 00/00/00", timezone = "Asia/Kolkata") Date startDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy 00/00/00", timezone = "Asia/Kolkata") Date endDate,
        boolean frozen
) {


}
