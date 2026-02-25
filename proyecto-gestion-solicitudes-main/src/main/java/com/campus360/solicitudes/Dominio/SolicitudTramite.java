package com.campus360.solicitudes.Dominio;

import java.util.List;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name="solicitud_tramite")
@PrimaryKeyJoinColumn(name = "id_solicitud")
public class SolicitudTramite extends Solicitud {
    // Atributo privado (-) según el diagrama
    @Column(name = "tipo_tramite",length = 50, nullable = false)
    private String tipoTramite;

    // Constructor vacío
    public SolicitudTramite() {
    }

    // Constructor con parámetros
    public SolicitudTramite(/*Tramite tramite,*/int idSolicitud, Date fechaCreacion, String estado, String prioridad, int slaObjetivo, 
                     Usuario solicitante, List<Adjunto> adjuntos, List<HistorialEstado> historial) {
        super(idSolicitud, fechaCreacion, estado, prioridad, slaObjetivo, solicitante, adjuntos, historial);
        //this.tramite = tramite;
    }

    // --- Métodos de Comportamiento (+) ---

    /**
     * Valida que los documentos adjuntos cumplan con lo requerido por el trámite.
     */
    public void validarDocumentosTramite() {
        // Lógica de dominio: verificar si el trámite tiene requisitos y si se cumplen
        System.out.println("Validando documentos para el trámite...");
    }


    // --- Getters y Setters ---

    public String getTipoTramite() {
        return tipoTramite;
    }

    public void setTipoTramite(String tipoTramite) {
        this.tipoTramite = tipoTramite;
    }
}
