package com.david.shelter.client;

import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.david.shelter.client.dto.MapResponse;
import com.david.shelter.model.enums.Province;

@Service 
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
    public Optional<MapResponse> getGeolocation(Province province) {

        // Puede devolver un array vacio, pero le pedimos limite 1 asi que o tiene tamaño 0 o tamaño 1, si no viene vacio envolvemos el unico elemento en un Optional
        MapResponse[] response = this.client
            .get()
            .uri(uri -> uri
                .queryParam("city", province)
                .queryParam("country", "spain")
                .queryParam("format", "json")
                .queryParam("limit", 1)
                .build())
            .retrieve()
            .body(MapResponse[].class);      

        if (response.length == 0) {
            return Optional.empty();
        }

        return Optional.of(response[0]);
    }
}
