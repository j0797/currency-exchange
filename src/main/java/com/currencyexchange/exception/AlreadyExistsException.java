package com.currencyexchange.exception;

public class AlreadyExistsException extends DatabaseException {
    public AlreadyExistsException(String message) {
        super(message);
    }
}
