package com.moveinsync.billingreportservice.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    private static final String DATE_FORMAT_1 = "yyyy-MM-dd";

    public static String formatDate(String reqDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH/mm/ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat(DATE_FORMAT_1);
        String formattedDate = "";
        try {
            Date date = inputFormat.parse(reqDate);
            formattedDate = outputFormat.format(date);
        } catch (Exception e) {

        }
        return formattedDate;
    }
}
