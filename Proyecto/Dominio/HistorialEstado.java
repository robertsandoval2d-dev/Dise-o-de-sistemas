package Dominio;
import java.util.Date;

public class HistorialEstado {

    // Atributos privados 
    private int idHistorial;
    private Date fechaCambio;
    private String estadoAnterior;
    private String estadoNuevo;
    private String comentario;
    private Usuario usuarioResponsable;

    // Constructor vacío
    public HistorialEstado() {
    }

    // Constructor con parámetros
    public HistorialEstado(int idHistorial, Date fechaCambio, String estadoAnterior, 
                           String estadoNuevo, String comentario, Usuario usuarioResponsable) {
        this.idHistorial = idHistorial;
        this.fechaCambio = fechaCambio;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.comentario = comentario;
        this.usuarioResponsable = usuarioResponsable;
    }

  // --- Métodos de Comportamiento ---
    public void registrarCambio() {
        // Aquí iría la lógica para persistir el cambio en una base de datos 
        // o realizar alguna acción específica.
        System.out.println("Registrando cambio de estado...");
    }

    // Getters y Setters
    public int getIdHistorial() {
        return idHistorial;
    }

    public void setIdHistorial(int idHistorial) {
        this.idHistorial = idHistorial;
    }

    public Date getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(Date fechaCambio) {
        this.fechaCambio = fechaCambio;
    }

    public String getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(String estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public String getEstadoNuevo() {
        return estadoNuevo;
    }

    public void setEstadoNuevo(String estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Usuario getUsuarioResponsable() {
        return usuarioResponsable;
    }

    public void setUsuarioResponsable(Usuario usuarioResponsable) {
        this.usuarioResponsable = usuarioResponsable;
    }
}