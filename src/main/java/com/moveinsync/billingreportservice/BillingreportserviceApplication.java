package com.moveinsync.billingreportservice;

import com.moveinsync.billingreportservice.exceptions.ReportErrorSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;

@SpringBootApplication()
public class BillingreportserviceApplication {

  private static final Logger logger = LoggerFactory.getLogger(BillingreportserviceApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(BillingreportserviceApplication.class, args);
  }

  @Bean
  public MessageSource messageSource() {
    return new ReportErrorSource();
  }

}
