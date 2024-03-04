package com.moveinsync.billingreportservice.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalReportRequestDTO {

    private ReportFilterDTO reportFilter;
    private String reportName;
    private String startDate;
    private String endDate;
    private String bunit;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class ReportFilterDTO {
        private List<String> vendor;
        private String contract;
        private String entityId;
        private String parentEntity;
    }
}
