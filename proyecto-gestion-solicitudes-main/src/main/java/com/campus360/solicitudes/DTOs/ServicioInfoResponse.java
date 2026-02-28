package com.campus360.solicitudes.DTOs;

import java.math.BigDecimal;
import java.util.List;

public class ServicioInfoResponse {

    private int id;
    private String nombre;
    private String descripcion;
    private BigDecimal costo;
    private boolean activo;
    private List<RequisitoDTO> requisitos;

    public ServicioInfoResponse() {
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public boolean isActivo() {
        return activo;
    }

    public List<RequisitoDTO> getRequisitos() {
        return requisitos;
    }

}