package com.example.android.navigatour;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    static final int IDM_SETTINGS = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        menu.add(Menu.NONE,IDM_SETTINGS,Menu.NONE, R.string.menu_settings);
        return(super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId()==IDM_SETTINGS){
            Intent intent = new Intent();
            intent.setClass(this, SettingsActivity.class);
            startActivity(intent);
        }
        return true;
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
