package com.agrocesar.model;

import java.time.LocalDateTime;

public class Usuario {

    // NOT NULL (DDL garantiza)
    private Long id;
    private String nombre;
    private String email;
    private String passwordHash;
    private String rol;
    private Integer activo;
    private LocalDateTime fechaCreacion;

    // Nullable
    private Long municipioId;
    private String telefono;
    private LocalDateTime ultimoLogin;

    // Constructor vacío (necesario para JDBI builder y Spring)
    public Usuario() {}

    // Constructor completo
    public Usuario(Long id, String nombre, String email, String passwordHash,
                   String rol, Integer activo, LocalDateTime fechaCreacion,
                   Long municipioId, String telefono, LocalDateTime ultimoLogin) {

        this.id            = id;
        this.nombre        = nombre;
        this.email         = email;
        this.passwordHash  = passwordHash;
        this.rol           = rol;
        this.activo        = activo;
        this.fechaCreacion = fechaCreacion;
        this.municipioId   = municipioId;
        this.telefono      = telefono;
        this.ultimoLogin   = ultimoLogin;
    }

    // Getters
    public Long getId()                    { return id; }
    public String getNombre()              { return nombre; }
    public String getEmail()               { return email; }
    public String getPasswordHash()        { return passwordHash; }
    public String getRol()                 { return rol; }
    public Integer getActivo()             { return activo; }
    public LocalDateTime getFechaCreacion(){ return fechaCreacion; }
    public Long getMunicipioId()           { return municipioId; }
    public String getTelefono()            { return telefono; }
    public LocalDateTime getUltimoLogin()  { return ultimoLogin; }

    // Setters
    public void setId(Long id)                           { this.id = id; }
    public void setNombre(String nombre)                 { this.nombre = nombre; }
    public void setEmail(String email)                   { this.email = email; }
    public void setPasswordHash(String passwordHash)     { this.passwordHash = passwordHash; }
    public void setRol(String rol)                       { this.rol = rol; }
    public void setActivo(Integer activo)                { this.activo = activo; }
    public void setFechaCreacion(LocalDateTime f)        { this.fechaCreacion = f; }
    public void setMunicipioId(Long municipioId)         { this.municipioId = municipioId; }
    public void setTelefono(String telefono)             { this.telefono = telefono; }
    public void setUltimoLogin(LocalDateTime ultimoLogin){ this.ultimoLogin = ultimoLogin; }

    // Builder estático (reemplaza @Builder de Lombok)
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String nombre;
        private String email;
        private String passwordHash;
        private String rol;
        private Integer activo;
        private LocalDateTime fechaCreacion;
        private Long municipioId;
        private String telefono;
        private LocalDateTime ultimoLogin;

        public Builder id(Long id)                          { this.id = id; return this; }
        public Builder nombre(String nombre)                { this.nombre = nombre; return this; }
        public Builder email(String email)                  { this.email = email; return this; }
        public Builder passwordHash(String passwordHash)    { this.passwordHash = passwordHash; return this; }
        public Builder rol(String rol)                      { this.rol = rol; return this; }
        public Builder activo(Integer activo)               { this.activo = activo; return this; }
        public Builder fechaCreacion(LocalDateTime f)       { this.fechaCreacion = f; return this; }
        public Builder municipioId(Long municipioId)        { this.municipioId = municipioId; return this; }
        public Builder telefono(String telefono)            { this.telefono = telefono; return this; }
        public Builder ultimoLogin(LocalDateTime ultimoLogin){ this.ultimoLogin = ultimoLogin; return this; }

        public Usuario build() {
            return new Usuario(id, nombre, email, passwordHash, rol, activo,
                               fechaCreacion, municipioId, telefono, ultimoLogin);
        }
    }
}