package com.campus360.solicitudes.Client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import com.campus360.solicitudes.DTOs.RequisitoDTO;
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

        // String url = catalogoBaseUrl + "/servicios/" + catalogoId;

        // return restTemplate.getForObject(
        //         url,
        //         ServicioInfoResponse.class
        // );

        // 1. Log para saber que estamos usando el Mock
        System.out.println("DEBUG: Usando simulación para Servicio ID: " + catalogoId);

        // 2. Crear el objeto principal de respuesta
        ServicioInfoResponse mockResponse = new ServicioInfoResponse();
        
        // Usamos reflexión o setters si los tienes, o modificamos la clase para que tenga setters.
        // Asumiendo que agregas setters básicos para la prueba:
        mockResponse.setId(catalogoId);
        mockResponse.setNombre("Certificado de notas");
        mockResponse.setDescripcion("Servicio simulado para pruebas de refactorización");
        mockResponse.setCosto(new java.math.BigDecimal("20.00"));
        mockResponse.setActivo(true);

        // 3. Crear los requisitos simulados
        List<RequisitoDTO> requisitos = new java.util.ArrayList<>();
        
        // Requisito tipo Archivo (muy importante si tu controlador maneja adjuntos)
        requisitos.add(new RequisitoDTO("Copia de DNI", "ARCHIVO", true));

        // 4. Asignar la lista al objeto de respuesta
        mockResponse.setRequisitos(requisitos);

        return mockResponse;
    }
}