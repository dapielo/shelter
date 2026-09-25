package com.david.shelter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice 
public class GlobalExceptionHandler {

    // Si Jackson encuentra un JSON mal formado (poruqe no se corresponde el texto de un campo con uno de los posibles valores de un Enum),
    // lanza esta excepcion, la capturamos aqui 
    @ExceptionHandler (HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionBody> handleMalformedJSON(HttpMessageNotReadableException e, WebRequest request) {
        
        ExceptionBody body = new ExceptionBody(
            400, 
            "Alguno de los campos enviados tenía información incorrecta", 
            request.getDescription(false));

        return new ResponseEntity<ExceptionBody>(body, HttpStatus.BAD_REQUEST);
    }
    
}
