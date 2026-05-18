package com.agrocesar.service;

import com.agrocesar.model.CultivoAgricultor;
import com.agrocesar.repository.CultivoAgricultorRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CultivoAgricultorService {

    private final CultivoAgricultorRepository repository;

    // Inyección por constructor — práctica recomendada con Spring
    public CultivoAgricultorService(CultivoAgricultorRepository repository) {
        this.repository = repository;
    }

    // Lista todos los cultivos activos del agricultor autenticado
    public List<CultivoAgricultor> listarPorUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId);
    }

    // Busca un cultivo verificando que pertenece al usuario — evita acceso cruzado
    public Optional<CultivoAgricultor> buscarPorIdYUsuario(Long id, Long usuarioId) {
        return repository.findByIdAndUsuarioId(id, usuarioId);
    }

    // Registra un nuevo cultivo. La fecha de siembra no puede ser futura.
    public Long registrar(CultivoAgricultor cultivo) {
        if (cultivo.getFechaSiembra() == null) {
            throw new IllegalArgumentException("La fecha de siembra es obligatoria.");
        }
        if (cultivo.getFechaSiembra().isAfter(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de siembra no puede ser futura.");
        }
        return repository.insert(cultivo);
    }

    // Actualiza un cultivo. Verifica que pertenece al usuario antes de modificar.
    public void actualizar(CultivoAgricultor cultivo, Long usuarioId) {
        Optional<CultivoAgricultor> existente = repository.findByIdAndUsuarioId(cultivo.getId(), usuarioId);
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("Cultivo no encontrado o no pertenece al usuario.");
        }
        repository.update(cultivo);
    }

    // Baja lógica del cultivo. Verifica propiedad antes de desactivar.
    public void eliminar(Long id, Long usuarioId) {
        int filas = repository.deactivate(id, usuarioId);
        if (filas == 0) {
            throw new IllegalArgumentException("Cultivo no encontrado o no pertenece al usuario.");
        }
    }
}
