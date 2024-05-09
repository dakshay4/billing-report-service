package com.moveinsync.billingreportservice.services;

import com.google.common.collect.Lists;
import com.moveinsync.billing.model.BillingStatusVO;
import com.moveinsync.billing.types.BillingCurrentStatus;
import com.moveinsync.billingreportservice.Configurations.UserContextResolver;
import com.moveinsync.billingreportservice.Utils.DateUtils;
import com.moveinsync.billingreportservice.clientservice.BillingCalculationClientImpl;
import com.moveinsync.billingreportservice.clientservice.ContractWebClientImpl;
import com.moveinsync.billingreportservice.clientservice.ReportingService;
import com.moveinsync.billingreportservice.clientservice.TripsheetDomainServiceImpl;
import com.moveinsync.billingreportservice.clientservice.VmsClientImpl;
import com.moveinsync.billingreportservice.constants.Constants;
import com.moveinsync.billingreportservice.dto.BillingCycleDTO;
import com.moveinsync.billingreportservice.dto.BillingReportRequestDTO;
import com.moveinsync.billingreportservice.dto.ExternalReportRequestDTO;
import com.moveinsync.billingreportservice.dto.FreezeBillingDTO;
import com.moveinsync.billingreportservice.dto.FreezeBillingResponseDTO;
import com.moveinsync.billingreportservice.dto.RegenerateBillDTO;
import com.moveinsync.billingreportservice.dto.ReportDataDTO;
import com.moveinsync.billingreportservice.dto.ReportGenerationTime;
import com.moveinsync.billingreportservice.dto.VendorFreezeBillingAuditDTO;
import com.moveinsync.billingreportservice.dto.VendorResponseDTO;
import com.moveinsync.billingreportservice.enums.BillingEntityType;
import com.moveinsync.billingreportservice.enums.BillingReportAggregatedTypes;
import com.moveinsync.billingreportservice.enums.ContractHeaders;
import com.moveinsync.billingreportservice.enums.DateHeaders;
import com.moveinsync.billingreportservice.enums.DutyHeaders;
import com.moveinsync.billingreportservice.enums.OfficeHeaders;
import com.moveinsync.billingreportservice.enums.VehicleHeaders;
import com.moveinsync.billingreportservice.enums.VendorHeaders;
import com.moveinsync.billingreportservice.exceptions.MisCustomException;
import com.moveinsync.billingreportservice.exceptions.ReportErrors;

