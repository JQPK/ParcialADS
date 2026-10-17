package com.techservices.ms.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String API_KEY_SECURITY_SCHEME = "ApiKeyAuth";
    public static final String BEARER_SECURITY_SCHEME = "BearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TechServices Perú — Microservicio de Clientes (ms-clientes)")
                        .version("1.0.0")
                        .description("Microservicio autónomo para la gestión completa (CRUD) de clientes con control de cuota de consumo por tokens y seguridad por API Key y Bearer Token. Cumple los requisitos E2.01 a E2.14 del Examen Parcial N.° 01.")
                        .contact(new Contact()
                                .name("TechServices Perú - Soporte de Arquitectura")
                                .email("soporte@techservices.pe")))
                .addSecurityItem(new SecurityRequirement()
                        .addList(API_KEY_SECURITY_SCHEME)
                        .addList(BEARER_SECURITY_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(API_KEY_SECURITY_SCHEME, new SecurityScheme()
                                .name("x-api-key")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .description("Clave de API asignada al usuario autorizado"))
                        .addSecuritySchemes(BEARER_SECURITY_SCHEME, new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("Token")
                                .description("Token de autenticación en formato 'Bearer <token>'")));
    }
}
