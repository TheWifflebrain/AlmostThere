package com.example.almostthere;

import android.media.RingtoneManager;
import android.net.Uri;

import org.junit.Test;
import static org.junit.Assert.*;

public class MapActivityTest {

    /**
     * This is the first half of the checkAlarm() in the MapActivity
     * Could only test the first half so that is why Boolean result = test.checkAlarm();
     * was not used.
     * Should return false for both
     */
    @Test
    public void checkAlarm(){
        Boolean alarmGoingOff = false;
        Boolean foundAlarm = false;
        if(alarmGoingOff){
            Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            foundAlarm = true;
            if (alert == null) {
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                if (alert == null)
                    alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        assertEquals(alarmGoingOff, foundAlarm);
    }

    /**
     * This is the first half of the checkAlarm() in the MapActivity
     * Could only test the first half so that is why Boolean result = test.checkAlarm();
     * was not used.
     * Should return true for both
     */
    @Test
    public void checkAlarm2(){
        Boolean alarmGoingOff = true;
        Boolean foundAlarm = false;
        if(alarmGoingOff){
            Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            foundAlarm = true;
            if (alert == null) {
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                if (alert == null)
                    alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        assertEquals(alarmGoingOff, foundAlarm);
    }
}