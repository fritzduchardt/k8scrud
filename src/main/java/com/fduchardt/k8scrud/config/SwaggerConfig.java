package com.fduchardt.k8scrud.config;

import org.springframework.context.annotation.*;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.builders.*;
import springfox.documentation.spi.*;
import springfox.documentation.spring.web.plugins.*;
import springfox.documentation.swagger2.annotations.*;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build();
    }
}
