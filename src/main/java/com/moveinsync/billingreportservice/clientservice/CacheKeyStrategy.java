package com.moveinsync.billingreportservice.clientservice;

import com.moveinsync.billingreportservice.exceptions.MisCustomException;
import com.moveinsync.billingreportservice.exceptions.MisError;
import com.moveinsync.billingreportservice.exceptions.ReportErrors;

public class CacheKeyStrategy {

  public static final String DELIMITER = "#";

  public static String generateCacheKeyWithDelimiter(Object... params) {
    if (params == null || params.length == 0) {
      throw new MisCustomException(ReportErrors.NO_PARAMETERS_PROVIDED_FOR_GENERATING_CACHE_KEY);
    }

    StringBuilder keyBuilder = new StringBuilder();
    for (Object param : params) {
      keyBuilder.append(param).append(DELIMITER);
    }
    // Remove the trailing '|'
    keyBuilder.deleteCharAt(keyBuilder.length() - 1);
    return keyBuilder.toString();
  }
}
