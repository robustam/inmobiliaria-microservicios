FROM maven:3.9-jdk-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY busqueda-service/pom.xml busqueda-service/pom.xml
COPY busqueda-service/src busqueda-service/src
RUN mvn -f pom.xml -pl busqueda-service -am package -DskipTests

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /workspace/busqueda-service/target/*.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app.jar"]