package com.example.android.navigatour;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

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
    int[] bestTime;
    double[] bestCost;
    String[] names;
    ArrayList<LatLng> locations;
    ArrayList<String> transport;

    // This is the Adapter being used to display the list's data
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    GMapV2Direction md = new GMapV2Direction();

    public TSPResultsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tspresults);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.attractionsView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Get results passed in by TSPActivity
        bestPath = getIntent().getStringArrayExtra("results");
        bestCost = getIntent().getDoubleArrayExtra("cost");
        bestTime = getIntent().getIntArrayExtra("time");
        names = getIntent().getStringArrayExtra("names");
        locations = new ArrayList<>();
        transport = new ArrayList<>();

        // Create locations based on lat lng strings
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

        ArrayList<String> markerNamesList = new ArrayList<>(); // For map markers
        ArrayList<String> listNamesList = new ArrayList<>(); // For RecyclerView

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

            // Format string accordingly and add to array for the RecyclerView
            String transportTitle = ".";
            if(i > 0) {
                transportTitle = transport.get(i-1);

                // Format string intuitively
                if(transportTitle.equals("public")) {
                    transportTitle = "Take public transport to ";
                }
                else if(transportTitle.equals("taxi")) {
                    transportTitle = "Take a taxi to ";
                }
                else {
                    // Foot
                    transportTitle = "Walk to ";
                }

                transportTitle += names[i] + ". Cost: $" + String.valueOf(bestCost[i-1]) + ". Time taken: " + String.valueOf(bestTime[i-1]) + " mins";
            }
            else {
                transportTitle = names[i];
            }

            // Add to appropriate arrays
            String markerTitle = String.valueOf(i+1) + numberSuffix + " stop: " + names[i];
            String listTitle = String.valueOf(i+1) + numberSuffix + " stop: " + transportTitle;

            markerNamesList.add(markerTitle);
            listNamesList.add(listTitle);

            //markerNamesList.add(transportTitle);
            Marker marker = mMap.addMarker(new MarkerOptions().position(attraction).title(markerTitle));
            marker.showInfoWindow();

            // Zoom to the first location
            if(i == 0) {
                CameraPosition cameraPosition = new CameraPosition.Builder().target(attraction).zoom(15).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }

        // Set as RecyclerView adapter
        mAdapter = new MyAdapter(listNamesList.toArray(new String[listNamesList.size()]));
        mRecyclerView.setAdapter(mAdapter);

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

            // Draw route with color based on transport mode
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

            // Draw path
            ArrayList<LatLng> directionPoint = md.getDirection(doc);
            rectLine = new PolylineOptions().width(12).color(this.color);

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

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private String[] mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView mTextView;
            public ViewHolder(TextView v) {
                super(v);
                mTextView = v;
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(String[] myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            TextView v = (TextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.tsp_recycler_layout, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.mTextView.setText(mDataset[position]);

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.length;
        }
    }

}

