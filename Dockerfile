# Creates Spring Boot Fat Jar
FROM openjdk:11
LABEL maintainer="fduchardt"
COPY . /k8scrud
WORKDIR /k8scrud
RUN ./gradlew bootJar
RUN mv ./build/libs/*.jar ./build/libs/k8scrud.jar

# Debian container with kubectl and Java
FROM debian:buster
RUN apt update && \
      apt install -y curl && \
      curl -LO https://storage.googleapis.com/kubernetes-release/release/`curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt`/bin/linux/amd64/kubectl && \
      chmod +x ./kubectl && \
      mv ./kubectl /usr/local/bin/kubectl
RUN apt install -y openjdk-11-jre
LABEL maintainer="fduchardt"
COPY --from=0 /k8scrud/build/libs/k8scrud.jar ./
CMD java -jar k8scrud.jar