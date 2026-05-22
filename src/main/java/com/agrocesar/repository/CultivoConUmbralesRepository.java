package com.agrocesar.repository;

import com.agrocesar.dto.CultivoConUmbralesDTO;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.List;
import java.util.Optional;

@RegisterRowMapper(CultivoConUmbralesDTOMapper.class)
public interface CultivoConUmbralesRepository {

    @SqlQuery("SELECT * FROM V_CULTIVOS_CON_UMBRALES WHERE ACTIVO = 1")
    List<CultivoConUmbralesDTO> findAll();

    @SqlQuery("SELECT * FROM V_CULTIVOS_CON_UMBRALES WHERE ID = :id AND ACTIVO = 1")
    Optional<CultivoConUmbralesDTO> findById(@Bind("id") Long id);

    @SqlQuery("SELECT * FROM V_CULTIVOS_CON_UMBRALES WHERE USUARIO_ID = :usuarioId AND ACTIVO = 1")
    List<CultivoConUmbralesDTO> findByUsuarioId(@Bind("usuarioId") Long usuarioId);
}