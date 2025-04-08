# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy built artifacts
COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/target/site ./site

# Set environment variables
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE="default"
ENV SPRING_DOC_SWAGGER_UI_ENABLED="true"
ENV SPRING_DOC_API_DOCS_ENABLED="true"
ENV SPRING_DOC_SWAGGER_UI_PATH="/swagger-ui.html"
ENV SPRING_DOC_API_DOCS_PATH="/v3/api-docs"

# Expose port
EXPOSE 8080

# Create start script
RUN echo '#!/bin/sh\n\
java $JAVA_OPTS -jar app.jar' > /app/start.sh

RUN chmod +x /app/start.sh

# Create a non-root user
RUN groupadd -r spring && useradd -r -g spring spring
RUN chown -R spring:spring /app
USER spring

# Start the application
CMD ["/app/start.sh"] 