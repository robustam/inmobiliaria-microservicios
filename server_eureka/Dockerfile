FROM maven:3.9-jdk-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY server_eureka/pom.xml server_eureka/pom.xml
COPY server_eureka/src server_eureka/src
RUN mvn -f pom.xml -pl server_eureka -am package -DskipTests

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /workspace/server_eureka/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]