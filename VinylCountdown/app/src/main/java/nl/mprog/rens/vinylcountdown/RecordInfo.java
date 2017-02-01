package nl.mprog.rens.vinylcountdown;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rens on 16/01/2017.
 */

public class RecordInfo implements Serializable {

    private String title;
    private String artist;
    private String imgLinkmed;
    private String imgLinklarge;
    private String summary;
    private String mbid;
    private Map<String, String> tracks;

    // Firebase required empty constructor.
    public RecordInfo(){}

    public RecordInfo(String title, String artist, String imgLinkmed, String imgLinklarge, String summary, String mbid){
        this.artist = artist;
        this.imgLinkmed = imgLinkmed;
        this.title = title;
        this.imgLinklarge = imgLinklarge;
        this.summary = summary;
        this.mbid = mbid;
        tracks = new HashMap<>();
    }

    // Getters for all fields
    public String getMbid() {
        return mbid;
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

    public String getSummary() {
        return summary;
    }

    public String getTitle() {
        return title;
    }

    public Map<String, String> getTracks() {
        return tracks;
    }

    public void addTrack(String title, String duration){
        if (title != null && duration != null){
            title = title.replace("#", "no").replace(".", "");
            tracks.put(title.replace("#", "no"),duration);
        }
    }
}