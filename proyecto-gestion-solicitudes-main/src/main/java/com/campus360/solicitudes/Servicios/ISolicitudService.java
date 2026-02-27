package com.campus360.solicitudes.Servicios;

import java.util.ArrayList;

import com.campus360.solicitudes.DTOs.SolicitudCreateDTO;
import com.campus360.solicitudes.DTOs.SolicitudDTO;
import com.campus360.solicitudes.Dominio.Adjunto;
import java.util.List;

public interface ISolicitudService {
    public boolean servRegistrarSolicitud(SolicitudCreateDTO dto,ArrayList <Adjunto> adjuntos);
    public boolean servAnularSolicitud(int solicitudId);
    public List<SolicitudDTO> servObtenerHistorial(Integer usuarioID); 
    public SolicitudDTO obtenerDetalleCompleto(int solicitudId, String rol); 
}
