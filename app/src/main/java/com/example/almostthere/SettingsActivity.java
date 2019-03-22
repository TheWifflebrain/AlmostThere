package com.example.almostthere;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Set;

public class SettingsActivity extends AppCompatActivity {
    private ImageView buttonBack;
    private EditText setRadius;
    private static final String TAG = "SettingsActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings2);
        buttonBack = (ImageView) findViewById(R.id.backToMain);
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
    }

    public void refreshActivity() {
        Intent i = new Intent(this, MapActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();

    }

}
