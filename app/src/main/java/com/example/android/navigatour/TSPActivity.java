package com.example.android.navigatour;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

<<<<<<< Updated upstream
public class TSPActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
=======
public class TSPActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
>>>>>>> Stashed changes
    HashMap<String,ArrayList<HashMap<String,String>>> activitiesG;
    int minTime;
    double budgetRemaining;
    SharedPreferences sharedPref;
    // Final results are stored in these arrays
    ArrayList<String> bestPath = new ArrayList<>();
    ArrayList<Double> bestCost = new ArrayList<>();
    ArrayList<Integer> bestTime = new ArrayList<>();
    ArrayList<String> attractions;
    ArrayList<String> attractionNames;
    ArrayList<String> chineseAttractionNames;
    ListView attractionsList;
    ArrayAdapter<String> adapter;
    HashMap<String, HashMap<String, String>> attractionDataHash;

    SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tsp);

        // Load attractions graph from JSON
        String jsonString = loadJSON(this, "attractions.json");
        activitiesG = new Gson().fromJson(jsonString, new TypeToken<HashMap<String,ArrayList<HashMap<String,String>>>>(){}.getType());

        String attractionNamesString = loadJSON(this, "attractionNames.json");
        attractionDataHash = new Gson().fromJson(attractionNamesString, new TypeToken<HashMap<String,HashMap<String, String>>>(){}.getType());

        attractions = new ArrayList<>();
        attractionNames = new ArrayList<>();
        chineseAttractionNames = new ArrayList<>();

        // Process attraction data from other JSON files
        for(String key : attractionDataHash.keySet()) {
            if(!key.equals("mbs")) {
                attractions.add(key);
                attractionNames.add(attractionDataHash.get(key).get("name"));
                chineseAttractionNames.add(attractionDataHash.get(key).get("chinese"));
            }
        }

        attractionsList = (ListView)findViewById(R.id.attractionsList);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, new ArrayList<String>());

        attractionsList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        attractionsList.setAdapter(adapter);
<<<<<<< Updated upstream
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(this);
        String royalTheme = getString(R.string.checkBoxYellow);
        boolean isRoyalTheme = sharedPref.getBoolean(royalTheme,false);
        changeTheme(isRoyalTheme);
    }
    private void changeTheme (boolean ifTheme){
        LinearLayout tsplayout = (LinearLayout) findViewById(R.id.tsplayout);
        if (ifTheme){
            //myTheme = R.style.YellowTheme;
            tsplayout.setBackgroundResource(R.color.whiteYellow);

        }else{
            //myTheme = R.style.AppRedTheme;
            tsplayout.setBackgroundResource(R.color.whiteRed);
        }
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("checkBoxYellow")){
            boolean checked = sharedPreferences.getBoolean(s,false);
            changeTheme(checked);
        }
