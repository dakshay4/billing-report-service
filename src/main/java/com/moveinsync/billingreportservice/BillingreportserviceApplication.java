package com.moveinsync.billingreportservice;


import com.moveinsync.billingreportservice.exceptions.ReportErrorSource;
import com.moveinsync.processing.CircuitBreakerBuilder;
import com.moveinsync.processing.ClientBuilder;
import com.moveinsync.tripsheetdomain.client.TripsheetDomainClient;
import com.moveinsync.tripsheetdomain.client.TripsheetDomainWebClient;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;

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
