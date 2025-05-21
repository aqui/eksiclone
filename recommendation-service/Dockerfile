FROM openjdk:21-slim

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8774

ENTRYPOINT ["java", "-jar", "app.jar"]
