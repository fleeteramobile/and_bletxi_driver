package com.onewaytripcalltaxi.driver;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.onewaytripcalltaxi.driver.adapter.StatementListAdapter;
import com.onewaytripcalltaxi.driver.data.apiData.StatementData;
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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StatementofHistory extends MainActivity {

    int start = 0;
    StatementListAdapter stat_adapter;
    RecyclerView history_recyclerView;
    private final List<StatementData> statData = new ArrayList<>();
    TextView no_data,header_titleTxt1,header_titleTxt2,back_text,wallet_amount;
    ImageView filter_icon;
    Dialog loadingDialog;
    private Dialog dialog1;
    private final int limit = 10;
    private int prevLimt;

    ImageView callText;

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
        stat_adapter = new StatementListAdapter(StatementofHistory.this, statData);
        history_recyclerView.setAdapter(stat_adapter);

        back_text = findViewById(R.id.back_text);
        header_titleTxt1 = findViewById(R.id.header_titleTxt1);
        header_titleTxt2 = findViewById(R.id.header_titleTxt2);
        callText = findViewById(R.id.callText);
        no_data = findViewById(R.id.nodataTxt);
        wallet_amount = findViewById(R.id.wallet_amount);

        filter_icon=findViewById(R.id.filter_icon);
        header_titleTxt1.setVisibility(View.VISIBLE);
        header_titleTxt2.setVisibility(View.GONE);
        filter_icon.setVisibility(View.VISIBLE);
        showDialog();

        try {
            JSONObject j = new JSONObject();
            j.put("driver_id", SessionSave.getSession("Id", StatementofHistory.this));
           // j.put("start", start);
           // j.put("limit", limit);

            final String url = "type=driver_report_list";
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
        callText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        filter_icon.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                filterDate();
            }
        });

       /* history_recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                                j.put("driver_id", SessionSave.getSession("Id", StatementofHistory.this));
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
        });*/

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void filterDate() {
        try {
            final View view = View.inflate(StatementofHistory.this, R.layout.date_filter_dialog, null);
            final Dialog mcancelDialog = new Dialog(StatementofHistory.this, R.style.dialogwinddow);
            mcancelDialog.setContentView(view);
            mcancelDialog.setCancelable(true);
            mcancelDialog.show();


//            FontHelper.applyFont(StatementofHistory.this, mcancelDialog.findViewById(R.id.alert_id));
//            Colorchange.ChangeColor((ViewGroup) view, StatementofHistory.this);

            final Button button_success = mcancelDialog.findViewById(R.id.okbtn);
            final Button button_failure = mcancelDialog.findViewById(R.id.cancelbtn);

            EditText ToDate = mcancelDialog.findViewById(R.id.ToDate);
            EditText FromDate = mcancelDialog.findViewById(R.id.FromDate);
             SimpleDateFormat dateFormatter;
            String FilterToDate,FilterFromDate;
            ToDate.setInputType(InputType.TYPE_NULL);
            FromDate.setInputType(InputType.TYPE_NULL);
            ToDate.requestFocus();
            dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

            FromDate.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View view) {

                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    Calendar newDate = Calendar.getInstance();
                    DatePickerDialog datePickerDialog = new DatePickerDialog(StatementofHistory.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    //todo
                                    newDate.set(year, month, dayOfMonth);
//                                    FromDate.setText(dateFormatter.format(newDate.getTime()));
                                    TimePickerDialog timePickerDialog = new TimePickerDialog(StatementofHistory.this,
                                            new TimePickerDialog.OnTimeSetListener() {
                                                @Override
                                                public void onTimeSet(TimePicker timePicker, int hour, int minute) {

                                                    newDate.set(Calendar.HOUR_OF_DAY, hour);
                                                    newDate.set(Calendar.MINUTE, minute);

                                                    FromDate.setText(dateFormatter.format(newDate.getTime())+":00");
                                                    
                                                    Log.v(TAG, "The choosen one " + newDate.getTime()+":00");

                                                }
                                            },calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),false);
                                    timePickerDialog.show();
                                }
                            },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.show();

                }
            });

            ToDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    Calendar newDate = Calendar.getInstance();
                    DatePickerDialog datePickerDialog = new DatePickerDialog(StatementofHistory.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    //todo

                                    newDate.set(year, month, dayOfMonth);
//                                    ToDate.setText(dateFormatter.format(newDate.getTime()));

                                    TimePickerDialog timePickerDialog = new TimePickerDialog(StatementofHistory.this,
                                            new TimePickerDialog.OnTimeSetListener() {
                                                @Override
                                                public void onTimeSet(TimePicker timePicker, int hour, int minute) {

                                                    newDate.set(Calendar.HOUR_OF_DAY, hour);
                                                    newDate.set(Calendar.MINUTE, minute);

                                                    ToDate.setText(dateFormatter.format(newDate.getTime())+":00");

                                                    Log.v(TAG, "The choosen one " + newDate.getTime());

                                                }
                                            },calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),false);
                                    timePickerDialog.show();

                                }
                            },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.show();
                }
            });

            button_success.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    mcancelDialog.dismiss();
                    try {
                        JSONObject j = new JSONObject();
                        j.put("driver_id", SessionSave.getSession("Id", StatementofHistory.this));
                         j.put("from_date", ToDate.getText().toString());
                         j.put("to_date", FromDate.getText().toString());

                        final String url = "type=driver_report_list";
                        new callWalletHistory(url, j);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });


            button_failure.setVisibility(View.VISIBLE);
            button_failure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    mcancelDialog.dismiss();
                }
            });

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private class callWalletHistory implements APIResult {
        public callWalletHistory(String url, JSONObject data) {
            if (isOnline()) {
                new APIService_Retrofit_JSON(StatementofHistory.this, this, data, false).execute(url);
            } else {
                dialog1 = Utils.alert_view(StatementofHistory.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, StatementofHistory.this, "");
            }
        }

        @Override
        public void getResult(final boolean isSuccess, final String result) {
            if (isSuccess) {
                statData.clear();
                try {
                    final JSONObject json = new JSONObject(result);

                    closeDialog();



                    if (json.getInt("status") == 1) {

                        JSONObject json2 = json.getJSONObject("detail");

                        JSONArray mWalletArray = json2.getJSONArray("driver_report");

                        wallet_amount.setText(SessionSave.getSession("site_currency", StatementofHistory.this) + " " +mWalletArray.getJSONObject(0).getString("balance"));

                        for (int i = 0; i < mWalletArray.length(); i++) {
                            JSONObject mJsonObject = mWalletArray.getJSONObject(i);
                            StatementData mStatementData = new StatementData();
                            mStatementData.setAdded_amount(mJsonObject.getString("added_amount"));
                            mStatementData.setBalance(mJsonObject.getString("balance"));
                            mStatementData.setCreatedate(mJsonObject.getString("createdate"));
                            mStatementData.setDescription(mJsonObject.getString("description"));
                            mStatementData.setWallet_item(mJsonObject.getString("wallet_item"));
                            statData.add(mStatementData);
                        }


                    }

                } catch (final JSONException e) {
                    e.printStackTrace();
                } finally {

                    if (statData.size() == 0) {
                        no_data.setVisibility(View.VISIBLE);
                    } else {
                        no_data.setVisibility(View.GONE);
                        stat_adapter = new StatementListAdapter(StatementofHistory.this, statData);
                        history_recyclerView.setAdapter(stat_adapter);
                        stat_adapter.notifyDataSetChanged();
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
            if (NetworkStatus.isOnline(StatementofHistory.this)) {
                if (loadingDialog != null && loadingDialog.isShowing())
                    loadingDialog.dismiss();
                View view = View.inflate(StatementofHistory.this, R.layout.progress_bar, null);
                loadingDialog = new Dialog(StatementofHistory.this, R.style.dialogwinddow);
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
