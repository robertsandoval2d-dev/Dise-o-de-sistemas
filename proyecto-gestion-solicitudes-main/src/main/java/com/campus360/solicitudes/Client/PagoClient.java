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

    public boolean generarOrden(GenerarOrdenPagoRequest request) {
    //     try {
    //         // Enviamos el request que ya tiene el solicitudId y el monto
    //         String url = pagoBaseUrl + "/ordenes";
            
    //         // Si el servicio responde 200 OK, asumimos que el módulo de pago
    //         // ya asoció internamente nuestra solicitud con su orden.
    //         ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);
            
    //         return response.getStatusCode().is2xxSuccessful();
    //     } catch (Exception e) {
    //         System.err.println("Error al contactar módulo de pagos: " + e.getMessage());
    //         return false;
    //     }
    // }
    System.out.println("[MOCK PAGO] Recibida orden para Solicitud ID: " + request.getSolicitudId());
        System.out.println("[MOCK PAGO] Monto a procesar: " + request.getMonto());
        
        // Simulamos que el módulo de pago recibió la solicitud y la registró exitosamente
        return true;
    }

}