package nl.mprog.rens.vinylcountdown;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

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

    public void closeNews(View view) {
        finish();
    }
}
