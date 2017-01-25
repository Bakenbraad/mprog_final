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
        }
        // Display the message appropriately.
        TextView offerTV = (TextView) findViewById(R.id.message_offer);
        TextView senderTV = (TextView) findViewById(R.id.message_sender);
        TextView contentTV = (TextView) findViewById(R.id.message_content);
        TextView timeTV = (TextView) findViewById(R.id.message_time);

        offerTV.setText(message.getBuyOffer());
        senderTV.setText(message.getSender().getUsername());
        contentTV.setText(message.getMessageContent() + "\nPlease accept my offer or contact me at: " + message.getSender().getEmail());
        timeTV.setText(message.getTime());

        // Get the user for message sending:
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Get both the selling and buying user profiles.

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

                        // Generate and send the message
                        Message reply = new Message();
                        reply.messageReply("accept", message.getBuyOffer(), message.getSellOffer(), senderProfile, message.getReceiverID(), receiverProfile, message.getSenderID());
                        DatabaseReference mMessageReference = FirebaseDatabase.getInstance().getReference().child("messages");
                        mMessageReference.push().setValue(reply);
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

                        // Make sure the rejected user can resubmit an offer from the marketplace by updating the current state of the offer.
                        final DatabaseReference mMarketReference = FirebaseDatabase.getInstance().getReference().child("marketplace").child("offers").child(message.getSenderID());
                        ValueEventListener marketListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                // Get the recordsaleinfo in its current state from the market.
                                RecordSaleInfo currentRecordSaleInfo = dataSnapshot.getValue(RecordSaleInfo.class);

                                // Compare the latest bidding user and compare to the sender of this message.
                                if (currentRecordSaleInfo.getCurrentBidUser().equals(message.getSenderID())){
                                    currentRecordSaleInfo.setCurrentBidUser("none");
                                    mMarketReference.setValue(currentRecordSaleInfo);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        };
                        mMarketReference.addListenerForSingleValueEvent(marketListener);

                        // Generate and send the rejection message
                        Message reply = new Message();
                        reply.messageReply("reject", message.getBuyOffer(), message.getSellOffer(), senderProfile, message.getReceiverID(), receiverProfile, message.getReceiverID());
                        DatabaseReference mMessageReference = FirebaseDatabase.getInstance().getReference().child("messages");
                        mMessageReference.push().setValue(reply);
                        finish();
                    }

                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
