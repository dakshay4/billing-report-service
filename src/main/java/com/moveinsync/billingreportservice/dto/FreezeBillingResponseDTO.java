package com.moveinsync.billingreportservice.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class FreezeBillingResponseDTO {

    private final boolean freezeStatus;
    private final Integer vendorId;
    private final String vendorName;
}
