package com.a2zlogistics.logisa2z.a2zapp.database;

/**
 * Created by Shahrukh Khan on 8/19/2017.
 */


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.a2zlogistics.logisa2z.a2zapp.model.TransactionData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LocalDB extends SQLiteOpenHelper {


    private static LocalDB mInstance;
    private static Context mContext;
    // Database Version
    protected static final int DATABASE_VERSION = 1;

    // Database Name
    protected static final String DATABASE_NAME = "TRANSACTIONSDB";

    public static final String CREATE_TABLE = "CREATE TABLE";
    public static final String DROP_TABLE = "DROP TABLE";

    public static final String NUMBER = "NUMBER";
    public static final String TEXT = "TEXT";
    public static final String INTEGER = "INTEGER";
    public static final String REAL = "REAL";

    public static final String AUTO_INCREMENT = "AUTO INCREMENT";
    public static final String NOT_NULL = "NOT NULL";
    public static final String UNIQUE = "UNIQUE";

    private static final int ALL = 0;
    private static final int PAID = 1;
    private static final int RECHARGED = 2;

    public static final String PRIMARY_KEY = "PRIMARY KEY";
    public static final String TABLE_TRANSACTION = "TRANS";
    public static final String TRANSACTION_ID = "TXN_ID";
    public static final String USER_ID = "USER_ID";
    public static final String CARD_ID = "CARD_ID";
    public static final String CARD_NAME = "CARD_NAME";
    public static final String CARD_AMOUNT = "CARD_AMOUNT";
    public static final String CARD_STATUS = "CARD_STATUS";
    public static final String CARD_TIMESTAMP = "CARD_TIMESTAMP";
    public static final String CARD_REMARKS = "CARD_REMARKS";
    public static final String TXN_TYPE = "TXN_TYPE";
    public static final String VEHICLE_NUMBER = "VEHICLE_NUMBER";

    protected static final String CREATE_TABLE_TRANSACTION =
            CREATE_TABLE + " " + TABLE_TRANSACTION + "("
                    + TRANSACTION_ID + " " + TEXT + " " + PRIMARY_KEY + ","
                    + USER_ID + " " + TEXT + ","
                    + CARD_ID + " " + TEXT + ","
                    + CARD_NAME + " " + TEXT + ","
                    + CARD_AMOUNT + " " + INTEGER + ","
                    + CARD_STATUS + " " + INTEGER + ","
                    + CARD_TIMESTAMP + " " + INTEGER + ","
                    + CARD_REMARKS + " " + TEXT + ","
                    + TXN_TYPE + " " + TEXT + ","
                    + VEHICLE_NUMBER + " " + TEXT + ","
                    + "UNIQUE (" + TRANSACTION_ID + ") ON CONFLICT REPLACE" +
                    ")";

    private static Date initialDate;

    public static LocalDB getmInstance(Context context) {
        mContext = context;
        if (mInstance == null) {
            mInstance = new LocalDB(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        return mInstance;
    }


    public LocalDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        String str = "01/09/2017";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
        try {
            initialDate = sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TRANSACTION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<TransactionData> getData(int type, String from, String to) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        long fromDate = System.currentTimeMillis();
        long toDate = System.currentTimeMillis();
        try {
            if (from.equals("")) {
                fromDate = initialDate.getTime();
            } else {
                fromDate = sdf.parse(from).getTime();
            }
            if (to.equals("")) {
                toDate = System.currentTimeMillis();
            } else {
                toDate = sdf.parse(to).getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SQLiteDatabase db = mInstance.getReadableDatabase();
        List<TransactionData> list = new ArrayList<>();
        String selectQuery = null;
        if (type == ALL) {
            selectQuery = "SELECT  * FROM " + TABLE_TRANSACTION + " WHERE " + CARD_TIMESTAMP + " BETWEEN " + fromDate + " AND " + toDate
                    + " ORDER BY " + CARD_TIMESTAMP + " ASC";
        } else if (type == PAID) {
            selectQuery = "SELECT  * FROM " + TABLE_TRANSACTION + " WHERE " + TXN_TYPE + " LIKE 'Debit' AND "
                    + CARD_TIMESTAMP + " BETWEEN " + fromDate + " AND " + toDate + " ORDER BY " + CARD_TIMESTAMP + " ASC";
        } else if (type == RECHARGED) {
            selectQuery = "SELECT  * FROM " + TABLE_TRANSACTION + " WHERE " + TXN_TYPE + " LIKE 'Credit' AND "
                    + CARD_TIMESTAMP + " BETWEEN " + fromDate + " AND " + toDate + " ORDER BY " + CARD_TIMESTAMP + " ASC";
        }

        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToLast()) {
            do {
                TransactionData transactionData = new TransactionData();
                transactionData.setTxnId(c.getString(c.getColumnIndex(TRANSACTION_ID)));
                transactionData.setUserID(c.getString(c.getColumnIndex(USER_ID)));
                transactionData.setCardId(c.getString(c.getColumnIndex(CARD_ID)));
                transactionData.setCardName(c.getString(c.getColumnIndex(CARD_NAME)));
                transactionData.setCardAmount(c.getInt(c.getColumnIndex(CARD_AMOUNT)));
                transactionData.setCardStatus(c.getInt(c.getColumnIndex(CARD_STATUS)));
                transactionData.setCardTimeStamp(c.getLong(c.getColumnIndex(CARD_TIMESTAMP)));
                transactionData.setCardRemarks(c.getString(c.getColumnIndex(CARD_REMARKS)));
                transactionData.setTxnType(c.getString(c.getColumnIndex(TXN_TYPE)));
                transactionData.setVehicleNumber(c.getString(c.getColumnIndex(VEHICLE_NUMBER)));
                list.add(transactionData);
            } while (c.moveToPrevious());
        }
        c.close();
        db.close();
        return list;
    }

    public List<TransactionData> getCardData(String id, int type, String from, String to) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        long fromDate = System.currentTimeMillis();
        long toDate = System.currentTimeMillis();
        try {
            if (from.equals("")) {
                fromDate = initialDate.getTime();
            } else {
                fromDate = sdf.parse(from).getTime();
            }
            if (to.equals("")) {
                toDate = System.currentTimeMillis();
            } else {
                toDate = sdf.parse(to).getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SQLiteDatabase db = mInstance.getReadableDatabase();
        List<TransactionData> list = new ArrayList<>();
        String selectQuery = null;
        if (type == ALL) {
            selectQuery = "SELECT  * FROM " + TABLE_TRANSACTION + " WHERE " + CARD_ID
                    + " LIKE '" + id + "' AND " + CARD_TIMESTAMP + " BETWEEN " + fromDate + " AND " + toDate +
                    " ORDER BY " + CARD_TIMESTAMP + " ASC";
        } else if (type == PAID) {
            selectQuery = "SELECT  * FROM " + TABLE_TRANSACTION + " WHERE " + CARD_ID
                    + " LIKE '" + id + "' AND " + TXN_TYPE + " LIKE 'Debit' AND "
                    + CARD_TIMESTAMP + " BETWEEN " + fromDate + " AND " + toDate + " ORDER BY " + CARD_TIMESTAMP + " ASC";
        } else if (type == RECHARGED) {
            selectQuery = "SELECT  * FROM " + TABLE_TRANSACTION + " WHERE " + CARD_ID
                    + " LIKE '" + id + "' AND " + TXN_TYPE + " LIKE 'Credit' AND "
                    + CARD_TIMESTAMP + " BETWEEN " + fromDate + " AND " + toDate + " ORDER BY " + CARD_TIMESTAMP + " ASC";
        }

        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToLast()) {
            do {
                TransactionData transactionData = new TransactionData();
                transactionData.setTxnId(c.getString(c.getColumnIndex(TRANSACTION_ID)));
                transactionData.setUserID(c.getString(c.getColumnIndex(USER_ID)));
                transactionData.setCardId(c.getString(c.getColumnIndex(CARD_ID)));
                transactionData.setCardName(c.getString(c.getColumnIndex(CARD_NAME)));
                transactionData.setCardAmount(c.getInt(c.getColumnIndex(CARD_AMOUNT)));
                transactionData.setCardStatus(c.getInt(c.getColumnIndex(CARD_STATUS)));
                transactionData.setCardTimeStamp(c.getLong(c.getColumnIndex(CARD_TIMESTAMP)));
                transactionData.setCardRemarks(c.getString(c.getColumnIndex(CARD_REMARKS)));
                transactionData.setTxnType(c.getString(c.getColumnIndex(TXN_TYPE)));
                transactionData.setVehicleNumber(c.getString(c.getColumnIndex(VEHICLE_NUMBER)));
                list.add(transactionData);
            } while (c.moveToPrevious());
        }
        c.close();
        db.close();
        return list;
    }

    public void putData(TransactionData data) {
        SQLiteDatabase db = mInstance.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TRANSACTION_ID, data.getTxnId());
        contentValues.put(USER_ID, data.getUserID());
        contentValues.put(CARD_ID, data.getCardId());
        contentValues.put(CARD_NAME, data.getCardName());
        contentValues.put(CARD_AMOUNT, data.getCardAmount());
        contentValues.put(CARD_STATUS, data.getCardStatus());
        contentValues.put(CARD_TIMESTAMP, data.getCardTimeStamp());
        contentValues.put(CARD_REMARKS, data.getCardRemarks());
        contentValues.put(TXN_TYPE, data.getTxnType());
        contentValues.put(VEHICLE_NUMBER, data.getVehicleNumber());
        String where = TRANSACTION_ID + " =? ";
        String[] params = {data.getTxnId()};
        int id = db.update(TABLE_TRANSACTION, contentValues, where, params);
        if (id == 0)
            db.insertWithOnConflict(TABLE_TRANSACTION, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public String getLatestTransactionDate() {
        long timeStamp = 0;
        String date;
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String selectQuery = "SELECT MAX(" + CARD_TIMESTAMP + ") FROM " + TABLE_TRANSACTION;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            timeStamp = c.getLong(c.getColumnIndex("MAX(CARD_TIMESTAMP)"));
        }
        c.close();
        db.close();
        if (timeStamp == 0) {
            date = "2017-09-01T00:00:00";
        } else {
            Date temp = new Date(timeStamp);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            date = formatter.format(temp);
        }
        return date;
    }

    public void deleteTransactions() {
        SQLiteDatabase db = mInstance.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_TRANSACTION);
    }
}
