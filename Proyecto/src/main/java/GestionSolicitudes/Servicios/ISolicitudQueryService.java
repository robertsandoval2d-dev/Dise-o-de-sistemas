package GestionSolicitudes.Servicios;
import java.util.ArrayList;
import GestionSolicitudes.Dominio.*;

public interface ISolicitudQueryService {
    public ArrayList<Solicitud> obtenerHistorial(int usuarioID); 
    public Solicitud obtenerDetalleCompleto(int solicitudId); 
}
