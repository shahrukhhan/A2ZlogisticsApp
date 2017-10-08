package com.example.shahrukhkhan.freelance.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shahrukhkhan.freelance.interfaces.ListClickListener;
import com.example.shahrukhkhan.freelance.model.CardData;
import com.example.shahrukhkhan.freelance.R;

import java.util.List;

/**
 * Created by Shahrukh Khan on 9/19/2017.
 */

public class CardsAdapter extends BaseAdapter {

    private LayoutInflater layoutinflater;
    private List<CardData> cardList;
    private Context context;
    private int activityType;
    private ListClickListener listClickListener;

    public CardsAdapter(Context context, List<CardData> cardList, int activityType) {
        this.context = context;
        layoutinflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.cardList = cardList;
        this.activityType = activityType;
    }

    @Override
    public int getCount() {
        return cardList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        CardListHolder cardListHolder;
        if (view == null) {
            cardListHolder = new CardListHolder();
            view = layoutinflater.inflate(R.layout.card_item, viewGroup, false);
            cardListHolder.cardName = view.findViewById(R.id.card_name);
            cardListHolder.vehicleNumber = view.findViewById(R.id.card_vehicle_number);
            cardListHolder.cardBalance = view.findViewById(R.id.card_balance);
            cardListHolder.addBalance = view.findViewById(R.id.card_add_balance);
            view.setTag(cardListHolder);
        } else {
            cardListHolder = (CardListHolder) view.getTag();
        }
        if (activityType == R.id.cards_usage_icon) {
            cardListHolder.addBalance.setVisibility(View.INVISIBLE);
        } else {
            cardListHolder.addBalance.setVisibility(View.VISIBLE);
        }
        String name = cardList.get(i).getCardName() + " - " + cardList.get(i).getCardId();
        String vehicle = context.getResources().getString(R.string.vehicle_text) + cardList.get(i).getVehicleNumber();
        String balance = context.getResources().getString(R.string.balance_text) + cardList.get(i).getCardBalance();
        cardListHolder.cardName.setText(name);
        cardListHolder.vehicleNumber.setText(vehicle);
        cardListHolder.cardBalance.setText(balance);
        return view;
    }

    private static class CardListHolder {
        TextView cardName, vehicleNumber, cardBalance;
        ImageView addBalance;
    }
}
