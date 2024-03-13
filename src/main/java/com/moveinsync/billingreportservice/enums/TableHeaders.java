package com.moveinsync.billingreportservice.enums;

import java.util.Arrays;

public interface TableHeaders {
    public static <T extends Enum<T>> T getFromLabelName(Class<T> enumClass, String label) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> e.name().equals(label))
                .findFirst()
                .orElse(null);
    }

    public int getIndex();

    public String getColumnLabel();

    public ReportDataType getDataType();



}