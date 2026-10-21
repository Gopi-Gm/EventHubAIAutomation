package com.api.steps;

import com.api.context.ScenarioContext;
import com.api.models.request.BookingRequest;
import com.api.models.request.EventRequest;
import com.api.base.BookingService;
import com.api.base.EventService;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;

/**
 * Step definitions for the Booking Management feature (bookings.feature).
 *
 * Creates a temporary event in the "Given an event exists to book" step,
 * then performs booking operations against that event ID.
 */
public class BookingSteps {

    private final ScenarioContext context;
    private final BookingService bookingService;
    private final EventService eventService;

    public BookingSteps(ScenarioContext context) {
        this.context = context;
        this.bookingService = new BookingService();
        this.eventService = new EventService();
    }

    // ------------------------------------------------------------------ //
    //  Helper — apply JWT token before each service call
    // ------------------------------------------------------------------ //

    private void applyToken() {
        String token = context.getJwtToken();
        if (token != null) {
            bookingService.setAuthToken(token);
            eventService.setAuthToken(token);
        }
    }

    // ------------------------------------------------------------------ //
    //  Given — pre-conditions
    // ------------------------------------------------------------------ //

    @Given("an event exists to book")
    public void anEventExistsToBook() {
        applyToken();

        // Try to reuse a previously created event ID from the context
        if (context.getCreatedEventId() != null) {
            return;
        }

        // Otherwise create a fresh test event
        EventRequest eventRequest = new EventRequest(
                "Booking Test Event",
                "Temporary event created for booking tests",
                "Technology",
                "2027-12-25",
                "10:00",
                "New York Convention Center",
                "New York",
                100,
                49.99,
                "https://example.com/booking-test.jpg"
        );
        Response response = eventService.createEvent(eventRequest);
        com.api.utility.AssertionUtils.assertStatusCode(response, 200);

        String eventId = com.api.utility.JsonUtils.extractId(response);
        Assert.assertNotNull(eventId, "Event ID must not be null after creation");
        context.setCreatedEventId(eventId);
    }

    // ------------------------------------------------------------------ //
    //  When — Booking actions
    // ------------------------------------------------------------------ //

    @When("I request all bookings")
    public void iRequestAllBookings() {
        applyToken();
        Response response = bookingService.getAllBookings();
        context.setLastResponse(response);
    }

    @When("I create a booking for {int} tickets")
    public void iCreateABookingForTickets(int numberOfTickets) {
        applyToken();
        String eventId = context.getCreatedEventId();
        Assert.assertNotNull(eventId, "No event ID in context — ensure 'an event exists to book' ran first");

        double totalPrice = com.api.utility.DataUtils.roundPrice(numberOfTickets * 49.99);
        BookingRequest request = new BookingRequest(eventId, "Test User", "testuser@example.com", "9876543210", numberOfTickets, totalPrice);
        Response response = bookingService.createBooking(request);
        context.setLastResponse(response);
    }

    @And("the booking is created successfully")
    public void theBookingIsCreatedSuccessfully() {
        Response response = context.getLastResponse();
        Assert.assertNotNull(response, "No response stored in ScenarioContext");

        String bookingId = com.api.utility.JsonUtils.extractId(response);
        Assert.assertNotNull(bookingId, "Booking ID should not be null in the creation response");
        context.setCreatedBookingId(bookingId);
    }

    @When("I update the booking to {int} tickets")
    public void iUpdateTheBookingToTickets(int numberOfTickets) {
        applyToken();
        String bookingId = context.getCreatedBookingId();
        Assert.assertNotNull(bookingId, "No booking ID found in ScenarioContext");

        String eventId = context.getCreatedEventId();
        double totalPrice = com.api.utility.DataUtils.roundPrice(numberOfTickets * 49.99);
        BookingRequest request = new BookingRequest(eventId, "Test User", "testuser@example.com", "9876543210", numberOfTickets, totalPrice);
        Response response = bookingService.updateBooking(bookingId, request);
        context.setLastResponse(response);
    }

    @When("I cancel the booking")
    public void iCancelTheBooking() {
        applyToken();
        String bookingId = context.getCreatedBookingId();
        Assert.assertNotNull(bookingId, "No booking ID found in ScenarioContext");
        Response response = bookingService.cancelBooking(bookingId);
        context.setLastResponse(response);
    }

    @When("I fetch the created booking by ID")
    public void iFetchTheCreatedBookingById() {
        applyToken();
        String bookingId = context.getCreatedBookingId();
        Assert.assertNotNull(bookingId, "No booking ID found in ScenarioContext");
        Response response = bookingService.getBookingById(bookingId);
        context.setLastResponse(response);
    }

    @When("I fetch booking by ID {string}")
    public void iFetchBookingById(String bookingId) {
        applyToken();
        Response response = bookingService.getBookingById(bookingId);
        context.setLastResponse(response);
    }
}
