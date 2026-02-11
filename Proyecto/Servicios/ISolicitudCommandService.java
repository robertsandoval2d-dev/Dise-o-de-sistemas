package Servicios;
import java.util.ArrayList;
import Dominio.*;

public interface ISolicitudCommandService {
    public boolean registrarSolicitud(String Datos,int servicioid,ArrayList <Adjunto> adjuntos);
    public boolean anularSolicitud(int solicitudId);
}
