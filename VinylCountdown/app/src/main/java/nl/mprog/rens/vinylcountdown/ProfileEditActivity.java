package nl.mprog.rens.vinylcountdown;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileEditActivity extends AppCompatActivity {

    EditText usernameED;
    UserProfile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        Intent intent = getIntent();
        Bundle bundleProfile = intent.getExtras();

        // Get the profile and set the data:
        userProfile = (UserProfile) bundleProfile.getSerializable("profile");

        usernameED = (EditText) findViewById(R.id.usernameED);
        usernameED.setText(userProfile.getUsername());
    }

    public void saveProfile(View view) {

        if (!usernameED.getText().toString().equals(userProfile.getUsername())){

            new AlertDialog.Builder(this)
                    .setTitle("Confirm name change")
                    .setMessage("Do you want to change your username from " + userProfile.getUsername() + " to " + usernameED.getText().toString() + "?")
                    .setIcon(R.drawable.logo)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Get the current userprofile location in order to edit it
                            DatabaseReference mProfileReference = FirebaseDatabase.getInstance().getReference().child("users").child(userProfile.getUsername());
                            ValueEventListener settingsListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    // Get user id and update the profile
                                    String userID = dataSnapshot.getValue(String.class);
                                    String originalUsername = userProfile.getUsername();

                                    userProfile.setUsername(usernameED.getText().toString());

                                    // The userID can be used to update the current users'userprofile object
                                    DatabaseReference mProfileReference = FirebaseDatabase.getInstance().getReference().child("users");
                                    mProfileReference.child(userID).setValue(userProfile);
                                    mProfileReference.child(originalUsername).removeValue();
                                    mProfileReference.child(usernameED.getText().toString()).setValue(userID);
                                    finish();

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            };
                            mProfileReference.addListenerForSingleValueEvent(settingsListener);
                        }})
                    .setNegativeButton(android.R.string.no, null).show();


        }
        else {
            finish();
        }
    }
}
