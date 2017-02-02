package nl.mprog.rens.vinylcountdown.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import nl.mprog.rens.vinylcountdown.R;

/**
 * Rens van der Veldt - 10766162
 * Minor Programmeren
 *
 * LoginActivity.class
 *
 * The login activity is very self explanatory, it logs the user into firebase and sends them through to the
 * main activity. When a user is not registered they can navigate to the register activity. When a user
 * has already logged in and the session is not interrupted yet they are redirected to the main activity
 * immediately.
 */
public class LoginActivity extends AppCompatActivity {

    // Authentication service.
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // Declare Edittexts for authentication data.
    private EditText emailED;
    private EditText passED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get the instance of the firebase authenticator.
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // Check if the user has verified their email address.
                    if (user.isEmailVerified()){

                        // User is signed in and verified continue to main menu.
                        goToMain();
                    } else {

                        // Remind the user to verify their email address.
                        Toast.makeText(LoginActivity.this, "Please verify your email", Toast.LENGTH_LONG).show();
                    }

                }
            }
        };
    }

    /**
     * This function attempts to log the user in. The data the user entered (their email/password)
     * are checked for validity (not 0 length), and an attempt to authenticate is made to firebase.
     * If this is successful the authlistener will redirect the user. If not the user is made
     * aware through a toast.
     * @param view
     */
    public void login(View view) {

        // Get the email and password from the activity when the login button is pressed.
        emailED = (EditText) findViewById(R.id.login_email_ed);
        passED = (EditText) findViewById(R.id.login_pass_ed);

        String email = emailED.getText().toString();
        String password = passED.getText().toString();

        // Check if a valid email and password are valid and try to sign the user in.
        // If this is succesfull the state of the authentication listener changes and
        // the user will be automatically forwarded to the main activity.
        if (email.length() != 0){
            if (password.length() > 5){

                // Try to sign the user in.
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                // If the task fails the user doesn't have the right credentials or no
                                // internet connection.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Login not succesfull",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
            else{
                // Lets the user know Their password is invalid.
                Toast.makeText(LoginActivity.this, R.string.invalid_login_pas,
                        Toast.LENGTH_SHORT).show();
            }
        }
        else{
            // Let the user know their email address must be filled in.
            Toast.makeText(LoginActivity.this, R.string.invalid_login_mail,
                    Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * This function redirects the user to the registration activity.
     * @param view
     */
    public void goToRegisterActivity(View view) {
        Intent goToRegisterActivity = new Intent(this, RegisterActivity.class);
        startActivity(goToRegisterActivity);
        finish();
    }

    /**
     * This function makes the user continue to the main activity.
     */
    private void goToMain(){
        Intent goToMain = new Intent(this, MainActivity.class);
        startActivity(goToMain);
        finish();
    }

    /**
     * Adds the authentication listener on start of the activity.
     */
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * Removes the listener on a stop.
     */
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
