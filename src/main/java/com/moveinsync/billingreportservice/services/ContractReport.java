package com.moveinsync.billingreportservice.services;

import com.moveinsync.billing.model.ContractVO;
import com.moveinsync.billingreportservice.utils.NumberUtils;
import com.moveinsync.billingreportservice.clientservice.ContractWebClientImpl;
import com.moveinsync.billingreportservice.clientservice.TripsheetDomainServiceImpl;
import com.moveinsync.billingreportservice.clientservice.VmsClientImpl;
import com.moveinsync.billingreportservice.dto.BillingReportRequestDTO;
import com.moveinsync.billingreportservice.dto.ReportDataDTO;
import com.moveinsync.billingreportservice.enums.ContractHeaders;
import com.moveinsync.billingreportservice.enums.ReportDataType;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class ContractReport extends ReportBook<ContractHeaders>   {
    @Override
    public ContractHeaders[] getHeaders() {
        return ContractHeaders.values();
    }

    private final ContractWebClientImpl contractWebClient;

    public ContractReport(VmsClientImpl vmsClient, TripsheetDomainServiceImpl tripsheetDomainService, ContractWebClientImpl contractWebClient) {
        super(vmsClient, tripsheetDomainService);
        this.contractWebClient = contractWebClient;
    }

    @Override
    public ReportDataDTO generateReport(BillingReportRequestDTO billingReportRequestDTO, ReportDataDTO reportDataDTO) {
        List<List<String>> table = reportDataDTO.getTable();
        table = filterIncomingTableHeadersAndData(table);
        reportDataDTO.setTable(table);
        table = getContractReportFromNrsResponse(reportDataDTO);
        List<String> totalRow = totalRow(table);
        table.add(totalRow);
        for (Map.Entry<Integer, List<String>> entry : reportDataDTO.getSubTotalRow().entrySet()) {
            Integer index = entry.getKey();
            List<String> row = entry.getValue();
            row.set(0, "Sub Total");
            table.add(index, row);
        }
        reportDataDTO.setTable(table);
        return reportDataDTO;
    }

    private List<List<String>> getContractReportFromNrsResponse(ReportDataDTO reportDataDTO) {
        List<List<String>> table = reportDataDTO.getTable();
        List<String> header = table.get(0);
        int contractIdx = header.indexOf(ContractHeaders.CONTRACT.getLabel());
        header.set(0, ContractHeaders.CAPACITY.getLabel());
        header.set(1, ContractHeaders.VEHICLE_TYPE.getLabel());
        for (int i = 1; i < table.size(); i++) {
            String contractName = table.get(i).get(contractIdx);
            Optional<ContractVO> contractVO = contractWebClient.getContract(contractName);
            if(contractVO.isPresent()) {
                table.get(i).set(0, contractVO.get().getSeatCapacity().toString());
                table.get(i).set(1, contractVO.get().getCabType());
            }
        }
        sortDataBasedOnCapacity(table);
        /**
         * This we are setting to let know which indexes the Subtotal rows had been placed
         *
         * */
        Map<Integer, List<String>> capacityBasedSubTotal = getCapacityWiseSubTotal(table, header);
        Map<Integer, List<String>> indexWiseSubTotalRowPlacement = placeSubTotalRowInBetweenTable(table, header, capacityBasedSubTotal);
        reportDataDTO.setSubTotalRow(indexWiseSubTotalRowPlacement);
        return table;
    }

    /**
     * For Contract report the subtotal row determines the total of the values grouped on seat capacity of a contract
     * 1st step is to prepare all subtotal Rows
     * 2nd Step is to insert the subtotal row, just after the last distinct capacity row \
     * For E.g.
     * Index    CAPACITY    Vehicle Type    Trip Count
     * 0        5           HATCHBACK       80
     * 1        5           SEDAN           100
     * 2        7           SUV             400
     * 3        7           TRAVELER        100
     * 4        7           MAHINDRA        2000
     * The subtotal row will be placed after index 1st, and after index 4th, hence result is
     * Index    CAPACITY    Vehicle Type    Trip Count
     * 0        5           HATCHBACK       80
     * 1        5           SEDAN           100
     * 2        SUBTOTAL                    180
     * 3        7           SUV             400
     * 4        7           TRAVELER        100
     * 5        7           MAHINDRA        2000
     * 6        SUBTOTAL                    2500
     */
    private static @NotNull Map<Integer, List<String>> placeSubTotalRowInBetweenTable(List<List<String>> table, List<String> header, Map<Integer, List<String>> capacityBasedSubTotal) {
        int capacityBreakPoint = 0;
        Map<Integer, List<String>> indexWiseSubTotalRowPlacement = new TreeMap<>(Comparator.reverseOrder());
        for (int i = 1; i < table.size(); i++) {
            List<String> rowData = table.get(i);
            int capacity = getCapacity(rowData, header);
            if (capacityBreakPoint == 0)
                capacityBreakPoint = capacity;
            if (capacityBreakPoint != capacity) {
                indexWiseSubTotalRowPlacement.put(i, capacityBasedSubTotal.get(capacityBreakPoint));
                capacityBreakPoint = capacity;
            }
        }
        indexWiseSubTotalRowPlacement.put(table.size(), capacityBasedSubTotal.get(capacityBreakPoint));// FOR last Seat Capacity group
        return indexWiseSubTotalRowPlacement;
    }

    /**
     * THe Method will return map of capacity and subtotal row, for e.g. the table is -
     * CAPACITY    Vehicle Type    Trip Count
     * 5           HATCHBACK       80
     * 5           SEDAN           100
     * --------------------------------
     * 7           SUV             400
     * 7           TRAVELER        100
     * 7           MAHINDRA        2000
     * The output is
     * 5, ["", "", 180]
     * 7, ["", "", 2500]
     * @param table
     * @param header
     * @return
     */
    private static @NotNull Map<Integer, List<String>> getCapacityWiseSubTotal(List<List<String>> table, List<String> header) {
        Map<Integer, List<String>> capacityBasedSubTotal = new HashMap<>();
        for (int i = 1; i < table.size(); i++) {
            List<String> rowData = table.get(i);
            Integer capacity = getCapacity(rowData, header);
            int requiredColumns = ContractHeaders.values().length;
            List<String> capacityWiseSubTotalRow = capacityBasedSubTotal.getOrDefault(capacity,
                    new ArrayList<>(Collections.nCopies(requiredColumns, "")));
            for (int j = 0; j < requiredColumns; j++) {
                if(i == ContractHeaders.CAPACITY.getIndex()) continue;
                String value = capacityWiseSubTotalRow.get(j);
                ContractHeaders contractHeader = ContractHeaders.getFromLabelName(header.get(j));
                ReportDataType dataType = contractHeader != null ? contractHeader.getDataType() : ReportDataType.STRING;
                switch (dataType) {
                    case BIGDECIMAL -> {
                        rowData.set(j, String.valueOf(NumberUtils.roundOff(rowData.get(j))));
                        BigDecimal subTotal = NumberUtils.roundOffAndAnd(value, rowData.get(j));
                        value = String.valueOf(subTotal);
                    }
                    case INTEGER -> value = String.valueOf((value.isBlank() ? 0 : Integer.parseInt(value)) + NumberUtils.parseInteger(rowData.get(j)));
                    default -> {}
                }
                capacityWiseSubTotalRow.set(j, value);
            }
            capacityBasedSubTotal.put(capacity, capacityWiseSubTotalRow);
        }
        return capacityBasedSubTotal;
    }

    public static void sortDataBasedOnCapacity(List<List<String>> data) {
        // Get the header and remove it from the list
        List<String> header = data.remove(0);

        // Sort the data based on the "Capacity" column
        data.sort((row1, row2) -> {
            // Assuming "Capacity" is at index 0
            int capacity1 = getCapacity(row1, header);
            int capacity2 = getCapacity(row2, header);
            return Integer.compare(capacity1, capacity2);
        });

        // Add the header back to the sorted data
        data.add(0, header);
    }

    public static int getCapacity(List<String> row, List<String> header) {
        int capacityIdx = header.indexOf(ContractHeaders.CAPACITY.getLabel());
        try {
            return row.get(capacityIdx)==null || row.get(capacityIdx).isBlank() ? 0 : NumberUtils.parseInteger(row.get(0));
        }catch (NumberFormatException ex) {
            return 0;
        }
    }
}