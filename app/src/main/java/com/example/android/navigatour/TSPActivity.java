package com.example.android.navigatour;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
    ArrayList<String> attractions;
    ArrayList<String> attractionNames;
    ListView attractionsList;
    ArrayAdapter<String> adapter;
    HashMap<String, HashMap<String, String>> attractionDataHash;

    // Maximum attractions we perform brute force on is 5, as this produces a reasonably large time complexity
    static final int BRUTE_THRESHOLD = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tsp);

//        getActionBar().setTitle("Select Attractions to Visit");

        // Load attractions graph
        String jsonString = loadJSON(this, "attractions.json");
        activitiesG = new Gson().fromJson(jsonString, new TypeToken<HashMap<String,ArrayList<HashMap<String,String>>>>(){}.getType());

        String attractionNamesString = loadJSON(this, "attractionNames.json");
        attractionDataHash = new Gson().fromJson(attractionNamesString, new TypeToken<HashMap<String,HashMap<String, String>>>(){}.getType());

        attractions = new ArrayList<>();
        attractionNames = new ArrayList<>();

        for(String key : attractionDataHash.keySet()) {
            if(!key.equals("mbs")) {
                attractions.add(key);
                attractionNames.add(attractionDataHash.get(key).get("name"));
            }
        }

        attractionsList = (ListView)findViewById(R.id.attractionsList);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, attractionNames);
        attractionsList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        attractionsList.setAdapter(adapter);
    }

    public void findRoute(View view) {
        SparseBooleanArray checkedItems = attractionsList.getCheckedItemPositions();
        ArrayList<String> attractionsToVisit = new ArrayList<String>();
        for (int i = 0; i < checkedItems.size(); i++) {
            int position = checkedItems.keyAt(i);
            // Add attraction if it is checked
            if (checkedItems.valueAt(i))
                attractionsToVisit.add(attractions.get(position));
        }

        // Initialization
        minTime = -1;
        budgetRemaining = 0;
        bestPath.clear();

        String startLocation = "mbs";
        ArrayList<String> path = new ArrayList<>();
        path.add(startLocation);

        EditText budgetText = (EditText)findViewById(R.id.budgetText);
        String budgetStr = budgetText.getText().toString();

        if(isNumeric(budgetStr)) {
            double budget = Double.valueOf(budgetStr);

            // Determine which method to use based on the number of attractions
            if(path.size() - 2 >= BRUTE_THRESHOLD) // We deduct 2 because we already know we start and end at mbs
            {
                //        long started = System.nanoTime();
                //        System.out.println("Minimum time: " + minTime);
                //        System.out.println(bestPath);
                //        long time = System.nanoTime();
                //        long timeTaken= time - started;
                //        System.out.println("Time:" + timeTaken/1000000.0 + "ms");
                findBestPathBrute(attractionsToVisit, path, startLocation, "mbs", budget, 0);
            }
            else {
                //        started = System.nanoTime();
                findBestPathNN(attractionsToVisit, path, startLocation, "mbs", budget, 0);
                //        System.out.println("Minimum time: " + minTime);
                //        System.out.println(bestPath);
                //        time = System.nanoTime();
                //        timeTaken= time - started;
                //        System.out.println("Time:" + timeTaken/1000000.0 + "ms");
            }

            Intent intent = new Intent(this, TSPResultsActivity.class);
            String[] bestPathArray = bestPath.toArray(new String[bestPath.size()]);

            // Pass latLng values to map
            for(int i = 0; i < bestPathArray.length; i++) {
                HashMap<String, String> attraction = attractionDataHash.get(bestPathArray[i]);
                if(attraction != null) {
                    String latLng = attraction.get("latLng");
                    bestPathArray[i] = latLng;
                }
            }

            intent.putExtra("results", bestPathArray);
            startActivity(intent);
        }
        else {
            // Display error
            Toast.makeText(this, "Please enter a number for your budget.", Toast.LENGTH_SHORT).show();
        }


    }

    public String loadJSON(Context context, String fileName) {
        String json = null;
        try {
            InputStream inputStream = context.getAssets().open(fileName);
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
        else {
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
    }

    public void findBestPathNN(ArrayList<String> attractions, ArrayList<String> path, String previous, String finalLocation, double budget, int currentTime) {
        // Approximate a solution to visit all attractions using the nearest neighbour algorithm
        // Initialization
        minTime = -1;
        budgetRemaining = 0;
        bestPath.clear();

        while(attractions.size() > 0) {
            ArrayList<HashMap<String,String>> neighbours = activitiesG.get(previous);

            // Find foot distances to every other point from current location
            HashMap<String, HashMap<String, String>> neighbourInfo = new HashMap<>();
            for(HashMap<String, String> neighbour : neighbours) {
                neighbourInfo.put(neighbour.get("name"), neighbour);
            }

            // Find nearest neighbour and travel to it using the fastest way within our budget
            HashMap<String, String> nearestNeighbour = null;
            int shortestDistance = -1;
            for(String attraction : attractions) {
                HashMap<String, String> currentAttraction = neighbourInfo.get(attraction);
                int attractionDistance = Integer.valueOf(currentAttraction.get("foot_time"));
                // Check if attraction is nearer
                if(attractionDistance < shortestDistance || shortestDistance == -1) {
                    nearestNeighbour = currentAttraction;
                    shortestDistance = attractionDistance;
                }
            }

            // Take the fastest way we can afford to the nearestNeighbour
            double taxiCost = Double.valueOf(nearestNeighbour.get("taxi_cost"));
            double publicCost = Double.valueOf(nearestNeighbour.get("public_cost"));
            if(taxiCost <= budget) {
                currentTime += Integer.valueOf(nearestNeighbour.get("taxi_time"));
                budget -= taxiCost;
                path.add("taxi");
            }
            else if(publicCost <= budget) {
                currentTime += Integer.valueOf(nearestNeighbour.get("public_time"));
                budget -= publicCost;
                path.add("public");
            }
            else {
                currentTime += Integer.valueOf(nearestNeighbour.get("foot_time"));
                path.add("foot");
            }

            String nextLocation = nearestNeighbour.get("name");
            path.add(nextLocation);
            attractions.remove(nextLocation);
            previous = nextLocation;
        }

        // Finished the tour. Return to the final location
        int finalTime = currentTime;
        double finalBudget = budget;
        ArrayList<String> newPath = (ArrayList<String>)path.clone();

        ArrayList<HashMap<String,String>> neighbours = activitiesG.get(previous);
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

        // Set output to this
        minTime = finalTime;
        budgetRemaining = budget;
        bestPath = newPath;
    }

    public static boolean isNumeric(String str)
    {
        // Checks if a String is numeric. Retrieved from https://stackoverflow.com/a/1102916

        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }
}
