package com.example.android.navigatour;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class RecyclerActivity extends AppCompatActivity {

    ArrayList<AnimeJsonData> animeJsonData = new ArrayList<>();
//    private ArrayList<>
    private RecyclerView animeList;
    private AnimeAdapter mAnimeAdapter;
    private ArrayList<HashMap<String,String>> data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        data = (ArrayList<HashMap<String,String>>) getIntent().getSerializableExtra("RESTAURANTS");
        int counter = 0;
        AnimeJsonData tempData;
        ArrayList<String[]> imageUrl1Array = new ArrayList<>();

        //Adding data to animejsondata array
        while (counter < data.size()){
            tempData = new AnimeJsonData(data.get(counter));
            String[] imageUrl1 = tempData.getImageUrl();
            imageUrl1Array.add(imageUrl1);
            animeJsonData.add(tempData);
            counter++;
        }
        URL[][] urlss = new URL[data.size()][5];
        counter = 0;
        URL[] urls;
        while (counter < data.size()) {
            urls = convertStringToUrl(imageUrl1Array.get(counter));
            urlss[counter] = urls;
            counter++;
        }
        GetImageTask getImageTask = new GetImageTask();
        getImageTask.execute(urlss);

        //TODO 4.9 get a reference to the recycler view widget
        animeList = findViewById(R.id.recyclerView);
        //TODO 4.10 create an instance of LinearLayoutManager and
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        animeList.setLayoutManager(linearLayoutManager);
        //          assign it to the recycler view object
        //TODO 4.11 create an instance of the Adapter and
        //          assign it to the recycler view object
        mAnimeAdapter = new AnimeAdapter(this, animeJsonData);
        animeList.setAdapter(mAnimeAdapter);

    }

    public AnimeJsonData[] updateData(Bitmap[]...bitmaps){
        int counter = 0;
        AnimeJsonData[] animeJsonData1= new AnimeJsonData[animeJsonData.size()];
        while (counter < animeJsonData.size()){
            AnimeJsonData temp = animeJsonData.get(counter);
            temp.updateBitmap(bitmaps[counter]);
            animeJsonData1[counter] = temp;
            counter++;
        }
        return animeJsonData1;

    }

    //TODO 3.1 Create an inner class matching the keys of the JSON array
    public class AnimeJsonData{

        String name;
        String address;
        String openingtimes;
        String mealprice;
        String website;
        String[] imageurl;
        Bitmap[] imageBitmap;

        public AnimeJsonData(HashMap<String,String> data){
            this.name = data.get("name");
            this.address = data.get("address");
            this.openingtimes = data.get("openingtimes");
            this.mealprice = data.get("mealprice");
            this.website = data.get("website");
            this.imageurl = data.get("imageurl").split(" ");
            this.imageBitmap = new Bitmap[imageurl.length];
        }



        public String getName() {
            return name;
        }

        public String getAddress() {
            return address;
        }

        public String getOpeningtimes() {
            return openingtimes;
        }

        public String getMealprice() {
            return mealprice;
        }

        public String getWebsite() {
            return website;
        }
        public String[] getImageUrl(){
            return this.imageurl;
        }
        public String getImageurlAtIndex(int i){
            if (i >= imageurl.length){
                Log.i("RecyclerClass", "Index out of bounds");
                return null;
            }
            return imageurl[i];
        }
        public Bitmap[] getImageBitmap(){
            return imageBitmap;
        }
        public void updateBitmap(Bitmap[] imageBitmap){
            this.imageBitmap = imageBitmap;
        }
    }

    private URL[] convertStringToUrl(String[] strings){
        URL[] urls = new URL[strings.length];
        String tempStrings;
        int counter = 0;
//        int counter2 = 0;
        try {
            while (counter < strings.length) {
                tempStrings = strings[counter];
                    urls[counter] = new URL(strings[counter]);
                    counter++;
                }
        } catch (MalformedURLException e){
            e.printStackTrace();
        }

        return urls;

    }

    public class GetImageTask extends AsyncTask<URL[], Void, Bitmap[][]> {
        @Override
        protected Bitmap[][] doInBackground(URL[]... urls){
            Bitmap[][] foodPic = new Bitmap[urls.length][4]; //Create new array of bitmap with length depending on the url
            int i = 0;
            int j = 0;
            URL[] tempurl;
            while (i < urls.length) {
                tempurl = urls[i];
                j = 0;
                while (j < 4 && j < urls[i].length) {
                    try {
                        Log.i("URL",urls[i][j].toString());
                        InputStream in = urls[i][j].openStream();
                        foodPic[i][j] = BitmapFactory.decodeStream(in);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    j++;
                }
                i++;
                Log.i("I",String.valueOf(i) + " out of " + urls.length);
            }

            return foodPic;
        }

        @Override
        protected void onPostExecute(Bitmap[][] foodPic){
            Log.i("POST","Post executing...");
            Toast.makeText(RecyclerActivity.this,"I'm at postexecute", Toast.LENGTH_SHORT).show();
            AnimeJsonData[] data = updateData(foodPic);
            mAnimeAdapter.update(data);
            mAnimeAdapter.notifyDataSetChanged();
        }
    }
}