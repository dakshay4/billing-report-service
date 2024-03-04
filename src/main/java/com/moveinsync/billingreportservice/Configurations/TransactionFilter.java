package com.moveinsync.billingreportservice.Configurations;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;


@Component
public class TransactionFilter implements Filter {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        HttpServletRequest req = (HttpServletRequest) request;
        Arrays.stream(req.getCookies()).forEach(e->{
            if("empGuid".equals(e.getName())) UserContextResolver.getCurrentContext().setEmpGuid(e.getValue());
            if("buid".equals(e.getName())) UserContextResolver.getCurrentContext().setBuid(e.getValue());
        });

        LOG.info(
                "Starting a transaction for req : {}",
                req.getRequestURI());

        chain.doFilter(request, response);
        LOG.info(
                "Committing a transaction for req : {}",
                req.getRequestURI());
    }

    // other methods
}