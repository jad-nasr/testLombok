# Use the official OpenJDK 17 image from Docker Hub
FROM openjdk:17
# Set working directory inside the container
WORKDIR /app
# Copy the compiled Java application JAR file into the container ~~~~ the jar is the name set in the final-name in the pom.xml
COPY ./target/testApplication-service.jar /app
# Expose the port the Spring Boot application will run on
EXPOSE 8080
# Command to run the application
CMD ["java", "-jar", "testApplication-service.jar"]