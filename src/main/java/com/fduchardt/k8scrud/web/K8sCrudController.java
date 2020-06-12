package com.fduchardt.k8scrud.web;

import com.fduchardt.k8scrud.service.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;

@RequiredArgsConstructor
@RestController
@Slf4j
public class K8sCrudController {

    private final K8sCrudService k8sCrudService ;

    @PostMapping(path="/{yaml}")
    public K8sResponseDto create(@PathVariable String yaml, @RequestBody(required = false) Map<String, String> params) throws IOException, InterruptedException {
        return k8sCrudService.create(yaml, params);
    }

    @PutMapping(path="/{yaml}/{id}")
    public K8sResponseDto replace(@PathVariable String yaml, @PathVariable String id, @RequestParam(required = false) String mode, @RequestBody(required = false) Map<String, String> params) throws IOException, InterruptedException {
        if ("update".equals(mode)) {
            return k8sCrudService.apply(yaml, id, params);
        } else {
            return k8sCrudService.replace(yaml, id, params);
        }
    }

    @DeleteMapping(path="/{yaml}/{id}")
    public K8sResponseDto delete(@PathVariable String yaml, @PathVariable String id, @RequestBody(required = false) Map<String, String> params) throws IOException, InterruptedException {
        return k8sCrudService.delete(yaml, id, params);
    }

}
