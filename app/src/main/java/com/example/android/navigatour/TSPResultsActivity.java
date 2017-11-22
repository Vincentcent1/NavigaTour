package com.example.android.navigatour;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class TSPResultsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String[] bestPath;
    ArrayList<Double> latitudes;
    ArrayList<Double> longitudes;
    ArrayList<String> transport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tspresults);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        bestPath = getIntent().getStringArrayExtra("results");
        latitudes = new ArrayList<>();
        longitudes = new ArrayList<>();
        transport = new ArrayList<>();

        for(int i = 0; i < bestPath.length; i ++) {
            if(i % 2 == 0) {
                String[] latLng = bestPath[i].split(",");
                double lat = Float.valueOf(latLng[0]);
                double lng = Float.valueOf(latLng[1]);
                latitudes.add(lat);
                longitudes.add(lng);
            }
            else {
                String transportStr = bestPath[i];
                transport.add(transportStr);
            }
        }

        System.out.println(latitudes);
        System.out.println(longitudes);
        System.out.println(transport);

//        System.out.println("Best path: " + Arrays.toString(bestPath));
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        for(int i = 0; i < latitudes.size(); i ++) {
            LatLng attraction = new LatLng(latitudes.get(i), longitudes.get(i));
            mMap.addMarker(new MarkerOptions().position(attraction).title(String.valueOf(i)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(attraction));
        }
    }
}
