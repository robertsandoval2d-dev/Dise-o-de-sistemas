package com.campus360.solicitudes.Servicios;

import org.springframework.web.multipart.MultipartFile;
import com.campus360.solicitudes.DTOs.SolicitudCreateDTO;
import com.campus360.solicitudes.DTOs.SolicitudDTO;
import java.util.List;

public interface ISolicitudService {
    public boolean servRegistrarSolicitud(int usuarioID, String nombre, String rol, SolicitudCreateDTO dto, List<MultipartFile> archivos);
    public boolean servAnularSolicitud(int solicitudId, int usuarioID);
    public List<SolicitudDTO> servObtenerHistorialSolicitudes(Integer usuarioID); 
    public SolicitudDTO obtenerDetalleCompleto(int solicitudId, String rol, int usuarioId); 
    public List<SolicitudDTO> servListarSolicitudes();
}
