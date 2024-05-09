package com.moveinsync.billingreportservice.Utils;

import com.moveinsync.billingreportservice.Configurations.UserContextResolver;
import com.moveinsync.billingreportservice.constants.Constants;
import com.moveinsync.billingreportservice.enums.DateFormatPattern;
import com.moveinsync.billingreportservice.exceptions.MisLocale;
import com.moveinsync.timezone.MisTimeZoneUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;

public class DateUtils {


    private final static Logger logger = LoggerFactory.getLogger(DateUtils.class);

    public static LocalDate parse(String dateString) {
        for (DateFormatPattern format : DateFormatPattern.values()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format.getPattern(), Locale.forLanguageTag(UserContextResolver.getCurrentContext().getLocale()));
                return LocalDate.parse(dateString, formatter);
            } catch (Exception e) {
                logger.error("Failed to parse string date {} to the formats available", dateString);
            }
        }
        return null;
    }

    public static String formatDate(String reqDate, String requiredDateFormat) {
        SimpleDateFormat inputFormat = new SimpleDateFormat(DateFormatPattern.ETS_DATE_TIME_FORMAT.getPattern());
        SimpleDateFormat outputFormat = new SimpleDateFormat(requiredDateFormat);
        String formattedDate = "";
        try {
            Date date = inputFormat.parse(reqDate);
            formattedDate = outputFormat.format(date);
        } catch (Exception e) {
            logger.error("Failed to format string date {} to the Required format from the date format {}", reqDate, DateFormatPattern.ETS_DATE_TIME_FORMAT.getPattern());
        }
        return formattedDate;
    }

    public static String formatDate(Date reqDate, String requiredDateFormat) {
        SimpleDateFormat outputFormat = new SimpleDateFormat(requiredDateFormat);
        String formattedDate = "";
        try {
            formattedDate = outputFormat.format(reqDate);
        } catch (Exception e) {
            logger.error("Failed to format Util Date date {} to the Required Format", reqDate);
        }
        return formattedDate;
    }

    public static String formatDate(LocalDateTime reqDate, String requiredDateFormat) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(requiredDateFormat);
        String formattedDate = "";
        try {
            formattedDate = reqDate.format(formatter);
        } catch (Exception e) {
            logger.error("Failed to format LocalDateTime date {} to the Required Format", reqDate);
        }
        return formattedDate;
    }


    public static String formatDate(LocalDate reqDate, String requiredDateFormat) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(requiredDateFormat);
        String formattedDate = "";
        try {
            formattedDate = reqDate.format(formatter);
        } catch (Exception e) {
            logger.error("Failed to format LocalDate date {} to the Required Format", reqDate);
        }
        return formattedDate;
    }


    public static Date convert(String reqDate, SimpleDateFormat format) {
        try {
            return format.parse(reqDate);
        } catch (Exception e) {
            logger.error("Failed to format String date {} to the Format {}", reqDate, format);
        }
        return null;
    }

    public static long getEpochFromDate(Date date) {
        long epochMillis = MisTimeZoneUtils.getEpochFromDate(date, UserContextResolver.getCurrentContext().getBuid());
        logger.info("Epoch Millis {} from date is {} ", epochMillis, date);
        return epochMillis;
    }
}