import com.moveinsync.data.envers.models.AuditType;
import com.moveinsync.data.envers.models.EntityAuditDetails;
import com.moveinsync.tripsheetdomain.models.BillingCycleVO;
import com.moveinsync.tripsheetdomain.models.VendorResponse;
import com.moveinsync.tripsheetdomain.response.VendorBillingFrozenStatusDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BillingReportService {
    private static final Logger logger = LoggerFactory.getLogger(BillingReportService.class);
    private final VmsClientImpl vmsClient;
    private final ReportingService reportingService;
    private final ContractWebClientImpl contractWebClient;
    private final TripsheetDomainServiceImpl tripsheetDomainClient;
    private final BillingCalculationClientImpl billingCalculationClient;

    public BillingReportService(VmsClientImpl vmsClient,
                                ReportingService reportingService,
                                ContractWebClientImpl contractWebClient,
                                TripsheetDomainServiceImpl tripsheetDomainClient,
                                BillingCalculationClientImpl billingCalculationClient) {
        this.vmsClient = vmsClient;
        this.reportingService = reportingService;
        this.contractWebClient = contractWebClient;
        this.tripsheetDomainClient = tripsheetDomainClient;
        this.billingCalculationClient = billingCalculationClient;
    }

    public static void sortDataBasedOnCapacity(List<List<String>> data) {
        // Get the header and remove it from the list
        List<String> header = data.remove(0);

        // Sort the data based on the "Capacity" column
        data.sort((row1, row2) -> {
            // Assuming "Capacity" is at index 0
            int capacity1 = Integer.parseInt(row1.get(0));
            int capacity2 = Integer.parseInt(row2.get(0));
            return Integer.compare(capacity1, capacity2);
        });

        // Add the header back to the sorted data
        data.add(0, header);
    }

    public ReportDataDTO getData(BillingReportAggregatedTypes reportName, BillingReportRequestDTO reportRequestDTO) {
        String empGuid = UserContextResolver.getCurrentContext().getEmpGuid();
        VendorResponseDTO vendorResponseDTO = vmsClient.fetchVendorByEmpGuIdCached(empGuid);
        String vendorNameFromVms = (vendorResponseDTO != null ? vendorResponseDTO.getVendorName() : null);
        String vendorName = reportRequestDTO.getVendor() != null ? reportRequestDTO.getVendor() : vendorNameFromVms;
        ExternalReportRequestDTO externalReportRequestDTO = prepareNRSRequest(reportRequestDTO, vendorName, reportName);
        ReportDataDTO reportDataDTO = reportingService.getReportFromNrs(externalReportRequestDTO);
        logger.info("Response from reportDataDTO {}", reportDataDTO);
        switch (reportName) {
            case VENDOR -> {
                ReportBook<VendorHeaders> reportBook = new VendorReport(vmsClient, tripsheetDomainClient);
                reportDataDTO = reportBook.generateReport(reportRequestDTO, reportDataDTO);
            }

            case OFFICE -> {
                ReportBook<OfficeHeaders> reportBook = new OfficeReport(vmsClient, tripsheetDomainClient);
                reportDataDTO = reportBook.generateReport(reportRequestDTO, reportDataDTO);
            }

            case VEHICLE -> {
                ReportBook<VehicleHeaders> reportBook = new VehicleReport(vmsClient, tripsheetDomainClient);
                reportDataDTO = reportBook.generateReport(reportRequestDTO, reportDataDTO);
            }

            case DUTY -> {
                ReportBook<DutyHeaders> reportBook = new DutyReport(vmsClient, tripsheetDomainClient);
                reportDataDTO = reportBook.generateReport(reportRequestDTO, reportDataDTO);
            }

            case CONTRACT -> {
                ReportBook<ContractHeaders> reportBook = new ContractReport(vmsClient, tripsheetDomainClient, contractWebClient);
                reportDataDTO = reportBook.generateReport(reportRequestDTO, reportDataDTO);
            }

            case DATE -> {
                ReportBook<DateHeaders> reportBook = new DateReport(vmsClient, tripsheetDomainClient);
                reportDataDTO = reportBook.generateReport(reportRequestDTO, reportDataDTO);
            }
            default -> throw new MisCustomException(ReportErrors.INVALID_REPORT_TYPE);
        }
        return reportDataDTO;
    }

    private ExternalReportRequestDTO prepareNRSRequest(BillingReportRequestDTO reportRequestDTO, String vendorName,
                                                       BillingReportAggregatedTypes reportName) {
        ExternalReportRequestDTO.ReportFilterDTO reportFilterDTO = new ExternalReportRequestDTO.ReportFilterDTO();
        reportFilterDTO.setContract(reportRequestDTO.getContract());
        if(reportRequestDTO.isOfficePresent()) reportFilterDTO.setOffice(Lists.newArrayList(reportRequestDTO.getOffice()));
        reportFilterDTO.setEntity(BillingEntityType.TRIP);
        if (vendorName != null && BillingReportAggregatedTypes.VEHICLE.equals(reportName)) {
            reportFilterDTO.setVendor(Lists.newArrayList());
            reportFilterDTO.setEntity(BillingEntityType.VEHICLE);
            reportFilterDTO.setParentEntity("VENDOR:" + reportRequestDTO.getVendor());
        }
        if (BillingReportAggregatedTypes.VEHICLE.equals(reportName) && (reportRequestDTO.isOfficePresent() || reportRequestDTO.isContractPresent())) {
            reportFilterDTO.setEntity(BillingEntityType.TRIP);
            if(vendorName!=null) reportFilterDTO.setVendor(Lists.newArrayList(vendorName));
            reportFilterDTO.setParentEntity(null);
        }

        if (BillingReportAggregatedTypes.VENDOR.equals(reportName)) {
            reportFilterDTO.setVendor(Lists.newArrayList());
            reportFilterDTO.setEntityId(vendorName);
            if(reportRequestDTO.isOfficePresent()) reportFilterDTO.setEntity(BillingEntityType.TRIP);
            else reportFilterDTO.setEntity(BillingEntityType.VENDOR);
        }

        if ( BillingReportAggregatedTypes.CONTRACT.equals(reportName)) {
            if(vendorName!=null) reportFilterDTO.setEntityId(vendorName);
            reportFilterDTO.setEntity(BillingEntityType.TRIP);
        }

        if ( BillingReportAggregatedTypes.DUTY.equals(reportName)) {
            reportFilterDTO.setEntityId(reportFilterDTO.getEntityId()); //CAB ID
            reportFilterDTO.setEntity(BillingEntityType.TRIP);
        }

        if (BillingReportAggregatedTypes.OFFICE.equals(reportName)) {
            if(vendorName!=null) reportFilterDTO.setVendor(Lists.newArrayList(vendorName));
            reportFilterDTO.setEntity(BillingEntityType.TRIP);
        }
        return ExternalReportRequestDTO.builder().reportFilter(reportFilterDTO).reportName(reportName.getReportName())
                .bunit(reportRequestDTO.getBunitId()).startDate(DateUtils.formatDate(reportRequestDTO.getCycleStart(), "yyyy-MM-dd"))
                .endDate(DateUtils.formatDate(reportRequestDTO.getCycleEnd().toString(), "yyyy-MM-dd")).build();
    }

    public List<ReportGenerationTime> getReportGenerationTime(LocalDate startDate, LocalDate endDate) {
        List<BillingStatusVO> billingStatuses = contractWebClient.getBillingStatus(startDate, endDate);
        List<ReportGenerationTime> reportGenerationTimes = new ArrayList<>();
        for (BillingStatusVO billingStatus : billingStatuses) {
            if (billingStatus == null)
                continue;
            if (BillingCurrentStatus.GENERATED.equals(billingStatus.getBillingCurrentStatus()))
                reportGenerationTimes
                        .add(new ReportGenerationTime(billingStatus.getBillingType(), billingStatus.getUpdatedDate()));
        }
        return reportGenerationTimes;
    }

    public List<BillingCycleDTO> fetchAllBillingCycles() {
        Date current = new Date();
        List<BillingCycleVO> cycleVOS = tripsheetDomainClient.fetchBillingCyclesCached();
        return cycleVOS.parallelStream()
                .filter(billingCycleVO -> !billingCycleVO.getStartDate().after(current))
                .map(this::convertToDTO).collect(Collectors.toList());
    }

    private BillingCycleDTO convertToDTO(BillingCycleVO cycleVO) {
        return new BillingCycleDTO(cycleVO.getBillingCycleId(), cycleVO.getStartDate(), cycleVO.getEndDate(),
                cycleVO.getIsFrozen());
    }

    public List<FreezeBillingResponseDTO> freezeBilling(FreezeBillingDTO freezeBillingDTO) {
        Integer vendorId = freezeBillingDTO.vendorId();
        String vendorName = freezeBillingDTO.vendorName();
        boolean freezeStatus = freezeBillingDTO.frozen();
        Date startDate = freezeBillingDTO.startDate();
        Date endDate = freezeBillingDTO.endDate();

        List<FreezeBillingResponseDTO> freezeBillingResponse = new ArrayList<>();
        if(freezeStatus && (vendorId!=null && vendorId == -1) || vendorName == null ) {
            freezeBillingResponse.addAll(
                    freezeBillingForAllVendors(startDate, endDate)
            );
        }else {
            VendorResponse vendorResponseDTO = tripsheetDomainClient.findVendorByName(vendorName);
            if(vendorResponseDTO == null) throw new MisCustomException(ReportErrors.UNABLE_TO_FIND_VENDOR_FROM_VENDOR_NAME);
            vendorId = vendorResponseDTO.getId();
            boolean freezeResult = false;
            if(vendorId!=null && vendorId!=0 && vendorId!=-1) {
                if (freezeStatus) {
                    if (isVendorAuditNotDoneForBillingCycle(vendorId, startDate, endDate))
                        throw new MisCustomException(ReportErrors.VENDOR_AUDIT_NOT_DONE_FOR_BILLING_CYCLE);
                } else {
                    tripsheetDomainClient.updateFrozen(startDate, endDate, freezeStatus); // Unfreeze Bill Cycle
                }
                freezeResult = freezeBilling(startDate, vendorId, endDate, freezeStatus);
            }
            freezeBillingResponse.add(
                    new FreezeBillingResponseDTO(freezeResult, vendorId, vendorName)
            );
        }
        return freezeBillingResponse;
    }

    private List<FreezeBillingResponseDTO> freezeBillingForAllVendors(Date startDate, Date endDate) {
        List<FreezeBillingResponseDTO> freezeBillingResponse = new ArrayList<>();
        List<VendorResponse> vendorResponse = tripsheetDomainClient.findVendorByStatusCached(Constants.VENDOR_STATUS_ACTIVE);
        if(vendorResponse == null || vendorResponse.isEmpty()) throw new MisCustomException(ReportErrors.UNABLE_TO_FETCH_ALL_VENDORS);
        vendorResponse.forEach(vendor ->{
            boolean freezeResult;
            if (isVendorAuditNotDoneForBillingCycle(vendor.getId(), startDate, endDate)) {
                freezeResult = false;
            }else {
                freezeBilling(startDate, vendor.getId(), endDate, true);
                freezeResult = true;
            }
            freezeBillingResponse.add(
                    new FreezeBillingResponseDTO(freezeResult, vendor.getId(), vendor.getVendorName())
            );
        });
        return freezeBillingResponse;
    }

    private boolean freezeBilling(Date startDate, int vendorId, Date endDate, boolean freezeStatus) {
        boolean freezeResult = true;
        try {
            List<BillingCycleDTO> billingCycles = fetchAllBillingCycles();
            BillingCycleDTO cycle = billingCycles.stream().filter(e -> e.startDate().equals(startDate)).findFirst()
                    .orElse(null);
            if (cycle == null) throw new MisCustomException(ReportErrors.BILLING_CYCLE_NOT_FOUND, startDate);

            Map<String, Integer> freezeVendorBilling =
                    tripsheetDomainClient.freezeVendorBilling(vendorId, startDate, endDate, freezeStatus);
            logger.info("Freeze Vendor Billing Results {}", freezeVendorBilling);

            VendorBillingFrozenStatusDTO vendorBillingFrozenStatusDTO =
                    tripsheetDomainClient.updateVendorBillingFreezeStatus(vendorId, cycle.id(), freezeStatus);
            logger.info("vendorBillingFrozenStatusDTO Results {}", vendorBillingFrozenStatusDTO);

        }catch (WebClientResponseException webClientException) {
            logger.error("Web Client Call failed in Freeze Billing for VendorId: {} startDate: {} endDate: {}, response {}",
                    vendorId, startDate, endDate, webClientException.getResponseBodyAsString(), webClientException);
            freezeResult = false;
        } catch (Exception ex) {
            logger.warn("Failed in Freeze Billing for VendorId: {} startDate: {} endDate: {}",
                    vendorId, startDate, endDate, ex);
            freezeResult = false;
        }
        return freezeResult;
    }

    private boolean isVendorAuditNotDoneForBillingCycle(int vendorId, Date startDate, Date endDate) {
        List<Integer> vendorCabIds = tripsheetDomainClient.allCabsIdsOfActiveVendor(-1, vendorId);
        Integer count = tripsheetDomainClient.getCountBetweenDateAndByAudit(
                DateUtils.getEpochFromDate(startDate),
                DateUtils.getEpochFromDate(endDate),
                vendorCabIds
        );
        return count != null && count > 0;
    }

    public String regenerateBilling(RegenerateBillDTO regenerateBillDTO) {
        return billingCalculationClient.generateBill(
                regenerateBillDTO.billingCycleDTO().startDate(),
                regenerateBillDTO.billingCycleDTO().endDate(),
                UserContextResolver.getCurrentContext().getBuid()
        );

    }

    public List<VendorFreezeBillingAuditDTO> getVendorBillingAudit(int billingCycleID) {
        List<VendorFreezeBillingAuditDTO> vendorsFreezeBillingAudit = Lists.newArrayList();
        List<VendorResponse> vendorResponses = tripsheetDomainClient.findVendorByStatusCached(Constants.VENDOR_STATUS_ACTIVE);
        if(vendorResponses == null || vendorResponses.isEmpty()) throw new MisCustomException(ReportErrors.UNABLE_TO_FETCH_ALL_VENDORS);
        for (VendorResponse vendor : vendorResponses) {

            List<EntityAuditDetails> auditDetails = tripsheetDomainClient.getVendorBillingFrozenStatusAuditById(billingCycleID, vendor.getId());
            if (!auditDetails.isEmpty()){

                for (EntityAuditDetails entityAuditDetail : auditDetails) {
                    // Vendor wise freeze status is created automatically for the first time with freeze status as false in
                    // BillingFreezeCoreServiceHandler while taking any action on the billing entities for that particular billing
                    // cycle. So there is no need to show the audit history for such instances.
                    if (entityAuditDetail.getAuditType() == AuditType.CREATED) {
                        continue;
                    }

                    VendorFreezeBillingAuditDTO vendorBillingFreezeAuditVo = convertToVendorBillingFreezeAuditVO(entityAuditDetail, vendor);
                    vendorsFreezeBillingAudit.add(vendorBillingFreezeAuditVo);
                }
            }
        }
        vendorsFreezeBillingAudit.sort(Comparator.comparing(VendorFreezeBillingAuditDTO::timeStamp));
        return vendorsFreezeBillingAudit;
    }

    public static VendorFreezeBillingAuditDTO convertToVendorBillingFreezeAuditVO(
            EntityAuditDetails entityAuditDetails,
            VendorResponse vendorResponse){
        return new VendorFreezeBillingAuditDTO(
                entityAuditDetails.getComment(),
                entityAuditDetails.getAuditDate(),
                (String)entityAuditDetails.getPropertyAuditDetailsList().get(0).getOldValue(),
                (String)entityAuditDetails.getPropertyAuditDetailsList().get(0).getNewValue(),
                vendorResponse.getVendorName());
    }


    public  Map<String, String> cabToVendorNameMap(){
        return tripsheetDomainClient.cabToVendorNameMap();
    }
    public  Map<String, String> cabToVehicleNumberMap(){
        return tripsheetDomainClient.cabToVehicleNumberMap();
    }

}
