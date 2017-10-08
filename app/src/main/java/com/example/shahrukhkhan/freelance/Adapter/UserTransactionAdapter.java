package com.example.shahrukhkhan.freelance.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shahrukhkhan.freelance.Activities.UserTransactionActivity;
import com.example.shahrukhkhan.freelance.Model.UserTransactionData;
import com.example.shahrukhkhan.freelance.R;
import com.example.shahrukhkhan.freelance.Utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Shahrukh Khan on 9/27/2017.
 */

public class UserTransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<UserTransactionData> userTransactionDataList;
    private UserTransactionActivity transactionActivity;


    public UserTransactionAdapter(List<UserTransactionData> userTransactionDataList, UserTransactionActivity transactionActivity) {
        this.userTransactionDataList = userTransactionDataList;
        this.transactionActivity = transactionActivity;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TransactionListHolder transactionListHolder;

        public MyViewHolder(View view) {
            super(view);
            transactionListHolder = new TransactionListHolder();
            transactionListHolder.txnStatus = view.findViewById(R.id.txn_status);
            transactionListHolder.txnCardName = view.findViewById(R.id.txn_card_name);
            transactionListHolder.txnCardId = view.findViewById(R.id.txn_card_id);
            transactionListHolder.txnCardDate = view.findViewById(R.id.txn_date);
            transactionListHolder.txnCardAmount = view.findViewById(R.id.txn_amount);
            transactionListHolder.txnId = view.findViewById(R.id.txn_id);
            transactionListHolder.txnRemarks = view.findViewById(R.id.txn_remarks);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.transaction_item, parent, false);
        viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        UserTransactionData userTransactionData = userTransactionDataList.get(position);
        myViewHolder.transactionListHolder.txnStatus.setVisibility(View.GONE);
        String amount = Constants.RS + userTransactionData.getUserTxnAmt();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date testDate = null;
        try {
            testDate = sdf.parse(userTransactionData.getUserTxnDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
        String newFormat = formatter.format(testDate);
        myViewHolder.transactionListHolder.txnCardName.setText(userTransactionData.getUserTxnDesc());
        myViewHolder.transactionListHolder.txnCardId.setVisibility(View.GONE);
        myViewHolder.transactionListHolder.txnCardDate.setText(newFormat);
        myViewHolder.transactionListHolder.txnCardAmount.setText(amount);
        myViewHolder.transactionListHolder.txnId.setVisibility(View.GONE);
        myViewHolder.transactionListHolder.txnRemarks.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return userTransactionDataList.size();
    }

    private static class TransactionListHolder {
        TextView txnCardName, txnCardId, txnCardDate, txnCardAmount, txnId, txnRemarks, txnStatus;
    }

}