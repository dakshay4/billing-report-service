package com.moveinsync.billingreportservice.exceptions;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

@Configuration
public class ReportErrorSource extends ResourceBundleMessageSource {

  public ReportErrorSource() {
    setBasename(getBaseName());
    setDefaultEncoding("UTF-8");
  }

  private final static String service_baseName = "reportErrors";

  private String getBaseName() {
    return "error_" + service_baseName;
  }

}
