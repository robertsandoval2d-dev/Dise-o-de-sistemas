package com.campus360.solicitudes.Adapter;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.campus360.solicitudes.Client.PagoClient;
import com.campus360.solicitudes.DTOs.GenerarOrdenPagoRequest;

@Component
public class PagoAdapterImpl implements IPagoAdapter {

    @Autowired
    private PagoClient pagoClient;

    @Override
    public boolean procesarPago(Integer solicitudId, BigDecimal monto) {
        // Si no cuesta nada, retornamos true automáticamente
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            return true; 
        }
        
        // Armamos el DTO específico para el módulo de pagos
        GenerarOrdenPagoRequest pagoRequest = new GenerarOrdenPagoRequest();
        pagoRequest.setSolicitudId(solicitudId);
        pagoRequest.setMonto(monto);
        
        return pagoClient.generarOrden(pagoRequest);
    }
}
