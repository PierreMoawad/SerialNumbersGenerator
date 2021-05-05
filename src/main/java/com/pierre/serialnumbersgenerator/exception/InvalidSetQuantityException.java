package com.pierre.serialnumbersgenerator.exception;

public class InvalidSetQuantityException extends IllegalArgumentException {

    public InvalidSetQuantityException(Integer minQuantity, Integer maxQuantity) {

        super(String.format("Quantity Must Be Between [%d-%d]", minQuantity, maxQuantity));
    }
}
