package com.david.shelter.exception;

// Este DTO es el cuerpo del JSON que recibe el front en caso de que suceda algun error, consta de 3 piezas de informacion: el codigo de estado
// un mensaje indicando que ha salido mal, y la ruta de la API donde ha ocurrido el error
public record ExceptionBody(
    Integer status,
    String message,
    String path
) {}
