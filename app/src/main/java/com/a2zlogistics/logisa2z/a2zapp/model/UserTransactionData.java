package com.a2zlogistics.logisa2z.a2zapp.model;

/**
 * Created by Shahrukh Khan on 9/27/2017.
 */

public class UserTransactionData {
    private String userTxnId;
    private String userTxnDesc;
    private long userTxnDate;
    private float userTxnAmt;

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

    public long getUserTxnDate() {
        return userTxnDate;
    }

    public void setUserTxnDate(long userTxnDate) {
        this.userTxnDate = userTxnDate;
    }

    public float getUserTxnAmt() {
        return userTxnAmt;
    }

    public void setUserTxnAmt(float userTxnAmt) {
        this.userTxnAmt = userTxnAmt;
    }
}
