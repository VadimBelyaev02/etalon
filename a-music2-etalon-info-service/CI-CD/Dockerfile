ARG AND_DOCKER_PROXY_REGISTRY_NAME
# build stage
FROM $AND_DOCKER_PROXY_REGISTRY_NAME/maven:3.8.5-openjdk-17-slim AS builder
ARG MAVEN_CLI_OPTS
WORKDIR /app
COPY . .
RUN mvn clean install $MAVEN_CLI_OPTS -Dmaven.test.skip

#app package stage
FROM $AND_DOCKER_PROXY_REGISTRY_NAME/openjdk:17-alpine3.14
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/app.jar
CMD ["java", "-Xms64m", "-Xmx64m", "-jar", "/app/app.jar"] 
