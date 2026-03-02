package com.campus360.solicitudes.Adapter;

import java.math.BigDecimal;

public interface IPagoAdapter {
    boolean procesarPago(Integer solicitudId, BigDecimal monto);
}

