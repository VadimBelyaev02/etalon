# Spring Boot Exception Handling Starter

> A reusable Spring Boot starter for consistent exception handling across microservices.

## Table of Contents

1. [Problem](#problem)
2. [Solution](#solution)
3. [Building](#building)
4. [Installation](#installation)
6. [Configuration](#configuration)
7. [Usage](#usage)
8. [Customization](#customization)
9. [Examples](#examples)
10. [Testing](#testing)
11. [Contributing](#contributing)
12. [License](#license)

---

## Problem

We have multiple services containing the same exceptions, their handling logic is duplicated too.

## Solution

Create a spring boot starter and move handlers exceptions and all logic related to it from all services to the starter. 
As an outcome, we have one exception handler class, unique exception in the starter, we import these exception classes in our services.

## Features

Any exception thrown in a microservice, in which the starter is used, is caught by the starter's handler.

Moveover, the starter has functionality to determine which microservice threw an exception when it's called using feign client. 
When an HTTP call using feign client finishes with a non <i>2xx</i> status code, the ```FeignErrorDecoder.decode()``` is called (docs: <a href="https://github.com/OpenFeign/feign/blob/master/core/src/main/java/feign/codec/ErrorDecoder.java">ErrorDecoder</a>).


## Building

It can be locally installed using this command:

```
mvn clean install
```

## Installation

Add the following code to a <i>pom.xml<i> file of a microservice:

```xml
<dependency>
  <groupId>com.andersenlab.etalon</groupId>
  <artifactId>etalon-exception-handler-starter</artifactId>
  <version>${exception.handler.version}</version>
</dependency>
```

```exception.handler.version``` should be specified in the <i>properties</i> section, the version is taken from the starter's <i>pom.xml</i>

## Configuration

Enable/disable the exception handler in `application.properties` (enabled by default):

```
exception.handler.enabled=true
```

If it's disabled, no exceptions will be handled by the starter

## Usage

Instructions to enable and use the starter in a service:

1. Add the dependency
2. Configure feign clients (examples below)
3. Throw an exception which is imported from the starter
4. It's caught in the starter's exception handler

If it's needed to create a new exception class, it should be created in the starter.
