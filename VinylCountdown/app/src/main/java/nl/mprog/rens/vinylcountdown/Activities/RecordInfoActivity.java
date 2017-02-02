package nl.mprog.rens.vinylcountdown.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import nl.mprog.rens.vinylcountdown.AdapterClasses.CustomTrackAdapter;
import nl.mprog.rens.vinylcountdown.ObjectClasses.RecordInfo;
import nl.mprog.rens.vinylcountdown.R;

/**
 * Rens van der Veldt - 10766162
 * Minor Programmeren
 *
 * RecordInfoActivity.class
 *
 * This dialog theme activity displays a records wiki summary (if available otherwise "not available")
 * and the tracks on that album. The record concerning is passed through intent and is used to display
 * title artist and summary. The tracks require an adapter to be displayed in a listview. The activity
 * can be cancelled by closing using a button.
 */

public class RecordInfoActivity extends AppCompatActivity {

    // Declare values.
    String artist;
    String title;
    String summary;

    // Initialize hashmap for tracks.
    Map<String,String> tracks = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_info);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        // Get basic record info.
        RecordInfo recordBundle = (RecordInfo) bundle.getSerializable("recordInfo");
        artist = recordBundle.getArtist();
        title = recordBundle.getTitle();
        summary = recordBundle.getSummary();
        tracks = recordBundle.getTracks();

        // Set the artist.
        TextView artistTV = (TextView) findViewById(R.id.artistSell);
        artistTV.setText(artist);

        // Set the title.
        TextView titleTV = (TextView) findViewById(R.id.recordSell);
        titleTV.setText(title);

        // Set the summary and its scroller in order to not crop the activity..
        TextView summaryTV = (TextView) findViewById(R.id.summarySell);
        summaryTV.setText(summary);
        summaryTV.setScroller(new Scroller(this));
        summaryTV.setMaxLines(8);
        summaryTV.setVerticalScrollBarEnabled(true);
        summaryTV.setMovementMethod(new ScrollingMovementMethod());

        // Put the tracks in a listview.
        CustomTrackAdapter customTrackAdapter = new CustomTrackAdapter(tracks);
        ListView listView = (ListView) findViewById(R.id.rightTracks);
        listView.setAdapter(customTrackAdapter);
    }

    /**
     * Closes the record info.
     * @param view: passed from a button.
     */
    public void backToSale(View view) {
        finish();
    }
}
