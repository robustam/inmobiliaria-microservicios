FROM maven:3.9-jdk-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY reserva-service/pom.xml reserva-service/pom.xml
COPY reserva-service/src reserva-service/src
RUN mvn -f pom.xml -pl reserva-service -am package -DskipTests

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /workspace/reserva-service/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]