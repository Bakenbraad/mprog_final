package nl.mprog.rens.vinylcountdown;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.concurrent.ExecutionException;

public class SaleSearchActivity extends AppCompatActivity {

    EditText searchViewED;

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

    // This is the method (collection/search/wishlist)
    String method;

    // Initiate the navigation handler:
    HelperNavigationHandler helperNavigationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_search);

        // This method may also be approached from the sale, collection and wishlist activities,
        // causing the asynctask to behave a differently, here we get the method that was called:
        Intent intent = getIntent();
        Bundle methodBundle = intent.getExtras();
        method = methodBundle.getString("method");

        // Set the title accordingly:
        TextView currentPage = (TextView) findViewById(R.id.current_page);
        switch (method){
            case ("collectionSearch"):
                currentPage.setText("Add to Collection");
                break;
            case ("wishlistSearch"):
                currentPage.setText("Add to Wishlist");
                break;
            case ("saleSearch"):
                currentPage.setText("Selling");
                break;
        }

        // Get the searchview and switch and set the text for the searchview.
        searchViewED = (EditText) findViewById(R.id.searchSaleED);
        searchViewED.setHint("Search");

        // Initiate the navigation handler.
        helperNavigationHandler = new HelperNavigationHandler(this);

        // Initiate the authentication
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Check the login state and welcome the user if they are logged in.
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // User is signed out, they don't belong here so send them back!
                    new HelperNavigationHandler(getParent()).goToLogin();
                }
            }
        };

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
    }

    public void openDrawer(View view) {
        helperNavigationHandler.openDrawer();
    }

    public void searchMusic() throws ExecutionException, InterruptedException {

        // Get the query
        String query = searchViewED.getText().toString();

        // Search for music if the query is long enough, otherwise let the user know of their mistakes.
        if (query.length() < 2){
            Toast.makeText(this, "Please give us more to go on...",Toast.LENGTH_SHORT).show();
            searchViewED.setError("Invalid search");
        } else {
            new AsyncMusicSearch(this, query, method, mAuth.getCurrentUser().getUid()).execute();
        }
    }

    public void onSearchClick(View view) throws ExecutionException, InterruptedException {
        searchMusic();
    }
}
