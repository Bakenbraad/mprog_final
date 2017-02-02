package nl.mprog.rens.vinylcountdown.ObjectClasses;

import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 * Rens van der Veldt - 10766162
 * Minor Programmeren
 *
 * Message.class
 *
 * The message object has many properties, from these properties the message content,
 * subject and anything relevant is created. The messages come in two main formats. The reply
 * and market message. A market message is sent when an offer is completed/made on the marketplace.
 * This then falls into three categories, trade, price and bidding, which determine the exact
 * content of the message. The reply messages are sent when a market message is responded to by
 * the selling user. The profiles and userids are kept to maintain sender and receiver data.
 * The offerID is used to delete the offer when a deal is struck. Read is set to true when a user
 * has replied to the message.
 */

public class Message implements Serializable{

    // Declare properties.
    private String buyOffer;
    private String sellOffer;
    private UserProfile senderProfile;
    private UserProfile receiverProfile;
    private String messageContent;
    private String time;
    private String priceType;
    private String messageType;
    private String offerID;
    private String senderID;
    private String receiverID;
    private String messageID;
    private boolean read;

    /**
     * The constructor is empty, the 2 functions for reply and market messages format the message
     * according to the origin.
     */
    public Message(){}

    /**
     * This is a response message, formed in the inbox when a user clicks reply or reject. This
     * determines the content of the message.
     * The parameters are data necessary for sending messages and reproducing a response form the
     * preceding message (thus a message as parameter.) The userprofiles are re-submitted even though
     * they are already present in the message object because profiles can change. Id's dont have to
     * be updated because they stay the same.
     *
     * @param messageType: the type of message.
     * @param message: the message replied to.
     * @param sender: the senders profile.
     * @param receiver: the receivers profile.
     * @param messageID: the uid of the message.
     */
    public void messageReply(String messageType, Message message, UserProfile sender, UserProfile receiver, String messageID){

        buyOffer = message.getBuyOffer();
        sellOffer = message.getSellOffer();

        // Flip the receiver and sender ids.
        senderID = message.getReceiverID();
        receiverID = message.getSenderID();

        // Set the message according to the type of response (rejected/accepted)!
        if (messageType.equals("accept")){
            if (message.priceType.equals("Price") || message.priceType.equals("Bidding")){

                // The difference is that price and bidding have a currency value for buyoffer and need a euro sign in front.
                this.messageContent = "I would like to accept your offer of €" + buyOffer + " for " + sellOffer + ", please contact me via my email address.";
            } else {
                this.messageContent = "I would like to accept your offer of " + buyOffer + " for " + sellOffer + ", please contact me via my email address.";
            }

        }
        if (messageType.equals("reject")){
            if (message.priceType.equals("Price") || message.priceType.equals("Bidding")){

                // The difference is that price and bidding have a currency value for buyoffer and need a euro sign in front.
                this.messageContent = "I respectfully decline your offer of €" + buyOffer + " for " + sellOffer + ", feel free to make another request!";

            } else {
                this.messageContent = "I respectfully decline your offer of " + buyOffer + " for " + sellOffer + ", feel free to make another request!";
            }

        }

        // Set the time at which the message was sent:
        this.time = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new java.util.Date());

        // Set all other variables.
        this.setRead(false);
        this.messageType = messageType;
        this.senderProfile = sender;
        this.receiverProfile = receiver;
        this.messageID = messageID;
    }

    /**
     * This is the original message sent from the market. The message is created from the data put in
     * by the user in the buyactivity, along with the recordsaleinfo. The messagetype determines
     * that this is a market sent message. The pricetype is used to generate the content,
     * a trade offer has a different content than a bidding offer. The other data, sender and
     * receiver, are used to register the message in the firebase for retrieval by the reciever.
     * The buy and selloffer form the actual subject of the message. Sell offer is the record that
     * is being offered and buy offer is the price/record that is offered in exchange.
     * Finally the message ID is there to retrieve and edit messages, this is the key for the object
     * in firebase.
     *
     * @param messageType: the type of message.
     * @param sellOffer: what is offerd for (in the market).
     * @param sender: the senders profile.
     * @param senderID: the senders id. (Also passed because profiles can be updated.
     * @param receiver: the receivers profile.
     * @param messageID: the message uid.
     */
    public void messageMarket(String messageType, RecordSaleInfo recordSaleInfo, String sellOffer, UserProfile sender, String senderID, UserProfile receiver, String messageID){

        // Create a message according to the right advertisement priceType.
        switch (recordSaleInfo.getPriceType()) {
            case "Price":

                this.messageContent = "Hello, I saw your advertisement for " + recordSaleInfo.getTitle() + " and I would buy it for €" + sellOffer + ".";
                break;
            case "Bidding from":

                this.messageContent = "Hello, I saw your advertisement for " + recordSaleInfo.getTitle() + " and I bid €" + sellOffer + ".";
                break;
            case "Trade":

                this.messageContent = "Hello, I saw your advertisement for " + recordSaleInfo.getTitle() + " and I would like to trade you for " + sellOffer + ".";
                break;
        }

        this.senderProfile = sender;
        this.receiverProfile = receiver;
        this.priceType = recordSaleInfo.getPriceType();
        this.buyOffer = recordSaleInfo.getTitle();
        this.sellOffer = sellOffer;
        this.offerID = recordSaleInfo.getSaleUID();
        this.senderID = senderID;
        this.receiverID = recordSaleInfo.getUserID();
        this.messageType = messageType;
        this.setRead(false);
        this.messageID = messageID;

        // Set the time at which the message was sent:
        this.time = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new java.util.Date());
    }

    // Getters and setters.
    public String getBuyOffer() {
        return buyOffer;
    }public String getOfferID() {
        return offerID;
    }

    public void setOfferID(String offerID) {
        this.offerID = offerID;
    }

    public void setBuyOffer(String buyOffer) {
        this.buyOffer = buyOffer;
    }

    public String getSellOffer() {
        return sellOffer;
    }

    public void setSellOffer(String sellOffer) {
        this.sellOffer = sellOffer;
    }

    public UserProfile getSender() {
        return senderProfile;
    }

    public void setSender(UserProfile sender) {
        this.senderProfile = sender;
    }

    public UserProfile getReceiver() {
        return receiverProfile;
    }

    public void setReceiver(UserProfile receiver) {
        this.receiverProfile = receiver;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return priceType;
    }

    public void setType(String priceType) {
        this.priceType = priceType;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
