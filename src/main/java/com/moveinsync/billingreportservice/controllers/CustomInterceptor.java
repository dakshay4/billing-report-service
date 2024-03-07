package com.moveinsync.billingreportservice.controllers;

import com.moveinsync.billingreportservice.Configurations.UserContextResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class CustomInterceptor implements HandlerInterceptor {


    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LocalDateTime reqStartTime = LocalDateTime.now();
        UserContextResolver.getCurrentContext().setSessionStartTime(reqStartTime.toInstant(ZoneOffset.UTC).toEpochMilli());
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        LocalDateTime reqEndTime = LocalDateTime.now();
        long netTime = reqEndTime.toInstant(ZoneOffset.UTC).toEpochMilli() - UserContextResolver.getCurrentContext().getSessionStartTime();
        logger.info("API Path with Latency is, {}, {}MS ", request.getServletPath(), netTime);
        UserContextResolver.clear();
    }

}
