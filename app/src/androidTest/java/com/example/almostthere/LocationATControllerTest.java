package com.example.almostthere;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class LocationATControllerTest {

    /**
     * Checks if the withinRadius function works as it should
     */
    @Test
    public void withinRadius() {
        LocationATController test = new LocationATController();
        double distance = 500.0;
        double radius = 300.0;
        Boolean result = test.withinRadius(distance, radius);
        assertEquals(result, false);
    }

    /**
     * Checks if the withinRadius function works as it should
     */
    @Test
    public void withinRadius2() {
        LocationATController test2 = new LocationATController();
        double distance = 300.0;
        double radius = 500.0;
        Boolean result2 = test2.withinRadius(distance, radius);
        assertEquals(result2, true);
    }

    /**
     * Checks if the calculationByDistance() function works as it should
     */
    @Test
    public void calculationByDistance() {
        LocationATController test3 = new LocationATController();
        double startLat = 37.566;
        double startLong = 126.9784;
        double endLat = 35.6895;
        double endLong = 139.6917;

        double distance = test3.calculationByDistance(startLat, startLong, endLat, endLong);

        assertEquals(distance, 716, 1.0);
    }

}