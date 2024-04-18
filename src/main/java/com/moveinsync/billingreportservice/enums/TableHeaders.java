package com.moveinsync.billingreportservice.enums;

import java.util.Arrays;

public interface TableHeaders {

    public static <T extends TableHeaders> T getFromLabelName(Class<T> enumClass, String label) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> e.getLabel().equals(label))
                .findFirst()
                .orElse(null);
    }

    public static <T extends TableHeaders> T getFromName(Class<T> enumClass, String name) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> e.name().equals(name))
                .findFirst()
                .orElse(null);
    }

    public int getIndex();

    public String getLabel();

    public ReportDataType getDataType();

    public String name();



}