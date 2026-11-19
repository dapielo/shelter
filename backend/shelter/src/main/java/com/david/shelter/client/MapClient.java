package com.david.shelter.client;

import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import com.david.shelter.client.dto.MapInfo;
import com.david.shelter.model.enums.Province;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j  
// La documentacion de la API de Nominatim esta aqui https://nominatim.org/release-docs/latest/api/Search/ y esta bastante bien explicada
public class MapClient {

    private final RestClient client;

    public MapClient() {
        // usamos el builder que trae RestClient, nominatim obliga a indicar un User-Agent
        this.client = RestClient.builder()
            .baseUrl("https://nominatim.openstreetmap.org/search")
            .defaultHeader(HttpHeaders.USER_AGENT, "Shelter 1.0 (david.piedras1991@gmail.com)")
            .defaultHeader(HttpHeaders.ACCEPT, "application/json")
            .build();
    }

    // Puede devolver una respuesta vacia, por eso lo envolvemos en un Optional
    // lo vamos a cachear porque nominatium pone limites estrictos a las peticiones, excepto si el valor es null, que es es un fallo en la peticion
    @Cacheable (value = "geolocation", unless = "#result == null")
    public Optional<MapInfo> getGeolocation(Province province) {

        // Puede devolver un array vacio, pero le pedimos limite 1 asi que o tiene tamaño 0 o tamaño 1, si no viene vacio envolvemos el unico elemento en un Optional
        try {

            MapInfo[] response = this.client
            .get()
            .uri(uri -> uri
                .queryParam("county", province) // nominatim usa el parametro "county" para las provincias (no confundir con country que es para paises)
                .queryParam("country", "spain")
                .queryParam("format", "json")
                .queryParam("limit", 1)
                .build())
            .retrieve()
            .body(MapInfo[].class);      
                
                if (response.length == 0) {
                    return Optional.empty();
                }
                
            return Optional.of(response[0]);

        // Este error no se propaga al ControllerAdvice, si la carga de los datos del mapa falla, el mapa simplemente no se va a mostrar
        } catch (RestClientException e) {
            log.warn(e.getMessage());
            return Optional.empty();
        }

    }
}
