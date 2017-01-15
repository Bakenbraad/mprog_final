package nl.mprog.com.example.rens.vinylmarket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class SaleSearchActivity extends AppCompatActivity {

    EditText musicSearchED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_search);
    }

    public void searchMusic(View view) throws ExecutionException, InterruptedException {
        musicSearchED = (EditText) findViewById(R.id.searchSaleED);
        String query = musicSearchED.getText().toString();

        new RecordAsyncTask(this, query).execute();
    }
}
