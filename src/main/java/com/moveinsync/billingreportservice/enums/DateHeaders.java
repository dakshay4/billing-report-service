package com.moveinsync.billingreportservice.enums;

public enum DateHeaders implements TableHeaders {
    TOTAL_KM(0, GlobalColumnLabels.TOTAL_KM, ReportDataType.BIGDECIMAL),
    START_TIME(1, GlobalColumnLabels.START_TIME, ReportDataType.DATE),
    END_TIME(2, GlobalColumnLabels.END_TIME, ReportDataType.DATE),
    TOTAL_HOURS(3, GlobalColumnLabels.TOTAL_HOURS, ReportDataType.BIGDECIMAL),
    NUM_OF_TRIPS(4, GlobalColumnLabels.TRIP_COUNT, ReportDataType.INTEGER),
    ACTION(5, GlobalColumnLabels.ACTION, ReportDataType.STRING);


    private final int index;
    private final GlobalColumnLabels key;
    private final ReportDataType dataType;

    DateHeaders(int index, GlobalColumnLabels key, ReportDataType dataType) {
        this.index = index;
        this.key = key;
        this.dataType = dataType;
    }


    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public String getLabel() {
        return key.getLabelIdentifier();
    }

    @Override
    public ReportDataType getDataType() {
        return dataType;
    }
}
