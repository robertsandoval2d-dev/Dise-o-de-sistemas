package GestionSolicitudes.Dominio;
import java.util.Date;

/**
 * Entidad de Dominio: Adjunto
 */
public class Adjunto {

    // Atributos privados (-)
    private int idAdjunto;
    private String nombreArchivo;
    private String tipoArchivo;
    private double tamañoKB;
    private String ruta;
    private Date fechaCarga;

    // Constructor vacío
    public Adjunto() {
    }

    // Constructor completo
    public Adjunto(int idAdjunto, String nombreArchivo, String tipoArchivo, 
                   double tamañoKB, String ruta, Date fechaCarga) {
        this.idAdjunto = idAdjunto;
        this.nombreArchivo = nombreArchivo;
        this.tipoArchivo = tipoArchivo;
        this.tamañoKB = tamañoKB;
        this.ruta = ruta;
        this.fechaCarga = fechaCarga;
    }

  // --- Métodos de Comportamiento ---

    public boolean validarTipo() {
        // Lógica de dominio: ejemplo para permitir solo imágenes y documentos
        if (tipoArchivo == null) return false;
        String ext = tipoArchivo.toLowerCase();
        return ext.equals("pdf") || ext.equals("jpg") || ext.equals("png");
    }

    public boolean validarTamaño() {
        // Lógica de dominio: ejemplo límite de 10MB
        return tamañoKB > 0 && tamañoKB <= 10240;
    }

    public void guardar() {
        // Prepara el objeto para ser persistido por la capa de Service
        if (this.fechaCarga == null) {
            this.fechaCarga = new Date();
        }
    }

    public void eliminar() {
        // Lógica para marcar el objeto como inactivo o limpiar referencias
        System.out.println("Preparando eliminación del adjunto: " + nombreArchivo);
    }

    // --- Getters y Setters ---

    public int getIdAdjunto() {
        return idAdjunto;
    }

    public void setIdAdjunto(int idAdjunto) {
        this.idAdjunto = idAdjunto;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getTipoArchivo() {
        return tipoArchivo;
    }

    public void setTipoArchivo(String tipoArchivo) {
        this.tipoArchivo = tipoArchivo;
    }

    public double getTamañoKB() {
        return tamañoKB;
    }

    public void setTamañoKB(double tamañoKB) {
        this.tamañoKB = tamañoKB;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public Date getFechaCarga() {
        return fechaCarga;
    }

    public void setFechaCarga(Date fechaCarga) {
        this.fechaCarga = fechaCarga;
    }
}