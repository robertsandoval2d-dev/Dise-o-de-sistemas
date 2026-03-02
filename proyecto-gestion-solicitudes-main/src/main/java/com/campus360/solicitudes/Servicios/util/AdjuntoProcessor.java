package com.campus360.solicitudes.Servicios.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.campus360.solicitudes.Dominio.Adjunto;
import com.campus360.solicitudes.Dominio.Solicitud;
import com.campus360.solicitudes.Repositorio.IAlmacenamiento;

@Component
public class AdjuntoProcessor {

    @Autowired
    private IAlmacenamiento almacenamiento;

    public void vincularAdjuntos(Solicitud solicitud, List<MultipartFile> archivos) {
        List<Adjunto> adjuntos = procesarArchivos(archivos, solicitud);
        if (adjuntos != null) {
            solicitud.setAdjuntos(adjuntos);
        }
    }


    //Esta función llama a guardarArchivosEnDisco (arriba)
    public List<Adjunto> procesarArchivos(List<MultipartFile> archivos, Solicitud solicitud) {
    List<Adjunto> lista = new ArrayList<>();
    
    if (archivos != null && !archivos.isEmpty()) {
        for (MultipartFile archivo : archivos) {
            if (!archivo.isEmpty()) {
                // Llamamos a tu lógica de escritura física
                String rutaEnDisco = almacenamiento.guardarArchivoEnDisco(archivo);
                
                // Creamos el objeto para la Base de Datos
                Adjunto adj = new Adjunto();
                adj.setNombreArchivo(archivo.getOriginalFilename());
                adj.setRuta(rutaEnDisco); // Guardamos el String de la ruta
                adj.setTipoArchivo(archivo.getContentType());
                adj.setTamañoKB(archivo.getSize() / 1024); // Convertimos bytes a KB
                adj.setSolicitud(solicitud); // Vinculamos a la solicitud actual
                
                lista.add(adj);
            }
        }
    }
        return lista;
    }


}
