package com.a2zlogistics.logisa2z.a2zapp.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.a2zlogistics.logisa2z.a2zapp.R;
import com.a2zlogistics.logisa2z.a2zapp.activities.TransactionActivity;
import com.a2zlogistics.logisa2z.a2zapp.adapter.TransactionAdapter;
import com.a2zlogistics.logisa2z.a2zapp.database.LocalDB;
import com.a2zlogistics.logisa2z.a2zapp.interfaces.ListClickListener;
import com.a2zlogistics.logisa2z.a2zapp.model.TransactionData;
import com.a2zlogistics.logisa2z.a2zapp.utils.Constants;
import com.a2zlogistics.logisa2z.a2zapp.utils.MyVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class TransactionFragment extends Fragment implements ListClickListener {

    private List<TransactionData> transactionDataList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView noTransText;
    TransactionAdapter transactionAdapter;
    TransactionActivity transactionActivity;
    private int type;
    private static int isCreated = 0;

    public TransactionFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isCreated = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);
        Bundle args = getArguments();
        type = args.getInt("type");
        transactionActivity = (TransactionActivity) getActivity();
        noTransText = view.findViewById(R.id.no_trans_text);
        recyclerView = view.findViewById(R.id.transaction_recycler_view);
        progressBar = view.findViewById(R.id.frag_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(transactionActivity, LinearLayoutManager.VERTICAL));
        if (isCreated <= 1) {
            isCreated += 1;
            String date = LocalDB.getmInstance(transactionActivity.getApplicationContext()).getLatestTransactionDate();
            fetchLatestTransactions(date);
        } else {
            populateList();
        }
        return view;
    }

    public void populateList() {
        transactionDataList.clear();
        String from = transactionActivity.fromDate.getText().toString();
        String to = transactionActivity.toDate.getText().toString();
        if (transactionActivity.activityType == R.id.cards_usage_icon)
            transactionDataList = LocalDB.getmInstance(transactionActivity.getApplicationContext()).getCardData(transactionActivity.number, type, from, to);
        else
            transactionDataList = LocalDB.getmInstance(transactionActivity.getApplicationContext()).getData(type, from, to);
        if(transactionDataList.isEmpty())
            noTransText.setVisibility(View.VISIBLE);
        else
            noTransText.setVisibility(View.GONE);
        transactionAdapter = new TransactionAdapter(transactionDataList, this, transactionActivity);
        progressBar.setVisibility(View.GONE);
        recyclerView.setAdapter(transactionAdapter);
        transactionAdapter.notifyDataSetChanged();
    }

    public void fetchLatestTransactions(String strDate) {
        String latestDate = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        Date date = null;
        try {
            date = sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        latestDate = formatter.format(date);
        String url = Constants.API_URL + "/api/GetTransactions?date=" + latestDate;
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(transactionActivity.getApplicationContext());
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressBar.setVisibility(View.GONE);
                for (int i = 0; i < response.length(); i++) {
                    TransactionData transactionData = new TransactionData();
                    try {
                        JSONObject transaction = response.getJSONObject(i);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        Date date = null;
                        try {
                            date = sdf.parse(transaction.getString("TimeStamp"));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        transactionData.setTxnId(transaction.getString("TransactionID"));
                        transactionData.setCardId(transaction.getString("CardID"));
                        transactionData.setUserID(transaction.getString("Username"));
                        transactionData.setCardName(transaction.getString("CardType"));
                        transactionData.setCardAmount(transaction.getInt("Amount"));
                        transactionData.setCardStatus(transaction.getInt("Status"));
                        transactionData.setCardTimeStamp(date.getTime());
                        transactionData.setCardRemarks(transaction.getString("Remarks"));
                        transactionData.setTxnType(transaction.getString("Type"));
                        transactionData.setVehicleNumber(transaction.getString("VehicleNumber"));
                        LocalDB.getmInstance(transactionActivity.getApplicationContext()).putData(transactionData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                populateList();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    Toast.makeText(transactionActivity.getApplicationContext(),
                            getString(R.string.network_error),
                            Toast.LENGTH_LONG).show();
                } else {
                    NetworkResponse resp = error.networkResponse;
                    if (resp != null && resp.data != null && resp.statusCode != 0) {
                        switch (resp.statusCode) {
                            // handle bad request
                            case 400:
                                Toast.makeText(transactionActivity.getApplicationContext(),
                                        getString(R.string.bad_request),
                                        Toast.LENGTH_LONG).show();
                                break;
                            case 401:
                                break;
                            default:
                                Toast.makeText(transactionActivity.getApplicationContext(),
                                        getString(R.string.server_error),
                                        Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
        ) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                Map headers = new HashMap();
                headers.put("Authorization", "Bearer " + preferences.getString(Constants.TOKEN, ""));
                return headers;
            }
        };
        MyVolley.getInstance(transactionActivity.getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }

    @Override
    public void onItemClick(int position, int activityType) {
        transactionAdapter.notifyDataSetChanged();
    }
}
