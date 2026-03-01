package com.campus360.solicitudes.Dominio.ValueObjects;

import jakarta.persistence.Embeddable;

/**
 * Value Object que encapsula la lógica del estado
 * Evita usar String genérico y proporciona validaciones
 */
@Embeddable
public class Estado {
    
    public enum EstadoEnum {
        PENDIENTE("PENDIENTE"),
        EN_PROCESO("POR_APROBAR"),
        OBSERVADA("OBSERVADA"),
        CANCELADO("RECHAZADA"),
        
        private final String valor;
        
        EstadoEnum(String valor) {
            this.valor = valor;
        }
        
        public String getValor() {
            return valor;
        }
    }
    
    private String valor;
    
    public Estado() {}
    
    public Estado(EstadoEnum estado) {
        this.valor = estado.getValor();
    }
    
    public static Estado crear(String valor) {
        for (EstadoEnum e : EstadoEnum.values()) {
            if (e.getValor().equals(valor)) {
                return new Estado(e);
            }
        }
        throw new IllegalArgumentException("Estado inválido: " + valor);
    }
    
    public boolean isPendiente() {
        return this.valor.equals(EstadoEnum.PENDIENTE.getValor());
    }
    
    public boolean isCompletado() {
        return this.valor.equals(EstadoEnum.COMPLETADO.getValor());
    }
    
    public String getValor() {
        return valor;
    }
}
