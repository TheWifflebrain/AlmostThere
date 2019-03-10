package com.example.almostthere;

import org.json.JSONArray;
import org.json.JSONObject;

public class LocationController {
    public void withinRadius() {

    }
    
    public float getDistance(){
        float distance = 0;

        return distance;
    }

    public String makeDistanceURL (double loclat, double loclong, double destlat, double destlong ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(loclat));
        urlString.append(",");
        urlString.append(Double.toString( loclong));
        urlString.append("&destination=");// to
        urlString.append(Double.toString( destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlong));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("&key=SERVER-KEY");
        return urlString.toString();
    }

//    private JSONArray json;
//    private JSONArray legs;
//    JSONArray routeArray = json.getJSONArray(“routes”);
//    JSONObject distanceObj = legs.getJSONObject(“distance”);
//    String parsedDistance=distanceObj.getString(“text”);

}
