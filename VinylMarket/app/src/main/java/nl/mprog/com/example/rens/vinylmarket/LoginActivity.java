package nl.mprog.com.example.rens.vinylmarket;

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

public class LoginActivity extends AppCompatActivity {

    // Authentication service.
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // Data to authenticate with.
    private EditText emailED;
    private EditText passED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get current user instance.
        mAuth = FirebaseAuth.getInstance();

        // Listens if a user already is logged in on device.
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in so go to menu
                    goToMain();
                }
            }
        };
        goToMain();
    }

    // Logs the user in.
    public void login(View view) {

        // Get the email and password from the activity when the login button is pressed.
        emailED = (EditText) findViewById(R.id.editText2);
        passED = (EditText) findViewById(R.id.editText3);

        String email = emailED.getText().toString();
        String password = passED.getText().toString();

        // Check if a valid email and password are valid and try to sign the user in.
        // If this is succesfull the state of the authentication listener changes and
        // the user will be automatically forwarded to the main activity.
        if (email.length() != 0){
            if (password.length() > 5){
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {

                                    Toast.makeText(LoginActivity.this, R.string.invalid_login,
                                            Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    // Check if the user has verfied their email address.
                                    assert user != null;
                                    if (user.isEmailVerified()){
                                        goToMain();
                                    } else{
                                        // If the user is not yet verified, remind them.
                                        Toast.makeText(LoginActivity.this, R.string.verify_mail ,Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
            else{
                Toast.makeText(LoginActivity.this, R.string.invalid_login_pas,
                        Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(LoginActivity.this, R.string.invalid_login_mail,
                    Toast.LENGTH_SHORT).show();
        }

    }

    // Send the user to the registration activity.
    public void goToRegisterActivity(View view) {
        Intent goToRegisterActivity = new Intent(this, RegisterActivity.class);
        startActivity(goToRegisterActivity);
        finish();
    }

    // Continue to the main activity.
    private void goToMain(){
        Intent goToMain = new Intent(this, MainActivity.class);
        startActivity(goToMain);
        finish();
    }

    // Adds the listener for users when the activity starts.
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    // Removes the listener when it is stopped.
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
