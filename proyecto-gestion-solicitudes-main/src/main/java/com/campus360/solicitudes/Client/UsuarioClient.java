package com.campus360.solicitudes.Client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.campus360.solicitudes.DTOs.UsuarioExternoDTO;

@Component
public class UsuarioClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public UsuarioClient(RestTemplate restTemplate,
                         @Value("${usuarios.api.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public UsuarioExternoDTO obtenerUsuarioConToken(int usuarioId) {

        String url = String.format("%s/usuarios/%d", baseUrl, usuarioId);

        try {
            return restTemplate.getForObject(url, UsuarioExternoDTO.class);

        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Usuario no encontrado en módulo externo"
            );

        } catch (HttpServerErrorException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Error en el módulo de usuarios"
            );

        } catch (ResourceAccessException e) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo conectar con el módulo de usuarios"
            );

        } catch (RestClientException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error inesperado al consumir el servicio externo"
            );
        }
    }
}