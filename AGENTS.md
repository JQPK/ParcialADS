# AGENTS.md — Directrices de desarrollo para Antigravity

> **Proyecto:** TechServices Perú — Arquitectura de Software, Examen Parcial N.° 01 (2026-II).  
> **Fuente de requisitos:** `PARCIAL I 2026 I ARQUITECTURA SOFTWARE.docx`, ejercicios 1 y 2 (páginas 1 a 4).  
> **Propósito:** definir **qué debe implementar el agente, cómo debe implementarlo y cómo debe verificarlo**. Este archivo es una especificación de trabajo; no es una implementación ni evidencia de que el sistema ya exista.

## 1. Rol y objetivo del agente

Actúa como **arquitecto de software sénior y desarrollador backend Java/Spring Boot**. Diseña, construye, prueba, documenta y deja listos para despliegue **dos proyectos Spring Boot distintos**:

1. **`api-techservices`** — API REST segura del ejercicio 1, para consultar un cliente por ID con autenticación y descuento de saldo de consultas.
2. **`ms-clientes`** — microservicio **independiente** del ejercicio 2, con CRUD de clientes, la misma protección y control de consumo para consultas GET.

**No fusionar ambos ejercicios en una sola aplicación.** Cada proyecto debe tener su propio ejecutable, estructura, configuración y servicio en Render. El microservicio debe poder arrancar y responder sin ejecutar `api-techservices`.

### 1.1. Prioridad de requisitos

1. Satisfacer **todos los requerimientos explícitos del examen** listados en las secciones 3 y 4.
2. Cumplir las reglas de negocio y los criterios verificables de este documento.
3. Aplicar buenas prácticas sin reemplazar ni desviar el alcance obligatorio.
4. Si hay una decisión no especificada en el Word, usar una solución sencilla, segura y documentar la decisión; **no presentarla como un requisito textual del examen**.
5. Antes de crear o modificar archivos, inspeccionar el repositorio. Reutilizar trabajo válido, evitar sobrescrituras innecesarias y no borrar datos, credenciales locales ni código ajeno sin autorización.

### 1.2. Alcance y exclusiones

- **Obligatorio:** backend REST, MySQL, administración de la base mediante phpMyAdmin, seguridad por cabeceras, cuotas de consultas, pruebas con Postman Web, despliegue en Render y documentación del microservicio mediante Swagger/OpenAPI.
- **No requerido por el examen:** frontend visual, landing page, aplicación móvil, pasarela de pagos, registro público de usuarios, OAuth, JWT, Kubernetes, mensajería o gateway de microservicios.
- Se pueden añadir medidas técnicas ligeras que faciliten la seguridad y la verificación, pero **no sustituir la autenticación exigida por `x-api-key` y `Authorization: Bearer ...`**.

## 2. Estándares tecnológicos y organización

### 2.1. Tecnologías

| Componente | Directriz |
|---|---|
| Lenguaje | Java 17 o versión LTS compatible con el Spring Boot elegido. Preferencia de implementación: Java 17. |
| Framework | Spring Boot, versión estable y compatible con Java elegido. |
| Dependencias | Spring Web, Spring Data JPA, MySQL Connector/J, Bean Validation y soporte de pruebas; Spring Security o filtro equivalente para autenticación. |
| Arquitectura | Capas **Modelo → Repositorio → Servicio → Controlador**, complementadas por DTO, Seguridad, Configuración y Excepciones. |
| Base de datos | **MySQL**, base con nombre exacto `bd_techservices`. |
| Administración BD | **phpMyAdmin** para crear, inspeccionar y administrar la BD; phpMyAdmin **no sustituye** al servidor MySQL. |
| Compilación | Maven con `pom.xml` independiente en cada aplicación. |
| Documentación API | Swagger/OpenAPI obligatorio en `ms-clientes`. |
| Despliegue | **Dos servicios web separados en Render**, conectados a una instancia MySQL accesible desde Render. |
| Pruebas de consumo | **Postman Web** contra las URL públicas de Render. |

No fijar versiones de librerías obsoletas: usar versiones estables compatibles y mantener las dependencias gestionadas por Spring Boot cuando sea posible.

### 2.2. Estructura esperada del repositorio

