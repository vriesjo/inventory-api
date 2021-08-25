package com.intergamma.inventory.resource.util;

import com.intergamma.inventory.exception.BadRequestException;
import com.intergamma.inventory.exception.Problem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;

@ControllerAdvice
public class ExceptionalHandler {

    @Value("${intergamma.api.name}")
    private String applicationName;

    @ExceptionHandler
    public ResponseEntity<Problem> handleBadRequestException(BadRequestException ex, NativeWebRequest request) {
        return ResponseEntity
                        .badRequest()
                        .headers(HeaderUtil.createError(applicationName, true, ex.getEntityName(), ex.getErrorKey(), ex.getMessage()))
                        .body(new Problem(ex.getEntityName(), ex.getErrorKey()));
    }
}
