package com.onewaytripcalltaxi.driver;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.onewaytripcalltaxi.driver.data.CommonData;
import com.onewaytripcalltaxi.driver.interfaces.APIResult;
import com.onewaytripcalltaxi.driver.interfaces.ClickInterface;
import com.onewaytripcalltaxi.driver.service.APIService_Retrofit_JSON;
import com.onewaytripcalltaxi.driver.service.ServiceGenerator;
import com.onewaytripcalltaxi.driver.utils.CToast;
import com.onewaytripcalltaxi.driver.utils.Colorchange;
import com.onewaytripcalltaxi.driver.utils.FontHelper;
import com.onewaytripcalltaxi.driver.utils.NC;
import com.onewaytripcalltaxi.driver.utils.SessionSave;
import com.onewaytripcalltaxi.driver.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * This class is used to  withdraw both referal and trip amount
 */

public class WithDrawMenuAct extends MainActivity implements ClickInterface {

    public static WithDrawMenuAct withdrawAct;

    private TextView  withdraw_btn2, withdraw_btn3,txt_referalamt, txt_tripamount, txt_refpendingamt, txt_trippendingamt, btn_back, versionText,txt_incentiveamt,txt_incentpendingamt;
EditText with_amount;
    CardView withdraw_btn1,back_menu;
    private ImageView btn_withdrawhistory;

    private Dialog dialog1;

    @Override
    public int setLayout() {

        setLocale();
        return R.layout.withdraw_menu;
    }

