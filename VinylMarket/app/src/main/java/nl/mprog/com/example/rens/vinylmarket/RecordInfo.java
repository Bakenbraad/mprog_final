package nl.mprog.com.example.rens.vinylmarket;

/**
 * Created by Rens on 11/01/2017.
 */

public class RecordInfo {

    String title;
    String artist;
    String published;
    String imgLink;
    String summary;
    String mbID;

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

    // Set all fields.
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
}
