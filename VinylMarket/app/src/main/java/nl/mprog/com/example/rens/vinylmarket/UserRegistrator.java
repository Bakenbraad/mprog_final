package nl.mprog.com.example.rens.vinylmarket;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
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

import java.util.concurrent.Executor;

/**
 * Created by Rens on 12/01/2017.
 */

public class UserRegistrator {
    final String email;
    final String password;
    final String passwordComp;
    final String username;
    final Context c;

    // The authenticator for firebase.
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // Reference for writing and reading purposes.
    private DatabaseReference mUsersReference;

    public UserRegistrator(String email, String password, String passwordComp, String username, Context c) {
        this.email = email;
        this.password = password;
        this.passwordComp = passwordComp;
        this.username = username;
        this.c = c;
    }


    // Check if email is valid, from: http://stackoverflow.com/questions/1819142/how-should-i-validate-an-e-mail-address
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    // Check if the entered info is correct according to the standards and proceed to register.
    public void ValidateRegistration(){

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
                                    // Get  object and use the values to update the UI
                                    if (!dataSnapshot.hasChild(username)){
                                        Register();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(c, R.string.inv_con, Toast.LENGTH_SHORT).show();
                                }
                            };
                            userRef.addValueEventListener(usernameListener);

                        } else {
                            Toast.makeText(c, R.string.inv_username, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(c, R.string.inv_pass_num, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(c, R.string.inv_pass_len, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(c, R.string.inv_pass_comp, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(c, R.string.inv_email, Toast.LENGTH_SHORT).show();
        }
    }


    // Registers the user after the userdata is validated.
    public void Register(){

        mAuth = FirebaseAuth.getInstance();
        // Try to create the user.
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {

                            // Registration failed.
                            Toast.makeText(c, R.string.invalid_reg, Toast.LENGTH_SHORT).show();
                        }
                        else{
                            // If user is created register their username.
                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

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

                                                // Let the user know that registration was successful and return.
                                                Toast.makeText(c, R.string.valid_reg, Toast.LENGTH_SHORT).show();
                                                user.sendEmailVerification();
                                            }
                                            else{
                                                // Username registration was not successful.
                                                Toast.makeText(c, R.string.invalid_reg, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}
