package com.moveinsync.billingreportservice.services;

import com.moveinsync.billingreportservice.clientservice.TripsheetDomainServiceImpl;
import com.moveinsync.billingreportservice.clientservice.VmsClientImpl;
import com.moveinsync.billingreportservice.dto.BillingReportRequestDTO;
import com.moveinsync.billingreportservice.dto.ReportDataDTO;
import com.moveinsync.billingreportservice.enums.VehicleHeaders;

import java.util.List;
import java.util.Map;

public class VehicleReport extends ReportBook<VehicleHeaders> {
    public VehicleReport(VmsClientImpl vmsClient, TripsheetDomainServiceImpl tripsheetDomainService) {
        super(vmsClient, tripsheetDomainService);
    }

    @Override
    public VehicleHeaders[] getHeaders() {
        return VehicleHeaders.values();
    }


    @Override
    public ReportDataDTO generateReport(BillingReportRequestDTO billingReportRequestDTO, ReportDataDTO reportDataDTO) {
        List<List<String>> table = reportDataDTO.getTable();
        table = filterIncomingTableHeadersAndData(table);
        int index = VehicleHeaders.VEHICLE_NUMBER.getIndex();
        for(int i=1; i<table.size(); i++) {
            List<String> row = table.get(i);
            Map<String, String> cabVehicleMap = getTripsheetDomainService().cabToVehicleNumberMap();
            String vehicleNumber = cabVehicleMap.get(row.get(VehicleHeaders.ENTITY_ID.getIndex()));
            row.set(index, vehicleNumber);
        }
        List<String> totalRow = totalRow(table);
        table.add(totalRow);
        reportDataDTO.setTable(table);
        return reportDataDTO;
    }
}