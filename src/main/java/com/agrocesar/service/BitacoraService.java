package com.agrocesar.service;

import com.agrocesar.model.BitacoraCultivo;
import com.agrocesar.repository.BitacoraCultivoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de logica de negocio para la bitacora de actividades
 * agricolas del cultivo.
 *
 * Actua como intermediario entre el controller y el repository,
 * aplicando validaciones antes de persistir o modificar entradas.
 */
@Service
public class BitacoraService {
    private static final Logger log = LoggerFactory.getLogger(BitacoraService.class);

    private final BitacoraCultivoRepository bitacoraCultivoRepository;

    /**
     * Constructor con inyeccion por constructor.
     *
     * @param bitacoraCultivoRepository repositorio JDBI para la tabla
     *                                  BITACORA_CULTIVO
     */
    public BitacoraService(BitacoraCultivoRepository bitacoraCultivoRepository) {
        this.bitacoraCultivoRepository = bitacoraCultivoRepository;
    }

    /**
     * Lista todas las entradas de bitacora de todos los cultivos
     * de un agricultor, ordenadas por fecha de actividad descendente.
     *
     * @param usuarioId id del agricultor autenticado
     * @return lista de entradas, vacia si no tiene registros
     */
    public List<BitacoraCultivo> listarPorUsuario(Long usuarioId) {
        return bitacoraCultivoRepository.listarPorUsuario(usuarioId);
    }

    /**
     * Lista las entradas de bitacora de un agricultor filtradas
     * por un rango de fechas de actividad.
     *
     * @param usuarioId  id del agricultor autenticado
     * @param fechaDesde fecha inicial del rango (inclusive)
     * @param fechaHasta fecha final del rango (inclusive)
     * @return lista de entradas en el rango, vacia si no hay
     */
    public List<BitacoraCultivo> listarPorUsuarioRango(Long usuarioId,
            LocalDate fechaDesde,
            LocalDate fechaHasta) {
        return bitacoraCultivoRepository.listarPorUsuarioRango(usuarioId, fechaDesde, fechaHasta);
    }

    /**
     * Lista las entradas de bitacora de un cultivo especifico,
     * ordenadas por fecha de actividad descendente.
     *
     * @param cultivoAgricultorId id del cultivo del agricultor
     * @return lista de entradas del cultivo
     */
    public List<BitacoraCultivo> listarPorCultivo(Long cultivoAgricultorId) {
        return bitacoraCultivoRepository.listarPorCultivo(cultivoAgricultorId);
    }

    /**
     * Busca una entrada de bitacora por su id.
     *
     * @param id identificador unico de la entrada
     * @return Optional con la entrada si existe, vacio si no
     */
    public Optional<BitacoraCultivo> buscarPorId(Long id) {
        return bitacoraCultivoRepository.buscarPorId(id);
    }

    /**
     * Registra una nueva entrada en la bitacora del cultivo.
     * La fecha de actividad no puede ser futura.
     *
     * @param cultivoAgricultorId id del cultivo del agricultor
     * @param tipoActividadId     id del tipo de actividad realizada
     * @param alertaId            id de la alerta asociada, puede ser null
     * @param descripcion         nota libre del agricultor, puede ser null
     * @param fechaActividad      fecha en que se realizo la actividad
     * @param responsable         nombre de quien realizo la actividad, puede ser
     *                            null
     * @param ubicacion           ubicacion donde se realizo, puede ser null
     * @throws IllegalArgumentException si la fecha es futura
     */
    public void registrar(Long cultivoAgricultorId, Long tipoActividadId,
            Long alertaId, String descripcion,
            LocalDate fechaActividad, String responsable,
            String ubicacion, String estado) {

        bitacoraCultivoRepository.insertar(cultivoAgricultorId, tipoActividadId,
                alertaId, descripcion, fechaActividad, responsable, ubicacion, estado);

        log.info("[Bitacora] Entrada registrada: cultivo={} tipo={} estado={}",
                cultivoAgricultorId, tipoActividadId, estado);
    }

    /**
     * Actualiza una entrada existente de la bitacora.
     *
     * @param id              identificador de la entrada a actualizar
     * @param tipoActividadId nuevo tipo de actividad
     * @param alertaId        nueva alerta asociada, puede ser null
     * @param descripcion     nueva descripcion
     * @param fechaActividad  nueva fecha de actividad
     * @param responsable     nombre de quien realizo la actividad
     * @param ubicacion       ubicacion donde se realizo
     * @param estado          nuevo estado de la entrada
     * @return true si se actualizo, false si no se encontro la entrada
     * @throws IllegalArgumentException si la fecha es futura
     */
    public boolean actualizar(Long id, Long tipoActividadId, Long alertaId,
            String descripcion, LocalDate fechaActividad,
            String responsable, String ubicacion, String estado) {

        if (fechaActividad != null && fechaActividad.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de actividad no puede ser futura.");
        }

        int filas = bitacoraCultivoRepository.actualizar(id, tipoActividadId,
                alertaId, descripcion, fechaActividad, responsable, ubicacion, estado);

        if (filas > 0) {
            log.info("[Bitacora] Entrada actualizada: id={}", id);
        }
        return filas > 0;
    }

    public boolean cambiarEstado(Long id, String estado) {
        int filas = bitacoraCultivoRepository.cambiarEstado(id, estado);
        if (filas > 0) {
            log.info("[Bitacora] Estado cambiado: id={} estado={}", id, estado);
        }
        return filas > 0;
    }

    /**
     * Elimina una entrada de la bitacora.
     *
     * @param id identificador de la entrada a eliminar
     * @return true si se elimino, false si no se encontro
     */
    public boolean eliminar(Long id) {
        int filas = bitacoraCultivoRepository.eliminar(id);
        if (filas > 0) {
            log.info("[Bitacora] Entrada eliminada: id={}", id);
        }
        return filas > 0;
    }

}
