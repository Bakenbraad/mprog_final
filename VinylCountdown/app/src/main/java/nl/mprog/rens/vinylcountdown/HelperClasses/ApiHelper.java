package nl.mprog.rens.vinylcountdown.HelperClasses;

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
import java.util.List;

import nl.mprog.rens.vinylcountdown.ObjectClasses.RecordInfo;

/**
 * Rens van der Veldt - 10766162
 * Minor Programmeren
 *
 * ApiHelper.class
 *
 * The apiHelper class employs the help of the lastfm api to retrieve data. The methods album.search
 * and album.getinfo are used. Album search is used to perform the raw query and get mbids for all
 * corresponding albums. These mbids are used in the recordInfoSearch method to retrieve more data about
 * each album and store these in recordinfo objects.
 */

public class ApiHelper {

    /**
     * Search takes a query and retrieves a list of albumIDs by means of the
     * albumIDSearch function. This fills the albumids list which is then used to do a recordinfosearch,
     * this function takes in the mbid results in albumids and find the info for the corresponding albums.
     * These are subsequently addend to a list and returned for further usage to wherever the Api
     * helper is employed, for example in asyncMusicSearch the data is displayed by a custom adapter
     * and in marketAsynctask the data is cross-referenced with the data in the marketplace.
     *
     * @param query: the query to search for.
     */
    public List<RecordInfo> Search(String query) throws IOException {

        // Retrieve all album ids matching the query
        List<String> albumIDs = getAlbumIDs(query);


        // Declare a list of recordInfo objects.
        List<RecordInfo> searchResults = new ArrayList<>();

        // Find the object for each mbid.
        for (int i = 0; i < albumIDs.size(); i++){

            // Extract mbid and perform recordInfoSearch.
            String mbid = albumIDs.get(i);
            RecordInfo recordInfo = recordInfoSearch(mbid);

            // If the object contains null values, don't add.
            if (recordInfo != null){
                searchResults.add(recordInfo);
            }
        }

        // Returns the search results!
        return searchResults;
    }

    /**
     * Input reader used to parse reader to string.
     * Constructed from: http://stackoverflow.com/questions/2492076/android-reading-from-an-input-stream-efficiently
     *
     * @param rd: The input reader.
     * @throws IOException
     */
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }


    /**
     * Get all album Id's that match the query that is put in. First, a url is generated form the query
     * and api url. Secondly, the returned JSon object is read and parsed. Finally, a check only adds
     * those albums with mbids. The list is then returned.
     *
     * @param query: The query to search for.
     * @throws IOException
     */
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

    /**
     * The recordInfoSearch method takes in an mbid and gets the album information from the api in
     * json format. This is subsequently read and the required properties are put into a recordInfo
     * object.
     *
     * @param mbid: a unique id that defines albums.
     */
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

            // Get the basic data form the json to be put into an object.
            String title = album.getString("name");
            String artist = album.getString("artist");
            String imgLinkmed = album.getJSONArray("image").getJSONObject(3).getString("#text");
            String imgLinklarge = album.getJSONArray("image").getJSONObject(4).getString("#text");

            // Get the wiki if object has one, you don't want the link after the wiki, after <a href
            // the wiki should stop.
            String[] summarySplit = new String[2];
            if (album.has("wiki") && album.getJSONObject("wiki").has("summary")){
                String summary = album.getJSONObject("wiki").getString("summary");
                summarySplit = summary.split("<a href");
            }
            else{
                summarySplit[0] = "Not available.";
            }

            // Put the primary data into the object.
            RecordInfo recordInfo = new RecordInfo(title, artist, imgLinkmed, imgLinklarge, summarySplit[0], mbid);

            // Tracks need iteration so are done separately.
            JSONArray tracks = album.getJSONObject("tracks").getJSONArray("track");
            int trackCount = tracks.length();

            for (int i = 0; i < trackCount; i++){

                // Get the tracks name and duration for each track.
                String trackName = tracks.getJSONObject(i).getString("name");
                String trackDuration = tracks.getJSONObject(i).getString("duration");

                if (trackName != null && trackDuration != null){
                    // Add the track to the recordinfo object.
                    recordInfo.addTrack(trackName, trackDuration);
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
