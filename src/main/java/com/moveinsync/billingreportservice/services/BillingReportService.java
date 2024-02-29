package com.moveinsync.billingreportservice.services;


import com.moveinsync.billingreportservice.dto.BillingReportRequestDTO;
import com.moveinsync.billingreportservice.enums.BillingReportAggregatedTypes;
import org.springframework.stereotype.Component;

@Component
public class BillingReportService {
    public void getData(BillingReportAggregatedTypes reportName, BillingReportRequestDTO reportRequestDTO) {

        switch (reportName) {
            case VENDOR -> {

                break;
            }
            case VEHICLE -> {

                break;
            }
            case OFFICE -> {

                break;
            }
            case CONTRACT -> {

                break;
            }
            case DUTY -> {

                break;
            }
        }
    }
}
