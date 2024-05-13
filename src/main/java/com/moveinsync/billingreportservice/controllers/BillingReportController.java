package com.moveinsync.billingreportservice.controllers;

import com.moveinsync.billingreportservice.dto.BillingCycleDTO;
import com.moveinsync.billingreportservice.dto.BillingReportRequestDTO;
import com.moveinsync.billingreportservice.dto.FreezeBillingDTO;
import com.moveinsync.billingreportservice.dto.FreezeBillingResponseDTO;
import com.moveinsync.billingreportservice.dto.RegenerateBillDTO;
import com.moveinsync.billingreportservice.dto.RegenerateBillResponseDTO;
import com.moveinsync.billingreportservice.dto.ReportDataDTO;
import com.moveinsync.billingreportservice.dto.ReportGenerationTime;
import com.moveinsync.billingreportservice.enums.BillingReportAggregatedTypes;
import com.moveinsync.billingreportservice.services.BillingReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/billingReports/web")
@RequiredArgsConstructor
public class BillingReportController {

  private final BillingReportService billingReportService;
  private final MessageSource messageSource;

  @PostMapping("/data/{reportName}")
  public ReportDataDTO reportdata(@PathVariable BillingReportAggregatedTypes reportName,
      @RequestBody BillingReportRequestDTO reportRequestDTO) {
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
    return billingReportService.fetchAllBillingCycles();
  }

  @PostMapping("/freeze-billing")
  public ResponseEntity<List<FreezeBillingResponseDTO>>  freezeBilling(
          @RequestBody FreezeBillingDTO freezeBillingDTO
  ) {
    List<FreezeBillingResponseDTO> result = billingReportService.freezeBilling(freezeBillingDTO);
    return ResponseEntity.ok(result);
  }

  @PostMapping("/regenerate-billing")
  public ResponseEntity<RegenerateBillResponseDTO> regenerateBilling(
          @RequestBody RegenerateBillDTO regenerateBillDTO
  ) {
    String message = billingReportService.regenerateBilling(regenerateBillDTO);
    RegenerateBillResponseDTO regenerateBillResponseDTO = new RegenerateBillResponseDTO(message);
    return ResponseEntity.ok(regenerateBillResponseDTO);
  }

  @GetMapping("/vendors/audits/all")
  public ResponseEntity getVendorBillingAudit(@RequestParam int billingCycleID)
          throws NumberFormatException {
    return ResponseEntity.ok(billingReportService.getVendorBillingAudit(billingCycleID));
  }

  @GetMapping("/all/cab-vendor")
  public ResponseEntity getAllCabs() {
    return ResponseEntity.ok(billingReportService.cabToVendorNameMap());
  }

  @GetMapping("/all/cab-vehicle")
  public ResponseEntity cabToVehicleNumberMap() {
    return ResponseEntity.ok(billingReportService.cabToVehicleNumberMap());
  }
}
