package com.a2zlogistics.logisa2z.a2zapp.model;

/**
 * Created by Shahrukh Khan on 8/12/2017.
 */

public class CardData {
    private String cardName, cardId, vehicleNumber;
    private int cardStatus;
    private float cardBalance;
    private boolean fabStatus;

    public void setCardBalance(float cardBalance) {
        this.cardBalance = cardBalance;
    }

    public float getCardBalance() {
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
