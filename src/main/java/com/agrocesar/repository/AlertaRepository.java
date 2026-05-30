package com.agrocesar.repository;

import com.agrocesar.dto.AlertaVistaDTO;
import com.agrocesar.model.Alerta;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import com.agrocesar.dto.CultivoMasAfectadoDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RegisterRowMapper(AlertaVistaDTOMapper.class)
@RegisterRowMapper(CultivoMasAfectadoMapper.class)

public interface AlertaRepository {

    //Escritura (tabla ALERTAS)

    @SqlUpdate("""
        INSERT INTO ALERTAS (
            CULTIVO_AGRICULTOR_ID, TIPO_ALERTA, SEVERIDAD, DESCRIPCION,
            RECOMENDACION, FECHA_DIA_PRONOSTICO, VALOR_DETECTADO, VALOR_UMBRAL
        ) VALUES (
            :cultivoAgricultorId, :tipoAlerta, :severidad, :descripcion,
            :recomendacion, :fechaDiaPronostico, :valorDetectado, :valorUmbral
        )
        """)
    void insert(@BindBean Alerta alerta);

    @SqlUpdate("""
        UPDATE ALERTAS SET RECOMENDACION = :recomendacion
        WHERE ID = :id
        """)
    int actualizarRecomendacion(@Bind("id") Long id,
                                @Bind("recomendacion") String recomendacion);

    @SqlUpdate("UPDATE ALERTAS SET LEIDA = 1 WHERE ID = :id")
    int marcarLeida(@Bind("id") Long id);

    //Lectura (vista V_ALERTAS)

    @SqlQuery("""
        SELECT * FROM V_ALERTAS
        WHERE ALERTA_ID = :id
        """)
    Optional<AlertaVistaDTO> findById(@Bind("id") Long id);

    @SqlQuery("""
        SELECT * FROM V_ALERTAS
        WHERE USUARIO_ID = :usuarioId
        ORDER BY FECHA_GENERACION DESC
        """)
    List<AlertaVistaDTO> findByUsuarioId(@Bind("usuarioId") Long usuarioId);

    @SqlQuery("""
        SELECT * FROM V_ALERTAS
        WHERE USUARIO_ID = :usuarioId AND LEIDA = 0
        ORDER BY FECHA_GENERACION DESC
        """)
    List<AlertaVistaDTO> findNoLeidasByUsuarioId(@Bind("usuarioId") Long usuarioId);
    
    @SqlQuery("""
        SELECT * FROM V_ALERTAS
        WHERE MUNICIPIO_ID = :municipioId
        ORDER BY FECHA_GENERACION DESC
        """)
    List<AlertaVistaDTO> findByMunicipioId(@Bind("municipioId") Long municipioId);

    @SqlQuery("""
        SELECT * FROM V_ALERTAS
        WHERE CATALOGO_ID = :catalogoId
        ORDER BY FECHA_GENERACION DESC
        """)
    List<AlertaVistaDTO> findByCatalogoId(@Bind("catalogoId") Long catalogoId);

    @SqlQuery("""
        SELECT * FROM V_ALERTAS
        WHERE CULTIVO_AGRICULTOR_ID = :cultivoId
        ORDER BY FECHA_GENERACION DESC
        """)
    List<AlertaVistaDTO> findByCultivoId(@Bind("cultivoId") Long cultivoId);

    @SqlQuery("""
        SELECT * FROM V_ALERTAS
        WHERE USUARIO_ID = :usuarioId AND CULTIVO_AGRICULTOR_ID = :cultivoId
        ORDER BY FECHA_GENERACION DESC
        """)
    List<AlertaVistaDTO> findByUsuarioIdAndCultivoId(@Bind("usuarioId") Long usuarioId,
                                                     @Bind("cultivoId") Long cultivoId);

    @SqlQuery("""
        SELECT * FROM V_ALERTAS
        WHERE TIPO_ALERTA = :tipoAlerta
        ORDER BY FECHA_GENERACION DESC
        """)
    List<AlertaVistaDTO> findByTipo(@Bind("tipoAlerta") String tipoAlerta);

    @SqlQuery("""
        SELECT * FROM V_ALERTAS
        WHERE USUARIO_ID = :usuarioId AND TIPO_ALERTA = :tipoAlerta
        ORDER BY FECHA_GENERACION DESC
        """)
    List<AlertaVistaDTO> findByUsuarioIdAndTipo(@Bind("usuarioId") Long usuarioId,
                                                @Bind("tipoAlerta") String tipoAlerta);

    @SqlQuery("""
        SELECT * FROM V_ALERTAS
        WHERE SEVERIDAD = :severidad
        ORDER BY FECHA_GENERACION DESC
        """)
    List<AlertaVistaDTO> findBySeveridad(@Bind("severidad") String severidad);

    @SqlQuery("""
        SELECT * FROM V_ALERTAS
        WHERE USUARIO_ID = :usuarioId AND SEVERIDAD = :severidad
        ORDER BY FECHA_GENERACION DESC
        """)
    List<AlertaVistaDTO> findByUsuarioIdAndSeveridad(@Bind("usuarioId") Long usuarioId,
                                                    @Bind("severidad") String severidad);

    @SqlQuery("""
        SELECT * FROM V_ALERTAS
        ORDER BY FECHA_GENERACION DESC
        """)
    List<AlertaVistaDTO> findAll();
    @SqlQuery("""
    SELECT * FROM V_ALERTAS
    WHERE FECHA_DIA_PRONOSTICO BETWEEN :fechaDesde AND :fechaHasta
    ORDER BY FECHA_DIA_PRONOSTICO DESC
    """)
    List<AlertaVistaDTO> findByRangoFechas(
            @Bind("fechaDesde") LocalDate fechaDesde,
            @Bind("fechaHasta") LocalDate fechaHasta
    );

    @SqlQuery("""
    SELECT CULTIVO AS nombreCultivo, MUNICIPIO, COUNT(*) AS totalAlertas
    FROM V_ALERTAS
    WHERE FECHA_DIA_PRONOSTICO BETWEEN :fechaDesde AND :fechaHasta
    GROUP BY CULTIVO, MUNICIPIO
    ORDER BY COUNT(*) DESC
    """)
    List<CultivoMasAfectadoDTO> findCultivosMasAfectados(
            @Bind("fechaDesde") LocalDate fechaDesde,
            @Bind("fechaHasta") LocalDate fechaHasta
    );

    @SqlQuery("SELECT COUNT(*) FROM ALERTAS WHERE LEIDA = 0")
    int countActivas();

    @SqlQuery("SELECT COUNT(*) FROM ALERTAS WHERE LEIDA = 0 AND SEVERIDAD IN ('ALTA', 'CRITICA')")
    int countCriticas();
}