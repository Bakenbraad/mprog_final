package nl.mprog.com.example.rens.vinylmarket;

import java.util.ArrayList;

/**
 * Created by Rens on 11/01/2017.
 */

public class User {

    String username;
    String email;
    String password;
    String id;
    ArrayList<String> wishlist;
    ArrayList<String> collection;

    // Constructor takes all basic info for the user
    public User(String username, String email, String id, String password){
        setEmail(email);
        setId(id);
        setUsername(username);
        setPassword(password);
    }

    // Getters for all info
    public String getId() {
        return id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public ArrayList<String> getCollection() {
        return collection;
    }

    public ArrayList<String> getWishlist() {
        return wishlist;
    }

    // Setters for every field except the lists.
    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Adders and removers for the wishlist and collection.
    public void addCollection(String mbid) {
        collection.add(mbid);
    }

    public void addWishlist(String mbid) {
        collection.add(mbid);
    }

    public void removeCollection(String mbid) {
        collection.remove(mbid);
    }

    public void removeWishlist(String mbid) {
        wishlist.remove(mbid);
    }
}

