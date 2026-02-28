package com.campus360.solicitudes.Servicios;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.campus360.solicitudes.Client.AprobacionesClient;
import com.campus360.solicitudes.Client.CatalogoClient;
import com.campus360.solicitudes.Client.PagoClient;
import com.campus360.solicitudes.DTOs.ActualizarSolicitudDTO;
import com.campus360.solicitudes.DTOs.GenerarOrdenPagoRequest;
import com.campus360.solicitudes.DTOs.OrdenPagoResponse;
import com.campus360.solicitudes.DTOs.RequisitoDTO;
import com.campus360.solicitudes.DTOs.ServicioInfoResponse;
import com.campus360.solicitudes.DTOs.SolicitudAprobacionDTO;
import com.campus360.solicitudes.DTOs.SolicitudCreateDTO;
import com.campus360.solicitudes.DTOs.SolicitudDTO;
import com.campus360.solicitudes.Dominio.Adjunto;
import com.campus360.solicitudes.Dominio.HistorialEstado;
import com.campus360.solicitudes.Dominio.Solicitud;
import com.campus360.solicitudes.Dominio.SolicitudServicio;
import com.campus360.solicitudes.Dominio.SolicitudTramite;
import com.campus360.solicitudes.Dominio.Usuario;
import com.campus360.solicitudes.Repositorio.IAlmacenamiento;
import com.campus360.solicitudes.Repositorio.ISolicitudRepository;
import com.campus360.solicitudes.Repositorio.IUsuarioRepository;
import com.campus360.solicitudes.Servicios.sla.ISlaStrategy;
import com.campus360.solicitudes.Servicios.sla.SlaStrategyFactory;


import jakarta.transaction.Transactional;

@Service
public class SolicitudService implements ISolicitudService/*, ISolicitudQueryService*/ {

    @Autowired
    private ISolicitudRepository repoSolicitud;
    @Autowired
    private IUsuarioRepository repoUsuario;
    @Autowired
    private IAlmacenamiento almacenamiento;

    // Inyección de Clientes Externos
    @Autowired
    private CatalogoClient catalogoClient;
    @Autowired
    private PagoClient pagoClient;
    @Autowired
    private AprobacionesClient aprobacionesClient;


