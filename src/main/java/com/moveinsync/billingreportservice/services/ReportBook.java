package com.moveinsync.billingreportservice.services;

import com.moveinsync.billingreportservice.utils.DateFormatReader;
import com.moveinsync.billingreportservice.utils.DateUtils;
import com.moveinsync.billingreportservice.utils.NumberUtils;
import com.moveinsync.billingreportservice.clientservice.TripsheetDomainServiceImpl;
import com.moveinsync.billingreportservice.clientservice.VmsClientImpl;
import com.moveinsync.billingreportservice.dto.BillingReportRequestDTO;
import com.moveinsync.billingreportservice.dto.ReportDataDTO;
import com.moveinsync.billingreportservice.dto.VendorResponseDTO;
import com.moveinsync.billingreportservice.enums.DateFormatPattern;
import com.moveinsync.billingreportservice.enums.ReportDataType;
import com.moveinsync.billingreportservice.enums.TableHeaders;
import com.moveinsync.tripsheetdomain.models.BillingCycleVO;
import com.moveinsync.tripsheetdomain.response.VendorBillingFrozenStatusDTO;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ReportBook<T extends TableHeaders> {

    private final VmsClientImpl vmsClient;
    private final TripsheetDomainServiceImpl tripsheetDomainService;

    public VmsClientImpl getVmsClient() {
        return vmsClient;
    }

    public TripsheetDomainServiceImpl getTripsheetDomainService() {
        return tripsheetDomainService;
    }

    protected ReportBook(VmsClientImpl vmsClient, TripsheetDomainServiceImpl tripsheetDomainService) {
        this.vmsClient = vmsClient;
        this.tripsheetDomainService = tripsheetDomainService;
    }


    public abstract T[] getHeaders();


    public abstract ReportDataDTO generateReport(BillingReportRequestDTO billingReportRequestDTO, ReportDataDTO reportDataDTO);

    public List<String> getHeaderRow(List<List<String>> table) {
        if(table!=null && !table.isEmpty()) return table.get(0);
        return new ArrayList<>();
    }

    /**
     * This will Filter out the table columns and only allow Column Labels which are present in Report enum
     * Also Rearrange the column labels as per the index specified in the Report Enum
     * @param table
     * @return
     */
    protected List<List<String>> filterIncomingTableHeadersAndData(List<List<String>> table) {
        if(table == null || table.isEmpty()) return new ArrayList<>();
        List<String> header = table.get(0);
        Set<String> headerLabels = Arrays.stream(getHeaders()).map(e->e.getLabel()).collect(
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

    /**
     * The method is used to return the grand total row of the table data
     * It iterates over each row, and update the Decimal values rounded off and
     * updates DATE cell with the {@link com.moveinsync.billingreportservice.enums.DateFormatPattern-ETS_DATE_TIME_FORMAT}
     * @param table
     * @return
     */
    protected List<String> totalRow(List<List<String>> table) {
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
                    case BIGDECIMAL -> {
                        rowData.set(j, String.valueOf(NumberUtils.roundOff(rowData.get(j))));
                        BigDecimal subTotal = NumberUtils.roundOffAndAnd(value, rowData.get(j));
                        value = String.valueOf(subTotal);
                    }
                    case INTEGER ->
                        value = String.valueOf((value.isEmpty() ? 0 : Integer.parseInt(value)) + NumberUtils.parseInteger(rowData.get(j)));

                    case DATE -> {
                        String formattedDate = DateUtils.formatDate(DateUtils.parse(rowData.get(j)), DateFormatPattern.DD_MM_YYYY.getPattern());
                        rowData.set(j, formattedDate);
                    }
                    default -> {}
                }
                totalRow.set(j, value);
            }
        }
        totalRow.set(0, "Total");
        return totalRow;
    }

    protected List<List<String>> reorderTable(List<List<String>> table) {
        List<String> baseRow = table.get(0);
        List<Integer> reorderIndices = new ArrayList<>();
        Queue<String> diffLabels = new LinkedList<>();
        for (T header : getHeaders()) {
            String columnLabel = header.getLabel();
            int index = baseRow.indexOf(columnLabel);
            if(index==-1) diffLabels.add(columnLabel);
            reorderIndices.add(index);
        }

        for (int i = 0; i < table.size(); i++) {
            List<String> row = table.get(i);
            List<String> reorderedRow = new ArrayList<>(row.size());
            for (int index : reorderIndices) {
                if (index != -1) {
                    reorderedRow.add(row.get(index));
                } else {
                    reorderedRow.add(diffLabels.poll()); // or any default value if not found
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
        if(frozenRowIndex < getHeaderRow(table).size()) getHeaderRow(table).set(frozenRowIndex, tableHeaderFrozen.getLabel());
        for (int i = 1; i < table.size(); i++) {
            List<String> dataRows = table.get(i);
            String vendorName = tableHeaderVendor!=null ? dataRows.get(tableHeaderVendor.getIndex()) : null;
            VendorResponseDTO vendorResponseDTO = vmsClient.fetchVendorByVendorNameCached(vendorName);
            Integer vendorId = null;
            if (vendorResponseDTO == null) return;
            vendorId = vendorResponseDTO.getId();
            DateFormatReader.readDateFormatFromAnnotation(BillingReportRequestDTO.class,
                    BillingReportRequestDTO.Fields.cycleStart);
            Date start = DateUtils.convert(billingReportRequestDTO.getCycleStart(), new SimpleDateFormat(Objects.requireNonNull(DateFormatReader
                    .readDateFormatFromAnnotation(BillingReportRequestDTO.class, BillingReportRequestDTO.Fields.cycleStart))));
            Date end = DateUtils.convert(billingReportRequestDTO.getCycleEnd(), new SimpleDateFormat(Objects.requireNonNull(DateFormatReader
                    .readDateFormatFromAnnotation(BillingReportRequestDTO.class, BillingReportRequestDTO.Fields.cycleEnd))));
            BillingCycleVO billingCycleVO = tripsheetDomainService.fetchBillingCycle(start, end);
            if(billingCycleVO!=null) {
                VendorBillingFrozenStatusDTO vendorBillingFrozenStatusDTO = tripsheetDomainService
                        .findVendorBillingFrozenStatusById(billingCycleVO.getBillingCycleId(), vendorId);
                dataRows.set(frozenRowIndex,  String.valueOf(vendorBillingFrozenStatusDTO.isFreezed()));
            }
        }
    }

    public void replaceEntityIdByVendorName(List<List<String>> table, int index) {
        Map<String, String> cabMap = tripsheetDomainService.cabToVendorNameMap();
        for(int i=1; i<table.size(); i++) {
            List<String> row = table.get(i);
            String entityId = row.get(index);
            if(cabMap.containsKey(entityId))
                row.set(index, cabMap.get(entityId));
        }
    }

    public static void sortList(List<List<String>> table, int startRow, int columnIndex) {
        table.subList(startRow, table.size()).sort(Comparator.comparing(row -> row.get(columnIndex)));
    }
}
