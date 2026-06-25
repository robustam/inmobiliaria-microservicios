FROM maven:3.9-jdk-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY propiedad-service/pom.xml propiedad-service/pom.xml
COPY propiedad-service/src propiedad-service/src
RUN mvn -f pom.xml -pl propiedad-service -am package -DskipTests

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /workspace/propiedad-service/target/*.jar app.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "app.jar"]