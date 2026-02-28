package com.campus360.solicitudes.DTOs;

public class SolicitudCreateDTO {
    private int servicioId;
    private String prioridad;   
    private String descripcion; 

    public SolicitudCreateDTO() {}
    
    public SolicitudCreateDTO(int servicioId, String prioridad, String descripcion) {
        this.servicioId = servicioId;
        this.prioridad = prioridad;
        this.descripcion = descripcion;
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


    

     

   

    


}
