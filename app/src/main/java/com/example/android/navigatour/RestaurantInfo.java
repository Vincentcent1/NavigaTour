package com.example.android.navigatour;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class RestaurantInfo extends AppCompatActivity {

    TextView textView;
    ImageView imageView1, imageView2, imageView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_info);
        ArrayList<String> animeJsonData = (ArrayList<String>) getIntent().getSerializableExtra("RESTAURANT");
        String[] imageUrlStrings= (String[]) getIntent().getSerializableExtra("IMAGEURL");

        textView = findViewById(R.id.text_view_information);
        imageView1 = findViewById(R.id.image_view_food1);
        imageView2 = findViewById(R.id.image_view_food2);
        imageView3 = findViewById(R.id.image_view_food3);

        URL[] imageUrls = RecyclerActivity.convertStringToUrl(imageUrlStrings);

        GetImageTaskSpecific getImageTaskSpecific = new GetImageTaskSpecific();
        getImageTaskSpecific.execute(imageUrls);

        String information = animeJsonData.get(0) + "\n" + animeJsonData.get(1) + "\n" +
                animeJsonData.get(2) + "\n" + animeJsonData.get(3) + "\n" + animeJsonData.get(4);
        textView.setText(information);

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
