package com.moveinsync.billingreportservice.clientservice;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.moveinsync.billingreportservice.Configurations.UserContextResolver;
import com.moveinsync.billingreportservice.constants.Constants;
import com.moveinsync.billingreportservice.dto.VendorResponseDTO;
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
  private final LoadingCache<Integer, List<VendorResponse>> vendorListCached;

  public TripsheetDomainServiceImpl(TripsheetDomainWebClient tripsheetDomainWebClient) {
    this.tripsheetDomainWebClient = tripsheetDomainWebClient;
    this.billingCycleCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS)
            .build(new CacheLoader<String, List<BillingCycleVO>>() {
              @Override
              public List<BillingCycleVO> load(String key) {
                return fetchAllBillingCycles();
              }
            });
    this.vendorListCached = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES)
            .build(new CacheLoader<Integer, List<VendorResponse>>() {
              @Override
              public List<VendorResponse> load(Integer status) {
                return findVendorByStatus(status);
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
            billingCycleId,
            vendorId,
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

  private List<VendorResponse> findVendorByStatus(Integer status) {
    try {
      return tripsheetDomainWebClient.findVendorByStatus(UserContextResolver.getCurrentContext().getBuid(), List.of(status)).getBody();
    }catch (WebClientResponseException ex) {
      logger.error("Failed to get all Vendors by status ", ex);
    }
    return null;
  }

  public List<VendorResponse> findVendorByStatusCached(Integer status) {
    try {
      return vendorListCached.get(status);
    } catch (Exception e) {
    throw new MisCustomException(ReportErrors.UNABLE_TO_FETCH_FROM_CACHE, e);
    }
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
      String entityId = cab.getVendor().getVendorId() + "-" + cab.getCabId();
      String vendorName = cab.getVendor().getVendorName();
      map.put(entityId, vendorName.toUpperCase());
    }
    return map;
  }

  public Map<String, String> cabToVehicleNumberMap() {
    List<CabDTO> cabs = tripsheetDomainWebClient.getAllCabs(UserContextResolver.getCurrentContext().getBuid()).getBody();
    Map<String, String> map = new HashMap<>();
    for (CabDTO cab : cabs) {
      String entityId = cab.getVendor().getVendorId() + "-" + cab.getCabId();
      String vehicleNumber = cab.getVendor().getVendorName() + "-" + cab.getVendor().getVendorId() + " (" + cab.getRegistration() + ")";
      map.put(entityId, vehicleNumber);
    }
    return map;
  }

  public Map<String, Integer> cabIdToIdentifierMap() {
    List<CabDTO> cabs = tripsheetDomainWebClient.getAllCabs(UserContextResolver.getCurrentContext().getBuid()).getBody();
    Map<String, Integer> map = new HashMap<>();
    for (CabDTO cab : cabs) {
      String key = cab.getVendor().getVendorId() + "-" + cab.getCabId();
      Integer value = cab.getId();
      map.put(key, value);
    }
    return map;
  }

  public List<CabSignInResponseDTO> billingDuties(
          Long startDate, Long endDate, Integer cabId, Integer state, Integer status, Boolean audited
  ) {
    try {
      return tripsheetDomainWebClient.billingDuties(
              UserContextResolver.getCurrentContext().getBuid(),
              startDate, endDate, cabId, state, status, audited
      ).getBody();
    } catch (WebClientResponseException webClientResponseException) {
      logger.error("API Call Failed to get Billing duties from Tripsheet domain ", webClientResponseException);
    }
    return new ArrayList<>();
  }

  public VendorResponse findVendorByName(String vendorName) {
    List<VendorResponse> vendorResponses = findVendorByStatusCached(Constants.VENDOR_STATUS_ACTIVE);
    return vendorResponses.stream().filter(e-> vendorName.equals(e.getVendorName())).findFirst().orElse(null);
  }

}
