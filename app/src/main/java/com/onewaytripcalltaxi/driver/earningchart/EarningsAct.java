package com.onewaytripcalltaxi.driver.earningchart;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.onewaytripcalltaxi.driver.BaseActivity;
import com.onewaytripcalltaxi.driver.InboxActivity;
import com.onewaytripcalltaxi.driver.MeAct;
import com.onewaytripcalltaxi.driver.MyApplication;
import com.onewaytripcalltaxi.driver.MyStatus;
import com.onewaytripcalltaxi.driver.OngoingAct;
import com.onewaytripcalltaxi.driver.OnmywayActivity;
import com.onewaytripcalltaxi.driver.R;
import com.onewaytripcalltaxi.driver.SettlementHistoryActivity;
import com.onewaytripcalltaxi.driver.StatementofHistory;
import com.onewaytripcalltaxi.driver.StreetPickUpAct;
import com.onewaytripcalltaxi.driver.TripHistoryAct;
import com.onewaytripcalltaxi.driver.WalletHistory;
import com.onewaytripcalltaxi.driver.WebviewAct;
import com.onewaytripcalltaxi.driver.WithDrawMenuAct;
import com.onewaytripcalltaxi.driver.adapter.IncentiveAdapter;
import com.onewaytripcalltaxi.driver.data.CommonData;
import com.onewaytripcalltaxi.driver.data.IncentiveData;
import com.onewaytripcalltaxi.driver.data.apiData.ApiRequestData;
import com.onewaytripcalltaxi.driver.earningchart.dateadapter.DateListAdapter;
import com.onewaytripcalltaxi.driver.interfaces.ClickInterface;
import com.onewaytripcalltaxi.driver.service.CoreClient;
import com.onewaytripcalltaxi.driver.service.NonActivity;
import com.onewaytripcalltaxi.driver.service.RetrofitCallbackClass;
import com.onewaytripcalltaxi.driver.service.ServiceGenerator;
import com.onewaytripcalltaxi.driver.utils.CL;
import com.onewaytripcalltaxi.driver.utils.CToast;
import com.onewaytripcalltaxi.driver.utils.Colorchange;
import com.onewaytripcalltaxi.driver.utils.FontHelper;
import com.onewaytripcalltaxi.driver.utils.NC;
import com.onewaytripcalltaxi.driver.utils.NetworkStatus;
import com.onewaytripcalltaxi.driver.utils.SessionSave;
import com.onewaytripcalltaxi.driver.utils.Systems;
import com.onewaytripcalltaxi.driver.utils.Utils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetSequence;
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.FullscreenPromptBackground;


/**
 * This class is used to show driver earning details based on date
 */
public class EarningsAct extends BaseActivity implements ClickInterface {

    private final int REQUEST_READ_PHONE_STATE = 292;
    BarChart mChart;
    ArrayList<BarEntry> yVals1;
    ArrayList<BarEntry> xVals1;
    Earningresponse data;
    TextView trips, eAmt, weekAmt, tripHist, wek_txt,amt_onmyway;
    String checked = "OUT";
    NonActivity nonactiityobj = new NonActivity();
    private LinearLayout date, uiLay;
    private ImageView chart_loading;
    private LinearLayout home_lay, earnings_lay, profile_lay, streetpick_lay,track_now_lay,inbox_lay,onmyway_lay;
    private ImageView earnings_iv;
    private TextView badge_notification;
    private RelativeLayout triphistory_lay;
    private ScrollView earnings_layout;
    private NetworkStatus networkStatus;
    private Dialog errorDialog;
    int position = 0;
    private TextView btn_withdraw, btn_settlement,total_earnings ,txt_recharge_link, wallet_amount,btnWithdrawHistory,earnings_amount,btn_report_list,nodataTxt,showall_Txt;
    private MaterialTapTargetSequence mTapTarget;
    private RecyclerView incentive_recyclerView;
     List list_x_axis_name = new ArrayList<String>();
    RecyclerView dateRecyclerView;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    DateListAdapter adapter;

