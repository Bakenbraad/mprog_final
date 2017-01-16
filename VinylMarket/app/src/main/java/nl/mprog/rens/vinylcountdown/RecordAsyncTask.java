package nl.mprog.rens.vinylcountdown;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ListView;

import java.util.List;import nl.mprog.com.example.rens.vinylmarket.R;

/**
 * Created by Rens on 14/01/2017.
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
        ListView lv = (ListView) context.findViewById(R.id.saleResult);
        if (searchResults != null){
            CustomAlbumAdapter customAlbumAdapter = new CustomAlbumAdapter(context, R.layout.record_layout, searchResults);
            lv.setAdapter(customAlbumAdapter);
            customAlbumAdapter.notifyDataSetChanged();
        }
    }
}
