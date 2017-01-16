package nl.mprog.rens.vinylcountdown;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Rens on 16/01/2017.
 * From: http://stackoverflow.com/questions/36472953/how-to-set-custom-font-in-xml-file-instead-of-java-file
 */

public class MyTextView extends TextView {

    public MyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextView(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/MontserratAlternates-Regular.ttf");
        setTypeface(tf ,1);

    }
}
