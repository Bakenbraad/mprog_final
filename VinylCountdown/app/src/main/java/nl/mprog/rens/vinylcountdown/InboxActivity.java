package nl.mprog.rens.vinylcountdown;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import nl.mprog.rens.vinylcountdown.AdapterClasses.CustomInboxAdapter;
import nl.mprog.rens.vinylcountdown.HelperClasses.NavigationHelper;
import nl.mprog.rens.vinylcountdown.ObjectClasses.Message;

/**
 * Rens van der Veldt - 10766162
 * Minor Programmeren
 *
 * InboxActivity.class
 *
 * This activity has the main function to show a user their received messages. The oncreate calls
 * loadInbox and retrieves the users messages and sets an onclicklistener that allows for continuing
 * to the message detail activity.
 */

public class InboxActivity extends AppCompatActivity {

    // Authenticator:
    // The authenticator for firebase.
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;

    // Declare the navigation handler:
    NavigationHelper navigationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        // Initiate the navigation handler.
        navigationHelper = new NavigationHelper(this);

        // Initiate the authentication
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

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

        // Load the users inbox, if the user isn't logged in the auth listener will redirect them back.
        loadInbox();
    }

    // Open drawer is called when the button to open the menu is pressed.
    public void openDrawer(View view) {
        navigationHelper.openDrawer();
    }

    /**
     * This function loads the users inbox. A database reference using the users profile retrieves
     * their messages (received not sent) and puts them in a list for the CustomInboxAdapter to use.
     * The results are displayed in a custom inbox_item in the listview. The onclick listener retrieves
     * the clicked item and sends a user to the message detail view with the item clicked.
     */
    public void loadInbox(){

        final List<Message> messageList = new ArrayList<>();
        final ListView lv = (ListView) findViewById(R.id.inboxList);

        // Set the empty view
        TextView emptyView = (TextView) findViewById(R.id.inbox_empty_item);
        emptyView.setText(R.string.no_messages);
        lv.setEmptyView(emptyView);

        // Get a reference to the messages.
        DatabaseReference mInboxReference = FirebaseDatabase.getInstance().getReference().child("messages");
        Query queryRef = mInboxReference.orderByChild(user.getUid());
        ValueEventListener refListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Get all the users received messages.
                for (DataSnapshot chatSnapshot: dataSnapshot.getChildren()) {
                    Message message = (Message) chatSnapshot.getValue(Message.class);

                    // Check if this is not the users own sent message and add.
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
                        Intent goToInboxDetail = new Intent(getApplicationContext(), InboxDetailActivity.class);
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