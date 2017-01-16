package nl.mprog.rens.vinylcountdown;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;import nl.mprog.com.example.rens.vinylmarket.R;

public class CollectionSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_search);
    }

    public void goToCollection(View view) {
        Intent goToCollection = new Intent(this, CollectionActivity.class);
        startActivity(goToCollection);
    }
}
