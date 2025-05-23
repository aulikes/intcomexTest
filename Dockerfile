# Etapa 1: construir el proyecto
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle clean bootJar --no-daemon

# Etapa 2: imagen de ejecución
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/build/libs/intcomex-rest-api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 80
ENTRYPOINT ["java", "-Dserver.port=80", "-Dspring.profiles.active=prod", "-jar", "app.jar"]


#USADO PARA AWS
# FROM eclipse-temurin:21-jdk
# WORKDIR /app
# COPY app.jar app.jar
# EXPOSE 80
# ENTRYPOINT ["java", "-Dserver.port=80", "-Dspring.profiles.active=prod", "-jar", "app.jar"]

