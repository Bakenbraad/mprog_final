package nl.mprog.rens.vinylcountdown;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Rens van der Veldt - 10766162
 * Minor Programmeren
 *
 * CustomInboxAdapter.class
 *
 * This adapter displays the users in-app inbox in a nice way. A list of messages is used
 * to fill a listview. The sender, time, subject are set and if the message is not yet replied to
 * the subject is made bold. Because all messages in-app are automatically generated, this adapter
 * also constructs the subject from the message properties.
 * Constructed from: https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView
 */

public class CustomInboxAdapter extends ArrayAdapter<Message> {

    // List of items
    private List<Message> objects;

    // Constructor
    public CustomInboxAdapter(Context context, int textViewResourceId, List<Message> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
    }


    public View getView(int position, View convertView, ViewGroup parent){

        // Assign the view we are converting to a local variable
        View v = convertView;

        // First check to see if the view is null. if so, we have to inflate it.
        // To inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.inbox_item, null);
        }

        Message i = objects.get(position);

        if (i != null) {

            // Find the views and set the appropriate values.
            TextView subjectTV = (TextView) v.findViewById(R.id.subjectTV);
            TextView senderTV = (TextView) v.findViewById(R.id.senderTV);
            TextView timeTV = (TextView) v.findViewById(R.id.messageTime);

            // Format the message according to the message type (can be offer, from the marketplace, or reject/accept from inbox)
            String messageType = i.getMessageType();

            if (i.isRead()){
                subjectTV.setTypeface(Typeface.DEFAULT);
            }

            if (messageType.equals("offer")){
                String priceType = i.getType();

                // Generate a subject from the type and specifications of the message.
                switch (priceType) {
                    case "Trade":
                        subjectTV.setText(i.getSellOffer() + " trading for " + i.getBuyOffer());
                        break;
                    case "Bidding from":
                        subjectTV.setText("A bid of $" + i.getSellOffer() + " for " + i.getBuyOffer());
                        break;
                    case "Price":
                        subjectTV.setText("Buying " + i.getSellOffer() + " for " + i.getBuyOffer());
                        break;
                }

            } else if (i.getMessageType().equals("accept")){

                // Format the acceptation subject.
                subjectTV.setText("Re: " + i.getSender().getUsername() + " accepted your offer of " + i.getBuyOffer());

            } else if (i.getMessageType().equals("reject")){

                // Format the rejection subject.
                subjectTV.setText("Re: " + i.getSender().getUsername() + " rejected your offer of " + i.getBuyOffer());
            }

            // Set the time and sender:
            timeTV.setText(i.getTime());
            senderTV.setText(i.getSender().getUsername());
        }

        // The view must be returned to our activity
        return v;

    }
}
