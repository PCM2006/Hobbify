package com.app.hobbify.model;

public record PerfilEmpresa(long id, String nombreComercial, String descripcion, String correoPublico,
                            String telefonoPublico, String sitioWeb, String instagramUrl, String facebookUrl,
                            String whatsapp) {}
