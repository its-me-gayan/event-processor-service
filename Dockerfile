# ============================
# 1) Build Stage
# ============================
FROM gradle:8.6-jdk17 AS builder
WORKDIR /app

# Copy Gradle wrapper and config first (cache efficient but safe)
COPY gradle gradle
COPY gradlew .
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Force Gradle wrapper to be executable
RUN chmod +x gradlew

# Pre-download dependencies (cached unless build.gradle changes)
RUN ./gradlew --no-daemon build -x test || true

# Copy full source (this invalidates cache if you change any code)
COPY src src

# Build final boot JAR (clean forces rebuild)
RUN ./gradlew clean bootJar --no-daemon


# ============================
# 2) Run Stage
# ============================
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy built jar from build stage
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

# Force Spring Boot to run on 0.0.0.0 inside Docker
ENTRYPOINT ["java", "-Dserver.address=0.0.0.0", "-jar", "app.jar"]
