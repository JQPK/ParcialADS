# TechServices Perú — Examen Parcial N.° 01 (2026-II)

> **Asignatura:** Arquitectura de Software  
> **Institución:** Escuela Profesional de Ingeniería de Sistemas  
> **Tema:** Desarrollo de API REST segura y transformación en microservicio autónomo en la nube

---

## 1. Estructura del Proyecto

El repositorio contiene dos aplicaciones Spring Boot independientes, scripts SQL reproducibles, documentación técnica y colecciones de prueba para Postman Web:

```text
techservices/
├── AGENTS.md                                # Especificación técnica y requerimientos obligatorios
├── README.md                                # Guía general del proyecto
├── render.yaml                              # Configuración de despliegue automatizado en Render
├── database/
│   ├── schema.sql                           # DDL: Creación de bd_techservices, usuarios_api y clientes
│   └── seed.sql                             # DML: Datos iniciales de usuarios autorizados y clientes
├── api-techservices/                        # Ejercicio 1: API REST Segura con cuota de tokens
│   ├── pom.xml
│   ├── .env.example
│   ├── src/main/java/com/techservices/api/
│   │   ├── model/                           # Entidades JPA (Cliente, UsuarioApi, EstadoUsuario)
│   │   ├── repository/                      # Repositorios JPA con decremento atómico
│   │   ├── service/                         # Lógica de negocio y control transaccional
│   │   ├── controller/                      # Controlador REST (GET /api/clientes/{id})
│   │   ├── security/                        # Interceptor x-api-key + Bearer token
│   │   ├── dto/                             # Contratos JSON y DTOs
│   │   └── exception/                       # Manejador global de excepciones HTTP
│   ├── src/main/resources/
│   └── src/test/java/                       # Tests de integración T01-T10 (H2 MySQL mode)
├── ms-clientes/                             # Ejercicio 2: Microservicio CRUD autónomo
│   ├── pom.xml
│   ├── .env.example
│   ├── src/main/java/com/techservices/ms/
│   │   ├── model/
│   │   ├── repository/
│   │   ├── service/
│   │   ├── controller/
│   │   ├── security/
│   │   ├── config/                          # Configuración OpenAPI 3.0 / Swagger UI
│   │   ├── dto/
│   │   └── exception/
│   ├── src/main/resources/
│   └── src/test/java/                       # Tests de integración T11-T20
├── postman/
│   ├── TechServices-API.postman_collection.json         # Pruebas Ejercicio 1
│   ├── MS-Clientes.postman_collection.json              # Pruebas Ejercicio 2
│   └── TechServices.postman_environment.example.json   # Variables de entorno Postman
└── docs/
    ├── ARQUITECTURA.md                      # Justificación técnica, capas y concurrencia
    ├── DESPLIEGUE_RENDER.md                 # Guía paso a paso para despliegue en Render y MySQL nube
    └── VERIFICACION.md                      # Matriz de casos de prueba T01 a T20
```

---

## 2. Base de Datos MySQL y phpMyAdmin

### Paso 1: Creación de la Base de Datos
Abre phpMyAdmin (local o en la nube como Clever Cloud / Aiven) y ejecuta:
```sql
-- Ejecutar el contenido de: database/schema.sql
```
Crea la base de datos `bd_techservices` y las tablas `usuarios_api` y `clientes`.

### Paso 2: Carga Inicial de Datos
En phpMyAdmin, ejecuta:
```sql
-- Ejecutar el contenido de: database/seed.sql
```
Registra:
- **Usuario de prueba oficial del examen:**  
  `api_key`: `MI_API_KEY_2026`  
  `token`: `TOKEN_SEGURO_123456`  
  `tokens_disponibles`: `10`  
  `estado`: `ACTIVO`
