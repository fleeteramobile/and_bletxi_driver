package com.onewaytripcalltaxi.driver;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.onewaytripcalltaxi.driver.interfaces.APIResult;
import com.onewaytripcalltaxi.driver.service.APIService_Retrofit_JSON;
import com.onewaytripcalltaxi.driver.utils.SessionSave;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SettlementLists extends MainActivity {

    ArrayList<String> Date = new ArrayList<String>();
    ArrayList<String> Dates = new ArrayList<String>();
    ArrayList<String> _id = new ArrayList<String>();
    ArrayList<String> driver_id = new ArrayList<String>();
    ArrayList<String> wallet_item = new ArrayList<String>();
    ArrayList<String> description = new ArrayList<String>();
    ArrayList<String> added_amount = new ArrayList<String>();
    ArrayList<String> balance = new ArrayList<String>();

    private Dialog dialog1;


    @Override
    public int setLayout() {
        return R.layout.activity_settlement_lists;
    }

    @Override
    public void Initialize() {
        try {
            JSONObject j = new JSONObject();
            j.put("driver_id", SessionSave.getSession("Id", SettlementLists.this));


            final String url = "type=driver_report_list";
            new callWalletHistory(url, j);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private class callWalletHistory implements APIResult {
        public callWalletHistory(String url, JSONObject data) {


            new APIService_Retrofit_JSON(SettlementLists.this, this, data, false).execute(url);

        }

        @Override
        public void getResult(boolean isSuccess, String result) {
            if (isSuccess) {
                try {
                    final JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {
                        JSONObject json2 = json.getJSONObject("detail");
                        JSONArray mWalletArray = json2.getJSONArray("driver_report");
                        for (int i = 0; i < mWalletArray.length(); i++) {
                            JSONObject mJsonObject = mWalletArray.getJSONObject(i);
                            String string = mJsonObject.getString("createdate");
                            String[] parts = string.split(" ");
                            String part1 = parts[0]; // 004
                            Date.add(part1);
                            _id.add(mJsonObject.getString("_id"));
                            driver_id.add(mJsonObject.getString("driver_id"));
                            wallet_item.add(mJsonObject.getString("wallet_item"));
                            description.add(mJsonObject.getString("description"));
                            added_amount.add(mJsonObject.getString("added_amount"));
                            balance.add(mJsonObject.getString("balance"));

                        }


                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } finally {
                    for (String yeas : Date) {
                        if (Dates.contains(yeas)) {
                            Dates.add("");
                        } else {
                            Dates.add(yeas);
                        }

                    }

                    RecyclerView recyclerView = findViewById(R.id.recycler_view);

                    // Initialise adapter with constructor values
                    ContentAdapter mAdapter = new ContentAdapter(Dates, _id, driver_id, wallet_item, description, added_amount, balance);

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());

                    // Set adapter to recyclerView
                    recyclerView.setAdapter(mAdapter);
                }

            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre, date;

        private ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            genre = view.findViewById(R.id.genre);
            year = view.findViewById(R.id.year);
            date = view.findViewById(R.id.date);
        }
    }

    private class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {


        ArrayList<String> Dates = new ArrayList<String>();
        ArrayList<String> _id = new ArrayList<String>();
        ArrayList<String> driver_id = new ArrayList<String>();
        ArrayList<String> wallet_item = new ArrayList<String>();
        ArrayList<String> _description = new ArrayList<String>();
        ArrayList<String> added_amount = new ArrayList<String>();
        ArrayList<String> _balance = new ArrayList<String>();

        public ContentAdapter(ArrayList<String> dates, ArrayList<String> id, ArrayList<String> driverId, ArrayList<String> walletItem, ArrayList<String> description, ArrayList<String> addedAmount, ArrayList<String> balance) {
            Dates = dates;
            _id = id;
            driver_id = driverId;
            wallet_item = walletItem;
            _description = description;
            added_amount = addedAmount;
            _balance = balance;


        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.settlement_list_items, viewGroup, false);

            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            if(Dates.get(i).isEmpty()){
                viewHolder.year.setVisibility(View.GONE) ;
            }else{
                viewHolder.year.setText(Dates.get(i));
            }
            if(added_amount.get(i).contains("-"))
            {
                viewHolder.title.setText("Debit");
            }
            else {
                viewHolder.title.setText("Credit");
            }


            viewHolder.genre.setText(wallet_item.get(i));
            viewHolder.date.setText(added_amount.get(i));
        }

        @Override
        public int getItemCount() {
            return _description.size();
        }
    }
}