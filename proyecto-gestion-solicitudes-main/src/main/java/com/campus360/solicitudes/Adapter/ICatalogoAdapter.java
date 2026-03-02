package com.campus360.solicitudes.Adapter;

import com.campus360.solicitudes.DTOs.ServicioInfoResponse;

public interface ICatalogoAdapter {
    ServicioInfoResponse obtenerServicioActivo(int servicioId);
}

