package com.example.almostthere;

public class Destination {
    private Location endPoint;
    private float radius;

    public Destination(Location endPoint, float radius) {
        this.endPoint = endPoint;
        this.radius = radius;
    }

    public Location getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Location endPoint) {
        this.endPoint = endPoint;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}