# Account Service

[![Quality Gate Status](https://sonarqube9.andersenlab.com/api/project_badges/measure?project=a-music2-etalon-account-service-dev&metric=alert_status&token=sqb_d024e2833162e58c81c8281a9ea9cf7010cdf68e)](https://sonarqube9.andersenlab.com/dashboard?id=a-music2-etalon-account-service-dev)
[![Coverage](https://sonarqube9.andersenlab.com/api/project_badges/measure?project=a-music2-etalon-account-service-dev&metric=coverage&token=sqb_d024e2833162e58c81c8281a9ea9cf7010cdf68e)](https://sonarqube9.andersenlab.com/dashboard?id=a-music2-etalon-account-service-dev)
[![Maintainability Rating](https://sonarqube9.andersenlab.com/api/project_badges/measure?project=a-music2-etalon-account-service-dev&metric=sqale_rating&token=sqb_d024e2833162e58c81c8281a9ea9cf7010cdf68e)](https://sonarqube9.andersenlab.com/dashboard?id=a-music2-etalon-account-service-dev)

Account Service is an internal API which grants capability to manage user accounts.


## Technologies Used

`Java 17`, `Spring Boot 3.1.x`, `Maven`, `OpenFeign`, `Mapstruct`, `PostgreSQL`, `Liquibase`, `JUnit`, `Wiremock`, `Testcontainers`

## Configuration & Local Setup

### Pre-requirements

Before running the service locally, ensure the following tools and services are available:

1. **Docker** is installed and running.
2. Start the required components (PostgreSQL, pgAdmin) using the `docker-compose.yml` file provided from the [onboarding documentation](https://wiki.andersenlab.com/x/q15fDg).

### Local execution
All mandatory configurations for local environment are already predefined in [application-default.properties](src/main/resources/application-default.properties). At this point, the service is ready to be started.<br>
> ⚠️ Note: Ensure that all required [microservices](src/main/java/com/andersenlab/etalon/accountservice/client) are also running, as some API endpoints rely on them.

### PostgreSQL

To interact with the local PostgreSQL instance running inside Docker, you can use the web-based pgAdmin interface available at: [http://localhost:5050/](http://localhost:5050/)<br>
A server named **"Etalon Local"** is already preconfigured. You can use it to explore and manage the `etalon` database.

- **Password**: `postgres`

> ⚠️ Note: pgAdmin is preconfigured and will ask only for password prompt in order to connect to the database server.

## CI/CD

### Overview

Build and deploy steps are automated and handled by [Gitlab pipelines](https://git.andersenlab.com/Andersen/a-music2-etalon/a-music2-etalon-account-service/-/pipelines) for the default `dev` branch.

- For each **opened** MR there is a pipeline that is doing code quality check
- For each **merged** MR there is a pipeline that builds docker image, publishes it and deploys to AWS infrastructure.

### Build from custom branch

It is possible to build and deploy custom docker image from any branch for testing purposes without passing a merge request.

1) Prepare a local Git branch, commit all changes, and push it to the remote repository.
2) Open the [Run pipeline](https://git.andersenlab.com/Andersen/a-music2-etalon/a-music2-etalon-account-service/-/pipelines/new) page on the Gitlab.
3) Using **Run for branch name or tag** dropbox select branch that should be build and deployed.
4) In the **Variables** section provide Input variable key as `CI_COMMIT_BRANCH` with constant value `dev`.
5) Press **Run pipeline** button and wait until the job will return successful status.

## Code analysis

Code is continuously analyzed by **SonarQube**. Passing the quality gate is mandatory for all merge requests to the `dev` branch.<br>
Link to the SonarQube dashboard: [Sonar Qube - account-service](https://sonarqube9.andersenlab.com/dashboard?id=a-music2-etalon-account-service-dev)

## Architecture
``` mermaid
graph TD
  subgraph External
    User[External Consumer]
  end

  subgraph AWS Environment
    APIGW[AWS API Gateway]
    TX[Account Service]
    DB[(PostgreSQL DB)]
    

    subgraph Consumed APIs
      Card[Card Service]
      UserSvc[User Service]
    end
  end

  %% External to AWS
  User -->|HTTPS Request| APIGW
  APIGW --> TX

  %% Internal Interactions
  TX --> DB

  %% Outgoing API Calls
  TX --> Card
  TX --> UserSvc
```




