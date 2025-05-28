ARG AND_DOCKER_PROXY_REGISTRY_NAME
# build stage
FROM $AND_DOCKER_PROXY_REGISTRY_NAME/maven:3.8.5-openjdk-17-slim AS builder
WORKDIR /usr/test/src/deposit-service
COPY . .
RUN mvn clean install -Dmaven.test.skip

#app package stage
FROM $AND_DOCKER_PROXY_REGISTRY_NAME/openjdk:17-alpine3.14
WORKDIR /app
RUN apk add curl
COPY --from=builder /usr/test/src/deposit-service/target/deposit-service*.jar /app/deposit-service.jar
CMD ["java", "-jar","-Dspring.profiles.active=dev", "/app/deposit-service.jar"]
