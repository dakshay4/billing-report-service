package com.moveinsync.billingreportservice.enums;

public enum GlobalColumnLabels {

    VENDOR("Vendor"),
    ENTITY_ID("Entity Id"),
    VEHICLE_NUMBER("Vehicle Number"),
    OFFICE("Office"),
    DATE("Date"),
    TRIP_COUNT("Trip Count"),
    DUTY_COUNT("Duty Count"),
    TOTAL_TRIP_KM("Total Trip Km"),
    TOTAL_KM("Total km"),
    ADJUSTMENTS("Adjustments"),
    ESCORT_COUNT("Escort Count"),
    ESCORT_COST("Escort Cost"),
    BASE_COST("Base Cost"),
    EXTRA_KM_COST("Extra Km Cost"),
    EXTRA_DUTY_COST("Extra Duty Cost"),
    EXTRA_HOUR_COST("Extra Hour Cost"),
    DRIVER_ALLOWANCE("Driver Allowance"),
    AC_COST("Ac Cost"),
    EXPENSE_COST("Expense Cost"),
    GRAND_TOTAL("Grand Total"),
    GST("GST"),
    GRAND_TOTAL_GST("Grand Total GST"),
    FROZEN("Frozen"),
    CAPACITY("Capacity"),
    VEHICLE_TYPE("Vehicle Type"),
    CONTRACT("Contract"),
    ACTION("Action"),
    START_TIME("Start Time"),
    END_TIME("End Time"),
    TOTAL_HOURS("Total Hours"),
    NUM_OF_TRIPS("Number of Trips"),
    DUTY_ID("Duty Id");


    private final String labelIdentifier;

    GlobalColumnLabels(String label) {
        this.labelIdentifier = label;
    }

    public String getLabelIdentifier() {
        return labelIdentifier;
    }
}
