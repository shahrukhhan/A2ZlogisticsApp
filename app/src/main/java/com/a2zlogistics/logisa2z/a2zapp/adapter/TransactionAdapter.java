package com.a2zlogistics.logisa2z.a2zapp.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a2zlogistics.logisa2z.a2zapp.R;
import com.a2zlogistics.logisa2z.a2zapp.activities.TransactionActivity;
import com.a2zlogistics.logisa2z.a2zapp.interfaces.ListClickListener;
import com.a2zlogistics.logisa2z.a2zapp.model.TransactionData;
import com.a2zlogistics.logisa2z.a2zapp.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Shahrukh Khan on 8/19/2017.
 */

public class TransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TransactionData> transactionDataList;
    private ListClickListener listClickListener;
    private TransactionActivity transactionActivity;
    private int expandPos = -1;


    public TransactionAdapter(List<TransactionData> transactionDataList, ListClickListener listClickListener, TransactionActivity transactionActivity) {
        this.transactionDataList = transactionDataList;
        this.listClickListener = listClickListener;
        this.transactionActivity = transactionActivity;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
            transactionListHolder.txnRemarks2 = view.findViewById(R.id.txn_remarks2);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getAdapterPosition() == expandPos) {
                expandPos = -1;
            } else {
                expandPos = getAdapterPosition();
            }
            listClickListener.onItemClick(getAdapterPosition(), 0);
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
        TransactionData transactionData = transactionDataList.get(position);
        if (transactionData.getCardStatus() == 0) {
            myViewHolder.transactionListHolder.txnStatus.setVisibility(View.VISIBLE);
            myViewHolder.transactionListHolder.txnStatus.setText(R.string.failed);
            myViewHolder.transactionListHolder.txnStatus.setTextColor(ColorStateList.valueOf(Color.parseColor("#B94A48")));
        } else if (transactionData.getCardStatus() == 1) {
            myViewHolder.transactionListHolder.txnStatus.setVisibility(View.GONE);
        } else if (transactionData.getCardStatus() == 2) {
            myViewHolder.transactionListHolder.txnStatus.setVisibility(View.VISIBLE);
            myViewHolder.transactionListHolder.txnStatus.setText(R.string.pending);
            myViewHolder.transactionListHolder.txnStatus.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFA500")));
        }
        String amount;
        String remarks;
        String remarks2 = "";
        if (transactionData.getTxnType().equals("Credit")) {
            amount = "+ " + Constants.RS + String.format(Locale.US, "%.2f", transactionData.getCardAmount());
            remarks = "Remarks: " + transactionData.getCardRemarks();
        } else {
            amount = "- " + Constants.RS + String.format(Locale.US, "%.2f", transactionData.getCardAmount());
            String[] info = transactionData.getCardRemarks().split(",");
            if(info.length == 3) {
                remarks = "Product: " + info[0] + "\nVolume: " + info[1] + " litres" + "\nRate: ₹" + info[2];
            } else {
                remarks = "Product: " + info[0] + "\nLocation: " + info[3];
                remarks2 = "Volume: " + info[1] + " litres" + "\nRate: ₹" + info[2];
            }
        }
        String name = transactionData.getCardName() + " - " + transactionData.getCardId();
        String id = "Txn Id: " + transactionData.getTxnId();
        Date testDate = new Date(transactionData.getCardTimeStamp());
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US);
        String newFormat = formatter.format(testDate);
        myViewHolder.transactionListHolder.txnCardName.setText(name);
        myViewHolder.transactionListHolder.txnCardId.setText(transactionData.getVehicleNumber());
        myViewHolder.transactionListHolder.txnCardDate.setText(newFormat);
        myViewHolder.transactionListHolder.txnCardAmount.setText(amount);
        myViewHolder.transactionListHolder.txnId.setText(id);
        myViewHolder.transactionListHolder.txnRemarks.setText(remarks);
        myViewHolder.transactionListHolder.txnRemarks2.setText(remarks2);
        if (position == expandPos) {
            myViewHolder.transactionListHolder.txnId.setVisibility(View.VISIBLE);
            myViewHolder.transactionListHolder.txnRemarks.setVisibility(View.VISIBLE);
            myViewHolder.transactionListHolder.txnRemarks2.setVisibility(View.VISIBLE);
        } else {
            myViewHolder.transactionListHolder.txnId.setVisibility(View.GONE);
            myViewHolder.transactionListHolder.txnRemarks.setVisibility(View.GONE);
            myViewHolder.transactionListHolder.txnRemarks2.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return transactionDataList.size();
    }

    private static class TransactionListHolder {
        TextView txnCardName, txnCardId, txnCardDate, txnCardAmount, txnId, txnRemarks, txnStatus, txnRemarks2;
    }
}
