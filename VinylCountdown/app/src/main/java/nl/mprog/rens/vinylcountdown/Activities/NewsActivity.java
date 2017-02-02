package nl.mprog.rens.vinylcountdown.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import nl.mprog.rens.vinylcountdown.R;

/**
 * Rens van der Veldt - 10766162
 * Minor Programmeren
 *
 * NewsActivity.class
 *
 * This activity displays a newsitem object passed by intent.
 */
public class NewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_news);

        // Get the content from the last activity.
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String contentText = bundle.getString("content");

        // Set the full text
        TextView content = (TextView) findViewById(R.id.full_content);
        content.setText(contentText);

    }

    /**
     * Closes the activity.
     * @param view: passed fro the close button.
     */
    public void closeNews(View view) {
        finish();
    }
}
