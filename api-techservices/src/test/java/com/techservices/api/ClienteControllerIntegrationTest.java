package com.techservices.api;

import com.techservices.api.model.Cliente;
import com.techservices.api.model.EstadoUsuario;
import com.techservices.api.model.UsuarioApi;
import com.techservices.api.repository.ClienteRepository;
import com.techservices.api.repository.UsuarioApiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ClienteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioApiRepository usuarioApiRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    private UsuarioApi usuarioValido;
    private UsuarioApi usuarioSegundo;
    private UsuarioApi usuarioSinTokens;
    private UsuarioApi usuarioInactivo;
    private Cliente cliente1;

    @BeforeEach
    void setUp() {
        clienteRepository.deleteAll();
        usuarioApiRepository.deleteAll();

        // Usuario 1: Credenciales del examen con 10 tokens
        usuarioValido = usuarioApiRepository.save(new UsuarioApi(
                null,
                "admin_demo",
                "MI_API_KEY_2026",
                "TOKEN_SEGURO_123456",
                10,
                EstadoUsuario.ACTIVO
        ));

        // Usuario 2: Segundo usuario activo con 5 tokens
        usuarioSegundo = usuarioApiRepository.save(new UsuarioApi(
                null,
                "user_dos",
                "KEY_SEGUNDA_USER",
                "TOKEN_SEGUNDO_USER",
                5,
                EstadoUsuario.ACTIVO
        ));

        // Usuario 3: Usuario activo con 0 tokens
        usuarioSinTokens = usuarioApiRepository.save(new UsuarioApi(
                null,
                "user_cero",
                "KEY_CERO_TOKENS",
                "TOKEN_CERO_TOKENS",
                0,
                EstadoUsuario.ACTIVO
        ));

        // Usuario 4: Usuario inactivo
        usuarioInactivo = usuarioApiRepository.save(new UsuarioApi(
                null,
                "user_inactivo",
                "KEY_INACTIVO",
                "TOKEN_INACTIVO",
                10,
                EstadoUsuario.INACTIVO
        ));

        // Cliente 1
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
    @DisplayName("T01: GET con credenciales correctas, saldo positivo y cliente existente retorna 200 y descuenta 1 token")
    void testT01_ConsultaExitosaDescuentaUnToken() throws Exception {
        mockMvc.perform(get("/api/clientes/" + cliente1.getId())
                        .header("x-api-key", "MI_API_KEY_2026")
                        .header("Authorization", "Bearer TOKEN_SEGURO_123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Consulta realizada correctamente")))
                .andExpect(jsonPath("$.data.id", is(cliente1.getId().intValue())))
                .andExpect(jsonPath("$.data.nombres", is("Ana")))
                .andExpect(jsonPath("$.data.dni", is("12345678")))
                .andExpect(jsonPath("$.tokensRestantes", is(9)));

        // Verificar persistencia del saldo en base de datos
        UsuarioApi usuarioBd = usuarioApiRepository.findById(usuarioValido.getId()).orElseThrow();
        assertEquals(9, usuarioBd.getTokensDisponibles());
    }

    @Test
    @DisplayName("T02: GET sin x-api-key retorna 401 y no altera saldo")
    void testT02_SinApiKeyRetorna401() throws Exception {
        mockMvc.perform(get("/api/clientes/" + cliente1.getId())
                        .header("Authorization", "Bearer TOKEN_SEGURO_123456"))
                .andExpect(status().isUnauthorized());

        UsuarioApi usuarioBd = usuarioApiRepository.findById(usuarioValido.getId()).orElseThrow();
        assertEquals(10, usuarioBd.getTokensDisponibles());
    }

    @Test
    @DisplayName("T03: GET sin Authorization retorna 401 y no altera saldo")
    void testT03_SinAuthorizationRetorna401() throws Exception {
        mockMvc.perform(get("/api/clientes/" + cliente1.getId())
                        .header("x-api-key", "MI_API_KEY_2026"))
                .andExpect(status().isUnauthorized());

        UsuarioApi usuarioBd = usuarioApiRepository.findById(usuarioValido.getId()).orElseThrow();
        assertEquals(10, usuarioBd.getTokensDisponibles());
    }

    @Test
    @DisplayName("T04: GET con API Key o Token erróneos retorna 401 y no altera saldo")
    void testT04_CredencialesInvalidasRetorna401() throws Exception {
        mockMvc.perform(get("/api/clientes/" + cliente1.getId())
                        .header("x-api-key", "KEY_ERRONEA")
                        .header("Authorization", "Bearer TOKEN_SEGURO_123456"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/clientes/" + cliente1.getId())
                        .header("x-api-key", "MI_API_KEY_2026")
                        .header("Authorization", "Bearer TOKEN_ERRONEO"))
                .andExpect(status().isUnauthorized());

        UsuarioApi usuarioBd = usuarioApiRepository.findById(usuarioValido.getId()).orElseThrow();
        assertEquals(10, usuarioBd.getTokensDisponibles());
    }

    @Test
    @DisplayName("T05: GET con API Key de un usuario y Token de otro retorna 401 y no altera saldos")
    void testT05_CredencialesCruzadasRetorna401() throws Exception {
        mockMvc.perform(get("/api/clientes/" + cliente1.getId())
                        .header("x-api-key", usuarioValido.getApiKey())
                        .header("Authorization", "Bearer " + usuarioSegundo.getTokenAutenticacion()))
                .andExpect(status().isUnauthorized());

        assertEquals(10, usuarioApiRepository.findById(usuarioValido.getId()).orElseThrow().getTokensDisponibles());
        assertEquals(5, usuarioApiRepository.findById(usuarioSegundo.getId()).orElseThrow().getTokensDisponibles());
    }

    @Test
    @DisplayName("T06: GET de usuario inactivo retorna 403 Forbidden y no altera saldo")
    void testT06_UsuarioInactivoRetorna403() throws Exception {
        mockMvc.perform(get("/api/clientes/" + cliente1.getId())
                        .header("x-api-key", usuarioInactivo.getApiKey())
                        .header("Authorization", "Bearer " + usuarioInactivo.getTokenAutenticacion()))
                .andExpect(status().isForbidden());

        assertEquals(10, usuarioApiRepository.findById(usuarioInactivo.getId()).orElseThrow().getTokensDisponibles());
    }

    @Test
    @DisplayName("T07: GET con saldo de tokens cero retorna 429 Too Many Requests y no altera saldo")
    void testT07_SaldoAgotadoRetorna429() throws Exception {
        mockMvc.perform(get("/api/clientes/" + cliente1.getId())
                        .header("x-api-key", usuarioSinTokens.getApiKey())
                        .header("Authorization", "Bearer " + usuarioSinTokens.getTokenAutenticacion()))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.error", is("Quota Exceeded")));

        assertEquals(0, usuarioApiRepository.findById(usuarioSinTokens.getId()).orElseThrow().getTokensDisponibles());
    }

    @Test
    @DisplayName("T08: GET de ID inexistente con credenciales válidas retorna 404 Not Found y NO descuenta tokens")
    void testT08_ClienteInexistenteNoDescuentaTokens() throws Exception {
        mockMvc.perform(get("/api/clientes/999999")
                        .header("x-api-key", "MI_API_KEY_2026")
                        .header("Authorization", "Bearer TOKEN_SEGURO_123456"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));

        // ¡CRÍTICO!: El saldo debe mantenerse en 10 porque la consulta no fue exitosa
        UsuarioApi usuarioBd = usuarioApiRepository.findById(usuarioValido.getId()).orElseThrow();
        assertEquals(10, usuarioBd.getTokensDisponibles());
    }

    @Test
    @DisplayName("T09: Dos consultas exitosas sucesivas descuentan 2 tokens (uno por cada GET)")
    void testT09_DosConsultasSucesivasDescuentanDosTokens() throws Exception {
        // Primera consulta
        mockMvc.perform(get("/api/clientes/" + cliente1.getId())
                        .header("x-api-key", "MI_API_KEY_2026")
                        .header("Authorization", "Bearer TOKEN_SEGURO_123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokensRestantes", is(9)));

        // Segunda consulta
        mockMvc.perform(get("/api/clientes/" + cliente1.getId())
                        .header("x-api-key", "MI_API_KEY_2026")
                        .header("Authorization", "Bearer TOKEN_SEGURO_123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokensRestantes", is(8)));

        UsuarioApi usuarioBd = usuarioApiRepository.findById(usuarioValido.getId()).orElseThrow();
        assertEquals(8, usuarioBd.getTokensDisponibles());
    }
}
