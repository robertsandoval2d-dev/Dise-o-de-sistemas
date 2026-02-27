package com.campus360.solicitudes.Servicios.sla;

import java.util.Calendar;
import java.util.Date;

import com.campus360.solicitudes.Dominio.Solicitud;

public class SlaAltaStrategy implements SlaStrategy {

  @Override
  public void aplicarSla(Solicitud solicitud) {
  
    int diasPlazo = 1;
    int horas = diasPlazo * 24;
    
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());
    cal.add(Calendar.DAY_OF_YEAR, diasPlazo);
    
    solicitud.setSlaObjetivo(horas);
    solicitud.setFechaLimite(cal.getTime());
    solicitud.setFechaUltimoCambio(new Date());
  }
}
