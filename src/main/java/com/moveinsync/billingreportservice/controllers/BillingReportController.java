package com.moveinsync.billingreportservice.controllers;

import com.moveinsync.billing.exception.UserDefinedException;
import com.moveinsync.billingreportservice.Configurations.UserContextResolver;
import com.moveinsync.billingreportservice.dto.BillingReportRequestDTO;
import com.moveinsync.billingreportservice.dto.ReportDataDTO;
import com.moveinsync.billingreportservice.enums.BillingReportAggregatedTypes;
import com.moveinsync.billingreportservice.services.BillingReportService;
import com.moveinsync.tripsheetdomain.response.EmployeeAdditionalAttributeLong;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/billingReports/web")
public class BillingReportController {

    @Autowired
    private BillingReportService billingReportService;

    private final Logger LOG = LoggerFactory.getLogger(getClass());


    @GetMapping("/data/{reportName}")
    public ResponseEntity<ReportDataDTO> reportdata(@PathVariable BillingReportAggregatedTypes reportName, @RequestBody BillingReportRequestDTO reportRequestDTO)
        throws UserDefinedException {
        return ResponseEntity.ok(billingReportService.getData(reportName, reportRequestDTO));
    }
}
