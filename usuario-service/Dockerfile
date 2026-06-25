FROM maven:3.9-jdk-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY usuario-service/pom.xml usuario-service/pom.xml
COPY usuario-service/src usuario-service/src
RUN mvn -f pom.xml -pl usuario-service -am package -DskipTests

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /workspace/usuario-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]