package nl.mprog.rens.vinylcountdown;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Rens on 16/01/2017.
 */

public class ApiHelper {

    // Search takes a query and retrieves a list of albumIDs by means of the
    // albumIDSearch function. This fills the albumids list which is then used to do a recordsearch, this
    // function takes in the mbid results in albumids and find the info for the corresponding albums.
    // These are subsequently addend to a list and returned for passing through the custom adapter.
    public List<RecordInfo> Search(String query) throws IOException {

        // If the method is 1 search for albums using the query:

        List<String> albumIDs = getAlbumIDs(query);


        // Get all data from the albums and put them in the generated searchResults list.
        List<RecordInfo> searchResults = new ArrayList<>();
        for (int i = 0; i < albumIDs.size(); i++){

            // This gets the data for each album and returns a recordinfo object, this is added to an array of these objects,
            // also known as the search results!
            String mbid = albumIDs.get(i);
            RecordInfo recordInfo = recordInfoSearch(mbid);
            if (recordInfo != null){
                searchResults.add(recordInfo);
            }
        }

        // Returns the search results!
        return searchResults;
    }

    // Reads input imput and parses to string.
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    // Get all search results for a certain query.
    private List<String> getAlbumIDs(String query) throws IOException {

        // Initiate return list.
        List<String> albumIDs = new ArrayList<>();

        // Format the query to make it url readable.
        query = query.replace(" ", "%20");

        // Create the url string.
        String albumURL = "http://ws.audioscrobbler.com/2.0/?method=album.search&limit=80&album=" + query + "&api_key=53ed794c5c41de92a71525c6e303cf19&format=json";

        // Try to create the input stream.
        try (InputStream is = new URL(albumURL).openStream()) {

            // Get input and turn it to a JSONObject.
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);

            String resultCount = json.getJSONObject("results").getString("opensearch:totalResults");
            // If results are found, the mbids are extracted from the matches and added to a list.
            if (!resultCount.equals("0")){
                JSONObject results = json.getJSONObject("results");
                JSONObject albumMatches = results.getJSONObject("albummatches");
                JSONArray albums = albumMatches.getJSONArray("album");

                for (int i = 0; i < albums.length(); i++){

                    // Extract id from each album object and add it to the albumID list.
                    JSONObject album = albums.getJSONObject(i);
                    if (!album.getString("mbid").equals("")){
                        if (!albumIDs.contains(album.getString("mbid"))){
                            albumIDs.add(album.getString("mbid"));
                        }
                    }
                }
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return albumIDs;
    }

    // Searches an album by mbid and returns an object with all necessary data.
    public RecordInfo recordInfoSearch(String mbid){

        // Create the url string for method album.getinfo
        String albumUrl = "http://ws.audioscrobbler.com/2.0/?method=album.getinfo&api_key=53ed794c5c41de92a71525c6e303cf19&mbid=" + mbid + "&format=json";

        // Try data retrieval
        try (InputStream is = new URL(albumUrl).openStream()) {

            // Get input and turn it to a JSONObject.
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            JSONObject album = json.getJSONObject("album");

            // Create an object, recordinfo, and add the data.
            // Get the items from the json seperately for clarities sake.
            String title = album.getString("name");
            String artist = album.getString("artist");
            String imgLinkmed = album.getJSONArray("image").getJSONObject(3).getString("#text");
            String imgLinklarge = album.getJSONArray("image").getJSONObject(4).getString("#text");

            // Get the wiki if object has one
            String[] summarySplit = new String[2];
            if (album.has("wiki") && album.getJSONObject("wiki").has("summary")){
                String summary = album.getJSONObject("wiki").getString("summary");
                summarySplit = summary.split("<a href");
            }
            else{
                summarySplit[0] = "Not available.";
            }


            RecordInfo recordInfo = new RecordInfo(title, artist, imgLinkmed, imgLinklarge, summarySplit[0], mbid);

            // Tracks need iteration so is done seperately:
            JSONArray tracks = album.getJSONObject("tracks").getJSONArray("track");
            int trackCount = tracks.length();

            for (int i = 0; i < trackCount; i++){

                // Get the trackname and duration for each track.
                String trackname = tracks.getJSONObject(i).getString("name");
                String trackduration = tracks.getJSONObject(i).getString("duration");

                if (trackname != null && trackduration != null){
                    // Add the track to the recordinfo object.
                    recordInfo.addTrack(trackname, trackduration);
                }
                // Add the track to the recordinfo object.
            }

            // Finally return the object with all info.
            return recordInfo;

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
