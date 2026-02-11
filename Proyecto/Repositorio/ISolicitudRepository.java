package Repositorio;
import Dominio.*;
import java.util.ArrayList;

public interface ISolicitudRepository {
    public int saveSolicitud(Solicitud solicitud);
    public ArrayList<Solicitud> findAllByUsuario(int usarioID);
    public Solicitud findById(int idSolicitud);
}
