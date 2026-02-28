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

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public List<RequisitoDTO> getRequisitos() {
        return requisitos;
    }

    public void setRequisitos(List<RequisitoDTO> requisitos) {
        this.requisitos = requisitos;
    }

}