package com.campus360.solicitudes.Servicios;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

// Importa aquí tus clases de entidad (Solicitud, Usuario, etc.)
import com.campus360.solicitudes.Dominio.Solicitud; 
import com.campus360.solicitudes.Dominio.Usuario;
import com.campus360.solicitudes.Repositorio.ISolicitudRepository;

@ExtendWith(MockitoExtension.class)
class SolicitudServiceTest {

    @Mock
    private ISolicitudRepository repoSolicitud;

    @InjectMocks
    private SolicitudService servicio; // Asegúrate que el nombre coincida con tu clase

@Test
    void servAnularSolicitud_CuandoExito_RetornaTrue() {
        // --- PREPARACIÓN DE DATOS (Arrange) ---
        int idSol = 1; int idUser = 10;
        Usuario dueño = new Usuario(); 
        dueño.setIdUsuario(idUser);
        
        Solicitud sol = new Solicitud(); 
        sol.setSolicitante(dueño);
        sol.setEstado("PENDIENTE");

        // DEFINICIÓN DE COMPORTAMIENTO (Mocking)
        when(repoSolicitud.findById(idSol)).thenReturn(Optional.of(sol));

        // --- EJECUCIÓN DE LA PRUEBA (Act) ---
        boolean resultado = servicio.servAnularSolicitud(idSol, idUser);

        // --- VERIFICACIÓN DE RESULTADOS (Assert) ---
        assertTrue(resultado, "La función debería retornar true en caso de éxito");
        
        // Verificamos que se llamó al repositorio para eliminar
        verify(repoSolicitud, times(1)).deleteById(idSol);
    }

    @Test
    void servAnularSolicitud_CuandoNoExiste_LanzaException() {
        // --- PREPARACIÓN Y COMPORTAMIENTO ---
        // Simulamos que el repositorio devuelve vacío (Optional.empty)
        when(repoSolicitud.findById(99)).thenReturn(Optional.empty());

        // --- EJECUCIÓN Y VERIFICACIÓN DE EXCEPCIÓN ---
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            servicio.servAnularSolicitud(99, 1);
        });
        
        // Validamos que el mensaje de error sea el correcto
        assertEquals("La solicitud con ID 99 no existe.", ex.getMessage());
        
        // Verificamos que NUNCA se intentó borrar nada
        verify(repoSolicitud, never()).deleteById(anyInt());
    }

    @Test
    void servAnularSolicitud_CuandoUsuarioNoEsDueño_LanzaException() {
        // --- PREPARACIÓN DE DATOS ---
        Usuario dueñoReal = new Usuario(); 
        dueñoReal.setIdUsuario(50);
        Solicitud sol = new Solicitud(); 
        sol.setSolicitante(dueñoReal);

        // Simulamos que encontramos la solicitud, pero el dueño es el ID 50
        when(repoSolicitud.findById(1)).thenReturn(Optional.of(sol));

        // --- EJECUCIÓN Y VERIFICACIÓN ---
        assertThrows(RuntimeException.class, () -> {
            servicio.servAnularSolicitud(1, 99); // Usuario 99 intenta anular
        });
        
        // Verificamos que no se procedió con la eliminación
        verify(repoSolicitud, never()).deleteById(1);
    }

    @Test
    void servAnularSolicitud_CuandoEstadoNoEsPendiente_LanzaException() {
        // --- PREPARACIÓN DE DATOS ---
        Usuario dueño = new Usuario(); 
        dueño.setIdUsuario(10);
        Solicitud sol = new Solicitud(); 
        sol.setSolicitante(dueño);
        sol.setEstado("EN_PROCESO"); // Estado que impide la anulación

        when(repoSolicitud.findById(1)).thenReturn(Optional.of(sol));

        // --- EJECUCIÓN Y VERIFICACIÓN ---
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            servicio.servAnularSolicitud(1, 10);
        });
        
        // Verificamos que el mensaje mencione el estado inválido
        assertTrue(ex.getMessage().contains("no se puede anular"), "El mensaje debe indicar que el estado impide la anulación");
        verify(repoSolicitud, never()).deleteById(1);
    }
}