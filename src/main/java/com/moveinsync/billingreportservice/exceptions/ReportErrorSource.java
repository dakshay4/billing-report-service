package com.moveinsync.billingreportservice.exceptions;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class ReportErrorSource extends ResourceBundleMessageSource {

  public ReportErrorSource() {
    setBasename(getBaseName());
    setDefaultEncoding("UTF-8");
  }

  private static final String SERVICE_BASE_NAME = "reportErrors";

  private String getBaseName() {
    return "error_" + SERVICE_BASE_NAME;
  }

}
