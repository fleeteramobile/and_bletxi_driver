package com.onewaytripcalltaxi.driver;

import android.app.Dialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.onewaytripcalltaxi.driver.adapter.IncentiveAdapter_new;
import com.onewaytripcalltaxi.driver.data.CommonData;
import com.onewaytripcalltaxi.driver.data.IncentiveData;
import com.onewaytripcalltaxi.driver.interfaces.APIResult;
import com.onewaytripcalltaxi.driver.service.APIService_Retrofit_JSON;
import com.onewaytripcalltaxi.driver.utils.Colorchange;
import com.onewaytripcalltaxi.driver.utils.FontHelper;
import com.onewaytripcalltaxi.driver.utils.NC;
import com.onewaytripcalltaxi.driver.utils.SessionSave;
import com.onewaytripcalltaxi.driver.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class IncentiveActivity extends MainActivity {

    private RecyclerView in_recyclerView;
    private TextView no_data, back_btn;
    int start = 0;
    private final int limit = 10;
    private LinearLayoutManager mLayoutManager;
    private final List<IncentiveData> pastData = new ArrayList<>();
    Dialog loadingDialog;
    private int prevLimt;
    private IncentiveAdapter_new incentive_adapter;


    @Override
    public int setLayout() {
        setLocale();
        return R.layout.incentive_lay;
    }

    @Override
    public void Initialize() {

        CommonData.mActivitylist.add(this);
        CommonData.sContext = this;
        CommonData.current_act = "Incentive";

        Colorchange.ChangeColor((ViewGroup) (((ViewGroup) IncentiveActivity.this
                .findViewById(android.R.id.content)).getChildAt(0)), IncentiveActivity.this);

        FontHelper.applyFont(this, findViewById(R.id.slide_lay));

        in_recyclerView = findViewById(R.id.in_recyclerView);
        mLayoutManager = new LinearLayoutManager(IncentiveActivity.this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        in_recyclerView.setLayoutManager(mLayoutManager);
        no_data = findViewById(R.id.nodataTxt);

        back_btn = findViewById(R.id.back_btn);


        try {
            JSONObject j = new JSONObject();

            j.put("driver_id", SessionSave.getSession("Id", IncentiveActivity.this));

            final String url = "type=active_incentive_list";
            new Incentive_Data(url, j);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private class Incentive_Data implements APIResult {
        public Incentive_Data(String url, JSONObject data) {
            if (isOnline()) {
                new APIService_Retrofit_JSON(IncentiveActivity.this, this, data, false).execute(url);
            } else {
                dialog1 = Utils.alert_view(IncentiveActivity.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, IncentiveActivity.this, "");
            }
        }

        @Override
        public void getResult(final boolean isSuccess, final String result) {
            if (isSuccess) {
                pastData.clear();
                try {
                    final JSONObject json = new JSONObject(result);

                    //  closeDialog();


                    if (json.getInt("status") == 1) {

                        JSONArray mWalletArray = json.getJSONArray("active_incentive_list");
                        for (int i = 0; i < mWalletArray.length(); i++) {
                            JSONObject mJsonObject = mWalletArray.getJSONObject(i);
                            IncentiveData minboxData = new IncentiveData();
                            minboxData.set_id(mJsonObject.getString("_id"));
                            minboxData.setIncentive_name(mJsonObject.getString("incentive_name"));
                            minboxData.setIncentive_amount(mJsonObject.getString("incentive_amount"));
                            minboxData.setTime_from(mJsonObject.getString("time_from"));
                            minboxData.setTime_to(mJsonObject.getString("time_to"));
                            minboxData.setSp_time_from(mJsonObject.getString("sp_time_from"));
                            minboxData.setSp_time_to(mJsonObject.getString("sp_time_to"));
                            minboxData.setDriver_availability_range(mJsonObject.getString("driver_availability_range"));
                            minboxData.setDriver_rating_range(mJsonObject.getString("driver_rating_range"));
                            minboxData.setDriver_accept_range(mJsonObject.getString("driver_accept_range"));
                            minboxData.setTrips_range(mJsonObject.getString("trips_range"));
                            minboxData.setIs_feature_incentive(mJsonObject.getString("is_feature_incentive"));
                            pastData.add(minboxData);
                        }


                    }

                } catch (final JSONException e) {
                    e.printStackTrace();
                } finally {

                    if (pastData.size() == 0) {
                        no_data.setVisibility(View.VISIBLE);
                    } else {
                        no_data.setVisibility(View.GONE);
                        incentive_adapter = new IncentiveAdapter_new(IncentiveActivity.this, pastData);
                        in_recyclerView.setAdapter(incentive_adapter);
                        incentive_adapter.notifyDataSetChanged();
                    }


                }
            } else {
                runOnUiThread(new Runnable() {
                    public void run() {
                        // CToast.ShowToast(InboxActivity.this, getString(R.string.server_error));
                    }
                });
            }
        }
    }

}
