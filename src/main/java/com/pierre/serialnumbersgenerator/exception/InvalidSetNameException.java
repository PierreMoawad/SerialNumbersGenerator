package com.pierre.serialnumbersgenerator.exception;

public class InvalidSetNameException extends IllegalArgumentException {

    public InvalidSetNameException() {

        super("Set Name Already Exists, Please Choose a Unique Name");
    }
}