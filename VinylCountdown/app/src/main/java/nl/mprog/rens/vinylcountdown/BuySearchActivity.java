package nl.mprog.rens.vinylcountdown;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class BuySearchActivity extends AppCompatActivity {

    // Initiate the search ED and the results lv
    EditText searchViewED;

    // Authenticator:
    // The authenticator for firebase.
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference mDatabase;

    // Initiate drawer modules:
    DrawerLayout drawerLayout;
    ListView drawers;
    String[] navigations;
    Button menuButton;

    // Initiate the navigation handler:
    HelperNavigationHandler helperNavigationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_search);

        // Initiate the navigation handler.
        helperNavigationHandler = new HelperNavigationHandler(this);

        // Initiate the authentication
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Check the login state and welcome the user if they are logged in.
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // User is signed out, they don't belong here so send them back!
                    new HelperNavigationHandler(getParent()).goToLogin();
                }
            }
        };

        // Get the button and searchview
        menuButton = (Button) findViewById(R.id.menubutton);

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

        // Set the spinner adapter.
        Spinner dropdownBuy = (Spinner) findViewById(R.id.spinner_buy);
        String[] items = getResources().getStringArray(R.array.buyFilter);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdownBuy.setAdapter(adapter);

        // Navigation drawer from: https://developer.android.com/training/implementing-navigation/nav-drawer.html#Init
        navigations = getResources().getStringArray(R.array.menuOptions);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawers = (ListView) findViewById(R.id.main_drawer);
        menuButton = (Button) findViewById(R.id.menubutton);

        // Set the adapter for the list view
        drawers.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_item, navigations));
        // Set the list's click listener
        drawers.setOnItemClickListener(new BuySearchActivity.DrawerItemClickListener());
    }

    // Change the button appearance when it is clicked.
    public void openDrawer(View view) {

        if(drawerLayout.isDrawerOpen(drawers)){
            drawerLayout.closeDrawer(drawers);
            menuButton.setText("+");
        }
        else {
            drawerLayout.openDrawer(drawers);
            menuButton.setText("-");
        }
    }

    // This is the onclicklistener for the drawer, it sends data to navigation.
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            helperNavigationHandler.redirect(position);
            drawerLayout.closeDrawer(drawers);
            menuButton.setText("+");
        }
    }

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

    public void onSearchClick(View view) throws ExecutionException, InterruptedException {
        searchMarket();
    }

    /*
    This is the asynctask that manages the loading of the marketplace, the Background process runs the
    api manager in order to get all mbids that match a certain query. Subsequently, the firebase
    offers are ordered by mbid matching all the apis results, if a filter is selected this is also taken into
    account. This returns all the offered records in a listview that match the search query as if they
    were sent to the lastfm api directly.
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
            dialog.setMessage("Looking for offers of " + query);
            dialog.show();
        }

        @Override
        protected List<RecordInfo> doInBackground(Void... params) {

            // Create an API manager object.
            HelperApiManager helperApiManager = new HelperApiManager();

            // Use the helperApiManager to search for results using the correct method.
            try {
                searchResults = helperApiManager.Search(query);
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
            emptyView.setText("Nothing found in the marketplace");
            lv.setEmptyView(emptyView);

            // If there are results put them in the listview.
            if (searchResults != null) {

                // Get matches for each mbid result.
                for (int i = 0; i < searchResults.size(); i++) {
                    final String mbid = searchResults.get(i).getMbid();

                    // Match every mbid to the marketplace data.
                    DatabaseReference marketplaceRef = FirebaseDatabase.getInstance().getReference().child("marketplace").child("offers");
                    final Query queryRef = marketplaceRef.orderByChild(mbid);
                    ValueEventListener refListener = new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get UserSettings object and use the values to update the UI
                            for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                                RecordSaleInfo recordSaleInfo = (RecordSaleInfo) chatSnapshot.getValue(RecordSaleInfo.class);
                                if (recordSaleInfo.getMbid().equals(mbid)) {
                                    if (recordSaleInfo.priceType.equals(filter) || filter.equals("Any")) {
                                        recordSaleInfoList.add(recordSaleInfo);
                                    }
                                }
                            }

                            // Set the adapter
                            CustomMarketAdapter customMarketAdapter = new CustomMarketAdapter(getApplicationContext(), R.layout.record_market_item, recordSaleInfoList);
                            lv.setAdapter(customMarketAdapter);
                            customMarketAdapter.notifyDataSetChanged();

                            // Set a listener, this is used to send record info to the selling details.
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
                                    finish();
                                }
                            });
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
