package com.pierre.serialnumbersgenerator.exception;

public class SetNotReadyException extends RuntimeException {

    public SetNotReadyException() {

        super("The Requested Set is Under Construction, Please Try Again Later");
    }
}