```text
techservices/
├── AGENTS.md                         # Este archivo, en la raíz
├── README.md                         # Guía general de instalación, ejecución y verificación
├── database/
│   ├── schema.sql                     # Crea las tablas en bd_techservices
│   └── seed.sql                       # Datos demostrativos reproducibles
├── api-techservices/
│   ├── pom.xml
│   ├── .env.example                    # Solo nombres y valores ficticios
│   ├── src/main/java/.../
│   │   ├── model/
│   │   ├── repository/
│   │   ├── service/
│   │   ├── controller/
│   │   ├── security/
│   │   ├── dto/
│   │   └── exception/
│   ├── src/main/resources/
│   └── src/test/java/.../
├── ms-clientes/
│   ├── pom.xml
│   ├── .env.example
│   ├── src/main/java/.../
│   │   ├── model/
│   │   ├── repository/
│   │   ├── service/
│   │   ├── controller/
│   │   ├── security/
│   │   ├── dto/
│   │   └── exception/
│   ├── src/main/resources/
│   └── src/test/java/.../
├── postman/
│   ├── TechServices-API.postman_collection.json
│   ├── MS-Clientes.postman_collection.json
│   └── TechServices.postman_environment.example.json
└── docs/
    ├── ARQUITECTURA.md
    ├── DESPLIEGUE_RENDER.md
    └── VERIFICACION.md
```

La estructura es una **convención de implementación recomendada**, no una transcripción literal del Word. Se admiten variaciones razonables si mantienen independencia, legibilidad y separación en capas.

### 2.3. Reglas de código

- Controladores delgados: reciben solicitudes, validan entrada y llaman a servicios; **no** contienen SQL ni reglas de cuota.
- Servicios: implementan reglas de negocio, transacciones y orquestación.
- Repositorios: consultas y persistencia mediante Spring Data JPA.
- Modelos: entidades JPA adecuadas a las tablas.
- DTO: respuestas públicas sin exponer entidades sensibles ni secretos.
- Validación de entradas con Bean Validation y validaciones de negocio.
- Excepciones centralizadas con `@RestControllerAdvice` o equivalente.
- Evitar duplicación innecesaria; priorizar código comprensible y comprobable.
- Mantener `README.md`, documentación y colecciones Postman sincronizadas con el código realmente implementado.

## 3. Ejercicio 1 — API REST segura (`api-techservices`)

**Objetivo funcional:** permitir la consulta de un cliente por ID exclusivamente a usuarios autorizados y con saldo de tokens disponibles.

### 3.1. Requisitos obligatorios del examen (E1.01–E1.13)

- [ ] **E1.01** Crear la base MySQL **`bd_techservices`** mediante phpMyAdmin.
- [ ] **E1.02** Crear tabla `usuarios_api` con ID, nombre de usuario, API Key, token de autenticación, tokens disponibles y estado.
- [ ] **E1.03** Crear tabla `clientes` con ID, nombres, apellidos, DNI, correo electrónico y teléfono.
- [ ] **E1.04** Registrar **al menos cinco clientes** y **dos usuarios autorizados** que puedan consumir la API.
- [ ] **E1.05** Implementar Spring Boot con capas Modelo, Repositorio, Servicio y Controlador.
- [ ] **E1.06** Exponer un GET para consultar un cliente a través de su ID.
- [ ] **E1.07** Exigir y validar **ambas** cabeceras HTTP: API Key y Token.
- [ ] **E1.08** Validar saldo disponible antes de permitir la consulta.
- [ ] **E1.09** Descontar **exactamente un token solo si la consulta fue correcta**.
- [ ] **E1.10** Devolver JSON con datos del cliente, tokens restantes y mensaje de confirmación.
- [ ] **E1.11** Rechazar consultas cuando se agote el saldo de tokens.
- [ ] **E1.12** Desplegar la API en Render, conectada a MySQL accesible por Internet.
- [ ] **E1.13** Consumir y comprobar la API pública desde Postman Web.

### 3.2. Endpoint obligatorio

```http
GET /api/clientes/1
x-api-key: MI_API_KEY_2026
Authorization: Bearer TOKEN_SEGURO_123456
```

**Importante:** las credenciales mostradas son el **ejemplo que aparece en el examen**, no secretos de producción. La implementación debe permitir configurar usuarios y credenciales sin incrustarlas en el código. Si se emplean esos valores en un entorno de demostración, tratarlos como datos exclusivamente de prueba.

