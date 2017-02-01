package nl.mprog.rens.vinylcountdown;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

import nl.mprog.rens.vinylcountdown.HelperClasses.ApiHelper;
import nl.mprog.rens.vinylcountdown.HelperClasses.AsyncImgLoad;
import nl.mprog.rens.vinylcountdown.HelperClasses.AsyncMusicSearch;
import nl.mprog.rens.vinylcountdown.ObjectClasses.Message;
import nl.mprog.rens.vinylcountdown.ObjectClasses.RecordInfo;
import nl.mprog.rens.vinylcountdown.ObjectClasses.RecordSaleInfo;
import nl.mprog.rens.vinylcountdown.ObjectClasses.UserProfile;

/**
 * Rens van der Veldt - 10766162
 * Minor Programmeren
 *
 * BuyActivity.class
 *
 * This activity manages the buying seciton of the app. This starts with displaying the market data
 * in the view and displaying the appropriate buy type (allowing users to bid/trade/buy for a price).
 * The other functionality is that this activity, on a successful buy action, sends the seller a
 * message containing the details of the deal.
 *
 * Constructed from:
 * http://stackoverflow.com/questions/3090650/android-loading-an-image-from-the-web-with-asynctask
 */

public class BuyActivity extends AppCompatActivity {

    // Authenticator:
    // The authenticator for firebase.
    private FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseAuth.AuthStateListener mAuthListener;

    // The record sale info from the intent, this is the data that was passed from the past activity
    // and regards the record that is being sold.
    RecordSaleInfo recordSaleInfo;
    RecordInfo recordInfo;

    // Userprofiles for sending messages, both are used because messages can be replied to (sender
    // and receiver are swapped on replies):
    UserProfile sellerProfile;
    UserProfile buyerProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        // Get the selected record from the intent.
        Intent intent = this.getIntent();
        Bundle recordInfoBundle = intent.getExtras();
        recordSaleInfo = (RecordSaleInfo) recordInfoBundle.getSerializable("recordSaleInfo");

        // Hide the keyboard if its open!
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        // Get the current user from the firebaseAuth.
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Check the login state, unauthorized users may not use the market and are sent back to
        // the login activity.
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

        // This section uses similar code to retrieve the selling and buying users profiles needed
        // for the message service.
        getProfiles();


        // Check if this is the users' advertisement, users cannot reply to their own advertisements
        // thus the button that creates offers is removed from view.
        if (recordSaleInfo.getUserID().equals(user.getUid())){
            ImageView buyButton = (ImageView) findViewById(R.id.buyButton);
            buyButton.setVisibility(View.GONE);
        }

        // Set the correct display.
        displayPriceType();

        // Get the rest of the data of this record, the result is set to be the record in this activity.
        new SingleAsyncRequest(recordSaleInfo.getMbid()).execute();

