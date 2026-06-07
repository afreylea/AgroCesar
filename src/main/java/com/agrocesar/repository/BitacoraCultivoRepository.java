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
        return jdbi.withHandle(handle -> handle.createCall(
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
                }));
    }

    public List<BitacoraCultivo> listarPorUsuario(Long usuarioId) {
        return jdbi.withHandle(handle -> handle.createCall(
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
                }));
    }

    public List<BitacoraCultivo> listarPorUsuarioRango(Long usuarioId,
            LocalDate fechaDesde,
            LocalDate fechaHasta) {
        return jdbi.withHandle(handle -> handle.createCall(
                "{ call PKG_BITACORA.listar_por_usuario_rango(:p_usuario_id, " +
                        ":p_fecha_desde, :p_fecha_hasta, :p_cursor) }")
                .bind("p_usuario_id", usuarioId)
                .bind("p_fecha_desde", java.sql.Date.valueOf(fechaDesde))
                .bind("p_fecha_hasta", java.sql.Date.valueOf(fechaHasta))
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
                }));
    }

    public Optional<BitacoraCultivo> buscarPorId(Long id) {
        return jdbi.withHandle(handle -> handle.createCall("{ call PKG_BITACORA.buscar_por_id(:p_id, :p_cursor) }")
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
                }));
    }

    public void insertar(Long cultivoAgricultorId, Long tipoActividadId,
            Long alertaId, String descripcion, LocalDate fechaActividad,
            String responsable, String ubicacion, String estado) {
        jdbi.useHandle(handle -> handle.createCall(
                "{ call PKG_BITACORA.insertar(:p_cultivo_agricultor_id, :p_tipo_actividad_id, " +
                        ":p_alerta_id, :p_descripcion, :p_fecha_actividad, :p_responsable, :p_ubicacion, :p_estado) }")
                .bind("p_cultivo_agricultor_id", cultivoAgricultorId)
                .bind("p_tipo_actividad_id", tipoActividadId)
                .bind("p_alerta_id", alertaId)
                .bind("p_descripcion", descripcion)
                .bind("p_fecha_actividad", java.sql.Date.valueOf(fechaActividad))
                .bind("p_responsable", responsable)
                .bind("p_ubicacion", ubicacion)
                .bind("p_estado", estado)
                .invoke());
    }

    public int cambiarEstado(Long id, String estado) {
        return jdbi.withHandle(handle -> handle.createCall(
                "{ call PKG_BITACORA.cambiar_estado(:p_id, :p_estado, :p_rows_updated) }")
                .bind("p_id", id)
                .bind("p_estado", estado)
                .registerOutParameter("p_rows_updated", java.sql.Types.NUMERIC)
                .invoke((Function<OutParameters, Integer>) out -> out.getInt("p_rows_updated")));
    }

    public int actualizar(Long id, Long tipoActividadId, Long alertaId,
            String descripcion, LocalDate fechaActividad,
            String responsable, String ubicacion, String estado) {
        return jdbi.withHandle(handle -> handle.createCall(
                "{ call PKG_BITACORA.actualizar(:p_id, :p_tipo_actividad_id, " +
                        ":p_alerta_id, :p_descripcion, :p_fecha_actividad, " +
                        ":p_responsable, :p_ubicacion, :p_rows_updated) }")
                .bind("p_id", id)
                .bind("p_tipo_actividad_id", tipoActividadId)
                .bind("p_alerta_id", alertaId)
                .bind("p_descripcion", descripcion)
                .bind("p_fecha_actividad", java.sql.Date.valueOf(fechaActividad))
                .bind("p_responsable", responsable)
                .bind("p_ubicacion", ubicacion)
                .registerOutParameter("p_rows_updated", java.sql.Types.NUMERIC)
                .invoke((Function<OutParameters, Integer>) out -> out.getInt("p_rows_updated")));
    }

    public int eliminar(Long id) {
        return jdbi.withHandle(handle -> handle.createCall("{ call PKG_BITACORA.eliminar(:p_id, :p_rows_deleted) }")
                .bind("p_id", id)
                .registerOutParameter("p_rows_deleted", java.sql.Types.NUMERIC)
                .invoke((Function<OutParameters, Integer>) out -> out.getInt("p_rows_deleted")));
    }
}