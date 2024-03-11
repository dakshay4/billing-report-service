package com.moveinsync.billingreportservice.clientservice;

import com.moveinsync.billingreportservice.Configurations.UserContextResolver;
import org.springframework.stereotype.Component;

@Component
public class ContractCacheKeyGenerator implements CacheKeyStrategy {


    @Override
    public String generateCacheKey(String contractName) {
        return UserContextResolver.getCurrentContext().getBuid() + "|" + contractName;
    }
}