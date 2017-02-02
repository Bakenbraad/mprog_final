package nl.mprog.rens.vinylcountdown.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import nl.mprog.rens.vinylcountdown.AdapterClasses.CustomMarketAdapter;
import nl.mprog.rens.vinylcountdown.HelperClasses.ApiHelper;
import nl.mprog.rens.vinylcountdown.HelperClasses.NavigationHelper;
import nl.mprog.rens.vinylcountdown.ObjectClasses.RecordInfo;
import nl.mprog.rens.vinylcountdown.ObjectClasses.RecordSaleInfo;
import nl.mprog.rens.vinylcountdown.R;

/**
 * Rens van der Veldt - 10766162
 * Minor Programmeren
 *
 * BuySearchActivity.class
 *
 * This activity manages the market search, this is a two part search that consists of retrieving data from
 * the api and using that data to crossreference with the marketplace in firebase. This returns a list of all
 * records that match the query and are being sold at the moment. When a record in the marketplace
 * is clicked the data is passed to the buyactivity.
 */

public class BuySearchActivity extends AppCompatActivity {

    // Declare the search ED
    EditText searchViewED;

    // Authenticator:
    // The authenticator for firebase.
    FirebaseAuth.AuthStateListener mAuthListener;

    // Initiate the navigation handler:
    NavigationHelper navigationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_search);

        // Initiate the navigation handler.
        navigationHelper = new NavigationHelper(this);

        // If a query was sent (from the wishlist activity) this is automatically used to
        // search the marketplace.
        Intent intent = getIntent();
        Bundle methodBundle = intent.getExtras();
        if (methodBundle != null){

            String wishlistQuery = methodBundle.getString("query");

            // Check if a query exists
            if (wishlistQuery != null && !wishlistQuery.equals("")){

                // Perform a search using the given query.
                new MarketAsyncTask(wishlistQuery, "Any").execute();

                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        }


        // Check the login state.
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // User is signed out, they don't belong here so send them back!
                    new NavigationHelper(getParent()).goToLogin();
                }
            }
        };

        // When the search button in the keyboard is used, do a marketsearch
        // using the input query.
        searchViewED = (EditText) findViewById(R.id.searchSaleED);
        searchViewED.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    // On search action send query to asynctask
                    try {
                        searchMarket();
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });

        // Set the spinner adapter, this is used as a filter.
        Spinner dropdownBuy = (Spinner) findViewById(R.id.spinner_buy);
        String[] items = getResources().getStringArray(R.array.buyFilter);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, items);
        dropdownBuy.setAdapter(adapter);
    }

    // Open drawer is called when the button to open the menu is pressed.
    public void openDrawer(View view) {
        navigationHelper.openDrawer();
    }

    /**
     * This function is used to execute the market search, the query is checked for validity and then
     * passed, along with the filter, to the marketasynctask which manages the getting of results.
     * These results are displayed in the results listview by the asynctask.
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void searchMarket() throws ExecutionException, InterruptedException {

        // Get the query and the price type.
        String query = searchViewED.getText().toString();
        Spinner marketFilter = (Spinner) findViewById(R.id.spinner_buy);
        String priceType = marketFilter.getSelectedItem().toString();

        // Hotfix for string mismatch
        if (priceType.equals("Bidding")){
            priceType = "Bidding from";
        }

        // Search for music if the query is long enough, otherwise let the user know of their mistakes.
        if (query.length() < 2){
            Toast.makeText(this, "Please give us more to go on...",Toast.LENGTH_SHORT).show();
            searchViewED.setError("Invalid search");
        } else {
            new MarketAsyncTask(query, priceType).execute();
        }
    }

    /**
     * This is an inbetween step that makes it possible for the function to be called by a button
     * as well as on the keyboard by the built in search action.
     * @param view
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void onSearchClick(View view) throws ExecutionException, InterruptedException {
        searchMarket();
    }

    public void setMarketData(final ListView lv, List<RecordSaleInfo> recordSaleInfoList){

        // Set the adapter
        CustomMarketAdapter customMarketAdapter = new CustomMarketAdapter(getApplicationContext(), R.layout.record_market_item, recordSaleInfoList);
        lv.setAdapter(customMarketAdapter);
        customMarketAdapter.notifyDataSetChanged();

        // Set a listener, this is used to send info to the buyactivity.
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                Intent goToBuy = new Intent(getApplicationContext(), BuyActivity.class);
                RecordSaleInfo recordSaleInfo = (RecordSaleInfo) lv.getItemAtPosition(arg2);

                // Put the values into the next activity.
                Bundle bundle = new Bundle();
                bundle.putSerializable("recordSaleInfo", recordSaleInfo);
                goToBuy.putExtras(bundle);

                // Start the activity
                startActivity(goToBuy);
            }
        });
    }

    /**
    * This is the asynctask that manages the loading of the marketplace, the Background process runs the
    * api manager in order to get all mbids that match a certain query. Subsequently, the firebase
    * offers are filtered by mbid matching all the apis results, if a filter is selected this is also taken into
    * account when adding results to the list. This returns all the offered records in a listview that match the
    * search query as if they were sent to the lastfm api directly.
     */
    public class MarketAsyncTask extends AsyncTask<Void, Void, List<RecordInfo>> implements Serializable {

        private String query;
        private String filter;
        private List<RecordInfo> searchResults;
        ProgressDialog dialog;


        public MarketAsyncTask(String query, String filter) {

            this.query = query;
            this.filter = filter;

            // Create a progress dialog.
            this.dialog = new ProgressDialog(BuySearchActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage(getString(R.string.dialog_market_search) + query);
            dialog.show();
        }

        @Override
        protected List<RecordInfo> doInBackground(Void... params) {

            // Create an API manager object.
            ApiHelper apiHelper = new ApiHelper();

            // Use the apiHelper to search for results using the correct method.
            try {
                searchResults = apiHelper.Search(query);
            } catch (IOException e) {
                e.printStackTrace();
            }


            return searchResults;
        }

        @Override
        protected void onPostExecute(List<RecordInfo> searchResults) {

            // Declare the list of record sales.
            final List<RecordSaleInfo> recordSaleInfoList = new ArrayList<>();

            // Close the dialog
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            // Declare the listview and set its emptyview
            final ListView lv= (ListView) findViewById(R.id.buyResult);

            // Set the empty view
            TextView emptyView = (TextView) findViewById(R.id.inbox_empty_item);
            emptyView.setText(R.string.no_market_results);
            lv.setEmptyView(emptyView);

            // If there are results put them in the listview.
            if (searchResults != null) {

                // Get matches for each mbid result.
                for (int i = 0; i < searchResults.size(); i++) {
                    final String mbid = searchResults.get(i).getMbid();

                    // Match every mbid to the marketplace data. This is done by getting a reference to the
                    // marketplace and querying every mbid.
                    DatabaseReference marketplaceRef = FirebaseDatabase.getInstance().getReference().child("marketplace").child("offers");
                    final Query queryRef = marketplaceRef.orderByChild(mbid);
                    ValueEventListener refListener = new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            // Get the datasnapshot matching the query
                            for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {

                                // Get the object form the marketplace.
                                RecordSaleInfo recordSaleInfo = (RecordSaleInfo) chatSnapshot.getValue(RecordSaleInfo.class);
                                if (recordSaleInfo.getMbid().equals(mbid)) {

                                    // Apply the filter.
                                    if (recordSaleInfo.priceType.equals(filter) || filter.equals("Any")) {
                                        recordSaleInfoList.add(recordSaleInfo);
                                    }
                                }
                            }

                            setMarketData(lv, recordSaleInfoList);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    queryRef.addListenerForSingleValueEvent(refListener);

                }

                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        }
    }
}
