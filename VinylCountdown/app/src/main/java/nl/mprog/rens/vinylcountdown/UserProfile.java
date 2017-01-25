package nl.mprog.rens.vinylcountdown;

import java.io.Serializable;

/**
 * Created by Rens on 19/01/2017.
 */

public class UserProfile implements Serializable {

    private String username;
    private String email;
    private String location;

    public UserProfile(){}

    public UserProfile(String username, String email, String location) {
        this.username = username;
        this.email = email;
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
