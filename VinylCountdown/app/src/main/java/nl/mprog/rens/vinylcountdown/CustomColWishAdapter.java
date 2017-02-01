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
 * Created by Rens on 16/01/2017.
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

        // assign the view we are converting to a local variable
        View v = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.record_item, null);
        }

        ColWishRecord i = objects.get(position);

        if (i != null) {

            // This is how you obtain a reference to the TextViews.
            // These TextViews are created in the XML files we defined.

            TextView artist = (TextView) v.findViewById(R.id.artistTV);
            TextView title = (TextView) v.findViewById(R.id.titleTV);

            artist.setText(i.getRecordInfo().getArtist());
            title.setText(i.getRecordInfo().getTitle());

            // Download and set the image for the album:
            ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
            new AsyncImgLoad(imageView).execute(i.getRecordInfo().getImgLinkmed());
        }

        // the view must be returned to our activity
        return v;

    }
}


