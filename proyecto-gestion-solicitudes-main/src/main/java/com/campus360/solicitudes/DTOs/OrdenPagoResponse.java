package com.campus360.solicitudes.DTOs;

public class OrdenPagoResponse {

    private String numeroOrden;
    private String estado;
    private String urlPago;

    // getters y setters
    public String getNumeroOrden() {
        return numeroOrden;
    }
    public void setNumeroOrden(String numeroOrden) {
        this.numeroOrden = numeroOrden;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public String getUrlPago() {
        return urlPago;
    }
    public void setUrlPago(String urlPago) {
        this.urlPago = urlPago;
    }
}