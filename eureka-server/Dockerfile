# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the target directory
COPY target/eureka-server-jar.jar eureka-server-jar.jar

# Expose the port the application runs on
EXPOSE 8761

# Command to run the application
ENTRYPOINT ["java", "-jar", "eureka-server-jar.jar"]