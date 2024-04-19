package com.moveinsync.billingreportservice.services;

import com.mis.serverdata.pc.duty.PlatformDuty;
import com.moveinsync.billingreportservice.Configurations.UserContextResolver;
import com.moveinsync.billingreportservice.Utils.DateUtils;
import com.moveinsync.billingreportservice.clientservice.TripsheetDomainServiceImpl;
import com.moveinsync.billingreportservice.clientservice.VmsClientImpl;
import com.moveinsync.billingreportservice.constants.Constants;
import com.moveinsync.billingreportservice.dto.BillingReportRequestDTO;
import com.moveinsync.billingreportservice.dto.ReportDataDTO;
import com.moveinsync.billingreportservice.enums.DateHeaders;
import com.moveinsync.timezone.MisTimeZoneUtils;
import com.moveinsync.tripsheetdomain.response.CabSignInResponseDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DateReport<T extends Enum<T>> extends ReportBook<DateHeaders> {


    protected DateReport(VmsClientImpl vmsClient, TripsheetDomainServiceImpl tripsheetDomainService) {
        super(vmsClient, tripsheetDomainService);
    }

    @Override
    public DateHeaders[] getHeaders() {
        return DateHeaders.values();
    }

    @Override
    public ReportDataDTO generateReport(BillingReportRequestDTO billingReportRequestDTO, ReportDataDTO reportDataDTO) {
        LocalDateTime dutyTime = DateUtils.parse(billingReportRequestDTO.getDate());
        Integer cabId = 0;
        List<List<String>> table = new ArrayList<>();
        List<CabSignInResponseDTO> billingDuties = getTripsheetDomainService().billingDuties(
                MisTimeZoneUtils.getEpochFromLocalDateTime(dutyTime, UserContextResolver.getCurrentContext().getBuid()),
                MisTimeZoneUtils.getEpochFromLocalDateTime(dutyTime, UserContextResolver.getCurrentContext().getBuid()),
                cabId, PlatformDuty.STATE_OFF_DUTY, Constants.CAB_SIGN_IN_STATUS_ACTIVE,true
        );
        List<String> headerRow = new ArrayList<>();
        for (DateHeaders header : getHeaders()) {
            headerRow.add(header.getLabel());
        }
        table.add(headerRow);
        billingDuties.forEach(billingDuty->{

        });
        return null;
    }
}
