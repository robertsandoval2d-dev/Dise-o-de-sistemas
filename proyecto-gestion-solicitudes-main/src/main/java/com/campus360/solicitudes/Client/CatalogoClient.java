package com.campus360.solicitudes.Client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.campus360.solicitudes.DTOs.ServicioInfoResponse;

@Component
public class CatalogoClient {

    private final RestTemplate restTemplate;
    private final String catalogoBaseUrl;

    public CatalogoClient(
            RestTemplate restTemplate,
            @Value("${catalogo.service.url}") String catalogoBaseUrl) {
        this.restTemplate = restTemplate;
        this.catalogoBaseUrl = catalogoBaseUrl;
    }

    public ServicioInfoResponse obtenerServicio(int catalogoId) {

        String url = catalogoBaseUrl + "/servicios/" + catalogoId;

        return restTemplate.getForObject(
                url,
                ServicioInfoResponse.class
        );
    }
}