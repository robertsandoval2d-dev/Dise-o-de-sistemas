package com.campus360.solicitudes.Servicios;

import java.util.ArrayList;
import org.springframework.web.multipart.MultipartFile;
import com.campus360.solicitudes.DTOs.SolicitudCreateDTO;
import com.campus360.solicitudes.DTOs.SolicitudDTO;
import com.campus360.solicitudes.Dominio.Adjunto;
import java.util.List;

public interface ISolicitudService {
    public boolean servRegistrarSolicitud(int usuarioID, String nombre, String rol, SolicitudCreateDTO dto,ArrayList <Adjunto> adjuntos, List<MultipartFile> archivos);
    public boolean servAnularSolicitud(int solicitudId, int usuarioID);
    public List<SolicitudDTO> servObtenerHistorial(Integer usuarioID); 
    public SolicitudDTO obtenerDetalleCompleto(int solicitudId, String rol); 
    public List<SolicitudDTO> servListarSolicitudes();
}
