package nl.mprog.rens.vinylcountdown;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by Rens on 16/01/2017.
 */

public class CustomMarketAdapter extends ArrayAdapter<RecordSaleInfo> {

    // List of items
    private List<RecordSaleInfo> objects;

    // Constructor
    public CustomMarketAdapter(Context context, int textViewResourceId, List<RecordSaleInfo> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
    }


    public View getView(int position, View convertView, ViewGroup parent){

        // assign the view we are converting to a local variable
        View v = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.record_market_item, null);
        }

        final RecordSaleInfo i = objects.get(position);

        if (i != null) {

            // Find the views and set the appropriate values.
            TextView artistTV = (TextView) v.findViewById(R.id.marketArtist);
            TextView titleTV = (TextView) v.findViewById(R.id.marketTitle);
            RatingBar conditionTV = (RatingBar) v.findViewById(R.id.marketCondition);
            TextView priceTV = (TextView) v.findViewById(R.id.marketPrice);
            TextView priceTypeTV = (TextView) v.findViewById(R.id.marketPriceType);
            final TextView userTV = (TextView) v.findViewById(R.id.marketUsername);

            artistTV.setText(i.getArtist());
            titleTV.setText(i.getTitle());

            // Get the current selling user.
            DatabaseReference mSettingsReference = FirebaseDatabase.getInstance().getReference().child("users").child(i.getUserID());
            // User is signed in
            ValueEventListener settingsListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get UserSettings object and use the values to update the UI
                    UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                    if (userProfile != null) {
                        userTV.setText(userProfile.getUsername());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mSettingsReference.addValueEventListener(settingsListener);

            // Download and set the image for the album:
            ImageView imageView = (ImageView) v.findViewById(R.id.marketImage);
            new AsyncTaskImgDownload(imageView).execute(i.getImgLink());

            conditionTV.setRating(i.getCondition());
            if (i.priceType.equals("Trade")){
                priceTV.setText("Trade");
            }else if(i.priceType.equals("Bidding from")){
                priceTypeTV.setText("Bidding");
                priceTV.setText("$ " +i.getPrice());
            }else{
                priceTV.setText("$ " +i.getPrice());
            }
        }

        // the view must be returned to our activity
        return v;

    }
}


