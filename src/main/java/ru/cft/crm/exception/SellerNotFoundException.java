package ru.cft.crm.exception;

public class SellerNotFoundException extends RuntimeException {
    public SellerNotFoundException(String message) {
        super(message);
    }
}
