package nl.mprog.rens.vinylcountdown;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.concurrent.ExecutionException;import nl.mprog.com.example.rens.vinylmarket.R;

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
