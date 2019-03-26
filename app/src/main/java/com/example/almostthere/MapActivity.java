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

    //var for logs
    private static final String TAG = "MapActivity";

    //vars for google maps and its permissions
    private GoogleMap mMap;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    //camera vars
    private static final float DEFAULT_ZOOM = 15f;
    Boolean setCamera = true;
    Boolean setMoveToCurrentLocation = true;

    //widgets
    private ImageView startPinGps;
    private ImageView endPinGps;
    private ImageView breakPin;
    private ImageView buttonSettings;
    private ImageView setPin;

    //locations vars
    private double startLongitude;
    private double startLatitude;
    private double endLongitude;
    private double endLatitude;
    double newDistance = 0.0;
    MarkerOptions options = null;

    //radius vars
    public String radiusS = "0.25";
    public Double radiusD = 0.25;

    //repeating function var
    private Handler handler = new Handler();

    //shared prefs vars
    public static final String RADIUS_SETTINGS = "RADIUS_SETTINGS";
    public static final String APP_PREFS = "APPLICATION_PREFERENCES";

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

    private void init() {
        Log.d(TAG, "init: initializing");

        startPinGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMoveToCurrentLocation = true;
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation(setCamera = true);

            }
        });

        endPinGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked dgps icon");
                setMoveToCurrentLocation = false;
                if(endLongitude != 0.000000) {
                    moveCamera(new LatLng(endLatitude, endLongitude),
                            DEFAULT_ZOOM,
                            "Desination Location");
                }
            }
        });

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked settings icon");
                    Intent intent = new Intent(MapActivity.this, SettingsActivity.class);
                    startActivity(intent);

            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng view) {
                if(options == null) {
                    Log.d(TAG, "onClick: added pin");
                    options = new MarkerOptions()
                            .position(view);
                    mMap.addMarker(options);
                    Log.d(TAG, "new distance equals: " + newDistance);
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

                Log.d(TAG, "endLat" + endLatitude + "    endLong:" + endLongitude);

            }

        });

        setPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked distance icon");
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

        breakPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked break pin icon");
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

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "Actually in the new runnable new distance: " + newDistance);

            if(newDistance > 0.001) {
                if(setMoveToCurrentLocation == true){
                    getDeviceLocation(setCamera = true);
                }
                else{
                    getDeviceLocation(setCamera = false);
                }
                updateDistanceUI();

                if(newDistance > radiusD && newDistance > 0.0001) {
                    Log.d(TAG, "repeating function");
                    handler.postDelayed(this, 3000);
                }
                else{
                    Log.d(TAG, "stopped updating the distance in runnable");
                }
            }
        }
    };

    public void updateDistanceUI(){
        newDistance = CalculationByDistance(startLatitude, startLongitude, endLatitude, endLongitude);
        String stringDistance = String.format("%.3f", newDistance);
        TextView textView = (TextView) findViewById(R.id.distanceLeft);
        textView.setText("Distance left to go: " + stringDistance + " miles\nRadius is set at: " + radiusD + " miles");

        radiusD = getRadiusD();

        Log.d(TAG, "radiusMapAct = " + radiusD);

        if(newDistance <= radiusD){
            //Alarm set off

            textView.setText("Within radius. Alarm going off!");
        }
    }

    public Double getRadiusD(){
        SharedPreferences sharedPrefs = getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        String radiusSP = sharedPrefs.getString(RADIUS_SETTINGS, null);
        Log.d(TAG, "radiusSP = "+ radiusSP);
        if(radiusSP == null || radiusSP == ""){
            radiusD = .25;
        }
        else{
            radiusS = radiusSP;
            radiusD = Double.parseDouble(radiusS);
        }
        Log.d(TAG, "radiusMapAct = " + radiusD);

        return radiusD;
    }

    public double CalculationByDistance(double startLat, double startLong, double endLat, double endLong) {
        //radius of earth in Km
        int Radius=6371;
        double dLat = Math.toRadians(endLat-startLat);
        double dLon = Math.toRadians(endLong-startLong);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(startLat)) * Math.cos(Math.toRadians(endLat)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        //dividing by 1.609 to go from km to miles
        double distMiles = (Radius*c)/1.609;
        Log.i("Radius Value",+ distMiles + "  in miles.");

        return distMiles;
    }



    private void getDeviceLocation(final Boolean setCamera) {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
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

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: map failed");
    }

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

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occurred but we can resolve it
            Log.d(TAG, "isServicesOK: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
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

