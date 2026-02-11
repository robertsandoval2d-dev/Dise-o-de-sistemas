package Servicios;
import java.util.ArrayList;
import Dominio.*;

public interface ISolicitudQueryService {
    public ArrayList<Solicitud> obtenerHistorial(int usuarioID); 
    public Solicitud obtenerDetalleCompleto(int solicitudId); 
}
