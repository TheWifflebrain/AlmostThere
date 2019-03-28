package com.example.almostthere;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener{

    /** var for logs */
    private static final String TAG = "MapActivity";

    /** vars for google maps and its permissions */
    private GoogleMap mMap;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    /** camera vars */
    private static final float DEFAULT_ZOOM = 15f;
    Boolean setCamera = true;
    Boolean setMoveToCurrentLocation = true;

    /** widgets */
    private ImageView startPinGps;
    private ImageView endPinGps;
    private ImageView breakPin;
    private ImageView buttonSettings;
    private ImageView setPin;
    private TextView textView;

    /** locations vars */
    double newDistance = 0.0;
    MarkerOptions options = null;
    LocationAT endLocation = new LocationAT();
    LocationAT startLocation = new LocationAT();
    Destination endDestination = new Destination();
    LocationATController distanceLocationATController = new LocationATController();
    public Boolean alarmGoingOff = false;

    /** radius vars */
    public String radiusS = "0.25";
    public Double radiusD = 0.25;

    /** repeating function var */
    private Handler handler = new Handler();

    /** shared prefs vars */
    public static final String RADIUS_SETTINGS = "RADIUS_SETTINGS";
    public static final String APP_PREFS = "APPLICATION_PREFERENCES";

    /** vars for timer */
    TimerATController timerAT = new TimerATController();
    String timeItTook = "";


    /**
     * This function creates all the items that are displayed on the screen
     * such as the buttons, textViews, and displays your current location if
     * google maps services are okay
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        startPinGps = findViewById(R.id.ic_gsp);
        buttonSettings = findViewById(R.id.settingsIV);
        setPin = findViewById(R.id.ic_set);
        breakPin = findViewById(R.id.ic_break);
        endPinGps = findViewById(R.id.ic_locateFinalDestination);

        if (isServicesOK()) {
            getLocationPermission();
        }

        TextView textView = findViewById(R.id.distanceLeft);
        getRadiusD();
        textView.setText("No pin set yet.\n" + "Radius is set at: " + endDestination.getRadius() + " miles");
    }

    /**
     * This initializes this activity.
     * This makes all of the buttons and widgets on the screen workable by having their
     * particular functions being called to work.
     */
    private void init() {
        Log.d(TAG, "init: initializing");

        /**
         * The working behinds of the icon that looks like a GPS.
         */
        startPinGps.setOnClickListener(new View.OnClickListener() {
            /**
             * Moves the camera to current location and gets the location of the device.
             * @param view
             */
            @Override
            public void onClick(View view) {
                setMoveToCurrentLocation = true;
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation(setCamera = true);
            }
        });

        /**
         * The working behinds of the icon that looks like a pin or marker
         */
        endPinGps.setOnClickListener(new View.OnClickListener() {
            /**
             * Moves the camera to the end destination and discontinues updating the camera
             * to current location.
             * @param view
             */
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked endPinGps icon");
                setMoveToCurrentLocation = false;
                if(endDestination.getEndPoint().getLongitude() != 0.000000) {
                    moveCamera(new LatLng(endDestination.getEndPoint().getLatitude(), endDestination.getEndPoint().getLongitude()),
                            DEFAULT_ZOOM, "Destination Location");
                }
            }
        });

        /**
         * The working behinds of the icon that looks like a cog wheel.
         */
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            /**
             * Goes to the settings activity.
             * @param view
             */
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked settings icon");
                    Intent intent = new Intent(MapActivity.this, SettingsActivity.class);
                    startActivity(intent);
            }
        });

        /**
         * The working behinds of creating a destination pin by long clicking on the screen.
         */
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            /**
             * Creates a pin at the location where you longed clicked and sets the latitude and
             * longitude of that marker.
             * @param view
             */
            @Override
            public void onMapLongClick(LatLng view) {
                if(options == null) {
                    Log.d(TAG, "onClick: added pin");
                    options = new MarkerOptions()
                            .position(view);
                    mMap.addMarker(options);
                    Log.i(TAG, "new distance equals: " + newDistance);
                }
                else{
                    mMap.clear();
                    options = null;
                    options = new MarkerOptions()
                            .position(view);
                    mMap.addMarker(options);
                }
                endLocation.setLatitude(view.latitude);
                endLocation.setLongitude(view.longitude);
                endDestination.setEndPoint(endLocation);

                Log.i(TAG, "endLat" + endDestination.getEndPoint().getLatitude() + "    endLong:" + endDestination.getEndPoint().getLongitude());
            }
        });

        /**
         * The working behinds of the icon that looks like a double check mark.
         */
        setPin.setOnClickListener(new View.OnClickListener() {
            /**
             * Updates the distance repeatedly when clicked unless there is no marker or
             * if distance is less than radius.
             * @param view
             */
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked double check mark icon");
                if(options != null) {
                    updateDistanceUI();

                    timerAT.timer.start();
                    if (newDistance > 0.001 && newDistance > endDestination.getRadius()) {
                        handler.postDelayed(runnable, 3000);
                    }
                    else{
                        Log.d(TAG, "Stopped repeating updating distance in setPin");
                    }
                }
            }
        });

        /**
         * The working behinds of the icon that looks a minus sign inside a circle
         */
        breakPin.setOnClickListener(new View.OnClickListener() {
            /**
             * Ends updating distance, deletes the pin, and resets the latitude and longitude.
             * @param view
             */
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked minus in circle icon");
                if(options != null) {
                    timerAT.timer.cancel();
                    newDistance = 0.0;
                    options = null;
                    endDestination.getEndPoint().setLatitude(startLocation.getLatitude());
                    endDestination.getEndPoint().setLongitude(startLocation.getLongitude());
                    mMap.clear();
                    updateDistanceUI();
                    TextView textView = findViewById(R.id.distanceLeft);
                    textView.setText("Ended Calculating Distance.\nRadius is set at: " + endDestination.getRadius() + " miles");
                }
            }
        });
    }

    /**
     * Creates a function that will constantly repeat.
     */
    private Runnable runnable = new Runnable() {
        @Override
        /**
         * Checks to see when updating the distance and UI should be called.
         */
        public void run() {
            Log.i(TAG, "In the new runnable new distance: " + newDistance);

            /** Only updates the distance if the distance between pins are significant */
            if(newDistance > 0.001) {
                if(setMoveToCurrentLocation == true){
                    getDeviceLocation(setCamera = true);
                }
                else{
                    getDeviceLocation(setCamera = false);
                }

                updateDistanceUI();

                /** repeating updates if the distance between pins is greater than the radius */
                if(newDistance > endDestination.getRadius()) {
                    Log.d(TAG, "repeating updating the distance in runnable");
                    handler.postDelayed(this, 3000);
                }
                else{
                    Log.d(TAG, "stopped updating the distance in runnable");
                }
            }
        }
    };

    /**
     * Updates the text in the black box at the bottom which tells the radius, distance, and
     * when the alarm is going off.
     * Checks to see if the alarm is suppose to go off
     */
    public void updateDistanceUI(){
        newDistance =  updateDistance();
        String stringDistance = String.format("%.3f", newDistance);
        textView = findViewById(R.id.distanceLeft);
        textView.setText("Distance left to go: " + stringDistance + " miles\nRadius is set at: " + endDestination.getRadius() + " miles");

        Log.i(TAG, "radiusMapAct = " + endDestination.getRadius());

        updateAlarmUI(alarmGoingOff);
    }

    /**
     * Updates the distance left from the new starting to ending location
     * @return distance left from starting to ending location
     */
    public Double updateDistance(){
        newDistance = distanceLocationATController.calculationByDistance(startLocation.getLatitude(), startLocation.getLongitude(), endDestination.getEndPoint().getLatitude(), endDestination.getEndPoint().getLongitude());
        getRadiusD();
        Log.i(TAG, "radiusMapAct = " + endDestination.getRadius());


        return newDistance;
    }

    /**
     * Updates the textbox when alarm is going off to display that instead of distance info
     * @param alarm is the alarm going off?
     */
    public void updateAlarmUI(Boolean alarm){
        alarmGoingOff = alarm;

        alarmGoingOff = checkAlarm();
        if(alarmGoingOff == true){
            timeItTook = timerAT.timeItTookLength;
            textView.setText("Within radius. Alarm going off!\nIt took " + timeItTook);
            timerAT.timer.cancel();
        }
        else{

        }
    }

    /**
     * Checks to see if the alarm is suppose to go off
     */
    public Boolean checkAlarm(){
        alarmGoingOff = distanceLocationATController.withinRadius(newDistance, endDestination.getRadius());
        if(alarmGoingOff == true){

        }
        else{

        }
        return alarmGoingOff;
    }

    /**
     * Gets the radius from the settings page and converts it from a string to a double.
     * @return the radius in the type of double
     */
    public void getRadiusD(){
        /** Getting the radius from the settings page */
        SharedPreferences sharedPrefs = getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        String radiusSP = sharedPrefs.getString(RADIUS_SETTINGS, null);
        Log.i(TAG, "radiusSP = "+ radiusSP);

        /** converting the string into a double */
        if(radiusSP == null || radiusSP == ""){
            radiusD = .25;
        }
        else{
            radiusS = radiusSP;
            radiusD = Double.parseDouble(radiusS);
        }

        endDestination.setRadius(radiusD);
        Log.i(TAG, "radiusMapAct = " + endDestination.getRadius());
    }

    /**
     * Gets the current location if possible and moves the camera to the current location while
     * setting the start latitude and longitude
     * @param setCamera boolean to check if the camera is suppose to zoom in on the current location
     */
    private void getDeviceLocation(final Boolean setCamera) {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        /** checking if you can access current location */
        try {
            if (mLocationPermissionsGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    /**
                     * Updating the current location and resetting the latitude and longitude
                     * @param task updating distance
                     */
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            startLocation.setLongitude(currentLocation.getLongitude());
                            startLocation.setLatitude(currentLocation.getLatitude());

                            if(setCamera == true) {
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                        DEFAULT_ZOOM,
                                        "My Location");
                            }
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    /**
     * Moves the camera to location it is specified to go to
     * @param latLng Location of pin
     * @param zoom how close the screen is zoomed in on the pin
     * @param title what the pin is called
     */
    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    /**
     * Initializes the google map to display on the screen
     */
    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }

    /**
     * Checks to see if google maps cannot connect to servers or load
     * @param connectionResult connection cannot connect to server or load
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: map failed");
    }

    /**
     * Sets up the map being displayed on the screen by google maps
     * @param googleMap the map by google
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation(setCamera = true);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            init();
        }
    }

    /**
     * Checking if the services provided by google are up and the app can connect
     * @return if the map can connect to the services by google
     */
    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            /** everything is fine and the user can make map requests */
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            /** an error occurred but we can resolve it */
            Log.d(TAG, "isServicesOK: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /**
     * Checking to see if the app has the permission to use the GPS location service
     */
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Callback for the result from requesting permissions
     * @param requestCode The request code passed in requestPermissions
     * @param permissions The requested permissions
     * @param grantResults The grant results for PERMISSION_GRANTED or PERMISSION_DENIED permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                /** permission was granted */
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    initMap();
                }
            }
        }
    }
}

