package nl.mprog.rens.vinylcountdown;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

import nl.mprog.com.example.rens.vinylmarket.R;

public class RegisterActivity extends AppCompatActivity {

    // Registeration fields.
    EditText emailED;
    EditText passwordED;
    EditText passwordCompareED;
    EditText usernameED;

    UserRegistrator userRegistrator;

    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }



    public void signUp(View view){

        // Edittext fields to retrieve data from.
        emailED = (EditText) findViewById(R.id.regEmail);
        passwordED = (EditText) findViewById(R.id.regPass);
        passwordCompareED = (EditText) findViewById(R.id.regPassComp);
        usernameED = (EditText) findViewById(R.id.regUsername);

        // The strings used for registration.
        final String email = emailED.getText().toString();
        final String password = passwordED.getText().toString();
        final String passwordComp = passwordCompareED.getText().toString();
        final String username = usernameED.getText().toString();

        // Register the user using the user registrator class.
        userRegistrator = new UserRegistrator(email, password, passwordComp, username, this, this);
        userRegistrator.initRegistration();
        // If the user data is validated, continue registering them and put their username in the database.

    }



    public void goToLogin(View view) {
        forward();
    }

    public void forward(){
        Intent goToLogin = new Intent(this, LoginActivity.class);
        startActivity(goToLogin);
        finish();
    }
}
