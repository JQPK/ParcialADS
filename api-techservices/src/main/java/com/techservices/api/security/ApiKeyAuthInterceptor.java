package com.techservices.api.security;

import com.techservices.api.exception.ForbiddenException;
import com.techservices.api.exception.QuotaExceededException;
import com.techservices.api.exception.UnauthorizedException;
import com.techservices.api.model.UsuarioApi;
import com.techservices.api.repository.UsuarioApiRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiKeyAuthInterceptor implements HandlerInterceptor {

    public static final String AUTH_USER_ATTRIBUTE = "authenticatedUser";
    public static final String HEADER_API_KEY = "x-api-key";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final UsuarioApiRepository usuarioApiRepository;

    public ApiKeyAuthInterceptor(UsuarioApiRepository usuarioApiRepository) {
        this.usuarioApiRepository = usuarioApiRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Permitir peticiones preflight CORS
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String apiKey = request.getHeader(HEADER_API_KEY);
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new UnauthorizedException("La cabecera 'x-api-key' es obligatoria para acceder al servicio.");
        }

        String authHeader = request.getHeader(HEADER_AUTHORIZATION);
        if (authHeader == null || authHeader.trim().isEmpty()) {
            throw new UnauthorizedException("La cabecera 'Authorization' es obligatoria para acceder al servicio.");
        }

        if (!authHeader.startsWith(BEARER_PREFIX)) {
            throw new UnauthorizedException("La cabecera 'Authorization' debe tener el formato 'Bearer <token>'.");
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();
        if (token.isEmpty()) {
            throw new UnauthorizedException("El token de autenticación no puede estar vacío.");
        }

        // Buscar el usuario verificando que AMBAS credenciales pertenezcan al mismo usuario
        UsuarioApi usuario = usuarioApiRepository.findByApiKeyAndTokenAutenticacion(apiKey.trim(), token)
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas: API Key o Token no coinciden con un usuario autorizado."));

        // Verificar estado activo
        if (!usuario.isActivo()) {
            throw new ForbiddenException("El usuario autorizado se encuentra inactivo. Comuníquese con el administrador.");
        }

        // Verificar saldo de tokens antes de permitir la ejecución
        if (!usuario.tieneTokensDisponibles()) {
            throw new QuotaExceededException("Ha agotado su saldo de tokens disponibles para realizar consultas.");
        }

        // Almacenar el usuario autenticado en el contexto de la petición
        request.setAttribute(AUTH_USER_ATTRIBUTE, usuario);
        return true;
    }
}