    // Linear Layout Manager
    LinearLayoutManager HorizontalLayout;

    View ChildView;
    int RecyclerViewItemPosition;

    LinearLayout new_recharge_lay,new_withdraw_lay,new_statement_lay,new_settlement_lay;

    private Dialog dialog1;
    private LinearLayout layout_earnings_items;
    private int bank_id_status;
    private final List<IncentiveData> pastData = new ArrayList<>();
    private IncentiveAdapter incentive_adapter;
    private LinearLayoutManager mLayoutManager;
    ArrayList<String> mDateArray = new ArrayList<>();
CardView back_trip_details;
    TextView date_txt_main;
          ImageView  next, pre;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_lay);
        NetworkStatus.appContext = this;
        networkStatus = new NetworkStatus();
        registerReceiver(networkStatus, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        initalize();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(networkStatus);
        if (mTapTarget != null) {
            mTapTarget.dismiss();
        }
        if (dialog1 != null)
            Utils.closeDialog(dialog1);

        super.onDestroy();
    }

    public void initalize() {


        layout_earnings_items = findViewById(R.id.layout_earnings_items);
        mChart = findViewById(R.id.chart1);
        date = findViewById(R.id.dates);

        trips = findViewById(R.id.trips);
        eAmt = findViewById(R.id.amt);
        amt_onmyway=findViewById(R.id.amt_onmyway);
        wek_txt = findViewById(R.id.wek_txt);
        //  FontHelper.applyFont(this, findViewById(R.id.chartact));
        earnings_layout = findViewById(R.id.earnings_layout);
        earnings_iv = findViewById(R.id.earnings_iv);
        earnings_iv.setImageResource(R.drawable.earnings_focus);
        home_lay = findViewById(R.id.home_lay);
        earnings_lay = findViewById(R.id.earnings_lay);
        profile_lay = findViewById(R.id.profile_lay);
        streetpick_lay = findViewById(R.id.streetpick_lay);
        triphistory_lay = findViewById(R.id.triphistory_lay);
        uiLay = findViewById(R.id.uilay);
        weekAmt = findViewById(R.id.week_amt);
        tripHist = findViewById(R.id.trip_history);
        chart_loading = findViewById(R.id.charlay_loading);
        btn_withdraw = findViewById(R.id.btn_withdraw);
        btn_settlement = findViewById(R.id.btn_settlement);
        total_earnings = findViewById(R.id.total_earnings);
        txt_recharge_link = findViewById(R.id.txt_recharge_link);
        wallet_amount = findViewById(R.id.wallet_amount);
        btnWithdrawHistory = findViewById(R.id.btn_withdraw_history);
        track_now_lay = findViewById(R.id.track_now_lay);
        inbox_lay=findViewById(R.id.inbox_lay);
        onmyway_lay=findViewById(R.id.onmyway_lay);
        earnings_amount=findViewById(R.id.earnings_amount);
        btn_report_list=findViewById(R.id.btn_report_list);
        nodataTxt=findViewById(R.id.nodataTxt);

        new_recharge_lay=findViewById(R.id.new_recharge_lay);
        new_withdraw_lay=findViewById(R.id.new_withdraw_lay);
        new_statement_lay=findViewById(R.id.new_statement_lay);
        new_settlement_lay=findViewById(R.id.new_settlement_lay);

        mLayoutManager = new LinearLayoutManager(EarningsAct.this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        badge_notification=findViewById(R.id.badge_notification);
        back_trip_details=findViewById(R.id.back_trip_details);
        date_txt_main = findViewById(R.id.date_txt_main);


        next = findViewById(R.id.next);
        pre = findViewById(R.id.pre);

        pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position >= 1) {
                    position = position - 1;
                    date_txt_main.setText(mDateArray.get(position).toString());

                    int i = position;

                    if (i == 0) {
                        wek_txt.setText(NC.getResources().getString(R.string.Week));
                    } else {
                        wek_txt.setText(NC.getResources().getString(R.string.Selected_Week));
                    }
                    RoundedBarChartRenderer barChartRender = new RoundedBarChartRenderer(mChart, mChart.getAnimator(), mChart.getViewPortHandler());
                    barChartRender.setRadius(30);
                    mChart.setRenderer(barChartRender);
                    mChart.notifyDataSetChanged();
                    weekAmt.setText(SessionSave.getSession("site_currency", EarningsAct.this) + " " + data.weekly_earnings.get(i).this_week_earnings);

                    date.invalidate();
                    setmChart(i);

                }
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position < mDateArray.size() - 1) {
                    position = position + 1;
                    date_txt_main.setText(mDateArray.get(position).toString());


                    int i = position;

                    if (i == 0) {
                        wek_txt.setText(NC.getResources().getString(R.string.Week));
                    } else {
                        wek_txt.setText(NC.getResources().getString(R.string.Selected_Week));
                    }
                    RoundedBarChartRenderer barChartRender = new RoundedBarChartRenderer(mChart, mChart.getAnimator(), mChart.getViewPortHandler());
                    barChartRender.setRadius(30);
                    mChart.setRenderer(barChartRender);
                    mChart.notifyDataSetChanged();
                    weekAmt.setText(SessionSave.getSession("site_currency", EarningsAct.this) + " " + data.weekly_earnings.get(i).this_week_earnings);

                    date.invalidate();
                    setmChart(i);
                }
            }
        });
        date_txt_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        dateRecyclerView = (RecyclerView) findViewById(R.id.dateRecyclerview);
        RecyclerViewLayoutManager
                = new LinearLayoutManager(
                getApplicationContext());

        // Set LayoutManager on Recycler View
        dateRecyclerView.setLayoutManager(
                RecyclerViewLayoutManager);
        adapter = new DateListAdapter(mDateArray);
        HorizontalLayout
                = new LinearLayoutManager(
                EarningsAct.this,
                LinearLayoutManager.HORIZONTAL,
                false);
        dateRecyclerView.setLayoutManager(HorizontalLayout);

        // Set adapter on recycler view
        dateRecyclerView.setAdapter(adapter);

        new_recharge_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(EarningsAct.this, WebviewAct.class);

                in.putExtra("type", "1");
                in.putExtra(CommonData.IS_FROM_EARNINGS, true);
                startActivity(in);
            }
        });
        new_withdraw_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(EarningsAct.this, WithDrawMenuAct.class);
                startActivity(in);
            }
        });
        new_settlement_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //SettlementHistoryActivity
                Intent in = new Intent(EarningsAct.this, SettlementHistoryActivity.class);
                startActivity(in);
            }
        });
        new_statement_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(EarningsAct.this, StatementofHistory.class);
                startActivity(in);
            }
        });
        back_trip_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        if (SessionSave.getSession("admin_on_my_way_enabled_status",EarningsAct.this).equalsIgnoreCase("1"))
        {
            onmyway_lay.setVisibility(View.VISIBLE);
        } else {
            onmyway_lay.setVisibility(View.GONE);
        }
        btn_withdraw.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//                if (bank_id_status==0) {
