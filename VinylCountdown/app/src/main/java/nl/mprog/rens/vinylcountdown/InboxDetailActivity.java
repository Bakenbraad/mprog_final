package nl.mprog.rens.vinylcountdown;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import nl.mprog.rens.vinylcountdown.ObjectClasses.Message;
import nl.mprog.rens.vinylcountdown.ObjectClasses.UserProfile;

/**
 * Rens van der Veldt - 10766162
 * Minor Programmeren
 *
 * InboxDetailActivity.class
 *
 * This activity displays a message objects' data. Buttons for replies are set to visible/invisible
 * in accordance to whether the message is answerable. If not, a text explains this. When a reply
 * is clicked this activity also formulates what the reply should be and sends it to the other user.
 */

public class InboxDetailActivity extends AppCompatActivity {

    // Declare the message.
    Message message;

    // Authenticator:
    // The authenticator for firebase.
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    FirebaseUser user;

    // Declare user profiles for sending reply messages:
    UserProfile senderProfile;
    UserProfile receiverProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_detail);

        // Get the message that was clicked from the intent.
        Intent intent = getIntent();
        Bundle messageBundle = intent.getExtras();
        message = (Message) messageBundle.getSerializable("message");

        // Check if this message is from the market or a reply a reply must have hidden buttons:
        if (message.getMessageType().equals("reject") || message.getMessageType().equals("accept")){

            // Remove the buttons from view and don't show a text because this is a reply.
            findViewById(R.id.accept_button).setVisibility(View.INVISIBLE);
            findViewById(R.id.reject_button).setVisibility(View.INVISIBLE);

        } else if(message.isRead()){

            // Remove the buttons from view and show a text saying that this message is replied to.
            findViewById(R.id.accept_button).setVisibility(View.INVISIBLE);
            findViewById(R.id.reject_button).setVisibility(View.INVISIBLE);
            findViewById(R.id.already_replied_text).setVisibility(View.VISIBLE);
        }

        // Find the views that need to be set.
        TextView offerTV = (TextView) findViewById(R.id.message_offer);
        TextView senderTV = (TextView) findViewById(R.id.message_sender);
        TextView contentTV = (TextView) findViewById(R.id.message_content);
        TextView timeTV = (TextView) findViewById(R.id.message_time);

        // Set the views texts.
        offerTV.setText(message.getBuyOffer());
        senderTV.setText(message.getSender().getUsername());
        timeTV.setText(message.getTime());

        // Only show the others contact info if they have accepted!
        if (message.getMessageType().equals("accept")){
            contentTV.setText(message.getMessageContent() + " " + message.getSender().getEmail());
        } else{
            contentTV.setText(message.getMessageContent());
        }

        // Get the user for message sending:
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Get the users profiles:
        // Get both the selling and buying user profiles (this must be redone because profiles can change)

        // Sellers profile
        DatabaseReference mSellersReference = FirebaseDatabase.getInstance().getReference().child("users").child(message.getReceiverID());
        ValueEventListener senderListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get UserSettings object and use the values to update the UI
                senderProfile = dataSnapshot.getValue(UserProfile.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mSellersReference.addListenerForSingleValueEvent(senderListener);

        // Buyers profile
        DatabaseReference mBuyersReference = FirebaseDatabase.getInstance().getReference().child("users").child(message.getSenderID());
        ValueEventListener receiversListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get UserSettings object and use the values to update the UI
                receiverProfile = dataSnapshot.getValue(UserProfile.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mBuyersReference.addListenerForSingleValueEvent(receiversListener);
    }

    /**
     * This finishes the activity.
     * @param view
     */
    public void cancelMessage(View view) {
        finish();
    }

    /**
     * If the user clicks the accept button a dialog is thrown to confirm what the user wants and
     * let them remove their offer from the marketplace. This function, on confirmation sends
     * a message to the other user and lets them know you accepted, sets this message as read and finally
     * removes the offer it concerns from the marketplace.
     * @param view
     */
    public void confirmMessage(View view) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.confirm_message)
                .setMessage(R.string.accept_text)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Remove the offer from the marketplace.
                        DatabaseReference mMarketReference = FirebaseDatabase.getInstance().getReference().child("marketplace").child("offers").child(message.getOfferID());
                        mMarketReference.removeValue();

                        // Set this message as read/replied:
                        DatabaseReference mMessageReadReference = FirebaseDatabase.getInstance().getReference().child("messages").child(message.getMessageID());
                        message.setRead(true);
                        mMessageReadReference.setValue(message);

                        // Get a database and key to put reply message at that key
                        DatabaseReference mMessageReference = FirebaseDatabase.getInstance().getReference().child("messages");
                        Message reply = new Message();
                        String replyMessageID = mMessageReference.push().getKey();

                        // Generate reply
                        reply.messageReply("accept", message, senderProfile, receiverProfile, replyMessageID);

                        // Put reply in firebase
                        mMessageReference.child(replyMessageID).setValue(reply);
                        finish();
                    }

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * This repeats the same process for the rejection message. But without removing the record from
     * the marketplace.
     * @param view
     */
    public void rejectMessage(View view) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.confirm_message)
                .setMessage(R.string.reject_text)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Set this message as read/replied:
                        DatabaseReference mMessageReadReference = FirebaseDatabase.getInstance().getReference().child("messages").child(message.getMessageID());
                        message.setRead(true);
                        mMessageReadReference.setValue(message);

                        // Generate the message and put it in the firebase
                        DatabaseReference mMessageReference = FirebaseDatabase.getInstance().getReference().child("messages");
                        String replyMessageID = mMessageReference.push().getKey();

                        // Generate and send the rejection message
                        Message reply = new Message();
                        reply.messageReply("reject", message, senderProfile, receiverProfile, replyMessageID);
                        mMessageReference.child(replyMessageID).setValue(reply);
                        finish();
                    }

                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
