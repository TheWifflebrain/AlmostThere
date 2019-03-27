package com.example.almostthere;

public class Destination {
    private LocationAT endPoint;
    private double endRadius;
    private String destAddress;

    public Destination(LocationAT endPoint, double radius) {
        this.endPoint = endPoint;
        this.endRadius = radius;
    }

    public Destination() { }

    public LocationAT getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(LocationAT endPoint) {
        this.endPoint = endPoint;
    }

    public double getRadius() {
        return endRadius;
    }

    public void setRadius(double radius) { this.endRadius = radius; }

    public String getAddress() {
        return destAddress;
    }

    public void setAddress(String address) {
        this.destAddress = address;
    }


}