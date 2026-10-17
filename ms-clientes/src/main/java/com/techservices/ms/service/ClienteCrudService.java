package com.techservices.ms.service;

import com.techservices.ms.dto.*;
import com.techservices.ms.exception.ConflictException;
import com.techservices.ms.exception.QuotaExceededException;
import com.techservices.ms.exception.ResourceNotFoundException;
import com.techservices.ms.model.Cliente;
import com.techservices.ms.model.UsuarioApi;
import com.techservices.ms.repository.ClienteRepository;
import com.techservices.ms.repository.UsuarioApiRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteCrudService {

    private final ClienteRepository clienteRepository;
    private final UsuarioApiRepository usuarioApiRepository;

    public ClienteCrudService(ClienteRepository clienteRepository, UsuarioApiRepository usuarioApiRepository) {
        this.clienteRepository = clienteRepository;
        this.usuarioApiRepository = usuarioApiRepository;
    }

    /**
     * READ (Todos): Consulta la lista de clientes y descuenta 1 token si la consulta es exitosa.
     */
    @Transactional
    public ConsultaListaClientesResponse listarTodos(UsuarioApi usuario) {
        // Descuento atómico de 1 token por consulta de listado exitosa
        int filasAfectadas = usuarioApiRepository.decrementarToken(usuario.getId());
        if (filasAfectadas == 0) {
            throw new QuotaExceededException("Ha agotado su saldo de tokens disponibles para realizar consultas.");
        }

        List<ClienteResponseDto> clientes = clienteRepository.findAll().stream()
                .map(ClienteResponseDto::fromEntity)
                .collect(Collectors.toList());

        int tokensRestantes = usuarioApiRepository.findTokensDisponiblesById(usuario.getId())
                .orElse(0);

        return new ConsultaListaClientesResponse(
                "Listado de clientes obtenido correctamente",
                clientes.size(),
                clientes,
                tokensRestantes
        );
    }

    /**
     * READ (Por ID): Consulta un cliente por ID y descuenta 1 token si el cliente existe.
     */
    @Transactional
    public ConsultaClienteResponse obtenerPorId(Long id, UsuarioApi usuario) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente con ID " + id + " no encontrado."));

        int filasAfectadas = usuarioApiRepository.decrementarToken(usuario.getId());
        if (filasAfectadas == 0) {
            throw new QuotaExceededException("Ha agotado su saldo de tokens disponibles para realizar consultas.");
        }

        int tokensRestantes = usuarioApiRepository.findTokensDisponiblesById(usuario.getId())
                .orElse(0);

        return new ConsultaClienteResponse(
                "Consulta realizada correctamente",
                ClienteResponseDto.fromEntity(cliente),
                tokensRestantes
        );
    }

    /**
     * CREATE: Registra un nuevo cliente. NO descuenta tokens (operación de escritura).
     */
    @Transactional
    public ClienteResponseDto crear(ClienteCreateDto dto) {
        if (clienteRepository.existsByDni(dto.getDni().trim())) {
            throw new ConflictException("Ya existe un cliente registrado con el DNI " + dto.getDni());
        }
        if (clienteRepository.existsByCorreoElectronico(dto.getCorreoElectronico().trim())) {
            throw new ConflictException("Ya existe un cliente registrado con el correo " + dto.getCorreoElectronico());
        }

        Cliente nuevoCliente = new Cliente(
                null,
                dto.getNombres().trim(),
                dto.getApellidos().trim(),
                dto.getDni().trim(),
                dto.getCorreoElectronico().trim(),
                dto.getTelefono().trim()
        );

        Cliente guardado = clienteRepository.save(nuevoCliente);
        return ClienteResponseDto.fromEntity(guardado);
    }

    /**
     * UPDATE: Actualiza un cliente existente por ID. NO descuenta tokens (operación de escritura).
     */
    @Transactional
    public ClienteResponseDto actualizar(Long id, ClienteUpdateDto dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente con ID " + id + " no encontrado para actualizar."));

        if (clienteRepository.existsByDniAndIdNot(dto.getDni().trim(), id)) {
            throw new ConflictException("El DNI " + dto.getDni() + " ya pertenece a otro cliente registrado.");
        }
        if (clienteRepository.existsByCorreoElectronicoAndIdNot(dto.getCorreoElectronico().trim(), id)) {
            throw new ConflictException("El correo " + dto.getCorreoElectronico() + " ya pertenece a otro cliente registrado.");
        }

        cliente.setNombres(dto.getNombres().trim());
        cliente.setApellidos(dto.getApellidos().trim());
        cliente.setDni(dto.getDni().trim());
        cliente.setCorreoElectronico(dto.getCorreoElectronico().trim());
        cliente.setTelefono(dto.getTelefono().trim());

        Cliente actualizado = clienteRepository.save(cliente);
        return ClienteResponseDto.fromEntity(actualizado);
    }

    /**
     * DELETE: Elimina un cliente existente por ID. NO descuenta tokens (operación de escritura).
     */
    @Transactional
    public void eliminar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente con ID " + id + " no encontrado para eliminar.");
        }
        clienteRepository.deleteById(id);
    }
}
