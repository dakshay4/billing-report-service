package com.moveinsync.billingreportservice.exceptions;

public class MisCustomException extends RuntimeException {

  private final MisError misError;
  private Object[] args;

  public MisCustomException(MisError error) {
    super();
    this.misError = error;
    this.args = null;
  }

  public MisCustomException(MisError misError, Object[] args) {
    super(misError.getMessageKey());
    this.misError = misError;
    this.args = args;
  }

  public MisCustomException(MisError misError, Throwable cause) {
    super(misError.getMessageKey(), cause);
    this.misError = misError;
  }

  public MisCustomException(MisError misError, Object[] args, Throwable cause) {
    super(misError.getMessageKey(), cause);
    this.misError = misError;
    this.args = args;
  }

  public MisError getMisError() {
    return misError;
  }

  public Object[] getArgs() {
    return args;
  }
}
