package com.campus360.solicitudes.Servicios;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.campus360.solicitudes.DTOs.ActualizarSolicitudDTO;
import com.campus360.solicitudes.DTOs.SolicitudCreateDTO;
import com.campus360.solicitudes.DTOs.SolicitudDTO;
import com.campus360.solicitudes.Dominio.Adjunto;
import com.campus360.solicitudes.Dominio.HistorialEstado;
import com.campus360.solicitudes.Dominio.Solicitud;
import com.campus360.solicitudes.Dominio.Usuario;
import com.campus360.solicitudes.Repositorio.IAlmacenamiento;
import com.campus360.solicitudes.Repositorio.ISolicitudRepository;
import com.campus360.solicitudes.Repositorio.IUsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class SolicitudService implements ISolicitudService/*, ISolicitudQueryService*/ {

    @Autowired
    private ISolicitudRepository repoSolicitud;
    @Autowired
    private IUsuarioRepository repoUsuario;

    
    @Autowired
    private IAlmacenamiento almacenamiento;

    @Transactional // Garantiza que todo se guarde o nada se guarde
    public boolean servRegistrarSolicitud(SolicitudCreateDTO dto,ArrayList <Adjunto> adjuntos){
         boolean registroExitoso = false;

        //CatalogoService.obetnerRequisitos(servicioID)
        registroExitoso = validarDatosYAdjuntos();
        //llamada a guardar adjuntos a sistema de almacenamiento
        almacenamiento.guardarAdjunto();

        Usuario solicitante = obtenerOCrearUsuario(dto.getUsuarioId());

        Solicitud solicitud = new Solicitud();
        // solicitud.setTipo(dto.getTipo());
        // solicitud.setCatalogoId(dto.getCatalogoId());
        solicitud.setDescripcion(dto.getDescripcion());
        solicitud.setPrioridad(dto.getPrioridad());
    
         //Logica para cacular sla
         solicitud.calcularSLA();
         
         solicitud.setSolicitante(solicitante);
         solicitud.setEstado("PENDIENTE");
         solicitud.crear();


         // 5. LO QUE FALTA: Procesar y vincular adjuntos
        if (adjuntos != null && !adjuntos.isEmpty()) {
            for (Adjunto adj : adjuntos) {
                adj.setSolicitud(solicitud);
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

    public List<SolicitudDTO> servObtenerHistorial(Integer usuarioID){ 
        List<Solicitud> solicitudes = repoSolicitud.findBySolicitanteIdUsuario(usuarioID);
         return solicitudes.stream()
                      .map(sol -> new SolicitudDTO(sol))
                      .collect(Collectors.toList());
    }

    public SolicitudDTO obtenerDetalleCompleto(int solicitudId, String rol){
        
        Solicitud solicitud = repoSolicitud.findById(solicitudId).orElse(null);
        if (solicitud != null) {
        // REGLA DE NEGOCIO: Si es APROBADOR y el estado es PENDIENTE
        // Usamos .equalsIgnoreCase por si el frontend manda "aprobador" en minúsculas
        if ("APROBADOR".equalsIgnoreCase(rol) && "PENDIENTE".equalsIgnoreCase(solicitud.getEstado())) {
            
            solicitud.setEstado("EN PROCESO");
            
            
            // Opcional: Aquí podrías registrar en tu tabla de historial
            HistorialEstado h = new HistorialEstado();
            h.setEstadoAnterior("PENDIENTE");
            h.setEstadoNuevo("EN PROCESO");
            h.setComentario("Visto por el aprobador");
            h.setFechaCambio(new Date());
            h.setSolicitud(solicitud);

            solicitud.getHistorial().add(h);

            repoSolicitud.save(solicitud);

            System.out.println("Estado actualizado a EN PROCESO por acceso de Aprobador");
        }
         //lógica para calulcar estado de sla
         return new SolicitudDTO(solicitud);
    }                      

       return null; 
    }

    public Solicitud obtenerDetalleCompleto(int solicitudId){
        Solicitud solicitud = repoSolicitud.findById(solicitudId).orElse(null);
        //lógica para calulcar estado de sla
        return solicitud;
    }

    public boolean servAnularSolicitud(int solicitudId){
         
        Solicitud porEliminar=repoSolicitud.findById(solicitudId).orElse(null);

        String estado=porEliminar.getEstado();


         if("PENDIENTE".equals(estado)){
            repoSolicitud.deleteById(solicitudId);
            return true;
         }
         else{
            
            return false;
            
         }
                 
     }

    public List<SolicitudDTO> servListarSolicitudes(){
        List<Solicitud> solicitudes = repoSolicitud.findAll();

        return solicitudes.stream()
                      .map(s -> new SolicitudDTO(s))
                      .collect(Collectors.toList());
        
    }


    public boolean servActualizarSolicitud(Integer idSolicitud,ActualizarSolicitudDTO dto, String rol){
        Solicitud solicitud = repoSolicitud.findById(idSolicitud).orElse(null);
        if ("APROBADOR".equalsIgnoreCase(rol)){
            solicitud.setEstado(dto.getEstado());

            //Se guarda en el historial
            HistorialEstado h = new HistorialEstado();
            h.setEstadoAnterior(solicitud.getEstado());
            h.setEstadoNuevo(dto.getEstado());
            h.setComentario(dto.getComentario());
            h.setFechaCambio(new Date());
            h.setSolicitud(solicitud);

            solicitud.getHistorial().add(h);
            
            repoSolicitud.save(solicitud);
            return true;
        }
        else{
            return false;
        }

    }

    public IUsuarioRepository getRepoUsuario() {
        return repoUsuario;
    }


    public void setRepoUsuario(IUsuarioRepository repoUsuario) {
        this.repoUsuario = repoUsuario;
    }



}


