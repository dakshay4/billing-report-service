package com.moveinsync.billingreportservice.enums;

public enum BillingReportAggregatedTypes {

    VENDOR("Billing_NEW_VENDOR_Report"),
    VEHICLE("Billing_NEW_VEHICLE_Report"),
    CONTRACT("Billing_NEW_CONTRACT_Report"),
    OFFICE("Billing_NEW_OFFICE_Report"),
    DUTY("Billing_NEW_DUTY_Report");

    String reportName;
    BillingReportAggregatedTypes(String reportName) {
        this.reportName = reportName;
    }

    public String getReportName() {
        return reportName;
    }
}
