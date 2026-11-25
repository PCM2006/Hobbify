package com.app.hobbify.controller;

import java.security.Principal;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.hobbify.model.DatosPerfilEmpresa;
import com.app.hobbify.model.PerfilEmpresa;
import com.app.hobbify.model.PerfilPublico;
import com.app.hobbify.service.EmpresaService;

@RestController
@RequestMapping("/api/v1/empresas")
class EmpresaController {

    private final EmpresaService empresas;

    EmpresaController(EmpresaService empresas) {
        this.empresas = empresas;
    }

    @GetMapping("/me")
    PerfilEmpresa perfilPropio(Principal principal) {
        return empresas.perfilPropio(principal.getName());
    }

    @PutMapping("/me")
    PerfilEmpresa actualizarPerfilPropio(Principal principal, @Valid @RequestBody DatosPerfilEmpresa datos) {
        return empresas.actualizarPerfilPropio(principal.getName(), datos);
    }

    @GetMapping("/{id:[0-9]+}")
    PerfilPublico perfilPublico(@PathVariable long id) {
        return empresas.perfilPublico(id);
    }
}
