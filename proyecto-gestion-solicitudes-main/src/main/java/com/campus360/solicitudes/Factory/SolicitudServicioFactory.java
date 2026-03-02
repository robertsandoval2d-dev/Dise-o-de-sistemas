package com.campus360.solicitudes.Factory;

import com.campus360.solicitudes.Dominio.Solicitud;          // La clase padre
import com.campus360.solicitudes.Dominio.SolicitudServicio;  // La clase hija que vas a crear
import com.campus360.solicitudes.DTOs.ServicioInfoResponse; // El DTO que viene del catálogo
import org.springframework.stereotype.Component;            // Para que Spring reconozca la fábrica
import com.campus360.solicitudes.DTOs.SolicitudCreateDTO; // El DTO que viene del controlador

@Component("servicioFactory")
public class SolicitudServicioFactory implements ISolicitudFactory {
    @Override
    public Solicitud crear(ServicioInfoResponse info, SolicitudCreateDTO dto) {
        SolicitudServicio ss = new SolicitudServicio();
        ss.setTipoServicio(info.getNombre());
        ss.setDescripcion(dto.getDescripcion());
        ss.setPrioridad(dto.getPrioridad());
        // 3. Conversión de fecha específica (Lógica encapsulada)
        if (dto.getFechaProgramada() != null) {
            try {
                java.util.Date fechaConvertida = java.util.Date.from(
                    dto.getFechaProgramada()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toInstant()
                );
                ss.setFechaSolicitada(fechaConvertida);
            } catch (Exception e) {
                // LANZAMOS EL ERROR (Esto es lo que probarás)
                throw new RuntimeException("Error de formato en fecha programada: " + e.getMessage());
            }
        }
        return ss;
    }
}
