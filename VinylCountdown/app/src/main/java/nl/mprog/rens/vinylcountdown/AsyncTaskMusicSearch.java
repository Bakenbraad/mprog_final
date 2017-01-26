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
 * Created by Rens on 16/01/2017.
 */

public class AsyncTaskMusicSearch extends AsyncTask<Void, Void, List<RecordInfo>> implements Serializable {

    private String query;
    private List<RecordInfo> searchResults;
    private Activity activity;
    private ProgressDialog dialog;
    private String method;
    private String userID;


    public AsyncTaskMusicSearch(Activity c, String query, String method, String userID){

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
            emptyView.setText("No search results");
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
                                .setTitle("Confirm addition")
                                .setMessage("Do you want to add this record to your collection?")
                                .setIcon(R.drawable.logo)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        // Create a collection item:
                                        CollectionWishlistRecord collectionWishlistRecord = new CollectionWishlistRecord(recordInfo, userID);

                                        // Put the record in the collection.
                                        DatabaseReference mCollectionReference = FirebaseDatabase.getInstance().getReference().child("collection");
                                        mCollectionReference.push().setValue(collectionWishlistRecord);

                                        // Notify the user.
                                        Toast.makeText(activity.getApplicationContext(), "Added to collection", Toast.LENGTH_SHORT).show();

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
                                .setTitle("Confirm addition")
                                .setMessage("Do you want to add this record to your wishlist?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        // Create a collection item:
                                        CollectionWishlistRecord collectionWishlistRecord = new CollectionWishlistRecord(recordInfo, userID);

                                        // Put the record in the collection.
                                        DatabaseReference mCollectionReference = FirebaseDatabase.getInstance().getReference().child("wishlist");
                                        mCollectionReference.push().setValue(collectionWishlistRecord);

                                        // Notify the user.
                                        Toast.makeText(activity.getApplicationContext(), "Added to wishlist", Toast.LENGTH_SHORT).show();

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
