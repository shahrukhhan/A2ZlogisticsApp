package com.example.shahrukhkhan.freelance.Model;

/**
 * Created by Shahrukh Khan on 9/27/2017.
 */

public class UserTransactionData {
    private String userTxnId;
    private String userTxnDesc;
    private String userTxnDate;
    private int userTxnAmt;

    public String getUserTxnId() {
        return userTxnId;
    }

    public void setUserTxnId(String userTxnId) {
        this.userTxnId = userTxnId;
    }

    public String getUserTxnDesc() {
        return userTxnDesc;
    }

    public void setUserTxnDesc(String userTxnDesc) {
        this.userTxnDesc = userTxnDesc;
    }

    public String getUserTxnDate() {
        return userTxnDate;
    }

    public void setUserTxnDate(String userTxnDate) {
        this.userTxnDate = userTxnDate;
    }

    public int getUserTxnAmt() {
        return userTxnAmt;
    }

    public void setUserTxnAmt(int userTxnAmt) {
        this.userTxnAmt = userTxnAmt;
    }
}
