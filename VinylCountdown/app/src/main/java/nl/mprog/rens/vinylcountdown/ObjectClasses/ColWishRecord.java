package nl.mprog.rens.vinylcountdown.ObjectClasses;

import java.io.Serializable;

/**
 * Rens van der Veldt - 10766162
 * Minor Programmeren
 *
 * ColWishRecord.class
 *
 * This object is used to store collection and wishlist data. The difference between these is marked
 * by their location in the database. This object stores the users id, its own unique id ( a user
 * can have more than one of a record) and the record data as a recordinfo object.
 * The class implements serializable so it can be passed between activities.
 */

public class ColWishRecord implements Serializable{

    // This class has to remember the users id and recordinfo.
    private String userID;
    private RecordInfo recordInfo;
    private String colWishID;

    // Constructor.
    public ColWishRecord(RecordInfo recordInfo, String userID, String colWishID) {
        this.recordInfo = recordInfo;
        this.userID = userID;
        this.colWishID = colWishID;
    }

    // Empty constructor required by firebase.
    public ColWishRecord(){}

    // Getters (setters are never used!)
    public String getColWishID() {
        return colWishID;
    }

    public String getUserID() {
        return userID;
    }

    public RecordInfo getRecordInfo() {
        return recordInfo;
    }
}
