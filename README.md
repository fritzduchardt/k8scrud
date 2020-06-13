# K8scrud (Create, Replace, Update & Delete Kubernetes Manifests dynamically)

K8scrud allows you to dynamically deploy or delete applications in your K8s cluster, triggered by REST-calls.

This project is intended both as a *proof-of-concept* for emulation as well as a *production-ready solution* that can be easily installed with kubectl or helm.

## How is it done?

**In a nutshell**, K8scrud deploys a Pod with a web server and kubectl to your cluster. You transfer your K8s manifest to this pod. Now you can deploy or delete it dynamically by calling the web server with the name of the manifest.

**More specifically**, K8scrud runs on 1 Pod for simplicity but could be scaled up to ensure availability. Its Pods mount the same Persistent Volume for storage of K8s manifests. Due to limitations of some cloud providers with Binding PV from multiple nodes, they are configured to run on the same node.
You have the option to either install K8scrud into a singular namespace with RBAC rights **only on that namespace** or into the kube-system namespace with **cluster admin rights**.

## Installation

### Installation with kubectl 

Installation into the default namespace:

```shell script
kubectl apply -f src/main/k8s-manifest/deploy-to-default-namespace.yaml
```

Installation into kube-system with cluster admin rights:

```shell script
kubectl apply -f src/main/k8s-manifest/deploy-to-kube-system.yaml
```

Both installations are preconfigured to report metrics to [Prometheus](https://prometheus.io/)

### Installation with helm

Installation into the default namespace:

```shell script
helm install k8scrud ./src/main/helm/k8scrud --set-string namespace=default 
```

Installation into kube-system with cluster admin rights:
```shell script
helm install k8scrud ./k8scrud --set serviceAccount.useClusterAdminRole=true
```

Installation with **Prometheus scraping** enabled:
```shell script
helm install k8scrud ./k8scrud --set serviceAccount.useClusterAdminRole=true --set-string service.annotations."prometheus\.io/scrape"=true --set service.annotations."prometheus\.io/path"="/prometheus" --set-string service.annotations."prometheus\.io/port"=9090
```

**Please note** that the K8sCrud Helm chart is based on the standard template and is configurable beyond the afore mentioned settings.

## Usage

After installation, wait until the K8scrud Pods are running. This can take a couple of seconds, since a Persistent Volume needs to get provisioned:

### Upload manifests to K8scrud

Transfer your K8s manifest files to K8scrud, e.g. for the provided example-with-ingress.yaml file:

```shell script
# Obtain the name of one of the K8scrud Pods
K8SCRUD_POD=$(kubectl get pod -n default -l app.kubernetes.io/name=k8scrud -o jsonpath="{.items[0].metadata.name}")

# Copy your K8s manifest to the K8scrud Pod into the "k8scrud-manifests" folder
kubectl cp -n default  k8scrud-manifests/example.yaml $K8SCRUD_POD:/k8scrud-manifests
```

**Please ensure that your manifest contains all K8s resources for your deployment in one file concatenated with "---"**

### Make dynamic deployments

Once your manifest file is transferred, you can dynamically deploy it by calling the **K8scrud REST endpoint** while stating the name of the file **without suffix**.

```shell script
# Open a connection to K8scrud from your local machine
kubectl port-forward -n default service/k8scrud 8080

# Call the K8scrud REST endpoint for dynamic deployment creation
curl -v -H Content-Type:application/json -X POST localhost:8080/example --data '{"yourEnvValue": "some-value"}'
```

In the request payload you can specify parameters that will be swapped out for placeholders in your manifests, e.g. *{{ .K8scrud.params.yourEnvValue }}*
Also, every deployment will generate a K8scrud deployment Id that can also be swapped out for a placeholder in the manifest: *{{ .K8scrud.id }}*

**Now, a dynamic deployment has been made**. The **deployment id** can be taken from the response body JSON from the key "k8sCrudId".

### Verification

You can verify that a dynamic deployment has been made by calling:

```shell script
# See all K8scrud example K8s resources
kubectl get all -l name=k8scrud-example-nginx
```

### Delete deployments dynamically

Deployments can be dynamically deleted like this:

```shell script
# Call the K8scrud REST endpoint for dynamic deployment deletion
curl -v -X DELETE localhost:8080/example/[k8sCrudId-of-your-deployment]
```

Enjoy! If you questions or feature requests, **please contact fritz@duchardt.net**

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
