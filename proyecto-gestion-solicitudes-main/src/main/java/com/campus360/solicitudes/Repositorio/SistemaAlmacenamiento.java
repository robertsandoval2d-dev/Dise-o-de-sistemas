package com.campus360.solicitudes.Repositorio;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Service
public class SistemaAlmacenamiento implements IAlmacenamiento {
    public SistemaAlmacenamiento(){

    }

    public String guardarArchivoEnDisco(MultipartFile file){
        try {
            // 1. Definimos la carpeta "uploads" dentro de la raíz del proyecto
            String rootPath = System.getProperty("user.dir");
            String nombreCarpeta = "uploads";

            // 2. Creamos el objeto File para la subcarpeta
            File directory = new File(rootPath, nombreCarpeta);

            // 3. Si la subcarpeta no existe, la creamos
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 4. Generar nombre único (Timestamp + nombre original)
            String nombreUnico = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            // 5. Usamos Paths.get con dos argumentos para que Java ponga el "/" o "\" correcto
            Path rutaDestino = Paths.get(directory.getAbsolutePath(), nombreUnico);

            // 6. Escribimos los bytes del archivo
            Files.write(rutaDestino, file.getBytes());

            // 7. Retornamos la ruta absoluta para guardarla en la base de datos
            return rutaDestino.toString();

        } catch (IOException e) {
            throw new RuntimeException("Error al escribir el archivo en el servidor: " + e.getMessage());
        }
    }

}
