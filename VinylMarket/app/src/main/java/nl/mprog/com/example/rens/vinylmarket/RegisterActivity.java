package nl.mprog.com.example.rens.vinylmarket;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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

import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    // Registeration fields.
    EditText emailED;
    EditText passwordED;
    EditText passwordCompareED;
    EditText usernameED;

    UserRegistrator userRegistrator;

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
        userRegistrator = new UserRegistrator(email, password, passwordComp, username, this);
        userRegistrator.ValidateRegistration();
        forward();

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
