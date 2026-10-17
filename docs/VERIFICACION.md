# Matriz de Verificación y Pruebas — TechServices Perú

Este documento contiene la matriz completa de casos de prueba según las directrices de `AGENTS.md` (Sección 9), detallando los escenarios para **`api-techservices`** (T01 a T10) y para **`ms-clientes`** (T11 a T20).

---

## 1. Casos de Prueba: Ejercicio 1 (`api-techservices`)

| Caso | Escenario de Prueba | Headers HTTP | Método / Ruta | HTTP Esperado | Saldo Inicial | Saldo Final | Detalle / Aserción |
|:---:|---|---|---|:---:|:---:|:---:|---|
| **T01** | Consulta exitosa con credenciales válidas | `x-api-key: MI_API_KEY_2026`<br>`Authorization: Bearer TOKEN_SEGURO_123456` | `GET /api/clientes/1` | **200 OK** | 10 | 9 | Retorna datos del cliente 1, mensaje "Consulta realizada correctamente" y `tokensRestantes: 9`. |
| **T02** | Petición sin cabecera `x-api-key` | `Authorization: Bearer TOKEN_SEGURO_123456` | `GET /api/clientes/1` | **401 Unauthorized** | 10 | 10 | Error descriptivo; saldo inalterado. |
| **T03** | Petición sin cabecera `Authorization` | `x-api-key: MI_API_KEY_2026` | `GET /api/clientes/1` | **401 Unauthorized** | 10 | 10 | Error descriptivo; saldo inalterado. |
| **T04** | API Key o Token inválidos | `x-api-key: ERRONEA`<br>`Authorization: Bearer TOKEN_SEGURO_123456` | `GET /api/clientes/1` | **401 Unauthorized** | 10 | 10 | Error descriptivo; saldo inalterado. |
| **T05** | Credenciales cruzadas (Key de usuario 1, Token de usuario 2) | `x-api-key: MI_API_KEY_2026`<br>`Authorization: Bearer TOKEN_EMPRESA_SEC_2026` | `GET /api/clientes/1` | **401 Unauthorized** | 10 (U1)<br>5 (U2) | 10 (U1)<br>5 (U2) | Credenciales no pertenecen al mismo usuario; ningún saldo se modifica. |
| **T06** | Usuario con estado INACTIVO | `x-api-key: KEY_INACTIVO_999`<br>`Authorization: Bearer TOKEN_INACTIVO_999999` | `GET /api/clientes/1` | **403 Forbidden** | 10 | 10 | Acceso denegado por cuenta inactiva; saldo inalterado. |
| **T07** | Usuario con saldo de tokens = 0 | `x-api-key: KEY_SIN_TOKENS_001`<br>`Authorization: Bearer TOKEN_SIN_SALDO_000000` | `GET /api/clientes/1` | **429 Too Many Requests** | 0 | 0 | Error "Quota Exceeded"; no permite la consulta; saldo sigue en 0. |
| **T08** | Cliente ID inexistente con credenciales válidas | `x-api-key: MI_API_KEY_2026`<br>`Authorization: Bearer TOKEN_SEGURO_123456` | `GET /api/clientes/999999` | **404 Not Found** | 10 | 10 | Cliente no existe; **CRÍTICO: NO se descuenta token**. |
| **T09** | Dos consultas exitosas sucesivas | `x-api-key: MI_API_KEY_2026`<br>`Authorization: Bearer TOKEN_SEGURO_123456` | `GET /api/clientes/1` (x2) | **200 OK** | 10 | 8 | Disminuye exactamente 1 token en cada GET exitoso (10 -> 9 -> 8). |
| **T10** | Concurrencia con saldo limitado (10 hilos simultáneos para 3 tokens) | `x-api-key: KEY_CONCURRENT`<br>`Authorization: Bearer TOKEN_CONCURRENT` | `GET /api/clientes/1` (paralelo) | **200 / 429** | 3 | 0 | Exactamente 3 peticiones obtienen 200, 7 peticiones reciben 429; saldo final exactamente 0 (nunca negativo). |

---

## 2. Casos de Prueba: Ejercicio 2 (`ms-clientes`)

| Caso | Escenario de Prueba | Método / Ruta | HTTP Esperado | ¿Consume Token? | Detalle / Aserción |
|:---:|---|---|:---:|:---:|---|
| **T11** | Listado de todos los clientes | `GET /api/clientes` | **200 OK** | **SÍ (1)** | Retorna lista de clientes y `tokensRestantes` actualizado. |
| **T12** | Registro de nuevo cliente con datos válidos | `POST /api/clientes` | **201 Created** | **NO** | Persiste nuevo cliente; saldo de tokens del usuario permanece inalterado. |
| **T13** | Registro con datos inválidos o DNI duplicado | `POST /api/clientes` | **400 / 409** | **NO** | 400 por DNI no numérico/formato; 409 si el DNI ya existe; saldo inalterado. |
| **T14** | Actualización de cliente existente | `PUT /api/clientes/1` | **200 OK** | **NO** | Modifica datos del cliente; saldo inalterado. |
| **T15** | Eliminación de cliente existente | `DELETE /api/clientes/1` | **204 No Content** | **NO** | Elimina registro; respuesta sin cuerpo; saldo inalterado. |
| **T16** | PUT o DELETE sobre ID inexistente | `PUT /api/clientes/999`<br>`DELETE /api/clientes/999` | **404 Not Found** | **NO** | Recurso no encontrado; saldo inalterado. |
| **T17** | Documentación OpenAPI / Swagger UI | `GET /swagger-ui/index.html`<br>`GET /v3/api-docs` | **200 OK** | **NO** | Carga interfaz Swagger UI con las 5 operaciones y autenticación. |
| **T18** | Consumo público de `api-techservices` | `GET https://.../api/clientes/1` | **200 OK** | **SÍ (1)** | Verificado desde Postman Web en la URL pública de Render. |
| **T19** | Consumo público de `ms-clientes` | `GET / POST https://.../api/clientes` | **200 / 201** | **Según tabla** | Verificado desde Postman Web en la segunda URL de Render. |
| **T20** | Demostración de autonomía del microservicio | Cualquier endpoint de `ms-clientes` | **200 / 201** | **Según tabla** | Microservicio responde completamente sin iniciar `api-techservices`. |
