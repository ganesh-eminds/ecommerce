# Use a lightweight Java 17 JDK base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the jar file (adjust target name if necessary)
COPY target/*.jar app.jar

# Expose port (not mandatory but good practice)
EXPOSE 8081

# Run the application
ENTRYPOINT ["java","-jar","/app/app.jar"]