    // Initialize the views on layout and variable declarations
    @Override
    public void Initialize() {

        try {
            Colorchange.ChangeColor((ViewGroup) (((ViewGroup) WithDrawMenuAct.this
                    .findViewById(android.R.id.content)).getChildAt(0)), WithDrawMenuAct.this);

            CommonData.mActivitylist.add(this);
            CommonData.sContext = this;
            CommonData.current_act = "WithDrawMenuAct";
            FontHelper.applyFont(this, findViewById(R.id.withdraw_menu));
            withdrawAct = this;

            back_menu = findViewById(R.id.back_menu);
            withdraw_btn1 = findViewById(R.id.withdraw_btn1);
            withdraw_btn2 = findViewById(R.id.withdraw_btn2);
            withdraw_btn3=findViewById(R.id.withdraw_btn3);

            txt_referalamt = findViewById(R.id.withdraw_BalanceAmttext1);
            txt_tripamount = findViewById(R.id.withdraw_BalanceAmttext2);
            txt_incentiveamt=findViewById(R.id.withdraw_BalanceAmttext3);
            txt_refpendingamt = findViewById(R.id.withdraw_pendingtext1);
            txt_trippendingamt = findViewById(R.id.withdraw_pendingtext2);
            txt_incentpendingamt=findViewById(R.id.withdraw_pendingtext3);
            with_amount=findViewById(R.id.with_amount);

            versionText = findViewById(R.id.version_text);

            btn_back = findViewById(R.id.slideImg);

            btn_withdrawhistory = findViewById(R.id.history);
            txt_referalamt.setText(SessionSave.getSession("site_currency", WithDrawMenuAct.this) + SessionSave.getSession("driver_wallet_amount", WithDrawMenuAct.this));
            with_amount.setText(SessionSave.getSession("driver_wallet_amount", WithDrawMenuAct.this));
            txt_refpendingamt.setText(NC.getResources().getString(R.string.payment_pending) + "- " + SessionSave.getSession("site_currency", WithDrawMenuAct.this) + " " + SessionSave.getSession("driver_wallet_pending_amount", WithDrawMenuAct.this));
            txt_tripamount.setText(SessionSave.getSession("site_currency", WithDrawMenuAct.this) + SessionSave.getSession("trip_amount", WithDrawMenuAct.this));
            txt_trippendingamt.setText(NC.getResources().getString(R.string.payment_pending) + "- " + SessionSave.getSession("site_currency", WithDrawMenuAct.this) + " " + SessionSave.getSession("trip_pending_amount", WithDrawMenuAct.this));

            txt_incentiveamt.setText(SessionSave.getSession("site_currency", WithDrawMenuAct.this) + SessionSave.getSession("incentive_amount", WithDrawMenuAct.this));
            txt_incentpendingamt.setText(NC.getResources().getString(R.string.payment_pending) + "- " + SessionSave.getSession("site_currency", WithDrawMenuAct.this) + " " + SessionSave.getSession("incentive_pending_amount", WithDrawMenuAct.this));
            String host = "";
            try {
                URL urls = new URL(ServiceGenerator.API_BASE_URL);
                host = urls.getHost();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            ((TextView) findViewById(R.id.baseUrl)).setText(host);

           /* if (APP_VERSION.equals("")) {
                APP_VERSION = BuildConfig.VERSION_NAME;
            }
*/

            versionText.setText(APP_VERSION);


            withdraw_btn1.setOnClickListener(v -> {

                if (Double.parseDouble(SessionSave.getSession("driver_wallet_amount", WithDrawMenuAct.this).trim()) > 0) {
                    withDraw(1);
                } else {

                    dialog1 = Utils.alert_view(WithDrawMenuAct.this, "", NC.getResources().getString(R.string.no_sufficient_amount), NC.getResources().getString(R.string.ok),
                            "", true, WithDrawMenuAct.this, "");
                }

            });

            withdraw_btn2.setOnClickListener(v -> {
                if (Double.parseDouble(SessionSave.getSession("trip_amount", WithDrawMenuAct.this).trim()) > Double.parseDouble(SessionSave.getSession("min_wallet_amount", WithDrawMenuAct.this))) {
                    withDraw(2);
                } else {

                    dialog1 = Utils.alert_view(WithDrawMenuAct.this, "", NC.getResources().getString(R.string.no_sufficient_amount), NC.getResources().getString(R.string.ok),
                            "", true, WithDrawMenuAct.this, "");
                }
            });

            withdraw_btn3.setOnClickListener(v -> {
                if (Double.parseDouble(SessionSave.getSession("incentive_amount", WithDrawMenuAct.this).trim()) >0) {
                    withDraw(3);
                } else {

                    dialog1 = Utils.alert_view(WithDrawMenuAct.this, "", NC.getResources().getString(R.string.no_sufficient_amount), NC.getResources().getString(R.string.ok),
                            "", true, WithDrawMenuAct.this, "");
                }
            });


            btn_withdrawhistory.setOnClickListener(v -> startActivity(new Intent(WithDrawMenuAct.this, WithdrawHistoryAct.class)));

            back_menu.setOnClickListener(v -> {
                onBackPressed();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        if (dialog1 != null)
            Utils.closeDialog(dialog1);
        super.onDestroy();
    }

    /**
     * This class is used to  withdraw both referal and trip amount
     * Withdraw popup
     * type:1(referal)
     * type:2(trip)
     */
    public void withDraw(final int type) {
        try {
            final View view = View.inflate(WithDrawMenuAct.this, R.layout.withdrawrequestdialog, null);
            final Dialog mcancelDialog = new Dialog(WithDrawMenuAct.this, R.style.dialogwinddow);
            mcancelDialog.setContentView(view);
            mcancelDialog.setCancelable(true);
            mcancelDialog.show();


            FontHelper.applyFont(WithDrawMenuAct.this, mcancelDialog.findViewById(R.id.alert_id));
            Colorchange.ChangeColor((ViewGroup) view, WithDrawMenuAct.this);

            final Button button_success = mcancelDialog.findViewById(R.id.okbtn);
            final Button button_failure = mcancelDialog.findViewById(R.id.cancelbtn);

            TextView txt_avaibal = mcancelDialog.findViewById(R.id.paymentavailedTxt);
            TextView txt_pendingbal = mcancelDialog.findViewById(R.id.paymentpendingTxt);
            TextView txt_reqqmt = mcancelDialog.findViewById(R.id.withdrawamountTxt);
            TextView min_wallet_amtTxt=mcancelDialog.findViewById(R.id.min_wallet_amtTxt);
            LinearLayout min_wallet_amtLay=mcancelDialog.findViewById(R.id.min_wallet_amtLay);

            if (type == 1) {
                txt_avaibal.setText(SessionSave.getSession("site_currency", this) + " " + SessionSave.getSession("driver_wallet_amount", WithDrawMenuAct.this));
                txt_pendingbal.setText(SessionSave.getSession("site_currency", this) + " " + SessionSave.getSession("driver_wallet_pending_amount", WithDrawMenuAct.this));
                txt_reqqmt.setText(SessionSave.getSession("site_currency", this) + " " + SessionSave.getSession("driver_wallet_amount", WithDrawMenuAct.this));
                //min_wallet_amtTxt.setText(SessionSave.getSession("site_currency", this) + " " + SessionSave.getSession("min_wallet_amount", WithDrawMenuAct.this));
                min_wallet_amtLay.setVisibility(View.GONE);

            } else if (type == 2) {
                txt_avaibal.setText(SessionSave.getSession("site_currency", this) + " " + SessionSave.getSession("trip_amount", WithDrawMenuAct.this));
                txt_pendingbal.setText(SessionSave.getSession("site_currency", this) + " " + SessionSave.getSession("trip_pending_amount", WithDrawMenuAct.this));
               // txt_reqqmt.setText(SessionSave.getSession("site_currency", this) + " " + SessionSave.getSession("trip_amount", WithDrawMenuAct.this));
                min_wallet_amtTxt.setText(SessionSave.getSession("site_currency", this) + " " + SessionSave.getSession("min_wallet_amount", WithDrawMenuAct.this));
                min_wallet_amtLay.setVisibility(View.VISIBLE);

                double remain_amt;
                remain_amt=Double.parseDouble( SessionSave.getSession("trip_amount", WithDrawMenuAct.this))-Double.parseDouble( SessionSave.getSession("min_wallet_amount", WithDrawMenuAct.this));
                txt_reqqmt.setText(SessionSave.getSession("site_currency", this) + " " + remain_amt);

            } else if (type == 3) {

                txt_avaibal.setText(SessionSave.getSession("site_currency", this) + " " + SessionSave.getSession("incentive_amount", WithDrawMenuAct.this));
                txt_pendingbal.setText(SessionSave.getSession("site_currency", this) + " " + SessionSave.getSession("incentive_pending_amount", WithDrawMenuAct.this));
                txt_reqqmt.setText(SessionSave.getSession("site_currency", this) + " " + SessionSave.getSession("incentive_amount", WithDrawMenuAct.this));
               // min_wallet_amtTxt.setText(SessionSave.getSession("site_currency", this) + " " + SessionSave.getSession("min_wallet_amount", WithDrawMenuAct.this));
                min_wallet_amtLay.setVisibility(View.GONE);

               /* double remain_amt;
                remain_amt=Double.parseDouble( SessionSave.getSession("incentive_amount", WithDrawMenuAct.this))-Double.parseDouble( SessionSave.getSession("min_wallet_amount", WithDrawMenuAct.this));
                txt_reqqmt.setText(SessionSave.getSession("site_currency", this) + " " + remain_amt);*/
            }else{

            }

            button_success.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    mcancelDialog.dismiss();

                    try {
                        if (type == 1) {
                            ///if (Double.parseDouble(SessionSave.getSession("driver_wallet_amount", WithDrawMenuAct.this).trim()) > 0) {
                                JSONObject j = new JSONObject();

                                try {
                                    j.put("driver_id", SessionSave.getSession("Id", WithDrawMenuAct.this));
                                    j.put("driver_wallet_amount", SessionSave.getSession("driver_wallet_amount", WithDrawMenuAct.this));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                final String withdrawUrl = "type=driver_wallet_request";
//                                new WithDraw(withdrawUrl, j);
//                            } else {
//                                CToast.ShowToast(WithDrawMenuAct.this, NC.getString(R.string.insufficent_amout));
//                            }
                        } else  if (type == 2){
                            double remain_amt;
                            remain_amt=Double.parseDouble( SessionSave.getSession("trip_amount", WithDrawMenuAct.this))-Double.parseDouble( SessionSave.getSession("min_wallet_amount", WithDrawMenuAct.this));

                            if (Double.parseDouble(SessionSave.getSession("trip_amount", WithDrawMenuAct.this).trim()) > 0) {
                                JSONObject j = new JSONObject();

                                j.put("driver_id", SessionSave.getSession("Id", WithDrawMenuAct.this));
                                j.put("available_amount", SessionSave.getSession("trip_amount", WithDrawMenuAct.this));
                               // j.put("request_amount", SessionSave.getSession("remain_amt", WithDrawMenuAct.this));
                                j.put("request_amount", remain_amt);

                                final String withdrawUrl = "type=driver_send_withdraw_request";
                                new WithDraw(withdrawUrl, j);
                            } else {
                                CToast.ShowToast(WithDrawMenuAct.this, NC.getString(R.string.insufficent_amout));
                            }
                        }else if(type==3){
                           // double remain_amt;
                            //remain_amt=Double.parseDouble( SessionSave.getSession("incentive_amount", WithDrawMenuAct.this))-Double.parseDouble( SessionSave.getSession("min_wallet_amount", WithDrawMenuAct.this));
                            if (Double.parseDouble(SessionSave.getSession("incentive_amount", WithDrawMenuAct.this).trim()) > 0) {
                                JSONObject j = new JSONObject();

                                j.put("driver_id", SessionSave.getSession("Id", WithDrawMenuAct.this));
                                j.put("available_amount", SessionSave.getSession("incentive_amount", WithDrawMenuAct.this));
                                j.put("request_amount", SessionSave.getSession("incentive_amount", WithDrawMenuAct.this));
                                //j.put("request_amount", remain_amt);

                                final String withdrawUrl = "type=driver_withdraw_incentive";
                                new WithDraw(withdrawUrl, j);
                            } else {
                                CToast.ShowToast(WithDrawMenuAct.this, NC.getString(R.string.insufficent_amout));
                            }
                        }


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


    /**
     * Withdraw API response parsing.
     */
    private class WithDraw implements APIResult {

        public WithDraw(final String url, JSONObject data) {

            try {
                if (isOnline()) {
                    new APIService_Retrofit_JSON(WithDrawMenuAct.this, this, data, false).execute(url);
                } else {

                    dialog1 = Utils.alert_view(WithDrawMenuAct.this, "", NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok),
                            "", true, WithDrawMenuAct.this, "");

                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        /**
         * Parse the response and update the UI.
         */
        @Override
        public void getResult(final boolean isSuccess, final String result) {

            try {

                Log.e("result", result);
                if (isSuccess) {
                    final JSONObject json = new JSONObject(result);
                    if (json.getString("status").trim().equals("1")) {

                        if (json.has("details")) {
                            JSONObject details = json.getJSONObject("details");
                            if (details.has("trip_pending_amount")) {
                                SessionSave.saveSession("trip_pending_amount", details.getString("trip_pending_amount"), WithDrawMenuAct.this);
                                SessionSave.saveSession("trip_amount", details.getString("trip_amount"), WithDrawMenuAct.this);
                                SessionSave.saveSession("total_amount", details.getString("total_amount"), WithDrawMenuAct.this);
                                startActivity(new Intent(WithDrawMenuAct.this, WithDrawMenuAct.class));
                            }


                        }
                        if (json.has("driver_wallet_amount")) {
                            SessionSave.saveSession("driver_wallet_amount", json.getString("driver_wallet_amount"), WithDrawMenuAct.this);
                            SessionSave.saveSession("driver_wallet_pending_amount", json.getString("driver_wallet_pending_amount"), WithDrawMenuAct.this);
                            startActivity(new Intent(WithDrawMenuAct.this, WithDrawMenuAct.class));
                        }

                        if (json.has("driver_incentive_amount")) {
                            SessionSave.saveSession("incentive_amount", json.getString("driver_incentive_amount"), WithDrawMenuAct.this);
                            SessionSave.saveSession("incentive_pending_amount", json.getString("driver_incentive_pending_amount"), WithDrawMenuAct.this);
                            startActivity(new Intent(WithDrawMenuAct.this, WithDrawMenuAct.class));
                        }
                        txt_referalamt.setText(SessionSave.getSession("site_currency", WithDrawMenuAct.this) + " " + SessionSave.getSession("driver_wallet_amount", WithDrawMenuAct.this));
                        with_amount.setText(SessionSave.getSession("driver_wallet_amount", WithDrawMenuAct.this));
                        txt_refpendingamt.setText(NC.getResources().getString(R.string.payment_pending) + "- " + SessionSave.getSession("site_currency", WithDrawMenuAct.this) + " " + SessionSave.getSession("driver_wallet_pending_amount", WithDrawMenuAct.this));

                        txt_tripamount.setText(SessionSave.getSession("site_currency", WithDrawMenuAct.this) + " " + SessionSave.getSession("trip_amount", WithDrawMenuAct.this));
                        txt_trippendingamt.setText(NC.getResources().getString(R.string.payment_pending) + "- " + SessionSave.getSession("site_currency", WithDrawMenuAct.this) + " " + SessionSave.getSession("trip_pending_amount", WithDrawMenuAct.this));

                        txt_incentiveamt.setText(SessionSave.getSession("site_currency", WithDrawMenuAct.this) + " " + SessionSave.getSession("incentive_amount", WithDrawMenuAct.this));
                        txt_incentpendingamt.setText(NC.getResources().getString(R.string.payment_pending) + "- " + SessionSave.getSession("site_currency", WithDrawMenuAct.this) + " " + SessionSave.getSession("incentive_pending_amount", WithDrawMenuAct.this));
                    }

                    dialog1 = Utils.alert_view(WithDrawMenuAct.this, "", json.getString("message"), NC.getResources().getString(R.string.ok),
                            "", true, WithDrawMenuAct.this, "");

                }

            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