### 3.3. Flujo de negocio obligatorio

1. Recibir `GET /api/clientes/{id}`.
2. Leer `x-api-key` y `Authorization` y verificar que el segundo encabezado tenga formato `Bearer <token>`.
3. Verificar **ambas credenciales asociadas al mismo usuario** y que el usuario esté activo.
4. Comprobar que su saldo sea mayor que cero.
5. Buscar el cliente solicitado.
6. Si el cliente existe y se puede responder con éxito, descontar **una sola unidad** del usuario autenticado.
7. Devolver `200 OK` con datos del cliente, saldo actualizado y mensaje de confirmación.
8. Ante credenciales incorrectas, usuario inactivo, saldo agotado o cliente inexistente, **no descontar tokens**.

El descuento y la lectura necesaria para autorizar la consulta deberán gestionarse de forma **atómica y segura ante concurrencia** (transacción y bloqueo o actualización condicional). Nunca permitir saldo negativo ni doble descuento por una sola consulta.

### 3.4. Ejemplo de respuesta exitosa

El examen exige el **contenido** de la respuesta, pero no prescribe nombres exactos de propiedades; se adopta este contrato de referencia:

```json
{
  "message": "Consulta realizada correctamente",
  "data": {
    "id": 1,
    "nombres": "Ana",
    "apellidos": "Torres",
    "dni": "12345678",
    "correoElectronico": "ana.torres@example.com",
    "telefono": "999888777"
  },
  "tokensRestantes": 4
}
```

Los valores son **ficticios**. El contrato que finalmente se implemente debe mantenerse consistente entre código, tests, Swagger (cuando aplique) y Postman.

## 4. Ejercicio 2 — Microservicio autónomo (`ms-clientes`)

**Objetivo funcional:** disponer de un servicio nuevo que exponga CRUD de clientes, seguridad y consumo por consultas, operando sin arrancar ni invocar la API del ejercicio 1.

### 4.1. Requisitos obligatorios del examen (E2.01–E2.14)

- [ ] **E2.01** Crear **otro proyecto Spring Boot** denominado `ms-clientes`, independiente del anterior.
- [ ] **E2.02** Reutilizar y adaptar entidades, repositorios y funcionalidades necesarios para consultar clientes.
- [ ] **E2.03** Mantener arquitectura en capas: Modelo, Repositorio, Servicio y Controlador.
- [ ] **E2.04** Configurar MySQL administrado mediante phpMyAdmin.
- [ ] **E2.05** Exponer GET de todos los clientes y GET por ID.
- [ ] **E2.06** Proteger los endpoints mediante API Key y Token.
- [ ] **E2.07** Implementar CRUD completo: **POST** crear con validaciones; **GET** todos/por ID; **PUT** actualizar por ID; **DELETE** eliminar por ID.
- [ ] **E2.08** Mantener descuento de una unidad por **consulta GET exitosa**.
- [ ] **E2.09** Retornar respuestas JSON estandarizadas y estados HTTP apropiados.
- [ ] **E2.10** Utilizar **variables de entorno** para credenciales y conexión; no escribir contraseñas en el código.
- [ ] **E2.11** Desplegar como servicio **independiente** en Render y con URL diferente a la del ejercicio 1.
- [ ] **E2.12** Documentar endpoints mediante **Swagger/OpenAPI**.
- [ ] **E2.13** Validar consumo desde **Postman Web** usando su URL pública.
- [ ] **E2.14** Demostrar ejecución independiente sin iniciar la aplicación original.

### 4.2. Contrato mínimo de endpoints

| Método | Ruta | Propósito | Token de consumo |
|---|---|---|---|
| `GET` | `/api/clientes` | Obtener lista de clientes | Descontar 1 si retorna exitosamente. |
| `GET` | `/api/clientes/{id}` | Obtener un cliente | Descontar 1 si el cliente existe y la respuesta es exitosa. |
| `POST` | `/api/clientes` | Crear cliente validando la entrada | No descontar por ser una operación de escritura. |
| `PUT` | `/api/clientes/{id}` | Actualizar cliente existente | No descontar por ser una operación de escritura. |
| `DELETE` | `/api/clientes/{id}` | Eliminar cliente existente | No descontar por ser una operación de escritura. |

