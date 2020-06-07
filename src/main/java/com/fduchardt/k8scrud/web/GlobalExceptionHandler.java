package com.fduchardt.k8scrud.web;

import com.fduchardt.k8scrud.exception.*;
import com.fduchardt.k8scrud.service.*;
import lombok.extern.slf4j.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.*;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @SuppressWarnings("unused")
    private void handleIllegalArgumentException(IllegalArgumentException e) {
        log.debug("Controller called with illegal argument", e);
    }

    @ExceptionHandler(K8sCrudException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @SuppressWarnings("unused")
    private K8sResponseDto handleInternalServerError(K8sCrudException e) {
        log.error("K8sCrud Exception for k8scrudId: {} and command: {}", e.getK8sCrudId(), e.getCommand(), e);
        return new K8sResponseDto(e.getMessage(), e.getK8sCrudId());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @SuppressWarnings("unused")
    private void handleInternalServerError(RuntimeException e) {
        log.error("Internal Server Error", e);
    }
}