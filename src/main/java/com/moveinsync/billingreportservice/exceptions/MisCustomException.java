package com.moveinsync.billingreportservice.exceptions;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Getter
public class MisCustomException extends RuntimeException implements Serializable{

  @Serial
  private static final long serialVersionUID = 3882501414944821995L;

  private final transient MisError misError;
  private final transient Object[] args;

  public MisCustomException(MisError error) {
    super();
    this.misError = error;
    this.args = null;
  }

  public MisCustomException(MisError misError, Throwable cause) {
    super(misError.getMessageKey(), cause);
    this.misError = misError;
    this.args = null;
  }

  public MisCustomException(MisError misError, Object[] args, Throwable cause) {
    super(misError.getMessageKey(), cause);
    this.misError = misError;
    this.args = args;
  }

  public MisCustomException(MisError misError, Object ... args) {
    super(misError.getMessageKey());
    this.misError = misError;
    this.args = args;
  }

  public MisCustomException(MisError misError, Throwable cause, Object ... args) {
    super(misError.getMessageKey(), cause);
    this.misError = misError;
    this.args = args;
  }

}
