package com.agrocesar.repository;

import com.agrocesar.model.Usuario;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

@RegisterBeanMapper(Usuario.class)
public interface UsuarioRepository {

    @SqlQuery("SELECT id, nombre, email, password_hash, rol, municipio_id, activo " +
            "FROM usuarios WHERE id = :id")
    Optional<Usuario> findById(@Bind("id") Long id);

    @SqlQuery("SELECT id, nombre, email, password_hash, rol, municipio_id, activo " +
            "FROM usuarios WHERE email = :email")
    Optional<Usuario> findByEmail(@Bind("email") String email);

    @SqlQuery("SELECT id, nombre, email, password_hash, rol, municipio_id, activo " +
            "FROM usuarios WHERE activo = 1 ORDER BY nombre")
    List<Usuario> findAllActivos();

    @SqlQuery("SELECT id, nombre, email, password_hash, rol, municipio_id, activo " +
            "FROM usuarios WHERE rol = :rol AND activo = 1 ORDER BY nombre")
    List<Usuario> findByRol(@Bind("rol") String rol);

    @SqlUpdate("INSERT INTO usuarios (nombre, email, password_hash, rol, municipio_id, activo) " +
            "VALUES (:nombre, :email, :passwordHash, :rol, :municipioId, 1)")
    @GetGeneratedKeys("id")
    Long insert(@BindBean Usuario usuario);

    @SqlUpdate("UPDATE usuarios SET nombre = :nombre, email = :email, municipio_id = :municipioId " +
            "WHERE id = :id")
    int update(@BindBean Usuario usuario);

    @SqlUpdate("UPDATE usuarios SET activo = 0 WHERE id = :id")
    int desactivar(@Bind("id") Long id);

    @SqlUpdate("UPDATE usuarios
}
