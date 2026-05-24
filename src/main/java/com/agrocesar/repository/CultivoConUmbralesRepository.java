package com.agrocesar.repository;

import com.agrocesar.dto.CultivoConUmbralesDTO;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import com.agrocesar.dto.RankingCultivoDTO;
import java.util.List;
import java.util.Optional;
import com.agrocesar.repository.RankingCultivoDTOMapper;


@RegisterRowMapper(RankingCultivoDTOMapper.class)
@RegisterRowMapper(CultivoConUmbralesDTOMapper.class)
public interface CultivoConUmbralesRepository {

    @SqlQuery("SELECT * FROM V_CULTIVOS_CON_UMBRALES WHERE ACTIVO = 1")
    List<CultivoConUmbralesDTO> findAll();

    @SqlQuery("SELECT * FROM V_CULTIVOS_CON_UMBRALES WHERE ID = :id AND ACTIVO = 1")
    Optional<CultivoConUmbralesDTO> findById(@Bind("id") Long id);

    @SqlQuery("SELECT * FROM V_CULTIVOS_CON_UMBRALES WHERE USUARIO_ID = :usuarioId AND ACTIVO = 1")
    List<CultivoConUmbralesDTO> findByUsuarioId(@Bind("usuarioId") Long usuarioId);

    @SqlQuery("""
    SELECT cultivo AS nombre, municipio, SUM(hectareas) AS totalHectareas
    FROM V_CULTIVOS_CON_UMBRALES
    WHERE ACTIVO = 1
    GROUP BY cultivo, municipio
    ORDER BY totalHectareas DESC
    """)
    List<RankingCultivoDTO> findRankingCultivos();
}