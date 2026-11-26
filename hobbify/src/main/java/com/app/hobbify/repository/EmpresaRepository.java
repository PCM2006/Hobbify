package com.app.hobbify.repository;

import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.app.hobbify.model.DatosPerfilEmpresa;
import com.app.hobbify.model.PerfilEmpresa;
import com.app.hobbify.model.PresentacionEmpresa;
import com.app.hobbify.model.PublicacionResumen;

@Repository
public class EmpresaRepository {

    private static final String COLUMNAS_PERFIL = """
            e.id_usuario, e.nombre_comercial, e.descripcion, e.correo_publico,
            e.telefono_publico, e.sitio_web, e.instagram_url, e.facebook_url, e.whatsapp,
            e.color_principal
            """;

    private static final String CONSULTA_PERFIL_ACTIVO = "SELECT " + COLUMNAS_PERFIL + """
            FROM public.empresas e
            JOIN public.usuarios u ON u.id_usuario = e.id_usuario
            WHERE u.estado = 'activo'
            """;

    private static final String ACTUALIZACION_PERFIL_PROPIO = """
            FROM public.usuarios u
            WHERE u.id_usuario = e.id_usuario AND u.correo = :correo AND u.estado = 'activo'
            """ + "RETURNING " + COLUMNAS_PERFIL;

    private static final RowMapper<PerfilEmpresa> PERFIL_MAPPER = (rs, row) -> new PerfilEmpresa(
            rs.getLong("id_usuario"), rs.getString("nombre_comercial"), rs.getString("descripcion"),
            rs.getString("correo_publico"), rs.getString("telefono_publico"), rs.getString("sitio_web"),
            rs.getString("instagram_url"), rs.getString("facebook_url"), rs.getString("whatsapp"),
            rs.getString("color_principal"));

    private final JdbcClient jdbc;

    EmpresaRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<PerfilEmpresa> buscarPerfilPropio(String correo) {
        return jdbc.sql(CONSULTA_PERFIL_ACTIVO + "AND u.correo = :correo")
                .param("correo", correo)
                .query(PERFIL_MAPPER)
                .optional();
    }

    public Optional<PerfilEmpresa> buscarPerfilPublico(long id) {
        return jdbc.sql(CONSULTA_PERFIL_ACTIVO + "AND e.id_usuario = :id")
                .param("id", id)
                .query(PERFIL_MAPPER)
                .optional();
    }

    public Optional<PerfilEmpresa> actualizarPerfilPropio(String correo, DatosPerfilEmpresa datos) {
        return jdbc.sql("""
                UPDATE public.empresas e
                SET nombre_comercial = :nombreComercial, descripcion = :descripcion,
                    correo_publico = :correoPublico, telefono_publico = :telefonoPublico,
                    sitio_web = :sitioWeb, instagram_url = :instagramUrl,
                    facebook_url = :facebookUrl, whatsapp = :whatsapp
                """ + ACTUALIZACION_PERFIL_PROPIO)
                .param("correo", correo)
                .param("nombreComercial", datos.nombreComercial())
                .param("descripcion", datos.descripcion(), Types.VARCHAR)
                .param("correoPublico", datos.correoPublico(), Types.VARCHAR)
                .param("telefonoPublico", datos.telefonoPublico(), Types.VARCHAR)
                .param("sitioWeb", datos.sitioWeb(), Types.VARCHAR)
                .param("instagramUrl", datos.instagramUrl(), Types.VARCHAR)
                .param("facebookUrl", datos.facebookUrl(), Types.VARCHAR)
                .param("whatsapp", datos.whatsapp(), Types.VARCHAR)
                .query(PERFIL_MAPPER)
                .optional();
    }

    public Optional<PerfilEmpresa> actualizarPresentacionPropia(String correo, PresentacionEmpresa datos) {
        return jdbc.sql("""
                UPDATE public.empresas e
                SET nombre_comercial = :nombreComercial, descripcion = :descripcion,
                    color_principal = :colorPrincipal
                """ + ACTUALIZACION_PERFIL_PROPIO)
                .param("correo", correo)
                .param("nombreComercial", datos.nombreComercial())
                .param("descripcion", datos.descripcion())
                .param("colorPrincipal", datos.colorPrincipal())
                .query(PERFIL_MAPPER)
                .optional();
    }

    public List<PublicacionResumen> publicacionesVisibles(long idEmpresa) {
        return jdbc.sql("""
                SELECT id_publicacion, tipo, titulo, publicada_en
                FROM public.publicaciones
                WHERE id_empresa = :idEmpresa AND estado = 'publicada'
                ORDER BY publicada_en DESC, id_publicacion DESC
                """)
                .param("idEmpresa", idEmpresa)
                .query((rs, row) -> new PublicacionResumen(rs.getLong("id_publicacion"), rs.getString("tipo"),
                        rs.getString("titulo"), rs.getObject("publicada_en", OffsetDateTime.class)))
                .list();
    }
}
