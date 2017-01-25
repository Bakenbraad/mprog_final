package nl.mprog.rens.vinylcountdown;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BuyActivity extends AppCompatActivity {

    // Authenticator:
    // The authenticator for firebase.
    private FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseAuth.AuthStateListener mAuthListener;

    // Values for extra info request.
    String summary;
    Map<String,String> tracks = new HashMap<>();

    // The record sale info from the intent;
    RecordSaleInfo recordSaleInfo;
    RecordInfo recordInfo;

    // Userprofiles for sending messages:
    UserProfile sellerProfile;
    UserProfile buyerProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        // Get the selected record.
        Intent intent = this.getIntent();
        Bundle recordInfo = intent.getExtras();
        recordSaleInfo = (RecordSaleInfo) recordInfo.getSerializable("recordSaleInfo");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Check the login state, unauthorized users may not use the market.
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // User is signed out, they don't belong here so send them back!
                    Intent goToLogin = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(goToLogin);
                }
            }
        };

        // Get both the selling and buying user profiles.

        // Sellers profile
        DatabaseReference mSellersReference = FirebaseDatabase.getInstance().getReference().child("users").child(recordSaleInfo.getUserID());
        ValueEventListener sellerListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get UserSettings object and use the values to update the UI
                sellerProfile = dataSnapshot.getValue(UserProfile.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mSellersReference.addListenerForSingleValueEvent(sellerListener);

        // Buyers profile
        DatabaseReference mBuyersReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        ValueEventListener buyersListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get UserSettings object and use the values to update the UI
                buyerProfile = dataSnapshot.getValue(UserProfile.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mBuyersReference.addListenerForSingleValueEvent(buyersListener);

        // Check if this is the users' advertisement:
        if (recordSaleInfo.getUserID().equals(user.getUid())){
            ImageView buyButton = (ImageView) findViewById(R.id.buyButton);
            buyButton.setVisibility(View.GONE);
        }

        // Set the correct offer type:
        if (recordSaleInfo.getPriceType().equals("Bidding from")){
            LinearLayout biddingLayout = (LinearLayout) findViewById(R.id.bid_lin_lay);
            TextView currentBidTV = (TextView) findViewById(R.id.bidPriceCurrent);
            TextView originalBidTV = (TextView) findViewById(R.id.bidPriceOriginal);

            // Set the original and current price.
            currentBidTV.setText(String.valueOf(recordSaleInfo.getCurrentBid()));
            originalBidTV.setText(String.valueOf(recordSaleInfo.getPrice()));
            biddingLayout.setVisibility(View.VISIBLE);
        }
        else if (recordSaleInfo.getPriceType().equals("Trade")){

            LinearLayout biddingLayout = (LinearLayout) findViewById(R.id.trade_lin_lay);
            biddingLayout.setVisibility(View.VISIBLE);
        }
        else if (recordSaleInfo.getPriceType().equals("Price")){

            LinearLayout biddingLayout = (LinearLayout) findViewById(R.id.price_lin_lay);
            TextView priceTV = (TextView) findViewById(R.id.buyPrice);
            priceTV.setText(String.valueOf(recordSaleInfo.getPrice()));
            ImageButton buyButton = (ImageButton) findViewById(R.id.buyButton);
            buyButton.setImageResource(R.drawable.cartin);
            biddingLayout.setVisibility(View.VISIBLE);
        }

        // Get the rest of the data:
        new SingleAsyncRequest(recordSaleInfo.getMbid()).execute();


        // Find all views
        TextView artistTV = (TextView) findViewById(R.id.buyArtist);
        TextView titleTV = (TextView) findViewById(R.id.buyTitle);
        ImageView imageView = (ImageView) findViewById(R.id.buyImage);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.buyRating);

        // Get the selling users username:
        DatabaseReference mUserReference = FirebaseDatabase.getInstance().getReference().child("users").child(recordSaleInfo.getUserID());
        ValueEventListener userProfileListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get UserSettings object and use the values to update the UI
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                if (userProfile != null) {
                    TextView descriptionTV = (TextView) findViewById(R.id.buyDesc);
                    descriptionTV.setText(userProfile.getUsername() + " about this record: '" + recordSaleInfo.getDescription() + "'");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mUserReference.addListenerForSingleValueEvent(userProfileListener);

        // Set the views
        artistTV.setText(recordSaleInfo.getArtist());
        titleTV.setText(recordSaleInfo.getTitle());
        new AsyncTaskImgDownload(imageView).execute(recordSaleInfo.getImgLink());
        ratingBar.setRating(recordSaleInfo.getCondition());
    }

    public void createBuy(View view) {

        // Get the database ref for writing:
        DatabaseReference mMarketReference = FirebaseDatabase.getInstance().getReference().child("marketplace").child("offers").child(recordSaleInfo.getSaleUID());
        DatabaseReference mMessageReference = FirebaseDatabase.getInstance().getReference().child("messages");

        // This is the checking that happens in order to validate a bid.
        if (recordSaleInfo.getPriceType().equals("Bidding from")){

            // Get the users bid from the layout and compare it to the other bids.
            EditText yourBidED = (EditText) findViewById(R.id.bidPriceYours);
            float yourBid;

            if (yourBidED.getText() != null){
                yourBid = Float.parseFloat(yourBidED.getText().toString());
            } else{
                yourBid = 0;
            }

            if (yourBid > 0 && yourBid > recordSaleInfo.getCurrentBid()){
                if (!recordSaleInfo.getCurrentBidUser().equals(user.getUid())){

                    // If the bid is legit update the data and write it to firebase.
                    recordSaleInfo.setCurrentBid(yourBid);
                    recordSaleInfo.setCurrentBidUser(user.getUid());
                    mMarketReference.setValue(recordSaleInfo);

                    // Send the user a message about your offer:
                    Message tradeMessage = new Message();
                    tradeMessage.messageMarket("offer", recordSaleInfo.getPriceType(), recordSaleInfo.getTitle(), String.valueOf(yourBid),
                            recordSaleInfo.getSaleUID(), buyerProfile, user.getUid(), sellerProfile, recordSaleInfo.getUserID());
                    mMessageReference.push().setValue(tradeMessage);

                    // Let the user know of their succes and send them back!
                    Toast.makeText(this, "Succesfully sent a message to the record owner!", Toast.LENGTH_LONG).show();
                    Intent goBackToSearch = new Intent(this, BuySearchActivity.class);
                    startActivity(goBackToSearch);
                    finish();

                } else {
                    Toast.makeText(this, "You already placed a bid!", Toast.LENGTH_LONG).show();
                }
            } else {
                // Bid is insufficient
                Toast.makeText(this, "Insufficient bid!", Toast.LENGTH_LONG).show();
            }
        }

        // This is the checking that happens for trades, if a record is selected, you can send a message to the user offering it.
        else if (recordSaleInfo.getPriceType().equals("Trade")){

            // Check if anything is selected.
            TextView selectedTV = (TextView) findViewById(R.id.selectedRecord);
            if (selectedTV.getVisibility() == View.VISIBLE){

                // Check if user isn't sending multiple messages:
                if (!recordSaleInfo.getCurrentBidUser().equals(user.getUid())){

                    // Get the selected record.
                    String tradeProposal = selectedTV.getText().toString();

                    // Update the marketplace info:
                    recordSaleInfo.setCurrentBidUser(user.getUid());
                    mMarketReference.setValue(recordSaleInfo);

                    // Send the user a message about your trade offer:
                    Message tradeMessage = new Message();
                    tradeMessage.messageMarket("offer", recordSaleInfo.getPriceType(), recordSaleInfo.getTitle(), tradeProposal,
                            recordSaleInfo.getSaleUID(), buyerProfile, user.getUid(), sellerProfile, recordSaleInfo.getUserID());
                    mMessageReference.push().setValue(tradeMessage);

                    // Let the user know of their succes and send them back!
                    Toast.makeText(this, "Succesfully sent a message to the record owner!", Toast.LENGTH_LONG).show();
                    Intent goBackToSearch = new Intent(this, BuySearchActivity.class);
                    startActivity(goBackToSearch);
                    finish();

                } else {
                    Toast.makeText(this, "You already sent this user an offer!", Toast.LENGTH_LONG).show();
                }

            }
            else{
                Toast.makeText(this, "Please select a record first!", Toast.LENGTH_LONG).show();
            }
        }

        // This is the checking that happens for prices, if someone clicks buy, a message is sent that someone wants to buy the record.
        else if (recordSaleInfo.getPriceType().equals("Price")){

            // Update the marketplace info:
            recordSaleInfo.setCurrentBidUser(user.getUid());
            mMarketReference.setValue(recordSaleInfo);

            // Check if user already responded.
            if (!recordSaleInfo.getCurrentBidUser().equals(user.getUid())){

                // Send the user a message about your offer:
                Message tradeMessage = new Message();
                tradeMessage.messageMarket("offer", recordSaleInfo.getPriceType(), recordSaleInfo.getTitle(), String.valueOf(recordSaleInfo.getPrice()),
                        recordSaleInfo.getSaleUID(), buyerProfile, user.getUid(), sellerProfile, recordSaleInfo.getUserID());
                mMessageReference.push().setValue(tradeMessage);

                // Let the user know of their succes and send them back!
                Toast.makeText(this, "Succesfully sent a message to the record owner!", Toast.LENGTH_LONG).show();
                Intent goBackToSearch = new Intent(this, BuySearchActivity.class);
                startActivity(goBackToSearch);
                finish();
            }
            else{
                // Let the user know that they already responded to this offer.
                Toast.makeText(this, "You already replied to this offer!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void moreInfo(View view){

        Intent goToSaleDetail = new Intent(this, RecordInfoActivity.class);

        // Put the values into the next activity.
        goToSaleDetail.putExtra("title", recordSaleInfo.getTitle());
        goToSaleDetail.putExtra("artist", recordSaleInfo.getArtist());
        goToSaleDetail.putExtra("summary", summary);
        goToSaleDetail.putExtra("tracks", (Serializable) tracks);

        startActivity(goToSaleDetail);
    }

    public void setRecord(RecordInfo recordInfo){
        this.recordInfo = recordInfo;

    }

    public void tradeSearch(View view) {

        // Make sure the listview is visible:
        ListView lv = (ListView) findViewById(R.id.saleResult);
        lv.setVisibility(View.VISIBLE);

        // And the textview is gone.
        TextView tv = (TextView) findViewById(R.id.tradeResult);
        tv.setVisibility(View.GONE);

        // Get the query from the edittext and perform a search with it.
        EditText editText = (EditText) findViewById(R.id.tradeSearchED);
        String query = editText.getText().toString();
        if (query.length() > 2){
            new AsyncTaskMusicSearch(this, query, "tradeSearch").execute();
        }
        else {
            Toast.makeText(this, "Please give us more to go on!", Toast.LENGTH_SHORT).show();
        }

    }

    public void backToSearch(View view) {
        finish();
    }

    public class SingleAsyncRequest extends AsyncTask<Void, Void, RecordInfo>{

        RecordInfo searchResult;
        String mbid;

        public SingleAsyncRequest(String mbid){
            this.mbid = mbid;
        }
        @Override
        protected RecordInfo doInBackground(Void... params) {

            // Create an API manager object.
            ApiManager apiManager = new ApiManager();

            // Use the apiManager to search for results using the correct method.
            searchResult = apiManager.recordInfoSearch(mbid);

            return searchResult;
        }

        @Override
        protected void onPostExecute(RecordInfo recordInfo) {
            super.onPostExecute(recordInfo);
            setRecord(recordInfo);
        }
    }
}

