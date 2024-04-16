package com.moveinsync.billingreportservice.Utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

public class DateUtils {


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
    SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH/mm/ss");
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
}
