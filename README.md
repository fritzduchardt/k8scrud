# K8scrud (Create ,Replace, Update & Delete Kubernetes Manifests dynamically)

K8scrud allows you to dynamically deploy or delete applications in your K8s cluster triggered by REST-calls.

This project is intended both as a *proof-of-concept* for emulation as well as a *production-ready solution* that can be easily installed with kubectl or helm.

## How is it done?

**In a nutshell:** K8scrud deploys a Pod with a web server with access to *kubectl* to your cluster. You transfer your K8s manifest to this Pod. Now you can deploy or delete this manifest at will by executing REST calls the web server passing it the name of the manifest.

**More specifically:** K8scrud runs on 2 Pods to improve availability. Those use the same *Persistent Volume* for storage of K8s manifests. 

You have the option to either install K8scrud into a singular namespace with RBAC rights only on that namespace or into the kube-system namespace with cluster admin rights.

## Installation

### Installation with kubectl 

Installation into the default namespace:

```shell script
kubectl apply -f src/main/k8s-manifest/deploy-to-default-namespace.yaml
```
Installation into kube-system namespace:
```shell script
kubectl apply -f src/main/k8s-manifest/deploy-to-kube-system.yaml
```


### Installation with helm

Installation into the default namespace:

```shell script
helm install k8scrud ./src/main/helm/k8scrud --debug --set-string namespace=default 
```

Installation into kube-system with cluster admin rights:
```shell script
helm install k8scrud ./src/main/helm/k8scrud --set serviceAccount.useClusterAdminRole=true
```
## Usage

**Transfer your K8s manifest files to one of the K8scrud Pods**, e.g. for the provided example.yaml file:

```shell script
K8SCRUD_POD=$(kubectl get pod -n kube-system -l app.kubernetes.io/name=k8scrud -o jsonpath="{.items[0].metadata.name}")
kubectl cp -n kube-system k8scrud-manifests/example.yaml $K8SCRUD_POD:/k8s-manifests
```
**Warning:** make sure to change the namespace to where your installation namespace or kube-system if required.

Please ensure that your manifest contains all K8s resources for your deployment in one file concatenated with "---"

**Once the file is transferred, you can dynamically deploy it by calling the K8scrud REST endpoint**.

```shell script
kubectl port-forward service/k8scrud 8080 -n kube-system
curl -v -X PUT localhost:8080/example
```
**Warning:** make sure to change the namespace to where your installation namespace or kube-system if required.

**The deployments can be dynamically deleted like this:**

```shell script
curl -v -X DELETE localhost:8080/example
```

## Development

### Prerequisites

* Java 11
* Lombok Plugin for IDE
* Go Template Plugin for IDE
* Kubernetes Plugin for IDE
* kubectl
* helm
* docker

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
* Docker 19.03.8
* Helm 3
* Kubernetes 1.17


## Author

* Fritz Duchardt
