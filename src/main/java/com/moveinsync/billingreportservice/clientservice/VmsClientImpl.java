package com.moveinsync.billingreportservice.clientservice;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.moveinsync.billingreportservice.configurations.UserContextResolver;
import com.moveinsync.billingreportservice.dto.VendorResponseDTO;
import com.moveinsync.billingreportservice.exceptions.MisCustomException;
import com.moveinsync.billingreportservice.exceptions.ReportErrors;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class VmsClientImpl {

    private static final String LIST_ALL_VENDOR_OF_BU_API = "vendors/bu/";
    private static final String LIST_VENDOR_OF_BU_FROM_EMP_GUID = "vendors/id/";
    private final WebClient vmsClient;
    private final LoadingCache<String, Optional<VendorResponseDTO>> vendorByEmpGuidCacheCache;
    private final LoadingCache<String, Optional<VendorResponseDTO>> vendorByVendorNameCache;


    public VmsClientImpl(WebClient vmsClient) {
        this.vmsClient = vmsClient;
        this.vendorByEmpGuidCacheCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS)
                .build(new CacheLoader<String, Optional<VendorResponseDTO>>() {
                    @Override
                    public Optional<VendorResponseDTO> load(String empGuid) {
                        return fetchVendorByEmpGuId(empGuid);
                    }
                });
        this.vendorByVendorNameCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS)
                .build(new CacheLoader<String, Optional<VendorResponseDTO>>() {
                    @Override
                    public Optional<VendorResponseDTO> load(String vendorName) {
                        return fetchVendorByVendorName(vendorName);
                    }
                });
    }

    private Optional<VendorResponseDTO> fetchVendorByVendorName(String vendorName) {
        List<VendorResponseDTO> res =  vmsClient.get().uri(LIST_ALL_VENDOR_OF_BU_API + UserContextResolver.getCurrentContext().getBuid()).retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<VendorResponseDTO>>() {}).block();
        return res!=null ?
                res.parallelStream().filter(e->vendorName.equalsIgnoreCase(e.getVendorName())).findFirst() :
                Optional.empty();
    }

    private Optional<VendorResponseDTO> fetchVendorByEmpGuId(String empGuid) {
        VendorResponseDTO responseDTO = vmsClient.get().uri(LIST_VENDOR_OF_BU_FROM_EMP_GUID + empGuid).retrieve()
                .bodyToMono(VendorResponseDTO.class).block();
        return responseDTO!=null ? Optional.of(responseDTO) : Optional.empty();
    }


    public VendorResponseDTO fetchVendorByVendorNameCached(String vendorName) {
        try {
            Optional<VendorResponseDTO> res = vendorByVendorNameCache.get(vendorName);
            return res.orElse(null);
        } catch (Exception e) {
            throw new MisCustomException(ReportErrors.UNABLE_TO_FETCH_FROM_CACHE, e);
        }
    }

    public VendorResponseDTO fetchVendorByEmpGuIdCached(String empGuid) {
        try {
            Optional<VendorResponseDTO> res = vendorByEmpGuidCacheCache.get(empGuid);
            return res.orElse(null);
        }catch (WebClientResponseException | WebClientRequestException ex) {
            throw new MisCustomException(ReportErrors.CLIENT_ERROR, ex);
        } catch (Exception e) {
            throw new MisCustomException(ReportErrors.UNABLE_TO_FETCH_FROM_CACHE, e);
        }
    }
}
