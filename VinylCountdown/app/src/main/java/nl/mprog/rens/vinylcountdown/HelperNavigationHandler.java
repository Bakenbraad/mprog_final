package nl.mprog.rens.vinylcountdown;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Rens on 26/01/2017.
 */

public class HelperNavigationHandler {

    Activity activity;
    FirebaseAuth mAuth;

    // Declare drawer modules:
    DrawerLayout drawerLayout;
    ListView drawers;
    String[] navigations;
    Button menuButton;

    public HelperNavigationHandler(Activity activity){

        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();

        // Navigation drawer from: https://developer.android.com/training/implementing-navigation/nav-drawer.html#Init
        navigations = activity.getResources().getStringArray(R.array.menuOptions);
        drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        drawers = (ListView) activity.findViewById(R.id.main_drawer);
        menuButton = (Button) activity.findViewById(R.id.menubutton);

        // Set the adapter for the list view
        drawers.setAdapter(new ArrayAdapter<String>(activity,
                R.layout.drawer_item, navigations));

        // Set the list's click listener
        drawers.setOnItemClickListener(new DrawerItemClickListener());
    }

    public void redirect(int direction){
        // Get all options that are in the menu.
        String[] menuOptions = activity.getResources().getStringArray(R.array.menuOptions);
        switch (menuOptions[direction]){
            case ("Menu"):
                goToMenu();
                break;
            case ("Sell"):
                goToSaleSearch();
                break;
            case ("Buy"):
                goToMarketSearch();
                break;
            case ("Profile"):
                goToProfile();
                break;
            case ("Logout"):
                goToLogin();
                break;
            case ("Inbox"):
                goToInbox();
                break;
            case ("Wishlist"):
                goToWishlist();
                break;
            case ("Collection"):
                goToCollection();
                break;
        }
    }

    // Redirects the user to their profile.
    public void goToProfile() {
        Intent goToProfile = new Intent(activity, ProfileActivity.class);
        activity.startActivity(goToProfile);
        if (!(activity instanceof MainActivity)){
            activity.finish();
        }
    }

    // Redirects the user to where they can search the marketplace.
    public void goToMarketSearch() {
        Intent goToMarketSearch = new Intent(activity, BuySearchActivity.class);
        activity.startActivity(goToMarketSearch);
        if (!(activity instanceof MainActivity)){
            activity.finish();
        }
    }

    // Redirects the user to where they can search possible records they can sell.
    public void goToSaleSearch() {
        Intent goToSaleSearch = new Intent(activity, SaleSearchActivity.class);
        goToSaleSearch.putExtra("method", "saleSearch");
        activity.startActivity(goToSaleSearch);
        if (!(activity instanceof MainActivity)){
            activity.finish();
        }
    }

    // Redirects the user to where they can view their collection.
    public void goToCollection() {
        Intent goToCollection = new Intent(activity, ColWishActivity.class);
        goToCollection.putExtra("method", "collection");
        activity.startActivity(goToCollection);
        if (!(activity instanceof MainActivity)){
            activity.finish();
        }
    }

    // Redirects the user to where they can view their wishlist.
    public void goToWishlist() {
        Intent goToWishlist = new Intent(activity, ColWishActivity.class);
        goToWishlist.putExtra("method", "wishlist");
        activity.startActivity(goToWishlist);
        if (!(activity instanceof MainActivity)){
            activity.finish();
        }
    }

    // Redirects the user to the login screen and logs them out.
    public void goToLogin() {
        Intent goToLogin = new Intent(activity, LoginActivity.class);
        mAuth.getCurrentUser();
        mAuth.signOut();
        activity.startActivity(goToLogin);
        activity.finish();
    }

    // Redirects to the inbox:
    public void goToInbox() {
        Intent goToInbox = new Intent(activity, InboxActivity.class);
        activity.startActivity(goToInbox);
        if (!(activity instanceof MainActivity)){
            activity.finish();
        }
    }

    // Redirects the user to where they can search possible records they can sell.
    public void goToMenu() {
        Intent goToMenu = new Intent(activity, MainActivity.class);
        activity.startActivity(goToMenu);
        activity.finish();
    }

    // Change the button appearance when it is clicked.
    public void openDrawer() {

        if(drawerLayout.isDrawerOpen(drawers)){
            drawerLayout.closeDrawer(drawers);
            menuButton.setText("+");
        }
        else {
            drawerLayout.openDrawer(drawers);
            menuButton.setText("-");
        }
    }

    // This is the onclicklistener for the drawer, it sends data to navigation.
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            redirect(position);
            drawerLayout.closeDrawer(drawers);
            menuButton.setText("+");
        }
    }
}
