package com.moveinsync.billingreportservice.Configurations;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TransactionFilter implements Filter {

  private final Logger LOG = LoggerFactory.getLogger(getClass());

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    HttpServletRequest req = (HttpServletRequest) request;
    String empGuid = req.getHeader("empGuid");
    String buid = req.getHeader("buid");
    if (req.getCookies() != null) {
      for (Cookie e : req.getCookies()) {
        if (empGuid == null && "empGuid".equals(e.getName()))
          empGuid = e.getValue();
        if (buid == null && "buid".equals(e.getName()))
          buid = e.getValue();
      }
    }
    UserContextResolver.getCurrentContext().setEmpGuid(empGuid);
    UserContextResolver.getCurrentContext().setBuid(buid);

    chain.doFilter(request, response);
  }

  // other methods
}