package com.app.hobbify.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.app.hobbify.model.DatosPerfilEmpresa;
import com.app.hobbify.model.PerfilEmpresa;
import com.app.hobbify.model.PerfilPublico;
import com.app.hobbify.repository.EmpresaRepository;

@Service
public class EmpresaService {

    private final EmpresaRepository empresas;

    EmpresaService(EmpresaRepository empresas) {
        this.empresas = empresas;
    }

    public PerfilEmpresa perfilPropio(String correo) {
        return empresas.buscarPerfilPropio(correo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cuenta no disponible"));
    }

    public PerfilEmpresa actualizarPerfilPropio(String correo, DatosPerfilEmpresa datos) {
        return empresas.actualizarPerfilPropio(correo, datos)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cuenta no disponible"));
    }

    public PerfilPublico perfilPublico(long id) {
        PerfilEmpresa perfil = empresas.buscarPerfilPublico(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada"));
        return new PerfilPublico(perfil, empresas.publicacionesVisibles(id));
    }
}