    public SolicitudService() {
    }
    public SolicitudService(ISolicitudRepository repoSolicitud, IUsuarioRepository repoUsuario, IAlmacenamiento almacenamiento,
                            CatalogoClient catalogoClient, PagoClient pagoClient, AprobacionesClient aprobacionesClient) {
        this.repoSolicitud = repoSolicitud;
        this.repoUsuario = repoUsuario;
        this.almacenamiento = almacenamiento;
        this.catalogoClient = catalogoClient;
        this.pagoClient = pagoClient;
        this.aprobacionesClient = aprobacionesClient;
    }

@Transactional
    public boolean servRegistrarSolicitud(int usuarioID, String nombre, String rol, SolicitudCreateDTO dto, ArrayList<Adjunto> adjuntos, List<MultipartFile> archivos) {
        
        // 1. Obtener información del catálogo y validar requisitos
        ServicioInfoResponse servicioInfo = catalogoClient.obtenerServicio(dto.getServicioId());
        if (servicioInfo == null || !servicioInfo.isActivo()) {
            throw new RuntimeException("El servicio solicitado no existe o no está activo");
        }

        // 2. Validación dinámica de requisitos
        if (servicioInfo.getRequisitos() != null) {
            for (RequisitoDTO req : servicioInfo.getRequisitos()) {
                
                // Solo validamos si el requisito es obligatorio
                if (req.isObligatorio()) {
                    
                    // Caso A: El requisito es un archivo (FILE / ARCHIVO)
                    if ("FILE".equalsIgnoreCase(req.getTipo()) || "ARCHIVO".equalsIgnoreCase(req.getTipo())) {
                        // Verificamos que la lista de MultipartFiles contenga al menos un archivo
                        if (archivos == null || archivos.isEmpty()) {
                            throw new RuntimeException("Falta adjuntar el archivo obligatorio: " + req.getCampo());
                        }
                    } 
                    
                    // Caso B: El requisito es un dato de texto (como Sede de Recojo o Hora de Alquiler)
                    else if ("STRING".equalsIgnoreCase(req.getTipo()) || "DATETIME".equalsIgnoreCase(req.getTipo())) {
                        // Verificamos que el campo descripción del DTO no esté vacío
                        if (dto.getDescripcion() == null || dto.getDescripcion().isBlank()) {
                            throw new RuntimeException("Debe especificar '" + req.getCampo() + "' en la descripción de la solicitud.");
                        }
                    }
                }
            }
        }

        // 3. OBTENER USUARIO (Modificado)
        // Usamos getReferenceById si ya existe para evitar cargar todo el objeto y sus colecciones, 
        // esto evita el error de "Row was already updated"
        Usuario solicitante;
        if (repoUsuario.existsById(usuarioID)) {
            solicitante = repoUsuario.getReferenceById(usuarioID);
        } else {
            Usuario nuevo = new Usuario();
            nuevo.setIdUsuario(usuarioID);
            nuevo.setNombre(nombre); 
            nuevo.setRol(rol);
            // Guardamos pero NO usamos flush aquí para dejar que la transacción gestione el cierre
            solicitante = repoUsuario.save(nuevo);
        }
        boolean esServicio = servicioInfo.getRequisitos().stream()
            .anyMatch(r -> "DATE".equalsIgnoreCase(r.getTipo()) || "DATETIME".equalsIgnoreCase(r.getTipo()));

        Solicitud solicitud;
        if (esServicio) {
            SolicitudServicio ss = new SolicitudServicio();
            ss.setTipoServicio(servicioInfo.getNombre());
            // Seteamos una fecha por defecto o la extraída de algún lugar
            ss.setFechaSolicitada(new Date()); 
            solicitud = ss;
        } else {
            SolicitudTramite st = new SolicitudTramite();
            st.setTipoTramite(servicioInfo.getNombre());
            solicitud = st;
        }
        solicitud.setDescripcion(dto.getDescripcion());
        solicitud.setPrioridad(dto.getPrioridad());
        solicitud.setSolicitante(solicitante);
        solicitud.setEstado("PENDIENTE");

        // Calcular SLA según prioridad
        ISlaStrategy strategy = SlaStrategyFactory.obtenerStrategy(solicitud.getPrioridad());
        strategy.aplicarSla(solicitud);

        // Vincular adjuntos a la solicitud
        if (adjuntos != null) {
            for (Adjunto adj : adjuntos) {
                adj.setSolicitud(solicitud);
                solicitud.agregarAdjunto(adj);
            }
        }

        // 4. Persistencia inicial para obtener ID
        solicitud.crear();
        solicitud = repoSolicitud.save(solicitud);

        // 5. Lógica de Pago (si el costo es mayor a 0)
        if (servicioInfo.getCosto() != null && servicioInfo.getCosto().compareTo(BigDecimal.ZERO) > 0) {
            GenerarOrdenPagoRequest pagoRequest = new GenerarOrdenPagoRequest();
            pagoRequest.setSolicitudId( solicitud.getIdSolicitud());
            pagoRequest.setMonto(servicioInfo.getCosto());    
            // Solo notificamos y esperamos confirmación de recepción
            if (pagoClient.generarOrden(pagoRequest)) {
                solicitud.setEstado("PENDIENTE_PAGO");
            } else {
                throw new RuntimeException("El módulo de pagos no pudo procesar la solicitud.");
            }
        }

        // 6. Notificar al módulo de Aprobaciones
        SolicitudAprobacionDTO aprobacionDTO = new SolicitudAprobacionDTO();
        aprobacionDTO.setIdSolicitud(solicitud.getIdSolicitud());
        aprobacionDTO.setEstado(solicitud.getEstado());
        aprobacionDTO.setFechaCreacion(new Date());
        aprobacionDTO.setNombreSolicitante(solicitante.getNombre()); // Asumiendo que Usuario tiene getNombre

        boolean enviadoAprobaciones = aprobacionesClient.enviarSolicitudConAdjuntos(aprobacionDTO, archivos);

        if (!enviadoAprobaciones) {
            // Dependiendo de la regla de negocio, podrías lanzar excepción para hacer rollback
            throw new RuntimeException("No se pudo sincronizar la solicitud con el módulo de aprobaciones");
        }

        return true;
    }

