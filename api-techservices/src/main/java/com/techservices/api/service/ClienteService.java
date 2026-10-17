package com.techservices.api.service;

import com.techservices.api.dto.ClienteDto;
import com.techservices.api.dto.ConsultaClienteResponse;
import com.techservices.api.exception.QuotaExceededException;
import com.techservices.api.exception.ResourceNotFoundException;
import com.techservices.api.model.Cliente;
import com.techservices.api.model.UsuarioApi;
import com.techservices.api.repository.ClienteRepository;
import com.techservices.api.repository.UsuarioApiRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioApiRepository usuarioApiRepository;

    public ClienteService(ClienteRepository clienteRepository, UsuarioApiRepository usuarioApiRepository) {
        this.clienteRepository = clienteRepository;
        this.usuarioApiRepository = usuarioApiRepository;
    }

    /**
     * Consulta un cliente por ID aplicando el control estricto de cuota:
     * 1. Busca el cliente. Si no existe, lanza 404 SIN descontar tokens.
     * 2. Si existe, descuenta exactamente 1 token de forma atómica.
     * 3. Si la cuota ya no permite el descuento, rechaza con 429.
     * 4. Retorna los datos del cliente, mensaje de éxito y tokens restantes.
     */
    @Transactional
    public ConsultaClienteResponse consultarClientePorId(Long id, UsuarioApi usuario) {
        // 1. Verificar existencia del cliente antes de cualquier descuento
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente con ID " + id + " no encontrado."));

        // 2. Descuento atómico de 1 token
        int filasAfectadas = usuarioApiRepository.decrementarToken(usuario.getId());
        if (filasAfectadas == 0) {
            throw new QuotaExceededException("Ha agotado su saldo de tokens disponibles para realizar consultas.");
        }

        // 3. Obtener el saldo restante actualizado desde la base de datos
        int tokensRestantes = usuarioApiRepository.findTokensDisponiblesById(usuario.getId())
                .orElse(0);

        // 4. Construir respuesta estandarizada
        return new ConsultaClienteResponse(
                "Consulta realizada correctamente",
                ClienteDto.fromEntity(cliente),
                tokensRestantes
        );
    }
}
