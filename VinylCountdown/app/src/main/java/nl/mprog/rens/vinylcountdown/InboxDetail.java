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

public class InboxDetail extends AppCompatActivity {

    Message message;

    // Authenticator:
    // The authenticator for firebase.
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    FirebaseUser user;

    // Userprofiles for sending messages:
    UserProfile senderProfile;
    UserProfile receiverProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_detail);

        // Get the message that was clicked.
        Intent intent = getIntent();
        Bundle messageBundle = intent.getExtras();
        message = (Message) messageBundle.getSerializable("message");

        // Check if this message is from the market or a reply a reply must have hidden buttons:
        if (message.getMessageType().equals("reject") || message.getMessageType().equals("accept")){
            findViewById(R.id.accept_button).setVisibility(View.INVISIBLE);
            findViewById(R.id.reject_button).setVisibility(View.INVISIBLE);
        } else if(message.isRead()){
            findViewById(R.id.accept_button).setVisibility(View.INVISIBLE);
            findViewById(R.id.reject_button).setVisibility(View.INVISIBLE);
            findViewById(R.id.already_replied_text).setVisibility(View.VISIBLE);
        }
        // Display the message appropriately.
        TextView offerTV = (TextView) findViewById(R.id.message_offer);
        TextView senderTV = (TextView) findViewById(R.id.message_sender);
        TextView contentTV = (TextView) findViewById(R.id.message_content);
        TextView timeTV = (TextView) findViewById(R.id.message_time);

        offerTV.setText(message.getBuyOffer());
        senderTV.setText(message.getSender().getUsername());
        if (message.getMessageType().equals("accept")){
            contentTV.setText(message.getMessageContent() + " " +message.getSender().getEmail());
        } else{
            contentTV.setText(message.getMessageContent());
        }

        timeTV.setText(message.getTime());

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

    public void cancelMessage(View view) {
        finish();
    }

    public void confirmMessage(View view) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirming message")
                .setMessage("If you accept the offer, your record will be removed from the marketplace and the receiver will be made aware!")
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
                        reply.messageReply("accept", message.getBuyOffer(), message.getSellOffer(), senderProfile, message.getReceiverID(), receiverProfile, message.getSenderID(), replyMessageID);
                        // Put reply in firebase
                        mMessageReference.child(replyMessageID).setValue(reply);
                        finish();
                    }

                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    public void rejectMessage(View view) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Rejecting message")
                .setMessage("If you reject the offer, your record will stay in the marketplace and the receiver will be made aware!")
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
                        reply.messageReply("reject", message.getBuyOffer(), message.getSellOffer(), senderProfile, message.getReceiverID(), receiverProfile, message.getSenderID(), replyMessageID);
                        mMessageReference.child(replyMessageID).setValue(reply);
                        finish();
                    }

                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
