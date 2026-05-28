# Multi-stage build para optimizar la imagen
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Copiar archivos de configuración
COPY pom.xml .
COPY src ./src

# Compilar la aplicación
RUN mvn clean package -DskipTests

# Segunda etapa: imagen runtime
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copiar JAR desde la etapa de build
COPY --from=builder /build/target/innovacionti-1.0.0.jar app.jar

# Aceptar argumento de build
ARG GOOGLE_MAPS_API_KEY
ENV GOOGLE_MAPS_API_KEY=${GOOGLE_MAPS_API_KEY}

# Variables de entorno adicionales
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE=prod

# Exponer puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD java -version

# Ejecutar la aplicación
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
