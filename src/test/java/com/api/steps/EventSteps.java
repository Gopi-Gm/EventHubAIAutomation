package com.api.steps;

import com.api.context.ScenarioContext;
import com.api.models.request.EventRequest;

import com.api.base.EventService;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;

/**
 * Step definitions for the Event Management feature (events.feature).
 *
 * EventService, CategoryService and CityService are instantiated here
 * and configured with the JWT token from ScenarioContext after the
 * Background login step runs.
 */
public class EventSteps {

    private final ScenarioContext context;
    private final EventService eventService;


    public EventSteps(ScenarioContext context) {
        this.context = context;
        this.eventService = new EventService();

    }

    // ------------------------------------------------------------------ //
    //  Helper — apply JWT token to all services before each action
    // ------------------------------------------------------------------ //

    private void applyToken() {
        String token = context.getJwtToken();
        if (token != null) {
            eventService.setAuthToken(token);

        }
    }

    // ------------------------------------------------------------------ //
    //  When — Event actions
    // ------------------------------------------------------------------ //

    @When("I request all events")
    public void iRequestAllEvents() {
        applyToken();
        Response response = eventService.getAllEvents();
        context.setLastResponse(response);
    }

    @When("I create an event with title {string} in city {string}")
    public void iCreateAnEventWithTitleInCity(String title, String city) {
        applyToken();
        EventRequest request = new EventRequest(
                title,
                "Automated test event — " + title,
                "Technology",
                "2027-12-20",
                "09:00",
                city + " Convention Center",
                city,
                500,
                99.99,
                "https://example.com/test-image.jpg"
        );
        Response response = eventService.createEvent(request);
        context.setLastResponse(response);
    }

    @And("the event is created successfully")
    public void theEventIsCreatedSuccessfully() {
        Response response = context.getLastResponse();
        Assert.assertNotNull(response, "No response stored in ScenarioContext");

        String eventId = com.api.utility.JsonUtils.extractId(response);
        Assert.assertNotNull(eventId,
                "Created event ID should not be null in response. Body: " + response.getBody().asString());
        context.setCreatedEventId(eventId);
    }

    @When("I update the event title to {string}")
    public void iUpdateTheEventTitleTo(String newTitle) {
        applyToken();
        String eventId = context.getCreatedEventId();
        Assert.assertNotNull(eventId, "No created event ID found in ScenarioContext");

        EventRequest updated = new EventRequest(
                newTitle,
                "Updated automated test event",
                "Technology",
                "2027-12-20",
                "09:00",
                "San Francisco Convention Center",
                "San Francisco",
                600,
                119.99,
                "https://example.com/updated-image.jpg"
        );
        Response response = eventService.updateEvent(eventId, updated);
        context.setLastResponse(response);
    }

    @When("I delete the created event")
    public void iDeleteTheCreatedEvent() {
        applyToken();
        String eventId = context.getCreatedEventId();
        Assert.assertNotNull(eventId, "No created event ID found in ScenarioContext");
        Response response = eventService.deleteEvent(eventId);
        context.setLastResponse(response);
    }

    @When("I fetch the created event by ID")
    public void iFetchTheCreatedEventById() {
        applyToken();
        String eventId = context.getCreatedEventId();
        Assert.assertNotNull(eventId, "No created event ID found in ScenarioContext");
        Response response = eventService.getEventById(eventId);
        context.setLastResponse(response);
    }

    @When("I fetch event by ID {string}")
    public void iFetchEventById(String eventId) {
        applyToken();
        Response response = eventService.getEventById(eventId);
        context.setLastResponse(response);
    }

    @When("I search events with query {string}")
    public void iSearchEventsWithQuery(String query) {
        applyToken();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}
        Response response = eventService.searchEvents(query);
        String body = response.getBody().asString();
        if (response.getStatusCode() == 200 && (body.contains("\"data\":[]") || body.contains("\"data\": []") || body.contains("\"total\":0"))) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ignored) {}
            response = eventService.searchEvents(query);
        }
        context.setLastResponse(response);
    }

    @When("I get events filtered by category {string}")
    public void iGetEventsFilteredByCategory(String category) {
        applyToken();
        Response response = eventService.getEventsByCategory(category);
        context.setLastResponse(response);
    }

    @When("I get events filtered by city {string}")
    public void iGetEventsFilteredByCity(String city) {
        applyToken();
        Response response = eventService.getEventsByCity(city);
        context.setLastResponse(response);
    }


}
