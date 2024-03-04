package com.moveinsync.billingreportservice.external;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NrsReportRequest {

    private String reportName;
    private String startDate;
    private String endDate;
    private String bunit;

    private ReportFilter reportFilter;
}