    // @Transactional // Garantiza que todo se guarde o nada se guarde
    // public boolean servRegistrarSolicitud(int usuarioID, SolicitudCreateDTO dto,ArrayList <Adjunto> adjuntos){
    //      boolean registroExitoso = false;

    //     //CatalogoService.obetnerRequisitos(servicioID)
    //     registroExitoso = validarDatosYAdjuntos();
    //     //llamada a guardar adjuntos a sistema de almacenamiento
    //     almacenamiento.guardarAdjunto();

    //     Usuario solicitante = obtenerOCrearUsuario(usuarioID);

    //     Solicitud solicitud = new Solicitud();
    //     // solicitud.setTipo(dto.getTipo());
    //     // solicitud.setCatalogoId(dto.getCatalogoId());
    //     solicitud.setDescripcion(dto.getDescripcion());
    //     solicitud.setPrioridad(dto.getPrioridad());
    
    //     //Logica para cacular sla
    //     ISlaStrategy strategy = SlaStrategyFactory.obtenerStrategy(solicitud.getPrioridad());
    //     strategy.aplicarSla(solicitud);

         
    //      solicitud.setSolicitante(solicitante);
    //      solicitud.setEstado("PENDIENTE");
    //      solicitud.crear();


    //      // 5. LO QUE FALTA: Procesar y vincular adjuntos
    //     if (adjuntos != null && !adjuntos.isEmpty()) {
    //         for (Adjunto adj : adjuntos) {
    //             adj.setSolicitud(solicitud);
    //             // Usas tu método 'agregarAdjunto' que ya tienes
    //             solicitud.agregarAdjunto(adj); 
                
    //             // Si el almacenamiento es físico, podrías llamar aquí a:
    //             // almacenamiento.guardarAdjunto(adj); 
    //         }
    //     }




    //      repoSolicitud.save(solicitud);
         

    //      return registroExitoso;
   
    // }


