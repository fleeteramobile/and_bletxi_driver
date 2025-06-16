package com.onewaytripcalltaxi.driver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.onewaytripcalltaxi.driver.adapter.SettlementHistoryAdapter;
import com.onewaytripcalltaxi.driver.data.apiData.ApiRequestData;
import com.onewaytripcalltaxi.driver.data.apiData.ListClass;
import com.onewaytripcalltaxi.driver.data.apiData.SettlementHistoryData;
import com.onewaytripcalltaxi.driver.earningchart.EarningsAct;
import com.onewaytripcalltaxi.driver.service.CoreClient;
import com.onewaytripcalltaxi.driver.service.RetrofitCallbackClass;
import com.onewaytripcalltaxi.driver.utils.Colorchange;
import com.onewaytripcalltaxi.driver.utils.NC;
import com.onewaytripcalltaxi.driver.utils.SessionSave;

import java.util.ArrayList;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettlementHistoryActivity extends BaseActivity {

    RecyclerView recyclerView;

    TextView txt_credit_val;

    CardView btn_req;

    SettlementHistoryAdapter mAdapter;

    TextView leftIcon, header_titleTxt, txt_nodata;

    FrameLayout showProgress;

    private final List<ListClass> pastData = new ArrayList<>();

CardView back_menu;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.settlement_history_lay);
        Colorchange.ChangeColor((ViewGroup) (((ViewGroup) SettlementHistoryActivity.this
                .findViewById(android.R.id.content)).getChildAt(0)), SettlementHistoryActivity.this);
        mAdapter = new SettlementHistoryAdapter(SettlementHistoryActivity.this, pastData);

        recyclerView = findViewById(R.id.recycler_view);
        back_menu = findViewById(R.id.back_menu);

        txt_credit_val = findViewById(R.id.txt_credit_val);

        txt_nodata = findViewById(R.id.txt_nodata);

        btn_req = findViewById(R.id.btn_req);

        leftIcon = findViewById(R.id.leftIcon);

        header_titleTxt = findViewById(R.id.header_titleTxt);

        leftIcon.setVisibility(View.VISIBLE);

        header_titleTxt.setText(NC.getString(R.string.settlement_history));

        showProgress = findViewById(R.id.showProgress);

        showProgress.setVisibility(View.VISIBLE);

        LinearLayoutManager ss = new LinearLayoutManager(SettlementHistoryActivity.this);

        ss.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(ss);

        setHistoryDetail();

        recyclerView.setAdapter(mAdapter);

        txt_credit_val.setSelected(true);

        btn_req.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                startActivity(new Intent(SettlementHistoryActivity.this, SettlementDetail.class));

            }

        });

        back_menu.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                onBackPressed();

                finish();

            }

        });

    }

    private void setHistoryDetail() {

//        CoreClient client = new ServiceGenerator(SettlementHistoryActivity.this, false).createService(CoreClient.class);
        CoreClient client = MyApplication.getInstance().getApiManagerWithEncryptBaseUrl();
        ApiRequestData.SettlementHistory apiData = new ApiRequestData.SettlementHistory();

        apiData.driver_id = SessionSave.getSession("Id", SettlementHistoryActivity.this);

        Call<SettlementHistoryData> settlementHistoryDataCall = client.settlement_historyCall(apiData, SessionSave.getSession("Lang", SettlementHistoryActivity.this));

        settlementHistoryDataCall.enqueue(new RetrofitCallbackClass<>(SettlementHistoryActivity.this, new Callback<SettlementHistoryData>() {

            @Override

            public void onResponse(Call<SettlementHistoryData> call, Response<SettlementHistoryData> response) {

                showProgress.setVisibility(View.GONE);

                try {

                    if (response.isSuccessful()) {

                        SettlementHistoryData data = response.body();

                        if (data != null) {

                            if (data.status == 1) {

                                txt_credit_val.setText(" Your Credits : "+" "+SessionSave.getSession("site_currency", SettlementHistoryActivity.this) + data.total_amount_driver);

                                if (data.list == null || data.list.size() == 0) {

                                    recyclerView.setVisibility(View.GONE);

                                    txt_nodata.setVisibility(View.VISIBLE);

                                    txt_nodata.setText(data.message);

                                } else {

                                    recyclerView.setVisibility(View.VISIBLE);

                                    txt_nodata.setVisibility(View.GONE);

                                    pastData.addAll(data.list);

                                    if (mAdapter == null) {

                                        mAdapter = new SettlementHistoryAdapter(SettlementHistoryActivity.this, pastData);

                                        recyclerView.setAdapter(mAdapter);

                                    } else {

                                        mAdapter.notifyDataSetChanged();

                                    }

                                }

                            } else {

                                recyclerView.setVisibility(View.GONE);

                                txt_nodata.setVisibility(View.VISIBLE);

                                txt_nodata.setText(data.message);

                            }

                        }

                    }

                } catch (Exception e) {

                    e.printStackTrace();

                }

            }

            @Override

            public void onFailure(Call<SettlementHistoryData> call, Throwable t) {

                t.printStackTrace();

                showProgress.setVisibility(View.GONE);

            }

        }));

    }

    @Override

    public void onBackPressed() {

        super.onBackPressed();

        startActivity(new Intent(SettlementHistoryActivity.this, EarningsAct.class));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}