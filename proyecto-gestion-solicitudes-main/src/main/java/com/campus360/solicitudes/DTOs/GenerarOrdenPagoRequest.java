package com.campus360.solicitudes.DTOs;

import java.math.BigDecimal;

public class GenerarOrdenPagoRequest {

    private Integer solicitudId;
    private BigDecimal monto;

    // getters y setters
    public Integer getSolicitudId() {
        return solicitudId;
    }

    public void setSolicitudId(Integer solicitudId) {
        this.solicitudId = solicitudId;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
}