package com.intergamma.inventory.exception;

import org.springframework.http.HttpStatus;

import java.net.URI;
import java.util.Map;

public abstract class GenericException extends RuntimeException {

    private final URI type;
    private final String title;
    private final HttpStatus status;
    private final Map<String, Object> parameters;

    protected GenericException(
                    final URI type,
                    final String title,
                    final HttpStatus status,
                    final Map<String, Object> parameters
    ) {
        this.type = type;
        this.title = title;
        this.status = status;
        this.parameters = parameters;
    }

}
