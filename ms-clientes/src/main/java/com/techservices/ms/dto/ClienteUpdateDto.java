package com.techservices.ms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO para la actualización de datos de un cliente existente")
public class ClienteUpdateDto {

    @Schema(description = "Nombres actualizados", example = "Renato Daniel")
    @NotBlank(message = "El campo 'nombres' es obligatorio")
    @Size(min = 2, max = 100, message = "Los nombres deben tener entre 2 y 100 caracteres")
    private String nombres;

    @Schema(description = "Apellidos actualizados", example = "Salas Quispe")
    @NotBlank(message = "El campo 'apellidos' es obligatorio")
    @Size(min = 2, max = 100, message = "Los apellidos deben tener entre 2 y 100 caracteres")
    private String apellidos;

    @Schema(description = "Documento Nacional de Identidad (8 dígitos numéricos)", example = "71234567")
    @NotBlank(message = "El campo 'dni' es obligatorio")
    @Pattern(regexp = "^[0-9]{8}$", message = "El DNI debe contener exactamente 8 dígitos numéricos")
    private String dni;

    @Schema(description = "Correo electrónico actualizado", example = "renato.daniel@techservices.pe")
    @NotBlank(message = "El campo 'correoElectronico' es obligatorio")
    @Email(message = "El formato del correo electrónico no es válido")
    @Size(max = 150, message = "El correo no puede exceder los 150 caracteres")
    private String correoElectronico;

    @Schema(description = "Teléfono de contacto actualizado", example = "987000111")
    @NotBlank(message = "El campo 'telefono' es obligatorio")
    @Pattern(regexp = "^[0-9+\\- ]{7,20}$", message = "El teléfono debe contener entre 7 y 20 dígitos o caracteres válidos")
    private String telefono;

    public ClienteUpdateDto() {
    }

    public ClienteUpdateDto(String nombres, String apellidos, String dni, String correoElectronico, String telefono) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.dni = dni;
        this.correoElectronico = correoElectronico;
        this.telefono = telefono;
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
