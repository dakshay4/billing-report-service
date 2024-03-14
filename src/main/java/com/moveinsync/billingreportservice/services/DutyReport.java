package com.moveinsync.billingreportservice.services;

import com.moveinsync.billingreportservice.dto.ReportDataDTO;
import com.moveinsync.billingreportservice.enums.DutyHeaders;
import com.moveinsync.billingreportservice.enums.VendorHeaders;

import java.util.List;

public class DutyReport<T extends Enum<T>> extends ReportBook<DutyHeaders>  {


    @Override
    public DutyHeaders[] getHeaders() {
        return DutyHeaders.values();
    }

    @Override
    public ReportDataDTO generateReport(ReportDataDTO reportDataDTO) {
        List<List<String>> table = reportDataDTO.getTable();
        table = filterIncomingTableHeadersAndData(table);
        List<String> totalRow = totalRow(table);
        table.add(totalRow);
        reportDataDTO.setTable(table);
        return reportDataDTO;
    }
}
