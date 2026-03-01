package com.campus360.solicitudes.Adapter;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.campus360.solicitudes.DTOs.SolicitudAprobacionDTO;

public interface IAprobacionesAdapter {
    boolean sincronizarSolicitud(SolicitudAprobacionDTO dto, List<MultipartFile> archivos);
}

