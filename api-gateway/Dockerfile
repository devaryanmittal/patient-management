# Use an official Maven image to build the application
FROM maven:3.9.4-eclipse-temurin-21 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:21-jre

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/api-gateway-0.0.1-SNAPSHOT.jar api-gateway.jar

# Expose the port the app runs on
EXPOSE 4004

# Run the JAR file
ENTRYPOINT ["java", "-jar", "api-gateway.jar"]