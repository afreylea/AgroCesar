package com.agrocesar.repository;

import com.agrocesar.dto.RankingCultivoDTO;
import com.agrocesar.model.CultivoAgricultor;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

@RegisterRowMapper(CultivoAgricultorMapper.class)
public interface CultivoAgricultorRepository {

    @SqlQuery("""
        SELECT SEQ_CULTIVOS_AGRICULTOR.NEXTVAL FROM DUAL
             """)
    Long nextId();

    @SqlUpdate("""
        INSERT INTO CULTIVOS_AGRICULTOR (ID, CATALOGO_ID, USUARIO_ID, MUNICIPIO_ID, 
                                HECTAREAS, FECHA_SIEMBRA, 
                                TEMP_MIN_OVERRIDE, TEMP_MAX_OVERRIDE, 
                                LLUVIA_MIN_OVERRIDE, LLUVIA_MAX_OVERRIDE,
                                HUMEDAD_MIN_OVERRIDE, HUMEDAD_MAX_OVERRIDE, 
                                LATITUD_CULTIVO, LONGITUD_CULTIVO, TIPO_SUELO)
        VALUES (:id, :catalogoId, :usuarioId, :municipioId, :hectareas, :fechaSiembra,
                :tempMinOverride, :tempMaxOverride,
                :lluviaMinOverride, :lluviaMaxOverride,
                :humedadMinOverride, :humedadMaxOverride,
                :latitudCultivo, :longitudCultivo, :tipoSuelo)
              """)
    void insert(@BindBean CultivoAgricultor cultivoAgricultor);

    @SqlQuery("""
        SELECT * FROM CULTIVOS_AGRICULTOR
        WHERE USUARIO_ID = :usuarioId AND ACTIVO = 1
        ORDER BY FECHA_CREACION DESC
             """)
    List<CultivoAgricultor> findActiveByUsuarioId(@Bind("usuarioId") Long usuarioId);

    @SqlQuery("""
        SELECT * FROM CULTIVOS_AGRICULTOR
        WHERE USUARIO_ID = :usuarioId AND ACTIVO = 0
        ORDER BY FECHA_CREACION DESC
             """)
    List<CultivoAgricultor> findInactiveByUsuarioId(@Bind("usuarioId") Long usuarioId);

    @SqlQuery("""
        SELECT * FROM CULTIVOS_AGRICULTOR
        WHERE MUNICIPIO_ID = :municipioId AND ACTIVO = 1
        ORDER BY FECHA_CREACION DESC
             """)
    List<CultivoAgricultor> findByMunicipioId(@Bind("municipioId") Long municipioId);

    @SqlQuery("SELECT * FROM CULTIVOS_AGRICULTOR WHERE ID = :id")
    Optional<CultivoAgricultor> findById(@Bind("id") Long id);

    @SqlUpdate("""
        UPDATE CULTIVOS_AGRICULTOR SET
        HECTAREAS            = :hectareas,
        TEMP_MIN_OVERRIDE    = :tempMinOverride,
        TEMP_MAX_OVERRIDE    = :tempMaxOverride,
        LLUVIA_MIN_OVERRIDE  = :lluviaMinOverride,
        LLUVIA_MAX_OVERRIDE  = :lluviaMaxOverride,
        HUMEDAD_MIN_OVERRIDE = :humedadMinOverride,
        HUMEDAD_MAX_OVERRIDE = :humedadMaxOverride,
        LATITUD_CULTIVO      = :latitudCultivo,
        LONGITUD_CULTIVO     = :longitudCultivo,
        TIPO_SUELO           = :tipoSuelo
        WHERE ID = :id AND ACTIVO = 1
              """)
    int actualizar(@BindBean CultivoAgricultor cultivo);

    @SqlUpdate("UPDATE CULTIVOS_AGRICULTOR SET ACTIVO = 0 WHERE ID = :id")
    int desactivar(@Bind("id") Long id);

    @SqlQuery("""
        SELECT cc.NOMBRE, m.NOMBRE AS MUNICIPIO, SUM(ca.HECTAREAS) AS TOTAL_HECTAREAS
        FROM CULTIVOS_AGRICULTOR ca
        JOIN CULTIVOS_CATALOGO cc ON ca.CATALOGO_ID = cc.ID
        JOIN MUNICIPIOS m         ON ca.MUNICIPIO_ID = m.ID
        WHERE ca.ACTIVO = 1
        GROUP BY cc.NOMBRE, m.NOMBRE
        ORDER BY TOTAL_HECTAREAS DESC
        FETCH FIRST 5 ROWS ONLY
             """)
    @RegisterRowMapper(RankingCultivoDTOMapper.class)
    List<RankingCultivoDTO> findRankingCultivos();
}