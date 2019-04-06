package com.example.almostthere;

import org.junit.Test;

import java.text.DecimalFormat;

import static junit.framework.TestCase.assertEquals;

public class TimerATControllerTest {
    long totalSeconds = 86400;/** one day in seconds */
    long timeItTook = 0;
    long timeItTookSec = 0;
    long timeItTookMin = 0;
    long timeItTookHour = 0;
    String timeItTookLength = "";
    DecimalFormat df = new DecimalFormat("#00");

    /**
     * Could not figure out how to use CountDownTimer timer = new CountDownTimer(totalSeconds * 1000, intervalSeconds * 1000)
     * in a test case. You cannot just test the onTick(mili) function by itself
     * So that is why it is not test.onTick();
     * Just used the code from onTick and put it in the function
     */
    @Test
    public void checkTimer(){
        /** Testing 50 seconds (50000 milliseconds = 50 seconds) */
        timeItTook = ((totalSeconds * 1000 - 50000) / 1000);
        timeItTookSec = timeItTook%60;
        timeItTookHour = timeItTook/60;
        timeItTookMin = timeItTookHour%60;
        timeItTookHour = timeItTookHour/60;

        /** One day minus 50 seconds */
        timeItTookLength = (df.format(timeItTookHour) + ":" + df.format(timeItTookMin) + "::" + df.format(timeItTookSec));
        String time = "23:59::10";
        assertEquals(time, timeItTookLength);
    }

    /**
     * Could not figure out how to use CountDownTimer timer = new CountDownTimer(totalSeconds * 1000, intervalSeconds * 1000)
     * in a test case. You cannot just test the onTick(mili) function by itself
     * So that is why it is not test.onTick();
     * Just used the code from onTick and put it in the function
     */
    @Test
    public void checkTimer2(){
        /** Testing 50 seconds (2000000 milliseconds = 2000 seconds) */
        timeItTook = ((totalSeconds * 1000 - 2000000) / 1000);
        timeItTookSec = timeItTook%60;
        timeItTookHour = timeItTook/60;
        timeItTookMin = timeItTookHour%60;
        timeItTookHour = timeItTookHour/60;

        /** One day minus 2000 seconds */
        timeItTookLength = (df.format(timeItTookHour) + ":" + df.format(timeItTookMin) + "::" + df.format(timeItTookSec));
        String time = "23:26::40";
        assertEquals(time, timeItTookLength);
    }
}
