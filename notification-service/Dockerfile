FROM maven:3.9.5-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY notification-service/pom.xml notification-service/
RUN mvn dependency:go-offline -pl notification-service -am -q
COPY notification-service/src notification-service/src
RUN mvn package -pl notification-service -am -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app
COPY --from=builder /app/notification-service/target/*.jar app.jar
RUN chown spring:spring app.jar
USER spring
EXPOSE 8084
ENTRYPOINT ["java","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75.0","-jar","app.jar"]
