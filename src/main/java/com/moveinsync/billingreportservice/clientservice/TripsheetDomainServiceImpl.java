package com.moveinsync.billingreportservice.clientservice;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.moveinsync.billingreportservice.Configurations.UserContextResolver;
import com.moveinsync.billingreportservice.exceptions.MisCustomException;
import com.moveinsync.billingreportservice.exceptions.ReportErrors;
import com.moveinsync.tripsheetdomain.client.TripsheetDomainWebClient;
import com.moveinsync.tripsheetdomain.models.BillingCycleVO;
import com.moveinsync.tripsheetdomain.response.VendorBillingFrozenStatusDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class TripsheetDomainServiceImpl {

  private static final Logger logger = LoggerFactory.getLogger(TripsheetDomainServiceImpl.class);
  private static final String API_FETCH_ALL_BILLING_CYCLE = "/bill/cycle/all";
  private final TripsheetDomainWebClient tripsheetDomainWebClient;
  private final LoadingCache<String, List<BillingCycleVO>> billingCycleCache;

  public TripsheetDomainServiceImpl(TripsheetDomainWebClient tripsheetDomainWebClient) {
    this.tripsheetDomainWebClient = tripsheetDomainWebClient;
    this.billingCycleCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS)
            .build(new CacheLoader<String, List<BillingCycleVO>>() {
              @Override
              public List<BillingCycleVO> load(String key) {
                return fetchAllBillingCycles();
              }
            });
  }

  public List<BillingCycleVO> fetchBillingCyclesCached() {
    try {
      return billingCycleCache.get(UserContextResolver.getCurrentContext().getBuid());
    } catch (Exception e) {
      throw new MisCustomException(ReportErrors.UNABLE_TO_FETCH_FROM_CACHE, e);
    }
  }

  public BillingCycleVO fetchBillingCycle(Date cycleStart, Date cycleEnd) {
    try {
      return fetchBillingCyclesCached().stream()
          .filter(billCycle -> billCycle.getStartDate().equals(cycleStart) && billCycle.getEndDate().equals(cycleEnd))
          .findFirst().orElse(null);
    } catch (Exception e) {
      throw new MisCustomException(ReportErrors.UNABLE_TO_FETCH_FROM_CACHE, e);
    }
  }

  private List<BillingCycleVO> fetchAllBillingCycles() {
    return tripsheetDomainWebClient.fetchAllBillingCycle(UserContextResolver.getCurrentContext().getBuid()).getBody();
  }

  public VendorBillingFrozenStatusDTO findVendorBillingFrozenStatusById(Integer billingCycleId, Integer vendorId) {
    return tripsheetDomainWebClient
        .getVendorBillingFrozenStatusById(UserContextResolver.getCurrentContext().getBuid(), billingCycleId, vendorId)
        .getBody();
  }
}
