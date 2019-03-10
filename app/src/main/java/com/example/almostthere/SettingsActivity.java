package com.example.almostthere;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.Set;

public class SettingsActivity extends AppCompatActivity {
    private ImageView buttonBack;
    private static final String TAG = "SettingsActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings2);
        //buttonBack = (ImageView) findViewById(R.id.backToMain);

//        buttonBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(TAG, "onClick: clicked settings icon");
//                Intent intent = new Intent(SettingsActivity.this, MapActivity.class);
//                startActivity(intent);
//            }
//        });
    }

}
