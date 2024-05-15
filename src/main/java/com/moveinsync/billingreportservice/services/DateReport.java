package com.moveinsync.billingreportservice.services;

import com.mis.serverdata.pc.duty.PlatformDuty;
import com.moveinsync.billingreportservice.configurations.UserContextResolver;
import com.moveinsync.billingreportservice.utils.DateUtils;
import com.moveinsync.billingreportservice.utils.NumberUtils;
import com.moveinsync.billingreportservice.clientservice.TripsheetDomainServiceImpl;
import com.moveinsync.billingreportservice.clientservice.VmsClientImpl;
import com.moveinsync.billingreportservice.constants.Constants;
import com.moveinsync.billingreportservice.dto.BillingReportRequestDTO;
import com.moveinsync.billingreportservice.dto.ReportDataDTO;
import com.moveinsync.billingreportservice.enums.DateHeaders;
import com.moveinsync.billingreportservice.exceptions.MisCustomException;
import com.moveinsync.billingreportservice.exceptions.ReportErrors;
import com.moveinsync.timezone.MisTimeZoneUtils;
import com.moveinsync.tripsheetdomain.response.CabSignInResponseDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DateReport extends ReportBook<DateHeaders> {


    protected DateReport(VmsClientImpl vmsClient, TripsheetDomainServiceImpl tripsheetDomainService) {
        super(vmsClient, tripsheetDomainService);
    }

    @Override
    public DateHeaders[] getHeaders() {
        return DateHeaders.values();
    }

    @Override
    public ReportDataDTO generateReport(BillingReportRequestDTO billingReportRequestDTO, ReportDataDTO reportDataDTO) {
        LocalDate dutyTime = DateUtils.parse(billingReportRequestDTO.getDate());
        String entityId = billingReportRequestDTO.getEntityId();
        Map<String, Integer> entityIdTocabMap = getTripsheetDomainService().cabIdToIdentifierMap();
        Integer cabIdentifier = entityIdTocabMap.get(entityId);
        if(dutyTime == null) throw new MisCustomException(ReportErrors.INVALID_DATE_FORMAT, billingReportRequestDTO.getDate());
        if(cabIdentifier == null) throw new MisCustomException(ReportErrors.INVALID_CAB_ID, billingReportRequestDTO.getDate());
        List<List<String>> table = new ArrayList<>();
        List<CabSignInResponseDTO> billingDuties = getTripsheetDomainService().billingDuties(
                MisTimeZoneUtils.getEpochFromLocalDateTime(dutyTime.atStartOfDay(), UserContextResolver.getCurrentContext().getBuid()),
                MisTimeZoneUtils.getEpochFromLocalDateTime(dutyTime.atStartOfDay(), UserContextResolver.getCurrentContext().getBuid()),
                cabIdentifier, PlatformDuty.STATE_OFF_DUTY, Constants.CAB_SIGN_IN_STATUS_ACTIVE,true
        );
        List<String> headerRow = new ArrayList<>();
        for (DateHeaders header : getHeaders()) {
            headerRow.add(header.getLabel());
        }
        table.add(headerRow);
        billingDuties.forEach(billingDuty->{
            List<String> row = new ArrayList<>();
            row.add(NumberUtils.roundOff(billingDuty.getTotalKM()).toString());
            row.add(DateUtils.formatDate(billingDuty.getReportIn(),"dd/MM/yyyy hh:mm"));
            row.add(DateUtils.formatDate(billingDuty.getReportOff(),"dd/MM/yyyy hh:mm"));
            row.add(String.valueOf(billingDuty.getDutyHours()));
            row.add(String.valueOf(billingDuty.getBillingTripCount()));
            row.add(String.valueOf(billingDuty.getDutyId()));
            row.add("View Map");
            table.add(row);
        });
        return new ReportDataDTO(table, null);
    }
}
