package com.fduchardt.k8scrud.service;

import lombok.*;

@RequiredArgsConstructor
@Getter
public enum SupportedK8sCommand {

    APPLY("kubectl apply -f -"),
    CREATE("kubectl create -f -"),
    DELETE("kubectl delete -f -");

    private final String command;
}
