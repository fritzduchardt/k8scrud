# K8scrud (Create,Replace,Update & Delete)

K8scrud allows you to dynamically deploy or delete applications in your K8s cluster, triggered by REST-calls.

This project is intended both as a proof-of-concept for emulation as well as a prod-ready solution that can be easily installed with kubectl or helm.

## How it is done

**In a nutshell**, K8scrud deploys a Pod with a web server and kubectl to your cluster. You transfer your K8s manifest to this pod. Now you can deploy or delete it dynamically by calling the web server with the name of the manifest.

**More specifically**, K8scrud runs on 2 Pods to ensure availability. Therefore, that use the same Persistent Volume. 

## Development

### Prerequisites

* Java 11
* Lombok Plugin for IDE

### Build & run with Docker

Build image with:

```shell script
docker build -t k8scrud . 
```

Run container on port 8765

```shell script
docker run -p 8765:8080 k8scrud
```

### Build & run with Gradle

Build application with:

```shell script
./gradlew bootJar
```

Run on port 8765

```shell script
./gradlew bootRun -Pargs=--server.port=8765
```

## Running the tests

```shell script
 # all tests
./gradlew test

 # only unit tests
./gradlew testUnits

 # only IT tests
./gradlew testIT
 
```

## Built With

* OpenJDK Java 11
* Spring Boot 2.2.6
* Gradle 6.3
* Lombok 1.18
* JUnit 5

## Author

* Fritz Duchardt
