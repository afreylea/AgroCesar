package com.agrocesar.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    private Long id;
    private String nombre;
    private String email;
    private String passwordHash;
    private String rol;           // AGRICULTOR o ADMIN
    private Long municipioId;
    private Integer activo;       // 1 = activo, 0 = desactivado
}