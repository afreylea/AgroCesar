package com.agrocesar.repository;

import com.agrocesar.model.Usuario;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.OutParameters;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Repository
public class UsuarioRepository {

    private final Jdbi jdbi;
    private final UsuarioMapper mapper = new UsuarioMapper();

    public UsuarioRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    // ----------------------------------------------------------------
    //  CONSULTAS
    // ----------------------------------------------------------------

    public Optional<Usuario> findById(Long id) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_USUARIOS.prc_find_by_id(:p_id, :p_cursor) }")
                .bind("p_id", id)
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, Optional<Usuario>>) out -> {
                    try {
                        ResultSet rs = (ResultSet) out.getObject("p_cursor");
                        if (rs.next()) {
                            return Optional.of(mapper.map(rs, null));
                        }
                        return Optional.<Usuario>empty();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
        );
    }

    public Optional<Usuario> findByEmail(String email) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_USUARIOS.prc_find_by_email(:p_email, :p_cursor) }")
                .bind("p_email", email)
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, Optional<Usuario>>) out -> {
                    try {
                        ResultSet rs = (ResultSet) out.getObject("p_cursor");
                        if (rs.next()) {
                            return Optional.of(mapper.map(rs, null));
                        }
                        return Optional.<Usuario>empty();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
        );
    }

    // Usado por CustomUserDetailsService — solo devuelve usuarios activos
    public Optional<Usuario> findByEmailAndActivo(String email) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_USUARIOS.prc_find_by_email_activo(:p_email, :p_cursor) }")
                .bind("p_email", email)
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, Optional<Usuario>>) out -> {
                    try {
                        ResultSet rs = (ResultSet) out.getObject("p_cursor");
                        if (rs.next()) {
                            return Optional.of(mapper.map(rs, null));
                        }
                        return Optional.<Usuario>empty();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
        );
    }

    public List<Usuario> findActivos() {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_USUARIOS.prc_find_activos(:p_cursor) }")
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, List<Usuario>>) out -> {
                    List<Usuario> list = new ArrayList<>();
                    try {
                        ResultSet rs = (ResultSet) out.getObject("p_cursor");
                        while (rs.next()) {
                            list.add(mapper.map(rs, null));
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return list;
                })
        );
    }

    public List<Usuario> findInactivos() {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_USUARIOS.prc_find_inactivos(:p_cursor) }")
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, List<Usuario>>) out -> {
                    List<Usuario> list = new ArrayList<>();
                    try {
                        ResultSet rs = (ResultSet) out.getObject("p_cursor");
                        while (rs.next()) {
                            list.add(mapper.map(rs, null));
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return list;
                })
        );
    }

    public List<Usuario> findAll() {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_USUARIOS.prc_find_all(:p_cursor) }")
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, List<Usuario>>) out -> {
                    List<Usuario> list = new ArrayList<>();
                    try {
                        ResultSet rs = (ResultSet) out.getObject("p_cursor");
                        while (rs.next()) {
                            list.add(mapper.map(rs, null));
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return list;
                })
        );
    }

    public List<Usuario> findByRol(String rol) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_USUARIOS.prc_find_by_rol(:p_rol, :p_cursor) }")
                .bind("p_rol", rol)
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, List<Usuario>>) out -> {
                    List<Usuario> list = new ArrayList<>();
                    try {
                        ResultSet rs = (ResultSet) out.getObject("p_cursor");
                        while (rs.next()) {
                            list.add(mapper.map(rs, null));
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return list;
                })
        );
    }

    // ----------------------------------------------------------------
    //  ESCRITURA
    // ----------------------------------------------------------------

    public void insert(Usuario usuario) {
        jdbi.useHandle(handle ->
            handle.createCall(
                "{ call PKG_USUARIOS.prc_insert(:p_nombre, :p_apellido, :p_email, " +
                ":p_password_hash, :p_rol, :p_municipio_id, :p_telefono) }")
                .bind("p_nombre",        usuario.getNombre())
                .bind("p_apellido",      usuario.getApellido())
                .bind("p_email",         usuario.getEmail())
                .bind("p_password_hash", usuario.getPasswordHash())
                .bind("p_rol",           usuario.getRol())
                .bind("p_municipio_id",  usuario.getMunicipioId())
                .bind("p_telefono",      usuario.getTelefono())
                .invoke()
        );
    }

    public int update(Usuario usuario) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_USUARIOS.prc_update(:p_id, :p_nombre, :p_apellido, " +
                ":p_email, :p_municipio_id, :p_telefono, :p_rows_updated) }")
                .bind("p_id",           usuario.getId())
                .bind("p_nombre",       usuario.getNombre())
                .bind("p_apellido",     usuario.getApellido())
                .bind("p_email",        usuario.getEmail())
                .bind("p_municipio_id", usuario.getMunicipioId())
                .bind("p_telefono",     usuario.getTelefono())
                .registerOutParameter("p_rows_updated", java.sql.Types.NUMERIC)
                .invoke((Function<OutParameters, Integer>) out -> out.getInt("p_rows_updated"))
        );
    }

    public void actualizarUltimoLogin(String email) {
        jdbi.useHandle(handle ->
            handle.createCall(
                "{ call PKG_USUARIOS.prc_actualizar_ultimo_login(:p_email) }")
                .bind("p_email", email) 
                .invoke()
        );
    }

    public int desactivar(Long id) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_USUARIOS.prc_desactivar(:p_id, :p_rows_updated) }")
                .bind("p_id", id)
                .registerOutParameter("p_rows_updated", java.sql.Types.NUMERIC)
                .invoke((Function<OutParameters, Integer>) out -> out.getInt("p_rows_updated"))
        );
    }

    public int activar(Long id) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_USUARIOS.prc_activar(:p_id, :p_rows_updated) }")
                .bind("p_id", id)
                .registerOutParameter("p_rows_updated", java.sql.Types.NUMERIC)
                .invoke((Function<OutParameters, Integer>) out -> out.getInt("p_rows_updated"))
        );
    }
}