package com.fduchardt.k8scrud.web;

import com.fduchardt.k8scrud.service.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;

@RequiredArgsConstructor
@RestController
@Slf4j
public class K8sCrudController {

    private final K8sCrudService k8sCrudService ;

    @GetMapping(path="/health")
    public ResponseEntity health() {
        return ResponseEntity.ok().build();
    }


    @PostMapping(path="/{yaml}")
    public K8sResponseDto create(@PathVariable String yaml) throws IOException, InterruptedException {
        return new K8sResponseDto(k8sCrudService.create(yaml));
    }

    @PutMapping(path="/{yaml}/{id}")
    public K8sResponseDto replace(@PathVariable String yaml, @PathVariable String id, @RequestParam(required = false) String mode) throws IOException, InterruptedException {
        if ("update".equals(mode)) {
            return new K8sResponseDto(k8sCrudService.apply(yaml, id));
        } else {
            return new K8sResponseDto(k8sCrudService.replace(yaml, id));
        }
    }

    @DeleteMapping(path="/{yaml}/{id}")
    public K8sResponseDto delete(@PathVariable String yaml, @PathVariable String id) throws IOException, InterruptedException {
        return new K8sResponseDto(k8sCrudService.delete(yaml, id));
    }

}
