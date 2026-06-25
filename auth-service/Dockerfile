FROM maven:3.9-openjdk-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY auth-service/pom.xml auth-service/pom.xml
COPY auth-service/src auth-service/src
RUN mvn -f pom.xml -pl auth-service -am package -DskipTests

FROM openjdk:17-jre-slim
WORKDIR /app
COPY --from=build /workspace/auth-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]