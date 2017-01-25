package nl.mprog.rens.vinylcountdown;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Rens on 23/01/2017.
 */

public class AsyncTaskNews extends AsyncTask<Void, Void, List<NewsItem>> {

    Activity activity;
    Context c;
    List<NewsItem> newsList = new ArrayList();

    public AsyncTaskNews(Activity activity, Context context){
        this.activity = activity;
        this.c = context;
    }

    @Override
    protected List<NewsItem> doInBackground(Void... params) {

        DatabaseReference mSettingsReference = FirebaseDatabase.getInstance().getReference().child("news");

        ValueEventListener settingsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get UserSettings object and use the values to update the UI
                for (DataSnapshot chatSnapshot: dataSnapshot.getChildren()) {
                    NewsItem newsItem =  chatSnapshot.getValue(NewsItem.class);
                    newsList.add(newsItem);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mSettingsReference.addValueEventListener(settingsListener);
        return null;

    }

    @Override
    protected void onPostExecute(List<NewsItem> newsItems) {

        // Find the listview to fill
        final ListView lv = (ListView) activity.findViewById(R.id.news_list);

        // Fill the view.
        CustomNewsAdapter customNewsAdapter = new CustomNewsAdapter(activity.getApplicationContext(), R.layout.news_item, newsList);
        lv.setAdapter(customNewsAdapter);
        customNewsAdapter.notifyDataSetChanged();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                Intent goToNews = new Intent(activity, NewsActivity.class);
                NewsItem newsItem = (NewsItem) lv.getItemAtPosition(arg2);

                // Get all the values from the record info.
                String content = newsItem.getContent();

                // Put the value into the next activity.
                goToNews.putExtra("content", content);

                // Start the activity
                activity.startActivity(goToNews);
            }
        });
    }
}
