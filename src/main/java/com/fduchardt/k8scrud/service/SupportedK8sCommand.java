package com.fduchardt.k8scrud.service;

import lombok.*;

@RequiredArgsConstructor
@Getter
public enum SupportedK8sCommand {

    CREATE("kubectl create -f -"),
    REPLACE("kubectl replace -f -"),
    APPLY("kubectl apply -f -"),
    DELETE("kubectl delete -f -");

    private final String command;
}
