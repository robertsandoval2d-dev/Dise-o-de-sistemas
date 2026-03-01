package com.campus360.solicitudes.Factory;

import com.campus360.solicitudes.Dominio.Solicitud;          // La clase padre
import com.campus360.solicitudes.Dominio.SolicitudServicio;  // La clase hija que vas a crear
import com.campus360.solicitudes.DTOs.ServicioInfoResponse; // El DTO que viene del catálogo
import org.springframework.stereotype.Component;            // Para que Spring reconozca la fábrica

@Component("servicioFactory")
public class SolicitudServicioFactory implements ISolicitudFactory {
    @Override
    public Solicitud crear(ServicioInfoResponse info) {
        SolicitudServicio ss = new SolicitudServicio();
        ss.setTipoServicio(info.getNombre());
        ss.setFechaSolicitada(new java.util.Date()); // Configuración específica de Servicios
        return ss;
    }
}
