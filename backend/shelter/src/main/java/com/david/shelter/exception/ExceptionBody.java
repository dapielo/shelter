package com.david.shelter.exception;

public record ExceptionBody(
    Integer status,
    String message,
    String path
) {}
