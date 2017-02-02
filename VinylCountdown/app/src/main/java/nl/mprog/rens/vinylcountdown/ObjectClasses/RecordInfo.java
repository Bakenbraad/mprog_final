package nl.mprog.rens.vinylcountdown.ObjectClasses;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Rens van der Veldt - 10766162
 * Minor Programmeren
 *
 * RecordInfo.class
 *
 * The recordInfo class contains basic info about records retrieved by the apiHelper from the
 * last fm api. In the constructor strings are added that form the basic properties. A hashmap is
 * initialized to store tracks. The tracks are added separately. The class implements Serializable
 * because it has to be passable between activities.
 */

public class RecordInfo implements Serializable {

    // Declare all properties.
    private String title;
    private String artist;
    private String imgLinkmed;
    private String imgLinklarge;
    private String summary;
    private String mbid;
    private Map<String, String> tracks;

    // Firebase requires empty constructor.
    public RecordInfo(){}

    // Constructor that localizes the values.
    public RecordInfo(String title, String artist, String imgLinkmed, String imgLinklarge, String summary, String mbid){

        this.artist = artist;
        this.imgLinkmed = imgLinkmed;
        this.title = title;
        this.imgLinklarge = imgLinklarge;
        this.summary = summary;
        this.mbid = mbid;

        // Initialize tracks hashmap.
        tracks = new HashMap<>();
    }

    // Getters and setters
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

    /**
     * Adds a title and duration as a key value pair to the tracks hashmap.
     * # and . are replaced by this function because they are not accepted characters
     * by firebase.
     * @param title: title of a song.
     * @param duration: duration in seconds of that song.
     */
    public void addTrack(String title, String duration){

        if (title != null && duration != null){

            // Replace unwanted characters.
            title = title.replace("#", "no").replace(".", "");
            tracks.put(title.replace("#", "no"),duration);
        }
    }
}