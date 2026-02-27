package com.campus360.solicitudes.Client;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.campus360.solicitudes.DTOs.SolicitudAprobacionDTO;

@Component
public class AprobacionesClient {

    private final RestTemplate restTemplate;
    private final String aprobacionesBaseUrl;

    public AprobacionesClient(
            RestTemplate restTemplate,
            @Value("${aprobaciones.service.url}") String aprobacionesBaseUrl) {
        this.restTemplate = restTemplate;
        this.aprobacionesBaseUrl = aprobacionesBaseUrl;
    }

    public boolean enviarSolicitudConAdjuntos(
            SolicitudAprobacionDTO dto,
            List<MultipartFile> archivos) {

        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            // Parte JSON
            HttpHeaders jsonHeaders = new HttpHeaders();
            jsonHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<SolicitudAprobacionDTO> jsonPart =
                    new HttpEntity<>(dto, jsonHeaders);

            body.add("solicitud", jsonPart);

            // Parte archivos
            if (archivos != null && !archivos.isEmpty()) {
                for (MultipartFile file : archivos) {
                    HttpHeaders fileHeaders = new HttpHeaders();
                    fileHeaders.setContentType(
                            MediaType.parseMediaType(file.getContentType())
                    );

                    HttpEntity<Resource> filePart = new HttpEntity<>(
                            new ByteArrayResource(file.getBytes()) {
                                @Override
                                public String getFilename() {
                                    return file.getOriginalFilename();
                                }
                            },
                            fileHeaders
                    );

                    body.add("archivos", filePart);
                }
            }

            // Headers generales
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity =
                    new HttpEntity<>(body, headers);

            // URL completa
            String url = aprobacionesBaseUrl + "/registrar";

            ResponseEntity<String> response =
                    restTemplate.postForEntity(url, requestEntity, String.class);

            return response.getStatusCode().is2xxSuccessful();

        } catch (Exception e) {
            throw new RuntimeException("Error enviando solicitud a aprobaciones", e);
        }
    }
}
