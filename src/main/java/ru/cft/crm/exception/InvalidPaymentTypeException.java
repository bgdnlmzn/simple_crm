package ru.cft.crm.exception;

public class InvalidPaymentTypeException extends RuntimeException {
    public InvalidPaymentTypeException(String message) {
        super(message);
    }
}
