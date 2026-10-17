package com.techservices.api.controller;

import com.techservices.api.dto.ConsultaClienteResponse;
import com.techservices.api.model.UsuarioApi;
import com.techservices.api.security.ApiKeyAuthInterceptor;
import com.techservices.api.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    /**
     * Endpoint obligatorio del Ejercicio 1 (E1.06):
     * GET /api/clientes/{id}
     * Cabeceras requeridas:
     *   x-api-key: MI_API_KEY_2026
     *   Authorization: Bearer TOKEN_SEGURO_123456
     */
    @GetMapping("/{id}")
    public ResponseEntity<ConsultaClienteResponse> consultarClientePorId(
            @PathVariable Long id,
            @RequestAttribute(ApiKeyAuthInterceptor.AUTH_USER_ATTRIBUTE) UsuarioApi usuarioAutenticado) {

        ConsultaClienteResponse response = clienteService.consultarClientePorId(id, usuarioAutenticado);
        return ResponseEntity.ok(response);
    }
}
