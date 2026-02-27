package com.campus360.solicitudes.Client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import com.campus360.solicitudes.DTOs.OrdenPagoResponse;
import com.campus360.solicitudes.DTOs.GenerarOrdenPagoRequest;

@Component
public class PagoClient {

    private final RestTemplate restTemplate;
    private final String pagoBaseUrl;

    public PagoClient(
            RestTemplate restTemplate,
            @Value("${pago.service.url}") String pagoBaseUrl) {
        this.restTemplate = restTemplate;
        this.pagoBaseUrl = pagoBaseUrl;
    }

    public OrdenPagoResponse generarOrden(GenerarOrdenPagoRequest request) {

        String url = pagoBaseUrl + "/ordenes";

        ResponseEntity<OrdenPagoResponse> response =
                restTemplate.postForEntity(
                        url,
                        request,
                        OrdenPagoResponse.class
                );

        return response.getBody();
    }
}