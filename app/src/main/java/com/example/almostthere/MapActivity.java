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

    /** locations vars */
    private double startLongitude;
    private double startLatitude;
    private double endLongitude;
    private double endLatitude;
    double newDistance = 0.0;
    MarkerOptions options = null;

    /** radius vars */
    public String radiusS = "0.25";
    public Double radiusD = 0.25;

    /** repeating function var */
    private Handler handler = new Handler();

    /** shared prefs vars */
    public static final String RADIUS_SETTINGS = "RADIUS_SETTINGS";
    public static final String APP_PREFS = "APPLICATION_PREFERENCES";


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

        startPinGps = (ImageView) findViewById(R.id.ic_gsp);
        buttonSettings = (ImageView) findViewById(R.id.settingsIV);
        setPin = (ImageView) findViewById(R.id.ic_set);
        breakPin = (ImageView) findViewById(R.id.ic_break);
        endPinGps = (ImageView) findViewById(R.id.ic_locateFinalDestination);

        if (isServicesOK()) {
            getLocationPermission();
        }

        TextView textView = (TextView) findViewById(R.id.distanceLeft);
        radiusD = getRadiusD();
        textView.setText("No pin set yet.\n" + "Radius is set at: " + radiusD + " miles");
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
                if(endLongitude != 0.000000) {
                    moveCamera(new LatLng(endLatitude, endLongitude),
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
                endLatitude = view.latitude;
                endLongitude = view.longitude;

                Log.i(TAG, "endLat" + endLatitude + "    endLong:" + endLongitude);
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
                    if (newDistance > 0.001 && newDistance > radiusD) {
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
                    newDistance = 0.0;
                    options = null;
                    endLatitude = startLatitude;
                    endLongitude = startLongitude;
                    mMap.clear();
                    updateDistanceUI();
                    TextView textView = (TextView) findViewById(R.id.distanceLeft);
                    textView.setText("Ended Calculating Distance.\nRadius is set at: " + radiusD + " miles");
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
                if(newDistance > radiusD) {
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
        newDistance = CalculationByDistance(startLatitude, startLongitude, endLatitude, endLongitude);
        String stringDistance = String.format("%.3f", newDistance);
        TextView textView = (TextView) findViewById(R.id.distanceLeft);
        textView.setText("Distance left to go: " + stringDistance + " miles\nRadius is set at: " + radiusD + " miles");

        radiusD = getRadiusD();

        Log.i(TAG, "radiusMapAct = " + radiusD);

        if(newDistance <= radiusD){
            /** Alarm set off */

            textView.setText("Within radius. Alarm going off!");
        }
    }

    /**
     * Gets the radius from the settings page and converts it from a string to a double.
     * @return the radius in the type of double
     */
    public Double getRadiusD(){
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
        Log.i(TAG, "radiusMapAct = " + radiusD);

        return radiusD;
    }

    /**
     * Calculating the distance between two pins
     * @param startLat latitude for the starting location
     * @param startLong longitude for the starting location
     * @param endLat latitude for the ending location
     * @param endLong longitude for the ending location
     * @return distance from each pin in miles
     */
    public double CalculationByDistance(double startLat, double startLong, double endLat, double endLong) {
        int Radius=6371; /** radius of earth in Km */
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
                            currentLocation.setLongitude(currentLocation.getLongitude());
                            currentLocation.setLatitude(currentLocation.getLatitude());
                            startLatitude = currentLocation.getLatitude();
                            startLongitude = currentLocation.getLongitude();
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

