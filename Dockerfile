# Multi-stage build для CRM приложения

# Stage 1: Build
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# Копируем gradle файлы
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew gradlew.bat ./

# Копируем модули
COPY crm-auth-ldap ./crm-auth-ldap
COPY crm-core ./crm-core

# Собираем проект
RUN chmod +x gradlew
RUN ./gradlew :crm-core:bootJar --no-daemon -x test

# Stage 2: Runtime
FROM eclipse-temurin:17-jre
WORKDIR /app

# Копируем собранный jar
COPY --from=build /app/crm-core/build/libs/*.jar app.jar

# Создаем непривилегированного пользователя
RUN useradd -m -u 1001 appuser && chown -R appuser:appuser /app
USER appuser

# Expose порт
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
