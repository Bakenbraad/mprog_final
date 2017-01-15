package nl.mprog.com.example.rens.vinylmarket;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rens on 11/01/2017.
 */

public class RecordInfo {

    String title;
    String artist;
    String published;
    String imgLinkmed;
    String imgLinklarge;
    String summary;
    String mbID;
    Map<String, String> tracks;

    public RecordInfo(String title, String artist, String published, String imgLinkmed, String imgLinklarge, String summary, String mbID){
        setArtist(artist);
        setImgLinkmed(imgLinkmed);
        setImgLinklarge(imgLinklarge);
        setMbid(mbID);
        setPublished(published);
        setSummary(summary);
        setTitle(title);
    }

    // Getters for all fields
    public String getMbid() {
        return mbID;
    }

    public String getArtist() {
        return artist;
    }

    public String getImgLinkmed() {
        return imgLinkmed;
    }

    public String getImgLinklarge(){
        return imgLinklarge;
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

    public Map<String, String> getTracks() {
        return tracks;
    }

    // Set all fields.
    public void setMbid(String mbID) {
        this.mbID = mbID;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setImgLinkmed(String imgLinkmed) {
        this.imgLinkmed = imgLinkmed;
    }

    public void setImgLinklarge(String imgLinklarge) {
        this.imgLinklarge = imgLinklarge;
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

    public void addTrack(String title, String duration){
        tracks.put(title,duration);
    }
}
