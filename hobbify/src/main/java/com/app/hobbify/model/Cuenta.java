package com.app.hobbify.model;

public record Cuenta(long id, String nombre, String apellido, String correo, String hash,
                     String estado, String nombreComercial) {

    public TipoCuenta tipo() {
        return nombreComercial == null ? TipoCuenta.USUARIO : TipoCuenta.EMPRESA;
    }

    public boolean activa() {
        return "activo".equals(estado);
    }
}
