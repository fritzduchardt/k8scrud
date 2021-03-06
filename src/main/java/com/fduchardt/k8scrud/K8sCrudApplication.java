package com.fduchardt.k8scrud;

import com.fduchardt.k8scrud.service.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;

@Slf4j
@SpringBootApplication
public class K8sCrudApplication {

    @Autowired
    K8sCrudService k8sDispatcher;

    public static void main(String[] args) {
        SpringApplication.run(K8sCrudApplication.class, args);
    }
}
