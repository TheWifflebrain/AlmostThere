package com.example.almostthere;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

public class MyLocationService extends BroadcastReceiver {
    public static final String ACTION_PROCESS_UPDATE = "com.example.almostthere.UPDATE_LOCATION";

    @Override
    public void onReceive(Context context, Intent intent){
        if(intent!=null){
            final String action = intent.getAction();
            if(ACTION_PROCESS_UPDATE.equals(action)){
                LocationResult result = LocationResult.extractResult(intent);
                if(result != null){

                    try{
                        MapActivity.getInstance().getDeviceLocation(true);
                        MapActivity.getInstance().updateDistanceUI();
                        MapActivity.getInstance().updateDistance();
                        boolean alarm = MapActivity.getInstance().checkAlarm();
                        MapActivity.getInstance().updateAlarmUI(alarm);

                    }catch(Exception e){
                        Log.d("LocationService", "MyLocationService not working");
                    }
                }
            }
        }
    }
}
