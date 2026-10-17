package com.techservices.api.dto;

import com.techservices.api.model.Cliente;

public class ClienteDto {

    private Long id;
    private String nombres;
    private String apellidos;
    private String dni;
    private String correoElectronico;
    private String telefono;

    public ClienteDto() {
    }

    public ClienteDto(Long id, String nombres, String apellidos, String dni, String correoElectronico, String telefono) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.dni = dni;
        this.correoElectronico = correoElectronico;
        this.telefono = telefono;
    }

    public static ClienteDto fromEntity(Cliente cliente) {
        if (cliente == null) return null;
        return new ClienteDto(
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
