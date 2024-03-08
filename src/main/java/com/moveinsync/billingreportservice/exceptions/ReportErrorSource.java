package com.moveinsync.billingreportservice.exceptions;

import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

public class ReportErrorSource extends ResourceBundleMessageSource {

    private final static String service_baseName = "reportErrors";

    public ReportErrorSource() {
        setBasename(getBaseName());
        setDefaultEncoding("UTF-8");
    }

    private String getBaseName() {
        return "error_" + service_baseName ;
    }

    @Override
    protected String getMessageInternal(String key, Object[] args, Locale locale) {
        return super.getMessageInternal(key, args, locale);
    }
}
