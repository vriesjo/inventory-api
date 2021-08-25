package com.intergamma.inventory.resource.util;

import org.springframework.http.HttpHeaders;

public interface HeaderUtil {

    static HttpHeaders createdEntityCreatedHeaders(final String applicationName, final String entityName, final String param) {
        String message = applicationName + "." + entityName + ".created";

        return createEntityHeaders(applicationName, message, param);
    }

    static HttpHeaders createEntityUpdatedHeaders(final String applicationName, final String entityName, final String param) {
        String message = applicationName + "." + entityName + ".updated";

        return createEntityHeaders(applicationName, message, param);
    }

    static HttpHeaders createEntityDeletedHeaders(String applicationName, String entityName, String param) {
        String message = applicationName + "." + entityName + ".deleted";

        return createEntityHeaders(applicationName, message, param);
    }

    private static HttpHeaders createEntityHeaders(final String applicationName, final String message, final String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-" + applicationName + "-msg", message);
        headers.add("X-" + applicationName + "-param", param);

        return headers;
    }

    public static HttpHeaders createError(String applicationName, boolean enableTranslation, String entityName, String errorKey, String defaultMessage) {
        String message = enableTranslation ? "error." + errorKey : defaultMessage;
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-" + applicationName + "-error", message);
        headers.add("X-" + applicationName + "-params", entityName);
        return headers;
    }
}
