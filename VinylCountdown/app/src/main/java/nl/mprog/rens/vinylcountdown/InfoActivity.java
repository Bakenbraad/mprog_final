package nl.mprog.rens.vinylcountdown;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

/**
 * Rens van der Veldt - 10766162
 * Minor Programmeren
 *
 * InfoActivity.class
 *
 * This class simply displays the info about the app accessible from the main menu.
 */

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        setContentView(R.layout.activity_info);
    }

    /**
     * Quits the info activity.
     * @param view
     */
    public void backToMain(View view) {
        finish();
    }
}
