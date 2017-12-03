package com.example.android.navigatour;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class RestaurantInfo extends AppCompatActivity {

    TextView textViewRestaurantName, textViewAddress, textViewWebsite, textViewOrderTime, textViewAveragePrice, textViewDescription;
    ImageView imageView1, imageView2, imageView3;
    ArrayList<String> restaurantArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_info);
        restaurantArray = (ArrayList<String>) getIntent().getSerializableExtra("RESTAURANT");
        String[] imageUrlStrings= (String[]) getIntent().getSerializableExtra("IMAGEURL");
//        getSupportActionBar().setTitle(restaurantClasses.get(0));
        getSupportActionBar().setHomeButtonEnabled(true);
        textViewRestaurantName = findViewById(R.id.name);
        textViewAddress = findViewById(R.id.address);
        textViewAveragePrice = findViewById(R.id.averagePrice);
        textViewOrderTime = findViewById(R.id.orderTime);
        textViewWebsite = findViewById(R.id.website);
        textViewDescription = findViewById(R.id.description);

        imageView1 = findViewById(R.id.bigpic);
        imageView2 = findViewById(R.id.smallpic1);
        imageView3 = findViewById(R.id.smallpic2);

        URL[] imageUrls = RecyclerActivity.convertStringToUrl(imageUrlStrings);

        GetImageTaskSpecific getImageTaskSpecific = new GetImageTaskSpecific();
        getImageTaskSpecific.execute(imageUrls);

//        String information = restaurantClasses.get(0) + "\n" + restaurantClasses.get(1) + "\n" +
//                restaurantClasses.get(2) + "\n" + restaurantClasses.get(3) + "\n" + restaurantClasses.get(4);
        textViewRestaurantName.setText(restaurantArray.get(0) + restaurantArray.get(6));
        textViewDescription.setText(restaurantArray.get(1));
        textViewAddress.setText(restaurantArray.get(2));
        textViewOrderTime.setText(restaurantArray.get(3));
        textViewAveragePrice.setText(restaurantArray.get(4));
        textViewWebsite.setText(restaurantArray.get(5));




    }

    public void DisplayMap(View view) {
        String address = restaurantArray.get(2);
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + address );
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);

    }

    public class GetImageTaskSpecific extends AsyncTask<URL[], Void, Bitmap[][]> {
        @Override
        protected Bitmap[][] doInBackground(URL[]... urls){
            Bitmap[][] foodPic = new Bitmap[urls.length][3]; //Create new array of bitmap with length depending on the url
            int i = 0;
            int j = 0;
            URL[] tempurl;
            while (i < urls.length) {
                tempurl = urls[i];
                j = 0;
                while (j < 3 && j < urls[i].length) {
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
            imageView1.setImageBitmap(foodPic[0][0]);
            imageView2.setImageBitmap(foodPic[0][1]);
            imageView3.setImageBitmap(foodPic[0][2]);
        }
    }
}
