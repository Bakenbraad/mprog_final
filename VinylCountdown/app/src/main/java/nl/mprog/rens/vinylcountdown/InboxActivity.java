package nl.mprog.rens.vinylcountdown;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InboxActivity extends AppCompatActivity {

    // Authenticator:
    // The authenticator for firebase.
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    // Initiate drawer modules:
    DrawerLayout drawerLayout;
    ListView drawers;
    String[] navigations;
    Button menuButton;
    CustomInboxAdapter customInboxAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        mAuth = FirebaseAuth.getInstance();
        menuButton = (Button) findViewById(R.id.menubutton);

        // Load the users inbox, if the user isn't logged in the auth listener will redirect them back.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            new MessageAsyncTask(user.getUid()).execute();
        }

        // Check the login state.
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // Get the inbox
                    new MessageAsyncTask(user.getUid()).execute();

                } else {
                    // User is signed out, they don't belong here so send them back!
                    goToLogin();
                }
            }
        };

        // Navigation drawer from: https://developer.android.com/training/implementing-navigation/nav-drawer.html#Init
        navigations = getResources().getStringArray(R.array.menuOptions);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawers = (ListView) findViewById(R.id.main_drawer);

        // Set the adapter for the list view
        drawers.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_item, navigations));
        // Set the list's click listener
        drawers.setOnItemClickListener(new InboxActivity.DrawerItemClickListener());
    }


    // Redirects the user to their profile.
    public void goToProfile() {
        Intent goToProfile = new Intent(this, ProfileActivity.class);
        startActivity(goToProfile);
        finish();
    }

    // Redirects the user to where they can search the marketplace.
    public void goToMarketSearch() {
        Intent goToMarketSearch = new Intent(this, BuySearchActivity.class);
        startActivity(goToMarketSearch);
        finish();
    }

    // Redirects the user to where they can search possible records they can sell.
    public void goToSaleSearch() {
        Intent goToSaleSearch = new Intent(this, SaleSearchActivity.class);
        startActivity(goToSaleSearch);
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

    // Redirects to the inbox:
    public void goToInbox() {
        Intent goToInbox = new Intent(this, InboxActivity.class);
        startActivity(goToInbox);
        finish();
    }

    public void goToMainMenu(){
        Intent goToMain = new Intent(this, MainActivity.class);
        startActivity(goToMain);
        finish();
    }

    public void openDrawer(View view) {

        if (drawerLayout.isDrawerOpen(drawers)) {
            drawerLayout.closeDrawer(drawers);
            menuButton.setText("+");
        } else {
            drawerLayout.openDrawer(drawers);
            menuButton.setText("-");
        }
    }

    public void refreshContent(View view) {
        ListView lv = (ListView) findViewById(R.id.inboxList);
        CustomInboxAdapter customInboxAdapter = (CustomInboxAdapter) lv.getAdapter();
        customInboxAdapter.notifyDataSetChanged();
    }

    // onclicklistener for the drawer, manages navigation.
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position) {
        // Get the right position and go to the right activity.
        String[] menuOptions = getResources().getStringArray(R.array.menuOptions);
        switch (menuOptions[position]) {
            case ("Menu"):
                goToMainMenu();
                break;
            case ("Sell"):
                goToSaleSearch();
                break;
            case ("Buy"):
                goToMarketSearch();
                break;
            case ("Profile"):
                goToProfile();
                break;
            case ("Logout"):
                goToLogin();
                break;
            case ("Inbox"):
                goToInbox();
                break;
        }
        drawerLayout.closeDrawer(drawers);
        menuButton.setText("+");
    }

    public class MessageAsyncTask extends AsyncTask<Void, Void, List<Message>>{

        String userID;
        List<Message> messageList;

        public MessageAsyncTask(String userID){
            this.userID = userID;
        }

        @Override
        protected List<Message> doInBackground(Void... params) {

            // Initiate messageList
            messageList = new ArrayList<>();
            DatabaseReference mInboxReference = FirebaseDatabase.getInstance().getReference().child("messages");
            Query queryRef = mInboxReference.orderByChild(userID);
            ValueEventListener refListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get UserSettings object and use the values to update the UI
                    for (DataSnapshot chatSnapshot: dataSnapshot.getChildren()) {
                        Message message = (Message) chatSnapshot.getValue(Message.class);

                        // Check if this is not the users own message and add:
                        if (!message.getSenderID().equals(userID)){
                            messageList.add(message);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            queryRef.addListenerForSingleValueEvent(refListener);

            return messageList;
        }

        @Override
        protected void onPostExecute(List<Message> messages) {

            if (messages != null){

                // Set the adapter with the retrieved messages.
                final ListView lv = (ListView) findViewById(R.id.inboxList);
                CustomInboxAdapter customInboxAdapter = new CustomInboxAdapter(getApplicationContext(), R.layout.inbox_item, messages);
                lv.setAdapter(customInboxAdapter);
                customInboxAdapter.notifyDataSetChanged();

                // Set a listener for the listview.
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {

                        // Get the message from the listview.
                        Message message = (Message) lv.getItemAtPosition(arg2);

                        // Send it to the detail view.
                        Intent goToInboxDetail = new Intent(getApplicationContext(), InboxDetail.class);
                        goToInboxDetail.putExtra("message", message);
                        startActivity(goToInboxDetail);
                    }
                });

            }
        }
    }


}