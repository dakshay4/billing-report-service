package com.moveinsync.billingreportservice.clientservice;

import com.moveinsync.billingreportservice.Configurations.UserContextResolver;
import com.moveinsync.billingreportservice.Configurations.WebClientException;
import com.moveinsync.tripsheetdomain.models.BillingCycleVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class TripsheetDomainClientImpl {

  private static final Logger logger = LoggerFactory.getLogger(TripsheetDomainClientImpl.class);
  private static final String API_FETCH_ALL_BILLING_CYCLE = "/bill/cycle/all";
  private final WebClient tripsheetDomainClient;

  public TripsheetDomainClientImpl(WebClient tripsheetDomainClient) {
    this.tripsheetDomainClient = tripsheetDomainClient;
  }

  public List<BillingCycleVO> fetchAllBillingCycles() {
    Mono<ResponseEntity<List<BillingCycleVO>>> mono = tripsheetDomainClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(UserContextResolver.getCurrentContext().getBuid() + API_FETCH_ALL_BILLING_CYCLE).build())
        .retrieve().bodyToMono(new ParameterizedTypeReference<ResponseEntity<List<BillingCycleVO>>>() {
        });
    mono.subscribe(response -> logger.info("Response: {}", response), error -> {
      if (error instanceof WebClientException) {
        WebClientException webClientException = (WebClientException) error;
        throw new WebClientException(webClientException.getStatusCode(), webClientException.getResponseBody());
      } else {
        logger.info("Error: {}", error.getMessage());
      }
    });
    return mono.block().getBody();
  }
}
