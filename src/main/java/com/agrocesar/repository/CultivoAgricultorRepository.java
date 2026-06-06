package com.agrocesar.repository;

import com.agrocesar.model.CultivoAgricultor;
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
public class CultivoAgricultorRepository {

    private final Jdbi jdbi;
    private final CultivoAgricultorMapper mapper = new CultivoAgricultorMapper();

    public CultivoAgricultorRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    // ----------------------------------------------------------------
    // CONSULTAS
    // ----------------------------------------------------------------

    public List<CultivoAgricultor> findByUsuarioId(Long usuarioId) {
        return jdbi.withHandle(handle -> handle.createCall(
                "{ call PKG_CULTIVOS_AGRICULTOR.prc_find_by_usuario(:p_usuario_id, :p_cursor) }")
                .bind("p_usuario_id", usuarioId)
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, List<CultivoAgricultor>>) out -> {
                    List<CultivoAgricultor> list = new ArrayList<>();
                    try {
                        ResultSet rs = (ResultSet) out.getObject("p_cursor");
                        while (rs.next()) {
                            list.add(mapper.map(rs, null));
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return list;
                }));
    }

    public Optional<CultivoAgricultor> findByIdAndUsuarioId(Long id, Long usuarioId) {
        return jdbi.withHandle(handle -> handle.createCall(
                "{ call PKG_CULTIVOS_AGRICULTOR.prc_find_by_id_and_usuario(:p_id, :p_usuario_id, :p_cursor) }")
                .bind("p_id", id)
                .bind("p_usuario_id", usuarioId)
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, Optional<CultivoAgricultor>>) out -> {
                    try {
                        ResultSet rs = (ResultSet) out.getObject("p_cursor");
                        if (rs.next()) {
                            return Optional.of(mapper.map(rs, null));
                        }
                        return Optional.<CultivoAgricultor>empty();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }));
    }

    // ----------------------------------------------------------------
    // ESCRITURA
    // ----------------------------------------------------------------

    public void insert(CultivoAgricultor cultivo) {
        jdbi.useHandle(handle -> handle.createCall(
                "{ call PKG_CULTIVOS_AGRICULTOR.prc_insert(" +
                        ":p_usuario_id, :p_catalogo_id, :p_municipio_id, " +
                        ":p_hectareas, :p_fecha_siembra, " +
                        ":p_latitud_cultivo, :p_longitud_cultivo, :p_tipo_suelo) }")
                .bind("p_usuario_id", cultivo.getUsuarioId())
                .bind("p_catalogo_id", cultivo.getCatalogoId())
                .bind("p_municipio_id", cultivo.getMunicipioId())
                .bind("p_hectareas", cultivo.getHectareas())
                .bind("p_fecha_siembra", cultivo.getFechaSiembra())
                .bind("p_latitud_cultivo", cultivo.getLatitudCultivo())
                .bind("p_longitud_cultivo", cultivo.getLongitudCultivo())
                .bind("p_tipo_suelo", cultivo.getTipoSuelo())
                .invoke());
    }

    public int update(CultivoAgricultor cultivo) {
        return jdbi.withHandle(handle -> handle.createCall(
                "{ call PKG_CULTIVOS_AGRICULTOR.prc_update(" +
                        ":p_id, :p_usuario_id, :p_hectareas, " +
                        ":p_fecha_siembra, :p_latitud_cultivo, :p_longitud_cultivo, :p_tipo_suelo, " +
                        ":p_temp_min_override, :p_temp_max_override, " +
                        ":p_lluvia_min_override, :p_lluvia_max_override, " +
                        ":p_humedad_min_override, :p_humedad_max_override, " +
                        ":p_rows_updated) }")
                .bind("p_id", cultivo.getId())
                .bind("p_usuario_id", cultivo.getUsuarioId())
                .bind("p_hectareas", cultivo.getHectareas())
                .bind("p_fecha_siembra", cultivo.getFechaSiembra())
                .bind("p_latitud_cultivo", cultivo.getLatitudCultivo())
                .bind("p_longitud_cultivo", cultivo.getLongitudCultivo())
                .bind("p_tipo_suelo", cultivo.getTipoSuelo())
                .bind("p_temp_min_override", cultivo.getTempMinOverride())
                .bind("p_temp_max_override", cultivo.getTempMaxOverride())
                .bind("p_lluvia_min_override", cultivo.getLluviaMinOverride())
                .bind("p_lluvia_max_override", cultivo.getLluviaMaxOverride())
                .bind("p_humedad_min_override", cultivo.getHumedadMinOverride())
                .bind("p_humedad_max_override", cultivo.getHumedadMaxOverride())
                .registerOutParameter("p_rows_updated", java.sql.Types.NUMERIC)
                .invoke((Function<OutParameters, Integer>) out -> out.getInt("p_rows_updated")));
    }

    public int deactivate(Long id, Long usuarioId) {
        return jdbi.withHandle(handle -> handle.createCall(
                "{ call PKG_CULTIVOS_AGRICULTOR.prc_deactivate(:p_id, :p_usuario_id, :p_rows_updated) }")
                .bind("p_id", id)
                .bind("p_usuario_id", usuarioId)
                .registerOutParameter("p_rows_updated", java.sql.Types.NUMERIC)
                .invoke((Function<OutParameters, Integer>) out -> out.getInt("p_rows_updated")));
    }
}