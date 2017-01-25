package nl.mprog.rens.vinylcountdown;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Rens on 16/01/2017.
 */

public class AsyncTaskMusicSearch extends AsyncTask<Void, Void, List<RecordInfo>> implements Serializable {

    private String query;
    private List<RecordInfo> searchResults;
    private Activity activity;
    private ProgressDialog dialog;
    private String method;


    public AsyncTaskMusicSearch(Activity c, String query, String method){

        this.query = query;
        this.activity = c;
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
            searchResults = apiManager.Search(query);
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

                        // Get all the values from the record info.
                        String title = recordInfo.getTitle();
                        String artist = recordInfo.getArtist();
                        String summary = recordInfo.getSummary();
                        String imgLink = recordInfo.getImgLinklarge();
                        String mbid = recordInfo.getMbid();
                        Map<String, String> tracks = recordInfo.getTracks();

                        // Put the values into the next activity.
                        goToSale.putExtra("title", title);
                        goToSale.putExtra("artist", artist);
                        goToSale.putExtra("summary", summary);
                        goToSale.putExtra("imgLink", imgLink);
                        goToSale.putExtra("mbid", mbid);
                        goToSale.putExtra("tracks", (Serializable) tracks);

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

            // Hide the keyboard
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        }
    }
}
