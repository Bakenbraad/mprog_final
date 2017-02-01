package nl.mprog.rens.vinylcountdown;

import java.io.Serializable;

/**
 * Created by Rens on 26/01/2017.
 */

public class ColWishRecord implements Serializable{

    public String getUserID() {
        return userID;
    }

    // This class has to remember the users id and recordinfo.
    private String userID;
    private RecordInfo recordInfo;
    private String colWishID;

    public RecordInfo getRecordInfo() {
        return recordInfo;
    }


    public ColWishRecord(RecordInfo recordInfo, String userID, String colWishID) {
        this.recordInfo = recordInfo;
        this.userID = userID;
        this.colWishID = colWishID;
    }

    // Empty constructor.
    public ColWishRecord(){}

    public String getColWishID() {
        return colWishID;
    }
}
