package com.app.hobbify.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PresentacionEmpresa(
        @NotBlank @Size(max = 140) String nombreComercial,
        @NotBlank String descripcion,
        @NotBlank @Pattern(regexp = "#[0-9A-Fa-f]{6}") String colorPrincipal) {

    public PresentacionEmpresa {
        nombreComercial = nombreComercial == null ? null : nombreComercial.strip();
        descripcion = descripcion == null ? null : descripcion.strip();
    }
}
