# Guía de Despliegue en Render y Configuración MySQL en la Nube

Esta guía describe cómo desplegar los servicios **`api-techservices`** (Ejercicio 1) y **`ms-clientes`** (Ejercicio 2) en **Render**, conectados a una base de datos **MySQL accesible por Internet y administrada con phpMyAdmin**.

---

## 1. Aprovisionamiento de MySQL con phpMyAdmin en la Nube

El examen exige:
1. Base de datos MySQL con nombre `bd_techservices`.
2. Administración mediante phpMyAdmin.
3. Conexión accesible por Internet desde Render.

### Opción Recomendada: Clever Cloud (MySQL + phpMyAdmin Gratuito)
1. Ingresa a [Clever Cloud Console](https://console.clever-cloud.com/).
2. Haz clic en **Create** > **an add-on** > selecciona **MySQL**.
3. Selecciona el plan gratuito (*Dev*).
4. Asigna el nombre `bd_techservices`.
5. Una vez creada la base de datos:
   - En el menú lateral verás el botón **phpMyAdmin**. Haz clic para abrirlo directamente en el navegador.
   - En phpMyAdmin, dirígete a la pestaña **Importar** (o **SQL**).
   - Ejecuta primero el contenido de `database/schema.sql` y luego `database/seed.sql`.
6. En el panel de Clever Cloud obtendrás las credenciales de conexión:
   - **Host:** `bxxxxxx-mysql.services.clever-cloud.com`
   - **Port:** `3306`
   - **Database:** `bd_techservices` (o el nombre generado)
   - **User:** `xxxxxx`
   - **Password:** `xxxxxx`
   - **JDBC URL:**  
     `jdbc:mysql://HOST:PORT/DATABASE?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`

### Alternativa: Aiven o Railway
Si utilizas Aiven o Railway para alojar MySQL, puedes gestionar la base conectando una instancia local de phpMyAdmin (XAMPP o Docker) apuntando al host remoto de Aiven/Railway, o utilizar DBeaver/phpMyAdmin web.

---

## 2. Despliegue de `api-techservices` en Render (Ejercicio 1)

1. En el panel de [Render](https://dashboard.render.com/), haz clic en **New +** > **Web Service**.
2. Conecta tu repositorio de GitHub / GitLab donde está subido el proyecto.
3. Configura los siguientes parámetros:
   - **Name:** `api-techservices`
   - **Region:** Ohio (US East) o Frankfurt
   - **Branch:** `main`
   - **Root Directory:** `api-techservices`
   - **Runtime:** `Java`
   - **Build Command:** `mvn clean package -DskipTests`
   - **Start Command:** `java -jar target/api-techservices-1.0.0.jar`
   - **Instance Type:** `Free`
4. En la sección **Environment Variables**, añade:
   - `PORT`: `8080`
   - `DB_URL`: `jdbc:mysql://<HOST>:<PORT>/<DATABASE>?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`
   - `DB_USERNAME`: `<USUARIO_MYSQL_NUBE>`
   - `DB_PASSWORD`: `<PASSWORD_MYSQL_NUBE>`
   - `SPRING_PROFILES_ACTIVE`: `prod`
5. Haz clic en **Deploy Web Service**.
6. Render generará una URL pública similar a:  
   `https://api-techservices-xxxx.onrender.com`

---

## 3. Despliegue de `ms-clientes` en Render (Ejercicio 2)

1. En el panel de Render, haz clic nuevamente en **New +** > **Web Service**.
2. Selecciona el **mismo repositorio**, pero configurando la carpeta del microservicio:
   - **Name:** `ms-clientes`
   - **Root Directory:** `ms-clientes`
   - **Runtime:** `Java`
   - **Build Command:** `mvn clean package -DskipTests`
   - **Start Command:** `java -jar target/ms-clientes-1.0.0.jar`
   - **Instance Type:** `Free`
3. En **Environment Variables**, añade:
   - `PORT`: `8081`
   - `DB_URL`: *(Misma URL de conexión MySQL que el ejercicio 1)*
   - `DB_USERNAME`: *(Mismo usuario MySQL)*
   - `DB_PASSWORD`: *(Misma contraseña MySQL)*
   - `SPRING_PROFILES_ACTIVE`: `prod`
4. Haz clic en **Deploy Web Service**.
5. Render generará una **segunda URL pública independiente**:  
   `https://ms-clientes-xxxx.onrender.com`

---

## 4. Verificación desde Postman Web

1. Abre [Postman Web](https://web.postman.co/).
2. Importa la colección `postman/TechServices-API.postman_collection.json` y el ambiente `postman/TechServices.postman_environment.example.json`.
3. Edita las variables del ambiente en Postman:
   - `apiBaseUrl`: `https://api-techservices-xxxx.onrender.com`
   - `apiKey`: `MI_API_KEY_2026`
   - `authToken`: `TOKEN_SEGURO_123456`
   - `clientId`: `1`
4. Ejecuta las peticiones de prueba:
   - Consulta exitosa `GET /api/clientes/1` -> HTTP 200 con cliente y saldo actualizado.
   - Consulta con saldo 0 o sin credenciales -> Rechazo controlado (401, 403, 429).
