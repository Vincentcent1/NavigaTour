package com.example.android.navigatour;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

public class TSPActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tsp);

        String jsonString = "Your JSON string";
        HashMap<String,String> map = new Gson().fromJson(jsonString, new TypeToken<HashMap<String, String>>(){}.getType());
    }
}
