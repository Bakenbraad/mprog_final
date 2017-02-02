package nl.mprog.rens.vinylcountdown.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import nl.mprog.rens.vinylcountdown.ObjectClasses.UserProfile;
import nl.mprog.rens.vinylcountdown.R;

/**
 * Rens van der Veldt - 10766162
 * Minor Programmeren
 *
 * ProfileEditActivity.class
 *
 * This activity allows the user to edit their profile. This requires that the username is unique in
 * the database. The name is first set to be editable in an edittext and when a user changes their
 * name and presses save the profile and username (if not taken) is updated in the firebase database.
 */
public class ProfileEditActivity extends AppCompatActivity {

    // Declare variables.
    EditText usernameED;
    UserProfile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        // Get the intent.
        Intent intent = getIntent();
        Bundle bundleProfile = intent.getExtras();

        // Get the profile and set the data:
        userProfile = (UserProfile) bundleProfile.getSerializable("profile");

        // Set the users username as text and hint (if they remove their name they can still review
        // it in the hint.
        usernameED = (EditText) findViewById(R.id.usernameED);
        usernameED.setText(userProfile.getUsername());
        usernameED.setHint(userProfile.getUsername());
    }

    /**
     * This function saves the new profile if changes have been made. When changes have been made
     * a dialog is thrown to confirm the users wish to change their username. Upon confirmation the
     * username is checked for availability by reference to database. If the name is available the
     * userprofile and username are updated in the database. The username is fully intergrated
     * and updates in the marketplace, new messages and welcome texts immediately.
     * @param view: passed from button.
     */
    public void saveProfile(View view) {

        if (!usernameED.getText().toString().equals(userProfile.getUsername())){

            new AlertDialog.Builder(this)
                    .setTitle("Confirm name change")
                    .setMessage("Do you want to change your username from " + userProfile.getUsername() + " to " + usernameED.getText().toString() + "?")
                    .setIcon(R.drawable.logo)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {

                            // Get the current userprofile location in order to edit it.
                            DatabaseReference mProfileReference = FirebaseDatabase.getInstance().getReference().child("users").child(userProfile.getUsername());
                            ValueEventListener settingsListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    // This part checks for username availability. The reference exists means that username is taken.
                                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");
                                    ValueEventListener usernameListener = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if (!dataSnapshot.hasChild(usernameED.getText().toString())){

                                                // Username is available, get on with profile change.

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