**Aclaración de alcance:** el examen exige descuento por **consulta exitosa**. Se concreta como descuento para los GET correctos; POST, PUT y DELETE requieren autenticación, pero **no** consumen cuota de consultas. Si se necesita otra política, debe documentarse explícitamente como cambio de alcance.

- Todos los endpoints de clientes deben validar `x-api-key` y `Authorization: Bearer ...`.
- No crear una dependencia HTTP obligatoria hacia `api-techservices`.
- Es válido que ambos servicios usen **la misma MySQL** y las mismas tablas para esta práctica; esto **no** elimina su independencia de ejecución. Documentar que es una simplificación académica y que una autonomía total de datos requeriría mayor separación.
- Con base de datos compartida, **los saldos de usuarios deben persistir en MySQL y ser coherentes** entre ambos servicios; no administrar contadores independientes en memoria.
- El microservicio debe proporcionar por sí mismo autenticación, consultas, escrituras, conexión a BD y manejo de errores.

### 4.3. Validaciones de datos recomendadas

Estas validaciones operativas complementan el requisito general de validar los datos en `POST`:

- `nombres` y `apellidos`: obligatorios, no vacíos, con longitud razonable.
- `dni`: obligatorio; para los datos de prueba peruanos, validar ocho dígitos; conservarlo como **texto**, no como entero.
- `correoElectronico`: obligatorio y con formato de correo válido.
- `telefono`: texto validado según longitud y formato definidos en el proyecto.
- Impedir inconsistencias de unicidad donde corresponda (por ejemplo, DNI); tratar duplicados con error apropiado.
- En `PUT`, validar igual que en creación y rechazar IDs inexistentes.
- En `DELETE`, rechazar IDs inexistentes y eliminar solo el registro indicado.

### 4.4. Swagger/OpenAPI

Documentar para cada endpoint: método, ruta, descripción, parámetros, cabeceras de seguridad, cuerpos de entrada/salida, ejemplos y posibles estados HTTP. Definir un mecanismo de seguridad OpenAPI para `x-api-key` y `Authorization` (Bearer), y configurar la interfaz para que sea posible probar solicitudes autorizadas.

Ruta recomendada de documentación, si la integración elegida la ofrece: `/swagger-ui/index.html` y especificación `/v3/api-docs`. No marcar Swagger como finalizado sin comprobar que cargue y documente realmente las cinco operaciones del CRUD.

## 5. Base de datos y carga inicial

### 5.1. Esquema mínimo

La BD debe llamarse **exactamente** `bd_techservices` y contener al menos:

**`usuarios_api`**

| Campo lógico exigido | Nombre técnico sugerido | Consideraciones |
|---|---|---|
| ID | `id` | Identificador y clave primaria. |
| Nombre del usuario | `nombre_usuario` | Obligatorio. |
| API Key | `api_key` o `api_key_hash` | Se recomienda guardar representación verificada/hasheada, no exponer la credencial. |
| Token de autenticación | `token_autenticacion` o `token_hash` | Se recomienda proteger su almacenamiento; no retornar en endpoints. |
| Tokens disponibles | `tokens_disponibles` | Entero ≥ 0, persistente y actualizado atómicamente. |
| Estado | `estado` | Activo/inactivo; definir valores y validarlos. |

**`clientes`**

| Campo lógico exigido | Nombre técnico sugerido |
|---|---|
| ID | `id` |
| Nombres | `nombres` |
| Apellidos | `apellidos` |
| DNI | `dni` |
| Correo electrónico | `correo_electronico` |
| Teléfono | `telefono` |

**Norma de consistencia:** una vez definidos los nombres físicos en `schema.sql`, el JPA, las consultas, los seeds y la documentación deben usar esos nombres coherentemente. No cambiar los nombres lógicos de las tablas exigidas.

### 5.2. Scripts y datos de prueba

- Generar `database/schema.sql` que permita crear las tablas desde phpMyAdmin; explicar primero la creación de `bd_techservices`.
- Generar `database/seed.sql` con **cinco o más clientes** y **dos o más usuarios activos**, con saldos iniciales suficientes para verificar descuentos y bloqueo por agotamiento.
- Hacer que los datos de demostración sean reproducibles y no expongan credenciales reales.
- Documentar **cómo obtener y utilizar las credenciales de demostración** de ambos usuarios sin incrustar secretos en el repositorio ni en los logs.
- Añadir instrucciones para comprobar mediante SQL que el saldo desciende después de una consulta exitosa y **permanece igual** ante un GET fallido.
- Evitar que el inicio del aplicativo borre, reinicie o reemplace saldos existentes. No usar recreación automática destructiva del esquema en producción.

