package com.api.base;

import com.api.filters.LoggingFilters;
import static io.restassured.RestAssured.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class BaseService {

    private static final String BASE_URL = com.api.utility.ConfigReader.getBaseUrl();
    private String authToken;

    static {
        RestAssured.filters(new LoggingFilters());
    }

    public BaseService() {}

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    private RequestSpecification buildRequest() {
        RequestSpecification spec = given().baseUri(BASE_URL).contentType(ContentType.JSON);
        if (this.authToken != null) {
            spec.header("Authorization", "Bearer " + this.authToken);
        }
        return spec;
    }

    protected Response postRequest(Object payload, String endpoint) {
        return buildRequest().body(payload).post(endpoint);
    }

    protected Response putRequest(Object payload, String endpoint) {
        return buildRequest().body(payload).put(endpoint);
    }

    protected Response getRequest(String endpoint) {
        return buildRequest().get(endpoint);
    }

    protected Response postRequest(String baseurl, Object payload, String endpoint) {
        return given().baseUri(baseurl).contentType(ContentType.JSON).body(payload).post(endpoint);
    }

    protected Response deleteRequest(String endpoint) {
        return buildRequest().delete(endpoint);
    }
}
