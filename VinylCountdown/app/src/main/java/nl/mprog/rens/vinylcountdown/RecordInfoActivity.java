package nl.mprog.rens.vinylcountdown;

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

public class RecordInfoActivity extends AppCompatActivity {

    String artist;
    String title;
    String summary;

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

        TextView artistTV = (TextView) findViewById(R.id.artistSell);
        artistTV.setText(artist);

        TextView titleTV = (TextView) findViewById(R.id.recordSell);
        titleTV.setText(title);

        // Set the summary and its scroller.
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

    public void backToSale(View view) {
        finish();
    }
}
