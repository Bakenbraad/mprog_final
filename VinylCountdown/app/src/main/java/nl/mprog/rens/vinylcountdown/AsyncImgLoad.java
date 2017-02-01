package nl.mprog.rens.vinylcountdown;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Rens van der Veldt - 10766162
 * Minor Programmeren
 *
 * AsyncImgLoad.class
 *
 * This asynctask extension downloads an image from a url link and returns an imageview with the
 * downloaded image as bitmap.
 *
 * Constructed from:
 * http://stackoverflow.com/questions/3090650/android-loading-an-image-from-the-web-with-asynctask
 */

// This class downloads and sets the images for each row
class AsyncImgLoad extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    AsyncImgLoad(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {

        // Get the url that should contain the image.
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {

            // Read the url as inputstream and parse it to a bitmap.
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {

        // Set the resulting bitmap in the image view.
        bmImage.setImageBitmap(result);
    }
}
