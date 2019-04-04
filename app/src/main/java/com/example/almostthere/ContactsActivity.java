package com.example.almostthere;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ContactsActivity extends AppCompatActivity {

    private static final String TAG = "ContactsActivity";
    final int READ_CONTACTS_PERMISSION_REQUEST_CODE = 1;
    Boolean canRead = false;

    private TextView contactTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_choose);
        contactTextView = findViewById(R.id.contactsTV);
        readPermission();
        getContacts();
    }

    private void getContacts(){
        ContentResolver cR = getContentResolver();
        StringBuilder builder = new StringBuilder();
        Cursor cursor = cR.query(ContactsContract.Contacts.
                CONTENT_URI, null, null, null, null);

        if(cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)));

                if (hasPhoneNumber > 0) {
                    Cursor cursor2 = cR.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                            new String[]{id}, null);

                    while (cursor2.moveToNext()) {
                        String phoneNumber = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.
                                Phone.NUMBER));
                        builder.append(name).append(": ").append(phoneNumber).append("\n\n");

                    }
                    cursor2.close();
                }
            }
        }
        cursor.close();
        contactTextView.setText(builder.toString());
    }

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
     * Checks if permissions for SMS is good
     * @param permission the permissions string
     * @return if permissions is either granted or not
     */
    public boolean checkPermissionContacts(String permission){
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

}
