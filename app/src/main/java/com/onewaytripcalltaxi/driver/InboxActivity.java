package com.onewaytripcalltaxi.driver;

import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.onewaytripcalltaxi.driver.adapter.InboxListAdapter;
import com.onewaytripcalltaxi.driver.data.CommonData;
import com.onewaytripcalltaxi.driver.data.InboxData;
import com.onewaytripcalltaxi.driver.interfaces.APIResult;
import com.onewaytripcalltaxi.driver.service.APIService_Retrofit_JSON;
import com.onewaytripcalltaxi.driver.utils.Colorchange;
import com.onewaytripcalltaxi.driver.utils.FontHelper;
import com.onewaytripcalltaxi.driver.utils.NC;
import com.onewaytripcalltaxi.driver.utils.NetworkStatus;
import com.onewaytripcalltaxi.driver.utils.SessionSave;
import com.onewaytripcalltaxi.driver.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InboxActivity extends MainActivity{

    private RecyclerView inbox_recyclerView;
    private TextView no_data,back_btn;
    int start = 0;
    private int limit = 10;
    private LinearLayoutManager mLayoutManager;
    private final List<InboxData> pastData = new ArrayList<>();
    Dialog loadingDialog;
    private int prevLimt;

    InboxListAdapter inbox_list_adapter;




    @Override
    public int setLayout() {
        setLocale();
        return R.layout.inbox_lay;
    }

    @Override
    public void Initialize() {

        CommonData.mActivitylist.add(this);
        CommonData.sContext = this;
        CommonData.current_act = "Inbox";

        Colorchange.ChangeColor((ViewGroup) (((ViewGroup) InboxActivity.this
                .findViewById(android.R.id.content)).getChildAt(0)), InboxActivity.this);

        FontHelper.applyFont(this, findViewById(R.id.slide_lay));

        inbox_recyclerView=findViewById(R.id.inbox_recyclerView);
        mLayoutManager = new LinearLayoutManager(InboxActivity.this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        inbox_recyclerView.setLayoutManager(mLayoutManager);
        no_data = findViewById(R.id.nodataTxt);

        back_btn=findViewById(R.id.back_btn);



        try {
            JSONObject j = new JSONObject();
            //j.put("phone", SessionSave.getSession("country_code", InboxActivity.this)+SessionSave.getSession("phone_number", InboxActivity.this));
          //  j.put("phone", "902009020");
            j.put("phone", SessionSave.getSession("phone_number", InboxActivity.this));
            j.put("offset", start);
            j.put("limit", limit);
            j.put("user_type", "D");

            final String url = "type=notifications_list";
            new callMessage(url, j);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        inbox_recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    int visibleItemCount = mLayoutManager.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    int pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
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
                                //j.put("driver_id", SessionSave.getSession("Id", InboxActivity.this));
                               // j.put("phone", "902009020");
                                j.put("phone", SessionSave.getSession("phone_number", InboxActivity.this));

                                j.put("start", start);
                                j.put("limit", limit);
                                j.put("user_type", "D");

                                final String url = "type=notifications_list";
                                new callMessage(url, j);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private class callMessage implements APIResult {
        public callMessage(String url, JSONObject data) {
            if (isOnline()) {
                new APIService_Retrofit_JSON(InboxActivity.this, this, data, false).execute(url);
            } else {
                dialog1 = Utils.alert_view(InboxActivity.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, InboxActivity.this, "");
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

                        JSONArray mWalletArray = json.getJSONArray("detail");
                        for (int i = 0; i < mWalletArray.length(); i++) {
                            JSONObject mJsonObject = mWalletArray.getJSONObject(i);
                            InboxData minboxData = new InboxData();
                            minboxData.setMessage(mJsonObject.getString("message"));
                            minboxData.setTime(mJsonObject.getString("time"));
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
                        inbox_list_adapter = new InboxListAdapter(InboxActivity.this, pastData);
                        inbox_recyclerView.setAdapter(inbox_list_adapter);
                        inbox_list_adapter.notifyDataSetChanged();
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


    public void showDialog() {
        try {
            if (NetworkStatus.isOnline(InboxActivity.this)) {
                if (loadingDialog != null && loadingDialog.isShowing())
                    loadingDialog.dismiss();
                View view = View.inflate(InboxActivity.this, R.layout.progress_bar, null);
                loadingDialog = new Dialog(InboxActivity.this, R.style.dialogwinddow);
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
