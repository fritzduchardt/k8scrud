package com.fduchardt.k8scrud.web;

import lombok.*;
import lombok.experimental.*;

@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class K8sResponseDto {
    String message;
}
