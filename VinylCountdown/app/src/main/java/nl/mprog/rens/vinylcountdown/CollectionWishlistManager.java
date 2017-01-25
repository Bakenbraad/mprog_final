package nl.mprog.rens.vinylcountdown;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Rens on 24/01/2017.
 */

public class CollectionWishlistManager {
    // Authentication service.
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mCollectionReference;

    public void addToCollection(String mbid){

        // Get current user and add mbid to their collection.
        FirebaseUser user = mAuth.getCurrentUser();
        mCollectionReference = FirebaseDatabase.getInstance().getReference();
        mCollectionReference.child("collections").child(user.getUid()).setValue(mbid);
    }
}
