package com.example.shahrukhkhan.freelance.model;

/**
 * Created by Shahrukh Khan on 8/19/2017.
 */

public class TransactionData {
    private String txnId;
    private String userID;
    private String cardName;
    private String cardTimeStamp;
    private String cardId;
    private String cardRemarks;
    private String cardType;
    private String txnType;
    private String vehicleNumber;
    private int cardAmount, cardStatus;

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardTimeStamp(String cardTimeStamp) {
        this.cardTimeStamp = cardTimeStamp;
    }

    public String getCardTimeStamp() {
        return cardTimeStamp;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardAmount(int cardAmount) {
        this.cardAmount = cardAmount;
    }

    public int getCardAmount() {
        return cardAmount;
    }

    public void setCardStatus(int cardStatus) {
        this.cardStatus = cardStatus;
    }

    public int getCardStatus() {
        return cardStatus;
    }

    public void setCardRemarks(String cardRemarks) {
        this.cardRemarks = cardRemarks;
    }

    public String getCardRemarks() {
        return cardRemarks;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardId() {
        return cardId;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }
}
