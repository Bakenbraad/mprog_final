package nl.mprog.rens.vinylcountdown.HelperClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import nl.mprog.rens.vinylcountdown.Activities.LoginActivity;
import nl.mprog.rens.vinylcountdown.R;
import nl.mprog.rens.vinylcountdown.ObjectClasses.UserProfile;

/**
 * Rens van der Veldt - 10766162
 * Minor Programmeren
 *
 * RegistrationHelper.class
 *
 * This helper class does the main registration for users. After data is entered into the edittexts
 * in the registerFirebase activity and the registerFirebase button is pressed, this class takes over. The entered
 * data is checked and compared for validity. Finally the user is registered and sent a verification
 * email.
 */

public class RegistrationHelper {

    // Declare all values.
    private final String email;
    private final String password;
    private final String passwordComp;
    private final String username;
    private final Context c;
    private final Activity activity;
    private final String location;

    // The authenticator for firebase.
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // Reference for writing and reading purposes.
    private DatabaseReference mUsersReference;

    /**
     * Constructor makes all variables local.
     */
    public RegistrationHelper(String email, String password, String passwordComp, String username, Context c, Activity activity) {

        // Make all parameters local.
        this.email = email;
        this.password = password;
        this.passwordComp = passwordComp;
        this.username = username;
        this.c = c;
        this.activity = activity;
        location = "default";
    }


    /**
     * Checks if the email has a valid format.
     * From: http://stackoverflow.com/questions/1819142/how-should-i-validate-an-e-mail-address
     */

    private static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    /**
     * This function checks if the entered data is correct and starts the registration when it is.
     * When it isn't this function throws errors and toasts to let the user know what they are doing
     * wrong.
     */
    public void checkedRegistration() {

        // Checks email format, password equality, if it contains numbers (a requirement) and the length of each.
        if (!isValidEmail(email)) {

            Toast.makeText(c, R.string.inv_email, Toast.LENGTH_SHORT).show();
            EditText edittext = (EditText) activity.findViewById(R.id.regEmail);
            edittext.setError("E-mail addresses must be in the form of vinyl@countdown.com");
            edittext.setText("");

        } else if (!passwordComp.equals(password)) {

            Toast.makeText(c, R.string.inv_pass_comp, Toast.LENGTH_SHORT).show();
            EditText edittext = (EditText) activity.findViewById(R.id.regPassComp);
            edittext.setError("Passwords must match");
            edittext.setText("");

        } else if (password.length() <= 4 || !(password.matches(".*\\d+.*"))) {

            Toast.makeText(c, R.string.inv_pass_len, Toast.LENGTH_SHORT).show();
            EditText edittext = (EditText) activity.findViewById(R.id.regPass);
            edittext.setError("Password must contain at least one number and be 4 characters");
            edittext.setText("");

        } else if (username.length() < 4) {

            Toast.makeText(c, R.string.inv_username, Toast.LENGTH_SHORT).show();
            EditText edittext = (EditText) activity.findViewById(R.id.regUsername);
            edittext.setError("Invalid Username");
            edittext.setText("");

        } else {

            // This part finally checks for username availability. The reference exists means that
            // username is taken and throws a toast.
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");
            ValueEventListener usernameListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild(username)) {

                        // Username available, get on with registering
                        registerFirebase(email, password);
                    } else {
                        Toast.makeText(c, R.string.inv_usertaken, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(c, R.string.inv_con, Toast.LENGTH_SHORT).show();
                }

            };
            userRef.addListenerForSingleValueEvent(usernameListener);
        }

    }

    /**
     * Actual registration happens here using email and password to create a new firebase user.
     * on success the user is registered to their account
     * in the form of a profile by the final function, registerUserprofile.
     * @param email: the users email adress.
     * @param password: the users password.
     */
    private void registerFirebase(final String email, final String password){

        FirebaseAuth mAuthRegister = FirebaseAuth.getInstance();
        mAuthRegister.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If creation is not successful firebase throws an error, the email
                        // address is already taken or no internet connection is available.
                        if (!task.isSuccessful()) {
                            Toast.makeText(c, R.string.inv_emailtaken, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            // Final step of registeration, this adds the current user with name to the database
                            // and sends the validation email.
                            registerUserProfile();
                        }
                    }
                });

    }


    /**
     * This function performs the last part, the userprofile is created and sent to the firebase. Then
     * the account email is sent a validation email address, this is to prevent users signing up with
     * fake addresses. The profile is written with double reference in order to easily check for username
     * availability in the registration of other users. The second reference is used to store the full
     * profile. They are chained by username and can be retrieved by asking the current users uid.
     */
    private void registerUserProfile(){

        // Try signing in (needed for validation email).
        mAuth.signInWithEmailAndPassword(email, password);
        final FirebaseUser user = mAuth.getCurrentUser();

        // Set the users display/user name and update the profile.
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
        assert user != null;
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            // If profile is updated add user to users database in a username : id : profile structure.
                            mUsersReference = FirebaseDatabase.getInstance().getReference();
                            mUsersReference.child("users").child(username).setValue(user.getUid());
                            mUsersReference.child("users").child(user.getUid()).setValue(new UserProfile(username, email,location));

                            // Let the user know that registration was successful and return.
                            Toast.makeText(c, R.string.valid_reg, Toast.LENGTH_SHORT).show();
                            user.sendEmailVerification();
                            Intent goToLogin = new Intent(activity.getApplicationContext(), LoginActivity.class);
                            c.startActivity(goToLogin);

                        } else {
                            // Username registration was not successful.
                            Toast.makeText(c, R.string.invalid_reg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
