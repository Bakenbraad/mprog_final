package nl.mprog.rens.vinylcountdown;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity {

    // Authenticator:
    // The authenticator for firebase.
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    // Initiate drawer modules:
    DrawerLayout drawerLayout;
    ListView drawers;
    String[] navigations;
    Button menuButton;

    // Initiate the navigation handler:
    HelperNavigationHandler helperNavigationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initiate the navigation handler.
        helperNavigationHandler = new HelperNavigationHandler(this);

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
                    new HelperNavigationHandler(getParent()).goToLogin();
                }
            }
        };

        // Get the news info:
        NewsLoader();

        // Navigation drawer from: https://developer.android.com/training/implementing-navigation/nav-drawer.html#Init
        navigations = getResources().getStringArray(R.array.menuOptions);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawers = (ListView) findViewById(R.id.main_drawer);
        menuButton = (Button) findViewById(R.id.menubutton);

        // Set the adapter for the list view
        drawers.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_item, navigations));
        // Set the list's click listener
        drawers.setOnItemClickListener(new DrawerItemClickListener());
    }

    // Change the button appearance when it is clicked.
    public void openDrawer(View view) {

        if(drawerLayout.isDrawerOpen(drawers)){
            drawerLayout.closeDrawer(drawers);
            menuButton.setText("+");
        }
        else {
            drawerLayout.openDrawer(drawers);
            menuButton.setText("-");
        }
    }

    // This is the onclicklistener for the drawer, it sends data to navigation.
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            helperNavigationHandler.redirect(position);
            drawerLayout.closeDrawer(drawers);
            menuButton.setText("+");
        }
    }

    public void NewsLoader(){

        // Declare the list and database reference.
        final List<NewsItem> newsList = new ArrayList();
        DatabaseReference mSettingsReference = FirebaseDatabase.getInstance().getReference().child("news");

        ValueEventListener settingsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get UserSettings object and use the values to update the UI
                for (DataSnapshot chatSnapshot: dataSnapshot.getChildren()) {
                    NewsItem newsItem =  chatSnapshot.getValue(NewsItem.class);
                    newsList.add(newsItem);
                }

                final ListView lv = (ListView) findViewById(R.id.news_list);

                // Fill the view.
                CustomNewsAdapter customNewsAdapter = new CustomNewsAdapter(getApplicationContext(), R.layout.news_item, newsList);
                lv.setAdapter(customNewsAdapter);
                customNewsAdapter.notifyDataSetChanged();

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {
                        Intent goToNews = new Intent(getApplicationContext(), NewsActivity.class);
                        NewsItem newsItem = (NewsItem) lv.getItemAtPosition(arg2);

                        // Get all the values from the record info.
                        String content = newsItem.getContent();

                        // Put the value into the next activity.
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
}
