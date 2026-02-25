package GestionSolicitudes.Controller;

import GestionSolicitudes.Dominio.Solicitud;
import GestionSolicitudes.Servicios.ISolicitudCommandService;
import GestionSolicitudes.Servicios.ISolicitudQueryService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@RestController 
@RequestMapping("/api/solicitudes") // Esto define la base del URL
public class SolicitudController{
    //Atributos privados
    private ISolicitudCommandService command;
    private ISolicitudQueryService query;

    //Constructor
    public SolicitudController(ISolicitudCommandService solicitudCommandService,ISolicitudQueryService solicitudQueryService){
        this.command = solicitudCommandService;
        this.query = solicitudQueryService;
    }

    //APIS 
    @PostMapping // Esto hace que responda al método POST
    public ResponseEntity<Boolean> crearSolicitud(
            @RequestParam("datos") String datos,
            @RequestParam("servicioId") int servicioId,
            @RequestParam("adjuntos") List<MultipartFile> adjuntos) {

        // Llamas a tu lógica de negocio
        //boolean resultado = command.registrarSolicitud(datos,servicioId,adjuntos);
        
        return ResponseEntity.ok(/*resultado*/false);
    }

    // RF-03: Listar solicitudes del solicitante
    // Ejemplo: GET /api/solicitudes?usuarioId=123
    @GetMapping
    public ResponseEntity<ArrayList<Solicitud>> listarSolicitudes(@RequestParam int usuarioId) {
        ArrayList<Solicitud> historial = query.obtenerHistorial(usuarioId);
        return ResponseEntity.ok(historial);
    }

    // RF-07: Obtener detalle de una solicitud
    // Ejemplo: GET /api/solicitudes/5
    @GetMapping("/{id}")
    public ResponseEntity<Solicitud> obtenerDetalle(@PathVariable("id") int solicitudId) {
        Solicitud solicitud = query.obtenerDetalleCompleto(solicitudId);
        
        if (solicitud != null) {
            return ResponseEntity.ok(solicitud);
        } else {
            return ResponseEntity.notFound().build(); // Retorna 404 si no existe
        }
    }

    // RF-06: Anular una solicitud
    // Ejemplo: DELETE /api/solicitudes/5
    @DeleteMapping("/{id}")
    public ResponseEntity<String> anular(@PathVariable("id") int solicitudId) {
        boolean exito = command.anularSolicitud(solicitudId);
        
        if (exito) {
            return ResponseEntity.ok("Solicitud anulada correctamente");
        } else {
            return ResponseEntity.badRequest().body("No se pudo anular la solicitud (verifique el estado)");
        }
    }
}