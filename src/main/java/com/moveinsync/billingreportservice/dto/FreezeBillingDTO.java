package com.moveinsync.billingreportservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public record FreezeBillingDTO(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy 00/00/00", timezone = "Asia/Kolkata") Date startDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy 00/00/00", timezone = "Asia/Kolkata") Date endDate,
        boolean frozen,
        Integer vendorId){
}
