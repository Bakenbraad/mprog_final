package nl.mprog.rens.vinylcountdown;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SaleDetail extends AppCompatActivity {

    String artist;
    String title;
    String summary;
    String imgLink;
    String mbid;
    Map<String,String> tracks = new HashMap<>();
    String[] items;
    EditText priceED;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_detail);

        Intent intent = getIntent();
        Bundle recordBundle = intent.getExtras();



        // Set the info.
        artist = recordBundle.getString("artist");
        title = recordBundle.getString("title");
        summary = recordBundle.getString("summary");
        imgLink = recordBundle.getString("imgLink");
        mbid = recordBundle.getString("mbid");
        tracks = (Map<String, String>) recordBundle.getSerializable("tracks");

        priceED = (EditText) findViewById(R.id.priceED);

        TextView artistTV = (TextView) findViewById(R.id.artistDetail);
        TextView titleTV = (TextView) findViewById(R.id.titleDetail);
        artistTV.setText(artist);
        titleTV.setText(title);
        ImageView imageView = (ImageView) findViewById(R.id.imageDetail);
        new DownloadImageTask(imageView).execute(imgLink);

        // Set the adapter.
        final Spinner dropdown = (Spinner)findViewById(R.id.spinner);
        items = new String[]{"Price", "Bidding from", "Trade"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(items[position].equals("Trade")){
                    priceED.setVisibility(View.INVISIBLE);
                }
                else {
                    priceED.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void moreInfo(View view){

        Intent goToSaleDetail = new Intent(this, SaleActivity.class);

        // Put the values into the next activity.
        goToSaleDetail.putExtra("title", title);
        goToSaleDetail.putExtra("artist", artist);
        goToSaleDetail.putExtra("summary", summary);
        goToSaleDetail.putExtra("tracks", (Serializable) tracks);

        startActivity(goToSaleDetail);
    }

    public void createSale (View view){

        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        Spinner saleTypeSP = (Spinner) findViewById(R.id.spinner);
        EditText descriptionED = (EditText) findViewById(R.id.descriptionED);

        float price;
        float condition = ratingBar.getRating();
        String description = descriptionED.getText().toString();
        String saleType = saleTypeSP.getSelectedItem().toString();
        if (!saleType.equals("Trade")){
            price = Float.parseFloat(priceED.getText().toString());
        }
        else {
            price = Float.parseFloat("0.0");
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && description.length() != 0 && condition != 0 && !saleType.equals("")){

            // Create the object.
            RecordSaleInfo recordSaleInfo = new RecordSaleInfo(mbid, description, price, saleType, condition, user.getUid(), imgLink, artist, title);

            // Write it to the Database and the user-offer relation table.
            DatabaseReference mUsersReference = FirebaseDatabase.getInstance().getReference();
            mUsersReference.child("marketplace").child("offers").child(recordSaleInfo.getSaleUID()).setValue(recordSaleInfo);

            // Back to search
            Intent goToMain = new Intent(this, SaleSearchActivity.class);
            goToMain.putExtra("method","saleSearch");
            startActivity(goToMain);
            finish();
        }
        else {
            Toast.makeText(this, "One or more of your fields is not complete", Toast.LENGTH_LONG);
        }



    }

    public void backToSearch(View view) {
        finish();
    }
}
