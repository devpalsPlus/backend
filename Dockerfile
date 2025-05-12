FROM openjdk:17-jdk-slim
LABEL authors="nkr42"
COPY build/libs/devpals-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]