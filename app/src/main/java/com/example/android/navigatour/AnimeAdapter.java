package com.example.android.navigatour;

//TODO 4.1 (in a separate XML File) Design your list item layout

//TODO 4.2 go back to activity_main.xml and put in the recycler view widget

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class AnimeAdapter extends RecyclerView.Adapter<AnimeAdapter.AnimeViewHolder> {

//    private RecyclerActivity.AnimeJsonData[] data;
    private ArrayList<RecyclerActivity.AnimeJsonData> data;
    private static int viewHolderCount = 0;
    Context parentContext;

    //TODO 4.4 - Constructor
    AnimeAdapter(Context context, ArrayList<RecyclerActivity.AnimeJsonData> data){
        this.parentContext = context;
        this.data = data;
    }
    //TODO 4.5 - onCreateViewHolder
    //TODO 4.7 - onBindViewHolder
    //TODO 4.8 - getItemCount

    @Override
    public AnimeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflates the viewholder layout, instantiate the VH class
        int layoutIDForListItem = R.layout.cards_layout;
        LayoutInflater inflater = LayoutInflater.from(parentContext);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIDForListItem,parent,shouldAttachToParentImmediately);

        AnimeViewHolder animeViewHolder = new AnimeViewHolder(view);

        return animeViewHolder;
    }

    @Override
    public void onBindViewHolder(AnimeViewHolder holder, int position) {
        //Attach data to the widget
        holder.bind(position);
        //Download image from url
    }

    @Override
    public int getItemCount(){
        //Return the number of items
        return data.size();
    }

    public void update(RecyclerActivity.AnimeJsonData[] data){
        ArrayList<RecyclerActivity.AnimeJsonData> data2 = new ArrayList<>(Arrays.asList(data));
        this.data = data2;
    }


    class AnimeViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
        ImageView foodImageView;
        TextView restaurantTextView;
        View v;

        AnimeViewHolder(View v){
            super(v);
            this.v = v;
            //TODO 4.3 Invoke the superclass constructor
            // and get references to the various widgets in the List Item Layout
            v.setOnClickListener(this);
        }



        //TODO 4.6 - write a bind method to attach content
        //            to the respective widgets
        public void bind(int position){
//            String address = data.get(position).getAddress();
//            String openingtimes = data.get(position).getOpeningtimes();
//            String mealprice = data.get(position).getMealprice();
//            String website = data.get(position).getWebsite();
            //Get the data for restaurant and picture
            String restaurantName = data.get(position).getName();
            Bitmap[] pic = data.get(position).getImageBitmap();

            //get the widgets
            restaurantTextView = this.v.findViewById(R.id.text_view_restaurant);
            foodImageView = this.v.findViewById(R.id.image_view_food);

            if (pic[0] != null){
                foodImageView.setImageBitmap(pic[0]);
            }
            //attach data to widgets
            restaurantTextView.setText(restaurantName);


//            int resID = parentContext.getResources().getIdentifier(filename, typeOfResource, packageName);
            //TODO 4.6 B pass the resource ID to the image widget
//            picture.setImageResource(resID);

            //TODO 4.6 C pass the character name to the characterName widget
//            characterName.setText();

            //TODO 4.6 D display the position number
//            itemNumber.setText(String.valueOf(position));

        }



        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            AlertDialog.Builder builder
                    = new AlertDialog.Builder(parentContext);

        }
    }

    public void imageReady(Bitmap[] foodPic){

    }


}