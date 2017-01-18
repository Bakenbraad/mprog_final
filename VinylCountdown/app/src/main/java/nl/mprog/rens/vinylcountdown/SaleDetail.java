package nl.mprog.rens.vinylcountdown;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class SaleDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_detail);

        Intent intent = getIntent();
        Bundle recordBundle = intent.getExtras();

        // Set the info.
        String artist = recordBundle.getString("artist");
        String title = recordBundle.getString("title");
        String summary = recordBundle.getString("summary");
        String imgLink = recordBundle.getString("imgLink");
        String mbid = recordBundle.getString("mbid");


        TextView artistTV = (TextView) findViewById(R.id.artistDetail);
        TextView titleTV = (TextView) findViewById(R.id.titleDetail);
        artistTV.setText(artist);
        titleTV.setText(title);
        ImageView imageView = (ImageView) findViewById(R.id.imageDetail);
        new DownloadImageTask(imageView).execute(imgLink);

        // Set the adapter.
        Spinner dropdown = (Spinner)findViewById(R.id.spinner);
        String[] items = new String[]{"Price", "Bidding from", "Trade"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
    }
}
