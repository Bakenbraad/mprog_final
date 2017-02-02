package nl.mprog.rens.vinylcountdown.HelperClasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import nl.mprog.rens.vinylcountdown.ObjectClasses.ColWishRecord;
import nl.mprog.rens.vinylcountdown.AdapterClasses.CustomAlbumAdapter;
import nl.mprog.rens.vinylcountdown.R;
import nl.mprog.rens.vinylcountdown.ObjectClasses.RecordInfo;
import nl.mprog.rens.vinylcountdown.Activities.SaleActivity;

/**
 * Rens van der Veldt - 10766162
 * Minor Programmeren
 *
 * AsyncMusicSearch.class
 *
 * This asynctask manages the main api search function. The results of which are put into a listview of different kinds,
 * depending on the methodcall. The constructor takes the activity at which the function is ran, the query with which
 * to run the api manager, the method (which determines how the data is displayed and what onclick does) and finally
 * the user ID. The userID is used in the methods for wishlist and collection when a record is added to either.
 */

public class AsyncMusicSearch extends AsyncTask<Void, Void, List<RecordInfo>> implements Serializable {

    private String query;
    private List<RecordInfo> searchResults;
    private Activity activity;
    private ProgressDialog dialog;
    private String method;
    private String userID;


    /**
     * The main asynctast function, takes in the activity where it is executed, a query to search with
     * and the method and userid, the userid being used for writing collection and wishlist data.
     */
    public AsyncMusicSearch(Activity activity, String query, String method, String userID){

        this.query = query;
        this.activity = activity;
        this.method = method;
        this.userID = userID;

        // Create a progress dialog.
        dialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // Throw a dialog that shows the user progress.
        this.dialog.setMessage("Looking for " + query);
        this.dialog.show();
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
        super.onPostExecute(searchResults);

        // Close the dialog
        if (dialog.isShowing() && dialog != null) {
            dialog.dismiss();
        }

        // Find the listview to fill
        final ListView lv = (ListView) activity.findViewById(R.id.saleResult);

        // If there are results put them in the listview.
        if (searchResults != null){

            // Show the amount of results
            Toast.makeText(activity, "Found " + searchResults.size() + " records!", Toast.LENGTH_SHORT).show();

            // Set the empty view
            TextView emptyView = (TextView) activity.findViewById(R.id.inbox_empty_item);
            emptyView.setText(R.string.no_search_results);
            lv.setEmptyView(emptyView);

            // Create the adapter with the search results.
            CustomAlbumAdapter customAlbumAdapter = new CustomAlbumAdapter(activity, R.layout.record_item, searchResults);
            lv.setAdapter(customAlbumAdapter);
            customAlbumAdapter.notifyDataSetChanged();

            // Depending on the method different listeners should be set.
            switch (method) {
                case "saleSearch":
                    setSaleListener(lv);
                    break;
                case "tradeSearch":
                    setTradeListener(lv);
                    break;
                case "collectionSearch":
                    setCollectionListener(lv);
                    break;
                case "wishlistSearch":
                    setWishlistListener(lv);
                    break;
            }

            // Hide the keyboard after search.
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        }
    }

    /**
     * Sets a listener when the wishlist method is called. This makes it possible for an onclick
     * to write a colwishrecord object to database after a dialog is confirmed.
     * @param lv: The listview where a listener should be set.
     */
    private void setWishlistListener(final ListView lv){

        // Set a listener, this is used to send record info to the selling details.
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {

                // Get the object from the listview.
                final RecordInfo recordInfo = (RecordInfo) lv.getItemAtPosition(arg2);

                // Throw a dialog to confirm the addition.
                new AlertDialog.Builder(activity)
                        .setTitle(R.string.confirm_add)
                        .setMessage(R.string.confirm_wish_text)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                // Get a reference.
                                DatabaseReference mCollectionReference = FirebaseDatabase.getInstance().getReference().child("wishlist");

                                // Create a collection item:
                                String colWishKey = mCollectionReference.push().getKey();
                                ColWishRecord colWishRecord = new ColWishRecord(recordInfo, userID, colWishKey);

                                // Put the record in the collection.
                                mCollectionReference.child(colWishKey).setValue(colWishRecord);

                                // Notify the user.
                                Toast.makeText(activity.getApplicationContext(), R.string.wish_add, Toast.LENGTH_SHORT).show();

                                activity.finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
    }

    /**
     * Sets a listener when the collection method is called. This makes it possible for an onclick
     * to write a colwishrecord object to database after a dialog is confirmed.
     * @param lv: The listview where a listener should be set.
     */
    private void setCollectionListener(final ListView lv){

        // Set a listener, this is used to send record info to the selling details.
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {

                // Get the object from the listview.
                final RecordInfo recordInfo = (RecordInfo) lv.getItemAtPosition(arg2);

                // Throw a dialog to confirm the addition.
                new AlertDialog.Builder(activity)
                        .setTitle(R.string.confirm_add)
                        .setMessage(R.string.confirm_add_text)
                        .setIcon(R.drawable.logo)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                // Get a reference.
                                DatabaseReference mCollectionReference = FirebaseDatabase.getInstance().getReference().child("collection");

                                // Create a collection item:
                                String colWishKey = mCollectionReference.push().getKey();
                                ColWishRecord colWishRecord = new ColWishRecord(recordInfo, userID, colWishKey);

                                // Put the record in the collection.
                                mCollectionReference.child(colWishKey).setValue(colWishRecord);

                                // Notify the user.
                                Toast.makeText(activity.getApplicationContext(), R.string.acc_collection, Toast.LENGTH_SHORT).show();

                                activity.finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
    }

    /**
     * The trade listener is used when a user is searching for a record to trade in the marketplace.
     * The onclick then hides the results list and shows which record was selected in a textview.
     * @param lv: The listview where a listener should be set.
     */
    private void setTradeListener(final ListView lv){

        // Set a listener, this is used to send record info to the selling details.
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {

                // If looking for a trade record, replace the listview with selected record.
                RecordInfo recordInfo = (RecordInfo) lv.getItemAtPosition(arg2);

                // Hide the list and show the result in a textview. Another textview is used for invisible data passing.
                lv.setVisibility(View.GONE);
                TextView tradeResultTV = (TextView) activity.findViewById(R.id.tradeResult);
                TextView selectedView = (TextView) activity.findViewById(R.id.selectedRecord);

                tradeResultTV.setText("Selected: " + recordInfo.getTitle() + ", by " + recordInfo.getArtist());
                selectedView.setText(recordInfo.getTitle());
                tradeResultTV.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * The sale listener is called when the salesearch method is called. This listener sends the
     * user to the sale activity with the clicked recordinfo object to potentially create a sale.
     * @param lv: The listview where a listener should be set.
     */
    private void setSaleListener(final ListView lv){

        // Set a listener, this is used to send record info to the selling details.
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                Intent goToSale = new Intent(activity, SaleActivity.class);
                RecordInfo recordInfo = (RecordInfo) lv.getItemAtPosition(arg2);

                // Put the recordInfo into the next activity.
                goToSale.putExtra("recordInfo", recordInfo);

                // Start the activity
                activity.startActivity(goToSale);
            }
        });
    }
}
