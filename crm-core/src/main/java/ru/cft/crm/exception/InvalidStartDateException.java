package ru.cft.crm.exception;

public class InvalidStartDateException extends RuntimeException{
    public InvalidStartDateException(String message) {
        super(message);
    }
}
