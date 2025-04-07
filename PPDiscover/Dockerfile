# Build stage
FROM maven:3.9.8-eclipse-temurin-21 AS build-stage
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Production stage
FROM openjdk:21
WORKDIR /app
COPY --from=build-stage /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"] 