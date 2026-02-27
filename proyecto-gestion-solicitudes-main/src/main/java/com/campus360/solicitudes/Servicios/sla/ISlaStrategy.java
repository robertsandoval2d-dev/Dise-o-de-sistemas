package com.campus360.solicitudes.Servicios.sla;

import com.campus360.solicitudes.Dominio.Solicitud;

public interface SlaStrategy {

  void aplicarSla(Solicitud solicitud);

}
