package com.agrocesar.repository;

import com.agrocesar.model.BitacoraCultivo;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.OutParameters;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Repository
public class BitacoraCultivoRepository {

    private final Jdbi jdbi;
    private final BitacoraCultivoMapper mapper = new BitacoraCultivoMapper();

    public BitacoraCultivoRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public List<BitacoraCultivo> listarPorCultivo(Long cultivoAgricultorId) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_BITACORA.listar_por_cultivo(:p_cultivo_agricultor_id, :p_cursor) }")
                .bind("p_cultivo_agricultor_id", cultivoAgricultorId)
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, List<BitacoraCultivo>>) out -> {
                    List<BitacoraCultivo> list = new ArrayList<>();
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

    public List<BitacoraCultivo> listarPorUsuario(Long usuarioId) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_BITACORA.listar_por_usuario(:p_usuario_id, :p_cursor) }")
                .bind("p_usuario_id", usuarioId)
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, List<BitacoraCultivo>>) out -> {
                    List<BitacoraCultivo> list = new ArrayList<>();
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

    public Optional<BitacoraCultivo> buscarPorId(Long id) {
        return jdbi.withHandle(handle ->
            handle.createCall("{ call PKG_BITACORA.buscar_por_id(:p_id, :p_cursor) }")
                .bind("p_id", id)
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, Optional<BitacoraCultivo>>) out -> {
                    try {
                        ResultSet rs = (ResultSet) out.getObject("p_cursor");
                        if (rs.next()) {
                            return Optional.of(mapper.map(rs, null));
                        }
                        return Optional.<BitacoraCultivo>empty();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
        );
    }

    public void insertar(Long cultivoAgricultorId, Long tipoActividadId,
                         Long alertaId, String descripcion, LocalDate fechaActividad) {
        jdbi.useHandle(handle ->
            handle.createCall(
                "{ call PKG_BITACORA.insertar(:p_cultivo_agricultor_id, :p_tipo_actividad_id, " +
                ":p_alerta_id, :p_descripcion, :p_fecha_actividad) }")
                .bind("p_cultivo_agricultor_id", cultivoAgricultorId)
                .bind("p_tipo_actividad_id",     tipoActividadId)
                .bind("p_alerta_id",             alertaId)
                .bind("p_descripcion",           descripcion)
                .bind("p_fecha_actividad",       java.sql.Date.valueOf(fechaActividad))
                .invoke()
        );
    }

    public int actualizar(Long id, Long tipoActividadId, Long alertaId,
                          String descripcion, LocalDate fechaActividad) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_BITACORA.actualizar(:p_id, :p_tipo_actividad_id, " +
                ":p_alerta_id, :p_descripcion, :p_fecha_actividad, :p_rows_updated) }")
                .bind("p_id",                id)
                .bind("p_tipo_actividad_id", tipoActividadId)
                .bind("p_alerta_id",         alertaId)
                .bind("p_descripcion",       descripcion)
                .bind("p_fecha_actividad",   java.sql.Date.valueOf(fechaActividad))
                .registerOutParameter("p_rows_updated", java.sql.Types.NUMERIC)
                .invoke((Function<OutParameters, Integer>) out -> out.getInt("p_rows_updated"))
        );
    }

    public int eliminar(Long id) {
        return jdbi.withHandle(handle ->
            handle.createCall("{ call PKG_BITACORA.eliminar(:p_id, :p_rows_deleted) }")
                .bind("p_id", id)
                .registerOutParameter("p_rows_deleted", java.sql.Types.NUMERIC)
                .invoke((Function<OutParameters, Integer>) out -> out.getInt("p_rows_deleted"))
        );
    }
}