package com.api.base;

import com.api.models.request.BookingRequest;
import io.restassured.response.Response;

public class BookingService extends BaseService {

    private static final String BOOKINGS_ENDPOINT       = "/bookings";
    private static final String BOOKING_BY_ID_ENDPOINT  = "/bookings/{bookingId}";
    private static final String BOOKING_BY_REF_ENDPOINT = "/bookings/ref/{ref}";

    /**
     * Retrieve all bookings for the authenticated user.
     */
    public Response getAllBookings() {
        return getRequest(BOOKINGS_ENDPOINT);
    }

    /**
     * Get a specific booking by its ID.
     */
    public Response getBookingById(String bookingId) {
        return getRequest(BOOKING_BY_ID_ENDPOINT.replace("{bookingId}", bookingId));
    }

    /**
     * Create a new booking for an event.
     */
    public Response createBooking(BookingRequest bookingRequest) {
        return postRequest(bookingRequest, BOOKINGS_ENDPOINT);
    }

    /**
     * Update an existing booking (e.g., change number of tickets).
     */
    public Response updateBooking(String bookingId, BookingRequest bookingRequest) {
        return putRequest(bookingRequest, BOOKING_BY_ID_ENDPOINT.replace("{bookingId}", bookingId));
    }

    /**
     * Look up a booking by its reference code.
     */
    public Response getBookingByRef(String ref) {
        return getRequest(BOOKING_BY_REF_ENDPOINT.replace("{ref}", ref));
    }

    /**
     * Cancel (delete) a booking by ID.
     */
    public Response cancelBooking(String bookingId) {
        return deleteRequest(BOOKING_BY_ID_ENDPOINT.replace("{bookingId}", bookingId));
    }
}
