package com.campus360.solicitudes.Servicios.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.campus360.solicitudes.DTOs.ServicioInfoResponse;
import com.campus360.solicitudes.DTOs.SolicitudCreateDTO;
import com.campus360.solicitudes.Dominio.Solicitud;
import com.campus360.solicitudes.Dominio.Usuario;
import com.campus360.solicitudes.Factory.ISolicitudFactory;
import com.campus360.solicitudes.Servicios.sla.SlaStrategyFactory;

@Component
public class SolicitudFactoryManager {
    @Autowired
    @Qualifier("servicioFactory")
    private ISolicitudFactory servicioFactory;

    @Autowired
    @Qualifier("tramiteFactory")
    private ISolicitudFactory tramiteFactory;

    public Solicitud crearInstanciaSolicitud(ServicioInfoResponse info, SolicitudCreateDTO dto, Usuario solicitante) {
        boolean esServicio = info.getRequisitos().stream()
                .anyMatch(r -> "DATE".equalsIgnoreCase(r.getTipo()) || "DATETIME".equalsIgnoreCase(r.getTipo()));

        ISolicitudFactory fabrica = esServicio ? servicioFactory : tramiteFactory;
        Solicitud solicitud = fabrica.crear(info, dto);

        solicitud.setSolicitante(solicitante);
        solicitud.setEstado("PENDIENTE");

        SlaStrategyFactory.obtenerStrategy(solicitud.getPrioridad()).aplicarSla(solicitud);
        
        return solicitud;
    }

}
