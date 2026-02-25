package GestionSolicitudes.Servicios;
import java.util.ArrayList;
import GestionSolicitudes.Dominio.*;

public interface ISolicitudCommandService {
    public boolean registrarSolicitud(String Datos,int servicioid,ArrayList <Adjunto> adjuntos);
    public boolean anularSolicitud(int solicitudId);
}
