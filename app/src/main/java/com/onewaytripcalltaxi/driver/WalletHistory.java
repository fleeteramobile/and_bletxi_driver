package com.onewaytripcalltaxi.driver;


import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.onewaytripcalltaxi.driver.adapter.WalletHistoryListAdapter;
import com.onewaytripcalltaxi.driver.data.WalletHistoryData;
import com.onewaytripcalltaxi.driver.interfaces.APIResult;
import com.onewaytripcalltaxi.driver.service.APIService_Retrofit_JSON;
import com.onewaytripcalltaxi.driver.utils.NC;
import com.onewaytripcalltaxi.driver.utils.NetworkStatus;
import com.onewaytripcalltaxi.driver.utils.SessionSave;
import com.onewaytripcalltaxi.driver.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WalletHistory extends MainActivity {

    int start = 0;
    WalletHistoryListAdapter past_booking_adapter;
    RecyclerView history_recyclerView;
    private final List<WalletHistoryData> pastData = new ArrayList<>();
    TextView no_data,header_titleTxt1,header_titleTxt2,back_text;
    ImageView filter_icon;
    Dialog loadingDialog;
    private Dialog dialog1;
    private int limit = 10;
    private int prevLimt;


    @Override
    public int setLayout() {
        return R.layout.corporate_list;
    }

    @Override
    public void Initialize() {
        history_recyclerView = findViewById(R.id.corporate_recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        history_recyclerView.setLayoutManager(llm);
        past_booking_adapter = new WalletHistoryListAdapter(WalletHistory.this, pastData);
        history_recyclerView.setAdapter(past_booking_adapter);
        back_text = findViewById(R.id.back_text);

        header_titleTxt1 = findViewById(R.id.header_titleTxt1);
        header_titleTxt2 = findViewById(R.id.header_titleTxt2);
        no_data = findViewById(R.id.nodataTxt);
        filter_icon = findViewById(R.id.filter_icon);
        header_titleTxt1.setVisibility(View.GONE);
        header_titleTxt2.setVisibility(View.VISIBLE);
        filter_icon.setVisibility(View.GONE);

//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
//        params.setMargins(0,0,0,0);
//        header_titleTxt.setGravity(Gravity.CENTER);
//        back_text.setLayoutParams(params);
//        back_text.setGravity(Gravity.CENTER_VERTICAL);
//        header_titleTxt.setLayoutParams(params);
        showDialog();

        try {
            JSONObject j = new JSONObject();
            j.put("driver_id", SessionSave.getSession("Id", WalletHistory.this));
            j.put("start", start);
            j.put("limit", limit);

            final String url = "type=driver_wallet_logs";
            new callWalletHistory(url, j);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        back_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        history_recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) //check for scroll down
                    {
                        int visibleItemCount = llm.getChildCount();
                        int totalItemCount = llm.getItemCount();
                        int pastVisiblesItems = llm.findFirstVisibleItemPosition();
                        Log.v("...", "Last Item Wow !" + visibleItemCount + "___" + pastVisiblesItems + "___" + totalItemCount);


                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            // loading = false;
                            Log.v("...", "Last Item Wow !");
                            //Do pagination.. i.e. fetch new data
                            if (totalItemCount >= 10 && limit >= prevLimt && totalItemCount == limit) {
                                if (start == 0)
                                    start = 11;
                                else
                                    start += 10;
                                prevLimt = limit;
                                limit += 10;
                                try {
                                    JSONObject j = new JSONObject();
                                    j.put("driver_id", SessionSave.getSession("Id", WalletHistory.this));
                                    j.put("start", start);
                                    j.put("limit", limit);

                                    final String url = "type=driver_wallet_logs";
                                    new callWalletHistory(url, j);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
            }
        });



    }


    private class callWalletHistory implements APIResult {
        public callWalletHistory(String url, JSONObject data) {
            if (isOnline()) {
                new APIService_Retrofit_JSON(WalletHistory.this, this, data, false).execute(url);
            } else {
                dialog1 = Utils.alert_view(WalletHistory.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, WalletHistory.this, "");
            }
        }

        @Override
        public void getResult(final boolean isSuccess, final String result) {
            if (isSuccess) {
                pastData.clear();
                try {
                    final JSONObject json = new JSONObject(result);

                    closeDialog();



                    if (json.getInt("status") == 1) {

                        JSONArray mWalletArray = json.getJSONArray("result");
                        for (int i = 0; i < mWalletArray.length(); i++) {
                            JSONObject mJsonObject = mWalletArray.getJSONObject(i);
                            WalletHistoryData mWalletHistoryData = new WalletHistoryData();
                            mWalletHistoryData.setAmt(mJsonObject.getString("amt"));
                            mWalletHistoryData.setComments(mJsonObject.getString("comments"));
                            mWalletHistoryData.setCreated_date(mJsonObject.getString("created_date"));
                            mWalletHistoryData.setSign(mJsonObject.getString("sign"));
                            mWalletHistoryData.setUpdated_balance(mJsonObject.getString("updated_balance"));
                            pastData.add(mWalletHistoryData);
                        }


                    }

                } catch (final JSONException e) {
                    e.printStackTrace();
                } finally {

                    if (pastData.size() == 0) {
                        no_data.setText(NC.getResources().getString(R.string.no_transaction_available));
                        no_data.setVisibility(View.VISIBLE);
                    } else {
                        no_data.setVisibility(View.GONE);
                        past_booking_adapter = new WalletHistoryListAdapter(WalletHistory.this, pastData);
                        history_recyclerView.setAdapter(past_booking_adapter);
                        past_booking_adapter.notifyDataSetChanged();
                    }


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


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    public void showDialog() {
        try {
            if (NetworkStatus.isOnline(WalletHistory.this)) {
                if (loadingDialog != null && loadingDialog.isShowing())
                    loadingDialog.dismiss();
                View view = View.inflate(WalletHistory.this, R.layout.progress_bar, null);
                loadingDialog = new Dialog(WalletHistory.this, R.style.dialogwinddow);
                loadingDialog.setContentView(view);
                loadingDialog.setCancelable(false);
                if (this != null)
                    loadingDialog.show();

                ImageView iv = loadingDialog.findViewById(R.id.giff);
                DrawableImageViewTarget imageViewTarget = new DrawableImageViewTarget(iv);
                Glide.with(this)
                        .load(R.raw.loading_anim)
                        .into(imageViewTarget);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //method to close dialog
    public void closeDialog() {

        try {
            if (loadingDialog != null)
                if (loadingDialog.isShowing())
                    loadingDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
