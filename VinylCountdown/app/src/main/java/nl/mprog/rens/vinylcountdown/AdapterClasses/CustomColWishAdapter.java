package nl.mprog.rens.vinylcountdown.AdapterClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import nl.mprog.rens.vinylcountdown.ObjectClasses.ColWishRecord;
import nl.mprog.rens.vinylcountdown.HelperClasses.AsyncImgLoad;
import nl.mprog.rens.vinylcountdown.R;

/**
 * Rens van der Veldt - 10766162
 * Minor Programmeren
 *
 * CustomColWishAdapter.class
 *
 * This adapter displays the recordinfo data in a nice way. Exteremely similar to the customalbumadapter
 * but because the objects are different a separate adapter has been made available.
 * Constructed from: https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView
 */

public class CustomColWishAdapter extends ArrayAdapter<ColWishRecord> {

    // List of items
    private List<ColWishRecord> objects;

    // Constructor
    public CustomColWishAdapter(Context context, int textViewResourceId, List<ColWishRecord> objects) {
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
            v = inflater.inflate(R.layout.record_item, null);
        }

        ColWishRecord i = objects.get(position);

        if (i != null) {

            // Set the object properties to the textviews.
            TextView artist = (TextView) v.findViewById(R.id.artistTV);
            TextView title = (TextView) v.findViewById(R.id.titleTV);

            artist.setText(i.getRecordInfo().getArtist());
            title.setText(i.getRecordInfo().getTitle());

            // Download and set the image for the album:
            ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
            new AsyncImgLoad(imageView).execute(i.getRecordInfo().getImgLinkmed());
        }

        // The view must be returned to our activity
        return v;

    }
}


