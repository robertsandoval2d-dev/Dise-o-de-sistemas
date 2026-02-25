package GestionSolicitudes.Dominio;

public class Usuario {

    // Atributos privados según el diagrama UML
    private int idUsuario;
    private String nombre;
    private String email;
    private String rol; 

    // Constructor vacío 
    public Usuario() {
    }

    // Constructor con parámetros
    public Usuario(int idUsuario, String nombre, String email, String rol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
    }

    // --- Métodos de Comportamiento ---

    public void crearSolicitud() {
        // En una arquitectura de capas, aquí solo iría lógica de estado interna
        // o validaciones de dominio propias del usuario.
        System.out.println("El usuario " + nombre + " está intentando crear una solicitud.");
    }

    public void consultarSolicitudes() {
        // Nota: En capas, la consulta real la haría el Service llamando a Persistence.
        // Este método en el dominio suele representar la capacidad del objeto.
    }

    public void anularSolicitud(int idSolicitud) {
        // Lógica de negocio pura: ej. verificar si el usuario tiene permisos según su Rol
        System.out.println("Solicitando anulación de la solicitud: " + idSolicitud);
    }

    public void reportarIncidencia() {
        // Lógica de dominio para marcar un comportamiento de incidencia
        System.out.println("Incidencia reportada por el usuario: " + idUsuario);
    }

    // --- Getters y Setters ---

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}