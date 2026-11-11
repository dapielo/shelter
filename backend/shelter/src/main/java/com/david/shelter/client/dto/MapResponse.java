package com.david.shelter.client.dto;

// De la respuesta solo nos quedamos con lo que nos interesa ya que jackson ignora los atributos que no incluimos en el record
public record MapResponse(
    String display_name,
    String lat,
    String lon,
    double[] boundingbox
) {}
