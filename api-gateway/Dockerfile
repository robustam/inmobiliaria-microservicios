FROM maven:3.9-jdk-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY api-gateway/src api-gateway/src
RUN mvn -f pom.xml -pl api-gateway -am package -DskipTests

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /workspace/api-gateway/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]