        // Find all views should be filled with record/sale data.
        TextView artistTV = (TextView) findViewById(R.id.buyArtist);
        TextView titleTV = (TextView) findViewById(R.id.buyTitle);
        ImageView imageView = (ImageView) findViewById(R.id.buyImage);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.buyRating);


        // Put all other data in the corresponding views.
        artistTV.setText(recordSaleInfo.getArtist());
        titleTV.setText(recordSaleInfo.getTitle());
        new AsyncImgLoad(imageView).execute(recordSaleInfo.getImgLink());
        ratingBar.setRating(recordSaleInfo.getCondition());
    }

    /**
     * Displays the right layout appropriate for the records price type. The display is set correctly
     * corresponding to bidding/trade/price types of sale. E.g. bidding displays the current bid
     * and allows users to input a new bid.
     */
    public void displayPriceType(){

        if (recordSaleInfo.getPriceType().equals(getString(R.string.bidding))){

            // Find all views corresponding to bidding.
            LinearLayout biddingLayout = (LinearLayout) findViewById(R.id.bid_lin_lay);
            TextView currentBidTV = (TextView) findViewById(R.id.bidPriceCurrent);
            TextView originalBidTV = (TextView) findViewById(R.id.bidPriceOriginal);

            // Set the original and current price and make the view visible.
            currentBidTV.setText(String.valueOf(recordSaleInfo.getCurrentBid()));
            originalBidTV.setText(String.valueOf(recordSaleInfo.getPrice()));
            biddingLayout.setVisibility(View.VISIBLE);
        }
        else if (recordSaleInfo.getPriceType().equals(getString(R.string.trade))){

            // Find all views corresponding to trading and set the view to visible.
            LinearLayout biddingLayout = (LinearLayout) findViewById(R.id.trade_lin_lay);
            biddingLayout.setVisibility(View.VISIBLE);
        }
        else if (recordSaleInfo.getPriceType().equals(getString(R.string.price))){

            // Find all views corresponding to set price.
            LinearLayout biddingLayout = (LinearLayout) findViewById(R.id.price_lin_lay);
            TextView priceTV = (TextView) findViewById(R.id.buyPrice);

            // Set the value for price and change the button to a buying button.
            priceTV.setText(String.valueOf(recordSaleInfo.getPrice()));
            ImageButton buyButton = (ImageButton) findViewById(R.id.buyButton);
            buyButton.setImageResource(R.drawable.cartin);

            // Make the view visible.
            biddingLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * This function retrieves the profiles of both users. The seller is used for setting their username
     * and the buyers is used for sending messages.
     */
    public void getProfiles(){

        // Sellers profile
        DatabaseReference mSellersReference = FirebaseDatabase.getInstance().getReference().child("users").child(recordSaleInfo.getUserID());
        ValueEventListener sellerListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Get the selling users data and use it to set the description.
                sellerProfile = dataSnapshot.getValue(UserProfile.class);

                TextView descriptionTV = (TextView) findViewById(R.id.buyDesc);

                // Get the selling users username and put it in front of the record description:
                descriptionTV.setText(sellerProfile.getUsername() + " about this record: '" + recordSaleInfo.getDescription() + "'");
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
                // Get the buying users data.
                buyerProfile = dataSnapshot.getValue(UserProfile.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mBuyersReference.addListenerForSingleValueEvent(buyersListener);
    }

   /**
    * This is the function that creates the offers. This consists of 3 steps, firstly a dialog is
    * created, the text of this dialog is then set according to the record being sold.
    * When a users confirms the sale the second part is started. This happens behind the screen.
    * According to the sale type the input data is checked and the marketplace is updated if a bid
    * is done. In the case of price and trade this is unnecessary.
    *
    * @param view is passed because this is an onclick that is called by pressing the offer
    * button in the buy view.
    */
    public void createBuy(View view) {

        // Create the dialog that asks for confirmation.
        new AlertDialog.Builder(this)
            .setTitle(R.string.confirm_offer)
            .setMessage("Do you want to let the owner know you want to exchange " + recordInfo.getTitle() + " by " + recordInfo.getArtist() + "?")
            .setIcon(R.drawable.logo)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {

                    // Get the database reference for writing:
                    DatabaseReference mMarketReference = FirebaseDatabase.getInstance().getReference().child("marketplace").child("offers").child(recordSaleInfo.getSaleUID());
                    DatabaseReference mMessageReference = FirebaseDatabase.getInstance().getReference().child("messages");

                    // A switch differentiates between bidding, price and trade and checks if the input
                    // by the user is correct in each case.
                    switch (recordSaleInfo.getPriceType()) {

                        // This is the case for bidding.
                        case "Bidding from":

                            // Get the users bid from the layout and compare it to the current bid.
                            EditText yourBidED = (EditText) findViewById(R.id.bidPriceYours);
                            float yourBid;

                            if (yourBidED.getText() != null) {
                                yourBid = Float.parseFloat(yourBidED.getText().toString());
                            } else {
                                yourBid = 0;
                            }

                            // If the bid is correct and the latest bid didn't come from the user (so you can't reply twice in a row),
                            // send a message to the user selling the record.
                            if (yourBid > 0 && yourBid > recordSaleInfo.getCurrentBid()) {
                                if (!recordSaleInfo.getCurrentBidUser().equals(user.getUid())) {

                                    // Write the bid.
                                    writeBid(yourBid, mMarketReference, mMessageReference);

                                } else {
                                    // This user already responded, let them know.
                                    Toast.makeText(getApplicationContext(), R.string.double_bid, Toast.LENGTH_LONG).show();
                                }
                            } else {
                                // Bid is insufficient, let the user know
                                Toast.makeText(getApplicationContext(), R.string.low_bid, Toast.LENGTH_LONG).show();
                            }
                            break;

                        // this is the case for trading.
                        case "Trade":

                            // Check if anything is selected, this means that the user has searched in the trade subview and
                            // clicked on any of the records, changing the list into a text displaying the selected record,
                            // this is then the record that is retrieved.
                            TextView selectedTV = (TextView) findViewById(R.id.selectedRecord);

                            // This is only visible if a record is selected thus a valid checking mechanism.
                            if (selectedTV.getVisibility() == View.VISIBLE) {

                                // Check if user isn't sending multiple messages:
                                if (!recordSaleInfo.getCurrentBidUser().equals(user.getUid())) {

                                    // Write the trade to database.
                                    writeTrade(selectedTV, mMarketReference, mMessageReference);

                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.double_bid, Toast.LENGTH_LONG).show();
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), R.string.record_unselect, Toast.LENGTH_LONG).show();
                            }
                            break;

                        // This is the case for a set price.
                        case "Price":

                            // Check if user already responded to this record.
                            if (!recordSaleInfo.getCurrentBidUser().equals(user.getUid())) {

                                // Write the offer to database.
                                writePrice(mMarketReference, mMessageReference);
                            } else {
                                // Let the user know that they already responded to this offer.
                                Toast.makeText(getApplicationContext(), R.string.double_bid, Toast.LENGTH_LONG).show();
                            }
                            break;
                    }
                }})
            .setNegativeButton(android.R.string.no, null).show();

    }

    /**
     * More info is called when a user presses the info button. This sends the info of the current
     * record, retrieved in the oncreate using the singleasyncrequest, to the activity that
     * displays the recordinfo.
     */

    public void moreInfo(View view){

        // This is checked because the class responisble for getting the recordinfo data is async.
        if (recordInfo != null){
            // Start an intent to the recordinfo activity.
            Intent goToRecordInfo = new Intent(this, RecordInfoActivity.class);

            // Put the extra info into the next activity.
            goToRecordInfo.putExtra("recordInfo", recordInfo);

            startActivity(goToRecordInfo);
        }
    }

    /**
     * This function is called in the singleasyncrequest to set the recordinfo data of the record in
     * this activity.
     * @param recordInfo is passed to be set as the local version of the recordinfo.
     */
    public void setRecord(RecordInfo recordInfo){
        this.recordInfo = recordInfo;

    }

    /**
     * This is the function used for searching records for the trade sale type.
     * A user enters a query and similar to the SaleSearchActivity a list of results
     * from the api is returned. The method here, tradeSearch is used to notify
     * the listview that onclick of a listview item the item should become the selected,
     * rather than open info about it. Selected means that the listview is replaced with
     * a textview displaying something like: "you selected" + title.
     *
     * In addition to this text another one is set as well with the raw title to simplify retrieval of the title.
      */

    public void tradeSearch(View view) {

        // Make sure the listview is visible.
        ListView lv = (ListView) findViewById(R.id.saleResult);
        lv.setVisibility(View.VISIBLE);

        // And the textview is gone.
        TextView tv = (TextView) findViewById(R.id.tradeResult);
        tv.setVisibility(View.GONE);

        // Get the query from the edittext and perform a search with it.
        EditText editText = (EditText) findViewById(R.id.tradeSearchED);
        String query = editText.getText().toString();
        if (query.length() > 2){
            new AsyncMusicSearch(this, query, "tradeSearch", user.getUid()).execute();
        }
        else {
            Toast.makeText(this, "Please give us more to go on!", Toast.LENGTH_SHORT).show();
        }

    }

    // This function is the onclick for a cancel button and just finishes the activity.
    public void backToSearch(View view) {
        finish();
    }

    public void writeTrade(TextView selectedTV, DatabaseReference mMarketReference, DatabaseReference mMessageReference){

        // Get the selected record.
        String tradeProposal = selectedTV.getText().toString();

        // Update the marketplace info:
        recordSaleInfo.setCurrentBidUser(user.getUid());
        mMarketReference.setValue(recordSaleInfo);

        // Send the selling user a message about your trade offer:
        Message tradeMessage = new Message();
        String tradeMessageID = mMessageReference.push().getKey();
        tradeMessage.messageMarket("offer", recordSaleInfo, tradeProposal, buyerProfile, user.getUid(), sellerProfile, tradeMessageID);
        mMessageReference.child(tradeMessageID).setValue(tradeMessage);

        // Let the user know of their succes and send them back!
        Toast.makeText(getApplicationContext(), R.string.success_message, Toast.LENGTH_LONG).show();
        Intent goBackToSearch = new Intent(getApplicationContext(), BuySearchActivity.class);
        startActivity(goBackToSearch);
        finish();
    }

    public void writeBid(float yourBid, DatabaseReference mMarketReference, DatabaseReference mMessageReference){

        // If the bid is legit update the data and write it to firebase.
        recordSaleInfo.setCurrentBid(yourBid);
        recordSaleInfo.setCurrentBidUser(user.getUid());
        mMarketReference.setValue(recordSaleInfo);

        // Send the selling user a message about your offer:
        Message tradeMessage = new Message();
        String tradeMessageID = mMessageReference.push().getKey();
        tradeMessage.messageMarket("offer", recordSaleInfo, String.valueOf(yourBid), buyerProfile, user.getUid(), sellerProfile, tradeMessageID);
        mMessageReference.child(tradeMessageID).setValue(tradeMessage);

        // Let the user know of their success and send them back!
        Toast.makeText(getApplicationContext(), R.string.success_message, Toast.LENGTH_LONG).show();
        Intent goBackToSearch = new Intent(getApplicationContext(), BuySearchActivity.class);
        startActivity(goBackToSearch);
        finish();
    }

    public void writePrice(DatabaseReference mMarketReference, DatabaseReference mMessageReference){

        // Update the marketplace info:
        recordSaleInfo.setCurrentBidUser(user.getUid());
        mMarketReference.setValue(recordSaleInfo);

        // Send the selling user a message about your offer, even asking prices should be able to be denied:
        Message tradeMessage = new Message();
        String tradeMessageID = mMessageReference.push().getKey();
        tradeMessage.messageMarket("offer", recordSaleInfo, String.valueOf(recordSaleInfo.getPrice()), buyerProfile, user.getUid(), sellerProfile, tradeMessageID);
        mMessageReference.child(tradeMessageID).setValue(tradeMessage);

        // Let the user know of their succes and send them back!
        Toast.makeText(getApplicationContext(), R.string.success_message, Toast.LENGTH_LONG).show();
        Intent goBackToSearch = new Intent(getApplicationContext(), BuySearchActivity.class);
        startActivity(goBackToSearch);
        finish();
    }

    /**
     * This class manages the search for the recordinfo that is called in the oncreate.
     * The recordinfo put back to the main class is used in the moreinfo function mentioned
     * above. This class itself executes a simple api request similar to salesearch.
     */
    public class SingleAsyncRequest extends AsyncTask<Void, Void, RecordInfo>{

        RecordInfo searchResult;
        String mbid;

        public SingleAsyncRequest(String mbid){
            this.mbid = mbid;
        }
        @Override
        protected RecordInfo doInBackground(Void... params) {

            // Create an API manager object.
            ApiHelper apiHelper = new ApiHelper();

            // Use the apiHelper to search for results using the correct method.
            searchResult = apiHelper.recordInfoSearch(mbid);

            return searchResult;
        }

        @Override
        protected void onPostExecute(RecordInfo recordInfo) {
            super.onPostExecute(recordInfo);
            setRecord(recordInfo);

            // Hide the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }
}

