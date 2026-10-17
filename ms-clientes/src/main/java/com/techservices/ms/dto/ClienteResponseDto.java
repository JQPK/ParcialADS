package com.techservices.ms.dto;

import com.techservices.ms.model.Cliente;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de respuesta con los datos públicos del cliente")
public class ClienteResponseDto {

    @Schema(description = "Identificador único del cliente", example = "1")
    private Long id;

    @Schema(description = "Nombres", example = "Ana")
    private String nombres;

    @Schema(description = "Apellidos", example = "Torres Quispe")
    private String apellidos;

    @Schema(description = "DNI", example = "12345678")
    private String dni;

    @Schema(description = "Correo electrónico", example = "ana.torres@techservices.pe")
    private String correoElectronico;

    @Schema(description = "Teléfono de contacto", example = "999888777")
    private String telefono;

    public ClienteResponseDto() {
    }

    public ClienteResponseDto(Long id, String nombres, String apellidos, String dni, String correoElectronico, String telefono) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.dni = dni;
        this.correoElectronico = correoElectronico;
        this.telefono = telefono;
    }

    public static ClienteResponseDto fromEntity(Cliente cliente) {
        if (cliente == null) return null;
        return new ClienteResponseDto(
                cliente.getId(),
                cliente.getNombres(),
                cliente.getApellidos(),
                cliente.getDni(),
                cliente.getCorreoElectronico(),
                cliente.getTelefono()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
