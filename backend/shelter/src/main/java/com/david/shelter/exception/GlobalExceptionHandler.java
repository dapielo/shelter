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

    // WebRequest es un objeto que contiene informacion de la peticion, Spring lo inyecta relleno de informacion automaticamente, igual que con otras
    // clases para las cuales tiene resolvedores, lo usamos para saber exactamnete en que endpoint se ha lanzado la excepcion.

    // Excepto para este primer hanlder, que captura una excepcion que no lanzamos nosotros, sino que la lanza jackson si no puede parsear un JSON
    // los mensajes de las respuestas nos vienen de las clases que las lanzan, de modo que lo unico que varia entre estos métodos es el código
    // de estado que les ponemos, el mensaje de error nos viene en la exception que viene como parametro y la ruta el el objeto Webrequest
    @ExceptionHandler (HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionBody> handleMalformedJSON(HttpMessageNotReadableException e, WebRequest request) {
        
        ExceptionBody body = new ExceptionBody(
            400, 
            "Alguno de los campos enviados tenía información incorrecta", 
            request.getDescription(false));

        return new ResponseEntity<ExceptionBody>(body, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler (ResourceNotFoundException.class)
    public ResponseEntity<ExceptionBody> handleInvalidemail(ResourceNotFoundException e, WebRequest request) {

        ExceptionBody body = new ExceptionBody(
            400, 
            e.getMessage(),
            request.getDescription(false) 
        );

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ExceptionBody> handleUserException(UserException e, WebRequest request) {

        ExceptionBody body = new ExceptionBody(
            400, 
            e.getMessage(), 
            request.getDescription(false)
        );

        return new ResponseEntity<>(body,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler (PasswordNotMatchingException.class)
    public ResponseEntity<ExceptionBody> handlePasswordNotMatching(PasswordNotMatchingException e, WebRequest request) {

        ExceptionBody body = new ExceptionBody(
            400, 
            e.getMessage(), 
            request.getDescription(false)
        );

        return new ResponseEntity<>(body,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler (FosterHomeException.class)
    public ResponseEntity<ExceptionBody> handleFosterHomeException(PasswordNotMatchingException e, WebRequest request) {

        ExceptionBody body = new ExceptionBody(
            400, 
            e.getMessage(), 
            request.getDescription(false)
        );

        return new ResponseEntity<>(body,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler (PetException.class) 
    public ResponseEntity<ExceptionBody> handlePetException(PetException e, WebRequest request) {

        ExceptionBody body = new ExceptionBody(
            400, 
            e.getMessage(), 
            request.getDescription(false)
        );   

        return new ResponseEntity<>(body,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler (LoginException.class) 
    public ResponseEntity<ExceptionBody> handleLoginException(LoginException e, WebRequest request) {

        ExceptionBody body = new ExceptionBody(
            400, 
            e.getMessage(), 
            request.getDescription(false)
        );   

        return new ResponseEntity<>(body,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler (InvalidTokenException.class) 
    public ResponseEntity<ExceptionBody> handleInvalidToken(InvalidTokenException e, WebRequest request) {

        ExceptionBody body = new ExceptionBody(
            400, 
            e.getMessage(), 
            request.getDescription(false)
        );   

        return new ResponseEntity<>(body,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler (UserFromTokenNotFoundEception.class) 
    public ResponseEntity<ExceptionBody> handleUserFromTokenNotFound(UserFromTokenNotFoundEception e, WebRequest request) {

        ExceptionBody body = new ExceptionBody(
            500, 
            e.getMessage(), 
            request.getDescription(false)
        );   

        return new ResponseEntity<>(body,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler (AdptionAlreadyExistsException.class) 
    public ResponseEntity<ExceptionBody> handleAdoptionAlreadyExists(AdptionAlreadyExistsException e, WebRequest request) {

        ExceptionBody body = new ExceptionBody(
            400, 
            e.getMessage(), 
            request.getDescription(false)
        );   

        return new ResponseEntity<>(body,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler (AdoptionException.class) 
    public ResponseEntity<ExceptionBody> handleAdoptionException(AdoptionException e, WebRequest request) {

        ExceptionBody body = new ExceptionBody(
            400, 
            e.getMessage(), 
            request.getDescription(false)
        );   

        return new ResponseEntity<>(body,HttpStatus.BAD_REQUEST);
    }
}
