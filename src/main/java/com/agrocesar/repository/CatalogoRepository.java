package com.agrocesar.repository;

import com.agrocesar.model.CultivoCatalogo;
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
public class CatalogoRepository {

    private final Jdbi jdbi;
    private final CultivoCatalogoMapper mapper = new CultivoCatalogoMapper();

    public CatalogoRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    // ----------------------------------------------------------------
    //  CONSULTAS
    // ----------------------------------------------------------------

    public Optional<CultivoCatalogo> findById(Long id) {
        return jdbi.withHandle(handle ->
            handle.createCall("{ call PKG_CATALOGO.prc_find_by_id(:p_id, :p_cursor) }")
                .bind("p_id", id)
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, Optional<CultivoCatalogo>>) out -> {
                    try {
                        ResultSet rs = (ResultSet) out.getObject("p_cursor");
                        if (rs.next()) {
                            return Optional.of(mapper.map(rs, null));
                        }
                        return Optional.<CultivoCatalogo>empty();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
        );
    }

    public List<CultivoCatalogo> findAllActivos() {
        return jdbi.withHandle(handle ->
            handle.createCall("{ call PKG_CATALOGO.prc_find_all_activos(:p_cursor) }")
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, List<CultivoCatalogo>>) out -> {
                    List<CultivoCatalogo> list = new ArrayList<>();
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

    public List<CultivoCatalogo> findAll() {
        return jdbi.withHandle(handle ->
            handle.createCall("{ call PKG_CATALOGO.prc_find_all(:p_cursor) }")
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, List<CultivoCatalogo>>) out -> {
                    List<CultivoCatalogo> list = new ArrayList<>();
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

    public void insert(CultivoCatalogo catalogo) {
        jdbi.useHandle(handle ->
            handle.createCall(
                "{ call PKG_CATALOGO.prc_insert(:p_nombre, :p_descripcion, :p_categoria, " +
                ":p_temp_min, :p_temp_max, :p_lluvia_min, :p_lluvia_max, " +
                ":p_humedad_min, :p_humedad_max, :p_tipo_suelo, " +
                ":p_dias_min, :p_dias_max, :p_fuente_datos, :p_imagen_url) }")
                .bind("p_nombre",       catalogo.getNombre())
                .bind("p_descripcion",  catalogo.getDescripcion())
                .bind("p_categoria",    catalogo.getCategoria())
                .bind("p_temp_min",     catalogo.getTempMin())
                .bind("p_temp_max",     catalogo.getTempMax())
                .bind("p_lluvia_min",   catalogo.getLluviaMin())
                .bind("p_lluvia_max",   catalogo.getLluviaMax())
                .bind("p_humedad_min",  catalogo.getHumedadMin())
                .bind("p_humedad_max",  catalogo.getHumedadMax())
                .bind("p_tipo_suelo",   catalogo.getTipoSuelo())
                .bind("p_dias_min",     catalogo.getDiasCosechaMin())
                .bind("p_dias_max",     catalogo.getDiasCosechaMax())
                .bind("p_fuente_datos", catalogo.getFuenteDatos())
                .bind("p_imagen_url",   catalogo.getImagenUrl())
                .invoke()
        );
    }

    public int update(CultivoCatalogo catalogo) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_CATALOGO.prc_update(:p_id, :p_nombre, :p_descripcion, :p_categoria, " +
                ":p_temp_min, :p_temp_max, :p_lluvia_min, :p_lluvia_max, " +
                ":p_humedad_min, :p_humedad_max, :p_tipo_suelo, " +
                ":p_dias_min, :p_dias_max, :p_fuente_datos, :p_imagen_url, :p_rows_updated) }")
                .bind("p_id",           catalogo.getId())
                .bind("p_nombre",       catalogo.getNombre())
                .bind("p_descripcion",  catalogo.getDescripcion())
                .bind("p_categoria",    catalogo.getCategoria())
                .bind("p_temp_min",     catalogo.getTempMin())
                .bind("p_temp_max",     catalogo.getTempMax())
                .bind("p_lluvia_min",   catalogo.getLluviaMin())
                .bind("p_lluvia_max",   catalogo.getLluviaMax())
                .bind("p_humedad_min",  catalogo.getHumedadMin())
                .bind("p_humedad_max",  catalogo.getHumedadMax())
                .bind("p_tipo_suelo",   catalogo.getTipoSuelo())
                .bind("p_dias_min",     catalogo.getDiasCosechaMin())
                .bind("p_dias_max",     catalogo.getDiasCosechaMax())
                .bind("p_fuente_datos", catalogo.getFuenteDatos())
                .bind("p_imagen_url",   catalogo.getImagenUrl())
                .registerOutParameter("p_rows_updated", java.sql.Types.NUMERIC)
                .invoke((Function<OutParameters, Integer>) out -> out.getInt("p_rows_updated"))
        );
    }

    public int desactivar(Long id) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_CATALOGO.prc_desactivar(:p_id, :p_rows_updated) }")
                .bind("p_id", id)
                .registerOutParameter("p_rows_updated", java.sql.Types.NUMERIC)
                .invoke((Function<OutParameters, Integer>) out -> out.getInt("p_rows_updated"))
        );
    }

    public int activar(Long id) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_CATALOGO.prc_activar(:p_id, :p_rows_updated) }")
                .bind("p_id", id)
                .registerOutParameter("p_rows_updated", java.sql.Types.NUMERIC)
                .invoke((Function<OutParameters, Integer>) out -> out.getInt("p_rows_updated"))
        );
    }
}