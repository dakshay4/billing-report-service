package com.moveinsync.billingreportservice.clientservice;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.moveinsync.billing.model.ContractVO;
import com.moveinsync.billingreportservice.Configurations.UserContextResolver;
import com.moveinsync.billingreportservice.Configurations.WebClientException;
import com.moveinsync.billingreportservice.Utils.DateUtils;
import org.joda.time.LocalDate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


@Component
public class BillingCalculationClientImpl {

    private final WebClient billingCalculationClient;
    private final LoadingCache<String, String> cache;


    public BillingCalculationClientImpl(WebClient billingCalculationClient) {
        this.billingCalculationClient = billingCalculationClient;
        this.cache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES)
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String key) {
                        Object[] params = key.split(CacheKeyStrategy.DELIMITER);
                        Date startDate = LocalDate.parse(params[0].toString()).toDate();
                        Date endDate = LocalDate.parse(params[1].toString()).toDate();
                        String buId = params[2].toString();
                        return generateBill(startDate, endDate, buId);
                    }
                });
    }

    private String generateBill(Date startDate, Date endDate, String buId) {
        Mono<String> mono = billingCalculationClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(String.format("/bill-calculation/%s/bill",buId))
                        .queryParam("startDate", DateUtils.formatDate(startDate,"yyyy-MM-dd"))
                        .queryParam("endDate", DateUtils.formatDate(endDate,"yyyy-MM-dd"))
                        .queryParam("callerId", "1").build()
                )
                .retrieve().bodyToMono(String.class);
        String res = mono.block();
        return res;
    }

    public String generateBillCached(Date startDate, Date endDate, String buId) {
        try {
            return cache.get(CacheKeyStrategy.generateCacheKeyWithDelimiter(LocalDate.fromDateFields(startDate), LocalDate.fromDateFields(endDate), buId));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
