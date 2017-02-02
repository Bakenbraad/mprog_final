package nl.mprog.rens.vinylcountdown.ObjectClasses;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 * Rens van der Veldt - 10766162
 * Minor Programmeren
 *
 * RecordSaleInfo.class
 *
 * The recordSaleInfo contains some basic info about a record and the data that was added by a user
 * when creating this object. The creation of this object happens in the sale activity when a user
 * decides to sell a record. They specify a pricetype, price (not when trade is selected), write
 * a short story about their record and finally input a condition for their record by means of ratingbar.
 * Upon creation the current time is stored as well. When an offer is updated by means of reaction on
 * the marketplace the last user to react is stored in order to prevent spamming. Currentbid stores
 * the highest current bid and in the case that pricetype is bidding cannot be overwritten by a lower
 * price. The id of the user is stored in order to show which user is selling and to send messages.
 */

public class RecordSaleInfo implements Serializable{

    // Declare properties.
    private String mbid;
    private String description;
    private float price;
    public String priceType;
    private float condition;
    private String userID;
    private String saleUID;
    private String imgLink;
    private String artist;
    private String title;
    private String timeCreated;
    private String currentBidUser;
    private float currentBid;

    // Empty constructor needed for firebase storage.
    public RecordSaleInfo(){}

    /**
     * Constructor that extracts data from recordinfo object and stores sale specific data locally.
     * @param recordInfo: the record being sold.
     * @param description: the users description.
     * @param price: the price ( not used when bidding ).
     * @param priceType: can be bidding, price, trade.
     * @param condition: a float from ratingbar representing a scale 1-5.
     * @param userID: the selling users id.
     */
    @SuppressLint("SimpleDateFormat")
    public RecordSaleInfo(RecordInfo recordInfo, String description, float price, String priceType, float condition, String userID){

        // Localize all info needed for the recordsaleinfo object.
        this.mbid = recordInfo.getMbid();
        this.description = description;
        this.price = price;
        this.priceType = priceType;
        this.condition = condition;
        this.userID = userID;
        this.imgLink = recordInfo.getImgLinklarge();
        this.artist = recordInfo.getArtist();
        this.title = recordInfo.getTitle();

        // Create a unique id for the record.
        saleUID = userID + mbid + String.valueOf(System.currentTimeMillis());

        // These should be initialized to default values.
        this.currentBid = 0;
        this.currentBidUser = "None";

        // Set the time created.
        this.timeCreated = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new java.util.Date());
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getImgLink() {
        return imgLink;
    }

    public String getMbid() {
        return mbid;
    }

    public String getDescription() {
        return description;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getPriceType() {
        return priceType;
    }

    public float getCondition() {
        return condition;
    }

    public String getUserID() {
        return userID;
    }

    public String getSaleUID() {
        return saleUID;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCurrentBidUser() {
        return currentBidUser;
    }

    public void setCurrentBidUser(String currentBidUser) { this.currentBidUser = currentBidUser; }

    public float getCurrentBid() {
        return currentBid;
    }

    public void setCurrentBid(float currentBid) {
        this.currentBid = currentBid;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

}