//                    dialog1 = Utils.alert_view(EarningsAct.this, "" + NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.withdraw_message), NC.getResources().getString(R.string.ok),
//                            "", true, EarningsAct.this, "");
//                }else{
                    Intent in = new Intent(EarningsAct.this, WithDrawMenuAct.class);
                    startActivity(in);
               // }


            }
        });

//

        inbox_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent deleteAcc = new Intent(EarningsAct.this, InboxActivity.class);
                startActivity(deleteAcc);
            }
        });
        onmyway_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent deleteAcc = new Intent(EarningsAct.this, OnmywayActivity.class);
                startActivity(deleteAcc);
            }
        });

        btn_settlement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EarningsAct.this, SettlementHistoryActivity.class));
            }
        });

        txt_recharge_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(EarningsAct.this, WebviewAct.class);

                in.putExtra("type", "1");
                in.putExtra(CommonData.IS_FROM_EARNINGS, true);
                startActivity(in);
            }
        });
        track_now_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!SessionSave.getSession("trip_id", EarningsAct.this).equals("")) {
                    Intent in = new Intent(EarningsAct.this, OngoingAct.class);
                    startActivity(in);
                } else {
                    CToast.ShowToast(EarningsAct.this, NC.getResources().getString(R.string.no_tracking));
                }
            }
        });

        btnWithdrawHistory.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                    Intent in = new Intent(EarningsAct.this, WalletHistory.class);
                    startActivity(in);


            }
        });

        btn_report_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(EarningsAct.this, StatementofHistory.class);
                startActivity(in);
            }
        });

        DrawableImageViewTarget imageViewTarget = new DrawableImageViewTarget(chart_loading);
        Glide.with(EarningsAct.this)
                .load(R.raw.loading_anim)
                .into(imageViewTarget);
        Colorchange.ChangeColor((ViewGroup) (((ViewGroup) EarningsAct.this
                .findViewById(android.R.id.content)).getChildAt(0)), EarningsAct.this);

        Glide.with(this).load(SessionSave.getSession("image_path", this) + "earningsFocus.png").apply(RequestOptions.placeholderOf(R.drawable.earnings_focus).error(R.drawable.earnings_focus)).into((ImageView) findViewById(R.id.earnings_iv));
        setData();


       // mChart.setVisibility(View.INVISIBLE);
        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);
        mChart.getDescription().setEnabled(false);
        mChart.setMaxVisibleValueCount(60);
        mChart.setPinchZoom(false);
        mChart.setDrawGridBackground(false);
        mChart.setDrawValueAboveBar(true);

        Weekaxisformatter xAxisFormatter = new Weekaxisformatter();

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(15f);
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setTextSize(12);
        xAxis.setLabelCount(8, true);

         xAxis.setEnabled(false);


        ValueFormatter custom = new MyAxisValueFormatter(SessionSave.getSession("site_currency", EarningsAct.this));
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setLabelCount(8, true);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextSize(12);
        leftAxis.setTextColor(getResources().getColor(R.color.pickup_red));

        leftAxis.setDrawAxisLine(false);


        YAxis rightAxis = mChart.getAxisRight();
