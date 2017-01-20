package nl.mprog.rens.vinylcountdown;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
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
import java.util.Map;

/**
 * Created by Rens on 19/01/2017.
 */

public class MarketAsyncTask extends AsyncTask<Void, Void, List<RecordInfo>> implements Serializable {

    private String query;
    private List<RecordInfo> searchResults;
    private Activity context;
    private int method;
    private ProgressDialog dialog;


    public MarketAsyncTask(Activity c, String query, int method) {

        this.query = query;
        this.context = c;
        this.method = method;

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
        ApiManager apiManager = new ApiManager();

        // Use the apiManager to search for results using the correct method.
        try {
            searchResults = apiManager.Search(query, method);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return searchResults;
    }

    @Override
    protected void onPostExecute(List<RecordInfo> searchResults) {
        super.onPostExecute(searchResults);

        // Close the dialog
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        // Find the listview to fill
        final ListView lv = (ListView) context.findViewById(R.id.saleResult);

        // If there are results put them in the listview.
        if (searchResults != null) {
            // Make a list for the matches.
            final List<RecordSaleInfo> recordSaleInfoList = new ArrayList<>();

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
                        for (DataSnapshot chatSnapshot: dataSnapshot.getChildren()) {
                            RecordSaleInfo recordSaleInfo = (RecordSaleInfo) chatSnapshot.getValue(RecordSaleInfo.class);
                            if (recordSaleInfo.getMbid().equals(mbid)) {
                                recordSaleInfoList.add(recordSaleInfo);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                queryRef.addListenerForSingleValueEvent(refListener);

            }
            CustomMarketAdapter customMarketAdapter = new CustomMarketAdapter(context, R.layout.record_sale_layout, recordSaleInfoList);
            lv.setAdapter(customMarketAdapter);
            customMarketAdapter.notifyDataSetChanged();

            // Set a listener, this is used to send record info to the selling details.
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    Intent goToBuy = new Intent(context, BuyActivity.class);
                    RecordSaleInfo recordSaleInfo = (RecordSaleInfo) lv.getItemAtPosition(arg2);

                    // Get all the values from the record info.
                    String title = recordSaleInfo.getTitle();
                    String artist = recordSaleInfo.getArtist();
                    String description = recordSaleInfo.getDescription();
                    String imgLink = recordSaleInfo.getImgLink();
                    String mbid = recordSaleInfo.getMbid();

                    // Put the values into the next activity.
                    goToBuy.putExtra("title", title);
                    goToBuy.putExtra("artist", artist);
                    goToBuy.putExtra("summary", description);
                    goToBuy.putExtra("imgLink", imgLink);
                    goToBuy.putExtra("mbid", mbid);

                    // Start the activity
                    context.startActivity(goToBuy);
                }
            });
        }
    }
}


