package com.moveinsync.billingreportservice.controllers;

import com.moveinsync.billingreportservice.dto.BillingReportRequestDTO;
import com.moveinsync.billingreportservice.enums.BillingReportAggregatedTypes;
import com.moveinsync.billingreportservice.services.BillingReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/billingReports")
public class BillingReportController {

    @Autowired
    private BillingReportService billingReportService;

    @GetMapping("/data/{reportName}")
    public void reportdata(@PathVariable BillingReportAggregatedTypes reportName, @RequestBody BillingReportRequestDTO reportRequestDTO) {
        billingReportService.getData(reportName, reportRequestDTO);
    }
}
