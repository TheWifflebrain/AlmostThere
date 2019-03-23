package com.example.almostthere;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.google.android.libraries.places.internal.lf.r;
import static java.lang.Math.abs;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));


    //widgets
    private AutoCompleteTextView mSearchText;
    private ImageView mGps;
    private ImageView dGps;
    private ImageView breakPin;

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    //private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    //private PlaceInfo mPlace;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private ImageView buttonSettings;

    //Location location;
    private ImageView setPin;
    private double longitude;
    private double latitude;

    private double startLongitude;
    private double startLatitude;

    private double endLongitude;
    private double endLatitude;
    private Handler handler = new Handler();
    double newDistance = 0.0;
    Boolean setCamera = true;
    Boolean setMoveToCurrentLocation = true;

    SharedPreferences shared;
    public String radiusS = "0.25";
    public Double radiusD = 0.25;

    public static final String RADIUS_SETTINGS = "RADIUS_SETTINGS";
    public static final String APP_PREFS = "APPLICATION_PREFERENCES";


    MarkerOptions options = null;

    public MapActivity() {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
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
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        //mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);
        mGps = (ImageView) findViewById(R.id.ic_gsp);
        buttonSettings = (ImageView) findViewById(R.id.settingsIV);
        setPin = (ImageView) findViewById(R.id.ic_set);
        breakPin = (ImageView) findViewById(R.id.ic_break);
        dGps = (ImageView) findViewById(R.id.ic_locateFinalDestination);

        if (isServicesOK()) {
            getLocationPermission();
        }

/*
        Log.i(TAG, "Repeating updating the distance for new distance: " + newDistance);
        if(newDistance > 0.001){
            Thread t = new Thread(){
                //@Override
                public void run() {
                    while (!isInterrupted()) {
                        try {
                            Thread.sleep(2000);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i(TAG, "Repeating updating the distance");
                                    updateDistanceUI();
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            t.start();
        }

        */

    }

    private void init() {
        Log.d(TAG, "init: initializing");



        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMoveToCurrentLocation = true;
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation(setCamera = true);
            }
        });

        dGps.setOnClickListener(new View.OnClickListener() {
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
                    if (newDistance > 0.001) {
                        handler.postDelayed(runnable, 3000);
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
                    textView.setText("Ended Calculating Distance");

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
                handler.postDelayed(this, 3000);
            }
        }
    };

    public void updateDistanceUI(){
        newDistance = CalculationByDistance(startLatitude, startLongitude, endLatitude, endLongitude);
        String stringDistance = String.format("%.3f", newDistance);
        TextView textView = (TextView) findViewById(R.id.distanceLeft);
        textView.setText("Distance in Miles: " + stringDistance);

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

        if(newDistance <= radiusD){
            //Alarm set off

            textView.setText("Within radius. Alarm going off!");
        }
    }

    public double CalculationByDistance(double startLat, double startLong, double endLat, double endLong) {
        int Radius=6371;//radius of earth in Km
        double lat1 = startLat;
        double lat2 = endLat;
        double lon1 = startLong;
        double lon2 = endLong;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult= (Radius*c)/1.609;
        Log.i("Radius Value",""+valueResult+"  in miles.");

        return valueResult;
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
                    //initialize our map
                    initMap();

                }
            }
        }
    }




}