//        rightAxis.setDrawGridLines(false);
//        rightAxis.setGranularityEnabled(true);
//        rightAxis.setGranularity(0.1f);
//        rightAxis.setLabelCount(8, false);
//        rightAxis.setValueFormatter(custom);
//        rightAxis.setSpaceTop(15f);
//        rightAxis.setAxisMinimum(0f);
        rightAxis.setEnabled(false);
        rightAxis.setDrawAxisLine(false);





        XYMarkerView mv = new XYMarkerView(this, xAxisFormatter, SessionSave.getSession("site_currency", EarningsAct.this));
        mv.setChartView(mChart);
        mChart.setMarker(mv);
        mChart.getLegend().setEnabled(false);
        mChart.getAxisLeft().setEnabled(false);
mChart.getAxisRight().setEnabled(false);









        earnings_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Intent intent = new Intent(EarningsAct.this, EarningsAct.class);
                // startActivity(intent);
                //finish();

            }
        });

        home_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(EarningsAct.this, MyStatus.class);
                startActivity(intent);
                // finish();
            }
        });

        profile_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EarningsAct.this, MeAct.class);
                startActivity(intent);
                // finish();
            }
        });

        streetpick_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!SessionSave.getSession("driver_type", EarningsAct.this).equalsIgnoreCase("D")) {
                    if (SessionSave.getSession("trip_id", EarningsAct.this).equals("")) {
                        Intent intent = new Intent(EarningsAct.this, StreetPickUpAct.class);
                        startActivity(intent);
                    } else if (!SessionSave.getSession("trip_id", EarningsAct.this).equals("") && SessionSave.getSession(CommonData.IS_STREET_PICKUP, EarningsAct.this, false)) {
                        Intent intent = new Intent(EarningsAct.this, StreetPickUpAct.class);
                        startActivity(intent);
                    } else {
                        showStreetAlert(NC.getString(R.string.you_are_in_trip));
                    }
                } else {
                    CToast.ShowToast(EarningsAct.this, SessionSave.getSession("account_message", EarningsAct.this));
                }
            }
        });

