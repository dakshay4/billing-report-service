package com.moveinsync.billingreportservice.Configurations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserContextResolver {

    private static final Map<Long, UserContextResolver> contextMap = new ConcurrentHashMap<>();

    private String empGuid;
    private String buid;

    private final static Logger logger = LoggerFactory.getLogger(UserContextResolver.class);



    public String getEmpGuid() {
        return empGuid;
    }

    public String getBuid() {
        return buid;
    }

    public void setEmpGuid(String empGuid) {
        this.empGuid = empGuid;
    }

    public void setBuid(String buid) {
        this.buid = buid;
    }

    public static UserContextResolver getCurrentContext() {
        long threadId = Thread.currentThread().getId();
        return contextMap.computeIfAbsent(threadId, k -> new UserContextResolver());
    }

    public static void clear() {
        long threadId = Thread.currentThread().getId();
        logger.info("Deleting User Context Session with Thread - {}", threadId);
        contextMap.remove(threadId);
    }
}