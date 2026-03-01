package com.campus360.solicitudes.DTOs;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

public class SolicitudCreateDTO {
    private int servicioId;
    private String prioridad;   
    private String descripcion; 
    // Recibimos la fecha en formato ISO o el que prefieras
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaProgramada;

    public SolicitudCreateDTO() {}
    
    public SolicitudCreateDTO(int servicioId, String prioridad, String descripcion, LocalDateTime fechaProgramada) {
        this.servicioId = servicioId;
        this.prioridad = prioridad;
        this.descripcion = descripcion;
        this.fechaProgramada = fechaProgramada;
    }

     public int getServicioId() {
        return servicioId;
    }

    public void setServicioId(int servicioId) {
        this.servicioId = servicioId;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaProgramada() {
        return fechaProgramada;
    }
    
    public void setFechaProgramada(LocalDateTime fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }
     

   

    


}
