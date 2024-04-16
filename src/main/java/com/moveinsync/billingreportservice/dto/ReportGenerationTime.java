package com.moveinsync.billingreportservice.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ReportGenerationTime(String reportType, Long generateTime) {

}
