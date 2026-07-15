# ============================================
# ETAPA 1: BUILD
# ============================================
FROM eclipse-temurin:23-jdk AS builder

WORKDIR /build

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x gradlew

RUN ./gradlew dependencies --no-daemon

COPY src ./src

RUN ./gradlew build -x test --no-daemon


# ============================================
# ETAPA 2: RUNTIME
# ============================================
FROM eclipse-temurin:23-jre

WORKDIR /app

COPY --from=builder /build/build/libs/fundamentos01-api.jar app.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]