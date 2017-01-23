package nl.mprog.rens.vinylcountdown;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.concurrent.ExecutionException;

public class SaleSearchActivity extends AppCompatActivity {

    EditText searchViewED;
    Switch switchMethod;
    int currentMethod;
    String query;

    // Authenticator:
    // The authenticator for firebase.
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference mDatabase;

    // Initiate drawer modules:
    DrawerLayout drawerLayout;
    ListView drawers;
    String[] navigations;
    Button menuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_search);

        // Get the searchview and switch and set the text for the searchview.
        searchViewED = (EditText) findViewById(R.id.searchSaleED);
        searchViewED.setHint("Enter Album");

        switchMethod = (Switch) findViewById(R.id.switch1);

        // Change the method when its clicked.
        switchMethod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    // method is search artists
                    searchViewED.setHint("Enter Artist");
                    Toast.makeText(getApplicationContext(),"Artist mode", Toast.LENGTH_SHORT).show();
                } else {
                    // method is search albums
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
                        searchMusic();
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });

        // Navigation drawer from: https://developer.android.com/training/implementing-navigation/nav-drawer.html#Init
        navigations = getResources().getStringArray(R.array.menuOptions);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawers = (ListView) findViewById(R.id.sale_drawer);

        // Set the adapter for the list view
        drawers.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_layout, navigations));
        // Set the list's click listener
        drawers.setOnItemClickListener(new SaleSearchActivity.DrawerItemClickListener());
    }

    // Redirects the user to their profile.
    public void goToProfile() {
        Intent goToProfile = new Intent(this, ProfileActivity.class);
        startActivity(goToProfile);
        finish();
    }

    // Redirects the user to where they can search possible records they can sell.
    public void goToSaleSearch() {

    }

    public void goToBuySearch(){
        Intent goToBuySeach = new Intent(this, BuySearchActivity.class);
        startActivity(goToBuySeach);
        finish();
    }

    // Redirects the user to the login screen and logs them out.
    public void goToLogin() {
        Intent goToLogin = new Intent(this, LoginActivity.class);
        mAuth.getCurrentUser();
        mAuth.signOut();
        startActivity(goToLogin);
        finish();
    }

    public void openDrawer(View view) {
        drawerLayout.openDrawer(drawers);
    }

    // onclicklistener for the drawer, manages navigation.
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        // Get the right position and go to the right activity.
        String[] menuOptions = getResources().getStringArray(R.array.menuOptions);
        switch (menuOptions[position]){
            case ("Menu"):
                break;
            case ("Sell"):
                goToSaleSearch();
                break;
            case ("Buy"):
                goToBuySearch();
                break;
            case ("Profile"):
                goToProfile();
                break;
            case ("Logout"):
                goToLogin();
                break;
        }
        drawerLayout.closeDrawer(drawers);
    }

    public void searchMusic() throws ExecutionException, InterruptedException {

        // Search for music if the query is long enough, otherwise let the user know of their mistakes.
        if (searchViewED.getText().toString().length() < 2){
            Toast.makeText(this, "Please give us more to go on...",Toast.LENGTH_SHORT).show();
            searchViewED.setError("Invalid search");
        } else {
            query = searchViewED.getText().toString();
            if (switchMethod.isChecked()){
                new RecordAsyncTask(this, query, 1).execute();
            }
            else {
                new RecordAsyncTask(this, query, 2).execute();
            }
        }
    }

    public void onSearchClick(View view) throws ExecutionException, InterruptedException {
        searchMusic();
    }
}