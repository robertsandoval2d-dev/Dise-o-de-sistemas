package Servicios;
import Dominio.*;
import Repositorio.*;
import java.util.ArrayList;

public class SolicitudService implements ISolicitudCommandService, ISolicitudQueryService{
    // Atributos privados 
    private ISolicitudRepository repositorio;
    private IAlmacenamiento almacenamiento;

    /*Constructor*/
    public SolicitudService(ISolicitudRepository repositorio,IAlmacenamiento almacenamiento){
        this.repositorio = repositorio;
        this.almacenamiento = almacenamiento;
    }

    // --- Métodos de Comportamiento ---
    public boolean registrarSolicitud(String Datos,int servicioid,ArrayList <Adjunto> adjuntos){
        boolean registroExitoso = false;
        
        //CatalogoService.obetnerRequisitos(servicioID)
        registroExitoso = validarDatosYAdjuntos();
        //llamada a guardar adjuntos a sistema de almacenamiento
        almacenamiento.guardarAdjunto();

        Solicitud solicitud = new Solicitud();
        //Logica para cacular sla
        solicitud.setEstado("POR REVISAR");
        repositorio.saveSolicitud(solicitud);

        return registroExitoso;
   
    }

    public ArrayList<Solicitud> obtenerHistorial(int usuarioID){ 
        return repositorio.findAllByUsuario(usuarioID);
    }

    public Solicitud obtenerDetalleCompleto(int solicitudId){
        Solicitud solicitud = repositorio.findById(solicitudId);
        //lógica para calulcar estado de sla
        return solicitud;
    }

    public boolean anularSolicitud(int solicitudId){
        boolean anulacionExitosa=false;
        //logica de anulación
        return anulacionExitosa;
    }

    private boolean validarDatosYAdjuntos(){
        //lógica de validación
        return true;
    }
    
}