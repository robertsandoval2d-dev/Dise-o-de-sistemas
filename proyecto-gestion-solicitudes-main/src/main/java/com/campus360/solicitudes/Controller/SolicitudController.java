package com.campus360.solicitudes.Controller;


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
    private final JwtUtil jwtUtil;


    //Constructor
    public SolicitudController(JwtUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }

    //APIS

    @GetMapping("usuario/listar")
    public ResponseEntity<List<SolicitudDTO>> listarSolicitudesDeUsuario(
            @RequestHeader(value = "Authorization", required = false) String authHeader){

        //String token = extraerToken(authHeader);
        // int usuarioId = jwtUtil.obtenerIdUsuario(token);

        int usuarioId = 1; // Para pruebas

        List<SolicitudDTO> solicitudes = servSolicitud.servObtenerHistorialSolicitudes(usuarioId);

        if (solicitudes == null || solicitudes.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204
        }

        return ResponseEntity.ok(solicitudes); // 200
    }


    @GetMapping("solicitud/{id}")
    public ResponseEntity<?> consultarSolicitud(@PathVariable Integer id, @RequestHeader(value = "Authorization", required = false) String authHeader){
        //String token = extraerToken(authHeader);

        // String rol = jwtUtil.obtenerRol(token);
        // int usuarioId = jwtUtil.obtenerIdUsuario(token);

        String rolUsuario = "APROBADOR"; // Para pruebas, asignamos un rol fijo. En producción, esto se obtiene del token.
        int usuarioId = 1; // Para pruebas, asignamos un ID fijo. En producción, esto se obtiene del token.

        
        SolicitudDTO resultado = servSolicitud.obtenerDetalleCompleto(id, rolUsuario,usuarioId);

        if (resultado == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Solicitud no encontrada");
    }

    return ResponseEntity.ok(resultado);
    }


    @PostMapping(value = "/registrar", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> crearSolicitud(@RequestHeader(value = "Authorization", required = false) String authHeader,
        @RequestPart("solicitud") SolicitudCreateDTO dto,
        @RequestPart(value = "archivos", required = false) List<MultipartFile> archivos
    ) {
        //String token = extraerToken(authHeader);

        // int usuarioId = jwtUtil.obtenerIdUsuario(token);
        // String nombre = jwtUtil.obtenerNombre(token);
        // String rol = jwtUtil.obtenerRol(token);
       
        //  Para fines de prueba, si no se envía el token, se asignan valores por defecto. En producción, esto debería ser un error.
        int usuarioId = 1; 
        String nombre = "Cesar Alberto Pérez García";
        String rol = "ESTUDIANTE";

        try {
            // 2. Llamar al servicio con el DTO y la lista de objetos Adjunto
            boolean exito = servSolicitud.servRegistrarSolicitud(usuarioId, nombre, rol, dto,archivos);

            if (exito) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("{\"mensaje\": \"Solicitud creada con éxito y archivos guardados\"}");
            } else {
                return ResponseEntity.badRequest().body("{\"error\": \"No se pudo procesar la solicitud\"}");
            }

        } 
        catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("anular/{id}")
    public ResponseEntity<?> anularSolicitud(@RequestHeader(value = "Authorization", required = false) String authHeader, @PathVariable Integer id){

        try {
            // Extraer el usuario del token
            //String token = extraerToken(authHeader);
            // String usuarioActualId = jwtUtil.obtenerIdUsuario(token);

            int usuarioActualId = 1; // Para pruebas, asignamos un ID fijo. En producción, esto se obtiene del token.
            
            // Pasar tanto el ID de solicitud como el usuario actual
            boolean eliminado = servSolicitud.servAnularSolicitud(id, usuarioActualId);
            
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

    @PatchMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarSolicitud(
        @RequestHeader(value = "Authorization", required = false) String authHeader,
        @PathVariable Integer id, 
        @RequestPart("datos") ActualizarSolicitudDTO dto,
        @RequestPart(value = "archivos", required = false) List<MultipartFile> archivos) {

        //String token = extraerToken(authHeader);
        // String rol = jwtUtil.obtenerRol(token); // Obtenemos el rol del TOKEN, no de un header manual
        // int usarioId = jwtUtil.obtenerIdUsuario(token); // Obtenemos el ID del usuario del TOKEN, no de un header manual

        int usuarioId = 1; // Para pruebas, asignamos un ID fijo. En producción, esto se obtiene del token.
        String rol = "ESTUDIANTE"; // Para pruebas, asignamos un rol fijo. En producción, esto se obtiene del token.

        boolean actualizado = servSolicitud.servActualizarSolicitud(id,usuarioId, dto, rol, archivos);

        if (actualizado) {
            return ResponseEntity.ok().body("{ \"mensaje\": \"La solicitud ha sido actualizada correctamente\"}");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"mensaje\": \"No tienes permisos para esta acción\"}");
        }
    }

    private String extraerToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token no enviado o inválido");
        }
        return authHeader.substring(7);
    }

}
