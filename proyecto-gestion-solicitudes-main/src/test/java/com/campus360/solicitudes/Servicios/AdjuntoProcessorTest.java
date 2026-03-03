package com.campus360.solicitudes.Servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.campus360.solicitudes.Dominio.Adjunto;
import com.campus360.solicitudes.Dominio.Solicitud;
import com.campus360.solicitudes.Repositorio.IAlmacenamiento;
import com.campus360.solicitudes.Servicios.util.AdjuntoProcessor;

@ExtendWith(MockitoExtension.class)
public class AdjuntoProcessorTest {

    @Mock
    private IAlmacenamiento almacenamiento;

    @InjectMocks
    private AdjuntoProcessor adjuntoProcessor;

    @Test
    void testCP01_ListaVacia() {
        List<Adjunto> resultado = adjuntoProcessor.procesarArchivos(Collections.emptyList(), new Solicitud());
        assertTrue(resultado.isEmpty()); // Esperamos lista de tamaño 0
    }

    @Test
    void testCP02_ArchivoVacio() {
        MockMultipartFile vacio = new MockMultipartFile("file", "vacio.txt", "text/plain", new byte[0]);
        
        List<Adjunto> resultado = adjuntoProcessor.procesarArchivos(List.of(vacio), new Solicitud());
        
        assertTrue(resultado.isEmpty()); // Debe ignorar el archivo vacío
    }

    @Test
    void testCP03_FlujoExitoso(){
        MockMultipartFile valido = new MockMultipartFile("archivo", "test.png", "image/png", new byte[1024]);

        when(almacenamiento.guardarArchivoEnDisco(any())).thenReturn("/ruta/test.png");

        List<Adjunto> resultado = adjuntoProcessor.procesarArchivos(List.of(valido), new Solicitud());

        assertEquals(1, resultado.size());
        assertEquals(1.0, resultado.get(0).getTamañoKB()); // 1024/1024 = 1.0

    }


    @Test
    void testCP04_DatosMixtos() {
        MockMultipartFile valido = new MockMultipartFile("archivo1", "foto.jpg", "image/jpeg", new byte[1024]);
        MockMultipartFile vacio = new MockMultipartFile("archivo2", "nada.txt", "text/plain", new byte[0]);
        
        when(almacenamiento.guardarArchivoEnDisco(any())).thenReturn("/ruta/foto.jpg");

        List<Adjunto> resultado = adjuntoProcessor.procesarArchivos(List.of(valido, vacio), new Solicitud());

        assertEquals(1, resultado.size()); // Solo debe haber procesado el válido
        assertEquals("foto.jpg", resultado.get(0).getNombreArchivo());
    }
    

}
