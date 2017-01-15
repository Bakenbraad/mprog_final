package nl.mprog.com.example.rens.vinylmarket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Rens on 13/01/2017.
 */

public class ApiManager {

    private String rawquery;

    // Constructor to put in query.
    public List<RecordInfo> Search(String query) {

        // Set the query to make it readable for other functions.
        this.rawquery = query;

        // Find all matching album ids these are automatically put in a list for recordSearch to use.
        musicSearch();

        // Get all data from these albums and put them in the generated list.
        List<RecordInfo> searchResults = null;
        for (int i = 0; i < albumIDs.size(); i++){

            // This gets the data for each album and returns a recordinfo object, this is added to an array of these objects,
            // also known as the search results!
            String mbid = albumIDs.get(i);
            RecordInfo recordInfo = recordSearch(mbid);
            if (recordInfo != null){
                searchResults.add(recordInfo);
            }
        }

        // Returns the search results!
        return searchResults;
    }

    // Creates a list of all album ids that correspond to the search query.
    private List<String> albumIDs = new ArrayList<>();

    // Search for any albums/artists albums that correspond to the query.
    private void musicSearch(){

        // Split the query if multiple are submitted (comma seperated).
        List<String> queryList = Arrays.asList(rawquery.split(","));

        // Try to get results with each entered query treated as album and artist.
        for (int i = 0; i < queryList.size(); i++){
            try {
                getAlbumIDs(queryList.get(i));
                getArtistsAlbumIDs(queryList.get(i));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    // Get all search results for albums.
    private void getAlbumIDs(String query) throws IOException {

        // Format the query to make it url readable.
        query = query.replace(" ", "%20");

        // Create the url string.
        String albumURL = "http://ws.audioscrobbler.com/2.0/?method=album.search&limit=15&album=" + query + "&api_key=53ed794c5c41de92a71525c6e303cf19&format=json";

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

                    // Extract id from each album object.
                    JSONObject album = albums.getJSONObject(i);
                    if (!album.getString("mbid").equals("")){
                        albumIDs.add(album.getString("mbid"));
                    }
                }
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    // This is a two step api request, you get the artists id first and then retrieve their albums using
    // the artist search and artist getTopAlbums method.
    private void getArtistsAlbumIDs(String query) throws IOException {

        // Initialize the artist id list. This is used for retrieving albums from artists.
        List<String> artistIDs = new ArrayList<>();

        // Format the query to make it url readable, for example blonde on blonde becomes blonde%20on%20blonde.
        query = query.replace(" ", "%20");

        // Create the url string for artist search.
        String artistURL = "http://ws.audioscrobbler.com/2.0/?method=artist.search&limit=4&artist=" + query + "&api_key=53ed794c5c41de92a71525c6e303cf19&format=json";

        // Try to create the input stream.
        try (InputStream is = new URL(artistURL).openStream()) {

            // Get input and turn it to a JSONObject.
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);

            // If results are found, the mbids for the artist are extracted from the matches and added to a list.
            if (!json.getJSONObject("results").getString("opensearch:totalResults").equals("0")){
                JSONArray artists = json.getJSONObject("results").getJSONObject("artistmatches").getJSONArray("artist");

                // Go over each match and get the artists' mbids.
                for (int i = 0; i < artists.length(); i++){

                    // Extract id from each album object.
                    JSONObject album = artists.getJSONObject(i);
                    if (!album.getString("mbid").equals("")){
                        artistIDs.add(album.getString("mbid"));
                    }
                }
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < artistIDs.size(); i++){
            String mbidQuery = artistIDs.get(i);

            // Create the url string for the mbid query.
            String artistMbidURL = "http://ws.audioscrobbler.com/2.0/?method=artist.getTopAlbums&limit=5&mbid=" + mbidQuery + "&api_key=53ed794c5c41de92a71525c6e303cf19&format=json";

            // For each id found in the artist query get their top albums.
            try (InputStream is = new URL(artistMbidURL).openStream()) {

                // Get input and turn it to a JSONObject.
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                JSONObject json = new JSONObject(jsonText);

                // If results are found, the mbids for the artist are extracted from the matches and added to a list.
                JSONArray albums = json.getJSONObject("topalbums").getJSONArray("album");

                // Go over each album and get the albums mbid.
                for (int j = 0; j < albums.length(); j++){

                    // Extract mbid from each album object and add it to the list.
                    JSONObject album = albums.getJSONObject(i);
                    String mbid = album.getString("mbid");
                    if (!mbid.equals("")){
                        albumIDs.add(mbid);
                    }
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private RecordInfo recordSearch(String mbid){

        RecordInfo recordInfo = new RecordInfo("unknown","unknown","unknown","unknown","unknown","unknown","unknown");

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
            String published = album.getJSONObject("wiki").getString("published");
            String imgLinkmed = album.getJSONArray("image").getJSONObject(1).getString("#text");
            String imgLinklarge = album.getJSONArray("image").getJSONObject(2).getString("#text");
            String summary = album.getJSONObject("wiki").getString("summary");

            recordInfo = new RecordInfo(title, artist, published, imgLinkmed, imgLinklarge, summary, mbid);

            // Tracks need iteration so is done seperately:
            JSONArray tracks = album.getJSONObject("tracks").getJSONArray("track");
            int trackCount = tracks.length();

//            for (int i = 0; i < trackCount; i++){
//
//                // Get the trackname and duration for each track.
//                String trackname = tracks.getJSONObject(i).getString("name");
//                String trackduration = tracks.getJSONObject(i).getString("duration");
//
//                if (trackname != null || trackduration != null){
//                    // Add the track to the recordinfo object.
//                    recordInfo.addTrack(trackname, trackduration);
//                }
//                // Add the track to the recordinfo object.
//
//            }

            // Finally return the object with all info.
            return recordInfo;

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return recordInfo;
    }
}
