package com.moveinsync.billingreportservice.Utils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.moveinsync.billingreportservice.dto.BillingReportRequestDTO;

import java.lang.reflect.Field;

public class DateFormatReader {


    public static String  readDateFormatFromAnnotation(Class klass, String fieldName) {
        Field cycleStartField;
        try {
            cycleStartField = klass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return null;
        }

        // Getting the JsonFormat annotation
        JsonFormat jsonFormatAnnotation = cycleStartField.getAnnotation(JsonFormat.class);
        if (jsonFormatAnnotation != null) {
            // Getting the pattern value from the annotation
            String pattern = jsonFormatAnnotation.pattern();
            return pattern;
        } else {
            System.out.println("No @JsonFormat annotation found on cycleStart field.");
        }
        return null;
    }
}
