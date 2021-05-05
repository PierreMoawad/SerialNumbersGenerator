package com.pierre.serialnumbersgenerator.exception;

import java.io.FileNotFoundException;

public class SetNotFoundException extends FileNotFoundException {

    public SetNotFoundException() {

        super("The Requested Set Doesn't Exist");
    }
}
