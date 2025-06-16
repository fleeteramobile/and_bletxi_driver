package com.onewaytripcalltaxi.driver;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.onewaytripcalltaxi.driver.adapter.Withdraw_history_adapter;
import com.onewaytripcalltaxi.driver.adapter.Withdraw_referalhistory_adapter;
import com.onewaytripcalltaxi.driver.interfaces.APIResult;
import com.onewaytripcalltaxi.driver.interfaces.ClickInterface;
import com.onewaytripcalltaxi.driver.service.APIService_Retrofit_JSON;
import com.onewaytripcalltaxi.driver.utils.CL;
import com.onewaytripcalltaxi.driver.utils.Colorchange;
import com.onewaytripcalltaxi.driver.utils.FontHelper;
import com.onewaytripcalltaxi.driver.utils.NC;
import com.onewaytripcalltaxi.driver.utils.SessionSave;
import com.onewaytripcalltaxi.driver.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by developer on 5/10/16.
 */


/**
 * This class is used to view withdraw history for both referal and trip
 */
public class WithdrawHistoryAct extends MainActivity implements ClickInterface {


    private ArrayList<HashMap<String, String>> data;
    private ListView list;
    private TextView back_home, btn_referal, btn_trip, btn_back;
    private ImageView btn_filter;

    private TextView txt_view;

    private TextView txt_fromdate, txt_todate;

    Spinner Statusspn;
    private int viewType = 1;

    private int _hour = 0;
    private int _min = 0;
    private int _date = 0;
    private int _month = 0;
    private int _year = 0;
    private String _ampm = "AM";

    private final String fromtime = " 00:00";
    private final String totime = " 23:59";

    int temp_status = 0;


    private Dialog dt_mDialog;
    private LinearLayout no_data_txt;
    RelativeLayout lay_list;
    private View trip_underline;
    private View referl_underline;

    private Dialog dialog1;

    @Override
    public int setLayout() {
        return R.layout.listview;
    }


    /**
     * Initialize the views on layout
     */
    @Override
    public void Initialize() {
        list = findViewById(R.id.listView);
        back_home = findViewById(R.id.back_home);
        btn_referal = findViewById(R.id.btnreferal);
        btn_trip = findViewById(R.id.btntrip);
        btn_back = findViewById(R.id.slideImg);
        txt_view = findViewById(R.id.view);
        trip_underline = findViewById(R.id.trip_underline);
        referl_underline = findViewById(R.id.referl_underline);
        btn_filter = findViewById(R.id.filter);
        no_data_txt = findViewById(R.id.no_data);
        lay_list = findViewById(R.id.lay_list);


        Colorchange.ChangeColor((ViewGroup) (((ViewGroup) WithdrawHistoryAct.this
                .findViewById(android.R.id.content)).getChildAt(0)), WithdrawHistoryAct.this);

        ImageView iv = findViewById(R.id.progress_history);
        DrawableImageViewTarget imageViewTarget = new DrawableImageViewTarget(iv);
        Glide.with(WithdrawHistoryAct.this)
                .load(R.raw.loading_anim)
                .into(imageViewTarget);


        btn_filter.setVisibility(View.INVISIBLE);


        btn_referal.setOnClickListener(v -> {

            viewType = 1;
            if (data != null)
                data.clear();
            list.setAdapter(null);
            btn_filter.setVisibility(View.INVISIBLE);
            SetWithdrawList();
        });

        btn_trip.setOnClickListener(v -> {

            viewType = 2;
            if (data != null)
                data.clear();
            list.setAdapter(null);
            btn_filter.setVisibility(View.VISIBLE);
            SetWithdrawList();
        });

        btn_back.setOnClickListener(v -> finish());

        back_home.setOnClickListener(v -> startActivity(new Intent(WithdrawHistoryAct.this, MyStatus.class)));

        btn_filter.setOnClickListener(v -> withDrawFilter());

    }

