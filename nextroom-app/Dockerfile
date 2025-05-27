FROM openjdk:17-alpine

WORKDIR /app

COPY target/nextroom-app-0.0.1-SNAPSHOT.jar /app/nextroom-app.jar
COPY src/main/resources/application.properties /app/config/application.properties

EXPOSE 8090

CMD ["java", "-jar", "nextroom-app.jar"]
