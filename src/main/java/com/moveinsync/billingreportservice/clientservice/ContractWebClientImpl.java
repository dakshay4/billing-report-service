package com.moveinsync.billingreportservice.clientservice;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.moveinsync.billing.model.BillingStatusVO;
import com.moveinsync.billing.model.ContractVO;
import com.moveinsync.billingreportservice.configurations.UserContextResolver;
import com.moveinsync.billingreportservice.configurations.WebClientException;
import com.moveinsync.billingreportservice.constants.Constants;
import com.moveinsync.billingreportservice.exceptions.MisCustomException;
import com.moveinsync.billingreportservice.exceptions.ReportErrors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import reactor.core.publisher.Mono;

@Component
public class ContractWebClientImpl {

  private static final Logger logger = LoggerFactory.getLogger(ContractWebClientImpl.class);
  private final WebClient contractClient;
  private final LoadingCache<String, Optional<ContractVO>> cache;

  private static final String API_CONTRACT_GET_LIST = "/api/contracts/";
  private static final String API_BILLING_STATUS_ALL = "/api/billingStatus/";

  public ContractWebClientImpl(WebClient contractClient) {
    this.contractClient = contractClient;
    this.cache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES)
        .build(new CacheLoader<String, Optional<ContractVO>>() {
          @Override
          public Optional<ContractVO> load(String key) {
            Object[] params = key.split(CacheKeyStrategy.DELIMITER);
            String buId = params[0].toString();
            String contractName = params[1].toString();
            return getContractByName(buId, contractName);
          }
        });
  }

  public Optional<ContractVO> getContractByName(String buId, String contractName) {
    Mono<List<ContractVO>> mono = contractClient.get()
        .uri(uriBuilder -> uriBuilder.path(API_CONTRACT_GET_LIST).queryParam("name", contractName)
            .queryParam("buid", buId).build())
        .retrieve().bodyToMono(new ParameterizedTypeReference<List<ContractVO>>() {
        });
    mono.subscribe(response -> {}, error -> {
      if (error instanceof WebClientException) {
        WebClientException webClientException = (WebClientException) error;
        throw new WebClientException(webClientException.getStatusCode(), webClientException.getResponseBody());
      } else {
        logger.info("Error while Fetching Contract {} is : {}",contractName, error.getMessage());
      }
    });
    List<ContractVO> contracts = mono.block();
    if (contracts == null || contracts.isEmpty())
      return Optional.empty();
    else
      return Optional.of(contracts.get(0));
  }

  public Optional<ContractVO> getContract(String contractName) {
    try {
      return cache.get(CacheKeyStrategy.generateCacheKeyWithDelimiter(
              UserContextResolver.getCurrentContext().getBuid(),
              contractName));
    } catch (Exception e) {
      throw new MisCustomException(ReportErrors.UNABLE_TO_FETCH_FROM_CONTRACT, e, contractName);
    }
  }

  public List<BillingStatusVO> getBillingStatus(LocalDate startDate, LocalDate endDate) {
    Mono<List<BillingStatusVO>> mono = contractClient.get()
        .uri(uriBuilder -> uriBuilder.path(API_BILLING_STATUS_ALL).pathSegment("{buId}")
            .queryParam(Constants.START_DATE_STR,
                String.valueOf(startDate.atStartOfDay(ZoneId.of("Asia/Kolkata")).toInstant().toEpochMilli()))
            .queryParam(Constants.END_DATE_STR,
                String.valueOf(endDate.atStartOfDay(ZoneId.of("Asia/Kolkata")).toInstant().toEpochMilli()))
            .build(UserContextResolver.getCurrentContext().getBuid()))
        .retrieve().bodyToMono(new ParameterizedTypeReference<List<BillingStatusVO>>() {
        });
    mono.subscribe(response -> logger.info("Response: {}", response), error -> {
      if (error instanceof WebClientException) {
        WebClientException webClientException = (WebClientException) error;
        throw new WebClientException(webClientException.getStatusCode(), webClientException.getResponseBody());
      } else {
        logger.info("Error: {}", error.getMessage());
      }
    });
    return mono.block();
  }
}
