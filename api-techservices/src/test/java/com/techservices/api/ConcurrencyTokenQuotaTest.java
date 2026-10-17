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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ConcurrencyTokenQuotaTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioApiRepository usuarioApiRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    private UsuarioApi usuario;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        clienteRepository.deleteAll();
        usuarioApiRepository.deleteAll();

        // Usuario con saldo exacto de 3 tokens
        usuario = usuarioApiRepository.save(new UsuarioApi(
                null,
                "user_concurrente",
                "KEY_CONCURRENT",
                "TOKEN_CONCURRENT",
                3,
                EstadoUsuario.ACTIVO
        ));

        cliente = clienteRepository.save(new Cliente(
                null,
                "Ana",
                "Torres",
                "12345678",
                "ana.torres@techservices.pe",
                "999888777"
        ));
    }

    @Test
    @DisplayName("T10: Peticiones concurrentes con saldo limitado no generan saldo negativo ni sobregiro")
    void testT10_ConcurrenciaSinSaldoNegativo() throws Exception {
        int totalHilos = 10; // 10 solicitudes paralelas intentando consumir 3 tokens
        ExecutorService executor = Executors.newFixedThreadPool(totalHilos);
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(totalHilos);

        AtomicInteger exitos = new AtomicInteger(0);
        AtomicInteger rechazados = new AtomicInteger(0);

        for (int i = 0; i < totalHilos; i++) {
            executor.submit(() -> {
                try {
                    latch.await();
                    int statusCode = mockMvc.perform(get("/api/clientes/" + cliente.getId())
                                    .header("x-api-key", usuario.getApiKey())
                                    .header("Authorization", "Bearer " + usuario.getTokenAutenticacion()))
                            .andReturn()
                            .getResponse()
                            .getStatus();

                    if (statusCode == 200) {
                        exitos.incrementAndGet();
                    } else if (statusCode == 429) {
                        rechazados.incrementAndGet();
                    }
                } catch (Exception e) {
                    // registrar error
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        // Liberar todos los hilos simultáneamente
        latch.countDown();
        finishLatch.await();
        executor.shutdown();

        // El saldo final debe ser exactamente 0, NUNCA negativo
        UsuarioApi usuarioBd = usuarioApiRepository.findById(usuario.getId()).orElseThrow();
        assertEquals(0, usuarioBd.getTokensDisponibles(), "El saldo final debe ser exactamente 0");
        assertEquals(3, exitos.get(), "Exactamente 3 peticiones deben haber tenido éxito (200)");
        assertEquals(7, rechazados.get(), "Exactamente 7 peticiones deben haber sido rechazadas (429)");
    }
}
