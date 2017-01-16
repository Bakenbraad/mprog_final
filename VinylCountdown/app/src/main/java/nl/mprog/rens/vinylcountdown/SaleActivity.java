package nl.mprog.rens.vinylcountdown;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class SaleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);

        Intent intent = getIntent();
        Bundle recordBundle = intent.getExtras();

        RecordInfo recordInfo = (RecordInfo) recordBundle.get("recordInfo");

        // Get basic record info.
        String artist = recordInfo.getArtist();
        String title = recordInfo.getTitle();
        String summary = recordInfo.getSummary();
        String imgLink = recordInfo.getImgLinklarge();

        // Set basic record info
        ImageView imageView = (ImageView) findViewById(R.id.largeImage);
        new DownloadImageTask(imageView).execute(imgLink);

        TextView artistTV = (TextView) findViewById(R.id.artistSell);
        artistTV.setText(artist);

        TextView titleTV = (TextView) findViewById(R.id.recordSell);
        titleTV.setText(title);

        TextView summaryTV = (TextView) findViewById(R.id.summarySell);
        summaryTV.setText(summary);
    }
}
