 package com.campus360.solicitudes.Servicios;
 import java.util.ArrayList;

import com.campus360.solicitudes.DTOs.SolicitudCreateDTO;
import com.campus360.solicitudes.Dominio.Adjunto;
import com.campus360.solicitudes.Dominio.Solicitud;
import java.util.List;

 public interface ISolicitudCommandService {
        public boolean servRegistrarSolicitud(SolicitudCreateDTO dto,ArrayList <Adjunto> adjuntos);
        public boolean anularSolicitud(int solicitudId);
        //----------------------------------------------------------
        public List<Solicitud> servObtenerHistorial(Integer usuarioID); 
        public Solicitud obtenerDetalleCompleto(int solicitudId); 
 }
