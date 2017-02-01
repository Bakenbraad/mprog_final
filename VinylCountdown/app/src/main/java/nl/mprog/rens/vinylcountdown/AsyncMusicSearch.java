package nl.mprog.rens.vinylcountdown;

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


    public AsyncMusicSearch(Activity c, String query, String method, String userID){

        this.query = query;
        this.activity = c;
        this.method = method;
        this.userID = userID;

        // Create a progress dialog.
        dialog = new ProgressDialog(c);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
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

            Toast.makeText(activity, "Found " + searchResults.size() + " records!", Toast.LENGTH_SHORT).show();

            // Set the empty view
            TextView emptyView = (TextView) activity.findViewById(R.id.inbox_empty_item);
            emptyView.setText(R.string.no_search_results);
            lv.setEmptyView(emptyView);

            CustomAlbumAdapter customAlbumAdapter = new CustomAlbumAdapter(activity, R.layout.record_item, searchResults);
            lv.setAdapter(customAlbumAdapter);
            customAlbumAdapter.notifyDataSetChanged();

            if (method.equals("saleSearch")){
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

            if (method.equals("tradeSearch")){
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

            else if (method.equals("collectionSearch")){
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
                                    }})
                                .setNegativeButton(android.R.string.no, null).show();
                    }
                });

            }

            else if (method.equals("wishlistSearch")){
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
                                    }})
                                .setNegativeButton(android.R.string.no, null).show();
                    }
                });

            }

            // Hide the keyboard
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        }
    }
}
