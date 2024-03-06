package com.moveinsync.billingreportservice.controllers;

import com.moveinsync.billing.exception.UserDefinedException;
import com.moveinsync.billingreportservice.Configurations.UserContextResolver;
import com.moveinsync.billingreportservice.dto.BillingReportRequestDTO;
import com.moveinsync.billingreportservice.dto.ReportDataDTO;
import com.moveinsync.billingreportservice.enums.BillingReportAggregatedTypes;
import com.moveinsync.billingreportservice.services.BillingReportService;
import com.moveinsync.tripsheetdomain.response.EmployeeAdditionalAttributeLong;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RestController
@RequestMapping("/billingReports/web")
public class BillingReportController {

    @Autowired
    private BillingReportService billingReportService;

    private final Logger LOG = LoggerFactory.getLogger(getClass());


    @GetMapping("/data/{reportName}")
    public ResponseEntity<ReportDataDTO> reportdata(@PathVariable BillingReportAggregatedTypes reportName, @RequestBody BillingReportRequestDTO reportRequestDTO)
        throws UserDefinedException {
        return ResponseEntity.ok(billingReportService.getData(reportName, reportRequestDTO));
    }


    private static final int GUID_LENGTH = 36;
    private static final int[] GUID_GROUP_LENGTHS = {8, 4, 4, 4, 12};
    private static final char NATIVE_ID_SEPARATOR = '$';

    private static final String BUSINESS_UNIT_SEP="-";

    public static String createGUID(String businessUnitId, String entityIdPrefix,
                                    String nativeId) {
        String subDomainName = businessUnitId.split(BUSINESS_UNIT_SEP)[0];
        String siteName = businessUnitId.split(BUSINESS_UNIT_SEP)[1];

        StringBuilder input = new StringBuilder();
        input.append(entityIdPrefix);
        input.append(subDomainName);
        input.append(siteName);

        StringBuilder guid = new StringBuilder();
        int charIndex = 0;
        int groupIndex = 0;
        int cummulativeLength = 0;

        while (groupIndex < GUID_GROUP_LENGTHS.length) {
            cummulativeLength += GUID_GROUP_LENGTHS[groupIndex];
            while (charIndex < cummulativeLength) {
                if (charIndex == input.length()) {
                    guid.append(NATIVE_ID_SEPARATOR);
                } else if (charIndex > input.length()) {
                    guid.append(0);
                } else {
                    guid.append(input.charAt(charIndex));
                }
                charIndex++;
            }

            groupIndex++;
            if (groupIndex < GUID_GROUP_LENGTHS.length) {
                guid.append('-');
            }
        }

        /*
         * Assumption: Any native id, in our system, will not be greater than length 12. Hence, we can
         * perform a blind replace at the end without bothering about spoiling the beauty of GUID.
         */
        if (!entityIdPrefix.equals("LO")) {
            guid.replace(GUID_LENGTH - nativeId.length(), GUID_LENGTH, nativeId);
            return guid.toString();
        }

        return replaceCharsWithZeroInLastGroup(guid, nativeId);
    }

    private static String replaceCharsWithZeroInLastGroup(StringBuilder guid, String nativeId) {
        for (int i = guid.length() - 1; i >= 0; i--) {
            if (guid.charAt(i) == '-') {
                break;
            }
            guid.setCharAt(i, '0');
        }
        guid.replace(GUID_LENGTH - nativeId.length(), GUID_LENGTH, nativeId);
        return guid.toString();
    }

    public static String getGuidWithoutNativeId(String guid) {
        // input : EMwfoMIS-$000-0000-0000-000000000001, output : EMwfoMIS-$000-0000-0000
        return StringUtils.substringBeforeLast(guid, "-");
    }
}
