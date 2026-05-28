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

# Copy the built jar from the build stage
COPY --from=build /app/target/billing-service-0.0.1-SNAPSHOT.jar billing-service.jar

# Expose port 4001 for the Billing Service
EXPOSE 4001

# Run the application
ENTRYPOINT ["java", "-jar", "billing-service.jar"]
