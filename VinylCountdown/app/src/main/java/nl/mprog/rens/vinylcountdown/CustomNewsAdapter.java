package nl.mprog.rens.vinylcountdown;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Rens on 16/01/2017.
 */

public class CustomNewsAdapter extends ArrayAdapter<NewsItem> {

    // List of items
    private List<NewsItem> objects;

    // Constructor
    public CustomNewsAdapter(Context context, int textViewResourceId, List<NewsItem> objects) {
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
            v = inflater.inflate(R.layout.news_item, null);
        }

        final NewsItem i = objects.get(position);

        if (i != null) {

            // Find the views and set the appropriate values.
            TextView authorTV = (TextView) v.findViewById(R.id.news_author);
            TextView titleTV = (TextView) v.findViewById(R.id.news_title);
            TextView contentTV = (TextView) v.findViewById(R.id.news_content);

            authorTV.setText(i.getAuthor());
            titleTV.setText(i.getTitle());
            contentTV.setText(i.getContent());
        }

        // the view must be returned to our activity
        return v;

    }
}


