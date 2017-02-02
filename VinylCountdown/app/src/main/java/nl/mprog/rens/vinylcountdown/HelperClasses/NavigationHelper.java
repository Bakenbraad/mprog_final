package nl.mprog.rens.vinylcountdown.HelperClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

import nl.mprog.rens.vinylcountdown.Activities.BuySearchActivity;
import nl.mprog.rens.vinylcountdown.Activities.ColWishActivity;
import nl.mprog.rens.vinylcountdown.Activities.InboxActivity;
import nl.mprog.rens.vinylcountdown.Activities.LoginActivity;
import nl.mprog.rens.vinylcountdown.Activities.MainActivity;
import nl.mprog.rens.vinylcountdown.Activities.ProfileActivity;
import nl.mprog.rens.vinylcountdown.Activities.RecordSearchActivity;
import nl.mprog.rens.vinylcountdown.R;

/**
 * Rens van der Veldt - 10766162
 * Minor Programmeren
 *
 * NavigationHelper.class
 *
 * This class manages all the navigation and is a requirement for all classes with a drawer.
 * The navigationhelper declares and initiates the drawer and all dependant components.
 * When an item in the drawer is clicked this class redirects the user to that page.
 * The class also manages the pressing of the menu button and the appearance change.
 *
 * Partially constructed from: https://developer.android.com/training/implementing-navigation/nav-drawer.html#Init
 */

public class NavigationHelper {

    private Activity activity;
    private FirebaseAuth mAuth;

    // Declare drawer modules:
    private DrawerLayout drawerLayout;
    private ListView drawers;
    private Button menuButton;

    public NavigationHelper(Activity activity){

        // The auth is needed for the navigation to the login activity.
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();

        // Get all resources and views.
        String[] navigations = activity.getResources().getStringArray(R.array.menuOptions);
        drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        drawers = (ListView) activity.findViewById(R.id.main_drawer);
        menuButton = (Button) activity.findViewById(R.id.menubutton);

        // Set the adapter for the list view
        drawers.setAdapter(new ArrayAdapter<>(activity,
                R.layout.drawer_item, navigations));

        // Set the list's click listener
        drawers.setOnItemClickListener(new DrawerItemClickListener());
    }

    private void redirect(int direction){
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
    private void goToProfile() {
        Intent goToProfile = new Intent(activity, ProfileActivity.class);
        activity.startActivity(goToProfile);
        if (!(activity instanceof MainActivity)){
            activity.finish();
        }
    }

    // Redirects the user to where they can search the marketplace.
    private void goToMarketSearch() {
        Intent goToMarketSearch = new Intent(activity, BuySearchActivity.class);
        activity.startActivity(goToMarketSearch);
        if (!(activity instanceof MainActivity)){
            activity.finish();
        }
    }

    // Redirects the user to where they can search possible records they can sell.
    private void goToSaleSearch() {
        Intent goToSaleSearch = new Intent(activity, RecordSearchActivity.class);
        goToSaleSearch.putExtra("method", "saleSearch");
        activity.startActivity(goToSaleSearch);
        if (!(activity instanceof MainActivity)){
            activity.finish();
        }
    }

    // Redirects the user to where they can view their collection.
    private void goToCollection() {
        Intent goToCollection = new Intent(activity, ColWishActivity.class);
        goToCollection.putExtra("method", "collection");
        activity.startActivity(goToCollection);
        if (!(activity instanceof MainActivity)){
            activity.finish();
        }
    }

    // Redirects the user to where they can view their wishlist.
    private void goToWishlist() {
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
    private void goToInbox() {
        Intent goToInbox = new Intent(activity, InboxActivity.class);
        activity.startActivity(goToInbox);
        if (!(activity instanceof MainActivity)){
            activity.finish();
        }
    }

    // Redirects the user to where they can search possible records they can sell.
    private void goToMenu() {
        Intent goToMenu = new Intent(activity, MainActivity.class);
        activity.startActivity(goToMenu);
        activity.finish();
    }

    // Change the button appearance when it is clicked.
    public void openDrawer() {

        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

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
