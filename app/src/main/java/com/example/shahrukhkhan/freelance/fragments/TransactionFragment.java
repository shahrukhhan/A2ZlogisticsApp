package com.example.shahrukhkhan.freelance.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.shahrukhkhan.freelance.activities.TransactionActivity;
import com.example.shahrukhkhan.freelance.adapter.TransactionAdapter;
import com.example.shahrukhkhan.freelance.database.LocalDB;
import com.example.shahrukhkhan.freelance.interfaces.ListClickListener;
import com.example.shahrukhkhan.freelance.model.TransactionData;
import com.example.shahrukhkhan.freelance.R;
import com.example.shahrukhkhan.freelance.utils.Constants;
import com.example.shahrukhkhan.freelance.utils.MyVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TransactionFragment extends Fragment implements ListClickListener {

    private List<String> pendingTransactions;
    private List<TransactionData> transactionDataList = new ArrayList<>();
    private RecyclerView recyclerView;
    TransactionAdapter transactionAdapter;
    TransactionActivity transactionActivity;
    private static int count = 0;
    private int type;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public TransactionFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);
        transactionActivity = (TransactionActivity) getActivity();
        Bundle args = getArguments();
        type = args.getInt("type");
        recyclerView = view.findViewById(R.id.transaction_recycler_view);
        preferences = PreferenceManager.getDefaultSharedPreferences(transactionActivity.getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        if (preferences.getInt(Constants.LOGIN, -1) == 1) {
            editor = preferences.edit();
            editor.putInt(Constants.LOGIN, 0);
            editor.apply();
            fetchAllTransactions();
        } else {
            test();
        }
        return view;
    }

    public void test() {
        pendingTransactions = LocalDB.getmInstance(getActivity().getApplicationContext()).getPendingTransactionsId();
        if (pendingTransactions.size() > 0) {
            for (String pendingTransaction : pendingTransactions) {
                fetchTransactions(pendingTransaction);
            }
        } else {
            populateList();
        }
    }

    public void fetchTransactions(String transactionId) {
        String url = Constants.API_URL + "/api/GetTransactions/" + transactionId;
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                TransactionData transactionData = new TransactionData();
                try {
                    transactionData.setTxnId(response.getString("TransactionID"));
                    transactionData.setCardId(response.getString("CardID"));
                    transactionData.setUserID(response.getString("Username"));
                    transactionData.setCardName(response.getString("CardType"));
                    transactionData.setCardAmount(response.getInt("Amount"));
                    transactionData.setCardStatus(response.getInt("Status"));
                    transactionData.setCardTimeStamp(response.getString("TimeStamp"));
                    transactionData.setCardRemarks(response.getString("Remarks"));
                    transactionData.setTxnType(response.getString("Type"));
                    transactionData.setVehicleNumber(response.getString("VehicleNumber"));
                    LocalDB.getmInstance(getActivity().getApplicationContext()).putData(transactionData);
                    count += 1;
                    if (count == pendingTransactions.size()) {
                        populateList();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
        MyVolley.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    public void populateList() {
        count = 0;
        transactionDataList.clear();
        if (transactionActivity.activityType == R.id.cards_usage_icon)
            transactionDataList = LocalDB.getmInstance(getActivity().getApplicationContext()).getCardData(transactionActivity.number, type);
        else
            transactionDataList = LocalDB.getmInstance(getActivity().getApplicationContext()).getData(type);
        transactionAdapter = new TransactionAdapter(transactionDataList, this, transactionActivity);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.setAdapter(transactionAdapter);
                transactionAdapter.notifyDataSetChanged();
            }
        }, 1000);
    }

    private void fetchAllTransactions() {
        String url = Constants.API_URL + "/api/GetTransactions";
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(transactionActivity.getApplicationContext());
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    TransactionData transactionData = new TransactionData();
                    try {
                        JSONObject transaction = response.getJSONObject(i);
                        transactionData.setTxnId(transaction.getString("TransactionID"));
                        transactionData.setCardId(transaction.getString("CardID"));
                        transactionData.setUserID(transaction.getString("Username"));
                        transactionData.setCardName(transaction.getString("CardType"));
                        transactionData.setCardAmount(transaction.getInt("Amount"));
                        transactionData.setCardStatus(transaction.getInt("Status"));
                        transactionData.setCardTimeStamp(transaction.getString("TimeStamp"));
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
