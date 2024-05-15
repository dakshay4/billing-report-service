package com.moveinsync.billingreportservice.utils;

import com.moveinsync.billingreportservice.configurations.UserContextResolver;
import com.moveinsync.billingreportservice.enums.DateFormatPattern;
import com.moveinsync.timezone.MisTimeZoneUtils;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;


@UtilityClass
public class DateUtils {


    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

    public static LocalDate parse(String dateString) {
        for (DateFormatPattern format : DateFormatPattern.values()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format.getPattern(), Locale.forLanguageTag(UserContextResolver.getCurrentContext().getLocale()));
                return LocalDate.parse(dateString, formatter);
            } catch (Exception ignored) {
                // Formatter unable to parse the format for the dateString, it will try in loop other formats, and is success, return otherwise log.
            }
        }
        logger.error("Failed to parse string date {} to the formats available", dateString);
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
