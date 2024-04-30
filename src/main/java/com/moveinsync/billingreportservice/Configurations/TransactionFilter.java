package com.moveinsync.billingreportservice.Configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moveinsync.billingreportservice.constants.Constants;
import com.moveinsync.billingreportservice.exceptions.MisCustomException;
import com.moveinsync.billingreportservice.exceptions.MisErrorHttpResponse;
import com.moveinsync.billingreportservice.exceptions.ReportErrors;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class TransactionFilter implements Filter {

  private final Logger LOG = LoggerFactory.getLogger(getClass());

  private final MessageSource messageSource;

  private final ObjectMapper objectMapper;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws ServletException, IOException, MisCustomException {

    try {
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
      if (buid == null)
        throw new MisCustomException(ReportErrors.BUID_NOT_FOUND);
      if (empGuid == null)
        throw new MisCustomException(ReportErrors.EMP_GUID_NOT_FOUND);
      UserContextResolver.getCurrentContext().setEmpGuid(empGuid);
      UserContextResolver.getCurrentContext().setBuid(buid);

      chain.doFilter(request, response);
    } catch (MisCustomException ex) {
      HttpServletResponse httpResponse = (HttpServletResponse) response;
      httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      httpResponse.setContentType(Constants.CONTENT_TYPE_APPLICATION_JSON);
      Locale locale = LocaleUtils.toLocale(UserContextResolver.getCurrentContext().getLocale());
      String localizedMessage = messageSource.getMessage(ex.getMisError().getMessageKey(), ex.getArgs(), locale);
      MisErrorHttpResponse errorResponse = new MisErrorHttpResponse(ex.getMisError().getErrorType().name(), localizedMessage, System.currentTimeMillis(),
              ((HttpServletRequest) request).getRequestURL().toString());
      String errorResponseJson = objectMapper.writeValueAsString(errorResponse);
      httpResponse.getWriter().write(errorResponseJson);
    }
  }
  // other methods
}