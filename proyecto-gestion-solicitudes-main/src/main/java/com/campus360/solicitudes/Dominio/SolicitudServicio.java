package com.campus360.solicitudes.Dominio;

import java.util.List;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;


@Entity
@Table(name="solicitud_servicio")
@PrimaryKeyJoinColumn(name = "id_solicitud")
public class SolicitudServicio extends Solicitud{
     // Atributo privado (-)
    @Column(name = "tipo_servicio",length = 50, nullable = false)
    private String tipoServicio;
    @Column(name = "fecha_solicitada", nullable = false)
    private Date fechaSolicitada;

    public SolicitudServicio() {
        super();
    }

    public SolicitudServicio(int idSolicitud, Date fechaCreacion, String estado, String prioridad, int slaObjetivo, 
                     Usuario solicitante, List<Adjunto> adjuntos, List<HistorialEstado> historial) {
        super(idSolicitud, fechaCreacion, estado, prioridad, slaObjetivo,solicitante, adjuntos, historial);
    }

    // --- Métodos de Comportamiento (+) ---

    public void validarCreacion() {
        validarTipoServicio();
        validarFechaSolicitada();
        calcularSLA();
    }

    public void validarFechaSolicitada() {
        if (fechaSolicitada.before(new Date())) {
            throw new IllegalStateException("La fecha solicitada no puede ser pasada.");
        }
    }

    public void validarTipoServicio() {
        if (tipoServicio == null || tipoServicio.isBlank()) {
            throw new IllegalArgumentException("Debe especificar un tipo de servicio.");
        }
    }

    // --- Getters y Setters ---

    public Date getFechaSolicitada() {
        return fechaSolicitada;
    }

    public void setFechaSolicitada(Date fechaSolicitada) {
        this.fechaSolicitada = fechaSolicitada;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }
}
