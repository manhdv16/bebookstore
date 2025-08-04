FROM maven:3.8.6-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -Dskiptests

FROM openjdk:17-jdk-slim
WORKDIR /run
COPY --from=build /app/target/*.jar /run/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/run/app.jar"]