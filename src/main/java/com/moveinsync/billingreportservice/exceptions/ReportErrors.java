package com.moveinsync.billingreportservice.exceptions;

public enum ReportErrors implements MisError {

  UNABLE_TO_FETCH_REPORTS(ErrorType.RUNTIME, "UNABLE_TO_FETCH_REPORTS"),
  REPORTING_SERVER_CALL_FAILED(ErrorType.RUNTIME, "REPORTING_SERVER_CALL_FAILED"),
  CONTRACT_SERVER_CALL_FAILED(ErrorType.RUNTIME, "CONTRACT_SERVER_CALL_FAILED"),
  UNABLE_TO_FETCH_FROM_CACHE(ErrorType.RUNTIME, "UNABLE_TO_FETCH_FROM_CACHE"),
  INVALID_REPORT_TYPE(ErrorType.VALIDATION, "INVALID_REPORT_TYPE"),
  OPERATION_NOT_ALLOWED(ErrorType.VALIDATION, "OPERATION_NOT_ALLOWED"),
  VENDOR_AUDIT_NOT_DONE_FOR_BILLING_CYCLE(ErrorType.VALIDATION, "VENDOR_AUDIT_NOT_DONE_FOR_BILLING_CYCLE"),
  NO_PARAMETERS_PROVIDED_FOR_GENERATING_CACHE_KEY(ErrorType.RUNTIME, "No.parameters.provided.for.generating.cache.key");

  private ErrorType errorType;
  private String messageKey;

  ReportErrors(ErrorType errorType, String messageKey) {
    this.errorType = errorType;
    this.messageKey = messageKey;
  }

  public ErrorType getErrorType() {
    return errorType;
  }

  public String getMessageKey() {
    return messageKey;
  }
}
