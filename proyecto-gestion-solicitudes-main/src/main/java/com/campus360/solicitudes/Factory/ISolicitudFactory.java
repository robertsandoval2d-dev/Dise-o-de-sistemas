package com.campus360.solicitudes.Factory;

import com.campus360.solicitudes.Dominio.Solicitud;
import com.campus360.solicitudes.DTOs.ServicioInfoResponse;

public interface ISolicitudFactory {
    // El contrato: toda fábrica debe saber crear una Solicitud
    Solicitud crear(ServicioInfoResponse info);
}
