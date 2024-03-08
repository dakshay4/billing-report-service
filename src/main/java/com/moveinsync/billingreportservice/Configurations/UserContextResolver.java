package com.moveinsync.billingreportservice.Configurations;

import com.moveinsync.billingreportservice.exceptions.MisLocale;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserContextResolver {

    private static final Map<Long, UserContextResolver> contextMap = new ConcurrentHashMap<>();

    private String empGuid;
    private String buid;
    private Long sessionStartTime;
    /**
     * {@link MisLocale}
     */
    private String locale = "en_US";

    private final static Logger logger = LoggerFactory.getLogger(UserContextResolver.class);


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