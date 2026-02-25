package com.campus360.solicitudes.DTOs;

public class SolicitudCreateDTO {
    private String tipo;
    private String prioridad;   
    private String descripcion; 
    private Integer usuarioId; 

    public SolicitudCreateDTO() {}
    
    public SolicitudCreateDTO(String tipo, String prioridad, String descripcion,
            Integer usuarioId) {
        this.tipo = tipo;
        this.prioridad = prioridad;
        this.descripcion = descripcion;
        this.usuarioId = usuarioId;
    }

     public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    

     

   

    


}
