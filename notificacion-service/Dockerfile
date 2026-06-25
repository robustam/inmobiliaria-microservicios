FROM maven:3.9-jdk-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY notificacion-service/pom.xml notificacion-service/pom.xml
COPY notificacion-service/src notificacion-service/src
RUN mvn -f pom.xml -pl notificacion-service -am package -DskipTests

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /workspace/notificacion-service/target/*.jar app.jar
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "app.jar"]