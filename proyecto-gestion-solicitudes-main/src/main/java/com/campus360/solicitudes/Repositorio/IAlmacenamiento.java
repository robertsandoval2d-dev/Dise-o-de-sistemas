package com.campus360.solicitudes.Repositorio;

import org.springframework.web.multipart.MultipartFile;
public interface IAlmacenamiento {
    public String guardarArchivoEnDisco(MultipartFile file);
}
