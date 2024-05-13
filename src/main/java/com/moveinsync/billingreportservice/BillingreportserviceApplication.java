package com.moveinsync.billingreportservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moveinsync.billingreportservice.exceptions.ReportErrorSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BillingreportserviceApplication {


  public static void main(String[] args) {
    SpringApplication.run(BillingreportserviceApplication.class, args);
  }

  @Bean
  public MessageSource messageSource() {
    return new ReportErrorSource();
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

}
