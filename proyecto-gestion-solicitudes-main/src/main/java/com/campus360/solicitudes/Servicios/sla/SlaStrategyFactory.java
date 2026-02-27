package com.campus360.solicitudes.Servicios.sla;

public class SlaStrategyFactory {

   public static ISlaStrategy obtenerStrategy(String prioridad) {

       return switch (prioridad.toUpperCase()) {
           case "ALTA" -> new SlaAltaStrategy();
           case "BAJA" -> new SlaBajaStrategy();
           default -> throw new IllegalArgumentException("Prioridad no válida");
       };
   }
}
