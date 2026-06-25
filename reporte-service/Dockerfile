FROM maven:3.9-jdk-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY reporte-service/pom.xml reporte-service/pom.xml
COPY reporte-service/src reporte-service/src
RUN mvn -f pom.xml -pl reporte-service -am package -DskipTests

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /workspace/reporte-service/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]