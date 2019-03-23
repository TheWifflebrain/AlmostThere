package com.example.almostthere;

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

import java.util.Set;

public class SettingsActivity extends AppCompatActivity {
    private ImageView buttonBack;
    private EditText setRadius;
    private static final String TAG = "SettingsActivity";
    private Button buttonSetRadius;
    public Double radius = 0.25;
    public String radiusS = "";
    public static final String MY_RADIUS = "RADIUS";


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

                String result = "bye";
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",result);
                setResult(RESULT_OK,returnIntent);
                finish();

                //Intent returnIntent = new Intent();
                //setResult(RESULT_CANCELED, returnIntent);
                //finish();
            }



        });

        buttonSetRadius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked changed radius");
                radiusS = setRadius.getText().toString();
                radius = Double.parseDouble(radiusS);
                Log.d(TAG, "radius = " + radius);

                SharedPreferences prefs = getSharedPreferences(MY_RADIUS, MODE_PRIVATE);
                String radiusText = prefs.getString(radiusS, radiusS);
                EditText radiusSet = (EditText) findViewById(R.id.txtRadius);
                radiusSet.setText(radiusText);
                Log.d(TAG, "radiusSP = " + radiusText);
                Toast.makeText(SettingsActivity.this, "Set Radius to: " + radiusText, Toast.LENGTH_LONG).show();
            }

        });

    }

    public void refreshActivity() {
        Intent i = new Intent(this, MapActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();

    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        //Refresh your stuff here
    }

}
