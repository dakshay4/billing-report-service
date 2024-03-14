package com.moveinsync.billingreportservice.clientservice;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.moveinsync.billingreportservice.Configurations.UserContextResolver;
import com.moveinsync.billingreportservice.Configurations.WebClientException;
import com.moveinsync.billingreportservice.exceptions.MisCustomException;
import com.moveinsync.billingreportservice.exceptions.ReportErrors;
import com.moveinsync.tripsheetdomain.client.TripsheetDomainWebClient;
import com.moveinsync.tripsheetdomain.models.BillingCycleVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class BillingCycleServiceImpl {

  private static final Logger logger = LoggerFactory.getLogger(BillingCycleServiceImpl.class);
  private static final String API_FETCH_ALL_BILLING_CYCLE = "/bill/cycle/all";
  private final TripsheetDomainWebClient tripsheetDomainWebClient;
  private final LoadingCache<String, List<BillingCycleVO>> cache;

  public BillingCycleServiceImpl(TripsheetDomainWebClient tripsheetDomainWebClient) {
    this.tripsheetDomainWebClient = tripsheetDomainWebClient;
    this.cache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES)
            .build(new CacheLoader<String, List<BillingCycleVO>>() {
              @Override
              public List<BillingCycleVO> load(String key) {
                return fetchAllBillingCycles();
              }
            });
  }

  public List<BillingCycleVO> fetchBillingCyclesCached() {
    try {
      return cache.get(UserContextResolver.getCurrentContext().getBuid());
    } catch (Exception e) {
      throw new MisCustomException(ReportErrors.UNABLE_TO_FETCH_FROM_CACHE, e);
    }
  }

  private List<BillingCycleVO> fetchAllBillingCycles() {
    return tripsheetDomainWebClient.fetchAllBillingCycle(UserContextResolver.getCurrentContext().getBuid()).getBody();
  }
}
