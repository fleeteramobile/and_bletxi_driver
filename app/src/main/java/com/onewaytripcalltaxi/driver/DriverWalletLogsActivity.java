package com.onewaytripcalltaxi.driver;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.onewaytripcalltaxi.driver.adapter.DriverWalletLogsAdapter;
import com.onewaytripcalltaxi.driver.interfaces.APIResult;
import com.onewaytripcalltaxi.driver.service.APIService_Retrofit_JSON;
import com.onewaytripcalltaxi.driver.utils.NC;
import com.onewaytripcalltaxi.driver.utils.SessionSave;
import com.onewaytripcalltaxi.driver.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DriverWalletLogsActivity extends MainActivity {

    ArrayList<String> year = new ArrayList<String>();
    ArrayList<String> years = new ArrayList<String>();
    ArrayList<String> genre = new ArrayList<String>();

RecyclerView driver_logs;
    @Override
    public int setLayout() {
        return R.layout.activity_driver_wallet_logs;
    }

    @Override
    public void Initialize() {
        driver_logs = findViewById(R.id.driver_logs);
        try {
            JSONObject j = new JSONObject();
            j.put("driver_id", SessionSave.getSession("Id", DriverWalletLogsActivity.this));


            final String url = "type=driver_report_list";
            new callWalletHistory(url, j);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class callWalletHistory implements APIResult {
        public callWalletHistory(String url, JSONObject data) {
            if (isOnline()) {
                new APIService_Retrofit_JSON(DriverWalletLogsActivity.this, this, data, false).execute(url);
            } else {
                dialog1 = Utils.alert_view(DriverWalletLogsActivity.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, DriverWalletLogsActivity.this, "");
            }
        }

        @Override
        public void getResult(final boolean isSuccess, final String result) {
            if (isSuccess) {

                try {
                    final JSONObject json = new JSONObject(result);
                    JSONObject json2 = json.getJSONObject("detail");
                    JSONArray mWalletArray = json2.getJSONArray("driver_report");


                    for (int i = 0; i < mWalletArray.length(); i++) {
                        JSONObject mJsonObject = mWalletArray.getJSONObject(i);
                       // StatementData mStatementData = new StatementData();

                      //  mStatementData.setCreatedate(mJsonObject.getString("createdate"));
                        genre.add(mJsonObject.getString("description"));

                                String[] separated =  mJsonObject.getString("createdate").split(" ");
                        System.out.println(" result_of_driver_wallet_logs"+ " "+separated[0]);
                        System.out.println(" result_of_driver_wallet_logs"+ " "+separated[1]);
                        year.add(separated[0]);

                    }
                } catch (final JSONException e) {
                    e.printStackTrace();
                } finally {

                    for (String yeas : year) {
                        if (years.contains(yeas)) {
                            years.add("");
                        } else {
                            years.add(yeas);
                        }
                    }
                    DriverWalletLogsAdapter mAdapter = new DriverWalletLogsAdapter(genre,years);

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    driver_logs.setLayoutManager(mLayoutManager);


                    // Set adapter to recyclerView
                    driver_logs.setAdapter(mAdapter);
               //     mAdapter.notifyDataSetChanged();
                }
            } else {
                runOnUiThread(new Runnable() {
                    public void run() {
                        // CToast.ShowToast(WalletHistory.this, getString(R.string.server_error));
                    }
                });
            }
        }
    }


}