package com.api.context;

import io.restassured.response.Response;

/**
 * ScenarioContext holds shared mutable state for a single Cucumber scenario.
 *
 * One instance is created per scenario by PicoContainer and injected into
 * every step-definition class that declares it as a constructor parameter.
 * This replaces static fields and avoids cross-scenario pollution.
 */
public class ScenarioContext {

    /** JWT access token — set by login steps, consumed by all authenticated steps. */
    private String jwtToken;

    /** ID of the most recently created event — shared across Event scenario steps. */
    private String createdEventId;

    /** ID of the most recently created booking — shared across Booking scenario steps. */
    private String createdBookingId;

    /** Last RestAssured response — stored by action steps, read by assertion steps. */
    private Response lastResponse;

    // ------------------------------------------------------------------ //

    public String getJwtToken() { return jwtToken; }
    public void setJwtToken(String jwtToken) { this.jwtToken = jwtToken; }

    public String getCreatedEventId() { return createdEventId; }
    public void setCreatedEventId(String createdEventId) { this.createdEventId = createdEventId; }

    public String getCreatedBookingId() { return createdBookingId; }
    public void setCreatedBookingId(String createdBookingId) { this.createdBookingId = createdBookingId; }

    public Response getLastResponse() { return lastResponse; }
    public void setLastResponse(Response lastResponse) { this.lastResponse = lastResponse; }
}
