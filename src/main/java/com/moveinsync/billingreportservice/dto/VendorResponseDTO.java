package com.moveinsync.billingreportservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class VendorResponseDTO {

    private String vendorKey;
    private String vendorId;
    private String vendorName;
    private String pointOfContact;
    private String emailId;
    private String address;
    private String phoneNumber;
    private String businessUnitId;
    private boolean status;

}
