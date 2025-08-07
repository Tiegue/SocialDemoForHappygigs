# graphqlserver/Dockerfile
# Hint: straight copy jar file, So the jar file should be built before running this.
#FROM eclipse-temurin:21-jdk-alpine
#
#WORKDIR /app
#
#COPY target/graphqlserver-*.jar app.jar
#
#EXPOSE 4446
#
#ENTRYPOINT ["java", "-jar", "app.jar"]

# Below is full build command
# graphqlserver/Dockerfile

# --- Stage 1: Build using full source including parent pom.xml ---
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /build
# copy the entire socialDemo project (including parent pom)
COPY . .
WORKDIR /build/graphqlserver
RUN mvn clean package -DskipTests

# --- Stage 2: Run the app ---
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app
COPY --from=builder /build/graphqlserver/target/graphqlserver-*.jar app.jar

EXPOSE 4446
ENTRYPOINT ["java", "-jar", "app.jar"]

