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

import org.w3c.dom.Text;

import java.util.List;

/**
 * Rens van der Veldt - 10766162
 * Minor Programmeren
 *
 * CustomMarketAdapter.class
 *
 * This adapter displays the marketplace data in a nice way. It puts all relevant data from
 * a recordSaleInfo object into place.
 * Constructed from: https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView
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

        // Assign the view we are converting to a local variable
        View v = convertView;

        // First check to see if the view is null. if so, we have to inflate it.
        // To inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.record_market_item, null);
        }

        final RecordSaleInfo i = objects.get(position);

        if (i != null) {

            // Find the views.
            TextView artistTV = (TextView) v.findViewById(R.id.marketArtist);
            TextView titleTV = (TextView) v.findViewById(R.id.marketTitle);
            RatingBar conditionTV = (RatingBar) v.findViewById(R.id.marketCondition);
            TextView priceTV = (TextView) v.findViewById(R.id.marketPrice);
            TextView priceTypeTV = (TextView) v.findViewById(R.id.marketPriceType);
            TextView timeTV = (TextView) v.findViewById(R.id.marketTime);

            // This view has to be declared final because it is assigned a text from a listener.
            final TextView userTV = (TextView) v.findViewById(R.id.marketUsername);

            // Set the values.
            artistTV.setText(i.getArtist());
            titleTV.setText(i.getTitle());
            timeTV.setText(i.getTimeCreated());
            conditionTV.setRating(i.getCondition());

            // Download and set the image for the album:
            ImageView imageView = (ImageView) v.findViewById(R.id.marketImage);
            new AsyncImgLoad(imageView).execute(i.getImgLink());

            // Get the current selling user for display purposes.
            DatabaseReference mSettingsReference = FirebaseDatabase.getInstance().getReference().child("users").child(i.getUserID());

            ValueEventListener settingsListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    // Get UserSettings object and use the values to update the view
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

            // Set the appropriate price type.
            if (i.priceType.equals("Trade")){
                priceTV.setText(R.string.trade);
            }else if(i.priceType.equals("Bidding from")){
                priceTypeTV.setText(R.string.bidding);
                priceTV.setText("$ " + i.getPrice());
            }else{
                priceTV.setText("$ " + i.getPrice());
            }
        }

        // The view must be returned to our activity
        return v;

    }
}


