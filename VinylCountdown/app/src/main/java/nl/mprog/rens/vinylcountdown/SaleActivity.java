package nl.mprog.rens.vinylcountdown;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import java.util.HashMap;
import java.util.Map;

public class SaleActivity extends AppCompatActivity {

    String artist;
    String title;
    String summary;
    String imgLink;
    String mbid;
    Map<String,String> tracks = new HashMap<>();
    String[] items;
    EditText priceED;
    RecordInfo recordInfo;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        // Get basic record info.
        recordInfo = (RecordInfo) bundle.getSerializable("recordInfo");

        artist = recordInfo.getArtist();
        title = recordInfo.getTitle();
        summary = recordInfo.getSummary();
        tracks = recordInfo.getTracks();
        imgLink = recordInfo.getImgLinklarge();
        mbid = recordInfo.getMbid();

        priceED = (EditText) findViewById(R.id.priceED);

        TextView artistTV = (TextView) findViewById(R.id.artistDetail);
        TextView titleTV = (TextView) findViewById(R.id.titleDetail);
        final TextView priceTV = (TextView) findViewById(R.id.textView8);

        artistTV.setText(artist);
        titleTV.setText(title);
        ImageView imageView = (ImageView) findViewById(R.id.imageDetail);
        new AsyncImgLoad(imageView).execute(imgLink);

        // Set the spinner adapter.
        final Spinner dropdown = (Spinner)findViewById(R.id.spinner);
        items = new String[]{"Price", "Bidding", "Trade"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(items[position].equals("Trade")){
                    priceTV.setText("Trading");
                    priceED.setVisibility(View.INVISIBLE);
                }
                else if (items[position].equals("Price")){
                    priceTV.setText("Price: $");
                    priceED.setVisibility(View.VISIBLE);
                }
                else if (items[position].equals("Bidding")){
                    priceTV.setText("Asking price: $");
                    priceED.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void moreInfo(View view){

        Intent goToSaleDetail = new Intent(this, RecordInfoActivity.class);

        // Put the values into the next activity.
        goToSaleDetail.putExtra("recordInfo", recordInfo);

        startActivity(goToSaleDetail);
    }

    public void createSale (View view){

        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        Spinner saleTypeSP = (Spinner) findViewById(R.id.spinner);
        EditText descriptionED = (EditText) findViewById(R.id.descriptionED);

        final float price;
        final float condition = ratingBar.getRating();
        final String description = descriptionED.getText().toString();
        String saleType = saleTypeSP.getSelectedItem().toString();

        // Hotfix for string mismatch
        if (saleType.equals("Bidding")){
            saleType = "Bidding from";
        }

        if (!saleType.equals("Trade")){
            price = Float.parseFloat(priceED.getText().toString());
        }
        else {
            price = Float.parseFloat("0.0");
        }

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && description.length() != 0 && condition != 0 && !saleType.equals("")){

            // Throw a dialog to confirm the addition.
            final String finalSaleType = saleType;
            new AlertDialog.Builder(this)
                    .setTitle("Confirm sale")
                    .setIcon(R.drawable.logo)
                    .setMessage("Do you want to sell " + recordInfo.getTitle() + " by " + recordInfo.getArtist() + "?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {

                            // Create the object.
                            RecordSaleInfo recordSaleInfo = new RecordSaleInfo(mbid, description, price, finalSaleType, condition, user.getUid(), imgLink, artist, title);

                            // Write it to the Database and the user-offer relation table.
                            DatabaseReference mUsersReference = FirebaseDatabase.getInstance().getReference();
                            mUsersReference.child("marketplace").child("offers").child(recordSaleInfo.getSaleUID()).setValue(recordSaleInfo);

                            // Back to search
                            Intent goToMain = new Intent(getApplicationContext(), SaleSearchActivity.class);
                            goToMain.putExtra("method", "saleSearch");
                            startActivity(goToMain);
                            Toast.makeText(getApplicationContext(), "Your offer has been created", Toast.LENGTH_LONG).show();
                            finish();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }
        else {
            Toast.makeText(this, "One or more of your fields is not complete", Toast.LENGTH_LONG).show();
        }



    }

    public void backToSearch(View view) {
        finish();
    }
}
