package com.example.android.navigatour;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    SharedPreferences sharedPref;
    Button part3;
    ImageView part1;
    Integer myTheme = R.style.AppRedTheme;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(myTheme);
        setContentView(R.layout.activity_main);
        bundle = savedInstanceState;
        part1 = (ImageView) findViewById(R.id.part1);
        part3 = (Button)findViewById(R.id.part2);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(this);
        String chkBoxLargeFontKey = getString(R.string.checkBoxKey);
        boolean isButtonLargeFont = sharedPref.getBoolean(chkBoxLargeFontKey,false);
        changeFont(isButtonLargeFont);
        String royalTheme = getString(R.string.checkBoxYellow);
        boolean isRoyalTheme = sharedPref.getBoolean(royalTheme,false);
        changeTheme(isRoyalTheme);
        String Chinese = getString(R.string.checkBoxChinese);
        boolean isChinese = sharedPref.getBoolean(Chinese,false);
        changeLanguage(isChinese);


    }
    public void changeFont(boolean iblf){
        if (iblf){
            part3.setTextSize(35);
        }else{
            part3.setTextSize(TypedValue.COMPLEX_UNIT_DIP,30);
        }
    }
    public void changeTheme (boolean ifTheme){
        ConstraintLayout bgElement = (ConstraintLayout) findViewById(R.id.mylayout);
        if (ifTheme){
            //myTheme = R.style.YellowTheme;
            bgElement.setBackgroundResource(R.color.whiteYellow);
            part1.setImageResource(R.drawable.royalmichellinstar);
            part1.setScaleType(ImageView.ScaleType.FIT_CENTER);
            part1.setBackgroundResource(R.color.lightYellow);
            part3.setBackgroundResource(R.color.lightYellow);
            part3.setTextColor(getResources().getColor(R.color.royalYellow));
            //setContentView(R.layout.activity_main);
        }else{
            //myTheme = R.style.AppRedTheme;
            bgElement.setBackgroundResource(R.color.crimsonRed);
            part1.setImageResource(R.drawable.michelinguide);
            part1.setScaleType(ImageView.ScaleType.CENTER_CROP);
            part3.setBackgroundResource(R.color.darkRed);
            part3.setTextColor(getResources().getColor(R.color.whiteRed));
            //setContentView(R.layout.activity_main);
            //super.onRestart();
            //super.onCreate(bundle);
        }
    }
    public void changeLanguage(boolean ifChinese){

        if (ifChinese){
            part3.setText("开启新加坡名胜之旅！");
        }else{
            part3.setText(R.string.function3);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.settings){
//code for the intent goes here
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return true;
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("checkBoxKey")){

            //same code as above
            boolean checked = sharedPreferences.getBoolean(s,false);

            //REMINDER - write code for this method
            changeFont(checked);
        }
        if (s.equals("checkBoxYellow")){
            boolean checked = sharedPreferences.getBoolean(s,false);

            //REMINDER - write code for this method
            changeTheme(checked);
        }
        if (s.equals("checkBoxChinese")){
            boolean checked = sharedPreferences.getBoolean(s,false);

            //REMINDER - write code for this method
            changeLanguage(checked);
        }
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
