package com.moveinsync.billingreportservice.exceptions;

import com.moveinsync.billingreportservice.Configurations.UserContextResolver;
import com.moveinsync.http.v2.MisHttpResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.LocaleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ControllerAdvice
public class MisExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(MisCustomException.class)
    public ResponseEntity<MisErrorHttpResponse> handleCustomException(MisCustomException ex, HttpServletRequest request) {
        Locale locale = LocaleUtils.toLocale(UserContextResolver.getCurrentContext().getLocale());
        String localizedMessage = messageSource.getMessage(ex.getMisError().getMessageKey(), ex.getArgs(), locale);
        localizedMessage = replaceUnreplacedPlaceholders(localizedMessage);
        HttpStatus statusCode = ex.getMisError().getErrorType().getStatusCode();
        MisErrorHttpResponse errorResponse = new MisErrorHttpResponse(
                "Error",
                statusCode.value(),
                localizedMessage,
                System.currentTimeMillis(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse,statusCode);
    }

    private String replaceUnreplacedPlaceholders(String message) {
        // Regular expression to match placeholders like {0}, {1}, etc.
        String regex = "\\{\\d+\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);

        // Replace all matches with "_"
        return matcher.replaceAll("_");
    }
}
