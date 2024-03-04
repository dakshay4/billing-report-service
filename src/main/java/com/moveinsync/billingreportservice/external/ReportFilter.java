package com.moveinsync.billingreportservice.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportFilter {

    private List<String> vendor;
    private String contract;
    private String entityId;
    private String parentEntity;
}
