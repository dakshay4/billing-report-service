package com.moveinsync.billingreportservice.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BillingReportRequestDTO {

    private String bunitId;
    private String cycleStart;
    private String cycleEnd;
    private String vendor;
    private String contract;
    private String entityId;
    private String date;

}
