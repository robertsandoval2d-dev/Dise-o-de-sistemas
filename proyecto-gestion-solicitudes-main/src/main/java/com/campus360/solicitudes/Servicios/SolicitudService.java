package com.campus360.solicitudes.Servicios;


// import java.math.BigDecimal;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

 
import com.campus360.solicitudes.Adapter.IAprobacionesAdapter; 
import com.campus360.solicitudes.Adapter.ICatalogoAdapter; 
import com.campus360.solicitudes.Adapter.IPagoAdapter;
import com.campus360.solicitudes.DTOs.ActualizarSolicitudDTO;


import com.campus360.solicitudes.DTOs.ServicioInfoResponse;

import com.campus360.solicitudes.DTOs.SolicitudCreateDTO;
import com.campus360.solicitudes.DTOs.SolicitudDTO;
import com.campus360.solicitudes.Dominio.Adjunto;
import com.campus360.solicitudes.Dominio.HistorialEstado;
import com.campus360.solicitudes.Dominio.Solicitud;

import com.campus360.solicitudes.Dominio.Usuario;

import com.campus360.solicitudes.Repositorio.ISolicitudRepository;
import com.campus360.solicitudes.Repositorio.IUsuarioRepository;

import com.campus360.solicitudes.Servicios.util.AdjuntoProcessor;
import com.campus360.solicitudes.Servicios.util.SolicitudFactoryManager;
import com.campus360.solicitudes.Servicios.util.SolicitudValidator;
import com.campus360.solicitudes.Servicios.util.UsuarioManager;
import com.campus360.solicitudes.Factory.ISolicitudFactory;

import org.springframework.beans.factory.annotation.Qualifier;
import jakarta.transaction.Transactional;

@Service
public class SolicitudService implements ISolicitudService/*, ISolicitudQueryService*/ {

    @Autowired
    private ISolicitudRepository repoSolicitud;
    @Autowired
    private IUsuarioRepository repoUsuario;
    // @Autowired
    // private IAlmacenamiento almacenamiento;

    // Inyección de Clientes Externos
    @Autowired
    private ICatalogoAdapter catalogoAdapter;
    @Autowired
    private IPagoAdapter pagoAdapter;
    @Autowired
    private IAprobacionesAdapter aprobacionesAdapter;

    // Inyección de las dos fábricas
    @Autowired
    @Qualifier("servicioFactory")
    private ISolicitudFactory servicioFactory;
    @Autowired
    @Qualifier("tramiteFactory")
    private ISolicitudFactory tramiteFactory;

    //Inyección de componentes
    @Autowired private SolicitudValidator validator;
    @Autowired private UsuarioManager usuarioManager;
    @Autowired private SolicitudFactoryManager factoryManager;
    @Autowired private AdjuntoProcessor adjuntoProcessor;




    public SolicitudService() {
    }
    public SolicitudService(ISolicitudRepository repoSolicitud, IUsuarioRepository repoUsuario,
                            ICatalogoAdapter catalogoAdapter, IPagoAdapter pagoAdapter, IAprobacionesAdapter aprobacionesAdapter) {
        this.repoSolicitud = repoSolicitud;
        this.repoUsuario = repoUsuario;
        // this.almacenamiento = almacenamiento;
        this.catalogoAdapter = catalogoAdapter;
        this.pagoAdapter = pagoAdapter;
        this.aprobacionesAdapter = aprobacionesAdapter;
    }

    @Transactional
    public boolean servRegistrarSolicitud(int usuarioID, String nombre, String rol, SolicitudCreateDTO dto, List<MultipartFile> archivos) {
        
        // 1. Obtener y validar el servicio del catálogo
        ServicioInfoResponse servicioInfo = catalogoAdapter.obtenerServicioActivo(dto.getServicioId());
        
        validator.validarRequisitosDinamicos(servicioInfo, dto, archivos);
       

        // 2. Gestionar el solicitante (Obtener existente o crear nuevo)
        Usuario solicitante = usuarioManager.obtenerOGuardarUsuario(usuarioID, nombre, rol);

        // 3. Construir la solicitud usando Factory y Strategy (SLA)
        Solicitud solicitud = factoryManager.crearInstanciaSolicitud(servicioInfo, dto, solicitante);

        // LLAMADA LIMPIA AL PROCESADOR DE ARCHIVOS
        adjuntoProcessor.vincularAdjuntos(solicitud, archivos);

        // 4. Persistir la solicitud
        solicitud = repoSolicitud.save(solicitud);

        // 5. Procesos Externos (Pagos y Aprobaciones)
        if (!pagoAdapter.procesarPago(solicitud.getIdSolicitud(), servicioInfo.getCosto())) { 
            throw new RuntimeException("El módulo de pagos no pudo procesar la solicitud."); }

        if (!aprobacionesAdapter.sincronizarSolicitud(solicitud, solicitante, archivos)) {
        throw new RuntimeException("Fallo en la sincronización de aprobaciones");
    }

        return true;
    }

