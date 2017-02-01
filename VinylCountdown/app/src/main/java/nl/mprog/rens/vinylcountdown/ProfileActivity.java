package nl.mprog.rens.vinylcountdown;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import nl.mprog.rens.vinylcountdown.AdapterClasses.CustomMarketAdapter;
import nl.mprog.rens.vinylcountdown.HelperClasses.NavigationHelper;
import nl.mprog.rens.vinylcountdown.ObjectClasses.RecordSaleInfo;
import nl.mprog.rens.vinylcountdown.ObjectClasses.UserProfile;

public class ProfileActivity extends AppCompatActivity {

    // Authenticator:
    // The authenticator for firebase.
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    UserProfile userProfile;

    // Initiate the navigation handler:
    NavigationHelper navigationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initiate the navigation handler.
        navigationHelper = new NavigationHelper(this);

        // Initiate the authentication
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // If the user is logged in proceed to get their data.
        if (user != null) {

            // User is logged in proceed to set their username and email.
            final DatabaseReference mUserReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
            final ValueEventListener profileListener = new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    // Get UserSettings object and use the values to update the UI
                    userProfile = dataSnapshot.getValue(UserProfile.class);
                    if (userProfile != null) {

                        // The user is logged in so set their profile info.
                        TextView userNameTV = (TextView) findViewById(R.id.profileUserTV);
                        userNameTV.setText(userProfile.getUsername());

                        TextView emailTV = (TextView) findViewById(R.id.profileEmailTV);
                        emailTV.setText(userProfile.getEmail());

                        // Load the users sales.
                        loadSales(user);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mUserReference.addValueEventListener(profileListener);

        }

        // Check the login state.
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user == null) {

                    // User is signed out, they don't belong here so send them back!
                    new NavigationHelper(getParent()).goToLogin();
                }
            }
        };
    }

    // This part loads the users selling records. the users data is used to retrieve all records
    // in the marketplace matching their uid.
    public void loadSales(final FirebaseUser user){

        // Find the list that is going to show the users sales.
        final ListView lv = (ListView) findViewById(R.id.salesList);

        // Create a database reference to the marketplace.
        DatabaseReference marketplaceRef = FirebaseDatabase.getInstance().getReference().child("marketplace").child("offers");
        final Query queryRef = marketplaceRef.orderByChild(user.getUid());

        // Declare a list of recordsaleinfo objects.
        final List<RecordSaleInfo> recordSaleInfoList = new ArrayList<>();

        // Get the sales from the marketplace.
        ValueEventListener refListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Get the datasnapshot matching the query
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {

                    // Get the object form the marketplace.
                    RecordSaleInfo recordSaleInfo = (RecordSaleInfo) chatSnapshot.getValue(RecordSaleInfo.class);
                    if (recordSaleInfo.getUserID().equals(user.getUid())) {

                        // Add the object to the list.
                        recordSaleInfoList.add(recordSaleInfo);
                    }
                }

                // Set the adapter
                CustomMarketAdapter customMarketAdapter = new CustomMarketAdapter(getApplicationContext(), R.layout.record_market_item, recordSaleInfoList);
                lv.setAdapter(customMarketAdapter);
                customMarketAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        queryRef.addListenerForSingleValueEvent(refListener);
    }


    public void openDrawer(View view) {
        navigationHelper.openDrawer();
    }


    public void editUsername(View view){
        Intent goToProfileEdit = new Intent(this, ProfileEditActivity.class);
        goToProfileEdit.putExtra("profile", userProfile);
        startActivity(goToProfileEdit);
    }
}
