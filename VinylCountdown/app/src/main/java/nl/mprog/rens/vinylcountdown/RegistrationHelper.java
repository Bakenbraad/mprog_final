package nl.mprog.rens.vinylcountdown;

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

/**
 * Created by Rens on 16/01/2017.
 */

public class RegistrationHelper {
    final String email;
    final String password;
    final String passwordComp;
    final String username;
    final Context c;
    final Activity activity;
    final String location;

    // The authenticator for firebase.
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;

    // Reference for writing and reading purposes.
    private DatabaseReference mUsersReference;

    public RegistrationHelper(String email, String password, String passwordComp, String username, Context c, Activity activity) {
        this.email = email;
        this.password = password;
        this.passwordComp = passwordComp;
        this.username = username;
        this.c = c;
        this.activity = activity;
        location = "default";
    }


    // Check if email is valid, from: http://stackoverflow.com/questions/1819142/how-should-i-validate-an-e-mail-address
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    // Check if the entered info is correct according to the standards and proceed to register.
    public void initRegistration(){

        // Checks email format, password equality, if it contains numbers (a requirement) and the length of each.
        if (isValidEmail(email)){
            if (passwordComp.equals(password)){
                if (password.length() > 4){
                    if ((password.matches(".*\\d+.*"))){
                        if (username.length() > 4){

                            // This part checks for username availability. The reference exists means that username is taken.
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");
                            ValueEventListener usernameListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (!dataSnapshot.hasChild(username)){

                                        // Username available, get on with registering
                                        register(email, password);
                                    }
                                    else {
                                        Toast.makeText(c, R.string.inv_usertaken, Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(c, R.string.inv_con, Toast.LENGTH_SHORT).show();
                                }

                            };
                            userRef.addListenerForSingleValueEvent(usernameListener);

                        } else {
                            Toast.makeText(c, R.string.inv_username, Toast.LENGTH_SHORT).show();
                            EditText edittext = (EditText) activity.findViewById(R.id.regUsername);
                            edittext.setError("Invalid Username");
                            edittext.setText("");
                        }
                    } else {
                        Toast.makeText(c, R.string.inv_pass_num, Toast.LENGTH_SHORT).show();
                        EditText edittext = (EditText) activity.findViewById(R.id.regPass);
                        edittext.setError("Password must contain at least one number and be 5 characters");
                        edittext.setText("");
                    }
                } else {
                    Toast.makeText(c, R.string.inv_pass_len, Toast.LENGTH_SHORT).show();
                    EditText edittext = (EditText) activity.findViewById(R.id.regPass);
                    edittext.setError("Password must contain at least one number and be 4 characters");
                    edittext.setText("");
                }
            } else {
                Toast.makeText(c, R.string.inv_pass_comp, Toast.LENGTH_SHORT).show();
                EditText edittext = (EditText) activity.findViewById(R.id.regPassComp);
                edittext.setError("Passwords must match");
                edittext.setText("");
            }
        } else {
            Toast.makeText(c, R.string.inv_email, Toast.LENGTH_SHORT).show();
            EditText edittext = (EditText) activity.findViewById(R.id.regEmail);
            edittext.setError("E-mail addresses must be in the form of vinyl@countdown.com");
            edittext.setText("");
        }
    }


    private void register(final String email, final String password){

        FirebaseAuth mAuthRegister = FirebaseAuth.getInstance();
        mAuthRegister.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(c, R.string.inv_emailtaken, Toast.LENGTH_SHORT).show();
                        }
                        else{
                            // Final step of registeration, this adds the current user with name to the database
                            // and sends the validation email.
                            registerUsername();
                        }
                    }
                });

    }


    // Registers the user after the userdata is validated.
    private void registerUsername(){

        mAuth.signInWithEmailAndPassword(email,password);
        final FirebaseUser user = mAuth.getCurrentUser();

        // Set the users display/user name and update the profile.
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            // If profile is updated add user to users database in a username : id structure.
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
