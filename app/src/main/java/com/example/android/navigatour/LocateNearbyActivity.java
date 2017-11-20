package com.example.android.navigatour;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by setia on 11/15/2017.
 */

public class LocateNearbyActivity extends AppCompatActivity{
    ArrayList<HashMap<String,String >> allData =  new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate_nearby);
    }

    public void onLocateClick(View view){
        URL requestURL = null;
        try {
            requestURL = new URL("http://apir.viamichelin.com/apir/2/findPoi.xml/RESTAURANT/eng?center="+ "103.85" + ":"+ "1.29" + "&nb=10&dist=1000&source=RESGR&filter=AGG.provider%20eq%20RESGR&charset=UTF-8&ie=UTF-8&authKey=RESTGP20171120074056040173531595");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        GetRestaurantsTask getRestaurantsTask = new GetRestaurantsTask();
        getRestaurantsTask.execute(requestURL);



    }

    public void parseRestaurant(InputSource is){
        try {

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            DefaultHandler handler = new DefaultHandler() {

                HashMap<String,String> individualData;
                boolean bstartpoi = false;
                boolean brname = false;
                boolean baddress = false;
                boolean bopeningtimes = false;
                boolean bmealprice = false;

                public void startElement(String uri, String localName,
                                         String qName, Attributes attributes)
                        throws SAXException {

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

                }

                public void endElement(String uri, String localName,
                                       String qName)
                        throws SAXException {

                    if (qName.equalsIgnoreCase("POI")){
                        allData.add(individualData);
                    }

//                    System.out.println("End Element :" + qName);

                }

                public void characters(char ch[], int start, int length)
                        throws SAXException {

//                    System.out.println(new String(ch, start, length));


                    if (brname) {
                        individualData.put("name","Restaurant Name : " + new String(ch, start, length));
                        System.out.println("Restaurant Name : " + new String(ch, start, length));
                        brname = false;
                    }

                    if (baddress) {
                        individualData.put("address", "Address : " + new String(ch, start, length));
                        System.out.println("Address : "
                                + new String(ch, start, length));
                        baddress = false;
                    }

                    if (bopeningtimes) {
                        individualData.put("openingtimes", "Opening Times: " + new String(ch, start, length));
                        System.out.println("Opening Times : "
                                + new String(ch, start, length));
                        bopeningtimes = false;
                    }

                    if (bmealprice) {
                        individualData.put("mealprice", "Meal Price: " + new String(ch, start, length));
                        System.out.println("Meal Price : "
                                + new String(ch, start, length));
                        bmealprice = false;
                    }

                }

            };

//            InputStream inputStream= new FileInputStream(file);
//            Reader reader = new InputStreamReader(inputStream,"UTF-8");
            saxParser.parse(is, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HashMap<String,String> restaurants = allData.get(0);
        Iterator iterator= restaurants.keySet().iterator();
        String textDisplayed = "";
        while (iterator.hasNext()){
            textDisplayed += restaurants.get(iterator.next());
            if (iterator.hasNext()){
                textDisplayed += "\n";
            }
        }
        TextView restaurantTextView = findViewById(R.id.restaurantsTextView);
        restaurantTextView.setText(textDisplayed);
    }


    public static String performGetCall(String requestURL) {
        URL url;
        String response = "";
        try {

            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response += line;
                }
            }
            else {
                response = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


    public class GetRestaurantsTask extends AsyncTask<URL, Void, InputSource> {

        @Override
        protected InputSource doInBackground(URL... urls) {
            InputSource is = null;
            URL requestURL = urls[0];
            try {
                is = new InputSource(requestURL.openStream());
                is.setEncoding("UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }

            return is;
        }

        @Override
        protected void onPostExecute(InputSource is) {
            parseRestaurant(is);
        }


    }



}
