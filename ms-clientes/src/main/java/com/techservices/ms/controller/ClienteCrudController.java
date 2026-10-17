package com.techservices.ms.controller;

import com.techservices.ms.dto.*;
import com.techservices.ms.model.UsuarioApi;
import com.techservices.ms.security.ApiKeyAuthInterceptor;
import com.techservices.ms.service.ClienteCrudService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Operaciones CRUD de clientes con control de cuota de consumo y seguridad")
@SecurityRequirement(name = "ApiKeyAuth")
@SecurityRequirement(name = "BearerAuth")
public class ClienteCrudController {

    private final ClienteCrudService clienteCrudService;

    public ClienteCrudController(ClienteCrudService clienteCrudService) {
        this.clienteCrudService = clienteCrudService;
    }

    /**
     * READ (Todos): GET /api/clientes
     */
    @Operation(
            summary = "Listar todos los clientes",
            description = "Retorna la lista de todos los clientes registrados. Descuenta exactamente 1 token de la cuota del usuario autorizado al responder exitosamente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "Credenciales faltantes o no autorizadas", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Usuario inactivo", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "429", description = "Saldo de tokens agotado", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<ConsultaListaClientesResponse> listarTodos(
            @Parameter(hidden = true)
            @RequestAttribute(ApiKeyAuthInterceptor.AUTH_USER_ATTRIBUTE) UsuarioApi usuarioAutenticado) {

        ConsultaListaClientesResponse response = clienteCrudService.listarTodos(usuarioAutenticado);
        return ResponseEntity.ok(response);
    }

    /**
     * READ (Por ID): GET /api/clientes/{id}
     */
    @Operation(
            summary = "Obtener cliente por ID",
            description = "Consulta la información individual de un cliente por su identificador. Descuenta 1 token solo si el cliente existe."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado correctamente"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado (no descuenta tokens)", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "429", description = "Saldo de tokens agotado", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ConsultaClienteResponse> obtenerPorId(
            @Parameter(description = "Identificador único del cliente", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(hidden = true)
            @RequestAttribute(ApiKeyAuthInterceptor.AUTH_USER_ATTRIBUTE) UsuarioApi usuarioAutenticado) {

        ConsultaClienteResponse response = clienteCrudService.obtenerPorId(id, usuarioAutenticado);
        return ResponseEntity.ok(response);
    }

    /**
     * CREATE: POST /api/clientes
     */
    @Operation(
            summary = "Crear nuevo cliente",
            description = "Registra un nuevo cliente previa validación de formato y unicidad de DNI (8 dígitos) y correo. Operación de escritura: NO consume tokens de consulta."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada no válidos", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "DNI o correo ya existente", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping
    public ResponseEntity<ClienteResponseDto> crear(
            @Valid @RequestBody ClienteCreateDto dto) {

        ClienteResponseDto response = clienteCrudService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * UPDATE: PUT /api/clientes/{id}
     */
    @Operation(
            summary = "Actualizar cliente existente",
            description = "Actualiza los datos de un cliente registrado. Valida existencia y unicidad de DNI. Operación de escritura: NO consume tokens de consulta."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada no válidos", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado para actualizar", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "DNI o correo en conflicto con otro registro", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDto> actualizar(
            @Parameter(description = "ID del cliente a modificar", example = "1", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ClienteUpdateDto dto) {

        ClienteResponseDto response = clienteCrudService.actualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE: DELETE /api/clientes/{id}
     */
    @Operation(
            summary = "Eliminar cliente",
            description = "Elimina físicamente un cliente registrado por su ID. Operación de escritura: NO consume tokens de consulta."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente eliminado exitosamente (sin cuerpo)"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado para eliminar", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del cliente a eliminar", example = "1", required = true)
            @PathVariable Long id) {

        clienteCrudService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
