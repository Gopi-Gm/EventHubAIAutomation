package com.api.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request model for creating / updating an event.
 *
 * @JsonProperty annotations map Java field names to the API's expected JSON field names:
 *   Java field   →  JSON field sent to API
 *   ----------      ----------------------
 *   location     →  venue        (API requires "venue")
 *   capacity     →  totalSeats   (API requires "totalSeats")
 *   image        →  imageUrl     (API requires "imageUrl")
 */
public class EventRequest {

    private String title;
    private String description;
    private String category;
    private String eventDate;
    private String eventTime;

    @JsonProperty("venue")
    private String location;

    private String city;

    @JsonProperty("totalSeats")
    private int capacity;

    private double price;

    @JsonProperty("imageUrl")
    private String image;

    public EventRequest() {
    }

    public EventRequest(String title, String description, String category, String eventDate,
                        String eventTime, String location, String city, int capacity,
                        double price, String image) {
        this.title       = title;
        this.description = description;
        this.category    = category;
        this.eventDate   = eventDate;
        this.eventTime   = eventTime;
        this.location    = location;
        this.city        = city;
        this.capacity    = capacity;
        this.price       = price;
        this.image       = image;
    }

    public String getTitle()                   { return title; }
    public void   setTitle(String title)       { this.title = title; }

    public String getDescription()                      { return description; }
    public void   setDescription(String description)    { this.description = description; }

    public String getCategory()                    { return category; }
    public void   setCategory(String category)     { this.category = category; }

    public String getEventDate()                     { return eventDate; }
    public void   setEventDate(String eventDate)     { this.eventDate = eventDate; }

    public String getEventTime()                     { return eventTime; }
    public void   setEventTime(String eventTime)     { this.eventTime = eventTime; }

    @JsonProperty("venue")
    public String getLocation()                    { return location; }
    public void   setLocation(String location)     { this.location = location; }

    public String getCity()              { return city; }
    public void   setCity(String city)   { this.city = city; }

    @JsonProperty("totalSeats")
    public int  getCapacity()              { return capacity; }
    public void setCapacity(int capacity)  { this.capacity = capacity; }

    public double getPrice()              { return price; }
    public void   setPrice(double price)  { this.price = price; }

    @JsonProperty("imageUrl")
    public String getImage()             { return image; }
    public void   setImage(String image) { this.image = image; }
}
