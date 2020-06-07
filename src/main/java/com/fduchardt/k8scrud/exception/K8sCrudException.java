package com.fduchardt.k8scrud.exception;

import lombok.*;

@RequiredArgsConstructor
@Data
public class K8sCrudException extends RuntimeException {
    private final String k8sCrudId;
    private final String message;
    private final String[] command;
}
