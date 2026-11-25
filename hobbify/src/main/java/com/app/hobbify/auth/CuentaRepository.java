package com.app.hobbify.auth;

import java.sql.Types;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
class CuentaRepository {

    private final JdbcClient jdbc;

    CuentaRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    Optional<Long> crearUsuario(String nombre, String apellido, String correo, String hash) {
        return jdbc.sql("""
                INSERT INTO public.usuarios (nombre, apellido, correo, contrasena_hash)
                VALUES (:nombre, :apellido, :correo, :hash)
                ON CONFLICT (correo) DO NOTHING
                RETURNING id_usuario
                """)
                .param("nombre", nombre)
                .param("apellido", apellido, Types.VARCHAR)
                .param("correo", correo)
                .param("hash", hash)
                .query(Long.class)
                .optional();
    }

    void crearEmpresa(long idUsuario, String nombreComercial) {
        jdbc.sql("""
                INSERT INTO public.empresas (id_usuario, nombre_comercial)
                VALUES (:idUsuario, :nombreComercial)
                """)
                .param("idUsuario", idUsuario)
                .param("nombreComercial", nombreComercial)
                .update();
    }

    Optional<Cuenta> buscarPorCorreo(String correo) {
        return jdbc.sql("""
                SELECT u.id_usuario, u.nombre, u.apellido, u.correo, u.contrasena_hash,
                       u.estado, e.nombre_comercial
                FROM public.usuarios u
                LEFT JOIN public.empresas e ON e.id_usuario = u.id_usuario
                WHERE u.correo = lower(btrim(:correo))
                """)
                .param("correo", correo)
                .query((rs, row) -> new Cuenta(rs.getLong("id_usuario"), rs.getString("nombre"),
                        rs.getString("apellido"), rs.getString("correo"), rs.getString("contrasena_hash"),
                        rs.getString("estado"), rs.getString("nombre_comercial")))
                .optional();
    }

    record Cuenta(long id, String nombre, String apellido, String correo, String hash,
                  String estado, String nombreComercial) {

        TipoCuenta tipo() {
            return nombreComercial == null ? TipoCuenta.USUARIO : TipoCuenta.EMPRESA;
        }

        boolean activa() {
            return "activo".equals(estado);
        }
    }
}
