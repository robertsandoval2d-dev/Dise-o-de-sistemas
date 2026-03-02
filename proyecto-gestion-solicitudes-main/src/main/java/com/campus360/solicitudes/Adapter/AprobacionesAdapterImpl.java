package com.campus360.solicitudes.Adapter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.campus360.solicitudes.Client.AprobacionesClient;
import com.campus360.solicitudes.DTOs.SolicitudAprobacionDTO;

@Component
public class AprobacionesAdapterImpl implements IAprobacionesAdapter {

    @Autowired
    private AprobacionesClient aprobacionesClient;

    @Override
    public boolean sincronizarSolicitud(SolicitudAprobacionDTO dto, List<MultipartFile> archivos) {
        return aprobacionesClient.enviarSolicitudConAdjuntos(dto, archivos);
    }
}
