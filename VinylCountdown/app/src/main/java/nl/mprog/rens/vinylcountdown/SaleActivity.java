package nl.mprog.rens.vinylcountdown;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import static nl.mprog.rens.vinylcountdown.R.id.imageView;

public class SaleActivity extends AppCompatActivity {

    String artist;
    String title;
    String summary;
    String imgLink;
    String mbid;
    Map<String,String> tracks = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);

        Intent intent = getIntent();
        Bundle recordBundle = intent.getExtras();

        // Get basic record info.
        artist = recordBundle.getString("artist");
        title = recordBundle.getString("title");
        summary = recordBundle.getString("summary");
        imgLink = recordBundle.getString("imgLink");
        mbid = recordBundle.getString("mbid");
        tracks = (Map<String, String>) recordBundle.getSerializable("tracks");

        // Set basic record info
        ImageView imageView = (ImageView) findViewById(R.id.largeImage);
        new DownloadImageTask(imageView).execute(imgLink);

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
        TrackAdapter trackAdapter = new TrackAdapter(tracks);
        ListView listView = (ListView) findViewById(R.id.rightTracks);
        listView.setAdapter(trackAdapter);
    }

    public void goToSaleDetail(View view){

        Intent goToSaleDetail = new Intent(this, SaleDetail.class);

        // Put the values into the next activity.
        goToSaleDetail.putExtra("title", title);
        goToSaleDetail.putExtra("artist", artist);
        goToSaleDetail.putExtra("summary", summary);
        goToSaleDetail.putExtra("imgLink", imgLink);
        goToSaleDetail.putExtra("mbid", mbid);

        startActivity(goToSaleDetail);
    }
}
