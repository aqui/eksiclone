FROM openjdk:21-slim

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8771

ENTRYPOINT ["java", "-jar", "app.jar"]
