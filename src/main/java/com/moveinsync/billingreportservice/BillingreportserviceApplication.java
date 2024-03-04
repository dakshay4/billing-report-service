package com.moveinsync.billingreportservice;


import com.moveinsync.processing.CircuitBreakerBuilder;
import com.moveinsync.processing.ClientBuilder;
import com.moveinsync.tripsheetdomain.client.TripsheetDomainClient;
import com.moveinsync.tripsheetdomain.client.TripsheetDomainWebClient;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(
//		exclude = { SecurityAutoConfiguration.class }

)
public class BillingreportserviceApplication {


	@Value("${els_url}")
	private String elsUrl;

	@Value("${tripsheetdomain_url}")
	private String tripsheetDomainUrl;


	private static final Logger logger = LoggerFactory.getLogger(BillingreportserviceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(BillingreportserviceApplication.class, args);
	}

}
