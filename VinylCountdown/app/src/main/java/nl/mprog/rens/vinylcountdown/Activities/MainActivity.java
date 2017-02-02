package nl.mprog.rens.vinylcountdown.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import nl.mprog.rens.vinylcountdown.AdapterClasses.CustomNewsAdapter;
import nl.mprog.rens.vinylcountdown.HelperClasses.NavigationHelper;
import nl.mprog.rens.vinylcountdown.ObjectClasses.NewsItem;
import nl.mprog.rens.vinylcountdown.ObjectClasses.UserProfile;
import nl.mprog.rens.vinylcountdown.R;

/**
 * Rens van der Veldt - 10766162
 * Minor Programmeren
 *
 * MainActivity.class
 *
 * The main activity consists of a couple of things. A welcome text, welcoming the user personally with a retrieved
 * username from the firebase. The news section, this is a listview that displays news items retrieved from
 * firebase and each may be clicked to view the content. Finally there is an info button which
 * is mainly useful for new users. This button displays a text in the infoActivity explaining the
 * various functions of the app and how to navigate around.
 */

public class MainActivity extends AppCompatActivity {

    // Authenticator:
    // The authenticator for firebase.
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    // Initiate the navigation handler:
    NavigationHelper navigationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initiate the navigation handler.
        navigationHelper = new NavigationHelper(this);

        // Initiate the authentication
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is logged in proceed to set their username in the welcome text!
            DatabaseReference mSettingsReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
            ValueEventListener settingsListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    // Get UserSettings object and use the values to update the UI
                    UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                    if (userProfile != null) {
                        TextView userNameTV = (TextView) findViewById(R.id.textUser);
                        userNameTV.setText(userProfile.getUsername());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mSettingsReference.addValueEventListener(settingsListener);
        }
        // Listen for the login state.
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null){
                    // User is signed out, they don't belong here so send them back!
                    new NavigationHelper(getParent()).goToLogin();
                }
            }
        };

        // Get the news info:
        NewsLoader();
    }

    /**
     * The newsloader function retrieves all news objects from firebase and loads them into
     * a listview. The listview then gets an onclicklistener that redirects the news item to
     * the news activity, a dialog theme activity where the content of the news item
     * can be viewed, away from the otherwise chaos of the homescreen.
     */
    public void NewsLoader(){

        // Declare the list and database reference.
        final List<NewsItem> newsList = new ArrayList();
        DatabaseReference mSettingsReference = FirebaseDatabase.getInstance().getReference().child("news");

        ValueEventListener settingsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Get the news articles and add them to a list of newsitems
                for (DataSnapshot chatSnapshot: dataSnapshot.getChildren()) {

                    NewsItem newsItem =  chatSnapshot.getValue(NewsItem.class);
                    newsList.add(newsItem);
                }

                // Find the listview that should contain the news items.
                final ListView lv = (ListView) findViewById(R.id.news_list);

                // Fill the view.
                CustomNewsAdapter customNewsAdapter = new CustomNewsAdapter(getApplicationContext(), R.layout.news_item, newsList);
                lv.setAdapter(customNewsAdapter);
                customNewsAdapter.notifyDataSetChanged();

                // Set the listener that should open the newsactivity with the message content.
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {
                        Intent goToNews = new Intent(getApplicationContext(), NewsActivity.class);
                        NewsItem newsItem = (NewsItem) lv.getItemAtPosition(arg2);

                        // Get the content of the news item.
                        String content = newsItem.getContent();

                        // Put the content into the next activity.
                        goToNews.putExtra("content", content);

                        // Start the activity
                        startActivity(goToNews);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mSettingsReference.addValueEventListener(settingsListener);
    }

    /**
     * Open drawer is called when the button to open the menu is pressed.
     * @param view: passed from the button.
     */
    public void openDrawer(View view) {
        navigationHelper.openDrawer();
    }

    /**
     * This function redirects the user to the dialog themed info screen. A simple activity with
     * info about the app. This function is called by the info button.
     * @param view
     */
    public void goToInfo(View view) {
        Intent goToInfo = new Intent(this, InfoActivity.class);
        startActivity(goToInfo);
    }
}
