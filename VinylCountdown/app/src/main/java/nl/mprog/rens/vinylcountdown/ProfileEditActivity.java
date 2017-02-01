package nl.mprog.rens.vinylcountdown;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

                                    // First check if the username is available.
                                    // This part checks for username availability. The reference exists means that username is taken.
                                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");
                                    ValueEventListener usernameListener = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if (!dataSnapshot.hasChild(usernameED.getText().toString())){

                                                // Username available, get on with namechange

                                                // Get user id and update the profile
                                                String userID = dataSnapshot.child(userProfile.getUsername()).getValue(String.class);
                                                String originalUsername = userProfile.getUsername();

                                                userProfile.setUsername(usernameED.getText().toString());

                                                // The userID can be used to update the current users' userprofile object
                                                DatabaseReference mProfileReference = FirebaseDatabase.getInstance().getReference().child("users");
                                                mProfileReference.child(userID).setValue(userProfile);

                                                // Remove the old one and replace it with the new user.
                                                mProfileReference.child(originalUsername).removeValue();
                                                mProfileReference.child(usernameED.getText().toString()).setValue(userID);
                                                finish();

                                            }
                                            else {
                                                Toast.makeText(ProfileEditActivity.this, R.string.inv_usertaken, Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Toast.makeText(ProfileEditActivity.this, R.string.inv_con, Toast.LENGTH_SHORT).show();
                                        }

                                    };
                                    userRef.addListenerForSingleValueEvent(usernameListener);
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
