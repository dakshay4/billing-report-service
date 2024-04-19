package com.moveinsync.billingreportservice.enums;

public enum GlobalColumnLabels {

    ENTITY_ID("Entity Id"),
    VEHICLE_NUMBER("Vehicle Number"),
    OFFICE("Office"),
    DATE("Date"),
    TRIP_COUNT("Total Trip Count"),
    DUTY_COUNT("No of Duties"),
    TOTAL_TRIP_KM("Total Trip Km"),
    TOTAL_KM("Total km"),
    ADJUSTMENTS("Misc Adjustments"),
    ESCORT_COUNT("Escort Count"),
    ESCORT_COST("Escort Cost"),
    BASE_COST("Contract Price"),
    EXTRA_KM_COST("Extra Km Cost"),
    EXTRA_DUTY_COST("Extra Duty Cost"),
    EXTRA_HOUR_COST("Extra Hour Cost"),
    DRIVER_ALLOWANCE("Driver Allowance"),
    AC_COST("Ac Cost"),
    EXPENSE_COST("Expense Cost"),
    GRAND_TOTAL("Grand Total"),
    GST("GST"),
    GRAND_TOTAL_GST("Total"),
    FROZEN("Frozen"),
    CAPACITY("Capacity"),
    VEHICLE_TYPE("Vehicle Type"),
    CONTRACT("Contract"),
    ACTION("Action");

    private final String labelIdentifier;

    GlobalColumnLabels(String label) {
        this.labelIdentifier = label;
    }

    public String getLabelIdentifier() {
        return labelIdentifier;
    }
}
