package com.moveinsync.billingreportservice.services;


import com.moveinsync.billingreportservice.Configurations.UserContextResolver;
import com.moveinsync.billingreportservice.dto.BillingReportRequestDTO;
import com.moveinsync.billingreportservice.dto.ExternalReportRequestDTO;
import com.moveinsync.billingreportservice.dto.VendorResponseDTO;
import com.moveinsync.billingreportservice.enums.BillingReportAggregatedTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class BillingReportService {



    @Autowired
    private WebClient vmsClient;

    public Object getData(BillingReportAggregatedTypes reportName, BillingReportRequestDTO reportRequestDTO) {

        String empGuid = UserContextResolver.getCurrentContext().getEmpGuid();
        VendorResponseDTO vendorResponseDTO = vmsClient.get().uri("vendors/id/"+ empGuid).retrieve().bodyToMono(VendorResponseDTO.class).block();
        String vendorName = reportRequestDTO.getVendor()!= null ? reportRequestDTO.getVendor() : (vendorResponseDTO!=null ? vendorResponseDTO.getVendorName() : null);

        switch (reportName) {
            case VENDOR -> {
                prepareNRSRequest(reportRequestDTO);
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

    private void prepareNRSRequest(BillingReportRequestDTO reportRequestDTO) {
        ExternalReportRequestDTO.ReportFilterDTO reportFilterDTO = new ExternalReportRequestDTO.ReportFilterDTO();
        reportFilterDTO.setContract(reportRequestDTO.getContract());
        reportFilterDTO.setVendor(vendorName);
        reportFilterDTO.setEntityId(reportRequestDTO.getEntityId());
        reportFilterDTO.setEntityId(reportRequestDTO.getEntityId());
    }

    private void validateManadatoryParameters() {

    }
}
