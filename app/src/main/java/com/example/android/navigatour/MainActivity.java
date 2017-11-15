package com.example.android.navigatour;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startTSP(View view){
        Intent intent = new Intent(this, TSPActivity.class);
        startActivity(intent);

    }

    public void startLocateNearby(View view){
        Intent intent = new Intent(this,LocateNearbyActivity.class);
        startActivity(intent);
    }
}
