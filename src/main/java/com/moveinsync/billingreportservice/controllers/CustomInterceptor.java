package com.moveinsync.billingreportservice.controllers;

import com.moveinsync.billingreportservice.Configurations.UserContextResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class CustomInterceptor implements HandlerInterceptor {


    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Long startTime;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LocalDateTime reqStartTime = LocalDateTime.now();
        logger.info("Request Start Time {} " + reqStartTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss:n a")));
        logger.info("Start Time Consumed {} " + reqStartTime.toInstant(ZoneOffset.UTC).toEpochMilli());
        this.startTime = reqStartTime.toEpochSecond(ZoneOffset.UTC);
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        LocalDateTime reqEndTime = LocalDateTime.now();
        logger.info("Request ENd Time {} " + reqEndTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss:n a")));
        logger.info("End Time Consumed {} " + reqEndTime.toInstant(ZoneOffset.UTC).toEpochMilli());
        long netTime = reqEndTime.toEpochSecond(ZoneOffset.UTC) - this.startTime;
        logger.info("Net Time Consumed {} " + netTime);
        UserContextResolver.clear();
    }

}