## 6. Autenticación, autorización y cuota: reglas transversales

### 6.1. Cabeceras innegociables

```http
x-api-key: <API_KEY_DEL_USUARIO>
Authorization: Bearer <TOKEN_DEL_USUARIO>
```

- Validar presencia, estructura, existencia, coincidencia con **el mismo usuario** y su estado habilitado.
- No permitir acceso con una sola cabecera, sin cabeceras, ni con una pareja API Key/Token perteneciente a usuarios distintos.
- No devolver API Keys, tokens, contraseñas, hashes, credenciales de BD ni trazas internas en respuestas.
- No registrar cabeceras sensibles en logs. No usar valores de ejemplo como credenciales fijas globales ni añadir rutas de bypass para la demostración.
- Usar HTTPS para las URL públicas proporcionadas por Render y seguridad de conexión a MySQL según soporte del proveedor.

### 6.2. Política de consumo

- Una consulta GET respondida satisfactoriamente implica **una** unidad descontada, no más.
- Si no existe el cliente solicitado, **no** descontar (GET por ID → 404).
- Si se carece de saldo, responder rechazo y **no** consultar exitosamente ni permitir saldo negativo.
- GET de listado que retorna correctamente `200`, incluso con lista vacía, se considera consulta exitosa a efectos de cuota; mantener este comportamiento documentado.
- Fallas de autenticación, validación, base de datos o errores inesperados **no** deben consumir tokens.
- La actualización de saldo y la operación GET deben asegurar consistencia transaccional frente a múltiples solicitudes simultáneas.
- En respuestas correctas de GET incluir el saldo actualizado, en especial en el ejercicio 1.

### 6.3. Respuestas y errores HTTP

Convención propuesta para estandarizar la aplicación, **sin atribuir estos códigos exactos al Word**:

| Situación | HTTP recomendado |
|---|---|
| GET exitoso | `200 OK` |
| POST exitoso | `201 Created` |
| PUT exitoso | `200 OK` |
| DELETE exitoso sin cuerpo | `204 No Content` |
| Datos de entrada inválidos | `400 Bad Request` |
| Credenciales faltantes o inválidas | `401 Unauthorized` |
| Usuario autenticado pero inactivo/no habilitado | `403 Forbidden` |
| Cliente no encontrado | `404 Not Found` |
| DNI u otro dato único duplicado | `409 Conflict` |
| Saldo de tokens agotado | `429 Too Many Requests` (cuota agotada; aclararlo en la respuesta) |
| Error interno inesperado | `500 Internal Server Error` |

Usar un formato JSON consistente para errores, por ejemplo `{ "message": "...", "status": 404, "path": "..." }`, sin exponer datos internos. `204` debe carecer de cuerpo, por definición del estándar HTTP.

## 7. Variables de entorno y configuración

En ambos proyectos, parametrizar **al menos** lo siguiente:

```dotenv
# Ejemplo de nombres; NUNCA registrar valores reales en Git
DB_URL=jdbc:mysql://HOST:3306/bd_techservices
DB_USERNAME=USUARIO_DB
DB_PASSWORD=CAMBIAR_EN_ENTORNO_SEGURO
PORT=8080
SPRING_PROFILES_ACTIVE=prod
```

- `application.yml` o `application.properties` debe leer las variables necesarias con `spring.datasource.*` y respetar el puerto proporcionado por Render (`PORT`).
- Crear `.env.example` únicamente con **marcadores ficticios**. Excluir `.env`, archivos de secretos y credenciales reales mediante `.gitignore`.
- Configurar conexiones y permisos MySQL compatibles con el proveedor externo elegido; no asumir que `localhost` funciona desde Render.
- **Render aloja las aplicaciones Spring Boot**; para MySQL se necesita una instancia accesible de forma segura desde Render. Documentar el proveedor real de MySQL y la forma de administrarlo con phpMyAdmin. No asumir que phpMyAdmin es un host de MySQL.
- No publicar credenciales ni capturas que contengan secretos en evidencias o documentación.

## 8. Despliegue en Render y prueba desde Internet

### 8.1. API original

