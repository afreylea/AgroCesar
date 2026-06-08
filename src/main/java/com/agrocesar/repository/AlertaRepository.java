package com.agrocesar.repository;

import com.agrocesar.dto.AlertaVistaDTO;
import com.agrocesar.dto.CultivoMasAfectadoDTO;
import com.agrocesar.model.Alerta;
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
public class AlertaRepository {

    private final Jdbi jdbi;
    private final AlertaVistaDTOMapper alertaMapper       = new AlertaVistaDTOMapper();
    private final CultivoMasAfectadoMapper afectadoMapper = new CultivoMasAfectadoMapper();

    public AlertaRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    // ----------------------------------------------------------------
    //  ESCRITURA (tabla ALERTAS)
    // ----------------------------------------------------------------

    public void insert(Alerta alerta) {
        jdbi.useHandle(handle ->
            handle.createCall(
                "{ call PKG_ALERTAS.prc_insert(" +
                ":p_cultivo_agricultor_id, :p_tipo_alerta, :p_severidad, " +
                ":p_descripcion, :p_recomendacion, :p_fecha_dia_pronostico, " +
                ":p_valor_detectado, :p_valor_umbral) }")
                .bind("p_cultivo_agricultor_id", alerta.getCultivoAgricultorId())
                .bind("p_tipo_alerta",           alerta.getTipoAlerta())
                .bind("p_severidad",             alerta.getSeveridad())
                .bind("p_descripcion",           alerta.getDescripcion())
                .bind("p_recomendacion",         alerta.getRecomendacion())
                .bind("p_fecha_dia_pronostico",  alerta.getFechaDiaPronostico())
                .bind("p_valor_detectado",       alerta.getValorDetectado())
                .bind("p_valor_umbral",          alerta.getValorUmbral())
                .invoke()
        );
    }

