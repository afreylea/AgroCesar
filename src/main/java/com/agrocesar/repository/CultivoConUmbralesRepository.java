package com.agrocesar.repository;

import com.agrocesar.dto.CultivoConUmbralesDTO;
import com.agrocesar.dto.RankingCultivoDTO;
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
public class CultivoConUmbralesRepository {

    private final Jdbi jdbi;
    private final CultivoConUmbralesDTOMapper mapperCultivos = new CultivoConUmbralesDTOMapper();
    private final RankingCultivoDTOMapper mapperRanking = new RankingCultivoDTOMapper();

    public CultivoConUmbralesRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    // ----------------------------------------------------------------
    //  CONSULTAS sobre V_CULTIVOS_CON_UMBRALES
    // ----------------------------------------------------------------

    public List<CultivoConUmbralesDTO> findAll() {
        return jdbi.withHandle(handle ->
            handle.createCall("{ call PKG_CULTIVOS_UMBRALES.prc_find_all(:p_cursor) }")
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, List<CultivoConUmbralesDTO>>) out -> {
                    List<CultivoConUmbralesDTO> list = new ArrayList<>();
                    try {
                        ResultSet rs = (ResultSet) out.getObject("p_cursor");
                        while (rs.next()) {
                            list.add(mapperCultivos.map(rs, null));
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return list;
                })
        );
    }

    public Optional<CultivoConUmbralesDTO> findById(Long id) {
        return jdbi.withHandle(handle ->
            handle.createCall("{ call PKG_CULTIVOS_UMBRALES.prc_find_by_id(:p_id, :p_cursor) }")
                .bind("p_id", id)
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, Optional<CultivoConUmbralesDTO>>) out -> {
                    try {
                        ResultSet rs = (ResultSet) out.getObject("p_cursor");
                        if (rs.next()) {
                            return Optional.of(mapperCultivos.map(rs, null));
                        }
                        return Optional.<CultivoConUmbralesDTO>empty();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
        );
    }

    public List<CultivoConUmbralesDTO> findByUsuarioId(Long usuarioId) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_CULTIVOS_UMBRALES.prc_find_by_usuario(:p_usuario_id, :p_cursor) }")
                .bind("p_usuario_id", usuarioId)
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, List<CultivoConUmbralesDTO>>) out -> {
                    List<CultivoConUmbralesDTO> list = new ArrayList<>();
                    try {
                        ResultSet rs = (ResultSet) out.getObject("p_cursor");
                        while (rs.next()) {
                            list.add(mapperCultivos.map(rs, null));
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return list;
                })
        );
    }

    public List<RankingCultivoDTO> findRankingCultivos() {
        return jdbi.withHandle(handle ->
            handle.createCall("{ call PKG_CULTIVOS_UMBRALES.prc_ranking_cultivos(:p_cursor) }")
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, List<RankingCultivoDTO>>) out -> {
                    List<RankingCultivoDTO> list = new ArrayList<>();
                    try {
                        ResultSet rs = (ResultSet) out.getObject("p_cursor");
                        while (rs.next()) {
                            list.add(mapperRanking.map(rs, null));
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return list;
                })
        );
    }
}