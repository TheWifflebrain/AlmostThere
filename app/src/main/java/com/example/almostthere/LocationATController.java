package com.example.almostthere;

import android.util.Log;

public class LocationATController {

    private static final String TAG = "LocationAtController";

    /**
     * Checks to see if the alarm should be going off by checking if the distance is within the
     * radius or not
     * @param distance distance to end location from starting location
     * @param radius the radius that the user set
     * @return if distance is within radius or not
     */
    public Boolean withinRadius(double distance, double radius) {
        Log.i(TAG,"LocationAtController: distance:  " + distance + "     radius:  " + radius);
        if(distance <= radius){
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Calculating the distance between two pins
     * @param startLat latitude for the starting location
     * @param startLong longitude for the starting location
     * @param endLat latitude for the ending location
     * @param endLong longitude for the ending location
     * @return distance from each pin in miles
     */
    public double calculationByDistance(double startLat, double startLong, double endLat, double endLong) {
        int Radius = 6371; /** radius of earth in Km */
        double dLat = Math.toRadians(endLat-startLat);
        double dLon = Math.toRadians(endLong-startLong);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(startLat)) * Math.cos(Math.toRadians(endLat)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double distMiles = (Radius*c)/1.609; /** dividing by 1.609 to go from km to miles */
        Log.i("Radius Value",+ distMiles + "  in miles.");

        return distMiles;
    }

}
