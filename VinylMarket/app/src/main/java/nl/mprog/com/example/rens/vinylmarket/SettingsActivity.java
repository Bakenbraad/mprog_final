package nl.mprog.com.example.rens.vinylmarket;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class SettingsActivity extends AppCompatActivity {

    TextView welcomeTextTV;
    TextView usernameTextTV;
    TextView emailTextTV;
    TextView collectionTextTV;

    // Authentication service.
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        welcomeTextTV = (TextView) findViewById(R.id.welcomeTV);
        usernameTextTV = (TextView) findViewById(R.id.usernameTV);
        emailTextTV = (TextView) findViewById(R.id.emailTV);
        collectionTextTV = (TextView) findViewById(R.id.collectionTV);

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null){
            // Get the collection count
            // Placeholder strings
            String welcomeText = "Welcome, " + user.getDisplayName();
            String usernameText = "Username " + user.getDisplayName();
            String emailText = "Email " + user.getEmail();
            String collectionText = "You have " + " records";

            welcomeTextTV.setText(welcomeText);
        }else{
            goToLogin();
        }



    }

    public void goToLogin(){
        Intent goToLogin = new Intent(this, LoginActivity.class);
        startActivity(goToLogin);
    }

}
