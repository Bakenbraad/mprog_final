package nl.mprog.rens.vinylcountdown.AdapterClasses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import nl.mprog.rens.vinylcountdown.R;

/**
 * Rens van der Veldt - 10766162
 * Minor Programmeren
 *
 * CustomTrackAdapter.class
 *
 * This class is an extension of the baseadapter (in contrary to the arrayadapters extensions that are
 * the other custom adapters in this project) and displays keys and values from a hashmap. In this case
 * the keys are track titles and the values their duration. These are then displayed in a nice fashion.
 * From: http://stackoverflow.com/questions/19466757/hashmap-to-listview
 */

public class CustomTrackAdapter extends BaseAdapter {
    private final ArrayList mData;

    public CustomTrackAdapter(Map<String, String> map) {
        mData = new ArrayList();
        mData.addAll(map.entrySet());
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Map.Entry<String, String> getItem(int position) {
        return (Map.Entry) mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;

        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_item, parent, false);
        } else {
            result = convertView;
        }

        Map.Entry<String, String> item = getItem(position);

        ((TextView) result.findViewById(R.id.trackTitle)).setText(item.getKey());
        ((TextView) result.findViewById(R.id.trackDuration)).setText(item.getValue());

        return result;
    }
}