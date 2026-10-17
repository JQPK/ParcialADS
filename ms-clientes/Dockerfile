# =====================================================================
# Dockerfile para ms-clientes (Ejercicio 2)
# Multi-stage build optimizado para Render y despliegue en la nube
# =====================================================================

# Etapa 1: Compilación con Maven y Java 17
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen ligera de ejecución
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/ms-clientes-1.0.0.jar app.jar
ENV PORT=8081
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
