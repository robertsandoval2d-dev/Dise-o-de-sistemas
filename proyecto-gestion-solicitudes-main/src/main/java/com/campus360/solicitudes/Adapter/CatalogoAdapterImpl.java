package com.campus360.solicitudes.Adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.campus360.solicitudes.Client.CatalogoClient;
import com.campus360.solicitudes.DTOs.ServicioInfoResponse;

@Component
public class CatalogoAdapterImpl implements ICatalogoAdapter {

    @Autowired
    private CatalogoClient catalogoClient;

    @Override
    public ServicioInfoResponse obtenerServicioActivo(int servicioId) {
        ServicioInfoResponse info = catalogoClient.obtenerServicio(servicioId);
        
        // La validación se queda aquí, encapsulada
        if (info == null || !info.isActivo()) {
            throw new RuntimeException("El servicio solicitado no existe o no está activo");
        }
        return info;
    }
}
