package com.moveinsync.billingreportservice.controllers;

import com.moveinsync.billing.exception.UserDefinedException;
import com.moveinsync.billingreportservice.Configurations.UserContextResolver;
import com.moveinsync.billingreportservice.dto.BillingCycleDTO;
import com.moveinsync.billingreportservice.dto.BillingReportRequestDTO;
import com.moveinsync.billingreportservice.dto.FreezeBillingDTO;
import com.moveinsync.billingreportservice.dto.RegenerateBillDTO;
import com.moveinsync.billingreportservice.dto.ReportDataDTO;
import com.moveinsync.billingreportservice.dto.ReportGenerationTime;
import com.moveinsync.billingreportservice.enums.BillingReportAggregatedTypes;
import com.moveinsync.billingreportservice.exceptions.MisCustomException;
import com.moveinsync.billingreportservice.exceptions.MisError;
import com.moveinsync.billingreportservice.exceptions.ReportErrors;
import com.moveinsync.billingreportservice.services.BillingReportService;
import com.moveinsync.http.v2.MisHttpResponse;
import com.moveinsync.tripsheetdomain.models.BillingCycleVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

  private final Logger LOG = LoggerFactory.getLogger(getClass());
  @Autowired
  private BillingReportService billingReportService;
  @Autowired
  private MessageSource messageSource;

  @PostMapping("/data/{reportName}")
  public ReportDataDTO reportdata(@PathVariable BillingReportAggregatedTypes reportName,
      @RequestBody BillingReportRequestDTO reportRequestDTO) throws UserDefinedException {
    return billingReportService.getData(reportName, reportRequestDTO);
  }

  @GetMapping("/reportGenerationTime")
  public List<ReportGenerationTime> reportdata(
      @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
      @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate) {
    return billingReportService.getReportGenerationTime(startDate, endDate);
  }

  @GetMapping("/billing-cycles/all")
  public List<BillingCycleDTO> billingCyclesAll() {
    List<BillingCycleDTO> billingCycles = billingReportService.fetchAllBillingCycles();
    return billingCycles;
  }

  @PostMapping("/freeze-billing")
  public ResponseEntity<Boolean>  freezeBilling(
          @RequestBody FreezeBillingDTO freezeBillingDTO
  ) {
    boolean result = billingReportService.freezeBilling(freezeBillingDTO);
    return ResponseEntity.ok(result);
  }

  @PostMapping("/regenerate-billing")
  public ResponseEntity<String> regenerateBilling(
          @RequestBody RegenerateBillDTO regenerateBillDTO
  ) {
    String message = billingReportService.regenerateBilling(regenerateBillDTO);
    return ResponseEntity.ok(message);
  }

  @GetMapping("/exception")
  public Object exception() {
    throw new MisCustomException(ReportErrors.UNABLE_TO_FETCH_REPORTS);
    // return messageSource.getMessage("UNABLE_TO_FETCH_REPORTS", new Object[]{"Test"}, Locale.US);

  }
}
