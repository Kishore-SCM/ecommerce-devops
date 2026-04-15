FROM maven:3.9.5-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY user-service/pom.xml user-service/
RUN mvn dependency:go-offline -pl user-service -am -q
COPY user-service/src user-service/src
RUN mvn package -pl user-service -am -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app
COPY --from=builder /app/user-service/target/*.jar app.jar
RUN chown spring:spring app.jar
USER spring
EXPOSE 8083
ENTRYPOINT ["java","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75.0","-jar","app.jar"]
