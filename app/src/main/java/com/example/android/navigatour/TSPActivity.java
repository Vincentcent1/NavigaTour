package com.example.android.navigatour;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class TSPActivity extends AppCompatActivity {
    HashMap<String,ArrayList<HashMap<String,String>>> activitiesG;
    int minTime; // TODO: Change to max int value
    double budgetRemaining;
    ArrayList<String> bestPath = new ArrayList<>();

    public String loadAttractions(Context context) {
        String json = null;
        try {
            InputStream inputStream = context.getAssets().open("attractions.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return json;
    }

    public void findBestPathBrute(ArrayList<String> attractions, ArrayList<String> path, String previous, String finalLocation, double budget, int currentTime) {
        /*
        *  Find the best order to visit all attractions by iterating through all possible routes and transport modes.
        *  This is O(n! * m^(n-1)) where n is the number of attractions, and m is the number of transport modes
        *  Starting point: previous
        */

        // Finished the tour. Return to the final location
        if(attractions.size() == 0) {
            String currentLocation = previous;
            ArrayList<HashMap<String,String>> neighbours = activitiesG.get(currentLocation);
            int finalTime = currentTime;
            double finalBudget = budget;

            ArrayList<String> newPath = (ArrayList<String>)path.clone();

            for(HashMap<String, String> neighbour : neighbours) {
                if(neighbour.get("name").equals(finalLocation)) {
                    // Find fastest way back that is within budget
                    double taxiCost = Double.valueOf(neighbour.get("taxi_cost"));
                    double publicCost = Double.valueOf(neighbour.get("public_cost"));
                    if(taxiCost <= budget) {
                        finalTime = currentTime + Integer.valueOf(neighbour.get("taxi_time"));
                        finalBudget -= taxiCost;
                        newPath.add("taxi");
                    }
                    else if(publicCost <= budget) {
                        finalTime = currentTime + Integer.valueOf(neighbour.get("public_time"));
                        finalBudget -= publicCost;
                        newPath.add("public");
                    }
                    else {
                        finalTime = currentTime + Integer.valueOf(neighbour.get("foot_time"));
                        newPath.add("foot");
                    }

                    break;
                }
            }

            newPath.add(finalLocation);
            // If this is a better time or minTime is uninitialized, set minTime to this
            if(finalTime < minTime || minTime == -1) {
                minTime = finalTime;
                budgetRemaining = budget;
                bestPath = newPath;
            }
        }

        // Try all routes
        for(int i = 0; i < attractions.size(); i ++) { // Loop V times
            String newLocation = attractions.get(i);
            ArrayList<String> newAttractions = (ArrayList<String>)attractions.clone();
            newAttractions.remove(newLocation);

            ArrayList<String> newPath = (ArrayList<String>)path.clone();

            // Cost: O(V)
            for(HashMap<String, String> neighbour : activitiesG.get(previous)) {
                if(neighbour.get("name").equals(newLocation)) {
                    // Try all 3 possible modes of transport
                    double taxiCost = Double.valueOf(neighbour.get("taxi_cost"));
                    if(taxiCost <= budget) {
                        double currentBudget = budget - taxiCost;
                        int time = Integer.valueOf(neighbour.get("taxi_time"));
                        newPath.add("taxi");
                        newPath.add(newLocation);
                        findBestPathBrute(newAttractions, newPath, newLocation, finalLocation, currentBudget, currentTime+time);
                    }

                    double publicCost = Double.valueOf(neighbour.get("public_cost"));
                    if(publicCost <= budget) {
                        double currentBudget = budget - publicCost;
                        int time = Integer.valueOf(neighbour.get("public_time"));
                        newPath.add("public");
                        newPath.add(newLocation);
                        findBestPathBrute(newAttractions, newPath, newLocation, finalLocation, currentBudget, currentTime+time);
                    }

                    int time = Integer.valueOf(neighbour.get("foot_time"));
                    newPath.add("foot");
                    newPath.add(newLocation);
                    findBestPathBrute(newAttractions, newPath, newLocation, finalLocation, budget, currentTime+time);

                    break;
                }
            }
        }
    }

    public void findBestPathNN(ArrayList<String> attractions, ArrayList<String> path, String previous, String finalLocation, double budget, int currentTime) {
        // Finished the tour. Return to the final location
        if(attractions.size() == 0) {
            String currentLocation = previous;
            ArrayList<HashMap<String,String>> neighbours = activitiesG.get(currentLocation);
            int finalTime = currentTime;
            double finalBudget = budget;

            ArrayList<String> newPath = (ArrayList<String>)path.clone();

            for(HashMap<String, String> neighbour : neighbours) {
                if(neighbour.get("name").equals(finalLocation)) {
                    // Find fastest way back that is within budget
                    double taxiCost = Double.valueOf(neighbour.get("taxi_cost"));
                    double publicCost = Double.valueOf(neighbour.get("public_cost"));
                    if(taxiCost <= budget) {
                        finalTime = currentTime + Integer.valueOf(neighbour.get("taxi_time"));
                        finalBudget -= taxiCost;
                        newPath.add("taxi");
                    }
                    else if(publicCost <= budget) {
                        finalTime = currentTime + Integer.valueOf(neighbour.get("public_time"));
                        finalBudget -= publicCost;
                        newPath.add("public");
                    }
                    else {
                        finalTime = currentTime + Integer.valueOf(neighbour.get("foot_time"));
                        newPath.add("foot");
                    }

                    break;
                }
            }

            newPath.add(finalLocation);
            // If this is a better time or minTime is uninitialized, set minTime to this
            if(finalTime < minTime || minTime == -1) {
                minTime = finalTime;
                budgetRemaining = budget;
                bestPath = newPath;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tsp);

        // Load attractions graph
        String jsonString = loadAttractions(this);
        activitiesG = new Gson().fromJson(jsonString, new TypeToken<HashMap<String,ArrayList<HashMap<String,String>>>>(){}.getType());

        // Initialization
        minTime = -1;
        budgetRemaining = 0;

        ArrayList<String> attractions = new ArrayList<>();
        attractions.add("flyer");
        attractions.add("vivo");
        attractions.add("rws");

        String startLocation = "mbs";
        ArrayList<String> path = new ArrayList<>();
        path.add(startLocation);

        findBestPathBrute(attractions, path, startLocation, "mbs", 0, 0);
        System.out.println("Minimum time: " + minTime);
        System.out.println(bestPath);
    }
}
