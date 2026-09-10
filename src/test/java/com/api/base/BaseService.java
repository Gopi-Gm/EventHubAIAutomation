package com.api.base;

import java.security.PrivateKey;

import com.api.filters.LoggingFilters;
import com.api.models.request.LoginRequest;

import static io.restassured.RestAssured.*;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class BaseService {
	
	// this is wrapper for restassured
	// this is stores base url
	//creating the request
	// handling the response (using this baservice we can able to do these action)
	
	// constant are allways return uppercase, if some thing is constant that will be final class, and final also vcome with static
	// staic is only for variable, not the method
	
	private  static final String BASE_URL="http://64.227.160.186:8080";
	
	private RequestSpecification requestspecification; //instance variable
	
	
   static	{
		RestAssured.filters(new LoggingFilters()); // this is java entity it will execute only once
	}
	
	public BaseService() {   // constructor- it's initilize the instance variable

		requestspecification = given().baseUri(BASE_URL);
	
	}
	
	
	protected void setAuthToken(String token) {
		requestspecification.header("Authorization", "Bearer " + token);
		
	}
	
	protected Response postRequest(Object payload , String endpoint) {
		return requestspecification.contentType(ContentType.JSON).body(payload).post(endpoint);
		
	}
	
	
	protected Response putRequest(Object payload , String endpoint) {
		return requestspecification.contentType(ContentType.JSON).body(payload).put(endpoint);
		
	}
	
	protected Response getRequest(String endpoint) {
		return requestspecification.get(endpoint);
		
	}
	
	protected Response postRequest(String baseurl ,Object payload, String endpoint) {
		return requestspecification.baseUri(baseurl).contentType(ContentType.JSON).body(payload).post(endpoint);
		
	}
	
	
	

	
	

}
