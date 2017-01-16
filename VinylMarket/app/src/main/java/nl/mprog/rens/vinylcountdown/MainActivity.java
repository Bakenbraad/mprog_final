package nl.mprog.rens.vinylcountdown;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;import nl.mprog.com.example.rens.vinylmarket.R;

public class MainActivity extends AppCompatActivity {

    // Authenticator:
    // The authenticator for firebase.
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check the login state.
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in

                } else {
                    // User is signed out, they don't belong here so send them back!
                    goToLogin();
                }
            }
        };
    }

    // Redirects the user to their profile.
    public void goToProfile(View view) {
        Intent goToProfile = new Intent(this, ProfileActivity.class);
        startActivity(goToProfile);
    }

    // Redirects the user to where they can search the marketplace.
    public void goToMarketSearch(View view) {
        Intent goToProfile = new Intent(this, MarketSearchActivity.class);
        startActivity(goToProfile);
    }

    // Redirects the user to where they can search possible records they can sell.
    public void goToSaleSearch(View view) {
        Intent goToProfile = new Intent(this, SaleSearchActivity.class);
        startActivity(goToProfile);
    }

    // Logs the user out and redirects them back to the login.
    public void logUserOut(View view) {
        mAuth.getCurrentUser();
        mAuth.signOut();
        goToLogin();
    }

    // Redirects the user to the login screen.
    public void goToLogin() {
        Intent goToLogin = new Intent(this, LoginActivity.class);
        startActivity(goToLogin);
        finish();
    }
}
