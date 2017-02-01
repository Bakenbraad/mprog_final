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
import android.widget.TextView;

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
    FirebaseUser user;

    // Initiate the navigation handler:
    HelperNavigationHandler helperNavigationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        // Initiate the navigation handler.
        helperNavigationHandler = new HelperNavigationHandler(this);

        // Initiate the authentication
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

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

        // Load the users inbox, if the user isn't logged in the auth listener will redirect them back.
        loadInbox();
    }

    public void openDrawer(View view) {
        helperNavigationHandler.openDrawer();
    }

    // This function loads the users inbox.
    public void loadInbox(){

        final List<Message> messageList = new ArrayList<>();
        final ListView lv = (ListView) findViewById(R.id.inboxList);

        // Set the empty view
        TextView emptyView = (TextView) findViewById(R.id.inbox_empty_item);
        emptyView.setText("No messages yet");
        lv.setEmptyView(emptyView);

        DatabaseReference mInboxReference = FirebaseDatabase.getInstance().getReference().child("messages");
        Query queryRef = mInboxReference.orderByChild(user.getUid());
        ValueEventListener refListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get UserSettings object and use the values to update the UI
                for (DataSnapshot chatSnapshot: dataSnapshot.getChildren()) {
                    Message message = (Message) chatSnapshot.getValue(Message.class);

                    // Check if this is not the users own message and add:
                    if (!message.getSenderID().equals(user.getUid())){
                        messageList.add(message);
                    }
                }

                // Set the adapter with the retrieved messages.
                CustomInboxAdapter customInboxAdapter = new CustomInboxAdapter(getApplicationContext(), R.layout.inbox_item, messageList);
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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        queryRef.addValueEventListener(refListener);
    }


}