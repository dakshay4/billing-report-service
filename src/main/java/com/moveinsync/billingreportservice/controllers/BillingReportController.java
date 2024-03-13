package com.moveinsync.billingreportservice.controllers;

import com.moveinsync.billing.exception.UserDefinedException;
import com.moveinsync.billing.model.BillingCycleVO;
import com.moveinsync.billingreportservice.Configurations.UserContextResolver;
import com.moveinsync.billingreportservice.dto.BillingReportRequestDTO;
import com.moveinsync.billingreportservice.dto.ReportDataDTO;
import com.moveinsync.billingreportservice.dto.ReportGenerationTime;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


@RestController
@RequestMapping("/billingReports/web")
public class BillingReportController {

    @Autowired
    private BillingReportService billingReportService;

    @Autowired
    private MessageSource messageSource;


    private final Logger LOG = LoggerFactory.getLogger(getClass());


    @PostMapping("/data/{reportName}")
    public ReportDataDTO reportdata(@PathVariable BillingReportAggregatedTypes reportName, @RequestBody BillingReportRequestDTO reportRequestDTO)
        throws UserDefinedException {
        return billingReportService.getData(reportName, reportRequestDTO);
    }

    @GetMapping("/reportGenerationTime")
    public List<ReportGenerationTime> reportdata(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        return billingReportService.getReportGenerationTime(startDate, endDate);
    }

    @GetMapping("/billing-cycles/all")
    public List<BillingCycleVO> billingCyclesAll() {
        List<BillingCycleVO> dummy = new ArrayList<>();
        for(int i=12;i>1;i--) {
            dummy.add(new BillingCycleVO(UUID.randomUUID(), UserContextResolver.getCurrentContext().getBuid(),
                    Date.from(LocalDateTime.of(2023, i, 01, 0, 0).toInstant(ZoneOffset.UTC)),
                    Date.from(LocalDateTime.of(2023, i, 01, 0, 0).toInstant(ZoneOffset.UTC))));

        }
        for(int i=12;i>1;i--) {
            dummy.add(new BillingCycleVO(UUID.randomUUID(), UserContextResolver.getCurrentContext().getBuid(),
                    Date.from(LocalDateTime.of(2024, i, 01, 0, 0).toInstant(ZoneOffset.UTC)),
                    Date.from(LocalDateTime.of(2024, i, 01, 0, 0).toInstant(ZoneOffset.UTC))));

        }
        return dummy;
    }

    @GetMapping("/exception")
    public Object exception() {
        throw new MisCustomException(ReportErrors.UNABLE_TO_FETCH_REPORTS);
//        return messageSource.getMessage("UNABLE_TO_FETCH_REPORTS", new Object[]{"Test"}, Locale.US);

    }
}
