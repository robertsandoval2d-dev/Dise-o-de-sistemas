package com.campus360.solicitudes.DTOs;

import com.campus360.solicitudes.Dominio.Usuario;

public class UsuarioExternoDTO {
    private int id;
    private String nombre;
    private String rol;
    private String token; // solo para uso temporal

    public UsuarioExternoDTO() {
    }
    public UsuarioExternoDTO(int id, String nombre, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.rol = rol;
    }
    public UsuarioExternoDTO(Usuario usuario) {
        this.id = usuario.getIdUsuario();
        this.nombre = usuario.getNombre();
        this.rol = usuario.getRol();
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
    public String getRol() {
        return rol;
    }
    public void setRol(String rol) {
        this.rol = rol;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    
}

