package com.moveinsync.billingreportservice.Configurations;

public class WebClientException extends RuntimeException{

    private final int statusCode;
    private final String responseBody;

    public WebClientException(int statusCode, String responseBody) {
        super("HTTP status code: " + statusCode);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