1. Verificar build y arranque local de `api-techservices`.
2. Aprovisionar o identificar MySQL accesible desde el servicio Render y administrar su estructura con phpMyAdmin.
3. Configurar las variables de entorno en Render.
4. Desplegar **solo** el proyecto `api-techservices` como servicio web.
5. Comprobar que la URL pública responda a un GET autenticado, por ejemplo `https://<url-api-render>/api/clientes/1`.

### 8.2. Microservicio

1. Verificar build y arranque local de `ms-clientes` sin ejecutar la API original.
2. Crear **otro servicio web en Render**, distinto al anterior, con configuración propia.
3. Conectarlo a MySQL e inicializar únicamente lo que resulte necesario, sin borrar datos existentes.
4. Verificar GET listado, GET por ID, POST, PUT y DELETE utilizando la URL pública propia.
5. Abrir Swagger/OpenAPI desde la URL pública del microservicio.
6. Detener o no iniciar `api-techservices` y **repetir los GET/CRUD de `ms-clientes`**, para evidenciar independencia.

**No afirmar que algún servicio está publicado sin URL verificable y prueba real.** Si faltan cuentas, acceso a Render, permisos o una base MySQL externa, dejar el proyecto listo, proporcionar los pasos reproducibles e indicar el bloqueo real sin fabricar evidencias.

## 9. Pruebas automáticas y criterios de aceptación

### 9.1. Pruebas obligatorias a implementar o ejecutar

| Caso | Escenario | Resultado esperado |
|---|---|---|
| T01 | GET con API Key y Token correctos del mismo usuario, saldo positivo y cliente existente | `200`; datos del cliente; saldo disminuye exactamente en 1. |
| T02 | GET sin `x-api-key` | `401`; saldo sin cambio. |
| T03 | GET sin `Authorization` | `401`; saldo sin cambio. |
| T04 | GET con API Key o Token inválidos | `401`; saldo sin cambio. |
| T05 | GET con API Key de un usuario y Token de otro | `401`; saldo sin cambio. |
| T06 | GET de usuario inactivo | Acceso denegado; saldo sin cambio. |
| T07 | GET con saldo cero | Rechazo de cuota; saldo sigue en cero. |
| T08 | GET de ID inexistente con credenciales válidas | `404`; saldo sin cambio. |
| T09 | Dos consultas exitosas sucesivas | Saldo disminuye en 2, una unidad por cada GET. |
| T10 | Consultas simultáneas con saldo limitado | Ningún saldo negativo; no se autoriza más de lo permitido. |
| T11 | GET `/api/clientes` del microservicio | `200`, listado; descuento de 1 token si la consulta es exitosa. |
| T12 | POST válido del microservicio | `201`; cliente persistido; sin descuento por escritura. |
| T13 | POST inválido o con DNI duplicado | `400`/`409`; no se crea duplicado. |
| T14 | PUT de cliente existente | Cambio persistido; sin descuento por escritura. |
| T15 | DELETE de cliente existente | Cliente eliminado; sin descuento por escritura. |
| T16 | PUT o DELETE de ID inexistente | `404`; manejo controlado del error. |
| T17 | Swagger del microservicio | Interfaz y OpenAPI muestran rutas y seguridad requeridas. |
| T18 | API original en Render | GET autenticado responde desde Postman Web usando la URL pública. |
| T19 | Microservicio en Render | CRUD responde desde Postman Web usando una **segunda URL pública**. |
| T20 | Aplicación original apagada | `ms-clientes` mantiene consultas/CRUD operativos. |

Construir tests unitarios y de integración pertinentes (JUnit y herramientas compatibles con Spring Boot). Las pruebas con base de datos real o de integración deben comprobar el saldo persistido, no solo mocks de servicios.

### 9.2. Entregables de verificación

- Colección Postman para la API original y otra para el microservicio.
- Ambiente Postman de ejemplo con variables `baseUrl`, `apiKey` y `authToken`, sin credenciales reales.
- Instrucciones de ejecución local y desde Postman Web.
- Evidencias o resultados **reales** de pruebas de autenticación, cuota, GET y CRUD.
- URLs públicas comprobadas de ambos servicios y de Swagger, si el entorno de Render ya está configurado.
- Demostración documentada de que el microservicio funciona sin ejecutar la API original.
- Documentación de estados HTTP, payloads y principales decisiones técnicas.

