package com.campus360.solicitudes.Dominio;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.ToString;

@Entity
@Table(name="usuario")
public class Usuario {
    // Atributos privados según el diagrama UML
    @Id
    @Column(name="id_usuario")
    private int idUsuario;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "rol", length = 30)
    private String rol; 

    @OneToMany(mappedBy = "solicitante")
    @ToString.Exclude
    private List<Solicitud> solicitudes = new ArrayList<>();

    

    // Constructor vacío 
    public Usuario() {
    }

    // Constructor con parámetros
    public Usuario(int idUsuario, String nombre, String rol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.rol = rol;
    }

    // --- Métodos de Comportamiento ---

    public boolean esAdmin() {
        return "ADMIN".equalsIgnoreCase(this.rol);
    }

    public boolean puedeCrearSolicitud() {
        return "ESTUDIANTE".equalsIgnoreCase(this.rol);
    }

    public void validarPuedeCrearSolicitud() {
        if (!this.rol.equals("ESTUDIANTE")) {
            throw new IllegalStateException("El usuario no tiene permiso para crear solicitudes.");
        }

        if (this.solicitudes.size() >= 5) {
            throw new IllegalStateException("Ha alcanzado el límite de solicitudes activas.");
        }
    }

    public void anularSolicitud(Solicitud solicitud) {
        if (!this.solicitudes.contains(solicitud)) {
            throw new IllegalArgumentException("No pertenece al usuario.");
        }
        solicitud.setEstado("ANULADA");
    }
    // --- Getters y Setters ---

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
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

    public List<Solicitud> getSolicitudes() {
        return solicitudes;
    }

    public void setSolicitudes(List<Solicitud> solicitudes) {
        this.solicitudes = solicitudes;
    }

}
