# Documento de Arquitectura de Software

> **Proyecto:** TechServices Perú — Examen Parcial N.° 01 (2026-II)  
> **Patrón Arquitectónico:** Arquitectura en Capas (Layered Architecture) & Microservicios Autónomos  
> **Estándar:** RESTful API sobre HTTP/HTTPS

---

## 1. Visión General de la Solución

El sistema satisface la necesidad de la empresa **TechServices Perú** de exponer la información de sus clientes de forma segura y controlada mediante cuotas de consumo por tokens.

La solución se compone de dos proyectos backend completamente independientes:
1. **`api-techservices`**: API REST segura que atiende la consulta de cliente por ID (`GET /api/clientes/{id}`), validando cabeceras de autenticación y gestionando el consumo de tokens.
2. **`ms-clientes`**: Microservicio desacoplado que implementa el ciclo de vida completo (CRUD) de clientes, con Swagger/OpenAPI y consumo de tokens exclusivo para lecturas `GET`.

---

## 2. Diagrama de Capas y Componentes

```text
┌────────────────────────────────────────────────────────┐
│                   Cliente HTTP                         │
│       (Postman Web / Swagger UI / App Externa)         │
└──────────────────────────┬─────────────────────────────┘
                           │ Encabezados HTTP:
                           │ x-api-key: <KEY>
                           │ Authorization: Bearer <TOKEN>
                           ▼
┌────────────────────────────────────────────────────────┐
│                   Capa de Seguridad                    │
│      ApiKeyAuthInterceptor (WebMvc Handler)            │
│  - Extrae y valida x-api-key y Bearer token            │
│  - Comprueba usuario único, activo y con saldo > 0     │
│  - Inyecta UsuarioApi en el HttpServletRequest         │
└──────────────────────────┬─────────────────────────────┘
                           ▼
┌────────────────────────────────────────────────────────┐
│                 Capa de Controladores                  │
│       ClienteController / ClienteCrudController        │
│  - Define rutas REST y verbos HTTP                     │
│  - Valida formato de parámetros y DTO de entrada       │
│  - Delega lógica a la capa de servicio                 │
└──────────────────────────┬─────────────────────────────┘
                           ▼
┌────────────────────────────────────────────────────────┐
│                  Capa de Servicios                     │
│      ClienteService / ClienteCrudService               │
│  - Orquestación transaccional (@Transactional)         │
│  - Reglas de negocio (unicidad, existencia)            │
│  - Descuento de tokens ATÓMICO en lecturas exitosas   │
└──────────────────────────┬─────────────────────────────┘
                           ▼
┌────────────────────────────────────────────────────────┐
│                 Capa de Repositorios                   │
│   ClienteRepository / UsuarioApiRepository (JPA)       │
│  - Consultas tipadas mediante Spring Data JPA          │
│  - Operación atómica condicional de decremento         │
└──────────────────────────┬─────────────────────────────┘
                           ▼
┌────────────────────────────────────────────────────────┐
│                  Base de Datos MySQL                   │
│                    bd_techservices                     │
│  - Tabla: usuarios_api (control de saldo y estado)     │
│  - Tabla: clientes (datos de clientes)                 │
└────────────────────────────────────────────────────────┘
```

---

## 3. Estrategia de Control de Concurrencia y Atomicidad

Uno de los requisitos más críticos es que el saldo de tokens de un usuario nunca debe ser negativo ni descontarse más de lo debido, incluso bajo alta concurrencia.

### Solución Implementada:
1. **Verificación Previa:** El interceptor verifica que el usuario tenga saldo (`tokens_disponibles > 0`) antes de permitir que la petición llegue al controlador.
2. **Comprobación de Existencia del Recurso:** El servicio primero busca el cliente en la base de datos. Si no existe (`404`), la transacción termina de inmediato **sin realizar ningún descuento**.
3. **Actualización Condicional Atómica en Motor de Base de Datos:**
   ```sql
   UPDATE usuarios_api 
   SET tokens_disponibles = tokens_disponibles - 1 
   WHERE id = :id AND tokens_disponibles > 0;
   ```
   Esta instrucción es ejecutada directamente por el motor transaccional de MySQL / InnoDB. El bloqueo a nivel de fila (*row-level lock*) garantiza que dos peticiones simultáneas no puedan decrementar el saldo por debajo de cero.
4. Si las filas afectadas son 0, se lanza `QuotaExceededException` (HTTP 429).

---

## 4. Política de Cuotas por Método HTTP

| Servicio | Método | Endpoint | ¿Consume Token? | Justificación |
|---|---|---|:---:|---|
| `api-techservices` | `GET` | `/api/clientes/{id}` | **SÍ (1)** | Consulta exitosa por ID (Requisito E1.09) |
| `ms-clientes` | `GET` | `/api/clientes` | **SÍ (1)** | Consulta exitosa de listado (Requisito E2.08) |
| `ms-clientes` | `GET` | `/api/clientes/{id}` | **SÍ (1)** | Consulta exitosa por ID (Requisito E2.08) |
| `ms-clientes` | `POST` | `/api/clientes` | **NO** | Operación de creación / escritura |
| `ms-clientes` | `PUT` | `/api/clientes/{id}` | **NO** | Operación de modificación / escritura |
| `ms-clientes` | `DELETE` | `/api/clientes/{id}` | **NO** | Operación de eliminación / escritura |

---

## 5. Respuestas y Manejo Centralizado de Errores

Se implementa `GlobalExceptionHandler` con `@RestControllerAdvice` para capturar cualquier anomalía y retornar respuestas JSON estandarizadas:

```json
{
  "timestamp": "2026-10-17T15:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "La cabecera 'x-api-key' es obligatoria para acceder al servicio.",
  "path": "/api/clientes/1"
}
```

Códigos HTTP utilizados:
- `200 OK`: Consulta o actualización exitosa.
- `201 Created`: Cliente creado exitosamente.
- `204 No Content`: Cliente eliminado exitosamente.
- `400 Bad Request`: Parámetros inválidos o validación de datos fallida.
- `401 Unauthorized`: Falta de cabeceras, formato inválido o credenciales no coincidentes.
- `403 Forbidden`: Usuario inactivo.
- `404 Not Found`: Cliente no encontrado (sin descuento de token).
- `409 Conflict`: DNI o correo duplicado.
- `429 Too Many Requests`: Saldo de tokens agotado.
- `500 Internal Server Error`: Fallo interno no controlado.
