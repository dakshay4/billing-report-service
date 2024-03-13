package com.moveinsync.billingreportservice.exceptions;

public interface MisError {

  public ErrorType getErrorType();

  public String getMessageKey();
}
