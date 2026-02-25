package com.campus360.solicitudes.Servicios;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.campus360.solicitudes.DTOs.SolicitudCreateDTO;
import com.campus360.solicitudes.Dominio.Adjunto;
import com.campus360.solicitudes.Dominio.Solicitud;
import com.campus360.solicitudes.Dominio.Usuario;
import com.campus360.solicitudes.Repositorio.IAlmacenamiento;
import com.campus360.solicitudes.Repositorio.ISolicitudRepository;
import com.campus360.solicitudes.Repositorio.IUsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class SolicitudService implements ISolicitudCommandService/*, ISolicitudQueryService*/ {

    @Autowired
    private ISolicitudRepository repoSolicitud;
    @Autowired
    private IUsuarioRepository repoUsuario;

    
    @Autowired
    private IAlmacenamiento almacenamiento;

    @Transactional // Garantiza que todo se guarde o nada se guarde
    public boolean servRegistrarSolicitud(SolicitudCreateDTO dto,ArrayList <Adjunto> adjuntos){
         boolean registroExitoso = false;
        //AccesoService.validarUsuario(dto.getusuarioId());
        //CatalogoService.obetnerRequisitos(servicioID);
        registroExitoso = validarDatosYAdjuntos();
        //llamada a guardar adjuntos a sistema de almacenamiento
        almacenamiento.guardarAdjunto();

        Usuario solicitante = obtenerOCrearUsuario(dto.getUsuarioId());

        Solicitud solicitud = new Solicitud();
        // solicitud.setTipo(dto.getTipo());
        // solicitud.setCatalogoId(dto.getCatalogoId());
        // solicitud.setDescripcion(dto.getDescripcion());
        solicitud.setPrioridad(dto.getPrioridad());
    
         //Logica para cacular sla
         solicitud.calcularSLA();
         
         solicitud.setSolicitante(solicitante);
         solicitud.setEstado("POR REVISAR");
         solicitud.crear();


         // 5. LO QUE FALTA: Procesar y vincular adjuntos
        if (adjuntos != null && !adjuntos.isEmpty()) {
            for (Adjunto adj : adjuntos) {
                // Usas tu método 'agregarAdjunto' que ya tienes
                solicitud.agregarAdjunto(adj); 
                
                // Si el almacenamiento es físico, podrías llamar aquí a:
                // almacenamiento.guardarAdjunto(adj); 
            }
        }
         



         repoSolicitud.save(solicitud);

         return registroExitoso;
   
    }


    private boolean validarDatosYAdjuntos(){
        //lógica de validación
         return true;
    }

    private Usuario obtenerOCrearUsuario(int usuarioId) {

    // boolean existeEnSistemaCentral = accesoService.validarUsuario(usuarioId);

    // if (!existeEnSistemaCentral) {
    //     throw new RuntimeException("Usuario no válido en sistema central");
    // }

    return repoUsuario.findById(usuarioId)
            .orElseGet(() -> crearUsuarioDesdeSistemaCentral(usuarioId));
    }

    private Usuario crearUsuarioDesdeSistemaCentral(int usuarioId) {
        //UsuarioDTO datos = accesoService.obtenerDatosUsuario(usuarioId);
        Usuario nuevo = new Usuario(/*datos*/);
        return repoUsuario.save(nuevo);
    }

    public List<Solicitud> servObtenerHistorial(Integer usuarioID){ 
         return repoSolicitud.findBySolicitanteIdUsuario(usuarioID);
     }


     public Solicitud obtenerDetalleCompleto(int solicitudId){
         Solicitud solicitud = repoSolicitud.findById(solicitudId).orElse(null);
         //lógica para calulcar estado de sla
         return solicitud;
     }


     public boolean anularSolicitud(int solicitudId){
         boolean anulacionExitosa=false;
        repoSolicitud.deleteById(solicitudId);
         //logica de anulación

         anulacionExitosa=true;
         return anulacionExitosa;
     }




     public IUsuarioRepository getRepoUsuario() {
        return repoUsuario;
    }


    public void setRepoUsuario(IUsuarioRepository repoUsuario) {
        this.repoUsuario = repoUsuario;
    }



}