## 10. Orden de ejecución obligatorio para Antigravity

1. **Inspeccionar el repositorio** y registrar archivos existentes aprovechables.
2. **Definir el contrato y los datos**: esquema SQL, seed reproducible, estados de usuario, formato de credenciales, cuota y respuestas.
3. **Crear/completar `api-techservices`**: capas, autenticación, GET por ID, descuento atómico y manejo de errores.
4. **Probar el ejercicio 1** con una BD compatible, incluidos casos negativos y controles de saldo.
5. **Crear/completar `ms-clientes` como aplicación separada**: CRUD, seguridad local, consulta de listado, cuota y Swagger.
6. **Probar el ejercicio 2**, incluido el arranque independiente y operaciones GET/POST/PUT/DELETE.
7. **Preparar los dos despliegues** de Render con configuración y variables por servicio; efectuar el despliegue cuando existan los accesos necesarios.
8. **Crear y validar colecciones Postman** de ambas URL públicas cuando estén disponibles.
9. **Actualizar README y `docs/`**, describiendo ejecución, arquitectura, despliegue y evidencias verificadas.
10. **Entregar un reporte final de estado**, con requisitos cumplidos, pruebas realizadas, URLs disponibles y pendientes reales.

**Modo de trabajo:** no limitarse a generar explicaciones o esqueletos; implementar código funcional y ejecutable dentro del repositorio que se proporcione. No declarar una tarea finalizada solo por crear carpetas o clases vacías.

## 11. Definición de terminado (Definition of Done)

El trabajo solo puede considerarse completamente terminado cuando se cumplan **todas** las condiciones siguientes:

- [ ] Existen **dos proyectos Spring Boot compilables, ejecutables e independientes**.
- [ ] `bd_techservices` existe y las tablas `usuarios_api` y `clientes` tienen los campos obligatorios.
- [ ] Hay **al menos dos usuarios habilitados** y **cinco clientes** de demostración.
- [ ] Ambas aplicaciones exigen API Key + Token en las cabeceras de sus endpoints de clientes.
- [ ] El ejercicio 1 devuelve JSON con cliente, saldo restante y mensaje confirmatorio.
- [ ] Cada GET exitoso de ambas aplicaciones descuenta solo una unidad y se bloquean consultas sin saldo.
- [ ] Los errores y las solicitudes no exitosas **no** consumen saldo.
- [ ] `ms-clientes` implementa GET todos, GET por ID, POST, PUT y DELETE.
- [ ] Swagger/OpenAPI documenta de manera efectiva el CRUD del microservicio.
- [ ] No hay secretos reales hardcodeados ni credenciales reales versionadas.
- [ ] Las pruebas relevantes pasan y se dispone de evidencia reproducible.
- [ ] Ambas aplicaciones están desplegadas y comprobadas en **servicios Render diferentes** mediante Postman Web.
- [ ] `ms-clientes` sigue funcionando con `api-techservices` apagada.
- [ ] README, scripts SQL, ejemplos de configuración, documentación y colecciones Postman están actualizados.

Si el despliegue público no puede completarse por falta de infraestructura o accesos, **no marcar los últimos criterios como realizados**: consignar su estado como pendiente, el motivo y las instrucciones exactas para finalizarlo.

## 12. Trazabilidad con el Word original

Este archivo conserva la totalidad de los requisitos funcionales de ambos ejercicios de las **páginas 1 a 4** del Word:

- **Ejercicio 1 (5 puntos):** 13 requisitos enumerados → `E1.01` a `E1.13`.
- **Ejercicio 2 (5 puntos):** 14 requisitos enumerados → `E2.01` a `E2.14`.
- **Contrato de ejemplo literal del examen:** `GET /api/clientes/1`, `x-api-key: MI_API_KEY_2026`, `Authorization: Bearer TOKEN_SEGURO_123456`.
- **Tecnologías impuestas por el examen:** Spring Boot, MySQL/phpMyAdmin, Render, Postman Web y Swagger/OpenAPI para el ejercicio 2.

Las carpetas propuestas, el formato JSON de ejemplo, la elección sugerida de Java 17, los códigos HTTP concretos, las reglas específicas de validación y los detalles de atomicidad son **criterios de implementación añadidos para que Antigravity construya una solución comprobable**, no citas literales del examen.
