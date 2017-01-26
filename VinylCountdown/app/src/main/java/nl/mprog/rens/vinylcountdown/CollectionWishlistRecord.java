package nl.mprog.rens.vinylcountdown;

import java.io.Serializable;

/**
 * Created by Rens on 26/01/2017.
 */

public class CollectionWishlistRecord implements Serializable{

    public String getUserID() {
        return userID;
    }

    // This class has to remember the users id and recordinfo.
    private String userID;
    private RecordInfo recordInfo;

    public RecordInfo getRecordInfo() {
        return recordInfo;
    }

    public CollectionWishlistRecord(RecordInfo recordInfo, String userID) {
        this.recordInfo = recordInfo;
        this.userID = userID;
    }

    // Empty constructor.
    public CollectionWishlistRecord(){}
}