    public int actualizarRecomendacion(Long id, String recomendacion) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_ALERTAS.prc_actualizar_recomendacion(:p_id, :p_recomendacion, :p_rows_updated) }")
                .bind("p_id",            id)
                .bind("p_recomendacion", recomendacion)
                .registerOutParameter("p_rows_updated", java.sql.Types.NUMERIC)
                .invoke((Function<OutParameters, Integer>) out -> out.getInt("p_rows_updated"))
        );
    }

    public int marcarLeida(Long id) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_ALERTAS.prc_marcar_leida(:p_id, :p_rows_updated) }")
                .bind("p_id", id)
                .registerOutParameter("p_rows_updated", java.sql.Types.NUMERIC)
                .invoke((Function<OutParameters, Integer>) out -> out.getInt("p_rows_updated"))
        );
    }

    // ----------------------------------------------------------------
    //  LECTURA (vista V_ALERTAS)
    // ----------------------------------------------------------------

    public Optional<AlertaVistaDTO> findById(Long id) {
        return jdbi.withHandle(handle ->
            handle.createCall("{ call PKG_ALERTAS.prc_find_by_id(:p_id, :p_cursor) }")
                .bind("p_id", id)
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, Optional<AlertaVistaDTO>>) out -> {
                    try {
                        ResultSet rs = (ResultSet) out.getObject("p_cursor");
                        if (rs.next()) {
                            return Optional.of(alertaMapper.map(rs, null));
                        }
                        return Optional.<AlertaVistaDTO>empty();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
        );
    }

    public List<AlertaVistaDTO> findByUsuarioId(Long usuarioId) {
        return jdbi.withHandle(handle ->
            handle.createCall("{ call PKG_ALERTAS.prc_find_by_usuario(:p_usuario_id, :p_cursor) }")
                .bind("p_usuario_id", usuarioId)
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, List<AlertaVistaDTO>>) out -> mapAlertaList(out))
        );
    }

    public List<AlertaVistaDTO> findNoLeidasByUsuarioId(Long usuarioId) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_ALERTAS.prc_find_no_leidas_by_usuario(:p_usuario_id, :p_cursor) }")
                .bind("p_usuario_id", usuarioId)
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, List<AlertaVistaDTO>>) out -> mapAlertaList(out))
        );
    }

    public List<AlertaVistaDTO> findByMunicipioId(Long municipioId) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_ALERTAS.prc_find_by_municipio(:p_municipio_id, :p_cursor) }")
                .bind("p_municipio_id", municipioId)
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, List<AlertaVistaDTO>>) out -> mapAlertaList(out))
        );
    }

    public List<AlertaVistaDTO> findByCatalogoId(Long catalogoId) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_ALERTAS.prc_find_by_catalogo(:p_catalogo_id, :p_cursor) }")
                .bind("p_catalogo_id", catalogoId)
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, List<AlertaVistaDTO>>) out -> mapAlertaList(out))
        );
    }

    public List<AlertaVistaDTO> findByCultivoId(Long cultivoId) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_ALERTAS.prc_find_by_cultivo(:p_cultivo_id, :p_cursor) }")
                .bind("p_cultivo_id", cultivoId)
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, List<AlertaVistaDTO>>) out -> mapAlertaList(out))
        );
    }

    public List<AlertaVistaDTO> findByUsuarioIdAndCultivoId(Long usuarioId, Long cultivoId) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_ALERTAS.prc_find_by_usuario_and_cultivo(:p_usuario_id, :p_cultivo_id, :p_cursor) }")
                .bind("p_usuario_id",  usuarioId)
                .bind("p_cultivo_id",  cultivoId)
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, List<AlertaVistaDTO>>) out -> mapAlertaList(out))
        );
    }

    public List<AlertaVistaDTO> findByTipo(String tipoAlerta) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_ALERTAS.prc_find_by_tipo(:p_tipo_alerta, :p_cursor) }")
                .bind("p_tipo_alerta", tipoAlerta)
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, List<AlertaVistaDTO>>) out -> mapAlertaList(out))
        );
    }

    public List<AlertaVistaDTO> findByUsuarioIdAndTipo(Long usuarioId, String tipoAlerta) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_ALERTAS.prc_find_by_usuario_and_tipo(:p_usuario_id, :p_tipo_alerta, :p_cursor) }")
                .bind("p_usuario_id",  usuarioId)
                .bind("p_tipo_alerta", tipoAlerta)
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, List<AlertaVistaDTO>>) out -> mapAlertaList(out))
        );
    }

    public List<AlertaVistaDTO> findBySeveridad(String severidad) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_ALERTAS.prc_find_by_severidad(:p_severidad, :p_cursor) }")
                .bind("p_severidad", severidad)
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, List<AlertaVistaDTO>>) out -> mapAlertaList(out))
        );
    }

    public List<AlertaVistaDTO> findByUsuarioIdAndSeveridad(Long usuarioId, String severidad) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_ALERTAS.prc_find_by_usuario_and_severidad(:p_usuario_id, :p_severidad, :p_cursor) }")
                .bind("p_usuario_id", usuarioId)
                .bind("p_severidad",  severidad)
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, List<AlertaVistaDTO>>) out -> mapAlertaList(out))
        );
    }


    public List<AlertaVistaDTO> findAll() {
        return jdbi.withHandle(handle ->
            handle.createCall("{ call PKG_ALERTAS.prc_find_all(:p_cursor) }")
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, List<AlertaVistaDTO>>) out -> mapAlertaList(out))
        );
    }

    public List<AlertaVistaDTO> findByRangoFechas(LocalDate fechaDesde, LocalDate fechaHasta) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_ALERTAS.prc_find_by_rango_fechas(:p_fecha_desde, :p_fecha_hasta, :p_cursor) }")
                .bind("p_fecha_desde", java.sql.Date.valueOf(fechaDesde))
                .bind("p_fecha_hasta", java.sql.Date.valueOf(fechaHasta))
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, List<AlertaVistaDTO>>) out -> mapAlertaList(out))
        );
    }

    public List<CultivoMasAfectadoDTO> findCultivosMasAfectados(LocalDate fechaDesde, LocalDate fechaHasta) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_ALERTAS.prc_find_cultivos_mas_afectados(:p_fecha_desde, :p_fecha_hasta, :p_cursor) }")
                .bind("p_fecha_desde", java.sql.Date.valueOf(fechaDesde))
                .bind("p_fecha_hasta", java.sql.Date.valueOf(fechaHasta))
                .registerOutParameter("p_cursor", java.sql.Types.REF_CURSOR)
                .invoke((Function<OutParameters, List<CultivoMasAfectadoDTO>>) out -> {
                    List<CultivoMasAfectadoDTO> list = new ArrayList<>();
                    try {
                        ResultSet rs = (ResultSet) out.getObject("p_cursor");
                        while (rs.next()) {
                            list.add(afectadoMapper.map(rs, null));
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return list;
                })
        );
    }

    public int countActivas() {
        return jdbi.withHandle(handle ->
            handle.createCall("{ call PKG_ALERTAS.prc_count_activas(:p_count) }")
                .registerOutParameter("p_count", java.sql.Types.NUMERIC)
                .invoke((Function<OutParameters, Integer>) out -> out.getInt("p_count"))
        );
    }

    public int countCriticas() {
        return jdbi.withHandle(handle ->
            handle.createCall("{ call PKG_ALERTAS.prc_count_criticas(:p_count) }")
                .registerOutParameter("p_count", java.sql.Types.NUMERIC)
                .invoke((Function<OutParameters, Integer>) out -> out.getInt("p_count"))
        );
    }

    public int countActivasPorPeriodo(LocalDate fechaDesde, LocalDate fechaHasta) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_ALERTAS.prc_count_activas_por_periodo(:p_fecha_desde, :p_fecha_hasta, :p_count) }")
                .bind("p_fecha_desde", java.sql.Date.valueOf(fechaDesde))
                .bind("p_fecha_hasta", java.sql.Date.valueOf(fechaHasta))
                .registerOutParameter("p_count", java.sql.Types.NUMERIC)
                .invoke((Function<OutParameters, Integer>) out -> out.getInt("p_count"))
        );
    }

    public int countCriticasPorPeriodo(LocalDate fechaDesde, LocalDate fechaHasta) {
        return jdbi.withHandle(handle ->
            handle.createCall(
                "{ call PKG_ALERTAS.prc_count_criticas_por_periodo(:p_fecha_desde, :p_fecha_hasta, :p_count) }")
                .bind("p_fecha_desde", java.sql.Date.valueOf(fechaDesde))
                .bind("p_fecha_hasta", java.sql.Date.valueOf(fechaHasta))
                .registerOutParameter("p_count", java.sql.Types.NUMERIC)
                .invoke((Function<OutParameters, Integer>) out -> out.getInt("p_count"))
        );
    }

    // ----------------------------------------------------------------
    //  HELPER PRIVADO
    // ----------------------------------------------------------------

    // Extrae la lógica repetida de iterar un REF_CURSOR y mapear AlertaVistaDTO
    private List<AlertaVistaDTO> mapAlertaList(OutParameters out) {
        List<AlertaVistaDTO> list = new ArrayList<>();
        try {
            ResultSet rs = (ResultSet) out.getObject("p_cursor");
            while (rs.next()) {
                list.add(alertaMapper.map(rs, null));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}