package com.example.almostthere;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    /** var for logs */
    private static final String TAG = "SettingsActivity";

    /** vars for UI */
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
    private Button buttonChooseContact;

    /** vars for contacts */
    private final int PICK_CONTACT = 1;
    final int READ_CONTACTS_PERMISSION_REQUEST_CODE = 1;
    Boolean canRead = false;

    /** var to set radius */
    public String radiusSet = "";
    public String messageSet = "";
    public String contactSet = "";
    public String messageSetDist = "";
    public String sendWhenMiles = "";
    public String cNumber = "";

    /**
     * Creates the settings intent
     * @param savedInstanceState the previous Intent without data loss
     */
    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        readPermission();

        setContentView(R.layout.activity_settings2);
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
        buttonChooseContact = findViewById(R.id.buttonSetContact2);

        /** setting the hint for radius to the current radius variable */
        SharedPreferences prefs = getSharedPreferences(MapActivity.APP_PREFS, Context.MODE_PRIVATE);
        String r = prefs.getString(MapActivity.RADIUS_SETTINGS, radiusSet);
        setRadius.setHint(r + " mi");
        setRadius.setHintTextColor(ResourcesCompat.getColor(getResources(), R.color.cardview_dark_background, null));

        /**
         * Workings behind the set radius button
         */
        buttonSetRadius.setOnClickListener(new View.OnClickListener() {
            /**
             * Changes the radius when the user updates it
             * @param view the UI
             */
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked changed radius");
                radiusSet = setRadius.getText().toString();
                if(radiusSet.equals("")){
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
             * @param view the UI
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
             * @param view the UI
             */
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked set contact button");

                SharedPreferences prefs2 = getSharedPreferences(MapActivity.APP_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs2.edit();

                if(contactSet == null || contactSet == " " || cNumber.equals("")){
                    contactSet = "";
                }

                if(!cNumber.equals("")){
                    editor.putString(MapActivity.CONTACT_SETTINGS, cNumber);
                    editor.apply();
                    Toast.makeText(SettingsActivity.this, "Your contact has been set!", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Contact using cNumber: " + cNumber);
                }
                else {
                    contactSet = setContact.getText().toString();
                    editor.putString(MapActivity.CONTACT_SETTINGS, contactSet);
                    editor.apply();
                    Toast.makeText(SettingsActivity.this, "Your contact has been set!", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Contact using contactSet: " + contactSet);
                }
            }
        });

        /**
         * Workings behind setting the miles for sending a message
         */
        buttonSendSendWhen.setOnClickListener(new View.OnClickListener() {
            /**
             * Changes the send SMS by distance variable
             * @param view the UI
             */
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked set send when distance");
                if(sendWhenMiles == null || sendWhenMiles == " "){
                    sendWhenMiles = "";
                }
                    sendWhenMiles = setSendWhen.getText().toString();
                    SharedPreferences prefs2 = getSharedPreferences(MapActivity.APP_PREFS, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs2.edit();
                    editor.putString(MapActivity.SEND_WHEN_SETTINGS, sendWhenMiles);
                    editor.apply();
                    Toast.makeText(SettingsActivity.this, "Send message when " + sendWhenMiles + " miles left to go.", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Send when: " + sendWhenMiles);
            }
        });

        /**
         * Workings behind setting the message by distance
         */
        buttonSetMessage1.setOnClickListener(new View.OnClickListener() {
            /**
             * Changes the SMS by distance
             * @param view
             */
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked set message by distance");
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

        /**
         * Workings behind the choose contact button
         */
        buttonChooseContact.setOnClickListener(new View.OnClickListener() {
            /**
             * Changes the SMS by distance
             * @param view the UI
             */
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked choose contact button");
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });
    }


    /**
     * Setting up the s.xml page as new toolbar
     * @param menu toolbar
     * @return if it can set up new toolbar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.s, menu);
        return true;
    }

    /**
     * Knows which button you clicked on
     * @param item essentially buttons listed in the m.xml
     * @return option was selected
     */
    public boolean onOptionsItemSelected(MenuItem item){
        int res_id = item.getItemId();
        Log.d(TAG, "onClick: clicked back icon");
        if(res_id==R.id.backToMain){
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        }
        return true;
    }

    /**
     * Pulling up the contacts list and retrieving the phone number from the contact selected
     * @param reqCode the request code to access contacts
     * @param resultCode the result code
     * @param data the intent data
     */
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data){
        Log.i(TAG, "Entered into retrieving contact data");
        super.onActivityResult(reqCode, resultCode, data);

        if(reqCode == PICK_CONTACT){
            if(resultCode == Activity.RESULT_OK){
                Uri contactData = data.getData();
                Cursor c = getContentResolver().query(contactData, null, null, null, null);

                if(c.moveToFirst()){
                    String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                    String hasNumber = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                    if(hasNumber.equalsIgnoreCase("1")){
                        phones.moveToFirst();
                        cNumber = phones.getString(phones.getColumnIndex("data1"));
                        Log.i(TAG, "contact phone number is: " + cNumber);
                        SharedPreferences prefs2 = getSharedPreferences(MapActivity.APP_PREFS, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs2.edit();
                        editor.putString(MapActivity.CONTACT_SETTINGS, cNumber);
                        editor.apply();
                        setContact.setHint(cNumber + " mi");
                        setContact.setHintTextColor(ResourcesCompat.getColor(getResources(), R.color.cardview_dark_background, null));
                    }
                    else{
                        Toast.makeText(this, "No number associated with the contact", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(this, "You picked " + name, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Making sure the permissions is good
     */
    private void readPermission(){
        Log.d(TAG, "getSendSMSPermission: getting sms permissions");
        String[] permissions = {Manifest.permission.READ_CONTACTS};
        if(checkPermissionContacts(Manifest.permission.READ_CONTACTS)){
            canRead = true;
        }
        else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    READ_CONTACTS_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Checks if permissions for reading contacts is ok
     * @param permission the permissions string
     * @return if permissions is either granted or not
     */
    public boolean checkPermissionContacts(String permission){
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

}
