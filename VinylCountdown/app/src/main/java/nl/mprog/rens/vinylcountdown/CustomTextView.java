package nl.mprog.rens.vinylcountdown;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Rens van der Veldt - 10766162
 * Minor Programmeren
 *
 * CustomTextView.class
 *
 * This is an extension of the regular textview and applies a custom font (Montserrat regular).
 * Constructed from: http://stackoverflow.com/questions/3651086/android-using-custom-font
 */

public class CustomTextView extends TextView {

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTextView(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/MontserratAlternates-Regular.ttf");
        setTypeface(tf ,1);

    }
}
