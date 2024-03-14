package com.moveinsync.billingreportservice.services;

import com.moveinsync.billingreportservice.Utils.NumberUtils;
import com.moveinsync.billingreportservice.dto.ReportDataDTO;
import com.moveinsync.billingreportservice.enums.BillingReportAggregatedTypes;
import com.moveinsync.billingreportservice.enums.ContractHeaders;
import com.moveinsync.billingreportservice.enums.ReportDataType;
import com.moveinsync.billingreportservice.enums.TableHeaders;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ReportBook<T extends TableHeaders> {

    public abstract T[] getHeaders();


    public abstract ReportDataDTO generateReport(ReportDataDTO reportDataDTO);

    public List<List<String>> filterIncomingTableHeadersAndData(List<List<String>> table) {
        if(table == null) return new ArrayList<>();
        List<String> header = table.get(0);
        Set<String> headerLabels = Arrays.stream(getHeaders()).map(e->e.getColumnLabel()).collect(
                Collectors.toSet());
        List<Integer> validIndices = new ArrayList<>();
        for (int i = 0; i < header.size(); i++)
            if (headerLabels.contains(header.get(i)))
                validIndices.add(i);

        table = table.stream().map(row -> validIndices.stream().map(row::get).collect(Collectors.toList()))
                .collect(Collectors.toList());
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
                ContractHeaders contractHeader = ContractHeaders.getFromLabelName(header.get(j));
                ReportDataType dataType = contractHeader != null ? contractHeader.getDataType() : ReportDataType.STRING;
                switch (dataType) {
                    case BIGDECIMAL:
                        rowData.set(j, String.valueOf(NumberUtils.roundOff(rowData.get(j))));
                        BigDecimal subTotal = NumberUtils.roundOffAndAnd(value, rowData.get(j));
                        value = String.valueOf(subTotal);
                        break;
                    case INTEGER:
                        value = String.valueOf((value.isEmpty() ? 0 : Integer.parseInt(value)) + Integer.parseInt(rowData.get(j)));
                }
                totalRow.set(j, value);
            }
        }
        totalRow.set(0, "Total");
        return totalRow;
    }

}
