package nl.mprog.rens.vinylcountdown;

import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 * Created by Rens on 16/01/2017.
 */

public class RecordSaleInfo implements Serializable{

    String mbid;
    String description;
    float price;
    String priceType;
    float condition;
    String userID;
    String saleUID;
    String imgLink;
    String artist;
    String title;

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    String timeCreated;

    public String getCurrentBidUser() {
        return currentBidUser;
    }

    public void setCurrentBidUser(String currentBidUser) {
        this.currentBidUser = currentBidUser;
    }

    String currentBidUser;

    public float getCurrentBid() {
        return currentBid;
    }

    public void setCurrentBid(float currentBid) {
        this.currentBid = currentBid;
    }

    float currentBid;

    public RecordSaleInfo(){}

    public RecordSaleInfo(String mbid, String description, float price, String priceType, float condition, String userID, String imgLink, String artist, String title){

        this.mbid = mbid;
        this.description = description;
        this.price = price;
        this.priceType = priceType;
        this.condition = condition;
        this.userID = userID;
        this.imgLink = imgLink;
        this.artist = artist;
        this.title = title;
        this.currentBid = 0;
        this.currentBidUser = "None";
        saleUID = userID + mbid + String.valueOf(System.currentTimeMillis());
        this.timeCreated = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new java.util.Date());

    }

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

    public void setMbid(String mbid) {
        this.mbid = mbid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public float getCondition() {
        return condition;
    }

    public void setCondition(float condition) {
        this.condition = condition;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getSaleUID() {
        return saleUID;
    }

    public void setSaleUID(String saleUID) {
        this.saleUID = saleUID;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

