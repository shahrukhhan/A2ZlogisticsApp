package com.a2zlogistics.logisa2z.a2zapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a2zlogistics.logisa2z.a2zapp.R;
import com.a2zlogistics.logisa2z.a2zapp.activities.DiscountActivity;
import com.a2zlogistics.logisa2z.a2zapp.model.DiscountData;
import com.a2zlogistics.logisa2z.a2zapp.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Shahrukh Khan on 12/21/2017.
 */

public class DiscountAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<DiscountData> discountDataList;
    private DiscountActivity discountActivity;


    public DiscountAdapter(List<DiscountData> discountDataList, DiscountActivity discountActivity) {
        this.discountDataList = discountDataList;
        this.discountActivity = discountActivity;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private DiscountAdapter.DiscountListHolder discountListHolder;

        public MyViewHolder(View view) {
            super(view);
            discountListHolder = new DiscountAdapter.DiscountListHolder();
            discountListHolder.discountStatus = view.findViewById(R.id.txn_status);
            discountListHolder.discountCardName = view.findViewById(R.id.txn_card_name);
            discountListHolder.discountCardId = view.findViewById(R.id.txn_card_id);
            discountListHolder.discountCardDate = view.findViewById(R.id.txn_date);
            discountListHolder.discountCardAmount = view.findViewById(R.id.txn_amount);
            discountListHolder.discountId = view.findViewById(R.id.txn_id);
            discountListHolder.discountRemarks = view.findViewById(R.id.txn_remarks);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.transaction_item, parent, false);
        viewHolder = new DiscountAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        DiscountData discountData = discountDataList.get(position);
        myViewHolder.discountListHolder.discountStatus.setVisibility(View.GONE);
        String amount = Constants.RS + String.format(Locale.US, "%.2f", discountData.getDiscountAmount());
        Date testDate = new Date(discountData.getTimeStamp());
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        String newFormat = formatter.format(testDate);
        myViewHolder.discountListHolder.discountCardName.setText(discountData.getCardId().toUpperCase());
        myViewHolder.discountListHolder.discountCardId.setVisibility(View.GONE);
        myViewHolder.discountListHolder.discountCardDate.setText(newFormat);
        myViewHolder.discountListHolder.discountCardAmount.setText(amount);
        myViewHolder.discountListHolder.discountId.setVisibility(View.GONE);
        myViewHolder.discountListHolder.discountRemarks.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return discountDataList.size();
    }

    private static class DiscountListHolder {
        TextView discountCardName, discountCardId, discountCardDate, discountCardAmount, discountId, discountRemarks, discountStatus;
    }
}
