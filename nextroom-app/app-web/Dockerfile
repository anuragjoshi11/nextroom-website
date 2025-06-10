FROM openjdk:17-alpine

WORKDIR /app

COPY target/app-web-0.0.1-SNAPSHOT.jar ./nextroom-app.jar
COPY src/main/resources/application.properties ./config/application.properties

EXPOSE 8090

CMD ["java", "-jar", "nextroom-app.jar"]
