package com.moveinsync.billingreportservice.exceptions;



public class MisCustomException extends RuntimeException {


    private final MisError misError;
    private final Object[] args;


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

    public MisError getMisError() {
        return misError;
    }

    public Object[] getArgs() {
        return args;
    }
}
