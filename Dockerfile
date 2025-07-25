# Etapa 1: build del proyecto con Gradle
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle clean bootJar --no-daemon

# Etapa 2: imagen liviana para ejecución
FROM eclipse-temurin:21-jre
WORKDIR /app
# Copiar el jar generado (detectado automáticamente con wildcard)
COPY --from=builder /app/build/libs/*.jar app.jar
# Expone el puerto interno del contenedor
EXPOSE 8090
# Arrancar la app con perfil `prod` y puerto fijo
ENTRYPOINT ["java", "-Dserver.port=8090", "-Dspring.profiles.active=${SPRING_PROFILE}", "-jar", "app.jar"]

# docker build -t intcomex-api .
# docker run -e SPRING_PROFILE=dev -p 8090:8090 --name intcomex intcomex-api

#USADO PARA AWS
# FROM eclipse-temurin:21-jdk
# WORKDIR /app
# COPY app.jar app.jar
# EXPOSE 80
# ENTRYPOINT ["java", "-Dserver.port=80", "-Dspring.profiles.active=prod", "-jar", "app.jar"]

