package com.intergamma.inventory.exception;

public class Problem {

    private final String entityName;

    private final String errorKey;

    public Problem(final String entityName, final String errorKey) {
        this.entityName = entityName;
        this.errorKey = errorKey;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getErrorKey() {
        return errorKey;
    }
}
