# ==========================================
# Stage 1: Build & Package Spring Boot App
# ==========================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests=true

# ==========================================
# Stage 2: Hardened Runtime Container
# ==========================================
FROM eclipse-temurin:21-jre-alpine AS runner

# Create non-root system user and group
RUN addgroup -S intelliflow && adduser -S intelliflow -G intelliflow

WORKDIR /app
COPY --from=builder /app/target/intelliflow-backend-1.0.0-SNAPSHOT.jar app.jar

# Set ownership
RUN chown -R intelliflow:intelliflow /app
USER intelliflow

# Production JVM Heap Tuning
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
