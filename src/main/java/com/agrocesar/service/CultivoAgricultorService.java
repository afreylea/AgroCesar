package com.agrocesar.service;

import com.agrocesar.dto.CultivoResumen;
import com.agrocesar.model.CultivoAgricultor;
import com.agrocesar.model.CultivoCatalogo;
import com.agrocesar.repository.CultivoAgricultorRepository;
import com.agrocesar.repository.CatalogoRepository;
import com.agrocesar.repository.MunicipioRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CultivoAgricultorService {

    private final CultivoAgricultorRepository cultivoRepository;
    private final CatalogoRepository catalogoRepository;
    private final MunicipioRepository municipioRepository;

    // Inyección por constructor — práctica recomendada con Spring
    public CultivoAgricultorService(CultivoAgricultorRepository cultivoRepository, 
                                    CatalogoRepository catalogoRepository,
                                    MunicipioRepository municipioRepository) {
        this.cultivoRepository = cultivoRepository;
        this.catalogoRepository = catalogoRepository;
        this.municipioRepository = municipioRepository;
    }

    // Lista todos los cultivos activos del agricultor autenticado
    public List<CultivoAgricultor> listarPorUsuario(Long usuarioId) {
        return cultivoRepository.findByUsuarioId(usuarioId);
    }

    // Busca un cultivo verificando que pertenece al usuario — evita acceso cruzado
    public Optional<CultivoAgricultor> buscarPorIdYUsuario(Long id, Long usuarioId) {
        return cultivoRepository.findByIdAndUsuarioId(id, usuarioId);
    }

    public List<CultivoResumen> listarResumenPorUsuario(Long usuarioId) {
        return listarPorUsuario(usuarioId).stream()
            .map(c -> {
                var cat = catalogoRepository.findById(c.getCatalogoId());
                String nombreCultivo = cat.map(CultivoCatalogo::getNombre).orElse("Sin nombre");
                String categoria = cat.map(CultivoCatalogo::getCategoria).orElse("");
                String imagenUrl = cat.map(CultivoCatalogo::getImagenUrl).orElse(null);
                String municipio = municipioRepository.findById(c.getMunicipioId())
                        .map(mun -> mun.getNombre()).orElse("Sin municipio");
    
                return new CultivoResumen(c.getId(), nombreCultivo, categoria,
                        municipio, c.getHectareas(), c.getFechaSiembra(),
                        c.getMunicipioId(), c.getLatitudCultivo(), c.getLongitudCultivo(), imagenUrl);
            }).toList();
    }

    // Registra un nuevo cultivo. La fecha de siembra no puede ser futura.
    public void registrar(CultivoAgricultor cultivo) {
        if (cultivo.getFechaSiembra() == null) {
            throw new IllegalArgumentException("La fecha de siembra es obligatoria.");
        }
        if (cultivo.getFechaSiembra().isAfter(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de siembra no puede ser futura.");
        }
        cultivoRepository.insert(cultivo);
    }

    // Actualiza un cultivo. Verifica que pertenece al usuario antes de modificar.
    public void actualizar(CultivoAgricultor cultivo, Long usuarioId) {
        Optional<CultivoAgricultor> existente = cultivoRepository.findByIdAndUsuarioId(cultivo.getId(), usuarioId);
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("Cultivo no encontrado o no pertenece al usuario.");
        }
        cultivoRepository.update(cultivo);
    }

    // Baja lógica del cultivo. Verifica propiedad antes de desactivar.
    public void eliminar(Long id, Long usuarioId) {
        int filas = cultivoRepository.deactivate(id, usuarioId);
        if (filas == 0) {
            throw new IllegalArgumentException("Cultivo no encontrado o no pertenece al usuario.");
        }
    }
}