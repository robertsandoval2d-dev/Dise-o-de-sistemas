package com.campus360.solicitudes.Servicios.util;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.campus360.solicitudes.DTOs.RequisitoDTO;
import com.campus360.solicitudes.DTOs.ServicioInfoResponse;
import com.campus360.solicitudes.DTOs.SolicitudCreateDTO;


@Component
public class SolicitudValidator {

    public void validarRequisitosDinamicos(ServicioInfoResponse info, SolicitudCreateDTO dto, List<MultipartFile> archivos) {
        if (info.getRequisitos() == null) return;
        // 1. Contamos cuántos archivos OBLIGATORIOS pide el catálogo
        long archivosRequeridos = info.getRequisitos().stream()
            .filter(req -> req.isObligatorio() && 
                    ("FILE".equalsIgnoreCase(req.getTipo()) || "ARCHIVO".equalsIgnoreCase(req.getTipo())))
            .count();

        // 2. Contamos cuántos archivos REALES (no vacíos) envió el usuario
        long archivosEnviados = 0;
        if (archivos != null) {
            archivosEnviados = archivos.stream()
                .filter(f -> !f.isEmpty() && f.getOriginalFilename() != null && !f.getOriginalFilename().isBlank())
                .count();
        }

        // 3. Validación de cantidad
        if (archivosEnviados < archivosRequeridos) {
            throw new RuntimeException("Faltan archivos adjuntos. Se requieren " + archivosRequeridos + 
                                    " archivos obligatorios, pero se recibieron " + archivosEnviados + ".");
        }
        // 4. Validación de campos de texto (se mantiene igual)
        for (RequisitoDTO req : info.getRequisitos()) {
            if (req.isObligatorio() && ("STRING".equalsIgnoreCase(req.getTipo()) || "DATETIME".equalsIgnoreCase(req.getTipo()))) {
                if (dto.getFechaProgramada() == null || (dto.getDescripcion() == null || dto.getDescripcion().isBlank())) {
                    throw new RuntimeException("Debe especificar '" + req.getCampo() + "' en la solicitud.");
                }
            }
        }
    }

}
