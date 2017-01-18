package nl.mprog.rens.vinylcountdown;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Rens on 16/01/2017.
 */

public class RecordAsyncTask extends AsyncTask<Void, Void, List<RecordInfo>> implements Serializable {

    private String query;
    private List<RecordInfo> searchResults;
    private Activity context;
    private int method;
    private ProgressDialog dialog;



    public RecordAsyncTask(Activity c, String query, int method){

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
        if (searchResults != null){
            CustomAlbumAdapter customAlbumAdapter = new CustomAlbumAdapter(context, R.layout.record_layout, searchResults);
            lv.setAdapter(customAlbumAdapter);
            customAlbumAdapter.notifyDataSetChanged();

            // Set a listener, this is used to send record info to the selling details.
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    Intent goToSaleDetail = new Intent(context, SaleActivity.class);
                    RecordInfo recordInfo = (RecordInfo) lv.getItemAtPosition(arg2);

                    // Get all the values from the record info.
                    String title = recordInfo.getTitle();
                    String artist = recordInfo.getArtist();
                    String summary = recordInfo.getSummary();
                    String imgLink = recordInfo.getImgLinklarge();
                    String mbid = recordInfo.getMbid();
                    Map<String, String> tracks = recordInfo.getTracks();

                    // Put the values into the next activity.
                    goToSaleDetail.putExtra("title", title);
                    goToSaleDetail.putExtra("artist", artist);
                    goToSaleDetail.putExtra("summary", summary);
                    goToSaleDetail.putExtra("imgLink", imgLink);
                    goToSaleDetail.putExtra("mbid", mbid);
                    goToSaleDetail.putExtra("tracks", (Serializable) tracks);

                    // Start the activity
                    context.startActivity(goToSaleDetail);
                }
            });
        }
    }
}
