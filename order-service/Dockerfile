FROM maven:3.9.5-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY order-service/pom.xml order-service/
RUN mvn dependency:go-offline -pl order-service -am -q
COPY order-service/src order-service/src
RUN mvn package -pl order-service -am -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app
COPY --from=builder /app/order-service/target/*.jar app.jar
RUN chown spring:spring app.jar
USER spring
EXPOSE 8082
ENTRYPOINT ["java","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75.0","-jar","app.jar"]
