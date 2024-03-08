package com.moveinsync.billingreportservice.exceptions;

public enum ReportErrors implements MisError{

    UNABLE_TO_FETCH_REPORTS(ErrorType.RUNTIME, "UNABLE_TO_FETCH_REPORTS"),
    REPORTING_SERVER_CALL_FAILED(ErrorType.RUNTIME, "REPORTING_SERVER_CALL_FAILED"),
    CONTRACT_SERVER_CALL_FAILED(ErrorType.RUNTIME, "CONTRACT_SERVER_CALL_FAILED");

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
