# Use a maintained OpenJDK 17 image
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy the JAR built by Maven
COPY target/auth-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8089

ENTRYPOINT ["java", "-jar", "app.jar"]
