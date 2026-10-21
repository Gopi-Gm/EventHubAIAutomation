package com.api.base;

import io.restassured.response.Response;

public class HealthService extends BaseService {

    private static final String HEALTH_ENDPOINT = "/health";

    /**
     * API health check.
     */
    public Response getHealth() {
        return getRequest(HEALTH_ENDPOINT);
    }
}
