package com.moveinsync.billingreportservice.Utils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.moveinsync.billingreportservice.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class DateFormatReader {



    private DateFormatReader() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    private final static Logger logger = LoggerFactory.getLogger(DateFormatReader.class);

    /**
     * Reads the date format pattern from a JsonFormat annotation on the specified field in the given class.
     *
     * @param klass     The class containing the field.
     * @param fieldName The name of the field.
     * @return The date format pattern if found, otherwise a default pattern dd/MM/yyyy HH/mm/ss.
     */
    public static String readDateFormatFromAnnotation(final Class<?> klass, final String fieldName) {
        try {
            final Field field = klass.getDeclaredField(fieldName);
            final JsonFormat jsonFormatAnnotation = field.getAnnotation(JsonFormat.class);
            if (jsonFormatAnnotation != null) {
                return jsonFormatAnnotation.pattern();
            }
        } catch (NoSuchFieldException e) {
            logger.error("Field '{}' not found in class '{}'", fieldName, klass.getName(), e);
        } catch (SecurityException e) {
            logger.error("SecurityException while accessing field '{}' in class '{}'", fieldName, klass.getName(), e);
        }
        logger.info("No @JsonFormat annotation found on field: {}", fieldName);
        logger.info("Returning Default format {}", Constants.ETS_DATE_TIME_FORMAT);
        return Constants.ETS_DATE_TIME_FORMAT;
    }
}
