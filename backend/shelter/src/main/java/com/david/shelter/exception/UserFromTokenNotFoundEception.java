package com.david.shelter.exception;

public class UserFromTokenNotFoundEception extends RuntimeException {

    public UserFromTokenNotFoundEception(String message) {
        super(message);
    }
}