    private Usuario obtenerOCrearUsuario(int usuarioId, String nombre, String rol) {
        return repoUsuario.findById(usuarioId)
                    .orElseGet(() -> {
                        Usuario nuevo = new Usuario();
                        nuevo.setIdUsuario(usuarioId);
                        nuevo.setNombre(nombre); 
                        nuevo.setRol(rol);
                        return repoUsuario.saveAndFlush(nuevo);
                    });
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
            solicitud.actualizarSeguimientoSLA("EN PROCESO");

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

    public boolean servAnularSolicitud(int solicitudId, int usuarioID){
         
        Solicitud porEliminar = repoSolicitud.findById(solicitudId).orElse(null);

        //Validar que la solicitud existe
        if(porEliminar == null){
            return false;
        }
        //Validar que es la solicitud correcta del usuario
        if(porEliminar.getSolicitante().getIdUsuario() != usuarioId) {
            return false; //No es el dueño de la solicitud
        }
        
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


    
    public String guardarArchivoEnDisco(MultipartFile file) {
            try {
                // 1. Definimos la carpeta "uploads" dentro de la raíz del proyecto
                String rootPath = System.getProperty("user.dir");
                String nombreCarpeta = "uploads";

                // 2. Creamos el objeto File para la subcarpeta
                File directory = new File(rootPath, nombreCarpeta);

                // 3. Si la subcarpeta no existe, la creamos
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // 4. Generar nombre único (Timestamp + nombre original)
                String nombreUnico = System.currentTimeMillis() + "_" + file.getOriginalFilename();

                // 5. Usamos Paths.get con dos argumentos para que Java ponga el "/" o "\" correcto
                Path rutaDestino = Paths.get(directory.getAbsolutePath(), nombreUnico);

                // 6. Escribimos los bytes del archivo
                Files.write(rutaDestino, file.getBytes());

                // 7. Retornamos la ruta absoluta para guardarla en la base de datos
                return rutaDestino.toString();

            } catch (IOException e) {
                throw new RuntimeException("Error al escribir el archivo en el servidor: " + e.getMessage());
            }
    }



    //Esta función llama a guardarArchivosEnDisco (arriba)
    public List<Adjunto> procesarArchivos(List<MultipartFile> archivos, Solicitud solicitud) {
    List<Adjunto> lista = new ArrayList<>();
    
    if (archivos != null && !archivos.isEmpty()) {
        for (MultipartFile archivo : archivos) {
            if (!archivo.isEmpty()) {
                // Llamamos a tu lógica de escritura física
                String rutaEnDisco = guardarArchivoEnDisco(archivo);
                
                // Creamos el objeto para la Base de Datos
                Adjunto adj = new Adjunto();
                adj.setNombreArchivo(archivo.getOriginalFilename());
                adj.setRuta(rutaEnDisco); // Guardamos el String de la ruta
                adj.setTipoArchivo(archivo.getContentType());
                adj.setSolicitud(solicitud); // Vinculamos a la solicitud actual
                
                lista.add(adj);
            }
        }
    }
        return lista;
    }

    public boolean servActualizarSolicitud(Integer idSolicitud,ActualizarSolicitudDTO dto, String rol, List<MultipartFile> nuevosAdjuntos){
        Solicitud solicitud = repoSolicitud.findById(idSolicitud).orElse(null);
        
        String estadoAnterior = solicitud.getEstado();
        String nuevoEstado = dto.getEstado();
        
        // 1. VALIDACIÓN DE PERMISOS POR ROL
                if ("ESTUDIANTE".equalsIgnoreCase(rol)) {
                    // El estudiante SOLO puede corregir si la solicitud está OBSERVADA
                    if (!"OBSERVADO".equalsIgnoreCase(solicitud.getEstado())) {
                        return false; 
                    }
                    // Forzamos que el estado pase a PENDIENTE al subsanar
                    nuevoEstado = "PENDIENTE";
                } 
                else if (!"APROBADOR".equalsIgnoreCase(rol)) {
                    // Si no es ni estudiante ni aprobador, no tiene permiso
                    return false;
                }

                List<Adjunto> newAdjuntos = procesarArchivos(nuevosAdjuntos, solicitud);
                solicitud.getAdjuntos().addAll(newAdjuntos);

                // 2. ACTUALIZACIÓN INTELIGENTE DEL SLA
                // Aquí es donde el tiempo se reanuda y la fecha límite se "empuja" hacia adelante
                solicitud.actualizarSeguimientoSLA(nuevoEstado);


                // Agregamos nuevos archivos si existen
                if (nuevosAdjuntos != null) {
                    for (Adjunto adj : newAdjuntos) {
                        solicitud.agregarAdjunto(adj);
                    }
                }

                // 3. REGISTRO EN EL HISTORIAL
                HistorialEstado h = new HistorialEstado();
                h.setEstadoAnterior(estadoAnterior);
                h.setEstadoNuevo(nuevoEstado);
                h.setComentario(dto.getComentario());
                h.setFechaCambio(new Date());
                h.setSolicitud(solicitud);

                solicitud.getHistorial().add(h);

                // 4. PERSISTENCIA
                repoSolicitud.save(solicitud);
                return true;
     }
     
    // public boolean servActualizarSolicitud(Integer idSolicitud,ActualizarSolicitudDTO dto, String rol){
    //     Solicitud solicitud = repoSolicitud.findById(idSolicitud).orElse(null);
    //     if ("APROBADOR".equalsIgnoreCase(rol)){
    //         solicitud.setEstado(dto.getEstado());

    //         //Se guarda en el historial
    //         HistorialEstado h = new HistorialEstado();
    //         h.setEstadoAnterior(solicitud.getEstado());
    //         h.setEstadoNuevo(dto.getEstado());
    //         h.setComentario(dto.getComentario());
    //         h.setFechaCambio(new Date());
    //         h.setSolicitud(solicitud);

    //         solicitud.getHistorial().add(h);
            
    //         repoSolicitud.save(solicitud);
    //         return true;
    //     }
    //     else{
    //         return false;
    //     }

    // }

    public IUsuarioRepository getRepoUsuario() {
        return repoUsuario;
    }


    public void setRepoUsuario(IUsuarioRepository repoUsuario) {
        this.repoUsuario = repoUsuario;
    }



}


