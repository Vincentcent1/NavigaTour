package com.example.android.navigatour;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class TSPResultsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String[] bestPath;
    String[] names;
    ArrayList<LatLng> locations;
    ArrayList<String> transport;

    GMapV2Direction md = new GMapV2Direction();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tspresults);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        bestPath = getIntent().getStringArrayExtra("results");
        names = getIntent().getStringArrayExtra("names");
        locations = new ArrayList<>();
        transport = new ArrayList<>();

        for(int i = 0; i < bestPath.length; i ++) {
            if(i % 2 == 0) {
                String[] latLng = bestPath[i].split(",");
                double lat = Float.valueOf(latLng[0]);
                double lng = Float.valueOf(latLng[1]);
                locations.add(new LatLng(lat, lng));
            }
            else {
                String transportStr = bestPath[i];
                transport.add(transportStr);
            }
        }
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

        // Display markers for each attraction
        for(int i = 0; i < locations.size(); i ++) {
            LatLng attraction = locations.get(i);
            String numberSuffix = "th";
            // Set appropriate suffix for (i+1)
            switch (i+1) {
                case 1:
                    numberSuffix = "st";
                    break;
                case 2:
                    numberSuffix = "nd";
                    break;
                case 3:
                    numberSuffix = "rd";
                    break;
                default:
                    numberSuffix = "th";
                    break;
            }

            String markerTitle = String.valueOf(i+1) + numberSuffix + " stop: " + names[i];
            Marker marker = mMap.addMarker(new MarkerOptions().position(attraction).title(markerTitle));
            marker.showInfoWindow();

            // Zoom to the first location
            if(i == 0) {
                CameraPosition cameraPosition = new CameraPosition.Builder().target(attraction).zoom(15).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }

        // Draw paths between each location
        drawPath();
    }

    private class showRoute extends AsyncTask<Void, Void, Document> {

        Document doc;
        PolylineOptions rectLine;
        LatLng fromLocation;
        LatLng toLocation;
        String transport;
        int color;

        public showRoute(LatLng fromLocation, LatLng toLocation, String transport) {
            this.fromLocation = fromLocation;
            this.toLocation = toLocation;

            if(transport.equals("taxi")) {
                this.transport = GMapV2Direction.MODE_DRIVING;
                this.color = Color.parseColor("#60A3F4");
            }
            else if(transport.equals("public")) {
                this.transport = GMapV2Direction.MODE_PUBLIC;
                this.color = Color.parseColor("#60F4B1");
            }
            else {
                this.transport = GMapV2Direction.MODE_WALKING;
                this.color = Color.parseColor("#F4B160");
            }
        }

        @Override
        protected Document doInBackground(Void... params) {
            doc = md.getDocument(this.fromLocation, this.toLocation, this.transport);

            ArrayList<LatLng> directionPoint = md.getDirection(doc);
            rectLine = new PolylineOptions().width(7).color(this.color);

            for(int i = 0 ; i < directionPoint.size() ; i++) {
                rectLine.add(directionPoint.get(i));
            }

            return null;
        }

        @Override
        protected void onPostExecute(Document result) {

            mMap.addPolyline(rectLine);
        }

    }

    private void drawPath()
    {
        for (int i = 0; i < locations.size() - 1; i ++)
        {
            LatLng location1 = locations.get(i);
            LatLng location2 = locations.get(i+1);
            String currentTransport = transport.get(i);

            new showRoute(location1, location2, currentTransport).execute();
        }
    }
}

