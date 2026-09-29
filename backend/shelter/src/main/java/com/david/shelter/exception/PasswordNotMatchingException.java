package com.david.shelter.exception;

public class PasswordNotMatchingException extends RuntimeException {

    public PasswordNotMatchingException(String message) {
        super(message);
    }
}
