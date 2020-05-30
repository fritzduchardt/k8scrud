package com.fduchardt.k8scrud.web;

import com.fduchardt.k8scrud.service.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;

@RequiredArgsConstructor
@RestController
@Slf4j
public class K8sCrudController {

    private final K8sCrudService k8sDispatcher;

    @PutMapping(path="/{yaml}")
    public K8sResponseDto apply(@PathVariable String yaml) throws IOException, InterruptedException {
        return new K8sResponseDto(k8sDispatcher.apply(yaml));
    }

    @DeleteMapping(path="/{yaml}/{id}")
    public K8sResponseDto delete(@PathVariable String yaml, @PathVariable String id) throws IOException, InterruptedException {
        return new K8sResponseDto(k8sDispatcher.delete(yaml, id));
    }

}
