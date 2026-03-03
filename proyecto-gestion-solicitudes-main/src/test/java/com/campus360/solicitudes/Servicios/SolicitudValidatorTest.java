package com.campus360.solicitudes.Servicios;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.campus360.solicitudes.DTOs.RequisitoDTO;
import com.campus360.solicitudes.DTOs.ServicioInfoResponse;
import com.campus360.solicitudes.DTOs.SolicitudCreateDTO;
import com.campus360.solicitudes.Servicios.util.SolicitudValidator;

@ExtendWith(MockitoExtension.class)
public class SolicitudValidatorTest {

    @InjectMocks
    private SolicitudValidator validator;

    @Test
    void testCP01_ValidacionExitosa() {
        // --- PREPARACIÓN ---
        ServicioInfoResponse info = new ServicioInfoResponse();
        List<RequisitoDTO> requisitos = new ArrayList<>();
        
        RequisitoDTO reqFile = new RequisitoDTO();
        reqFile.setTipo("FILE");
        reqFile.setObligatorio(true);
        
        RequisitoDTO reqTexto = new RequisitoDTO();
        reqTexto.setTipo("STRING");
        reqTexto.setCampo("descripcion");
        reqTexto.setObligatorio(true);
        
        requisitos.add(reqFile);
        requisitos.add(reqTexto);
        info.setRequisitos(requisitos);

        SolicitudCreateDTO dto = new SolicitudCreateDTO();
        dto.setDescripcion("Contenido válido");
        dto.setFechaProgramada(java.time.LocalDateTime.now());
        
        MockMultipartFile archivoValido = new MockMultipartFile("file", "doc.pdf", "application/pdf", new byte[1024]);
        List<MultipartFile> archivos = List.of(archivoValido);

        // --- EJECUCIÓN Y VERIFICACIÓN ---
        assertDoesNotThrow(() -> validator.validarRequisitosDinamicos(info, dto, archivos));
    }

    @Test
    void testCP02_FaltanArchivosObligatorios_LanzaException() {
        // --- PREPARACIÓN ---
        ServicioInfoResponse info = new ServicioInfoResponse();
        RequisitoDTO req1 = new RequisitoDTO(); req1.setTipo("FILE"); req1.setObligatorio(true);
        info.setRequisitos(List.of(req1));

        // --- ENTRADA (Lista vacía cuando se requiere 1) ---
        List<MultipartFile> archivos = List.of();

        // --- VERIFICACIÓN ---
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            validator.validarRequisitosDinamicos(info, new SolicitudCreateDTO(), archivos);
        });

        assertTrue(ex.getMessage().contains("Faltan archivos adjuntos"));
    }

    @Test
    void testCP03_ArchivoVacio_NoDebeContabilizarse() {
        // --- PREPARACIÓN ---
        ServicioInfoResponse info = new ServicioInfoResponse();
        RequisitoDTO req = new RequisitoDTO(); req.setTipo("FILE"); req.setObligatorio(true);
        info.setRequisitos(List.of(req));

        // --- ENTRADA (Envía un archivo pero está vacío / 0 bytes) ---
        MockMultipartFile fileVacio = new MockMultipartFile("f1", "vacio.txt", "text/plain", new byte[0]);
        List<MultipartFile> archivos = List.of(fileVacio);

        // --- VERIFICACIÓN ---
        assertThrows(RuntimeException.class, () -> {
            validator.validarRequisitosDinamicos(info, new SolicitudCreateDTO(), archivos);
        });
    }

    @Test
    void testCP04_CampoTextoFaltante_LanzaException() {
        // --- PREPARACIÓN ---
        ServicioInfoResponse info = new ServicioInfoResponse();
        RequisitoDTO req = new RequisitoDTO();
        req.setTipo("STRING");
        req.setCampo("descripcion");
        req.setObligatorio(true);
        info.setRequisitos(List.of(req));

        // --- ENTRADA (Descripción nula o vacía) ---
        SolicitudCreateDTO dto = new SolicitudCreateDTO();
        dto.setDescripcion(""); 

        // --- VERIFICACIÓN ---
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            validator.validarRequisitosDinamicos(info, dto, List.of());
        });

        assertEquals("Debe especificar 'descripcion' en la solicitud.", ex.getMessage());
    }

    @Test
    void testCP05_RequisitosNulos_NoHaceNada() {
        // --- PREPARACIÓN ---
        ServicioInfoResponse info = new ServicioInfoResponse();
        info.setRequisitos(null); 

        // --- VERIFICACIÓN ---
        assertDoesNotThrow(() -> validator.validarRequisitosDinamicos(info, null, null));
    }
}
