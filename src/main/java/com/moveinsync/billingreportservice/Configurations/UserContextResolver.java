package com.moveinsync.billingreportservice.Configurations;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserContextResolver {

    private static final Map<Long, UserContextResolver> contextMap = new ConcurrentHashMap<>();

    private String empGuid;
    private String buid;


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
        contextMap.remove(threadId);
    }
}