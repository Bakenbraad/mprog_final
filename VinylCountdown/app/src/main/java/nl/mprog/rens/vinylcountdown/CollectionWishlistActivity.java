package nl.mprog.rens.vinylcountdown;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CollectionWishlistActivity extends AppCompatActivity {

    // Authenticator:
    // The authenticator for firebase.
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    // Initiate drawer modules:
    DrawerLayout drawerLayout;
    ListView drawers;
    String[] navigations;
    Button menuButton;

    // Initiate the navigation handler:
    HelperNavigationHandler helperNavigationHandler;

    // Declare the overall method
    String method;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

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

        // This part gets a reference and retrieves the collection from the firebase by querying
        // the users id. The id identifies to which user the collection item belongs.
        // after retrieval the listview is filled using the collection data.
        if (user != null){

            // Set the collection/wishlist method:
            Intent intent = getIntent();
            Bundle methodBundle = intent.getExtras();
            method = methodBundle.getString("method");

            // Adjust the currentpage
            TextView currentPage = (TextView) findViewById(R.id.current_page);
            if (method.equals("collection")){
                currentPage.setText("Collection");
            }
            else {
                currentPage.setText("Wishlist");
            }

            // Collection/wishlist reference:
            DatabaseReference collectionReference = FirebaseDatabase.getInstance().getReference().child(method);


            // Get the users collected records.
            Query queryRef = collectionReference.orderByChild("userID").equalTo(user.getUid());
            ValueEventListener collectionListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get UserSettings object and use the values to update the UI
                    List<CollectionWishlistRecord> collectionWishlistRecords = new ArrayList<>();

                    for (DataSnapshot chatSnapshot: dataSnapshot.getChildren()) {
                        CollectionWishlistRecord collectionWishlistRecord = (CollectionWishlistRecord) chatSnapshot.getValue(CollectionWishlistRecord.class);

                        collectionWishlistRecords.add(collectionWishlistRecord);
                    }
                    ListView lv = (ListView) findViewById(R.id.collectionList);

                    // Extract the recordinfo objects from the results.
                    List<RecordInfo> recordInfoList = new ArrayList<>();
                    for (int i = 0; i < collectionWishlistRecords.size(); i ++){
                        recordInfoList.add(collectionWishlistRecords.get(i).getRecordInfo());
                    }

                    // Put the collection in the adapter
                    CustomAlbumAdapter customAlbumAdapter = new CustomAlbumAdapter(getBaseContext(), R.layout.record_item, recordInfoList);
                    lv.setAdapter(customAlbumAdapter);
                    customAlbumAdapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            queryRef.addValueEventListener(collectionListener);
        }

        // Set the spinner.
        Spinner sortingOptions = (Spinner) findViewById(R.id.sortingOptions);
        String[] sortingItems = getResources().getStringArray(R.array.sortingOptions);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, sortingItems);
        sortingOptions.setAdapter(adapter);

        // Navigation drawer from: https://developer.android.com/training/implementing-navigation/nav-drawer.html#Init
        navigations = getResources().getStringArray(R.array.menuOptions);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawers = (ListView) findViewById(R.id.main_drawer);
        menuButton = (Button) findViewById(R.id.menubutton);

        // Set the adapter for the list view
        drawers.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_item, navigations));
        // Set the list's click listener
        drawers.setOnItemClickListener(new CollectionWishlistActivity.DrawerItemClickListener());
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

    public void goToAddSearch(View view) {

        // This function leads to the same search activity as for sale but puts a different
        // onclick listener in the results
        Intent goToAddSearch = new Intent(this, SaleSearchActivity.class);
        if (method.equals("collection")){
            goToAddSearch.putExtra("method", "collectionSearch");
        } else {
            goToAddSearch.putExtra("method", "wishlistSearch");
        }
        startActivity(goToAddSearch);
    }
}
