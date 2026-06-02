package com.agrocesar.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImagenService {
    @Value("${app.imagenes.ruta}")
    private String rutaImagenes;

    // Guarda el archivo en disco y devuelve el nombre final
    public String guardar(MultipartFile archivo) throws IOException {
        Path directorito = Paths.get(rutaImagenes);
        if (!Files.exists(directorito)) {
            Files.createDirectories(directorito);
        }
        // Genera nombre unico para evitar colisiones con los archivos }
        String extension = obtenerExtensiones(archivo.getOriginalFilename());
        String nombreArchivo = UUID.randomUUID().toString() + extension;

        // Copia el archivo al disco
        Path destino = directorito.resolve(nombreArchivo);
        Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
        return nombreArchivo;

    }

    // Elimina un archivo anterior si existe
    public void eliminar(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.isBlank())
            return;
        try {
            Path archivo = Paths.get(rutaImagenes).resolve(nombreArchivo);
            Files.deleteIfExists(archivo);
        } catch (IOException e) {
            // No interrumpe el flujo si falla el borrado
        }
    }

    public String obtenerExtensiones(String nombreOriginal) {
        if (nombreOriginal == null || !nombreOriginal.contains(".")) {
            return ".jpg";
        }
        return nombreOriginal.substring(nombreOriginal.lastIndexOf(".")).toLowerCase();
    }
}