- **Segundo usuario activo:** `TECH_KEY_987654` / `TOKEN_EMPRESA_SEC_2026` (5 tokens).
- **Usuario sin saldo:** `KEY_SIN_TOKENS_001` (0 tokens) para probar HTTP 429.
- **Usuario inactivo:** `KEY_INACTIVO_999` para probar HTTP 403.
- **6 Clientes iniciales** con DNIs peruanos y correos únicos.

---

## 3. Ejercicio 1: `api-techservices`

API REST con arquitectura en capas (`Modelo -> Repositorio -> Servicio -> Controlador`).

### Endpoint Principal
```http
GET /api/clientes/1
x-api-key: MI_API_KEY_2026
Authorization: Bearer TOKEN_SEGURO_123456
```

### Reglas de Negocio
1. Requiere obligatoriamente ambas cabeceras HTTP (`x-api-key` y `Authorization: Bearer <token>`).
2. Valida que ambas credenciales pertenezcan al **mismo usuario** y que esté en estado `ACTIVO`.
3. Valida que el usuario tenga saldo (`tokens_disponibles > 0`).
4. Si el cliente existe, descuenta **exactamente 1 token** de forma atómica y retorna `200 OK`:
   ```json
   {
     "message": "Consulta realizada correctamente",
     "data": {
       "id": 1,
       "nombres": "Ana",
       "apellidos": "Torres Quispe",
       "dni": "12345678",
       "correoElectronico": "ana.torres@techservices.pe",
       "telefono": "999888777"
     },
     "tokensRestantes": 9
   }
   ```
5. Si el cliente no existe, retorna `404 Not Found` y **NO descuenta tokens**.
6. Si el saldo se agota, rechaza con `429 Too Many Requests`.

### Ejecución de Pruebas Automatizadas
```bash
cd api-techservices
mvn clean test
```
*Las pruebas ejecutan los casos T01 a T10 en memoria con H2 en modo MySQL sin dependencias externas.*

---

## 4. Ejercicio 2: `ms-clientes`

Microservicio independiente y desacoplado de la API original.

### Endpoints CRUD
| Método | Ruta | Función | ¿Descuenta Token? |
|---|---|---|:---:|
| `GET` | `/api/clientes` | Listar todos los clientes | **SÍ (1)** |
| `GET` | `/api/clientes/{id}` | Obtener cliente por ID | **SÍ (1)** |
| `POST` | `/api/clientes` | Crear cliente con validación Jakarta | **NO** |
| `PUT` | `/api/clientes/{id}` | Actualizar cliente | **NO** |
| `DELETE` | `/api/clientes/{id}` | Eliminar cliente | **NO** |

### Swagger / OpenAPI
Interfaz interactiva disponible en:
```text
http://localhost:8081/swagger-ui/index.html
```
Documenta todas las operaciones, parámetros, esquemas de entrada/salida y seguridad (`x-api-key` y `BearerAuth`).

---

## 5. Despliegue en Render

Sigue la guía detallada en [`docs/DESPLIEGUE_RENDER.md`](file:///d:/ParcialADS/docs/DESPLIEGUE_RENDER.md).

Variables requeridas en cada servicio web de Render:
- `PORT`: `8080` (en API) / `8081` (en Microservicio)
- `DB_URL`: `jdbc:mysql://<HOST>:<PORT>/bd_techservices?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`
- `DB_USERNAME`: `<USUARIO_MYSQL>`
- `DB_PASSWORD`: `<PASSWORD_MYSQL>`
- `SPRING_PROFILES_ACTIVE`: `prod`

---

## 6. Pruebas desde Postman Web

1. Importa en Postman Web:
   - `postman/TechServices-API.postman_collection.json`
   - `postman/MS-Clientes.postman_collection.json`
   - `postman/TechServices.postman_environment.example.json`
2. Configura las variables `apiBaseUrl` y `msBaseUrl` con las URLs asignadas por Render.
3. Ejecuta los flujos y verifica los códigos de respuesta (`200`, `201`, `204`, `401`, `403`, `404`, `409`, `429`).
