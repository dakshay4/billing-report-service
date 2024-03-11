package com.moveinsync.billingreportservice.exceptions;

public enum ReportErrors implements MisError{

    UNABLE_TO_FETCH_REPORTS(ErrorType.RUNTIME, "UNABLE_TO_FETCH_REPORTS"),
    REPORTING_SERVER_CALL_FAILED(ErrorType.RUNTIME, "REPORTING_SERVER_CALL_FAILED"),
    CONTRACT_SERVER_CALL_FAILED(ErrorType.RUNTIME, "CONTRACT_SERVER_CALL_FAILED"),
    UNABLE_TO_FETCH_FROM_CACHE(ErrorType.RUNTIME, "UNABLE_TO_FETCH_FROM_CACHE"),
    INVALID_REPORT_TYPE(ErrorType.VALIDATION,"INVALID_REPORT_TYPE" );

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
