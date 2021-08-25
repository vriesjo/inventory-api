package com.intergamma.inventory.config;

import java.net.URI;

public final class Constants {

    private Constants() {}

    public static final String PROBLEM_BASE_URL = "https://www.intergamma/inventory/problem";
    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");
}
