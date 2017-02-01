package nl.mprog.rens.vinylcountdown;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Rens van der Veldt - 10766162
 * Minor Programmeren
 *
 * CustomAlbumAdapter.class
 *
 * This adapter displays the recordinfo data in a nice way.
 * Constructed from: https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView
 */

public class CustomAlbumAdapter extends ArrayAdapter<RecordInfo> {

    // List of items
    private List<RecordInfo> objects;

    // Constructor
    public CustomAlbumAdapter(Context context, int textViewResourceId, List<RecordInfo> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
    }


    public View getView(int position, View convertView, ViewGroup parent){

        // Make the view a local variable.
        View v = convertView;

        // First check to see if the view is null. if so, we have to inflate it.
        // To inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.record_item, null);
        }

        RecordInfo i = objects.get(position);

        if (i != null) {

            // Set the object properties to the textviews.
            TextView artist = (TextView) v.findViewById(R.id.artistTV);
            TextView title = (TextView) v.findViewById(R.id.titleTV);

            artist.setText(i.getArtist());
            title.setText(i.getTitle());

            // Download and set the image for the album:
            ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
            new AsyncImgLoad(imageView).execute(i.getImgLinkmed());
        }

        // The view must be returned to our activity
        return v;

    }
}


