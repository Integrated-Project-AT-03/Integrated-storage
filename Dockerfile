# Use an official OpenJDK image as the base image
FROM openjdk:17-jdk-alpine AS build

# Set the working directory in the container
WORKDIR /app

# Copy the Maven wrapper and project files to the container
COPY .mvn/ .mvn
COPY mvnw .
COPY pom.xml .

# Download the dependencies and initialize the Maven wrapper
RUN ./mvnw dependency:go-offline

# Copy the rest of the project files
COPY src ./src

# Build the project
RUN ./mvnw clean package -DskipTests

# Create the final image
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the built jar file from the build stage
COPY --from=build /app/target/Integrated-storage-0.0.1-SNAPSHOT.jar /app.jar

# Command to run the application
CMD ["java", "-jar", "/app.jar"]
