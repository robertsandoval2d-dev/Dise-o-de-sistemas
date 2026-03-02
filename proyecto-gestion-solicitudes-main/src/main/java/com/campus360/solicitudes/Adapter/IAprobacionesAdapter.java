package com.campus360.solicitudes.Adapter;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.campus360.solicitudes.Dominio.Solicitud;
import com.campus360.solicitudes.Dominio.Usuario;

public interface IAprobacionesAdapter {
    public boolean sincronizarSolicitud(Solicitud solicitud,Usuario solicitante, List<MultipartFile> archivos);
}

