package com.agrocesar.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Municipio {

    private Long id;
    private String nombre;
    private String departamento;
    private Double latitud;
    private Double longitud;
    private Integer activo;
}