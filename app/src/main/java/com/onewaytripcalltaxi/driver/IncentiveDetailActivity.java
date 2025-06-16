package com.onewaytripcalltaxi.driver;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onewaytripcalltaxi.driver.data.CommonData;
import com.onewaytripcalltaxi.driver.interfaces.APIResult;
import com.onewaytripcalltaxi.driver.service.APIService_Retrofit_JSON;
import com.onewaytripcalltaxi.driver.utils.CL;
import com.onewaytripcalltaxi.driver.utils.Colorchange;
import com.onewaytripcalltaxi.driver.utils.FontHelper;
import com.onewaytripcalltaxi.driver.utils.NC;
import com.onewaytripcalltaxi.driver.utils.SessionSave;
import com.onewaytripcalltaxi.driver.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class IncentiveDetailActivity extends MainActivity {
    private TextView back_btn, title_Txt, inc_amtTxt, inc_enddate_Txt, avail_lbl, complete_lbl, rating_lbl, accept_lbl, avail_Txt, complete_Txt, rating_Txt, accpt_Txt, statusTxt, incentive_content, finalres_Txt, finalstatus_Txt;
    private String incentive_id = "";
    private static Typeface ContenttypeFace;
    private LinearLayout secondlay;


    @Override
    public int setLayout() {
        setLocale();
        return R.layout.incentive_detail_lay;
    }

    @Override
    public void Initialize() {
        CommonData.mActivitylist.add(this);
        CommonData.sContext = this;
        CommonData.current_act = "Incentivedetail";

        Colorchange.ChangeColor((ViewGroup) (((ViewGroup) IncentiveDetailActivity.this
                .findViewById(android.R.id.content)).getChildAt(0)), IncentiveDetailActivity.this);

        FontHelper.applyFont(this, findViewById(R.id.slide_lay));
        back_btn = findViewById(R.id.back_btn);
        title_Txt = findViewById(R.id.title_Txt);
        inc_amtTxt = findViewById(R.id.inc_amtTxt);
        inc_enddate_Txt = findViewById(R.id.inc_enddate_Txt);
        avail_lbl = findViewById(R.id.avail_lbl);
        complete_lbl = findViewById(R.id.complete_lbl);
        rating_lbl = findViewById(R.id.rating_lbl);
        accept_lbl = findViewById(R.id.accept_lbl);
        title_Txt.setTypeface(setcontentTypeface(), Typeface.BOLD);


        avail_Txt = findViewById(R.id.avail_Txt);
        complete_Txt = findViewById(R.id.complete_Txt);
        rating_Txt = findViewById(R.id.rating_Txt);
        accpt_Txt = findViewById(R.id.accpt_Txt);
        statusTxt = findViewById(R.id.statusTxt);

        incentive_content = findViewById(R.id.incentive_content);
        finalres_Txt = findViewById(R.id.finalres_Txt);
        finalstatus_Txt = findViewById(R.id.finalstatus_Txt);
        finalres_Txt.setTypeface(setcontentTypeface(), Typeface.BOLD);
        finalstatus_Txt.setTypeface(setcontentTypeface(), Typeface.BOLD);
        incentive_content.setTypeface(setcontentTypeface(), Typeface.BOLD);
        secondlay = findViewById(R.id.secondlay);


        Bundle bun = getIntent().getExtras();
        if (bun != null) {
            incentive_id = bun.getString("incentive_id");

            title_Txt.setText(bun.getString("incentive_name"));
            inc_amtTxt.setText(SessionSave.getSession("site_currency", IncentiveDetailActivity.this) + " " + bun.getString("incentive_amount"));
            inc_enddate_Txt.setText(bun.getString("incentive_enddate"));
            avail_lbl.setText(bun.getString("driver_availability") + " %");
            complete_lbl.setText(bun.getString("trips_range"));
            rating_lbl.setText(">" + bun.getString("driver_rating"));
            accept_lbl.setText(bun.getString("driver_accept") + " %");

            if (bun.getString("future_trip").equalsIgnoreCase("1")) {
                statusTxt.setText(NC.getResources().getString(R.string.notactive));
                statusTxt.setTextColor(CL.getColor(R.color.grey_color_new));
            } else if (bun.getString("future_trip").equalsIgnoreCase("2")) {
                statusTxt.setText(NC.getResources().getString(R.string.past));
                statusTxt.setTextColor(CL.getColor(R.color.pickup_red));
            }else {
                statusTxt.setText(NC.getResources().getString(R.string.active));
                statusTxt.setTextColor(CL.getColor(R.color.marker_green));

            }

        }

        try {
            JSONObject j = new JSONObject();
            j.put("driver_id", SessionSave.getSession("Id", IncentiveDetailActivity.this));
            j.put("incentive_id", incentive_id);

            final String url = "type=driver_statistics_incentive";
            new DetailApi(url, j);
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

    public Typeface setcontentTypeface() {

        if (ContenttypeFace == null)
            ContenttypeFace = Typeface.createFromAsset(IncentiveDetailActivity.this.getAssets(), FontHelper.FONT_TYPEFACE);
        return ContenttypeFace;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private class DetailApi implements APIResult {
        public DetailApi(String url, JSONObject data) {
            if (isOnline()) {
                new APIService_Retrofit_JSON(IncentiveDetailActivity.this, this, data, false).execute(url);
            } else {
                dialog1 = Utils.alert_view(IncentiveDetailActivity.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, IncentiveDetailActivity.this, "");
            }
        }

        @Override
        public void getResult(final boolean isSuccess, final String result) {
            if (isSuccess) {
                try {
                    final JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {

                        Object item = json.get("driver_statistics");
                        if (item instanceof JSONArray) {
                            secondlay.setVisibility(View.GONE);
                        } else {
                            JSONObject valuejson = json.getJSONObject("driver_statistics");
                            avail_Txt.setText(valuejson.getString("driver_avality") + " %");
                            complete_Txt.setText(valuejson.getString("completed_trip_all"));
                            rating_Txt.setText(valuejson.getString("avg_rating"));
                            accpt_Txt.setText(valuejson.getString("driver_accept_range") + " %");
                            secondlay.setVisibility(View.VISIBLE);


                            if (valuejson.getString("status").equalsIgnoreCase("1")) {
                                incentive_content.setVisibility(View.VISIBLE);
                                finalstatus_Txt.setText(NC.getResources().getString(R.string.successful));
                                finalstatus_Txt.setTextColor(CL.getColor(R.color.marker_green));
                            }else if (valuejson.getString("status").equalsIgnoreCase("2")) {
                                incentive_content.setVisibility(View.GONE);
                                finalstatus_Txt.setText(NC.getResources().getString(R.string.try_next_time));
                                finalstatus_Txt.setTextColor(CL.getColor(R.color.pickup_red));
                            }else {
                                incentive_content.setVisibility(View.GONE);
                                finalstatus_Txt.setText(NC.getResources().getString(R.string.inprogress));
                                finalstatus_Txt.setTextColor(CL.getColor(R.color.new_blue_color_map));


                            }

                        }


                    }
                } catch (final JSONException e) {
                    e.printStackTrace();
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
