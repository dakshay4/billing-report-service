package com.moveinsync.billingreportservice.Utils;

import com.moveinsync.billingreportservice.Configurations.UserContextResolver;
import com.moveinsync.billingreportservice.constants.Constants;
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


    private static final String[] DATE_FORMATS = {
            // Standard date formats
            "yyyy-MM-dd",
            "dd/MM/yyyy",
            "MM/dd/yyyy",
            "dd.MM.yyyy",
            "yyyy/MM/dd",

            "dd-MMM-yyyy",
            "dd-MM-yyyy",
            "MMM-dd-yyyy",
            "MMM/dd/yyyy",
            "dd/MMM/yyyy",
            "MMM dd, yyyy",
            "dd MMM yyyy",
            "MMM yyyy",

            // Date formats with full month names
            "dd-MMMM-yyyy",
            "MMMM-dd-yyyy",
            "MMMM/dd/yyyy",
            "dd/MMMM/yyyy",
            "MMMM dd, yyyy",
            "dd MMMM yyyy",
            "MMMM yyyy",

            // Date formats with weekday names
            "EEEE, dd MMMM yyyy",
            "EEEE, MMMM dd, yyyy",
            Constants.ETS_DATE_TIME_FORMAT
    };

    private static final String[] DATE_TIME_FORMATS = {
            "yyyy-MM-dd HH:mm:ss",
            "dd/MM/yyyy HH:mm:ss",
            "MM/dd/yyyy HH:mm:ss",
            "dd.MM.yyyy HH:mm:ss",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss.SSS",
            "dd/MM/yyyy HH:mm:ss.SSS",
            "MM/dd/yyyy HH:mm:ss.SSS",
            "dd.MM.yyyy HH:mm:ss.SSS",
            "yyyy/MM/dd HH:mm:ss.SSS",
            // Date formats with time zone
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ssXXX",
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS",
            Constants.ETS_DATE_TIME_FORMAT
    };

    private final static Logger logger = LoggerFactory.getLogger(DateUtils.class);

    public static LocalDate parse(String dateString) {
        for (String format : DATE_FORMATS) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format, Locale.forLanguageTag(UserContextResolver.getCurrentContext().getLocale()));
                return LocalDate.parse(dateString, formatter);
            } catch (Exception e) {
                logger.error("Failed to parse string date {} to the formats available", dateString);
            }
        }
        return null;
    }

    public static String formatDate(String reqDate, String requiredDateFormat) {
        SimpleDateFormat inputFormat = new SimpleDateFormat(Constants.ETS_DATE_TIME_FORMAT);
        SimpleDateFormat outputFormat = new SimpleDateFormat(requiredDateFormat);
        String formattedDate = "";
        try {
            Date date = inputFormat.parse(reqDate);
            formattedDate = outputFormat.format(date);
        } catch (Exception e) {
            logger.error("Failed to format string date {} to the Required format from the date format {}", reqDate, Constants.ETS_DATE_TIME_FORMAT);
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
