package nl.mprog.rens.vinylcountdown;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class SaleSearchActivity extends AppCompatActivity {

    EditText searchViewED;
    Switch switchMethod;
    int currentMethod;
    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_search);

        // Get the searchview and switch and set the text for the searchview.
        searchViewED = (EditText) findViewById(R.id.searchSaleED);
        searchViewED.setHint("Enter Album");

        switchMethod = (Switch) findViewById(R.id.switch1);

        // Set the initial method:
        currentMethod = 1;

        // Change the method when its clicked.
        switchMethod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    // method is search artists
                    currentMethod = 1;
                    searchViewED.setHint("Enter Artist");
                    Toast.makeText(getApplicationContext(),"Artist mode", Toast.LENGTH_SHORT).show();
                } else {
                    // method is search albums
                    currentMethod = 2;
                    searchViewED.setHint("Enter Album");
                    Toast.makeText(getApplicationContext(),"Album mode", Toast.LENGTH_SHORT).show();
                }
            }
        });

        searchViewED.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    // On search action send query to asynctask
                    try {
                        searchMusic(currentMethod);
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public void searchMusic(int method) throws ExecutionException, InterruptedException {

        // Search for music if the query is long enough, otherwise let the user know of their mistakes.
        if (searchViewED.getText().toString().length() < 2){
            Toast.makeText(this, "Please give us more to go on...",Toast.LENGTH_SHORT).show();
            searchViewED.setError("Invalid search");
        } else {
            query = searchViewED.getText().toString();
            new RecordAsyncTask(this, query, method).execute();
        }
    }

    public void onSearchClick(View view) throws ExecutionException, InterruptedException {
        searchMusic(currentMethod);
    }
}
