package com.moveinsync.billingreportservice.clientservice;

public interface CacheKeyStrategy {
    String generateCacheKey(String contractName);
}
