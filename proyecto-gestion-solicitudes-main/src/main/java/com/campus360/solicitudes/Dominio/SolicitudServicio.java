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

    public SolicitudServicio(/*Servicio servicio,*/ int idSolicitud, Date fechaCreacion, String estado, String prioridad, int slaObjetivo, 
                     Usuario solicitante, List<Adjunto> adjuntos, List<HistorialEstado> historial) {
        super(idSolicitud, fechaCreacion, estado, prioridad, slaObjetivo,solicitante, adjuntos, historial);
        //this.servicio = servicio;
    }

    // --- Métodos de Comportamiento (+) ---

    public void obtenerReglasServicio() {
        // Lógica: Consultar las reglas de negocio del servicio
        /*if (this.servicio != null) {
            System.out.println("Obteniendo reglas para el servicio.");
        }*/
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