    // --- MÉTODOS PRIVADOS DE APOYO ---
    // private ServicioInfoResponse buscarServicioActivo(int servicioId) {
    //     // Toda la validación de nulos y de activo ahora la hace el Adapter 
    //         return catalogoAdapter.obtenerServicioActivo(servicioId);
    // }

   
    // private void validarRequisitosDinamicos(ServicioInfoResponse info, SolicitudCreateDTO dto, List<MultipartFile> archivos) {
    //     if (info.getRequisitos() == null) return;
    //     // 1. Contamos cuántos archivos OBLIGATORIOS pide el catálogo
    //     long archivosRequeridos = info.getRequisitos().stream()
    //         .filter(req -> req.isObligatorio() && 
    //                 ("FILE".equalsIgnoreCase(req.getTipo()) || "ARCHIVO".equalsIgnoreCase(req.getTipo())))
    //         .count();

    //     // 2. Contamos cuántos archivos REALES (no vacíos) envió el usuario
    //     long archivosEnviados = 0;
    //     if (archivos != null) {
    //         archivosEnviados = archivos.stream()
    //             .filter(f -> !f.isEmpty() && f.getOriginalFilename() != null && !f.getOriginalFilename().isBlank())
    //             .count();
    //     }

    //     // 3. Validación de cantidad
    //     if (archivosEnviados < archivosRequeridos) {
    //         throw new RuntimeException("Faltan archivos adjuntos. Se requieren " + archivosRequeridos + 
    //                                 " archivos obligatorios, pero se recibieron " + archivosEnviados + ".");
    //     }
    //     // 4. Validación de campos de texto (se mantiene igual)
    //     for (RequisitoDTO req : info.getRequisitos()) {
    //         if (req.isObligatorio() && ("STRING".equalsIgnoreCase(req.getTipo()) || "DATETIME".equalsIgnoreCase(req.getTipo()))) {
    //             if (dto.getFechaProgramada() == null || (dto.getDescripcion() == null || dto.getDescripcion().isBlank())) {
    //                 throw new RuntimeException("Debe especificar '" + req.getCampo() + "' en la descripción.");
    //             }
    //         }
    //     }
    // }




    // private Usuario obtenerOGuardarUsuario(int id, String nombre, String rol) {
    //     if (repoUsuario.existsById(id)) {
    //         return repoUsuario.getReferenceById(id);
    //     }
    //     Usuario nuevo = new Usuario();
    //     nuevo.setIdUsuario(id);
    //     nuevo.setNombre(nombre);
    //     nuevo.setRol(rol);
    //     return repoUsuario.save(nuevo);
    // }




    // private Solicitud crearInstanciaSolicitud(ServicioInfoResponse info, SolicitudCreateDTO dto, Usuario solicitante) {
    //     boolean esServicio = info.getRequisitos().stream()
    //             .anyMatch(r -> "DATE".equalsIgnoreCase(r.getTipo()) || "DATETIME".equalsIgnoreCase(r.getTipo()));

    //     ISolicitudFactory fabrica = esServicio ? servicioFactory : tramiteFactory;
    //     Solicitud solicitud = fabrica.crear(info, dto);

    //     solicitud.setSolicitante(solicitante);
    //     solicitud.setEstado("PENDIENTE");

    //     SlaStrategyFactory.obtenerStrategy(solicitud.getPrioridad()).aplicarSla(solicitud);
        
