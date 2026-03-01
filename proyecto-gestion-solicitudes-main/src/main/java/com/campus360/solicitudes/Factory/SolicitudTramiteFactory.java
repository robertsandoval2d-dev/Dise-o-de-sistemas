package com.campus360.solicitudes.Factory;

import com.campus360.solicitudes.Dominio.Solicitud; 
import com.campus360.solicitudes.Dominio.SolicitudTramite; 
import com.campus360.solicitudes.DTOs.ServicioInfoResponse;
import org.springframework.stereotype.Component;

@Component("tramiteFactory")
public class SolicitudTramiteFactory implements ISolicitudFactory {
    @Override
    public Solicitud crear(ServicioInfoResponse info) {
        SolicitudTramite st = new SolicitudTramite();
        st.setTipoTramite(info.getNombre());
        return st;
    }
}
