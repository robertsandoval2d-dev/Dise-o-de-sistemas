package com.campus360.solicitudes.Servicios.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.campus360.solicitudes.Dominio.Usuario;
import com.campus360.solicitudes.Repositorio.IUsuarioRepository;

@Component
public class UsuarioManager {
    @Autowired
    private IUsuarioRepository repoUsuario;

    public Usuario obtenerOGuardarUsuario(int id, String nombre, String rol) {
        if (repoUsuario.existsById(id)) {
            return repoUsuario.getReferenceById(id);
        }
        Usuario nuevo = new Usuario();
        nuevo.setIdUsuario(id);
        nuevo.setNombre(nombre);
        nuevo.setRol(rol);
        return repoUsuario.save(nuevo);
    }

}