=======

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPref.registerOnSharedPreferenceChangeListener(this);
        String Chinese = getString(R.string.checkBoxChinese);
        boolean isChinese = sharedPref.getBoolean(Chinese,false);
        changeLanguage(isChinese);
    }

    public void changeLanguage(boolean ifChinese){
        // Update ArrayAdapter
        adapter.clear();
        EditText budgetText = findViewById(R.id.budgetText);
        CheckBox wantOptimal = findViewById(R.id.wantOptimal);
        Button findButton = findViewById(R.id.findButton);

        if(ifChinese) {
            adapter.addAll(chineseAttractionNames);
            budgetText.setHint(R.string.budgetTextChinese);
            wantOptimal.setText(R.string.wantOptimalChinese);
            findButton.setText(R.string.findStrChinese);
        }
        else {
            adapter.addAll(attractionNames);
            budgetText.setHint(R.string.budgetText);
            wantOptimal.setText(R.string.wantOptimal);
            findButton.setText(R.string.findStr);
        }
        adapter.notifyDataSetChanged();
>>>>>>> Stashed changes
    }



    public void findRoute(View view) {
        // Find all attractions the user wants to visit
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
        bestCost.clear();
        bestTime.clear();

        // Initialization: Assume user starts at MBS
        String startLocation = "mbs";
        ArrayList<String> path = new ArrayList<>();
        ArrayList<Integer> timeArray = new ArrayList<>();
        ArrayList<Double> cost = new ArrayList<>();
        path.add(startLocation);

        EditText budgetText = (EditText)findViewById(R.id.budgetText);
        String budgetStr = budgetText.getText().toString();

        CheckBox wantOptimalCheckbox = (CheckBox)findViewById(R.id.wantOptimal);
        boolean wantOptimal = wantOptimalCheckbox.isChecked();

        if(isNumeric(budgetStr)) {
            double budget = Double.valueOf(budgetStr);

            // Determine which method to use based on user selection
            // Or if the number of attractions is low, we use the brute force solution
            if(wantOptimal)// || path.size() - 2 <= BRUTE_THRESHOLD) // We deduct 2 because we already know we start and end at mbs
            {
                // Time duration taken
                long started = System.nanoTime();
                long time = System.nanoTime();
                findBestPathBrute(attractionsToVisit, path, cost, timeArray, startLocation, "mbs", budget, 0);
                System.out.println("Minimum time (brute force): " + minTime);
                System.out.println(bestPath);
                long timeTaken= time - started;
                System.out.println("Time:" + timeTaken/1000000.0 + "ms");
            }
            else {
                // Nearest Neighbour approximation
                long started = System.nanoTime();
                findBestPathNN(attractionsToVisit, path, cost, timeArray, startLocation, "mbs", budget, 0);
                System.out.println("Minimum time (NN approximation): " + minTime);
                System.out.println(bestPath);
                long time = System.nanoTime();
                long timeTaken= time - started;
                System.out.println("Time:" + timeTaken/1000000.0 + "ms");
            }

            // Prepare results for result display view
            Intent intent = new Intent(this, TSPResultsActivity.class);
            String[] bestPathArray = bestPath.toArray(new String[bestPath.size()]);
            Double[] bestCostArray = bestCost.toArray(new Double[bestCost.size()]);
            Integer[] bestTimeArray = bestTime.toArray(new Integer[bestTime.size()]);
            String[] attractionNamesArray = new String[bestPath.size()];
            String[] attractionChineseNamesArray = new String[bestPath.size()];

            // Pass latLng values to map
            for(int i = 0; i < bestPathArray.length; i++) {
                HashMap<String, String> attraction = attractionDataHash.get(bestPathArray[i]);
                if(attraction != null) {
                    String latLng = attraction.get("latLng");
                    bestPathArray[i] = latLng;

                    if(i % 2 == 0) {
                        String attractionName = attraction.get("name");
                        attractionNamesArray[i/2] = attractionName;
                        attractionChineseNamesArray[i/2] = attraction.get("chinese");
                    }
                }
            }

            int[] bestPrimitiveTimeArray = new int[bestTimeArray.length];
            double[] bestPrimitiveCostArray = new double[bestCostArray.length];

            for(int i = 0; i < bestTimeArray.length; i ++ ) {
                bestPrimitiveTimeArray[i] = bestTimeArray[i];
                bestPrimitiveCostArray[i] = bestCostArray[i];
            }

            // Pass results to result display view
            intent.putExtra("results", bestPathArray);
            intent.putExtra("time", bestPrimitiveTimeArray);
            intent.putExtra("cost", bestPrimitiveCostArray);
            intent.putExtra("names", attractionNamesArray);
            intent.putExtra("chineseNames", attractionChineseNamesArray);
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

    public void findBestPathBrute(ArrayList<String> attractions, ArrayList<String> path, ArrayList<Double> cost, ArrayList<Integer> time, String previous, String finalLocation, double budget, int currentTime) {
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
            ArrayList<Double> newCost = (ArrayList<Double>)cost.clone();
            ArrayList<Integer> newTime = (ArrayList<Integer>)time.clone();

            for(HashMap<String, String> neighbour : neighbours) {
                if(neighbour.get("name").equals(finalLocation)) {
                    // Find fastest way back that is within budget
                    double taxiCost = Double.valueOf(neighbour.get("taxi_cost"));
                    double publicCost = Double.valueOf(neighbour.get("public_cost"));
                    if(taxiCost <= budget) {
                        int taxiTime =  Integer.valueOf(neighbour.get("taxi_time"));
                        finalTime = currentTime + taxiTime;
                        finalBudget -= taxiCost;
                        newPath.add("taxi");
                        newCost.add(taxiCost);
                        newTime.add(taxiTime);
                    }
                    else if(publicCost <= budget) {
                        int publicTime = Integer.valueOf(neighbour.get("public_time"));
                        finalTime = currentTime + publicTime;
                        finalBudget -= publicCost;
                        newPath.add("public");
                        newCost.add(publicCost);
                        newTime.add(publicTime);
                    }
                    else {
                        int footTime = Integer.valueOf(neighbour.get("foot_time"));
                        finalTime = currentTime + footTime;
                        newPath.add("foot");
                        newCost.add(0.0);
                        newTime.add(footTime);
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
                bestCost = newCost;
                bestTime = newTime;
            }
        }
        else {
            // Try all routes
            for(int i = 0; i < attractions.size(); i ++) { // Loop V times
                ArrayList<String> newAttractions = (ArrayList<String>)attractions.clone();
                String newLocation = newAttractions.get(i);
                newAttractions.remove(newLocation);

                // Cost: O(V)
                for(HashMap<String, String> neighbour : activitiesG.get(previous)) {
                    if(neighbour.get("name").equals(newLocation)) {
                        // Try all 3 possible modes of transport
                        double taxiCost = Double.valueOf(neighbour.get("taxi_cost"));
                        if(taxiCost <= budget) {
                            double currentBudget = budget - taxiCost;
                            int taxiTime = Integer.valueOf(neighbour.get("taxi_time"));
                            ArrayList<String> newTaxiPath = (ArrayList<String>)path.clone();
                            ArrayList<Double> newCost = (ArrayList<Double>)cost.clone();
                            ArrayList<Integer> newTime = (ArrayList<Integer>)time.clone();

                            newTaxiPath.add("taxi");
                            newTaxiPath.add(newLocation);
                            newCost.add(taxiCost);
                            newTime.add(taxiTime);

                            findBestPathBrute(newAttractions, newTaxiPath, newCost, newTime, newLocation, finalLocation, currentBudget, currentTime+taxiTime);
                        }

                        double publicCost = Double.valueOf(neighbour.get("public_cost"));
                        if(publicCost <= budget) {
                            double currentBudget = budget - publicCost;
                            int publicTime = Integer.valueOf(neighbour.get("public_time"));
                            ArrayList<String> newPublicPath = (ArrayList<String>)path.clone();
                            ArrayList<Double> newCost = (ArrayList<Double>)cost.clone();
                            ArrayList<Integer> newTime = (ArrayList<Integer>)time.clone();

                            newPublicPath.add("public");
                            newPublicPath.add(newLocation);
                            newCost.add(publicCost);
                            newTime.add(publicTime);

                            findBestPathBrute(newAttractions, newPublicPath, newCost, newTime, newLocation, finalLocation, currentBudget, currentTime+publicTime);
                        }

                        ArrayList<String> newPath = (ArrayList<String>)path.clone();
                        ArrayList<Double> newCost = (ArrayList<Double>)cost.clone();
                        ArrayList<Integer> newTime = (ArrayList<Integer>)time.clone();

                        int footTime = Integer.valueOf(neighbour.get("foot_time"));
                        newPath.add("foot");
                        newPath.add(newLocation);
                        newCost.add(0.0);
                        newTime.add(footTime);

                        findBestPathBrute(newAttractions, newPath, newCost, newTime, newLocation, finalLocation, budget, currentTime+footTime);

                        break;
                    }
                }
            }
        }
    }

    public void findBestPathNN(ArrayList<String> attractions, ArrayList<String> path, ArrayList<Double> cost, ArrayList<Integer> time, String previous, String finalLocation, double budget, int currentTime) {
        // Approximate a solution to visit all attractions using the nearest neighbour algorithm
        // Initialization
        minTime = -1;
        budgetRemaining = 0;
        bestPath.clear();
        bestCost.clear();
        bestTime.clear();

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
                int taxiTime = Integer.valueOf(nearestNeighbour.get("taxi_time"));
                currentTime += taxiTime;
                budget -= taxiCost;
                path.add("taxi");
                cost.add(taxiCost);
                time.add(taxiTime);
            }
            else if(publicCost <= budget) {
                int publicTime = Integer.valueOf(nearestNeighbour.get("public_time"));
                currentTime += publicTime;
                budget -= publicCost;
                path.add("public");
                cost.add(publicCost);
                time.add(publicTime);
            }
            else {
                int footTime = Integer.valueOf(nearestNeighbour.get("foot_time"));
                currentTime += footTime;
                path.add("foot");
                cost.add(0.0);
                time.add(footTime);
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
        ArrayList<Integer> newTime = (ArrayList<Integer>)time.clone();
        ArrayList<Double> newCost = (ArrayList<Double>)cost.clone();

        ArrayList<HashMap<String,String>> neighbours = activitiesG.get(previous);
        for(HashMap<String, String> neighbour : neighbours) {
            if(neighbour.get("name").equals(finalLocation)) {
                // Find fastest way back that is within budget
                double taxiCost = Double.valueOf(neighbour.get("taxi_cost"));
                double publicCost = Double.valueOf(neighbour.get("public_cost"));
                if(taxiCost <= budget) {
                    int taxiTime = Integer.valueOf(neighbour.get("taxi_time"));
                    finalTime = currentTime + taxiTime;
                    finalBudget -= taxiCost;
                    newPath.add("taxi");
                    newTime.add(taxiTime);
                    newCost.add(taxiCost);
                }
                else if(publicCost <= budget) {
                    int publicTime = Integer.valueOf(neighbour.get("public_time"));
                    finalTime = currentTime + publicTime;
                    finalBudget -= publicCost;
                    newPath.add("public");
                    newTime.add(publicTime);
                    newCost.add(publicCost);
                }
                else {
                    int footTime = Integer.valueOf(neighbour.get("foot_time"));
                    finalTime = currentTime + footTime;
                    newPath.add("foot");
                    newTime.add(footTime);
                    newCost.add(0.0);
                }

                break;
            }
        }

        newPath.add(finalLocation);

        // Set output to this
        minTime = finalTime;
        budgetRemaining = budget;
        bestPath = newPath;
        bestTime = newTime;
        bestCost = newCost;
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
        if (s.equals(getString(R.string.checkBoxChinese))){
            changeLanguage(sharedPreferences.getBoolean(s,false));
        }
    }
}
