package com.app.hobbify.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.hibernate.validator.constraints.URL;

public record DatosPerfilEmpresa(
        @NotBlank @Size(max = 140) String nombreComercial,
        String descripcion,
        @Email @Size(max = 254) String correoPublico,
        @Size(max = 30) String telefonoPublico,
        @URL(regexp = "(?i)https?://[^\\s]+") @Size(max = 250) String sitioWeb,
        @URL(regexp = "(?i)https?://[^\\s]+") String instagramUrl,
        @URL(regexp = "(?i)https?://[^\\s]+") String facebookUrl,
        @Size(max = 30) String whatsapp) {

    public DatosPerfilEmpresa {
        nombreComercial = limpiar(nombreComercial);
        descripcion = limpiar(descripcion);
        correoPublico = limpiar(correoPublico);
        telefonoPublico = limpiar(telefonoPublico);
        sitioWeb = limpiar(sitioWeb);
        instagramUrl = limpiar(instagramUrl);
        facebookUrl = limpiar(facebookUrl);
        whatsapp = limpiar(whatsapp);
    }

    private static String limpiar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.strip();
    }
}
