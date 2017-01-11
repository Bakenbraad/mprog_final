package nl.mprog.com.example.rens.vinylmarket;

/**
 * Created by Rens on 11/01/2017.
 */

public class RecordSaleInfo {

    // Basic record info.
    String title;
    String artist;
    String published;
    String imgLink;
    String summary;
    String mbID;

    // The idea of the user that sells the record
    String sellingID;

    // Saletype can be swap, price or bid. Price should correspond to a set price, an updated price (bid)
    // and when swap might display a link to the sellers wishlist (optional idea).
    String saleType;
    String price;

    // A description set upon creation that describes the specific record being sold (pressing condition etc.).
    String description;

    // Getters for all fields
    public String getMbid() {
        return mbID;
    }

    public String getArtist() {
        return artist;
    }

    public String getImgLink() {
        return imgLink;
    }

    public String getPublished() {
        return published;
    }

    public String getSummary() {
        return summary;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getMbID() {
        return mbID;
    }

    public String getPrice() {
        return price;
    }

    public String getSaleType() {
        return saleType;
    }

    public String getSellingID() {
        return sellingID;
    }

    // Setters for all fields.
    public void setMbid(String mbID) {
        this.mbID = mbID;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMbID(String mbID) {
        this.mbID = mbID;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
    }

    public void setSellingID(String sellingID) {
        this.sellingID = sellingID;
    }
}
