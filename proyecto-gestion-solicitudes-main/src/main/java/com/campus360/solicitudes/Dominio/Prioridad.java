package com.campus360.solicitudes.Dominio.ValueObjects;

import jakarta.persistence.Embeddable;

/**
 * Value Object para la prioridad
 * Solo contiene ALTA y BAJA según el SLA Strategy del proyecto
 */
@Embeddable
public class Prioridad {
    
    public enum NivelPrioridad {
        BAJA("BAJA", 3),      // 3 días de plazo
        ALTA("ALTA", 1);      // 1 día de plazo
        
        private final String nombre;
        private final int diasPlazo;
        
        NivelPrioridad(String nombre, int diasPlazo) {
            this.nombre = nombre;
            this.diasPlazo = diasPlazo;
        }
        
        public String getNombre() {
            return nombre;
        }
        
        public int getDiasPlazo() {
            return diasPlazo;
        }
        
        public int getHoras() {
            return diasPlazo * 24;
        }
    }
    
    private String valor;
    
    public Prioridad() {}
    
    public Prioridad(NivelPrioridad prioridad) {
        this.valor = prioridad.getNombre();
    }
    
    public static Prioridad crear(String valor) {
        for (NivelPrioridad p : NivelPrioridad.values()) {
            if (p.getNombre().equals(valor.toUpperCase())) {
                return new Prioridad(p);
            }
        }
        throw new IllegalArgumentException("Prioridad inválida: " + valor + ". Válidas: ALTA, BAJA");
    }
    
    public boolean esAlta() {
        return valor.equals(NivelPrioridad.ALTA.getNombre());
    }
    
    public boolean esBaja() {
        return valor.equals(NivelPrioridad.BAJA.getNombre());
    }
    
    public int getDiasPlazo() {
        NivelPrioridad nivel = NivelPrioridad.valueOf(valor);
        return nivel.getDiasPlazo();
    }
    
    public int getHorasPlazo() {
        NivelPrioridad nivel = NivelPrioridad.valueOf(valor);
        return nivel.getHoras();
    }
    
    public String getValor() {
        return valor;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Prioridad)) return false;
        return this.valor.equals(((Prioridad) obj).valor);
    }
    
    @Override
    public int hashCode() {
        return valor.hashCode();
    }
}
