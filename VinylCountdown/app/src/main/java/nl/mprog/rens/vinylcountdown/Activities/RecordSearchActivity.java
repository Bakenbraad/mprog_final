package nl.mprog.rens.vinylcountdown.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
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

import nl.mprog.rens.vinylcountdown.HelperClasses.AsyncMusicSearch;
import nl.mprog.rens.vinylcountdown.HelperClasses.NavigationHelper;
import nl.mprog.rens.vinylcountdown.R;

/**
 * Rens van der Veldt - 10766162
 * Minor Programmeren
 *
 * RecordSearchActivity.class
 *
 * The sale search activity allows users to search for records with help of the last fm api. A query
 * of artist, title or album can be put in and the ApiHelper returns a list of results. These results
 * are neatly displayed and users can select them in order to do an action dependant on the method called.
 * The search method continues to the SaleActivity and lets the user sell the found records. From collection
 * the user gets a dialog whether they want to confirm adding to the collection. From the wishlist the same
 * happens as with the colleciton method. But the item is added to the wishlist instead.
 */
public class RecordSearchActivity extends AppCompatActivity {

    // Declare the search editText.
    EditText searchViewED;

    // Authenticator:
    // The authenticator for firebase.
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    // This is the method (collection/search/wishlist)
    String method;

    // Initiate the navigation handler:
    NavigationHelper navigationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_search);

        // This method may be approached from the sale, collection and wishlist activities,
        // causing the asynctask to behave a differently, here we get the method that was called
        // from the intent:
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
        navigationHelper = new NavigationHelper(this);

        // Initiate the authentication
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Check the login state.
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // User is signed out, they don't belong here so send them back!
                    new NavigationHelper(getParent()).goToLogin();
                }
            }
        };

        // Set the action of the keyboard to be search and make that activate the searchMusic function.
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

    /**
     * Open drawer is called when the button to open the menu is pressed.
     * @param view: passed from a button.
     */
    public void openDrawer(View view) {
        navigationHelper.openDrawer();
    }

    /**
     * This function executes the asynctask that gets the records matching the query. This is done
     * using the api helper.
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void searchMusic() throws ExecutionException, InterruptedException {

        // Get the query from the edittext.
        String query = searchViewED.getText().toString();

        // Search for music if the query is long enough, otherwise let the user know of their mistakes.
        if (query.length() < 2){

            // Too short a query will take a long time to load.
            Toast.makeText(this, "Please give us more to go on...",Toast.LENGTH_SHORT).show();
            searchViewED.setError("Invalid search");

        } else {

            // Check if there is a user available, this is used in the collection/wishlist method for
            // writing colWishRecords to the database.
            if (mAuth.getCurrentUser() != null){

                // Find music!
                new AsyncMusicSearch(this, query, method, mAuth.getCurrentUser().getUid()).execute();
            }
        }
    }

    /**
     * This is the onclick for the search button. This executes the searchMusic function.
     * @param view
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void onSearchClick(View view) throws ExecutionException, InterruptedException {
        searchMusic();
    }
}
