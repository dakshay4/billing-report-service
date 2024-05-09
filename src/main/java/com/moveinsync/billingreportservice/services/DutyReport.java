package com.moveinsync.billingreportservice.services;

import com.moveinsync.billingreportservice.clientservice.TripsheetDomainServiceImpl;
import com.moveinsync.billingreportservice.clientservice.VmsClientImpl;
import com.moveinsync.billingreportservice.dto.BillingReportRequestDTO;
import com.moveinsync.billingreportservice.dto.ReportDataDTO;
import com.moveinsync.billingreportservice.enums.DutyHeaders;

import java.util.List;

public class DutyReport extends ReportBook<DutyHeaders>  {


    public DutyReport(VmsClientImpl vmsClient, TripsheetDomainServiceImpl tripsheetDomainService) {
        super(vmsClient, tripsheetDomainService);
    }

    @Override
    public DutyHeaders[] getHeaders() {
        return DutyHeaders.values();
    }

    @Override
    public ReportDataDTO generateReport(BillingReportRequestDTO billingReportRequestDTO, ReportDataDTO reportDataDTO) {
        List<List<String>> table = reportDataDTO.getTable();
        table = filterIncomingTableHeadersAndData(table);
        List<String> totalRow = totalRow(table);
        table.add(totalRow);
        reportDataDTO.setTable(table);
        return reportDataDTO;
    }
}