    /**
     * showing Withdraw filter popup
     */
    public void withDrawFilter() {
        try {

            final View view = View.inflate(WithdrawHistoryAct.this, R.layout.withdrawfilter, null);
            final Dialog mcancelDialog = new Dialog(WithdrawHistoryAct.this, R.style.dialogwinddow);
            mcancelDialog.setContentView(view);
            mcancelDialog.setCancelable(true);
            mcancelDialog.show();
            Colorchange.ChangeColor(mcancelDialog.findViewById(R.id.alert_id), WithdrawHistoryAct.this);
            FontHelper.applyFont(WithdrawHistoryAct.this, mcancelDialog.findViewById(R.id.alert_id));

            txt_fromdate = view.findViewById(R.id.fromdateTxt);
            txt_todate = view.findViewById(R.id.todateTxt);
            Statusspn = view.findViewById(R.id.statusspn);


            ArrayAdapter adapter = ArrayAdapter.createFromResource(
                    this, R.array.Status, android.R.layout.simple_spinner_item);

            adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
            Statusspn.setAdapter(adapter);
            txt_fromdate.setText("yyyy-mm-dd");
            txt_todate.setText("yyyy-mm-dd");

            txt_fromdate.setOnClickListener(v -> Pickdate(1));

            txt_todate.setOnClickListener(v -> Pickdate(2));


            Statusspn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int arg2, long arg3) {
                    // TODO Auto-generated method stub
                    temp_status = parent.getSelectedItemPosition();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // TODO Auto-generated method stub
                }
            });

            final Button button_success = mcancelDialog.findViewById(R.id.okbtn);
            final Button button_cancel = mcancelDialog.findViewById(R.id.cancelbtn);

            button_success.setOnClickListener(v -> {

                if ((!txt_fromdate.getText().toString().isEmpty()) && (!txt_todate.getText().toString().isEmpty())) {
                    lay_list.setVisibility(View.GONE);
                    list.setAdapter(null);
                    mcancelDialog.dismiss();
                    new FilterApi(txt_fromdate.getText().toString(), txt_todate.getText().toString(), temp_status);
                } else {
                    dialog1 = Utils.alert_view(WithdrawHistoryAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.please_choosedate), NC.getResources().getString(R.string.ok), "", true, WithdrawHistoryAct.this, "");
                }

            });

            button_cancel.setOnClickListener(v -> mcancelDialog.dismiss());

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        if (dialog1 != null)
            Utils.closeDialog(dialog1);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();

        viewType = 1;

        if (data != null)
            data.clear();
        btn_filter.setVisibility(View.INVISIBLE);
        SetWithdrawList();
    }

    /**
     * Pick date selection
     * Type:1(fromdate)
     * Type:2(todate)
     */
    private void Pickdate(final int type) {
        final Context context = this;
        try {
            final View r_view = View.inflate(context, R.layout.date_time_picker_dialog, null);

            FontHelper.applyFont(context, r_view.findViewById(R.id.inner_content));
            dt_mDialog = new Dialog(context, R.style.dialogwinddow);
            dt_mDialog.setContentView(r_view);
            dt_mDialog.setCancelable(true);
            dt_mDialog.show();
            Colorchange.ChangeColor(dt_mDialog.findViewById(R.id.inner_content), WithdrawHistoryAct.this);
            final DatePicker _datePicker = dt_mDialog.findViewById(R.id.datePicker1);
            final TimePicker _timePicker = dt_mDialog.findViewById(R.id.timePicker1);
            _timePicker.setVisibility(View.GONE);
            FontHelper.overrideFonts(context, _datePicker);
            FontHelper.overrideFonts(context, _timePicker);
            Calendar c = Calendar.getInstance();
            _timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY) + 1);
            _timePicker.setCurrentMinute(c.get(Calendar.MINUTE) + 1);
            _timePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            });
            Time now = new Time();
            Button butConfirmTime = dt_mDialog.findViewById(R.id.butConfirmTime);
            butConfirmTime.setOnClickListener(v -> {
                getCurrentDateAndTime(_timePicker, _datePicker);
                String seletecedString = _year + "-" + _month + "-" + _date;

                if (type == 1) {
                    txt_fromdate.setText(seletecedString);
                } else {
                    txt_todate.setText(seletecedString);
                }

                dt_mDialog.dismiss();
            });

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }


    /**
     * Getting current date and time from time picker
     */
    public void getCurrentDateAndTime(TimePicker _timePicker, DatePicker _datePicker) {

        _hour = _timePicker.getCurrentHour();
        _min = _timePicker.getCurrentMinute();
        _date = _datePicker.getDayOfMonth();
        _month = _datePicker.getMonth() + 1;
        _year = _datePicker.getYear();
        ampmValidation(_hour);
    }


    /**
     * Am and Pm Validation
     */
    private String ampmValidation(int inputHour) {

        if (inputHour >= 13) {
            _hour = inputHour - 12;
            _ampm = "PM";
        } else if (inputHour == 12) {
            _ampm = "PM";
        } else if (inputHour == 0) {
            _hour = 12;
            _ampm = "AM";
        }
        return _ampm;
    }


    /**
     * This method will be called from referal on click and trip onclick listener
     * viewType - 1(show referal history)
     * viewType - 2(show trip history)
     */
    public void SetWithdrawList() {
        if (viewType == 1) {
            btn_referal.setTextColor(CL.getResources().getColor(R.color.button_accept));

            referl_underline.setBackgroundColor(CL.getResources().getColor(R.color.button_accept));
            trip_underline.setBackgroundColor(CL.getResources().getColor(R.color.linebottom_light));
            btn_trip.setTextColor(CL.getResources().getColor(R.color.hintcolor));

            txt_view.setVisibility(View.GONE);

            new ReferalApi();
        } else {
            btn_referal.setTextColor(CL.getResources().getColor(R.color.hintcolor));
            btn_trip.setTextColor(CL.getResources().getColor(R.color.button_accept));
            referl_underline.setBackgroundColor(CL.getResources().getColor(R.color.linebottom_light));
            trip_underline.setBackgroundColor(CL.getResources().getColor(R.color.button_accept));
            txt_view.setVisibility(View.VISIBLE);

            new requestingApi();
        }
    }

    /**
     * Filter withdrawlist API response parsing.
     */
    public class FilterApi implements APIResult {
        public FilterApi(String fromdate, String todate, int status) {

            try {
                JSONObject j = new JSONObject();
                j.put("driver_id", SessionSave.getSession("Id", WithdrawHistoryAct.this));
                if (fromdate.contains("yyyy-mm-dd"))
                    j.put("from", "");
                else
                    j.put("from", fromdate + fromtime);
                if (todate.contains("yyyy-mm-dd"))
                    j.put("to", "");
                else
                    j.put("to", todate + totime);
                j.put("status", status);

                String driverTripRequesting = "type=search_driver_withdraw_list";
                if (isOnline()) {
                    new APIService_Retrofit_JSON(WithdrawHistoryAct.this, this, j, false).execute(driverTripRequesting);
                } else {
                    dialog1 = Utils.alert_view(WithdrawHistoryAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, WithdrawHistoryAct.this, "");
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        @Override
        public void getResult(boolean isSuccess, String result) {

            try {
                Log.e("_Result_", result);

                if (isSuccess) {
                    data = new ArrayList<>();
                    JSONObject object = new JSONObject(result);
                    if (object.getInt("status") == 1) {
                        JSONArray jobject = object.getJSONArray("details");
                        for (int i = 0; i < jobject.length(); i++) {
                            JSONObject jo = jobject.getJSONObject(i);
                            HashMap<String, String> hm = new HashMap<>();

                            hm.put("wallet_request_id", jo.getString("withdraw_request_id"));
                            hm.put("wallet_request_amount", jo.getString("withdraw_amount"));
                            hm.put("status", jo.getString("request_status"));
                            hm.put("wallet_request_date", jo.getString("request_date"));

                            data.add(hm);


                        }
                        list.setAdapter(new Withdraw_history_adapter(WithdrawHistoryAct.this, data, viewType));
                        if (data.size() <= 0) {
                            no_data_txt.setVisibility(View.VISIBLE);
                            lay_list.setVisibility(View.GONE);
                            findViewById(R.id.progress_history).setVisibility(View.GONE);
                        } else {
                            no_data_txt.setVisibility(View.GONE);
                            lay_list.setVisibility(View.VISIBLE);
                            findViewById(R.id.progress_history).setVisibility(View.GONE);
                        }
                    } else {
                        no_data_txt.setVisibility(View.VISIBLE);
                        lay_list.setVisibility(View.GONE);
                        findViewById(R.id.progress_history).setVisibility(View.GONE);
                    }
                } else {

                    runOnUiThread(() -> dialog1 = Utils.alert_view(WithdrawHistoryAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, WithdrawHistoryAct.this, ""));
                }
            } catch (Exception ex) {

                ex.printStackTrace();
            }
        }
    }

    /**
     * Trip Withdraw history API response parsing.
     */
    public class requestingApi implements APIResult {
        public requestingApi() {

            try {
                JSONObject j = new JSONObject();
                j.put("driver_id", SessionSave.getSession("Id", WithdrawHistoryAct.this));

                String driverTripRequesting = "type=driver_withdraw_list";
                if (isOnline()) {
                    new APIService_Retrofit_JSON(WithdrawHistoryAct.this, this, j, false).execute(driverTripRequesting);
                } else {
                    dialog1 = Utils.alert_view(WithdrawHistoryAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, WithdrawHistoryAct.this, "");
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        @Override
        public void getResult(boolean isSuccess, String result) {

            try {
                if (isSuccess) {
                    data = new ArrayList<>();
                    JSONObject object = new JSONObject(result);
                    if (object.getInt("status") == 1) {
                        JSONArray jobject = object.getJSONArray("details");
                        for (int i = 0; i < jobject.length(); i++) {
                            JSONObject jo = jobject.getJSONObject(i);
                            HashMap<String, String> hm = new HashMap<>();

                            hm.put("wallet_request_id", jo.getString("withdraw_request_id"));
                            hm.put("wallet_request_amount", jo.getString("withdraw_amount"));
                            hm.put("status", jo.getString("request_status"));
                            hm.put("wallet_request_date", jo.getString("request_date"));

                            data.add(hm);


                        }
                        list.setAdapter(new Withdraw_history_adapter(WithdrawHistoryAct.this, data, viewType));
                        if (data.size() <= 0) {
                            no_data_txt.setVisibility(View.VISIBLE);
                            lay_list.setVisibility(View.GONE);
                            findViewById(R.id.progress_history).setVisibility(View.GONE);
                        } else {
                            no_data_txt.setVisibility(View.GONE);
                            lay_list.setVisibility(View.VISIBLE);
                            findViewById(R.id.progress_history).setVisibility(View.GONE);
                        }
                    } else {
                        no_data_txt.setVisibility(View.VISIBLE);
                        lay_list.setVisibility(View.GONE);
                    }
                } else {

                    runOnUiThread(() -> dialog1 = Utils.alert_view(WithdrawHistoryAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, WithdrawHistoryAct.this, ""));
                }
            } catch (Exception ex) {

                ex.printStackTrace();
            }
        }
    }


    /**
     * Referal Withdraw history API response parsing.
     */
    public class ReferalApi implements APIResult {
        public ReferalApi() {

            try {
                JSONObject j = new JSONObject();
                j.put("driver_id", SessionSave.getSession("Id", WithdrawHistoryAct.this));

                String driverTripRequesting = "type=driver_wallet";
                if (isOnline()) {
                    new APIService_Retrofit_JSON(WithdrawHistoryAct.this, this, j, false).execute(driverTripRequesting);
                } else {
                    dialog1 = Utils.alert_view(WithdrawHistoryAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, WithdrawHistoryAct.this, "");
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        @Override
        public void getResult(boolean isSuccess, String result) {

            Log.e("result ", result);
            try {
                if (isSuccess) {
                    data = new ArrayList<>();
                    JSONObject object = new JSONObject(result);
                    if (object.getInt("status") == 1) {

                        JSONArray jobject = object.getJSONArray("request_lists");
                        for (int i = 0; i < jobject.length(); i++) {
                            JSONObject jo = jobject.getJSONObject(i);
                            HashMap<String, String> hm = new HashMap<>();

                            hm.put("wallet_request_id", jo.getString("wallet_request_id"));
                            hm.put("wallet_request_amount", jo.getString("wallet_request_amount"));
                            hm.put("status", jo.getString("status"));
                            hm.put("wallet_request_date", jo.getString("wallet_request_date"));
                            data.add(hm);
                        }
                        list.setAdapter(new Withdraw_referalhistory_adapter(WithdrawHistoryAct.this, data));
                        if (data.size() <= 0) {
                            no_data_txt.setVisibility(View.VISIBLE);
                            lay_list.setVisibility(View.GONE);
                            findViewById(R.id.progress_history).setVisibility(View.GONE);
                        } else {
                            no_data_txt.setVisibility(View.GONE);
                            lay_list.setVisibility(View.VISIBLE);
                            findViewById(R.id.progress_history).setVisibility(View.GONE);
                        }
                    } else {
                        dialog1 = Utils.alert_view(WithdrawHistoryAct.this, NC.getResources().getString(R.string.message), object.getString("message"), NC.getResources().getString(R.string.ok), "", true, WithdrawHistoryAct.this, "");
                    }
                } else {

                    runOnUiThread(() -> dialog1 = Utils.alert_view(WithdrawHistoryAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, WithdrawHistoryAct.this, ""));
                }
            } catch (Exception ex) {

                ex.printStackTrace();
            }
        }
    }

    @Override
    public void positiveButtonClick(DialogInterface dialog, int id, String s) {
        dialog.dismiss();
    }
}