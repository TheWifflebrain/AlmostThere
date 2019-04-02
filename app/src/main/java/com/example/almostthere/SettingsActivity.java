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

    /** var for logs */
    private static final String TAG = "SettingsActivity";

    /** vars for UI */
    private ImageView buttonBack;
    private EditText setRadius;
    private Button buttonSetRadius;
    private Button buttonSetMessage;
    private EditText setMessage;
    private Button buttonSetContact;
    private EditText setContact;
    private Button buttonSendSendWhen;
    private EditText setSendWhen;
    private Button buttonSetMessage1;
    private EditText setMessage1;

    /** var to set radius */
    public String radiusSet = "";
    public String messageSet = "";
    public String contactSet = "";
    public String messageSetDist = "";
    public String sendWhenMiles = "";

    /**
     * Creates the settings intent
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings2);
        buttonBack = findViewById(R.id.backToMain);
        buttonSetRadius = findViewById(R.id.radius_button);
        setRadius = findViewById(R.id.txtRadius);
        buttonSetMessage = findViewById(R.id.textSMSConfirm);
        setMessage = findViewById(R.id.messageSMS);
        buttonSetContact = findViewById(R.id.buttonSetContact);
        setContact = findViewById(R.id.numberSMS);
        buttonSendSendWhen = findViewById(R.id.buttonSetSendWhen);
        setSendWhen = findViewById(R.id.sendWhenMiles);
        buttonSetMessage1 = findViewById(R.id.textSMSConfirmDist);
        setMessage1 = findViewById(R.id.messageSMS1);

        /**
         * The workings behind the back arrow button
         */
        buttonBack.setOnClickListener(new View.OnClickListener() {
            /**
             * Loads the previous intent without the previous intent losing data
             * @param view
             */
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked settings icon");

                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        /**
         * Workings behind the set radius button
         */
        buttonSetRadius.setOnClickListener(new View.OnClickListener() {
            /**
             * Changes the radius when the user updates it
             * @param view
             */
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked changed radius");
                radiusSet = setRadius.getText().toString();
                if(radiusSet == null){
                    radiusSet = "0.25";
                }
                Log.i(TAG, "radius = " + radiusSet);

                /** saving the radius to shared preferences */
                SharedPreferences prefs = getSharedPreferences(MapActivity.APP_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(MapActivity.RADIUS_SETTINGS, radiusSet);
                editor.apply();
                Toast.makeText(SettingsActivity.this, "Set Radius to: " + radiusSet + " miles.", Toast.LENGTH_SHORT).show();
            }
        });

        /**
         * Workings behind the set message button
         */
        buttonSetMessage.setOnClickListener(new View.OnClickListener() {
            /**
             * Changes the radius when the user updates it
             * @param view
             */
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked set message button");
                if(messageSet == null || messageSet == " "){
                    messageSet = "";
                }
                else{
                    messageSet = setMessage.getText().toString();
                    SharedPreferences prefs1 = getSharedPreferences(MapActivity.APP_PREFS, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs1.edit();
                    editor.putString(MapActivity.MESSAGE_SETTINGS, messageSet);
                    editor.apply();
                    Toast.makeText(SettingsActivity.this, "Your message has been set!", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Message: " + messageSet);
                }
            }
        });

        /**
         * Workings behind the set contact button
         */
        buttonSetContact.setOnClickListener(new View.OnClickListener() {
            /**
             * Changes the radius when the user updates it
             * @param view
             */
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked set contact button");
                if(contactSet == null || contactSet == " "){
                    contactSet = "";
                }
                else{
                    contactSet = setContact.getText().toString();
                    SharedPreferences prefs2 = getSharedPreferences(MapActivity.APP_PREFS, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs2.edit();
                    editor.putString(MapActivity.CONTACT_SETTINGS, contactSet);
                    editor.apply();
                    Toast.makeText(SettingsActivity.this, "Your contact has been set!", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Contact: " + contactSet);
                }
            }
        });


        buttonSendSendWhen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked set contact button");
                if(sendWhenMiles == null || sendWhenMiles == " "){
                    sendWhenMiles = "";
                }
                else{
                    sendWhenMiles = setSendWhen.getText().toString();
                    SharedPreferences prefs2 = getSharedPreferences(MapActivity.APP_PREFS, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs2.edit();
                    editor.putString(MapActivity.SEND_WHEN_SETTINGS, sendWhenMiles);
                    editor.apply();
                    Toast.makeText(SettingsActivity.this, "Send message when " + sendWhenMiles + " miles left to go.", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Send when: " + sendWhenMiles);
                }
            }
        });

        buttonSetMessage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked set contact button");
                if(messageSetDist == null || messageSetDist == " "){
                    messageSetDist = "";
                }
                else{
                    messageSetDist = setMessage1.getText().toString();
                    SharedPreferences prefs2 = getSharedPreferences(MapActivity.APP_PREFS, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs2.edit();
                    editor.putString(MapActivity.SEND_WHEN_MESSAGE_SETTINGS, messageSetDist);
                    editor.apply();
                    Toast.makeText(SettingsActivity.this, "Message by distance: " + messageSetDist + ".", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Message by distance: " + messageSetDist);
                }
            }
        });
    }
}
