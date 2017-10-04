package com.example.shahrukhkhan.freelance.Model;

/**
 * Created by Shahrukh Khan on 8/12/2017.
 */

public class CardData {
    private String cardName, cardId, vehicleNumber;
    private int cardBalance, cardStatus;
    private boolean fabStatus;

    public void setCardBalance(int cardBalance) {
        this.cardBalance = cardBalance;
    }

    public int getCardBalance() {
        return cardBalance;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardStatus(int cardStatus) {
        this.cardStatus = cardStatus;
    }

    public int getCardStatus() {
        return cardStatus;
    }

    public void setFabStatus(boolean fabStatus) {
        this.fabStatus = fabStatus;
    }

    public boolean getFabStatus() {
        return fabStatus;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }
}
