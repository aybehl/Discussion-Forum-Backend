# Use a Maven image to build the JAR
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and dependency files
COPY pom.xml ./
COPY src ./src

# Build the JAR file
RUN mvn clean package -DskipTests

# Use a smaller base image for running the application
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "/app/app.jar"]
