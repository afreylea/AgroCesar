package com.agrocesar.model;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor  
public class Usuario {
    
    //SIEMPRE tienen valor (DDL garantiza)
    @NonNull private Long id;
    @NonNull private String nombre;
    @NonNull private String email;
    @NonNull private String passwordHash;
    @NonNull private String rol;
    @NonNull private Integer activo;
    @NonNull private LocalDateTime fechaCreacion;
    
    //Pueden ser NULL (DDL permite)
    private Long municipioId;
    private String telefono;
    private LocalDateTime ultimoLogin;
}