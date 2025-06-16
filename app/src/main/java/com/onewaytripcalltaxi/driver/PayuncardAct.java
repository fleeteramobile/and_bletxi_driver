package com.onewaytripcalltaxi.driver;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.onewaytripcalltaxi.driver.data.CommonData;
import com.onewaytripcalltaxi.driver.interfaces.APIResult;
import com.onewaytripcalltaxi.driver.interfaces.ClickInterface;
import com.onewaytripcalltaxi.driver.service.APIService_Retrofit_JSON;
import com.onewaytripcalltaxi.driver.utils.CToast;
import com.onewaytripcalltaxi.driver.utils.Colorchange;
import com.onewaytripcalltaxi.driver.utils.DatePicker_CardExpiry;
import com.onewaytripcalltaxi.driver.utils.FontHelper;
import com.onewaytripcalltaxi.driver.utils.FourDigitCardFormatWatcher;
import com.onewaytripcalltaxi.driver.utils.NC;
import com.onewaytripcalltaxi.driver.utils.SessionSave;
import com.onewaytripcalltaxi.driver.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Calendar;


/**
 * This class  is used to complete the payment by new card
 */
public class PayuncardAct extends MainActivity implements OnClickListener, ClickInterface, DatePicker_CardExpiry.DialogInterface {
    private EditText cardEdt;
    private TextView expireEdt;
    private TextView DoneBtn, backbtn;
    private EditText cvvEdt;
    private TextView HeadTitle;
    private String message;
    private String f_tripid;
    private String f_distance;
    private String f_nightfareapplicable;
    private String f_nightfare;
    private String f_eveningfare_applicable;
    private String f_eveningfare;
    private String f_passengerdiscount;
    private String f_waitingtime;
    private String f_waitingcost;
    private String f_taxamount;
    private String f_tripfare;
    private String f_total;
    private String f_fare;
    private String f_tips;
    private String creditcard_no;
    private String creditcard_cvv;
    private String expmonth;
    private String expyear;
    private String expdate;
    private int mMonth;
    private int mDay;
    private int mYear;
    private static final int DATE_DIALOG_ID = 0;
    private final String f_paymodid = "3";
    private String group_id;
    private String account_id;
    private String info;
    private String f_minutes_traveled;
    private String f_minutes_fare;
    private DatePicker_CardExpiry editNameDialog;
    private String company_tax;
    private String base_fare;
    private String promodiscount_amount;
    private String fare_calculation_type;
    private Dialog dialog1;
    private String distanceFare;
    /**
     * set layout to the activity
     */
    @Override
    public int setLayout() {
        setLocale();
        return R.layout.payuncard_lay;
    }
    /**
     * Initializing UI Components
     */
    @Override
    public void Initialize() {
        Bundle bun = getIntent().getExtras();
        CommonData.current_act = "PayuncardAct";
        FontHelper.applyFont(this, findViewById(R.id.id_paylay));
        Colorchange.ChangeColor((ViewGroup) (((ViewGroup) PayuncardAct.this
                .findViewById(android.R.id.content)).getChildAt(0)), PayuncardAct.this);
        if (bun != null) {
            HeadTitle = findViewById(R.id.signup_title);
            cardEdt = findViewById(R.id.cardEdt);
            cardEdt.addTextChangedListener(new FourDigitCardFormatWatcher(PayuncardAct.this));
            expireEdt = findViewById(R.id.expireEdt);
            expireEdt.setHint(NC.getString(R.string.p_expiry));
            cvvEdt = findViewById(R.id.cvvEdt);
            DoneBtn = findViewById(R.id.doneBtn);
            backbtn = findViewById(R.id.backImg);
            HeadTitle.setText(NC.getResources().getString(R.string.heading_payuncard));
            info = bun.getString("info");
            if (info.equals("Account")) {
                message = bun.getString("message");
                f_total = bun.getString("f_total");
                f_fare = bun.getString("f_fare");
                f_tips = bun.getString("f_tips");
                group_id = bun.getString("gid");
                account_id = bun.getString("aid");
                Log.i("f_total", f_total);
                Log.i("f_fare", f_fare);
                Log.i("f_tips", f_tips);
                Log.i("group", group_id);
                Log.i("account", account_id);
            } else if (info.equals("Uncard")) {
                message = bun.getString("message");
                f_total = bun.getString("f_total");
                f_fare = bun.getString("f_fare");
                f_tips = bun.getString("f_tips");
                Log.i("f_total", f_total);
                Log.i("f_fare", f_fare);
                Log.i("f_tips", f_tips);
            }
            DoneBtn.setText(NC.getResources().getString(R.string.done));
            Calendar cal = Calendar.getInstance();
            mYear = cal.get(Calendar.YEAR);
            mMonth = cal.get(Calendar.MONTH);
            mDay = cal.get(Calendar.DAY_OF_MONTH);
            setonclickListener();
            try {
                JSONObject detail = new JSONObject(message);
                JSONObject json = detail.getJSONObject("detail");
                f_tripid = json.getString("trip_id");
                f_distance = json.getString("distance");
                f_nightfareapplicable = json.getString("nightfare_applicable");
                f_nightfare = json.getString("nightfare");
                f_passengerdiscount = json.getString("passenger_discount");
                f_waitingtime = json.getString("waiting_time");
                f_waitingcost = json.getString("waiting_cost");
                f_taxamount = json.getString("tax_amount");
                f_tripfare = json.getString("trip_fare");
                f_eveningfare_applicable = json.getString("eveningfare_applicable");
                f_eveningfare = json.getString("eveningfare");
                f_minutes_traveled = json.getString("minutes_traveled");
                f_minutes_fare = json.getString("minutes_fare");
                company_tax = json.getString("company_tax");
                base_fare = json.getString("base_fare");
                promodiscount_amount = json.getString("promodiscount_amount");
                fare_calculation_type = json.getString("fare_calculation_type");
                if(json.has("distance_fare")) {
                    distanceFare = json.getString("distance_fare");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Setting on click listeners
     */
    private void setonclickListener() {
        DoneBtn.setOnClickListener(this);
        backbtn.setOnClickListener(this);
        expireEdt.setOnClickListener(this);
    }
    @Override
    protected void onDestroy() {
        if (dialog1 != null)
            Utils.closeDialog(dialog1);
        super.onDestroy();
    }
    /**
     * Donebtn onclick
     */
    @Override
    public void onClick(View v) {
        try {
            Calendar cl = Calendar.getInstance();
            int cl_year = cl.get(Calendar.YEAR);
            if (v == DoneBtn) {
                creditcard_no = cardEdt.getText().toString().trim().replaceAll("\\s", "");
                creditcard_cvv = cvvEdt.getText().toString().trim();
                expdate = expireEdt.getText().toString().trim();
                if (creditcard_no.length() == 0) {

                    dialog1 = Utils.alert_view(PayuncardAct.this, "", NC.getResources().getString(R.string.ent_ccard_no), NC.getResources().getString(R.string.ok),
                            "", true, PayuncardAct.this, "");


                } else if (creditcard_no.length() < 9) {

                    dialog1 = Utils.alert_view(PayuncardAct.this, "", NC.getResources().getString(R.string.ent_chk_ccard), NC.getResources().getString(R.string.ok),
                            "", true, PayuncardAct.this, "");


                } else if (creditcard_no.length() > 16) {

                    dialog1 = Utils.alert_view(PayuncardAct.this, "", NC.getResources().getString(R.string.ent_chk_ccard), NC.getResources().getString(R.string.ok),
                            "", true, PayuncardAct.this, "");


                } else if (expdate.length() == 0) {

                    dialog1 = Utils.alert_view(PayuncardAct.this, "", NC.getResources().getString(R.string.ent_exp_date), NC.getResources().getString(R.string.ok),
                            "", true, PayuncardAct.this, "");

                } else if (mYear < cl_year) {

                    dialog1 = Utils.alert_view(PayuncardAct.this, "", NC.getResources().getString(R.string.ent_exp_year_gt), NC.getResources().getString(R.string.ok),
                            "", true, PayuncardAct.this, "");

                } else if (creditcard_cvv.length() == 0) {

                    dialog1 = Utils.alert_view(PayuncardAct.this, "", NC.getResources().getString(R.string.ent_cvv), NC.getResources().getString(R.string.ok),
                            "", true, PayuncardAct.this, "");


                } else if (creditcard_cvv.length() < 3) {

                    dialog1 = Utils.alert_view(PayuncardAct.this, "", NC.getResources().getString(R.string.ent_chk_cvv), NC.getResources().getString(R.string.ok),
                            "", true, PayuncardAct.this, "");


                } else if (creditcard_cvv.length() > 4) {

                    dialog1 = Utils.alert_view(PayuncardAct.this, "", NC.getResources().getString(R.string.ent_chk_cvv), NC.getResources().getString(R.string.ok),
                            "", true, PayuncardAct.this, "");

                } else {
                    expmonth = Integer.toString(mMonth);
                    if (expmonth.length() == 1) {
                        expmonth = "0" + expmonth;
                    }
                    expyear = Integer.toString(mYear);
                    callurl();
                }
            }else if (v == expireEdt) {
                FragmentManager fm = getSupportFragmentManager();
                editNameDialog = new DatePicker_CardExpiry();
                Bundle bundle = new Bundle();
                bundle.putString("KEY", "1");
                editNameDialog.setArguments(bundle);
                editNameDialog.show(fm, "fragment_edit_name");

            } else if (v == backbtn) {
                onBackPressed();
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
    /**
     * Updating trip fare and response parsing
     */
    private void callurl() {
        try {
            String url = "type=tripfare_update";
            JSONObject j = new JSONObject();
            j.put("trip_id", f_tripid);
            j.put("distance", f_distance);
            j.put("actual_distance", MainActivity.mMyStatus.getdistance());
            j.put("actual_amount", f_total);
            j.put("trip_fare", f_tripfare);
            j.put("fare", f_fare);
            j.put("tips", f_tips);
            j.put("passenger_promo_discount", f_passengerdiscount);
            j.put("tax_amount", f_taxamount);
            j.put("remarks", "");
            j.put("nightfare_applicable", f_nightfareapplicable);
            j.put("nightfare", f_nightfare);
            j.put("eveningfare_applicable", f_eveningfare_applicable);
            j.put("eveningfare", f_eveningfare);
            j.put("waiting_time", f_waitingtime);
            j.put("waiting_cost", f_waitingcost);
            j.put("creditcard_no", creditcard_no);
            j.put("creditcard_cvv", creditcard_cvv);
            j.put("expmonth", expmonth);
            j.put("expyear", expyear);
            j.put("pay_mod_id", f_paymodid);
            j.put("passenger_discount", "");
            j.put("minutes_traveled", f_minutes_traveled);
            j.put("minutes_fare", f_minutes_fare);
            j.put("company_tax", company_tax);
            j.put("base_fare", base_fare);
            j.put("promodiscount_amount", promodiscount_amount);
            j.put("fare_calculation_type", fare_calculation_type);
            j.put("distance_fare",distanceFare);
            new FareUpdate(url, j);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccess(int monthOfYear, int year, int day) {
        mYear = year;
        mMonth = monthOfYear + 1;
        mDay = 2;
        editNameDialog.dismiss();
        expireEdt.setText(new StringBuilder().append(mMonth).append("/").append(mYear).append(" "));
    }

    @Override
    public void failure(String inputText) {
        editNameDialog.dismiss();
    }


    /**
     * FareUpdate API response parsing.
     */
    private class FareUpdate implements APIResult {
        String msg = "";
        FareUpdate(String url, JSONObject data) {
            new APIService_Retrofit_JSON(PayuncardAct.this, this, data, false).execute(url);
        }
        @Override
        public void getResult(boolean isSuccess, final String result) {
            try {
                if (isSuccess) {
                    JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {
                        JSONObject jsonDriver = json.getJSONObject("driver_statistics");
                        SessionSave.saveSession("driver_statistics", String.valueOf(jsonDriver), PayuncardAct.this);
                        msg = json.getString("message");
                        CommonData.travel_km = 0;
                        SessionSave.setGoogleDistance(0f, PayuncardAct.this);
                        SessionSave.setDistance(0f, PayuncardAct.this);
                        SessionSave.saveGoogleWaypoints(null, null, "", 0.0, "", PayuncardAct.this);
                        SessionSave.saveWaypoints(null, null, "", 0.0, "", PayuncardAct.this);
                        Intent jobintent = new Intent(PayuncardAct.this, JobdoneAct.class);
                        Bundle bun = new Bundle();
                        bun.putString("message", result);
                        jobintent.putExtras(bun);
                        startActivity(jobintent);
                    } else {
                        msg = json.getString("message");
                    }
                    dialog1 = Utils.alert_view(PayuncardAct.this, NC.getResources().getString(R.string.message), msg, NC.getResources().getString(R.string.ok), "", true, PayuncardAct.this, "");
                } else {
                    runOnUiThread(() -> CToast.ShowToast(PayuncardAct.this, NC.getString(R.string.server_error)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Date picker
     */
    private final DatePickerDialog.OnDateSetListener mDateSetListner = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear + 1;
            mDay = dayOfMonth;
            expireEdt.setText(new StringBuilder().append(mMonth).append("/").append(mYear).append(" "));
        }
    };
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DATE_DIALOG_ID) {
            DatePickerDialog datePickerDialog = this.customDatePicker();
            return datePickerDialog;
        }
        return null;
    }
    /**
     * Date picker custom dialog
     */
    private DatePickerDialog customDatePicker() {
        DatePickerDialog dpd = new DatePickerDialog(this, mDateSetListner, mYear, mMonth, mDay);
        try {
            Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField.get(dpd);
                    Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();
                    for (Field datePickerField : datePickerFields) {
                        if ("mDayPicker".equals(datePickerField.getName()) || "mDaySpinner".equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dpd;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    public void positiveButtonClick(DialogInterface dialog, int id, String s) {
        dialog.dismiss();
    }
    @Override
    public void negativeButtonClick(DialogInterface dialog, int id, String s) {
        dialog.dismiss();
    }
}
