package com.moveinsync.billingreportservice.clientservice;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.moveinsync.billingreportservice.Configurations.UserContextResolver;
import com.moveinsync.billingreportservice.exceptions.MisCustomException;
import com.moveinsync.billingreportservice.exceptions.ReportErrors;
import com.moveinsync.data.envers.models.EntityAuditDetails;
import com.moveinsync.models.CabDTO;
import com.moveinsync.tripsheetdomain.client.TripsheetDomainWebClient;
import com.moveinsync.tripsheetdomain.models.BillingCycleVO;
import com.moveinsync.tripsheetdomain.models.VendorResponse;
import com.moveinsync.tripsheetdomain.response.CabSignInResponseDTO;
import com.moveinsync.tripsheetdomain.response.VendorBillingFrozenStatusDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    List<BillingCycleVO> billingCycles = tripsheetDomainWebClient
        .fetchAllBillingCycle(UserContextResolver.getCurrentContext().getBuid()).getBody();
    return billingCycles != null ? billingCycles.stream()
        .sorted((o1, o2) -> o2.getStartDate().compareTo(o1.getStartDate())).collect(Collectors.toList())
        : new ArrayList<>();
  }

  public VendorBillingFrozenStatusDTO findVendorBillingFrozenStatusById(Integer billingCycleId, Integer vendorId) {
    return tripsheetDomainWebClient
        .getVendorBillingFrozenStatusById(UserContextResolver.getCurrentContext().getBuid(), billingCycleId, vendorId)
        .getBody();
  }

  public Map<String, Integer> freezeVendorBilling(int vendorId, Date startDate, Date endDate, boolean freezeStatus) {
    return tripsheetDomainWebClient.freezeVendorBilling(UserContextResolver.getCurrentContext().getBuid(), vendorId,
            startDate.toInstant().toEpochMilli(), endDate.toInstant().toEpochMilli(), freezeStatus).getBody();
  }


  public List<BillingCycleVO> updateFrozen(Date startDate, Date endDate, boolean b) {
     return tripsheetDomainWebClient.updateFrozen(UserContextResolver.getCurrentContext().getBuid(),
             startDate.toInstant().toEpochMilli(),
             endDate.toInstant().toEpochMilli(), false).getBody();

  }

  public VendorBillingFrozenStatusDTO updateVendorBillingFreezeStatus(int vendorId, int billingCycleId, boolean freezeStatus) {
    return tripsheetDomainWebClient.updateVendorBillingFreezeStatus(UserContextResolver.getCurrentContext().getBuid(),
            vendorId,
            billingCycleId,
            freezeStatus).getBody();
  }


  public List<Integer> allCabsIdsOfActiveVendor(int status, Integer vendorId) {
    return tripsheetDomainWebClient.allCabsIdsOfActiveVendor(UserContextResolver.getCurrentContext().getBuid(),
            status,
            vendorId
            ).getBody();
  }

  public Integer getCountBetweenDateAndByAudit(Long startDate, Long endDte, List<Integer> cabIds) {

    return tripsheetDomainWebClient.getCountBetweenDateAndByAudit(UserContextResolver.getCurrentContext().getBuid(),
            startDate, endDte, false, cabIds
            ).getBody();
  }

  public List<VendorResponse> findVendorByStatus(List<Integer> status) {
    return tripsheetDomainWebClient.findVendorByStatus(UserContextResolver.getCurrentContext().getBuid(), status).getBody();
  }

  public List<EntityAuditDetails> getVendorBillingFrozenStatusAuditById(int billingCycleID, Integer id) {
    try {
      return tripsheetDomainWebClient.getVendorBillingFrozenStatusAuditById(
              UserContextResolver.getCurrentContext().getBuid(),
              billingCycleID, id).getBody();
    }catch (WebClientResponseException ex) {
      logger.warn("Client error {}", ex.getResponseBodyAsString());
    }
    return new ArrayList<>();
  }

  public Map<String, String> cabToVendorNameMap() {
    List<CabDTO> cabs = tripsheetDomainWebClient.getAllCabs(UserContextResolver.getCurrentContext().getBuid()).getBody();
    Map<String, String> map = new HashMap<>();
    for (CabDTO cab : cabs) {
      String key = cab.getVendor().getVendorId() + "-" + cab.getCabId();
      String value = cab.getVendor().getVendorName();
      map.put(key, value.toUpperCase());
    }
    return map;
  }

  public Map<String, String> cabToVehicleNumberMap() {
    List<CabDTO> cabs = tripsheetDomainWebClient.getAllCabs(UserContextResolver.getCurrentContext().getBuid()).getBody();
    Map<String, String> map = new HashMap<>();
    for (CabDTO cab : cabs) {
      String key = cab.getVendor().getVendorId() + "-" + cab.getCabId();
      String value = cab.getVendor().getVendorName() + "-" + cab.getVendor().getVendorId() + " (" + cab.getRegistration() + ")";
      map.put(key, value);
    }
    return map;
  }

  public List<CabSignInResponseDTO> billingDuties(
          Long startDate, Long endDate, Integer cabId, Integer state, Integer status, Boolean audited
  ) {
    return tripsheetDomainWebClient.billingDuties(
            UserContextResolver.getCurrentContext().getBuid(),
            startDate, endDate, cabId, state, status, audited
    ).getBody();
  }
}
