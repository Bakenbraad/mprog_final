package nl.mprog.rens.vinylcountdown.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.Comparator;
import java.util.List;

import nl.mprog.rens.vinylcountdown.AdapterClasses.CustomColWishAdapter;
import nl.mprog.rens.vinylcountdown.HelperClasses.NavigationHelper;
import nl.mprog.rens.vinylcountdown.ObjectClasses.ColWishRecord;
import nl.mprog.rens.vinylcountdown.ObjectClasses.RecordInfo;
import nl.mprog.rens.vinylcountdown.R;

/**
 * Rens van der Veldt - 10766162
 * Minor Programmeren
 *
 * ColWishActivity.class
 *
 * This is a combined activity that displays the users collection or wishlist. The lists can be
 * expanded by pressing the add button and searching for a record to add. Whether wishlist or
 * collection is displayed is determined by the method that was called.
 * Long clicks allow for item deletion and regular clicks from collection lead to a sale
 * screen and from wishlist lead to a search in the marketplace for that record.
 */

public class ColWishActivity extends AppCompatActivity {

    // Authenticator:
    // The authenticator for firebase.
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    // Initiate the navigation handler:
    NavigationHelper navigationHelper;

    // Declare the overall method this can be collection or wishlist.
    String method;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_col_wish);

        // Set the collection/wishlist method.
        Intent intent = getIntent();
        Bundle methodBundle = intent.getExtras();
        method = methodBundle.getString("method");

        // Adjust the current page text
        TextView currentPage = (TextView) findViewById(R.id.current_page);
        if (method.equals("collection")){
            currentPage.setText(R.string.collection);
        }
        else {
            currentPage.setText(R.string.wishlist);
        }

        // Initiate the navigation handler.
        navigationHelper = new NavigationHelper(this);

        // Initiate the authentication
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

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

        // If there is a user continue loading list.
        if (user != null){
            loadColWishlist(user);
        }

        // Set the spinner.
        setSpinner();
    }

    public void setSpinner(){

        // Find the spinner and set the adapter
        final Spinner sortingOptions = (Spinner) findViewById(R.id.sortingOptions);
        final String[] sortingItems = getResources().getStringArray(R.array.sortingOptions);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, sortingItems);
        sortingOptions.setAdapter(adapter);

        // Set a listener for ordering.
        sortingOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                orderBy(sortingOptions);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * This function gets the method and sets the page title accordingly, it then gets a reference
     * and retrieves the collection/wishlist from the firebase by queryingbthe users id. The id
     * identifies to which user the collection item belongs. After retrieval the listview is filled
     * with the collection/wishlist data. Two listeners are added, one that listens for long clicks
     * which throw a dialog to confirm deletion of that record. The other is a regular
     * click listener and redirects the user to either sale activity (from the collection) or
     * buysearch activity (from the wishlist).
     */
    public void loadColWishlist(FirebaseUser user){

        // Collection/wishlist reference:
        final DatabaseReference collectionReference = FirebaseDatabase.getInstance().getReference().child(method);

        // Get the users collected/wishlist records.
        Query queryRef = collectionReference.orderByChild("userID").equalTo(user.getUid());
        ValueEventListener collectionListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Declare a list of colwishrecords.
                List<ColWishRecord> colWishRecords = new ArrayList<>();

                // Get all of these records and add them to the list.
                for (DataSnapshot chatSnapshot: dataSnapshot.getChildren()) {
                    ColWishRecord colWishRecord = (ColWishRecord) chatSnapshot.getValue(ColWishRecord.class);

                    colWishRecords.add(colWishRecord);
                }

                // Find the listview to fill
                final ListView lv = (ListView) findViewById(R.id.collectionList);

                // Put the results in the adapter
                CustomColWishAdapter customAlbumAdapter = new CustomColWishAdapter(getApplication(), R.layout.record_item, colWishRecords);
                lv.setAdapter(customAlbumAdapter);
                customAlbumAdapter.notifyDataSetChanged();

                lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {

                        // Get the record we are trying to delete.
                        final ColWishRecord colWishRecord = (ColWishRecord) lv.getItemAtPosition(pos);

                        // Throw a dialog to confirm deletion from wishlist/collection.
                        new AlertDialog.Builder(ColWishActivity.this)
                                .setTitle("Confirm deletion")
                                .setMessage("Do you want to remove " + colWishRecord.getRecordInfo().getTitle() + " from your " + method + ".")
                                .setIcon(R.drawable.logo)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        // Remove the object from the database.
                                        FirebaseDatabase.getInstance().getReference().child(method).child(colWishRecord.getColWishID()).removeValue();
                                        lv.deferNotifyDataSetChanged();
                                    }})
                                .setNegativeButton(android.R.string.no, null).show();
                        return true;
                    }
                });

                // When the record is clicked the wishlist redirects to the market and the collection
                // redirects to the saleactivity.
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {

                        // Get the object from the listview and use that to continue to search.
                        ColWishRecord colWishRecord = (ColWishRecord) lv.getItemAtPosition(arg2);
                        goToSearch(colWishRecord);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        queryRef.addValueEventListener(collectionListener);
    }

    /**
     * This function uses the method and a clicked object to continue to the
     * search variant of either buy or sale activity.
     */
    public void goToSearch(ColWishRecord colWishRecord){

        // Check where to go to next.
        if (method.equals("collection")){

            // Go to the sale activity.
            Intent goToSale = new Intent(ColWishActivity.this, SaleActivity.class);

            // Put the recordinfo as extra
            RecordInfo recordInfo = colWishRecord.getRecordInfo();
            goToSale.putExtra("recordInfo", recordInfo);

            // Start the activity
            startActivity(goToSale);
        }
        if (method.equals("wishlist")){

            // Go to the marketplace and put the title as query in order to search directly.
            Intent goToBuy = new Intent(ColWishActivity.this, BuySearchActivity.class);

            // Put the title as query into the next activity.
            goToBuy.putExtra("query", colWishRecord.getRecordInfo().getTitle());

            // Start the activity
            startActivity(goToBuy);
        }
    }

    /**
     * This funciton sorts the listview by whatever is selected.
     * @param sortingOptions: the spinner that is passed and from which you can retrieve the selected item.
     */
    public void orderBy(Spinner sortingOptions){

        final String selected = sortingOptions.getSelectedItem().toString();
        // Get the listviews adapter.
        ListView lv = (ListView) findViewById(R.id.collectionList);
        CustomColWishAdapter customColWishAdapter = (CustomColWishAdapter) lv.getAdapter();

        // Sort the listview by comparing title or artist if the adapter is not null (which happens when no list is present).
        if (customColWishAdapter != null) {

            customColWishAdapter.sort(new Comparator<ColWishRecord>() {

                public int compare(ColWishRecord arg0, ColWishRecord arg1) {
                    if (selected.equals("Artist")) {
                        return arg0.getRecordInfo().getArtist().compareTo(arg1.getRecordInfo().getArtist());
                    } else if (selected.equals("Album")){
                        return arg0.getRecordInfo().getTitle().compareTo(arg1.getRecordInfo().getTitle());
                    } else {
                        return arg0.getRecordInfo().getTitle().compareTo(arg1.getRecordInfo().getTitle());
                    }
                }
            });
        }
    }

    /**
     * Opens the drawer and calls the helper function. This only serves as an onclick link to the helper.
     * @param view
     */
    public void openDrawer(View view) {
        navigationHelper.openDrawer();
    }

    /**
     * When the add button is pressed this function sends the user to the search activity.
     * The method is also added to let the next activity know whether to add an item to
     * collection or wishlist.
     * @param view
     */
    public void goToAddSearch(View view) {

        // This function leads to the same search activity as for sale but puts a different
        // onclick listener in the results
        Intent goToAddSearch = new Intent(this, RecordSearchActivity.class);

        if (method.equals("collection")){
            goToAddSearch.putExtra("method", "collectionSearch");
        } else {
            goToAddSearch.putExtra("method", "wishlistSearch");
        }
        startActivity(goToAddSearch);
    }
}
