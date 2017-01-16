package nl.mprog.rens.vinylcountdown;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

/**
 * Created by Rens on 16/01/2017.
 */

public class RecordAsyncTask extends AsyncTask<Void, Void, List<RecordInfo>> {

    private String query;
    private List<RecordInfo> searchResults;
    private Activity context;


    public RecordAsyncTask(Activity c, String query){

        this.query = query;
        this.context = c;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<RecordInfo> doInBackground(Void... params) {
        ApiManager apiManager = new ApiManager();

        // Use the apiManager to search for results.
        searchResults = apiManager.Search(query);

        return searchResults;
    }

    @Override
    protected void onPostExecute(List<RecordInfo> searchResults) {
        super.onPostExecute(searchResults);
        final ListView lv = (ListView) context.findViewById(R.id.saleResult);
        if (searchResults != null){
            CustomAlbumAdapter customAlbumAdapter = new CustomAlbumAdapter(context, R.layout.record_layout, searchResults);
            lv.setAdapter(customAlbumAdapter);
            customAlbumAdapter.notifyDataSetChanged();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    Intent goToSaleDetail = new Intent(context, SaleActivity.class);
                    goToSaleDetail.putExtra("Record", (RecordSaleInfo) lv.getItemAtPosition(arg2));
                    context.startActivity(goToSaleDetail);
                }
            });
        }
    }
}
