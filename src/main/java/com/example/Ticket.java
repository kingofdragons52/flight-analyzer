package com.example;

public class Ticket {
    private String origin;
    private String destination;
    private String carrier;
    private String departure_time;
    private String arrival_time;
    private double price;

    public String getOrigin() {
        return origin;
    }
    public String getDestination() {
        return destination;
    }
    public String getCarrier() {
        return carrier;
    }
    public String getDeparture_time() {
        return departure_time;
    }
    public String getArrival_time() {
        return arrival_time;
    }
    public double getPrice() {
        return price;
    }
}