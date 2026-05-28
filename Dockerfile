FROM maven:3.9.9-eclipse-temurin-17 AS deps
WORKDIR /workspace

# Copy only the build descriptor first to maximize dependency-cache reuse.
COPY pom.xml .
RUN mvn -B -DskipTests dependency:go-offline

FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY --from=deps /root/.m2 /root/.m2
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app

RUN apk add --no-cache curl

COPY --from=build /workspace/target/*.jar app.jar

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"
ENV PORT=8080

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD curl --fail http://127.0.0.1:${PORT}/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
