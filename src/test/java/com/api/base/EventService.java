package com.api.base;

import com.api.models.request.EventRequest;
import io.restassured.response.Response;

public class EventService extends BaseService {

    private static final String EVENTS_ENDPOINT        = "/events";
    private static final String EVENT_BY_ID_ENDPOINT   = "/events/{eventId}";

    /**
     * Retrieve all events (optionally paginated via query params).
     */
    public Response getAllEvents() {
        return getRequest(EVENTS_ENDPOINT);
    }

    /**
     * Get a single event by its ID.
     */
    public Response getEventById(String eventId) {
        return getRequest(EVENT_BY_ID_ENDPOINT.replace("{eventId}", eventId));
    }

    /**
     * Create a new event.
     */
    public Response createEvent(EventRequest eventRequest) {
        return postRequest(eventRequest, EVENTS_ENDPOINT);
    }

    /**
     * Update an existing event.
     */
    public Response updateEvent(String eventId, EventRequest eventRequest) {
        return putRequest(eventRequest, EVENT_BY_ID_ENDPOINT.replace("{eventId}", eventId));
    }

    /**
     * Delete an event by ID.
     */
    public Response deleteEvent(String eventId) {
        return deleteRequest(EVENT_BY_ID_ENDPOINT.replace("{eventId}", eventId));
    }

    /**
     * Search events by query string (/events?search=...).
     */
    public Response searchEvents(String query) {
        return getRequest(EVENTS_ENDPOINT + "?search=" + query);
    }

    /**
     * Filter events by category (/events?category=...).
     */
    public Response getEventsByCategory(String category) {
        return getRequest(EVENTS_ENDPOINT + "?category=" + category);
    }

    /**
     * Filter events by city (/events?city=...).
     */
    public Response getEventsByCity(String city) {
        return getRequest(EVENTS_ENDPOINT + "?city=" + city);
    }
}