    //     return solicitud;
    // }


    
    // private void vincularAdjuntos(Solicitud solicitud, List<MultipartFile> archivos) {
    //     List<Adjunto> adjuntos = procesarArchivos(archivos, solicitud);
    //     if (adjuntos != null) {
    //         solicitud.setAdjuntos(adjuntos);
    //     }
    // }
    
    
    // private void gestionarOrdenDePago(Solicitud solicitud, BigDecimal costo) { 
    //     // El Adapter ahora se encarga de verificar si el costo es cero y armar el Request 
    //     if (!pagoAdapter.procesarPago(solicitud.getIdSolicitud(), costo)) { 
    //         throw new RuntimeException("El módulo de pagos no pudo procesar la solicitud."); } }

    
    // private void sincronizarConAprobaciones(Solicitud solicitud, Usuario solicitante, List<MultipartFile> archivos) { 
    //     SolicitudAprobacionDTO dto = new SolicitudAprobacionDTO(); 
    //     dto.setIdSolicitud(solicitud.getIdSolicitud()); 
    //     dto.setEstado(solicitud.getEstado()); 
    //     dto.setFechaCreacion(new Date()); 
    //     dto.setNombreSolicitante(solicitante.getNombre()); 
    //     // Llamada limpia al Adapter 
    //     if (!aprobacionesAdapter.sincronizarSolicitud(dto, archivos)) { 
    //         throw new RuntimeException("No se pudo sincronizar con el módulo de aprobaciones");
    //     } 
    // }

// @Transactional
//     public boolean servRegistrarSolicitud(int usuarioID, String nombre, String rol, SolicitudCreateDTO dto, List<MultipartFile> archivos) {
        
//         // 1. Obtener información del catálogo y validar requisitos
//         ServicioInfoResponse servicioInfo = catalogoClient.obtenerServicio(dto.getServicioId());
//         if (servicioInfo == null || !servicioInfo.isActivo()) {
//             throw new RuntimeException("El servicio solicitado no existe o no está activo");
//         }

//         // 2. Validación dinámica de requisitos
//         if (servicioInfo.getRequisitos() != null) {
//             for (RequisitoDTO req : servicioInfo.getRequisitos()) {
                
//                 // Solo validamos si el requisito es obligatorio
//                 if (req.isObligatorio()) {
                    
//                     // Caso A: El requisito es un archivo (FILE / ARCHIVO)
//                     if ("FILE".equalsIgnoreCase(req.getTipo()) || "ARCHIVO".equalsIgnoreCase(req.getTipo())) {
//                         // Verificamos que la lista de MultipartFiles contenga al menos un archivo
//                         if (archivos == null || archivos.isEmpty()) {
//                             throw new RuntimeException("Falta adjuntar el archivo obligatorio: " + req.getCampo());
//                         }
//                     } 
                    
//                     // Caso B: El requisito es un dato de texto (como Sede de Recojo o Hora de Alquiler)
//                     else if ("STRING".equalsIgnoreCase(req.getTipo()) || "DATETIME".equalsIgnoreCase(req.getTipo())) {
//                         // Verificamos que el campo descripción del DTO no esté vacío
//                         if (dto.getDescripcion() == null || dto.getDescripcion().isBlank()) {
//                             throw new RuntimeException("Debe especificar '" + req.getCampo() + "' en la descripción de la solicitud.");
//                         }
//                     }
//                 }
//             }
//         }

//         // 3. OBTENER USUARIO (Modificado)
//         // Usamos getReferenceById si ya existe para evitar cargar todo el objeto y sus colecciones, 
//         // esto evita el error de "Row was already updated"
//         Usuario solicitante;
//         if (repoUsuario.existsById(usuarioID)) {
//             solicitante = repoUsuario.getReferenceById(usuarioID);
//         } else {
//             Usuario nuevo = new Usuario();
//             nuevo.setIdUsuario(usuarioID);
//             nuevo.setNombre(nombre); 
//             nuevo.setRol(rol);
//             // Guardamos pero NO usamos flush aquí para dejar que la transacción gestione el cierre
//             solicitante = repoUsuario.save(nuevo);
//         }
//         // Si existe algún requisito de tipo fecha, determinamos que el flujo es para un "Servicio"
//         boolean esServicio = servicioInfo.getRequisitos().stream()
//          .anyMatch(r -> "DATE".equalsIgnoreCase(r.getTipo()) || "DATETIME".equalsIgnoreCase(r.getTipo()));

//         // Aplicamos el patrón Factory Method para no usar 'new' directamente.
//         ISolicitudFactory fabricaElegida = esServicio ? servicioFactory : tramiteFactory;
//         //Obtenemos un objeto 'Solicitud' sin que el Service necesite saber su clase hija exacta.
//         Solicitud solicitud = fabricaElegida.crear(servicioInfo);
        
//         solicitud.setDescripcion(dto.getDescripcion());
//         solicitud.setPrioridad(dto.getPrioridad());
//         solicitud.setSolicitante(solicitante);
//         solicitud.setEstado("PENDIENTE");

//         // Calcular SLA según prioridad
//         ISlaStrategy strategy = SlaStrategyFactory.obtenerStrategy(solicitud.getPrioridad());
//         strategy.aplicarSla(solicitud);

//         List<Adjunto> adjuntos = procesarArchivos(archivos, solicitud);

//         // Vincular adjuntos a la solicitud
//         if (adjuntos != null) {
//             solicitud.setAdjuntos(adjuntos);
//         }

//         // 4. Persistencia inicial para obtener ID
//         solicitud.crear();
//         solicitud = repoSolicitud.save(solicitud);

//         // 5. Lógica de Pago (si el costo es mayor a 0)
//         if (servicioInfo.getCosto() != null && servicioInfo.getCosto().compareTo(BigDecimal.ZERO) > 0) {
//             GenerarOrdenPagoRequest pagoRequest = new GenerarOrdenPagoRequest();
//             pagoRequest.setSolicitudId( solicitud.getIdSolicitud());
//             pagoRequest.setMonto(servicioInfo.getCosto());    
//             // Solo notificamos y esperamos confirmación de recepción
//             if (pagoClient.generarOrden(pagoRequest)) {
//                 solicitud.setEstado("PENDIENTE");
//             } else {
//                 throw new RuntimeException("El módulo de pagos no pudo procesar la solicitud.");
//             }
//         }

//         // 6. Notificar al módulo de Aprobaciones
//         SolicitudAprobacionDTO aprobacionDTO = new SolicitudAprobacionDTO();
//         aprobacionDTO.setIdSolicitud(solicitud.getIdSolicitud());
//         aprobacionDTO.setEstado(solicitud.getEstado());
//         aprobacionDTO.setFechaCreacion(new Date());
//         aprobacionDTO.setNombreSolicitante(solicitante.getNombre()); // Asumiendo que Usuario tiene getNombre

//         boolean enviadoAprobaciones = aprobacionesClient.enviarSolicitudConAdjuntos(aprobacionDTO, archivos);

//         if (!enviadoAprobaciones) {
//             // Dependiendo de la regla de negocio, podrías lanzar excepción para hacer rollback
//             throw new RuntimeException("No se pudo sincronizar la solicitud con el módulo de aprobaciones");
//         }

//         return true;
//     }


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
            System.out.println("La solicitud con ID " + solicitudId + " no existe.");
            return false;
        }
        //Validar que es la solicitud correcta del usuario
        if(porEliminar.getSolicitante().getIdUsuario() != usuarioID) {
            System.out.println("El usuario con ID " + usuarioID + " no es el dueño de la solicitud con ID " + solicitudId);
            return false; //No es el dueño de la solicitud
        }
        
        String estado=porEliminar.getEstado();

        if("PENDIENTE".equals(estado)){
            repoSolicitud.deleteById(solicitudId);
            return true;
        }
        else{
            System.out.println("La solicitud con ID " + solicitudId + " no se puede anular porque su estado es " + estado);
            return false;
         }  
     }

    public List<SolicitudDTO> servListarSolicitudes(){
        List<Solicitud> solicitudes = repoSolicitud.findAll();

        return solicitudes.stream()
                      .map(s -> new SolicitudDTO(s))
                      .collect(Collectors.toList());
        
    }


    // //Esta función llama a guardarArchivosEnDisco (arriba)
    // public List<Adjunto> procesarArchivos(List<MultipartFile> archivos, Solicitud solicitud) {
    // List<Adjunto> lista = new ArrayList<>();
    
    // if (archivos != null && !archivos.isEmpty()) {
    //     for (MultipartFile archivo : archivos) {
    //         if (!archivo.isEmpty()) {
    //             // Llamamos a tu lógica de escritura física
    //             String rutaEnDisco = almacenamiento.guardarArchivoEnDisco(archivo);
                
    //             // Creamos el objeto para la Base de Datos
    //             Adjunto adj = new Adjunto();
    //             adj.setNombreArchivo(archivo.getOriginalFilename());
    //             adj.setRuta(rutaEnDisco); // Guardamos el String de la ruta
    //             adj.setTipoArchivo(archivo.getContentType());
    //             adj.setTamañoKB(archivo.getSize() / 1024); // Convertimos bytes a KB
    //             adj.setSolicitud(solicitud); // Vinculamos a la solicitud actual
                
    //             lista.add(adj);
    //         }
    //     }
    // }
    //     return lista;
    // }

    public boolean servActualizarSolicitud(Integer idSolicitud,ActualizarSolicitudDTO dto, String rol, List<MultipartFile> nuevosAdjuntos){
        //Solicitud solicitud = repoSolicitud.findById(idSolicitud).orElse(null);
        Solicitud solicitud = repoSolicitud.findById(idSolicitud).orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
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

                List<Adjunto> newAdjuntos = adjuntoProcessor.procesarArchivos(nuevosAdjuntos, solicitud);
                //solicitud.getAdjuntos().addAll(newAdjuntos);

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
     


    public IUsuarioRepository getRepoUsuario() {
        return repoUsuario;
    }


    public void setRepoUsuario(IUsuarioRepository repoUsuario) {
        this.repoUsuario = repoUsuario;
    }



}


