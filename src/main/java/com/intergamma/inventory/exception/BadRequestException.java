package com.intergamma.inventory.exception;

import com.intergamma.inventory.config.Constants;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class BadRequestException extends GenericException {

    private final String entityName;

    private final String errorKey;

    public BadRequestException(final String defaultMessage, final String entityName, final String errorKey) {
        this(Constants.DEFAULT_TYPE, defaultMessage, entityName, errorKey);
    }

    private BadRequestException(final URI type, final String defaultMessage, final String entityName, final String errorKey) {
        super(type, defaultMessage, HttpStatus.BAD_REQUEST, createParameters(entityName, errorKey));
        this.entityName = entityName;
        this.errorKey = errorKey;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getErrorKey() {
        return errorKey;
    }

    private static Map<String, Object> createParameters(final String entityName, final String errorKey) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("message", "error." + errorKey);
        parameters.put("params", entityName);

        return parameters;
    }
}
