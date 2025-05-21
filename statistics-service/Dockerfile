FROM openjdk:21-slim

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8772

ENTRYPOINT ["java", "-jar", "app.jar"]
