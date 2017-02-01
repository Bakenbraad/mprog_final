package nl.mprog.rens.vinylcountdown.AdapterClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import nl.mprog.rens.vinylcountdown.ObjectClasses.NewsItem;
import nl.mprog.rens.vinylcountdown.R;

/**
 * Rens van der Veldt - 10766162
 * Minor Programmeren
 *
 * CustomNewsAdapter.class
 *
 * This adapter displays the news items in a nice way. Takes a list of NewsItems and fills
 * the views with their data.
 * Constructed from: https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView
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

        // Assign the view we are converting to a local variable
        View v = convertView;

        // First check to see if the view is null. if so, we have to inflate it.
        // To inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.news_item, null);
        }

        final NewsItem i = objects.get(position);

        if (i != null) {

            // Find the views and set the appropriate values.
            TextView authorTV = (TextView) v.findViewById(R.id.news_author);
            TextView titleTV = (TextView) v.findViewById(R.id.news_title);

            authorTV.setText(i.getAuthor());
            titleTV.setText(i.getTitle());
        }

        // The view must be returned to our activity
        return v;

    }
}


