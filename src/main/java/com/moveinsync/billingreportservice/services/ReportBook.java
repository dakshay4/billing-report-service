package com.moveinsync.billingreportservice.services;

import com.moveinsync.billingreportservice.Utils.DateFormatReader;
import com.moveinsync.billingreportservice.Utils.DateUtils;
import com.moveinsync.billingreportservice.Utils.NumberUtils;
import com.moveinsync.billingreportservice.clientservice.TripsheetDomainServiceImpl;
import com.moveinsync.billingreportservice.clientservice.VmsClientImpl;
import com.moveinsync.billingreportservice.dto.BillingReportRequestDTO;
import com.moveinsync.billingreportservice.dto.ReportDataDTO;
import com.moveinsync.billingreportservice.dto.VendorResponseDTO;
import com.moveinsync.billingreportservice.enums.ReportDataType;
import com.moveinsync.billingreportservice.enums.TableHeaders;
import com.moveinsync.billingreportservice.enums.VendorHeaders;
import com.moveinsync.tripsheetdomain.models.BillingCycleVO;
import com.moveinsync.tripsheetdomain.response.VendorBillingFrozenStatusDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ReportBook<T extends TableHeaders> {

    private final VmsClientImpl vmsClient;
    private final TripsheetDomainServiceImpl tripsheetDomainService;

    protected ReportBook(VmsClientImpl vmsClient, TripsheetDomainServiceImpl tripsheetDomainService) {
        this.vmsClient = vmsClient;
        this.tripsheetDomainService = tripsheetDomainService;
    }


    public abstract T[] getHeaders();

    private final static  Logger logger = LoggerFactory.getLogger(ReportBook.class);

//    public abstract Class getEnumClass();

    public abstract ReportDataDTO generateReport(BillingReportRequestDTO billingReportRequestDTO, ReportDataDTO reportDataDTO);

    public List<String> getHeaderRow(List<List<String>> table) {
        if(table!=null && !table.isEmpty()) return table.get(0);
        return new ArrayList<>();
    }

    public List<List<String>> filterIncomingTableHeadersAndData(List<List<String>> table) {
        logger.info("Filtered table {}", table);
        if(table == null || table.isEmpty()) return new ArrayList<>();
        List<String> header = table.get(0);
        Set<String> headerLabels = Arrays.stream(getHeaders()).map(e->e.getKey()).collect(
                Collectors.toSet());
        List<Integer> validIndices = new ArrayList<>();
        for (int i = 0; i < header.size(); i++)
            if (headerLabels.contains(header.get(i)))
                validIndices.add(i);

        table = table.stream().map(row -> validIndices.stream().map(row::get).collect(Collectors.toList()))
                .collect(Collectors.toList());
        table = reorderTable(table);
        return table;
    }

    public List<String> totalRow(List<List<String>> table) {
        if (table == null || table.isEmpty())
            return new ArrayList<>();
        List<String> header = table.get(0);
        int requiredColumns = header.size();
        List<String> totalRow = new ArrayList<>(Collections.nCopies(requiredColumns, ""));
        for (int i = 1; i < table.size(); i++) {
            List<String> rowData = table.get(i);
            for (int j = 0; j < requiredColumns; j++) {
                String value = totalRow.get(j);
                TableHeaders tableHeaders = TableHeaders.getFromLabelName(getHeaders()[0].getClass(), header.get(j));
                ReportDataType dataType = tableHeaders != null ? tableHeaders.getDataType() : ReportDataType.STRING;
                switch (dataType) {
                    case BIGDECIMAL:
                        rowData.set(j, String.valueOf(NumberUtils.roundOff(rowData.get(j))));
                        BigDecimal subTotal = NumberUtils.roundOffAndAnd(value, rowData.get(j));
                        value = String.valueOf(subTotal);
                        break;
                    case INTEGER:
                        value = String.valueOf((value.isEmpty() ? 0 : Integer.parseInt(value)) + NumberUtils.parseInteger(rowData.get(j)));
                }
                totalRow.set(j, value);
            }
        }
        totalRow.set(0, "Total");
        return totalRow;
    }

    public List<List<String>> reorderTable(List<List<String>> table) {
        List<String> baseRow = table.get(0);
        List<Integer> reorderIndices = new ArrayList<>();
        for (T header : getHeaders()) {
            String columnLabel = header.getKey();
            int index = baseRow.indexOf(columnLabel);
            reorderIndices.add(index);
        }

        for (int i = 0; i < table.size(); i++) {
            List<String> row = table.get(i);
            List<String> reorderedRow = new ArrayList<>(row.size());
            for (int index : reorderIndices) {
                if (index != -1) {
                    reorderedRow.add(row.get(index));
                } else {
                    reorderedRow.add(""); // or any default value if not found
                }
            }
            table.set(i, reorderedRow);
        }

        return table;
    }

    public void addFrozenColumn(BillingReportRequestDTO billingReportRequestDTO, List<List<String>> table,
                                TableHeaders tableHeaderFrozen,
                                TableHeaders tableHeaderVendor
                                ) {
        int frozenRowIndex = tableHeaderFrozen.getIndex();
        if(frozenRowIndex < getHeaderRow(table).size()) getHeaderRow(table).set(frozenRowIndex, tableHeaderFrozen.getKey());
        for (int i = 1; i < table.size(); i++) {
            List<String> dataRows = table.get(i);
            String vendorName = tableHeaderVendor!=null ? dataRows.get(tableHeaderVendor.getIndex()) : null;
            VendorResponseDTO vendorResponseDTO = vmsClient.fetchVendorByVendorNameCached(vendorName);
            String vendorId = null;
            if (vendorResponseDTO == null) return;
            vendorId = vendorResponseDTO.getVendorId();
            DateFormatReader.readDateFormatFromAnnotation(BillingReportRequestDTO.class,
                    BillingReportRequestDTO.Fields.cycleStart);
            Date start = DateUtils.convert(billingReportRequestDTO.getCycleStart(), new SimpleDateFormat(Objects.requireNonNull(DateFormatReader
                    .readDateFormatFromAnnotation(BillingReportRequestDTO.class, BillingReportRequestDTO.Fields.cycleStart))));
            Date end = DateUtils.convert(billingReportRequestDTO.getCycleEnd(), new SimpleDateFormat(Objects.requireNonNull(DateFormatReader
                    .readDateFormatFromAnnotation(BillingReportRequestDTO.class, BillingReportRequestDTO.Fields.cycleEnd))));
            BillingCycleVO billingCycleVO = tripsheetDomainService.fetchBillingCycle(start, end);
            if(billingCycleVO!=null) {
                VendorBillingFrozenStatusDTO vendorBillingFrozenStatusDTO = tripsheetDomainService
                        .findVendorBillingFrozenStatusById(billingCycleVO.getBillingCycleId(), Integer.parseInt(vendorId));
                dataRows.set(frozenRowIndex,  String.valueOf(vendorBillingFrozenStatusDTO.isFreezed()));
            }
        }
    }

    public void replaceEntityIdByVendorName(List<List<String>> table) {
        Map<String, String> cabMap = tripsheetDomainService.findAllCabs();
        for(int i=1; i<table.size(); i++) {
            List<String> row = table.get(i);
            String entityId = row.get(VendorHeaders.VENDOR.getIndex());
            if(cabMap.containsKey(entityId))
                row.set(VendorHeaders.VENDOR.getIndex(), cabMap.get(entityId));
        }
    }
}
