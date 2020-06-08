package com.fduchardt.k8scrud.exception;

import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class K8sCrudException extends RuntimeException {
    private final String k8sCrudId;
    private final String message;
    private String[] command;
}
