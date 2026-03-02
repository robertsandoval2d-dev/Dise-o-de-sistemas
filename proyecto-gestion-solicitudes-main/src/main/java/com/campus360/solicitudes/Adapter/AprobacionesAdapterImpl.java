package com.campus360.solicitudes.Adapter;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.campus360.solicitudes.Client.AprobacionesClient;
import com.campus360.solicitudes.DTOs.SolicitudAprobacionDTO;
import com.campus360.solicitudes.Dominio.Solicitud;
import com.campus360.solicitudes.Dominio.Usuario;

@Component
public class AprobacionesAdapterImpl implements IAprobacionesAdapter {

    @Autowired
    private AprobacionesClient aprobacionesClient;

    @Override
    public boolean sincronizarSolicitud(Solicitud solicitud,Usuario solicitante, List<MultipartFile> archivos) {
        // La creación del DTO se mudó aquí para que el Service no la vea
        SolicitudAprobacionDTO dto = new SolicitudAprobacionDTO();
        dto.setIdSolicitud(solicitud.getIdSolicitud());
        dto.setEstado(solicitud.getEstado());
        dto.setFechaCreacion(new Date());
        dto.setNombreSolicitante(solicitante.getNombre());

        return aprobacionesClient.enviarSolicitudConAdjuntos(dto, archivos);
    }
}
