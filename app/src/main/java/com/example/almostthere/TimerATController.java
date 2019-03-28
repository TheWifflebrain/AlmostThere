package com.example.almostthere;

import android.os.CountDownTimer;
import android.util.Log;

import java.text.DecimalFormat;

public class TimerATController {

    /** vars for logs */
    private static final String TAG = "TIMER CONTROLLER";

    /** vars for keeping time */
    long totalSeconds = 86400;/** one day in seconds */
    long intervalSeconds = 1;
    long timeItTook = 0;
    long timeItTookSec = 0;
    long timeItTookMin = 0;
    long timeItTookHour = 0;
    String timeItTookLength = "";
    DecimalFormat df = new DecimalFormat("#00");
    /**
     * A timer that counts up and counts up to a day
     */
    CountDownTimer timer = new CountDownTimer(totalSeconds * 1000, intervalSeconds * 1000) {
        /**
         * It counts up from 0 to 86400 which is one day in seconds
         * @param millisUntilFinished the milliseconds until the timer is finished
         */
        public void onTick(long millisUntilFinished) {
            Log.d(TAG, "seconds elapsed: " + String.valueOf((totalSeconds * 1000 - millisUntilFinished) / 1000));
            timeItTook = ((totalSeconds * 1000 - millisUntilFinished) / 1000);

            /** formatting time to 00:00::00 */
            timeItTookSec = timeItTook%60;
            timeItTookHour = timeItTook/60;
            timeItTookMin = timeItTookHour%60;
            timeItTookHour = timeItTookHour/60;

            Log.d(TAG, "time it took: " + timeItTookLength);
            timeItTookLength = (df.format(timeItTookHour) + ":" + df.format(timeItTookMin) + "::" + df.format(timeItTookSec));
        }

        /**
         * Lets the user know it the timer is up
         */
        public void onFinish() {
            Log.d( TAG, "DONE: Time's up!");
        }
    };
}
