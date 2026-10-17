package com.techservices.ms.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "usuarios_api")
public class UsuarioApi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_usuario", nullable = false, unique = true, length = 100)
    private String nombreUsuario;

    @Column(name = "api_key", nullable = false, unique = true, length = 100)
    private String apiKey;

    @Column(name = "token_autenticacion", nullable = false, length = 255)
    private String tokenAutenticacion;

    @Column(name = "tokens_disponibles", nullable = false)
    private Integer tokensDisponibles;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoUsuario estado;

    public UsuarioApi() {
    }

    public UsuarioApi(Long id, String nombreUsuario, String apiKey, String tokenAutenticacion, Integer tokensDisponibles, EstadoUsuario estado) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.apiKey = apiKey;
        this.tokenAutenticacion = tokenAutenticacion;
        this.tokensDisponibles = tokensDisponibles;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getTokenAutenticacion() {
        return tokenAutenticacion;
    }

    public void setTokenAutenticacion(String tokenAutenticacion) {
        this.tokenAutenticacion = tokenAutenticacion;
    }

    public Integer getTokensDisponibles() {
        return tokensDisponibles;
    }

    public void setTokensDisponibles(Integer tokensDisponibles) {
        this.tokensDisponibles = tokensDisponibles;
    }

    public EstadoUsuario getEstado() {
        return estado;
    }

    public void setEstado(EstadoUsuario estado) {
        this.estado = estado;
    }

    public boolean isActivo() {
        return EstadoUsuario.ACTIVO.equals(this.estado);
    }

    public boolean tieneTokensDisponibles() {
        return this.tokensDisponibles != null && this.tokensDisponibles > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioApi that = (UsuarioApi) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
