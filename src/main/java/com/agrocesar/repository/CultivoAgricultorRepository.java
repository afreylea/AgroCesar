package com.agrocesar.repository;

import com.agrocesar.model.CultivoAgricultor;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import java.util.List;
import java.util.Optional;

// @RegisterBeanMapper mapea automáticamente columnas SQL a campos Java
// El nombre de columna debe coincidir con el nombre del campo (case-insensitive)
@RegisterBeanMapper(CultivoAgricultor.class)
public interface CultivoAgricultorRepository {

    // Devuelve todos los cultivos activos de un agricultor
    @SqlQuery("SELECT * FROM CULTIVOS_AGRICULTOR WHERE USUARIO_ID = :usuarioId AND ACTIVO = 1")
    List<CultivoAgricultor> findByUsuarioId(@Bind("usuarioId") Long usuarioId);

    // Busca un cultivo por ID y verifica que pertenezca al agricultor autenticado
    @SqlQuery("SELECT * FROM CULTIVOS_AGRICULTOR WHERE ID = :id AND USUARIO_ID = :usuarioId AND ACTIVO = 1")
    Optional<CultivoAgricultor> findByIdAndUsuarioId(@Bind("id") Long id, @Bind("usuarioId") Long usuarioId);

    // Inserta un nuevo cultivo y retorna el ID generado por la secuencia Oracle
    @SqlUpdate("INSERT INTO CULTIVOS_AGRICULTOR (USUARIO_ID, CATALOGO_ID, MUNICIPIO_ID, HECTAREAS, " +
            "FECHA_SIEMBRA, LATITUD_CULTIVO, LONGITUD_CULTIVO, TIPO_SUELO, ACTIVO, FECHA_CREACION) " +
            "VALUES (:usuarioId, :catalogoId, :municipioId, :hectareas, " +
            ":fechaSiembra, :latitudCultivo, :longitudCultivo, :tipoSuelo, 1, SYSDATE)")
    @GetGeneratedKeys("ID")
    Long insert(@BindBean CultivoAgricultor cultivo);

    // Actualiza los campos editables de un cultivo existente
    @SqlUpdate("UPDATE CULTIVOS_AGRICULTOR SET HECTAREAS = :hectareas, " +
            "TEMP_MIN_OVERRIDE = :tempMinOverride, TEMP_MAX_OVERRIDE = :tempMaxOverride, " +
            "LLUVIA_MIN_OVERRIDE = :lluviaMinOverride, LLUVIA_MAX_OVERRIDE = :lluviaMaxOverride, " +
            "HUMEDAD_MIN_OVERRIDE = :humedadMinOverride, HUMEDAD_MAX_OVERRIDE = :humedadMaxOverride, " +
            "FECHA_ACTUALIZACION = SYSDATE " +
            "WHERE ID = :id AND USUARIO_ID = :usuarioId")
    int update(@BindBean CultivoAgricultor cultivo);

    // Baja lógica: marca ACTIVO = 0, no borra el registro
    @SqlUpdate("UPDATE CULTIVOS_AGRICULTOR SET ACTIVO = 0, FECHA_ACTUALIZACION = SYSDATE " +
            "WHERE ID = :id AND USUARIO_ID = :usuarioId")
    int deactivate(@Bind("id") Long id, @Bind("usuarioId") Long usuarioId);
}
