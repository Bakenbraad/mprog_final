package nl.mprog.rens.vinylcountdown.Activities;

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

import nl.mprog.rens.vinylcountdown.HelperClasses.AsyncImgLoad;
import nl.mprog.rens.vinylcountdown.ObjectClasses.RecordInfo;
import nl.mprog.rens.vinylcountdown.ObjectClasses.RecordSaleInfo;
import nl.mprog.rens.vinylcountdown.R;

/**
 * Rens van der Veldt - 10766162
 * Minor Programmeren
 *
 * SaleActivity.class
 *
 * The saleActivity allows user to create offers on the marketplace. This uses the recordInfo passed
 * from RecordSearchActivity to generate a recordSaleInfo combined with user input info, such as saletype,
 * price, etc. This activity also allows for users to view more info about the record using the info
 * button. This shows the RecordInfoActivity with more record info properies displayed.
 */
public class SaleActivity extends AppCompatActivity {

    // Declare values.
    String artist;
    String title;
    String summary;
    String imgLink;
    String mbid;
    Map<String,String> tracks = new HashMap<>();
    String[] items;
    EditText priceED;
    RecordInfo recordInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);

        // Get the intent.
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        // Get basic record info.
        recordInfo = (RecordInfo) bundle.getSerializable("recordInfo");

        assert recordInfo != null;
        artist = recordInfo.getArtist();
        title = recordInfo.getTitle();
        summary = recordInfo.getSummary();
        tracks = recordInfo.getTracks();
        imgLink = recordInfo.getImgLinklarge();
        mbid = recordInfo.getMbid();

        priceED = (EditText) findViewById(R.id.priceED);

        // Find the relevant views.
        TextView artistTV = (TextView) findViewById(R.id.artistDetail);
        TextView titleTV = (TextView) findViewById(R.id.titleDetail);
        final TextView priceTV = (TextView) findViewById(R.id.textView8);
        ImageView imageView = (ImageView) findViewById(R.id.imageDetail);

        // Set the values for the views.
        artistTV.setText(artist);
        titleTV.setText(title);
        new AsyncImgLoad(imageView).execute(imgLink);

        // Set the spinner adapter, this is the selector for price type.
        final Spinner dropdown = (Spinner)findViewById(R.id.spinner);
        items = new String[]{"Price", "Bidding", "Trade"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        // When an item is selected the ui should be slightly updated to match the users expectations.
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (items[position]) {
                    case "Trade":
                        priceTV.setText(R.string.tradingtext);
                        priceED.setVisibility(View.INVISIBLE);
                        break;
                    case "Price":
                        priceTV.setText(R.string.pricetext);
                        priceED.setVisibility(View.VISIBLE);
                        break;
                    case "Bidding":
                        priceTV.setText(R.string.biddingtext);
                        priceED.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Opens the recordInfoActivity showing more info about the record, this passes the recordInfo
     * object with that info in it.
     * @param view: passed from a button.
     */
    public void moreInfo(View view){

        Intent goToSaleDetail = new Intent(this, RecordInfoActivity.class);

        // Put the values into the next activity.
        goToSaleDetail.putExtra("recordInfo", recordInfo);

        startActivity(goToSaleDetail);
    }

    /**
     * This function creates a recordSaleInfo object and writes it to the marketplace, this is
     * essentially the offering of a record on the market for other users to view. First, the data
     * are retrieved from the editTexts and spinner. This data is then used to create a recordSaleInfo
     * object. Finally the object is written to the market, the user is notified and the activity is
     * finished.
     * @param view: passed from button.
     */
    public void createSale (View view){

        // Find required views.
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        Spinner saleTypeSP = (Spinner) findViewById(R.id.spinner);
        EditText descriptionED = (EditText) findViewById(R.id.descriptionED);

        // Get data from the views.
        final float price;
        final float condition = ratingBar.getRating();
        final String description = descriptionED.getText().toString();
        String saleType = saleTypeSP.getSelectedItem().toString();

        // Hotfix for string mismatch
        if (saleType.equals("Bidding")){
            saleType = "Bidding from";
        }

        // Trade doesn't need a price but you need to assert that other types have valid prices.
        if (!saleType.equals("Trade")){
            try{
                // Try finding a price in the view.
                price = Float.parseFloat(priceED.getText().toString());
            }
            catch (NumberFormatException e){
                // If an invalid price is added toast the error.
                Toast.makeText(this, R.string.inv_price, Toast.LENGTH_LONG).show();
                return;
            }

        }
        else {
            // Price for trade mustn't be null so initialize it to 0,0 for default.
            price = Float.parseFloat("0.0");
        }

        // Get the user, a market offer requires a users id.
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // If all is well, create a dialog to confirm addition to the market.
        if (user != null && description.length() != 0 && condition != 0 && !saleType.equals("")){

            final String finalSaleType = saleType;
            new AlertDialog.Builder(this)
                    .setTitle(R.string.confirm_sale)
                    .setIcon(R.drawable.logo)
                    .setMessage("Do you want to sell " + recordInfo.getTitle() + " by " + recordInfo.getArtist() + "?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {

                            // Create the object.
                            RecordSaleInfo recordSaleInfo = new RecordSaleInfo(recordInfo, description, price, finalSaleType, condition, user.getUid());

                            // Write it to the Database and the user-offer relation table.
                            DatabaseReference mUsersReference = FirebaseDatabase.getInstance().getReference();
                            mUsersReference.child("marketplace").child("offers").child(recordSaleInfo.getSaleUID()).setValue(recordSaleInfo);

                            // Back to search
                            Intent goToMain = new Intent(getApplicationContext(), RecordSearchActivity.class);
                            goToMain.putExtra("method", "saleSearch");
                            startActivity(goToMain);
                            Toast.makeText(getApplicationContext(), R.string.offer_success, Toast.LENGTH_LONG).show();
                            finish();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }
        else {
            // All is not well, alert the user.
            Toast.makeText(this, "One or more of your fields is not complete", Toast.LENGTH_LONG).show();
        }



    }

    public void backToSearch(View view) {
        finish();
    }
}
