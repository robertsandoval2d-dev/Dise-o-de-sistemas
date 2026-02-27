package com.campus360.solicitudes.DTOs;

import java.math.BigDecimal;

public class GenerarOrdenPagoRequest {

    private Long solicitudId;
    private BigDecimal monto;

    // getters y setters
    public Long getSolicitudId() {
        return solicitudId;
    }

    public void setSolicitudId(Long solicitudId) {
        this.solicitudId = solicitudId;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
}