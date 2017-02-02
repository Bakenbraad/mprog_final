package nl.mprog.rens.vinylcountdown.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import nl.mprog.rens.vinylcountdown.HelperClasses.RegistrationHelper;
import nl.mprog.rens.vinylcountdown.R;

/**
 * Rens van der Veldt - 10766162
 * Minor Programmeren
 *
 * RegisterActivity.class
 *
 * The RegisterActivity allows the user to input account details and initiates the registrationHelper
 * class to register the user using their info.
 */
public class RegisterActivity extends AppCompatActivity {

    // Registeration fields.
    EditText emailED;
    EditText passwordED;
    EditText passwordCompareED;
    EditText usernameED;

    // Declare the helper used for registration.
    RegistrationHelper registrationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    /**
     * This is the function that retrieves the user info from the editTexts. These are then
     * put into the registrationHelper in order to register the user.
     * @param view: passed from button.
     */
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

        // Register the user using the user RegistrationHelper class.
        registrationHelper = new RegistrationHelper(email, password, passwordComp, username, this, this);
        registrationHelper.checkedRegistration();

    }


    /**
     * This is used to return to the login activity.
     * @param view: passed from button.
     */
    public void goToLogin(View view) {
        Intent goToLogin = new Intent(this, LoginActivity.class);
        startActivity(goToLogin);
        finish();
    }
}
