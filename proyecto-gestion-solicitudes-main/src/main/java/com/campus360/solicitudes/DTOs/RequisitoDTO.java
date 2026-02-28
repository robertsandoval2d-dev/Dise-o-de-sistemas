package com.campus360.solicitudes.DTOs;

public class RequisitoDTO {

    private String campo; //fecha_hora_alquiler, Sede de Recojo
    private String tipo; // STRING, ARCHIVO, DATETIME
    private boolean obligatorio; //true. false
    
    public RequisitoDTO() {
    }

    public RequisitoDTO(String campo, String tipo, boolean obligatorio) {
        this.campo = campo;
        this.tipo = tipo;
        this.obligatorio = obligatorio;
    }

    public String getCampo() {
        return campo;
    }

    public String getTipo() {
        return tipo;
    }

    public boolean isObligatorio() {
        return obligatorio;
    }

}