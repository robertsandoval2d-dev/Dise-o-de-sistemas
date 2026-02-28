package com.campus360.solicitudes.Controller;


import java.util.ArrayList;
import java.util.List;



import org.springframework.http.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.campus360.solicitudes.DTOs.ActualizarSolicitudDTO;
import com.campus360.solicitudes.DTOs.SolicitudCreateDTO;
import com.campus360.solicitudes.DTOs.SolicitudDTO;
import com.campus360.solicitudes.Dominio.Adjunto;
// import com.campus360.solicitudes.Dominio.Solicitud;
// import com.campus360.solicitudes.Dominio.Usuario;
import com.campus360.solicitudes.Servicios.ISolicitudService;
//import com.campus360.solicitudes.Servicios.ISolicitudQueryService;
import com.campus360.solicitudes.Servicios.SolicitudService;
import com.campus360.solicitudes.Security.JwtUtil;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;




@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    @Autowired
    private SolicitudService servSolicitud;

    @Autowired
    private ISolicitudService command;
    // @Autowired
    // private ISolicitudQueryService query;
    @Autowired
    private final JwtUtil jwtUtil;


    //Constructor
    public SolicitudController(ISolicitudService solicitudCommandService, JwtUtil jwtUtil){
        this.command = solicitudCommandService;
        //this.query = solicitudQueryService;
        this.jwtUtil = jwtUtil;
    }


    //GETTERS Y SETTERS


      public ISolicitudService getCommand() {
        return command;
    }


    public void setCommand(ISolicitudService command) {
        this.command = command;
    }

    // public ISolicitudQueryService getQuery() {
    //     return query;
    // }

    // public void setQuery(ISolicitudQueryService query) {
    //     this.query = query;
    // }



    //APIS


    // @GetMapping
    // public List<SolicitudDTO> listarSolicitudes(@RequestParam Integer usuarioId){
    //     return servSolicitud.servObtenerHistorial(usuarioId);
    // }

    @GetMapping
    public List<SolicitudDTO> listarSolicitudes(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader){

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token no enviado o inválido");
        }

        String token = authorizationHeader.substring(7);

        int usuarioId = jwtUtil.obtenerIdUsuario(token);

        return servSolicitud.servObtenerHistorial(usuarioId);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> consultarSolicitud(@PathVariable Integer id, @RequestHeader(value = "X-User-Role", required = false) String rol){
        String rolUsuario = (rol != null) ? rol : "ESTUDIANTE";
        SolicitudDTO resultado = servSolicitud.obtenerDetalleCompleto(id, rolUsuario);

        if (resultado == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Solicitud no encontrada");
    }

    return ResponseEntity.ok(resultado);
    }


    @PostMapping(value = "/registrar", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> crearSolicitud(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
        @RequestPart("solicitud") SolicitudCreateDTO dto,
        @RequestPart(value = "archivos", required = false) List<MultipartFile> archivos
    ) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token no enviado o inválido");
        }

        String token = authorizationHeader.substring(7);

        int usuarioId = jwtUtil.obtenerIdUsuario(token);
        String nombre = jwtUtil.obtenerNombre(token);
        String rol = jwtUtil.obtenerRol(token);
        try {
            ArrayList<Adjunto> listaAdjuntosParaBD = new ArrayList<>();

            // 1. Procesar los archivos físicos si es que el usuario envió alguno
            if (archivos != null && !archivos.isEmpty()) {
                for (MultipartFile file : archivos) {
                    // Guardamos en el disco duro y obtenemos la ruta -  LLAMO AL OTRO METODO DE ABAJO 
                    String rutaFisica = servSolicitud.guardarArchivoEnDisco(file);

                    // Creamos el objeto Adjunto que se guardará en MySQL
                    Adjunto adj = new Adjunto();
                    adj.setNombreArchivo(file.getOriginalFilename());
                    adj.setRuta(rutaFisica);
                    adj.setTipoArchivo(file.getContentType());

                    listaAdjuntosParaBD.add(adj);
                }
            }

            // 2. Llamar al servicio con el DTO y la lista de objetos Adjunto
            boolean exito = servSolicitud.servRegistrarSolicitud(usuarioId, nombre, rol, dto, listaAdjuntosParaBD,archivos);

            if (exito) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("{\"mensaje\": \"Solicitud creada con éxito y archivos guardados\"}");
            } else {
                return ResponseEntity.badRequest().body("{\"error\": \"No se pudo procesar la solicitud\"}");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }



// private String guardarArchivoEnDisco(MultipartFile file) {
//     try {
//         // 1. Definimos la carpeta "uploads" dentro de la raíz del proyecto
//         String rootPath = System.getProperty("user.dir");
//         String nombreCarpeta = "uploads";

//         // 2. Creamos el objeto File para la subcarpeta
//         File directory = new File(rootPath, nombreCarpeta);

//         // 3. Si la subcarpeta no existe, la creamos
//         if (!directory.exists()) {
//             directory.mkdirs();
//         }

