package com.example.almostthere;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    //var for logs
    private static final String TAG = "SettingsActivity";

    //vars for UI
    private ImageView buttonBack;
    private EditText setRadius;
    private Button buttonSetRadius;

    //var to set radius
    public String radiusSet = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings2);
        buttonBack = (ImageView) findViewById(R.id.backToMain);
        buttonSetRadius = (Button) findViewById(R.id.radius_button);
        setRadius = (EditText) findViewById(R.id.txtRadius);


        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked settings icon");

                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }



        });

        buttonSetRadius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked changed radius");
                radiusSet = setRadius.getText().toString();
                if(radiusSet == null){
                    radiusSet = "0.25";
                }
                Log.d(TAG, "radius = " + radiusSet);

                SharedPreferences prefs = getSharedPreferences(MapActivity.APP_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(MapActivity.RADIUS_SETTINGS, radiusSet);
                editor.apply();
                Toast.makeText(SettingsActivity.this, "Set Radius to: " + radiusSet + " miles.", Toast.LENGTH_LONG).show();
            }

        });

    }


}
