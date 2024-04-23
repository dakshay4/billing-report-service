package com.moveinsync.billingreportservice.clientservice;

import com.moveinsync.billingreportservice.Utils.DateUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Date;


@Component
public class BillingCalculationClientImpl {

    private final WebClient billingCalculationClient;


    public BillingCalculationClientImpl(WebClient billingCalculationClient) {
        this.billingCalculationClient = billingCalculationClient;
    }

    public String generateBill(Date startDate, Date endDate, String buId) {
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

}
