FROM maven:3.9-jdk-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY imagen-service/pom.xml imagen-service/pom.xml
COPY imagen-service/src imagen-service/src
RUN mvn -f pom.xml -pl imagen-service -am package -DskipTests

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /workspace/imagen-service/target/*.jar app.jar
EXPOSE 8088
ENTRYPOINT ["java", "-jar", "app.jar"]