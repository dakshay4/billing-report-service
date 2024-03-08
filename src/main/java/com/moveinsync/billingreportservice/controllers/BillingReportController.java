package com.moveinsync.billingreportservice.controllers;

import com.moveinsync.billing.exception.UserDefinedException;
import com.moveinsync.billingreportservice.dto.BillingReportRequestDTO;
import com.moveinsync.billingreportservice.dto.ReportDataDTO;
import com.moveinsync.billingreportservice.enums.BillingReportAggregatedTypes;
import com.moveinsync.billingreportservice.exceptions.MisCustomException;
import com.moveinsync.billingreportservice.exceptions.MisError;
import com.moveinsync.billingreportservice.exceptions.ReportErrors;
import com.moveinsync.billingreportservice.services.BillingReportService;
import com.moveinsync.http.v2.MisHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;


@RestController
@RequestMapping("/billingReports/web")
public class BillingReportController {

    @Autowired
    private BillingReportService billingReportService;

    @Autowired
    private MessageSource messageSource;


    private final Logger LOG = LoggerFactory.getLogger(getClass());


    @PostMapping("/data/{reportName}")
    public MisHttpResponse<ReportDataDTO> reportdata(@PathVariable BillingReportAggregatedTypes reportName, @RequestBody BillingReportRequestDTO reportRequestDTO)
        throws UserDefinedException {
        return new MisHttpResponse(HttpStatus.OK.value(), billingReportService.getData(reportName, reportRequestDTO));
    }

    @GetMapping("/exception")
    public Object exception() {
        throw new MisCustomException(ReportErrors.UNABLE_TO_FETCH_REPORTS);
//        return messageSource.getMessage("UNABLE_TO_FETCH_REPORTS", new Object[]{"Test"}, Locale.US);

    }
}