//        btn_shift.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                btn_shift.setClickable(false);
//                new RequestingCheckBox();
//            }
//        });

        triphistory_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(EarningsAct.this, TripHistoryAct.class);
                startActivity(intent);

            }
        });

    }



    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }





    /**
     * Getting Earnings detail API call and response parsing.
     */
    private void setData() {
        showLoading();
        yVals1 = new ArrayList<>();
        xVals1 = new ArrayList<>();


        CoreClient client = MyApplication.getInstance().getApiManagerWithEncryptBaseUrl();
        ApiRequestData.Earnings request = new ApiRequestData.Earnings();

        request.setDriver_id(SessionSave.getSession("Id", EarningsAct.this));


        Call<Earningresponse> response = client.callData(ServiceGenerator.COMPANY_KEY, request);
        response.enqueue(new RetrofitCallbackClass<>(EarningsAct.this, new Callback<Earningresponse>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @SuppressLint({"SetTextI18n", "ResourceAsColor"})
            @Override
            public void onResponse(Call<Earningresponse> call, Response<Earningresponse> response) {
                closeLoading();

                if (response.isSuccessful() && EarningsAct.this != null) {
                    data = response.body();
                    Systems.out.println("***__" + data);
                    Systems.out.println("running_the_value" + data.min_wallet_amount);

                    if (data != null) {
                        if (data.status == 1) {
                            bank_id_status=data.bank_id_status;
                            SessionSave.saveSession("min_wallet_amount", data.min_wallet_amount, EarningsAct.this);

                            if (data.withdraw_array != null && data.withdraw_array.size() != 0) {
                                if (data.withdraw_array.get(0).wallet_balance != null) {
                                    SessionSave.saveSession("driver_wallet_amount", data.withdraw_array.get(0).driver_wallet_amount, EarningsAct.this);
                                    wallet_amount.setText(SessionSave.getSession("site_currency", EarningsAct.this) + " " + data.withdraw_array.get(0).wallet_balance);

                                }

                                if (data.withdraw_array.get(0).driver_wallet_pending_amount != null)
                                    SessionSave.saveSession("driver_wallet_pending_amount", data.withdraw_array.get(0).driver_wallet_pending_amount, EarningsAct.this);
                                if (data.withdraw_array.get(0).trip_amount != null)
                                    SessionSave.saveSession("trip_amount", data.withdraw_array.get(0).trip_amount, EarningsAct.this);
                                if (data.withdraw_array.get(0).trip_pending_amount != null)
                                    SessionSave.saveSession("trip_pending_amount", data.withdraw_array.get(0).trip_pending_amount, EarningsAct.this);
                                if (data.withdraw_array.get(0).total_amount != null)
                                    SessionSave.saveSession("total_amount", data.withdraw_array.get(0).total_amount, EarningsAct.this);
                                if (data.withdraw_array.get(0).driver_trip_wallet_amount != null)
                                    earnings_amount.setText(SessionSave.getSession("site_currency", EarningsAct.this) + " " + data.withdraw_array.get(0).driver_trip_wallet_amount);
                                if (data.withdraw_array.get(0).driver_incetive_amount != null)
                                    SessionSave.saveSession("incentive_amount", data.withdraw_array.get(0).driver_incetive_amount, EarningsAct.this);
                                if (data.withdraw_array.get(0).driver_incentive_pending_amount != null)
                                    SessionSave.saveSession("incentive_pending_amount", data.withdraw_array.get(0).driver_incentive_pending_amount, EarningsAct.this);


                            }

                            earnings_layout.setVisibility(View.VISIBLE);
                            date.removeAllViews();

                            showTapTargetPrompt();
                            for (int i = 0; i < data.weekly_earnings.size(); i++) {
                                mDateArray.add(data.weekly_earnings.get(i).date_text);
                                final TextView et = new TextView(EarningsAct.this);
                                et.setText(data.weekly_earnings.get(i).date_text + "\t");
                                et.setClickable(true);
                                et.setId(i);
                                et.setPadding(0, 0, 10, 0);
                                et.setTag(i);
                                et.setTextSize(15);
                                et.setTextColor(getResources().getColor(R.color.black));

//                                et.setTextAppearance(R.style.edittext_profile);
                                if (i == 0) {
                                    et.setTextColor(CL.getColor(R.color.app_theme_color));
                                    setmChart(0);

                                }
                                et.setOnClickListener(new View.OnClickListener() {


                                    @RequiresApi(api = Build.VERSION_CODES.M)
                                    @SuppressLint("ResourceAsColor")
                                    @Override
                                    public void onClick(View view) {
//                                        Systems.out.println("daaaaaaaaaaaaaaaaaaaa" + "daAdDAAD");
//                                        int i = (int) view.getTag();
//
//                                        if (i == 0) {
//                                            wek_txt.setText(NC.getResources().getString(R.string.Week));
//                                        } else {
//                                            wek_txt.setText(NC.getResources().getString(R.string.Selected_Week));
//                                        }
//                                        RoundedBarChartRenderer barChartRender = new RoundedBarChartRenderer(mChart,mChart.getAnimator(), mChart.getViewPortHandler());
//                                          barChartRender.setRadius(30);
//                                        mChart.setRenderer(barChartRender);
//                                        mChart.notifyDataSetChanged();
//                                        weekAmt.setText(SessionSave.getSession("site_currency", EarningsAct.this) + " " + data.weekly_earnings.get(i).this_week_earnings);
//                                        for (int j = 0; j < date.getChildCount(); j++) {
//
//                                            TextView v = (TextView) date.getChildAt(j);
//                                            v.setTextAppearance(R.style.edittext_style);
//
//                                        }
//
//                                        ((TextView) view).setTextColor(CL.getColor(R.color.highlightcolor));
//                                        date.invalidate();
//                                        setmChart(i);
                                    }
                                });

                                date.addView(et);


                                try {
                                    //if(data.)
                                    trips.setText(data.today_earnings.get(0).total_trips + " " + NC.getResources().getString(R.string.trips1));
                                    eAmt.setText(NC.getString(R.string.trip_normal)+" : " + SessionSave.getSession("site_currency", EarningsAct.this) + " " + data.today_earnings.get(0).total_amount);

                                    total_earnings.setText(SessionSave.getSession("site_currency", EarningsAct.this) + " " + data.today_earnings.get(0).total_amount);
                                    amt_onmyway.setText(NC.getString(R.string.trip_onmyway)+" : " + SessionSave.getSession("site_currency", EarningsAct.this) + " " + data.today_earnings.get(0).total_amount_on_my_way);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (data.weekly_earnings.get(0).this_week_earnings != null)
                                    weekAmt.setText(SessionSave.getSession("site_currency", EarningsAct.this) + " " + data.weekly_earnings.get(0).this_week_earnings);
                                else
                                    weekAmt.setText(SessionSave.getSession("site_currency", EarningsAct.this) + " 0");
                            }
                            date_txt_main.setText(mDateArray.get(position).toString());
                            adapter.notifyDataSetChanged();

                        } else if (data.status == -1) {
                            CToast.ShowToast(EarningsAct.this, data.message);
                        } else {
                            //CToast.ShowToast(EarningsAct.this, NC.getString(R.string.server_error));
                        }
                    } else {
                       // CToast.ShowToast(EarningsAct.this, NC.getString(R.string.server_error));
                    }

                }


            }

            @Override
            public void onFailure(Call<Earningresponse> call, Throwable t) {
                closeLoading();
                t.printStackTrace();
            }
        }));


    }

    private void closeLoading() {
        chart_loading.setVisibility(View.GONE);
        layout_earnings_items.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        chart_loading.setVisibility(View.VISIBLE);
        layout_earnings_items.setVisibility(View.GONE);
    }

    private void showTapTargetPrompt() {
        if (!SessionSave.getSession(CommonData.SHOW_TOOLTIP, EarningsAct.this, false) && txt_recharge_link != null) {
            txt_recharge_link.post(new Runnable() {
                @Override
                public void run() {

                    mTapTarget = new MaterialTapTargetSequence()
                            .addPrompt(new MaterialTapTargetPrompt.Builder(EarningsAct.this)
                                    .setTarget(txt_recharge_link)
                                    .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                                    .setFocalColour(CL.getResources().getColor(R.color.focal_white))
                                    .setFocalRadius(Float.parseFloat(Integer.toString(txt_recharge_link.getHeight() + wallet_amount.getHeight())))
                                    .setBackgroundColour(CL.getResources().getColor(R.color.tooltip_background))
                                    .setPrimaryText(NC.getString(R.string.wallet_tooltip_text))
                                    .setPrimaryTextColour(CL.getResources().getColor(R.color.white))
                                    .setSecondaryText(NC.getString(R.string.next))
                                    .setSecondaryTextColour(CL.getResources().getColor(R.color.pastbookingcashtext))
                                    .setPromptBackground(new FullscreenPromptBackground()).create(), 4000)
                            .addPrompt(new MaterialTapTargetPrompt.Builder(EarningsAct.this)
                                    .setTarget(btn_withdraw)
                                    .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                                    .setBackgroundColour(CL.getResources().getColor(R.color.tooltip_background))
                                    .setPrimaryText(NC.getString(R.string.withdraw_tooltip_text))
                                    .setSecondaryText(NC.getString(R.string.next))
                                    .setPrimaryTextColour(CL.getResources().getColor(R.color.white))
                                    .setSecondaryTextColour(CL.getResources().getColor(R.color.pastbookingcashtext))
                                    .setFocalPadding(R.dimen.sp_20)
                                    .setPromptBackground(new FullscreenPromptBackground())
                                    .create(), 4000)
                            .addPrompt(new MaterialTapTargetPrompt.Builder(EarningsAct.this)
                                    .setTarget(btn_settlement)
                                    .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                                    .setBackgroundColour(CL.getResources().getColor(R.color.tooltip_background))
                                    .setPrimaryText(NC.getString(R.string.settlement_tooltip_text))
                                    .setSecondaryText(NC.getString(R.string.ok))
                                    .setPrimaryTextColour(CL.getResources().getColor(R.color.white))
                                    .setSecondaryTextColour(CL.getResources().getColor(R.color.pastbookingcashtext))
                                    .setFocalPadding(R.dimen.sp_20)
                                    .setPromptBackground(new FullscreenPromptBackground())
                                    .create(), 4000).show();
                    SessionSave.saveSession(CommonData.SHOW_TOOLTIP, true, EarningsAct.this);

                }
            });
        }
    }

    /**
     * Setting Barchart values
     */
    void setmChart(int x) {

        try {
            mChart.setVisibility(View.VISIBLE);

            yVals1.clear();
            for (int i = 0; i < data.weekly_earnings.get(x).trip_amount.size(); i++) {
                yVals1.add(new BarEntry(i, Float.parseFloat(data.weekly_earnings.get(x).trip_amount.get(i).toString())));
            }
            for (int i = 0; i < data.weekly_earnings.get(x).day_list.size(); i++)
            {

            }

            Weekaxisformatter.mMonths = data.weekly_earnings.get(x).day_list;
            list_x_axis_name = data.weekly_earnings.get(x).day_list;

            System.out.println("aEQWRWAASFFASF"+" "+data.weekly_earnings.get(x).day_list.get(0).toString());

            RoundedBarChartRenderer barChartRender = new RoundedBarChartRenderer(mChart,mChart.getAnimator(), mChart.getViewPortHandler());
            barChartRender.setRadius(30);
            mChart.setRenderer(barChartRender);
            mChart.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        BarDataSet set1;
        set1 = new BarDataSet(yVals1, "");
        set1.setDrawValues(true);
        set1.setColor(Color.parseColor("#93cbfd"));

        set1.setGradientColor(Color.parseColor("#86c4fc"),Color.parseColor("#0d89fa"));



        //    set1.setColors(ColorTemplate.MATERIAL_COLORS);
        ArrayList<IBarDataSet> dataSets;
        dataSets = new ArrayList<>();
        dataSets.add(set1);
        BarData datas = new BarData(dataSets);
        datas.setValueFormatter(new MyAxisValueFormatter(SessionSave.getSession("site_currency", EarningsAct.this) + " "));
        datas.setValueTextSize(15f);
        set1.setGradientColor(Color.parseColor("#86c4fc"),Color.parseColor("#0d89fa"));

        datas.setValueTextColor(getResources().getColor(R.color.app_text_color));

        datas.setBarWidth(0.5f);
        mChart.invalidate();
        mChart.setData(null);
        mChart.setData(datas);
        mChart.setFitBars(false);
        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart .getXAxis().disableGridDashedLine();

        mChart.getXAxis().setDrawLabels(false);
        mChart.invalidate();
        //mChart.setData(datas);


    }

    @Override
    public void positiveButtonClick(DialogInterface dialog, int id, String s) {
        dialog.dismiss();
    }

    @Override
    public void negativeButtonClick(DialogInterface dialog, int id, String s) {
        dialog.dismiss();
    }

    /**
     * To check internet connectivity
     */
    public boolean isOnline() {

        ConnectivityManager connectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (NetworkInfo networkInfo : info)
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    public void showStreetAlert(String message) {
        try {
            if (EarningsAct.this != null) {
                if (errorDialog != null && errorDialog.isShowing())
                    errorDialog.dismiss();
                Systems.out.println("setCanceledOnTouchOutside" + message);
                final View view = View.inflate(EarningsAct.this, R.layout.netcon_lay, null);
                errorDialog = new Dialog(EarningsAct.this, R.style.dialogwinddow);
                errorDialog.setContentView(view);
                errorDialog.setCancelable(false);
                errorDialog.setCanceledOnTouchOutside(false);
                FontHelper.applyFont(EarningsAct.this, errorDialog.findViewById(R.id.alert_id));
                errorDialog.show();
                final TextView title_text = errorDialog.findViewById(R.id.title_text);
                final TextView message_text = errorDialog.findViewById(R.id.message_text);
                final Button button_success = errorDialog.findViewById(R.id.button_success);
                final Button button_failure = errorDialog.findViewById(R.id.button_failure);
                title_text.setText(NC.getResources().getString(R.string.message));
                message_text.setText(message);
                button_success.setText(NC.getResources().getString(R.string.track_now));
                button_failure.setText(NC.getResources().getString(R.string.cancel));
                button_success.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        // TODO Auto-generated method stub

                        errorDialog.dismiss();

                        Intent i = new Intent(EarningsAct.this, OngoingAct.class);
                        startActivity(i);

                    }
                });
                button_failure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        // TODO Auto-generated method stub
                        errorDialog.dismiss();

                    }
                });
            } else {
                try {
                    if (EarningsAct.this != null && errorDialog != null)
                        errorDialog.dismiss();
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(EarningsAct.this, MyStatus.class);
        startActivity(intent);
        finish();
    }

    private void startSOSService() {
        SessionSave.saveSession("sos_id", SessionSave.getSession("Id", EarningsAct.this), EarningsAct.this);
        SessionSave.saveSession("user_type", "d", EarningsAct.this);


    //    startService(new Intent(EarningsAct.this, SOSService.class));
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_READ_PHONE_STATE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startSOSService();
                }
            }
        }
    }

    /**
     * Driver Shift API call and response parsing.
     */


}
