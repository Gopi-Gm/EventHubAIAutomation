package com.api.base;

import com.api.models.request.LoginRequest;
import com.api.models.request.RegisterRequest;
import io.restassured.response.Response;

public class AuthService extends BaseService {

    private static final String LOGIN_ENDPOINT         = "/auth/login";
    private static final String REGISTER_ENDPOINT      = "/auth/register";
    private static final String ME_ENDPOINT            = "/auth/me";

    /**
     * Login and returns the full response. The JWT token is parsed by the caller.
     */
    public Response login(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);
        return postRequest(request, LOGIN_ENDPOINT);
    }

    /**
     * Register a new user account.
     */
    public Response register(String firstName, String lastName, String email,
                             String password, String confirmPassword) {
        RegisterRequest request = new RegisterRequest(firstName, lastName, email, password, confirmPassword);
        return postRequest(request, REGISTER_ENDPOINT);
    }

    /**
     * Get the currently logged-in user's profile (requires auth token set on the spec).
     */
    public Response getMe() {
        return getRequest(ME_ENDPOINT);
    }


}
