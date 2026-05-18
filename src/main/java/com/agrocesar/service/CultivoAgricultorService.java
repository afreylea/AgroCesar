package com.agrocesar.service;

import com.agrocesar.dto.RankingCultivoDTO;
import com.agrocesar.model.CultivoAgricultor;
import com.agrocesar.repository.CultivoAgricultorRepository;
import com.agrocesar.repository.CatalogoRepository;
import com.agrocesar.repository.MunicipioRepository;
import com.agrocesar.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CultivoAgricultorService {

    private final CultivoAgricultorRepository cultivoAgricultorRepository;
    private final CatalogoRepository catalogoRepository;
    private final MunicipioRepository municipioRepository;
    private final UsuarioRepository usuarioRepository;

    public CultivoAgricultorService(CultivoAgricultorRepository cultivoAgricultorRepository, CatalogoRepository catalogoRepository,
                                   MunicipioRepository municipioRepository, UsuarioRepository usuarioRepository) {

        this.cultivoAgricultorRepository = cultivoAgricultorRepository;
        this.catalogoRepository          = catalogoRepository;
        this.municipioRepository         = municipioRepository;
        this.usuarioRepository           = usuarioRepository;
    }

    public CultivoAgricultor registrar(Long usuarioId, Long catalogoId, Long municipioId,
                                       double hectareas, LocalDate fechaSiembra, Double latitudCultivo,
                                       Double longitudCultivo, String tipoSuelo) {
        
        if (usuarioId == null || catalogoId == null || municipioId == null)
            throw new IllegalArgumentException("Usuario, catálogo y municipio son obligatorios.");

        if (usuarioRepository.findById(usuarioId).isEmpty())
            throw new IllegalArgumentException("El usuario no existe.");

        if (catalogoRepository.findById(catalogoId).isEmpty())
            throw new IllegalArgumentException("El catálogo no existe.");

        if (municipioRepository.findById(municipioId).isEmpty())
            throw new IllegalArgumentException("El municipio no existe.");

        if (hectareas <= 0)
            throw new IllegalArgumentException("Las hectáreas deben ser mayores a cero.");

        if (fechaSiembra.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("La fecha de siembra no puede ser futura.");

        if (latitudCultivo != null && (latitudCultivo < -90 || latitudCultivo > 90))
            throw new IllegalArgumentException("La latitud debe estar entre -90 y 90.");

        if (longitudCultivo != null && (longitudCultivo < -180 || longitudCultivo > 180))
            throw new IllegalArgumentException("La longitud debe estar entre -180 y 180.");

        try {
            long id = cultivoAgricultorRepository.nextId();

            CultivoAgricultor cultivo = CultivoAgricultor.builder()
                    .id(id)
                    .usuarioId(usuarioId)
                    .catalogoId(catalogoId)
                    .municipioId(municipioId)
                    .hectareas(hectareas)
                    .fechaSiembra(fechaSiembra)
                    .latitudCultivo(latitudCultivo)
                    .longitudCultivo(longitudCultivo)
                    .tipoSuelo(tipoSuelo)
                    .build();

            cultivoAgricultorRepository.insert(cultivo);

            return cultivo;
        } catch (Exception e) {
            throw new RuntimeException("Error al registrar el cultivo: " + e.getMessage(), e);
        }
    }

    public List<CultivoAgricultor> obtenerActivosPorUsuario(Long usuarioId) {
        if (usuarioId == null)
            throw new IllegalArgumentException("El ID de usuario es obligatorio.");

        if (usuarioRepository.findById(usuarioId).isEmpty())
            throw new IllegalArgumentException("El usuario no existe.");

        try {
            return cultivoAgricultorRepository.findActiveByUsuarioId(usuarioId);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los cultivos activos: " + e.getMessage(), e);
        }
    }

    public List<CultivoAgricultor> obtenerInactivosPorUsuario(Long usuarioId) {
        if (usuarioId == null)
            throw new IllegalArgumentException("El ID de usuario es obligatorio.");

        if (usuarioRepository.findById(usuarioId).isEmpty())
            throw new IllegalArgumentException("El usuario no existe.");

        try {
            return cultivoAgricultorRepository.findInactiveByUsuarioId(usuarioId);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los cultivos inactivos: " + e.getMessage(), e);
        }
    }

    public List<CultivoAgricultor> obtenerPorMunicipioId(Long municipioId) {
        if (municipioId == null)
            throw new IllegalArgumentException("El ID de municipio es obligatorio.");

        if (municipioRepository.findById(municipioId).isEmpty())
            throw new IllegalArgumentException("El municipio no existe.");

        try {
            return cultivoAgricultorRepository.findByMunicipioId(municipioId);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los cultivos por municipio: " + e.getMessage(), e);
        }
    }

    public Optional<CultivoAgricultor> obtenerPorId(Long id) {
        if (id == null)
            throw new IllegalArgumentException("El ID del cultivo es obligatorio.");
        
        try {
            return cultivoAgricultorRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener el cultivo por ID: " + e.getMessage(), e);
        }
    }

    public boolean actualizar(long id, long usuarioId,double hectareas, Double tempMinOverride, Double tempMaxOverride, 
                              Double lluviaMinOverride, Double lluviaMaxOverride, Double humedadMinOverride, 
                              Double humedadMaxOverride, Double latitudCultivo, Double longitudCultivo, String tipoSuelo) {
        
        if (hectareas <= 0)
            throw new IllegalArgumentException("Las hectáreas deben ser mayores a cero.");

        if (tempMinOverride != null && tempMaxOverride != null && tempMinOverride > tempMaxOverride)
            throw new IllegalArgumentException("La temperatura mínima no puede ser mayor que la máxima.");

        if (tempMinOverride != null && tempMaxOverride != null && (tempMinOverride <-10 || tempMaxOverride > 50))
            throw new IllegalArgumentException("Las temperaturas deben estar entre -10 y 50 grados Celsius.");  

        if (lluviaMinOverride != null && lluviaMaxOverride != null && lluviaMinOverride > lluviaMaxOverride)
            throw new IllegalArgumentException("La lluvia mínima no puede ser mayor que la máxima.");

        if (lluviaMinOverride != null && lluviaMaxOverride != null && (lluviaMinOverride < 0 || lluviaMaxOverride < 0 || lluviaMinOverride > 1000 || lluviaMaxOverride > 1000))
            throw new IllegalArgumentException("La lluvia debe estar entre 0 y 1000 mm.");

        if (humedadMinOverride != null && humedadMaxOverride != null && humedadMinOverride > humedadMaxOverride)
            throw new IllegalArgumentException("La humedad mínima no puede ser mayor que la máxima.");

        if (humedadMinOverride != null && humedadMaxOverride != null && (humedadMinOverride < 0 || humedadMaxOverride < 0 || humedadMinOverride > 100 || humedadMaxOverride > 100))
            throw new IllegalArgumentException("La humedad debe estar entre 0 y 100%.");

        if (latitudCultivo != null && (latitudCultivo < -90 || latitudCultivo > 90))
            throw new IllegalArgumentException("La latitud debe estar entre -90 y 90.");

        if (longitudCultivo != null && (longitudCultivo < -180 || longitudCultivo > 180))
            throw new IllegalArgumentException("La longitud debe estar entre -180 y 180.");

        try {
            Optional<CultivoAgricultor> cultivoOpt = cultivoAgricultorRepository.findById(id);

            if (cultivoOpt.isEmpty())
                throw new IllegalArgumentException("El cultivo no existe.");

            if (!cultivoOpt.get().getUsuarioId().equals(usuarioId))
                throw new IllegalArgumentException("El cultivo no pertenece al usuario.");

            CultivoAgricultor cultivo = cultivoOpt.get();

            cultivo.setHectareas(hectareas);
            cultivo.setTempMinOverride(tempMinOverride);
            cultivo.setTempMaxOverride(tempMaxOverride);
            cultivo.setLluviaMinOverride(lluviaMinOverride);
            cultivo.setLluviaMaxOverride(lluviaMaxOverride);
            cultivo.setHumedadMinOverride(humedadMinOverride);
            cultivo.setHumedadMaxOverride(humedadMaxOverride);
            cultivo.setLatitudCultivo(latitudCultivo);
            cultivo.setLongitudCultivo(longitudCultivo);
            cultivo.setTipoSuelo(tipoSuelo);

            return cultivoAgricultorRepository.actualizar(cultivo) > 0;
        } catch (IllegalArgumentException e) {
            throw e;                         
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el cultivo: " + e.getMessage(), e);
        }
    }

    public boolean eliminar(Long id, Long usuarioId) {
        if (usuarioId == null)
            throw new IllegalArgumentException("El ID de usuario es obligatorio.");

        if (usuarioRepository.findById(usuarioId).isEmpty())
            throw new IllegalArgumentException("El usuario no existe.");

        try {
            Optional<CultivoAgricultor> cultivo = cultivoAgricultorRepository.findById(id);

            if (cultivo.isEmpty())
                throw new IllegalArgumentException("El cultivo no existe.");

            if (!cultivo.get().getUsuarioId().equals(usuarioId))
                throw new IllegalArgumentException("El cultivo no pertenece al usuario.");

            return cultivoAgricultorRepository.desactivar(id) > 0;
        } catch (IllegalArgumentException e) {
            throw e;                         
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el cultivo: " + e.getMessage(), e);
        }
    }

    public List<RankingCultivoDTO> obtenerRanking() {
        try {
            return cultivoAgricultorRepository.findRankingCultivos();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener el ranking de cultivos: " + e.getMessage(), e);
        }
    }
}