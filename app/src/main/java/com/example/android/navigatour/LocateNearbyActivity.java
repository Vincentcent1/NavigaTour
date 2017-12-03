package com.example.android.navigatour;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by setia on 11/15/2017.
 */
//To connect to emulator and do geofixing: telnet localhost <console-port>
public class LocateNearbyActivity extends AppCompatActivity {
    ArrayList<HashMap<String, String>> allData = new ArrayList<>();
    private Location userLocation;
    private final int LOCATION_PERMISSION_REQUEST = 1;
    private boolean locationReady = false;
    private LocationManager locationManager;
    private String provider;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest = new LocationRequest();
    private LocationCallback mLocationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate_nearby);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                userLocation = locationResult.getLastLocation();
            }
        };
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
//        getCurrentLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //might consider adding if statement to check if locationclient is provided
        startLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                    getCurrentLocation();

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(this, "Setting Changi Airport as default location", Toast.LENGTH_LONG).show();
                    userLocation = new Location("");
                    userLocation.setLatitude(1.36);
                    userLocation.setLongitude(103.99);
                    locationReady = true;
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void getCurrentLocation() {
        //&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permission if app doesn't have permission yet
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null
                        if (location != null) {
                            Toast.makeText(getApplicationContext(), "Location sent", Toast.LENGTH_SHORT).show();
                            userLocation = location;
                            locationReady = true;
                        } else {
                            Toast.makeText(getApplicationContext(), "Location is null", Toast.LENGTH_SHORT).show();
                            location = new Location("");
                            location.setLongitude(103.85);
                            location.setLatitude(1.29);
                            locationReady = true;
                        }
                    }
                });
    }


    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    protected void createLocationRequest() {

        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        //Check whether current location settings are satisfied
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getCurrentLocation();
            }
        });
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statuscode = ((ApiException) e).getStatusCode();
                switch (statuscode){
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(LocateNearbyActivity.this,
                                    1);
                        } catch (IntentSender.SendIntentException sendEx){
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }


    public void onLocateClick(View view){
        //Generate nearby restaurants on button click
//        getCurrentLocation();
//        ProgressBar progressBar = findViewById(R.id.locate_nearby_progressbar);
//        progressBar.setVisibility(View.VISIBLE);
//        while(!locationReady){
//            //wait
//
//        }
//        progressBar.setVisibility(View.GONE);
        if (userLocation == null){
            getCurrentLocation();
        }
        double longitude = userLocation.getLongitude();
        double latitude = userLocation.getLatitude();
        String longitudeS = String.valueOf(longitude);
        String latitudeS = String.valueOf(latitude);
        URL requestURL = null;
        try {
            //center: <longitude>:<Latitude>
            //Singapore: <103.85:1.29>
            requestURL = new URL("http://apir.viamichelin.com/apir/2/findPoi.xml/RESTAURANT/eng?center=" + longitudeS + ":"+ latitudeS + "&nb=10&dist=1000&source=RESGR&filter=AGG.provider%20eq%20RESGR&charset=UTF-8&ie=UTF-8&authKey=RESTGP20171120074056040173531595");
            Log.i("URL", requestURL.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        GetRestaurantsTask getRestaurantsTask = new GetRestaurantsTask();
        getRestaurantsTask.execute(requestURL);



    }

    //SAX Parser for Michelin website content
    public void parseRestaurant(InputSource is){
        try {

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();


            DefaultHandler handler = new DefaultHandler() {

                HashMap<String,String> individualData;
                boolean brname = false;
                boolean baddress = false;
                boolean bopeningtimes = false;
                boolean bmealprice = false;
                boolean bweb = false;
                boolean burl = false;
                boolean bdescription = false;
                boolean bstars = false;
                StringBuffer stringBuffer = new StringBuffer();

                public void startElement(String uri, String localName,
                                         String qName, Attributes attributes)
                        throws SAXException {
                    stringBuffer.setLength(0);

//                    System.out.println("Start Element :" + qName);

                    if (qName.equalsIgnoreCase("POI")) { //if tag equal name, it is the restaurant name
                        individualData = new HashMap<>(4);
                    }

                    if (qName.equalsIgnoreCase("NAME")) { //if tag equal name, it is the restaurant name
                        brname = true;
                    }

                    if (qName.equalsIgnoreCase("ADDRESS")) {
                        baddress = true;
                    }

                    if (qName.equalsIgnoreCase("OPENING_TIMES_LABEL")) {
                        bopeningtimes = true;
                    }

                    if (qName.equalsIgnoreCase("MEAL_PRICE")) {
                        bmealprice = true;
                    }
                    if (qName.equalsIgnoreCase("WEB")){
                        bweb = true;
                    }
                    if (qName.equalsIgnoreCase("URL_XL")){
                        burl = true;
                    }
                    if (qName.equalsIgnoreCase("DESCRIPTION")){
                        bdescription = true;
                    }
                    if (qName.equalsIgnoreCase("MICHELIN_STARS")){
                        bstars = true;
                    }
                }

                public void endElement(String uri, String localName,
                                       String qName)
                        throws SAXException {
                    String s = stringBuffer.toString().trim();


                    if (qName.equalsIgnoreCase("POI")){
                        allData.add(individualData);
                    }

                    if (qName.equalsIgnoreCase("NAME")) { //if tag equal name, it is the restaurant name
                        individualData.put("name", s);
                        brname = false;
                    }

                    if (qName.equalsIgnoreCase("ADDRESS")) {
                        individualData.put("address", s);
                        baddress = false;
                    }

                    if (qName.equalsIgnoreCase("OPENING_TIMES_LABEL")) {
                        individualData.put("openingtimes",s);
                        bopeningtimes = false;
                    }

                    if (qName.equalsIgnoreCase("MEAL_PRICE")) {
                        individualData.put("mealprice",s);
                        bmealprice = false;
                    }
                    if (qName.equalsIgnoreCase("WEB")){
                        individualData.put("website",s);
                        bweb = false;
                    }
                    if (qName.equalsIgnoreCase("URL_XL")){
                        if (individualData.get("imageurl") == null) {//If it is empty, add in the imageurl String directly
                                individualData.put("imageurl", s);
                            } else {
                                //if there is something, append the new imageurl behind the current imageurl separated by space
                                String temp = individualData.get("imageurl");
                                temp += " " + s;
                                individualData.put("imageurl", temp);
                            }
                        burl = false;
                    }
                    if (qName.equalsIgnoreCase("DESCRIPTION")){
                        individualData.put("description", s);
                        bdescription = false;
                    }
                    if (qName.equalsIgnoreCase("MICHELIN_STARS")){
                        Integer counter = Integer.valueOf(s);
                        String result = new String(new char[counter]).replace("\0", "★");
                        individualData.put("stars", result);
                        bstars = false;
                    }


//                    System.out.println("End Element :" + qName);

                }

                public void characters(char ch[], int start, int length)
                        throws SAXException {

//                    System.out.println(s);
                    stringBuffer.append(ch, start, length);

//                    if (!s.equals("")) {
//                        if (brname) {
//                            individualData.put("name", s);
//                            System.out.println("Restaurant Name : " + s);
//                            brname = false;
//                        }
//
//                        if (baddress) {
//                            individualData.put("address", s);
//                            System.out.println("Address : "
//                                    + s);
//                            baddress = false;
//                        }
//
//                        if (bopeningtimes) {
//                            individualData.put("openingtimes", s);
//                            System.out.println("Opening Times : "
//                                    + s);
//                            bopeningtimes = false;
//                        }
//
//                        if (bmealprice) {
//                            individualData.put("mealprice", s);
//                            System.out.println("Meal Price : "
//                                    + s);
//                            bmealprice = false;
//                        }
//
//                        if (bweb) {
//                            individualData.put("website", new String(ch, start, length));
//                            bweb = false;
//                        }
//                        if (burl) {
//                            //Add all imageURLs into one String, separated by spaces.
//                            if (individualData.get("imageurl") == null) {//If it is empty, add in the imageurl String directly
//                                individualData.put("imageurl", s);
//                            } else {
//                                //if there is something, append the new imageurl behind the current imageurl separated by space
//                                String temp = individualData.get("imageurl");
//                                temp += " " + s;
//                                individualData.put("imageurl", temp);
//                            }
//                            burl = false;
//                        }
//                        if (bdescription) {
//
//                            individualData.put("description", s);
//                            bdescription = false;
//                        }
//                        if (bstars) {
//                            String temp = s;
//                            Integer counter = Integer.valueOf(temp);
//                            String result = new String(new char[counter]).replace("\0", "★");
//                            individualData.put("stars", result);
//                            bstars = false;
//                        }
//                    }
                }

            };

//            InputStream inputStream= new FileInputStream(file);
//            Reader reader = new InputStreamReader(inputStream,"UTF-8");
            saxParser.parse(is, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HashMap<String,String> restaurants = allData.get(0);
//        Iterator iterator= restaurants.keySet().iterator();
//        String textDisplayed = "";
//        while (iterator.hasNext()){
//            textDisplayed += restaurants.get(iterator.next());
//            if (iterator.hasNext()){
//                textDisplayed += "\n";
//            }
//        }
//        TextView restaurantTextView = findViewById(R.id.restaurantsTextView);
//        restaurantTextView.setText(textDisplayed);
    }

    public class GetRestaurantsTask extends AsyncTask<URL, Void, InputSource> {

        @Override
        protected InputSource doInBackground(URL... urls) {
            InputSource is = null;
            URL requestURL = urls[0];
            boolean done = false;
            while (!done) {
                try {
                    HttpURLConnection conn = (HttpURLConnection) requestURL.openConnection();
                    conn.setReadTimeout(30000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("GET");
//                conn.setDoInput(true);
//                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    int responseCode = conn.getResponseCode();

                    String header = "" + conn.getHeaderField(9) + " " + conn.getHeaderField(10);
                    Log.i("HEADER",header);

                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        InputStream inputStream = new BufferedInputStream(conn.getInputStream());
                        is = new InputSource(inputStream);
                        is.setEncoding("UTF-8");
//                        Scanner scanner = new Scanner(inputStream);
//                        String responsebody = scanner.useDelimiter("\\A").next();
//                        System.out.println(responsebody);
                        done = true;
                        parseRestaurant(is);
                    } else {
                        done = false;
                    }
//                InputStream inputStream = requestURL.openStream();
//                inputStream.
//                is = new InputSource(requestURL.openStream());
//                is.setEncoding("UTF-8");
                } catch (IOException e) {
                    e.printStackTrace();
                }



            }

            return is;
        }

        @Override
        protected void onPostExecute(InputSource is) {
            Intent intent = new Intent(getBaseContext(),RecyclerActivity.class);
            intent.putExtra("RESTAURANTS", allData);
            startActivity(intent);
        }


    }



}
