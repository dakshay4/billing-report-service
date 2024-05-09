package com.moveinsync.billingreportservice.enums;


public enum DateFormatPattern {
    // Standard date formats
    YYYY_MM_DD("yyyy-MM-dd"),
    DD_MM_YYYY("dd/MM/yyyy"),
    MM_DD_YYYY("MM/dd/yyyy"),
    DD_MM_YYYY_DOT("dd.MM.yyyy"),
    YYYY_MM_DD_SLASH("yyyy/MM/dd"),
    DD_MMM_YYYY("dd-MMM-yyyy"),
    DD_MM_YYYY_HYPHEN("dd-MM-yyyy"),
    MMM_DD_YYYY("MMM-dd-yyyy"),
    MMM_DD_YYYY_SLASH("MMM/dd/yyyy"),
    DD_MMM_YYYY_SLASH("dd/MMM/yyyy"),
    MMM_DD_YYYY_COMMA("MMM dd, yyyy"),
    DD_MMM_YYYY_SPACE("dd MMM yyyy"),
    MMM_YYYY("MMM yyyy"),
    // Date formats with full month names
    DD_MMMM_YYYY("dd-MMMM-yyyy"),
    MMMM_DD_YYYY("MMMM-dd-yyyy"),
    MMMM_DD_YYYY_SLASH("MMMM/dd/yyyy"),
    DD_MMMMM_YYYY("dd/MMMM/yyyy"),
    MMMM_DD_YYYY_COMMA("MMMM dd, yyyy"),
    DD_MMMM_YYYY_SPACE("dd MMMM yyyy"),
    MMMM_YYYY("MMMM yyyy"),
    // Date formats with weekday names
    EEEE_DD_MMMM_YYYY("EEEE, dd MMMM yyyy"),
    EEEE_MMMM_DD_YYYY("EEEE, MMMM dd, yyyy"),
    ETS_DATE_TIME_FORMAT("dd/MM/yyyy HH/mm/ss"),
    // Date-time formats
    YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"),
    DD_MM_YYYY_HH_MM_SS("dd/MM/yyyy HH:mm:ss"),
    MM_DD_YYYY_HH_MM_SS("MM/dd/yyyy HH:mm:ss"),
    DD_MM_YYYY_DOT_HH_MM_SS("dd.MM.yyyy HH:mm:ss"),
    YYYY_MM_DD_SLASH_HH_MM_SS("yyyy/MM/dd HH:mm:ss"),
    YYYY_MM_DD_HH_MM_SS_SSS("yyyy-MM-dd HH:mm:ss.SSS"),
    DD_MM_YYYY_HH_MM_SS_SSS("dd/MM/yyyy HH:mm:ss.SSS"),
    MM_DD_YYYY_HH_MM_SS_SSS("MM/dd/yyyy HH:mm:ss.SSS"),
    DD_MM_YYYY_DOT_HH_MM_SS_SSS("dd.MM.yyyy HH:mm:ss.SSS"),
    YYYY_MM_DD_SLASH_HH_MM_SS_SSS("yyyy/MM/dd HH:mm:ss.SSS"),
    YYYY_MM_DD_T_HH_MM_SS_Z("yyyy-MM-dd'T'HH:mm:ss'Z'"),
    YYYY_MM_DD_T_HH_MM_SS_SSS_Z("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
    YYYY_MM_DD_T_HH_MM_SS_XXX("yyyy-MM-dd'T'HH:mm:ssXXX"),
    YYYY_MM_DD_T_HH_MM_SS_SSS_XXX("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"),
    YYYY_MM_DD_T_HH_MM_SS_SSSSSS("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"),
    YYYY_MM_DD_T_HH_MM_SS_SSSSSSSSS("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS");

    private final String pattern;

    DateFormatPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }
}
