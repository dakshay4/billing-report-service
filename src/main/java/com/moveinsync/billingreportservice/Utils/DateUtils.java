package com.moveinsync.billingreportservice.Utils;

import com.moveinsync.billingreportservice.Configurations.UserContextResolver;
import com.moveinsync.billingreportservice.exceptions.MisLocale;

import java.text.SimpleDateFormat;
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

          // Standard date-time formats
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

          // Date formats with abbreviated month names
          "dd-MMM-yyyy",
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

          // Date formats with time zone
          "yyyy-MM-dd'T'HH:mm:ss'Z'",
          "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
          "yyyy-MM-dd'T'HH:mm:ssXXX",
          "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
          "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
          "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS",

          "dd/MM/yyyy HH/mm/ss"
  };

  public static LocalDateTime parse(String dateString) {
    for (String format : DATE_FORMATS) {
      try {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format, Locale.forLanguageTag(UserContextResolver.getCurrentContext().getLocale()));
        return LocalDateTime.parse(dateString, formatter);
      } catch (Exception e) { }
    }
    return null;
  }

  public static String formatDate(String reqDate, String requiredDateFormat) {
    SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH/mm/ss");
    SimpleDateFormat outputFormat = new SimpleDateFormat(requiredDateFormat);
    String formattedDate = "";
    try {
      Date date = inputFormat.parse(reqDate);
      formattedDate = outputFormat.format(date);
    } catch (Exception e) {

    }
    return formattedDate;
  }

  public static String formatDate(Date reqDate, String requiredDateFormat) {
    SimpleDateFormat outputFormat = new SimpleDateFormat(requiredDateFormat);
    String formattedDate = "";
    try {
      formattedDate = outputFormat.format(reqDate);
    } catch (Exception e) {

    }
    return formattedDate;
  }

  public static String formatDate(LocalDateTime reqDate, String requiredDateFormat) {
    SimpleDateFormat outputFormat = new SimpleDateFormat(requiredDateFormat);
    String formattedDate = "";
    try {
      formattedDate = outputFormat.format(reqDate);
    } catch (Exception e) {

    }
    return formattedDate;
  }


  public static Date convert(String reqDate, SimpleDateFormat format) {
    try {
      Date date = format.parse(reqDate);
      return date;
    } catch (Exception e) {

    }
    return null;
  }

  public static long getEpochFromDate(Date date) {
    LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    return localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
  }

  public static Long getEpochFromDate(LocalDateTime startDate) {
    return startDate.toInstant(ZoneOffset.UTC).toEpochMilli();
  }
}
