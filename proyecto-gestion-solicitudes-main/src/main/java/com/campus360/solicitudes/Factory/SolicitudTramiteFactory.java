package com.campus360.solicitudes.Factory;

import com.campus360.solicitudes.Dominio.Solicitud; 
import com.campus360.solicitudes.Dominio.SolicitudTramite; 
import com.campus360.solicitudes.DTOs.ServicioInfoResponse;
import org.springframework.stereotype.Component;
import com.campus360.solicitudes.DTOs.SolicitudCreateDTO;

@Component("tramiteFactory")
public class SolicitudTramiteFactory implements ISolicitudFactory {
    @Override
    public Solicitud crear(ServicioInfoResponse info, SolicitudCreateDTO dto) {
        SolicitudTramite st = new SolicitudTramite();
        st.setTipoTramite(info.getNombre());
        st.setDescripcion(dto.getDescripcion());
        st.setPrioridad(dto.getPrioridad());
        return st;
    }
}
