package com.techservices.ms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techservices.ms.dto.ClienteCreateDto;
import com.techservices.ms.dto.ClienteUpdateDto;
import com.techservices.ms.model.Cliente;
import com.techservices.ms.model.EstadoUsuario;
import com.techservices.ms.model.UsuarioApi;
import com.techservices.ms.repository.ClienteRepository;
import com.techservices.ms.repository.UsuarioApiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ClienteCrudControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioApiRepository usuarioApiRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    private UsuarioApi usuario;
    private Cliente cliente1;

    @BeforeEach
    void setUp() {
        clienteRepository.deleteAll();
        usuarioApiRepository.deleteAll();

        // Usuario autorizado con 10 tokens
        usuario = usuarioApiRepository.save(new UsuarioApi(
                null,
                "admin_ms",
                "MI_API_KEY_2026",
                "TOKEN_SEGURO_123456",
                10,
                EstadoUsuario.ACTIVO
        ));

        // Cliente inicial
        cliente1 = clienteRepository.save(new Cliente(
                null,
                "Ana",
                "Torres",
                "12345678",
                "ana.torres@techservices.pe",
                "999888777"
        ));
    }

    @Test
    @DisplayName("T11: GET /api/clientes retorna 200, lista de clientes y descuenta 1 token")
    void testT11_ListarClientesDescuentaUnToken() throws Exception {
        mockMvc.perform(get("/api/clientes")
                        .header("x-api-key", "MI_API_KEY_2026")
                        .header("Authorization", "Bearer TOKEN_SEGURO_123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Listado de clientes obtenido correctamente")))
                .andExpect(jsonPath("$.total", is(1)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.tokensRestantes", is(9)));

        UsuarioApi usuarioBd = usuarioApiRepository.findById(usuario.getId()).orElseThrow();
        assertEquals(9, usuarioBd.getTokensDisponibles());
    }

    @Test
    @DisplayName("T12: POST /api/clientes con datos válidos retorna 201 Created y NO descuenta tokens")
    void testT12_CrearClienteValidoSinDescuento() throws Exception {
        ClienteCreateDto nuevo = new ClienteCreateDto(
                "Carlos",
                "Mendoza",
                "87654321",
                "carlos.mendoza@techservices.pe",
                "987654321"
        );

        mockMvc.perform(post("/api/clientes")
                        .header("x-api-key", "MI_API_KEY_2026")
                        .header("Authorization", "Bearer TOKEN_SEGURO_123456")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nombres", is("Carlos")))
                .andExpect(jsonPath("$.dni", is("87654321")));

        // Verificar que el cliente existe en base de datos
        assertTrue(clienteRepository.existsByDni("87654321"));

        // ¡CRÍTICO!: No debe descontar tokens por ser operación de escritura POST
        UsuarioApi usuarioBd = usuarioApiRepository.findById(usuario.getId()).orElseThrow();
        assertEquals(10, usuarioBd.getTokensDisponibles());
    }

    @Test
    @DisplayName("T13: POST con DNI duplicado retorna 409 Conflict y con formato inválido retorna 400 Bad Request")
    void testT13_CrearClienteInvalidoODuplicado() throws Exception {
        // DNI duplicado (ya existe cliente1 con 12345678)
        ClienteCreateDto duplicado = new ClienteCreateDto(
                "Pedro",
                "Gómez",
                "12345678",
                "pedro.gomez@techservices.pe",
                "912345678"
        );

        mockMvc.perform(post("/api/clientes")
                        .header("x-api-key", "MI_API_KEY_2026")
                        .header("Authorization", "Bearer TOKEN_SEGURO_123456")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicado)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)));

        // Formato inválido: DNI de menos de 8 dígitos y correo sin formato
        ClienteCreateDto invalido = new ClienteCreateDto(
                "",
                "Gómez",
                "1234", // Menor a 8 dígitos
                "correo-invalido",
                "123"
        );

        mockMvc.perform(post("/api/clientes")
                        .header("x-api-key", "MI_API_KEY_2026")
                        .header("Authorization", "Bearer TOKEN_SEGURO_123456")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));

        UsuarioApi usuarioBd = usuarioApiRepository.findById(usuario.getId()).orElseThrow();
        assertEquals(10, usuarioBd.getTokensDisponibles());
    }

    @Test
    @DisplayName("T14: PUT /api/clientes/{id} modifica datos y NO descuenta tokens")
    void testT14_ActualizarClienteExistente() throws Exception {
        ClienteUpdateDto actualizacion = new ClienteUpdateDto(
                "Ana María",
                "Torres Ramos",
                "12345678",
                "anamaria.torres@techservices.pe",
                "999111222"
        );

        mockMvc.perform(put("/api/clientes/" + cliente1.getId())
                        .header("x-api-key", "MI_API_KEY_2026")
                        .header("Authorization", "Bearer TOKEN_SEGURO_123456")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizacion)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombres", is("Ana María")))
                .andExpect(jsonPath("$.telefono", is("999111222")));

        Cliente modificado = clienteRepository.findById(cliente1.getId()).orElseThrow();
        assertEquals("Ana María", modificado.getNombres());

        // Tokens deben permanecer en 10
        UsuarioApi usuarioBd = usuarioApiRepository.findById(usuario.getId()).orElseThrow();
        assertEquals(10, usuarioBd.getTokensDisponibles());
    }

    @Test
    @DisplayName("T15: DELETE /api/clientes/{id} elimina cliente, retorna 204 y NO descuenta tokens")
    void testT15_EliminarClienteExistente() throws Exception {
        mockMvc.perform(delete("/api/clientes/" + cliente1.getId())
                        .header("x-api-key", "MI_API_KEY_2026")
                        .header("Authorization", "Bearer TOKEN_SEGURO_123456"))
                .andExpect(status().isNoContent());

        assertFalse(clienteRepository.existsById(cliente1.getId()));

        UsuarioApi usuarioBd = usuarioApiRepository.findById(usuario.getId()).orElseThrow();
        assertEquals(10, usuarioBd.getTokensDisponibles());
    }

    @Test
    @DisplayName("T16: PUT o DELETE con ID inexistente retorna 404 Not Found")
    void testT16_PutODeleteInexistente() throws Exception {
        ClienteUpdateDto actualizacion = new ClienteUpdateDto(
                "Inexistente",
                "Persona",
                "99998888",
                "inexistente@correo.com",
                "999888777"
        );

        mockMvc.perform(put("/api/clientes/999999")
                        .header("x-api-key", "MI_API_KEY_2026")
                        .header("Authorization", "Bearer TOKEN_SEGURO_123456")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizacion)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));

        mockMvc.perform(delete("/api/clientes/999999")
                        .header("x-api-key", "MI_API_KEY_2026")
                        .header("Authorization", "Bearer TOKEN_SEGURO_123456"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @DisplayName("T17: Swagger UI y OpenAPI spec están disponibles públicamente sin autenticación previa")
    void testT17_SwaggerDisponibleSinAutenticacion() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi", notNullValue()))
                .andExpect(jsonPath("$.info.title", containsString("Microservicio de Clientes")))
                .andExpect(jsonPath("$.paths['/api/clientes'].get", notNullValue()))
                .andExpect(jsonPath("$.paths['/api/clientes'].post", notNullValue()))
                .andExpect(jsonPath("$.paths['/api/clientes/{id}'].get", notNullValue()))
                .andExpect(jsonPath("$.paths['/api/clientes/{id}'].put", notNullValue()))
                .andExpect(jsonPath("$.paths['/api/clientes/{id}'].delete", notNullValue()));

        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }
}
