package com.moveinsync.billingreportservice.clientservice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.moveinsync.billing.model.ContractVO;
import com.moveinsync.billingreportservice.Configurations.UserContextResolver;
import com.moveinsync.billingreportservice.Configurations.WebClientException;
import com.moveinsync.billingreportservice.exceptions.MisCustomException;
import com.moveinsync.billingreportservice.exceptions.MisError;
import com.moveinsync.billingreportservice.exceptions.ReportErrors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class ContractWebClientImpl {

    private final static String API_CONTRACT_GET_LIST="/api/contracts";

    private final WebClient contractClient;

    private final CacheKeyStrategy cacheKeyStrategy;

    private final LoadingCache<String, ContractVO> cache;

    private static final Logger logger = LoggerFactory.getLogger(ContractWebClientImpl.class);

    public ContractWebClientImpl(WebClient contractClient) {
        this.contractClient = contractClient;
        this.cacheKeyStrategy = new ContractCacheKeyGenerator();
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(new CacheLoader<String, ContractVO>() {
                    @Override
                    public ContractVO load(String key) {
                        String[] parts = key.split("\\|", 2);
                        String contractName = parts[1];
                        return getContractByName(contractName);
                    }
                });
    }

    public ContractVO getContractByName(String contractName) {
        Mono<List<ContractVO>> mono = contractClient.get().uri(
                uriBuilder ->
                    uriBuilder.path(API_CONTRACT_GET_LIST)
                            .queryParam("name", contractName)
                            .queryParam("buid", UserContextResolver.getCurrentContext().getBuid()).build()
                ).retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ContractVO>>() {
                });
        mono.subscribe(
                response ->
                        logger.info("Response: {}", response),
                error -> {
                    if (error instanceof WebClientException) {
                        WebClientException webClientException = (WebClientException) error;
                        throw new WebClientException(webClientException.getStatusCode(), webClientException.getResponseBody());
                    } else {
                        logger.info("Error: {}", error.getMessage());
                    }
                });
       List<ContractVO> contracts = mono.block();
       if(contracts == null || contracts.isEmpty()) return null;
       else return contracts.get(0);
    }

    public ContractVO getContract(String contractName) {
        try {
            return cache.get(cacheKeyStrategy.generateCacheKey(contractName));
        } catch (Exception e) {
            throw new MisCustomException(ReportErrors.UNABLE_TO_FETCH_FROM_CACHE, e);
        }
    }
}