//         // 4. Generar nombre único (Timestamp + nombre original)
//         String nombreUnico = System.currentTimeMillis() + "_" + file.getOriginalFilename();

//         // 5. Usamos Paths.get con dos argumentos para que Java ponga el "/" o "\" correcto
//         Path rutaDestino = Paths.get(directory.getAbsolutePath(), nombreUnico);

//         // 6. Escribimos los bytes del archivo
//         Files.write(rutaDestino, file.getBytes());

//         // 7. Retornamos la ruta absoluta para guardarla en la base de datos
//         return rutaDestino.toString();

//     } catch (IOException e) {
//         throw new RuntimeException("Error al escribir el archivo en el servidor: " + e.getMessage());
//     }
// }



    //RESTO DE PETICIONES (JeanFranco)

    // @PostMapping(value = "/{id}/adjuntos", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    // public ResponseEntity<?> adjuntarArchivos(
    //         @PathVariable Integer id,
    //         @RequestPart(value = "archivos", required = false) List<MultipartFile> archivos
    // ) {
    //     try {
    //         ArrayList<Adjunto> listaAdjuntosParaBD = new ArrayList<>();

    //         if (archivos != null && !archivos.isEmpty()) {
    //             for (MultipartFile file : archivos) {
    //                 String rutaFisica = guardarArchivoEnDisco(file);

    //                 Adjunto adj = new Adjunto();
    //                 adj.setNombreArchivo(file.getOriginalFilename());
    //                 adj.setRuta(rutaFisica);
    //                 adj.setTipoArchivo(file.getContentType());

    //                 listaAdjuntosParaBD.add(adj);
    //             }
    //         }

    //         boolean exito = servSolicitud.servAdjuntarArchivos(id, listaAdjuntosParaBD);

    //         if (exito) {
    //             return ResponseEntity.status(HttpStatus.CREATED)
    //                     .body("{\"mensaje\": \"Adjuntos registrados con éxito\"}");
    //         } else {
    //             return ResponseEntity.badRequest().body("{\"error\": \"No se pudo registrar los adjuntos\"}");
    //         }

    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body("{\"error\": \"" + e.getMessage() + "\"}");
    //     }
    // }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> anularSolicitud(@RequestHeader(value = "Authorization") String authHeader, @PathVariable Integer id){

        try {
            // Extraer el usuario del token
            String token = authHeader.substring(7);
            String usuarioActualId = jwtUtil.obtenerIdUsuario(token);
            
            // Pasar tanto el ID de solicitud como el usuario actual
            boolean eliminado = servSolicitud.servAnularSolicitud(id, Integer.parseInt(usuarioActualId));
            
            if(eliminado){
                return ResponseEntity.ok().body(" {\"mensaje\": \"La solicitud fue eliminada correctamente\"}");
            }
            else{
                return ResponseEntity.status(HttpStatus.CONFLICT).body(" {\"mensaje\": \"No se pudo eliminar la solicitud\"}");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(" {\"error\": \"No autorizado\"}");
        }
    }


    @GetMapping("/listar")
    public ResponseEntity<List<SolicitudDTO>> listarSolicitudes(){
        List<SolicitudDTO> solicitudes=servSolicitud.servListarSolicitudes();
        return ResponseEntity.ok().body(solicitudes);
    }

    

    // @PatchMapping("/actualizar/{id}")
    // public ResponseEntity<?> actualizarSolicitud(@PathVariable Integer id, @RequestPart("datos") ActualizarSolicitudDTO dto,@RequestPart(value = "archivos", required = false) List<MultipartFile> archivos,@RequestHeader(value = "X-User-Role", required = true) String rol){

        
    //     boolean actualizado=servSolicitud.servActualizarSolicitud(id,dto, rol,archivos);

    //     if(actualizado){
    //         return ResponseEntity.ok().body("{ \"mensaje\": \"La solicitud ha sido actualizada correctamente\"}");
    //     }
    //     else{
    //         return ResponseEntity.status(HttpStatus.CONFLICT).body("\"mensaje\": \"La solicitud NO se pudo actualizar\"");
    //     }
    // }

    @PatchMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarSolicitud(
        @RequestHeader(value = "Authorization") String authHeader,
        @PathVariable Integer id, 
        @RequestPart("datos") ActualizarSolicitudDTO dto,
        @RequestPart(value = "archivos", required = false) List<MultipartFile> archivos) {

        String token = authHeader.substring(7);
        String rol = jwtUtil.obtenerRol(token); // Obtenemos el rol del TOKEN, no de un header manual

        boolean actualizado = servSolicitud.servActualizarSolicitud(id, dto, rol, archivos);

        if (actualizado) {
            return ResponseEntity.ok().body("{ \"mensaje\": \"La solicitud ha sido actualizada correctamente\"}");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"mensaje\": \"No tienes permisos para esta acción\"}");
        }
    }

}
